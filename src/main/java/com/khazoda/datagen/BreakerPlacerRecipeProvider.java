package com.khazoda.datagen;

import com.khazoda.breakerplacer.registry.RegBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class BreakerPlacerRecipeProvider extends FabricRecipeProvider {
  public BreakerPlacerRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
    super(output, registriesFuture);


  }


  @Override
  protected RecipeProvider createRecipeProvider(HolderLookup.Provider wrapperLookup, RecipeOutput recipeExporter) {
    return new RecipeProvider(wrapperLookup, recipeExporter) {
      @Override
      public void buildRecipes() {
        shaped(RecipeCategory.REDSTONE, RegBlocks.BREAKER_BLOCK, 1)
            .pattern("CCC")
            .pattern("CBC")
            .pattern("CRC")
            .define('C', Items.COBBLESTONE)
            .define('B', Items.BLAZE_ROD)
            .define('R', Items.REDSTONE)
            .unlockedBy("has_blaze_rod", this.has(Items.BLAZE_ROD))
            .save(recipeExporter);

        shaped(RecipeCategory.REDSTONE, RegBlocks.PLACER_BLOCK, 1)
            .pattern("CCC")
            .pattern("CBC")
            .pattern("CRC")
            .define('C', Items.COBBLESTONE)
            .define('B', Items.BREEZE_ROD)
            .define('R', Items.REDSTONE)
            .unlockedBy("has_breeze_rod", this.has(Items.BREEZE_ROD))
            .save(recipeExporter);
      }
    };
  }

  @Override
  public String getName() {
    return "";
  }
}
