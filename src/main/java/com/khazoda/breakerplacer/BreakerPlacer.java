package com.khazoda.breakerplacer;

import com.khazoda.breakerplacer.networking.ConfigSyncPayload;
import com.khazoda.breakerplacer.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Items;

public class BreakerPlacer implements ModInitializer {

  @Override
  public void onInitialize() {
    BreakerPlacerConfig.getInstance().load();

    RegBlocks.init();
    RegBlockEntities.init();
    RegSounds.init();
    RegScreenHandlers.init();
    RegNetworking.init();

    ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
      boolean toolDamage = BreakerPlacerConfig.getInstance().toolTakesDamage();
      ServerPlayNetworking.send(handler.getPlayer(), new ConfigSyncPayload(toolDamage));
    });

    ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> content.addAfter(Items.CRAFTER, RegBlocks.PLACER_BLOCK));
    ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> content.addAfter(Items.CRAFTER, RegBlocks.BREAKER_BLOCK));
    Constants.LOG.info("- Block Breaker & Block Placer Loaded -");
  }
}