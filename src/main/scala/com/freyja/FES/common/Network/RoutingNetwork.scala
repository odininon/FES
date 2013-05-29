package com.freyja.FES.common.Network

import scala.collection.mutable.ListBuffer
import net.minecraft.item.ItemStack
import net.minecraft.inventory.IInventory
import net.minecraft.tileentity.TileEntity


/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
abstract class RoutingNetwork {

  def mergeNetworks(network: RoutingNetwork)

  def count = 0

  def add(obj: Any) {}

  def getInjectors: ListBuffer[_] = null

  def getReceptacles: ListBuffer[_] = null

  def getLines: ListBuffer[_] = null

  def getAll: ListBuffer[_] = {
    val list = ListBuffer.empty[Any]
    list
  }

  def hasValidRoute(itemStack: ItemStack): Boolean = false

  def injectItemStack(itemStack: ItemStack, injector: RoutingEntity, slotNumber: Int, inventory: IInventory): Boolean = false

  def purgeNetwork(te: TileEntity)
}
