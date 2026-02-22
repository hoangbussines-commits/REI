package com.realisticrecipe.attachment;

import com.realisticrecipe.item.reader.ReaderMode;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerReaderData {
    private ReaderMode currentMode = ReaderMode.ENERGY;

    public ReaderMode getMode() {
        return currentMode;
    }

    public void setMode(ReaderMode mode) {
        this.currentMode = mode;
    }

    public void nextMode() {
        currentMode = currentMode.next();
    }

    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
        public static final Capability<PlayerReaderData> PLAYER_READER = CapabilityManager.get(new CapabilityToken<>() {});

        private PlayerReaderData data = null;
        private final LazyOptional<PlayerReaderData> optional = LazyOptional.of(this::getData);

        private PlayerReaderData getData() {
            if (data == null) {
                data = new PlayerReaderData();
            }
            return data;
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == PLAYER_READER) {
                return optional.cast();
            }
            return LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            if (data != null) {
                tag.putString("mode", data.getMode().name());
            }
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag tag) {
            if (tag.contains("mode")) {
                getData().setMode(ReaderMode.valueOf(tag.getString("mode")));
            }
        }
    }
}