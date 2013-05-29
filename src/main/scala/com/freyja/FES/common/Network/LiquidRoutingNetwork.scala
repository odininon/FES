package com.freyja.FES.common.Network

import net.minecraft.tileentity.TileEntity
import scala.collection.mutable.ListBuffer
import com.freyja.FES.common.inventories.{TileEntityLiquidLine, TileEntityLiquidReceptacle, TileEntityLiquidInjector}
import net.minecraftforge.liquids.LiquidStack
import scala.util.Random

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
class LiquidRoutingNetwork extends RoutingNetwork {

  private var injectors = ListBuffer.empty[TileEntityLiquidInjector]
  private var receptacles = ListBuffer.empty[TileEntityLiquidReceptacle]
  private var lines = ListBuffer.empty[TileEntityLiquidLine]

  def mergeNetworks(network: RoutingNetwork) {
    for (injector <- network.getInjectors) add(injector)
    for (line <- network.getLines) add(line)
    for (receptacle <- network.getReceptacles) add(receptacle)
  }


  override def add(obj: Any) {
    obj match {
      case te: TileEntityLiquidInjector => if (!injectors.contains(te)) injectors += te
      case te: TileEntityLiquidReceptacle => if (!receptacles.contains(te)) receptacles += te
      case te: TileEntityLiquidLine => if (!lines.contains(te)) lines += te
    }
  }

  override def getAll = {
    val list = ListBuffer.empty[RoutingEntity]
    for (injector <- getInjectors) list += injector
    for (receptacle <- getReceptacles) list += receptacle
    for (line <- getLines) list += line
    list
  }

  def purgeNetwork(te: TileEntity) {
    injectors.clear()
    receptacles.clear()
    lines.clear()
    this.add(te)
  }

  override def count = injectors.size

  override def getInjectors = injectors

  override def getReceptacles = receptacles

  override def getLines = lines

  def hasValidRoute(stack: LiquidStack): Boolean = {
    for (receptacle <- getReceptacles) {
      if (receptacle.canAccept(stack)) return true
    }
    false
  }

  def getValidReceptacles(stack: LiquidStack): List[TileEntityLiquidReceptacle] = {
    val list = ListBuffer.empty[TileEntityLiquidReceptacle]

    for (receptacle <- getReceptacles)
      if (receptacle.canAccept(stack)) list += receptacle

    list.toList
  }

  def injectLiquid(stack: LiquidStack, injector: LiquidRoutingEntity) {
    if (hasValidRoute(stack)) {

      val receptacle = (Random.shuffle(getValidReceptacles(stack))).head
      val amountFilled = receptacle.addLiquid(stack)

      injector.removeLiquid(stack, amountFilled)
    }
  }
}
