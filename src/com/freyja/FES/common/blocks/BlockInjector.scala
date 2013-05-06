package com.freyja.FES.common.blocks

import com.freyja.FES.common.inventories.TileEntityInjector
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import com.freyja.FES.common.Network.RoutingEntity
import net.minecraft.entity.player.EntityPlayer

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class BlockInjector(blockId: Int, material: Material) extends BlockContainer(blockId, material) {

  override def createNewTileEntity(world: World): TileEntity = new TileEntityInjector

  override def breakBlock(world: World, x: Int, y: Int, z: Int, par5: Int, par6: Int) {
    val te = world.getBlockTileEntity(x, y, z).asInstanceOf[TileEntityInjector]
    te.getNetwork.remove(te)

    super.breakBlock(world, x, y, z, par5, par6)
  }

  override def onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, meta: Int) {
    val te = world.getBlockTileEntity(x, y, z).asInstanceOf[TileEntityInjector]

    val otherTe = (for (
      i <- -1 to 1;
      j <- -1 to 1;
      k <- -1 to 1
    ) yield world.getBlockTileEntity(x + i, y + j, z + k)).toList.flatMap(x => x match {
      case `te` => None
      case x: RoutingEntity => Some(x)
      case _ => None
    })
    if (otherTe == null) return

    for (entity <- otherTe) {
      te.changeNetwork(te.getNetwork.mergeNetworks(entity.getNetwork))
      entity.changeNetwork(te.getNetwork)
    }
  }

  override def onBlockActivated(world: World, x: Int, y: Int, z: Int, player: EntityPlayer, par6: Int, par7: Float, par8: Float, par9: Float): Boolean = {
    val te = world.getBlockTileEntity(x, y, z).asInstanceOf[RoutingEntity]

    if (world.isRemote)
      player.addChatMessage(te.networkInfo)
    true
  }
}

