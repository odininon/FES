package com.freyja.FES.client.models;

import com.freyja.FES.common.Network.RoutingEntity;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.ForgeDirection;
import org.lwjgl.opengl.GL11;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
@SideOnly(Side.CLIENT)
public class ModelReceptacle extends ModelBase {
    private IModelCustom modelReceptacle;

    public ModelReceptacle()
    {
        modelReceptacle = AdvancedModelLoader.loadModel("/mods/FES/models/injector.obj");
    }

    public void render()
    {
        modelReceptacle.renderAll();
    }

    public void render(RoutingEntity te, double x, double y, double z)
    {
        GL11.glPushMatrix();


        GL11.glTranslatef((float) x + 0.5f, (float) y, (float) z + 0.5f);

        ForgeDirection orientation = te.getOrientation();

        if (orientation == ForgeDirection.DOWN) {
            GL11.glRotatef(180, 1, 0, 0);
            GL11.glTranslatef(0, -1f, 0f);
        }

        if (orientation == ForgeDirection.SOUTH) {
            GL11.glRotatef(90, 1, 0, 0);
            GL11.glTranslatef(0, -.5f, -.5f);
        }

        if (orientation == ForgeDirection.NORTH) {
            GL11.glRotatef(90, -1, 0, 0);
            GL11.glTranslatef(0, -.5f, .5f);
        }

        if (orientation == ForgeDirection.WEST) {
            GL11.glRotatef(90, 0, 0, 1);
            GL11.glTranslatef(.5f, -.5f, 0);
        }

        if (orientation == ForgeDirection.EAST) {
            GL11.glRotatef(90, 0, 0, -1);
            GL11.glTranslatef(-.5f, -.5f, 0);
        }

        GL11.glScalef(0.5f, 0.5f, 0.5f);

        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/FES/textures/receptacle.png");

        this.render();

        GL11.glPopMatrix();
    }
}
