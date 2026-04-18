package com.khazoda.breakerplacer.screen;

import com.khazoda.breakerplacer.registry.RegScreenHandlers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;

public class PlacerScreenHandler extends DispenserMenu {
  private final Container inventory;
  private final BlockPos pos;

  public PlacerScreenHandler(int syncId, Inventory playerInventory, BlockPos pos) {
    this(syncId, playerInventory, new SimpleContainer(9));
  }

  public PlacerScreenHandler(int syncId, Inventory playerInventory, Container inventory) {
    super(syncId, playerInventory, inventory);
    this.pos = BlockPos.ZERO;
    checkContainerSize(inventory, 9);
    this.inventory = inventory;
    inventory.startOpen(playerInventory.player);

    int m;
    int l;

    // Block Inventory
    for (m = 0; m < 3; ++m) {
      for (l = 0; l < 3; ++l) {
        this.addSlot(new Slot(inventory, l + m * 3, 62 + l * 18, 17 + m * 18));
      }
    }
    // Player Inventory
    for (m = 0; m < 3; ++m) {
      for (l = 0; l < 9; ++l) {
        this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
      }
    }
    // Player Hotbar
    for (m = 0; m < 9; ++m) {
      this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
    }
  }

  public BlockPos getPos() {
    return pos;
  }

  @Override
  public MenuType<?> getType() {
    return RegScreenHandlers.PLACER_SCREEN_HANDLER;
  }

  @Override
  public boolean stillValid(Player player) {
    return this.inventory.stillValid(player);
  }


}