package com.realisticrecipe.energymanager.util;

import com.realisticrecipe.energymanager.energy.REIEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyUtils {

    public static boolean isFEReceiver(Level level, BlockPos pos, Direction fromDirection) {
        if (level == null || level.isClientSide) return false;

        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) return false;

        return be.getCapability(ForgeCapabilities.ENERGY, fromDirection)
                .map(storage -> storage.canReceive())
                .orElse(false);
    }

    public static boolean isFEEmitter(Level level, BlockPos pos, Direction fromDirection) {
        if (level == null || level.isClientSide) return false;

        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) return false;

        return be.getCapability(ForgeCapabilities.ENERGY, fromDirection)
                .map(storage -> storage.canExtract())
                .orElse(false);
    }

    public static boolean hasAnyFEReceiver(Level level, BlockPos centerPos) {
        if (level == null || level.isClientSide) return false;

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = centerPos.relative(dir);
            if (isFEReceiver(level, neighborPos, dir.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    public static int distributeEnergyToNeighbors(Level level, BlockPos centerPos, int amount, boolean simulate) {
        if (level == null || level.isClientSide || amount <= 0) return 0;

        int totalSent = 0;
        int remaining = amount;

        for (Direction dir : Direction.values()) {
            if (remaining <= 0) break;

            BlockPos neighborPos = centerPos.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor == null) continue;

            IEnergyStorage storage = neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).orElse(null);
            if (storage != null && storage.canReceive()) {
                int sent = storage.receiveEnergy(remaining, simulate);
                totalSent += sent;
                remaining -= sent;
            }
        }

        return totalSent;
    }
}