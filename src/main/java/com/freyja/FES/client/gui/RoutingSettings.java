package com.freyja.FES.client.gui;

import com.freyja.FES.RoutingSettings.RoutingSettingsRegistry;
import com.freyja.FES.common.Network.RoutingEntity;
import com.freyja.FES.common.packets.PacketUpdateSettings;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

@SideOnly(Side.CLIENT)
public class RoutingSettings extends GuiScreen {

    private int index = 0;
    private RoutingEntity entity;

    private int x;
    private int y;
    private int z;

    public RoutingSettings(TileEntity te)
    {
        this.entity = (RoutingEntity) te;
        this.x = te.xCoord;
        this.y = te.yCoord;
        this.z = te.zCoord;
        this.index = RoutingSettingsRegistry.Instance().indexOf(entity.getSettings());
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 75, this.height / 2 - 10, 150, 20, RoutingSettingsRegistry.Instance().getRoutingSetting(index).getName()));
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);

        String str = "Routing Settings";
        drawString(this.fontRenderer, str, this.width / 2 - this.fontRenderer.getStringWidth(str) / 2, this.height / 2 - 30, 0xffffff);
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        super.actionPerformed(par1GuiButton);
        if (par1GuiButton.id == 0) {
            index += 1;
            if (index >= RoutingSettingsRegistry.Instance().getSize()) index = 0;

            if (index != RoutingSettingsRegistry.Instance().indexOf(entity.getSettings())) {
                ((GuiButton) buttonList.get(buttonList.indexOf(par1GuiButton))).displayString = RoutingSettingsRegistry.Instance().getRoutingSetting(index).getName();
            }
        }
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        if (index != RoutingSettingsRegistry.Instance().indexOf(entity.getSettings())) {
            entity.setSettings(RoutingSettingsRegistry.Instance().getRoutingSetting(index));
            PacketDispatcher.sendPacketToServer(new PacketUpdateSettings(index, x, y, z).makePacket());
        }
    }
}
