package com.freyja.FES.common.inventories

import net.minecraft.tileentity.TileEntity
import com.freyja.FES.common.Network.RoutingEntity
import net.minecraftforge.common.ForgeDirection
import net.minecraft.inventory.{ISidedInventory, IInventory}
import com.freyja.FES.common.utils.Position
import net.minecraft.item.ItemStack

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class TileEntityReceptacle extends TileEntity with RoutingEntity {
  private val orientation: ForgeDirection = ForgeDirection.UP
  private var connectedInventory: IInventory = null

  add(this)

  override def updateEntity() {
    if (worldObj.getTotalWorldTime % 10L == 0L) {
      updateConnections()
    }
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

    val maxIncreaseAmount = itemStack.getMaxStackSize - tempStack.stackSize

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
    if (!itemStack1.isItemEqual(itemStack2)) return false

    val maxIncreaseAmount = itemStack1.getMaxStackSize - itemStack1.stackSize

    maxIncreaseAmount > 0
  }

  def hasRoom(itemStack: ItemStack): Boolean = {
    if (getConnected == null) return false

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
          (for (slot <- 0 until getConnected.getSizeInventory) yield getConnected.getStackInSlot(slot)).flatMap(x => x match {
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
