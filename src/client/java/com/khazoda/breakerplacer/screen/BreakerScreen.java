package com.khazoda.breakerplacer.screen;

import com.khazoda.breakerplacer.Constants;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CyclingSlotIcon;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Optional;

public class BreakerScreen extends HandledScreen<BreakerScreenHandler> {
  private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/gui/container/breaker.png");
  private static final Text TOOL_SLOT_TOOLTIP = Text.translatable("container.breakerplacer.breaker.tool_slot_tooltip");
  private static final Identifier EMPTY_SLOT_PICKAXE_TEXTURE = Identifier.of(Constants.NAMESPACE, "item/empty_slot_pickaxe");
  private static final Identifier EMPTY_SLOT_SHOVEL_TEXTURE = Identifier.of(Constants.NAMESPACE, "item/empty_slot_shovel");
  private static final Identifier EMPTY_SLOT_AXE_TEXTURE = Identifier.of(Constants.NAMESPACE, "item/empty_slot_axe");
  private static final Identifier EMPTY_SLOT_SHEARS_TEXTURE = Identifier.of(Constants.NAMESPACE, "item/empty_slot_shears");
  private static final List<Identifier> EMPTY_SLOT_TEXTURES = List.of(
      EMPTY_SLOT_PICKAXE_TEXTURE, EMPTY_SLOT_SHOVEL_TEXTURE, EMPTY_SLOT_AXE_TEXTURE, EMPTY_SLOT_SHEARS_TEXTURE
  );

  private final CyclingSlotIcon templateSlotIcon = new CyclingSlotIcon(9);

  public BreakerScreen(BreakerScreenHandler handler, PlayerInventory inventory, Text title) {
    super(handler, inventory, title);
  }

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta) {
    renderBackground(context, mouseX, mouseY, delta);
    super.render(context, mouseX, mouseY, delta);
    this.renderSlotTooltip(context, mouseX, mouseY);
    drawMouseoverTooltip(context, mouseX, mouseY);
  }

  @Override
  protected void init() {
    super.init();
    titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
  }

  @Override
  protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
    int x = (width - backgroundWidth) / 2;
    int y = (height - backgroundHeight) / 2;
    context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);
    this.templateSlotIcon.render(this.handler, context, delta, x, y);
  }

  @Override
  protected void handledScreenTick() {
    super.handledScreenTick();
    this.templateSlotIcon.updateTexture(EMPTY_SLOT_TEXTURES);
  }

  private void renderSlotTooltip(DrawContext context, int mouseX, int mouseY) {
    Optional<Text> optional = Optional.empty();
    if (this.focusedSlot != null) {
      ItemStack itemStack = this.handler.getSlot(9).getStack();
      if (itemStack.isEmpty()) {
        if (this.focusedSlot.id == 9) {
          optional = Optional.of(TOOL_SLOT_TOOLTIP);
        }
      }
    }
    optional.ifPresent(text -> context.drawOrderedTooltip(this.textRenderer, this.textRenderer.wrapLines(text, 115), mouseX, mouseY));
  }
}
