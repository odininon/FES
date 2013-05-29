package com.freyja.FES.client.renderers;

import com.freyja.FES.client.models.ModelReceptacle;
import com.freyja.FES.common.Network.RoutingEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
@SideOnly(Side.CLIENT)
public class RenderReceptacle extends TileEntitySpecialRenderer {
    private ModelReceptacle modelReceptacle = new ModelReceptacle();

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f)
    {
        modelReceptacle.render((RoutingEntity) tileentity, x, y, z);
    }
}