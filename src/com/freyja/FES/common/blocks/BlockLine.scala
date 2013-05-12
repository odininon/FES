package com.freyja.FES.common.blocks

import com.freyja.FES.common.inventories.TileEntityLine
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class BlockLine(blockId: Int, material: Material) extends BlockContainer(blockId, material) {

  def createNewTileEntity(world: World): TileEntity = new TileEntityLine

  override def breakBlock(world: World, x: Int, y: Int, z: Int, par5: Int, par6: Int) {
    val te = world.getBlockTileEntity(x, y, z).asInstanceOf[TileEntityLine]

    for (entity <- te.getNetwork.getAll) {
      entity.getNetwork.remove(te)
    }


    super.breakBlock(world, x, y, z, par5, par6)
  }
}

