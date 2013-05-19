package com.freyja.FES.client.renderers;

import com.freyja.FES.common.blocks.BlockLine;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class RenderLine implements ISimpleBlockRenderingHandler {

    public static int renderId = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer)
    {
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
    {
        boolean[] directions = new boolean[6];
        for (int i = 0; i < 6; i++)
            directions[i] = ((BlockLine) block).canConnectOnSide(world, i, x, y, z);

        double quarter = (1D / 16D) * 4D;
        double eighth = (1D / 16D) * 2D;

        double min = quarter + eighth;
        double max = 1 - (min);

        renderer.setRenderBounds(min, min, min, max, max, max);
        renderer.renderStandardBlock(block, x, y, z);

        if (directions[0]) {
            renderer.setRenderBounds(min, 0, min, max, max, max);
            renderer.renderStandardBlock(block, x, y, z);
        }
        if (directions[1]) {
            renderer.setRenderBounds(min, min, min, max, 1.0D, max);
            renderer.renderStandardBlock(block, x, y, z);
        }
        if (directions[2]) {
            renderer.setRenderBounds(min, min, 0, max, max, max);
            renderer.renderStandardBlock(block, x, y, z);
        }
        if (directions[3]) {
            renderer.setRenderBounds(min, min, min, max, max, 1.0D);
            renderer.renderStandardBlock(block, x, y, z);
        }
        if (directions[4]) {
            renderer.setRenderBounds(0, min, min, max, max, max);
            renderer.renderStandardBlock(block, x, y, z);
        }
        if (directions[5]) {
            renderer.setRenderBounds(min, min, min, 1.0D, max, max);
            renderer.renderStandardBlock(block, x, y, z);
        }

        return true;
    }

    @Override
    public boolean shouldRender3DInInventory()
    {
        return false;
    }

    @Override
    public int getRenderId()
    {
        return renderId;
    }
}
