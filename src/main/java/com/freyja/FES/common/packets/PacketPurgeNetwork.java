package com.freyja.FES.common.packets;

import com.freyja.FES.common.Network.RoutingEntity;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class PacketPurgeNetwork extends BasePacket {

    protected int x;
    protected int y;
    protected int z;

    public PacketPurgeNetwork()
    {

    }

    public PacketPurgeNetwork(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    protected void write(ByteArrayDataOutput output)
    {
        output.writeInt(x);
        output.writeInt(y);
        output.writeInt(z);
    }

    @Override
    public void read(ByteArrayDataInput input)
    {
        this.x = input.readInt();
        this.y = input.readInt();
        this.z = input.readInt();
    }

    @Override
    public void execute(EntityPlayer player, Side side)
    {
        if (side == Side.CLIENT) {
            TileEntity tileEntity = player.worldObj.getBlockTileEntity(x, y, z);

            if (tileEntity instanceof RoutingEntity) {
                ((RoutingEntity) tileEntity).getNetwork().purgeNetwork(tileEntity);
            }
        }
    }
}
