package com.freyja.FES.utils;

import com.freyja.FES.FES;
import com.freyja.FES.RoutingSettings.ModSortSettings;
import com.freyja.FES.RoutingSettings.RoutingSettingsRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

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
        String[] mods = new String[]{"mcp", "FML", "Forge"};

        ArrayList<String> forbiddenMods = new ArrayList<String>();

        Collections.addAll(forbiddenMods, mods);

        RoutingSettingsRegistry.Instance().registerRoutingSetting(new ModSortSettings("Vanilla"));

        for (ModContainer mod : Loader.instance().getModList()) {
            if (!forbiddenMods.contains(mod.getModId()) && !mod.isImmutable()) {
                FES.logger().fine("Registering Routing Setting for " + mod.getName() + ".");
                RoutingSettingsRegistry.Instance().registerRoutingSetting(new ModSortSettings(mod.getModId()));
            }
        }
    }
}
