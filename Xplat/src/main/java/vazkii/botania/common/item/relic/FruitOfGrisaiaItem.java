/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.relic;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.item.Relic;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.mixin.LivingEntityAccessor;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.Locale;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class FruitOfGrisaiaItem extends RelicItem {

	public FruitOfGrisaiaItem(Properties props) {
		super(props);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 32;
	}

	@NotNull
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return isBoot(stack) ? UseAnim.DRINK : UseAnim.EAT;
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		var relic = XplatAbstractions.INSTANCE.findRelic(stack);
		if (player.canEat(false) && relic != null && relic.isRightPlayer(player)) {
			return ItemUtils.startUsingInstantly(world, player, hand);
		}
		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void onUseTick(@NotNull Level world, @NotNull LivingEntity living, @NotNull ItemStack stack, int count) {
		if (!(living instanceof Player player)) {
			return;
		}
		if (ManaItemHandler.instance().requestManaExact(stack, player, 500, true)) {
			if (count % 5 == 0) {
				player.gameEvent(GameEvent.EAT);
				player.getFoodData().eat(2, 2.4F);
			}

			if (count == 5) {
				if (player.canEat(false)) {
					((LivingEntityAccessor) player).setUseItemRemaining(20);
				}
			}
		}
	}

	public static boolean isBoot(ItemStack stack) {
		String name = stack.getHoverName().getString().toLowerCase(Locale.ROOT).trim();
		return name.equals("das boot");
	}

	public static Relic makeRelic(ItemStack stack) {
		return new RelicImpl(stack, botaniaRL("challenge/infinite_fruit"));
	}

}
