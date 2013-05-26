package com.freyja.FES

import cpw.mods.fml.common.{FMLCommonHandler, FMLLog, Mod}
import cpw.mods.fml.common.network.{NetworkRegistry, NetworkMod}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.block.Block
import cpw.mods.fml.common.Mod.{PostInit, Init, PreInit}
import cpw.mods.fml.common.event.{FMLPostInitializationEvent, FMLInitializationEvent, FMLPreInitializationEvent}
import net.minecraft.block.material.Material
import cpw.mods.fml.common.registry.{GameData, LanguageRegistry, GameRegistry}
import net.minecraftforge.common.Configuration
import java.util.logging.Logger
import com.freyja.FES.common.CommonProxy
import cpw.mods.fml.relauncher.Side
import net.minecraft.item.Item
import com.freyja.FES.common.blocks.{BlockPlayerInventory, BlockLine, BlockInjector, BlockReceptacle}
import com.freyja.FES.common.inventories.{TileEntityPlayerInventory, TileEntityLine, TileEntityInjector, TileEntityReceptacle}
import com.freyja.FES.utils.{ModCompatibility, Utils}
import com.freyja.FES.common.packets.PacketHandler
import com.freyja.FES.RoutingSettings.{NoneSetting, SmeltablesSettings, DefaultRoutingSetting, RoutingSettingsRegistry}
import cpw.mods.fml.client.FMLClientHandler

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

  def proxy = _proxy;


  /**
   * Blocks
   */
  var blockReceptacle: Block = null
  var blockInjector: Block = null
  var blockLine: Block = null
  var blockPlayer: Block = null

  /**
   * Ids
   */
  var blockReceptacleId: Int = 0
  var blockInjectorId: Int = 0
  var blockLineId: Int = 0
  var blockPlayerId: Int = 0

  def initConfigurations(event: FMLPreInitializationEvent): Configuration = {
    val config: Configuration = new Configuration(event.getSuggestedConfigurationFile)

    try {
      config.load()
      blockReceptacleId = config.getBlock("Receptacle", 2000).getInt(2000)
      blockInjectorId = config.getBlock("Injector", 2001).getInt(2001)
      blockLineId = config.getBlock("Line", 2002).getInt(2002)
      blockPlayerId = config.getBlock("Player", 2003).getInt(2003)

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
    GameRegistry.registerTileEntity(classOf[TileEntityReceptacle], "Receptacle")

    blockInjector = new BlockInjector(blockInjectorId, Material.circuits).setCreativeTab(creativeTab).setUnlocalizedName("FES:injector")
    registerObject(blockInjector, "Injector")
    GameRegistry.registerTileEntity(classOf[TileEntityInjector], "Injector")

    blockLine = new BlockLine(blockLineId, Material.circuits).setCreativeTab(creativeTab).setUnlocalizedName("FES:line")
    registerObject(blockLine, "Line")
    GameRegistry.registerTileEntity(classOf[TileEntityLine], "Line")

    blockPlayer = new BlockPlayerInventory(blockPlayerId, Material.circuits).setCreativeTab(creativeTab).setUnlocalizedName("FES:player")
    registerObject(blockPlayer, "Player")
    GameRegistry.registerTileEntity(classOf[TileEntityPlayerInventory], "Player")
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
    RoutingSettingsRegistry.Instance().registerRoutingSetting(new NoneSetting())
    RoutingSettingsRegistry.Instance().registerRoutingSetting(new DefaultRoutingSetting())
    RoutingSettingsRegistry.Instance().registerRoutingSetting(new SmeltablesSettings())
    ModCompatibility.init()
    ModCompatibility.registerSettings()
  }
}
