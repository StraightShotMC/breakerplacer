package com.khazoda.breakerplacer.screen;

import com.khazoda.breakerplacer.Constants;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class BreakerScreen extends AbstractContainerScreen<BreakerScreenHandler> {
  private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "textures/gui/container/breaker.png");
  private static final Component TOOL_SLOT_TOOLTIP = Component.translatable("container.breakerplacer.breaker.tool_slot_tooltip");
  private static final Identifier EMPTY_SLOT_PICKAXE_TEXTURE = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "item/empty_slot_pickaxe");
  private static final Identifier EMPTY_SLOT_SHOVEL_TEXTURE = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "item/empty_slot_shovel");
  private static final Identifier EMPTY_SLOT_AXE_TEXTURE = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "item/empty_slot_axe");
  private static final Identifier EMPTY_SLOT_HOE_TEXTURE = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "item/empty_slot_hoe");
  private static final Identifier EMPTY_SLOT_SWORD_TEXTURE = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "item/empty_slot_sword");
  private static final Identifier EMPTY_SLOT_SHEARS_TEXTURE = Identifier.fromNamespaceAndPath(Constants.NAMESPACE, "item/empty_slot_shears");
  private static final List<Identifier> EMPTY_SLOT_TEXTURES = List.of(
      EMPTY_SLOT_PICKAXE_TEXTURE, EMPTY_SLOT_SHOVEL_TEXTURE, EMPTY_SLOT_AXE_TEXTURE, EMPTY_SLOT_HOE_TEXTURE, EMPTY_SLOT_SWORD_TEXTURE, EMPTY_SLOT_SHEARS_TEXTURE
  );

  private final CyclingSlotBackground templateSlotIcon = new CyclingSlotBackground(9);

  public BreakerScreen(BreakerScreenHandler handler, Inventory inventory, Component title) {
    super(handler, inventory, title);
  }

  @Override
  protected void init() {
    super.init();
    titleLabelX = (imageWidth - font.width(title)) / 2;
  }

  @Override
  public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
    super.extractBackground(graphics, mouseX, mouseY, a);
    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;
    this.templateSlotIcon.extractRenderState(this.menu, graphics, a, x, y);
  }

  @Override
  protected void extractMenuBackground(GuiGraphicsExtractor graphics) {
    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;
    graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
  }

  @Override
  protected void containerTick() {
    super.containerTick();
    this.templateSlotIcon.tick(EMPTY_SLOT_TEXTURES);
  }

  private void renderSlotTooltip(GuiGraphicsExtractor context, int mouseX, int mouseY) {
    Optional<Component> optional = Optional.empty();
    if (this.hoveredSlot != null) {
      ItemStack itemStack = this.menu.getSlot(9).getItem();
      if (itemStack.isEmpty()) {
        if (this.hoveredSlot.index == 9) {
          optional = Optional.of(TOOL_SLOT_TOOLTIP);
        }
      }
    }
    optional.ifPresent(text -> context.setTooltipForNextFrame(this.font, this.font.split(text, 115), mouseX, mouseY));
  }
}
