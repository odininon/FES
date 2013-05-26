package com.freyja.FES.client.renderers;

import com.freyja.FES.client.models.ModelInjector;
import com.freyja.FES.common.inventories.TileEntityInjector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
@SideOnly(Side.CLIENT)
public class RenderInjector extends TileEntitySpecialRenderer {
    private ModelInjector modelInjector = new ModelInjector();

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f)
    {
        modelInjector.render((TileEntityInjector) tileentity, x, y, z);
    }
}
