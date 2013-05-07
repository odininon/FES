package com.freyja.FES.common.Network

import com.freyja.FES.common.inventories.{TileEntityLine, TileEntityReceptacle, TileEntityInjector}
import net.minecraft.item.ItemStack
import scala.util.Random
import com.freyja.FES.FES

/**
 * @author Freyja
 * Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class RoutingNetwork {
  private var injectors = List[TileEntityInjector]()
  private var receptacles = List[TileEntityReceptacle]()
  private var lines = List[TileEntityLine]()


  override def equals(obj: Any): Boolean = {
    obj match {
      case obj: RoutingNetwork => {
        this.injectors.equals(obj.getInjectors) && this.receptacles.equals(obj.getReceptacles) && this.lines.equals(obj.getLines)
      }
      case _ => false
    }
  }

  def contains(te: RoutingEntity): Boolean = {
    te match {
      case te: TileEntityInjector => injectors.contains(te)
      case te: TileEntityReceptacle => receptacles.contains(te)
      case te: TileEntityLine => lines.contains(te)
    }
  }

  def defaultNetwork(thing: AnyRef) {
    injectors = List.empty
    receptacles = List.empty
    lines = List.empty
    add(thing)
  }

  def info(): String = {
    (injectors.length + receptacles.length + lines.length).toString
  }

  def add(thing: AnyRef) {
    thing match {
      case thing: TileEntityInjector => injectors ::= thing
      case thing: TileEntityReceptacle => receptacles ::= thing
      case thing: TileEntityLine => lines ::= thing
    }
    sortNetwork()
  }

  def sortNetwork() {
    injectors = injectors.sortBy(_.toString)
    receptacles = receptacles.sortBy(_.toString)
    lines = lines.sortBy(_.toString)
  }

  def remove(thing: AnyRef) {
    thing match {
      case thing: TileEntityInjector => injectors diff List(thing)
      case thing: TileEntityReceptacle => receptacles diff List(thing)
      case thing: TileEntityLine => lines diff List(thing)
    }
  }

  def mergeNetworks(network: RoutingNetwork) {
    FES.logger.info("Merging networks " + this + " & " + network)
    for (injector <- network.getInjectors) {
      if (!injectors.contains(injector)) {
        add(injector)
      }
    }
    for (receptacle <- network.getReceptacles) {
      if (!receptacles.contains(receptacle)) {
        add(receptacle)
      }
    }
    for (line <- network.getLines) {
      if (!lines.contains(line))
        add(line)
    }
  }

  def slitNetworks(network: RoutingNetwork) {
    injectors diff network.getInjectors
    receptacles diff network.getReceptacles
    lines diff network.getLines
  }

  def getInjectors: Seq[TileEntityInjector] = {
    injectors
  }

  def getReceptacles: Seq[TileEntityReceptacle] = {
    receptacles
  }

  def getLines: Seq[TileEntityLine] = {
    lines
  }

  def hasValidRoute(itemStack: ItemStack): Boolean = {
    for (receptacle <- receptacles) if (receptacle.canAcceptItemStack(itemStack)) return true
    false
  }

  def getValidReceptacles(itemStack: ItemStack): List[TileEntityReceptacle] = {
    var validReceptacles = List[TileEntityReceptacle]()

    for (receptacle <- receptacles) if (receptacle.canAcceptItemStack(itemStack)) validReceptacles ::= receptacle

    validReceptacles
  }

  def injectItemIntoNetwork(injector: TileEntityInjector, itemStack: ItemStack, slotNumber: Int): Boolean = {
    if (hasValidRoute(itemStack)) {
      injector.removeItem(itemStack, slotNumber)

      val receptacle = (Random.shuffle(getValidReceptacles(itemStack))).head
      receptacle.addItem(itemStack)

      true
    } else {
      false
    }
  }
}
