/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.fx;

import com.mojang.serialization.MapCodec;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import org.jetbrains.annotations.NotNull;

public class SparkleParticleType extends ParticleType<SparkleParticleData> {
	public SparkleParticleType() {
		super(false);
	}

	@NotNull
	@Override
	public MapCodec<SparkleParticleData> codec() {
		return SparkleParticleData.CODEC;
	}

	@Override
	public StreamCodec<? super RegistryFriendlyByteBuf, SparkleParticleData> streamCodec() {
		return SparkleParticleData.STREAM_CODEC;
	}

	public static class Factory implements ParticleProvider<SparkleParticleData> {
		private final SpriteSet sprite;

		public Factory(SpriteSet sprite) {
			this.sprite = sprite;
		}

		@Override
		public Particle createParticle(SparkleParticleData data, ClientLevel world, double x, double y, double z, double mx, double my, double mz) {
			return new FXSparkle(world, x, y, z, data.size, data.r, data.g, data.b, data.m, data.fake, data.noClip, data.corrupt, sprite);
		}
	}
}
