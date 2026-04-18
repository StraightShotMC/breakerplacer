package com.khazoda.breakerplacer.block;

import com.khazoda.breakerplacer.Constants;
import com.khazoda.breakerplacer.block.entity.PlacerBlockEntity;
import com.khazoda.breakerplacer.networking.ParticlePayload;
import com.khazoda.breakerplacer.registry.RegBlockEntities;
import com.khazoda.breakerplacer.registry.RegSounds;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PlacerBlock extends BaseBlock {
  public static final MapCodec<PlacerBlock> CODEC = simpleCodec(PlacerBlock::new);

  public PlacerBlock(Properties settings) {
    super(settings.sound(SoundType.STONE).strength(3.5f).pushReaction(PushReaction.NORMAL).instrument(NoteBlockInstrument.BASS).mapColor(MapColor.STONE));
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, Boolean.FALSE));
  }

  public PlacerBlock() {
    this(defaultSettings);
  }

  @Override
  public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
    if (!world.isClientSide()) {
      MenuProvider screenHandlerFactory = state.getMenuProvider(world, pos);
      if (screenHandlerFactory != null) {
        player.openMenu(screenHandlerFactory);
        return InteractionResult.CONSUME;
      }
    }
    return InteractionResult.SUCCESS;
  }

  protected void activate(ServerLevel world, BlockState state, BlockPos pos) {
    PlacerBlockEntity be = world.getBlockEntity(pos, RegBlockEntities.PLACER_BLOCK_ENTITY).orElse(null);
    if (be == null) {
      Constants.LOG.warn("No matching block entity at {}, skipping block placement", pos);
    } else {
      int i = be.chooseNonEmptySlot(world.random);
      world.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Context.of(be.getBlockState()));
      if (i < 0) {
        world.playSound(
            null,
            pos,
            SoundEvents.DISPENSER_FAIL,
            SoundSource.BLOCKS,
            1f,
            1.2f
        );
      } else {
        ItemStack itemStack = be.getItem(i);
        Direction direction = state.getValue(BlockStateProperties.FACING);
        be.setItem(i, placeBlock(world, direction, pos.relative(direction), itemStack));
        be.setChanged();
      }
    }
  }

  protected static ItemStack placeBlock(ServerLevel world, Direction direction, BlockPos pos, ItemStack itemStack) {
    Item item = itemStack.getItem();
    if (item instanceof BlockItem) {
      try {
        /* Places block, and if placement fails (i.e. a block is already in the placement spot), play the error sound */
        DirectionalPlaceContext context = new DirectionalPlaceContext(world, pos, direction, itemStack, direction) {
          @Override
          public Direction getNearestLookingDirection() {
            return this.getClickedFace().getOpposite();
          }
          @Override
          public Direction getNearestLookingVerticalDirection() {
            return this.getClickedFace().getAxis() == Direction.Axis.Y ? this.getClickedFace() : Direction.UP;
          }
          @Override
          public Direction getHorizontalDirection() {
            return this.getClickedFace().getAxis() == Direction.Axis.Y ? Direction.NORTH : this.getClickedFace();
          }
          @Override
          public float getRotation() {
            return this.getClickedFace().toYRot();
          }
        };

        if (((BlockItem) item).place(context) == InteractionResult.FAIL) {
          world.playSound(
              null,
              pos.relative(direction.getOpposite()),
              RegSounds.FAIL,
              SoundSource.BLOCKS,
              1f,
              1f
          );
        } else {
          // Only shows particles floating if block above is not solid
          if (!world.getBlockState(pos.above()).isRedstoneConductor(world, pos.above())) {
            ParticlePayload.sendParticlePacketToClients(world,
                new ParticlePayload(ParticleTypes.WHITE_SMOKE,
                    pos,
                    new Vec3(0, 0.65, 0),
                    0.02f,
                    (byte) 10,
                    (byte) 2
                ));
          }
          world.playSound(
              null,
              pos,
              SoundEvents.BREEZE_SHOOT,
              SoundSource.BLOCKS,
              0.3f,
              1f
          );
        }
      } catch (Exception var8) {
        Constants.LOG.error("Error trying to place block at {}", pos, var8);
      }
    }
    return itemStack;
  }

  @Override
  protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
    this.activate(world, state, pos);
  }

  @Override
  protected MapCodec<? extends PlacerBlock> codec() {
    return CODEC;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new PlacerBlockEntity(pos, state);
  }
}