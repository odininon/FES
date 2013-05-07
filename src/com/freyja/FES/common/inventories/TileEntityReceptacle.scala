package com.freyja.FES.common.inventories

import net.minecraft.tileentity.TileEntity
import net.minecraft.item.ItemStack
import net.minecraft.inventory.IInventory
import net.minecraftforge.common.ForgeDirection
import com.freyja.FES.common.utils.Position
import com.freyja.FES.common.Network.RoutingEntity

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class TileEntityReceptacle extends TileEntity with RoutingEntity {
  private var connectedInventory: IInventory = null
  private var orientation: ForgeDirection = ForgeDirection.UP

  getNetwork.defaultNetwork(this)
  def canAcceptItemStack(itemStack: ItemStack): Boolean = {
    connectedInventory != null && getNumberOfStacks < connectedInventory.getSizeInventory
  }

  def getNumberOfStacks: Int = {
    (for (i <- 0 until connectedInventory.getSizeInventory)
    yield connectedInventory.getStackInSlot(i)).toList.flatMap(x => x match {
      case null => None
      case _ => Some(x)
    }).length
  }

  def addItem(itemStack: ItemStack) {
    for (
      i <- 0 until connectedInventory.getSizeInventory
      if connectedInventory.getStackInSlot(i) == null
    ) {
      connectedInventory.setInventorySlotContents(i, itemStack)
      return
    }
  }

  override def updateEntity() {
    if (this.worldObj.getTotalWorldTime % 10L == 0L) {
      checkConnectedInventory()
      checkNetwork()
    }
  }

  def checkNetwork() {
    val pos = new Position(xCoord, yCoord, zCoord, orientation)
    pos.moveBackwards(1)

    val te = worldObj.getBlockTileEntity(pos.x.toInt, pos.y.toInt, pos.z.toInt)

    te match {
      case null => getNetwork.defaultNetwork(this)
      case te: RoutingEntity => if (!te.isInstanceOf[RoutingEntity] && !getNetwork.equals(te.getNetwork)) {
        te.getNetwork.mergeNetworks(getNetwork)
        this.getNetwork.mergeNetworks(te.getNetwork)
      }
      case _ => None
    }
  }

  def propagateDeletion() {
    for (te <- getNetwork.getAll) {
      te.getNetwork.remove(this)
    }
  }

  def checkConnectedInventory() {
    val pos: Position = new Position(xCoord, yCoord, zCoord, orientation)
    pos.moveForwards(1)

    val te = worldObj.getBlockTileEntity(pos.x.toInt, pos.y.toInt, pos.z.toInt)
    te match {
      case null => if (connectedInventory != null) connectedInventory = null
      case te: IInventory => if (connectedInventory != te) connectedInventory = te
      case _ => None
    }
  }
}
