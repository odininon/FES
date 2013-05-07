package com.freyja.FES.common.inventories

import net.minecraft.tileentity.TileEntity
import net.minecraft.item.ItemStack
import net.minecraft.inventory.{ISidedInventory, IInventory}
import net.minecraftforge.common.ForgeDirection
import com.freyja.FES.common.utils.Position
import com.freyja.FES.common.Network.RoutingEntity

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class TileEntityInjector extends TileEntity with RoutingEntity {
  private var connectedInventory: IInventory = null
  private var orientation: ForgeDirection = ForgeDirection.UP

  getNetwork.defaultNetwork(this)

  def removeItem(itemStack: ItemStack, slotNumber: Int) {
    connectedInventory.setInventorySlotContents(slotNumber, null)
  }

  override def updateEntity() {
    if (this.worldObj.getTotalWorldTime % 10L == 0L) {
      checkConnectedInventory()
      checkNetwork()
      injectNetwork()
    }
  }

  def injectNetwork() {
    val numberOfSlots: Int = (connectedInventory match {
      case null => None
      case x: ISidedInventory => Some(x.getSizeInventorySide(orientation.getOpposite.ordinal()).length)
      case _ => Some(connectedInventory.getSizeInventory)
    }).getOrElse(-1)

    if (numberOfSlots == -1) return

    for (i <- 0 until numberOfSlots) {
      val itemStack: ItemStack = connectedInventory.getStackInSlot(i)
      if (itemStack != null && getNetwork.hasValidRoute(itemStack)) {
        connectedInventory match {
          case x: ISidedInventory => if (x.func_102008_b(i, itemStack, orientation.getOpposite.ordinal())) {
            getNetwork.injectItemIntoNetwork(this, itemStack, i)
          }
          case _ => getNetwork.injectItemIntoNetwork(this, itemStack, i)
        }
        return
      }
    }
  }

  def checkNetwork() {
    val pos = new Position(xCoord, yCoord, zCoord, orientation)
    pos.moveBackwards(1)

    val te = worldObj.getBlockTileEntity(pos.x.toInt, pos.y.toInt, pos.z.toInt)

    te match {
      case null => getNetwork.defaultNetwork(this)
      case te: RoutingEntity => if (!te.isInstanceOf[RoutingEntity] && !this.getNetwork.equals(te.getNetwork)) {
        te.getNetwork.mergeNetworks(this.getNetwork)
        this.getNetwork.mergeNetworks(te.getNetwork)
      }
      case _ => None
    }
  }

  def propagateDeletion() {
    for (te <- routingNetwork.getAll) {
      te.getNetwork.remove(this)
    }
  }

  def checkConnectedInventory() {
    val pos: Position = new Position(xCoord, yCoord, zCoord, orientation)
    pos.moveForwards(1)

    val te = worldObj.getBlockTileEntity(pos.x.toInt, pos.y.toInt, pos.z.toInt)
    te match {
      case null => if (this.connectedInventory != null) this.connectedInventory = null
      case te: IInventory => if (this.connectedInventory != te) this.connectedInventory = te
      case _ => None
    }
  }
}
