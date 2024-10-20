/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.network.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.client.gui.ItemsRemainingRenderHandler;
import vazkii.botania.network.BotaniaPacket;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public record UpdateItemsRemainingPacket(ItemStack stack, int count, @Nullable Component tooltip) implements BotaniaPacket {

	public static final ResourceLocation ID = botaniaRL("rem");

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeItem(stack);
		buf.writeVarInt(count);
		buf.writeBoolean(tooltip != null);
		if (tooltip != null) {
			buf.writeComponent(tooltip);
		}
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	public static UpdateItemsRemainingPacket decode(FriendlyByteBuf buf) {
		return new UpdateItemsRemainingPacket(
				buf.readItem(),
				buf.readVarInt(),
				buf.readBoolean() ? buf.readComponent() : null
		);
	}

	public static class Handler {
		public static void handle(UpdateItemsRemainingPacket packet) {
			ItemStack stack = packet.stack();
			int count = packet.count();
			Component tooltip = packet.tooltip();
			Minecraft.getInstance().execute(() -> ItemsRemainingRenderHandler.set(stack, count, tooltip));
		}
	}
}
