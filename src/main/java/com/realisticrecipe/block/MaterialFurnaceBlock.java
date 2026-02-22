package com.realisticrecipe.block;

import com.realisticrecipe.block.entity.MaterialFurnaceBlockEntity;
import com.realisticrecipe.init.ModBlockEntities;
import com.realisticrecipe.screen.MaterialFurnaceMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkHooks;
public class MaterialFurnaceBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

    public MaterialFurnaceBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }


    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MaterialFurnaceBlockEntity(pos, state);
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof MaterialFurnaceBlockEntity) {
            NetworkHooks.openScreen((ServerPlayer) player,
                    new SimpleMenuProvider(
                            (id, inv, p) -> new MaterialFurnaceMenu(id, inv, pos),
                            Component.translatable("container.material_furnace")),
                    pos);
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && !player.isCreative()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof MaterialFurnaceBlockEntity furnace) {
                ItemStack stack = new ItemStack(this);
                CompoundTag tag = furnace.saveWithoutMetadata();
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
        if (type == ModBlockEntities.MATERIAL_FURNACE.get()) {
            return (BlockEntityTicker<T>) new BlockEntityTicker<MaterialFurnaceBlockEntity>() {
                @Override
                public void tick(Level level, BlockPos pos, BlockState state, MaterialFurnaceBlockEntity be) {
                    be.tick();
                }
            };
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide) {
            CompoundTag tag = stack.getTagElement("BlockEntityTag");
            if (tag != null) {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof MaterialFurnaceBlockEntity furnace) {
                    furnace.load(tag);
                }
            }
        }
    }
}