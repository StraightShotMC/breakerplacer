package com.khazoda.breakerplacer.registry;

import com.khazoda.breakerplacer.BreakerPlacer;
import com.khazoda.breakerplacer.block.BreakerBlock;
import com.khazoda.breakerplacer.block.PlacerBlock;
import com.khazoda.breakerplacer.util.RegistryHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;

import static com.khazoda.breakerplacer.util.RegistryHelper.newID;

public class RegBlocks {
  public static final Item.Settings defaultItemSettings = new Item.Settings().maxCount(64);

  public static final Block BREAKER_BLOCK = register("breaker", BreakerBlock::new, defaultItemSettings);
  public static final Block PLACER_BLOCK = register("placer", PlacerBlock::new, defaultItemSettings);

  public static void init() {
    BreakerPlacer.loadedRegistries += 1;
  }


  /* Register block and item with default item settings */
  private static Block register(String name, Function<AbstractBlock.Settings, Block> factory, Item.Settings itemSettings) {
    Block block = factory.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, newID(name))));
    itemSettings = itemSettings.registryKey(RegistryKey.of(RegistryKeys.ITEM, newID(name))).useBlockPrefixedTranslationKey();
    return RegistryHelper.registerBlock(name, block, itemSettings);
  }

  /* Register block *with* corresponding item*/
  private static <I extends BlockItem> BlockItem register(String name, I blockItem) {
    return RegistryHelper.registerBlockItem(name, blockItem);
  }

  /* Register block *without* corresponding item */
  private static <B extends Block> B register(String name, B block) {
    return RegistryHelper.registerBlockOnly(name, block);
  }

  /* Register item */
  private static Item register(String name) {
    return RegistryHelper.registerItem(name, new Item(new Item.Settings().maxCount(64)));
  }

  /* Register armour material */
  private static ArmorMaterial register(String name, ArmorMaterial material) {
    return material;
  }
}
