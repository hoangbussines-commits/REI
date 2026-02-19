package com.realisticrecipe.lootmodifier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.function.Supplier;

public class TitaniumLootModifier extends LootModifier {
    public static final Supplier<Codec<TitaniumLootModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, TitaniumLootModifier::new)));

    private static final Random RANDOM = new Random();

    protected TitaniumLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        BlockState blockState = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (blockState == null) return generatedLoot;

        var block = blockState.getBlock();
        var blockId = ForgeRegistries.BLOCKS.getKey(block);

        if (blockId == null) return generatedLoot;

        if (blockId.toString().equals("minecraft:diamond_ore") ||
                blockId.toString().equals("minecraft:deepslate_diamond_ore")) {

            if (RANDOM.nextFloat() < 0.4f) {
                int count = RANDOM.nextInt(2) + 1; // 1-2
                Item titanium = ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation("realisticrecipe", "raw_titanium")
                );
                if (titanium != null) {
                    generatedLoot.add(new ItemStack(titanium, count));
                }
            }
        }

        else if (blockId.toString().equals("minecraft:gold_ore") ||
                blockId.toString().equals("minecraft:deepslate_gold_ore") ||
                blockId.toString().equals("minecraft:nether_gold_ore")) {

            float chance = 0.1f + (RANDOM.nextFloat() * 0.1f);
            if (RANDOM.nextFloat() < chance) {
                Item titanium = ForgeRegistries.ITEMS.getValue(
                        new ResourceLocation("realisticrecipe", "raw_titanium")
                );
                if (titanium != null) {
                    generatedLoot.add(new ItemStack(titanium, 1));
                }
            }
        }

        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}