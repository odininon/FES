package com.freyja.FES.client;

import com.freyja.FES.client.gui.RoutingSettings;
import com.freyja.FES.client.renderers.RenderInjector;
import com.freyja.FES.client.renderers.RenderLine;
import com.freyja.FES.client.renderers.RenderReceptacle;
import com.freyja.FES.common.CommonProxy;
import com.freyja.FES.common.inventories.TileEntityInjector;
import com.freyja.FES.common.inventories.TileEntityReceptacle;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class ClientProxy extends CommonProxy {

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return super.getClientGuiElement(ID, player, world, x, y, z);
    }

    @Override
    public void registerTESRS()
    {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInjector.class, new RenderInjector());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityReceptacle.class, new RenderReceptacle());
        RenderingRegistry.registerBlockHandler(new RenderLine());
    }

    @Override
    public void openLocalGui(int guiID, int x, int y, int z)
    {
        switch (guiID) {
            case 0:
                FMLClientHandler.instance().getClient().displayGuiScreen(new RoutingSettings(getWorld().getBlockTileEntity(x, y, z)));
        }
    }

    @Override
    public World getWorld()
    {
        return FMLClientHandler.instance().getClient().thePlayer.worldObj;
    }
}
