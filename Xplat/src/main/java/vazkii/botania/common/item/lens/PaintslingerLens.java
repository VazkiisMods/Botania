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
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.api.item.SparkEntity;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.network.EffectType;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.*;
import java.util.function.Function;

public class PaintslingerLens extends Lens {

	@Override
	public boolean collideBurst(ManaBurst burst, HitResult pos, boolean isManaBlock, boolean shouldKill, ItemStack stack) {
		Entity entity = burst.entity();
		boolean isRainbowLens = LensItem.isLensRainbow(stack);
		DyeColor lensColor = LensItem.getLensColor(stack);
		if (!entity.level().isClientSide && !burst.isFake() && (isRainbowLens || lensColor != null)) {
			List<DyeColor> possibleColors = isRainbowLens ? ColorHelper.supportedColors().toList() : List.of(lensColor);
			if (pos.getType() == HitResult.Type.ENTITY) {
				Entity collidedWith = ((EntityHitResult) pos).getEntity();
				if (collidedWith instanceof Sheep sheep) {
					int r = 20;
					DyeColor sheepColor = sheep.getColor();
					List<Sheep> sheepList = entity.level().getEntitiesOfClass(Sheep.class,
							new AABB(sheep.getX() - r, sheep.getY() - r, sheep.getZ() - r,
									sheep.getX() + r, sheep.getY() + r, sheep.getZ() + r),
							s -> s.getColor() == sheepColor);
					for (Sheep other : sheepList) {
						other.setColor(getColorToApply(possibleColors, sheep.level()));
					}
					shouldKill = true;
				} else if (collidedWith instanceof SparkEntity spark) {
					spark.setNetwork(getColorToApply(possibleColors, collidedWith.level()));
				}
			} else if (pos.getType() == HitResult.Type.BLOCK) {
				BlockPos hitPos = ((BlockHitResult) pos).getBlockPos();
				Block hitBlock = entity.level().getBlockState(hitPos).getBlock();
				ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(hitBlock);

				if (BotaniaAPI.instance().getPaintableBlocks().containsKey(blockId)) {
					List<BlockPos> coordsToPaint = new ArrayList<>();
					Queue<BlockPos> coordsToCheck = new ArrayDeque<>();
					Set<BlockPos> checkedCoords = new HashSet<>();
					coordsToCheck.add(hitPos);
					checkedCoords.add(hitPos);

					while (!coordsToCheck.isEmpty() && coordsToPaint.size() < 1000) {
						BlockPos coords = coordsToCheck.remove();
						coordsToPaint.add(coords);

						for (Direction dir : Direction.values()) {
							BlockPos candidatePos = coords.relative(dir);
							if (checkedCoords.add(candidatePos) && entity.level().getBlockState(candidatePos).is(hitBlock)) {
								coordsToCheck.add(candidatePos);
							}
						}
					}
					for (BlockPos coords : coordsToPaint) {
						DyeColor placeColor = getColorToApply(possibleColors, entity.level());
						BlockState stateThere = entity.level().getBlockState(coords);

						Function<DyeColor, Block> f = BotaniaAPI.instance().getPaintableBlocks().get(blockId);
						Block newBlock = f.apply(placeColor);
						if (newBlock != stateThere.getBlock()) {
							entity.level().setBlockAndUpdate(coords, newBlock.withPropertiesOf(stateThere));
							XplatAbstractions.INSTANCE.sendToNear(entity.level(), coords,
									new BotaniaEffectPacket(EffectType.PAINT_LENS,
											coords.getX(), coords.getY(), coords.getZ(), placeColor.getId()));
						}
					}
				}
			}
		}

		return shouldKill;
	}

	private static DyeColor getColorToApply(List<DyeColor> possibleColors, Level level) {
		return possibleColors.get(level.random.nextInt(possibleColors.size()));
	}

}
