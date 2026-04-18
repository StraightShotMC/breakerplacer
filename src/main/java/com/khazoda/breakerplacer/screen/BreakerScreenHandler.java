package com.khazoda.breakerplacer.screen;

import com.khazoda.breakerplacer.registry.RegScreenHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class BreakerScreenHandler extends AbstractContainerMenu {
  private final Container inventory;
  private final BlockPos pos;

  public BreakerScreenHandler(int syncId, Inventory playerInventory, BlockPos pos) {
    this(syncId, playerInventory, new SimpleContainer(10));
  }

  public BreakerScreenHandler(int syncId, Inventory playerInventory, Container inventory) {
    super(RegScreenHandlers.BREAKER_SCREEN_HANDLER, syncId);

    this.pos = BlockPos.ZERO;
    checkContainerSize(inventory, 10);
    this.inventory = inventory;
    inventory.startOpen(playerInventory.player);

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
      public boolean mayPlace(ItemStack stack) {
        return stack.has(DataComponents.TOOL);
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
  public ItemStack quickMoveStack(Player player, int slotIndex) {
    ItemStack newStack = ItemStack.EMPTY;
    Slot slot = this.slots.get(slotIndex);

      //noinspection ConstantValue (it's a lie)
      if (slot != null && slot.hasItem()) {
      ItemStack originalStack = slot.getItem();
      newStack = originalStack.copy();

      if (slotIndex < 10) {
        // Moving FROM Breaker TO Player
        if (!this.moveItemStackTo(originalStack, 10, 46, true)) {
          return ItemStack.EMPTY;
        }
      } else {
        // Moving FROM Player TO Breaker
        if (!this.moveItemStackTo(originalStack, 9, 10, false)) {
          if (!this.moveItemStackTo(originalStack, 0, 9, false)) {
            return ItemStack.EMPTY;
          }
        }
      }

      if (originalStack.isEmpty()) {
        slot.setByPlayer(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }

      if (originalStack.getCount() == newStack.getCount()) {
        return ItemStack.EMPTY;
      }

      slot.onTake(player, originalStack);
    }
    return newStack;
  }

  public BlockPos getPos() {
    return pos;
  }

  @Override
  public boolean stillValid(Player player) {
    return this.inventory.stillValid(player);
  }
}