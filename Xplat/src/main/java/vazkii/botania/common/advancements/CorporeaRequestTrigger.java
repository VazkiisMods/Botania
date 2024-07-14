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
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class CorporeaRequestTrigger extends SimpleCriterionTrigger<CorporeaRequestTrigger.Instance> {
	public static final ResourceLocation ID = prefix("corporea_index_request");
	public static final CorporeaRequestTrigger INSTANCE = new CorporeaRequestTrigger();

	private CorporeaRequestTrigger() {}

	public void trigger(ServerPlayer player, ServerLevel world, BlockPos pos, int count) {
		this.trigger(player, instance -> instance.test(world, pos, count));
	}

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	public record Instance(Optional<ContextAwarePredicate> player, MinMaxBounds.Ints extracted, Optional<LocationPredicate> location)
			implements
				SimpleInstance {

		public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(Instance::player),
				MinMaxBounds.Ints.CODEC.optionalFieldOf("extracted", MinMaxBounds.Ints.ANY).forGetter(Instance::extracted),
				ExtraCodecs.strictOptionalField(LocationPredicate.CODEC, "location").forGetter(Instance::location)
		).apply(instance, Instance::new));

		public static Criterion<Instance> numExtracted(MinMaxBounds.Ints extracted) {
			return INSTANCE.createCriterion(new Instance(Optional.empty(), extracted, Optional.empty()));
		}

		boolean test(ServerLevel world, BlockPos pos, int count) {
			return this.extracted.matches(count) && (this.location.isEmpty()
					|| this.location.get().matches(world, pos.getX(), pos.getY(), pos.getZ()));
		}
	}

}
