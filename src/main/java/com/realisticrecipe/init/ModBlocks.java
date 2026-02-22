package com.realisticrecipe.init;

import com.realisticrecipe.RealisticRecipe;
import com.realisticrecipe.block.MaterialFurnaceBlock;
import com.realisticrecipe.block.ReinforcedFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.realisticrecipe.block.BatteryFEBlock;
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, RealisticRecipe.MODID);

    public static final RegistryObject<Block> REINFORCED_FURNACE =
            BLOCKS.register("reinforced_furnace",
                    () -> new ReinforcedFurnaceBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .strength(50.0f, 1200.0f)
                            .requiresCorrectToolForDrops()
                            .lightLevel(state -> state.getValue(ReinforcedFurnaceBlock.LIT) ? 13 : 0)
                            .sound(SoundType.NETHERITE_BLOCK)));
    public static final RegistryObject<Block> BATTERY_FE =
            BLOCKS.register("battery_fe",
                    () -> new BatteryFEBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_GRAY)
                            .strength(3.0f, 6.0f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.METAL)));
    public static final RegistryObject<Block> MATERIAL_FURNACE =
            BLOCKS.register("material_furnace",
                    () -> new MaterialFurnaceBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.5f, 3.5f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.METAL)));

}