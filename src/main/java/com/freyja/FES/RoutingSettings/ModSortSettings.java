package com.freyja.FES.RoutingSettings;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class ModSortSettings implements IRoutingSetting {
    private String modId;

    public ModSortSettings(String modId)
    {
        this.modId = modId;
    }

    @Override
    public String getName()
    {
        return modId + " items";
    }

    @Override
    public boolean isItemValid(ItemStack itemStack)
    {
        String str = itemStack.getItemName().substring(itemStack.getItemName().lastIndexOf(".") + 1);
        return GameRegistry.findBlock(modId, str) != null && GameRegistry.findItem(modId, str) != null;
    }
}
