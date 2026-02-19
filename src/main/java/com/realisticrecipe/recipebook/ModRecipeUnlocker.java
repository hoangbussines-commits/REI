package com.realisticrecipe.recipebook;

import com.realisticrecipe.RealisticRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = RealisticRecipe.MODID)
public class ModRecipeUnlocker {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();

        Set<ResourceLocation> modRecipes = player.level().getRecipeManager().getRecipes().stream()
                .filter(recipe -> recipe.getId().getNamespace().equals(RealisticRecipe.MODID))
                .map(recipe -> recipe.getId())
                .collect(Collectors.toSet());

        player.awardRecipesByKey(modRecipes.toArray(new ResourceLocation[0]));

        System.out.println("[VanillaIndustry] Unlocked " + modRecipes.size() + " recipes for player: " + player.getName().getString());
    }
}