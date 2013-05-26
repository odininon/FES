package com.freyja.FES.client.renderers;

import com.freyja.FES.client.models.ModelInjector;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
@SideOnly(Side.CLIENT)
public class ItemRenderInjector implements IItemRenderer {
    private ModelInjector modelInjector;

    public ItemRenderInjector()
    {
        modelInjector = new ModelInjector();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        switch (type) {
            case ENTITY:
                renderInjector(0f, 0f, 0, 0.5f);
        }
    }

    private void renderInjector(float x, float y, float z, float scale)
    {
        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glTranslatef(x, y, z);
        GL11.glScalef(scale, scale, scale);
        GL11.glRotatef(180f, 0f, 1f, 0f);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/FES/textures/injector.png");
        modelInjector.render();

        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
