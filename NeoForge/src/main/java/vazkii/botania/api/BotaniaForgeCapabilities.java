package vazkii.botania.api;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
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
	public static final ItemCapability<AvatarWieldable, Void> AVATAR_WIELDABLE =
			ItemCapability.createVoid(AvatarWieldable.ID, AvatarWieldable.class);

	public static final ItemCapability<BlockProvider, Void> BLOCK_PROVIDER =
			ItemCapability.createVoid(BlockProvider.ID, BlockProvider.class);
	public static final ItemCapability<CoordBoundItem, Void> COORD_BOUND_ITEM =
			ItemCapability.createVoid(CoordBoundItem.ID, CoordBoundItem.class);
	public static final ItemCapability<ManaItem, Void> MANA_ITEM =
			ItemCapability.createVoid(ManaItem.ID, ManaItem.class);
	public static final ItemCapability<Relic, Void> RELIC =
			ItemCapability.createVoid(Relic.ID, Relic.class);

	public static final BlockCapability<ExoflameHeatable, Void> EXOFLAME_HEATABLE =
			BlockCapability.createVoid(ExoflameHeatable.ID, ExoflameHeatable.class);

	public static final BlockCapability<HornHarvestable, Void> HORN_HARVEST =
			BlockCapability.createVoid(HornHarvestable.ID, HornHarvestable.class);
	public static final BlockCapability<HourglassTrigger, Void> HOURGLASS_TRIGGER =
			BlockCapability.createVoid(HourglassTrigger.ID, HourglassTrigger.class);
	public static final BlockCapability<ManaCollisionGhost, Void> MANA_GHOST =
			BlockCapability.createVoid(ManaCollisionGhost.ID, ManaCollisionGhost.class);
	public static final BlockCapability<ManaReceiver, Direction> MANA_RECEIVER =
			BlockCapability.createSided(ManaReceiver.ID, ManaReceiver.class);

	public static final BlockCapability<SparkAttachable, Direction> SPARK_ATTACHABLE =
			BlockCapability.createSided(SparkAttachable.ID, SparkAttachable.class);
	public static final BlockCapability<ManaTrigger, Void> MANA_TRIGGER =
			BlockCapability.createVoid(ManaTrigger.ID, ManaTrigger.class);
	public static final BlockCapability<Wandable, Void> WANDABLE =
			BlockCapability.createVoid(Wandable.ID, Wandable.class);

	private BotaniaForgeCapabilities() {}
}
