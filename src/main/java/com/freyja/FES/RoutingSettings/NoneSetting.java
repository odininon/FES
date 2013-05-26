package com.freyja.FES.RoutingSettings;

import net.minecraft.item.ItemStack;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class NoneSetting implements IRoutingSetting {
    @Override
    public String getName()
    {
        return "No items";
    }

    @Override
    public boolean isItemValid(ItemStack itemStack)
    {
        return false;
    }
}
