package com.khazoda.breakerplacer.util;

import com.khazoda.breakerplacer.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class RegistryHelper {

  // General use Identifier() maker function
  public static Identifier newID(String name) {
    return Identifier.fromNamespaceAndPath(Constants.NAMESPACE, name);
  }

  // Block Registry Helper Functions
  // *******************************
  // 1. Default BlockItem Registration Entrypoint: creates Identifier from ModID & block name
  public static <B extends Block> B registerBlock(String name, B block, Item.Properties itemSettings) {
    return registerBlock(newID(name), block, itemSettings);
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
    return registerBlockOnly(newID(name), block);
  }

  public static <B extends Block> B registerBlockOnly(Identifier name, B block) {
    Registry.register(BuiltInRegistries.BLOCK, name, block);
    return block;
  }

  public static <I extends BlockItem> I registerBlockItem(String name, I blockItem) {
    return registerBlockItem(newID(name), blockItem);
  }

  public static <I extends BlockItem> I registerBlockItem(Identifier name, I blockItem) {
    Registry.register(BuiltInRegistries.ITEM, name, blockItem);
    return blockItem;
  }

  public static <I extends CreativeModeTab> I registerItemGroup(I itemGroup) {
    Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Identifier.parse("basicstorage"), itemGroup);
    return itemGroup;
  }

  // Item Registry Helper Functions
  // ******************************
  public static Item registerItem(String name, Item item) {
    return Registry.register(BuiltInRegistries.ITEM, newID(name), item);
  }

}
