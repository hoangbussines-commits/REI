package com.realisticrecipe;

import com.realisticrecipe.lootmodifier.ModLootModifiers;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RealisticRecipe.MODID)
public class RealisticRecipe {

    public static final String MODID = "realisticrecipe";

    public RealisticRecipe() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(bus);
        ModLootModifiers.register(bus);
        ModCreativeTab.TABS.register(bus);

    }
}

