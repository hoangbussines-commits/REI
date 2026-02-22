package com.realisticrecipe.recipe.recipemanager;

import com.realisticrecipe.recipe.data.MaterialFurnaceRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MaterialFurnaceRecipeCache {

    private static final List<MaterialFurnaceRecipe> RECIPES = new ArrayList<>();

    public static void clear() {
        RECIPES.clear();
    }

    public static void register(MaterialFurnaceRecipe recipe) {
        RECIPES.add(recipe);
    }

    public static MaterialFurnaceRecipe find(ItemStack input) {
        for (MaterialFurnaceRecipe recipe : RECIPES) {
            if (recipe.ingredient.test(input)) {
                return recipe;
            }
        }
        return null;
    }
}