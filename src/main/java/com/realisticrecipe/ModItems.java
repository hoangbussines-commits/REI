package com.realisticrecipe;

import com.realisticrecipe.init.ModBlocks;
import com.realisticrecipe.item.REIReaderItem;
import net.minecraft.world.item.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
@Mod.EventBusSubscriber(modid = RealisticRecipe.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RealisticRecipe.MODID);


    public static final RegistryObject<Item> REFINED_IRON =
            ITEMS.register("refined_iron", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REFINED_IRON_INGOT =
            ITEMS.register("refined_iron_ingot",
                    () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> IRON_PLATE =
            ITEMS.register("iron_plate", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RAW_TITANIUM =
            ITEMS.register("raw_titanium", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> TITANIUM_INGOT =
            ITEMS.register("titanium_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REINFORCED_INGOT =
            ITEMS.register("reinforced_ingot", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ELITE_ALLOY_CONTROLLER =
            ITEMS.register("elite_alloy_controller",
                    () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REINFORCED_HELMET =
            ITEMS.register("reinforced_helmet",
                    () -> new ArmorItem(ModArmorMaterials.REINFORCED, ArmorItem.Type.HELMET,
                            new Item.Properties()));
    public static final RegistryObject<Item> PIG_IRON_RAW =
            ITEMS.register("pig_iron_raw",
                    () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TITANIUM_NUGGET =
            ITEMS.register("titanium_nugget",
                    () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REINFORCED_CHESTPLATE =
            ITEMS.register("reinforced_chestplate",
                    () -> new ArmorItem(ModArmorMaterials.REINFORCED, ArmorItem.Type.CHESTPLATE,
                            new Item.Properties()));
    public static final RegistryObject<Item> REINFORCED_LEGGINGS =
            ITEMS.register("reinforced_leggings",
                    () -> new ArmorItem(ModArmorMaterials.REINFORCED, ArmorItem.Type.LEGGINGS,
                            new Item.Properties()));
    public static final RegistryObject<Item> ALLOY_INFUSED =
            ITEMS.register("alloy_infused",
                    () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> REINFORCED_BOOTS =
            ITEMS.register("reinforced_boots",
                    () -> new ArmorItem(ModArmorMaterials.REINFORCED, ArmorItem.Type.BOOTS,
                            new Item.Properties()));
    public static final RegistryObject<Item> REINFORCED_SWORD =
            ITEMS.register("reinforced_sword",
                    () -> new SwordItem(ModToolTiers.REINFORCED, 28, 1.6F,
                            new Item.Properties()));
    public static final RegistryObject<Item> REINFORCED_PICKAXE =
            ITEMS.register("reinforced_pickaxe",
                    () -> new PickaxeItem(ModToolTiers.REINFORCED, 1, 5.0F,  // -2.8 → 5.0
                            new Item.Properties()));

    public static final RegistryObject<Item> REINFORCED_AXE =
            ITEMS.register("reinforced_axe",
                    () -> new AxeItem(ModToolTiers.REINFORCED, 52, 5.0F,  // -3.0 → 5.0
                            new Item.Properties()));

    public static final RegistryObject<Item> REINFORCED_SHOVEL =
            ITEMS.register("reinforced_shovel",
                    () -> new ShovelItem(ModToolTiers.REINFORCED, 1.5F, 5.0F,  // -3.0 → 5.0
                            new Item.Properties()));

    public static final RegistryObject<Item> REINFORCED_HOE =
            ITEMS.register("reinforced_hoe",
                    () -> new HoeItem(ModToolTiers.REINFORCED, -4, 5.0F,  // 0.0 → 5.0
                            new Item.Properties()));
    public static final RegistryObject<Item> CONTROLLER_CHIP = ITEMS.register(
            "controller_chip",
            () -> new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> STEEL_INGOT =
            ITEMS.register("steel_ingot",
                    () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GRAPHITE_INGOT =
            ITEMS.register("graphite_ingot",
                    () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REINFORCED_FURNACE =
            ITEMS.register("reinforced_furnace",
                    () -> new BlockItem(ModBlocks.REINFORCED_FURNACE.get(), new Item.Properties()));
    public static final RegistryObject<Item> BATTERY_FE =
            ITEMS.register("battery_fe",
                    () -> new BlockItem(ModBlocks.BATTERY_FE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MATERIAL_FURNACE =
            ITEMS.register("material_furnace",
                    () -> new BlockItem(ModBlocks.MATERIAL_FURNACE.get(), new Item.Properties()));
    public static final RegistryObject<Item> REI_READER = ITEMS.register("rei_reader",
            REIReaderItem::new);
    public static final RegistryObject<Item> RAW_OSMIUM =
            ITEMS.register("raw_osmium",
                    () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> OSMIUM_INGOT =
            ITEMS.register("osmium_ingot",
                    () -> new Item(new Item.Properties()));
}

