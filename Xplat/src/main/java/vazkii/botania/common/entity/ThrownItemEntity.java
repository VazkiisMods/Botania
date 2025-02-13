/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.mixin.ItemEntityAccessor;

import java.util.function.Predicate;

public class ThrownItemEntity extends ItemEntity {
	public ThrownItemEntity(EntityType<ThrownItemEntity> type, Level world) {
		super(type, world);
	}

	public ThrownItemEntity(Level world, double x,
			double y, double z, ItemEntity item) {
		super(world, x, y, z, item.getItem());
		setPickUpDelay(((ItemEntityAccessor) item).getPickupDelay());
		setDeltaMovement(item.getDeltaMovement());
		setInvulnerable(true);
	}

	@NotNull
	@Override
	public EntityType<?> getType() {
		return BotaniaEntities.THROWN_ITEM;
	}

	@Override
	public void tick() {
		super.tick();

		// [VanillaCopy] derivative from ThrowableProjectile
		int pickupDelay = ((ItemEntityAccessor) this).getPickupDelay();
		Predicate<Entity> filter = e -> !e.isSpectator() && e.isAlive() && e.isPickable() && (!(e instanceof Player) || pickupDelay == 0);
		HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, filter);
		boolean teleported = false;
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
			BlockState blockState = this.level().getBlockState(blockPos);
			if (blockState.is(Blocks.NETHER_PORTAL)) {
				//todo this.handleInsidePortal(blockPos);
				teleported = true;
			} else if (blockState.is(Blocks.END_GATEWAY)) {
				BlockEntity blockEntity = this.level().getBlockEntity(blockPos);
				/*todo
				if (blockEntity instanceof TheEndGatewayBlockEntity gateway && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
					TheEndGatewayBlockEntity.teleportEntity(this.level(), blockPos, blockState, this, gateway);
				}

				 */

				teleported = true;
			}
		}

		if (teleported) {
			return;
		}

		// Bonk any entities hit
		if (!level().isClientSide && hitResult.getType() == HitResult.Type.ENTITY) {
			Entity bonk = ((EntityHitResult) hitResult).getEntity();
			bonk.hurt(damageSources().magic(), 2.0F);
			Entity item = new ItemEntity(level(), getX(), getY(), getZ(), getItem());
			level().addFreshEntity(item);
			item.setDeltaMovement(getDeltaMovement().scale(0.25));
			discard();
			return;
		}

		if (!level().isClientSide && getDeltaMovement().length() < 1.0F) {
			Entity item = new ItemEntity(level(), getX(), getY(), getZ(), getItem());
			level().addFreshEntity(item);
			item.setDeltaMovement(getDeltaMovement());
			discard();
		}
	}
}
