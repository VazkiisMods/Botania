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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.Optional;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

// Catch-all "used an item and it succeeded" trigger for Botania items, because making a separate
// trigger for each one is dumb.
public class UseItemSuccessTrigger extends SimpleCriterionTrigger<UseItemSuccessTrigger.Instance> {
	public static final ResourceLocation ID = prefix("use_item_success");
	public static final UseItemSuccessTrigger INSTANCE = new UseItemSuccessTrigger();

	private UseItemSuccessTrigger() {}

	public void trigger(ServerPlayer player, ItemStack stack, ServerLevel world, double x, double y, double z) {
		trigger(player, instance -> instance.test(stack, world, x, y, z));
	}

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	public record Instance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, Optional<LocationPredicate> location) implements SimpleInstance {

		public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(Instance::player),
				ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "item").forGetter(Instance::item),
				ExtraCodecs.strictOptionalField(LocationPredicate.CODEC, "location").forGetter(Instance::location)
		).apply(instance, Instance::new));

		public static Criterion<Instance> used(ItemLike... items) {
			return INSTANCE.createCriterion(new Instance(Optional.empty(),
					Optional.of(ItemPredicate.Builder.item().of(items).build()), Optional.empty()));
		}

		boolean test(ItemStack stack, ServerLevel world, double x, double y, double z) {
			return (this.item.isEmpty() || this.item.get().matches(stack))
					&& (this.location.isEmpty() || this.location.get().matches(world, x, y, z));
		}
	}
}
