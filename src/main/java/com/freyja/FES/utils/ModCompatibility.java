package com.freyja.FES.utils;

import com.freyja.FES.FES;
import cpw.mods.fml.common.Loader;

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

}
