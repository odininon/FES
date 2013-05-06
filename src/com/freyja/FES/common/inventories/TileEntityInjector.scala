package com.freyja.FES.common.inventories

import net.minecraft.tileentity.TileEntity
import net.minecraft.item.ItemStack
import com.freyja.FES.common.Network.{RoutingNetwork, RoutingEntity}

/**
 * @author Freyja
 * Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class TileEntityInjector extends TileEntity with RoutingEntity {

  def removeItem(itemStack: ItemStack) {}
}
