package com.khazoda.breakerplacer;

import com.khazoda.breakerplacer.registry.RegClientNetworking;
import com.khazoda.breakerplacer.registry.RegClientScreens;
import net.fabricmc.api.ClientModInitializer;

public class BreakerPlacerClient implements ClientModInitializer {

  @Override
  public void onInitializeClient() {
    RegClientScreens.init();
    RegClientNetworking.init();
  }
}