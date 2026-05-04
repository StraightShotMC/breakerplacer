package com.khazoda.breakerplacer.registry;

import com.khazoda.breakerplacer.block.entity.BreakerBlockEntity;
import com.khazoda.breakerplacer.block.entity.PlacerBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.khazoda.breakerplacer.Constants.ID;

public class RegBlockEntities {


  public static void init() {
  }  public static final BlockEntityType<PlacerBlockEntity> PLACER_BLOCK_ENTITY = Registry.register(
      BuiltInRegistries.BLOCK_ENTITY_TYPE, ID("placer_block_entity"),
      FabricBlockEntityTypeBuilder.create(PlacerBlockEntity::new,
          RegBlocks.PLACER_BLOCK).build());
  public static final BlockEntityType<BreakerBlockEntity> BREAKER_BLOCK_ENTITY = Registry.register(
      BuiltInRegistries.BLOCK_ENTITY_TYPE, ID("breaker_block_entity"),
      FabricBlockEntityTypeBuilder.create(BreakerBlockEntity::new,
          RegBlocks.BREAKER_BLOCK).build());



}
