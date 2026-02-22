package com.realisticrecipe.lootmodifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.realisticrecipe.ModItems;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Supplier;

public class OsmiumLootModifier extends LootModifier {
    public static final Supplier<Codec<OsmiumLootModifier>> CODEC = () ->
            RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, OsmiumLootModifier::new));

    private static final Random RANDOM = new Random();

    protected OsmiumLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        BlockState blockState = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (blockState == null) return generatedLoot;

        var block = blockState.getBlock();
        var blockId = ForgeRegistries.BLOCKS.getKey(block);

        // Lấy cấp độ Fortune từ tool
        int fortune = 0;
        var tool = context.getParamOrNull(LootContextParams.TOOL);
        if (tool != null) {
            fortune = tool.getEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.BLOCK_FORTUNE);
        }

        if (blockId == null) return generatedLoot;

        if (blockId.toString().equals("minecraft:diamond_ore") ||
                blockId.toString().equals("minecraft:deepslate_diamond_ore")) {

            float baseChance = blockId.toString().contains("deepslate") ? 0.25f : 0.2f;
            float chance = baseChance + (fortune * 0.05f);

            if (RANDOM.nextFloat() < chance) {
                int count = 1;
                if (blockId.toString().contains("deepslate") && RANDOM.nextFloat() < 0.3f) {
                    count = 2;
                }
                generatedLoot.add(new ItemStack(ModItems.RAW_OSMIUM.get(), count));
            }
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}