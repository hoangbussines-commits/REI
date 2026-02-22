package com.realisticrecipe.battery.powermanager.energy;

import net.minecraftforge.energy.EnergyStorage;

public class BatteryEnergyStorage extends EnergyStorage {
    private final Runnable onEnergyChanged;

    public BatteryEnergyStorage(int capacity, Runnable onEnergyChanged) {
        super(capacity);
        this.onEnergyChanged = onEnergyChanged;
    }

    public BatteryEnergyStorage(int capacity, int maxTransfer, Runnable onEnergyChanged) {
        super(capacity, maxTransfer);
        this.onEnergyChanged = onEnergyChanged;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && received > 0) onEnergyChanged.run();
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (!simulate && extracted > 0) onEnergyChanged.run();
        return extracted;
    }

    public void setEnergy(int energy) {
        this.energy = Math.min(energy, capacity);
        onEnergyChanged.run();
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return energy >= capacity;
    }

    public boolean isEmpty() {
        return energy <= 0;
    }

    public float getFillPercentage() {
        return (float) energy / capacity;
    }
}