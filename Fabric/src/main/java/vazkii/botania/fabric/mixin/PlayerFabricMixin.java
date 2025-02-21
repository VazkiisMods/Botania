/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.mixin;

import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import vazkii.botania.common.PlayerAccess;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.handler.PixieHandler;
import vazkii.botania.common.item.equipment.armor.terrasteel.TerrasteelHelmItem;
import vazkii.botania.common.item.equipment.bauble.*;
import vazkii.botania.common.item.relic.RingOfOdinItem;

@Mixin(Player.class)
public abstract class PlayerFabricMixin extends LivingEntity {

	protected PlayerFabricMixin(EntityType<? extends LivingEntity> entityType, Level world) {
		super(entityType, world);
	}

	@Shadow
	@Final
	private Inventory inventory;

	/**
	 * Registers the pixie spawn chance attribute on players
	 */
	@Inject(at = @At("RETURN"), method = "createAttributes")
	private static void addPixieAttribute(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
		cir.getReturnValue().add(PixieHandler.PIXIE_SPAWN_CHANCE);
	}

	/**
	 * Performs many reactions when being hit
	 */
	@ModifyArgs(method = "hurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
	private void onHurt(Args args) {
		Player self = (Player) (Object) this;
		DamageSource src = args.get(0);
		float amount = args.get(1);
		Container worn = EquipmentHandler.getAllWorn(self);
		for (int i = 0; i < worn.getContainerSize(); i++) {
			ItemStack stack = worn.getItem(i);
			if (stack.getItem() instanceof CloakOfVirtueItem cloak) {
				amount = cloak.onPlayerDamage(self, src, amount);
			}
		}

		// Should really make a separate inject for this, but putting it here works too
		PixieHandler.onDamageTaken(self, src);

		args.set(1, amount);
	}

	/**
	 * Tells the magnet ring about item drops
	 */
	@Inject(at = @At("HEAD"), method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;")
	public void onDrop(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
		Level world = this.level();
		if (!stack.isEmpty() && !world.isClientSide) {
			RingOfMagnetizationItem.onTossItem((Player) (Object) this);
		}
	}

	@Inject(at = @At("RETURN"), method = "tick")
	private void tickBeltTiara(CallbackInfo ci) {
		FlugelTiaraItem.updatePlayerFlyStatus((Player) (Object) this);
		SojournersSashItem.tickBelt((Player) (Object) this);
	}

	@ModifyArg(index = 0, method = "causeFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z"))
	private float cushionFall(float originalDist) {
		return SojournersSashItem.onPlayerFall((Player) (Object) this, originalDist);
	}

	// Multiply the damage on crit. Targets the first float LOAD after the sprint check for the crit.
	// Stores the entity for further handling in the common Player mixin.
	@ModifyVariable(
		at = @At(value = "LOAD", ordinal = 0),
		slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z", ordinal = 1)),
		method = "attack", ordinal = 0
	)
	private float onCritMul(float f, Entity target) {
		if (target instanceof LivingEntity living) {
			((PlayerAccess) this).botania$setCritTarget(living);
			return f * TerrasteelHelmItem.getCritDamageMult((Player) (Object) this);
		}
		return f;
	}
}
