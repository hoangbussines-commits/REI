package com.realisticrecipe.recipe.recipemanager;

import com.realisticrecipe.recipe.data.MaterialFurnaceRecipe;
import net.minecraft.world.item.ItemStack;

public class MaterialFurnaceRecipeManager {

    public static MaterialFurnaceRecipe findRecipe(ItemStack input) {
        return MaterialFurnaceRecipeCache.find(input);
    }
}