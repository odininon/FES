package com.freyja.FES.common.Network

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
trait RoutingEntity {
  private val routingNetwork: RoutingNetwork = new RoutingNetwork()

  def getNetwork = routingNetwork

  def defaultNetwork() {
    clearNetwork()
    add(this)
  }

  def clearNetwork() {}

  def add(obj: this.type) {
    routingNetwork.add(obj)
  }
}
