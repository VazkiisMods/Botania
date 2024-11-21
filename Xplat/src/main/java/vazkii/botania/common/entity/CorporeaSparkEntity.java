/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.entity;

import com.google.common.base.Predicates;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.block.WandHUD;
import vazkii.botania.api.corporea.CorporeaNode;
import vazkii.botania.api.corporea.CorporeaSpark;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.impl.corporea.DummyCorporeaNode;
import vazkii.botania.common.integration.corporea.CorporeaNodeDetectors;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.WandOfTheForestItem;
import vazkii.botania.common.lib.BotaniaTags;

import java.util.*;

public class CorporeaSparkEntity extends SparkBaseEntity implements CorporeaSpark {
	private static final int SCAN_RANGE = 8;

	private static final String TAG_MASTER = "master";
	private static final String TAG_CREATIVE = "creative";

	private static final EntityDataAccessor<Boolean> MASTER = SynchedEntityData.defineId(CorporeaSparkEntity.class, EntityDataSerializers.BOOLEAN);
	private static final EntityDataAccessor<Boolean> CREATIVE = SynchedEntityData.defineId(CorporeaSparkEntity.class, EntityDataSerializers.BOOLEAN);

	private CorporeaSpark master;
	private Set<CorporeaSpark> connections = new LinkedHashSet<>();
	private List<CorporeaSpark> relatives = new ArrayList<>();
	private boolean firstTick = true;

	public CorporeaSparkEntity(EntityType<CorporeaSparkEntity> type, Level world) {
		super(type, world);
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(MASTER, false);
		builder.define(CREATIVE, false);
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

		CorporeaNode node = getSparkNode();
		if (node instanceof DummyCorporeaNode && !level().getBlockState(getAttachPos()).is(BotaniaTags.Blocks.CORPOREA_SPARK_OVERRIDE)) {
			dropAndKill();
			return;
		}

		if (isMaster()) {
			master = this;
		}

		if (firstTick) {
			if (isMaster()) {
				restartNetwork();
			} else {
				findNetwork();
			}

			firstTick = false;
		}

		if (master != null && (!master.entity().isAlive() || master.getNetwork() != getNetwork())) {
			master = null;
		}
	}

	private void dropAndKill() {
		spawnAtLocation(new ItemStack(getSparkItem()), 0F);
		discard();
	}

	protected Item getSparkItem() {
		return isCreative() ? BotaniaItems.corporeaSparkCreative : isMaster() ? BotaniaItems.corporeaSparkMaster : BotaniaItems.corporeaSpark;
	}

	@Override
	public void remove(RemovalReason reason) {
		super.remove(reason);
		connections.remove(this);
		restartNetwork();
	}

	@Override
	public void introduceNearbyTo(Set<CorporeaSpark> network, CorporeaSpark master) {
		relatives.clear();
		for (CorporeaSpark spark : getNearbySparks()) {
			if (spark == null || network.contains(spark)
					|| spark.getNetwork() != getNetwork()
					|| spark.isMaster() || !spark.entity().isAlive()) {
				continue;
			}

			network.add(spark);
			relatives.add(spark);
			spark.introduceNearbyTo(network, master);
		}

		this.master = master;
		this.connections = network;
	}

	@SuppressWarnings("unchecked")
	private List<CorporeaSpark> getNearbySparks() {
		return (List) level().getEntitiesOfClass(Entity.class, new AABB(getX() - SCAN_RANGE, getY() - SCAN_RANGE, getZ() - SCAN_RANGE, getX() + SCAN_RANGE, getY() + SCAN_RANGE, getZ() + SCAN_RANGE), Predicates.instanceOf(CorporeaSpark.class));
	}

	private void restartNetwork() {
		connections = new LinkedHashSet<>();
		relatives = new ArrayList<>();

		if (master != null) {
			CorporeaSpark oldMaster = master;
			master = null;

			oldMaster.introduceNearbyTo(new LinkedHashSet<>(), oldMaster);
		}
	}

	private void findNetwork() {
		for (CorporeaSpark spark : getNearbySparks()) {
			if (spark.getNetwork() == getNetwork() && spark.entity().isAlive()) {
				CorporeaSpark master = spark.getMaster();
				if (master != null) {
					this.master = master;
					restartNetwork();

					break;
				}
			}
		}
	}

	private static void displayRelatives(Player player, List<CorporeaSpark> checked, CorporeaSpark spark) {
		if (spark == null) {
			return;
		}

		List<CorporeaSpark> sparks = spark.getRelatives();
		if (sparks.isEmpty()) {
			ManaSparkEntity.particleBeam(player, spark.entity(), spark.getMaster().entity());
		} else {
			for (CorporeaSpark endSpark : sparks) {
				if (!checked.contains(endSpark)) {
					ManaSparkEntity.particleBeam(player, spark.entity(), endSpark.entity());
					checked.add(endSpark);
					displayRelatives(player, checked, endSpark);
				}
			}
		}
	}

	@Override
	public CorporeaNode getSparkNode() {
		return CorporeaNodeDetectors.findNode(level(), this);
	}

	@Override
	public Set<CorporeaSpark> getConnections() {
		return connections;
	}

	@Override
	public List<CorporeaSpark> getRelatives() {
		return relatives;
	}

	@Override
	public void onItemExtracted(ItemStack stack) {
		((ServerLevel) level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), getX(), getY(), getZ(), 10, 0.125, 0.125, 0.125, 0.05);
	}

	@Override
	public void onItemsRequested(List<ItemStack> stacks) {
		List<Item> shownItems = new ArrayList<>();
		for (ItemStack stack : stacks) {
			if (!shownItems.contains(stack.getItem())) {
				shownItems.add(stack.getItem());
				((ServerLevel) level()).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack), getX(), getY(), getZ(), 10, 0.125, 0.125, 0.125, 0.05);
			}
		}
	}

	@Override
	public CorporeaSpark getMaster() {
		return master;
	}

	public void setMaster(boolean master) {
		entityData.set(MASTER, master);
	}

	@Override
	public boolean isMaster() {
		return entityData.get(MASTER);
	}

	public void setCreative(boolean creative) {
		entityData.set(CREATIVE, creative);
	}

	@Override
	public boolean isCreative() {
		return entityData.get(CREATIVE);
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (isAlive() && !stack.isEmpty()) {
			if (stack.getItem() instanceof WandOfTheForestItem) {
				if (!level().isClientSide) {
					if (player.isShiftKeyDown()) {
						dropAndKill();
						if (isMaster()) {
							restartNetwork();
						}
					} else {
						displayRelatives(player, new ArrayList<>(), master);
					}
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
			} else if (stack.is(BotaniaItems.phantomInk)) {
				if (!level().isClientSide) {
					setInvisible(true);
				}
				return InteractionResult.sidedSuccess(level().isClientSide);
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public void setNetwork(DyeColor color) {
		if (color == getNetwork()) {
			return;
		}

		super.setNetwork(color);

		// Do not access world during deserialization
		if (!firstTick) {
			if (isMaster()) {
				restartNetwork();
			} else {
				findNetwork();
			}
		}
	}

	@Override
	protected void readAdditionalSaveData(@NotNull CompoundTag cmp) {
		super.readAdditionalSaveData(cmp);
		setMaster(cmp.getBoolean(TAG_MASTER));
		setCreative(cmp.getBoolean(TAG_CREATIVE));
	}

	@Override
	protected void addAdditionalSaveData(@NotNull CompoundTag cmp) {
		super.addAdditionalSaveData(cmp);
		cmp.putBoolean(TAG_MASTER, isMaster());
		cmp.putBoolean(TAG_CREATIVE, isCreative());
	}

	public record WandHud(CorporeaSparkEntity entity) implements WandHUD {
		@Override
		public void renderHUD(GuiGraphics gui, Minecraft mc) {
			ItemStack sparkStack = new ItemStack(entity.getSparkItem());
			DyeColor networkColor = entity.getNetwork();
			Component networkColorName = Component.translatable("color.minecraft." + networkColor.getName())
					.withStyle(ChatFormatting.ITALIC);
			int textColor = ColorHelper.getColorLegibleOnGrayBackground(networkColor);

			int width = 4 + Math.max(
					mc.font.width(networkColorName),
					RenderHelper.itemWithNameWidth(mc, sparkStack)
			);
			int networkColorTextStart = mc.font.width(networkColorName) / 2;

			int centerX = mc.getWindow().getGuiScaledWidth() / 2;
			int centerY = mc.getWindow().getGuiScaledHeight() / 2;

			RenderHelper.renderHUDBox(gui, centerX - width / 2, centerY + 8, centerX + width / 2, centerY + 38);

			RenderHelper.renderItemWithNameCentered(gui, mc, sparkStack, centerY + 10, textColor);
			gui.drawString(mc.font, networkColorName, centerX - networkColorTextStart, centerY + 28, textColor);
		}
	}

}
