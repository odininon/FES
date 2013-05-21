package com.freyja.FES.common.inventories

import net.minecraft.tileentity.TileEntity
import com.freyja.FES.common.Network.RoutingEntity
import net.minecraftforge.common.ForgeDirection
import net.minecraft.inventory.{ISidedInventory, IInventory}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.packet.{Packet132TileEntityData, Packet}
import net.minecraft.network.INetworkManager
import com.freyja.FES.utils.{ModCompatibility, Position}

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class TileEntityReceptacle extends TileEntity with RoutingEntity {
  private var connectedInventory: IInventory = null

  add(this)

  override def updateEntity() {
    if (!initialized) initRotate(this)
    if (worldObj.getTotalWorldTime % 10L == 0L) {
      updateConnections()
    }
  }

  override def writeToNBT(par1NBTTagCompound: NBTTagCompound) {
    super.writeToNBT(par1NBTTagCompound)
    writeCustomNBT(par1NBTTagCompound)
  }


  override def readFromNBT(par1NBTTagCompound: NBTTagCompound) {
    super.readFromNBT(par1NBTTagCompound)
    readCustomNBT(par1NBTTagCompound)
  }

  override def getDescriptionPacket: Packet = {
    val tag: NBTTagCompound = new NBTTagCompound()
    writeCustomNBT(tag)
    new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag)
  }

  def writeCustomNBT(tag: NBTTagCompound) {
    tag.setInteger("Orientation", orientation.ordinal())
    tag.setBoolean("Initialized", initialized)
  }

  override def onDataPacket(net: INetworkManager, pkt: Packet132TileEntityData) {
    readCustomNBT(pkt.customParam1)
  }

  def readCustomNBT(tag: NBTTagCompound) {
    orientation = ForgeDirection.getOrientation(tag.getInteger("Orientation"))
    initialized = tag.getBoolean("Initialized")
  }

  def getConnected = connectedInventory

  def updateConnections() {
    val pos = new Position(this, orientation)
    pos.moveForwards(1)

    var te = worldObj.getBlockTileEntity(pos.x.toInt, pos.y.toInt, pos.z.toInt)

    te match {
      case null => if (this.connectedInventory != null) this.connectedInventory = null
      case te: IInventory => if (this.connectedInventory != te) this.connectedInventory = te
      case _ => None
    }

    pos.moveBackwards(2)

    te = worldObj.getBlockTileEntity(pos.x.toInt, pos.y.toInt, pos.z.toInt)

    te match {
      case null => None
      case te: RoutingEntity => if (!te.isInstanceOf[TileEntityReceptacle] && !this.getNetwork.equals(te.getNetwork)) {
        this.getNetwork.mergeNetworks(te.getNetwork)
        te.getNetwork.mergeNetworks(this.getNetwork)
      }
      case _ => None
    }
  }

  def canAccept(itemStack: ItemStack): Boolean = {
    hasRoom(itemStack)
  }

  def addItem(itemStack: ItemStack) {
    getConnected match {
      case x: ISidedInventory => {
        for (
          slot <- x.getSizeInventorySide(orientation.getOpposite.ordinal())
          if getConnected.getStackInSlot(slot) == null || getConnected.getStackInSlot(slot).isItemEqual(itemStack)
        ) {
          if (x.func_102007_a(slot, itemStack, orientation.getOpposite.ordinal())) {
            merge(itemStack, slot)
            if (itemStack.stackSize <= 0) {
              return
            }
          }
        }
      }
      case x: IInventory => {
        for (
          slot <- 0 until getConnected.getSizeInventory
          if getConnected.getStackInSlot(slot) == null || getConnected.getStackInSlot(slot).isItemEqual(itemStack)
        ) {
          merge(itemStack, slot)
          if (itemStack.stackSize <= 0) {
            return
          }
        }
      }
    }

  }

  def merge(itemStack: ItemStack, slot: Int) {
    val stack = Option(getConnected.getStackInSlot(slot)) getOrElse (new ItemStack(itemStack.getItem, 0))
    val tempStack = stack.copy()

    if (itemStack.hasTagCompound) {
      tempStack.setTagCompound(itemStack.getTagCompound)
    }

    val maxIncreaseAmount = getConnected.getInventoryStackLimit - tempStack.stackSize

    if (maxIncreaseAmount != 0) {
      if (itemStack.stackSize <= maxIncreaseAmount) {
        tempStack.stackSize += itemStack.stackSize
        itemStack.stackSize -= itemStack.stackSize
      } else {
        tempStack.stackSize += maxIncreaseAmount
        itemStack.stackSize -= maxIncreaseAmount
      }
      getConnected.setInventorySlotContents(slot, tempStack)
    }
  }

  def canMerge(itemStack1: ItemStack, itemStack2: ItemStack): Boolean = {
    if (itemStack1 == null || !itemStack1.isItemEqual(itemStack2)) return false

    val maxIncreaseAmount = getConnected.getInventoryStackLimit - itemStack1.stackSize

    maxIncreaseAmount >= itemStack2.stackSize
  }

  def hasRoom(itemStack: ItemStack): Boolean = {
    if (getConnected == null) return false

    if (ModCompatibility.isTConstructLoaded) {
      val height = ModCompatibility.getSmelteryHeight(getConnected)
      if (height > 0) {
        val tempStack =
          (for (slot <- 0 until height * 9; if !canMerge(getConnected.getStackInSlot(slot), itemStack)) yield getConnected.getStackInSlot(slot)).flatMap(x => x match {
            case null => None
            case _ => Some(x)
          })

        if ((height * 9) - tempStack.length >= itemStack.stackSize) {
          return true
        }
        return false
      }
    }

    getConnected match {
      case x: ISidedInventory => {
        for (slot <- x.getSizeInventorySide(orientation.getOpposite.ordinal())) {
          if (x.func_102007_a(slot, itemStack, orientation.getOpposite.ordinal())) {
            if (getConnected.getStackInSlot(slot) == null || canMerge(getConnected.getStackInSlot(slot), itemStack)) return true
          }
        }
        false
      }

      case x: IInventory => {
        val tempStack =
          (for (slot <- 0 until getConnected.getSizeInventory; if !canMerge(getConnected.getStackInSlot(slot), itemStack)) yield getConnected.getStackInSlot(slot)).flatMap(x => x match {
            case null => None
            case _ => Some(x)
          })

        if (tempStack.length < getConnected.getSizeInventory) {
          return true
        }
        false
      }
    }
  }
}
