/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.block.*;
import vazkii.botania.common.block.decor.FloatingFlowerBlock;
import vazkii.botania.common.block.mana.DrumBlock;
import vazkii.botania.common.block.mana.ManaPoolBlock;
import vazkii.botania.common.block.mana.ManaSpreaderBlock;
import vazkii.botania.common.block.red_string.RedStringBlock;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.common.lib.LibBlockNames;
import vazkii.botania.common.lib.LibMisc;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import static vazkii.botania.common.block.BotaniaBlocks.*;
import static vazkii.botania.common.block.BotaniaFlowerBlocks.*;

public class BlockTagProvider extends IntrinsicHolderTagsProvider<Block> {
	public static final Predicate<Block> BOTANIA_BLOCK = b -> LibMisc.MOD_ID.equals(BuiltInRegistries.BLOCK.getKey(b).getNamespace());

	public BlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, Registries.BLOCK, lookupProvider, (block) -> block.builtInRegistryHolder().key());
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(BlockTags.RAILS).add(BotaniaBlocks.ghostRail);
		tag(BlockTags.SLABS).add(getModBlocks(b -> b instanceof SlabBlock));
		tag(BlockTags.WOODEN_SLABS).add(
				BotaniaBlocks.livingwoodSlab, BotaniaBlocks.livingwoodStrippedSlab, BotaniaBlocks.livingwoodPlankSlab,
				BotaniaBlocks.dreamwoodSlab, BotaniaBlocks.dreamwoodStrippedSlab, BotaniaBlocks.dreamwoodPlankSlab,
				BotaniaBlocks.shimmerwoodPlankSlab);
		tag(BlockTags.STAIRS).add(getModBlocks(b -> b instanceof StairBlock));
		tag(BlockTags.WOODEN_STAIRS).add(
				BotaniaBlocks.livingwoodStairs, BotaniaBlocks.livingwoodStrippedStairs, BotaniaBlocks.livingwoodPlankStairs,
				BotaniaBlocks.dreamwoodStairs, BotaniaBlocks.dreamwoodStrippedStairs, BotaniaBlocks.dreamwoodPlankStairs,
				BotaniaBlocks.shimmerwoodPlankStairs);
		tag(BlockTags.WALLS).add(getModBlocks(b -> b instanceof WallBlock));
		tag(BlockTags.FENCES).add(getModBlocks(b -> b instanceof FenceBlock));
		tag(BlockTags.WOODEN_FENCES).add(BotaniaBlocks.livingwoodFence, BotaniaBlocks.dreamwoodFence);
		tag(BlockTags.FENCE_GATES).add(getModBlocks(b -> b instanceof FenceGateBlock));
		tag(BlockTags.DRAGON_IMMUNE).add(BotaniaBlocks.infrangiblePlatform);
		tag(BlockTags.WITHER_IMMUNE).add(BotaniaBlocks.infrangiblePlatform);

		tag(BotaniaTags.Blocks.MUNDANE_FLOATING_FLOWERS).add(
				Arrays.stream(DyeColor.values())
						.map(BotaniaBlocks::getFloatingFlower)
						.sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey))
						.toArray(Block[]::new)
		);

		tag(BotaniaTags.Blocks.SPECIAL_FLOATING_FLOWERS).add(BuiltInRegistries.BLOCK.stream().filter(BOTANIA_BLOCK)
				.filter(b -> b instanceof FloatingSpecialFlowerBlock)
				.sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey))
				.toArray(Block[]::new)
		);

		tag(BotaniaTags.Blocks.FLOATING_FLOWERS).addTag(BotaniaTags.Blocks.MUNDANE_FLOATING_FLOWERS)
				.addTag(BotaniaTags.Blocks.SPECIAL_FLOATING_FLOWERS);

		tag(BotaniaTags.Blocks.MYSTICAL_FLOWERS).add(
				Arrays.stream(DyeColor.values())
						.map(BotaniaBlocks::getFlower)
						.sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey))
						.toArray(Block[]::new)
		);

		tag(BotaniaTags.Blocks.SHINY_FLOWERS).add(
				Arrays.stream(DyeColor.values())
						.map(BotaniaBlocks::getShinyFlower)
						.sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey))
						.toArray(Block[]::new)
		);

		tag(BotaniaTags.Blocks.DOUBLE_MYSTICAL_FLOWERS).add(
				Arrays.stream(DyeColor.values())
						.map(BotaniaBlocks::getDoubleFlower)
						.sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey))
						.toArray(Block[]::new)
		);

		tag(BotaniaTags.Blocks.MISC_SPECIAL_FLOWERS).add(manastar, pureDaisy, bergamute);
		tag(BotaniaTags.Blocks.GENERATING_SPECIAL_FLOWERS).add(
				dandelifeon, endoflame, entropinnyum,
				gourmaryllis, hydroangeas, kekimurus,
				munchdew, narslimmus, rafflowsia, rosaArcana,
				shulkMeNot, spectrolus, thermalily
		);
		tag(BotaniaTags.Blocks.FUNCTIONAL_SPECIAL_FLOWERS).add(
				agricarnation, agricarnationChibi, bellethorn, bellethornChibi,
				bubbell, bubbellChibi, clayconia, clayconiaChibi,
				daffomill, dreadthorn, exoflame, fallenKanade, heiseiDream,
				hopperhock, hopperhockChibi, hyacidus, jadedAmaranthus,
				jiyuulia, jiyuuliaChibi, labellia, loonium, marimorphosis, marimorphosisChibi,
				medumone, orechid, orechidIgnem, pollidisiac, rannuncarpus, rannuncarpusChibi,
				solegnolia, solegnoliaChibi, spectranthemum, tangleberrie, tangleberrieChibi, tigerseye, vinculotus
		);
		tag(BotaniaTags.Blocks.SPECIAL_FLOWERS).addTag(BotaniaTags.Blocks.MISC_SPECIAL_FLOWERS)
				.addTag(BotaniaTags.Blocks.GENERATING_SPECIAL_FLOWERS)
				.addTag(BotaniaTags.Blocks.FUNCTIONAL_SPECIAL_FLOWERS);

		tag(BotaniaTags.Blocks.MINI_FLOWERS).add(
				getModBlocks(b -> XplatAbstractions.INSTANCE.isSpecialFlowerBlock(b)
						&& BuiltInRegistries.BLOCK.getKey(b).getPath().endsWith("_chibi"))
		);

		tag(BotaniaTags.Blocks.ENCHANTER_FLOWERS).addTag(BotaniaTags.Blocks.MYSTICAL_FLOWERS)
				.addTag(BotaniaTags.Blocks.SHINY_FLOWERS)
				.addTag(BotaniaTags.Blocks.MUNDANE_FLOATING_FLOWERS);

		// Special flowers intentionally excluded due to unwanted behaviors with tree growth and mod compat.
		tag(BlockTags.TALL_FLOWERS).addTag(BotaniaTags.Blocks.DOUBLE_MYSTICAL_FLOWERS);
		tag(BlockTags.SMALL_FLOWERS).addTag(BotaniaTags.Blocks.MYSTICAL_FLOWERS);

		tag(BlockTags.IMPERMEABLE).add(BotaniaBlocks.elfGlass, BotaniaBlocks.manaGlass, BotaniaBlocks.bifrost, BotaniaBlocks.bifrostPerm);
		tag(BlockTags.BEACON_BASE_BLOCKS).add(BotaniaBlocks.manasteelBlock, BotaniaBlocks.terrasteelBlock, BotaniaBlocks.elementiumBlock,
				BotaniaBlocks.manaDiamondBlock, BotaniaBlocks.dragonstoneBlock);

		tag(BlockTags.DIRT).add(getModBlocks(b -> b instanceof BotaniaGrassBlock));
		tag(BotaniaTags.Blocks.BLOCKS_ELEMENTIUM).add(BotaniaBlocks.elementiumBlock);
		tag(BotaniaTags.Blocks.BLOCKS_MANASTEEL).add(BotaniaBlocks.manasteelBlock);
		tag(BotaniaTags.Blocks.BLOCKS_TERRASTEEL).add(BotaniaBlocks.terrasteelBlock);

		tag(BotaniaTags.Blocks.CORPOREA_SPARK_OVERRIDE).add(
				BotaniaBlocks.corporeaBlock, BotaniaBlocks.corporeaBrick, BotaniaBlocks.corporeaBrickSlab, BotaniaBlocks.corporeaBrickStairs,
				BotaniaBlocks.corporeaBrickWall, BotaniaBlocks.corporeaCrystalCube, BotaniaBlocks.corporeaFunnel, BotaniaBlocks.corporeaIndex,
				BotaniaBlocks.corporeaInterceptor, BotaniaBlocks.corporeaSlab, BotaniaBlocks.corporeaStairs);

		tag(BlockTags.SAND); // We aren't calling vanilla's generation, so need to add a dummy so that using this below doesn't error out.
		tag(BotaniaTags.Blocks.TERRAFORMABLE)
				.add(Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.INFESTED_STONE, Blocks.STONE, Blocks.POLISHED_ANDESITE, Blocks.POLISHED_DIORITE, Blocks.POLISHED_GRANITE)
				.add(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.MYCELIUM)
				.add(Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.SNOW)
				.addTag(BlockTags.SAND);
		tag(BotaniaTags.Blocks.GAIA_BREAK_BLACKLIST).add(Blocks.BEACON, BotaniaBlocks.manaPylon, BotaniaBlocks.naturaPylon, BotaniaBlocks.gaiaPylon);
		tag(BotaniaTags.Blocks.MAGNET_RING_BLACKLIST).add(BotaniaBlocks.manaPool, BotaniaBlocks.creativePool, BotaniaBlocks.dilutedPool,
				BotaniaBlocks.fabulousPool, BotaniaBlocks.terraPlate, BotaniaBlocks.runeAltar);
		tag(BotaniaTags.Blocks.LAPUTA_IMMOBILE);
		tag(BotaniaTags.Blocks.LAPUTA_NO_DOUBLE_BLOCK);

		tag(BotaniaTags.Blocks.TERRA_PLATE_BASE).add(BotaniaBlocks.livingrock, BotaniaBlocks.shimmerrock);

		tag(BlockTags.CLIMBABLE).add(BotaniaBlocks.solidVines);

		tag(BlockTags.PLANKS).add(livingwoodPlanks, livingwoodPlanksMossy, livingwoodFramed, livingwoodPatternFramed,
				dreamwoodPlanks, dreamwoodPlanksMossy, dreamwoodFramed, dreamwoodPatternFramed, shimmerwoodPlanks);

		tag(BotaniaTags.Blocks.LIVINGWOOD_LOGS_GLIMMERING).add(livingwoodGlimmering, livingwoodLogGlimmering,
				livingwoodStrippedGlimmering, livingwoodLogStrippedGlimmering);
		tag(BotaniaTags.Blocks.DREAMWOOD_LOGS_GLIMMERING).add(dreamwoodGlimmering, dreamwoodLogGlimmering,
				dreamwoodStrippedGlimmering, dreamwoodLogStrippedGlimmering);

		tag(BotaniaTags.Blocks.LIVINGWOOD_LOGS)
				.add(livingwoodLog, livingwood, livingwoodLogStripped, livingwoodStripped)
				.addTag(BotaniaTags.Blocks.LIVINGWOOD_LOGS_GLIMMERING);
		tag(BotaniaTags.Blocks.DREAMWOOD_LOGS)
				.add(dreamwoodLog, dreamwood, dreamwoodLogStripped, dreamwoodStripped)
				.addTag(BotaniaTags.Blocks.DREAMWOOD_LOGS_GLIMMERING);
		tag(BlockTags.LOGS_THAT_BURN).addTag(BotaniaTags.Blocks.LIVINGWOOD_LOGS).addTag(BotaniaTags.Blocks.DREAMWOOD_LOGS);

		tag(BotaniaTags.Blocks.GHOST_RAIL_BARRIER).addTag(BotaniaTags.Blocks.DREAMWOOD_LOGS);

		tag(BotaniaTags.Blocks.ENDER_AIR_CONVERTABLE).add(Blocks.STONE, Blocks.DEEPSLATE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE);
		tag(BotaniaTags.Blocks.MARIMORPHOSIS_CONVERTABLE).add(Blocks.STONE, Blocks.DEEPSLATE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.TUFF);

		tag(BotaniaTags.Blocks.WEIGHT_LENS_WHITELIST);

		tag(BlockTags.MUSHROOM_GROW_BLOCK).add(
				BotaniaBlocks.biomeStoneFungal, BotaniaBlocks.biomeStoneFungalSlab, BotaniaBlocks.biomeStoneFungalStairs, BotaniaBlocks.biomeStoneFungalWall,
				BotaniaBlocks.biomeBrickFungal, BotaniaBlocks.biomeBrickFungalSlab, BotaniaBlocks.biomeBrickFungalStairs, BotaniaBlocks.biomeBrickFungalWall,
				BotaniaBlocks.biomeCobblestoneFungal, BotaniaBlocks.biomeCobblestoneFungalSlab, BotaniaBlocks.biomeCobblestoneFungalStairs, BotaniaBlocks.biomeCobblestoneFungalWall,
				BotaniaBlocks.biomeChiseledBrickFungal, fungalAltar);

		tag(BotaniaTags.Blocks.HORN_OF_THE_WILD_BREAKABLE).add(Blocks.MOSS_CARPET);

		tag(BlockTags.LEAVES);
		tag(BotaniaTags.Blocks.HORN_OF_THE_CANOPY_BREAKABLE).addTag(BlockTags.LEAVES);

		tag(BotaniaTags.Blocks.HORN_OF_THE_COVERING_BREAKABLE).add(Blocks.SNOW);

		tag(BlockTags.FIRE);
		tag(BotaniaTags.Blocks.UNWANDABLE).addTag(BlockTags.FIRE)
				.add(Blocks.CHORUS_PLANT, Blocks.SCULK_VEIN, Blocks.VINE, Blocks.REDSTONE_WIRE, Blocks.NETHER_PORTAL, BotaniaBlocks.solidVines);

		tag(BotaniaTags.Blocks.PASTURE_SEED_REPLACEABLE).add(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.MYCELIUM);

		tag(BotaniaTags.Blocks.UNETHICAL_TNT_CHECK).addOptional(new ResourceLocation("ae2:tiny_tnt"));

		tag(BlockTags.FLOWER_POTS)
				.add(Arrays.stream(DyeColor.values())
						.map(BotaniaBlocks::getPottedFlower)
						.sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey))
						.toArray(Block[]::new))
				.add(Arrays.stream(DyeColor.values())
						.map(BotaniaBlocks::getPottedShinyFlower)
						.sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey))
						.toArray(Block[]::new))
				.add(Arrays.stream(DyeColor.values())
						.map(BotaniaBlocks::getPottedMushroom)
						.sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey))
						.toArray(Block[]::new))
				.add(
						// misc
						manastarPotted, pureDaisyPotted, bergamutePotted,
						// generating
						dandelifeonPotted, endoflamePotted, entropinnyumPotted,
						gourmaryllisPotted, hydroangeasPotted, kekimurusPotted,
						munchdewPotted, narslimmusPotted, rafflowsiaPotted, rosaArcanaPotted,
						shulkMeNotPotted, spectrolusPotted, thermalilyPotted,
						// functional
						agricarnationPotted, agricarnationChibiPotted, bellethornPotted, bellethornChibiPotted, bubbellPotted,
						bubbellChibiPotted, clayconiaPotted, clayconiaChibiPotted, daffomillPotted, dreadthornPotted,
						exoflamePotted, fallenKanadePotted, heiseiDreamPotted, hopperhockPotted, hopperhockChibiPotted,
						hyacidusPotted, jadedAmaranthusPotted, jiyuuliaPotted, jiyuuliaChibiPotted, labelliaPotted,
						looniumPotted, marimorphosisPotted, marimorphosisChibiPotted, medumonePotted, orechidPotted,
						orechidIgnemPotted, pollidisiacPotted, rannuncarpusPotted, rannuncarpusChibiPotted, solegnoliaPotted,
						solegnoliaChibiPotted, spectranthemumPotted, tangleberriePotted, tangleberrieChibiPotted,
						tigerseyePotted, vinculotusPotted
				);

		tag(BotaniaTags.Blocks.AGRICARNATION_APPLY_BONEMEAL).add(Blocks.AZALEA, Blocks.FLOWERING_AZALEA);
		tag(BotaniaTags.Blocks.AGRICARNATION_GROWTH_CANDIDATE).addTag(BotaniaTags.Blocks.AGRICARNATION_APPLY_BONEMEAL);
		tag(BotaniaTags.Blocks.AGRICARNATION_GROWTH_EXCLUDED).add(Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM, Blocks.MANGROVE_LEAVES);

		registerMiningTags();
	}

	private void registerMiningTags() {
		tag(BlockTags.MINEABLE_WITH_HOE).add(
				getModBlocks(b -> b == cellBlock
						|| BuiltInRegistries.BLOCK.getKey(b).getPath().contains(LibBlockNames.PETAL_BLOCK_SUFFIX)
				)
		);
		tag(BlockTags.MINEABLE_WITH_SHOVEL).add(
				getModBlocks(b -> b == enchantedSoil
						|| b instanceof FloatingFlowerBlock || b instanceof BotaniaGrassBlock)
		);
		var pickaxe = Set.of(
				alchemyCatalyst, conjurationCatalyst,
				manasteelBlock, elementiumBlock, terrasteelBlock, manaDiamondBlock, dragonstoneBlock,
				manaGlass, elfGlass, bifrostPerm,
				BotaniaBlocks.managlassPane, BotaniaBlocks.alfglassPane, BotaniaBlocks.bifrostPane,
				runeAltar, brewery, terraPlate, distributor, manaVoid, manaDetector,
				pistonRelay, tinyPlanet, spawnerClaw,
				rfGenerator, prism, pump, sparkChanger, forestEye, enderEye,
				hourglass, starfield, blazeBlock
		);
		tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
				getModBlocks(b -> pickaxe.contains(b)
						|| b instanceof PetalApothecaryBlock
						|| b instanceof PylonBlock
						|| b instanceof ManaPoolBlock
						|| b instanceof RedStringBlock
						|| BuiltInRegistries.BLOCK.getKey(b).getPath().contains(LibBlockNames.AZULEJO_PREFIX)
						|| BuiltInRegistries.BLOCK.getKey(b).getPath().contains("corporea")
						|| BuiltInRegistries.BLOCK.getKey(b).getPath().contains(LibBlockNames.PAVEMENT_SUFFIX)
						|| BuiltInRegistries.BLOCK.getKey(b).getPath().contains("_quartz")
						|| (BuiltInRegistries.BLOCK.getKey(b).getPath().contains(LibBlockNames.METAMORPHIC_PREFIX)
								&& !(b instanceof WallBlock)) // vanilla includes #wall already
						|| (BuiltInRegistries.BLOCK.getKey(b).getPath().contains(LibBlockNames.LIVING_ROCK)
								&& !(b instanceof WallBlock)) // vanilla includes #wall already
						|| BuiltInRegistries.BLOCK.getKey(b).getPath().contains(LibBlockNames.SHIMMERROCK)
				)
		);
		var axe = Set.of(
				alfPortal, turntable, manaBomb, bellows, incensePlate,
				cacophonium, avatar, root, felPumpkin
		);
		tag(BlockTags.MINEABLE_WITH_AXE).add(
				getModBlocks(b -> axe.contains(b)
						|| b instanceof DrumBlock
						|| b instanceof OpenCrateBlock
						|| b instanceof PlatformBlock
						|| b instanceof ManaSpreaderBlock
						|| BuiltInRegistries.BLOCK.getKey(b).getPath().contains(LibBlockNames.LIVING_WOOD)
						|| BuiltInRegistries.BLOCK.getKey(b).getPath().contains(LibBlockNames.DREAM_WOOD)
						|| BuiltInRegistries.BLOCK.getKey(b).getPath().contains(LibBlockNames.SHIMMERWOOD_PLANKS)
				)
		);
	}

	@NotNull
	private Block[] getModBlocks(Predicate<Block> predicate) {
		return BuiltInRegistries.BLOCK.stream().filter(BOTANIA_BLOCK.and(predicate))
				.sorted(Comparator.comparing(BuiltInRegistries.BLOCK::getKey))
				.toArray(Block[]::new);
	}
}
