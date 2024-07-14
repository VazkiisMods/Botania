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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class RelicBindTrigger extends SimpleCriterionTrigger<RelicBindTrigger.Instance> {
	public static final ResourceLocation ID = prefix("relic_bind");
	public static final RelicBindTrigger INSTANCE = new RelicBindTrigger();

	private RelicBindTrigger() {}

	public void trigger(ServerPlayer player, ItemStack relic) {
		trigger(player, instance -> instance.test(relic));
	}

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	public record Instance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> relic) implements SimpleInstance {
		public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(Instance::player),
				ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "relic").forGetter(Instance::relic)
		).apply(instance, Instance::new));

		public static Criterion<Instance> bound(ItemLike relicItem) {
			return INSTANCE.createCriterion(new Instance(Optional.empty(),
					Optional.of(ItemPredicate.Builder.item().of(relicItem).build())));
		}

		boolean test(ItemStack stack) {
			return this.relic.isEmpty() || this.relic.get().matches(stack);
		}
	}
}
