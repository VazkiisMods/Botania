/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.lens;

import it.unimi.dsi.fastutil.ints.IntList;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.phys.HitResult;

import vazkii.botania.api.internal.ManaBurst;

import java.util.List;

public class CelebratoryLens extends Lens {

	@Override
	public boolean collideBurst(ManaBurst burst, HitResult pos, boolean isManaBlock, boolean shouldKill, ItemStack stack) {
		Entity entity = burst.entity();
		if (pos.getType() == HitResult.Type.BLOCK) {
			if (!entity.level().isClientSide && !burst.isFake() && !isManaBlock) {
				ItemStack fireworkStack = generateFirework(burst.getColor());

				FireworkRocketEntity rocket = new FireworkRocketEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), fireworkStack);
				entity.level().addFreshEntity(rocket);
			}
			return true;
		}

		return shouldKill;
	}

	private ItemStack generateFirework(int color) {
		ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);

		FireworkExplosion.Shape shape;
		double rand = Math.random();
		if (rand > 0.9) {
			shape = FireworkExplosion.Shape.STAR;
		} else if (rand > 0.25) {
			shape = FireworkExplosion.Shape.SMALL_BALL;
		} else {
			shape = FireworkExplosion.Shape.LARGE_BALL;
		}

		FireworkExplosion explosion = new FireworkExplosion(shape, IntList.of(color), IntList.of(),
				Math.random() < 0.05, Math.random() < 0.05);
		stack.set(DataComponents.FIREWORKS, new Fireworks((int) (Math.random() * 3 + 2), List.of(explosion)));

		return stack;
	}

}
