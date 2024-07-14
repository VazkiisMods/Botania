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
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.common.block.BotaniaFlowerBlocks;

import java.util.List;

public class FallenKanadeBlockEntity extends FunctionalFlowerBlockEntity {
	private static final int RANGE = 2;
	private static final int COST = 120;

	public FallenKanadeBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaFlowerBlocks.FALLEN_KANADE, pos, state);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (!getLevel().isClientSide) {
			boolean did = false;
			List<LivingEntity> entities = getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(getEffectivePos()).inflate(RANGE), FallenKanadeBlockEntity::canHeal);
			for (LivingEntity toHeal : entities) {
				if (toHeal.getEffect(MobEffects.REGENERATION) == null && getMana() >= COST) {
					toHeal.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 59, 2, true, true));
					addMana(-COST);
					did = true;
				}
			}
			if (did) {
				sync();
			}
		}
	}

	private static boolean canHeal(LivingEntity e) {
		// Don't try to heal anything dead
		if (!e.isAlive()) {
			return false;
		}
		// heal pets and non-spectating players
		return e instanceof Player player && !player.isSpectator() || e instanceof TamableAnimal animal && animal.isTame() || e instanceof AbstractHorse horse && horse.isTamed();
	}

	@Override
	public RadiusDescriptor getRadius() {
		return RadiusDescriptor.Rectangle.square(getEffectivePos(), RANGE);
	}

	@Override
	public int getColor() {
		return 0xFFFF00;
	}

	@Override
	public int getMaxMana() {
		return 900;
	}

}
