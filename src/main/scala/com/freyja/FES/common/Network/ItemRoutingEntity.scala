package com.freyja.FES.common.Network

import net.minecraftforge.common.ForgeDirection
import net.minecraft.tileentity.TileEntity
import com.freyja.FES.RoutingSettings.RoutingSettingsRegistry
import net.minecraft.inventory.{ISidedInventory, IInventory}
import scala.util.Random
import cpw.mods.fml.common.network.PacketDispatcher
import com.freyja.FES.common.inventories.{TileEntityItemInjector, TileEntityItemReceptacle}
import net.minecraft.item.ItemStack

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
trait ItemRoutingEntity extends RoutingEntity {

  override protected val routingNetwork: ItemRoutingNetwork = new ItemRoutingNetwork()

  override def getNetwork: ItemRoutingNetwork = routingNetwork


  routingSettings = RoutingSettingsRegistry.Instance().getRoutingSetting(0, RoutingSettingsRegistry.Type.ITEM)

  override def initRotate(tileEntity: TileEntity) {
    routingSettings = RoutingSettingsRegistry.Instance().getRoutingSetting(1, RoutingSettingsRegistry.Type.ITEM)
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

  override def relativeDirections(entity: TileEntity, injector: TileEntity): ForgeDirection = {
    injector match {
      case te: TileEntityItemReceptacle => calculateDirection(entity, injector)
      case te: TileEntityItemInjector => calculateDirection(entity, injector).getOpposite
    }
  }

  override def rotate(tileEntity: TileEntity) {
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

  override def injectItems(inventory: TileEntity) {
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

  override def removeItem(itemStack: ItemStack, slotNumber: Int, inventory: IInventory) {
    inventory.decrStackSize(slotNumber, itemStack.stackSize)
    inventory.onInventoryChanged()
  }
}
