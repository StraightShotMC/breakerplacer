package com.khazoda.breakerplacer.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;

public abstract class BaseBlock extends BaseEntityBlock {
  public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
  public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

  public static final Properties defaultSettings =
      Properties.of()
          .sound(SoundType.STONE)
          .strength(3.5f)
          .pushReaction(PushReaction.NORMAL)
          .instrument(NoteBlockInstrument.BASS)
          .mapColor(MapColor.STONE);

  protected BaseBlock(Properties settings) {
    super(settings);
    registerDefaultState(
        getStateDefinition()
            .any()
            .setValue(FACING, Direction.NORTH)
            .setValue(TRIGGERED, false)
    );
  }

  protected BaseBlock() {
    this(defaultSettings);
  }

  // Important: BlockWithEntity blocks should return MODEL unless you have special rendering
  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  protected abstract void activate(ServerLevel world, BlockState state, BlockPos pos);

  @Nullable
  @Override
  public MenuProvider getMenuProvider(
      BlockState state,
      Level world,
      BlockPos pos
  ) {
    BlockEntity blockEntity = world.getBlockEntity(pos);
    return blockEntity instanceof MenuProvider factory ? factory : null;
  }

  @Override
  protected void affectNeighborsAfterRemoval(
      BlockState state,
      ServerLevel world,
      BlockPos pos,
      boolean moved
  ) {
    if (state.is(world.getBlockState(pos).getBlock())) return;

    BlockEntity be = world.getBlockEntity(pos);
    if (be instanceof Container inventory) {
      Containers.dropContents(world, pos, inventory);
      world.updateNeighbourForOutputSignal(pos, this);
    }

    super.affectNeighborsAfterRemoval(state, world, pos, moved);
  }

  @Override
  protected void neighborChanged(
      BlockState state,
      Level world,
      BlockPos pos,
      Block sourceBlock,
      @Nullable Orientation wireOrientation,
      boolean notify
  ) {
    boolean powered =
        world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above());
    boolean triggered = state.getValue(TRIGGERED);

    if (powered && !triggered) {
      world.scheduleTick(pos, this, 4);
      world.setBlock(pos, state.setValue(TRIGGERED, true), Block.UPDATE_ALL);
    } else if (!powered && triggered) {
      world.setBlock(pos, state.setValue(TRIGGERED, false), Block.UPDATE_ALL);
    }
  }

  @Override
  protected void onPlace(
      BlockState state,
      Level world,
      BlockPos pos,
      BlockState oldState,
      boolean notify
  ) {
    if (!oldState.is(state.getBlock())) {
      if (world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.above())) {
        world.scheduleTick(pos, this, 4);
        world.setBlock(pos, state.setValue(TRIGGERED, true), Block.UPDATE_ALL);
      }
    }
  }

  protected abstract void tick(
      BlockState state,
      ServerLevel world,
      BlockPos pos,
      RandomSource random
  );

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, TRIGGERED);
  }

  @Nullable
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    return defaultBlockState()
        .setValue(FACING, ctx.getNearestLookingDirection().getOpposite())
        .setValue(TRIGGERED, false);
  }

  @Override
  protected boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Override
  protected int getAnalogOutputSignal(
      BlockState state,
      Level world,
      BlockPos pos,
      Direction direction
  ) {
    return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
  }

  @Override
  protected BlockState rotate(BlockState state, Rotation rotation) {
    return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
  }

  @Override
  protected BlockState mirror(BlockState state, Mirror mirror) {
    return state.rotate(mirror.getRotation(state.getValue(FACING)));
  }
}