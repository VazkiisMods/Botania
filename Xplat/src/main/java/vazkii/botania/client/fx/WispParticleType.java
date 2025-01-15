/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.fx;

import com.mojang.serialization.Codec;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;

import org.jetbrains.annotations.NotNull;

public class WispParticleType extends ParticleType<WispParticleData> {
	public WispParticleType() {
		super(false, WispParticleData.DESERIALIZER);
	}

	@NotNull
	@Override
	public Codec<WispParticleData> codec() {
		return WispParticleData.CODEC;
	}

	public static class Factory implements ParticleProvider<WispParticleData> {
		private final SpriteSet sprite;

		public Factory(SpriteSet sprite) {
			this.sprite = sprite;
		}

		@Override
		public Particle createParticle(WispParticleData data, ClientLevel world, double x, double y, double z, double mx, double my, double mz) {
			FXWisp ret = new FXWisp(world, x, y, z, mx, my, mz, data.size, data.r, data.g, data.b, data.depthTest, data.maxAgeMul, data.noClip, data.gravity);
			ret.pickSprite(sprite);
			return ret;
		}
	}
}
