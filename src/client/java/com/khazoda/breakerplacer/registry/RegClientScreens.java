package com.khazoda.breakerplacer.registry;

import com.khazoda.breakerplacer.screen.BreakerScreen;
import com.khazoda.breakerplacer.screen.PlacerScreen;
import net.minecraft.client.gui.screens.MenuScreens;

public class RegClientScreens {
  public static void init() {
    MenuScreens.register(RegScreenHandlers.PLACER_SCREEN_HANDLER, PlacerScreen::new);
    MenuScreens.register(RegScreenHandlers.BREAKER_SCREEN_HANDLER, BreakerScreen::new);
  }
}
