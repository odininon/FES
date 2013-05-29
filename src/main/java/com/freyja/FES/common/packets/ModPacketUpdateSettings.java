package com.freyja.FES.common.packets;

import com.freyja.FES.RoutingSettings.RoutingSettingsRegistry;
import com.freyja.FES.common.Network.RoutingEntity;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class ModPacketUpdateSettings extends PacketPurgeNetwork {

    private String modID;

    public ModPacketUpdateSettings() {}

    public ModPacketUpdateSettings(String modID, int x, int y, int z)
    {
        this.modID = modID;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    protected void write(ByteArrayDataOutput output)
    {
        super.write(output);
        output.writeUTF(modID);
    }

    @Override
    public void read(ByteArrayDataInput input)
    {
        super.read(input);
        this.modID = input.readUTF();
    }

    @Override
    public void execute(EntityPlayer player, Side side)
    {
        TileEntity tileEntity = player.worldObj.getBlockTileEntity(x, y, z);
        if (tileEntity instanceof RoutingEntity) {
            ((RoutingEntity) tileEntity).setSettings(RoutingSettingsRegistry.Instance().getModSetting(modID));
        }
        if (side == Side.SERVER) {
            PacketDispatcher.sendPacketToAllAround(x, y, z, 64, player.worldObj.getWorldInfo().getDimension(), new ModPacketUpdateSettings(modID, x, y, z).makePacket());
        }
    }
}
