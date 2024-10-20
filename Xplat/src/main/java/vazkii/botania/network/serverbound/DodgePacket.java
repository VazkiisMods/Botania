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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.bauble.RingOfDexterousMotionItem;
import vazkii.botania.network.BotaniaPacket;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class DodgePacket implements BotaniaPacket {
	public static final DodgePacket INSTANCE = new DodgePacket();
	public static final ResourceLocation ID = botaniaRL("do");

	public static DodgePacket decode(FriendlyByteBuf buf) {
		return INSTANCE;
	}

	@Override
	public void encode(FriendlyByteBuf buf) {

	}

	@Override
	public ResourceLocation getFabricId() {
		return ID;
	}

	public void handle(MinecraftServer server, ServerPlayer player) {
		server.execute(() -> {
			player.level().playSound(null, player.getX(), player.getY(), player.getZ(), BotaniaSounds.dash, SoundSource.PLAYERS, 1F, 1F);

			ItemStack ringStack = EquipmentHandler.findOrEmpty(BotaniaItems.dodgeRing, player);
			if (ringStack.isEmpty()) {
				player.connection.disconnect(Component.translatable("botaniamisc.invalidDodge"));
				return;
			}

			player.causeFoodExhaustion(0.3F);
			ItemNBTHelper.setInt(ringStack, RingOfDexterousMotionItem.TAG_DODGE_COOLDOWN, RingOfDexterousMotionItem.MAX_CD);
		});
	}
}
