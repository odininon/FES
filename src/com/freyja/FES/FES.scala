package com.freyja.FES

import cpw.mods.fml.common.{FMLCommonHandler, FMLLog, Mod}
import cpw.mods.fml.common.network.{NetworkRegistry, NetworkMod}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.block.Block
import cpw.mods.fml.common.Mod.{Init, PreInit}
import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPreInitializationEvent}
import com.freyja.FES.common.blocks.{BlockInjector, BlockLine, BlockReceptacle}
import net.minecraft.block.material.Material
import cpw.mods.fml.common.registry.{LanguageRegistry, GameRegistry}
import net.minecraftforge.common.Configuration
import java.util.logging.Logger
import com.freyja.FES.common.CommonProxy
import cpw.mods.fml.relauncher.Side
import net.minecraft.item.Item

/**
 *
 * @user Freyja
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 *
 */

@Mod(name = "Freyja's Easy Sorting", modid = "FES", modLanguage = "scala", version = "1.0.0")
@NetworkMod(channels = Array[String] {
  "FES"
}, clientSideRequired = true, serverSideRequired = false)
object FES {
  private val _creativeTab: CreativeTabs = new CreativeTabs("FES")
  private val _logger: Logger = Logger.getLogger("FES")
  private var _config: Configuration = null

  def creativeTab = _creativeTab

  def logger = _logger

  def config = _config


  private val proxy = findProxy()


  /**
   * Blocks
   */
  var blockReceptacle: Block = null
  var blockInjector: Block = null
  var blockLine: Block = null

  /**
   * Ids
   */
  var blockReceptacleId: Int = 0
  var blockInjectorId: Int = 0
  var blockLineId: Int = 0

  def initConfigurations(event: FMLPreInitializationEvent): Configuration = {
    val config: Configuration = new Configuration(event.getSuggestedConfigurationFile)

    try {
      config.load()
      blockReceptacleId = config.getBlock("Receptacle", 2000).getInt(2000)
      blockInjectorId = config.getBlock("Injector", 2001).getInt(2001)
      blockLineId = config.getBlock("Line", 2002).getInt(2002)

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

    blockReceptacle = new BlockReceptacle(blockReceptacleId, Material.rock).setCreativeTab(creativeTab).setUnlocalizedName("receptacle")
    registerObject(blockReceptacle, "Receptacle")

    blockInjector = new BlockInjector(blockInjectorId, Material.rock).setCreativeTab(creativeTab).setUnlocalizedName("injector")
    registerObject(blockInjector, "Injector")

    blockLine = new BlockLine(blockLineId, Material.rock).setCreativeTab(creativeTab).setUnlocalizedName("line")
    registerObject(blockLine, "Line")
  }


  def registerObject(registry: AnyRef, name: String) {
    registry match {
      case registry: Block => {
        GameRegistry.registerBlock(registry, registry.getUnlocalizedName)
        LanguageRegistry.addName(registry, name)
      }
      case registry: Item => {
        GameRegistry.registerItem(registry, registry.getUnlocalizedName)
        LanguageRegistry.addName(registry, name)
      }
      case _ => logger.info("Tried to register neither a block or item.")
    }
  }

  @Init
  def init(event: FMLInitializationEvent) {
    NetworkRegistry.instance().registerGuiHandler(this, proxy)
  }
}
