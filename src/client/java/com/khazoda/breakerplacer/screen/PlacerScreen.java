package com.khazoda.breakerplacer.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class PlacerScreen extends AbstractContainerScreen<PlacerScreenHandler> {
  //A path to the gui texture. In this example we use the texture from the dispenser
  private static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/dispenser.png");

  public PlacerScreen(PlacerScreenHandler handler, Inventory inventory, Component title) {
    super(handler, inventory, title);
  }

  @Override
  public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
    renderBackground(context, mouseX, mouseY, delta);
    super.render(context, mouseX, mouseY, delta);
    renderTooltip(context, mouseX, mouseY);
  }

  @Override
  protected void init() {
    super.init();
    titleLabelX = (imageWidth - font.width(title)) / 2;
  }

  @Override
  protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;
    context.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
  }
}
