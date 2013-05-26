package com.freyja.FES.common.inventories

import net.minecraft.item.ItemStack
import java.lang.String
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.entity.player.{InventoryPlayer, EntityPlayer}
import net.minecraft.tileentity.TileEntity
import net.minecraft.inventory.IInventory

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

class TileEntityPlayerInventory extends TileEntity with IInventory {
  private val inventories: Array[ItemStack] = new Array[ItemStack](36)

  def getSizeInventory: Int = inventories.length

  def getStackInSlot(i: Int): ItemStack = inventories(i)

  def decrStackSize(i: Int, j: Int): ItemStack = {
    if (this.inventories(i) != null) {
      var itemstack: ItemStack = null
      if (this.inventories(i).stackSize <= j) {
        itemstack = this.inventories(i)
        this.inventories(i) = null
        itemstack
      }
      else {
        itemstack = this.inventories(i).splitStack(j)
        if (this.inventories(i).stackSize == 0) {
          this.inventories(i) = null
        }
        itemstack
      }
    }
    else {
      null
    }
  }

  def getStackInSlotOnClosing(i: Int): ItemStack = {
    if (this.inventories(i) != null) {
      val itemstack: ItemStack = this.inventories(i)
      this.inventories(i) = null
      itemstack
    }
    else {
      null
    }
  }

  def setInventorySlotContents(i: Int, itemstack: ItemStack) {
    this.inventories(i) = itemstack
    if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit) {
      itemstack.stackSize = this.getInventoryStackLimit
    }
  }

  def getInvName: String = "PlayerInventoryStealer"

  def isInvNameLocalized: Boolean = false

  def getInventoryStackLimit: Int = 64

  def isUseableByPlayer(entityplayer: EntityPlayer): Boolean = true

  def openChest() {
  }

  def closeChest() {
  }

  def isStackValidForSlot(i: Int, itemstack: ItemStack): Boolean = {
    true
  }

  override def readFromNBT(par1NBTTagCompound: NBTTagCompound) {
    super.readFromNBT(par1NBTTagCompound)
  }

  override def writeToNBT(par1NBTTagCompound: NBTTagCompound) {
    super.writeToNBT(par1NBTTagCompound)
  }

  def setEmptyToItemStack(stack: ItemStack): Boolean = {
    for (slot <- 0 until inventories.length) {
      if (getStackInSlot(slot) == null) {
        setInventorySlotContents(slot, stack)
        return true
      }
    }
    false
  }

  def eatInventory(player: InventoryPlayer) {
    for (playerSlot <- 9 until player.getSizeInventory) {
      if (player.getStackInSlot(playerSlot) != null && setEmptyToItemStack(player.getStackInSlot(playerSlot)))
        player.setInventorySlotContents(playerSlot, null)
    }
  }
}
