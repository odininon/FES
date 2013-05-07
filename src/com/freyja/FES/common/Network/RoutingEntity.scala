package com.freyja.FES.common.Network

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
trait RoutingEntity {
  var routingNetwork: RoutingNetwork = new RoutingNetwork()

  def getNetwork = routingNetwork

}
