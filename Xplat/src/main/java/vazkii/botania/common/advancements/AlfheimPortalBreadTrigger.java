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

public class AlfheimPortalBreadTrigger extends SimpleCriterionTrigger<AlfheimPortalBreadTrigger.Instance> {
	public static final ResourceLocation ID = prefix("alf_portal_bread");
	public static final AlfheimPortalBreadTrigger INSTANCE = new AlfheimPortalBreadTrigger();

	private AlfheimPortalBreadTrigger() {}

	public void trigger(ServerPlayer player, BlockPos portal) {
		this.trigger(player, instance -> instance.test(player.serverLevel(), portal));
	}

	@Override
	public Codec<Instance> codec() {
		return Instance.CODEC;
	}

	public record Instance(Optional<ContextAwarePredicate> player, Optional<LocationPredicate> portalLocation)
			implements
				SimpleInstance {
		public static final Codec<Instance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(Instance::player),
				ExtraCodecs.strictOptionalField(LocationPredicate.CODEC, "portal_location").forGetter(Instance::portalLocation)
		).apply(instance, Instance::new));

		public static Criterion<Instance> sentBread() {
			return INSTANCE.createCriterion(new Instance(Optional.empty(), Optional.empty()));
		}

		boolean test(ServerLevel world, BlockPos portal) {
			return this.portalLocation.isEmpty()
					|| this.portalLocation.get().matches(world, portal.getX(), portal.getY(), portal.getZ());
		}
	}
}
