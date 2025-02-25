package vazkii.botania.common.item.relic;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.item.Relic;
import vazkii.botania.client.core.proxy.ClientProxy;
import vazkii.botania.common.BotaniaDamageTypes;
import vazkii.botania.common.advancements.RelicBindTrigger;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;
import java.util.UUID;

public class RelicImpl implements Relic {

	private final ItemStack stack;
	@Nullable
	private final ResourceLocation advancementId;

	public RelicImpl(ItemStack stack, @Nullable ResourceLocation advancementId) {
		this.stack = stack;
		this.advancementId = advancementId;
	}

	@Override
	public void bindToUUID(UUID uuid) {
		stack.set(BotaniaDataComponents.SOULBOUND, uuid);
	}

	@Nullable
	@Override
	public UUID getSoulbindUUID() {
		return stack.get(BotaniaDataComponents.SOULBOUND);
	}

	@Nullable
	@Override
	public ResourceLocation getAdvancement() {
		return advancementId;
	}

	@Override
	public void tickBinding(Player player) {
		if (stack.isEmpty()) {
			return;
		}

		if (getSoulbindUUID() == null) {
			bindToUUID(player.getUUID());
			if (player instanceof ServerPlayer serverPlayer) {
				RelicBindTrigger.INSTANCE.trigger(serverPlayer, stack);
			}
		} else if (!isRightPlayer(player) && player.tickCount % 10 == 0 && shouldDamageWrongPlayer()) {
			player.hurt(damageSource(player.level().registryAccess()), 2);
		}
	}

	@Override
	public boolean isRightPlayer(Player player) {
		return player.getUUID().equals(getSoulbindUUID());
	}

	private static DamageSource damageSource(RegistryAccess registryAccess) {
		return BotaniaDamageTypes.Sources.relicDamage(registryAccess);
	}

	public static void addDefaultTooltip(ItemStack stack, List<Component> tooltip) {
		var relic = XplatAbstractions.INSTANCE.findRelic(stack);
		if (relic == null) {
			return;
		}
		if (relic.getSoulbindUUID() == null) {
			tooltip.add(Component.translatable("botaniamisc.relicUnbound"));
		} else {
			var player = ClientProxy.INSTANCE.getClientPlayer();
			if (player == null || !relic.isRightPlayer(player)) {
				tooltip.add(Component.translatable("botaniamisc.notYourSagittarius"));
			} else {
				tooltip.add(Component.translatable("botaniamisc.relicSoulbound", player.getName()));
			}
		}
	}
}
