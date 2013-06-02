package com.freyja.FES.RoutingSettings;

import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.liquids.LiquidStack;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class LiquidSortSettings implements IRoutingSetting {

    private String name;

    public LiquidSortSettings(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public boolean isItemValid(ItemStack itemStack)
    {
        return false;
    }

    public boolean isLiquidValid(LiquidStack liquidStack)
    {
        return LiquidDictionary.getCanonicalLiquid(this.name) == liquidStack;
    }
}
