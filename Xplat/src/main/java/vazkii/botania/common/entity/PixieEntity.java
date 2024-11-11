/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.entity;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.client.fx.SparkleParticleData;

public class PixieEntity extends FlyingMob {
	private static final EntityDataAccessor<Integer> PIXIE_TYPE = SynchedEntityData.defineId(PixieEntity.class, EntityDataSerializers.INT);

	private LivingEntity summoner = null;
	private float damage = 0;
	private MobEffectInstance effect = null;

	public PixieEntity(EntityType<PixieEntity> type, Level world) {
		super(type, world);
	}

	public PixieEntity(Level world) {
		this(BotaniaEntities.PIXIE, world);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(PIXIE_TYPE, 0);
	}

	public void setPixieType(int type) {
		entityData.set(PIXIE_TYPE, type);
	}

	public int getPixieType() {
		return entityData.get(PIXIE_TYPE);
	}

	public void setProps(LivingEntity target, LivingEntity summoner, int type, float damage) {
		setTarget(target);
		this.summoner = summoner;
		this.damage = damage;
		setPixieType(type);
	}

	public void setApplyPotionEffect(MobEffectInstance effect) {
		this.effect = effect;
	}

	@Override
	protected void customServerAiStep() {
		LivingEntity target = getTarget();
		if (target != null) {
			double d0 = target.getX() + target.getBbWidth() / 2 - getX();
			double d1 = target.getY() + target.getBbHeight() / 2 - getY();
			double d2 = target.getZ() + target.getBbWidth() / 2 - getZ();
			double d3 = d0 * d0 + d1 * d1 + d2 * d2;

			float mod = 0.45F;
			if (getPixieType() == 1) {
				mod = 0.1F;
			}

			setDeltaMovement(d0 / d3 * mod, d1 / d3 * mod, d2 / d3 * mod);

			if (Math.sqrt(d3) < 1F) {
				if (summoner != null) {
					if (summoner instanceof Player player) {
						target.hurt(damageSources().playerAttack(player), damage);
					} else {
						target.hurt(damageSources().mobAttack(summoner), damage);
					}
				} else {
					target.hurt(damageSources().mobAttack(this), damage);
				}
				if (effect != null && !(target instanceof Player)) {
					target.addEffect(effect);
				}
				discard();
			}
		}

		yBodyRot = -((float) Math.atan2(getDeltaMovement().x(), getDeltaMovement().z()))
				* 180.0F / (float) Math.PI;
		setYRot(yBodyRot);
	}

	@Override
	public boolean isInvulnerableTo(@NotNull DamageSource source) {
		if (getPixieType() == 0 && source.getEntity() == summoner) {
			return true;
		}

		if (getPixieType() == 1 && !(source.getEntity() instanceof Player)) {
			return true;
		}

		return super.isInvulnerableTo(source);
	}

	@Override
	public void baseTick() {
		super.baseTick();

		if (!level().isClientSide
				&& (getTarget() == null || tickCount > 200)) {
			discard();
		}

		boolean dark = getPixieType() == 1;
		if (level().isClientSide) {
			for (int i = 0; i < 4; i++) {
				float r = dark ? 0.1F : 1F;
				float g = dark ? 0.025F : 0.25F;
				float b = dark ? 0.09F : 0.9F;
				SparkleParticleData data = SparkleParticleData.sparkle(0.1F + (float) Math.random() * 0.25F, r, g, b, 12);
				level().addParticle(data, getX() + (Math.random() - 0.5) * 0.25, getY() + 0.5 + (Math.random() - 0.5) * 0.25, getZ() + (Math.random() - 0.5) * 0.25, 0, 0, 0);
			}
		}
	}

	@Override
	public void remove(RemovalReason reason) {
		if (level() != null && level().isClientSide && getPixieType() == 0) {
			for (int i = 0; i < 12; i++) {
				SparkleParticleData data = SparkleParticleData.sparkle(1F + (float) Math.random() * 0.25F, 1F, 0.25F, 0.9F, 5);
				level().addParticle(data, getX() + (Math.random() - 0.5) * 0.25, getY() + 0.5 + (Math.random() - 0.5) * 0.25, getZ() + (Math.random() - 0.5) * 0.25, 0, 0, 0);
			}
		}
		super.remove(reason);
	}

	@Override
	public boolean isPickable() {
		return isAlive();
	}

	@Override
	public boolean canBeLeashed() {
		return false;
	}
}
