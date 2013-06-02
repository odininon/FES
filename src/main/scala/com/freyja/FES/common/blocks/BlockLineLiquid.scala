package com.freyja.FES.common.blocks

import com.freyja.FES.common.inventories._
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.common.ForgeDirection
import com.freyja.FES.client.renderers.RenderLineLiquid
import com.freyja.FES.common.Network.LiquidRoutingEntity
import com.freyja.FES.utils.Position
import cpw.mods.fml.common.network.PacketDispatcher
import com.freyja.FES.common.packets.PacketPurgeNetwork
import cpw.mods.fml.relauncher.{Side, SideOnly}

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class BlockLineLiquid(blockId: Int, material: Material) extends BlockContainer(blockId, material) {

  def createNewTileEntity(world: World): TileEntity = new TileEntityLiquidLine

  override def breakBlock(world: World, x: Int, y: Int, z: Int, par5: Int, par6: Int) {
    if (!world.isRemote) {
      val te = world.getBlockTileEntity(x, y, z).asInstanceOf[TileEntityLiquidLine]

      for (entity <- te.getNetwork.getAll) {
        entity.getNetwork.purgeNetwork(entity)
        PacketDispatcher.sendPacketToAllAround(x, y, z, 64, world.getWorldInfo.getDimension, new PacketPurgeNetwork(entity.xCoord, entity.yCoord, entity.zCoord).makePacket())
      }
    }

    super.breakBlock(world, x, y, z, par5, par6)
  }

  @SideOnly(Side.CLIENT)
  override def renderAsNormalBlock(): Boolean = false

  @SideOnly(Side.CLIENT)
  override def isOpaqueCube: Boolean = false

  @SideOnly(Side.CLIENT)
  override def getRenderType: Int = RenderLineLiquid.renderId

  def canConnectOnSide(world: IBlockAccess, direction: Int, x: Int, y: Int, z: Int): Boolean = {
    val position = new Position(x, y, z, ForgeDirection.getOrientation(direction))
    position.moveForwards(1)

    val tileEntity = world.getBlockTileEntity(position.x.toInt, position.y.toInt, position.z.toInt)

    tileEntity match {
      case x: TileEntityLiquidInjector => !x.getOrientation.eq(ForgeDirection.getOrientation(direction).getOpposite)
      case x: TileEntityLiquidReceptacle => !x.getOrientation.eq(ForgeDirection.getOrientation(direction).getOpposite)
      case x: LiquidRoutingEntity => true
      case _ => false
    }
  }

  override def onNeighborBlockChange(world: World, x: Int, y: Int, z: Int, par5: Int) {
    world.markBlockForUpdate(x, y, z)
  }

  override def setBlockBoundsBasedOnState(world: IBlockAccess, x: Int, y: Int, z: Int) {
    val quarter: Float = (1F / 16F) * 4F
    val eighth: Float = (1F / 16F) * 2F

    var xMin: Float = quarter + eighth
    var xMax: Float = 1F - (quarter + eighth)

    var yMin: Float = xMin
    var yMax: Float = xMax

    var zMin: Float = xMin
    var zMax: Float = xMax

    if (canConnectOnSide(world, 0, x, y, z)) yMin = 0F
    if (canConnectOnSide(world, 1, x, y, z)) yMax = 1F
    if (canConnectOnSide(world, 2, x, y, z)) zMin = 0F
    if (canConnectOnSide(world, 3, x, y, z)) zMax = 1F
    if (canConnectOnSide(world, 4, x, y, z)) xMin = 0F
    if (canConnectOnSide(world, 5, x, y, z)) xMax = 1F


    setBlockBounds(xMin, yMin, zMin, xMax, yMax, zMax)
  }

}

