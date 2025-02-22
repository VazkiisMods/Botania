/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.lens;

import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.api.mana.ManaSpreader;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.helper.ColorHelper;

public class FlareLens extends Lens {

	@Override
	public boolean allowBurstShooting(ItemStack stack, ManaSpreader spreader, boolean redstone) {
		return false;
	}

	@Override
	public void onControlledSpreaderTick(ItemStack stack, ManaSpreader spreader, boolean redstone) {
		if (!redstone) {
			emitParticles(stack, spreader);
		}
	}

	@Override
	public void onControlledSpreaderPulse(ItemStack stack, ManaSpreader spreader) {
		emitParticles(stack, spreader);
	}

	private void emitParticles(ItemStack stack, ManaSpreader spreader) {
		float rotationYaw = -(spreader.getRotationX() + 90F);
		float rotationPitch = spreader.getRotationY();

		// Lots of EntityThrowable copypasta
		float f = 0.3F;
		float mx = (float) (Mth.sin(rotationYaw / 180.0F * (float) Math.PI) * Mth.cos(rotationPitch / 180.0F * (float) Math.PI) * f / 2D);
		float mz = (float) (-(Mth.cos(rotationYaw / 180.0F * (float) Math.PI) * Mth.cos(rotationPitch / 180.0F * (float) Math.PI) * f) / 2D);
		float my = (float) (Mth.sin(rotationPitch / 180.0F * (float) Math.PI) * f / 2D);

		int hex;

		var level = spreader.getManaReceiverLevel();
		if (LensItem.isLensRainbow(stack)) {
			hex = Mth.hsvToRgb(level.getGameTime() * 2 % 360 / 360F, 1F, 1F);
		} else {
			DyeColor storedColor = LensItem.getLensColor(stack);
			if (storedColor != null) {
				hex = ColorHelper.getColorValue(storedColor);
			} else {
				hex = 0xFFFFFF;
			}
		}

		float r = ((hex & 0xFF0000) >> 16) / 255F;
		float g = ((hex & 0xFF00) >> 8) / 255F;
		float b = (hex & 0xFF) / 255F;

		WispParticleData data = WispParticleData.wisp(0.4F, r, g, b);
		// The start position is set a bit away from the spreader (along the burst's motion vector), as to not
		// collide with the spreader itself.
		var pos = spreader.getManaReceiverPos();
		level.addParticle(data, pos.getX() + 0.5 + mx * 4.5, pos.getY() + 0.5 + my * 4.5, pos.getZ() + 0.5 + mz * 4.5, mx, my, mz);
	}

}
