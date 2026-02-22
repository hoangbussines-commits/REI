package com.realisticrecipe.event;

import com.realisticrecipe.RealisticRecipe;
import com.realisticrecipe.config.PlayerConfigManager;
import com.realisticrecipe.ModItems;
import com.realisticrecipe.item.reader.ReaderMode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = RealisticRecipe.MODID, value = Dist.CLIENT)
public class ReaderInputEvents {
    @SubscribeEvent
    public static void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        var mc = net.minecraft.client.Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;

        var stack = player.getMainHandItem();
        if (!stack.is(ModItems.REI_READER.get())) return;

        boolean isShiftDown = GLFW.glfwGetKey(mc.getWindow().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS;
        if (!isShiftDown) return;

        ReaderMode current = PlayerConfigManager.load(player);
        ReaderMode next = current.next();
        PlayerConfigManager.save(player, next);

        player.displayClientMessage(
                Component.literal("§a[REI Tools] Switched to " + next.displayName)
                        .withStyle(ChatFormatting.YELLOW),
                true // action bar
        );

        event.setCanceled(true);
    }
}