package com.realisticrecipe.block;

import com.realisticrecipe.block.entity.ReinforcedFurnaceBlockEntity;
import com.realisticrecipe.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.Containers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
public class ReinforcedFurnaceBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public ReinforcedFurnaceBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(LIT, false));

    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ReinforcedFurnaceBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null
                : createTickerHelper(type, ModBlockEntities.REINFORCED_FURNACE.get(),
                (lvl, pos, st, be) -> ReinforcedFurnaceBlockEntity.tick(lvl, pos, st, be));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ReinforcedFurnaceBlockEntity furnace) {
            NetworkHooks.openScreen((ServerPlayer) player, furnace, pos);
        }
        return InteractionResult.CONSUME;
    }
    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        ItemStack tool = player.getMainHandItem();

        System.out.println("getDestroyProgress called! Tool: " + tool.getItem() + " at " + pos);

        if (tool.is(Items.DIAMOND_PICKAXE) || tool.is(Items.NETHERITE_PICKAXE)) {
            System.out.println("Using correct tool! Speed: 2.0");
            return 2.0f;
        }

        System.out.println("Using wrong tool! Speed: 0.01");
        return 0.01f;
    }
    @Override
    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        ItemStack tool = player.getMainHandItem();
        return tool.is(Items.DIAMOND_PICKAXE) || tool.is(Items.NETHERITE_PICKAXE);
    }
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ReinforcedFurnaceBlockEntity furnace) {
                Containers.dropContents(level, pos, furnace);

                ItemStack stack = new ItemStack(this);
                CompoundTag tag = new CompoundTag();
                tag.putInt("energy", furnace.getEnergyStorage().getEnergyStored());

                if (!furnace.isEmpty()) {
                    CompoundTag itemsTag = new CompoundTag();
                    ContainerHelper.saveAllItems(itemsTag, furnace.getItems());
                    tag.put("Items", itemsTag);
                }

                if (!tag.isEmpty()) {
                    stack.addTagElement("BlockEntityTag", tag);
                }

                popResource(level, pos, stack);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
        if (state.getValue(LIT)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;

            Direction direction = state.getValue(FACING);
            Direction opposite = direction.getOpposite();

            if (random.nextDouble() < 0.1) {
                level.playLocalSound(x, y, z, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            level.addParticle(ParticleTypes.SMOKE,
                    x + opposite.getStepX() * 0.5,
                    y + 1.0,
                    z + opposite.getStepZ() * 0.5,
                    0.0, 0.0, 0.0);
        }
    }
}