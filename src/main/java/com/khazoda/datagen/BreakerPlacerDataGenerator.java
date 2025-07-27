package com.khazoda.datagen;

import com.khazoda.breakerplacer.Constants;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jetbrains.annotations.Nullable;

public class BreakerPlacerDataGenerator implements DataGeneratorEntrypoint {
  @Override
  public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
    var pack = fabricDataGenerator.createPack();
    pack.addProvider(BreakerPlacerRecipeProvider::new);
  }

  @Override
  public @Nullable String getEffectiveModId() {
    return Constants.NAMESPACE;
  }
}
