package com.freyja.FES.common.Network

import net.minecraft.item.ItemStack
import net.minecraft.inventory.IInventory
import net.minecraft.tileentity.TileEntity
import com.freyja.FES.RoutingSettings.RoutingSettingsRegistry
import scala.util.Random
import cpw.mods.fml.common.network.PacketDispatcher
import net.minecraftforge.liquids.{LiquidStack, ITankContainer}
import net.minecraftforge.common.ForgeDirection

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class LiquidRoutingEntity extends RoutingEntity {


  override protected val routingNetwork: LiquidRoutingNetwork = new LiquidRoutingNetwork()

  override def getNetwork: LiquidRoutingNetwork = routingNetwork

  def initRotate(tileEntity: TileEntity) {
    routingSettings = RoutingSettingsRegistry.Instance().getRoutingSetting(1)
    if (tileEntity.worldObj != null && !tileEntity.worldObj.isRemote) {

      val otherTE = ((for (i <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord + i, tileEntity.yCoord, tileEntity.zCoord)).toList :::
        (for (j <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord + j, tileEntity.zCoord)).toList :::
        (for (k <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord + k)).toList).flatMap(x => x match {
        case x: ITankContainer => if (x != this) Some(x) else None
        case _ => None
      })

      if (otherTE.isEmpty) return

      val entity = Random.shuffle(otherTE).head
      setOrientation(calculateDirection(entity, tileEntity))
      PacketDispatcher.sendPacketToAllAround(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 64, tileEntity.worldObj.getWorldInfo.getDimension, tileEntity.getDescriptionPacket)

      initialized = true
    }
  }

  def rotate(tileEntity: TileEntity) {
    val currentRotation = orientation.ordinal()

    val otherTE = ((for (i <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord + i, tileEntity.yCoord, tileEntity.zCoord)).toList :::
      (for (j <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord + j, tileEntity.zCoord)).toList :::
      (for (k <- -1 to 1) yield tileEntity.worldObj.getBlockTileEntity(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord + k)).toList).flatMap(x => x match {
      case x: ITankContainer => if (x != this) Some(x) else None
      case _ => None
    })

    if (otherTE.isEmpty || otherTE.length == 1) return

    var newRotation = calculateDirection(Random.shuffle(otherTE).head, tileEntity).ordinal()

    while (newRotation == currentRotation) {
      newRotation = calculateDirection(Random.shuffle(otherTE).head, tileEntity).ordinal()
    }

    orientation = ForgeDirection.VALID_DIRECTIONS(newRotation)
  }

  def injectItems(inventory: TileEntity) {
    if (inventory == null) return

    var maxCapacity: Int = 0
    var liquidStack: LiquidStack = null

    inventory match {
      case te: ITankContainer => {
        val tank = te.getTank(orientation.getOpposite, null)
        if (tank != null) maxCapacity = tank.getCapacity
        liquidStack = te.drain(orientation.getOpposite, maxCapacity, false)
      }
    }

    if (liquidStack == null) return

    getNetwork.injectLiquid(liquidStack, this)
  }

  def removeLiquid(stack: LiquidStack, amountFilled: Int) {
    connectedInventory match {
      case te: ITankContainer => te.drain(orientation.getOpposite, stack.amount, true)
    }
  }

  def removeItem(itemStack: ItemStack, slotNumber: Int, inventory: IInventory) {}
}
