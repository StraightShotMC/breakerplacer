package com.khazoda.breakerplacer.screen;

import com.khazoda.breakerplacer.registry.RegScreenHandlers;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class BreakerScreenHandler extends ScreenHandler {
  private final Inventory inventory;
  private final BlockPos pos;

  public BreakerScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
    this(syncId, playerInventory, new SimpleInventory(10));
  }

  public BreakerScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
    super(RegScreenHandlers.BREAKER_SCREEN_HANDLER, syncId);

    this.pos = BlockPos.ORIGIN;
    checkSize(inventory, 10);
    this.inventory = inventory;
    inventory.onOpen(playerInventory.player);

    int m;
    int l;

    // Block Inventory (0-8)
    for (m = 0; m < 3; ++m) {
      for (l = 0; l < 3; ++l) {
        this.addSlot(new Slot(inventory, l + m * 3, 62 + l * 18, 17 + m * 18));
      }
    }

    // Tool Slot (9)
    this.addSlot(new Slot(inventory, 9, 26, 35) {
      @Override
      public boolean canInsert(ItemStack stack) {
        return stack.contains(DataComponentTypes.TOOL);
      }
    });

    // Player Inventory (10-36)
    for (m = 0; m < 3; ++m) {
      for (l = 0; l < 9; ++l) {
        this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
      }
    }

    // Player Hotbar (37-45)
    for (m = 0; m < 9; ++m) {
      this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
    }
  }

  @Override
  public ItemStack quickMove(PlayerEntity player, int slotIndex) {
    ItemStack newStack = ItemStack.EMPTY;
    Slot slot = this.slots.get(slotIndex);

    if (slot != null && slot.hasStack()) {
      ItemStack originalStack = slot.getStack();
      newStack = originalStack.copy();

      if (slotIndex < 10) {
        // Moving FROM Breaker TO Player
        if (!this.insertItem(originalStack, 10, 46, true)) {
          return ItemStack.EMPTY;
        }
      } else {
        // Moving FROM Player TO Breaker
        if (!this.insertItem(originalStack, 9, 10, false)) {
          if (!this.insertItem(originalStack, 0, 9, false)) {
            return ItemStack.EMPTY;
          }
        }
      }

      if (originalStack.isEmpty()) {
        slot.setStack(ItemStack.EMPTY);
      } else {
        slot.markDirty();
      }

      if (originalStack.getCount() == newStack.getCount()) {
        return ItemStack.EMPTY;
      }

      slot.onTakeItem(player, originalStack);
    }
    return newStack;
  }

  public BlockPos getPos() {
    return pos;
  }

  @Override
  public boolean canUse(PlayerEntity player) {
    return this.inventory.canPlayerUse(player);
  }
}