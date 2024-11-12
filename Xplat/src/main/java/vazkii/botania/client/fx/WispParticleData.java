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
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.codec.StreamCodec;

import org.jetbrains.annotations.NotNull;

import io.netty.buffer.ByteBuf;

public class WispParticleData implements ParticleOptions {
	public static final MapCodec<WispParticleData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.FLOAT.fieldOf("size").forGetter(d -> d.size),
			Codec.FLOAT.fieldOf("r").forGetter(d -> d.r),
			Codec.FLOAT.fieldOf("g").forGetter(d -> d.g),
			Codec.FLOAT.fieldOf("b").forGetter(d -> d.b),
			Codec.FLOAT.fieldOf("maxAgeMul").forGetter(d -> d.maxAgeMul),
			Codec.BOOL.fieldOf("depthTest").forGetter(d -> d.depthTest),
			Codec.BOOL.fieldOf("noClip").forGetter(d -> d.noClip),
			Codec.FLOAT.optionalFieldOf("gravity", 0.0f).forGetter(d -> d.gravity)
	)
			.apply(instance, WispParticleData::new));
	public static final StreamCodec<ByteBuf, WispParticleData> STREAM_CODEC = StreamCodec.of(
			(buf, d) -> {
				buf.writeFloat(d.size);
				buf.writeFloat(d.r);
				buf.writeFloat(d.g);
				buf.writeFloat(d.b);
				buf.writeFloat(d.maxAgeMul);
				buf.writeBoolean(d.depthTest);
				buf.writeBoolean(d.noClip);
				buf.writeFloat(d.gravity);
			},
			buf -> new WispParticleData(
					buf.readFloat(),
					buf.readFloat(),
					buf.readFloat(),
					buf.readFloat(),
					buf.readFloat(),
					buf.readBoolean(),
					buf.readBoolean(),
					buf.readFloat()
			)
	);
	public final float size;
	public final float r, g, b;
	public final float maxAgeMul;
	public final boolean depthTest;
	public final boolean noClip;
	public final float gravity;

	public static WispParticleData wisp(float size, float r, float g, float b) {
		return wisp(size, r, g, b, 1);
	}

	public static WispParticleData wisp(float size, float r, float g, float b, float maxAgeMul) {
		return wisp(size, r, g, b, maxAgeMul, true);
	}

	public static WispParticleData wisp(float size, float r, float g, float b, boolean depth) {
		return wisp(size, r, g, b, 1, depth);
	}

	public static WispParticleData wisp(float size, float r, float g, float b, float maxAgeMul, boolean depthTest) {
		return wisp(size, r, g, b, maxAgeMul, depthTest, 0.0f);
	}

	public static WispParticleData wisp(float size, float r, float g, float b, float maxAgeMul, float gravity) {
		return wisp(size, r, g, b, maxAgeMul, true, gravity);
	}

	public static WispParticleData wisp(float size, float r, float g, float b, float maxAgeMul, boolean depthTest, float gravity) {
		return new WispParticleData(size, r, g, b, maxAgeMul, depthTest, false, gravity);
	}

	private WispParticleData(float size, float r, float g, float b, float maxAgeMul, boolean depthTest, boolean noClip, float gravity) {
		this.size = size;
		this.r = r;
		this.g = g;
		this.b = b;
		this.maxAgeMul = maxAgeMul;
		this.depthTest = depthTest;
		this.noClip = noClip;
		this.gravity = gravity;
	}

	public WispParticleData withNoClip(boolean v) {
		if (noClip == v) {
			return this;
		} else {
			return new WispParticleData(size, r, g, b, maxAgeMul, depthTest, v, gravity);
		}
	}

	@NotNull
	@Override
	public ParticleType<WispParticleData> getType() {
		return BotaniaParticles.WISP;
	}
}
