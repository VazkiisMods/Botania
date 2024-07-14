/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;

import vazkii.botania.common.entity.GaiaGuardianEntity;

import java.util.Optional;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class GaiaGuardianNoArmorTrigger extends SimpleCriterionTrigger<GaiaGuardianNoArmorTrigger.Instance> {
	public static final ResourceLocation ID = prefix("gaia_guardian_no_armor");
	public static final GaiaGuardianNoArmorTrigger INSTANCE = new GaiaGuardianNoArmorTrigger();

	private GaiaGuardianNoArmorTrigger() {}

	public void trigger(ServerPlayer player, GaiaGuardianEntity guardian, DamageSource src) {
		trigger(player, instance -> instance.test(player, guardian, src));
	}

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	public record Instance(Optional<ContextAwarePredicate> player, Optional<EntityPredicate> guardian, Optional<DamageSourcePredicate> killingBlow)
			implements
				SimpleInstance {

		public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(Instance::player),
				ExtraCodecs.strictOptionalField(EntityPredicate.CODEC, "guardian").forGetter(Instance::guardian),
				ExtraCodecs.strictOptionalField(DamageSourcePredicate.CODEC, "killingBlow").forGetter(Instance::killingBlow)
		).apply(instance, Instance::new));

		public static Criterion<Instance> unarmoredKill() {
			return INSTANCE.createCriterion(new Instance(Optional.empty(), Optional.empty(), Optional.empty()));
		}

		boolean test(ServerPlayer player, GaiaGuardianEntity guardian, DamageSource src) {
			return (this.guardian.isEmpty() || this.guardian.get().matches(player, guardian))
					&& (this.killingBlow.isEmpty() || this.killingBlow.get().matches(player, src));
		}
	}
}
