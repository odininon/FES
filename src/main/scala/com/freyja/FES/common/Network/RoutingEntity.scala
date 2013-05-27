package com.freyja.FES.common.Network

import net.minecraft.inventory.{ISidedInventory, IInventory}
import scala.util.Random
import cpw.mods.fml.common.network.PacketDispatcher
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.ForgeDirection
import net.minecraft.item.ItemStack
import com.freyja.FES.common.inventories.{TileEntityInjector, TileEntityReceptacle}
import com.freyja.FES.RoutingSettings.{IRoutingSetting, RoutingSettingsRegistry}

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
trait RoutingEntity extends TileEntity {
  private val routingNetwork: RoutingNetwork = new RoutingNetwork()
  protected var initialized = false
  protected var orientation: ForgeDirection = ForgeDirection.UP

  protected var connectedInventory: TileEntity = null
  protected var pullItemStacks = true

  protected var routingSettings = RoutingSettingsRegistry.Instance().getRoutingSetting(0)

  def getNetwork = routingNetwork

  def defaultNetwork() {
    clearNetwork()
    add(this)
  }

  def clearNetwork() {}

  def add(obj: this.type) {
    routingNetwork.add(obj)
  }

  def initRotate(tileEntity: TileEntity) {
    routingSettings = RoutingSettingsRegistry.Instance().getRoutingSetting(1)
    if (tileEntity.worldObj != null && !tileEntity.worldObj.isRemote) {

      val otherTE = ((for (i <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord + i, tileEntity.yCoord, tileEntity.zCoord)).toList :::
        (for (j <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord + j, tileEntity.zCoord)).toList :::
        (for (k <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord + k)).toList).flatMap(x => x match {
        case x: IInventory => if (x != this) Some(x) else None
        case _ => None
      })

      if (otherTE.isEmpty) return

      val entity = Random.shuffle(otherTE).head
      setOrientation(calculateDirection(entity, tileEntity))
      PacketDispatcher.sendPacketToAllAround(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 64, tileEntity.worldObj.getWorldInfo.getDimension, tileEntity.getDescriptionPacket)

      initialized = true
    }
  }

  def getOrientation: ForgeDirection = {
    orientation
  }

  def setOrientation(direction: ForgeDirection) {
    orientation = direction
  }

  def calculateDirection(entity: TileEntity, injector: TileEntity): ForgeDirection = {
    val dx = -injector.xCoord + entity.xCoord
    val dy = -injector.yCoord + entity.yCoord
    val dz = -injector.zCoord + entity.zCoord

    if (dx >= 1 && dy == 0 && dz == 0) return ForgeDirection.EAST
    if (dx <= 1 && dy == 0 && dz == 0) return ForgeDirection.WEST
    if (dx == 0 && dy >= 1 && dz == 0) return ForgeDirection.UP
    if (dx == 0 && dy <= 1 && dz == 0) return ForgeDirection.DOWN
    if (dx == 0 && dy == 0 && dz >= 1) return ForgeDirection.SOUTH
    if (dx == 0 && dy == 0 && dz <= 1) return ForgeDirection.NORTH

    ForgeDirection.UNKNOWN
  }

  def relativeDirections(entity: TileEntity, injector: TileEntity): ForgeDirection = {
    injector match {
      case te: TileEntityReceptacle => calculateDirection(entity, injector)
      case te: TileEntityInjector => calculateDirection(entity, injector).getOpposite
    }
  }

  def rotate(tileEntity: TileEntity) {
    val currentRotation = orientation.ordinal()

    val otherTE = ((for (i <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord + i, tileEntity.yCoord, tileEntity.zCoord)).toList :::
      (for (j <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord + j, tileEntity.zCoord)).toList :::
      (for (k <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord + k)).toList).flatMap(x => x match {
      case x: IInventory => if (x != this) Some(x) else None
      case _ => None
    })

    if (otherTE.isEmpty || otherTE.length == 1) return

    var newRotation = calculateDirection(Random.shuffle(otherTE).head, tileEntity).ordinal()

    while (newRotation == currentRotation) {
      newRotation = calculateDirection(Random.shuffle(otherTE).head, tileEntity).ordinal()
    }

    orientation = ForgeDirection.VALID_DIRECTIONS(newRotation)
  }

  def getConnected = connectedInventory

  def injectItems(inventory: TileEntity) {
    if (inventory == null) return

    inventory match {
      case x: ISidedInventory => {
        for (slot <- x.getSizeInventorySide(relativeDirections(inventory, this).ordinal())) {
          val tempStack = x.getStackInSlot(slot)
          if (tempStack != null) {
            val itemStack = tempStack.copy
            itemStack.stackSize = pullItemStacks match {
              case false => 1
              case true => tempStack.stackSize
            }
            val canExtract = x.func_102008_b(slot, itemStack, orientation.getOpposite.ordinal()) && routingSettings.isItemValid(itemStack)
            if (canExtract && itemStack != null) {
              while (!getNetwork.injectItemStack(itemStack, this, slot, x) && itemStack.stackSize > 0) {
                itemStack.stackSize -= 1
              }
              if (itemStack.stackSize > 0) return
            }
          }
        }
      }
      case x: IInventory => {
        for (slot <- 0 until x.getSizeInventory) {
          val tempStack = x.getStackInSlot(slot)
          if (tempStack != null) {
            val itemStack = tempStack.copy
            itemStack.stackSize = pullItemStacks match {
              case false => 1
              case true => tempStack.stackSize
            }
            val canExtract = routingSettings.isItemValid(itemStack)
            if (canExtract && itemStack != null) {
              while (!getNetwork.injectItemStack(itemStack, this, slot, x) && itemStack.stackSize > 0) {
                itemStack.stackSize -= 1
              }
              if (itemStack.stackSize > 0) return
            }
          }
        }
      }
    }
  }

  def removeItem(itemStack: ItemStack, slotNumber: Int, inventory: IInventory) {
    inventory.decrStackSize(slotNumber, itemStack.stackSize)
    inventory.onInventoryChanged()
  }

  def setSettings(settings: IRoutingSetting) {
    routingSettings = settings
  }

  def getSettings = routingSettings
}
