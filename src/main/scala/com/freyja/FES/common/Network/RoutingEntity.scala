package com.freyja.FES.common.Network

import net.minecraft.inventory.IInventory
import scala.util.Random
import cpw.mods.fml.common.network.PacketDispatcher
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.ForgeDirection

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
trait RoutingEntity {
  private val routingNetwork: RoutingNetwork = new RoutingNetwork()
  protected var initialized = false
  protected var orientation: ForgeDirection = ForgeDirection.UP

  def getNetwork = routingNetwork

  def defaultNetwork() {
    clearNetwork()
    add(this)
  }

  def clearNetwork() {}

  def add(obj: this.type) {
    routingNetwork.add(obj)
  }

  def initRotate(tileEntity: TileEntity) {
    if (tileEntity.worldObj != null && !tileEntity.worldObj.isRemote) {

      val otherTE = ((for (i <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord + i, tileEntity.yCoord, tileEntity.zCoord)).toList :::
        (for (j <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord + j, tileEntity.zCoord)).toList :::
        (for (k <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord + k)).toList).flatMap(x => x match {
        case x: IInventory => if (x != this) Some(x) else None
        case _ => None
      })

      if (otherTE.isEmpty) return

      val entity = Random.shuffle(otherTE).head

      if (entity != null) {
        setOrientation(calculateDirection(entity, tileEntity))
        PacketDispatcher.sendPacketToAllAround(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 64, tileEntity.worldObj.getWorldInfo.getDimension, tileEntity.getDescriptionPacket)
      }

      initialized = true
    }
  }

  def getOrientation: ForgeDirection = {
    orientation
  }

  def setOrientation(direction: ForgeDirection) {
    orientation = direction
  }

  def calculateDirection(entity: TileEntity, injector: TileEntity): ForgeDirection = {
    val dx = -injector.xCoord + entity.xCoord
    val dy = -injector.yCoord + entity.yCoord
    val dz = -injector.zCoord + entity.zCoord

    if (dx >= 1 && dy == 0 && dz == 0) return ForgeDirection.EAST
    if (dx <= 1 && dy == 0 && dz == 0) return ForgeDirection.WEST
    if (dx == 0 && dy >= 1 && dz == 0) return ForgeDirection.UP
    if (dx == 0 && dy <= 1 && dz == 0) return ForgeDirection.DOWN
    if (dx == 0 && dy == 0 && dz >= 1) return ForgeDirection.SOUTH
    if (dx == 0 && dy == 0 && dz <= 1) return ForgeDirection.NORTH

    ForgeDirection.UNKNOWN
  }

  def rotate(tileEntity: TileEntity) {
    val currentRotation = orientation.ordinal()

    val otherTE = ((for (i <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord + i, tileEntity.yCoord, tileEntity.zCoord)).toList :::
      (for (j <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord + j, tileEntity.zCoord)).toList :::
      (for (k <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord + k)).toList).flatMap(x => x match {
      case x: IInventory => if (x != this) Some(x) else None
      case _ => None
    })

    if (otherTE.isEmpty || otherTE.length == 1) return

    var newRotation = calculateDirection(Random.shuffle(otherTE).head, tileEntity).ordinal()

    while (newRotation == currentRotation) {
      newRotation = calculateDirection(Random.shuffle(otherTE).head, tileEntity).ordinal()
    }

    orientation = ForgeDirection.VALID_DIRECTIONS(newRotation)
  }
}
