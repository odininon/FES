package com.freyja.FES.common.blocks

import com.freyja.FES.common.inventories.TileEntityInjector
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraft.entity.player.EntityPlayer

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class BlockInjector(blockId: Int, material: Material) extends BlockContainer(blockId, material) {

  override def createNewTileEntity(world: World): TileEntity = new TileEntityInjector

  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, par6: Int, par7: Float, par8: Float, par9: Float): Boolean = {
    if (world.isRemote) {
      val te = world.getBlockTileEntity(x, y, z).asInstanceOf[TileEntityInjector]
      player.addChatMessage("Network: " + te.getNetwork)
      player.addChatMessage("Connections: " + te.getNetwork.count)
      player.addChatMessage("Inventories: " + te.getConnected)
      true
    }
    false
  }
}

