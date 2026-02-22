package com.realisticrecipe.block.entity;

import com.realisticrecipe.block.ReinforcedFurnaceBlock;
import com.realisticrecipe.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import com.realisticrecipe.energymanager.energy.IEnergyHandler;
import com.realisticrecipe.energymanager.energy.REIEnergyStorage;
import com.realisticrecipe.energymanager.util.EnergyUtils;
import java.util.Optional;
import com.realisticrecipe.energymanager.energy.REIEnergyStorage;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import java.util.ArrayList;
import java.util.List;
public class ReinforcedFurnaceBlockEntity extends BlockEntity implements MenuProvider, WorldlyContainer {
    private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private int burnTime = 0;
    private int cookTime = 0;
    private final int cookTimeTotal = 50; // 2.5 giây
    private int burnTimeTotal = 0;
    private REIEnergyStorage energyStorage;
    private static final int ENERGY_PER_TICK = 40;
    private boolean hasNotifiedGeneration = true;
    private void notifyPlayersGenerating() {
        if (level == null || level.isClientSide) return;

        String message = String.format("§6[Reinforced Furnace §7%s §6] §aNow generating power!",
                worldPosition.toShortString());

        level.players().forEach(player -> {
            if (player.distanceToSqr(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ()) < 256) {
                player.sendSystemMessage(Component.literal(message));
            }
        });
    }

    protected final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> burnTime;
                case 1 -> burnTimeTotal;
                case 2 -> cookTime;
                case 3 -> cookTimeTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> burnTime = value;
                case 1 -> burnTimeTotal = value;
                case 2 -> cookTime = value;
                case 3 -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };
    public REIEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }
    public ReinforcedFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.REINFORCED_FURNACE.get(), pos, state);
        this.energyStorage = new REIEnergyStorage(100000, this::setChanged);
    }
    private void onEnergyChanged() {
        setChanged();
    }
    private void distributeEnergyFairly() {
        if (level == null || level.isClientSide) return;

        List<IEnergyStorage> receivers = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor == null) continue;

            IEnergyStorage storage = neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).orElse(null);
            if (storage != null && storage.canReceive() && storage.receiveEnergy(1, true) > 0) {
                receivers.add(storage);
            }
        }

        if (receivers.isEmpty()) return;

        int energyToSend = Math.min(ENERGY_PER_TICK, energyStorage.getEnergyStored());
        if (energyToSend <= 0) return;

        if (receivers.size() == 1) {
            int sent = receivers.get(0).receiveEnergy(energyToSend, false);
            energyStorage.extractEnergy(sent, false);
            return;
        }

        int energyPerReceiver = energyToSend / receivers.size();
        int remaining = energyToSend;

        for (IEnergyStorage receiver : receivers) {
            if (remaining <= 0) break;

            int toSend = Math.min(energyPerReceiver, remaining);
            int sent = receiver.receiveEnergy(toSend, false);
            energyStorage.extractEnergy(sent, false);
            remaining -= sent;
        }
    }
    public static void tick(Level level, BlockPos pos, BlockState state, ReinforcedFurnaceBlockEntity be) {
        boolean wasLit = be.burnTime > 0;
        boolean dirty = false;

        if (be.burnTime > 0) {
            be.burnTime--;
        }

        ItemStack fuel = be.items.get(1);
        ItemStack input = be.items.get(0);
        ItemStack output = be.items.get(2);

        if (!input.isEmpty() && be.canBurn(input, output)) {
            Optional<SmeltingRecipe> recipe = be.getRecipe(input);
            if (recipe.isPresent()) {
                if (be.burnTime == 0 && be.isFuel(fuel)) {
                    be.burnTime = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
                    be.burnTimeTotal = be.burnTime;
                    if (be.burnTime > 0) {
                        fuel.shrink(1);
                        dirty = true;
                    }
                }

                if (be.burnTime > 0) {
                    be.cookTime++;
                    if (be.cookTime >= be.cookTimeTotal) {
                        be.cookTime = 0;
                        be.craftItem(recipe.get());
                        dirty = true;
                    }
                }
            }
        } else if (be.canGeneratePower() && be.burnTime > 0 && !be.energyStorage.isFull() && be.hasReceiversNeedingPower()) {            be.energyStorage.addEnergy(ENERGY_PER_TICK);

            be.distributeEnergyFairly();

            dirty = true;

            if (be.hasNotifiedGeneration) {
                be.notifyPlayersGenerating();
                be.hasNotifiedGeneration = false;
            }
        } else {
            be.cookTime = 0;
        }

        if (be.burnTime == 0 && be.isFuel(fuel) && be.canGeneratePower() && !be.energyStorage.isFull() && be.hasReceiversNeedingPower()) {
            be.burnTime = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);
            be.burnTimeTotal = be.burnTime;
            if (be.burnTime > 0) {
                fuel.shrink(1);
                dirty = true;
            }
        }
        if (wasLit != (be.burnTime > 0)) {
            dirty = true;
            level.setBlock(pos, state.setValue(ReinforcedFurnaceBlock.LIT, be.burnTime > 0), 3);
        }

        if (be.burnTime == 0 || !be.canGeneratePower()) {
            be.hasNotifiedGeneration = true;
        }

        if (dirty) {
            setChanged(level, pos, state);
        }
    }
    private boolean hasReceiversNeedingPower() {
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(dir);
            BlockEntity neighbor = level.getBlockEntity(neighborPos);
            if (neighbor == null) continue;

            IEnergyStorage storage = neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).orElse(null);
            if (storage != null && storage.canReceive() && storage.receiveEnergy(1, true) > 0) {
                return true;
            }
        }
        return false;
    }

    private boolean canBurn(ItemStack input, ItemStack output) {
        if (input.isEmpty()) return false;
        Optional<SmeltingRecipe> recipe = getRecipe(input);
        if (recipe.isEmpty()) return false;
        ItemStack result = recipe.get().getResultItem(level.registryAccess());
        if (result.isEmpty()) return false;
        if (output.isEmpty()) return true;
        if (!ItemStack.isSameItem(output, result)) return false;
        return output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    private Optional<SmeltingRecipe> getRecipe(ItemStack input) {
        return level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SingleItemContainer(input), level);
    }

    private void craftItem(SmeltingRecipe recipe) {
        ItemStack input = items.get(0);
        ItemStack result = recipe.getResultItem(level.registryAccess());
        ItemStack output = items.get(2);

        input.shrink(1);

        if (output.isEmpty()) {
            items.set(2, result.copy());
        } else if (ItemStack.isSameItem(output, result)) {
            output.grow(result.getCount());
        }
    }

    private boolean isFuel(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
    }

    // Container methods
    @Override
    public int[] getSlotsForFace(Direction side) {
        return switch (side) {
            case DOWN -> new int[]{2};      // output
            case UP -> new int[]{0};         // input
            default -> new int[]{1};          // fuel
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return this.canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 2;
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(items, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return switch (slot) {
            case 0 -> true;                 // input
            case 1 -> isFuel(stack);        // fuel
            default -> false;
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.reinforced_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new FurnaceMenu(id, inventory, this, this.dataAccess);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("CookTime", cookTime);
        tag.putInt("BurnTimeTotal", burnTimeTotal);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        items = NonNullList.withSize(3, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items);
        burnTime = tag.getInt("BurnTime");
        cookTime = tag.getInt("CookTime");
        burnTimeTotal = tag.getInt("BurnTimeTotal");
    }

    public boolean canGeneratePower() {
        if (level == null) return false;
        if (level.isClientSide) return false;
        return EnergyUtils.hasAnyFEReceiver(level, worldPosition);
    }

    // Container wrapper for recipe lookup
    private record SingleItemContainer(ItemStack stack) implements Container {
        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return stack.isEmpty();
        }

        @Override
        public ItemStack getItem(int slot) {
            return slot == 0 ? stack : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {
        }
    }
}