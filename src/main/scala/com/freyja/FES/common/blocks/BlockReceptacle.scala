package com.freyja.FES.common.blocks

import com.freyja.FES.common.inventories.TileEntityReceptacle
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraft.entity.player.EntityPlayer

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class BlockReceptacle(blockId: Int, material: Material) extends BlockContainer(blockId, material) {

  def createNewTileEntity(world: World): TileEntity = new TileEntityReceptacle

  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, par6: Int, par7: Float, par8: Float, par9: Float): Boolean = {
    val te = world.getBlockTileEntity(x, y, z).asInstanceOf[TileEntityReceptacle]
    te.rotate()

    world.notifyBlockChange(x + 1, y, z, this.blockId)
    world.notifyBlockChange(x - 1, y, z, this.blockId)
    world.notifyBlockChange(x, y + 1, z, this.blockId)
    world.notifyBlockChange(x, y - 1, z, this.blockId)
    world.notifyBlockChange(x, y, z + 1, this.blockId)
    world.notifyBlockChange(x, y, z - 1, this.blockId)

    player.swingItem()
    true
  }

  override def renderAsNormalBlock(): Boolean = true
  override def isOpaqueCube: Boolean = false
  override def getRenderType: Int = -1
  override def hasTileEntity: Boolean = true

  override def breakBlock(world: World, x: Int, y: Int, z: Int, par5: Int, par6: Int) {
    val te = world.getBlockTileEntity(x, y, z).asInstanceOf[TileEntityReceptacle]

    for (entity <- te.getNetwork.getAll) {
      entity.getNetwork.remove(te)
    }

    super.breakBlock(world, x, y, z, par5, par6)
  }
}
