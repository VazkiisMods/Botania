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

import java.util.Optional;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class LokiPlaceTrigger extends SimpleCriterionTrigger<LokiPlaceTrigger.Instance> {
	public static final ResourceLocation ID = prefix("loki_placed_blocks");
	public static final LokiPlaceTrigger INSTANCE = new LokiPlaceTrigger();

	private LokiPlaceTrigger() {}

	public void trigger(ServerPlayer player, ItemStack ring, int blocksPlaced) {
		trigger(player, instance -> instance.test(ring, blocksPlaced));
	}

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	public record Instance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> ring, MinMaxBounds.Ints blocksPlaced) implements SimpleInstance {

		public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(Instance::player),
				ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "ring").forGetter(Instance::ring),
				MinMaxBounds.Ints.CODEC.optionalFieldOf("blocks_placed", MinMaxBounds.Ints.ANY).forGetter(Instance::blocksPlaced)
		).apply(instance, Instance::new));

		public static Criterion<Instance> blocksPlaced(MinMaxBounds.Ints blocksPlaced) {
			return INSTANCE.createCriterion(new Instance(Optional.empty(), Optional.empty(), blocksPlaced));
		}

		boolean test(ItemStack ring, int blocksPlaced) {
			return (this.ring.isEmpty() || this.ring.get().matches(ring)) && this.blocksPlaced.matches(blocksPlaced);
		}
	}
}
