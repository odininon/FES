package com.freyja.FES.client

import com.freyja.FES.common.CommonProxy
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import com.freyja.FES.client.renderers.{RenderLine, ItemRenderInjector, TileEntityRendererInjector}
import com.freyja.FES.common.inventories.TileEntityInjector
import cpw.mods.fml.client.registry.{RenderingRegistry, ClientRegistry}
import net.minecraftforge.client.MinecraftForgeClient
import com.freyja.FES.FES

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

class ClientProxy extends CommonProxy {
  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
    ???
  }

  override def registerTESRS() {
    ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileEntityInjector], new TileEntityRendererInjector())
    RenderingRegistry.registerBlockHandler(new RenderLine())
    MinecraftForgeClient.registerItemRenderer(FES.blockInjectorId, new ItemRenderInjector)
  }
}

