package com.freyja.FES.common.blocks

import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.world.World
import net.minecraft.tileentity.TileEntity
import com.freyja.FES.common.inventories.TileEntityPlayerInventory
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.client.renderer.texture.IconRegister

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class BlockPlayerInventory(id: Int, material: Material) extends BlockContainer(id, material) {

  override def createNewTileEntity(world: World): TileEntity = new TileEntityPlayerInventory

  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, par6: Int, par7: Float, par8: Float, par9: Float): Boolean = {
    val te = world.getBlockTileEntity(x, y, z).asInstanceOf[TileEntityPlayerInventory]

    te.eatInventory(player.inventory)
    true
  }

  override def registerIcons(par1IconRegister: IconRegister) {
    this.blockIcon = par1IconRegister.registerIcon("FES:player_side")
  }
}
