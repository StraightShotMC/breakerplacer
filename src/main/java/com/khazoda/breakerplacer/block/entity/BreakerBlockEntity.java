package com.khazoda.breakerplacer.block.entity;

import com.khazoda.breakerplacer.registry.RegBlockEntities;
import com.khazoda.breakerplacer.screen.BreakerScreenHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BreakerBlockEntity extends BaseBlockEntity implements WorldlyContainer {
  // Try tool slot (9) first, then 0-8
  private static final int[] AVAILABLE_SLOTS = new int[]{9, 0, 1, 2, 3, 4, 5, 6, 7, 8};

  public BreakerBlockEntity(BlockPos blockPos, BlockState blockState) {
    super(RegBlockEntities.BREAKER_BLOCK_ENTITY, blockPos, blockState);
  }

  @Override
  protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
    return new BreakerScreenHandler(syncId, playerInventory, this);
  }

  /* Returns true if item is successfully added, false if not */
  public void addToFirstFreeSlot(ItemStack stack) {
    int i = this.getMaxStackSize(stack);

    /* .size() - 1 to prevent tool slot from receiving items */
    for (int j = 0; j < this.inventory.size() - 1; j++) {
      ItemStack itemStack = this.inventory.get(j);
      if (itemStack.isEmpty() || ItemStack.isSameItemSameComponents(stack, itemStack)) {
        int k = Math.min(stack.getCount(), i - itemStack.getCount());
        if (k > 0) {
          if (itemStack.isEmpty()) {
            this.setItem(j, stack.split(k));
          } else {
            stack.shrink(k);
            itemStack.grow(k);
          }
        }
        if (stack.isEmpty()) {
          break;
        }
      }
    }
  }

  /* Exposes all slots to automation, starting with the tool slot */
  @Override
  public int[] getSlotsForFace(Direction side) {
    return AVAILABLE_SLOTS;
  }

  @Override
  public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
    return this.canPlaceItem(slot, stack);
  }

  @Override
  public boolean canPlaceItem(int slot, ItemStack stack) {
    if (slot == 9) {
      return stack.has(DataComponents.TOOL);
    }
    return super.canPlaceItem(slot, stack);
  }

  /* Stop tool slot from being extracted from by hoppers etc.*/
  @Override
  public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
    return slot != 9;
  }

  @Override
  public int getContainerSize() {
    return 10;
  }
}