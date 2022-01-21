/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.subtile.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;
import vazkii.botania.common.block.ModSubtiles;

import java.util.List;

public class SubTileFallenKanade extends TileEntityFunctionalFlower {
	private static final int RANGE = 2;
	private static final int COST = 120;

	public SubTileFallenKanade(BlockPos pos, BlockState state) {
		super(ModSubtiles.FALLEN_KANADE, pos, state);
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		if (!getLevel().isClientSide && getLevel().dimension() != Level.END) {
			boolean did = false;
			List<LivingEntity> entities = getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(getEffectivePos().offset(-RANGE, -RANGE, -RANGE), getEffectivePos().offset(RANGE + 1, RANGE + 1, RANGE + 1)), SubTileFallenKanade::canHeal);
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
		// heal pets and players
		return e instanceof Player || e instanceof TamableAnimal && ((TamableAnimal) e).isTame() || e instanceof AbstractHorse && ((AbstractHorse) e).isTamed();
	}

	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Square(getEffectivePos(), RANGE);
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
