package com.realisticrecipe.mixin;

import net.minecraft.core.RegistryAccess;
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
import com.mojang.datafixers.util.Pair;

@Mixin(RecipeManager.class)
public class MixinRecipeManagerIronTools {

    static {
        System.out.println("========== [VanillaIndustry] IRON TOOLS EXTERMINATOR LOADED ==========");
    }

    @Unique
    private static boolean hasLoggedFirstCall = false;

    @Unique
    private static final String[] IRON_TOOLS = {
            "minecraft:iron_pickaxe",
            "minecraft:iron_axe",
            "minecraft:iron_shovel",
            "minecraft:iron_hoe",
            "minecraft:iron_sword",
            "minecraft:flint_and_steel"
    };

    @Inject(
            method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void blockIronToolsCrafting(RecipeType<?> type, Container container, Level level,
                                        CallbackInfoReturnable<Optional<Recipe<?>>> cir) {
        if (!hasLoggedFirstCall) {
            System.out.println("[VanillaIndustry] IRON TOOLS CHECKER ACTIVATED!");
            hasLoggedFirstCall = true;
        }

        if (type != RecipeType.CRAFTING) return;

        if (!cir.getReturnValue().isPresent()) return;

        Recipe<?> recipe = cir.getReturnValue().get();
        RegistryAccess registryAccess = level.registryAccess();
        ItemStack result = recipe.getResultItem(registryAccess);
        ResourceLocation recipeId = recipe.getId();

        if (
                (result.getItem() == Items.IRON_PICKAXE && recipeId.toString().equals("minecraft:iron_pickaxe")) ||
                        (result.getItem() == Items.IRON_AXE && recipeId.toString().equals("minecraft:iron_axe")) ||
                        (result.getItem() == Items.IRON_SHOVEL && recipeId.toString().equals("minecraft:iron_shovel")) ||
                        (result.getItem() == Items.IRON_HOE && recipeId.toString().equals("minecraft:iron_hoe")) ||
                        (result.getItem() == Items.IRON_SWORD && recipeId.toString().equals("minecraft:iron_sword")) ||
                        (result.getItem() == Items.FLINT_AND_STEEL && recipeId.toString().equals("minecraft:flint_and_steel"))
        ) {
            System.out.println("[VanillaIndustry]  BLOCKED VANILLA TOOL CRAFTING: " + result.getItem());
            cir.setReturnValue(Optional.empty());
        }
    }

    @Inject(
            method = "byKey",
            at = @At("RETURN"),
            cancellable = true
    )
    private void blockIronToolsByKey(ResourceLocation id, CallbackInfoReturnable<Optional<Recipe<?>>> cir) {
        for (String tool : IRON_TOOLS) {
            if (id.toString().equals(tool)) {
                System.out.println("[VanillaIndustry]  REMOVED FROM RECIPE BOOK: " + tool);
                cir.setReturnValue(Optional.empty());
            }
        }
    }
    @Inject(
            method = "byKey",
            at = @At("RETURN"),
            cancellable = true
    )
    private void blockBlastingFurnace(ResourceLocation id, CallbackInfoReturnable<Optional<Recipe<?>>> cir) {
        if (id.toString().equals("minecraft:blast_furnace")) {
            System.out.println("[VanillaIndustry]  REMOVED BLAST FURNACE FROM RECIPE BOOK");
            cir.setReturnValue(Optional.empty());
        }
    }

    @Inject(
            method = "getRecipeFor(Lnet/minecraft/world/item/crafting/RecipeType;Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void blockBlastingFurnaceCrafting(RecipeType<?> type, Container container, Level level,
                                              CallbackInfoReturnable<Optional<Recipe<?>>> cir) {
        if (type != RecipeType.CRAFTING) return;

        if (!cir.getReturnValue().isPresent()) return;

        Recipe<?> recipe = cir.getReturnValue().get();
        ItemStack result = recipe.getResultItem(level.registryAccess());

        if (result.getItem() == Items.BLAST_FURNACE) {
            ResourceLocation recipeId = recipe.getId();
            if (recipeId.toString().equals("minecraft:blast_furnace")) {
                System.out.println("[VanillaIndustry]  BLOCKED VANILLA BLAST FURNACE CRAFTING");
                cir.setReturnValue(Optional.empty());
            }
        }
    }

}