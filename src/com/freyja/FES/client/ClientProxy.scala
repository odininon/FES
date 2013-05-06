package com.freyja.FES.client

import com.freyja.FES.common.CommonProxy
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

/**
 * @author Freyja
 * Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

class ClientProxy extends CommonProxy {
  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
    ???
  }
}

