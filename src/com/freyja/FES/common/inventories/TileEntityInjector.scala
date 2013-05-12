package com.freyja.FES.common.inventories

import net.minecraft.tileentity.TileEntity
import com.freyja.FES.common.Network.RoutingEntity
import com.freyja.FES.common.utils.Position
import net.minecraftforge.common.ForgeDirection
import net.minecraft.inventory.IInventory

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class TileEntityInjector extends TileEntity with RoutingEntity {
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
      case te: RoutingEntity => if (!te.isInstanceOf[TileEntityInjector] && !this.getNetwork.equals(te.getNetwork)) {
        this.getNetwork.mergeNetworks(te.getNetwork)
        te.getNetwork.mergeNetworks(this.getNetwork)
      }
      case _ => None
    }
  }
}
