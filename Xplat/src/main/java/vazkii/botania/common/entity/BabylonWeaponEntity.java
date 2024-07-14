/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.entity;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.BotaniaDamageTypes;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.helper.VecHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.relic.KeyOfTheKingsLawItem;

import java.util.List;

public class BabylonWeaponEntity extends ThrowableCopyEntity {
	private static final String TAG_CHARGING = "charging";
	private static final String TAG_VARIETY = "variety";
	private static final String TAG_CHARGE_TICKS = "chargeTicks";
	private static final String TAG_LIVE_TICKS = "liveTicks";
	private static final String TAG_DELAY = "delay";
	private static final String TAG_ROTATION = "rotation";

	private static final EntityDataAccessor<Boolean> CHARGING = SynchedEntityData.defineId(BabylonWeaponEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Integer> VARIETY = SynchedEntityData.defineId(BabylonWeaponEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> CHARGE_TICKS = SynchedEntityData.defineId(BabylonWeaponEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> LIVE_TICKS = SynchedEntityData.defineId(BabylonWeaponEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> DELAY = SynchedEntityData.defineId(BabylonWeaponEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> ROTATION = SynchedEntityData.defineId(BabylonWeaponEntity.class, EntityDataSerializers.FLOAT);

	public BabylonWeaponEntity(EntityType<BabylonWeaponEntity> type, Level world) {
		super(type, world);
	}

	public BabylonWeaponEntity(LivingEntity thrower, Level world) {
		super(BotaniaEntities.BABYLON_WEAPON, thrower, world);
	}

	@Override
	protected void defineSynchedData() {
		entityData.define(CHARGING, false);
		entityData.define(VARIETY, 0);
		entityData.define(CHARGE_TICKS, 0);
		entityData.define(LIVE_TICKS, 0);
		entityData.define(DELAY, 0);
		entityData.define(ROTATION, 0F);
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double dist) {
		return dist < 64 * 64;
	}

	@Override
	public boolean ignoreExplosion(Explosion explosion) {
		return true;
	}

	@Override
	public void tick() {
		Entity thrower = getOwner();
		if (!(thrower instanceof Player player) || !thrower.isAlive()) {
			if (!level().isClientSide) {
				discard();
			}
			return;
		}
		if (!level().isClientSide) {
			ItemStack stack = PlayerHelper.getFirstHeldItem(player, BotaniaItems.kingKey);
			boolean newCharging = !stack.isEmpty() && KeyOfTheKingsLawItem.isCharging(stack);
			if (isCharging() != newCharging) {
				setCharging(newCharging);
			}
		}

		Vec3 mot = getDeltaMovement();

		int liveTime = getLiveTicks();
		int delay = getDelay();
		boolean charging = isCharging() && liveTime == 0;

		if (charging) {
			setDeltaMovement(Vec3.ZERO);

			int chargeTime = getChargeTicks();
			setChargeTicks(chargeTime + 1);

			if (level().random.nextInt(20) == 0) {
				level().playSound(null, getX(), getY(), getZ(), BotaniaSounds.babylonSpawn, SoundSource.PLAYERS, 0.1F, 1F + level().random.nextFloat() * 3F);
			}
		} else {
			if (liveTime < delay) {
				setDeltaMovement(Vec3.ZERO);
			} else if (liveTime == delay) {
				Vec3 playerLook;
				BlockHitResult rtr = ToolCommons.raytraceFromEntity(player, 64, true);
				if (rtr.getType() != HitResult.Type.BLOCK) {
					playerLook = player.getLookAngle().scale(64).add(player.position());
				} else {
					playerLook = Vec3.atCenterOf(rtr.getBlockPos());
				}

				Vec3 thisVec = VecHelper.fromEntityCenter(this);

				mot = playerLook.subtract(thisVec.x, thisVec.y, thisVec.z).normalize().scale(2);
				level().playSound(null, getX(), getY(), getZ(), BotaniaSounds.babylonAttack, SoundSource.PLAYERS, 2F, 0.1F + level().random.nextFloat() * 3F);
			}

			if (!level().isClientSide) {
				setLiveTicks(liveTime + 1);
				AABB axis = new AABB(getX(), getY(), getZ(), xOld, yOld, zOld).inflate(2);
				List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, axis);
				for (LivingEntity living : entities) {
					if (living == thrower) {
						continue;
					}

					if (living.hurtTime == 0) {
						living.hurt(level().damageSources().playerAttack(player), 20);
						onHit(new EntityHitResult(living));
						return;
					}
				}
			}
		}

		super.tick();

		// Apply after super tick so drag is not applied by super
		setDeltaMovement(mot);

		if (level().isClientSide && liveTime > delay) {
			WispParticleData data = WispParticleData.wisp(0.3F, 1F, 1F, 0F, 1);
			level().addParticle(data, getX(), getY(), getZ(), 0, -0F, 0);
		}

		if (!level().isClientSide && liveTime > 200 + delay) {
			discard();
		}
	}

	@Override
	protected void onHitBlock(@NotNull BlockHitResult hit) {
		super.onHitBlock(hit);
		explodeAndDie();
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult hit) {
		super.onHitEntity(hit);
		if (hit.getEntity() != getOwner()) {
			explodeAndDie();
		}
	}

	private void explodeAndDie() {
		if (!level().isClientSide) {
			Holder<DamageType> type = level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(BotaniaDamageTypes.KEY_EXPLOSION);
			DamageSource source = new DamageSource(type, this, this.getOwner());
			level().explode(this, source, null, getX(), getY(), getZ(), 3F, false, Level.ExplosionInteraction.NONE);
			discard();
		}
	}

	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag cmp) {
		super.addAdditionalSaveData(cmp);
		cmp.putBoolean(TAG_CHARGING, isCharging());
		cmp.putInt(TAG_VARIETY, getVariety());
		cmp.putInt(TAG_CHARGE_TICKS, getChargeTicks());
		cmp.putInt(TAG_LIVE_TICKS, getLiveTicks());
		cmp.putInt(TAG_DELAY, getDelay());
		cmp.putFloat(TAG_ROTATION, getRotation());
	}

	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag cmp) {
		super.readAdditionalSaveData(cmp);
		setCharging(cmp.getBoolean(TAG_CHARGING));
		setVariety(cmp.getInt(TAG_VARIETY));
		setChargeTicks(cmp.getInt(TAG_CHARGE_TICKS));
		setLiveTicks(cmp.getInt(TAG_LIVE_TICKS));
		setDelay(cmp.getInt(TAG_DELAY));
		setRotation(cmp.getFloat(TAG_ROTATION));
	}

	public boolean isCharging() {
		return entityData.get(CHARGING);
	}

	public void setCharging(boolean charging) {
		entityData.set(CHARGING, charging);
	}

	public int getVariety() {
		return entityData.get(VARIETY);
	}

	public void setVariety(int var) {
		entityData.set(VARIETY, var);
	}

	public int getChargeTicks() {
		return entityData.get(CHARGE_TICKS);
	}

	public void setChargeTicks(int ticks) {
		entityData.set(CHARGE_TICKS, ticks);
	}

	public int getLiveTicks() {
		return entityData.get(LIVE_TICKS);
	}

	public void setLiveTicks(int ticks) {
		entityData.set(LIVE_TICKS, ticks);
	}

	public int getDelay() {
		return entityData.get(DELAY);
	}

	public void setDelay(int delay) {
		entityData.set(DELAY, delay);
	}

	public float getRotation() {
		return entityData.get(ROTATION);
	}

	public void setRotation(float rot) {
		entityData.set(ROTATION, rot);
	}

}
