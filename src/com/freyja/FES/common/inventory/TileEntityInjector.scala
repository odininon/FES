package com.freyja.FES.common.inventory

import net.minecraft.tileentity.TileEntity
import com.freyja.FES.common.Network.RoutingNetwork

/**
 *
 * @user Freyja
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 *
 */
class TileEntityInjector extends TileEntity {
  private var routingNetwork: RoutingNetwork = null

  routingNetwork = new RoutingNetwork()
  routingNetwork.add(this)

  override def updateEntity() {
    super.updateEntity()
      if (routingNetwork == null) {
      routingNetwork = new RoutingNetwork()
      routingNetwork.add(this)
    }
  }
}
