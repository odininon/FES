package com.freyja.FES.common.inventories

import net.minecraft.tileentity.TileEntity
import com.freyja.FES.common.Network.RoutingEntity
import com.freyja.FES.common.utils.Position
import net.minecraftforge.common.ForgeDirection
import net.minecraft.inventory.{ISidedInventory, IInventory}
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.{Packet132TileEntityData, Packet}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.INetworkManager

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class TileEntityInjector extends TileEntity with RoutingEntity {
  private var orientation: ForgeDirection = ForgeDirection.UP
  private var connectedInventory: IInventory = null

  private var pullItemStacks = true

  add(this)

  override def updateEntity() {
    if (worldObj.getTotalWorldTime % 10L == 0L) {
      updateConnections()
      injectItems()
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
  }

  override def onDataPacket(net: INetworkManager, pkt: Packet132TileEntityData) {
    readCustomNBT(pkt.customParam1)
  }

  def readCustomNBT(tag: NBTTagCompound) {
    orientation = ForgeDirection.getOrientation(tag.getInteger("Orientation"))
    pullItemStacks = tag.getBoolean("PullItemStacks")
  }

  def getConnected = connectedInventory

  def updateConnections() {
    val pos = new Position(this, orientation)
    pos.moveForwards(1)

    var te = worldObj.getBlockTileEntity(pos.x.toInt, pos.y.toInt, pos.z.toInt)

    te match {
      case null => if (this.connectedInventory != null) this.connectedInventory = null
      case te: IInventory => if (this.connectedInventory != te) this.connectedInventory = te
      case _ => None
    }

    pos.moveBackwards(2)

    te = worldObj.getBlockTileEntity(pos.x.toInt, pos.y.toInt, pos.z.toInt)

    te match {
      case null => None
      case te: RoutingEntity => if (!te.isInstanceOf[TileEntityInjector] && !this.getNetwork.equals(te.getNetwork)) {
        this.getNetwork.mergeNetworks(te.getNetwork)
        te.getNetwork.mergeNetworks(this.getNetwork)
      }
      case _ => None
    }
  }

  def removeItem(itemStack: ItemStack, slotNumber: Int) {
    getConnected.decrStackSize(slotNumber, itemStack.stackSize)
    getConnected.onInventoryChanged()
  }

  def injectItems() {
    if (getConnected == null) return

    getConnected match {
      case x: ISidedInventory => {
        for (slot <- x.getSizeInventorySide(orientation.getOpposite.ordinal())) {
          val tempStack = getConnected.getStackInSlot(slot)
          if (tempStack != null) {
            val itemStack = tempStack.copy
            itemStack.stackSize = pullItemStacks match {
              case false => 1
              case true => tempStack.stackSize
            }
            val canExtract = x.func_102008_b(slot, itemStack, orientation.getOpposite.ordinal())
            if (canExtract && itemStack != null && getNetwork.injectItemStack(itemStack, this, slot)) return
          }
        }
      }
      case x: IInventory => {
        for (slot <- 0 until x.getSizeInventory) {
          val tempStack = getConnected.getStackInSlot(slot)
          if (tempStack != null) {
            val itemStack = tempStack.copy
            itemStack.stackSize = pullItemStacks match {
              case false => 1
              case true => tempStack.stackSize
            }
            val canExtract = true
            if (canExtract && itemStack != null && getNetwork.injectItemStack(itemStack, this, slot)) return
          }
        }
      }
    }
  }

  def getOrientation: ForgeDirection = {
    orientation
  }

  def rotate() {
    val currentRotation = orientation.ordinal()
    var newRotation = currentRotation + 1

    if (newRotation >= ForgeDirection.VALID_DIRECTIONS.length) newRotation = 0

    orientation = ForgeDirection.VALID_DIRECTIONS(newRotation)

  }
}