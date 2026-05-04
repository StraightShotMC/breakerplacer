package com.khazoda.breakerplacer.block.entity;

import com.khazoda.breakerplacer.registry.RegBlockEntities;
import com.khazoda.breakerplacer.screen.PlacerScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class PlacerBlockEntity extends BaseBlockEntity {

  public PlacerBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(RegBlockEntities.PLACER_BLOCK_ENTITY, blockPos, blockState);
  }

  @Override
  protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
    return new PlacerScreenHandler(syncId, playerInventory, this);
  }
}
