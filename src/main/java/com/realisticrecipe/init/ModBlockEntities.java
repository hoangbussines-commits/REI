package com.realisticrecipe.init;

import com.realisticrecipe.RealisticRecipe;
import com.realisticrecipe.block.entity.MaterialFurnaceBlockEntity;
import com.realisticrecipe.block.entity.ReinforcedFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.realisticrecipe.block.entity.BatteryFEBlockEntity;
public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RealisticRecipe.MODID);

    public static final RegistryObject<BlockEntityType<ReinforcedFurnaceBlockEntity>> REINFORCED_FURNACE =
            BLOCK_ENTITIES.register("reinforced_furnace",
                    () -> BlockEntityType.Builder.of(
                            ReinforcedFurnaceBlockEntity::new,
                            ModBlocks.REINFORCED_FURNACE.get()
                    ).build(null));
    public static final RegistryObject<BlockEntityType<BatteryFEBlockEntity>> BATTERY_FE =
            BLOCK_ENTITIES.register("battery_fe",
                    () -> BlockEntityType.Builder.of(BatteryFEBlockEntity::new, ModBlocks.BATTERY_FE.get()).build(null));
    public static final RegistryObject<BlockEntityType<MaterialFurnaceBlockEntity>> MATERIAL_FURNACE =
            BLOCK_ENTITIES.register("material_furnace",
                    () -> BlockEntityType.Builder.of(MaterialFurnaceBlockEntity::new,
                            ModBlocks.MATERIAL_FURNACE.get()).build(null));
}