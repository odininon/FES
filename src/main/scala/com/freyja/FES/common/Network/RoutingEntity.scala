package com.freyja.FES.common.Network

import net.minecraft.inventory.IInventory
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.ForgeDirection
import net.minecraft.item.ItemStack
import com.freyja.FES.RoutingSettings.{IRoutingSetting, RoutingSettingsRegistry}

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
trait RoutingEntity extends TileEntity {
  protected val routingNetwork: RoutingNetwork = null
  protected var initialized = false
  protected var orientation: ForgeDirection = ForgeDirection.UP

  protected var connectedInventory: TileEntity = null
  protected var pullItemStacks = true

  protected var routingSettings = RoutingSettingsRegistry.Instance().getRoutingSetting(0)

  def getNetwork = routingNetwork

  def defaultNetwork() {
    clearNetwork()
    add(this)
  }

  def clearNetwork() {}

  def add(obj: this.type) {
    routingNetwork.add(obj)
  }

  def initRotate(tileEntity: TileEntity)

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

  def relativeDirections(entity: TileEntity, injector: TileEntity): ForgeDirection = ForgeDirection.UNKNOWN

  def rotate(tileEntity: TileEntity)

  def getConnected = connectedInventory

  def injectItems(inventory: TileEntity)

  def removeItem(itemStack: ItemStack, slotNumber: Int, inventory: IInventory)

  def setSettings(settings: IRoutingSetting) {
    routingSettings = settings
  }

  def getSettings = routingSettings
}
