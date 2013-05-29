package com.freyja.FES.common.Network

import scala.collection.mutable.ListBuffer
import com.freyja.FES.common.inventories.{TileEntityItemLine, TileEntityItemReceptacle, TileEntityItemInjector}
import net.minecraft.item.ItemStack
import net.minecraft.inventory.IInventory
import scala.util.Random
import net.minecraft.tileentity.TileEntity

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class ItemRoutingNetwork extends RoutingNetwork {

  private var injectors = ListBuffer.empty[TileEntityItemInjector]
  private var receptacles = ListBuffer.empty[TileEntityItemReceptacle]
  private var lines = ListBuffer.empty[TileEntityItemLine]

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

  override def mergeNetworks(network: RoutingNetwork) {
    for (injector <- network.getInjectors) add(injector)
    for (line <- network.getLines) add(line)
    for (receptacle <- network.getReceptacles) add(receptacle)
  }

  override def add(obj: Any) {
    obj match {
      case te: TileEntityItemInjector => if (!injectors.contains(te)) injectors += te
      case te: TileEntityItemReceptacle => if (!receptacles.contains(te)) receptacles += te
      case te: TileEntityItemLine => if (!lines.contains(te)) lines += te
    }
  }

  override def count = injectors.size + lines.size + receptacles.size

  override def getInjectors = injectors

  override def getReceptacles = receptacles

  override def getLines = lines

  override def getAll = {
    val list = ListBuffer.empty[RoutingEntity]
    for (injector <- getInjectors) list += injector
    for (receptacle <- getReceptacles) list += receptacle
    for (line <- getLines) list += line
    list
  }

  def getValidReceptacles(itemStack: ItemStack): List[TileEntityItemReceptacle] = {
    val list = ListBuffer.empty[TileEntityItemReceptacle]

    for (receptacle <- getReceptacles)
      if (receptacle.canAccept(itemStack)) list += receptacle

    list.toList
  }

  override def hasValidRoute(itemStack: ItemStack): Boolean = {
    for (receptacle <- getReceptacles) {
      if (receptacle.canAccept(itemStack)) return true
    }
    false
  }

  override def injectItemStack(itemStack: ItemStack, injector: RoutingEntity, slotNumber: Int, inventory: IInventory): Boolean = {
    if (hasValidRoute(itemStack)) {
      injector.removeItem(itemStack, slotNumber, inventory)

      val receptacle = (Random.shuffle(getValidReceptacles(itemStack))).head
      receptacle.addItem(itemStack)

      true
    } else {
      false
    }
  }

  override def purgeNetwork(te: TileEntity) {
    injectors.clear()
    receptacles.clear()
    lines.clear()
    this.add(te)
  }
}
