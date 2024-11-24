package vazkii.botania.mixin;

import net.minecraft.world.entity.Mob;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import vazkii.botania.xplat.XplatAbstractions;

@Mixin(Mob.class)
public class MobMixin {
	/**
	 * Prevent instant despawning when outside the nearest player's despawn sphere by pretending the mob is closer to
	 * that player than it actually is.
	 * 
	 * @param distToNearestPlayerSquared Squared distance to the nearest player.
	 * @return If mob should not despawn instantly (e.g. because it was spawned by a Loonium or by a monster spawner
	 *         with an active Life Imbuer) but is farther away than its despawn distance, return a distance just under
	 *         the (squared) despawn distance, otherwise return the original value.
	 */
	@ModifyVariable(
		method = "checkDespawn",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/MobCategory;getDespawnDistance()I", ordinal = 0),
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;distanceToSqr(Lnet/minecraft/world/entity/Entity;)D"),
			to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;removeWhenFarAway(D)Z")
		)
	)
	private double reduceDistToNearestPlayer(double distToNearestPlayerSquared) {
		Mob thisMob = (Mob) (Object) this;
		var looniumComponent = XplatAbstractions.INSTANCE.looniumComponent(thisMob);
		if (looniumComponent != null && looniumComponent.isSlowDespawn()) {
			double justUnderDespawnDistance = thisMob.getType().getCategory().getDespawnDistance() - 1;
			return Math.min(justUnderDespawnDistance * justUnderDespawnDistance, distToNearestPlayerSquared);
		}
		return distToNearestPlayerSquared;
	}
}
