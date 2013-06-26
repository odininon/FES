package com.freyja.FES

import cpw.mods.fml.common.{FMLCommonHandler, FMLLog, Mod}
import cpw.mods.fml.common.network.{NetworkRegistry, NetworkMod}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.block.Block
import cpw.mods.fml.common.Mod.{PostInit, Init, PreInit}
import cpw.mods.fml.common.event.{FMLPostInitializationEvent, FMLInitializationEvent, FMLPreInitializationEvent}
import net.minecraft.block.material.Material
import cpw.mods.fml.common.registry.{LanguageRegistry, GameRegistry}
import java.util.logging.Logger
import com.freyja.FES.common.CommonProxy
import cpw.mods.fml.relauncher.Side
import net.minecraft.item.Item
import com.freyja.FES.common.blocks._
import com.freyja.FES.common.inventories._
import com.freyja.FES.utils.{ModCompatibility, Utils}
import com.freyja.FES.common.packets.PacketHandler
import com.freyja.FES.RoutingSettings.{NoneSetting, SmeltablesSettings, DefaultRoutingSetting, RoutingSettingsRegistry}
import net.minecraftforge.common.Configuration

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */

@Mod(name = "Freyja's Easy Sorting", modid = "FES", modLanguage = "scala", useMetadata = true)
@NetworkMod(channels = Array[String] {
  "FES"
}, clientSideRequired = true, serverSideRequired = false, packetHandler = classOf[PacketHandler])
object FES {
  private val _creativeTab: CreativeTabs = new CreativeTabs("FES")
  private val _logger: Logger = Logger.getLogger("FES")
  private var _config: Configuration = null

  def creativeTab = _creativeTab

  def logger = _logger

  def config = _config


  private val _proxy = findProxy()

  def proxy = _proxy


  /**
   * Blocks
   */
  var blockReceptacle: Block = null
  var blockInjector: Block = null
  var blockLine: Block = null
  var blockPlayer: Block = null
  var blockLiquidInjector: Block = null
  var blockLiquidReceptacle: Block = null
  var blockLiquidLine: Block = null

  /**
   * Ids
   */
  var blockReceptacleId: Int = 0
  var blockInjectorId: Int = 0
  var blockLineId: Int = 0
  var blockPlayerId: Int = 0
  var blockLiquidInjectorId: Int = 0
  var blockLiquidReceptacleId: Int = 0
  var blockLiquidLineId: Int = 0

  def initConfigurations(event: FMLPreInitializationEvent): Configuration = {
    val config: Configuration = new Configuration(event.getSuggestedConfigurationFile)

    try {
      config.load()
      blockReceptacleId = config.getBlock("Receptacle", 2000).getInt(2000)
      blockInjectorId = config.getBlock("Injector", 2001).getInt(2001)
      blockLineId = config.getBlock("Line", 2002).getInt(2002)
      blockPlayerId = config.getBlock("Player", 2003).getInt(2003)

      blockLiquidInjectorId = config.getBlock("LiquidInjector", 2004).getInt(2004)
      blockLiquidReceptacleId = config.getBlock("LiquidReceptacle", 2005).getInt(2005)
      blockLiquidLineId = config.getBlock("LiquidLine", 2006).getInt(2006)

    } catch {
      case e: Exception => logger.warning("Failed to load configurations.")
    }
    finally {
      if (config.hasChanged) config.save()
    }
    config
  }

  private def findProxy(): CommonProxy = {
    FMLCommonHandler.instance().getEffectiveSide match {
      case Side.CLIENT => Class.forName("com.freyja.FES.client.ClientProxy").newInstance().asInstanceOf[CommonProxy]
      case _ => new CommonProxy
    }
  }

  @PreInit
  def preInit(event: FMLPreInitializationEvent) {
    logger.setParent(FMLLog.getLogger)
    _config = initConfigurations(event)

    blockReceptacle = new BlockReceptacle(blockReceptacleId, Material.circuits).setCreativeTab(creativeTab).setUnlocalizedName("FES:receptacle")
    registerObject(blockReceptacle, "Receptacle")
    GameRegistry.registerTileEntity(classOf[TileEntityItemReceptacle], "Receptacle")

    blockInjector = new BlockInjector(blockInjectorId, Material.circuits).setCreativeTab(creativeTab).setUnlocalizedName("FES:injector")
    registerObject(blockInjector, "Injector")
    GameRegistry.registerTileEntity(classOf[TileEntityItemInjector], "Injector")

    blockLine = new BlockLine(blockLineId, Material.circuits).setCreativeTab(creativeTab).setUnlocalizedName("FES:line")
    registerObject(blockLine, "Line")
    GameRegistry.registerTileEntity(classOf[TileEntityItemLine], "Line")

    blockPlayer = new BlockPlayerInventory(blockPlayerId, Material.circuits).setCreativeTab(creativeTab).setUnlocalizedName("FES:player")
    registerObject(blockPlayer, "Player")
    GameRegistry.registerTileEntity(classOf[TileEntityPlayerInventory], "Player")

    blockLiquidInjector = new BlockInjectorLiquid(blockLiquidInjectorId, Material.circuits).setCreativeTab(creativeTab).setUnlocalizedName("FES:liquidinejctor")
    registerObject(blockLiquidInjector, "Liquid Injector")
    GameRegistry.registerTileEntity(classOf[TileEntityLiquidInjector], "LiquidInjector")

    blockLiquidReceptacle = new BlockReceptacleLiquid(blockLiquidReceptacleId, Material.circuits).setCreativeTab(creativeTab).setUnlocalizedName("FES:liquidreceptacle")
    registerObject(blockLiquidReceptacle, "Liquid Receptacle")
    GameRegistry.registerTileEntity(classOf[TileEntityLiquidReceptacle], "LiquidReceptacle")

    blockLiquidLine = new BlockLineLiquid(blockLiquidLineId, Material.circuits).setCreativeTab(creativeTab).setUnlocalizedName("FES:liquidline")
    registerObject(blockLiquidLine, "Liquid Line")
    GameRegistry.registerTileEntity(classOf[TileEntityLiquidLine], "LiquidLine")
  }


  def registerObject(registry: AnyRef, name: String) {
    logger.info("Registering " + registry + " with name " + name)

    registry match {
      case registry: Block => {
        GameRegistry.registerBlock(registry, registry.getUnlocalizedName.substring(registry.getUnlocalizedName.lastIndexOf(".") + 1))
        LanguageRegistry.addName(registry, name)
      }
      case registry: Item => {
        GameRegistry.registerItem(registry, registry.getUnlocalizedName.substring(registry.getUnlocalizedName.lastIndexOf(".") + 1))
        LanguageRegistry.addName(registry, name)
      }
      case _ => logger.info("Tried to register something unknown.")
    }
  }

  @Init
  def init(event: FMLInitializationEvent) {
    NetworkRegistry.instance().registerGuiHandler(this, proxy)
    proxy.registerTESRS()

    Utils.RegisterRecipes()
  }

  @PostInit
  def postInit(event: FMLPostInitializationEvent) {
    RoutingSettingsRegistry.Instance().registerRoutingSetting(new NoneSetting(), RoutingSettingsRegistry.Type.BOTH)
    RoutingSettingsRegistry.Instance().registerRoutingSetting(new DefaultRoutingSetting(), RoutingSettingsRegistry.Type.BOTH)
    RoutingSettingsRegistry.Instance().registerRoutingSetting(new SmeltablesSettings(), RoutingSettingsRegistry.Type.ITEM)
    ModCompatibility.init()
    ModCompatibility.registerSettings()

    RoutingSettingsRegistry.Instance().sort(RoutingSettingsRegistry.Type.ITEM)
    RoutingSettingsRegistry.Instance().sort(RoutingSettingsRegistry.Type.LIQUID)
  }
}
