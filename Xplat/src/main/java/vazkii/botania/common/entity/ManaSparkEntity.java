/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.block.WandHUD;
import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.api.mana.ManaPool;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.api.mana.spark.ManaSpark;
import vazkii.botania.api.mana.spark.SparkAttachable;
import vazkii.botania.api.mana.spark.SparkHelper;
import vazkii.botania.api.mana.spark.SparkUpgradeType;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.helper.VecHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.SparkAugmentItem;
import vazkii.botania.common.item.WandOfTheForestItem;
import vazkii.botania.network.EffectType;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.*;

public class ManaSparkEntity extends SparkBaseEntity implements ManaSpark {
	private static final int TRANSFER_RATE = 1000;
	private static final String TAG_UPGRADE = "upgrade";
	private static final EntityDataAccessor<Integer> UPGRADE = SynchedEntityData.defineId(ManaSparkEntity.class, EntityDataSerializers.INT);

	private final Set<ManaSpark> outgoingTransfers = Collections.newSetFromMap(new WeakHashMap<>());

	private final ArrayList<ManaSpark> transfersTowardsSelfToRegister = new ArrayList<>();

	private boolean shouldFilterTransfers = true;
	private boolean receiverWasFull = true;
	private boolean firstTick = true;

	public ManaSparkEntity(EntityType<ManaSparkEntity> type, Level world) {
		super(type, world);
	}

	public ManaSparkEntity(Level world) {
		this(BotaniaEntities.SPARK, world);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(UPGRADE, 0);
	}

	@NotNull
	@Override
	public ItemStack getPickResult() {
		return new ItemStack(getSparkItem());
	}

	@Override
	public void tick() {
		if (level().isClientSide) {
			return;
		}

		// When loaded, initialize transfers
		if (firstTick) {
			updateTransfers();
		}

		SparkAttachable tile = getAttachedTile();
		if (tile == null) {
			dropAndKill();
			return;
		}
		var receiver = getAttachedManaReceiver();

		SparkUpgradeType upgrade = getUpgrade();
		Collection<ManaSpark> transfers = getOutgoingTransfers();

		switch (upgrade) {
			case DISPERSIVE -> {
				AABB aabb = VecHelper.boxForRange(
						this.position().with(Direction.Axis.Y, getY() + (getBbHeight() / 2.0)),
						SparkHelper.SPARK_SCAN_RANGE);
				List<Player> players = level().getEntitiesOfClass(Player.class, aabb, EntitySelector.ENTITY_STILL_ALIVE);

				Map<Player, Map<ManaItem, Integer>> receivingPlayers = new HashMap<>();

				ItemStack input = new ItemStack(getSparkItem());
				for (Player player : players) {
					List<ItemStack> stacks = new ArrayList<>();
					stacks.addAll(player.getInventory().items);
					stacks.addAll(player.getInventory().armor);

					Container inv = BotaniaAPI.instance().getAccessoriesInventory(player);
					for (int i = 0; i < inv.getContainerSize(); i++) {
						stacks.add(inv.getItem(i));
					}

					for (ItemStack stack : stacks) {
						var manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
						if (stack.isEmpty() || manaItem == null) {
							continue;
						}

						if (manaItem.canReceiveManaFromItem(input)) {
							Map<ManaItem, Integer> receivingStacks;
							boolean add = false;
							if (!receivingPlayers.containsKey(player)) {
								add = true;
								receivingStacks = new HashMap<>();
							} else {
								receivingStacks = receivingPlayers.get(player);
							}

							int recv = Math.min(receiver.getCurrentMana(), Math.min(TRANSFER_RATE, manaItem.getMaxMana() - manaItem.getMana()));
							if (recv > 0) {
								receivingStacks.put(manaItem, recv);
								if (add) {
									receivingPlayers.put(player, receivingStacks);
								}
							}
						}
					}
				}

				if (!receivingPlayers.isEmpty()) {
					List<Player> keys = new ArrayList<>(receivingPlayers.keySet());
					Collections.shuffle(keys);
					Player player = keys.iterator().next();

					Map<ManaItem, Integer> items = receivingPlayers.get(player);
					var e = items.entrySet().iterator().next();
					ManaItem manaItem = e.getKey();
					int cost = e.getValue();
					int manaToPut = Math.min(receiver.getCurrentMana(), cost);
					manaItem.addMana(manaToPut);
					receiver.receiveMana(-manaToPut);
					particlesTowards(player);
				}

			}
			case DOMINANT -> {
				if (receiverWasFull && !receiver.isFull()) {
					updateTransfers();
				}
				if (!transfersTowardsSelfToRegister.isEmpty()) {
					transfersTowardsSelfToRegister.remove(transfersTowardsSelfToRegister.size() - 1).registerTransfer(this);
				}
			}
			// Recessive does not need to be handled because recessive sparks get notified in all relevant cases
			default -> {
				if (receiverWasFull && !receiver.isFull()) {
					notifyOthers(getNetwork());
				}
			}
		}

		if (receiver != null) {
			receiverWasFull = receiver.isFull();
		} else {
			receiverWasFull = true;
		}

		if (!transfers.isEmpty()) {
			int manaTotal = Math.min(TRANSFER_RATE * transfers.size(), receiver.getCurrentMana());
			int count = transfers.size();
			int manaSpent = 0;

			if (manaTotal > 0) {
				if (shouldFilterTransfers) {
					filterTransfers();
					shouldFilterTransfers = false;
				}

				for (ManaSpark spark : transfers) {
					count--;
					SparkAttachable attached = spark.getAttachedTile();
					var attachedReceiver = spark.getAttachedManaReceiver();
					if (attached == null || attachedReceiver == null || attachedReceiver.isFull() || spark.areIncomingTransfersDone()) {
						shouldFilterTransfers = true;
						continue;
					}

					int spend = Math.min(attached.getAvailableSpaceForMana(), (manaTotal - manaSpent) / (count + 1));
					attachedReceiver.receiveMana(spend);
					manaSpent += spend;

					particlesTowards(spark.entity());
				}
				receiver.receiveMana(-manaSpent);
			}
		}

		firstTick = false;
	}

	@Override
	public void updateTransfers() {
		transfersTowardsSelfToRegister.clear();
		switch (getUpgrade()) {
			case RECESSIVE -> {
				var otherSparks = SparkHelper.getSparksAround(level(), getX(), getY() + (getBbHeight() / 2), getZ(), getNetwork());
				Collections.shuffle(otherSparks);
				for (var otherSpark : otherSparks) {
					SparkUpgradeType otherUpgrade = otherSpark.getUpgrade();
					if (otherSpark != this
							&& otherUpgrade != SparkUpgradeType.DOMINANT
							&& otherUpgrade != SparkUpgradeType.RECESSIVE
							&& otherUpgrade != SparkUpgradeType.ISOLATED) {
						outgoingTransfers.add(otherSpark);
					}
				}
			}
			case DOMINANT -> {
				List<ManaSpark> validSparks = SparkHelper.getSparksAround(level(), getX(), getY() + (getBbHeight() / 2), getZ(), getNetwork());
				for (var spark : validSparks) {
					SparkUpgradeType otherUpgrade = spark.getUpgrade();
					if (spark != this && otherUpgrade == SparkUpgradeType.NONE && spark.getAttachedManaReceiver() instanceof ManaPool) {
						transfersTowardsSelfToRegister.add(spark);
					}
				}
				Collections.shuffle(transfersTowardsSelfToRegister);
			}
		}
		filterTransfers();
	}

	private void particlesTowards(Entity e) {
		XplatAbstractions.INSTANCE.sendToTracking(this, new BotaniaEffectPacket(EffectType.SPARK_MANA_FLOW, getX(), getY(), getZ(),
				getId(), e.getId(), ColorHelper.getColorValue(getNetwork())));
	}

	public static void particleBeam(Player player, Entity e1, Entity e2) {
		if (e1 != null && e2 != null && !e1.level().isClientSide) {
			XplatAbstractions.INSTANCE.sendToPlayer(player, new BotaniaEffectPacket(EffectType.SPARK_NET_INDICATOR,
					e1.getX(), e1.getY(), e1.getZ(),
					e1.getId(), e2.getId()));
		}
	}

	protected Item getSparkItem() {
		return BotaniaItems.spark;
	}

	private void dropAndKill() {
		SparkUpgradeType upgrade = getUpgrade();
		spawnAtLocation(new ItemStack(getSparkItem()), 0F);
		if (upgrade != SparkUpgradeType.NONE) {
			spawnAtLocation(SparkAugmentItem.getByType(upgrade), 0F);
		}
		discard();
	}

	@Override
	public void remove(RemovalReason removalReason) {
		super.remove(removalReason);
		notifyOthers(getNetwork());
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (isAlive() && !stack.isEmpty()) {
			SparkUpgradeType upgrade = getUpgrade();
			if (stack.getItem() instanceof WandOfTheForestItem) {
				if (!level().isClientSide) {
					if (player.isShiftKeyDown()) {
						if (upgrade != SparkUpgradeType.NONE) {
							spawnAtLocation(SparkAugmentItem.getByType(upgrade), 0F);
							setUpgrade(SparkUpgradeType.NONE);

							// Recalculate transfers, recessive and dominant will register the proper transfers
							outgoingTransfers.clear();
							notifyOthers(getNetwork());
						} else {
							dropAndKill();
						}
					} else {
						SparkHelper.getSparksAround(level(), getX(), getY() + (getBbHeight() / 2), getZ(), getNetwork())
								.forEach(s -> particleBeam(player, this, s.entity()));
					}
				}

				return InteractionResult.sidedSuccess(level().isClientSide);
			} else if (stack.getItem() instanceof SparkAugmentItem newUpgrade && upgrade == SparkUpgradeType.NONE) {
				if (!level().isClientSide) {
					setUpgrade(newUpgrade.type);
					stack.shrink(1);
				}
				return InteractionResult.sidedSuccess(level().isClientSide);
			} else if (stack.is(BotaniaItems.phantomInk)) {
				if (!level().isClientSide) {
					setInvisible(true);
				}
				return InteractionResult.sidedSuccess(level().isClientSide);
			} else if (stack.getItem() instanceof DyeItem dye) {
				DyeColor color = dye.getDyeColor();
				if (color != getNetwork()) {
					if (!level().isClientSide) {
						setNetwork(color);
						stack.shrink(1);
					}
					return InteractionResult.sidedSuccess(level().isClientSide);
				}
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag cmp) {
		super.readAdditionalSaveData(cmp);
		setUpgrade(SparkUpgradeType.values()[cmp.getInt(TAG_UPGRADE)]);
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag cmp) {
		super.addAdditionalSaveData(cmp);
		cmp.putInt(TAG_UPGRADE, getUpgrade().ordinal());
	}

	@Override
	public SparkAttachable getAttachedTile() {
		return XplatAbstractions.INSTANCE.findSparkAttachable(level(), getAttachPos(), level().getBlockState(getAttachPos()), level().getBlockEntity(getAttachPos()), Direction.UP);
	}

	@Nullable
	@Override
	public ManaReceiver getAttachedManaReceiver() {
		return XplatAbstractions.INSTANCE.findManaReceiver(level(), getAttachPos(), Direction.UP);
	}

	private void filterTransfers() {
		Iterator<ManaSpark> iter = outgoingTransfers.iterator();
		while (iter.hasNext()) {
			ManaSpark spark = iter.next();
			SparkUpgradeType upgr = getUpgrade();
			SparkUpgradeType supgr = spark.getUpgrade();
			ManaReceiver arecv = spark.getAttachedManaReceiver();

			if (spark == this
					|| !((Entity) spark).isAlive()
					|| spark.areIncomingTransfersDone()
					|| getNetwork() != spark.getNetwork()
					|| arecv == null
					|| arecv.isFull()
					|| !(upgr == SparkUpgradeType.NONE && supgr == SparkUpgradeType.DOMINANT
							|| upgr == SparkUpgradeType.RECESSIVE && (supgr == SparkUpgradeType.NONE || supgr == SparkUpgradeType.DISPERSIVE)
							|| !(arecv instanceof ManaPool))) {
				iter.remove();
			}
		}
	}

	@Override
	public Collection<ManaSpark> getOutgoingTransfers() {
		return outgoingTransfers;
	}

	private boolean hasTransfer(ManaSpark entity) {
		return outgoingTransfers.contains(entity);
	}

	@Override
	public void registerTransfer(ManaSpark entity) {
		if (hasTransfer(entity)) {
			return;
		}
		outgoingTransfers.add(entity);
		filterTransfers();
	}

	private void notifyOthers(DyeColor network) {
		for (var spark : SparkHelper.getSparksAround(level(), getX(), getY() + (getBbHeight() / 2), getZ(), network)) {
			spark.updateTransfers();
		}
	}

	@Override
	public SparkUpgradeType getUpgrade() {
		return SparkUpgradeType.values()[entityData.get(UPGRADE)];
	}

	@Override
	public void setUpgrade(SparkUpgradeType upgrade) {
		entityData.set(UPGRADE, upgrade.ordinal());
		updateTransfers();
		notifyOthers(getNetwork());
	}

	@Override
	public void setNetwork(DyeColor color) {
		// The previous network needs to filter this spark out
		var previousNetwork = getNetwork();
		super.setNetwork(color);
		updateTransfers();
		notifyOthers(color);
		notifyOthers(previousNetwork);
	}

	@Override
	public boolean areIncomingTransfersDone() {
		if (getAttachedManaReceiver() instanceof ManaPool) {
			return false;
		}

		SparkAttachable attachable = getAttachedTile();
		return attachable != null && attachable.areIncomingTranfersDone();
	}

	public record WandHud(ManaSparkEntity entity) implements WandHUD {
		@Override
		public void renderHUD(GuiGraphics gui, Minecraft mc) {
			ItemStack sparkStack = new ItemStack(entity.getSparkItem());
			ItemStack augmentStack = SparkAugmentItem.getByType(entity.getUpgrade());
			DyeColor networkColor = entity.getNetwork();
			Component networkColorName = Component.translatable("color.minecraft." + networkColor.getName())
					.withStyle(ChatFormatting.ITALIC);
			int textColor = ColorHelper.getColorLegibleOnGrayBackground(networkColor);

			int width = 4 + Collections.max(Arrays.asList(
					mc.font.width(networkColorName),
					RenderHelper.itemWithNameWidth(mc, sparkStack),
					RenderHelper.itemWithNameWidth(mc, augmentStack)
			));
			int height = augmentStack.isEmpty() ? 30 : 50;
			int networkColorTextStart = mc.font.width(networkColorName) / 2;

			int centerX = mc.getWindow().getGuiScaledWidth() / 2;
			int centerY = mc.getWindow().getGuiScaledHeight() / 2;

			RenderHelper.renderHUDBox(gui, centerX - width / 2, centerY + 8, centerX + width / 2, centerY + 8 + height);

			RenderHelper.renderItemWithNameCentered(gui, mc, sparkStack, centerY + 10, textColor);
			RenderHelper.renderItemWithNameCentered(gui, mc, augmentStack, centerY + 28, textColor);
			gui.drawString(mc.font, networkColorName, centerX - networkColorTextStart, centerY + (augmentStack.isEmpty() ? 28 : 46), textColor);
		}
	}
}
