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
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class AlfheimPortalTrigger extends SimpleCriterionTrigger<AlfheimPortalTrigger.Instance> {
	public static final ResourceLocation ID = prefix("open_elf_portal");
	public static final AlfheimPortalTrigger INSTANCE = new AlfheimPortalTrigger();

	private AlfheimPortalTrigger() {}

	public void trigger(ServerPlayer player, ServerLevel world, BlockPos pos, ItemStack wand) {
		trigger(player, instance -> instance.test(world, pos, wand));
	}

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	public record Instance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> wand, Optional<LocationPredicate> location)
			implements
				SimpleInstance {

		public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(Instance::player),
				ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "wand").forGetter(Instance::wand),
				ExtraCodecs.strictOptionalField(LocationPredicate.CODEC, "location").forGetter(Instance::location)
		).apply(instance, Instance::new));

		public static Criterion<Instance> activatedPortal() {
			return INSTANCE.createCriterion(new Instance(Optional.empty(), Optional.empty(), Optional.empty()));
		}

		boolean test(ServerLevel world, BlockPos pos, ItemStack wand) {
			return (this.wand.isEmpty() || this.wand.get().matches(wand))
					&& (this.location.isEmpty() || this.location.get().matches(world, pos.getX(), pos.getY(), pos.getZ()));
		}
	}
}
