package com.khazoda.breakerplacer;

import com.khazoda.breakerplacer.registry.RegClientNetworking;
import com.khazoda.breakerplacer.registry.RegClientScreens;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class BreakerPlacerClient implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    RegClientScreens.init();
    RegClientNetworking.init();

    ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
      BreakerPlacerConfig.getInstance().load();
    });
  }
}