package com.freyja.FES.common.Network

import com.freyja.FES.common.inventories.{TileEntityReceptacle, TileEntityLine, TileEntityInjector}
import scala.collection.mutable.ListBuffer


/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class RoutingNetwork {
  private var injectors = ListBuffer.empty[TileEntityInjector]
  private var receptacles = ListBuffer.empty[TileEntityReceptacle]
  private var lines = ListBuffer.empty[TileEntityLine]


  override def equals(obj: Any): Boolean = {
    obj match {
      case net: RoutingNetwork => {
        if (injectors.sortBy(_.toString).equals(net.getInjectors.sortBy(_.toString)) && receptacles.sortBy(_.toString).equals(net.getReceptacles.sortBy(_.toString)) && lines.sortBy(_.toString).equals(net.getLines.sortBy(_.toString)))
          true
        false
      }
      case _ => false
    }
  }

  def mergeNetworks(network: RoutingNetwork) {
    for (injector <- network.getInjectors) add(injector)
    for (line <- network.getLines) add(line)
    for (receptacle <- network.getReceptacles) add(receptacle)
  }

  def count = injectors.length + lines.length + receptacles.length

  def add(obj: Any) {
    obj match {
      case te: TileEntityInjector => if (!injectors.contains(te)) injectors += te
      case te: TileEntityReceptacle => if (!receptacles.contains(te)) receptacles += te
      case te: TileEntityLine => if (!lines.contains(te)) lines += te
    }
  }

  def remove(obj: Any) {
    obj match {
      case te: TileEntityInjector => injectors -= te
      case te: TileEntityLine => lines -= te
      case te: TileEntityReceptacle => receptacles -= te
    }
  }

  def getInjectors = injectors

  def getReceptacles = receptacles

  def getLines = lines

  def getAll = {
    val list = ListBuffer.empty[RoutingEntity]
    for (injector <- getInjectors) list += injector
    for (receptacle <- getReceptacles) list += receptacle
    for (line <- getLines) list += line
    list
  }
}
