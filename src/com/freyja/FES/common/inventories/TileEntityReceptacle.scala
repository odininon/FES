package com.freyja.FES.common.inventories

import net.minecraft.tileentity.TileEntity
import net.minecraft.item.ItemStack
import com.freyja.FES.common.Network.RoutingEntity

/**
 * @author Freyja
 * Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class TileEntityReceptacle extends TileEntity with RoutingEntity {
  def canAcceptItemStack(itemStack: ItemStack): Boolean = false

  def addItem(itemStack: ItemStack) {}
}
