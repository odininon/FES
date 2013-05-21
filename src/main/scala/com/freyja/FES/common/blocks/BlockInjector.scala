package com.freyja.FES.common.blocks

import com.freyja.FES.common.inventories.TileEntityInjector
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraft.entity.player.EntityPlayer
import cpw.mods.fml.common.network.PacketDispatcher

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class BlockInjector(blockId: Int, material: Material) extends BlockContainer(blockId, material) {

  override def createNewTileEntity(world: World): TileEntity = new TileEntityInjector

  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, par6: Int, par7: Float, par8: Float, par9: Float): Boolean = {

    if (!world.isRemote) {
      val te = world.getBlockTileEntity(x, y, z).asInstanceOf[TileEntityInjector]
      te.rotate(te)

      world.notifyBlockChange(x + 1, y, z, this.blockId)
      world.notifyBlockChange(x - 1, y, z, this.blockId)
      world.notifyBlockChange(x, y + 1, z, this.blockId)
      world.notifyBlockChange(x, y - 1, z, this.blockId)
      world.notifyBlockChange(x, y, z + 1, this.blockId)
      world.notifyBlockChange(x, y, z - 1, this.blockId)

      PacketDispatcher.sendPacketToAllAround(x, y, z, 64, player.dimension, te.getDescriptionPacket)
    }

    player.swingItem()
    true
  }

  override def renderAsNormalBlock(): Boolean = true

  override def isOpaqueCube: Boolean = false

  override def getRenderType: Int = -1

  override def hasTileEntity: Boolean = true

  override def breakBlock(world: World, x: Int, y: Int, z: Int, par5: Int, par6: Int) {
    val te = world.getBlockTileEntity(x, y, z).asInstanceOf[TileEntityInjector]

    for (entity <- te.getNetwork.getAll) {
      entity.getNetwork.remove(te)
    }

    super.breakBlock(world, x, y, z, par5, par6)
  }
}

