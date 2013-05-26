package com.freyja.FES.utils;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.ItemData;
import net.minecraft.item.Item;

import java.util.Map;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class FreyjaGameData {
    private static Map<Integer, ItemData> idMap = Maps.newHashMap();
    private static ImmutableTable<String, String, Integer> modObjectTable;

    public static Item findItem(String modId, String name)
    {
        if (modObjectTable == null || !modObjectTable.contains(modId, name)) {
            return null;
        }

        return Item.itemsList[modObjectTable.get(modId, name)];
    }

    public static void newItemAdded(Item item)
    {
        ModContainer mc = Loader.instance().activeModContainer();
        if (mc == null) {
            mc = Loader.instance().getMinecraftModContainer();
        }
        String itemType = item.getClass().getName();
        ItemData itemData = new ItemData(item, mc);

        idMap.put(item.itemID, itemData);

    }

    public static void buildModObjectTable()
    {
        if (modObjectTable != null) {
            throw new IllegalStateException("Illegal call to buildModObjectTable!");
        }

        Map<Integer, Table.Cell<String, String, Integer>> map = Maps.transformValues(idMap, new Function<ItemData, Table.Cell<String, String, Integer>>() {
            public Table.Cell<String, String, Integer> apply(ItemData data)
            {
                if ("Minecraft".equals(data.getModId())) {
                    return null;
                }
                return Tables.immutableCell(data.getModId(), data.getItemType(), data.getItemId());
            }
        });

        ImmutableTable.Builder<String, String, Integer> tBuilder = ImmutableTable.builder();
        for (Table.Cell<String, String, Integer> c : map.values()) {
            if (c != null) {
                tBuilder.put(c);
            }
        }
        modObjectTable = tBuilder.build();
    }
}
