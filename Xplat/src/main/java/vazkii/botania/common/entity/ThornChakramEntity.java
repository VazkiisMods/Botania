/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.entity;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.helper.VecHelper;
import vazkii.botania.common.item.BotaniaItems;

public class ThornChakramEntity extends ThrowableProjectile implements ItemSupplier {
	private static final EntityDataAccessor<Integer> BOUNCES = SynchedEntityData.defineId(ThornChakramEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Boolean> FLARE = SynchedEntityData.defineId(ThornChakramEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> RETURN_TO = SynchedEntityData.defineId(ThornChakramEntity.class, EntityDataSerializers.INT);
	private static final int MAX_BOUNCES = 16;
	private boolean bounced = false;
	private ItemStack stack = ItemStack.EMPTY;

	public ThornChakramEntity(EntityType<ThornChakramEntity> type, Level world) {
		super(type, world);
	}

	public ThornChakramEntity(LivingEntity e, Level world, ItemStack stack) {
		super(BotaniaEntities.THORN_CHAKRAM, e, world);
		this.stack = stack.copy();
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(BOUNCES, 0);
		entityData.define(FLARE, false);
		entityData.define(RETURN_TO, -1);
	}

	@Override
	public boolean ignoreExplosion(Explosion explosion) {
		return true;
	}

	@Override
	public void tick() {
		// Standard motion
		Vec3 old = getDeltaMovement();

		super.tick();

		if (!bounced) {
			// Reset the drag applied by super
			setDeltaMovement(old);
		}

		bounced = false;

		// Returning motion
		if (isReturning()) {
			Entity thrower = getOwner();
			if (thrower != null) {
				Vec3 motion = VecHelper.fromEntityCenter(thrower).subtract(VecHelper.fromEntityCenter(this)).normalize();
				setDeltaMovement(motion);
			}
		}

		// Client FX
		if (level().isClientSide && isFire()) {
			double r = 0.1;
			double m = 0.1;
			for (int i = 0; i < 3; i++) {
				level().addParticle(ParticleTypes.FLAME, getX() + r * (Math.random() - 0.5), getY() + r * (Math.random() - 0.5), getZ() + r * (Math.random() - 0.5), m * (Math.random() - 0.5), m * (Math.random() - 0.5), m * (Math.random() - 0.5));
			}
		}

		// Server state control
		if (!level().isClientSide && (getTimesBounced() >= MAX_BOUNCES || tickCount > 60)) {
			Entity thrower = getOwner();
			if (thrower == null) {
				dropAndKill();
			} else {
				setEntityToReturnTo(thrower.getId());
				if (distanceToSqr(thrower) < 2) {
					dropAndKill();
				}
			}
		}
	}

	private void dropAndKill() {
		ItemStack stack = getItemStack();
		ItemEntity item = new ItemEntity(level(), getX(), getY(), getZ(), stack);
		level().addFreshEntity(item);
		discard();
	}

	private ItemStack getItemStack() {
		return !stack.isEmpty()
				? stack.copy()
				: isFire() ? new ItemStack(BotaniaItems.flareChakram) : new ItemStack(BotaniaItems.thornChakram);
	}

	@Override
	protected void onHit(@NotNull HitResult pos) {
		if (!isReturning()) {
			super.onHit(pos);
		}
	}

	@Override
	protected void onHitBlock(@NotNull BlockHitResult hit) {
		super.onHitBlock(hit);
		BlockState state = level().getBlockState(hit.getBlockPos());
		if (state.getBlock() instanceof BushBlock || state.is(BlockTags.LEAVES)) {
			return;
		}

		int bounces = getTimesBounced();
		if (bounces < MAX_BOUNCES) {
			Vec3 currentMovementVec = getDeltaMovement();
			Direction dir = hit.getDirection();
			Vec3 normalVector = new Vec3(dir.getStepX(), dir.getStepY(), dir.getStepZ()).normalize();
			Vec3 movementVec = normalVector.scale(-2 * currentMovementVec.dot(normalVector)).add(currentMovementVec);

			setDeltaMovement(movementVec);
			bounced = true;

			if (!level().isClientSide) {
				setTimesBounced(getTimesBounced() + 1);
			}
		}
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult hit) {
		super.onHitEntity(hit);
		if (!level().isClientSide && hit.getEntity() instanceof LivingEntity hitEntity && hit.getEntity() != getOwner()) {
			Entity thrower = getOwner();
			DamageSource src = damageSources().generic();
			if (thrower instanceof Player) {
				src = damageSources().thrown(this, thrower);
			} else if (thrower instanceof LivingEntity livingEntity) {
				src = damageSources().mobAttack(livingEntity);
			}
			hitEntity.hurt(src, 12);
			if (isFire()) {
				hitEntity.setSecondsOnFire(5);
			} else if (level().random.nextInt(3) == 0) {
				hitEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
			}
		}
	}

	@Override
	protected float getGravity() {
		return 0F;
	}

	private int getTimesBounced() {
		return entityData.get(BOUNCES);
	}

	private void setTimesBounced(int times) {
		entityData.set(BOUNCES, times);
	}

	public boolean isFire() {
		return entityData.get(FLARE);
	}

	public void setFire(boolean fire) {
		entityData.set(FLARE, fire);
	}

	private boolean isReturning() {
		return getEntityToReturnTo() > -1;
	}

	private int getEntityToReturnTo() {
		return entityData.get(RETURN_TO);
	}

	private void setEntityToReturnTo(int entityID) {
		entityData.set(RETURN_TO, entityID);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag compound) {
		super.addAdditionalSaveData(compound);
		if (!stack.isEmpty()) {
			compound.put("fly_stack", stack.save(new CompoundTag()));
		}
		compound.putBoolean("flare", isFire());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("fly_stack")) {
			stack = ItemStack.of(compound.getCompound("fly_stack"));
		}
		setFire(compound.getBoolean("flare"));
	}

	@NotNull
	@Override
	public ItemStack getItem() {
		return getItemStack();
	}
}
