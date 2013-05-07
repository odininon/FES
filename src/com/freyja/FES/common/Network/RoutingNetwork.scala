package com.freyja.FES.common.Network

import com.freyja.FES.common.inventories.{TileEntityLine, TileEntityReceptacle, TileEntityInjector}
import net.minecraft.item.ItemStack
import scala.util.Random
import scala.collection.mutable.ArrayBuffer

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class RoutingNetwork {
  private var injectors = ArrayBuffer.empty[TileEntityInjector]
  private var receptacles = ArrayBuffer.empty[TileEntityReceptacle]
  private var lines = ArrayBuffer.empty[TileEntityLine]


  override def equals(obj: Any): Boolean = {
    obj match {
      case obj: RoutingNetwork => {
        if (injectors.sortBy(_.toString).equals(obj.injectors.sortBy(_.toString)) && receptacles.sortBy(_.toString).equals(obj.receptacles.sortBy(_.toString)) && lines.sortBy(_.toString).equals(obj.lines.sortBy(_.toString)))
          true
        false
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
    injectors.clear()
    receptacles.clear()
    lines.clear()
    add(thing)
  }

  def info(): String = {
    (injectors.length + receptacles.length + lines.length).toString
  }

  def add(thing: AnyRef) {
    thing match {
      case thing: TileEntityInjector => if (!contains(thing)) injectors += thing
      case thing: TileEntityReceptacle => if (!contains(thing)) receptacles += thing
      case thing: TileEntityLine => if (!contains(thing)) lines += thing
      case _ => None
    }
  }

  def remove(thing: AnyRef) {
    thing match {
      case thing: TileEntityInjector => injectors -= thing
      case thing: TileEntityReceptacle => receptacles -= thing
      case thing: TileEntityLine => lines -= thing
    }
  }

  def getAll = {
    val all: ArrayBuffer[RoutingEntity] = ArrayBuffer[RoutingEntity]()

    all ++= getInjectors
    all ++= getReceptacles
    all ++= getLines

    all.toList
  }

  def mergeNetworks(network: RoutingNetwork) {
    for (injector <- network.getInjectors) add(injector)
    for (receptacle <- network.getReceptacles) add(receptacle)
    for (line <- network.getLines) add(line)
  }

  def splitNetwork(network: RoutingNetwork, thing: AnyRef): RoutingNetwork = {
    removeAll(thing)
    this
  }

  def removeAll(thing: AnyRef) {
    injectors.clear()
    receptacles.clear()
    lines.clear()

    add(thing)
  }

  def getInjectors: List[TileEntityInjector] = injectors.toList

  def getReceptacles: List[TileEntityReceptacle] = receptacles.toList

  def getLines: List[TileEntityLine] = lines.toList

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
