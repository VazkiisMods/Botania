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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

// [VanillaCopy] ThrowableProjectile
public abstract class ThrowableCopyEntity extends Projectile {
	protected ThrowableCopyEntity(EntityType<? extends ThrowableCopyEntity> entityType, Level level) {
		super(entityType, level);
	}

	protected ThrowableCopyEntity(EntityType<? extends ThrowableCopyEntity> entityType, double d, double e, double f, Level level) {
		this(entityType, level);
		this.setPos(d, e, f);
	}

	protected ThrowableCopyEntity(EntityType<? extends ThrowableCopyEntity> entityType, LivingEntity livingEntity, Level level) {
		this(entityType, livingEntity.getX(), livingEntity.getEyeY() - 0.10000000149011612D, livingEntity.getZ(), level);
		this.setOwner(livingEntity);
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double d) {
		double e = this.getBoundingBox().getSize() * 4.0D;
		if (Double.isNaN(e)) {
			e = 4.0D;
		}

		e *= 64.0D;
		return d < e * e;
	}

	@Override
	public void tick() {
		super.tick();
		HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
		boolean bl = false;
		if (hitResult.getType() == HitResult.Type.BLOCK) {
			BlockPos blockPos = ((BlockHitResult) hitResult).getBlockPos();
			BlockState blockState = this.level().getBlockState(blockPos);
			if (blockState.is(Blocks.NETHER_PORTAL)) {
				this.handleInsidePortal(blockPos);
				bl = true;
			} else if (blockState.is(Blocks.END_GATEWAY)) {
				BlockEntity blockEntity = this.level().getBlockEntity(blockPos);
				if (blockEntity instanceof TheEndGatewayBlockEntity gateway && TheEndGatewayBlockEntity.canEntityTeleport(this)) {
					TheEndGatewayBlockEntity.teleportEntity(this.level(), blockPos, blockState, this, gateway);
				}

				bl = true;
			}
		}

		if (hitResult.getType() != HitResult.Type.MISS && !bl) {
			this.onHit(hitResult);
		}

		this.checkInsideBlocks();
		Vec3 vec3 = this.getDeltaMovement();
		double d = this.getX() + vec3.x;
		double e = this.getY() + vec3.y;
		double f = this.getZ() + vec3.z;
		this.updateRotation();
		float j;
		if (this.isInWater()) {
			for (int i = 0; i < 4; ++i) {
				float g = 0.25F;
				this.level().addParticle(ParticleTypes.BUBBLE, d - vec3.x * 0.25D, e - vec3.y * 0.25D, f - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
			}

			j = 0.8F;
		} else {
			j = 0.99F;
		}

		this.setDeltaMovement(vec3.scale((double) j));
		if (!this.isNoGravity()) {
			Vec3 vec32 = this.getDeltaMovement();
			this.setDeltaMovement(vec32.x, vec32.y - (double) this.getGravity(), vec32.z);
		}

		this.setPos(d, e, f);
	}

	protected double getDefaultGravity() {
		return 0.03;
	}
}
