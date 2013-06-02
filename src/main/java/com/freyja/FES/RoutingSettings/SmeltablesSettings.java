package com.freyja.FES.RoutingSettings;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class SmeltablesSettings implements IRoutingSetting {

    @Override
    public String getName()
    {
        return "Smeltable";
    }

    @Override
    public boolean isItemValid(ItemStack itemStack)
    {
        return FurnaceRecipes.smelting().getSmeltingResult(itemStack) != null;
    }
}
