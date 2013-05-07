package com.freyja.FES.common.Network

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
trait RoutingEntity {
  private var routingNetwork: RoutingNetwork = new RoutingNetwork()
  routingNetwork.add(this)

  def getNetwork: RoutingNetwork = routingNetwork

  def changeNetwork(network: RoutingNetwork) {
    this.routingNetwork = network
  }

  def defaultNetwork() {
    routingNetwork.defaultNetwork(this)
  }

  def sameNetwork(te: RoutingEntity): Boolean = {
    routingNetwork.contains(te)
  }

  def networkInfo: String = {
    routingNetwork.info()
  }

}
