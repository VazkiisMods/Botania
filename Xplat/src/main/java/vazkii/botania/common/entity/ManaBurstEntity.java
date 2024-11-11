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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.*;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.block_entity.mana.ThrottledPacket;
import vazkii.botania.common.item.equipment.bauble.ManaseerMonocleItem;
import vazkii.botania.common.proxy.Proxy;
import vazkii.botania.xplat.BotaniaConfig;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.*;

public class ManaBurstEntity extends ThrowableProjectile implements ManaBurst {
	private static final String TAG_TICKS_EXISTED = "ticksExisted";
	private static final String TAG_COLOR = "color";
	private static final String TAG_MANA = "mana";
	private static final String TAG_STARTING_MANA = "startingMana";
	private static final String TAG_MIN_MANA_LOSS = "minManaLoss";
	private static final String TAG_TICK_MANA_LOSS = "manaLossTick";
	private static final String TAG_SPREADER_X = "spreaderX";
	private static final String TAG_SPREADER_Y = "spreaderY";
	private static final String TAG_SPREADER_Z = "spreaderZ";
	private static final String TAG_GRAVITY = "gravity";
	private static final String TAG_LENS_STACK = "lensStack";
	private static final String TAG_HAS_SHOOTER = "hasShooter";
	private static final String TAG_SHOOTER = "shooterUUID";
	private static final String TAG_LAST_COLLISION_X = "lastCollisionX";
	private static final String TAG_LAST_COLLISION_Y = "lastCollisionY";
	private static final String TAG_LAST_COLLISION_Z = "lastCollisionZ";
	private static final String TAG_WARPED = "warped";
	private static final String TAG_ORBIT_TIME = "orbitTime";
	private static final String TAG_TRIPPED = "tripped";
	private static final String TAG_MAGNETIZE_POS = "magnetizePos";
	private static final String TAG_LEFT_SOURCE = "leftSource";
	private static final String TAG_ALREADY_COLLIDED_AT = "alreadyCollidedAt";

	private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(ManaBurstEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> MANA = SynchedEntityData.defineId(ManaBurstEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> START_MANA = SynchedEntityData.defineId(ManaBurstEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Integer> MIN_MANA_LOSS = SynchedEntityData.defineId(ManaBurstEntity.class, EntityDataSerializers.INT);
	private static final EntityDataAccessor<Float> MANA_LOSS_PER_TICK = SynchedEntityData.defineId(ManaBurstEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<Float> GRAVITY = SynchedEntityData.defineId(ManaBurstEntity.class, EntityDataSerializers.FLOAT);
	private static final EntityDataAccessor<BlockPos> SOURCE_COORDS = SynchedEntityData.defineId(ManaBurstEntity.class, EntityDataSerializers.BLOCK_POS);
	private static final EntityDataAccessor<ItemStack> SOURCE_LENS = SynchedEntityData.defineId(ManaBurstEntity.class, EntityDataSerializers.ITEM_STACK);
	private static final EntityDataAccessor<Boolean> LEFT_SOURCE_POS = SynchedEntityData.defineId(ManaBurstEntity.class, EntityDataSerializers.BOOLEAN);

	private float accumulatedManaLoss = 0;
	private boolean fake = false;
	private final Set<BlockPos> alreadyCollidedAt = new HashSet<>();
	private boolean fullManaLastTick = true;
	private UUID shooterIdentity = null;
	private int _ticksExisted = 0;
	private boolean scanBeam = false;
	private BlockPos lastCollision;
	private boolean warped = false;
	private int orbitTime = 0;
	private boolean tripped = false;
	private BlockPos magnetizePos = null;

	public final List<PositionProperties> propsList = new ArrayList<>();

	public ManaBurstEntity(EntityType<ManaBurstEntity> type, Level world) {
		super(type, world);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		builder.define(COLOR, 0);
		builder.define(MANA, 0);
		builder.define(START_MANA, 0);
		builder.define(MIN_MANA_LOSS, 0);
		builder.define(MANA_LOSS_PER_TICK, 0F);
		builder.define(GRAVITY, 0F);
		builder.define(SOURCE_COORDS, ManaBurst.NO_SOURCE);
		builder.define(SOURCE_LENS, ItemStack.EMPTY);
		builder.define(LEFT_SOURCE_POS, false);
	}

	public static Vec3 calculateBurstVelocity(float xRot, float yRot) {
		float f = 0.4F;
		double mx = Mth.sin(yRot / 180.0F * (float) Math.PI) * Mth.cos(xRot / 180.0F * (float) Math.PI) * f / 2D;
		double mz = -(Mth.cos(yRot / 180.0F * (float) Math.PI) * Mth.cos(xRot / 180.0F * (float) Math.PI) * f) / 2D;
		double my = Mth.sin(xRot / 180.0F * (float) Math.PI) * f / 2D;
		return new Vec3(mx, my, mz);
	}

	public ManaBurstEntity(Level level, BlockPos pos, float rotX, float rotY, boolean fake) {
		this(BotaniaEntities.MANA_BURST, level);

		this.fake = fake;

		setBurstSourceCoords(pos);
		// spawn slightly lower than the exact center to ensure hitting pools at default horizontal spreader alignment
		moveTo(pos.getX() + 0.5, pos.getY() + (0.5 - 1.0 / 1024), pos.getZ() + 0.5, 0, 0);
		/* NB: this looks backwards but it's right. spreaders take rotX/rotY to respectively mean
		* "rotation *parallel* to the X and Y axes", while vanilla's methods take XRot/YRot
		* to respectively mean "rotation *around* the X and Y axes".
		* TODO consider renaming our versions to match vanilla
		*/
		setYRot(-(rotX + 90F));
		setXRot(rotY);
		setDeltaMovement(calculateBurstVelocity(getXRot(), getYRot()));
	}

	public ManaBurstEntity(Player player) {
		super(BotaniaEntities.MANA_BURST, player, player.level());

		setBurstSourceCoords(NO_SOURCE);
		setRot(player.getYRot() + 180, -player.getXRot());
		setDeltaMovement(calculateBurstVelocity(getXRot(), getYRot()));
	}

	@Override
	public void tick() {
		setTicksExisted(getTicksExisted() + 1);
		if ((!level().isClientSide || fake)
				&& !hasLeftSource()
				&& !blockPosition().equals(getBurstSourceBlockPos())) {
			// XXX: Should this check by bounding box instead of simply blockPosition()?
			// The burst's origin could be in another coord but part of its box still intersecting the source block
			// Not sure if that will trigger a collision then
			entityData.set(LEFT_SOURCE_POS, true);
		}

		super.tick();

		if (!fake && isAlive() && !scanBeam) {
			ping();
		}

		LensEffectItem lens = getLensInstance();
		if (lens != null) {
			lens.updateBurst(this, getSourceLens());
		}

		int mana = getMana();
		if (getTicksExisted() >= getMinManaLoss()) {
			accumulatedManaLoss += getManaLossPerTick();
			int loss = (int) accumulatedManaLoss;
			setMana(mana - loss);
			accumulatedManaLoss -= loss;

			if (getMana() <= 0) {
				discard();
			}
		}

		particles();

		fullManaLastTick = getMana() == getStartingMana();

		if (scanBeam) {
			PositionProperties props = PositionProperties.fromEntity(this);
			if (propsList.isEmpty()) {
				propsList.add(props);
			} else {
				PositionProperties lastProps = propsList.get(propsList.size() - 1);
				if (!props.coordsEqual(lastProps)) {
					propsList.add(props);
				}
			}
		}
	}

	@Override
	public boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> fluid, double mag) {
		return false;
	}

	@Override
	public boolean isInLava() {
		return false;
	}

	private ManaReceiver collidedTile = null;
	private boolean noParticles = false;

	public ManaReceiver getCollidedTile(boolean noParticles) {
		this.noParticles = noParticles;

		int iterations = 0;
		while (isAlive() && iterations < BotaniaConfig.common().spreaderTraceTime()) {
			tick();
			iterations++;
		}

		if (fake) {
			incrementFakeParticleTick();
		}

		return collidedTile;
	}

	@Override
	public boolean canChangeDimensions(Level oldLevel, Level newLevel) {
		return !fake;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		if (fake) {
			var msg = String.format("Fake bursts should never be saved at any time! Source pos %s, owner %s",
					getBurstSourceBlockPos(), getOwner());
			throw new IllegalStateException(msg);
		}
		tag.putInt(TAG_TICKS_EXISTED, getTicksExisted());
		tag.putInt(TAG_COLOR, getColor());
		tag.putInt(TAG_MANA, getMana());
		tag.putInt(TAG_STARTING_MANA, getStartingMana());
		tag.putInt(TAG_MIN_MANA_LOSS, getMinManaLoss());
		tag.putFloat(TAG_TICK_MANA_LOSS, getManaLossPerTick());
		tag.putFloat(TAG_GRAVITY, getBurstGravity());

		ItemStack stack = getSourceLens();
		CompoundTag lensCmp = new CompoundTag();
		if (!stack.isEmpty()) {
			lensCmp = stack.save(lensCmp);
		}
		tag.put(TAG_LENS_STACK, lensCmp);

		BlockPos coords = getBurstSourceBlockPos();
		tag.putInt(TAG_SPREADER_X, coords.getX());
		tag.putInt(TAG_SPREADER_Y, coords.getY());
		tag.putInt(TAG_SPREADER_Z, coords.getZ());

		if (lastCollision != null) {
			tag.putInt(TAG_LAST_COLLISION_X, lastCollision.getX());
			tag.putInt(TAG_LAST_COLLISION_Y, lastCollision.getY());
			tag.putInt(TAG_LAST_COLLISION_Z, lastCollision.getZ());
		}

		UUID identity = getShooterUUID();
		boolean hasShooter = identity != null;
		tag.putBoolean(TAG_HAS_SHOOTER, hasShooter);
		if (hasShooter) {
			tag.putUUID(TAG_SHOOTER, identity);
		}
		tag.putBoolean(TAG_WARPED, warped);
		tag.putInt(TAG_ORBIT_TIME, orbitTime);
		tag.putBoolean(TAG_TRIPPED, tripped);
		if (magnetizePos != null) {
			tag.put(TAG_MAGNETIZE_POS, BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, magnetizePos).get().orThrow());
		}
		tag.putBoolean(TAG_LEFT_SOURCE, hasLeftSource());

		var alreadyCollidedAt = new ListTag();
		for (BlockPos pos : this.alreadyCollidedAt) {
			alreadyCollidedAt.add(BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).get().orThrow());
		}
		tag.put(TAG_ALREADY_COLLIDED_AT, alreadyCollidedAt);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag cmp) {
		super.readAdditionalSaveData(cmp);
		setTicksExisted(cmp.getInt(TAG_TICKS_EXISTED));
		setColor(cmp.getInt(TAG_COLOR));
		setMana(cmp.getInt(TAG_MANA));
		setStartingMana(cmp.getInt(TAG_STARTING_MANA));
		setMinManaLoss(cmp.getInt(TAG_MIN_MANA_LOSS));
		setManaLossPerTick(cmp.getFloat(TAG_TICK_MANA_LOSS));
		setGravity(cmp.getFloat(TAG_GRAVITY));

		CompoundTag lensCmp = cmp.getCompound(TAG_LENS_STACK);
		ItemStack stack = ItemStack.of(lensCmp);
		if (!stack.isEmpty()) {
			setSourceLens(stack);
		} else {
			setSourceLens(ItemStack.EMPTY);
		}

		int x = cmp.getInt(TAG_SPREADER_X);
		int y = cmp.getInt(TAG_SPREADER_Y);
		int z = cmp.getInt(TAG_SPREADER_Z);

		setBurstSourceCoords(new BlockPos(x, y, z));

		if (cmp.contains(TAG_LAST_COLLISION_X)) {
			x = cmp.getInt(TAG_LAST_COLLISION_X);
			y = cmp.getInt(TAG_LAST_COLLISION_Y);
			z = cmp.getInt(TAG_LAST_COLLISION_Z);
			lastCollision = new BlockPos(x, y, z);
		}

		// Reread Motion because Entity.load clamps it to +/-10
		ListTag motion = cmp.getList("Motion", Tag.TAG_DOUBLE);
		setDeltaMovement(motion.getDouble(0), motion.getDouble(1), motion.getDouble(2));

		boolean hasShooter = cmp.getBoolean(TAG_HAS_SHOOTER);
		if (hasShooter) {
			UUID serializedUuid = cmp.getUUID(TAG_SHOOTER);
			UUID identity = getShooterUUID();
			if (!serializedUuid.equals(identity)) {
				setShooterUUID(serializedUuid);
			}
		}
		warped = cmp.getBoolean(TAG_WARPED);
		orbitTime = cmp.getInt(TAG_ORBIT_TIME);
		tripped = cmp.getBoolean(TAG_TRIPPED);
		if (cmp.contains(TAG_MAGNETIZE_POS)) {
			magnetizePos = BlockPos.CODEC.parse(NbtOps.INSTANCE, cmp.get(TAG_MAGNETIZE_POS)).get().orThrow();
		} else {
			magnetizePos = null;
		}
		entityData.set(LEFT_SOURCE_POS, cmp.getBoolean(TAG_LEFT_SOURCE));

		this.alreadyCollidedAt.clear();
		for (var tag : cmp.getList(TAG_ALREADY_COLLIDED_AT, Tag.TAG_INT_ARRAY)) {
			var pos = BlockPos.CODEC.parse(NbtOps.INSTANCE, tag).result();
			pos.ifPresent(this.alreadyCollidedAt::add);
		}
	}

	public void particles() {
		if (!isAlive() || !level().isClientSide) {
			return;
		}

		LensEffectItem lens = getLensInstance();
		if (lens != null && !lens.doParticles(this, getSourceLens())) {
			return;
		}

		int color = getColor();
		float r = (color >> 16 & 0xFF) / 255F;
		float g = (color >> 8 & 0xFF) / 255F;
		float b = (color & 0xFF) / 255F;
		float osize = getParticleSize();
		float size = osize;

		if (fake) {
			if (getMana() == getStartingMana()) {
				size = 2F;
			} else if (fullManaLastTick) {
				size = 4F;
			}

			if (!noParticles && shouldDoFakeParticles()) {
				SparkleParticleData data = SparkleParticleData.fake(0.4F * size, r, g, b, 1);
				level().addParticle(data, true, getX(), getY(), getZ(), 0, 0, 0);
			}
		} else {
			Player player = Proxy.INSTANCE.getClientPlayer();
			boolean depth = player == null || !ManaseerMonocleItem.hasMonocle(player);

			if (BotaniaConfig.client().subtlePowerSystem()) {
				WispParticleData data = WispParticleData.wisp(0.1F * size, r, g, b, depth);
				Proxy.INSTANCE.addParticleForceNear(level(), data, getX(), getY(), getZ(), (float) (Math.random() - 0.5F) * 0.02F, (float) (Math.random() - 0.5F) * 0.02F, (float) (Math.random() - 0.5F) * 0.01F);
			} else {
				float or = r;
				float og = g;
				float ob = b;

				double luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b; // Standard relative luminance calculation

				double iterX = getX();
				double iterY = getY();
				double iterZ = getZ();

				Vec3 currentPos = position();
				Vec3 oldPos = new Vec3(xo, yo, zo);
				Vec3 diffVec = oldPos.subtract(currentPos);
				Vec3 diffVecNorm = diffVec.normalize();

				double distance = 0.095;

				do {
					if (luminance < 0.1) {
						r = or + (float) Math.random() * 0.125F;
						g = og + (float) Math.random() * 0.125F;
						b = ob + (float) Math.random() * 0.125F;
					}
					size = osize + ((float) Math.random() - 0.5F) * 0.065F + (float) Math.sin(new Random(uuid.getMostSignificantBits()).nextInt(9001)) * 0.4F;
					WispParticleData data = WispParticleData.wisp(0.2F * size, r, g, b, depth);
					Proxy.INSTANCE.addParticleForceNear(level(), data, iterX, iterY, iterZ,
							(float) -getDeltaMovement().x() * 0.01F,
							(float) -getDeltaMovement().y() * 0.01F,
							(float) -getDeltaMovement().z() * 0.01F);

					iterX += diffVecNorm.x * distance;
					iterY += diffVecNorm.y * distance;
					iterZ += diffVecNorm.z * distance;

					currentPos = new Vec3(iterX, iterY, iterZ);
					diffVec = oldPos.subtract(currentPos);
					if (getOrbitTime() > 0) {
						break;
					}
				} while (Math.abs(diffVec.length()) > distance);

				WispParticleData data = WispParticleData.wisp(0.1F * size, or, og, ob, depth);
				level().addParticle(data, iterX, iterY, iterZ, (float) (Math.random() - 0.5F) * 0.06F, (float) (Math.random() - 0.5F) * 0.06F, (float) (Math.random() - 0.5F) * 0.06F);
			}
		}
	}

	@Override
	public void handleEntityEvent(byte event) {
		if (event == EntityEvent.DEATH) {
			int color = getColor();
			float r = (color >> 16 & 0xFF) / 255F;
			float g = (color >> 8 & 0xFF) / 255F;
			float b = (color & 0xFF) / 255F;

			int mana = getMana();
			int maxMana = getStartingMana();
			float size = (float) mana / (float) maxMana;

			if (!BotaniaConfig.client().subtlePowerSystem()) {
				for (int i = 0; i < 4; i++) {
					WispParticleData data = WispParticleData.wisp(0.15F * size, r, g, b);
					level().addParticle(data, getX(), getY(), getZ(), (float) (Math.random() - 0.5F) * 0.04F, (float) (Math.random() - 0.5F) * 0.04F, (float) (Math.random() - 0.5F) * 0.04F);
				}
			}
			SparkleParticleData data = SparkleParticleData.sparkle((float) 4, r, g, b, 2);
			level().addParticle(data, getX(), getY(), getZ(), 0, 0, 0);
		} else {
			super.handleEntityEvent(event);
		}
	}

	public float getParticleSize() {
		return (float) getMana() / (float) getStartingMana();
	}

	@Override
	protected void onHit(HitResult hit) {
		if (isFake()) {
			// [VanillaCopy] super, without firing gameEvents
			HitResult.Type type = hit.getType();
			if (type == HitResult.Type.ENTITY) {
				this.onHitEntity((EntityHitResult) hit);
			} else if (type == HitResult.Type.BLOCK) {
				this.onHitBlock((BlockHitResult) hit);
			}
		} else {
			super.onHit(hit);
		}
	}

	@Override
	protected void onHitBlock(@NotNull BlockHitResult hit) {
		if (!isFake()) {
			super.onHitBlock(hit);
		}
		BlockPos collidePos = hit.getBlockPos();
		if (collidePos.equals(lastCollision)) {
			return;
		}
		lastCollision = collidePos.immutable();
		BlockEntity tile = level().getBlockEntity(collidePos);
		BlockState state = level().getBlockState(collidePos);
		Block block = state.getBlock();

		var ghost = XplatAbstractions.INSTANCE.findManaGhost(level(), collidePos, state, tile);
		var ghostBehaviour = ghost != null ? ghost.getGhostBehaviour() : ManaCollisionGhost.Behaviour.RUN_ALL;

		if (ghostBehaviour == ManaCollisionGhost.Behaviour.SKIP_ALL
				|| block instanceof BushBlock
				|| block instanceof LeavesBlock) {
			return;
		}

		BlockPos sourcePos = getBurstSourceBlockPos();
		if (!hasLeftSource() && collidePos.equals(sourcePos)) {
			return;
		}

		var receiver = XplatAbstractions.INSTANCE.findManaReceiver(level(), collidePos, state, tile, hit.getDirection());
		collidedTile = receiver;

		if (!fake && !noParticles && !level().isClientSide) {
			if (receiver != null && receiver.canReceiveManaFromBursts() && onReceiverImpact(receiver)) {
				if (tile instanceof ThrottledPacket throttledPacket) {
					throttledPacket.markDispatchable();
				} else if (tile != null) {
					VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile);
				}
			}
		}

		var trigger = XplatAbstractions.INSTANCE.findManaTrigger(level(), collidePos, state, tile);
		if (trigger != null) {
			trigger.onBurstCollision(this);
		}

		if (ghostBehaviour == ManaCollisionGhost.Behaviour.RUN_RECEIVER_TRIGGER) {
			return;
		}

		onHitCommon(hit, true);

		setCollidedAt(collidePos);
	}

	@Override
	protected void onHitEntity(@NotNull EntityHitResult hit) {
		super.onHitEntity(hit);
		onHitCommon(hit, false);
	}

	private void onHitCommon(HitResult hit, boolean shouldKill) {
		LensEffectItem lens = getLensInstance();
		if (lens != null) {
			shouldKill = lens.collideBurst(this, hit, collidedTile != null
					&& collidedTile.canReceiveManaFromBursts(), shouldKill, getSourceLens());
		}

		if (shouldKill && isAlive()) {
			if (fake) {
				discard();
			} else if (!this.level().isClientSide) {
				this.level().broadcastEntityEvent(this, EntityEvent.DEATH);
				discard();
			}
		}
	}

	private boolean onReceiverImpact(ManaReceiver receiver) {
		if (hasWarped()) {
			return false;
		}

		LensEffectItem lens = getLensInstance();
		int mana = getMana();

		if (lens != null) {
			ItemStack stack = getSourceLens();
			mana = lens.getManaToTransfer(this, stack, receiver);
		}

		if (receiver instanceof ManaCollector collector) {
			mana *= collector.getManaYieldMultiplier(this);
		}

		if (mana > 0) {
			receiver.receiveMana(mana);
			return true;
		}

		return false;
	}

	@Override
	public void remove(RemovalReason reason) {
		super.remove(reason);

		if (!fake) {
			var spreader = getShooter();
			if (spreader != null && spreader.getIdentifier().equals(getShooterUUID())) {
				spreader.setCanShoot(true);
			}
		} else {
			setDeathTicksForFakeParticle();
		}
	}

	@Nullable
	private ManaSpreader getShooter() {
		var receiver = XplatAbstractions.INSTANCE.findManaReceiver(level(), getBurstSourceBlockPos(), null);
		return receiver instanceof ManaSpreader spreader ? spreader : null;
	}

	@Override
	public double getDefaultGravity() {
		return getBurstGravity();
	}

	@Override
	public boolean isFake() {
		return fake;
	}

	@Override
	public void setFake(boolean fake) {
		this.fake = fake;
	}

	public void setScanBeam() {
		scanBeam = true;
	}

	@Override
	public int getColor() {
		return entityData.get(COLOR);
	}

	@Override
	public void setColor(int color) {
		entityData.set(COLOR, color);
	}

	@Override
	public int getMana() {
		return entityData.get(MANA);
	}

	@Override
	public void setMana(int mana) {
		entityData.set(MANA, mana);
	}

	@Override
	public int getStartingMana() {
		return entityData.get(START_MANA);
	}

	@Override
	public void setStartingMana(int mana) {
		entityData.set(START_MANA, mana);
	}

	@Override
	public int getMinManaLoss() {
		return entityData.get(MIN_MANA_LOSS);
	}

	@Override
	public void setMinManaLoss(int minManaLoss) {
		entityData.set(MIN_MANA_LOSS, minManaLoss);
	}

	@Override
	public float getManaLossPerTick() {
		return entityData.get(MANA_LOSS_PER_TICK);
	}

	@Override
	public void setManaLossPerTick(float mana) {
		entityData.set(MANA_LOSS_PER_TICK, mana);
	}

	@Override
	public float getBurstGravity() {
		return entityData.get(GRAVITY);
	}

	@Override
	public void setGravity(float gravity) {
		entityData.set(GRAVITY, gravity);
	}

	@Override
	public BlockPos getBurstSourceBlockPos() {
		return entityData.get(SOURCE_COORDS);
	}

	@Override
	public void setBurstSourceCoords(BlockPos pos) {
		entityData.set(SOURCE_COORDS, pos);
	}

	@Override
	public ItemStack getSourceLens() {
		return entityData.get(SOURCE_LENS);
	}

	@Override
	public void setSourceLens(ItemStack lens) {
		entityData.set(SOURCE_LENS, lens);
	}

	@Override
	public int getTicksExisted() {
		return _ticksExisted;
	}

	@Override
	public boolean hasLeftSource() {
		return entityData.get(LEFT_SOURCE_POS);
	}

	public void setTicksExisted(int ticks) {
		_ticksExisted = ticks;
	}

	private LensEffectItem getLensInstance() {
		ItemStack lens = getSourceLens();
		if (!lens.isEmpty() && lens.getItem() instanceof LensEffectItem effect) {
			return effect;
		}

		return null;
	}

	@Override
	public boolean hasAlreadyCollidedAt(BlockPos pos) {
		return alreadyCollidedAt.contains(pos);
	}

	@Override
	public void setCollidedAt(BlockPos pos) {
		if (!hasAlreadyCollidedAt(pos)) {
			alreadyCollidedAt.add(pos.immutable());
		}
	}

	@Override
	public void setShooterUUID(UUID uuid) {
		shooterIdentity = uuid;
	}

	@Override
	public UUID getShooterUUID() {
		return shooterIdentity;
	}

	@Override
	public void ping() {
		var spreader = getShooter();
		if (spreader != null) {
			spreader.pingback(this, getShooterUUID());
		}
	}

	@Override
	public boolean hasWarped() {
		return warped;
	}

	@Override
	public void setWarped(boolean warped) {
		this.warped = warped;
	}

	@Override
	public int getOrbitTime() {
		return orbitTime;
	}

	@Override
	public void setOrbitTime(int time) {
		this.orbitTime = time;
	}

	@Override
	public boolean hasTripped() {
		return tripped;
	}

	@Override
	public void setTripped(boolean tripped) {
		this.tripped = tripped;
	}

	@Nullable
	@Override
	public BlockPos getMagnetizedPos() {
		return magnetizePos;
	}

	@Override
	public void setMagnetizePos(@Nullable BlockPos pos) {
		this.magnetizePos = pos;
	}

	protected boolean shouldDoFakeParticles() {
		if (BotaniaConfig.client().staticWandBeam()) {
			return true;
		}

		var spreader = getShooter();
		return spreader != null
				&& (getMana() != getStartingMana() && fullManaLastTick
						|| Math.abs(spreader.getBurstParticleTick() - getTicksExisted()) < 4);
	}

	private void incrementFakeParticleTick() {
		var spreader = getShooter();
		if (spreader != null) {
			spreader.setBurstParticleTick(spreader.getBurstParticleTick() + 2);
			if (spreader.getLastBurstDeathTick() != -1 && spreader.getBurstParticleTick() > spreader.getLastBurstDeathTick()) {
				spreader.setBurstParticleTick(0);
			}
		}
	}

	private void setDeathTicksForFakeParticle() {
		var spreader = getShooter();
		if (spreader != null) {
			spreader.setLastBurstDeathTick(getTicksExisted());
		}
	}

	public record PositionProperties(BlockPos coords, BlockState state) {
		public static PositionProperties fromEntity(Entity entity) {
			return new PositionProperties(entity.blockPosition(), entity.getFeetBlockState());
		}

		public boolean coordsEqual(PositionProperties props) {
			return coords.equals(props.coords);
		}

		public boolean isInvalidIn(Level level) {
			return !level.hasChunkAt(coords);
		}

		public boolean contentsEqual(Level world) {
			if (isInvalidIn(world)) {
				return false;
			}

			return world.getBlockState(coords) == state;
		}
	}

}
