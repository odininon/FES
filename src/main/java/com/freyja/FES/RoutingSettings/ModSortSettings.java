package com.freyja.FES.RoutingSettings;

import com.freyja.core.utils.FreyjaGameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
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
        //Checks Blocks
        String str = itemStack.getItemName().substring(itemStack.getItemName().lastIndexOf(".") + 1);
        if (GameRegistry.findBlock(modId, str) != null) {
            return true;
        } else {
            str = new ItemStack(itemStack.copy().getItem(), 0, 0).getItemName().substring(itemStack.getItemName().lastIndexOf(".") + 1);
            if (GameRegistry.findBlock(modId, str) != null) {
                return true;
            } else {
                str = itemStack.getItem().getClass().getName();
                if (GameRegistry.findBlock(modId, str) != null) {
                    return true;
                } else {
                    if (itemStack.getItem().getClass().getName().equals("net.minecraft.item.ItemBlock")) {
                        Block block = Block.blocksList[itemStack.itemID];
                        str = block.getClass().getName();
                        if (GameRegistry.findBlock(modId, str) != null) {
                            return true;
                        }
                    }
                }
            }
        }

        //Checks Items
        str = itemStack.getItemName().substring(itemStack.getItemName().lastIndexOf(".") + 1);
        if (FreyjaGameData.findItem(modId, str) != null) {
            return true;
        } else {
            str = new ItemStack(itemStack.copy().getItem(), 0, 0).getItemName().substring(itemStack.getItemName().lastIndexOf(".") + 1);
            if (FreyjaGameData.findItem(modId, str) != null) {
                return true;
            } else {
                str = itemStack.getItem().getClass().getName();
                if (FreyjaGameData.findItem(modId, str) != null) {
                    return true;
                }
            }
        }

        //Checks Vanilla
        if (modId.equalsIgnoreCase("Vanilla")) {
            if (itemStack.getItem().getClass().getName().equals("net.minecraft.item.ItemBlock")) {
                Block block = Block.blocksList[itemStack.itemID];
                return block.getClass().getName().startsWith("net.minecraft.");
            } else return itemStack.getItem().getClass().getName().startsWith("net.minecraft.item");
        }

        return false;

    }
}
