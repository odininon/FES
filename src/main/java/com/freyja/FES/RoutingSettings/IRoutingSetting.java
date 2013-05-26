package com.freyja.FES.RoutingSettings;

import net.minecraft.item.ItemStack;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public interface IRoutingSetting {

    public String getName();

    public boolean isItemValid(ItemStack itemStack);
}
