package com.realisticrecipe.block;

import com.realisticrecipe.block.entity.BatteryFEBlockEntity;
import com.realisticrecipe.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class BatteryFEBlock extends BaseEntityBlock {
    public BatteryFEBlock(Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        ItemStack tool = player.getMainHandItem();

        System.out.println("[REI]Battery getDestroyProgress called! Tool: " + tool.getItem() + " at " + pos);

        if (tool.is(Items.IRON_PICKAXE) ||
                tool.is(Items.DIAMOND_PICKAXE) ||
                tool.is(Items.NETHERITE_PICKAXE)) {
            System.out.println("[REI]Battery using correct tool! Speed: 2.0");
            return 2.0f;
        }

        System.out.println("[REI]Battery using wrong tool! Speed: 0.01");
        return 0.01f;
    }
    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && !player.isCreative()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BatteryFEBlockEntity battery) {
                ItemStack stack = new ItemStack(this);
                CompoundTag tag = new CompoundTag();
                tag.putInt("energy", battery.getBatteryStorage().getEnergyStored());
                stack.addTagElement("BlockEntityTag", tag);
                popResource(level, pos, stack);
                level.removeBlock(pos, false);
                return;
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, ModBlockEntities.BATTERY_FE.get(),
                (lvl, pos, st, be) -> BatteryFEBlockEntity.tick(lvl, pos, st, be));
    }
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide) {
            CompoundTag tag = stack.getTagElement("BlockEntityTag");
            if (tag != null && tag.contains("energy")) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof BatteryFEBlockEntity battery) {
                    battery.getBatteryStorage().setEnergy(tag.getInt("energy"));
                }
            }
        }
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BatteryFEBlockEntity(pos, state);
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof BatteryFEBlockEntity battery) {
            return battery.onBlockClicked(player);
        }

        return InteractionResult.PASS;
    }
}