package com.realisticrecipe;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.util.Lazy;

import java.util.function.Supplier;

public enum ModToolTiers implements Tier {
    REINFORCED(5, 5000, 25.0F, 28.0F, 25,  // speed 12.0 → 25.0
            () -> Ingredient.of(ModItems.REINFORCED_INGOT.get()));

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final Lazy<Ingredient> repairIngredient;

    ModToolTiers(int level, int uses, float speed, float damage, int enchantmentValue,
                 Supplier<Ingredient> repairIngredient) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = Lazy.of(repairIngredient);
    }

    @Override
    public int getUses() { return this.uses; }

    @Override
    public float getSpeed() { return this.speed; }

    @Override
    public float getAttackDamageBonus() { return this.damage; }

    @Override
    public int getLevel() { return this.level; }

    @Override
    public int getEnchantmentValue() { return this.enchantmentValue; }

    @Override
    public Ingredient getRepairIngredient() { return this.repairIngredient.get(); }
}