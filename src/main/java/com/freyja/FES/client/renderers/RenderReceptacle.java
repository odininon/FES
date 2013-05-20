package com.freyja.FES.client.renderers;

import com.freyja.FES.client.models.ModelReceptacle;
import com.freyja.FES.common.inventories.TileEntityReceptacle;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class RenderReceptacle extends TileEntitySpecialRenderer {
    private ModelReceptacle modelReceptacle = new ModelReceptacle();

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f)
    {
        modelReceptacle.render((TileEntityReceptacle) tileentity, x, y, z);
    }
}