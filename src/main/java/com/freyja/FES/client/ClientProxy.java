package com.freyja.FES.client;

import com.freyja.FES.client.gui.RoutingSettings;
import com.freyja.FES.client.renderers.RenderInjector;
import com.freyja.FES.client.renderers.RenderLine;
import com.freyja.FES.client.renderers.RenderLineLiquid;
import com.freyja.FES.client.renderers.RenderReceptacle;
import com.freyja.FES.common.CommonProxy;
import com.freyja.FES.common.inventories.TileEntityItemInjector;
import com.freyja.FES.common.inventories.TileEntityItemReceptacle;
import com.freyja.FES.common.inventories.TileEntityLiquidInjector;
import com.freyja.FES.common.inventories.TileEntityLiquidReceptacle;
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
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemInjector.class, new RenderInjector());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemReceptacle.class, new RenderReceptacle());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLiquidInjector.class, new RenderInjector());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLiquidReceptacle.class, new RenderReceptacle());
        RenderingRegistry.registerBlockHandler(new RenderLine());
        RenderingRegistry.registerBlockHandler(new RenderLineLiquid());
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
