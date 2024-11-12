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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.api.block.WandHUD;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.corporea.*;
import vazkii.botania.common.block.block_entity.mana.*;
import vazkii.botania.common.block.block_entity.red_string.*;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.lib.LibBlockNames;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static vazkii.botania.common.block.BotaniaBlocks.*;
import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class BotaniaBlockEntities {
	private static final Map<ResourceLocation, BlockEntityType<?>> ALL = new HashMap<>();
	public static final BlockEntityType<PetalApothecaryBlockEntity> ALTAR = type(prefix(LibBlockNames.ALTAR), PetalApothecaryBlockEntity::new,
			defaultAltar, forestAltar, plainsAltar, mountainAltar, fungalAltar,
			swampAltar, desertAltar, taigaAltar, mesaAltar, mossyAltar,
			livingrockAltar, deepslateAltar
	);
	public static final BlockEntityType<ManaSpreaderBlockEntity> SPREADER = type(prefix(LibBlockNames.SPREADER), ManaSpreaderBlockEntity::new, manaSpreader, redstoneSpreader, elvenSpreader, gaiaSpreader);
	public static final BlockEntityType<ManaPoolBlockEntity> POOL = type(prefix(LibBlockNames.POOL), ManaPoolBlockEntity::new, manaPool, dilutedPool, fabulousPool, creativePool);
	public static final BlockEntityType<RunicAltarBlockEntity> RUNE_ALTAR = type(prefix(LibBlockNames.RUNE_ALTAR), RunicAltarBlockEntity::new, runeAltar);
	public static final BlockEntityType<PylonBlockEntity> PYLON = type(prefix(LibBlockNames.PYLON), PylonBlockEntity::new, manaPylon, naturaPylon, gaiaPylon);
	public static final BlockEntityType<ManaSplitterBlockEntity> DISTRIBUTOR = type(prefix(LibBlockNames.DISTRIBUTOR), ManaSplitterBlockEntity::new, distributor);
	public static final BlockEntityType<ManaEnchanterBlockEntity> ENCHANTER = type(prefix(LibBlockNames.ENCHANTER), ManaEnchanterBlockEntity::new, enchanter);
	public static final BlockEntityType<SpreaderTurntableBlockEntity> TURNTABLE = type(prefix(LibBlockNames.TURNTABLE), SpreaderTurntableBlockEntity::new, turntable);
	public static final BlockEntityType<TinyPlanetBlockEntity> TINY_PLANET = type(prefix(LibBlockNames.TINY_PLANET), TinyPlanetBlockEntity::new, tinyPlanet);
	public static final BlockEntityType<OpenCrateBlockEntity> OPEN_CRATE = type(prefix(LibBlockNames.OPEN_CRATE), OpenCrateBlockEntity::new, openCrate);
	public static final BlockEntityType<CraftyCrateBlockEntity> CRAFT_CRATE = type(prefix(LibBlockNames.CRAFT_CRATE), CraftyCrateBlockEntity::new, craftCrate);
	public static final BlockEntityType<EyeOfTheAncientsBlockEntity> FOREST_EYE = type(prefix(LibBlockNames.FOREST_EYE), EyeOfTheAncientsBlockEntity::new, forestEye);
	public static final BlockEntityType<PlatformBlockEntity> PLATFORM = type(prefix(LibBlockNames.PLATFORM), PlatformBlockEntity::new, abstrusePlatform, spectralPlatform, infrangiblePlatform);
	public static final BlockEntityType<AlfheimPortalBlockEntity> ALF_PORTAL = type(prefix(LibBlockNames.ALF_PORTAL), AlfheimPortalBlockEntity::new, alfPortal);
	public static final BlockEntityType<BifrostBlockEntity> BIFROST = type(prefix(LibBlockNames.BIFROST), BifrostBlockEntity::new, bifrost);
	public static final BlockEntityType<FloatingFlowerBlockEntity> MINI_ISLAND = type(prefix(LibBlockNames.MINI_ISLAND), FloatingFlowerBlockEntity::new, ColorHelper.supportedColors().map(BotaniaBlocks::getFloatingFlower).toArray(Block[]::new));
	public static final BlockEntityType<TinyPotatoBlockEntity> TINY_POTATO = type(prefix(LibBlockNames.TINY_POTATO), TinyPotatoBlockEntity::new, tinyPotato);
	public static final BlockEntityType<LifeImbuerBlockEntity> SPAWNER_CLAW = type(prefix(LibBlockNames.SPAWNER_CLAW), LifeImbuerBlockEntity::new, spawnerClaw);
	public static final BlockEntityType<EnderOverseerBlockEntity> ENDER_EYE = type(prefix(LibBlockNames.ENDER_EYE_BLOCK), EnderOverseerBlockEntity::new, enderEye);
	public static final BlockEntityType<StarfieldCreatorBlockEntity> STARFIELD = type(prefix(LibBlockNames.STARFIELD), StarfieldCreatorBlockEntity::new, starfield);
	public static final BlockEntityType<PowerGeneratorBlockEntity> FLUXFIELD = type(prefix(LibBlockNames.FLUXFIELD), PowerGeneratorBlockEntity::new, rfGenerator);
	public static final BlockEntityType<BreweryBlockEntity> BREWERY = type(prefix(LibBlockNames.BREWERY), BreweryBlockEntity::new, brewery);
	public static final BlockEntityType<TerrestrialAgglomerationPlateBlockEntity> TERRA_PLATE = type(prefix(LibBlockNames.TERRA_PLATE), TerrestrialAgglomerationPlateBlockEntity::new, terraPlate);
	public static final BlockEntityType<RedStringContainerBlockEntity> RED_STRING_CONTAINER = type(prefix(LibBlockNames.RED_STRING_CONTAINER), XplatAbstractions.INSTANCE::newRedStringContainer, redStringContainer);
	public static final BlockEntityType<RedStringDispenserBlockEntity> RED_STRING_DISPENSER = type(prefix(LibBlockNames.RED_STRING_DISPENSER), RedStringDispenserBlockEntity::new, redStringDispenser);
	public static final BlockEntityType<RedStringNutrifierBlockEntity> RED_STRING_FERTILIZER = type(prefix(LibBlockNames.RED_STRING_FERTILIZER), RedStringNutrifierBlockEntity::new, redStringFertilizer);
	public static final BlockEntityType<RedStringComparatorBlockEntity> RED_STRING_COMPARATOR = type(prefix(LibBlockNames.RED_STRING_COMPARATOR), RedStringComparatorBlockEntity::new, redStringComparator);
	public static final BlockEntityType<RedStringSpooferBlockEntity> RED_STRING_RELAY = type(prefix(LibBlockNames.RED_STRING_RELAY), RedStringSpooferBlockEntity::new, redStringRelay);
	public static final BlockEntityType<ManaFlameBlockEntity> MANA_FLAME = type(prefix(LibBlockNames.MANA_FLAME), ManaFlameBlockEntity::new, manaFlame);
	public static final BlockEntityType<ManaPrismBlockEntity> PRISM = type(prefix(LibBlockNames.PRISM), ManaPrismBlockEntity::new, prism);
	public static final BlockEntityType<CorporeaIndexBlockEntity> CORPOREA_INDEX = type(prefix(LibBlockNames.CORPOREA_INDEX), CorporeaIndexBlockEntity::new, corporeaIndex);
	public static final BlockEntityType<CorporeaFunnelBlockEntity> CORPOREA_FUNNEL = type(prefix(LibBlockNames.CORPOREA_FUNNEL), CorporeaFunnelBlockEntity::new, corporeaFunnel);
	public static final BlockEntityType<ManaPumpBlockEntity> PUMP = type(prefix(LibBlockNames.PUMP), ManaPumpBlockEntity::new, pump);
	public static final BlockEntityType<FakeAirBlockEntity> FAKE_AIR = type(prefix(LibBlockNames.FAKE_AIR), FakeAirBlockEntity::new, fakeAir);
	public static final BlockEntityType<CorporeaInterceptorBlockEntity> CORPOREA_INTERCEPTOR = type(prefix(LibBlockNames.CORPOREA_INTERCEPTOR), CorporeaInterceptorBlockEntity::new, corporeaInterceptor);
	public static final BlockEntityType<CorporeaCrystalCubeBlockEntity> CORPOREA_CRYSTAL_CUBE = type(prefix(LibBlockNames.CORPOREA_CRYSTAL_CUBE), CorporeaCrystalCubeBlockEntity::new, corporeaCrystalCube);
	public static final BlockEntityType<IncensePlateBlockEntity> INCENSE_PLATE = type(prefix(LibBlockNames.INCENSE_PLATE), IncensePlateBlockEntity::new, incensePlate);
	public static final BlockEntityType<HoveringHourglassBlockEntity> HOURGLASS = type(prefix(LibBlockNames.HOURGLASS), HoveringHourglassBlockEntity::new, hourglass);
	public static final BlockEntityType<SparkTinkererBlockEntity> SPARK_CHANGER = type(prefix(LibBlockNames.SPARK_CHANGER), SparkTinkererBlockEntity::new, sparkChanger);
	public static final BlockEntityType<CocoonBlockEntity> COCOON = type(prefix(LibBlockNames.COCOON), CocoonBlockEntity::new, cocoon);
	public static final BlockEntityType<LuminizerBlockEntity> LIGHT_RELAY = type(prefix(LibBlockNames.LIGHT_RELAY), LuminizerBlockEntity::new, lightRelayDefault, lightRelayDetector, lightRelayToggle, lightRelayFork);
	public static final BlockEntityType<CacophoniumBlockEntity> CACOPHONIUM = type(prefix(LibBlockNames.CACOPHONIUM), CacophoniumBlockEntity::new, cacophonium);
	public static final BlockEntityType<BellowsBlockEntity> BELLOWS = type(prefix(LibBlockNames.BELLOWS), BellowsBlockEntity::new, bellows);
	public static final BlockEntityType<CellularBlockEntity> CELL_BLOCK = type(prefix(LibBlockNames.CELL_BLOCK), CellularBlockEntity::new, cellBlock);
	public static final BlockEntityType<RedStringInterceptorBlockEntity> RED_STRING_INTERCEPTOR = type(prefix(LibBlockNames.RED_STRING_INTERCEPTOR), RedStringInterceptorBlockEntity::new, redStringInterceptor);
	public static final BlockEntityType<GaiaHeadBlockEntity> GAIA_HEAD = type(prefix(LibBlockNames.GAIA_HEAD), GaiaHeadBlockEntity::new, gaiaHead, gaiaHeadWall);
	public static final BlockEntityType<CorporeaRetainerBlockEntity> CORPOREA_RETAINER = type(prefix(LibBlockNames.CORPOREA_RETAINER), CorporeaRetainerBlockEntity::new, corporeaRetainer);
	public static final BlockEntityType<TeruTeruBozuBlockEntity> TERU_TERU_BOZU = type(prefix(LibBlockNames.TERU_TERU_BOZU), TeruTeruBozuBlockEntity::new, teruTeruBozu);
	public static final BlockEntityType<AvatarBlockEntity> AVATAR = type(prefix(LibBlockNames.AVATAR), AvatarBlockEntity::new, avatar);
	public static final BlockEntityType<AnimatedTorchBlockEntity> ANIMATED_TORCH = type(prefix(LibBlockNames.ANIMATED_TORCH), AnimatedTorchBlockEntity::new, animatedTorch);

	private static <T extends BlockEntity> BlockEntityType<T> type(ResourceLocation id, BiFunction<BlockPos, BlockState, T> func, Block... blocks) {
		var ret = XplatAbstractions.INSTANCE.createBlockEntityType(func, blocks);
		var old = ALL.put(id, ret);
		if (old != null) {
			throw new IllegalArgumentException("Duplicate id " + id);
		}
		return ret;
	}

	public static void registerTiles(BiConsumer<BlockEntityType<?>, ResourceLocation> r) {
		for (var e : ALL.entrySet()) {
			r.accept(e.getValue(), e.getKey());
		}
	}

	public interface BECapConsumer<T> {
		void accept(Function<BlockEntity, T> factory, BlockEntityType<?>... types);
	}

	public static void registerWandHudCaps(BECapConsumer<WandHUD> consumer) {
		consumer.accept(be -> new AnimatedTorchBlockEntity.WandHud((AnimatedTorchBlockEntity) be), BotaniaBlockEntities.ANIMATED_TORCH);
		consumer.accept(be -> new BreweryBlockEntity.WandHud((BreweryBlockEntity) be), BotaniaBlockEntities.BREWERY);
		consumer.accept(be -> new CorporeaRetainerBlockEntity.WandHud((CorporeaRetainerBlockEntity) be), BotaniaBlockEntities.CORPOREA_RETAINER);
		consumer.accept(be -> new CraftyCrateBlockEntity.WandHud((CraftyCrateBlockEntity) be), BotaniaBlockEntities.CRAFT_CRATE);
		consumer.accept(be -> new ManaEnchanterBlockEntity.WandHud((ManaEnchanterBlockEntity) be), BotaniaBlockEntities.ENCHANTER);
		consumer.accept(be -> new HoveringHourglassBlockEntity.WandHud((HoveringHourglassBlockEntity) be), BotaniaBlockEntities.HOURGLASS);
		consumer.accept(be -> new ManaPoolBlockEntity.WandHud((ManaPoolBlockEntity) be), BotaniaBlockEntities.POOL);
		consumer.accept(be -> new ManaPrismBlockEntity.WandHud((ManaPrismBlockEntity) be), BotaniaBlockEntities.PRISM);
		consumer.accept(be -> new ManaSpreaderBlockEntity.WandHud((ManaSpreaderBlockEntity) be), BotaniaBlockEntities.SPREADER);
		consumer.accept(be -> new SpreaderTurntableBlockEntity.WandHud((SpreaderTurntableBlockEntity) be), BotaniaBlockEntities.TURNTABLE);
	}
}
