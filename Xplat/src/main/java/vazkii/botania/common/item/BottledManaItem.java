/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.entity.PixieEntity;
import vazkii.botania.common.helper.ItemNBTHelper;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class BottledManaItem extends Item {
	public static final int SWIGS = 6;
	private static final String TAG_SWIGS_LEFT = "swigsLeft";
	private static final String TAG_SEED = "randomSeed";

	private static final EffectAction[] EFFECT_ACTIONS = {
			BottledManaItem::effectRandomMotion,
			BottledManaItem::effectWater,
			BottledManaItem::effectSetOnFire,
			BottledManaItem::effectMiniExplosion,
			BottledManaItem::effectMegaJump,
			BottledManaItem::effectSetRandomHp,
			BottledManaItem::effectLotsOfHearts,
			BottledManaItem::effectDropAllItems,
			BottledManaItem::effectBreakNeck,
			BottledManaItem::effectHighestPossible,
			BottledManaItem::effectHyperspeed,
			BottledManaItem::effectNightVision,
			BottledManaItem::effectPixieFriend,
			BottledManaItem::effectNauseaBlindness,
			BottledManaItem::effectDropOwnHead
	};

	public BottledManaItem(Properties props) {
		super(props);
	}

	private static void effectRandomMotion(Level level, LivingEntity living) {
		living.setDeltaMovement((Math.random() - 0.5) * 3, living.getDeltaMovement().y(),
				(Math.random() - 0.5) * 3);
	}

	private static void effectWater(Level level, LivingEntity living) {
		if (!level.isClientSide && !level.dimensionType().ultraWarm()) {
			BlockPos playerPos = living.blockPosition();
			BlockState state = level.getBlockState(playerPos);
			BlockState replacedState;
			BlockPos waterPos;
			// loosely based on BucketItem#emptyContent:
			Player player = living instanceof Player ? (Player) living : null;
			if (state.isAir() || state.canBeReplaced(Fluids.WATER)
					|| state.getBlock() instanceof LiquidBlockContainer lbc
							&& lbc.canPlaceLiquid(player, level, playerPos, state, Fluids.WATER)) {
				waterPos = playerPos;
				replacedState = state;
			} else {
				BlockState aboveState = level.getBlockState(playerPos.above());
				waterPos = (aboveState.isAir() || aboveState.canBeReplaced(Fluids.WATER)
						|| aboveState.getBlock() instanceof LiquidBlockContainer lbc
								&& lbc.canPlaceLiquid(player, level, playerPos.above(), aboveState, Fluids.WATER))
										? playerPos.above()
										: null;
				replacedState = aboveState;
			}
			if (waterPos != null) {
				boolean placed;
				if (replacedState.getBlock() instanceof LiquidBlockContainer lbc) {
					placed = lbc.placeLiquid(level, waterPos, replacedState, Fluids.WATER.getSource(false));
				} else {
					if (replacedState.canBeReplaced(Fluids.WATER)) {
						level.destroyBlock(waterPos, true);
					}
					placed = level.setBlockAndUpdate(waterPos, Blocks.WATER.defaultBlockState());
				}
				if (placed) {
					level.playSound(living, waterPos, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 1.0f, level.random.nextFloat() + 0.5f);
				}
			}
		}
	}

	private static void effectSetOnFire(Level level, LivingEntity living) {
		if (!level.isClientSide) {
			living.setSecondsOnFire(4);
		}
	}

	private static void effectMiniExplosion(Level level, LivingEntity living) {
		if (!level.isClientSide) {
			level.explode(null, living.getX(), living.getY(),
					living.getZ(), 0.25F, Level.ExplosionInteraction.NONE);
		}
	}

	private static void effectMegaJump(Level level, LivingEntity living) {
		if (!level.dimensionType().ultraWarm()) {
			if (!level.isClientSide) {
				living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 5));
			}
			living.setDeltaMovement(living.getDeltaMovement().x(), 6, living.getDeltaMovement().z());
		}
	}

	private static void effectSetRandomHp(Level level, LivingEntity living) {
		if (!level.isClientSide) {
			float nextHealth = (float) (Math.random() * living.getMaxHealth());
			if (Mth.equal(nextHealth, 0.0F)) {
				nextHealth = 0.5F;
			}
			living.setHealth(nextHealth);
		}
	}

	private static void effectLotsOfHearts(Level level, LivingEntity living) {
		if (!level.isClientSide) {
			living.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 20 * 60 * 2, 9));
		}
	}

	private static void effectBreakNeck(Level level, LivingEntity living) {
		living.setXRot((float) Math.random() * 360F);
		living.setYRot((float) Math.random() * 180F);
	}

	private static void effectHighestPossible(Level level, LivingEntity living) {
		int x = Mth.floor(living.getX());
		int z = Mth.floor(living.getZ());
		for (int i = level.getMaxBuildHeight(); i > level.getMinBuildHeight(); i--) {
			BlockState state = level.getBlockState(new BlockPos(x, i, z));
			if (!state.isAir()) {
				living.teleportTo(living.getX(), i, living.getZ());
				break;
			}
		}
	}

	private static void effectDropAllItems(Level level, LivingEntity living) {
		if (!level.isClientSide && living instanceof Player player) {
			player.getInventory().dropAll();
		}
	}

	private static void effectHyperspeed(Level level, LivingEntity living) {
		if (!level.isClientSide) {
			living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 200));
		}
	}

	private static void effectNightVision(Level level, LivingEntity living) {
		if (!level.isClientSide) {
			living.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 6000, 0));
		}
	}

	private static void effectPixieFriend(Level level, LivingEntity living) {
		if (!level.isClientSide) {
			PixieEntity pixie = new PixieEntity(level);
			pixie.setPos(living.getX(), living.getY() + 1.5, living.getZ());
			level.addFreshEntity(pixie);
		}
	}

	private static void effectNauseaBlindness(Level level, LivingEntity living) {
		if (!level.isClientSide) {
			living.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 160, 3));
			living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 160, 0));
		}
	}

	private static void effectDropOwnHead(Level level, LivingEntity living) {
		if (!level.isClientSide && living instanceof Player player) {
			living.hurt(living.damageSources().magic(), living.getHealth() - 1);
			ItemStack skull = new ItemStack(Items.PLAYER_HEAD);
			ItemNBTHelper.setString(skull, "SkullOwner", player.getGameProfile().getName());
			living.spawnAtLocation(skull, 0);
		}
	}

	private long getSeed(ItemStack stack) {
		long seed = ItemNBTHelper.getLong(stack, TAG_SEED, -1);
		if (seed == -1) {
			return randomSeed(stack);
		}
		return seed;
	}

	private long randomSeed(ItemStack stack) {
		long seed = Math.abs(ThreadLocalRandom.current().nextLong());
		ItemNBTHelper.setLong(stack, TAG_SEED, seed);
		return seed;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> stacks, TooltipFlag flags) {
		stacks.add(Component.translatable("botaniamisc.bottleTooltip"));
	}

	@NotNull
	@Override
	public ItemStack finishUsingItem(@NotNull ItemStack stack, Level world, LivingEntity living) {
		EFFECT_ACTIONS[new Random(getSeed(stack)).nextInt(EFFECT_ACTIONS.length)].apply(world, living);
		int left = getSwigsLeft(stack);
		if (left <= 1) {
			// in case inventory is dropped:
			stack.setCount(0);
			return new ItemStack(Items.GLASS_BOTTLE);
		} else {
			setSwigsLeft(stack, left - 1);
			randomSeed(stack);
			return stack;
		}
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 20;
	}

	@NotNull
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	public static int getSwigsLeft(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_SWIGS_LEFT, SWIGS);
	}

	private void setSwigsLeft(ItemStack stack, int swigs) {
		ItemNBTHelper.setInt(stack, TAG_SWIGS_LEFT, swigs);
	}

	@FunctionalInterface
	private interface EffectAction {
		void apply(Level level, LivingEntity living);
	}
}
