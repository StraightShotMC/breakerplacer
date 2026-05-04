package com.khazoda.breakerplacer.block;

import com.khazoda.breakerplacer.BreakerPlacerConfig;
import com.khazoda.breakerplacer.Constants;
import com.khazoda.breakerplacer.block.entity.BreakerBlockEntity;
import com.khazoda.breakerplacer.networking.BlockBreakParticlePayload;
import com.khazoda.breakerplacer.networking.ParticlePayload;
import com.khazoda.breakerplacer.registry.RegBlockEntities;
import com.khazoda.breakerplacer.registry.RegSounds;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class BreakerBlock extends BaseBlock {
  public static final MapCodec<BreakerBlock> CODEC = simpleCodec(BreakerBlock::new);

  public BreakerBlock(Properties settings) {
    super(settings.sound(SoundType.STONE).strength(3.5f).pushReaction(PushReaction.NORMAL).instrument(NoteBlockInstrument.BASS).mapColor(MapColor.STONE));
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, Boolean.FALSE));
  }

  public BreakerBlock() {
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
    BreakerBlockEntity be = world.getBlockEntity(pos, RegBlockEntities.BREAKER_BLOCK_ENTITY).orElse(null);

    if (be == null) {
      Constants.LOG.warn("No matching block entity at {}, skipping block break attempt", pos);
    } else {
      world.gameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Context.of(be.getBlockState()));
      if (state.isAir()) return;

      Direction direction = state.getValue(BlockStateProperties.FACING);

      BlockPos targetPos = pos.relative(direction);
      BlockState targetBlockState = world.getBlockState(targetPos);
      Block targetBlock = targetBlockState.getBlock();
      BlockEntity targetBE = targetBlockState.hasBlockEntity() ? world.getBlockEntity(targetPos) : null;
      ItemStack toolToBreakWith = be.inventory.get(9);
      BlockInWorld cachedPos = new BlockInWorld(world, targetPos, false);

      if (targetBlock == Blocks.AIR && targetBE == null) {
        // If there is no block to break, play dispenser fail sound and return early
        world.playSound(
            null,
            pos,
            SoundEvents.DISPENSER_FAIL,
            SoundSource.BLOCKS,
            1f,
            1.2f
        );
        return;
      }
      try {
        if (!canBreakBlock(toolToBreakWith, cachedPos)) {
          // If block can't be broken with current tool play a failure sound and return early
          world.playSound(null, pos, RegSounds.FAIL, SoundSource.BLOCKS, 1f, 1f);
          return;
        }

        List<ItemStack> drops = getDroppedStacks(targetBlockState, world, targetPos, targetBE, toolToBreakWith);

        if (BreakerPlacerConfig.getInstance().toolTakesDamage() && toolToBreakWith.isDamageableItem()) {
          toolToBreakWith.hurtAndBreak(1, world, null, (item) -> {
            toolToBreakWith.setCount(0);
          });
        }

        // Remove broken block from the world expeditiously
        world.setBlock(targetPos, targetBlockState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

        // Do post-block break update stuff
        world.gameEvent(GameEvent.BLOCK_DESTROY, targetPos, GameEvent.Context.of(targetBlockState));

        // Show block breaking particles to clients nearby
        BlockBreakParticlePayload.sendBlockBreakParticlePayloadToClients(world, new BlockBreakParticlePayload(targetPos, targetBlockState));
        ParticlePayload.sendParticlePacketToClients(world, new ParticlePayload(ParticleTypes.FLAME, targetPos, new Vec3(0, 0, 0), 0f, (byte) 5, (byte) 2));
        if (!world.getBlockState(targetPos.above()).isRedstoneConductor(world, targetPos.above()))
          ParticlePayload.sendParticlePacketToClients(world, new ParticlePayload(ParticleTypes.WHITE_SMOKE, targetPos, new Vec3(0, 0.4, 0), 0.02f, (byte) 10, (byte) 2));

        // Play sounds to clients nearby
        world.playSound(null, targetPos, RegSounds.BREAK, SoundSource.BLOCKS, 0.35f, 1f);
        world.playSound(null, targetPos, targetBlockState.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.75f, 1f);

        drops.forEach(stack -> {
          be.addToFirstFreeSlot(stack);
          if (!stack.isEmpty()) {
            Containers.dropItemStack(world, targetPos.getX(), targetPos.getY(), targetPos.getZ(), stack);
          }
        });
      } catch (Exception e) {
        Constants.LOG.warn("Failed to add block ItemStack to breaker. {}", e.getMessage());
      }
      be.setChanged();
    }
  }

  public List<ItemStack> getDroppedStacks(BlockState state, ServerLevel world, BlockPos pos, @Nullable BlockEntity blockEntity, ItemStack tool) {
    BlockInWorld cachedPos = new BlockInWorld(world, pos, false);
    Optional<ResourceKey<LootTable>> optionalKey = state.getBlock().getLootTable();
    ResourceKey<LootTable> registryKey;
    if (optionalKey.isPresent()) {
      registryKey = optionalKey.get();
    } else {
      return Collections.emptyList();
    }
    LootParams.Builder builder = new LootParams.Builder(world).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, tool).withParameter(LootContextParams.BLOCK_STATE, state).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);

    LootParams parameterSet = builder.create(LootContextParamSets.BLOCK);
    ServerLevel serverWorld = parameterSet.getLevel();
    LootTable lootTable = serverWorld.getServer().reloadableRegistries().getLootTable(registryKey);

    if (state.requiresCorrectToolForDrops()) {
      if (tool.canBreakBlockInAdventureMode(cachedPos) || tool.getItem().isCorrectToolForDrops(tool, state)) {
        return lootTable.getRandomItems(parameterSet);
      }
    } else {
      return lootTable.getRandomItems(parameterSet);
    }
    return Collections.emptyList();
  }

  /**
   * miningSpeed > 0.95F ensures all hand-breakable items are breakable
   * miningSpeed >= blockHardness ensures the correct tools are used for harder-than-hand-breakable blocks
   **/
  public boolean canBreakBlock(ItemStack stack, BlockInWorld cachedPos) {
    BlockState blockState = cachedPos.getState();
    BlockPos blockPos = cachedPos.getPos();
    LevelReader world = cachedPos.getLevel();

    float blockHardness = blockState.getDestroySpeed(world, blockPos);

    if (blockHardness < 0.0F) {
      return false;
    }

    float miningSpeed = stack.getDestroySpeed(blockState);
    boolean canBreak = miningSpeed > 0.95F && miningSpeed >= blockHardness;
    boolean isCorrectTool = stack.isCorrectToolForDrops(blockState);

    return canBreak || isCorrectTool;
  }

  @Override
  protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
    this.activate(world, state, pos);
  }

  @Override
  protected MapCodec<? extends BreakerBlock> codec() {
    return CODEC;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new BreakerBlockEntity(pos, state);
  }
}