package com.freyja.FES.common.inventories

import net.minecraft.tileentity.TileEntity
import com.freyja.FES.common.Network.RoutingEntity

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class TileEntityLine extends TileEntity with RoutingEntity {

  override def updateEntity() {
    checkNetworks()
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

    if (otherTE == null) this.defaultNetwork()

    for (entity <- otherTE) {
      if (!entity.getNetwork.equals(this.getNetwork)) {
        entity.getNetwork.mergeNetworks(this.getNetwork)
        this.changeNetwork(entity.getNetwork)
      }
    }


  }

  def propergateDeletion() {

  }


  def reportConnections(): List[String] = {
    var strings: List[String] = List.empty
    strings ::= this.getNetwork.info()
    strings
  }

}
