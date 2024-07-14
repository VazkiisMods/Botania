package vazkii.botania.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

import vazkii.botania.api.block.ExoflameHeatable;
import vazkii.botania.api.block.HornHarvestable;
import vazkii.botania.api.block.HourglassTrigger;
import vazkii.botania.api.block.Wandable;
import vazkii.botania.api.item.AvatarWieldable;
import vazkii.botania.api.item.BlockProvider;
import vazkii.botania.api.item.CoordBoundItem;
import vazkii.botania.api.item.Relic;
import vazkii.botania.api.mana.ManaCollisionGhost;
import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.api.mana.ManaTrigger;
import vazkii.botania.api.mana.spark.SparkAttachable;

public final class BotaniaForgeCapabilities {
	public static final Capability<AvatarWieldable> AVATAR_WIELDABLE = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<BlockProvider> BLOCK_PROVIDER = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<CoordBoundItem> COORD_BOUND_ITEM = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<ManaItem> MANA_ITEM = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<Relic> RELIC = CapabilityManager.get(new CapabilityToken<>() {});

	public static final Capability<ExoflameHeatable> EXOFLAME_HEATABLE = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<HornHarvestable> HORN_HARVEST = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<HourglassTrigger> HOURGLASS_TRIGGER = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<ManaCollisionGhost> MANA_GHOST = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<ManaReceiver> MANA_RECEIVER = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<SparkAttachable> SPARK_ATTACHABLE = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<ManaTrigger> MANA_TRIGGER = CapabilityManager.get(new CapabilityToken<>() {});
	public static final Capability<Wandable> WANDABLE = CapabilityManager.get(new CapabilityToken<>() {});

	private BotaniaForgeCapabilities() {}
}
