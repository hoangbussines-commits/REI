package com.realisticrecipe.energymanager.energy;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

public interface IEnergyHandler {
    REIEnergyStorage getEnergyStorage();
    boolean canGeneratePower();

    default <T> LazyOptional<T> getEnergyCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY) {
            return LazyOptional.of(() -> getEnergyStorage()).cast();
        }
        return LazyOptional.empty();
    }

    void syncEnergy();
}