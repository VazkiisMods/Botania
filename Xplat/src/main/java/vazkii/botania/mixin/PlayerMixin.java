package vazkii.botania.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import vazkii.botania.api.item.AncientWillContainer;
import vazkii.botania.common.BotaniaStats;
import vazkii.botania.common.PlayerAccess;
import vazkii.botania.common.entity.BotaniaEntities;
import vazkii.botania.common.item.ResoluteIvyItem;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerAccess {
	protected PlayerMixin(EntityType<? extends LivingEntity> type, Level level) {
		super(type, level);
	}

	@Unique
	private LivingEntity terraWillCritTarget;

	@Shadow
	public abstract void awardStat(ResourceLocation stat, int i);

	/**
	 * Updates the distance by luminizer stat
	 */
	@Inject(
		at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Player;getVehicle()Lnet/minecraft/world/entity/Entity;"),
		method = "checkRidingStatistics", locals = LocalCapture.CAPTURE_FAILSOFT
	)
	private void trackLuminizerTravel(double dx, double dy, double dz, CallbackInfo ci, int cm, Entity mount) {
		if (mount.getType() == BotaniaEntities.PLAYER_MOVER) {
			awardStat(BotaniaStats.LUMINIZER_ONE_CM, cm);
		}
	}

	@Override
	public void botania$setCritTarget(LivingEntity entity) {
		this.terraWillCritTarget = entity;
	}

	// Perform damage source modifications and apply the potion effects.
	@ModifyArg(
		method = "attack",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z")
	)
	private DamageSource onDamageTarget(DamageSource source, float amount) {
		if (this.terraWillCritTarget != null) {
			DamageSource newSource = AncientWillContainer.onEntityAttacked(source, amount, (Player) (Object) this, terraWillCritTarget);
			this.terraWillCritTarget = null;
			return newSource;
		}
		return source;
	}

	// Clear the entity on any return after the capture.
	@Inject(
		at = @At(value = "RETURN"), method = "attack",
		slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getKnockbackBonus(Lnet/minecraft/world/entity/LivingEntity;)I"))
	)
	private void clearCritTarget(CallbackInfo ci) {
		this.terraWillCritTarget = null;
	}

	@Inject(
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;dropAll()V"),
		method = "dropEquipment"
	)
	private void captureIvyDrops(CallbackInfo ci) {
		ResoluteIvyItem.keepDropsOnDeath((Player) (Object) this);
	}
}
