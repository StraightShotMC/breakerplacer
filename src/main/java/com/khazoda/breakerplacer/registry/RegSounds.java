package com.khazoda.breakerplacer.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

import static com.khazoda.breakerplacer.Constants.ID;

public class RegSounds {
  public static final SoundEvent BREAK = register("break");
  public static final SoundEvent FAIL = register("fail");

  public static void init() {
  }

  private static SoundEvent register(String name) {
    return Registry.register(BuiltInRegistries.SOUND_EVENT, name, SoundEvent.createVariableRangeEvent(ID(name)));
  }
}
