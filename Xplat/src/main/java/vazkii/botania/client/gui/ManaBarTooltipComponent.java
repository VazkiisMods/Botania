/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import vazkii.botania.api.mana.ManaBarTooltip;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;

public class ManaBarTooltipComponent implements ClientTooltipComponent {
	private final float percentageFull;
	private final int pickLevel;

	private int mouseX, mouseY;
	// The total width of the tooltip box we're inside of, not to be confused
	// with the self-reported width of the component (getWidth).
	private int totalWidth;

	public ManaBarTooltipComponent(ManaBarTooltip component) {
		this.percentageFull = component.getPercentageFull();
		this.pickLevel = component.getPickLevel();
	}

	@Nullable
	public static ClientTooltipComponent tryConvert(TooltipComponent component) {
		if (component instanceof ManaBarTooltip t) {
			return new ManaBarTooltipComponent(t);
		}
		return null;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public int getWidth(Font font) {
		return 0;
	}

	public void setContext(int mouseX, int mouseY, int totalWidth) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.totalWidth = totalWidth;
	}

	@Override
	public void renderText(Font font, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource buffers) {
		int level = pickLevel;
		if (level < 0) {
			return;
		}

		boolean ss = level >= TerraShattererItem.LEVELS.length - 1;
		String rank = I18n.get("botania.rank" + pickLevel).replaceAll("&", "\u00a7");

		font.drawInBatch(rank, mouseX, mouseY - 16, 0xFFFFFF, true, matrix, buffers, Font.DisplayMode.NORMAL, 0, 0xF000F0);
		if (!ss) {
			rank = I18n.get("botania.rank" + (level + 1)).replaceAll("&", "\u00a7");
			font.drawInBatch(rank, mouseX + totalWidth - font.width(rank), mouseY - 16, 0xFFFFFF, true,
					matrix, buffers, Font.DisplayMode.NORMAL, 0, 0xF000F0);
		}
	}

	@Override
	public void renderImage(Font font, int x, int y, GuiGraphics gui) {
		PoseStack ps = gui.pose();
		int height = 3;
		int offsetFromBox = 4;

		ps.pushPose();

		float partialTicks = Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(true);
		if (pickLevel >= 0) {
			boolean ss = pickLevel >= TerraShattererItem.LEVELS.length - 1;

			int rainbowWidth = Math.min(totalWidth - (ss ? 0 : 1), (int) (totalWidth * percentageFull));
			float huePer = totalWidth == 0 ? 0F : 1F / totalWidth;
			float hueOff = (ClientTickHandler.ticksInGame + partialTicks) * 0.01F;

			gui.fill(mouseX - 1, mouseY - height - offsetFromBox - 1, mouseX + totalWidth + 1, mouseY - offsetFromBox, 0xFF000000);
			for (int i = 0; i < rainbowWidth; i++) {
				gui.fill(mouseX + i, mouseY - height - offsetFromBox, mouseX + i + 1, mouseY - offsetFromBox, 0xFF000000 | Mth.hsvToRgb((hueOff + huePer * i) % 1F, 1F, 1F));
			}
			gui.fill(mouseX + rainbowWidth, mouseY - height - offsetFromBox, mouseX + totalWidth, mouseY - offsetFromBox, 0xFF555555);
		} else {
			int manaBarWidth = (int) Math.ceil(totalWidth * percentageFull);

			gui.fill(mouseX - 1, mouseY - height - offsetFromBox - 1, mouseX + totalWidth + 1, mouseY - offsetFromBox, 0xFF000000);
			gui.fill(mouseX, mouseY - height - offsetFromBox, mouseX + manaBarWidth, mouseY - offsetFromBox, 0xFF000000 | Mth.hsvToRgb(0.528F, ((float) Math.sin((ClientTickHandler.ticksInGame +
					partialTicks) * 0.2) + 1F) * 0.3F + 0.4F, 1F));
			gui.fill(mouseX + manaBarWidth, mouseY - height - offsetFromBox, mouseX + totalWidth, mouseY - offsetFromBox, 0xFF555555);
		}
		ps.popPose();

		// Reset these after we're done each frame so it's obvious if things ever glitch out.
		mouseX = mouseY = 0;
		totalWidth = 50;
	}
}
