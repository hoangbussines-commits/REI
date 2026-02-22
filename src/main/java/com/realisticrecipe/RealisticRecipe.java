package com.realisticrecipe;

import com.realisticrecipe.init.ModBlockEntities;
import com.realisticrecipe.init.ModBlocks;
import com.realisticrecipe.init.ModMenuTypes;
import com.realisticrecipe.lootmodifier.ModLootModifiers;
import com.realisticrecipe.networking.ModMessages;
import com.realisticrecipe.recipe.recipemanager.MaterialFurnaceRecipeReloadListener;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RealisticRecipe.MODID)
public class RealisticRecipe {

    public static final String MODID = "realisticrecipe";

    public RealisticRecipe() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modBus);
        ModLootModifiers.register(modBus);
        ModCreativeTab.TABS.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenuTypes.MENUS.register(modBus);
        ModMessages.register();

        MinecraftForge.EVENT_BUS.addListener(this::onAddReloadListeners);
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new MaterialFurnaceRecipeReloadListener());
    }
}