package com.realisticrecipe.battery.powermanager.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BatteryUtils {

    public static boolean isBattery(Level level, BlockPos pos) {
        if (level == null || level.isClientSide) return false;
        BlockEntity be = level.getBlockEntity(pos);
        return be instanceof IBatteryHandler;
    }

    public static BatteryEnergyStorage getBattery(Level level, BlockPos pos) {
        if (level == null || level.isClientSide) return null;
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof IBatteryHandler handler) {
            return handler.getBatteryStorage();
        }
        return null;
    }
}