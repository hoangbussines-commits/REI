package com.realisticrecipe.init;

import com.realisticrecipe.RealisticRecipe;
import com.realisticrecipe.screen.MaterialFurnaceMenu;
import com.realisticrecipe.screen.MaterialFurnaceScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = RealisticRecipe.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModScreen {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.MATERIAL_FURNACE_MENU.get(), MaterialFurnaceScreen::new);
        });
    }
}