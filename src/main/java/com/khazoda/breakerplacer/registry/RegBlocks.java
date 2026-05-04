package com.khazoda.breakerplacer.registry;

import com.khazoda.breakerplacer.block.BreakerBlock;
import com.khazoda.breakerplacer.block.PlacerBlock;
import com.khazoda.breakerplacer.util.RegistryHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

import static com.khazoda.breakerplacer.Constants.ID;

public class RegBlocks {
  public static final Item.Properties defaultItemSettings = new Item.Properties().stacksTo(64);

  public static final Block BREAKER_BLOCK = register("breaker", BreakerBlock::new, defaultItemSettings);
  public static final Block PLACER_BLOCK = register("placer", PlacerBlock::new, defaultItemSettings);

  public static void init() {
  }


  /* Register block and item with default item settings */
  private static Block register(String name, Function<BlockBehaviour.Properties, Block> factory, Item.Properties itemSettings) {
    Block block = factory.apply(BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, ID(name))));
    itemSettings = itemSettings.setId(ResourceKey.create(Registries.ITEM, ID(name))).useBlockDescriptionPrefix();
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
    return RegistryHelper.registerItem(name, new Item(new Item.Properties().stacksTo(64)));
  }

  /* Register armour material */
  private static ArmorMaterial register(String name, ArmorMaterial material) {
    return material;
  }
}
