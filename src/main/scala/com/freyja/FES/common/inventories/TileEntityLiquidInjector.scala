package com.freyja.FES.common.inventories

import com.freyja.FES.common.Network.{ItemRoutingEntity, LiquidRoutingEntity}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.packet.{Packet132TileEntityData, Packet}
import com.freyja.FES.RoutingSettings.RoutingSettingsRegistry
import net.minecraft.network.INetworkManager
import net.minecraftforge.common.ForgeDirection
import com.freyja.FES.utils.Position
import net.minecraftforge.liquids.ITankContainer

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class TileEntityLiquidInjector extends LiquidRoutingEntity {
  add(this)

  override def updateEntity() {
    if (!initialized) initRotate(this)
    if (worldObj.getTotalWorldTime % 10L == 0L) {
      updateConnections()
      injectItems(getConnected)
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
    tag.setBoolean("PullItemStacks", pullItemStacks)
    tag.setBoolean("Initialized", initialized)
    tag.setInteger("RoutingSettings", RoutingSettingsRegistry.Instance().indexOf(routingSettings))
  }

  override def onDataPacket(net: INetworkManager, pkt: Packet132TileEntityData) {
    readCustomNBT(pkt.customParam1)
  }

  def readCustomNBT(tag: NBTTagCompound) {
    orientation = ForgeDirection.getOrientation(tag.getInteger("Orientation"))
    pullItemStacks = tag.getBoolean("PullItemStacks")
    initialized = tag.getBoolean("Initialized")
    routingSettings = RoutingSettingsRegistry.Instance().getRoutingSetting(tag.getInteger("RoutingSettings"))
  }

  def updateConnections() {
    var pos = new Position(this, orientation)
    pos.moveForwards(1)

    var te = worldObj.getBlockTileEntity(pos.x.toInt, pos.y.toInt, pos.z.toInt)

    te match {
      case null => if (this.connectedInventory != null) this.connectedInventory = null
      case x: ITankContainer => if (this.connectedInventory != te) this.connectedInventory = te
      case _ => None
    }

    for (direction <- ForgeDirection.values(); if !direction.eq(orientation)) {
      pos = new Position(this, direction)
      pos.moveForwards(1)

      te = worldObj.getBlockTileEntity(pos.x.toInt, pos.y.toInt, pos.z.toInt)

      te match {
        case null => None
        case te: LiquidRoutingEntity => if (te.isInstanceOf[TileEntityLiquidLine] && !this.getNetwork.equals(te.getNetwork)) {
          this.getNetwork.mergeNetworks(te.getNetwork)
          te.getNetwork.mergeNetworks(this.getNetwork)
        }
        case _ => None
      }
    }
  }
}
