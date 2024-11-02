/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.botania.common.block.flower.functional.LooniumBlockEntity;
import vazkii.botania.common.brew.effect.SoulCrossMobEffect;
import vazkii.botania.common.item.AssemblyHaloItem;
import vazkii.botania.common.item.equipment.bauble.CharmOfTheDivaItem;
import vazkii.botania.common.item.equipment.bauble.SojournersSashItem;
import vazkii.botania.common.item.equipment.tool.elementium.ElementiumAxeItem;
import vazkii.botania.common.item.rod.ShadedMesaRodItem;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFabricMixin extends Entity {

	public LivingEntityFabricMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Shadow
	public abstract ItemStack getItemInHand(InteractionHand hand);

	@Shadow
	protected int lastHurtByPlayerTime;

	@Inject(method = "dropAllDeathLoot", at = @At(shift = At.Shift.AFTER, value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;dropFromLootTable(Lnet/minecraft/world/damagesource/DamageSource;Z)V"))
	private void dropEnd(DamageSource source, CallbackInfo ci) {
		var self = (LivingEntity) (Object) this;
		ElementiumAxeItem.onEntityDrops(lastHurtByPlayerTime > 0, source, self, self::spawnAtLocation);
	}

	@Inject(at = @At("HEAD"), cancellable = true, method = "dropFromLootTable")
	private void dropLoonium(DamageSource source, boolean causedByPlayer, CallbackInfo ci) {
		var self = (LivingEntity) (Object) this;
		LooniumBlockEntity.dropLooniumItems(self, stack -> {
			if (!stack.isEmpty()) {
				self.spawnAtLocation(stack);
			}
			ci.cancel();
		});
	}

	/**
	 * Applies soul cross effect when being killed
	 */
	@Inject(at = @At("RETURN"), method = "createWitherRose")
	private void healKiller(@Nullable LivingEntity adversary, CallbackInfo ci) {
		if (!level().isClientSide && adversary != null) {
			SoulCrossMobEffect.onEntityKill((LivingEntity) (Object) this, adversary);
		}

	}

	@Inject(at = @At("HEAD"), method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", cancellable = true)
	private void onSwing(InteractionHand hand, boolean bl, CallbackInfo ci) {
		ItemStack stack = getItemInHand(hand);
		LivingEntity self = (LivingEntity) (Object) this;
		if (!level().isClientSide) {
			if (stack.getItem() instanceof AssemblyHaloItem halo && halo.onEntitySwing(stack, self)) {
				ci.cancel();
			} else if (stack.getItem() instanceof ShadedMesaRodItem rod) {
				rod.onEntitySwing(stack, self);
			}
		}
	}

	@Inject(at = @At("RETURN"), method = "jumpFromGround")
	private void onJump(CallbackInfo ci) {
		SojournersSashItem.onPlayerJump((LivingEntity) (Object) this);
	}

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterArmorAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F", ordinal = 0), method = "actuallyHurt")
	private void onActuallyHurt(DamageSource damageSource, float damageAmount, CallbackInfo ci) {
		if (damageSource.getDirectEntity() instanceof Player player) {
			CharmOfTheDivaItem.onEntityDamaged(player, (LivingEntity) (Object) this);
		}
	}
}
