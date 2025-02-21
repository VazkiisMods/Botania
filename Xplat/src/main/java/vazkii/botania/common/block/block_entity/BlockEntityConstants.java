package vazkii.botania.common.block.block_entity;

import com.google.common.collect.ImmutableSet;

import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.block.entity.BlockEntityType;

import vazkii.botania.api.block.Wandable;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.api.mana.ManaTrigger;
import vazkii.botania.api.mana.spark.SparkAttachable;
import vazkii.botania.common.block.BotaniaFlowerBlocks;

import java.util.Set;

public final class BlockEntityConstants {
	public static final Set<BlockEntityType<? extends Wandable>> SELF_WANDABLE_BES = ImmutableSet.of(
			BotaniaBlockEntities.ALF_PORTAL, BotaniaBlockEntities.ANIMATED_TORCH, BotaniaBlockEntities.CORPOREA_CRYSTAL_CUBE, BotaniaBlockEntities.CORPOREA_RETAINER,
			BotaniaBlockEntities.CRAFT_CRATE, BotaniaBlockEntities.ENCHANTER, BotaniaBlockEntities.HOURGLASS, BotaniaBlockEntities.PLATFORM, BotaniaBlockEntities.POOL,
			BotaniaBlockEntities.RUNE_ALTAR, BotaniaBlockEntities.SPREADER, BotaniaBlockEntities.TURNTABLE,
			BotaniaFlowerBlocks.DAFFOMILL, BotaniaFlowerBlocks.HOPPERHOCK, BotaniaFlowerBlocks.HOPPERHOCK_CHIBI,
			BotaniaFlowerBlocks.POLLIDISIAC, BotaniaFlowerBlocks.RANNUNCARPUS, BotaniaFlowerBlocks.RANNUNCARPUS_CHIBI
	);

	public static final Set<BlockEntityType<? extends ManaTrigger>> SELF_MANA_TRIGGER_BES = ImmutableSet.of(
			BotaniaBlockEntities.ANIMATED_TORCH, BotaniaBlockEntities.HOURGLASS, BotaniaBlockEntities.PRISM
	);

	public static final Set<BlockEntityType<? extends ManaReceiver>> SELF_MANA_RECEIVER_BES = ImmutableSet.of(
			BotaniaBlockEntities.AVATAR, BotaniaBlockEntities.BREWERY, BotaniaBlockEntities.DISTRIBUTOR, BotaniaBlockEntities.ENCHANTER,
			BotaniaBlockEntities.POOL, BotaniaBlockEntities.FLUXFIELD, BotaniaBlockEntities.RUNE_ALTAR,
			BotaniaBlockEntities.SPAWNER_CLAW, BotaniaBlockEntities.SPREADER, BotaniaBlockEntities.TERRA_PLATE
	);

	public static final Set<BlockEntityType<? extends SparkAttachable>> SELF_SPARK_ATTACHABLE_BES = ImmutableSet.of(
			BotaniaBlockEntities.ENCHANTER, BotaniaBlockEntities.POOL, BotaniaBlockEntities.TERRA_PLATE
	);

	public static final Set<BlockEntityType<? extends WorldlyContainer>> SELF_WORLDLY_CONTAINERS = ImmutableSet.of(
			BotaniaBlockEntities.PRISM, BotaniaBlockEntities.SPREADER, BotaniaBlockEntities.HOURGLASS,
			BotaniaBlockEntities.INCENSE_PLATE, BotaniaBlockEntities.OPEN_CRATE, BotaniaBlockEntities.SPARK_CHANGER,
			BotaniaBlockEntities.TINY_POTATO
	);

	private BlockEntityConstants() {}
}
