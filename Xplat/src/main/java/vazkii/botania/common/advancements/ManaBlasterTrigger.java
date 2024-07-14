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

public class ManaBlasterTrigger extends SimpleCriterionTrigger<ManaBlasterTrigger.Instance> {
	private static final ResourceLocation ID = prefix("fire_mana_blaster");
	public static final ManaBlasterTrigger INSTANCE = new ManaBlasterTrigger();

	private ManaBlasterTrigger() {}

	public void trigger(ServerPlayer player, ItemStack stack) {
		trigger(player, instance -> instance.test(stack, player));
	}

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	public record Instance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, Optional<EntityPredicate> user)
			implements
				SimpleInstance {

		public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(Instance::player),
				ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "item").forGetter(Instance::item),
				ExtraCodecs.strictOptionalField(EntityPredicate.CODEC, "user").forGetter(Instance::user)
		).apply(instance, Instance::new));

		public static Criterion<Instance> shoot() {
			return INSTANCE.createCriterion(new Instance(Optional.empty(), Optional.empty(), Optional.empty()));
		}

		boolean test(ItemStack stack, ServerPlayer entity) {
			return (this.item.isEmpty() || this.item.get().matches(stack))
					&& (this.user.isEmpty() || this.user.get().matches(entity, entity));
		}
	}
}
