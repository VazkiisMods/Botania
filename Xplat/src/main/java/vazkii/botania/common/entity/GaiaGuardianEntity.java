/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.entity;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;

import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.*;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.advancements.GaiaGuardianNoArmorTrigger;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.MathHelper;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.helper.VecHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.common.proxy.Proxy;
import vazkii.botania.network.EffectType;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.network.clientbound.SpawnGaiaGuardianPacket;
import vazkii.botania.xplat.XplatAbstractions;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.*;
import java.util.function.Supplier;

import static vazkii.botania.common.helper.PlayerHelper.isTruePlayer;
import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class GaiaGuardianEntity extends Mob {
	public static final float ARENA_RANGE = 12F;
	public static final int ARENA_HEIGHT = 5;

	private static final int SPAWN_TICKS = 160;
	public static final float MAX_HP = 320F;
	public static final Supplier<IMultiblock> ARENA_MULTIBLOCK = Suppliers.memoize(() -> {
		var beaconBase = PatchouliAPI.get().predicateMatcher(Blocks.IRON_BLOCK,
				state -> state.is(BlockTags.BEACON_BASE_BLOCKS));
		return PatchouliAPI.get().makeMultiblock(
				new String[][] {
						{
								"P_______P",
								"_________",
								"_________",
								"_________",
								"_________",
								"_________",
								"_________",
								"_________",
								"P_______P",
						},
						{
								"_________",
								"_________",
								"_________",
								"_________",
								"____B____",
								"_________",
								"_________",
								"_________",
								"_________",
						},
						{
								"_________",
								"_________",
								"_________",
								"___III___",
								"___I0I___",
								"___III___",
								"_________",
								"_________",
								"_________",
						}
				},
				'P', BotaniaBlocks.gaiaPylon,
				'B', Blocks.BEACON,
				'I', beaconBase,
				'0', beaconBase
		);
	});

	private static final int MOB_SPAWN_START_TICKS = 20;
	private static final int MOB_SPAWN_END_TICKS = 80;
	private static final int MOB_SPAWN_BASE_TICKS = 800;
	private static final int MOB_SPAWN_TICKS = MOB_SPAWN_BASE_TICKS + MOB_SPAWN_START_TICKS + MOB_SPAWN_END_TICKS;
	private static final int MOB_SPAWN_WAVES = 10;
	private static final int MOB_SPAWN_WAVE_TIME = MOB_SPAWN_BASE_TICKS / MOB_SPAWN_WAVES;
	private static final int DAMAGE_CAP = 32;

	private static final String TAG_INVUL_TIME = "invulTime";
	private static final String TAG_AGGRO = "aggro";
	private static final String TAG_SOURCE_X = "sourceX";
	private static final String TAG_SOURCE_Y = "sourceY";
	private static final String TAG_SOURCE_Z = "sourcesZ";
	private static final String TAG_MOB_SPAWN_TICKS = "mobSpawnTicks";
	private static final String TAG_HARD_MODE = "hardMode";
	private static final String TAG_PLAYER_COUNT = "playerCount";
	private static final TagKey<Block> BLACKLIST = BotaniaTags.Blocks.GAIA_BREAK_BLACKLIST;

	private static final EntityDataAccessor<Integer> INVUL_TIME = SynchedEntityData.defineId(GaiaGuardianEntity.class, EntityDataSerializers.INT);

	private static final List<BlockPos> PYLON_LOCATIONS = ImmutableList.of(
			new BlockPos(4, 1, 4),
			new BlockPos(4, 1, -4),
			new BlockPos(-4, 1, 4),
			new BlockPos(-4, 1, -4)
	);

	private static final List<ResourceLocation> CHEATY_BLOCKS = Arrays.asList(
			new ResourceLocation("openblocks", "beartrap"),
			new ResourceLocation("thaumictinkerer", "magnet")
	);

	private boolean spawnLandmines = false;
	private boolean spawnPixies = false;
	private boolean anyWithArmor = false;
	private boolean aggro = false;
	private int tpDelay = 0;
	private int mobSpawnTicks = 0;
	private int playerCount = 0;
	private boolean hardMode = false;
	private BlockPos source = ManaBurst.NO_SOURCE;
	private final List<UUID> playersWhoAttacked = new ArrayList<>();
	private final ServerBossEvent bossInfo = (ServerBossEvent) new ServerBossEvent(BotaniaEntities.DOPPLEGANGER.getDescription(), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS).setCreateWorldFog(true);
	private UUID bossInfoUUID = bossInfo.getId();
	public Player trueKiller = null;

	public GaiaGuardianEntity(EntityType<GaiaGuardianEntity> type, Level world) {
		super(type, world);
		xpReward = 825;
		if (world.isClientSide) {
			Proxy.INSTANCE.addBoss(this);
		}
	}

	public static boolean spawn(Player player, ItemStack stack, Level world, BlockPos pos, boolean hard) {
		//initial checks
		if (!(world.getBlockEntity(pos) instanceof BeaconBlockEntity) ||
				!isTruePlayer(player) ||
				countGaiaGuardiansAround(world, pos) > 0) {
			return false;
		}

		//check difficulty
		if (world.getDifficulty() == Difficulty.PEACEFUL) {
			if (!world.isClientSide) {
				player.sendSystemMessage(Component.translatable("botaniamisc.peacefulNoob").withStyle(ChatFormatting.RED));
			}
			return false;
		}

		//check pylons
		List<BlockPos> invalidPylonBlocks = checkPylons(world, pos);
		if (!invalidPylonBlocks.isEmpty()) {
			if (world.isClientSide) {
				warnInvalidBlocks(world, invalidPylonBlocks);
			} else {
				player.sendSystemMessage(Component.translatable("botaniamisc.needsCatalysts").withStyle(ChatFormatting.RED));
			}

			return false;
		}

		//check arena shape
		List<BlockPos> invalidArenaBlocks = checkArena(world, pos);
		if (!invalidArenaBlocks.isEmpty()) {
			if (world.isClientSide) {
				warnInvalidBlocks(world, invalidArenaBlocks);
			} else {
				XplatAbstractions.INSTANCE.sendToPlayer(player, new BotaniaEffectPacket(EffectType.ARENA_INDICATOR, pos.getX(), pos.getY(), pos.getZ()));

				player.sendSystemMessage(Component.translatable("botaniamisc.badArena").withStyle(ChatFormatting.RED));
			}

			return false;
		}

		//all checks ok, spawn the boss
		if (!world.isClientSide) {
			stack.shrink(1);

			GaiaGuardianEntity e = BotaniaEntities.DOPPLEGANGER.create(world);
			e.setPos(pos.getX() + 0.5, pos.getY() + 3, pos.getZ() + 0.5);
			e.setInvulTime(SPAWN_TICKS);
			e.setHealth(1F);
			e.source = pos;
			e.mobSpawnTicks = MOB_SPAWN_TICKS;
			e.hardMode = hard;

			List<Player> playersAround = e.getPlayersAround();
			int playerCount = playersAround.size();
			e.playerCount = playerCount;

			float healthMultiplier = 1;
			if (playerCount > 1) {
				healthMultiplier += playerCount * 0.25F;
			}
			e.getAttribute(Attributes.MAX_HEALTH).setBaseValue(MAX_HP * healthMultiplier);

			if (hard) {
				e.getAttribute(Attributes.ARMOR).setBaseValue(15);
			}

			e.playSound(BotaniaSounds.gaiaSummon, 1F, 1F);
			e.finalizeSpawn((ServerLevelAccessor) world, world.getCurrentDifficultyAt(e.blockPosition()), MobSpawnType.EVENT, null);
			world.addFreshEntity(e);

			for (Player nearbyPlayer : playersAround) {
				if (nearbyPlayer instanceof ServerPlayer serverPlayer) {
					CriteriaTriggers.SUMMONED_ENTITY.trigger(serverPlayer, e);
				}
			}
		}

		return true;
	}

	private static List<BlockPos> checkPylons(Level world, BlockPos beaconPos) {
		List<BlockPos> invalidPylonBlocks = new ArrayList<>();

		for (BlockPos coords : PYLON_LOCATIONS) {
			BlockPos pos_ = beaconPos.offset(coords);

			BlockState state = world.getBlockState(pos_);
			if (!state.is(BotaniaBlocks.gaiaPylon)) {
				invalidPylonBlocks.add(pos_);
			}
		}

		return invalidPylonBlocks;
	}

	private static List<BlockPos> checkArena(Level world, BlockPos beaconPos) {
		List<BlockPos> trippedPositions = new ArrayList<>();
		int range = (int) Math.ceil(ARENA_RANGE);
		BlockPos pos;

		for (int x = -range; x <= range; x++) {
			for (int z = -range; z <= range; z++) {
				if (Math.abs(x) == 4 && Math.abs(z) == 4 || MathHelper.pointDistancePlane(x, z, 0, 0) > ARENA_RANGE) {
					continue; // Ignore pylons and out of circle
				}

				boolean hasFloor = false;

				for (int y = -2; y <= ARENA_HEIGHT; y++) {
					if (x == 0 && y == 0 && z == 0) {
						continue; //the beacon
					}

					pos = beaconPos.offset(x, y, z);

					BlockState state = world.getBlockState(pos);

					boolean allowBlockHere = y < 0;
					boolean isBlockHere = !state.getCollisionShape(world, pos).isEmpty();

					if (allowBlockHere && isBlockHere) //floor is here! good
					{
						hasFloor = true;
					}

					if (y == 0 && !hasFloor) //column is entirely missing floor
					{
						trippedPositions.add(pos.below());
					}

					if (!allowBlockHere && isBlockHere && !state.is(BLACKLIST)) //ceiling is obstructed in this column
					{
						trippedPositions.add(pos);
					}
				}
			}
		}

		return trippedPositions;
	}

	private static void warnInvalidBlocks(Level world, Iterable<BlockPos> invalidPositions) {
		WispParticleData data = WispParticleData.wisp(0.5F, 1, 0.2F, 0.2F, 8, false);
		for (BlockPos pos_ : invalidPositions) {
			world.addParticle(data, pos_.getX() + 0.5, pos_.getY() + 0.5, pos_.getZ() + 0.5, 0, 0, 0);
		}
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, ARENA_RANGE * 1.5F));
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(INVUL_TIME, 0);
	}

	public int getInvulTime() {
		return entityData.get(INVUL_TIME);
	}

	public BlockPos getSource() {
		return source;
	}

	public void setInvulTime(int time) {
		entityData.set(INVUL_TIME, time);
	}

	@Override
	public void addAdditionalSaveData(CompoundTag cmp) {
		super.addAdditionalSaveData(cmp);
		cmp.putInt(TAG_INVUL_TIME, getInvulTime());
		cmp.putBoolean(TAG_AGGRO, aggro);
		cmp.putInt(TAG_MOB_SPAWN_TICKS, mobSpawnTicks);

		cmp.putInt(TAG_SOURCE_X, source.getX());
		cmp.putInt(TAG_SOURCE_Y, source.getY());
		cmp.putInt(TAG_SOURCE_Z, source.getZ());

		cmp.putBoolean(TAG_HARD_MODE, hardMode);
		cmp.putInt(TAG_PLAYER_COUNT, playerCount);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag cmp) {
		super.readAdditionalSaveData(cmp);
		setInvulTime(cmp.getInt(TAG_INVUL_TIME));
		aggro = cmp.getBoolean(TAG_AGGRO);
		mobSpawnTicks = cmp.getInt(TAG_MOB_SPAWN_TICKS);

		int x = cmp.getInt(TAG_SOURCE_X);
		int y = cmp.getInt(TAG_SOURCE_Y);
		int z = cmp.getInt(TAG_SOURCE_Z);
		source = new BlockPos(x, y, z);

		hardMode = cmp.getBoolean(TAG_HARD_MODE);
		if (cmp.contains(TAG_PLAYER_COUNT)) {
			playerCount = cmp.getInt(TAG_PLAYER_COUNT);
		} else {
			playerCount = 1;
		}

		if (this.hasCustomName()) {
			this.bossInfo.setName(this.getDisplayName());
		}
	}

	@Override
	public void setCustomName(@Nullable Component name) {
		super.setCustomName(name);
		this.bossInfo.setName(this.getDisplayName());
	}

	@Override
	public void heal(float amount) {
		if (getInvulTime() == 0) {
			super.heal(amount);
		}
	}

	@Override
	public void kill() {
		this.setHealth(0.0F);
	}

	@Override
	public boolean hurt(@NotNull DamageSource source, float amount) {
		Entity e = source.getEntity();
		if (e instanceof Player player && isTruePlayer(e) && getInvulTime() == 0) {

			if (!playersWhoAttacked.contains(player.getUUID())) {
				playersWhoAttacked.add(player.getUUID());
			}

			return super.hurt(source, Math.min(DAMAGE_CAP, amount));
		}

		return false;
	}

	@Override
	protected void actuallyHurt(@NotNull DamageSource source, float amount) {
		super.actuallyHurt(source, Math.min(DAMAGE_CAP, amount));

		Entity attacker = source.getDirectEntity();
		if (attacker != null) {
			Vec3 thisVector = VecHelper.fromEntityCenter(this);
			Vec3 playerVector = VecHelper.fromEntityCenter(attacker);
			Vec3 motionVector = thisVector.subtract(playerVector).normalize().scale(0.75);

			if (getHealth() > 0) {
				setDeltaMovement(-motionVector.x, 0.5, -motionVector.z);
				tpDelay = 4;
				spawnPixies = true;
			}
		}
		invulnerableTime = Math.max(invulnerableTime, 20);
	}

	@Override
	protected float getDamageAfterArmorAbsorb(DamageSource source, float damage) {
		return super.getDamageAfterArmorAbsorb(source, Math.min(DAMAGE_CAP, damage));
	}

	@Override
	public void die(@NotNull DamageSource source) {
		super.die(source);
		LivingEntity lastAttacker = getKillCredit();

		if (!level().isClientSide) {
			for (UUID u : playersWhoAttacked) {
				Player player = level().getPlayerByUUID(u);
				if (!isTruePlayer(player)) {
					continue;
				}
				DamageSource currSource = player == lastAttacker ? source : player.damageSources().playerAttack(player);
				if (player != lastAttacker) {
					// Vanilla handles this in attack code, but only for the killer
					CriteriaTriggers.PLAYER_KILLED_ENTITY.trigger((ServerPlayer) player, this, currSource);
				}
				if (!anyWithArmor) {
					GaiaGuardianNoArmorTrigger.INSTANCE.trigger((ServerPlayer) player, this, currSource);
				}
			}

			// Clear wither from nearby players
			for (Player player : getPlayersAround()) {
				if (player.getEffect(MobEffects.WITHER) != null) {
					player.removeEffect(MobEffects.WITHER);
				}
			}

			// Stop all the pixies leftover from the fight
			for (PixieEntity pixie : level().getEntitiesOfClass(PixieEntity.class, getArenaBB(getSource()), p -> p.isAlive() && p.getPixieType() == 1)) {
				pixie.spawnAnim();
				pixie.discard();
			}
			for (MagicLandmineEntity landmine : level().getEntitiesOfClass(MagicLandmineEntity.class, getArenaBB(getSource()))) {
				landmine.discard();
			}
		}

		playSound(BotaniaSounds.gaiaDeath, 1F, (1F + (level().random.nextFloat() - level().random.nextFloat()) * 0.2F) * 0.7F);
		level().addParticle(ParticleTypes.EXPLOSION_EMITTER, getX(), getY(), getZ(), 1D, 0D, 0D);
	}

	@Override
	public boolean removeWhenFarAway(double dist) {
		return false;
	}

	@Override
	public ResourceLocation getDefaultLootTable() {
		if (mobSpawnTicks > 0) {
			return BuiltInLootTables.EMPTY;
		}
		return prefix(hardMode ? "gaia_guardian_2" : "gaia_guardian");
	}

	@Override
	protected void dropFromLootTable(@NotNull DamageSource source, boolean wasRecentlyHit) {
		// Save true killer, they get extra loot
		if (wasRecentlyHit && isTruePlayer(source.getEntity())) {
			trueKiller = (Player) source.getEntity();
		}

		// Generate loot table for every single attacking player
		for (UUID u : playersWhoAttacked) {
			Player player = level().getPlayerByUUID(u);
			if (!isTruePlayer(player)) {
				continue;
			}

			Player saveLastAttacker = lastHurtByPlayer;
			Vec3 savePos = position();

			lastHurtByPlayer = player; // Fake attacking player as the killer
			// Spoof pos so drops spawn at the player
			setPos(player.getX(), player.getY(), player.getZ());
			super.dropFromLootTable(player.damageSources().playerAttack(player), wasRecentlyHit);
			setPos(savePos.x(), savePos.y(), savePos.z());
			lastHurtByPlayer = saveLastAttacker;
		}

		trueKiller = null;
	}

	@Override
	public void remove(RemovalReason reason) {
		if (level().isClientSide) {
			Proxy.INSTANCE.removeBoss(this);
		}
		super.remove(reason);
	}

	public List<Player> getPlayersAround() {
		return PlayerHelper.getRealPlayersIn(level(), getArenaBB(source));
	}

	public int getPlayerCount() {
		return playerCount;
	}

	private static int countGaiaGuardiansAround(Level world, BlockPos source) {
		List<GaiaGuardianEntity> l = world.getEntitiesOfClass(GaiaGuardianEntity.class, getArenaBB(source));
		return l.size();
	}

	@NotNull
	private static AABB getArenaBB(@NotNull BlockPos source) {
		double range = 15.0;
		return new AABB(source.getX() + 0.5 - range, source.getY() + 0.5 - range, source.getZ() + 0.5 - range, source.getX() + 0.5 + range, source.getY() + 0.5 + range, source.getZ() + 0.5 + range);
	}

	private void particles() {
		for (int i = 0; i < 360; i += 8) {
			float r = 0.6F;
			float g = 0F;
			float b = 0.2F;
			float m = 0.15F;
			float mv = 0.35F;

			float rad = i * (float) Math.PI / 180F;
			double x = source.getX() + 0.5 - Math.cos(rad) * ARENA_RANGE;
			double y = source.getY() + 0.5;
			double z = source.getZ() + 0.5 - Math.sin(rad) * ARENA_RANGE;

			WispParticleData data = WispParticleData.wisp(0.5F, r, g, b);
			level().addParticle(data, x, y, z, (float) (Math.random() - 0.5F) * m, (float) (Math.random() - 0.5F) * mv, (float) (Math.random() - 0.5F) * m);
		}

		if (getInvulTime() > 10) {
			Vec3 pos = VecHelper.fromEntityCenter(this).subtract(0, 0.2, 0);
			for (BlockPos arr : PYLON_LOCATIONS) {
				Vec3 pylonPos = new Vec3(source.getX() + arr.getX(), source.getY() + arr.getY(), source.getZ() + arr.getZ());
				double worldTime = tickCount;
				worldTime /= 5;

				float rad = 0.75F + (float) Math.random() * 0.05F;
				double xp = pylonPos.x + 0.5 + Math.cos(worldTime) * rad;
				double zp = pylonPos.z + 0.5 + Math.sin(worldTime) * rad;

				Vec3 partPos = new Vec3(xp, pylonPos.y, zp);
				Vec3 mot = pos.subtract(partPos).scale(0.04);

				float r = 0.7F + (float) Math.random() * 0.3F;
				float g = (float) Math.random() * 0.3F;
				float b = 0.7F + (float) Math.random() * 0.3F;

				WispParticleData data = WispParticleData.wisp(0.25F + (float) Math.random() * 0.1F, r, g, b, 1);
				level().addParticle(data, partPos.x, partPos.y, partPos.z, 0, -(-0.075F - (float) Math.random() * 0.015F), 0);
				WispParticleData data1 = WispParticleData.wisp(0.4F, r, g, b);
				level().addParticle(data1, partPos.x, partPos.y, partPos.z, (float) mot.x, (float) mot.y, (float) mot.z);
			}
		}
	}

	private void smashBlocksAround(int centerX, int centerY, int centerZ, int radius) {
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dy = -radius; dy <= radius + 1; dy++) {
				for (int dz = -radius; dz <= radius; dz++) {
					int x = centerX + dx;
					int y = centerY + dy;
					int z = centerZ + dz;

					BlockPos pos = new BlockPos(x, y, z);
					BlockState state = level().getBlockState(pos);
					Block block = state.getBlock();

					if (state.getDestroySpeed(level(), pos) == -1) {
						continue;
					}

					if (CHEATY_BLOCKS.contains(BuiltInRegistries.BLOCK.getKey(block))) {
						level().destroyBlock(pos, true);
					} else {
						//don't break blacklisted blocks
						if (state.is(BLACKLIST)) {
							continue;
						}
						//don't break the floor
						if (y < source.getY()) {
							continue;
						}
						//don't break blocks in pylon columns
						if (Math.abs(source.getX() - x) == 4 && Math.abs(source.getZ() - z) == 4) {
							continue;
						}

						level().destroyBlock(pos, true);
					}
				}
			}
		}
	}

	private void clearPotions(Player player) {
		Set<Holder<MobEffect>> effectsToRemove = new HashSet<>();
		for (var effectInstance : player.getActiveEffects()) {
			if (effectInstance.getDuration() < 160 && effectInstance.isAmbient() && effectInstance.getEffect().value().getCategory() != MobEffectCategory.HARMFUL) {
				effectsToRemove.add(effectInstance.getEffect());
			}
		}

		for (var effect : effectsToRemove) {
			player.removeEffect(effect);
			((ServerLevel) level()).getChunkSource().broadcastAndSend(player,
					new ClientboundRemoveMobEffectPacket(player.getId(), effect));
		}
	}

	private void keepInsideArena(Player player) {
		if (MathHelper.pointDistanceSpace(player.getX(), player.getY(), player.getZ(), source.getX() + 0.5, source.getY() + 0.5, source.getZ() + 0.5) >= ARENA_RANGE) {
			Vec3 sourceVector = new Vec3(source.getX() + 0.5, source.getY() + 0.5, source.getZ() + 0.5);
			Vec3 playerVector = VecHelper.fromEntityCenter(player);
			Vec3 motion = sourceVector.subtract(playerVector).normalize();

			player.setDeltaMovement(motion.x, 0.2, motion.z);
			player.hurtMarked = true;
		}
	}

	private void spawnMobs(List<Player> players) {
		for (int pl = 0; pl < playerCount; pl++) {
			for (int i = 0; i < 3 + level().random.nextInt(2); i++) {
				Mob entity = switch (level().random.nextInt(3)) {
					case 0 -> {
						if (level().random.nextInt(hardMode ? 3 : 12) == 0) {
							yield EntityType.WITCH.create(level());
						}
						yield EntityType.ZOMBIE.create(level());
					}
					case 1 -> {
						if (level().random.nextInt(8) == 0) {
							yield EntityType.WITHER_SKELETON.create(level());
						}
						yield EntityType.SKELETON.create(level());
					}
					case 2 -> {
						if (!players.isEmpty()) {
							for (int j = 0; j < 1 + level().random.nextInt(hardMode ? 8 : 5); j++) {
								PixieEntity pixie = new PixieEntity(level());
								pixie.setProps(players.get(random.nextInt(players.size())), this, 1, 8);
								pixie.setPos(getX() + getBbWidth() / 2, getY() + 2, getZ() + getBbWidth() / 2);
								pixie.finalizeSpawn((ServerLevelAccessor) level(), level().getCurrentDifficultyAt(pixie.blockPosition()),
										MobSpawnType.MOB_SUMMONED, null);
								level().addFreshEntity(pixie);
							}
						}
						yield null;
					}
					default -> null;
				};

				if (entity != null) {
					if (!entity.fireImmune()) {
						entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 600, 0));
					}
					float range = 6F;
					entity.setPos(getX() + 0.5 + Math.random() * range - range / 2, getY() - 1,
							getZ() + 0.5 + Math.random() * range - range / 2);
					entity.finalizeSpawn((ServerLevelAccessor) level(), level().getCurrentDifficultyAt(entity.blockPosition()),
							MobSpawnType.MOB_SUMMONED, null);
					if (entity instanceof WitherSkeleton && hardMode) {
						entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(BotaniaItems.elementiumSword));
					}
					level().addFreshEntity(entity);
				}
			}
		}
	}

	@Override
	public void aiStep() {
		super.aiStep();

		int invul = getInvulTime();

		if (level().isClientSide) {
			particles();
			Player player = Proxy.INSTANCE.getClientPlayer();
			if (getPlayersAround().contains(player)) {
				player.getAbilities().flying &= player.getAbilities().instabuild;
			}
			return;
		}

		bossInfo.setProgress(getHealth() / getMaxHealth());

		if (isPassenger()) {
			stopRiding();
		}

		if (level().getDifficulty() == Difficulty.PEACEFUL) {
			discard();
		}

		smashBlocksAround(Mth.floor(getX()), Mth.floor(getY()), Mth.floor(getZ()), 1);

		List<Player> players = getPlayersAround();

		if (players.isEmpty() && !level().players().isEmpty()) {
			discard();
		} else {
			for (Player player : players) {
				for (EquipmentSlot e : EquipmentSlot.values()) {
					if (e.getType() == EquipmentSlot.Type.ARMOR && !player.getItemBySlot(e).isEmpty()) {
						anyWithArmor = true;
						break;
					}
				}

				//also see SleepingHandler
				if (player.isSleeping()) {
					player.stopSleeping();
				}

				clearPotions(player);
				keepInsideArena(player);
				player.getAbilities().flying &= player.getAbilities().instabuild;
			}
		}

		if (!isAlive() || players.isEmpty()) {
			return;
		}

		boolean spawnMissiles = hardMode && tickCount % 15 < 4;

		if (invul > 0 && mobSpawnTicks == MOB_SPAWN_TICKS) {
			if (invul < SPAWN_TICKS) {
				if (invul > SPAWN_TICKS / 2 && level().random.nextInt(SPAWN_TICKS - invul + 1) == 0) {
					for (int i = 0; i < 2; i++) {
						spawnAnim();
					}
				}
			}

			setHealth(getHealth() + (getMaxHealth() - 1F) / SPAWN_TICKS);
			setInvulTime(invul - 1);

			setDeltaMovement(getDeltaMovement().x(), 0, getDeltaMovement().z());
		} else {
			if (aggro) {
				boolean dying = getHealth() / getMaxHealth() < 0.2;
				if (dying && mobSpawnTicks > 0) {
					setDeltaMovement(Vec3.ZERO);

					int reverseTicks = MOB_SPAWN_TICKS - mobSpawnTicks;
					if (reverseTicks < MOB_SPAWN_START_TICKS) {
						setDeltaMovement(getDeltaMovement().x(), 0.2, getDeltaMovement().z());
						setInvulTime(invul + 1);
					}

					if (reverseTicks > MOB_SPAWN_START_TICKS * 2 && mobSpawnTicks > MOB_SPAWN_END_TICKS && mobSpawnTicks % MOB_SPAWN_WAVE_TIME == 0) {
						spawnMobs(players);

						if (hardMode && tickCount % 3 < 2) {
							for (int i = 0; i < playerCount; i++) {
								spawnMissile();
							}
							spawnMissiles = false;
						}
					}

					mobSpawnTicks--;
					tpDelay = 10;
				} else if (tpDelay > 0) {
					if (invul > 0) {
						setInvulTime(invul - 1);
					}

					tpDelay--;
					if (tpDelay == 0 && getHealth() > 0) {
						teleportRandomly();

						if (spawnLandmines) {
							int count = dying && hardMode ? 7 : 6;
							for (int i = 0; i < count; i++) {
								int x = source.getX() - 10 + random.nextInt(20);
								int y = (int) players.get(random.nextInt(players.size())).getY();
								int z = source.getZ() - 10 + random.nextInt(20);

								MagicLandmineEntity landmine = BotaniaEntities.MAGIC_LANDMINE.create(level());
								landmine.setPos(x + 0.5, y, z + 0.5);
								landmine.summoner = this;
								level().addFreshEntity(landmine);
							}

						}

						for (int pl = 0; pl < playerCount; pl++) {
							for (int i = 0; i < (spawnPixies ? level().random.nextInt(hardMode ? 6 : 3) : 1); i++) {
								PixieEntity pixie = new PixieEntity(level());
								pixie.setProps(players.get(random.nextInt(players.size())), this, 1, 8);
								pixie.setPos(getX() + getBbWidth() / 2, getY() + 2, getZ() + getBbWidth() / 2);
								pixie.finalizeSpawn((ServerLevelAccessor) level(), level().getCurrentDifficultyAt(pixie.blockPosition()),
										MobSpawnType.MOB_SUMMONED, null);
								level().addFreshEntity(pixie);
							}
						}

						tpDelay = hardMode ? dying ? 35 : 45 : dying ? 40 : 60;
						spawnLandmines = true;
						spawnPixies = false;
					}
				}

				if (spawnMissiles) {
					spawnMissile();
				}
			} else {
				tpDelay = 30; // Trigger first teleport
				aggro = true;
			}
		}
	}

	@Override
	public boolean canChangeDimensions() {
		return false;
	}

	@Override
	public void startSeenByPlayer(ServerPlayer player) {
		super.startSeenByPlayer(player);
		bossInfo.addPlayer(player);
	}

	@Override
	public void stopSeenByPlayer(ServerPlayer player) {
		super.stopSeenByPlayer(player);
		bossInfo.removePlayer(player);
	}

	@Override
	protected void pushEntities() {
		if (getInvulTime() == 0) {
			super.pushEntities();
		}
	}

	@Override
	public boolean isPushable() {
		return super.isPushable() && getInvulTime() == 0;
	}

	private void spawnMissile() {
		MagicMissileEntity missile = new MagicMissileEntity(this, true);
		missile.setPos(getX() + (Math.random() - 0.5 * 0.1), getY() + 2.4 + (Math.random() - 0.5 * 0.1), getZ() + (Math.random() - 0.5 * 0.1));
		if (missile.findTarget()) {
			playSound(BotaniaSounds.missile, 1F, 0.8F + (float) Math.random() * 0.2F);
			level().addFreshEntity(missile);
		}
	}

	private void teleportRandomly() {
		//choose a location to teleport to
		double oldX = getX(), oldY = getY(), oldZ = getZ();
		double newX, newY = source.getY(), newZ;
		int tries = 0;

		do {
			newX = source.getX() + (random.nextDouble() - .5) * ARENA_RANGE;
			newZ = source.getZ() + (random.nextDouble() - .5) * ARENA_RANGE;
			tries++;
			//ensure it's inside the arena ring, and not just its bounding square
		} while (tries < 50 && MathHelper.pointDistanceSpace(newX, newY, newZ, source.getX(), source.getY(), source.getZ()) > 12);

		if (tries == 50) {
			//failsafe: teleport to the beacon
			newX = source.getX() + .5;
			newY = source.getY() + 1.6;
			newZ = source.getZ() + .5;
		}

		//for low-floor arenas, ensure landing on the ground
		BlockPos tentativeFloorPos = BlockPos.containing(newX, newY - 1, newZ);
		if (level().getBlockState(tentativeFloorPos).getCollisionShape(level(), tentativeFloorPos).isEmpty()) {
			newY--;
		}

		//teleport there
		teleportTo(newX, newY, newZ);

		//play sound
		level().playSound(null, oldX, oldY, oldZ, BotaniaSounds.gaiaTeleport, this.getSoundSource(), 1F, 1F);
		this.playSound(BotaniaSounds.gaiaTeleport, 1F, 1F);

		var random = getRandom();

		//spawn particles along the path
		int particleCount = 128;
		for (int i = 0; i < particleCount; ++i) {
			double progress = i / (double) (particleCount - 1);
			float vx = (random.nextFloat() - 0.5F) * 0.2F;
			float vy = (random.nextFloat() - 0.5F) * 0.2F;
			float vz = (random.nextFloat() - 0.5F) * 0.2F;
			double px = oldX + (newX - oldX) * progress + (random.nextDouble() - 0.5D) * getBbWidth() * 2.0D;
			double py = oldY + (newY - oldY) * progress + random.nextDouble() * getBbHeight();
			double pz = oldZ + (newZ - oldZ) * progress + (random.nextDouble() - 0.5D) * getBbWidth() * 2.0D;
			level().addParticle(ParticleTypes.PORTAL, px, py, pz, vx, vy, vz);
		}

		Vec3 oldPosVec = new Vec3(oldX, oldY + getBbHeight() / 2, oldZ);
		Vec3 newPosVec = new Vec3(newX, newY + getBbHeight() / 2, newZ);

		if (oldPosVec.distanceToSqr(newPosVec) > 1) {
			//damage players in the path of the teleport
			for (Player player : getPlayersAround()) {
				boolean hit = player.getBoundingBox().inflate(0.25).clip(oldPosVec, newPosVec)
						.isPresent();
				if (hit) {
					player.hurt(damageSources().mobAttack(this), 6);
				}
			}

			//break blocks in the path of the teleport
			int breakSteps = (int) oldPosVec.distanceTo(newPosVec);
			if (breakSteps >= 2) {
				for (int i = 0; i < breakSteps; i++) {
					float progress = i / (float) (breakSteps - 1);
					int breakX = Mth.floor(oldX + (newX - oldX) * progress);
					int breakY = Mth.floor(oldY + (newY - oldY) * progress);
					int breakZ = Mth.floor(oldZ + (newZ - oldZ) * progress);

					smashBlocksAround(breakX, breakY, breakZ, 1);
				}
			}
		}
	}

	public UUID getBossInfoUuid() {
		return bossInfoUUID;
	}

	public boolean isHardMode() {
		return hardMode;
	}

	public void readSpawnData(int playerCount, boolean hardMode, BlockPos source, UUID bossInfoUUID) {
		this.playerCount = playerCount;
		this.hardMode = hardMode;
		this.source = source;
		this.bossInfoUUID = bossInfoUUID;
		Proxy.INSTANCE.runOnClient(() -> () -> DopplegangerMusic.play(this));
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return XplatAbstractions.INSTANCE.toVanillaClientboundPacket(
				new SpawnGaiaGuardianPacket(new ClientboundAddEntityPacket(this), playerCount, hardMode, source, bossInfoUUID));
	}

	@Override
	public boolean canBeLeashed(Player player) {
		return false;
	}

	private static class DopplegangerMusic extends AbstractTickableSoundInstance {
		private final GaiaGuardianEntity guardian;

		private DopplegangerMusic(GaiaGuardianEntity guardian) {
			super(guardian.hardMode ? BotaniaSounds.gaiaMusic2 : BotaniaSounds.gaiaMusic1, SoundSource.RECORDS, SoundInstance.createUnseededRandom());
			this.guardian = guardian;
			this.x = guardian.getSource().getX();
			this.y = guardian.getSource().getY();
			this.z = guardian.getSource().getZ();
			this.looping = true;
		}

		public static void play(GaiaGuardianEntity guardian) {
			Minecraft.getInstance().getSoundManager().play(new DopplegangerMusic(guardian));
		}

		@Override
		public void tick() {
			if (!guardian.isAlive()) {
				stop();
			}
		}
	}
}
