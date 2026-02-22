package com.realisticrecipe.energymanager.materialenergy.energy;

import net.minecraftforge.energy.EnergyStorage;

public abstract class ModEnergyStorage extends EnergyStorage {
    public ModEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer, maxTransfer);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (extracted > 0 && !simulate) onEnergyChanged();
        return extracted;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (received > 0 && !simulate) onEnergyChanged();
        return received;
    }

    public void setEnergy(int energy) {
        if (energy < 0 || energy > capacity) {
            System.out.println("[REI]WARN: Invalid energy value " + energy + ", clamping to [0," + capacity + "]");
            energy = Math.min(Math.max(energy, 0), capacity);
        }
        this.energy = energy;
        onEnergyChanged();
    }

    public abstract void onEnergyChanged();
}