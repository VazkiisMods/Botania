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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.common.block.BotaniaFlowerBlocks;

import java.util.List;

public class HyacidusBlockEntity extends FunctionalFlowerBlockEntity {
	private static final int RANGE = 6;
	private static final int COST = 20;

	public HyacidusBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaFlowerBlocks.HYACIDUS, pos, state);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (getLevel().isClientSide || redstoneSignal > 0) {
			return;
		}

		List<LivingEntity> entities = getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(getEffectivePos()).inflate(RANGE));
		for (LivingEntity entity : entities) {
			if (!(entity instanceof Player) && entity.getEffect(MobEffects.POISON) == null && getMana() >= COST && !entity.level().isClientSide && entity.getMobType() != MobType.UNDEAD) {
				entity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
				addMana(-COST);
			}
		}
	}

	@Override
	public boolean acceptsRedstone() {
		return true;
	}

	@Override
	public int getColor() {
		return 0x8B438F;
	}

	@Override
	public int getMaxMana() {
		return 180;
	}

	@Override
	public RadiusDescriptor getRadius() {
		return RadiusDescriptor.Rectangle.square(getEffectivePos(), RANGE);
	}

}
