package com.freyja.FES.common.inventories

import net.minecraft.tileentity.TileEntity
import net.minecraft.item.ItemStack
import com.freyja.FES.common.Network.RoutingEntity
import net.minecraft.inventory.IInventory
import net.minecraftforge.common.ForgeDirection
import com.freyja.FES.common.utils.Position

/**
 * @author Freyja
 * Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class TileEntityReceptacle extends TileEntity with RoutingEntity {
  private var connectedInventory: IInventory = null
  private var orientation: ForgeDirection = ForgeDirection.UP

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
    checkConnectedInventory()
    checkNetwork()
  }

  def checkNetwork() {
    val pos = new Position(xCoord, yCoord, zCoord, orientation)
    pos.moveBackwards(1)

    val te = worldObj.getBlockTileEntity(pos.x.toInt, pos.y.toInt, pos.z.toInt)

    te match {
      case null => this.defaultNetwork()
      case te: RoutingEntity => if (!te.isInstanceOf[TileEntityReceptacle] && this.getNetwork != te.getNetwork) {
        te.getNetwork.mergeNetworks(this.getNetwork)
        this.changeNetwork(te.getNetwork)
      }
      case _ => None
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

  def reportConnections(): List[String] = {
    var strings: List[String] = List.empty
    if (connectedInventory == null) {
      strings ::= "Nothing Connected"
    } else {
      strings ::= connectedInventory.toString
    }
    strings ::= this.getNetwork.info()

    strings
  }
}
