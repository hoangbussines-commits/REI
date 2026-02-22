package com.realisticrecipe.recipe.recipemanager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.realisticrecipe.recipe.data.MaterialFurnaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class MaterialFurnaceRecipeReloadListener
        extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();

    public MaterialFurnaceRecipeReloadListener() {
        super(GSON, "material_furnace");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> objects,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        MaterialFurnaceRecipeCache.clear();

        for (JsonElement element : objects.values()) {
            JsonObject json = element.getAsJsonObject();
            MaterialFurnaceRecipe recipe =
                    MaterialFurnaceRecipeLoader.fromJson(json);
            MaterialFurnaceRecipeCache.register(recipe);
        }
    }
}