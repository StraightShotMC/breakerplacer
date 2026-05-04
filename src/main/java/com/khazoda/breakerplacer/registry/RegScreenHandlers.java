package com.khazoda.breakerplacer.registry;

import com.khazoda.breakerplacer.Constants;
import com.khazoda.breakerplacer.screen.BreakerScreenHandler;
import com.khazoda.breakerplacer.screen.PlacerScreenHandler;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.MenuType;

public class RegScreenHandlers {

  public static final MenuType<PlacerScreenHandler> PLACER_SCREEN_HANDLER = new ExtendedMenuType<>(PlacerScreenHandler::new, BlockPos.STREAM_CODEC.cast());
  public static final MenuType<BreakerScreenHandler> BREAKER_SCREEN_HANDLER = new ExtendedMenuType<>(BreakerScreenHandler::new, BlockPos.STREAM_CODEC.cast());

  public static void init() {
    Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "placer_screen_handler"), PLACER_SCREEN_HANDLER);
    Registry.register(BuiltInRegistries.MENU, Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "breaker_screen_handler"), BREAKER_SCREEN_HANDLER);
  }
}
