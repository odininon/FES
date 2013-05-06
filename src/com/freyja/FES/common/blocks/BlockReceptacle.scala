package com.freyja.FES.common.blocks

import com.freyja.FES.common.inventories.TileEntityReceptacle
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * @author odininon
 */
class BlockReceptacle(blockId: Int, material: Material) extends BlockContainer(blockId, material) {

  def createNewTileEntity(world: World): TileEntity = new TileEntityReceptacle
}

