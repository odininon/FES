package com.freyja.FES.common.blocks

import com.freyja.FES.common.inventories.TileEntityReceptacle
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraft.entity.player.EntityPlayer
import cpw.mods.fml.common.network.PacketDispatcher
import com.freyja.FES.common.Network.RoutingEntity
import com.freyja.FES.common.packets.PacketPurgeNetwork
import cpw.mods.fml.client.FMLClientHandler
import com.freyja.FES.client.gui.RoutingSettings
import com.freyja.FES.FES
import cpw.mods.fml.relauncher.{Side, SideOnly}

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class BlockReceptacle(blockId: Int, material: Material) extends BlockContainer(blockId, material) {

  def createNewTileEntity(world: World): TileEntity = new TileEntityReceptacle

  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, par6: Int, par7: Float, par8: Float, par9: Float): Boolean = {

    if (!world.isRemote) {
      if (player.isSneaking) {
        val te = world.getBlockTileEntity(x, y, z).asInstanceOf[TileEntityReceptacle]
        te.rotate(te)

        world.notifyBlockChange(x + 1, y, z, this.blockId)
        world.notifyBlockChange(x - 1, y, z, this.blockId)
        world.notifyBlockChange(x, y + 1, z, this.blockId)
        world.notifyBlockChange(x, y - 1, z, this.blockId)
        world.notifyBlockChange(x, y, z + 1, this.blockId)
        world.notifyBlockChange(x, y, z - 1, this.blockId)

        PacketDispatcher.sendPacketToAllAround(x, y, z, 64, player.dimension, te.getDescriptionPacket)
        player.swingItem()
      }
    } else {
      if (!player.isSneaking) {
        FES.proxy.openLocalGui(0, x, y, z)
      }
    }
    true
  }

  @SideOnly(Side.CLIENT)
  override def renderAsNormalBlock(): Boolean = true

  @SideOnly(Side.CLIENT)
  override def isOpaqueCube: Boolean = false

  @SideOnly(Side.CLIENT)
  override def getRenderType: Int = -1

  override def hasTileEntity: Boolean = true

  override def breakBlock(world: World, x: Int, y: Int, z: Int, par5: Int, par6: Int) {
    if (!world.isRemote) {
      val te = world.getBlockTileEntity(x, y, z).asInstanceOf[RoutingEntity]

      for (entity <- te.getNetwork.getAll) {
        entity.getNetwork.purgeNetwork(entity)
        PacketDispatcher.sendPacketToAllAround(x, y, z, 64, world.getWorldInfo.getDimension, new PacketPurgeNetwork(entity.xCoord, entity.yCoord, entity.zCoord).makePacket())
      }
    }

    super.breakBlock(world, x, y, z, par5, par6)
  }
}

