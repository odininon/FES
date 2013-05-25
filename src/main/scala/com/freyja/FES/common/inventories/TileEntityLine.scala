package com.freyja.FES.common.inventories

import com.freyja.FES.common.Network.RoutingEntity

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class TileEntityLine extends RoutingEntity {
  add(this)

  override def updateEntity() {
    if (worldObj.getTotalWorldTime % 10L == 0L) {
      updateConnections()
    }
  }

  def updateConnections() {
    val otherTE = ((for (i <- -1 to 1) yield worldObj.getBlockTileEntity(xCoord + i, yCoord, zCoord)).toList :::
      (for (j <- -1 to 1) yield worldObj.getBlockTileEntity(xCoord, yCoord + j, zCoord)).toList :::
      (for (k <- -1 to 1) yield worldObj.getBlockTileEntity(xCoord, yCoord, zCoord + k)).toList).flatMap(x => x match {
      case x: TileEntityLine => if (x != this) Some(x) else None
      case _ => None
    })

    for (entity <- otherTE) {
      if (!entity.getNetwork.equals(getNetwork)) {
        entity.getNetwork.mergeNetworks(getNetwork)
        getNetwork.mergeNetworks(entity.getNetwork)
      }
    }
  }
}
