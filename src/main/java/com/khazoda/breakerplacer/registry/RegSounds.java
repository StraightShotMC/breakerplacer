package com.khazoda.breakerplacer.registry;

import com.khazoda.breakerplacer.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class RegSounds {
  public static final SoundEvent BREAK = register("break");
  public static final SoundEvent FAIL = register("fail");

  public static void init() {
  }

  private static SoundEvent register(String name) {
    return Registry.register(BuiltInRegistries.SOUND_EVENT, name, SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath(Constants.NAMESPACE, name)));
  }
}
