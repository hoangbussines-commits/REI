package com.realisticrecipe;

import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RealisticRecipe.MODID)
public class JoinMessageHandler {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        event.getEntity().sendSystemMessage(
                Component.literal("§a[RealisticRecipe] Mods have been successfully downloaded! Have fun playing!")

        );
    }
}
