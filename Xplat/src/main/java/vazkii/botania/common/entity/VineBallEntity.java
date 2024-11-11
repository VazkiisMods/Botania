/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.entity;

import com.google.common.collect.ImmutableMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.item.BotaniaItems;

import java.util.Map;

public class VineBallEntity extends ThrowableProjectile implements ItemSupplier {
	private static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(VineBallEntity.class, EntityDataSerializers.FLOAT);
	private static final Map<Direction, BooleanProperty> propMap = ImmutableMap.of(Direction.NORTH, VineBlock.NORTH, Direction.SOUTH, VineBlock.SOUTH,
			Direction.WEST, VineBlock.WEST, Direction.EAST, VineBlock.EAST);

	public VineBallEntity(EntityType<VineBallEntity> type, Level world) {
		super(type, world);
	}

	public VineBallEntity(LivingEntity thrower, boolean gravity) {
		super(BotaniaEntities.VINE_BALL, thrower, thrower.level());
		entityData.set(GRAVITY, gravity ? 0.03F : 0F);
	}

	public VineBallEntity(double x, double y, double z, Level worldIn) {
		super(BotaniaEntities.VINE_BALL, x, y, z, worldIn);
		entityData.set(GRAVITY, 0.03F);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(GRAVITY, 0F);
	}

	@Override
	public void handleEntityEvent(byte id) {
		if (id == EntityEvent.DEATH) {
			for (int j = 0; j < 16; j++) {
				level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(BotaniaItems.vineBall)), getX(), getY(), getZ(), Math.random() * 0.2 - 0.1, Math.random() * 0.25, Math.random() * 0.2 - 0.1);
			}
		}
	}

	private void effectAndDieWithDrop() {
		effectAndDie();
		ItemEntity itemEntity = new ItemEntity(level(), getX(), getY(), getZ(), new ItemStack(BotaniaItems.vineBall));
		itemEntity.setDefaultPickUpDelay();
		level().addFreshEntity(itemEntity);
	}

	private void effectAndDie() {
		this.level().broadcastEntityEvent(this, EntityEvent.DEATH);
		discard();
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult hit) {
		super.onHitEntity(hit);
		if (!level().isClientSide) {
			effectAndDieWithDrop();
		}
	}

	@Override
	protected void onHitBlock(@NotNull BlockHitResult hit) {
		if (!this.level().isClientSide) {
			Direction dir = hit.getDirection();

			BlockPos pos = hit.getBlockPos();
			BlockState hitState = this.level().getBlockState(hit.getBlockPos());
			if (!hitState.is(BotaniaBlocks.solidVines)) {
				pos = pos.relative(dir);
			}

			int vinesPlaced = 0;
			if (dir.getAxis() != Direction.Axis.Y) {
				while (pos.getY() > this.level().dimensionType().minY() && vinesPlaced < 9) {
					BlockState state = this.level().getBlockState(pos);
					if (state.canBeReplaced() && !state.is(BotaniaBlocks.solidVines)) {
						BlockState stateToPlace = BotaniaBlocks.solidVines.defaultBlockState().setValue(propMap.get(dir.getOpposite()), true);

						if (!stateToPlace.canSurvive(this.level(), pos)) {
							break;
						}
						this.level().setBlockAndUpdate(pos, stateToPlace);
						this.level().levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(stateToPlace));
						vinesPlaced++;
					}
					if (this.level().getBlockState(pos).is(BotaniaBlocks.solidVines)) {
						pos = pos.below();
					} else {
						break;
					}
				}
			}

			if (vinesPlaced == 0) {
				effectAndDieWithDrop();
			} else {
				effectAndDie();
			}
		}
	}

	@Override
	protected double getDefaultGravity() {
		return entityData.get(GRAVITY);
	}

	@NotNull
	@Override
	public ItemStack getItem() {
		return new ItemStack(BotaniaItems.vineBall);
	}
}
