/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.entity.VineBallEntity;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.PlayerHelper;

import java.util.function.Predicate;

public class LivingwoodSlingshotItem extends Item {

	private static final Predicate<ItemStack> AMMO_FUNC = s -> s != null && s.is(BotaniaItems.vineBall);

	public LivingwoodSlingshotItem(Properties builder) {
		super(builder);
	}

	@Override
	public void releaseUsing(ItemStack stack, Level world, LivingEntity living, int duration) {
		int j = getUseDuration(stack, living) - duration;

		if (!world.isClientSide && (!(living instanceof Player player) || player.getAbilities().instabuild || PlayerHelper.hasAmmo(player, AMMO_FUNC))) {
			float f = j / 20.0F;
			f = (f * f + f * 2.0F) / 3.0F;

			if (f < 1F) {
				return;
			}

			if (living instanceof Player player && !player.getAbilities().instabuild) {
				PlayerHelper.consumeAmmo(player, AMMO_FUNC);
			}

			VineBallEntity ball = new VineBallEntity(living, false);
			ball.shootFromRotation(living, living.getXRot(), living.getYRot(), 0F, 1.5F, 1F);
			ball.setDeltaMovement(ball.getDeltaMovement().scale(1.6));
			world.addFreshEntity(ball);
			world.playSound(null, living.getX(), living.getY(), living.getZ(), BotaniaSounds.vineBallThrow, SoundSource.NEUTRAL, 1F, 0.4F / (living.getRandom().nextFloat() * 0.4F + 0.8F));
		}
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 72000;
	}

	@NotNull
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.getAbilities().instabuild || PlayerHelper.hasAmmo(player, AMMO_FUNC)) {
			return ItemUtils.startUsingInstantly(world, player, hand);
		}

		return InteractionResultHolder.pass(stack);
	}

}
