package com.freyja.FES.utils;

import com.freyja.FES.FES;
import com.freyja.FES.RoutingSettings.LiquidSortSettings;
import com.freyja.FES.RoutingSettings.ModSortSettings;
import com.freyja.FES.RoutingSettings.RoutingSettingsRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.ItemData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.liquids.LiquidDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class ModCompatibility {
    public static Class SmelteryLogic;
    private static Map<Integer, String> itemMap = new HashMap<Integer, String>();

    public static boolean isTConstructLoaded() {
        return Loader.isModLoaded("TConstruct");
    }

    public static int getSmelteryHeight(Object smelterty) {
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

    public static void init() {
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

        NBTTagList list = new NBTTagList();
        GameData.writeItemData(list);
        for (int i = 0; i < list.tagCount(); i++) {
            ItemData itemData = new ItemData((NBTTagCompound) list.tagAt(i));
            itemMap.put(itemData.getItemId(), itemData.getModId());
        }
    }

    public static void registerSettings() {
        ArrayList<String> mods = new ArrayList<String>();

        for (Object modID : getItemMap().values()) {
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

    public static Map<Integer, String> getItemMap() {
        return itemMap;
    }

    public static boolean partofMod(String modId, int itemID) {
        return getItemMap().containsKey(itemID) && getItemMap().get(itemID).equals(modId);
    }
}
