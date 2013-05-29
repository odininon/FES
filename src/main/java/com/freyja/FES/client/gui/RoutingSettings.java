package com.freyja.FES.client.gui;

import com.freyja.FES.RoutingSettings.ModSortSettings;
import com.freyja.FES.RoutingSettings.RoutingSettingsRegistry;
import com.freyja.FES.common.Network.RoutingEntity;
import com.freyja.FES.common.packets.ModPacketUpdateSettings;
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
    private int tempIndex = index;
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
            tempIndex += 1;
            if (tempIndex >= RoutingSettingsRegistry.Instance().getSize()) tempIndex = 0;

            if (tempIndex != index) {
                index = tempIndex;
                ((GuiButton) buttonList.get(buttonList.indexOf(par1GuiButton))).displayString = RoutingSettingsRegistry.Instance().getRoutingSetting(index).getName();
            }
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {

        super.mouseClicked(par1, par2, par3);

        if (par3 == 0 && isShiftKeyDown()) {
            for (Object aButtonList : this.buttonList) {
                GuiButton guibutton = (GuiButton) aButtonList;

                if (guibutton.mousePressed(this.mc, par1, par2)) {
                    this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
                    this.actionPerformedShift(guibutton);
                }
            }
        }

        if (par3 == 1) {
            for (Object aButtonList : this.buttonList) {
                GuiButton guibutton = (GuiButton) aButtonList;

                if (guibutton.mousePressed(this.mc, par1, par2)) {
                    this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
                    this.actionPerformedRightButton(guibutton);
                }
            }
        }
    }

    private void actionPerformedShift(GuiButton guibutton)
    {
        if (guibutton.id == 0) {
            tempIndex = 1;

            if (tempIndex != index) {
                index = tempIndex;
                ((GuiButton) buttonList.get(buttonList.indexOf(guibutton))).displayString = RoutingSettingsRegistry.Instance().getRoutingSetting(index).getName();
            }
        }
    }

    private void actionPerformedRightButton(GuiButton guibutton)
    {
        if (guibutton.id == 0) {
            tempIndex -= 1;
            if (tempIndex <= -1) tempIndex = RoutingSettingsRegistry.Instance().getSize() - 1;

            if (tempIndex != index) {
                index = tempIndex;
                ((GuiButton) buttonList.get(buttonList.indexOf(guibutton))).displayString = RoutingSettingsRegistry.Instance().getRoutingSetting(index).getName();
            }
        }
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        if (index != RoutingSettingsRegistry.Instance().indexOf(entity.getSettings())) {
            if (RoutingSettingsRegistry.Instance().getRoutingSetting(index) instanceof ModSortSettings) {
                PacketDispatcher.sendPacketToServer(new ModPacketUpdateSettings(((ModSortSettings) RoutingSettingsRegistry.Instance().getRoutingSetting(index)).getModId(), x, y, z).makePacket());
            } else {
                PacketDispatcher.sendPacketToServer(new PacketUpdateSettings(index, x, y, z).makePacket());
            }
        }
    }
}
