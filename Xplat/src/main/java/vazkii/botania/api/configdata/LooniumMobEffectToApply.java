package vazkii.botania.api.configdata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class LooniumMobEffectToApply {
	public static final Codec<LooniumMobEffectToApply> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					BuiltInRegistries.MOB_EFFECT.byNameCodec().fieldOf("effect").forGetter(me -> me.effect),
					ExtraCodecs.POSITIVE_INT.optionalFieldOf("duration", MobEffectInstance.INFINITE_DURATION)
							.forGetter(me -> me.duration),
					Codec.intRange(0, 255).optionalFieldOf("amplifier", 0)
							.forGetter(me -> me.amplifier)
			).apply(instance, LooniumMobEffectToApply::new)
	);

	private final MobEffect effect;
	private final int duration;
	private final int amplifier;

	private LooniumMobEffectToApply(MobEffect effect, int duration, int amplifier) {
		this.effect = effect;
		this.duration = duration;
		this.amplifier = amplifier;
	}

	public static Builder effect(MobEffect effect) {
		return new Builder(effect);
	}

	@NotNull
	public MobEffectInstance createMobEffectInstance() {
		return new MobEffectInstance(effect, duration, amplifier);
	}

	@Override
	public String toString() {
		return "MobEffectToApply{" +
				"effect=" + effect +
				", duration=" + duration +
				", amplifier=" + amplifier +
				'}';
	}

	public MobEffect getEffect() {
		return effect;
	}

	public int getDuration() {
		return duration;
	}

	public int getAmplifier() {
		return amplifier;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		return obj instanceof LooniumMobEffectToApply that && Objects.equals(this.effect, that.effect)
				&& this.duration == that.duration && this.amplifier == that.amplifier;
	}

	@Override
	public int hashCode() {
		return Objects.hash(effect, duration, amplifier);
	}

	public static class Builder {
		private final MobEffect effect;
		private int duration = MobEffectInstance.INFINITE_DURATION;
		private int amplifier = 0;

		private Builder(MobEffect effect) {
			this.effect = effect;
		}

		public Builder duration(int duration) {
			this.duration = duration;
			return this;
		}

		public Builder amplifier(int amplifier) {
			this.amplifier = amplifier;
			return this;
		}

		public LooniumMobEffectToApply build() {
			return new LooniumMobEffectToApply(effect, duration, amplifier);
		}
	}
}
