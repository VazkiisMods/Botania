/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.network.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.common.block.block_entity.corporea.CorporeaIndexBlockEntity;
import vazkii.botania.network.BotaniaPacket;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public record IndexKeybindRequestPacket(ItemStack stack) implements BotaniaPacket {
	public static final ResourceLocation ID = botaniaRL("idx");

	@Override
	public void encode(FriendlyByteBuf buf) {
		buf.writeItem(stack());
	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	public static IndexKeybindRequestPacket decode(FriendlyByteBuf buf) {
		return new IndexKeybindRequestPacket(buf.readItem());
	}

	public void handle(MinecraftServer server, ServerPlayer player) {
		var stack = this.stack();
		server.execute(() -> {
			if (player.isSpectator()) {
				return;
			}

			for (CorporeaIndexBlockEntity index : CorporeaIndexBlockEntity.getNearbyValidIndexes(player)) {
				index.performPlayerRequest(player, CorporeaHelper.instance().createMatcher(stack, true), stack.getCount());
			}
		});
	}
}
