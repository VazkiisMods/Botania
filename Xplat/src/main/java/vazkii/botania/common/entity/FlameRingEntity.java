/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.helper.MathHelper;

import java.util.List;

public class FlameRingEntity extends Entity {
	public FlameRingEntity(EntityType<FlameRingEntity> type, Level world) {
		super(type, world);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {}

	@Override
	public void baseTick() {
		super.baseTick();

		float radius = 5F;
		float renderRadius = (float) (radius - Math.random());

		for (int i = 0; i < Math.min(90, tickCount); i++) {
			float a = i;
			if (a % 2 == 0) {
				a = 45 + a;
			}

			if (level().random.nextInt(tickCount < 90 ? 8 : 20) == 0) {
				float rad = (float) (a * 4 * Math.PI / 180F);
				double x = Math.cos(rad) * renderRadius;
				double z = Math.sin(rad) * renderRadius;

				WispParticleData data1 = WispParticleData.wisp(0.65F + (float) Math.random() * 0.45F, 1F, (float) Math.random() * 0.25F, (float) Math.random() * 0.25F);
				level().addParticle(data1, getX() + x, getY() - 0.2, getZ() + z, (float) (Math.random() - 0.5F) * 0.15F, 0.055F + (float) Math.random() * 0.025F, (float) (Math.random() - 0.5F) * 0.15F);

				float gs = (float) Math.random() * 0.15F;
				float smokeRadius = (float) (renderRadius - Math.random() * renderRadius * 0.9);
				x = Math.cos(rad) * smokeRadius;
				z = Math.sin(rad) * smokeRadius;
				WispParticleData data = WispParticleData.wisp(0.65F + (float) Math.random() * 0.45F, gs, gs, gs, 1);
				level().addParticle(data, getX() + x, getY() - 0.2, getZ() + z, 0, -(-0.155F - (float) Math.random() * 0.025F), 0);
			}
		}

		if (level().random.nextInt(20) == 0) {
			level().playLocalSound(getX(), getY(), getZ(), SoundEvents.FIRE_AMBIENT, SoundSource.BLOCKS, 1F, 1F, false);
		}

		if (level().isClientSide) {
			return;
		}

		if (tickCount >= 300) {
			discard();
			return;
		}

		if (tickCount > 45) {
			AABB boundingBox = new AABB(getX(), getY(), getZ(), getX(), getY(), getZ()).inflate(radius, radius, radius);
			List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, boundingBox);

			if (entities.isEmpty()) {
				return;
			}

			for (LivingEntity entity : entities) {
				if (entity == null || MathHelper.pointDistancePlane(getX(), getZ(), entity.getX(), entity.getZ()) > radius) {
					continue;
				}

				entity.setRemainingFireTicks(4 * 20);
			}
		}
	}

	@Override
	public boolean hurt(@NotNull DamageSource source, float amount) {
		return false;
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag var1) {}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag var1) {}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
		return new ClientboundAddEntityPacket(this, entity);
	}
}
