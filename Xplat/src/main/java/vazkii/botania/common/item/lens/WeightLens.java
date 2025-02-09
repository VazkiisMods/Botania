/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.lens;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.xplat.BotaniaConfig;

public class WeightLens extends Lens {
	@Override
	public boolean collideBurst(ManaBurst burst, HitResult pos, boolean isManaBlock, boolean shouldKill, ItemStack stack) {
		ThrowableProjectile entity = burst.entity();
		if (!entity.level().isClientSide && !burst.isFake() && pos.getType() == HitResult.Type.BLOCK) {
			int harvestLevel = BotaniaConfig.common().harvestLevelWeight();

			ServerLevel level = (ServerLevel) entity.level();
			BlockPos bPos = ((BlockHitResult) pos).getBlockPos();
			BlockState state = level.getBlockState(bPos);

			if (FallingBlock.isFree(level.getBlockState(bPos.below()))
					&& state.getDestroySpeed(level, bPos) != -1
					&& level.getBlockEntity(bPos) == null
					&& canSilkTouch(level, bPos, state, harvestLevel, entity.getOwner())) {
				FallingBlockEntity falling = FallingBlockEntity.fall(level, bPos, state);
				falling.time = 1;
				level.sendParticles(new BlockParticleOption(ParticleTypes.FALLING_DUST, state), bPos.getX() + 0.5, bPos.getY() + 0.5, bPos.getZ() + 0.5, 10, 0.45, 0.45, 0.45, 5);
			}
		}

		return shouldKill;
	}

	private static boolean canSilkTouch(ServerLevel level, BlockPos pos, BlockState state, int harvestLevel, @Nullable Entity owner) {
		if (state.is(BotaniaTags.Blocks.WEIGHT_LENS_WHITELIST)) {
			return true;
		}
		ItemStack harvestToolStack = BoreLens.getHarvestToolStack(harvestLevel, state).copy();
		if (harvestToolStack.isEmpty()) {
			return false;
		}
		harvestToolStack.enchant(level.holderLookup(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING), 1);

		Item blockItem = state.getBlock().asItem();
		for (var drop : Block.getDrops(state, level, pos, null, owner, harvestToolStack)) {
			if (drop.is(blockItem)) {
				return true;
			}
		}
		return false;
	}
}
