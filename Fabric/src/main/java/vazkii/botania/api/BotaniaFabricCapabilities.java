package vazkii.botania.api;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.util.Unit;

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

public final class BotaniaFabricCapabilities {
	public static final ItemApiLookup<AvatarWieldable, Unit> AVATAR_WIELDABLE =
			ItemApiLookup.get(AvatarWieldable.ID, AvatarWieldable.class, Unit.class);
	public static final ItemApiLookup<BlockProvider, Unit> BLOCK_PROVIDER =
			ItemApiLookup.get(BlockProvider.ID, BlockProvider.class, Unit.class);
	public static final ItemApiLookup<CoordBoundItem, Unit> COORD_BOUND_ITEM =
			ItemApiLookup.get(CoordBoundItem.ID, CoordBoundItem.class, Unit.class);
	public static final ItemApiLookup<ManaItem, Unit> MANA_ITEM =
			ItemApiLookup.get(ManaItem.ID, ManaItem.class, Unit.class);
	public static final ItemApiLookup<Relic, Unit> RELIC =
			ItemApiLookup.get(Relic.ID, Relic.class, Unit.class);

	public static final BlockApiLookup<ExoflameHeatable, Unit> EXOFLAME_HEATABLE =
			BlockApiLookup.get(ExoflameHeatable.ID, ExoflameHeatable.class, Unit.class);
	public static final BlockApiLookup<HornHarvestable, Unit> HORN_HARVEST =
			BlockApiLookup.get(HornHarvestable.ID, HornHarvestable.class, Unit.class);
	public static final BlockApiLookup<HourglassTrigger, Unit> HOURGLASS_TRIGGER =
			BlockApiLookup.get(HourglassTrigger.ID, HourglassTrigger.class, Unit.class);
	public static final BlockApiLookup<ManaCollisionGhost, Unit> MANA_GHOST =
			BlockApiLookup.get(ManaCollisionGhost.ID, ManaCollisionGhost.class, Unit.class);
	public static final BlockApiLookup<ManaReceiver, /* @Nullable */ Direction> MANA_RECEIVER =
			BlockApiLookup.get(ManaReceiver.ID, ManaReceiver.class, Direction.class);
	public static final BlockApiLookup<SparkAttachable, Direction> SPARK_ATTACHABLE =
			BlockApiLookup.get(SparkAttachable.ID, SparkAttachable.class, Direction.class);
	public static final BlockApiLookup<ManaTrigger, Unit> MANA_TRIGGER =
			BlockApiLookup.get(ManaTrigger.ID, ManaTrigger.class, Unit.class);
	public static final BlockApiLookup<Wandable, Unit> WANDABLE =
			BlockApiLookup.get(Wandable.ID, Wandable.class, Unit.class);

	private BotaniaFabricCapabilities() {}
}
