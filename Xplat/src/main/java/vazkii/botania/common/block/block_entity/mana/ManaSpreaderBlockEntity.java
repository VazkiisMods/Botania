/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity.mana;

import com.google.common.base.Predicates;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.BotaniaAPIClient;
import vazkii.botania.api.block.WandBindable;
import vazkii.botania.api.block.WandHUD;
import vazkii.botania.api.block.Wandable;
import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.*;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.ExposedSimpleInventoryBlockEntity;
import vazkii.botania.common.block.mana.ManaSpreaderBlock;
import vazkii.botania.common.entity.ManaBurstEntity;
import vazkii.botania.common.entity.ManaBurstEntity.PositionProperties;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.handler.ManaNetworkHandler;
import vazkii.botania.common.helper.MathHelper;
import vazkii.botania.common.item.LexicaBotaniaItem;
import vazkii.botania.xplat.BotaniaConfig;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ManaSpreaderBlockEntity extends ExposedSimpleInventoryBlockEntity implements WandBindable, KeyLocked, ThrottledPacket, ManaSpreader, Wandable {
	private static final int TICKS_ALLOWED_WITHOUT_PINGBACK = 20;
	private static final double PINGBACK_EXPIRED_SEARCH_DISTANCE = 0.5;

	private static final String TAG_UUID = "uuid";
	private static final String TAG_MANA = "mana";
	private static final String TAG_REQUEST_UPDATE = "requestUpdate";
	private static final String TAG_ROTATION_X = "rotationX";
	private static final String TAG_ROTATION_Y = "rotationY";
	private static final String TAG_PADDING_COLOR = "paddingColor";
	private static final String TAG_CAN_SHOOT_BURST = "canShootBurst";
	private static final String TAG_PINGBACK_TICKS = "pingbackTicks";
	private static final String TAG_LAST_PINGBACK_X = "lastPingbackX";
	private static final String TAG_LAST_PINGBACK_Y = "lastPingbackY";
	private static final String TAG_LAST_PINGBACK_Z = "lastPingbackZ";

	private static final String TAG_FORCE_CLIENT_BINDING_X = "forceClientBindingX";
	private static final String TAG_FORCE_CLIENT_BINDING_Y = "forceClientBindingY";
	private static final String TAG_FORCE_CLIENT_BINDING_Z = "forceClientBindingZ";

	// Map Maker Tags

	private static final String TAG_INPUT_KEY = "inputKey";
	private static final String TAG_OUTPUT_KEY = "outputKey";

	private static final String TAG_MAPMAKER_OVERRIDE = "mapmakerOverrideEnabled";
	private static final String TAG_FORCED_COLOR = "mmForcedColor";
	private static final String TAG_FORCED_MANA_PAYLOAD = "mmForcedManaPayload";
	private static final String TAG_FORCED_TICKS_BEFORE_MANA_LOSS = "mmForcedTicksBeforeManaLoss";
	private static final String TAG_FORCED_MANA_LOSS_PER_TICK = "mmForcedManaLossPerTick";
	private static final String TAG_FORCED_GRAVITY = "mmForcedGravity";
	private static final String TAG_FORCED_VELOCITY_MULTIPLIER = "mmForcedVelocityMultiplier";

	private boolean mapmakerOverride = false;
	private int mmForcedColor = 0x20FF20;
	private int mmForcedManaPayload = 160;
	private int mmForcedTicksBeforeManaLoss = 60;
	private float mmForcedManaLossPerTick = 4F;
	private float mmForcedGravity = 0F;
	private float mmForcedVelocityMultiplier = 1F;

	private String inputKey = "";
	private final String outputKey = "";

	// End Map Maker Tags

	private UUID identity = UUID.randomUUID();

	private int mana;
	public float rotationX, rotationY;

	@Nullable
	public DyeColor paddingColor = null;

	private boolean requestsClientUpdate = false;
	private boolean hasReceivedInitialPacket = false;

	private ManaReceiver receiver = null;
	private ManaReceiver receiverLastTick = null;

	private boolean poweredLastTick = true;
	public boolean canShootBurst = true;
	public int lastBurstDeathTick = -1;
	public int burstParticleTick = 0;

	public int pingbackTicks = 0;
	public double lastPingbackX = 0;
	public double lastPingbackY = Integer.MIN_VALUE;
	public double lastPingbackZ = 0;

	private List<PositionProperties> lastTentativeBurst;
	private boolean invalidTentativeBurst = false;

	public ManaSpreaderBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.SPREADER, pos, state);
	}

	@Override
	public boolean isFull() {
		return mana >= getMaxMana();
	}

	@Override
	public void receiveMana(int mana) {
		this.mana = Math.min(this.mana + mana, getMaxMana());
		this.setChanged();
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		BotaniaAPI.instance().getManaNetworkInstance().fireManaNetworkEvent(this, ManaBlockType.COLLECTOR, ManaNetworkAction.REMOVE);
	}

	public static void commonTick(Level level, BlockPos worldPosition, BlockState state, ManaSpreaderBlockEntity self) {
		boolean inNetwork = ManaNetworkHandler.instance.isCollectorIn(level, self);
		boolean wasInNetwork = inNetwork;
		if (!inNetwork && !self.isRemoved()) {
			BotaniaAPI.instance().getManaNetworkInstance().fireManaNetworkEvent(self, ManaBlockType.COLLECTOR, ManaNetworkAction.ADD);
		}

		boolean powered = false;

		for (Direction dir : Direction.values()) {
			var relPos = worldPosition.relative(dir);
			if (level.hasChunkAt(relPos)) {
				var receiverAt = XplatAbstractions.INSTANCE.findManaReceiver(level, relPos, dir.getOpposite());
				if (receiverAt instanceof ManaPool pool) {
					if (wasInNetwork && (pool != self.receiver || self.getVariant() == ManaSpreaderBlock.Variant.REDSTONE)) {
						if (pool instanceof KeyLocked locked && !locked.getOutputKey().equals(self.getInputKey())) {
							continue;
						}

						int manaInPool = pool.getCurrentMana();
						if (manaInPool > 0 && !self.isFull()) {
							int manaMissing = self.getMaxMana() - self.mana;
							int manaToRemove = Math.min(manaInPool, manaMissing);
							pool.receiveMana(-manaToRemove);
							self.receiveMana(manaToRemove);
						}
					}
				}
				powered = powered || level.hasSignal(relPos, dir);
			}
		}

		if (self.needsNewBurstSimulation()) {
			self.checkForReceiver();
		}

		if (!self.canShootBurst) {
			if (self.pingbackTicks <= 0) {
				double x = self.lastPingbackX;
				double y = self.lastPingbackY;
				double z = self.lastPingbackZ;
				AABB aabb = new AABB(x, y, z, x, y, z).inflate(PINGBACK_EXPIRED_SEARCH_DISTANCE, PINGBACK_EXPIRED_SEARCH_DISTANCE, PINGBACK_EXPIRED_SEARCH_DISTANCE);
				@SuppressWarnings("unchecked")
				List<ManaBurst> bursts = (List<ManaBurst>) (List<?>) level.getEntitiesOfClass(ThrowableProjectile.class, aabb, Predicates.instanceOf(ManaBurst.class));
				ManaBurst found = null;
				UUID identity = self.getIdentifier();
				for (ManaBurst burst : bursts) {
					if (burst != null && identity.equals(burst.getShooterUUID())) {
						found = burst;
						break;
					}
				}

				if (found != null) {
					found.ping();
				} else {
					self.setCanShoot(true);
				}
			} else {
				self.pingbackTicks--;
			}
		}

		boolean shouldShoot = !powered;

		boolean redstoneSpreader = self.getVariant() == ManaSpreaderBlock.Variant.REDSTONE;
		if (redstoneSpreader) {
			shouldShoot = powered && !self.poweredLastTick;
		}

		if (shouldShoot && self.receiver instanceof KeyLocked locked) {
			shouldShoot = locked.getInputKey().equals(self.getOutputKey());
		}

		ItemStack lens = self.getItemHandler().getItem(0);
		ControlLensItem control = self.getLensController(lens);
		if (control != null) {
			if (redstoneSpreader) {
				if (shouldShoot) {
					control.onControlledSpreaderPulse(lens, self);
				}
			} else {
				control.onControlledSpreaderTick(lens, self, powered);
			}

			shouldShoot = shouldShoot && control.allowBurstShooting(lens, self, powered);
		}

		if (shouldShoot) {
			self.tryShootBurst();
		}

		if (self.receiverLastTick != self.receiver && !level.isClientSide) {
			self.requestsClientUpdate = true;
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(self);
		}

		self.poweredLastTick = powered;
		self.receiverLastTick = self.receiver;
	}

	@Override
	public void writePacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.writePacketNBT(cmp, registries);

		cmp.putUUID(TAG_UUID, getIdentifier());

		cmp.putInt(TAG_MANA, mana);
		cmp.putFloat(TAG_ROTATION_X, rotationX);
		cmp.putFloat(TAG_ROTATION_Y, rotationY);
		cmp.putBoolean(TAG_REQUEST_UPDATE, requestsClientUpdate);
		cmp.putInt(TAG_PADDING_COLOR, paddingColor == null ? -1 : paddingColor.getId());
		cmp.putBoolean(TAG_CAN_SHOOT_BURST, canShootBurst);

		cmp.putInt(TAG_PINGBACK_TICKS, pingbackTicks);
		cmp.putDouble(TAG_LAST_PINGBACK_X, lastPingbackX);
		cmp.putDouble(TAG_LAST_PINGBACK_Y, lastPingbackY);
		cmp.putDouble(TAG_LAST_PINGBACK_Z, lastPingbackZ);

		cmp.putString(TAG_INPUT_KEY, inputKey);
		cmp.putString(TAG_OUTPUT_KEY, outputKey);

		cmp.putInt(TAG_FORCE_CLIENT_BINDING_X, receiver == null ? 0 : receiver.getManaReceiverPos().getX());
		cmp.putInt(TAG_FORCE_CLIENT_BINDING_Y, receiver == null ? Integer.MIN_VALUE : receiver.getManaReceiverPos().getY());
		cmp.putInt(TAG_FORCE_CLIENT_BINDING_Z, receiver == null ? 0 : receiver.getManaReceiverPos().getZ());

		cmp.putBoolean(TAG_MAPMAKER_OVERRIDE, mapmakerOverride);
		cmp.putInt(TAG_FORCED_COLOR, mmForcedColor);
		cmp.putInt(TAG_FORCED_MANA_PAYLOAD, mmForcedManaPayload);
		cmp.putInt(TAG_FORCED_TICKS_BEFORE_MANA_LOSS, mmForcedTicksBeforeManaLoss);
		cmp.putFloat(TAG_FORCED_MANA_LOSS_PER_TICK, mmForcedManaLossPerTick);
		cmp.putFloat(TAG_FORCED_GRAVITY, mmForcedGravity);
		cmp.putFloat(TAG_FORCED_VELOCITY_MULTIPLIER, mmForcedVelocityMultiplier);

		requestsClientUpdate = false;
	}

	@Override
	public void readPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.readPacketNBT(cmp, registries);

		String tagUuidMostDeprecated = "uuidMost";
		String tagUuidLeastDeprecated = "uuidLeast";

		if (cmp.hasUUID(TAG_UUID)) {
			identity = cmp.getUUID(TAG_UUID);
		} else if (cmp.contains(tagUuidLeastDeprecated) && cmp.contains(tagUuidMostDeprecated)) { // legacy world compat
			long most = cmp.getLong(tagUuidMostDeprecated);
			long least = cmp.getLong(tagUuidLeastDeprecated);
			if (identity == null || most != identity.getMostSignificantBits() || least != identity.getLeastSignificantBits()) {
				this.identity = new UUID(most, least);
			}
		}

		mana = cmp.getInt(TAG_MANA);
		rotationX = cmp.getFloat(TAG_ROTATION_X);
		rotationY = cmp.getFloat(TAG_ROTATION_Y);
		requestsClientUpdate = cmp.getBoolean(TAG_REQUEST_UPDATE);

		if (cmp.contains(TAG_INPUT_KEY)) {
			inputKey = cmp.getString(TAG_INPUT_KEY);
		}
		if (cmp.contains(TAG_OUTPUT_KEY)) {
			inputKey = cmp.getString(TAG_OUTPUT_KEY);
		}

		mapmakerOverride = cmp.getBoolean(TAG_MAPMAKER_OVERRIDE);
		mmForcedColor = cmp.getInt(TAG_FORCED_COLOR);
		mmForcedManaPayload = cmp.getInt(TAG_FORCED_MANA_PAYLOAD);
		mmForcedTicksBeforeManaLoss = cmp.getInt(TAG_FORCED_TICKS_BEFORE_MANA_LOSS);
		mmForcedManaLossPerTick = cmp.getFloat(TAG_FORCED_MANA_LOSS_PER_TICK);
		mmForcedGravity = cmp.getFloat(TAG_FORCED_GRAVITY);
		mmForcedVelocityMultiplier = cmp.getFloat(TAG_FORCED_VELOCITY_MULTIPLIER);

		if (cmp.contains(TAG_PADDING_COLOR)) {
			paddingColor = cmp.getInt(TAG_PADDING_COLOR) == -1 ? null : DyeColor.byId(cmp.getInt(TAG_PADDING_COLOR));
		}
		if (cmp.contains(TAG_CAN_SHOOT_BURST)) {
			canShootBurst = cmp.getBoolean(TAG_CAN_SHOOT_BURST);
		}

		pingbackTicks = cmp.getInt(TAG_PINGBACK_TICKS);
		lastPingbackX = cmp.getDouble(TAG_LAST_PINGBACK_X);
		lastPingbackY = cmp.getDouble(TAG_LAST_PINGBACK_Y);
		lastPingbackZ = cmp.getDouble(TAG_LAST_PINGBACK_Z);

		if (requestsClientUpdate && level != null) {
			int x = cmp.getInt(TAG_FORCE_CLIENT_BINDING_X);
			int y = cmp.getInt(TAG_FORCE_CLIENT_BINDING_Y);
			int z = cmp.getInt(TAG_FORCE_CLIENT_BINDING_Z);
			if (y != Integer.MIN_VALUE) {
				var pos = new BlockPos(x, y, z);
				receiver = XplatAbstractions.INSTANCE.findManaReceiver(level, pos, null);
			} else {
				receiver = null;
			}
		}

		if (level != null && level.isClientSide) {
			hasReceivedInitialPacket = true;
		}
	}

	@Override
	public boolean canReceiveManaFromBursts() {
		return true;
	}

	@Override
	public Level getManaReceiverLevel() {
		return getLevel();
	}

	@Override
	public BlockPos getManaReceiverPos() {
		return getBlockPos();
	}

	@Override
	public int getCurrentMana() {
		return mana;
	}

	@Override
	public boolean onUsedByWand(@Nullable Player player, ItemStack wand, Direction side) {
		if (player == null) {
			return false;
		}

		if (!player.isShiftKeyDown()) {
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		} else {
			BlockHitResult bpos = LexicaBotaniaItem.doRayTrace(level, player, ClipContext.Fluid.NONE);
			if (!level.isClientSide) {
				double x = bpos.getLocation().x - getBlockPos().getX() - 0.5;
				double y = bpos.getLocation().y - getBlockPos().getY() - 0.5;
				double z = bpos.getLocation().z - getBlockPos().getZ() - 0.5;

				if (bpos.getDirection() != Direction.DOWN && bpos.getDirection() != Direction.UP) {
					Vec3 clickVector = new Vec3(x, 0, z);
					Vec3 relative = new Vec3(-0.5, 0, 0);
					double angle = Math.acos(clickVector.dot(relative) / (relative.length() * clickVector.length())) * 180D / Math.PI;

					rotationX = (float) angle + 180F;
					if (clickVector.z < 0) {
						rotationX = 360 - rotationX;
					}
				}

				double angle = y * 180;
				rotationY = -(float) angle;

				setChanged();
				requestsClientUpdate = true;
			}
		}
		return true;
	}

	private boolean needsNewBurstSimulation() {
		if (level.isClientSide && !hasReceivedInitialPacket) {
			return false;
		}

		if (lastTentativeBurst == null) {
			return true;
		}

		for (PositionProperties props : lastTentativeBurst) {
			if (!props.contentsEqual(level)) {
				invalidTentativeBurst = props.isInvalidIn(level);
				return !invalidTentativeBurst;
			}
		}

		return false;
	}

	private void tryShootBurst() {
		boolean redstone = getVariant() == ManaSpreaderBlock.Variant.REDSTONE;
		if ((receiver != null || redstone) && !invalidTentativeBurst) {
			if (canShootBurst && (redstone || receiver.canReceiveManaFromBursts() && !receiver.isFull())) {
				ManaBurstEntity burst = getBurst(false);
				if (burst != null) {
					if (!level.isClientSide) {
						this.receiveMana(-burst.getStartingMana());
						burst.setShooterUUID(getIdentifier());
						level.addFreshEntity(burst);
						burst.ping();
						if (!BotaniaConfig.common().silentSpreaders()) {
							level.playSound(null, worldPosition, BotaniaSounds.spreaderFire, SoundSource.BLOCKS, 0.05F * (paddingColor != null ? 0.2F : 1F), 0.7F + 0.3F * (float) Math.random());
						}
					}
				}
			}
		}
	}

	public ManaSpreaderBlock.Variant getVariant() {
		Block b = getBlockState().getBlock();
		if (b instanceof ManaSpreaderBlock spreader) {
			return spreader.variant;
		} else {
			return ManaSpreaderBlock.Variant.MANA;
		}
	}

	// Should only be called on server
	public void checkForReceiver() {
		ItemStack stack = getItemHandler().getItem(0);
		ControlLensItem control = getLensController(stack);
		if (control != null && !control.allowBurstShooting(stack, this, false)) {
			return;
		}

		ManaBurstEntity fakeBurst = getBurst(true);
		fakeBurst.setScanBeam();
		ManaReceiver receiver = fakeBurst.getCollidedTile(true);

		if (receiver != null && receiver.getManaReceiverLevel().hasChunkAt(receiver.getManaReceiverPos())) {
			this.receiver = receiver;
		} else {
			this.receiver = null;
		}
		lastTentativeBurst = fakeBurst.propsList;
	}

	@Override
	public ManaBurst runBurstSimulation() {
		ManaBurstEntity fakeBurst = getBurst(true);
		fakeBurst.setScanBeam();
		fakeBurst.getCollidedTile(true);
		return fakeBurst;
	}

	private ManaBurstEntity getBurst(boolean fake) {
		ManaSpreaderBlock.Variant variant = getVariant();
		float gravity = 0F;
		BurstProperties props = new BurstProperties(variant.burstMana, variant.preLossTicks, variant.lossPerTick, gravity, variant.motionModifier, variant.color);

		ItemStack lens = getItemHandler().getItem(0);
		if (!lens.isEmpty() && lens.getItem() instanceof LensEffectItem lensEffectItem) {
			lensEffectItem.apply(lens, props, level);
		}

		if (getCurrentMana() >= props.maxMana || fake) {
			ManaBurstEntity burst = new ManaBurstEntity(getLevel(), getBlockPos(), getRotationX(), getRotationY(), fake);
			burst.setSourceLens(lens);

			if (mapmakerOverride) {
				burst.setColor(mmForcedColor);
				burst.setMana(mmForcedManaPayload);
				burst.setStartingMana(mmForcedManaPayload);
				burst.setMinManaLoss(mmForcedTicksBeforeManaLoss);
				burst.setManaLossPerTick(mmForcedManaLossPerTick);
				burst.setGravity(mmForcedGravity);
				burst.setDeltaMovement(burst.getDeltaMovement().scale(mmForcedVelocityMultiplier));
			} else {
				burst.setColor(props.color);
				burst.setMana(props.maxMana);
				burst.setStartingMana(props.maxMana);
				burst.setMinManaLoss(props.ticksBeforeManaLoss);
				burst.setManaLossPerTick(props.manaLossPerTick);
				burst.setGravity(props.gravity);
				burst.setDeltaMovement(burst.getDeltaMovement().scale(props.motionModifier));
			}

			return burst;
		}
		return null;
	}

	public ControlLensItem getLensController(ItemStack stack) {
		if (!stack.isEmpty() && stack.getItem() instanceof ControlLensItem control) {
			if (control.isControlLens(stack)) {
				return control;
			}
		}

		return null;
	}

	public static class WandHud implements WandHUD {
		private final ManaSpreaderBlockEntity spreader;

		public WandHud(ManaSpreaderBlockEntity spreader) {
			this.spreader = spreader;
		}

		@Override
		public void renderHUD(GuiGraphics gui, Minecraft mc) {
			String spreaderName = new ItemStack(spreader.getBlockState().getBlock()).getHoverName().getString();

			ItemStack lensStack = spreader.getItemHandler().getItem(0);
			ItemStack recieverStack = spreader.receiver == null ? ItemStack.EMPTY : new ItemStack(spreader.level.getBlockState(spreader.receiver.getManaReceiverPos()).getBlock());

			int width = 4 + Collections.max(Arrays.asList(
					102, // Mana bar width
					mc.font.width(spreaderName),
					RenderHelper.itemWithNameWidth(mc, lensStack),
					RenderHelper.itemWithNameWidth(mc, recieverStack)
			));
			int height = 22 + (lensStack.isEmpty() ? 0 : 18) + (recieverStack.isEmpty() ? 0 : 18);

			int centerX = mc.getWindow().getGuiScaledWidth() / 2;
			int centerY = mc.getWindow().getGuiScaledHeight() / 2;
			RenderHelper.renderHUDBox(gui, centerX - width / 2, centerY + 8, centerX + width / 2, centerY + 8 + height);

			int color = spreader.getVariant().hudColor;
			BotaniaAPIClient.instance().drawSimpleManaHUD(gui, color, spreader.getCurrentMana(), spreader.getMaxMana(), spreaderName);
			RenderHelper.renderItemWithNameCentered(gui, mc, recieverStack, centerY + 30, color);
			RenderHelper.renderItemWithNameCentered(gui, mc, lensStack, centerY + (recieverStack.isEmpty() ? 30 : 48), color);
		}
	}

	@Override
	public void onClientDisplayTick() {
		if (level != null) {
			ManaBurstEntity burst = getBurst(true);
			burst.getCollidedTile(false);
		}
	}

	@Override
	public float getManaYieldMultiplier(ManaBurst burst) {
		return 1F;
	}

	@Override
	protected SimpleContainer createItemHandler() {
		return new SimpleContainer(1) {
			@Override
			public int getMaxStackSize() {
				return 1;
			}

			@Override
			public boolean canPlaceItem(int index, ItemStack stack) {
				return !stack.isEmpty() && stack.getItem() instanceof BasicLensItem;
			}
		};
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level != null) {
			if (!level.isClientSide) {
				checkForReceiver();
				VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
			}
		}
	}

	@Override
	public BlockPos getBinding() {
		if (receiver == null) {
			return null;
		}

		return receiver.getManaReceiverPos();
	}

	@Override
	public int getMaxMana() {
		return getVariant().manaCapacity;
	}

	@Override
	public String getInputKey() {
		return inputKey;
	}

	@Override
	public String getOutputKey() {
		return outputKey;
	}

	@Override
	public boolean canSelect(Player player, ItemStack wand, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public boolean bindTo(Player player, ItemStack wand, BlockPos pos, Direction side) {
		VoxelShape shape = player.level().getBlockState(pos).getShape(player.level(), pos);
		AABB axis = shape.isEmpty() ? new AABB(pos) : shape.bounds().move(pos);

		Vec3 thisVec = Vec3.atCenterOf(getBlockPos());
		Vec3 blockVec = new Vec3(axis.minX + (axis.maxX - axis.minX) / 2, axis.minY + (axis.maxY - axis.minY) / 2, axis.minZ + (axis.maxZ - axis.minZ) / 2);

		Vec3 diffVec = blockVec.subtract(thisVec);
		Vec3 diffVec2D = new Vec3(diffVec.x, diffVec.z, 0);
		Vec3 rotVec = new Vec3(0, 1, 0);
		double angle = MathHelper.angleBetween(rotVec, diffVec2D) / Math.PI * 180.0;

		if (blockVec.x < thisVec.x) {
			angle = -angle;
		}

		rotationX = (float) angle + 90;

		rotVec = new Vec3(diffVec.x, 0, diffVec.z);
		angle = MathHelper.angleBetween(diffVec, rotVec) * 180F / Math.PI;
		if (blockVec.y < thisVec.y) {
			angle = -angle;
		}
		rotationY = (float) angle;

		setChanged();
		return true;
	}

	@Override
	public void markDispatchable() {}

	@Override
	public float getRotationX() {
		return rotationX;
	}

	@Override
	public float getRotationY() {
		return rotationY;
	}

	@Override
	public void setRotationX(float rot) {
		rotationX = rot;
	}

	@Override
	public void setRotationY(float rot) {
		rotationY = rot;
	}

	public void rotate(Rotation rotation) {
		switch (rotation) {
			case CLOCKWISE_90 -> rotationX += 270F;
			case CLOCKWISE_180 -> rotationX += 180F;
			case COUNTERCLOCKWISE_90 -> rotationX += 90F;
			case NONE -> {
			}
		}

		if (rotationX >= 360F) {
			rotationX -= 360F;
		}
	}

	public void mirror(Mirror mirror) {
		switch (mirror) {
			case LEFT_RIGHT -> rotationX = 360F - rotationX;
			case FRONT_BACK -> rotationX = 180F - rotationX;
			case NONE -> {
			}
		}

		if (rotationX < 0F) {
			rotationX += 360F;
		}
	}

	@Override
	public void commitRedirection() {
		setChanged();
	}

	@Override
	public void setCanShoot(boolean canShoot) {
		canShootBurst = canShoot;
	}

	@Override
	public int getBurstParticleTick() {
		return burstParticleTick;
	}

	@Override
	public void setBurstParticleTick(int i) {
		burstParticleTick = i;
	}

	@Override
	public int getLastBurstDeathTick() {
		return lastBurstDeathTick;
	}

	@Override
	public void setLastBurstDeathTick(int i) {
		lastBurstDeathTick = i;
	}

	@Override
	public void pingback(ManaBurst burst, UUID expectedIdentity) {
		if (getIdentifier().equals(expectedIdentity)) {
			pingbackTicks = TICKS_ALLOWED_WITHOUT_PINGBACK;
			Entity e = burst.entity();
			lastPingbackX = e.getX();
			lastPingbackY = e.getY();
			lastPingbackZ = e.getZ();
			setCanShoot(false);
		}
	}

	@Override
	public UUID getIdentifier() {
		return identity;
	}

}
