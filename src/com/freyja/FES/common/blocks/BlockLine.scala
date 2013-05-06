package com.freyja.FES.common.blocks

import com.freyja.FES.common.inventories.TileEntityLine
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * @author odininon
 */
class BlockLine(blockId: Int, material: Material) extends BlockContainer(blockId, material) {

  def createNewTileEntity(world: World): TileEntity = new TileEntityLine
}

