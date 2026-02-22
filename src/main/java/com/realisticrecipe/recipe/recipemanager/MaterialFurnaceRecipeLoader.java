package com.realisticrecipe.recipe.recipemanager;

import com.google.gson.JsonObject;
import com.realisticrecipe.recipe.data.MaterialFurnaceRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class MaterialFurnaceRecipeLoader {

    public static MaterialFurnaceRecipe fromJson(JsonObject json) {
        Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
        ItemStack result =
                ShapedRecipe.itemStackFromJson(json.getAsJsonObject("result"));
        int power = json.get("PowerCost").getAsInt();
        int time = json.get("cookingtime").getAsInt();

        return new MaterialFurnaceRecipe(ingredient, result, power, time);
    }
}