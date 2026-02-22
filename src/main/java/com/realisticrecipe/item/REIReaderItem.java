package com.realisticrecipe.item;

import com.realisticrecipe.config.PlayerConfigManager;
import com.realisticrecipe.item.reader.ReaderMode;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class REIReaderItem extends Item {
    public REIReaderItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        if (context.getLevel().isClientSide) return InteractionResult.SUCCESS;

        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        BlockEntity be = context.getLevel().getBlockEntity(context.getClickedPos());

        ReaderMode mode = PlayerConfigManager.load(player);

        switch (mode) {
            case ENERGY -> handleEnergyRead(context, be, player);
            case ENTITY -> handleEntityRead(context, be, player);
        }

        return InteractionResult.CONSUME;
    }

    private void handleEnergyRead(UseOnContext context, BlockEntity be, Player player) {
        if (be == null) {
            player.sendSystemMessage(Component.literal("§c[REI Tools] No block entity found!"));
            return;
        }

        var energyCap = be.getCapability(ForgeCapabilities.ENERGY).resolve();
        if (energyCap.isPresent()) {
            IEnergyStorage energy = energyCap.get();
            String blockName = be.getBlockState().getBlock().getName().getString();
            String blockId = BuiltInRegistries.BLOCK.getKey(be.getBlockState().getBlock()).toString();
            int current = energy.getEnergyStored();
            int max = energy.getMaxEnergyStored();

            String message = String.format(
                    "§a[REI Tools] §f[%s]§7[%s]§f: The total energy this block can store is §e%d FE§f, the current energy is §6%d FE",
                    blockName, blockId, max, current
            );
            player.sendSystemMessage(Component.literal(message));
        } else {
            player.sendSystemMessage(Component.literal("§c[REI Tools] This block does not support FE!"));
        }
    }

    private void handleEntityRead(UseOnContext context, BlockEntity be, Player player) {
        String blockName = context.getLevel().getBlockState(context.getClickedPos()).getBlock().getName().getString();
        String blockId = BuiltInRegistries.BLOCK.getKey(context.getLevel().getBlockState(context.getClickedPos()).getBlock()).toString();

        player.sendSystemMessage(Component.literal("§a[REI Tools] §fBlock Info:"));
        player.sendSystemMessage(Component.literal("  §7Name: §f" + blockName));
        player.sendSystemMessage(Component.literal("  §7ID: §f" + blockId));

        if (be == null) {
            player.sendSystemMessage(Component.literal("  §7Type: §fVanilla Block (No Tile Entity)"));
            player.sendSystemMessage(Component.literal("  §7Hardness: §f" + context.getLevel().getBlockState(context.getClickedPos()).getDestroySpeed(context.getLevel(), context.getClickedPos())));
            player.sendSystemMessage(Component.literal("  §7Light Emission: §f" + context.getLevel().getBlockState(context.getClickedPos()).getLightEmission()));
        } else {
            String beType = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(be.getType()).toString();
            String beClass = be.getClass().getSimpleName();
            boolean hasEnergy = be.getCapability(ForgeCapabilities.ENERGY).isPresent();
            boolean hasItem = be.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent();
            boolean hasFluid = be.getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent();

            player.sendSystemMessage(Component.literal("  §7BE Type: §f" + beType));
            player.sendSystemMessage(Component.literal("  §7BE Class: §f" + beClass));
            player.sendSystemMessage(Component.literal("  §7Capabilities:"));
            player.sendSystemMessage(Component.literal("    §7- Energy: " + (hasEnergy ? "§aYes" : "§cNo")));
            player.sendSystemMessage(Component.literal("    §7- Item: " + (hasItem ? "§aYes" : "§cNo")));
            player.sendSystemMessage(Component.literal("    §7- Fluid: " + (hasFluid ? "§aYes" : "§cNo")));
        }
    }
}