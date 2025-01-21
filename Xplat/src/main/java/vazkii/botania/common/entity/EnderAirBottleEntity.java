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
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.common.loot.BotaniaLootTables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnderAirBottleEntity extends ThrowableProjectile implements ItemSupplier {
	public static final int PARTICLE_COLOR = 0x000008;

	public EnderAirBottleEntity(EntityType<EnderAirBottleEntity> type, Level world) {
		super(type, world);
	}

	public EnderAirBottleEntity(LivingEntity entity, Level world) {
		super(BotaniaEntities.ENDER_AIR_BOTTLE, entity, world);
	}

	public EnderAirBottleEntity(double x, double y, double z, Level world) {
		super(BotaniaEntities.ENDER_AIR_BOTTLE, x, y, z, world);
	}

	private void convertBlock(@NotNull BlockPos pos) {
		List<BlockPos> coordsList = getCoordsToPut(pos);
		this.level().levelEvent(LevelEvent.PARTICLES_SPELL_POTION_SPLASH, blockPosition(), PARTICLE_COLOR);

		for (BlockPos coords : coordsList) {
			this.level().setBlockAndUpdate(coords, Blocks.END_STONE.defaultBlockState());
			if (Math.random() < 0.1) {
				this.level().levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, coords, Block.getId(Blocks.END_STONE.defaultBlockState()));
			}
		}
	}

	@Override
	protected void onHitBlock(@NotNull BlockHitResult result) {
		super.onHitBlock(result);
		if (level().isClientSide) {
			return;
		}
		convertBlock(result.getBlockPos());
		discard();
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult result) {
		super.onHitEntity(result);
		if (this.level().isClientSide) {
			return;
		}
		Entity entity = result.getEntity();
		if (entity.getType() == EntityType.GHAST && this.level().dimension() == Level.OVERWORLD) {
			this.level().levelEvent(LevelEvent.PARTICLES_SPELL_POTION_SPLASH, blockPosition(), PARTICLE_COLOR);
			DamageSource source = entity.damageSources().thrown(this, getOwner());
			entity.hurt(source, 0);

			// Ghasts render as if they are looking straight ahead, but the look y component
			// can actually be nonzero, correct for that
			Vec3 lookVec = entity.getLookAngle();
			Vec3 vec = new Vec3(lookVec.x(), 0, lookVec.z()).normalize();

			// Position chosen to appear roughly in the ghast's face
			((ServerLevel) this.level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.GHAST_TEAR)),
					entity.getX() + (2.3 * vec.x), entity.getY() + vec.y + 2.6, entity.getZ() + (2.3 * vec.z),
					40,
					Math.abs(vec.z) + 0.15, 0.2, Math.abs(vec.x) + 0.15, 0.2);

			LootTable table = this.level().getServer().reloadableRegistries().getLootTable(BotaniaLootTables.GHAST_LOOT_TABLE);
			LootParams.Builder builder = new LootParams.Builder(((ServerLevel) level()));
			builder.withParameter(LootContextParams.THIS_ENTITY, entity);
			builder.withParameter(LootContextParams.ORIGIN, entity.position());
			builder.withParameter(LootContextParams.DAMAGE_SOURCE, source);

			LootParams context = builder.create(LootContextParamSets.ENTITY);
			for (ItemStack stack : table.getRandomItems(context)) {
				ItemEntity item = entity.spawnAtLocation(stack, 2);
				item.setDeltaMovement(item.getDeltaMovement().add(vec.scale(0.4)));
			}
		} else {
			convertBlock(BlockPos.containing(result.getLocation()));
		}
		discard();
	}

	private List<BlockPos> getCoordsToPut(BlockPos pos) {
		List<BlockPos> possibleCoords = new ArrayList<>();
		int range = 4;
		int rangeY = 4;

		for (BlockPos bPos : BlockPos.betweenClosed(pos.offset(-range, -rangeY, -range),
				pos.offset(range, rangeY, range))) {
			BlockState state = level().getBlockState(bPos);
			if (state.is(BotaniaTags.Blocks.ENDER_AIR_CONVERTABLE)) {
				possibleCoords.add(bPos.immutable());
			}
		}

		Collections.shuffle(possibleCoords);

		if (possibleCoords.size() > 64) {
			return possibleCoords.subList(0, 64);
		} else {
			return possibleCoords;
		}
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {}

	@NotNull
	@Override
	public ItemStack getItem() {
		return new ItemStack(BotaniaItems.enderAirBottle);
	}
}
