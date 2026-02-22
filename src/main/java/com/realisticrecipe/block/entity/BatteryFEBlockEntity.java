package com.realisticrecipe.block.entity;

import com.realisticrecipe.battery.powermanager.energy.BatteryEnergyStorage;
import com.realisticrecipe.battery.powermanager.energy.IBatteryHandler;
import com.realisticrecipe.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.Block.popResource;

public class BatteryFEBlockEntity extends BlockEntity implements IBatteryHandler {
    private BatteryEnergyStorage energyStorage;
    private LazyOptional<net.minecraftforge.energy.IEnergyStorage> energyHandler;
    private static final int MAX_ENERGY = 100000;

    public BatteryFEBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BATTERY_FE.get(), pos, state);
        this.energyStorage = new BatteryEnergyStorage(MAX_ENERGY, this::onBatteryChanged);
        this.energyHandler = LazyOptional.of(() -> energyStorage);
    }


    @Override
    public BatteryEnergyStorage getBatteryStorage() {
        return energyStorage;
    }
    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
        }
    }
    @Override
    public void onBatteryChanged() {
        setChanged();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            if (side == Direction.UP) {
                return LazyOptional.of(() -> new IEnergyStorage() {
                    @Override
                    public int receiveEnergy(int maxReceive, boolean simulate) { return 0; }

                    @Override
                    public int extractEnergy(int maxExtract, boolean simulate) {
                        return energyStorage.extractEnergy(maxExtract, simulate);
                    }

                    @Override
                    public int getEnergyStored() { return energyStorage.getEnergyStored(); }

                    @Override
                    public int getMaxEnergyStored() { return energyStorage.getMaxEnergyStored(); }

                    @Override
                    public boolean canExtract() { return true; }

                    @Override
                    public boolean canReceive() { return false; }
                }).cast();
            } else {
                return LazyOptional.of(() -> new IEnergyStorage() {
                    @Override
                    public int receiveEnergy(int maxReceive, boolean simulate) {
                        return energyStorage.receiveEnergy(maxReceive, simulate);
                    }

                    @Override
                    public int extractEnergy(int maxExtract, boolean simulate) { return 0; }

                    @Override
                    public int getEnergyStored() { return energyStorage.getEnergyStored(); }

                    @Override
                    public int getMaxEnergyStored() { return energyStorage.getMaxEnergyStored(); }

                    @Override
                    public boolean canExtract() { return false; }

                    @Override
                    public boolean canReceive() { return true; }
                }).cast();
            }
        }
        return super.getCapability(cap, side);
    }

    public InteractionResult onBlockClicked(Player player) {
        if (level == null || level.isClientSide) return InteractionResult.SUCCESS;

        String message = String.format("[Battery %d %d %d]: The total amount of electricity in the battery is %d/%d FE",
                worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                energyStorage.getEnergyStored(), energyStorage.getCapacity());

        player.sendSystemMessage(Component.literal(message));
        return InteractionResult.SUCCESS;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BatteryFEBlockEntity be) {
        if (level.isClientSide) return;

        BlockPos abovePos = pos.above();
        BlockEntity above = level.getBlockEntity(abovePos);
        if (above != null) {
            IEnergyStorage aboveStorage = above.getCapability(ForgeCapabilities.ENERGY, Direction.DOWN).orElse(null);
            if (aboveStorage != null && aboveStorage.canReceive()) {
                int extracted = be.energyStorage.extractEnergy(1000, true);
                if (extracted > 0) {
                    int received = aboveStorage.receiveEnergy(extracted, false);
                    be.energyStorage.extractEnergy(received, false);
                }
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("energy", energyStorage.getEnergyStored());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        energyStorage.setEnergy(tag.getInt("energy"));
    }
}