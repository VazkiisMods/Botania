/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.fx;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class WispParticleData implements ParticleOptions {
	public static final Codec<WispParticleData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
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

	@Override
	public void writeToNetwork(FriendlyByteBuf buf) {
		buf.writeFloat(size);
		buf.writeFloat(r);
		buf.writeFloat(g);
		buf.writeFloat(b);
		buf.writeFloat(maxAgeMul);
		buf.writeBoolean(depthTest);
		buf.writeBoolean(noClip);
		buf.writeFloat(gravity);
	}

	@NotNull
	@Override
	public String writeToString() {
		return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f %.2f %s",
				BuiltInRegistries.PARTICLE_TYPE.getKey(getType()), this.size, this.r, this.g, this.b, this.maxAgeMul, this.depthTest);
	}

	public static final Deserializer<WispParticleData> DESERIALIZER = new Deserializer<>() {
		@NotNull
		@Override
		public WispParticleData fromCommand(@NotNull ParticleType<WispParticleData> type, @NotNull StringReader reader) throws CommandSyntaxException {
			reader.expect(' ');
			float size = reader.readFloat();
			reader.expect(' ');
			float r = reader.readFloat();
			reader.expect(' ');
			float g = reader.readFloat();
			reader.expect(' ');
			float b = reader.readFloat();
			reader.expect(' ');
			float mam = reader.readFloat();
			boolean depth = true;
			if (reader.canRead()) {
				reader.expect(' ');
				depth = reader.readBoolean();
			}
			boolean noClip = false;
			if (reader.canRead()) {
				reader.expect(' ');
				noClip = reader.readBoolean();
			}
			float gravity = 0;
			if (reader.canRead()) {
				reader.expect(' ');
				gravity = reader.readFloat();
			}
			return new WispParticleData(size, r, g, b, mam, depth, noClip, gravity);
		}

		@Override
		public WispParticleData fromNetwork(@NotNull ParticleType<WispParticleData> type, FriendlyByteBuf buf) {
			return new WispParticleData(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readBoolean(), buf.readBoolean(), buf.readFloat());
		}
	};
}
