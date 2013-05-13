package com.freyja.FES.common.inventories

import net.minecraft.tileentity.TileEntity
import com.freyja.FES.common.Network.RoutingEntity
import net.minecraftforge.common.ForgeDirection
import net.minecraft.inventory.IInventory
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
    hasRoom
  }

  def addItem(itemStack: ItemStack) {
    for (
      slot <- 0 until getConnected.getSizeInventory
      if getConnected.getStackInSlot(slot) == null
    ) {
      getConnected.setInventorySlotContents(slot, itemStack)
      return
    }
  }

  def hasRoom: Boolean = {
    if (getConnected == null) return false
    val itemStack =
      (for (slot <- 0 until getConnected.getSizeInventory) yield getConnected.getStackInSlot(slot)).flatMap(x => x match {
        case null => None
        case _ => Some(x)
      })

    if (itemStack.length < getConnected.getSizeInventory) {
      return true
    }
    false
  }
}
