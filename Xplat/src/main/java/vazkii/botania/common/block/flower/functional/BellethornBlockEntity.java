/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.flower.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.common.block.BotaniaFlowerBlocks;

import java.util.List;
import java.util.function.Predicate;

public class BellethornBlockEntity extends FunctionalFlowerBlockEntity {
	public static final int RANGE = 6;
	public static final int RANGE_MINI = 1;

	protected BellethornBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public BellethornBlockEntity(BlockPos pos, BlockState state) {
		this(BotaniaFlowerBlocks.BELLETHORNE, pos, state);
	}

	@Override
	public int getColor() {
		return 0xBA3421;
	}

	@Override
	public int getMaxMana() {
		return 1000;
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (getLevel().isClientSide || redstoneSignal > 0) {
			return;
		}

		if (ticksExisted % 200 == 0) {
			sync();
		}

		final int manaToUse = getManaCost();

		if (ticksExisted % 5 == 0) {
			int range = getRange();
			List<LivingEntity> entities = getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(getEffectivePos()).inflate(range), getSelector());

			for (LivingEntity entity : entities) {
				if (getMana() < manaToUse) {
					break;
				}
				if (entity.hurtTime == 0) {
					int dmg = 4;
					if (entity instanceof Witch) {
						dmg = 20;
					}

					entity.hurt(getLevel().damageSources().magic(), dmg);
					addMana(-manaToUse);
				}
			}
		}
	}

	@Override
	public boolean acceptsRedstone() {
		return true;
	}

	public int getManaCost() {
		return 24;
	}

	public int getRange() {
		return RANGE;
	}

	public Predicate<Entity> getSelector() {
		return entity -> !(entity instanceof Player);
	}

	@Override
	public RadiusDescriptor getRadius() {
		return RadiusDescriptor.Rectangle.square(getEffectivePos(), getRange());
	}

	public static class Mini extends BellethornBlockEntity {
		public Mini(BlockPos pos, BlockState state) {
			super(BotaniaFlowerBlocks.BELLETHORNE_CHIBI, pos, state);
		}

		@Override
		public int getRange() {
			return RANGE_MINI;
		}
	}

}
