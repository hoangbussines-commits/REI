package com.realisticrecipe.battery.powermanager.energy;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;

public interface IBatteryHandler {
    BatteryEnergyStorage getBatteryStorage();

    default <T> LazyOptional<T> getBatteryCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return LazyOptional.of(() -> getBatteryStorage()).cast();
        }
        return LazyOptional.empty();
    }

    void onBatteryChanged();
}