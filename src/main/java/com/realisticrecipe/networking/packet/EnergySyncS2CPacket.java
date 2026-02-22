package com.realisticrecipe.networking.packet;

import com.realisticrecipe.block.entity.MaterialFurnaceBlockEntity;
import com.realisticrecipe.screen.MaterialFurnaceMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EnergySyncS2CPacket {
    private final int energy;
    private final BlockPos pos;

    public EnergySyncS2CPacket(int energy, BlockPos pos) {
        this.energy = energy;
        this.pos = pos;
    }

    public EnergySyncS2CPacket(FriendlyByteBuf buf) {
        this.energy = buf.readInt();
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(energy);
        buf.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        System.out.println("P " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + " E " + energy);
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            var level = Minecraft.getInstance().level;
            if (level == null) return;

            if (level.getBlockEntity(pos) instanceof MaterialFurnaceBlockEntity blockEntity) {
                blockEntity.setEnergy(energy);

                if (Minecraft.getInstance().player.containerMenu instanceof MaterialFurnaceMenu menu &&
                        menu.blockEntity.getBlockPos().equals(pos)) {
                }
            }
        });
        return true;
    }
}