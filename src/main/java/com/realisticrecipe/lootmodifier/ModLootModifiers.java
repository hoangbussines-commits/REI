package com.realisticrecipe.lootmodifier;

import com.mojang.serialization.Codec;
import com.realisticrecipe.RealisticRecipe;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, RealisticRecipe.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> TITANIUM_LOOT =
            LOOT_MODIFIER_SERIALIZERS.register("titanium_loot", TitaniumLootModifier.CODEC);

    public static void register(IEventBus bus) {
        LOOT_MODIFIER_SERIALIZERS.register(bus);
    }
}