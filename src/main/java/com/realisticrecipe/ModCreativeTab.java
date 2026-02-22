package com.realisticrecipe;

import com.realisticrecipe.init.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTab {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RealisticRecipe.MODID);

    public static final RegistryObject<CreativeModeTab> REALISTIC_TAB =
            TABS.register("realistic_tab", () ->
                    CreativeModeTab.builder()
                            .title(Component.translatable("creativetab.realisticrecipe"))
                            .icon(() -> new ItemStack(ModItems.REFINED_IRON.get()))
                            .displayItems((params, output) -> {
                                output.accept(ModItems.CONTROLLER_CHIP.get());
                                output.accept(ModItems.IRON_PLATE.get());
                                output.accept(ModItems.RAW_TITANIUM.get());
                                output.accept(ModItems.PIG_IRON_RAW.get());
                                output.accept(ModItems.RAW_OSMIUM.get());
                                output.accept(ModItems.OSMIUM_INGOT.get());
                                output.accept(ModItems.REFINED_IRON.get());
                                output.accept(ModItems.REFINED_IRON_INGOT.get());
                                output.accept(ModItems.STEEL_INGOT.get());
                                output.accept(ModItems.TITANIUM_INGOT.get());
                                output.accept(ModItems.REINFORCED_INGOT.get());
                                output.accept(ModItems.ALLOY_INFUSED.get());
                                output.accept(ModItems.ELITE_ALLOY_CONTROLLER.get());
                                output.accept(ModItems.REINFORCED_HELMET.get());
                                output.accept(ModItems.REINFORCED_CHESTPLATE.get());
                                output.accept(ModItems.REINFORCED_LEGGINGS.get());
                                output.accept(ModItems.REINFORCED_BOOTS.get());
                                output.accept(ModItems.REINFORCED_SWORD.get());
                                output.accept(ModItems.REINFORCED_PICKAXE.get());
                                output.accept(ModItems.REINFORCED_AXE.get());
                                output.accept(ModItems.REINFORCED_SHOVEL.get());
                                output.accept(ModItems.REINFORCED_HOE.get());
                                output.accept(ModItems.REI_READER.get());
                                output.accept(ModItems.TITANIUM_NUGGET.get());
                                output.accept(ModItems.GRAPHITE_INGOT.get());
                                output.accept(ModBlocks.REINFORCED_FURNACE.get());
                                output.accept(ModItems.BATTERY_FE.get());
                            })
                            .build()
            );
}
