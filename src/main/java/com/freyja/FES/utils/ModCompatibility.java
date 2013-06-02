package com.freyja.FES.utils;

import com.freyja.FES.FES;
import com.freyja.FES.RoutingSettings.LiquidSortSettings;
import com.freyja.FES.RoutingSettings.ModSortSettings;
import com.freyja.FES.RoutingSettings.RoutingSettingsRegistry;
import com.freyja.core.utils.FreyjaGameData;
import cpw.mods.fml.common.Loader;
import net.minecraftforge.liquids.LiquidDictionary;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class ModCompatibility {
    public static Class SmelteryLogic;

    public static boolean isTConstructLoaded()
    {
        return Loader.isModLoaded("TConstruct");
    }

    public static int getSmelteryHeight(Object smelterty)
    {
        try {
            if (SmelteryLogic == null)
                SmelteryLogic = Class.forName("mods.tinker.tconstruct.blocks.logic.SmelteryLogic");

            Object layers = SmelteryLogic.getField("layers").get(smelterty);

            if (layers instanceof Integer) return (Integer) layers;

            return 0;

        } catch (Exception e) {
            return 0;
        }
    }

    public static void init()
    {
        if (isTConstructLoaded()) {
            FES.logger().info("Loading TConstruct compatibility.");
            try {
                SmelteryLogic = Class.forName("mods.tinker.tconstruct.blocks.logic.SmelteryLogic");
            } catch (ClassNotFoundException e) {
                FES.logger().warning("Failed to loaded mods.tinker.tconstruct.blocks.logic.SmelteryLogic");
            }
        } else {
            FES.logger().info("Not loading TConstruct compatibility.");
        }
    }

    public static void registerSettings()
    {
        ArrayList<String> mods = new ArrayList<String>();

        for (Object modID : FreyjaGameData.getMap().values()) {
            String mod = (String) modID;
            if (!mods.contains(mod)) {
                mods.add(mod);
            }
        }

        Collections.sort(mods);


        RoutingSettingsRegistry.Instance().registerRoutingSetting(new ModSortSettings("Vanilla"), RoutingSettingsRegistry.Type.ITEM);

        for (String mod : mods) {
            FES.logger().fine("Registering Routing Setting for " + mod + ".");
            RoutingSettingsRegistry.Instance().registerRoutingSetting(new ModSortSettings(mod), RoutingSettingsRegistry.Type.ITEM);
        }

        for (String liquid : LiquidDictionary.getLiquids().keySet()) {
            FES.logger().info("Registering Rotuing Setting for " + liquid + ".");
            RoutingSettingsRegistry.Instance().registerRoutingSetting(new LiquidSortSettings(liquid), RoutingSettingsRegistry.Type.LIQUID);
        }
    }
}
