package vazkii.botania.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HurtByTargetGoal.class)
public class HurtByTargetGoalMixin {
	/**
	 * Prevent mobs from punching themselves. This can happen when either the Heisei Dream or Diva Charm brainwash
	 * another mob and that mob alerts other mobs to attack the same target. If the target is among the alerted mobs,
	 * it would attack itself.
	 * TODO[MixinExtras] Preventing the call in HurtByTargetGoal::alertOthers would be a more compatible option.
	 */
	@Inject(method = "alertOther", at = @At("HEAD"), cancellable = true)
	private void checkSelfTargeting(Mob mobToAlert, LivingEntity targetToAttack, CallbackInfo ci) {
		if (mobToAlert == targetToAttack) {
			ci.cancel();
		}
	}
}
