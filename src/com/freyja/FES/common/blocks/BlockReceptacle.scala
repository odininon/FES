package com.freyja.FES.common.blocks

import net.minecraft.block.material.Material
import net.minecraft.block.Block
import net.minecraft.world.World
import net.minecraft.tileentity.TileEntity
import com.freyja.FES.common.inventory.TileEntityReceptacle

/**
 *
 * @user Freyja
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 *
 */
class BlockReceptacle(blockId: Int, material: Material) extends Block(blockId, material) {
  override def createTileEntity(world: World, metadata: Int): TileEntity = new TileEntityReceptacle
}
