package com.freyja.FES.common.inventories

import net.minecraft.tileentity.TileEntity
import com.freyja.FES.common.Network.RoutingEntity

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class TileEntityLine extends TileEntity with RoutingEntity {
  getNetwork.defaultNetwork(this)

  override def updateEntity() {
    if (this.worldObj.getTotalWorldTime % 10L == 0L) {
      checkNetworks()
    }
  }

  def propagateDeletion() {
    for (te <- routingNetwork.getAll) {
      te.getNetwork.remove(this)
    }
  }

  def checkNetworks() {
    val otherTE = ((for (
      i <- -1 to 1
    ) yield worldObj.getBlockTileEntity(xCoord + i, yCoord, zCoord)).toList ::: (for (
      j <- -1 to 1
    ) yield worldObj.getBlockTileEntity(xCoord, yCoord + j, zCoord)).toList ::: (for (
      k <- -1 to 1
    ) yield worldObj.getBlockTileEntity(xCoord, yCoord, zCoord + k)).toList).flatMap(x => x match {
      case x: TileEntityLine => if (x != this) Some(x) else None
      case _ => None
    }
    )

    if (otherTE == null) getNetwork.defaultNetwork(this)

    for (entity <- otherTE) {
      if (!entity.getNetwork.equals(getNetwork)) {
        entity.getNetwork.mergeNetworks(getNetwork)
        getNetwork.mergeNetworks(entity.getNetwork)
      }
    }
  }
}
