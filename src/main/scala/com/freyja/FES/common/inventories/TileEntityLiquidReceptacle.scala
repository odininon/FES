package com.freyja.FES.common.inventories

import com.freyja.FES.common.Network.LiquidRoutingEntity
import scala.collection.mutable.ListBuffer
import net.minecraft.tileentity.TileEntity
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.packet.{Packet132TileEntityData, Packet}
import com.freyja.FES.RoutingSettings.{NoneSetting, DefaultRoutingSetting, LiquidSortSettings, RoutingSettingsRegistry}
import net.minecraft.network.INetworkManager
import net.minecraftforge.common.ForgeDirection
import com.freyja.FES.utils.Position
import net.minecraftforge.liquids.{LiquidStack, ITankContainer}

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class TileEntityLiquidReceptacle extends LiquidRoutingEntity {
  private var otherInventories: ListBuffer[TileEntity] = ListBuffer.empty[TileEntity]

  add(this)

  override def updateEntity() {
    if (!initialized) initRotate(this)
    if (worldObj.getTotalWorldTime % 10L == 0L) {
      updateConnections()
      inject()
    }
  }

  override def writeToNBT(par1NBTTagCompound: NBTTagCompound) {
    super.writeToNBT(par1NBTTagCompound)
    writeCustomNBT(par1NBTTagCompound)
  }


  override def readFromNBT(par1NBTTagCompound: NBTTagCompound) {
    super.readFromNBT(par1NBTTagCompound)
    readCustomNBT(par1NBTTagCompound)
  }

  override def getDescriptionPacket: Packet = {
    val tag: NBTTagCompound = new NBTTagCompound()
    writeCustomNBT(tag)
    new Packet132TileEntityData(xCoord, yCoord, zCoord, 1, tag)
  }

  def writeCustomNBT(tag: NBTTagCompound) {
    tag.setInteger("Orientation", orientation.ordinal())
    tag.setBoolean("Initialized", initialized)
    tag.setInteger("RoutingSettings", RoutingSettingsRegistry.Instance().indexOf(routingSettings, RoutingSettingsRegistry.Type.LIQUID))
  }

  override def onDataPacket(net: INetworkManager, pkt: Packet132TileEntityData) {
    readCustomNBT(pkt.customParam1)
  }

  def readCustomNBT(tag: NBTTagCompound) {
    orientation = ForgeDirection.getOrientation(tag.getInteger("Orientation"))
    initialized = tag.getBoolean("Initialized")
    routingSettings = RoutingSettingsRegistry.Instance().getRoutingSetting(tag.getInteger("RoutingSettings"), RoutingSettingsRegistry.Type.LIQUID)
  }

  def updateConnections() {
    var pos = new Position(this, orientation)
    pos.moveForwards(1)

    var te = worldObj.getBlockTileEntity(pos.x.toInt, pos.y.toInt, pos.z.toInt)

    te match {
      case null => if (this.connectedInventory != null) this.connectedInventory = null
      case te: ITankContainer => if (this.connectedInventory != te) this.connectedInventory = te
      case _ => None
    }

    for (direction <- ForgeDirection.values(); if !direction.eq(orientation)) {
      pos = new Position(this, direction)
      pos.moveForwards(1)

      te = worldObj.getBlockTileEntity(pos.x.toInt, pos.y.toInt, pos.z.toInt)

      te match {
        case null => None
        case te: ITankContainer => if (!otherInventories.contains(te) && !te.equals(connectedInventory)) otherInventories += te
        case te: LiquidRoutingEntity => if (te.isInstanceOf[TileEntityLiquidLine] && !this.getNetwork.equals(te.getNetwork)) {
          this.getNetwork.mergeNetworks(te.getNetwork)
          te.getNetwork.mergeNetworks(this.getNetwork)
        }
        case _ => None
      }
    }
  }

  def clearOthers() {
    otherInventories.clear()
  }

  def addLiquid(liquid: LiquidStack): Int = {
    connectedInventory match {
      case te: ITankContainer => {
        return te.fill(orientation.getOpposite, liquid, true)
      }
    }
    0
  }

  def canAccept(stack: LiquidStack): Boolean = {
    if (connectedInventory == null) return false

    var flag = false

    connectedInventory match {
      case te: ITankContainer => {
        if (te.fill(orientation.getOpposite, stack, false) > 0) flag = true
      }
    }

    val setting = getSettings match {
      case s: LiquidSortSettings => s.isLiquidValid(stack)
      case s: DefaultRoutingSetting => true
      case s: NoneSetting => false
    }

    flag && setting
  }

  def inject() {
    for (inventory <- otherInventories) {
      injectItems(inventory)
    }
  }
}
