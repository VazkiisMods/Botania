/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.tool.bow;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.item.equipment.tool.ToolCommons;

import java.util.function.Consumer;

public class CrystalBowItem extends LivingwoodBowItem {

	private static final int ARROW_COST = 200;

	public CrystalBowItem(Properties builder) {
		super(builder);
	}

	// [VanillaCopy] super
	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand handIn) {
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		boolean canMaterializeArrow = canFire(itemstack, playerIn); // Botania - custom check

		if (!playerIn.getAbilities().instabuild && !canMaterializeArrow) {
			return InteractionResultHolder.fail(itemstack);
		} else {
			playerIn.startUsingItem(handIn);
			return InteractionResultHolder.consume(itemstack);
		}
	}

	// [VanillaCopy] BowItem
	@Override
	public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, LivingEntity entityLiving, int timeLeft) {
		if (entityLiving instanceof Player player) {
			boolean canMaterializeArrow = canFire(stack, player); // Botania - custom check
			ItemStack arrowStack = player.getProjectile(stack);

			int i = (int) ((getUseDuration(stack) - timeLeft) * chargeVelocityMultiplier()); // Botania - velocity multiplier
			if (i < 0) {
				return;
			}

			if (!arrowStack.isEmpty() || canMaterializeArrow) {
				if (arrowStack.isEmpty()) {
					arrowStack = new ItemStack(Items.ARROW);
				}

				float power = getPowerForTime(i);
				if (!((double) power < 0.1D)) {
					boolean markUnpickable = player.getAbilities().instabuild || arrowStack.is(Items.ARROW); // Botania
					if (!level.isClientSide) {
						ArrowItem arrowItem = (ArrowItem) (arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
						AbstractArrow arrow = arrowItem.createArrow(level, arrowStack, player);
						arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 3.0F, 1.0F);
						if (power == 1.0F) {
							arrow.setCritArrow(true);
						}

						int powerEnch = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
						if (powerEnch > 0) {
							arrow.setBaseDamage(arrow.getBaseDamage() + (double) powerEnch * 0.5D + 0.5D);
						}

						int knockback = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
						if (knockback > 0) {
							arrow.setKnockback(knockback);
						}

						if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAME, stack) > 0) {
							arrow.setRemainingFireTicks(100 * 20);
						}

						stack.hurtAndBreak(1, player, (p) -> {
							p.broadcastBreakEvent(player.getUsedItemHand());
						});
						if (markUnpickable || player.getAbilities().instabuild && (arrowStack.is(Items.SPECTRAL_ARROW) || arrowStack.is(Items.TIPPED_ARROW))) {
							arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
						}

						level.addFreshEntity(arrow);
					}

					level.playSound(null, player.getX(), player.getY(), player.getZ(),
							SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
							1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
					if (!markUnpickable && !player.getAbilities().instabuild) {
						arrowStack.shrink(1);
						if (arrowStack.isEmpty()) {
							player.getInventory().removeItem(arrowStack);
						}
					}

					player.awardStat(Stats.ITEM_USED.get(this));
				}
			}
		}
	}

	@Override
	public float chargeVelocityMultiplier() {
		return 2F;
	}

	private boolean canFire(ItemStack stack, Player player) {
		boolean infinity = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
		return player.getAbilities().instabuild || ManaItemHandler.instance().requestManaExactForTool(stack, player, ARROW_COST / (infinity ? 2 : 1), false);
	}

	@Override
	public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
		boolean infinity = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
		return ToolCommons.damageItemIfPossible(stack, amount, entity, ARROW_COST / (infinity ? 2 : 1));
	}
}
