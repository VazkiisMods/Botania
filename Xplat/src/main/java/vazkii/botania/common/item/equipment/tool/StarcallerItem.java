/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.tool;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.entity.FallingStarEntity;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.item.equipment.tool.manasteel.ManasteelSwordItem;

public class StarcallerItem extends ManasteelSwordItem {

	private static final int MANA_PER_DAMAGE = 120;
	/* Number of ticks between two stars */
	private static final int INTERVAL = 12;

	public StarcallerItem(Properties props) {
		super(BotaniaAPI.instance().getTerrasteelItemTier(), props);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		if (!(entity instanceof Player player)) {
			return;
		}

		// Initialize timer for new items
		if (!stack.has(BotaniaDataComponents.LAST_TRIGGER_TIME)) {
			stack.set(BotaniaDataComponents.LAST_TRIGGER_TIME, world.getGameTime());
		}

		MobEffectInstance haste = player.getEffect(MobEffects.DIG_SPEED);
		float check = haste == null ? 0.16666667F : haste.getAmplifier() == 1 ? 0.5F : 0.4F;

		long timeSinceLast = world.getGameTime() - stack.getOrDefault(BotaniaDataComponents.LAST_TRIGGER_TIME, world.getGameTime());
		if (timeSinceLast > INTERVAL && player.getMainHandItem() == stack && player.attackAnim == check && !world.isClientSide) {
			stack.set(BotaniaDataComponents.LAST_TRIGGER_TIME, world.getGameTime());
			summonFallingStar(stack, world, player);
		}
	}

	private void summonFallingStar(ItemStack stack, Level world, Player player) {
		BlockHitResult pos = ToolCommons.raytraceFromEntity(player, 48, false);
		if (pos.getType() == HitResult.Type.BLOCK) {
			Vec3 posVec = Vec3.atLowerCornerOf(pos.getBlockPos());
			Vec3 motVec = new Vec3((0.5 * Math.random() - 0.25) * 18, 24, (0.5 * Math.random() - 0.25) * 18);
			posVec = posVec.add(motVec);
			motVec = motVec.normalize().reverse().scale(1.5);

			FallingStarEntity star = new FallingStarEntity(player, world);
			star.setPos(posVec.x, posVec.y, posVec.z);
			star.setDeltaMovement(motVec);
			world.addFreshEntity(star);

			if (!world.isRaining()
					&& Math.abs(world.getDayTime() - 18000) < 1800
					&& Math.random() < 0.125) {
				FallingStarEntity bonusStar = new FallingStarEntity(player, world);
				bonusStar.setPos(posVec.x, posVec.y, posVec.z);
				bonusStar.setDeltaMovement(motVec.x + Math.random() - 0.5,
						motVec.y + Math.random() - 0.5, motVec.z + Math.random() - 0.5);
				world.addFreshEntity(bonusStar);
			}

			stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(InteractionHand.MAIN_HAND));
			world.playSound(null, player.getX(), player.getY(), player.getZ(), BotaniaSounds.starcaller, SoundSource.PLAYERS, 1F, 1F);
		}
	}

	@Override
	public int getManaPerDamage() {
		return MANA_PER_DAMAGE;
	}
}
