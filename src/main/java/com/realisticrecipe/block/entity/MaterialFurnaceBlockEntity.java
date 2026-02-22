package com.realisticrecipe.block.entity;

import com.realisticrecipe.init.ModBlockEntities;
import com.realisticrecipe.networking.ModMessages;
import com.realisticrecipe.networking.packet.EnergySyncS2CPacket;
import com.realisticrecipe.energymanager.materialenergy.energy.ModEnergyStorage;
import com.realisticrecipe.recipe.data.MaterialFurnaceRecipe;
import com.realisticrecipe.recipe.recipemanager.MaterialFurnaceRecipeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MaterialFurnaceBlockEntity extends BlockEntity {
    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = 2;
    private static final int ENERGY_UPDATE_THRESHOLD = 500;
    private int lastEnergySent = 0;
    private MaterialFurnaceRecipe activeRecipe;
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ModEnergyStorage energyStorage = new ModEnergyStorage(20000, 20000) {
        @Override
        public void onEnergyChanged() {
            setChanged();
            int current = energyStorage.getEnergyStored();

            if (Math.abs(current - lastEnergySent) >= ENERGY_UPDATE_THRESHOLD) {
                lastEnergySent = current;
                ModMessages.sendToClients(new EnergySyncS2CPacket(current, worldPosition));
            }
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    private int lastEnergy = 0;
    private int progress = 0;
    private final int maxProgress = 100;

    public MaterialFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MATERIAL_FURNACE.get(), pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        System.out.println("[REI] has passed the super.onload step!");
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
        System.out.println("[REI] has passed the lazyItemHandler step!");

        if (level != null && !level.isClientSide) {
            int e = energyStorage.getEnergyStored();
        }
    }


    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("energy", energyStorage.getEnergyStored());
        tag.putInt("progress", progress);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt("energy", energyStorage.getEnergyStored());
        tag.putInt("progress", progress);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains("energy")) {
            energyStorage.setEnergy(tag.getInt("energy"));
        }
        progress = tag.getInt("progress");
    }

    public final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> maxProgress;
                case 2 -> energyStorage.getEnergyStored();
                case 3 -> energyStorage.getMaxEnergyStored();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int getCount() {
            return 4;
        }
    };

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("inventory", CompoundTag.TAG_COMPOUND)) {
            itemHandler.deserializeNBT(tag.getCompound("inventory"));
        } else {
            System.out.println("[REI]WARN: Missing inventory NBT at " + worldPosition);
        }

        if (tag.contains("energy", CompoundTag.TAG_INT)) {
            int energy = tag.getInt("energy");
            energyStorage.setEnergy(energy);
        } else {
            System.out.println("[REI]WARN: Missing energy NBT at " + worldPosition);
        }

        progress = tag.getInt("progress");
    }

    public int getEnergy() {
        return energyStorage.getEnergyStored();
    }

    public int getMaxEnergy() {
        return energyStorage.getMaxEnergyStored();
    }

    public void setEnergy(int energy) {
        System.out.println("S " + worldPosition.toShortString() + " E " + energy);
        energyStorage.setEnergy(energy);

    }

    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public int getProgress() {
        return progress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        ItemStack input = itemHandler.getStackInSlot(SLOT_INPUT);

        if (input.isEmpty()) {
            activeRecipe = null;
            progress = 0;
            return;
        }

        if (activeRecipe == null) {
            activeRecipe = MaterialFurnaceRecipeManager.findRecipe(input);
            progress = 0;
            return;
        }

        if (!activeRecipe.ingredient.test(input)) {
            activeRecipe = null;
            progress = 0;
            return;
        }

        ItemStack output = itemHandler.getStackInSlot(SLOT_OUTPUT);
        ItemStack result = activeRecipe.result;

        if (!output.isEmpty()
                && (!ItemStack.isSameItemSameTags(output, result)
                || output.getCount() + result.getCount() > output.getMaxStackSize())) {
            return;
        }

        int cookTime = activeRecipe.cookingTime;
        int powerCost = activeRecipe.powerCost;

        if (cookTime <= 0 || powerCost <= 0) return;

        int costPerTick = Math.max(1, powerCost / cookTime);

        if (energyStorage.getEnergyStored() < costPerTick) {
            return;
        }

        energyStorage.extractEnergy(costPerTick, false);
        progress++;

        if (progress >= cookTime) {
            itemHandler.extractItem(SLOT_INPUT, 1, false);

            if (output.isEmpty()) {
                itemHandler.setStackInSlot(SLOT_OUTPUT, result.copy());
            } else {
                output.grow(result.getCount());
            }

            progress = 0;
            activeRecipe = null;
        }

        setChanged();

        int currentEnergy = energyStorage.getEnergyStored();
        if (currentEnergy != lastEnergy) {
            lastEnergy = currentEnergy;
            ModMessages.sendToClients(
                    new EnergySyncS2CPacket(lastEnergy, worldPosition)
            );
        }
    }
}