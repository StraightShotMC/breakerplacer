package com.khazoda.breakerplacer.util;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.khazoda.breakerplacer.Constants.ID;

public class RegistryHelper {

  // Block Registry Helper Functions
  // *******************************
  // 1. Default BlockItem Registration Entrypoint: creates Identifier from ModID & block name
  public static <B extends Block> B registerBlock(String name, B block, Item.Properties itemSettings) {
    return registerBlock(ID(name), block, itemSettings);
  }

  // 2. Takes identifier and registers block and block items
  public static <B extends Block> B registerBlock(Identifier name, B block, Item.Properties itemSettings) {
    BlockItem item = new BlockItem(block, (itemSettings));
    item.registerBlocks(Item.BY_BLOCK, item);

    Registry.register(BuiltInRegistries.BLOCK, name, block);
    Registry.register(BuiltInRegistries.ITEM, name, item);
    return block;
  }

  public static <B extends Block> B registerBlockOnly(String name, B block) {
    return registerBlockOnly(ID(name), block);
  }

  public static <B extends Block> B registerBlockOnly(Identifier name, B block) {
    Registry.register(BuiltInRegistries.BLOCK, name, block);
    return block;
  }

  public static <I extends BlockItem> I registerBlockItem(String name, I blockItem) {
    return registerBlockItem(ID(name), blockItem);
  }

  public static <I extends BlockItem> I registerBlockItem(Identifier name, I blockItem) {
    Registry.register(BuiltInRegistries.ITEM, name, blockItem);
    return blockItem;
  }

  // Item Registry Helper Functions
  // ******************************
  public static Item registerItem(String name, Item item) {
    return Registry.register(BuiltInRegistries.ITEM, ID(name), item);
  }

}
