package com.freyja.FES.common.Network

import com.freyja.FES.common.inventories.{TileEntityLine, TileEntityReceptacle, TileEntityInjector}

/**
 *
 * @author odininon
 */
class RoutingNetwork {
  private var injectors = List[TileEntityInjector]()
  private var receptacles = List[TileEntityReceptacle]()
  private var lines = List[TileEntityLine]()


  def add(thing: AnyRef) {
    thing match {
      case thing: TileEntityInjector => injectors ::= thing
      case thing: TileEntityReceptacle => receptacles ::= thing
      case thing: TileEntityLine => lines ::= thing
    }
  }

  def getInjectors: List[_] = {
    injectors
  }

  def getReceptacles: List[_] = {
    receptacles
  }

  def getLine: List[_] = {
    lines
  }
}
