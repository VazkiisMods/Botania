/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data;

import com.google.gson.JsonElement;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.*;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.state.BotaniaStateProperties;
import vazkii.botania.api.state.enums.AlfheimPortalState;
import vazkii.botania.api.state.enums.CraftyCratePattern;
import vazkii.botania.common.block.*;
import vazkii.botania.common.block.decor.BotaniaMushroomBlock;
import vazkii.botania.common.block.decor.BuriedPetalBlock;
import vazkii.botania.common.block.decor.FloatingFlowerBlock;
import vazkii.botania.common.block.decor.FlowerMotifBlock;
import vazkii.botania.common.block.decor.PetalBlock;
import vazkii.botania.common.block.decor.panes.BotaniaPaneBlock;
import vazkii.botania.common.block.red_string.RedStringBlock;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.lib.LibBlockNames;
import vazkii.botania.common.lib.LibMisc;
import vazkii.botania.mixin.BlockModelGeneratorsAccessor;
import vazkii.botania.mixin.TextureSlotAccessor;
import vazkii.botania.xplat.XplatAbstractions;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.minecraft.data.models.model.ModelLocationUtils.getModelLocation;
import static net.minecraft.data.models.model.TextureMapping.getBlockTexture;
import static vazkii.botania.common.block.BotaniaBlocks.*;
import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class BlockstateProvider implements DataProvider {
	protected final PackOutput packOutput;

	protected final List<BlockStateGenerator> blockstates = new ArrayList<>();

	protected final Map<ResourceLocation, Supplier<JsonElement>> models = new HashMap<>();
	protected final BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput = models::put;

	public BlockstateProvider(PackOutput packOutput) {
		this.packOutput = packOutput;
	}

	protected Logger getLogger() {
		return BotaniaAPI.LOGGER;
	}

	@NotNull
	@Override
	public String getName() {
		return "Botania Blockstates and Models";
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		try {
			registerStatesAndModels();
		} catch (Exception e) {
			getLogger().error("Error registering states and models", e);
		}

		PackOutput.PathProvider blockstatePathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
		PackOutput.PathProvider modelPathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
		List<CompletableFuture<?>> output = new ArrayList<>();

		for (BlockStateGenerator state : blockstates) {
			ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
			Path path = blockstatePathProvider.json(id);
			output.add(DataProvider.saveStable(cache, state.get(), path));
		}

		for (Map.Entry<ResourceLocation, Supplier<JsonElement>> e : models.entrySet()) {
			ResourceLocation modelId = e.getKey();
			Path path = modelPathProvider.json(modelId);
			output.add(DataProvider.saveStable(cache, e.getValue().get(), path));
		}
		return CompletableFuture.allOf(output.toArray(CompletableFuture[]::new));
	}

	protected void registerStatesAndModels() {
		Set<Block> remainingBlocks = BuiltInRegistries.BLOCK.stream()
				.filter(b -> LibMisc.MOD_ID.equals(BuiltInRegistries.BLOCK.getKey(b).getNamespace()))
				.collect(Collectors.toSet());

		// Manually written blockstate + models
		remainingBlocks.remove(ghostRail);
		remainingBlocks.remove(solidVines);

		// Manually written model, generated blockstate
		manualModel(remainingBlocks, cocoon);
		manualModel(remainingBlocks, corporeaCrystalCube);
		manualModel(remainingBlocks, distributor);
		manualModel(remainingBlocks, prism);
		manualModel(remainingBlocks, runeAltar);
		manualModel(remainingBlocks, spawnerClaw);

		// Single blocks
		var alfPortalModel = ModelTemplates.CUBE_ALL.create(getModelLocation(alfPortal), TextureMapping.cube(alfPortal), this.modelOutput);
		var alfPortalActivatedModel = ModelTemplates.CUBE_ALL.create(getModelLocation(alfPortal, "_activated"), TextureMapping.cube(getModelLocation(alfPortal, "_activated")), this.modelOutput);
		this.blockstates.add(
				MultiVariantGenerator.multiVariant(alfPortal).with(
						PropertyDispatch.property(BotaniaStateProperties.ALFPORTAL_STATE)
								.select(AlfheimPortalState.OFF, Variant.variant().with(VariantProperties.MODEL, alfPortalModel))
								.select(AlfheimPortalState.ON_X, Variant.variant().with(VariantProperties.MODEL, alfPortalActivatedModel))
								.select(AlfheimPortalState.ON_Z, Variant.variant().with(VariantProperties.MODEL, alfPortalActivatedModel))
				));
		remainingBlocks.remove(alfPortal);

		singleVariantBlockState(bifrostPerm,
				ModelTemplates.CUBE_ALL.create(
						getModelLocation(bifrostPerm),
						TextureMapping.cube(bifrost), this.modelOutput));
		remainingBlocks.remove(bifrostPerm);

		singleVariantBlockState(cacophonium,
				ModelTemplates.CUBE_TOP.create(cacophonium, (new TextureMapping())
						.put(TextureSlot.SIDE, getBlockTexture(Blocks.NOTE_BLOCK))
						.put(TextureSlot.TOP, getBlockTexture(cacophonium, "_top")), this.modelOutput));
		remainingBlocks.remove(cacophonium);

		var crateTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/crate")), Optional.empty(),
				TextureSlot.BOTTOM, TextureSlot.SIDE);
		var craftCrateBottomTex = getBlockTexture(craftCrate, "_bottom");
		var crateDispatch = PropertyDispatch.property(BotaniaStateProperties.CRATE_PATTERN);
		for (var pattern : CraftyCratePattern.values()) {
			String suffix = pattern == CraftyCratePattern.NONE ? "" : "_" + pattern.getSerializedName().substring("crafty_".length());
			var model = crateTemplate.create(getModelLocation(craftCrate, suffix),
					new TextureMapping().put(TextureSlot.BOTTOM, craftCrateBottomTex)
							.put(TextureSlot.SIDE, getBlockTexture(craftCrate, suffix)),
					this.modelOutput);
			crateDispatch = crateDispatch.select(pattern, Variant.variant().with(VariantProperties.MODEL, model));
		}
		this.blockstates.add(MultiVariantGenerator.multiVariant(craftCrate).with(crateDispatch));
		remainingBlocks.remove(craftCrate);

		ResourceLocation corpSlabSide = prefix("block/corporea_slab_side");
		ResourceLocation corpBlock = getBlockTexture(corporeaBlock);
		var corpSlabBottomModel = ModelTemplates.SLAB_BOTTOM.create(corporeaSlab,
				new TextureMapping()
						.put(TextureSlot.BOTTOM, corpBlock).put(TextureSlot.TOP, corpBlock).put(TextureSlot.SIDE, corpBlock),
				this.modelOutput);
		var corpSlabTopModel = ModelTemplates.SLAB_TOP.create(getModelLocation(corporeaSlab, "_top"),
				new TextureMapping()
						.put(TextureSlot.BOTTOM, corpBlock).put(TextureSlot.TOP, corpBlock).put(TextureSlot.SIDE, corpBlock),
				this.modelOutput);
		var corpSlabDoubleModel = ModelTemplates.CUBE_BOTTOM_TOP.create(prefix("block/corporea_double_slab"),
				new TextureMapping()
						.put(TextureSlot.SIDE, corpSlabSide).put(TextureSlot.BOTTOM, corpBlock).put(TextureSlot.TOP, corpBlock),
				this.modelOutput);
		blockstates.add(BlockModelGeneratorsAccessor.makeSlabState(corporeaSlab, corpSlabBottomModel, corpSlabTopModel, corpSlabDoubleModel));
		remainingBlocks.remove(corporeaSlab);

		stairsBlock(remainingBlocks, corporeaStairs, corpBlock, corpBlock, corpBlock);

		this.blockstates.add(MultiVariantGenerator.multiVariant(elfGlass, IntStream.rangeClosed(0, 3)
				.mapToObj(i -> {
					var model = ModelTemplates.CUBE_ALL.create(
							getModelLocation(elfGlass, "_" + i),
							TextureMapping.cube(getBlockTexture(elfGlass, "_" + i)),
							this.modelOutput);
					return Variant.variant().with(VariantProperties.MODEL, model);
				})
				.toArray(Variant[]::new)));
		remainingBlocks.remove(elfGlass);

		singleVariantBlockState(enchantedSoil, ModelTemplates.CUBE_BOTTOM_TOP.create(
				enchantedSoil,
				TextureMapping.cubeBottomTop(enchantedSoil).put(TextureSlot.BOTTOM, getBlockTexture(Blocks.DIRT)),
				this.modelOutput
		));
		remainingBlocks.remove(enchantedSoil);

		var pumpkinModel = ModelTemplates.CUBE_ORIENTABLE.create(felPumpkin, new TextureMapping()
				.put(TextureSlot.SIDE, getBlockTexture(Blocks.PUMPKIN, "_side"))
				.put(TextureSlot.FRONT, getBlockTexture(felPumpkin))
				.put(TextureSlot.TOP, getBlockTexture(Blocks.PUMPKIN, "_top")),
				this.modelOutput
		);
		this.blockstates.add(MultiVariantGenerator.multiVariant(felPumpkin, Variant.variant().with(VariantProperties.MODEL, pumpkinModel))
				.with(BlockModelGeneratorsAccessor.horizontalDispatch()));
		remainingBlocks.remove(felPumpkin);

		ModelTemplate eightByEightTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/eightbyeight")),
				Optional.empty(),
				TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.NORTH, TextureSlot.SOUTH, TextureSlot.WEST, TextureSlot.EAST);
		singleVariantBlockState(forestEye, eightByEightTemplate.create(forestEye,
				new TextureMapping()
						.put(TextureSlot.BOTTOM, getBlockTexture(forestEye, "_bottom"))
						.put(TextureSlot.TOP, getBlockTexture(forestEye, "_top"))
						.put(TextureSlot.NORTH, getBlockTexture(forestEye, "_north"))
						.put(TextureSlot.SOUTH, getBlockTexture(forestEye, "_south"))
						.put(TextureSlot.WEST, getBlockTexture(forestEye, "_west"))
						.put(TextureSlot.EAST, getBlockTexture(forestEye, "_east")),
				this.modelOutput));
		remainingBlocks.remove(forestEye);

		var plateFile = getModelLocation(incensePlate);
		this.blockstates.add(MultiVariantGenerator.multiVariant(incensePlate, Variant.variant().with(VariantProperties.MODEL, plateFile))
				.with(BlockModelGeneratorsAccessor.horizontalDispatch()));
		remainingBlocks.remove(incensePlate);

		var fourHighBottomTopTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/four_high_bottom_top")),
				Optional.empty(),
				TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
		singleVariantBlockState(lightLauncher, fourHighBottomTopTemplate.create(lightLauncher,
				new TextureMapping()
						.put(TextureSlot.BOTTOM, getBlockTexture(lightLauncher, "_end"))
						.put(TextureSlot.TOP, getBlockTexture(lightLauncher, "_end"))
						.put(TextureSlot.SIDE, getBlockTexture(lightLauncher, "_side")),
				this.modelOutput
		));
		remainingBlocks.remove(lightLauncher);

		singleVariantBlockState(openCrate,
				crateTemplate.create(openCrate, new TextureMapping()
						.put(TextureSlot.SIDE, getBlockTexture(openCrate))
						.put(TextureSlot.BOTTOM, getBlockTexture(openCrate, "_bottom")),
						this.modelOutput
				));
		remainingBlocks.remove(openCrate);

		var threeHighBottomTopTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/three_high_bottom_top")),
				Optional.empty(),
				TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
		singleVariantBlockState(sparkChanger, threeHighBottomTopTemplate.create(sparkChanger,
				TextureMapping.cubeBottomTop(sparkChanger),
				this.modelOutput));
		remainingBlocks.remove(sparkChanger);

		singleVariantBlockState(starfield, fourHighBottomTopTemplate.create(starfield,
				TextureMapping.cubeBottomTop(starfield), this.modelOutput));
		remainingBlocks.remove(starfield);

		singleVariantBlockState(terraPlate, threeHighBottomTopTemplate.create(terraPlate,
				TextureMapping.cubeBottomTop(terraPlate), this.modelOutput));
		remainingBlocks.remove(terraPlate);

		var tenByTenAllTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/tenbyten_all")),
				Optional.empty(),
				TextureSlot.ALL);
		singleVariantBlockState(tinyPlanet, tenByTenAllTemplate.create(tinyPlanet,
				TextureMapping.cube(tinyPlanet), this.modelOutput));
		remainingBlocks.remove(tinyPlanet);

		singleVariantBlockState(turntable, ModelTemplates.CUBE_BOTTOM_TOP.create(turntable,
				TextureMapping.cubeBottomTop(turntable),
				this.modelOutput
		));
		remainingBlocks.remove(turntable);

		ResourceLocation[] topTexs = new ResourceLocation[6];
		ResourceLocation[] sideTexs = new ResourceLocation[6];
		ResourceLocation[] topStrippedTexs = new ResourceLocation[6];
		ResourceLocation[] sideStrippedTexs = new ResourceLocation[6];
		ResourceLocation[] sideGlimmeringTexs = new ResourceLocation[6];
		ResourceLocation[] sideGlimmeringStrippedTexs = new ResourceLocation[6];
		ResourceLocation[] logModels = new ResourceLocation[6];
		ResourceLocation[] strippedLogModels = new ResourceLocation[6];

		for (int i = 0; i < 6; i++) {
			String suffix = i == 0 ? "" : "_" + i;
			sideTexs[i] = getBlockTexture(dreamwoodLog, suffix);
			topTexs[i] = getBlockTexture(dreamwoodLog, "_top");
			sideStrippedTexs[i] = getBlockTexture(dreamwoodLogStripped, suffix);
			topStrippedTexs[i] = getBlockTexture(dreamwoodLogStripped, "_top");
			sideGlimmeringTexs[i] = getBlockTexture(dreamwoodLogGlimmering, suffix);
			sideGlimmeringStrippedTexs[i] = getBlockTexture(dreamwoodLogStrippedGlimmering, suffix);
			logModels[i] = getModelLocation(dreamwood, suffix);
			strippedLogModels[i] = getModelLocation(dreamwoodStripped, suffix);
		}

		pillarWithVariants(remainingBlocks, dreamwoodLog, topTexs, sideTexs);
		pillarWithVariants(remainingBlocks, dreamwood, sideTexs, sideTexs);
		pillarWithVariants(remainingBlocks, dreamwoodLogStripped, topStrippedTexs, sideStrippedTexs);
		pillarWithVariants(remainingBlocks, dreamwoodStripped, sideStrippedTexs, sideStrippedTexs);
		pillarWithVariants(remainingBlocks, dreamwoodLogGlimmering, topTexs, sideGlimmeringTexs);
		pillarWithVariants(remainingBlocks, dreamwoodGlimmering, sideGlimmeringTexs, sideGlimmeringTexs);
		pillarWithVariants(remainingBlocks, dreamwoodLogStrippedGlimmering, topStrippedTexs, sideGlimmeringStrippedTexs);
		pillarWithVariants(remainingBlocks, dreamwoodStrippedGlimmering, sideGlimmeringStrippedTexs, sideGlimmeringStrippedTexs);

		stairsBlockWithVariants(remainingBlocks, dreamwoodStairs, sideTexs, sideTexs, sideTexs);
		stairsBlockWithVariants(remainingBlocks, dreamwoodStrippedStairs, sideStrippedTexs, sideStrippedTexs, sideStrippedTexs);
		slabBlockWithVariants(remainingBlocks, dreamwoodSlab, logModels, sideTexs, sideTexs, sideTexs);
		slabBlockWithVariants(remainingBlocks, dreamwoodStrippedSlab, strippedLogModels, sideStrippedTexs, sideStrippedTexs, sideStrippedTexs);
		wallBlockWithVariants(remainingBlocks, dreamwoodWall, sideTexs);
		wallBlockWithVariants(remainingBlocks, dreamwoodStrippedWall, sideStrippedTexs);

		pillar(remainingBlocks, livingwoodLog, getBlockTexture(livingwoodLog, "_top"), getBlockTexture(livingwoodLog));
		pillar(remainingBlocks, livingwood, getBlockTexture(livingwoodLog), getBlockTexture(livingwoodLog));
		pillar(remainingBlocks, livingwoodLogStripped, getBlockTexture(livingwoodLogStripped, "_top"), getBlockTexture(livingwoodLogStripped));
		pillar(remainingBlocks, livingwoodStripped, getBlockTexture(livingwoodLogStripped), getBlockTexture(livingwoodLogStripped));
		pillar(remainingBlocks, livingwoodLogGlimmering, getBlockTexture(livingwoodLog, "_top"), getBlockTexture(livingwoodLogGlimmering));
		pillar(remainingBlocks, livingwoodGlimmering, getBlockTexture(livingwoodLogGlimmering), getBlockTexture(livingwoodLogGlimmering));
		pillar(remainingBlocks, livingwoodLogStrippedGlimmering, getBlockTexture(livingwoodLogStripped, "_top"), getBlockTexture(livingwoodLogStrippedGlimmering));
		pillar(remainingBlocks, livingwoodStrippedGlimmering, getBlockTexture(livingwoodLogStrippedGlimmering), getBlockTexture(livingwoodLogStrippedGlimmering));

		pillarAlt(remainingBlocks, livingwoodFramed, getBlockTexture(livingwoodPatternFramed), getBlockTexture(livingwoodFramed));
		pillarAlt(remainingBlocks, dreamwoodFramed, getBlockTexture(dreamwoodPatternFramed), getBlockTexture(dreamwoodFramed));

		stairsBlock(remainingBlocks, livingwoodStairs, getBlockTexture(livingwoodLog), getBlockTexture(livingwoodLog), getBlockTexture(livingwoodLog));
		stairsBlock(remainingBlocks, livingwoodStrippedStairs, getBlockTexture(livingwoodLogStripped), getBlockTexture(livingwoodLogStripped), getBlockTexture(livingwoodLogStripped));
		slabBlock(remainingBlocks, livingwoodSlab, getModelLocation(livingwood), getBlockTexture(livingwoodLog), getBlockTexture(livingwoodLog), getBlockTexture(livingwoodLog));
		slabBlock(remainingBlocks, livingwoodStrippedSlab, getModelLocation(livingwoodStripped), getBlockTexture(livingwoodLogStripped), getBlockTexture(livingwoodLogStripped), getBlockTexture(livingwoodLogStripped));
		wallBlock(remainingBlocks, livingwoodWall, getBlockTexture(livingwoodLog));
		wallBlock(remainingBlocks, livingwoodStrippedWall, getBlockTexture(livingwoodLogStripped));

		fenceBlock(remainingBlocks, dreamwoodFence, getBlockTexture(dreamwoodPlanks));
		fenceGateBlock(remainingBlocks, dreamwoodFenceGate, getBlockTexture(dreamwoodPlanks));
		fenceBlock(remainingBlocks, livingwoodFence, getBlockTexture(livingwoodPlanks));
		fenceGateBlock(remainingBlocks, livingwoodFenceGate, getBlockTexture(livingwoodPlanks));

		rotatedMirrored(remainingBlocks, livingrock, getBlockTexture(livingrock));

		ResourceLocation polishedLivingrockTexture = getBlockTexture(livingrockPolished);
		ResourceLocation polishedLivingrockSlabSideTexture = getBlockTexture(livingrockPolishedSlab);
		ResourceLocation polishedLivingrockSlabDoubleModel = ModelTemplates.CUBE_COLUMN.create(
				getModelLocation(livingrockPolishedSlab, "_double"),
				new TextureMapping()
						.put(TextureSlot.SIDE, polishedLivingrockSlabSideTexture)
						.put(TextureSlot.END, polishedLivingrockTexture),
				this.modelOutput
		);
		slabBlock(remainingBlocks, livingrockPolishedSlab, polishedLivingrockSlabDoubleModel, polishedLivingrockSlabSideTexture, polishedLivingrockTexture, polishedLivingrockTexture);

		var conjurationTexture = getBlockTexture(conjurationCatalyst);
		var conjurationMirrored = getBlockTexture(conjurationCatalyst, "_mirrored");
		checkeredBlockWithBlockstate(remainingBlocks, conjurationCatalyst, conjurationTexture, conjurationMirrored);

		// block entities with only particles
		particleOnly(remainingBlocks, animatedTorch, getBlockTexture(Blocks.REDSTONE_TORCH));
		particleOnly(remainingBlocks, avatar, getBlockTexture(livingwoodLog));
		particleOnly(remainingBlocks, bellows, getBlockTexture(livingwoodLog));
		particleOnly(remainingBlocks, brewery, getBlockTexture(livingrock));
		particleOnly(remainingBlocks, corporeaIndex, getBlockTexture(corporeaBlock));
		particleOnly(remainingBlocks, lightRelayDetector, getBlockTexture(lightRelayDetector));
		singleVariantBlockState(fakeAir, new ModelTemplate(Optional.empty(), Optional.empty()).create(fakeAir, new TextureMapping(), this.modelOutput));
		remainingBlocks.remove(fakeAir);
		particleOnly(remainingBlocks, lightRelayFork, getBlockTexture(lightRelayFork));
		particleOnly(remainingBlocks, gaiaHead, getBlockTexture(Blocks.SOUL_SAND));
		particleOnly(remainingBlocks, gaiaHeadWall, getBlockTexture(Blocks.SOUL_SAND));
		particleOnly(remainingBlocks, gaiaPylon, getBlockTexture(elementiumBlock));
		particleOnly(remainingBlocks, hourglass, getBlockTexture(manaGlass));
		particleOnly(remainingBlocks, lightRelayDefault, getBlockTexture(lightRelayDefault));
		particleOnly(remainingBlocks, manaFlame, new ResourceLocation("block/fire_0"));
		particleOnly(remainingBlocks, manaPylon, getBlockTexture(manasteelBlock));
		particleOnly(remainingBlocks, naturaPylon, getBlockTexture(terrasteelBlock));
		particleOnly(remainingBlocks, teruTeruBozu, getBlockTexture(Blocks.WHITE_WOOL));
		particleOnly(remainingBlocks, lightRelayToggle, getBlockTexture(lightRelayToggle));

		// Block groups
		Predicate<Block> flowers = b -> XplatAbstractions.INSTANCE.isSpecialFlowerBlock(b)
				|| b instanceof BotaniaMushroomBlock
				|| b instanceof BotaniaFlowerBlock;
		ModelTemplate crossTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/cross")), Optional.empty(), TextureSlot.CROSS);
		takeAll(remainingBlocks, flowers).forEach(b -> singleVariantBlockState(b, crossTemplate.create(b, TextureMapping.cross(b), this.modelOutput))
		);

		takeAll(remainingBlocks, b -> b instanceof FlowerMotifBlock).forEach(b -> {
			String name = BuiltInRegistries.BLOCK.getKey(b).getPath().replace("_motif", "");
			singleVariantBlockState(b, crossTemplate.create(b, new TextureMapping()
					.put(TextureSlot.CROSS, prefix("block/" + name)),
					this.modelOutput));
		});

		takeAll(remainingBlocks, corporeaFunnel, corporeaInterceptor, corporeaRetainer).forEach(b -> {
			singleVariantBlockState(b, ModelTemplates.CUBE_COLUMN.create(b,
					TextureMapping.column(getBlockTexture(b, "_side"), getBlockTexture(b, "_end")),
					this.modelOutput));
		});

		var drumModelTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/drum")), Optional.empty(), TextureSlot.TOP, TextureSlot.SIDE);
		takeAll(remainingBlocks, gatheringDrum, canopyDrum, wildDrum).forEach(b -> {
			singleVariantBlockState(b, drumModelTemplate.create(b,
					new TextureMapping()
							.put(TextureSlot.TOP, prefix("block/drum_top"))
							.put(TextureSlot.SIDE, getBlockTexture(b)),
					this.modelOutput));
		});

		var outsideSlot = TextureSlotAccessor.make("outside");
		var coreSlot = TextureSlotAccessor.make("core");
		var spreaderTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/spreader")), Optional.empty(),
				TextureSlot.SIDE, TextureSlot.BACK, TextureSlot.INSIDE, outsideSlot);
		var spreaderCoreTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/spreader_core")), Optional.of("_core"),
				coreSlot);
		var spreaderPaddingTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/spreader_padding")),
				Optional.empty(), TextureSlot.FRONT, TextureSlot.BACK, TextureSlot.SIDE);
		var spreaderScaffoldingTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/spreader_scaffolding")),
				Optional.of("_scaffolding"), TextureSlot.TOP, TextureSlot.SIDE, TextureSlot.BOTTOM);
		takeAll(remainingBlocks, manaSpreader, redstoneSpreader, gaiaSpreader, elvenSpreader).forEach(b -> {
			ResourceLocation outside;
			if (b == redstoneSpreader || b == manaSpreader) {
				outside = getBlockTexture(livingwoodLog);
			} else if (b == elvenSpreader) {
				outside = getBlockTexture(dreamwoodLog, "_3");
			} else {
				outside = getBlockTexture(b, "_outside");
			}
			ResourceLocation inside;
			if (b == redstoneSpreader || b == manaSpreader) {
				inside = getBlockTexture(livingwoodLogStripped);
			} else if (b == elvenSpreader) {
				inside = getBlockTexture(dreamwoodLogStripped, "_3");
			} else {
				inside = getBlockTexture(b, "_inside");
			}
			singleVariantBlockState(b, spreaderTemplate.create(b, new TextureMapping()
					.put(TextureSlot.SIDE, getBlockTexture(b, "_side"))
					.put(TextureSlot.BACK, getBlockTexture(b, "_back"))
					.put(TextureSlot.INSIDE, inside)
					.put(outsideSlot, outside), this.modelOutput));
			spreaderCoreTemplate.create(b, new TextureMapping()
					.put(coreSlot, getBlockTexture(b, "_core")), this.modelOutput);
			if (b != redstoneSpreader) {
				spreaderScaffoldingTemplate.create(b, new TextureMapping()
						.put(TextureSlot.TOP, getBlockTexture(b, "_scaffolding_top"))
						.put(TextureSlot.SIDE, getBlockTexture(b, "_scaffolding_side"))
						.put(TextureSlot.BOTTOM, getBlockTexture(b, "_scaffolding_bottom")), this.modelOutput);
			}
		});
		ColorHelper.supportedColors().forEach(color -> {
			Block wool = ColorHelper.WOOL_MAP.apply(color);
			spreaderPaddingTemplate.create(prefix("block/" + color.getName() + "_spreader_padding"),
					new TextureMapping()
							.put(TextureSlot.FRONT, getBlockTexture(wool))
							.put(TextureSlot.BACK, getBlockTexture(wool))
							.put(TextureSlot.SIDE, getBlockTexture(wool)),
					this.modelOutput);
		});

		var manaSlot = TextureSlotAccessor.make("mana");
		TextureSlot[] manaPoolSlots = new TextureSlot[] { TextureSlot.SIDE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.INSIDE };
		TextureSlot[] manaPoolFullSlots = new TextureSlot[] { TextureSlot.SIDE, TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.INSIDE, manaSlot };
		var poolTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/mana_pool")), Optional.empty(), manaPoolSlots);
		var dilutedPoolTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/diluted_mana_pool")), Optional.empty(), manaPoolSlots);
		var creativePoolTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/creative_mana_pool")), Optional.empty(), manaPoolSlots);
		var poolFullTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/mana_pool_full")), Optional.of("_full"), manaPoolFullSlots);
		var dilutedPoolFullTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/diluted_mana_pool_full")), Optional.of("_full"), manaPoolFullSlots);
		var creativePoolFullTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/creative_mana_pool_full")), Optional.of("_full"), manaPoolFullSlots);
		takeAll(remainingBlocks, manaPool, dilutedPool, fabulousPool, creativePool).forEach(b -> {
			Block blockForTexture = b == fabulousPool ? manaPool : b;
			ResourceLocation side = getBlockTexture(blockForTexture, "_side");
			ResourceLocation top = getBlockTexture(blockForTexture, "_top");
			ResourceLocation bottom = b == dilutedPool
					? getBlockTexture(manaPool, "_bottom") : getBlockTexture(blockForTexture, "_bottom");
			ResourceLocation inside = getBlockTexture(blockForTexture, "_inside");
			ModelTemplate template = b == dilutedPool
					? dilutedPoolTemplate
					: b == creativePool
							? creativePoolTemplate
					: poolTemplate;
			ModelTemplate fullTemplate = b == dilutedPool
					? dilutedPoolFullTemplate
					: b == creativePool
							? creativePoolFullTemplate
					: poolFullTemplate;
			TextureMapping mapping = new TextureMapping()
					.put(TextureSlot.SIDE, side)
					.put(TextureSlot.TOP, top)
					.put(TextureSlot.BOTTOM, bottom)
					.put(TextureSlot.INSIDE, inside);

			singleVariantBlockState(b, template.create(b, mapping, this.modelOutput));
			fullTemplate.create(b, mapping.put(manaSlot, prefix("block/mana_water")), this.modelOutput);
		});

		takeAll(remainingBlocks, pump, tinyPotato).forEach(b -> this.blockstates.add(MultiVariantGenerator.multiVariant(b, Variant.variant().with(VariantProperties.MODEL, getModelLocation(b)))
				.with(BlockModelGeneratorsAccessor.horizontalDispatch()))
		);

		takeAll(remainingBlocks, enderEye, manaDetector).forEach(b -> {
			var offModel = ModelTemplates.CUBE_ALL.create(b, TextureMapping.cube(b), this.modelOutput);
			var onModel = ModelTemplates.CUBE_ALL.create(getModelLocation(b, "_powered"),
					TextureMapping.cube(getBlockTexture(b, "_powered")), this.modelOutput);
			this.blockstates.add(MultiVariantGenerator.multiVariant(b).with(
					PropertyDispatch.property(BlockStateProperties.POWERED)
							.select(false, Variant.variant().with(VariantProperties.MODEL, offModel))
							.select(true, Variant.variant().with(VariantProperties.MODEL, onModel))
			));
		});

		var petalBlockModel = new ModelTemplate(Optional.of(prefix("block/shapes/cube_all_tinted")), Optional.empty(), TextureSlot.ALL)
				.create(prefix("block/petal_block"), new TextureMapping().put(TextureSlot.ALL, prefix("block/petal_block")), this.modelOutput);
		takeAll(remainingBlocks, b -> b instanceof PetalBlock).forEach(b -> singleVariantBlockState(b, petalBlockModel));

		takeAll(remainingBlocks, b -> b instanceof BotaniaGrassBlock).forEach(b -> {
			var model = ModelTemplates.CUBE_BOTTOM_TOP.create(b, new TextureMapping()
					.put(TextureSlot.SIDE, getBlockTexture(b, "_side"))
					.put(TextureSlot.BOTTOM, getBlockTexture(Blocks.DIRT))
					.put(TextureSlot.TOP, getBlockTexture(b, "_top")),
					this.modelOutput
			);
			this.blockstates.add(MultiVariantGenerator.multiVariant(b, BlockModelGeneratorsAccessor.createRotatedVariants(model)));
		});

		takeAll(remainingBlocks, b -> b instanceof RedStringBlock).forEach(this::redStringBlock);

		takeAll(remainingBlocks, b -> b instanceof BotaniaDoubleFlowerBlock).forEach(b -> {
			var bottom = ModelTemplates.CROSS.create(b, TextureMapping.cross(b), this.modelOutput);
			var top = ModelTemplates.CROSS.create(getModelLocation(b, "_top"), TextureMapping.cross(getBlockTexture(b, "_top")), this.modelOutput);
			this.blockstates.add(
					MultiVariantGenerator.multiVariant(b)
							.with(PropertyDispatch.property(TallFlowerBlock.HALF)
									.select(DoubleBlockHalf.LOWER, Variant.variant().with(VariantProperties.MODEL, bottom))
									.select(DoubleBlockHalf.UPPER, Variant.variant().with(VariantProperties.MODEL, top))
							)
			);
		});

		var mountainTextures = new ResourceLocation[] { getBlockTexture(biomeStoneMountain), getBlockTexture(biomeStoneMountain, "_1") };
		var mountainModels = new ResourceLocation[] { getModelLocation(biomeStoneMountain), getModelLocation(biomeStoneMountain, "_1") };
		var mountainWeights = new Integer[] { 5, 1 };
		rotatedMirroredWithVariants(remainingBlocks, biomeStoneMountain, mountainTextures, mountainWeights);
		stairsBlockWithVariants(remainingBlocks, biomeStoneMountainStairs, mountainTextures, mountainTextures, mountainTextures, mountainWeights);
		slabBlockWithVariants(remainingBlocks, biomeStoneMountainSlab, mountainModels, mountainTextures, mountainTextures, mountainTextures, mountainWeights);
		wallBlockWithVariants(remainingBlocks, biomeStoneMountainWall, mountainTextures, mountainWeights);

		var mountainBrickTextures = new ResourceLocation[] {
				getBlockTexture(biomeBrickMountain),
				getBlockTexture(biomeBrickMountain, "_1"),
				getBlockTexture(biomeBrickMountain, "_2"),
				getBlockTexture(biomeBrickMountain, "_3"),
				getBlockTexture(biomeBrickMountain, "_4"),
				getBlockTexture(biomeBrickMountain, "_5")
		};
		var mountainBrickModels = new ResourceLocation[] {
				getModelLocation(biomeBrickMountain),
				getModelLocation(biomeBrickMountain, "_1"),
				getModelLocation(biomeBrickMountain, "_2"),
				getModelLocation(biomeBrickMountain, "_3"),
				getModelLocation(biomeBrickMountain, "_4"),
				getModelLocation(biomeBrickMountain, "_5"),
		};
		cubeAllWithVariants(remainingBlocks, biomeBrickMountain, mountainBrickTextures);
		stairsBlockWithVariants(remainingBlocks, biomeBrickMountainStairs, mountainBrickTextures, mountainBrickTextures, mountainBrickTextures);
		slabBlockWithVariants(remainingBlocks, biomeBrickMountainSlab, mountainBrickModels, mountainBrickTextures, mountainBrickTextures, mountainBrickTextures);
		wallBlockWithVariants(remainingBlocks, biomeBrickMountainWall, mountainBrickTextures);

		var taigaTextures = new ResourceLocation[] { getBlockTexture(biomeStoneTaiga), getBlockTexture(biomeStoneTaiga, "_1") };
		var taigaModels = new ResourceLocation[] { getModelLocation(biomeStoneTaiga), getModelLocation(biomeStoneTaiga, "_1") };
		rotatedMirroredWithVariants(remainingBlocks, biomeStoneTaiga, taigaTextures);
		stairsBlockWithVariants(remainingBlocks, biomeStoneTaigaStairs, taigaTextures, taigaTextures, taigaTextures);
		slabBlockWithVariants(remainingBlocks, biomeStoneTaigaSlab, taigaModels, taigaTextures, taigaTextures, taigaTextures);
		wallBlockWithVariants(remainingBlocks, biomeStoneTaigaWall, taigaTextures);

		var plainsBrickSide = getBlockTexture(biomeBrickPlains);
		var plainsBrickTop = getBlockTexture(biomeBrickPlains, "_top");
		pillarAlt(remainingBlocks, biomeBrickPlains, plainsBrickTop, plainsBrickSide);
		stairsBlock(remainingBlocks, biomeBrickPlainsStairs, plainsBrickSide, plainsBrickTop, plainsBrickTop);
		slabBlock(remainingBlocks, biomeBrickPlainsSlab, getModelLocation(biomeBrickPlains), plainsBrickSide, plainsBrickTop, plainsBrickTop);
		wallBlock(remainingBlocks, biomeBrickPlainsWall, plainsBrickSide, plainsBrickTop, plainsBrickTop);

		var forestBrickTextures = new ResourceLocation[] { getBlockTexture(biomeBrickForest), getBlockTexture(biomeBrickForest, "_1") };
		var forestBrickModels = new ResourceLocation[] { getModelLocation(biomeBrickForest), getModelLocation(biomeBrickForest, "_1") };
		var forestBrickWeights = new Integer[] { 2, 1 };
		cubeAllWithVariants(remainingBlocks, biomeBrickForest, forestBrickTextures, forestBrickWeights);
		stairsBlockWithVariants(remainingBlocks, biomeBrickForestStairs, forestBrickTextures, forestBrickTextures, forestBrickTextures, forestBrickWeights);
		slabBlockWithVariants(remainingBlocks, biomeBrickForestSlab, forestBrickModels, forestBrickTextures, forestBrickTextures, forestBrickTextures, forestBrickWeights);
		wallBlockWithVariants(remainingBlocks, biomeBrickForestWall, forestBrickTextures, forestBrickWeights);

		var fungalBrickTextures = new ResourceLocation[] { getBlockTexture(biomeBrickFungal), getBlockTexture(biomeBrickFungal, "_1") };
		var fungalBrickModels = new ResourceLocation[] { getModelLocation(biomeBrickFungal), getModelLocation(biomeBrickFungal, "_1") };
		cubeAllWithVariants(remainingBlocks, biomeBrickFungal, fungalBrickTextures);
		stairsBlockWithVariants(remainingBlocks, biomeBrickFungalStairs, fungalBrickTextures, fungalBrickTextures, fungalBrickTextures);
		slabBlockWithVariants(remainingBlocks, biomeBrickFungalSlab, fungalBrickModels, fungalBrickTextures, fungalBrickTextures, fungalBrickTextures);
		wallBlockWithVariants(remainingBlocks, biomeBrickFungalWall, fungalBrickTextures);

		var swampBrickTopTextures = new ResourceLocation[] {
				getBlockTexture(biomeBrickSwamp, "_top"),
				getBlockTexture(biomeBrickSwamp, "_top_1")
		};
		var swampBrickBottomTextures = new ResourceLocation[] {
				getBlockTexture(biomeBrickSwamp, "_bottom"),
				getBlockTexture(biomeBrickSwamp, "_bottom")
		};
		var swampBrickSideTextures = new ResourceLocation[] {
				getBlockTexture(biomeBrickSwamp),
				getBlockTexture(biomeBrickSwamp)
		};
		var swampBrickModels = new ResourceLocation[] {
				getModelLocation(biomeBrickSwamp),
				getModelLocation(biomeBrickSwamp, "_1")
		};
		directionalPillarWithVariants(remainingBlocks, biomeBrickSwamp, swampBrickTopTextures, swampBrickBottomTextures, swampBrickSideTextures);
		stairsBlockWithVariants(remainingBlocks, biomeBrickSwampStairs, swampBrickSideTextures, swampBrickBottomTextures, swampBrickTopTextures);
		slabBlockWithVariants(remainingBlocks, biomeBrickSwampSlab, swampBrickModels, swampBrickSideTextures, swampBrickBottomTextures, swampBrickTopTextures);
		wallBlockWithVariants(remainingBlocks, biomeBrickSwampWall, swampBrickSideTextures, swampBrickBottomTextures, swampBrickTopTextures);

		var swampChiseledBrickTopTextures = new ResourceLocation[] {
				getBlockTexture(biomeChiseledBrickSwamp, "_top"),
				getBlockTexture(biomeChiseledBrickSwamp, "_top_1")
		};
		var swampChiseledBrickBottomTextures = new ResourceLocation[] {
				getBlockTexture(biomeChiseledBrickSwamp, "_bottom"),
				getBlockTexture(biomeChiseledBrickSwamp, "_bottom")
		};
		var swampChiseledBrickSideTextures = new ResourceLocation[] {
				getBlockTexture(biomeChiseledBrickSwamp),
				getBlockTexture(biomeChiseledBrickSwamp)
		};
		directionalPillarWithVariants(remainingBlocks, biomeChiseledBrickSwamp, swampChiseledBrickTopTextures, swampChiseledBrickBottomTextures, swampChiseledBrickSideTextures);

		var swampCobblestoneTextures = new ResourceLocation[] { getBlockTexture(biomeCobblestoneSwamp), getBlockTexture(biomeCobblestoneSwamp, "_1") };
		var swampCobblestoneModels = new ResourceLocation[] { getModelLocation(biomeCobblestoneSwamp), getModelLocation(biomeCobblestoneSwamp, "_1") };
		cubeAllWithVariants(remainingBlocks, biomeCobblestoneSwamp, swampCobblestoneTextures);
		stairsBlockWithVariants(remainingBlocks, biomeCobblestoneSwampStairs, swampCobblestoneTextures, swampCobblestoneTextures, swampCobblestoneTextures);
		slabBlockWithVariants(remainingBlocks, biomeCobblestoneSwampSlab, swampCobblestoneModels, swampCobblestoneTextures, swampCobblestoneTextures, swampCobblestoneTextures);
		wallBlockWithVariants(remainingBlocks, biomeCobblestoneSwampWall, swampCobblestoneTextures);

		var mesaBrick = getBlockTexture(biomeBrickMesa);
		var mesaBrickMirrored = getBlockTexture(biomeBrickMesa, "_mirrored");
		var mesaBrickModel = checkeredBlockWithBlockstate(remainingBlocks, biomeBrickMesa, mesaBrick, mesaBrickMirrored);
		checkeredSlabBlock(remainingBlocks, biomeBrickMesaSlab, mesaBrickModel, mesaBrick, mesaBrickMirrored);
		checkeredStairsBlock(remainingBlocks, biomeBrickMesaStairs, mesaBrick, mesaBrickMirrored);
		checkeredWallBlock(remainingBlocks, biomeBrickMesaWall, mesaBrick, mesaBrickMirrored);

		var mesaChiseledBrickSide = getBlockTexture(biomeChiseledBrickMesa);
		var mesaChiseledBrickTop = getBlockTexture(biomeChiseledBrickMesa, "_top");
		pillarAlt(remainingBlocks, biomeChiseledBrickMesa, mesaChiseledBrickTop, mesaChiseledBrickSide);

		// Remaining slabs, stairs, walls are handled automatically.
		for (Block stone : new Block[] { biomeStoneDesert, biomeStoneForest, biomeStoneFungal, biomeStoneMesa, biomeStonePlains, biomeStoneSwamp }) {
			rotatedMirrored(remainingBlocks, stone, getBlockTexture(stone));
		}

		for (String variant : new String[] { "dark", "mana", "blaze", "lavender", "red", "elf", "sunny" }) {
			ResourceLocation quartzId = prefix(variant + "_quartz");
			Block quartz = BuiltInRegistries.BLOCK.get(quartzId);
			singleVariantBlockState(quartz,
					ModelTemplates.CUBE_BOTTOM_TOP.create(quartz, TextureMapping.cubeBottomTop(quartz), this.modelOutput));

			ResourceLocation pillarId = prefix(variant + "_quartz_pillar");
			Block pillar = BuiltInRegistries.BLOCK.get(pillarId);
			var pillarModel = ModelTemplates.CUBE_COLUMN.create(pillar,
					TextureMapping.column(getBlockTexture(pillar, "_side"), getBlockTexture(pillar, "_end")),
					this.modelOutput);
			this.blockstates.add(BlockModelGeneratorsAccessor.createAxisAlignedPillarBlock(pillar, pillarModel));

			ResourceLocation chiseledId = prefix("chiseled_" + variant + "_quartz");
			Block chiseled = BuiltInRegistries.BLOCK.get(chiseledId);
			singleVariantBlockState(chiseled,
					ModelTemplates.CUBE_COLUMN.create(chiseled, new TextureMapping()
							.put(TextureSlot.SIDE, getBlockTexture(chiseled, "_side"))
							.put(TextureSlot.END, getBlockTexture(chiseled, "_end")), this.modelOutput));

			remainingBlocks.remove(quartz);
			remainingBlocks.remove(pillar);
			remainingBlocks.remove(chiseled);
		}

		takeAll(remainingBlocks, b -> b instanceof BuriedPetalBlock).forEach(b -> {
			DyeColor color = ((BuriedPetalBlock) b).color;
			ResourceLocation wool = new ResourceLocation("block/" + color.getSerializedName() + "_wool");
			particleOnly(remainingBlocks, b, wool);
		});

		var apothecaryTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/petal_apothecary")), Optional.empty(),
				TextureSlot.SIDE, TextureSlot.TOP, TextureSlot.BOTTOM);
		takeAll(remainingBlocks, b -> b instanceof PetalApothecaryBlock).forEach(b -> singleVariantBlockState(b,
				apothecaryTemplate.create(b, new TextureMapping()
						.put(TextureSlot.SIDE, getBlockTexture(b, "_side"))
						.put(TextureSlot.TOP, getBlockTexture(b, "_top"))
						.put(TextureSlot.BOTTOM, getBlockTexture(b, "_bottom")), this.modelOutput))
		);

		takeAll(remainingBlocks, b -> b instanceof FloatingFlowerBlock).forEach(b -> {
			// Models generated by FloatingFlowerModelProvider
			singleVariantBlockState(b, getModelLocation(b));
		});

		takeAll(remainingBlocks, b -> b instanceof BotaniaPaneBlock).forEach(b -> {
			String name = BuiltInRegistries.BLOCK.getKey(b).getPath();
			var mapping = new TextureMapping()
					.put(TextureSlot.EDGE, getBlockTexture(b))
					.put(TextureSlot.PANE, prefix("block/" + name.substring(0, name.length() - "_pane".length())));
			ResourceLocation postModel = ModelTemplates.STAINED_GLASS_PANE_POST.create(b, mapping, this.modelOutput);
			ResourceLocation sideModel = ModelTemplates.STAINED_GLASS_PANE_SIDE.create(b, mapping, this.modelOutput);
			ResourceLocation sideAltModel = ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create(b, mapping, this.modelOutput);
			ResourceLocation noSideModel = ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create(b, mapping, this.modelOutput);
			ResourceLocation noSideAltModel = ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create(b, mapping, this.modelOutput);

			// [VanillaCopy] BlockModelGenerator glass panes
			this.blockstates.add(MultiPartGenerator.multiPart(b)
					.with(Variant.variant().with(VariantProperties.MODEL, postModel))
					.with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, sideModel))
					.with(Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant().with(VariantProperties.MODEL, sideModel).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
					.with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, sideAltModel))
					.with(Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, sideAltModel).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
					.with(Condition.condition().term(BlockStateProperties.NORTH, false), Variant.variant().with(VariantProperties.MODEL, noSideModel))
					.with(Condition.condition().term(BlockStateProperties.EAST, false), Variant.variant().with(VariantProperties.MODEL, noSideAltModel))
					.with(Condition.condition().term(BlockStateProperties.SOUTH, false), Variant.variant().with(VariantProperties.MODEL, noSideAltModel).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
					.with(Condition.condition().term(BlockStateProperties.WEST, false), Variant.variant().with(VariantProperties.MODEL, noSideModel).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)));
		});

		takeAll(remainingBlocks, b -> b instanceof StairBlock).forEach(b -> {
			String name = BuiltInRegistries.BLOCK.getKey(b).getPath();
			String baseName = name.substring(0, name.length() - LibBlockNames.STAIR_SUFFIX.length());
			boolean quartz = name.contains("quartz");
			if (quartz) {
				ResourceLocation side = prefix("block/" + baseName + "_side");
				ResourceLocation bottom = prefix("block/" + baseName + "_bottom");
				ResourceLocation top = prefix("block/" + baseName + "_top");
				stairsBlock(new HashSet<>(), b, side, bottom, top);
			} else {
				var tex = prefix("block/" + baseName);
				stairsBlock(new HashSet<>(), b, tex, tex, tex);
			}
		});

		takeAll(remainingBlocks, b -> b instanceof SlabBlock).forEach(slabBlock -> {
			String name = BuiltInRegistries.BLOCK.getKey(slabBlock).getPath();
			String baseName = name.substring(0, name.length() - LibBlockNames.SLAB_SUFFIX.length());
			Block base = BuiltInRegistries.BLOCK.get(prefix(baseName));
			boolean quartz = name.contains("quartz");
			if (quartz) {
				var side = getBlockTexture(base, "_side");
				var bottom = getBlockTexture(base, "_bottom");
				var top = getBlockTexture(base, "_top");
				var doubleModel = getModelLocation(base);
				slabBlock(new HashSet<>(), slabBlock, doubleModel, side, bottom, top);
			} else {
				var baseTex = getBlockTexture(base);
				var doubleModel = getModelLocation(base);
				slabBlock(new HashSet<>(), slabBlock, doubleModel, baseTex, baseTex, baseTex);
			}
		});

		takeAll(remainingBlocks, b -> b instanceof WallBlock).forEach(wallBlock -> {
			String name = BuiltInRegistries.BLOCK.getKey(wallBlock).getPath();
			String baseName = name.substring(0, name.length() - LibBlockNames.WALL_SUFFIX.length());
			Block base = BuiltInRegistries.BLOCK.get(prefix(baseName));
			var baseTexture = getBlockTexture(base);
			wallBlock(new HashSet<>(), wallBlock, baseTexture);
		});

		remainingBlocks.forEach(this::cubeAllNoRemove);
	}

	protected void particleOnly(Set<Block> blocks, Block b, ResourceLocation particle) {
		singleVariantBlockState(b, ModelTemplates.PARTICLE_ONLY.create(b, TextureMapping.particle(particle), this.modelOutput));
		blocks.remove(b);
	}

	protected void manualModel(Set<Block> blocks, Block b) {
		singleVariantBlockState(b, getModelLocation(b));
		blocks.remove(b);
	}

	protected void stairsBlock(Set<Block> blocks, Block block, ResourceLocation sideTex, ResourceLocation bottomTex, ResourceLocation topTex) {
		stairsBlockWithVariants(blocks, block, new ResourceLocation[] { sideTex }, new ResourceLocation[] { bottomTex }, new ResourceLocation[] { topTex });
	}

	protected void checkeredStairsBlock(Set<Block> blocks, Block block, ResourceLocation texture, ResourceLocation mirroredTexture) {
		BiFunction<String, Optional<String>, ModelTemplate> checkeredTemplate = (model, suffix) -> new ModelTemplate(Optional.of(prefix("block/shapes/" + model)), suffix, TextureSlot.SIDE, TextureSlot.NORTH);
		TextureMapping checkeredMapping = new TextureMapping().put(TextureSlot.SIDE, texture).put(TextureSlot.NORTH, mirroredTexture);

		var checkeredStairsModel = checkeredTemplate.apply("stairs_checkered", Optional.empty()).create(biomeBrickMesaStairs, checkeredMapping, this.modelOutput);
		var checkeredStairsModelRot90 = checkeredTemplate.apply("stairs_checkered_90deg", Optional.of("_90deg")).create(biomeBrickMesaStairs, checkeredMapping, this.modelOutput);
		var checkeredStairsOuterModel = checkeredTemplate.apply("stairs_outer_checkered", Optional.of("_outer")).create(biomeBrickMesaStairs, checkeredMapping, this.modelOutput);
		var checkeredStairsOuterModelRot90 = checkeredTemplate.apply("stairs_outer_checkered_90deg", Optional.of("_outer_90deg")).create(biomeBrickMesaStairs, checkeredMapping, this.modelOutput);
		var checkeredStairsInnerModel = checkeredTemplate.apply("stairs_inner_checkered", Optional.of("_inner")).create(biomeBrickMesaStairs, checkeredMapping, this.modelOutput);
		var checkeredStairsInnerModelRot90 = checkeredTemplate.apply("stairs_inner_checkered_90deg", Optional.of("_inner_90deg")).create(biomeBrickMesaStairs, checkeredMapping, this.modelOutput);

		stairsBlockWithModels(blocks, block, checkeredStairsInnerModel, checkeredStairsInnerModelRot90, checkeredStairsModel, checkeredStairsModelRot90, checkeredStairsOuterModel, checkeredStairsOuterModelRot90);
	}

	protected void stairsBlockWithVariants(Set<Block> blocks, Block block, ResourceLocation[] sideTextures, ResourceLocation[] bottomTextures, ResourceLocation[] topTextures) {
		var weights = new Integer[sideTextures.length];
		Arrays.fill(weights, 1);
		stairsBlockWithVariants(blocks, block, sideTextures, bottomTextures, topTextures, weights);
	}

	protected void stairsBlockWithVariants(Set<Block> blocks, Block block, ResourceLocation[] sideTextures, ResourceLocation[] bottomTextures, ResourceLocation[] topTextures, Integer[] weights) {
		int length = sideTextures.length;
		if (length != topTextures.length || length != bottomTextures.length || length != weights.length) {
			throw new IllegalArgumentException("Arrays must have equal length");
		}
		ResourceLocation[] innerModels = new ResourceLocation[length];
		ResourceLocation[] straightModels = new ResourceLocation[length];
		ResourceLocation[] outerModels = new ResourceLocation[length];
		for (int i = 0; i < length; i++) {
			String suffix = i == 0 ? "" : "_" + i;
			var mapping = new TextureMapping()
					.put(TextureSlot.SIDE, sideTextures[i])
					.put(TextureSlot.BOTTOM, bottomTextures[i])
					.put(TextureSlot.TOP, topTextures[i]);
			ResourceLocation modelIdInner = getModelLocation(block, "_inner" + suffix);
			ResourceLocation modelIdStraight = getModelLocation(block, suffix);
			ResourceLocation modelIdOuter = getModelLocation(block, "_outer" + suffix);
			innerModels[i] = ModelTemplates.STAIRS_INNER.create(modelIdInner, mapping, this.modelOutput);
			straightModels[i] = ModelTemplates.STAIRS_STRAIGHT.create(modelIdStraight, mapping, this.modelOutput);
			outerModels[i] = ModelTemplates.STAIRS_OUTER.create(modelIdOuter, mapping, this.modelOutput);
		}
		stairsBlockWithModels(blocks, block, innerModels, straightModels, outerModels, weights);
	}

	protected void stairsBlockWithModels(Set<Block> blocks, Block block, ResourceLocation[] innerModels, ResourceLocation[] straightModels, ResourceLocation[] outerModels, Integer[] weights) {
		stairsBlockWithModels(blocks, block, innerModels, straightModels, outerModels, weights, true);
	}

	protected void stairsBlockWithModels(Set<Block> blocks, Block block, ResourceLocation innerModel, ResourceLocation innerModelRot90, ResourceLocation straightModel, ResourceLocation straightModelRot90, ResourceLocation outerModel, ResourceLocation outerModelRot90) {
		stairsBlockWithModels(blocks, block, new ResourceLocation[] { innerModel }, new ResourceLocation[] { innerModelRot90 }, new ResourceLocation[] { straightModel }, new ResourceLocation[] { straightModelRot90 }, new ResourceLocation[] { outerModel }, new ResourceLocation[] { outerModelRot90 }, new Integer[] { 1 }, true);
	}

	protected void stairsBlockWithModels(Set<Block> blocks, Block block, ResourceLocation[] innerModels, ResourceLocation[] straightModels, ResourceLocation[] outerModels, Integer[] weights, Boolean uvlock) {
		stairsBlockWithModels(blocks, block, innerModels, innerModels, straightModels, straightModels, outerModels, outerModels, weights, uvlock);
	}

	protected void stairsBlockWithModels(Set<Block> blocks, Block block, ResourceLocation[] innerModels, ResourceLocation[] innerModelsRot90, ResourceLocation[] straightModels, ResourceLocation[] straightModelsRot90, ResourceLocation[] outerModels, ResourceLocation[] outerModelsRot90, Integer[] weights, Boolean uvlock) {
		int length = innerModels.length;
		if (length != straightModels.length || length != outerModels.length || length != weights.length) {
			throw new IllegalArgumentException("Arrays must have equal length");
		}
		var propertyDispatch = PropertyDispatch.properties(
				BlockStateProperties.HORIZONTAL_FACING,
				BlockStateProperties.HALF,
				BlockStateProperties.STAIRS_SHAPE
		);
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			for (Half half : Half.values()) {
				for (StairsShape stairsShape : StairsShape.values()) {
					// Stair blockstates are super weird. If it's left and bottom, you need to rotate it 90deg ccw compared to
					// usual, and if it's right and top, you need to rotate it 90deg cw. This is the cleanest way I could think
					// of to do that.

					// Additionally, we're using an alternate model for everything that's rotated 90/270 degrees to be able to
					// have a consistent "checkered" texture for some stairs. Normal stairs just provide the same model twice
					// which makes no impact in the generated blockstate.
					boolean isLeft = stairsShape == StairsShape.INNER_LEFT || stairsShape == StairsShape.OUTER_LEFT;
					boolean isRight = stairsShape == StairsShape.INNER_RIGHT || stairsShape == StairsShape.OUTER_RIGHT;
					int rotationOffset = isLeft && half == Half.BOTTOM ? -1 : isRight && half == Half.TOP ? 1 : 0;

					VariantProperties.Rotation[] rotations = VariantProperties.Rotation.values();
					VariantProperties.Rotation yRot = switch (direction) {
						case EAST -> rotations[(4 + rotationOffset) % 4];
						case WEST -> rotations[(2 + rotationOffset) % 4];
						case SOUTH -> rotations[(1 + rotationOffset) % 4];
						case NORTH -> rotations[(3 + rotationOffset) % 4];
						default -> throw new IllegalStateException();
					};
					VariantProperties.Rotation xRot = switch (half) {
						case BOTTOM -> VariantProperties.Rotation.R0;
						case TOP -> VariantProperties.Rotation.R180;
					};
					boolean rotatedModel = yRot == VariantProperties.Rotation.R90 || yRot == VariantProperties.Rotation.R270;
					ResourceLocation[] models = switch (stairsShape) {
						case STRAIGHT -> rotatedModel ? straightModelsRot90 : straightModels;
						case OUTER_RIGHT, OUTER_LEFT -> rotatedModel ? outerModelsRot90 : outerModels;
						case INNER_RIGHT, INNER_LEFT -> rotatedModel ? innerModelsRot90 : innerModels;
					};
					var indices = IntStream.range(0, length).boxed();
					propertyDispatch.select(direction, half, stairsShape, indices.map(i -> maybeUVLock(uvlock, maybeWeight(weights[i], maybeYRot(yRot, maybeXRot(xRot, Variant.variant()
							.with(VariantProperties.MODEL, models[i])
					))))
					).toList());
				}
			}
		}
		this.blockstates.add(MultiVariantGenerator.multiVariant(block).with(propertyDispatch));
		blocks.remove(block);
	}

	protected void slabBlock(Set<Block> blocks, Block block, ResourceLocation doubleModel, ResourceLocation side, ResourceLocation bottom, ResourceLocation top) {
		slabBlockWithVariants(blocks, block, new ResourceLocation[] { doubleModel }, new ResourceLocation[] { side }, new ResourceLocation[] { bottom }, new ResourceLocation[] { top });
	}

	protected void checkeredSlabBlock(Set<Block> blocks, Block block, ResourceLocation doubleModel, ResourceLocation texture, ResourceLocation mirroredTexture) {
		BiFunction<String, Optional<String>, ModelTemplate> checkeredTemplate = (model, suffix) -> new ModelTemplate(Optional.of(prefix("block/shapes/" + model)), suffix, TextureSlot.SIDE, TextureSlot.NORTH);
		TextureMapping checkeredMapping = new TextureMapping().put(TextureSlot.SIDE, texture).put(TextureSlot.NORTH, mirroredTexture);

		var slabModel = checkeredTemplate.apply("slab_checkered", Optional.empty()).create(biomeBrickMesaSlab, checkeredMapping, this.modelOutput);
		var slabTopModel = checkeredTemplate.apply("slab_top_checkered", Optional.of("_top")).create(biomeBrickMesaSlab, checkeredMapping, this.modelOutput);
		slabBlockWithModels(blocks, block, slabModel, slabTopModel, doubleModel);
	}

	protected void slabBlockWithVariants(Set<Block> blocks, Block block, ResourceLocation[] doubleModels, ResourceLocation[] sideTextures, ResourceLocation[] bottomTextures, ResourceLocation[] topTextures) {
		var weights = new Integer[sideTextures.length];
		Arrays.fill(weights, 1);
		slabBlockWithVariants(blocks, block, doubleModels, sideTextures, bottomTextures, topTextures, weights);
	}

	protected void slabBlockWithVariants(Set<Block> blocks, Block block, ResourceLocation[] doubleModels, ResourceLocation[] sideTextures, ResourceLocation[] bottomTextures, ResourceLocation[] topTextures, Integer[] weights) {
		int length = sideTextures.length;
		if (length != topTextures.length || length != bottomTextures.length || length != weights.length) {
			throw new IllegalArgumentException("Arrays must have equal length");
		}
		ResourceLocation[] bottomModels = new ResourceLocation[length];
		ResourceLocation[] topModels = new ResourceLocation[length];
		for (int i = 0; i < length; i++) {
			String suffix = i == 0 ? "" : "_" + i;
			var mapping = new TextureMapping()
					.put(TextureSlot.SIDE, sideTextures[i])
					.put(TextureSlot.BOTTOM, bottomTextures[i])
					.put(TextureSlot.TOP, topTextures[i]);
			ResourceLocation modelIdBottom = getModelLocation(block, suffix);
			ResourceLocation modelIdTop = getModelLocation(block, "_top" + suffix);
			bottomModels[i] = ModelTemplates.SLAB_BOTTOM.create(modelIdBottom, mapping, this.modelOutput);
			topModels[i] = ModelTemplates.SLAB_TOP.create(modelIdTop, mapping, this.modelOutput);
		}
		slabBlockWithModels(blocks, block, bottomModels, topModels, doubleModels, weights);
	}

	protected void slabBlockWithModels(Set<Block> blocks, Block block, ResourceLocation bottomModel, ResourceLocation topModel, ResourceLocation doubleModel) {
		slabBlockWithModels(blocks, block, new ResourceLocation[] { bottomModel }, new ResourceLocation[] { topModel }, new ResourceLocation[] { doubleModel }, new Integer[] { 1 });
	}

	protected void slabBlockWithModels(Set<Block> blocks, Block block, ResourceLocation[] bottomModels, ResourceLocation[] topModels, ResourceLocation[] doubleModels, Integer[] weights) {
		int length = doubleModels.length;
		if (length != topModels.length || length != bottomModels.length || length != weights.length) {
			throw new IllegalArgumentException("Arrays must have equal length");
		}
		var indicesBottom = IntStream.range(0, length).boxed();
		var indicesTop = IntStream.range(0, length).boxed();
		var indicesDouble = IntStream.range(0, length).boxed();
		this.blockstates.add(MultiVariantGenerator.multiVariant(block).with(
				PropertyDispatch.property(BlockStateProperties.SLAB_TYPE)
						.select(SlabType.BOTTOM, indicesBottom.map(i -> maybeWeight(weights[i], Variant.variant().with(VariantProperties.MODEL, bottomModels[i]))).toList())
						.select(SlabType.TOP, indicesTop.map(i -> maybeWeight(weights[i], Variant.variant().with(VariantProperties.MODEL, topModels[i]))).toList())
						.select(SlabType.DOUBLE, indicesDouble.map(i -> maybeWeight(weights[i], Variant.variant().with(VariantProperties.MODEL, doubleModels[i]))).toList())
		));
		blocks.remove(block);
	}

	protected void wallBlock(Set<Block> blocks, Block block, ResourceLocation texture) {
		wallBlock(blocks, block, texture, texture, texture);
	}

	protected void wallBlock(Set<Block> blocks, Block block, ResourceLocation sideTexture, ResourceLocation bottomTexture, ResourceLocation topTexture) {
		wallBlockWithVariants(blocks, block, new ResourceLocation[] { sideTexture }, new ResourceLocation[] { bottomTexture }, new ResourceLocation[] { topTexture });
	}

	protected void checkeredWallBlock(Set<Block> blocks, Block block, ResourceLocation texture, ResourceLocation mirroredTexture) {
		BiFunction<String, Optional<String>, ModelTemplate> checkeredTemplate = (model, suffix) -> new ModelTemplate(Optional.of(prefix("block/shapes/" + model)), suffix, TextureSlot.SIDE, TextureSlot.NORTH);
		TextureMapping checkeredMapping = new TextureMapping().put(TextureSlot.SIDE, texture).put(TextureSlot.NORTH, mirroredTexture);

		var checkeredWallPostModel = checkeredTemplate.apply("wall_post_checkered", Optional.of("_post")).create(biomeBrickMesaWall, checkeredMapping, this.modelOutput);
		var checkeredWallSideModel = checkeredTemplate.apply("wall_side_checkered", Optional.of("_side")).create(biomeBrickMesaWall, checkeredMapping, this.modelOutput);
		var checkeredWallSideModelRot90 = checkeredTemplate.apply("wall_side_checkered_90deg", Optional.of("_side_90deg")).create(biomeBrickMesaWall, checkeredMapping, this.modelOutput);
		var checkeredWallSideTallModel = checkeredTemplate.apply("wall_side_tall_checkered", Optional.of("_side_tall")).create(biomeBrickMesaWall, checkeredMapping, this.modelOutput);
		var checkeredWallSideTallModelRot90 = checkeredTemplate.apply("wall_side_tall_checkered_90deg", Optional.of("_side_tall_90deg")).create(biomeBrickMesaWall, checkeredMapping, this.modelOutput);

		wallBlockWithModels(blocks, block, checkeredWallPostModel, checkeredWallSideModel, checkeredWallSideModelRot90, checkeredWallSideTallModel, checkeredWallSideTallModelRot90);
	}

	protected void wallBlockWithVariants(Set<Block> blocks, Block block, ResourceLocation[] textures) {
		wallBlockWithVariants(blocks, block, textures, textures, textures);
	}

	protected void wallBlockWithVariants(Set<Block> blocks, Block block, ResourceLocation[] textures, Integer[] weights) {
		wallBlockWithVariants(blocks, block, textures, textures, textures, weights);
	}

	protected void wallBlockWithVariants(Set<Block> blocks, Block block, ResourceLocation[] sideTextures, ResourceLocation[] bottomTextures, ResourceLocation[] topTextures) {
		var weights = new Integer[sideTextures.length];
		Arrays.fill(weights, 1);
		wallBlockWithVariants(blocks, block, sideTextures, bottomTextures, topTextures, weights);
	}

	protected void wallBlockWithVariants(Set<Block> blocks, Block block, ResourceLocation[] sideTextures, ResourceLocation[] bottomTextures, ResourceLocation[] topTextures, Integer[] weights) {
		int length = sideTextures.length;
		if (length != bottomTextures.length && length != topTextures.length && length != weights.length) {
			throw new IllegalArgumentException("Arrays must have equal length");
		}
		ResourceLocation[] postModels = new ResourceLocation[length];
		ResourceLocation[] lowModels = new ResourceLocation[length];
		ResourceLocation[] tallModels = new ResourceLocation[length];
		for (int i = 0; i < length; i++) {
			String suffix = i == 0 ? "" : "_" + i;
			var mapping = new TextureMapping()
					.put(TextureSlot.WALL, sideTextures[i])
					.put(TextureSlot.BOTTOM, bottomTextures[i])
					.put(TextureSlot.TOP, topTextures[i]);
			ResourceLocation modelIdPost = getModelLocation(block, "_post" + suffix);
			ResourceLocation modelIdLow = getModelLocation(block, "_side" + suffix);
			ResourceLocation modelIdTall = getModelLocation(block, "_side_tall" + suffix);
			var postTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/wall_post")), Optional.of("_post"),
					TextureSlot.WALL, TextureSlot.BOTTOM, TextureSlot.TOP);
			var sideTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/wall_side")), Optional.of("_side"),
					TextureSlot.WALL, TextureSlot.BOTTOM, TextureSlot.TOP);
			var sideTallTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/wall_side_tall")), Optional.of("_side_tall"),
					TextureSlot.WALL, TextureSlot.BOTTOM, TextureSlot.TOP);
			postModels[i] = postTemplate.create(modelIdPost, mapping, this.modelOutput);
			lowModels[i] = sideTemplate.create(modelIdLow, mapping, this.modelOutput);
			tallModels[i] = sideTallTemplate.create(modelIdTall, mapping, this.modelOutput);
		}
		wallBlockWithModels(blocks, block, postModels, lowModels, tallModels, weights);
	}

	protected void wallBlockWithModels(Set<Block> blocks, Block block, ResourceLocation[] postModels, ResourceLocation[] lowModels, ResourceLocation[] tallModels, Integer[] weights) {
		wallBlockWithModels(blocks, block, postModels, lowModels, tallModels, weights, true);
	}

	protected void wallBlockWithModels(Set<Block> blocks, Block block, ResourceLocation postModel, ResourceLocation lowModel, ResourceLocation lowModelRot90, ResourceLocation tallModel, ResourceLocation tallodelRot90) {
		wallBlockWithModels(blocks, block, new ResourceLocation[] { postModel }, new ResourceLocation[] { lowModel }, new ResourceLocation[] { lowModelRot90 }, new ResourceLocation[] { tallModel }, new ResourceLocation[] { tallodelRot90 }, new Integer[] { 1 }, true);
	}

	protected void wallBlockWithModels(Set<Block> blocks, Block block, ResourceLocation[] postModels, ResourceLocation[] lowModels, ResourceLocation[] tallModels, Integer[] weights, Boolean uvlock) {
		wallBlockWithModels(blocks, block, postModels, lowModels, lowModels, tallModels, tallModels, weights, uvlock);
	}

	protected void wallBlockWithModels(Set<Block> blocks, Block block, ResourceLocation[] postModels, ResourceLocation[] lowModels, ResourceLocation[] lowModelsRot90, ResourceLocation[] tallModels, ResourceLocation[] tallodelsRot90, Integer[] weights, Boolean uvlock) {
		int length = postModels.length;
		if (length != lowModels.length || length != tallModels.length || length != weights.length) {
			throw new IllegalArgumentException("Arrays must have equal length");
		}
		var multiPartGenerator = MultiPartGenerator.multiPart(block);
		var indicesPost = IntStream.range(0, length).boxed();
		multiPartGenerator.with(Condition.condition().term(BlockStateProperties.UP, true),
				indicesPost.map(i -> maybeWeight(weights[i], Variant.variant().with(VariantProperties.MODEL, postModels[i]))).toArray(Variant[]::new));
		var wallSides = List.of(BlockStateProperties.EAST_WALL, BlockStateProperties.WEST_WALL, BlockStateProperties.SOUTH_WALL, BlockStateProperties.NORTH_WALL);

		// We're using an alternate model for everything that's rotated 90/270 degrees to be able to
		// have a consistent "checkered" texture for some walls. Normal walls just provide the same model twice
		// which makes no impact in the generated blockstate.
		for (EnumProperty<WallSide> wallSide : wallSides) {
			VariantProperties.Rotation yRot =
					wallSide == BlockStateProperties.EAST_WALL ? VariantProperties.Rotation.R90
							: wallSide == BlockStateProperties.WEST_WALL ? VariantProperties.Rotation.R270
							: wallSide == BlockStateProperties.SOUTH_WALL ? VariantProperties.Rotation.R180
							: VariantProperties.Rotation.R0;
			boolean rotatedModel = yRot == VariantProperties.Rotation.R90 || yRot == VariantProperties.Rotation.R270;
			var indicesLow = IntStream.range(0, length).boxed();
			var indicesTall = IntStream.range(0, length).boxed();
			multiPartGenerator
					.with(Condition.condition().term(wallSide, WallSide.LOW), indicesLow.map(i -> maybeUVLock(uvlock, maybeWeight(weights[i], maybeYRot(yRot,
							Variant.variant()
									.with(VariantProperties.MODEL, rotatedModel ? lowModelsRot90[i] : lowModels[i])
					)))).toArray(Variant[]::new))
					.with(Condition.condition().term(wallSide, WallSide.TALL), indicesTall.map(i -> maybeUVLock(uvlock, maybeWeight(weights[i], maybeYRot(yRot,
							Variant.variant()
									.with(VariantProperties.MODEL, rotatedModel ? tallodelsRot90[i] : tallModels[i])
					)))).toArray(Variant[]::new));
		}
		this.blockstates.add(multiPartGenerator);
		blocks.remove(block);
	}

	protected void fenceBlock(Set<Block> blocks, Block block, ResourceLocation tex) {
		var mapping = TextureMapping.defaultTexture(tex);
		var postModel = ModelTemplates.FENCE_POST.create(block, mapping, this.modelOutput);
		var sideModel = ModelTemplates.FENCE_SIDE.create(block, mapping, this.modelOutput);
		this.blockstates.add(BlockModelGeneratorsAccessor.makeFenceState(block, postModel, sideModel));
		blocks.remove(block);
	}

	protected void fenceGateBlock(Set<Block> blocks, Block block, ResourceLocation tex) {
		var mapping = TextureMapping.defaultTexture(tex);
		var openModel = ModelTemplates.FENCE_GATE_OPEN.create(block, mapping, this.modelOutput);
		var closedModel = ModelTemplates.FENCE_GATE_CLOSED.create(block, mapping, this.modelOutput);
		var openWallModel = ModelTemplates.FENCE_GATE_WALL_OPEN.create(block, mapping, this.modelOutput);
		var closedWallModel = ModelTemplates.FENCE_GATE_WALL_CLOSED.create(block, mapping, this.modelOutput);
		this.blockstates.add(BlockModelGeneratorsAccessor.makeFenceGateState(block, openModel, closedModel, openWallModel, closedWallModel, false));
		blocks.remove(block);
	}

	protected void cubeAllNoRemove(Block block) {
		cubeAll(new HashSet<>(), block);
	}

	protected void cubeAll(Set<Block> blocks, Block block) {
		ResourceLocation texture = getBlockTexture(block);
		cubeAllWithVariants(blocks, block, new ResourceLocation[] { texture });
	}

	protected ResourceLocation checkeredBlockWithBlockstate(Set<Block> blocks, Block block, ResourceLocation texture, ResourceLocation mirroredTexture) {
		BiFunction<String, Optional<String>, ModelTemplate> checkeredTemplate = (model, suffix) -> new ModelTemplate(Optional.of(prefix("block/shapes/" + model)), suffix, TextureSlot.SIDE, TextureSlot.NORTH);
		TextureMapping checkeredMapping = new TextureMapping().put(TextureSlot.SIDE, texture).put(TextureSlot.NORTH, mirroredTexture);

		var blockModel = checkeredTemplate.apply("cube_checkered", Optional.empty()).create(block, checkeredMapping, this.modelOutput);
		cubeAllWithModels(blocks, block, new ResourceLocation[] { blockModel }, new Integer[] { 1 });
		return blockModel;
	}

	protected void cubeAllWithVariants(Set<Block> blocks, Block block, ResourceLocation[] textures) {
		var weights = new Integer[textures.length];
		Arrays.fill(weights, 1);
		cubeAllWithVariants(blocks, block, textures, weights);
	}

	protected void cubeAllWithVariants(Set<Block> blocks, Block block, ResourceLocation[] textures, Integer[] weights) {
		int length = textures.length;
		if (length != weights.length) {
			throw new IllegalArgumentException("Arrays must have equal length");
		}
		ResourceLocation[] models = new ResourceLocation[length];
		for (int i = 0; i < length; i++) {
			String suffix = i == 0 ? "" : "_" + i;
			ResourceLocation modelId = getModelLocation(block, suffix);
			models[i] = ModelTemplates.CUBE_ALL.create(modelId, TextureMapping.cube(textures[i]), this.modelOutput);
		}
		cubeAllWithModels(blocks, block, models, weights);
	}

	protected void cubeAllWithModels(Set<Block> blocks, Block block, ResourceLocation[] models, Integer[] weights) {
		int length = models.length;
		if (length != weights.length) {
			throw new IllegalArgumentException("Arrays must have equal length");
		}
		var indices = IntStream.range(0, length).boxed();
		this.blockstates.add(MultiVariantGenerator.multiVariant(block, indices.map(i -> maybeWeight(weights[i], Variant.variant().with(VariantProperties.MODEL, models[i]))
		).toArray(Variant[]::new)));
		blocks.remove(block);
	}

	protected void singleVariantBlockState(Block b, ResourceLocation model) {
		this.blockstates.add(MultiVariantGenerator.multiVariant(b, Variant.variant().with(VariantProperties.MODEL, model)));
	}

	protected void rotatedMirrored(Set<Block> blocks, Block block, ResourceLocation texture) {
		rotatedMirroredWithVariants(blocks, block, new ResourceLocation[] { texture });
	}

	protected void rotatedMirroredWithVariants(Set<Block> blocks, Block block, ResourceLocation[] textures) {
		var weights = new Integer[textures.length];
		Arrays.fill(weights, 1);
		rotatedMirroredWithVariants(blocks, block, textures, weights);
	}

	protected void rotatedMirroredWithVariants(Set<Block> blocks, Block block, ResourceLocation[] textures, Integer[] weights) {
		int length = textures.length;
		if (length != weights.length) {
			throw new IllegalArgumentException("Arrays must have equal length");
		}
		ResourceLocation[] models = new ResourceLocation[length];
		ResourceLocation[] mirroredModels = new ResourceLocation[length];
		for (int i = 0; i < length; i++) {
			String suffix = i == 0 ? "" : "_" + i;
			ResourceLocation modelId = getModelLocation(block, suffix);
			ResourceLocation mirriredModelId = getModelLocation(block, "_mirrored" + suffix);
			models[i] = ModelTemplates.CUBE_ALL.create(modelId, TextureMapping.cube(textures[i]), this.modelOutput);
			mirroredModels[i] = ModelTemplates.CUBE_MIRRORED_ALL.create(mirriredModelId, TextureMapping.cube(textures[i]), this.modelOutput);
		}
		rotatedMirroredWithModels(blocks, block, models, mirroredModels, weights);
	}

	protected void rotatedMirroredWithModels(Set<Block> blocks, Block block, ResourceLocation[] models, ResourceLocation[] mirroredModels, Integer[] weights) {
		int length = models.length;
		if (length != mirroredModels.length || length != weights.length) {
			throw new IllegalArgumentException("Arrays must have equal length");
		}
		var indices = IntStream.range(0, length).boxed();
		this.blockstates.add(MultiVariantGenerator.multiVariant(block, indices.flatMap(i -> Stream.of(
				maybeWeight(weights[i], Variant.variant().with(VariantProperties.MODEL, models[i])),
				maybeWeight(weights[i], Variant.variant().with(VariantProperties.MODEL, mirroredModels[i])),
				maybeWeight(weights[i], Variant.variant()
						.with(VariantProperties.MODEL, models[i])
						.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)),
				maybeWeight(weights[i], Variant.variant()
						.with(VariantProperties.MODEL, mirroredModels[i])
						.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
		)).toArray(Variant[]::new)));
		blocks.remove(block);
	}

	protected void pillar(Set<Block> blocks, Block block, ResourceLocation top, ResourceLocation side) {
		pillarWithVariants(blocks, block, new ResourceLocation[] { top }, new ResourceLocation[] { side });
	}

	protected void pillarWithVariants(Set<Block> blocks, Block block, ResourceLocation[] topTextures, ResourceLocation[] sideTextures) {
		var weights = new Integer[topTextures.length];
		Arrays.fill(weights, 1);
		pillarWithVariants(blocks, block, topTextures, sideTextures, weights);
	}

	protected void pillarWithVariants(Set<Block> blocks, Block block, ResourceLocation[] topTextures, ResourceLocation[] sideTextures, Integer[] weights) {
		int length = topTextures.length;
		if (length != sideTextures.length || length != weights.length) {
			throw new IllegalArgumentException("Arrays must have equal length");
		}
		ResourceLocation[] topModels = new ResourceLocation[length];
		ResourceLocation[] horizontalModels = new ResourceLocation[length];
		for (int i = 0; i < length; i++) {
			String suffix = i == 0 ? "" : "_" + i;
			ResourceLocation modelIdTop = getModelLocation(block, suffix);
			ResourceLocation modelIdHorizontal = getModelLocation(block, "_horizontal" + suffix);
			topModels[i] = ModelTemplates.CUBE_COLUMN.create(modelIdTop, TextureMapping.column(sideTextures[i], topTextures[i]), this.modelOutput);
			horizontalModels[i] = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(modelIdHorizontal, TextureMapping.column(sideTextures[i], topTextures[i]), this.modelOutput);
		}
		pillarWithModels(blocks, block, topModels, horizontalModels, weights);
	}

	protected void pillarWithModels(Set<Block> blocks, Block block, ResourceLocation[] topModels, ResourceLocation[] horizontalModels, Integer[] weights) {
		int length = topModels.length;
		if (length != horizontalModels.length || length != weights.length) {
			throw new IllegalArgumentException("Arrays must have equal length");
		}
		var indicesX = IntStream.range(0, length).boxed();
		var indicesY = IntStream.range(0, length).boxed();
		var indicesZ = IntStream.range(0, length).boxed();
		this.blockstates.add(MultiVariantGenerator.multiVariant(block).with(
				PropertyDispatch.property(BlockStateProperties.AXIS)
						.select(Direction.Axis.Y, indicesX.map(i -> maybeWeight(weights[i], Variant.variant()
								.with(VariantProperties.MODEL, topModels[i]))).toList())
						.select(Direction.Axis.Z, indicesY.map(i -> maybeWeight(weights[i], Variant.variant()
								.with(VariantProperties.MODEL, horizontalModels[i])
								.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))).toList())
						.select(Direction.Axis.X, indicesZ.map(i -> maybeWeight(weights[i], Variant.variant()
								.with(VariantProperties.MODEL, horizontalModels[i])
								.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))).toList())
		));
		blocks.remove(block);
	}

	// Alternative pillar model that rotates and mirrors some additional faces
	protected void pillarAlt(Set<Block> blocks, Block block, ResourceLocation top, ResourceLocation side) {
		pillarAltWithVariants(blocks, block, new ResourceLocation[] { top }, new ResourceLocation[] { side });
	}

	protected void pillarAltWithVariants(Set<Block> blocks, Block block, ResourceLocation[] topTextures, ResourceLocation[] sideTextures) {
		int length = topTextures.length;
		if (length != sideTextures.length) {
			throw new IllegalArgumentException("Arrays must have equal length");
		}
		ResourceLocation[] topModels = new ResourceLocation[length];
		ResourceLocation[] horizontalXModels = new ResourceLocation[length];
		ResourceLocation[] horizontalZModels = new ResourceLocation[length];
		ModelTemplate horizontalXTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/cube_column_horizontal_x")), Optional.of("_horizontal_x"), TextureSlot.END, TextureSlot.SIDE);
		ModelTemplate horizontalZTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/cube_column_horizontal_z")), Optional.of("_horizontal_z"), TextureSlot.END, TextureSlot.SIDE);
		for (int i = 0; i < length; i++) {
			String suffix = i == 0 ? "" : "_" + i;
			ResourceLocation modelIdTop = getModelLocation(block, suffix);
			ResourceLocation modelIdHorizontalX = getModelLocation(block, "_horizontal_x" + suffix);
			ResourceLocation modelIdHorizontalZ = getModelLocation(block, "_horizontal_z" + suffix);
			topModels[i] = ModelTemplates.CUBE_COLUMN.create(modelIdTop, TextureMapping.column(sideTextures[i], topTextures[i]), this.modelOutput);
			horizontalXModels[i] = horizontalXTemplate.create(modelIdHorizontalX, TextureMapping.column(sideTextures[i], topTextures[i]), this.modelOutput);
			horizontalZModels[i] = horizontalZTemplate.create(modelIdHorizontalZ, TextureMapping.column(sideTextures[i], topTextures[i]), this.modelOutput);
		}
		pillarAltWithModels(blocks, block, topModels, horizontalXModels, horizontalZModels);
	}

	protected void pillarAltWithModels(Set<Block> blocks, Block block, ResourceLocation[] yModels, ResourceLocation[] xModels, ResourceLocation[] zModels) {
		this.blockstates.add(MultiVariantGenerator.multiVariant(block).with(
				PropertyDispatch.property(BlockStateProperties.AXIS)
						.select(Direction.Axis.Y, Stream.of(yModels).map(rl -> Variant.variant().with(VariantProperties.MODEL, rl)).toList())
						.select(Direction.Axis.X, Stream.of(xModels).map(rl -> Variant.variant().with(VariantProperties.MODEL, rl)).toList())
						.select(Direction.Axis.Z, Stream.of(zModels).map(rl -> Variant.variant().with(VariantProperties.MODEL, rl)).toList())
		));
		blocks.remove(block);
	}

	protected void directionalPillar(Set<Block> blocks, Block block, ResourceLocation top, ResourceLocation bottom, ResourceLocation side) {
		directionalPillarWithVariants(blocks, block, new ResourceLocation[] { top }, new ResourceLocation[] { top }, new ResourceLocation[] { side });
	}

	protected void directionalPillarWithVariants(Set<Block> blocks, Block block, ResourceLocation[] topTextures, ResourceLocation[] bottomTextures, ResourceLocation[] sideTextures) {
		var weights = new Integer[topTextures.length];
		Arrays.fill(weights, 1);
		directionalPillarWithVariants(blocks, block, topTextures, bottomTextures, sideTextures, weights);
	}

	protected void directionalPillarWithVariants(Set<Block> blocks, Block block, ResourceLocation[] topTextures, ResourceLocation[] bottomTextures, ResourceLocation[] sideTextures, Integer[] weights) {
		int length = topTextures.length;
		if (length != bottomTextures.length || length != sideTextures.length || length != weights.length) {
			throw new IllegalArgumentException("Arrays must have equal length");
		}
		ResourceLocation[] topModels = new ResourceLocation[length];
		ResourceLocation[] horizontalModels = new ResourceLocation[length];
		ModelTemplate topTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/cube_column_directional")), Optional.empty(), TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
		ModelTemplate horizontalTemplate = new ModelTemplate(Optional.of(prefix("block/shapes/cube_column_directional_horizontal")), Optional.of("_horizontal"), TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE);
		for (int i = 0; i < length; i++) {
			TextureMapping mapping = new TextureMapping()
					.put(TextureSlot.SIDE, sideTextures[i])
					.put(TextureSlot.TOP, topTextures[i])
					.put(TextureSlot.BOTTOM, bottomTextures[i]);
			String suffix = i == 0 ? "" : "_" + i;
			ResourceLocation modelIdTop = getModelLocation(block, suffix);
			ResourceLocation modelIdHorizontal = getModelLocation(block, "_horizontal" + suffix);
			topModels[i] = topTemplate.create(modelIdTop, mapping, this.modelOutput);
			horizontalModels[i] = horizontalTemplate.create(modelIdHorizontal, mapping, this.modelOutput);
		}
		directionalPillarWithModels(blocks, block, topModels, horizontalModels, weights);
	}

	protected void directionalPillarWithModels(Set<Block> blocks, Block block, ResourceLocation[] topModels, ResourceLocation[] horizontalModels, Integer[] weights) {
		int length = topModels.length;
		if (length != horizontalModels.length || length != weights.length) {
			throw new IllegalArgumentException("Arrays must have equal length");
		}
		var indicesUp = IntStream.range(0, length).boxed();
		var indicesDown = IntStream.range(0, length).boxed();
		var indicesNorth = IntStream.range(0, length).boxed();
		var indicesSouth = IntStream.range(0, length).boxed();
		var indicesEast = IntStream.range(0, length).boxed();
		var indicesWest = IntStream.range(0, length).boxed();
		this.blockstates.add(MultiVariantGenerator.multiVariant(block).with(
				PropertyDispatch.property(BlockStateProperties.FACING)
						.select(Direction.UP, indicesUp.map(i -> maybeWeight(weights[i], Variant.variant()
								.with(VariantProperties.MODEL, topModels[i]))).toList())
						.select(Direction.DOWN, indicesDown.map(i -> maybeWeight(weights[i], Variant.variant()
								.with(VariantProperties.MODEL, topModels[i])
								.with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))).toList())
						.select(Direction.NORTH, indicesNorth.map(i -> maybeWeight(weights[i], Variant.variant()
								.with(VariantProperties.MODEL, horizontalModels[i]))).toList())
						.select(Direction.SOUTH, indicesSouth.map(i -> maybeWeight(weights[i], Variant.variant()
								.with(VariantProperties.MODEL, horizontalModels[i])
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))).toList())
						.select(Direction.EAST, indicesEast.map(i -> maybeWeight(weights[i], Variant.variant()
								.with(VariantProperties.MODEL, horizontalModels[i])
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))).toList())
						.select(Direction.WEST, indicesWest.map(i -> maybeWeight(weights[i], Variant.variant()
								.with(VariantProperties.MODEL, horizontalModels[i])
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))).toList())
		));
		blocks.remove(block);
	}

	protected <T> Variant withMaybe(VariantProperty<T> property, T value, boolean shouldAdd, Variant variant) {
		if (shouldAdd) {
			variant.with(property, value);
		}
		return variant;
	}

	protected Variant maybeUVLock(Boolean uvlock, Variant variant) {
		return withMaybe(VariantProperties.UV_LOCK, uvlock, uvlock, variant);
	}

	protected Variant maybeWeight(int weight, Variant variant) {
		return withMaybe(VariantProperties.WEIGHT, weight, weight != 1, variant);
	}

	protected Variant maybeXRot(VariantProperties.Rotation rotation, Variant variant) {
		return withMaybe(VariantProperties.X_ROT, rotation, rotation != VariantProperties.Rotation.R0, variant);
	}

	protected Variant maybeYRot(VariantProperties.Rotation rotation, Variant variant) {
		return withMaybe(VariantProperties.Y_ROT, rotation, rotation != VariantProperties.Rotation.R0, variant);
	}

	@SafeVarargs
	@SuppressWarnings("varargs")
	public final <T> Collection<T> takeAll(Set<T> src, T... items) {
		List<T> ret = Arrays.asList(items);
		for (T item : items) {
			if (!src.contains(item)) {
				getLogger().warn("Item {} not found in set", item);
			}
		}
		if (!src.removeAll(ret)) {
			getLogger().warn("takeAll array didn't yield anything ({})", Arrays.toString(items));
		}
		return ret;
	}

	public final <T> Collection<T> takeAll(Set<T> src, Predicate<T> pred) {
		List<T> ret = new ArrayList<>();

		Iterator<T> iter = src.iterator();
		while (iter.hasNext()) {
			T item = iter.next();
			if (pred.test(item)) {
				iter.remove();
				ret.add(item);
			}
		}

		if (ret.isEmpty()) {
			getLogger().warn("takeAll predicate yielded nothing", new Throwable());
		}
		return ret;
	}

	protected void redStringBlock(Block b) {
		ResourceLocation selfName = getBlockTexture(b);
		ResourceLocation front = prefix("block/red_string_sender");
		var model = ModelTemplates.CUBE_ORIENTABLE.create(b, new TextureMapping()
				.put(TextureSlot.TOP, selfName)
				.put(TextureSlot.FRONT, front)
				.put(TextureSlot.SIDE, selfName), this.modelOutput);
		this.blockstates.add(MultiVariantGenerator.multiVariant(b, Variant.variant().with(VariantProperties.MODEL, model))
				.with(BlockModelGeneratorsAccessor.facingDispatch()));
	}
}
