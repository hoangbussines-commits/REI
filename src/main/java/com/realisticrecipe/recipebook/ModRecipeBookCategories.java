package com.realisticrecipe.recipebook;

import com.google.common.collect.ImmutableList;
import com.realisticrecipe.RealisticRecipe;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RealisticRecipe.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModRecipeBookCategories {

    public static RecipeBookCategories VANILLA_INDUSTRY;

    @SubscribeEvent
    public static void registerRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
        VANILLA_INDUSTRY = RecipeBookCategories.create("VANILLA_INDUSTRY");

        event.registerBookCategories(RecipeBookType.CRAFTING,
                ImmutableList.of(VANILLA_INDUSTRY,
                        RecipeBookCategories.CRAFTING_SEARCH,
                        RecipeBookCategories.CRAFTING_BUILDING_BLOCKS,
                        RecipeBookCategories.CRAFTING_REDSTONE,
                        RecipeBookCategories.CRAFTING_EQUIPMENT,
                        RecipeBookCategories.CRAFTING_MISC));

        event.registerAggregateCategory(VANILLA_INDUSTRY,
                ImmutableList.of(VANILLA_INDUSTRY));

        event.registerRecipeCategoryFinder(RecipeType.CRAFTING, recipe -> {
            if (recipe.getId().getNamespace().equals(RealisticRecipe.MODID)) {
                return VANILLA_INDUSTRY;
            }
            return null;
        });
    }
}