package com.khazoda.breakerplacer.block.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class BaseBlockEntity extends RandomizableContainerBlockEntity implements ExtendedScreenHandlerFactory<BlockPos> {
  public NonNullList<ItemStack> inventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);

  protected BaseBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
    super(blockEntityType, blockPos, blockState);
  }

  @Override
  protected NonNullList<ItemStack> getItems() {
    return this.inventory;
  }

  @Override
  protected void setItems(NonNullList<ItemStack> inventory) {
    this.inventory = inventory;
  }

  public int chooseNonEmptySlot(RandomSource random) {
    int i = -1;
    int j = 1;
    for (int k = 0; k < this.inventory.size(); k++) {
      if (!this.inventory.get(k).isEmpty() && random.nextInt(j++) == 0) {
        i = k;
      }
    }
    return i;
  }

  @Override
  protected void loadAdditional(ValueInput view) {
    super.loadAdditional(view);
    ContainerHelper.loadAllItems(view, inventory);
  }

  @Override
  protected void saveAdditional(ValueOutput view) {
    ContainerHelper.saveAllItems(view, inventory);
    super.saveAdditional(view);
  }

  @Override
  public ClientboundBlockEntityDataPacket getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }

  @Override
  public BlockPos getScreenOpeningData(ServerPlayer player) {
    return worldPosition;
  }

  @Override
  protected Component getDefaultName() {
    return Component.translatable(getBlockState().getBlock().getDescriptionId());
  }

  @Override
  public int getContainerSize() {
    return 3 * 3;
  }
}