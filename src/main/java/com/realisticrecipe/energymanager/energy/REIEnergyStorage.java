package com.realisticrecipe.energymanager.energy;

import net.minecraftforge.energy.EnergyStorage;
import java.util.function.Consumer;

public class REIEnergyStorage extends EnergyStorage {
    private final Runnable onEnergyChanged;
    private Consumer<Integer> onReceive;
    private Consumer<Integer> onExtract;

    public REIEnergyStorage(int capacity, Runnable onEnergyChanged) {
        super(capacity);
        this.onEnergyChanged = onEnergyChanged;
    }

    public REIEnergyStorage(int capacity, int maxTransfer, Runnable onEnergyChanged) {
        super(capacity, maxTransfer);
        this.onEnergyChanged = onEnergyChanged;
    }

    public REIEnergyStorage(int capacity, int maxReceive, int maxExtract, Runnable onEnergyChanged) {
        super(capacity, maxReceive, maxExtract);
        this.onEnergyChanged = onEnergyChanged;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && received > 0) {
            onEnergyChanged.run();
            if (onReceive != null) onReceive.accept(received);
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (!simulate && extracted > 0) {
            onEnergyChanged.run();
            if (onExtract != null) onExtract.accept(extracted);
        }
        return extracted;
    }

    public void setEnergy(int energy) {
        this.energy = Math.min(energy, capacity);
        onEnergyChanged.run();
    }

    public void addEnergy(int amount) {
        this.energy = Math.min(energy + amount, capacity);
        onEnergyChanged.run();
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        if (this.energy > capacity) this.energy = capacity;
        onEnergyChanged.run();
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMaxReceive() {
        return maxReceive;
    }

    public int getMaxExtract() {
        return maxExtract;
    }

    public void setOnReceive(Consumer<Integer> onReceive) {
        this.onReceive = onReceive;
    }

    public void setOnExtract(Consumer<Integer> onExtract) {
        this.onExtract = onExtract;
    }

    public boolean canGenerate() {
        return energy < capacity;
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