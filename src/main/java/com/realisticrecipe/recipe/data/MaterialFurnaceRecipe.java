package com.realisticrecipe.recipe.data;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class MaterialFurnaceRecipe {

    public final Ingredient ingredient;
    public final ItemStack result;
    public final int powerCost;
    public final int cookingTime;

    public MaterialFurnaceRecipe(
            Ingredient ingredient,
            ItemStack result,
            int powerCost,
            int cookingTime
    ) {
        this.ingredient = ingredient;
        this.result = result;
        this.powerCost = powerCost;
        this.cookingTime = cookingTime;
    }
}