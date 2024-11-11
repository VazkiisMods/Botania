/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.block.Bound;
import vazkii.botania.api.block.WandBindable;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.LuminizerBlock;
import vazkii.botania.common.entity.BotaniaEntities;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.helper.VecHelper;

import java.util.ArrayList;
import java.util.List;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class LuminizerBlockEntity extends BotaniaBlockEntity implements WandBindable {
	public static final int MAX_DIST = 20;

	private static final String TAG_BIND_X = "bindX";
	private static final String TAG_BIND_Y = "bindY";
	private static final String TAG_BIND_Z = "bindZ";
	private static final String TAG_NO_PARTICLE = "noParticle";

	private BlockPos bindPos = Bound.UNBOUND_POS;
	private int ticksElapsed = 0;
	private boolean noParticle = false;

	public LuminizerBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.LIGHT_RELAY, pos, state);
	}

	public void mountEntity(Entity e) {
		BlockPos nextDest = getNextDestination();
		if (e.isPassenger() || level.isClientSide || nextDest == null || !isValidBinding()) {
			return;
		}

		PlayerMoverEntity mover = new PlayerMoverEntity(level, worldPosition, nextDest);
		level.addFreshEntity(mover);
		e.startRiding(mover);
		if (!(e instanceof ItemEntity)) {
			mover.playSound(BotaniaSounds.lightRelay, 1F, (float) Math.random() * 0.3F + 0.7F);
		}
		if (e instanceof ServerPlayer serverPlayer) {
			PlayerHelper.grantCriterion(serverPlayer, botaniaRL("main/luminizer_ride"), "code_triggered");
		}
	}

	public static void clientTick(Level level, BlockPos worldPosition, BlockState state, LuminizerBlockEntity self) {
		self.ticksElapsed++;

		BlockPos nextDest = self.getNextDestination();
		if (!self.isNoParticle() && nextDest != null && nextDest.getY() != Integer.MIN_VALUE && self.isValidBinding()) {
			Vec3 vec = self.getMovementVector();
			if (vec != null) {
				double dist = 0.1;
				int size = (int) (vec.length() / dist);
				int count = 10;
				int start = self.ticksElapsed % size;

				Vec3 vecMag = vec.normalize().scale(dist);
				Vec3 vecTip = vecMag.scale(start).add(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);

				double radPer = Math.PI / 16.0;
				float mul = 0.5F;
				float mulPer = 0.4F;
				float maxMul = 2;
				WispParticleData data = WispParticleData.wisp(0.1F, 0.4F, 0.4F, 1F, 1);
				for (int i = start; i < start + count; i++) {
					mul = Math.min(maxMul, mul + mulPer);
					double rad = radPer * (i + self.ticksElapsed * 0.4);
					Vec3 intermediate = vecMag.cross(VecHelper.ONE).scale(mul);
					Vec3 vecRot = VecHelper.rotate(intermediate, rad, vecMag).add(vecTip);
					level.addParticle(data, vecRot.x, vecRot.y, vecRot.z, (float) -vecMag.x, (float) -vecMag.y, (float) -vecMag.z);
					vecTip = vecTip.add(vecMag);
				}
			}
		}
	}

	public static void serverTick(Level level, BlockPos worldPosition, BlockState state, LuminizerBlockEntity self) {
		self.ticksElapsed++;

		BlockPos nextDest = self.getNextDestination();
		if (nextDest != null && nextDest.getY() != Integer.MIN_VALUE && self.isValidBinding()) {
			BlockPos endpoint = self.getEndpoint();

			if (endpoint != null) {
				AABB aabb = state.getShape(level, worldPosition).bounds().move(worldPosition);
				float range = 0.6F;
				List<ThrownEnderpearl> enderPearls = level.getEntitiesOfClass(ThrownEnderpearl.class, aabb.inflate(range));
				for (ThrownEnderpearl pearl : enderPearls) {
					pearl.teleportTo(
							endpoint.getX() + pearl.getX() - worldPosition.getX(),
							endpoint.getY() + pearl.getY() - worldPosition.getY(),
							endpoint.getZ() + pearl.getZ() - worldPosition.getZ()
					);
				}
			}
		}
	}

	private boolean isValidBinding() {
		BlockPos nextDest = getNextDestination();
		if (nextDest == null) {
			return false;
		}

		Block block = level.getBlockState(nextDest).getBlock();
		return block instanceof LuminizerBlock;
	}

	private BlockPos getEndpoint() {
		List<LuminizerBlockEntity> pointsPassed = new ArrayList<>();
		LuminizerBlockEntity relay = this;
		BlockPos lastCoords = null;

		// Doing while(true) gives an unreachable code error
		boolean run = true;
		while (run) {
			if (pointsPassed.contains(relay)) {
				return null; // Circular path
			}
			pointsPassed.add(relay);

			BlockPos coords = relay.getNextDestination();
			if (coords == null) {
				return lastCoords;
			}

			BlockEntity tile = level.getBlockEntity(coords);
			if (tile != null && tile instanceof LuminizerBlockEntity tileRelay) {
				relay = tileRelay;
			} else {
				return lastCoords;
			}

			lastCoords = coords;
		}

		return null;
	}

	public void setNoParticle() {
		noParticle = true;
	}

	public boolean isNoParticle() {
		return noParticle;
	}

	public Vec3 getMovementVector() {
		BlockPos dest = getNextDestination();
		if (dest == null) {
			return null;
		}

		return new Vec3(dest.getX() - worldPosition.getX(), dest.getY() - worldPosition.getY(), dest.getZ() - worldPosition.getZ());
	}

	@Override
	public BlockPos getBinding() {
		return bindPos;
	}

	public BlockPos getNextDestination() {
		BlockState state = getBlockState();
		if (state.is(BotaniaBlocks.lightRelayToggle) && state.getValue(BlockStateProperties.POWERED)) {
			return null;
		} else if (state.is(BotaniaBlocks.lightRelayFork)) {
			BlockPos torchPos = null;
			for (int i = -2; i < 3; i++) {
				BlockPos testPos = worldPosition.offset(0, i, 0);

				BlockState testState = level.getBlockState(testPos);
				if (testState.is(BotaniaBlocks.animatedTorch)) {
					torchPos = testPos;
					break;
				}
			}

			if (torchPos != null) {
				AnimatedTorchBlockEntity torch = (AnimatedTorchBlockEntity) level.getBlockEntity(torchPos);
				Direction side = AnimatedTorchBlockEntity.SIDES[torch.side].getOpposite();
				for (int i = 1; i < MAX_DIST; i++) {
					BlockPos testPos = worldPosition.relative(side, i);
					BlockState testState = level.getBlockState(testPos);
					if (testState.getBlock() instanceof LuminizerBlock) {
						return testPos;
					}
				}
			}
		}

		return getBinding();
	}

	@Override
	public boolean canSelect(Player player, ItemStack wand, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public boolean bindTo(Player player, ItemStack wand, BlockPos pos, Direction side) {
		if (!(player.level().getBlockState(pos).getBlock() instanceof LuminizerBlock)
				|| pos.distSqr(getBlockPos()) > MAX_DIST * MAX_DIST) {
			return false;
		}

		bindPos = pos;
		setChanged();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		return true;
	}

	@Override
	public void readPacketNBT(CompoundTag cmp) {
		bindPos = new BlockPos(
				cmp.getInt(TAG_BIND_X),
				cmp.getInt(TAG_BIND_Y),
				cmp.getInt(TAG_BIND_Z)
		);
		noParticle = cmp.getBoolean(TAG_NO_PARTICLE);
	}

	@Override
	public void writePacketNBT(CompoundTag cmp) {
		cmp.putInt(TAG_BIND_X, bindPos.getX());
		cmp.putInt(TAG_BIND_Y, bindPos.getY());
		cmp.putInt(TAG_BIND_Z, bindPos.getZ());
		cmp.putBoolean(TAG_NO_PARTICLE, noParticle);
	}

	public static class PlayerMoverEntity extends Entity {
		private static final String TAG_EXIT_X = "exitX";
		private static final String TAG_EXIT_Y = "exitY";
		private static final String TAG_EXIT_Z = "exitZ";
		private static final EntityDataAccessor<BlockPos> EXIT_POS = SynchedEntityData.defineId(PlayerMoverEntity.class, EntityDataSerializers.BLOCK_POS);

		public PlayerMoverEntity(EntityType<PlayerMoverEntity> type, Level world) {
			super(type, world);
			noPhysics = true;
		}

		public PlayerMoverEntity(Level world, BlockPos pos, BlockPos exitPos) {
			this(BotaniaEntities.PLAYER_MOVER, world);
			setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
			setExit(exitPos);
		}

		@Override
		protected void defineSynchedData(SynchedEntityData.Builder builder) {
			builder.define(EXIT_POS, ManaBurst.NO_SOURCE);
		}

		@Override
		public void tick() {
			super.tick();

			if (getPassengers().isEmpty() && !level().isClientSide) {
				discard();
				return;
			}

			boolean isItem = getPassengers().stream().allMatch(ItemEntity.class::isInstance);
			if (!isItem && tickCount % 30 == 0) {
				playSound(BotaniaSounds.lightRelay, 0.25F, (float) Math.random() * 0.3F + 0.7F);
			}
			if (!isItem && tickCount % 10 == 0) {
				gameEvent(GameEvent.ELYTRA_GLIDE);
			}

			BlockPos pos = blockPosition();
			BlockPos exitPos = getExitPos();

			if (!level().isClientSide && pos.equals(exitPos)) {
				boolean done = true;
				BlockEntity tile = level().getBlockEntity(pos);
				if (tile instanceof LuminizerBlockEntity relay) {
					BlockState state = level().getBlockState(pos);
					if (state.is(BotaniaBlocks.lightRelayDetector)) {
						level().setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, true));
						level().scheduleTick(pos, state.getBlock(), 2);
					}

					BlockPos bind = relay.getNextDestination();
					if (bind != null && relay.isValidBinding()) {
						setExit(bind);
						done = false;
					}
				}

				if (done) {
					for (Entity e : getPassengers()) {
						e.stopRiding();
					}
					discard();
					return;
				}
			}
			Vec3 thisVec = position();
			Vec3 motVec = thisVec.reverse().add(exitPos.getX() + 0.5, exitPos.getY() + 0.5, exitPos.getZ() + 0.5).normalize().scale(0.5);

			int color;

			int count = 4;
			for (int i = 0; i < count; i++) {
				color = Mth.hsvToRgb(tickCount / 36F + 1F / count * i, 1F, 1F);
				double rad = Math.PI * 2.0 / count * i + tickCount / Math.PI;
				double cos = Math.cos(rad);
				double sin = Math.sin(rad);
				double s = 0.4;

				int r = (color >> 16) & 0xFF;
				int g = (color >> 8) & 0xFF;
				int b = color & 0xFF;
				SparkleParticleData data = SparkleParticleData.sparkle(1.2F, r / 255F, g / 255F, b / 255F, 10);
				level().addParticle(data, getX() + cos * s, getY() - 0.5, getZ() + sin * s, 0, 0, 0);
			}

			setPos(getX() + motVec.x, getY() + motVec.y, getZ() + motVec.z);
		}

		@SoftImplement("IEntityExtension") // todo implement on fabric
		public boolean shouldRiderSit() {
			return false;
		}

		@Override
		public boolean hurt(@NotNull DamageSource source, float damage) {
			return false;
		}

		@Override
		protected void readAdditionalSaveData(@NotNull CompoundTag cmp) {
			setExit(new BlockPos(cmp.getInt(TAG_EXIT_X), cmp.getInt(TAG_EXIT_Y), cmp.getInt(TAG_EXIT_Z)));
		}

		@Override
		protected void addAdditionalSaveData(@NotNull CompoundTag cmp) {
			BlockPos exit = getExitPos();
			cmp.putInt(TAG_EXIT_X, exit.getX());
			cmp.putInt(TAG_EXIT_Y, exit.getY());
			cmp.putInt(TAG_EXIT_Z, exit.getZ());
		}

		// [VanillaCopy] Pig logic to select a dismount location
		@Override
		public Vec3 getDismountLocationForPassenger(LivingEntity living) {
			Direction direction = living.getDirection();
			int[][] aint = DismountHelper.offsetsForDirection(direction);
			BlockPos blockpos = this.blockPosition();
			BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos();

			for (Pose pose : living.getDismountPoses()) {
				AABB axisalignedbb = living.getLocalBoundsForPose(pose);

				for (int[] aint1 : aint) {
					blockpos$mutable.set(blockpos.getX() + aint1[0], blockpos.getY(), blockpos.getZ() + aint1[1]);
					double d0 = this.level().getBlockFloorHeight(blockpos$mutable);
					if (DismountHelper.isBlockFloorValid(d0)) {
						Vec3 vector3d = Vec3.upFromBottomCenterOf(blockpos$mutable, d0);
						if (DismountHelper.canDismountTo(this.level(), living, axisalignedbb.move(vector3d))) {
							living.setPose(pose);
							return vector3d;
						}
					}
				}
			}

			return super.getDismountLocationForPassenger(living);
		}

		@Override
		public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entity) {
			return new ClientboundAddEntityPacket(this, entity);
		}

		public BlockPos getExitPos() {
			return entityData.get(EXIT_POS);
		}

		public void setExit(BlockPos pos) {
			entityData.set(EXIT_POS, pos);
		}

	}

}
