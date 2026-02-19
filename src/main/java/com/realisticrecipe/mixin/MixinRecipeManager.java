package com.realisticrecipe.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(RecipeManager.class)
public class MixinRecipeManager {

    static {
        System.out.println("========== [VanillaIndustry] MIXIN RECIPE MANAGER LOADED ==========");
    }

    @Unique
    private static boolean hasLoggedFirstCall = false;

    @Unique
    private boolean checkingRecipe = false;

    @SuppressWarnings("unchecked")
    @Inject(
            method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onGetRecipeFor(RecipeType<?> type, Container container, Level level,
                                ResourceLocation id, CallbackInfoReturnable<Optional<Recipe<?>>> cir) {

        if (!hasLoggedFirstCall) {
            System.out.println("[VanillaIndustry] FIRST CALL DETECTED!");
            hasLoggedFirstCall = true;
        }

        if (checkingRecipe) {
            return;
        }

        if (type == RecipeType.SMELTING || type == RecipeType.BLASTING) {
            ItemStack input = container.getItem(0);

            if (input.getItem() == Items.RAW_IRON) {
                checkingRecipe = true;

                Optional<?> original = ((RecipeManager)(Object)this)
                        .getRecipeFor((RecipeType<Recipe<Container>>) type, container, level, id);

                checkingRecipe = false;

                if (original.isPresent() && original.get() instanceof Pair) {
                    Pair<?, ?> pair = (Pair<?, ?>) original.get();
                    if (pair.getSecond() instanceof Recipe) {
                        Recipe<?> recipe = (Recipe<?>) pair.getSecond();
                        RegistryAccess registryAccess = level.registryAccess();
                        ItemStack result = recipe.getResultItem(registryAccess);

                        if (result.getItem() == Items.IRON_INGOT) {
                            cir.setReturnValue(Optional.empty());
                        }
                    }
                }
            }
        }
    }
}