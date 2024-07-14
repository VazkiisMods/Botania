/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.NotNull;

public class ExtrapolatedBucketItem extends Item {

	public ExtrapolatedBucketItem(Properties props) {
		super(props);
	}

	// [VanillaCopy] BucketItem, only the empty cases
	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand interactionHand) {
		ItemStack itemStack = player.getItemInHand(interactionHand);
		BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
		if (blockHitResult.getType() == HitResult.Type.MISS) {
			return InteractionResultHolder.pass(itemStack);
		} else if (blockHitResult.getType() != HitResult.Type.BLOCK) {
			return InteractionResultHolder.pass(itemStack);
		} else {
			BlockPos blockPos = blockHitResult.getBlockPos();
			Direction direction = blockHitResult.getDirection();
			BlockPos blockPos2 = blockPos.relative(direction);
			if (level.mayInteract(player, blockPos) && player.mayUseItemAt(blockPos2, direction, itemStack)) {
				BlockState blockState;
				blockState = level.getBlockState(blockPos);
				if (blockState.getBlock() instanceof BucketPickup bucketPickup) {
					ItemStack itemStack2 = bucketPickup.pickupBlock(player, level, blockPos, blockState);
					if (!itemStack2.isEmpty()) {
						player.awardStat(Stats.ITEM_USED.get(this));
						bucketPickup.getPickupSound().ifPresent((soundEvent) -> {
							player.playSound(soundEvent, 1.0F, 1.0F);
						});
						// Botania: some particles
						for (int x = 0; x < 5; x++) {
							level.addParticle(ParticleTypes.POOF, blockPos.getX() + Math.random(), blockPos.getY() + Math.random(), blockPos.getZ() + Math.random(), 0, 0, 0);
						}
						level.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);
						ItemStack itemStack3 = itemStack; // Botania: don't overwrite ourselves
						if (!level.isClientSide) {
							CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) player, itemStack2);
						}

						return InteractionResultHolder.sidedSuccess(itemStack3, level.isClientSide());
					}
				}

				return InteractionResultHolder.fail(itemStack);
			} else {
				return InteractionResultHolder.fail(itemStack);
			}
		}
	}

}
