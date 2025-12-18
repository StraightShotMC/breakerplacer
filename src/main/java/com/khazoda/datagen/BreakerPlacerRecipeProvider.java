package com.khazoda.datagen;

import com.khazoda.breakerplacer.registry.RegBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class BreakerPlacerRecipeProvider extends FabricRecipeProvider {
  public BreakerPlacerRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
    super(output, registriesFuture);


  }


  @Override
  protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
    return new RecipeGenerator(wrapperLookup, recipeExporter) {
      @Override
      public void generate() {
        createShaped(RecipeCategory.REDSTONE, RegBlocks.BREAKER_BLOCK, 1)
            .pattern("CCC")
            .pattern("CBC")
            .pattern("CRC")
            .input('C', Items.COBBLESTONE)
            .input('B', Items.BLAZE_ROD)
            .input('R', Items.REDSTONE)
            .criterion("has_blaze_rod", this.conditionsFromItem(Items.BLAZE_ROD))
            .offerTo(recipeExporter);

        createShaped(RecipeCategory.REDSTONE, RegBlocks.PLACER_BLOCK, 1)
            .pattern("CCC")
            .pattern("CBC")
            .pattern("CRC")
            .input('C', Items.COBBLESTONE)
            .input('B', Items.BREEZE_ROD)
            .input('R', Items.REDSTONE)
            .criterion("has_breeze_rod", this.conditionsFromItem(Items.BREEZE_ROD))
            .offerTo(recipeExporter);
      }
    };
  }

  @Override
  public String getName() {
    return "";
  }
}
