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

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.network.clientbound.UpdateItemsRemainingPacket;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.regex.Pattern;

public final class ItemsRemainingRenderHandler {

	private static final int maxTicks = 30;
	private static final int leaveTicks = 20;

	private static ItemStack stack = ItemStack.EMPTY;
	@Nullable
	private static Component customString;
	private static int ticks, count;

	public static void render(GuiGraphics gui, float partTicks) {
		PoseStack ms = gui.pose();
		if (ticks > 0 && !stack.isEmpty()) {
			int pos = maxTicks - ticks;
			Minecraft mc = Minecraft.getInstance();
			int x = mc.getWindow().getGuiScaledWidth() / 2 + 10 + Math.max(0, pos - leaveTicks);
			int y = mc.getWindow().getGuiScaledHeight() / 2;

			int start = maxTicks - leaveTicks;
			float alpha = ticks + partTicks > start ? 1F : (ticks + partTicks) / start;

			// RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
			int xp = x + (int) (16F * (1F - alpha));
			ms.pushPose();
			ms.translate(xp, y, 0F);
			ms.scale(alpha, 1F, 1F);
			gui.renderItem(stack, 0, 0);
			ms.popPose();

			Component text = Component.empty();

			if (customString == null) {
				if (!stack.isEmpty()) {
					text = stack.getHoverName().copy().withStyle(ChatFormatting.GREEN);
					if (count >= 0) {
						int max = stack.getMaxStackSize();
						int stacks = count / max;
						int rem = count % max;

						if (stacks == 0) {
							text = Component.literal(Integer.toString(count));
						} else {
							Component stacksText = Component.literal(Integer.toString(stacks)).withStyle(ChatFormatting.AQUA);
							Component maxText = Component.literal(Integer.toString(max)).withStyle(ChatFormatting.GRAY);
							Component remText = Component.literal(Integer.toString(rem)).withStyle(ChatFormatting.YELLOW);
							text = Component.literal(count + " (")
									.append(stacksText)
									.append("*")
									.append(maxText)
									.append("+")
									.append(remText)
									.append(")");
						}
					} else if (count == -1) {
						text = Component.literal("\u221E");
					}
				}
			} else {
				text = customString;
			}

			int color = 0x00FFFFFF | (int) (alpha * 0xFF) << 24;
			gui.drawString(mc.font, text, x + 20, y + 6, color);
		}
	}

	public static void tick() {
		if (ticks > 0) {
			--ticks;
		}
	}

	public static void send(@Nullable Player player, ItemStack stack, int count) {
		send(player, stack, count, null);
	}

	public static void set(ItemStack stack, int count, @Nullable Component str) {
		ItemsRemainingRenderHandler.stack = stack;
		ItemsRemainingRenderHandler.count = count;
		ItemsRemainingRenderHandler.customString = str;
		ticks = stack.isEmpty() ? 0 : maxTicks;
	}

	public static void send(@Nullable Player entity, ItemStack stack, int count, @Nullable Component str) {
		XplatAbstractions.INSTANCE.sendToPlayer(entity, new UpdateItemsRemainingPacket(stack, count, str));
	}

	public static void send(Player player, ItemStack displayStack, Pattern pattern) {
		int count = 0;
		for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (!stack.isEmpty() && pattern.matcher(stack.getDescriptionId()).find()) {
				count += stack.getCount();
			}
		}

		send(player, displayStack, count, null);
	}

}
