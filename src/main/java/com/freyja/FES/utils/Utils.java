package com.freyja.FES.utils;

import com.freyja.FES.FES;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author Freyja
 *         Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class Utils {
    public static void RegisterRecipes()
    {
        GameRegistry.addRecipe(new ItemStack(FES.blockReceptacle()), "III", "I I", "IRI", 'I', new ItemStack(Item.ingotIron), 'R', new ItemStack(Item.redstone));
        GameRegistry.addRecipe(new ItemStack(FES.blockInjector()), "IRI", "I I", "III", 'I', new ItemStack(Item.ingotIron), 'R', new ItemStack(Item.redstone));
        GameRegistry.addRecipe(new ItemStack(FES.blockLine(), 6), "II", "IR", 'I', new ItemStack(Item.ingotIron), 'R', new ItemStack(Item.redstone));
    }

}
