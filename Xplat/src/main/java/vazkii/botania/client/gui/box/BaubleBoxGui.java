/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.gui.box;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import vazkii.botania.client.lib.ResourcesLib;

public class BaubleBoxGui extends AbstractContainerScreen<BaubleBoxContainer> {

	private static final ResourceLocation texture = new ResourceLocation(ResourcesLib.GUI_BAUBLE_BOX);
	private int mouseX;
	private int mouseY;

	public BaubleBoxGui(BaubleBoxContainer container, Inventory player, Component title) {
		super(container, player, title);
	}

	@Override
	public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(gui, mouseX, mouseY, partialTicks);
		super.render(gui, mouseX, mouseY, partialTicks);
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.renderTooltip(gui, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(GuiGraphics gui, int x, int y) {
		// No-op, there's no space for gui titles
	}

	@Override
	protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		gui.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);
		InventoryScreen.renderEntityInInventoryFollowsMouse(gui, leftPos + 8, topPos + 8, leftPos + 57, topPos + 78, 30, 0.0625F, this.mouseX, this.mouseY, minecraft.player);
	}

}
