/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.relic;

import com.google.common.base.Suppliers;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.Relic;
import vazkii.botania.client.gui.TooltipHandler;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class DiceOfFateItem extends RelicItem {
	public DiceOfFateItem(Properties props) {
		super(props);
	}

	public static final Supplier<List<ItemStack>> RELIC_STACKS = Suppliers.memoize(() -> List.of(
			new ItemStack(BotaniaItems.infiniteFruit),
			new ItemStack(BotaniaItems.kingKey),
			new ItemStack(BotaniaItems.flugelEye),
			new ItemStack(BotaniaItems.thorRing),
			new ItemStack(BotaniaItems.odinRing),
			new ItemStack(BotaniaItems.lokiRing)
	)
	);

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		var relic = XplatAbstractions.INSTANCE.findRelic(stack);

		if (relic != null && relic.isRightPlayer(player)) {
			if (world.isClientSide) {
				return InteractionResultHolder.success(stack);
			}

			world.playSound(null, player.getX(), player.getY(), player.getZ(), BotaniaSounds.diceOfFate, SoundSource.PLAYERS, 1F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

			List<Integer> possible = new ArrayList<>();
			for (int i = 0; i < 6; i++) {
				if (!hasRelicAlready(player, i)) {
					possible.add(i);
				}
			}

			if (!possible.isEmpty()) {
				int relicIdx = possible.get(world.random.nextInt(possible.size()));
				player.sendSystemMessage(Component.translatable("botaniamisc.diceRoll", relicIdx + 1).withStyle(ChatFormatting.DARK_GREEN));
				var toGive = RELIC_STACKS.get().get(relicIdx).copy();
				return InteractionResultHolder.consume(toGive);
			} else {
				int roll = world.random.nextInt(6) + 1;
				ResourceLocation tableId = BotaniaAPI.botaniaRL("dice/roll_" + roll);
				LootTable table = world.getServer().getLootData().getLootTable(tableId);
				LootParams context = new LootParams.Builder((ServerLevel) world)
						.withParameter(LootContextParams.THIS_ENTITY, player)
						.withParameter(LootContextParams.ORIGIN, player.position())
						.withLuck(player.getLuck())
						.create(LootContextParamSets.GIFT);

				List<ItemStack> generated = table.getRandomItems(context);
				for (ItemStack drop : generated) {
					player.getInventory().placeItemBackInInventory(drop);
				}
				String langKey = generated.isEmpty() ? "botaniamisc.dudDiceRoll" : "botaniamisc.diceRoll";
				player.sendSystemMessage(Component.translatable(langKey, roll).withStyle(ChatFormatting.DARK_GREEN));

				stack.shrink(1);
				return InteractionResultHolder.consume(stack);
			}
		}

		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag flags) {
		super.appendHoverText(stack, tooltipContext, tooltip, flags);
		tooltip.add(Component.literal(""));
		TooltipHandler.addOnShift(tooltip, () -> {
			String name = stack.getDescriptionId() + ".poem";
			for (int i = 0; i < 4; i++) {
				tooltip.add(Component.translatable(name + i).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
			}
		});
	}

	public static Relic makeRelic(ItemStack stack) {
		return new RelicImpl(stack, null) {
			@Override
			public boolean shouldDamageWrongPlayer() {
				return false;
			}
		};
	}

	private boolean hasRelicAlready(Player player, int relicId) {
		if (relicId < 0 || relicId > 6 || !(player instanceof ServerPlayer mpPlayer)) {
			return true;
		}

		var stack = RELIC_STACKS.get().get(relicId);
		var relic = XplatAbstractions.INSTANCE.findRelic(stack);

		if (relic != null && relic.getAdvancement() != null) {
			return PlayerHelper.hasAdvancement(mpPlayer, relic.getAdvancement());
		}

		return false;
	}

}
