package com.freyja.FES.client

import com.freyja.FES.common.CommonProxy
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import com.freyja.FES.client.renderers._
import com.freyja.FES.common.inventories.{TileEntityReceptacle, TileEntityInjector}
import cpw.mods.fml.client.registry.{RenderingRegistry, ClientRegistry}
import cpw.mods.fml.client.FMLClientHandler
import com.freyja.FES.client.gui.RoutingSettings
import com.freyja.FES.common.Network.RoutingEntity

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

class ClientProxy extends CommonProxy {
  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef = {
    ???
  }


  override def getWorld: World = FMLClientHandler.instance().getClient.thePlayer.worldObj

  override def registerTESRS() {
    ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileEntityInjector], new RenderInjector())
    ClientRegistry.bindTileEntitySpecialRenderer(classOf[TileEntityReceptacle], new RenderReceptacle())
    RenderingRegistry.registerBlockHandler(new RenderLine())
    //MinecraftForgeClient.registerItemRenderer(FES.blockInjectorId, new ItemRenderInjector)
  }

  override def openLocalGui(guiID: Int, x: Int, y: Int, z: Int) {
    guiID match {
      case 0 => FMLClientHandler.instance().getClient.displayGuiScreen(new RoutingSettings(getWorld.getBlockTileEntity(x, y, z).asInstanceOf[RoutingEntity]))
    }
  }
}

