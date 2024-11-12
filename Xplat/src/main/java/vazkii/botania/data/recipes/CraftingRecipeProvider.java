/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data.recipes;

import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import org.apache.commons.lang3.mutable.MutableObject;

import vazkii.botania.api.state.enums.CraftyCratePattern;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.crafting.recipe.*;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.common.lib.LibBlockNames;
import vazkii.botania.common.lib.LibItemNames;
import vazkii.botania.common.lib.ResourceLocationHelper;
import vazkii.botania.mixin.RecipeProviderAccessor;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CraftingRecipeProvider extends BotaniaRecipeProvider {
	public CraftingRecipeProvider(PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	public void buildRecipes(Consumer<FinishedRecipe> consumer) {
		specialRecipe(consumer, AncientWillRecipe.SERIALIZER);
		specialRecipe(consumer, BlackHoleTalismanExtractRecipe.SERIALIZER);
		specialRecipe(consumer, CompositeLensRecipe.SERIALIZER);
		specialRecipe(consumer, CosmeticAttachRecipe.SERIALIZER);
		specialRecipe(consumer, CosmeticRemoveRecipe.SERIALIZER);
		specialRecipe(consumer, ResoluteIvyRecipe.SERIALIZER);
		specialRecipe(consumer, LaputaShardUpgradeRecipe.SERIALIZER);
		specialRecipe(consumer, LensDyeingRecipe.SERIALIZER);
		specialRecipe(consumer, ManaBlasterClipRecipe.SERIALIZER);
		specialRecipe(consumer, ManaBlasterLensRecipe.SERIALIZER);
		specialRecipe(consumer, ManaBlasterRemoveLensRecipe.SERIALIZER);
		specialRecipe(consumer, MergeVialRecipe.SERIALIZER);
		specialRecipe(consumer, PhantomInkRecipe.SERIALIZER);
		specialRecipe(consumer, SpellbindingClothRecipe.SERIALIZER);
		specialRecipe(consumer, SplitLensRecipe.SERIALIZER);
		specialRecipe(consumer, TerraShattererTippingRecipe.SERIALIZER);

		registerMain(consumer);
		registerMisc(consumer);
		registerTools(consumer);
		registerTrinkets(consumer);
		registerLenses(consumer);
		registerCorporeaAndRedString(consumer);
		registerFloatingFlowers(consumer);
		registerConversions(consumer);
		registerDecor(consumer);
	}

	public static InventoryChangeTrigger.TriggerInstance conditionsFromItem(ItemLike item) {
		return RecipeProviderAccessor.botania_condition(ItemPredicate.Builder.item().of(item).build());
	}

	private static InventoryChangeTrigger.TriggerInstance conditionsFromItems(ItemLike... items) {
		ItemPredicate[] preds = new ItemPredicate[items.length];
		for (int i = 0; i < items.length; i++) {
			preds[i] = ItemPredicate.Builder.item().of(items[i]).build();
		}

		return RecipeProviderAccessor.botania_condition(preds);
	}

	public static InventoryChangeTrigger.TriggerInstance conditionsFromTag(TagKey<Item> tag) {
		return RecipeProviderAccessor.botania_condition(ItemPredicate.Builder.item().of(tag).build());
	}

	/** Addons: override this to return your modid */
	protected ResourceLocation prefix(String path) {
		return ResourceLocationHelper.prefix(path);
	}

	private void registerMain(Consumer<FinishedRecipe> consumer) {
		InventoryChangeTrigger.TriggerInstance hasAnyDye = conditionsFromItems(
				ColorHelper.supportedColors().map(DyeItem::byColor).toArray(ItemLike[]::new)
		);
		MutableObject<FinishedRecipe> base = new MutableObject<>();
		MutableObject<FinishedRecipe> gog = new MutableObject<>();
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BotaniaBlocks.manaSpreader)
				.define('P', BotaniaTags.Items.PETALS)
				.define('W', BotaniaTags.Items.LIVINGWOOD_LOGS)
				.define('C', Items.COPPER_INGOT)
				.pattern("WWW")
				.pattern("CP ")
				.pattern("WWW")
				.group("botania:spreader")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.LIVINGWOOD_LOGS))
				.save(base::setValue);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BotaniaBlocks.manaSpreader)
				.define('P', BotaniaTags.Items.PETALS)
				.define('W', BotaniaTags.Items.LIVINGWOOD_LOGS)
				.pattern("WWW")
				.pattern("WP ")
				.pattern("WWW")
				.group("botania:spreader")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.LIVINGWOOD_LOGS))
				.save(gog::setValue);
		consumer.accept(new GogAlternationResult(gog.getValue(), base.getValue()));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, BotaniaBlocks.redstoneSpreader)
				.requires(BotaniaBlocks.manaSpreader)
				.requires(Items.REDSTONE)
				.group("botania:spreader")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.manaSpreader))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BotaniaBlocks.elvenSpreader)
				.define('P', BotaniaTags.Items.PETALS)
				.define('E', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.define('W', BotaniaTags.Items.DREAMWOOD_LOGS)
				.pattern("WWW")
				.pattern("EP ")
				.pattern("WWW")
				.group("botania:spreader")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_ELEMENTIUM))
				.unlockedBy("has_alt_item", conditionsFromTag(BotaniaTags.Items.DREAMWOOD_LOGS))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, BotaniaBlocks.gaiaSpreader)
				.requires(BotaniaBlocks.elvenSpreader)
				.requires(BotaniaTags.Items.GEMS_DRAGONSTONE)
				.requires(BotaniaItems.lifeEssence)
				.group("botania:spreader")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.manaPool)
				.define('R', BotaniaBlocks.livingrock)
				.pattern("R R")
				.pattern("RRR")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.livingrock))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.dilutedPool)
				.define('R', BotaniaBlocks.livingrockSlab)
				.pattern("R R")
				.pattern("RRR")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.livingrock))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.fabulousPool)
				.define('R', BotaniaBlocks.shimmerrock)
				.pattern("R R")
				.pattern("RRR")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.shimmerrock))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaItems.rainbowRod))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.fabulousPool)
				.define('P', BotaniaBlocks.manaPool)
				.define('B', BotaniaBlocks.bifrostPerm)
				.pattern("BPB")
				.pattern("BBB")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.bifrostPerm))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaItems.rainbowRod))
				.save(consumer, prefix(BuiltInRegistries.ITEM.getKey(BotaniaBlocks.fabulousPool.asItem()).getPath() + "_upgrade"));
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.runeAltar)
				.define('P', BotaniaItems.manaPearl)
				.define('S', BotaniaBlocks.livingrock)
				.pattern("SSS")
				.pattern("SPS")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaPearl))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.runeAltar)
				.define('P', BotaniaTags.Items.GEMS_MANA_DIAMOND)
				.define('S', BotaniaBlocks.livingrock)
				.pattern("SSS")
				.pattern("SPS")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.GEMS_MANA_DIAMOND))
				.save(consumer, prefix("runic_altar_alt"));
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.manaPylon)
				.define('D', BotaniaTags.Items.GEMS_MANA_DIAMOND)
				.define('G', Items.GOLD_INGOT)
				.define('M', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern(" G ")
				.pattern("MDM")
				.pattern(" G ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.GEMS_MANA_DIAMOND))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.naturaPylon)
				.define('P', BotaniaBlocks.manaPylon)
				.define('T', BotaniaTags.Items.NUGGETS_TERRASTEEL)
				.define('E', Items.ENDER_EYE)
				.pattern(" T ")
				.pattern("TPT")
				.pattern(" E ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.manaPylon))
				.unlockedBy("has_alt_item", conditionsFromTag(BotaniaTags.Items.INGOTS_TERRASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.gaiaPylon)
				.define('P', BotaniaBlocks.manaPylon)
				.define('D', BotaniaItems.pixieDust)
				.define('E', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.pattern(" D ")
				.pattern("EPE")
				.pattern(" D ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.pixieDust))
				.unlockedBy("has_alt_item", conditionsFromTag(BotaniaTags.Items.INGOTS_ELEMENTIUM))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.distributor)
				.define('R', BotaniaBlocks.livingrock)
				.define('S', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern("RRR")
				.pattern("S S")
				.pattern("RRR")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.manaVoid)
				.define('S', BotaniaBlocks.livingrock)
				.define('O', Items.OBSIDIAN)
				.pattern("SSS")
				.pattern("O O")
				.pattern("SSS")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.livingrock))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BotaniaBlocks.manaDetector)
				.define('R', Items.REDSTONE)
				.define('T', Blocks.TARGET)
				.define('S', BotaniaBlocks.livingrock)
				.pattern("RSR")
				.pattern("STS")
				.pattern("RSR")
				.unlockedBy("has_item", conditionsFromItem(Blocks.TARGET))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaBlocks.livingrock))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.turntable)
				.define('P', Items.STICKY_PISTON)
				.define('W', BotaniaTags.Items.LIVINGWOOD_LOGS)
				.pattern("WWW")
				.pattern("WPW")
				.pattern("WWW")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.LIVINGWOOD_LOGS))
				.unlockedBy("has_alt_item", conditionsFromItem(Items.STICKY_PISTON))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.tinyPlanet)
				.define('P', BotaniaItems.tinyPlanet)
				.define('S', Items.STONE)
				.pattern("SSS")
				.pattern("SPS")
				.pattern("SSS")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.tinyPlanet))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaItems.manaPearl))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.alchemyCatalyst)
				.define('P', BotaniaItems.manaPearl)
				.define('B', Items.BREWING_STAND)
				.define('S', BotaniaBlocks.livingrock)
				.define('G', Items.GOLD_INGOT)
				.pattern("SGS")
				.pattern("BPB")
				.pattern("SGS")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaPearl))
				.unlockedBy("has_alt_item", conditionsFromItem(Items.BREWING_STAND))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BotaniaBlocks.openCrate)
				.define('W', BotaniaBlocks.livingwoodPlanks)
				.pattern("WWW")
				.pattern("W W")
				.pattern("W W")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.livingwoodPlanks))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BotaniaBlocks.craftCrate)
				.define('C', Items.CRAFTING_TABLE)
				.define('W', BotaniaBlocks.dreamwoodPlanks)
				.pattern("WCW")
				.pattern("W W")
				.pattern("W W")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.dreamwoodPlanks))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BotaniaBlocks.forestEye)
				.define('S', BotaniaBlocks.livingrock)
				.define('E', Items.ENDER_EYE)
				.define('M', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern("MSM")
				.pattern("SES")
				.pattern("MSM")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.wildDrum)
				.define('W', BotaniaTags.Items.LIVINGWOOD_LOGS)
				.define('H', BotaniaItems.grassHorn)
				.define('L', Items.LEATHER)
				.pattern("WLW")
				.pattern("WHW")
				.pattern("WLW")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.grassHorn))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.gatheringDrum)
				.define('E', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.define('W', BotaniaTags.Items.DREAMWOOD_LOGS)
				.define('L', Items.LEATHER)
				.pattern("WLW")
				.pattern("WEW")
				.pattern("WLW")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_ELEMENTIUM))
				.unlockedBy("has_alt_item", conditionsFromTag(BotaniaTags.Items.DREAMWOOD_LOGS))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.canopyDrum)
				.define('W', BotaniaTags.Items.LIVINGWOOD_LOGS)
				.define('H', BotaniaItems.leavesHorn)
				.define('L', Items.LEATHER)
				.pattern("WLW")
				.pattern("WHW")
				.pattern("WLW")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.leavesHorn))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.abstrusePlatform, 2)
				.define('0', BotaniaTags.Items.LIVINGWOOD_LOGS)
				.define('P', BotaniaItems.manaPearl)
				.define('3', BotaniaBlocks.livingwoodFramed)
				.define('4', BotaniaBlocks.livingwoodPatternFramed)
				.pattern("343")
				.pattern("0P0")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaPearl))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.spectralPlatform, 2)
				.define('0', BotaniaTags.Items.DREAMWOOD_LOGS)
				.define('3', BotaniaBlocks.dreamwoodFramed)
				.define('4', BotaniaBlocks.dreamwoodPatternFramed)
				.define('D', BotaniaItems.pixieDust)
				.pattern("343")
				.pattern("0D0")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.pixieDust))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.alfPortal)
				.define('T', BotaniaTags.Items.NUGGETS_TERRASTEEL)
				.define('W', BotaniaTags.Items.LIVINGWOOD_LOGS)
				.pattern("WTW")
				.pattern("WTW")
				.pattern("WTW")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_TERRASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.conjurationCatalyst)
				.define('P', BotaniaBlocks.alchemyCatalyst)
				.define('B', BotaniaItems.pixieDust)
				.define('S', BotaniaBlocks.livingrock)
				.define('G', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.pattern("SBS")
				.pattern("GPG")
				.pattern("SGS")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.pixieDust))
				.unlockedBy("has_alt_item", conditionsFromTag(BotaniaTags.Items.INGOTS_ELEMENTIUM))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.spawnerClaw)
				.define('P', Items.PRISMARINE_BRICKS)
				.define('B', Items.BLAZE_ROD)
				.define('S', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.define('E', BotaniaItems.enderAirBottle)
				.define('M', BotaniaTags.Items.BLOCKS_MANASTEEL)
				.pattern("BSB")
				.pattern("PMP")
				.pattern("PEP")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.enderAirBottle))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BotaniaBlocks.enderEye)
				.define('R', Items.REDSTONE)
				.define('E', Items.ENDER_EYE)
				.define('O', Items.OBSIDIAN)
				.pattern("RER")
				.pattern("EOE")
				.pattern("RER")
				.unlockedBy("has_item", conditionsFromItem(Items.ENDER_EYE))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.starfield)
				.define('P', BotaniaItems.pixieDust)
				.define('E', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.define('O', Items.OBSIDIAN)
				.pattern("EPE")
				.pattern("EOE")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.pixieDust))
				.unlockedBy("has_alt_item", conditionsFromTag(BotaniaTags.Items.INGOTS_ELEMENTIUM))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.rfGenerator)
				.define('R', Items.REDSTONE_BLOCK)
				.define('S', BotaniaBlocks.livingrock)
				.define('M', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern("SRS")
				.pattern("RMR")
				.pattern("SRS")
				.unlockedBy("has_item", conditionsFromItem(Items.REDSTONE_BLOCK))
				.unlockedBy("has_alt_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.brewery)
				.define('A', BotaniaItems.runeMana)
				.define('R', BotaniaBlocks.livingrock)
				.define('S', Items.BREWING_STAND)
				.define('M', BotaniaTags.Items.BLOCKS_MANASTEEL)
				.pattern("RSR")
				.pattern("RAR")
				.pattern("RMR")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.runeMana))
				.unlockedBy("has_alt_item", conditionsFromItem(Items.BREWING_STAND))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.terraPlate)
				.define('0', BotaniaItems.runeWater)
				.define('1', BotaniaItems.runeFire)
				.define('2', BotaniaItems.runeEarth)
				.define('3', BotaniaItems.runeAir)
				.define('8', BotaniaItems.runeMana)
				.define('L', Blocks.LAPIS_BLOCK)
				.define('M', BotaniaTags.Items.BLOCKS_MANASTEEL)
				.pattern("LLL")
				.pattern("0M1")
				.pattern("283")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.RUNES))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.prism)
				.define('P', Items.PRISMARINE_CRYSTALS)
				.define('S', BotaniaBlocks.spectralPlatform)
				.define('G', Items.GLASS)
				.pattern("GPG")
				.pattern("GSG")
				.pattern("GPG")
				.unlockedBy("has_item", conditionsFromItem(Items.PRISMARINE_CRYSTALS))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaBlocks.spectralPlatform))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.pump)
				.define('B', Items.BUCKET)
				.define('S', BotaniaBlocks.livingrock)
				.define('I', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern("SSS")
				.pattern("IBI")
				.pattern("SSS")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.incensePlate)
				.define('S', BotaniaBlocks.livingwoodSlab)
				.define('W', BotaniaTags.Items.LIVINGWOOD_LOGS)
				.pattern("SSW")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.LIVINGWOOD_LOGS))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BotaniaBlocks.hourglass)
				.define('R', Items.REDSTONE)
				.define('S', BotaniaTags.Items.INGOTS_MANASTEEL)
				.define('G', Items.GOLD_INGOT)
				.define('M', BotaniaBlocks.manaGlass)
				.pattern("GMG")
				.pattern("RSR")
				.pattern("GMG")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.manaGlass))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, BotaniaBlocks.ghostRail)
				.requires(Items.RAIL)
				.requires(BotaniaBlocks.spectralPlatform)
				.unlockedBy("has_item", conditionsFromItem(Items.RAIL))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaBlocks.spectralPlatform))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BotaniaBlocks.sparkChanger)
				.define('R', Items.REDSTONE)
				.define('S', BotaniaBlocks.livingrock)
				.define('E', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.pattern("ESE")
				.pattern("SRS")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_ELEMENTIUM))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.felPumpkin)
				.define('P', Items.PUMPKIN)
				.define('B', Items.BONE)
				.define('S', Items.STRING)
				.define('F', Items.ROTTEN_FLESH)
				.define('G', Items.GUNPOWDER)
				.pattern(" S ")
				.pattern("BPF")
				.pattern(" G ")
				.unlockedBy("has_item", conditionsFromItem(Items.PUMPKIN))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaBlocks.cocoon)
				.define('S', Items.STRING)
				.define('C', BotaniaItems.manaweaveCloth)
				.define('P', BotaniaBlocks.felPumpkin)
				.define('D', BotaniaItems.pixieDust)
				.pattern("SSS")
				.pattern("CPC")
				.pattern("SDS")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.felPumpkin))
				.save(base::setValue);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaBlocks.cocoon)
				.define('S', Items.STRING)
				.define('P', BotaniaBlocks.felPumpkin)
				.define('I', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern("SSS")
				.pattern("SPS")
				.pattern("SIS")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.felPumpkin))
				.save(gog::setValue);
		consumer.accept(new GogAlternationResult(gog.getValue(), base.getValue()));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, BotaniaBlocks.lightRelayDefault)
				.requires(BotaniaItems.redString)
				.requires(BotaniaTags.Items.GEMS_DRAGONSTONE)
				.requires(Items.GLOWSTONE_DUST)
				.requires(Items.GLOWSTONE_DUST)
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.GEMS_DRAGONSTONE))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, BotaniaBlocks.lightRelayDetector)
				.requires(BotaniaBlocks.lightRelayDefault)
				.requires(Items.REDSTONE)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.lightRelayDefault))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, BotaniaBlocks.lightRelayFork)
				.requires(BotaniaBlocks.lightRelayDefault)
				.requires(Items.REDSTONE_TORCH)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.lightRelayDefault))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, BotaniaBlocks.lightRelayToggle)
				.requires(BotaniaBlocks.lightRelayDefault)
				.requires(Items.LEVER)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.lightRelayDefault))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BotaniaBlocks.lightLauncher)
				.define('D', BotaniaTags.Items.DREAMWOOD_LOGS)
				.define('L', BotaniaBlocks.lightRelayDefault)
				.pattern("DDD")
				.pattern("DLD")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.lightRelayDefault))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaBlocks.manaBomb)
				.define('T', Items.TNT)
				.define('G', BotaniaItems.lifeEssence)
				.define('L', BotaniaTags.Items.LIVINGWOOD_LOGS)
				.pattern("LTL")
				.pattern("TGT")
				.pattern("LTL")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.bellows)
				.define('R', BotaniaItems.runeAir)
				.define('S', BotaniaBlocks.livingwoodSlab)
				.define('L', Items.LEATHER)
				.pattern("SSS")
				.pattern("RL ")
				.pattern("SSS")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.runeAir))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.bifrostPerm)
				.requires(BotaniaItems.rainbowRod)
				.requires(BotaniaBlocks.elfGlass)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.rainbowRod))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BotaniaBlocks.cellBlock, 3)
				.requires(Items.CACTUS, 3)
				.requires(Items.BEETROOT)
				.requires(Items.CARROT)
				.requires(Items.POTATO)
				.unlockedBy("has_item", conditionsFromItem(BotaniaFlowerBlocks.dandelifeon))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.teruTeruBozu)
				.define('C', BotaniaItems.manaweaveCloth)
				.define('S', Items.SUNFLOWER)
				.pattern("C")
				.pattern("C")
				.pattern("S")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaweaveCloth))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BotaniaBlocks.avatar)
				.define('D', BotaniaTags.Items.GEMS_MANA_DIAMOND)
				.define('W', BotaniaTags.Items.LIVINGWOOD_LOGS)
				.pattern(" W ")
				.pattern("WDW")
				.pattern("W W")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.GEMS_MANA_DIAMOND))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BotaniaBlocks.animatedTorch)
				.define('D', BotaniaTags.Items.DUSTS_MANA)
				.define('T', Items.REDSTONE_TORCH)
				.pattern("D")
				.pattern("T")
				.unlockedBy("has_item", conditionsFromItem(Items.REDSTONE_TORCH))
				.unlockedBy("has_alt_item", conditionsFromTag(BotaniaTags.Items.DUSTS_MANA))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.livingwoodTwig)
				.define('W', BotaniaTags.Items.LIVINGWOOD_LOGS)
				.pattern(" W")
				.pattern("W ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.LIVINGWOOD_LOGS))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.redstoneRoot)
				.requires(Items.REDSTONE)
				.requires(Ingredient.of(Items.FERN, Items.GRASS))
				.unlockedBy("has_item", conditionsFromItem(Items.REDSTONE))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.dreamwoodTwig)
				.define('W', BotaniaTags.Items.DREAMWOOD_LOGS)
				.pattern(" W")
				.pattern("W ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.DREAMWOOD_LOGS))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.gaiaIngot)
				.define('S', BotaniaItems.lifeEssence)
				.define('I', BotaniaTags.Items.INGOTS_TERRASTEEL)
				.pattern(" S ")
				.pattern("SIS")
				.pattern(" S ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.manaweaveCloth)
				.define('S', BotaniaItems.manaString)
				.pattern("SS")
				.pattern("SS")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaString))
				.save(consumer);
		Ingredient dyes = Ingredient.of(Items.WHITE_DYE, Items.ORANGE_DYE, Items.MAGENTA_DYE,
				Items.LIGHT_BLUE_DYE, Items.YELLOW_DYE, Items.LIME_DYE, Items.PINK_DYE, Items.GRAY_DYE,
				Items.LIGHT_GRAY_DYE, Items.CYAN_DYE, Items.PURPLE_DYE, Items.BLUE_DYE, Items.BROWN_DYE,
				Items.GREEN_DYE, Items.RED_DYE, Items.BLACK_DYE);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.fertilizer)
				.requires(Items.BONE_MEAL)
				.requires(dyes, 4)
				.unlockedBy("has_item", hasAnyDye)
				.save(base::setValue, "botania:fertilizer_dye");
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.fertilizer, 3)
				.requires(Items.BONE_MEAL)
				.requires(dyes, 4)
				.unlockedBy("has_item", hasAnyDye)
				.save(gog::setValue, "botania:fertilizer_dye");
		consumer.accept(new GogAlternationResult(gog.getValue(), base.getValue()));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.drySeeds)
				.requires(BotaniaItems.grassSeeds)
				.requires(Items.DEAD_BUSH)
				.group("botania:seeds")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.grassSeeds))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.goldenSeeds)
				.requires(BotaniaItems.grassSeeds)
				.requires(Items.WHEAT)
				.group("botania:seeds")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.grassSeeds))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.vividSeeds)
				.requires(BotaniaItems.grassSeeds)
				.requires(Items.GREEN_DYE)
				.group("botania:seeds")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.grassSeeds))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.scorchedSeeds)
				.requires(BotaniaItems.grassSeeds)
				.requires(Items.BLAZE_POWDER)
				.group("botania:seeds")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.grassSeeds))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.infusedSeeds)
				.requires(BotaniaItems.grassSeeds)
				.requires(Items.PRISMARINE_SHARD)
				.group("botania:seeds")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.grassSeeds))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.mutatedSeeds)
				.requires(BotaniaItems.grassSeeds)
				.requires(Items.SPIDER_EYE)
				.group("botania:seeds")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.grassSeeds))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.darkQuartz, 8)
				.define('Q', Items.QUARTZ)
				.define('C', Ingredient.of(Items.COAL, Items.CHARCOAL))
				.pattern("QQQ")
				.pattern("QCQ")
				.pattern("QQQ")
				.unlockedBy("has_item", conditionsFromItem(Items.QUARTZ))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.blazeQuartz, 8)
				.define('Q', Items.QUARTZ)
				.define('C', Items.BLAZE_POWDER)
				.pattern("QQQ")
				.pattern("QCQ")
				.pattern("QQQ")
				.unlockedBy("has_item", conditionsFromItem(Items.QUARTZ))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.lavenderQuartz, 8)
				.define('Q', Items.QUARTZ)
				.define('C', Ingredient.of(Items.ALLIUM, Items.PINK_TULIP, Items.LILAC, Items.PEONY))
				.pattern("QQQ")
				.pattern("QCQ")
				.pattern("QQQ")
				.unlockedBy("has_item", conditionsFromItem(Items.QUARTZ))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.redQuartz, 8)
				.define('Q', Items.QUARTZ)
				.define('C', Items.REDSTONE)
				.pattern("QQQ")
				.pattern("QCQ")
				.pattern("QQQ")
				.unlockedBy("has_item", conditionsFromItem(Items.QUARTZ))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.sunnyQuartz, 8)
				.define('Q', Items.QUARTZ)
				.define('C', Items.SUNFLOWER)
				.pattern("QQQ")
				.pattern("QCQ")
				.pattern("QQQ")
				.unlockedBy("has_item", conditionsFromItem(Items.QUARTZ))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.vineBall)
				.define('V', Items.VINE)
				.pattern("VVV")
				.pattern("VVV")
				.pattern("VVV")
				.unlockedBy("has_item", conditionsFromItem(Items.VINE))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, BotaniaItems.necroVirus)
				.requires(BotaniaItems.pixieDust)
				.requires(BotaniaItems.vineBall)
				.requires(Items.MAGMA_CREAM)
				.requires(Items.FERMENTED_SPIDER_EYE)
				.requires(Items.ENDER_EYE)
				.requires(Items.ZOMBIE_HEAD)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.pixieDust))
				.unlockedBy("has_alt_item", conditionsFromItem(Items.ZOMBIE_HEAD))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, BotaniaItems.nullVirus)
				.requires(BotaniaItems.pixieDust)
				.requires(BotaniaItems.vineBall)
				.requires(Items.MAGMA_CREAM)
				.requires(Items.FERMENTED_SPIDER_EYE)
				.requires(Items.ENDER_EYE)
				.requires(Items.SKELETON_SKULL)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.pixieDust))
				.unlockedBy("has_alt_item", conditionsFromItem(Items.SKELETON_SKULL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaItems.spark)
				.define('P', BotaniaTags.Items.PETALS)
				.define('B', Items.BLAZE_POWDER)
				.define('N', Items.GOLD_NUGGET)
				.pattern(" P ")
				.pattern("BNB")
				.pattern(" P ")
				.unlockedBy("has_item", conditionsFromItem(Items.BLAZE_POWDER))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BotaniaItems.sparkUpgradeDispersive)
				.requires(BotaniaItems.pixieDust)
				.requires(BotaniaTags.Items.INGOTS_MANASTEEL)
				.requires(BotaniaItems.runeWater)
				.group("botania:spark_upgrade")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.pixieDust))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaItems.spark))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.sparkUpgradeDominant)
				.requires(BotaniaItems.pixieDust)
				.requires(BotaniaTags.Items.INGOTS_MANASTEEL)
				.requires(BotaniaItems.runeFire)
				.group("botania:spark_upgrade")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.pixieDust))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaItems.spark))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.sparkUpgradeRecessive)
				.requires(BotaniaItems.pixieDust)
				.requires(BotaniaTags.Items.INGOTS_MANASTEEL)
				.requires(BotaniaItems.runeEarth)
				.group("botania:spark_upgrade")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.pixieDust))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaItems.spark))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.sparkUpgradeIsolated)
				.requires(BotaniaItems.pixieDust)
				.requires(BotaniaTags.Items.INGOTS_MANASTEEL)
				.requires(BotaniaItems.runeAir)
				.group("botania:spark_upgrade")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.pixieDust))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaItems.spark))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.vial, 3)
				.define('G', BotaniaBlocks.manaGlass)
				.pattern("G G")
				.pattern(" G ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.manaGlass))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaBlocks.brewery))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.flask, 3)
				.define('G', BotaniaBlocks.elfGlass)
				.pattern("G G")
				.pattern(" G ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.elfGlass))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.worldSeed, 4)
				.define('S', Items.WHEAT_SEEDS)
				.define('D', BotaniaTags.Items.GEMS_DRAGONSTONE)
				.define('G', Items.GRASS_BLOCK)
				.pattern("G")
				.pattern("S")
				.pattern("D")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.GEMS_DRAGONSTONE))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BotaniaItems.thornChakram, 2)
				.define('T', BotaniaTags.Items.INGOTS_TERRASTEEL)
				.define('V', Items.VINE)
				.pattern("VVV")
				.pattern("VTV")
				.pattern("VVV")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_TERRASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BotaniaItems.flareChakram, 2)
				.define('P', BotaniaItems.pixieDust)
				.define('B', Items.BLAZE_POWDER)
				.define('C', BotaniaItems.thornChakram)
				.pattern("BBB")
				.pattern("CPC")
				.pattern("BBB")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.thornChakram))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.phantomInk, 4)
				.requires(BotaniaItems.manaPearl)
				.requires(Ingredient.of(
						ColorHelper.supportedColors().map(DyeItem::byColor).toArray(ItemLike[]::new)
				))
				.requires(Ingredient.of(Items.GLASS, Items.WHITE_STAINED_GLASS, Items.ORANGE_STAINED_GLASS,
						Items.MAGENTA_STAINED_GLASS, Items.LIGHT_BLUE_STAINED_GLASS, Items.YELLOW_STAINED_GLASS,
						Items.LIME_STAINED_GLASS, Items.PINK_STAINED_GLASS, Items.GRAY_STAINED_GLASS,
						Items.LIGHT_GRAY_STAINED_GLASS, Items.CYAN_STAINED_GLASS, Items.PURPLE_STAINED_GLASS,
						Items.BLUE_STAINED_GLASS, Items.BROWN_STAINED_GLASS, Items.GREEN_STAINED_GLASS,
						Items.RED_STAINED_GLASS, Items.BLACK_STAINED_GLASS))
				.requires(Items.GLASS_BOTTLE, 4)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaPearl))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.keepIvy)
				.requires(BotaniaItems.pixieDust)
				.requires(Items.VINE)
				.requires(BotaniaItems.enderAirBottle)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.enderAirBottle))
				.save(consumer);

	}

	private void registerMisc(Consumer<FinishedRecipe> consumer) {
		Ingredient mushrooms = Ingredient.of(BotaniaBlocks.whiteMushroom, BotaniaBlocks.orangeMushroom,
				BotaniaBlocks.magentaMushroom, BotaniaBlocks.lightBlueMushroom, BotaniaBlocks.yellowMushroom,
				BotaniaBlocks.limeMushroom, BotaniaBlocks.pinkMushroom, BotaniaBlocks.grayMushroom, BotaniaBlocks.lightGrayMushroom,
				BotaniaBlocks.cyanMushroom, BotaniaBlocks.purpleMushroom, BotaniaBlocks.blueMushroom, BotaniaBlocks.brownMushroom,
				BotaniaBlocks.greenMushroom, BotaniaBlocks.redMushroom, BotaniaBlocks.blackMushroom);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.MUSHROOM_STEW)
				.requires(mushrooms, 2)
				.requires(Items.BOWL)
				.unlockedBy("has_item", conditionsFromItem(Items.BOWL))
				.unlockedBy("has_orig_recipe", RecipeUnlockedTrigger.unlocked(new ResourceLocation("mushroom_stew")))
				.save(consumer, "botania:mushroom_stew");

		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.COBWEB)
				.define('S', Items.STRING)
				.define('M', BotaniaItems.manaString)
				.pattern("S S")
				.pattern(" M ")
				.pattern("S S")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaString))
				.save(consumer, prefix("cobweb"));

		petalApothecary(Items.COBBLESTONE, BotaniaBlocks.defaultAltar)
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.PETALS))
				.save(consumer);
		petalApothecary(Items.MOSSY_COBBLESTONE, BotaniaBlocks.mossyAltar)
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.PETALS))
				.save(consumer);
		petalApothecary(BotaniaBlocks.livingrock, BotaniaBlocks.livingrockAltar)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.livingrock))
				.save(consumer);
		petalApothecary(Items.COBBLED_DEEPSLATE, BotaniaBlocks.deepslateAltar)
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.PETALS))
				.save(consumer);
		for (String metamorphicVariant : LibBlockNames.METAMORPHIC_VARIANTS) {
			Block apothecary = getBlockOrThrow(prefix("apothecary_" + metamorphicVariant));
			Block cobble = getBlockOrThrow(prefix(LibBlockNames.METAMORPHIC_PREFIX + metamorphicVariant + "_cobblestone"));
			petalApothecary(cobble, apothecary)
					.group("botania:metamorphic_apothecary")
					.unlockedBy("has_item", conditionsFromItem(cobble))
					.unlockedBy("has_flower_item", conditionsFromItem(BotaniaFlowerBlocks.marimorphosis))
					.save(consumer);
		}
		ColorHelper.supportedColors().forEach(color -> {
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BotaniaBlocks.getShinyFlower(color))
					.requires(Items.GLOWSTONE_DUST)
					.requires(Items.GLOWSTONE_DUST)
					.requires(BotaniaBlocks.getFlower(color))
					.group("botania:shiny_flower")
					.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.getFlower(color)))
					.save(consumer);
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.getFloatingFlower(color))
					.define('S', BotaniaItems.grassSeeds)
					.define('D', Items.DIRT)
					.define('F', BotaniaBlocks.getShinyFlower(color))
					.pattern("F")
					.pattern("S")
					.pattern("D")
					.group("botania:floating_flowers")
					.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.getShinyFlower(color)))
					.save(consumer);
			ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BotaniaBlocks.getPetalBlock(color))
					.define('P', BotaniaItems.getPetal(color))
					.pattern("PPP")
					.pattern("PPP")
					.pattern("PPP")
					.group("botania:petal_block")
					.unlockedBy("has_item", conditionsFromItem(BotaniaItems.getPetal(color)))
					.save(consumer);
			ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, BotaniaBlocks.getMushroom(color))
					.requires(Ingredient.of(Items.RED_MUSHROOM, Items.BROWN_MUSHROOM))
					.requires(DyeItem.byColor(color))
					.group("botania:mushroom")
					.unlockedBy("has_item", conditionsFromItem(Items.RED_MUSHROOM))
					.unlockedBy("has_alt_item", conditionsFromItem(Items.BROWN_MUSHROOM))
					.save(consumer, "botania:mushroom_" + color.ordinal());
			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.getPetal(color), 4)
					.requires(BotaniaBlocks.getDoubleFlower(color))
					.group("botania:petal_double")
					.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.getDoubleFlower(color)))
					.unlockedBy("has_alt_item", conditionsFromItem(BotaniaItems.getPetal(color)))
					.save(consumer, "botania:petal_" + color.getName() + "_double");
			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.getPetal(color), 2)
					.requires(BotaniaBlocks.getFlower(color))
					.group("botania:petal")
					.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.getFlower(color)))
					.unlockedBy("has_alt_item", conditionsFromItem(BotaniaItems.getPetal(color)))
					.save(consumer, "botania:petal_" + color.getName());
			ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, DyeItem.byColor(color))
					.requires(Ingredient.of(BotaniaTags.Items.getPetalTag(color)))
					.group("botania:dye")
					.unlockedBy("has_item", conditionsFromItem(BotaniaItems.getPetal(color)))
					.save(consumer, "botania:dye_" + color.getName());
		});
	}

	private void registerTools(Consumer<FinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, BotaniaItems.lexicon)
				.requires(ItemTags.SAPLINGS)
				.requires(Items.BOOK)
				.unlockedBy("has_item", conditionsFromTag(ItemTags.SAPLINGS))
				.unlockedBy("has_alt_item", conditionsFromItem(Items.BOOK))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.twigWand)
				.define('P', BotaniaTags.Items.PETALS)
				.define('S', BotaniaItems.livingwoodTwig)
				.pattern(" PS")
				.pattern(" SP")
				.pattern("S  ")
				.group("botania:twig_wand")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.PETALS))
				.save(WrapperResult.ofType(WandOfTheForestRecipe.SERIALIZER, consumer));
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.dreamwoodWand)
				.define('P', BotaniaTags.Items.PETALS)
				.define('S', BotaniaItems.dreamwoodTwig)
				.pattern(" PS")
				.pattern(" SP")
				.pattern("S  ")
				.group("botania:twig_wand")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.PETALS))
				.save(WrapperResult.ofType(WandOfTheForestRecipe.SERIALIZER, consumer));
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.manaTablet)
				.define('P', BotaniaItems.manaPearl)
				.define('S', BotaniaBlocks.livingrock)
				.pattern("SSS")
				.pattern("SPS")
				.pattern("SSS")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaPearl))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.manaTablet)
				.define('P', BotaniaTags.Items.GEMS_MANA_DIAMOND)
				.define('S', BotaniaBlocks.livingrock)
				.pattern("SSS")
				.pattern("SPS")
				.pattern("SSS")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.GEMS_MANA_DIAMOND))
				.save(consumer, prefix("mana_tablet_alt"));

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.cacophonium)
				.define('N', Items.NOTE_BLOCK)
				.define('G', Items.COPPER_INGOT)
				.pattern(" G ")
				.pattern("GNG")
				.pattern("GG ")
				.unlockedBy("has_item", conditionsFromItem(Items.NOTE_BLOCK))
				.save(consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.grassHorn)
				.define('S', BotaniaItems.grassSeeds)
				.define('W', BotaniaTags.Items.LIVINGWOOD_LOGS)
				.pattern(" W ")
				.pattern("WSW")
				.pattern("WW ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.LIVINGWOOD_LOGS))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, BotaniaItems.leavesHorn)
				.requires(BotaniaItems.grassHorn)
				.requires(ItemTags.LEAVES)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.grassHorn))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, BotaniaItems.snowHorn)
				.requires(BotaniaItems.grassHorn)
				.requires(Items.SNOWBALL)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.grassHorn))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.manaMirror)
				.define('P', BotaniaItems.manaPearl)
				.define('R', BotaniaBlocks.livingrock)
				.define('S', BotaniaItems.livingwoodTwig)
				.define('T', BotaniaItems.manaTablet)
				.define('I', BotaniaTags.Items.INGOTS_TERRASTEEL)
				.pattern(" PR")
				.pattern(" SI")
				.pattern("T  ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaTablet))
				.unlockedBy("has_alt_item", conditionsFromTag(BotaniaTags.Items.INGOTS_TERRASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.openBucket)
				.define('E', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.pattern("E E")
				.pattern(" E ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_ELEMENTIUM))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.spawnerMover)
				.define('A', BotaniaItems.enderAirBottle)
				.define('D', BotaniaTags.Items.GEMS_DRAGONSTONE)
				.define('E', BotaniaItems.lifeEssence)
				.define('I', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.pattern("EIE")
				.pattern("ADA")
				.pattern("EIE")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.slingshot)
				.define('A', BotaniaItems.runeAir)
				.define('T', BotaniaItems.livingwoodTwig)
				.pattern(" TA")
				.pattern(" TT")
				.pattern("T  ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.runeAir))
				.save(consumer);

		registerSimpleArmorSet(consumer, Ingredient.of(BotaniaTags.Items.INGOTS_MANASTEEL), "manasteel", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL));
		registerSimpleArmorSet(consumer, Ingredient.of(BotaniaTags.Items.INGOTS_ELEMENTIUM), "elementium", conditionsFromTag(BotaniaTags.Items.INGOTS_ELEMENTIUM));
		registerSimpleArmorSet(consumer, Ingredient.of(BotaniaItems.manaweaveCloth), "manaweave", conditionsFromItem(BotaniaItems.manaweaveCloth));

		registerTerrasteelUpgradeRecipe(consumer, BotaniaItems.terrasteelHelm, BotaniaItems.manasteelHelm, BotaniaItems.runeSpring);
		registerTerrasteelUpgradeRecipe(consumer, BotaniaItems.terrasteelChest, BotaniaItems.manasteelChest, BotaniaItems.runeSummer);
		registerTerrasteelUpgradeRecipe(consumer, BotaniaItems.terrasteelLegs, BotaniaItems.manasteelLegs, BotaniaItems.runeAutumn);
		registerTerrasteelUpgradeRecipe(consumer, BotaniaItems.terrasteelBoots, BotaniaItems.manasteelBoots, BotaniaItems.runeWinter);

		registerToolSetRecipes(consumer, Ingredient.of(BotaniaTags.Items.INGOTS_MANASTEEL), Ingredient.of(BotaniaItems.livingwoodTwig),
				conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL), BotaniaItems.manasteelSword, BotaniaItems.manasteelPick, BotaniaItems.manasteelAxe,
				BotaniaItems.manasteelHoe, BotaniaItems.manasteelShovel, BotaniaItems.manasteelShears);
		registerToolSetRecipes(consumer, Ingredient.of(BotaniaTags.Items.INGOTS_ELEMENTIUM), Ingredient.of(BotaniaItems.dreamwoodTwig),
				conditionsFromTag(BotaniaTags.Items.INGOTS_ELEMENTIUM), BotaniaItems.elementiumSword, BotaniaItems.elementiumPick, BotaniaItems.elementiumAxe,
				BotaniaItems.elementiumHoe, BotaniaItems.elementiumShovel, BotaniaItems.elementiumShears);

		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BotaniaItems.terraSword)
				.define('S', BotaniaItems.livingwoodTwig)
				.define('I', BotaniaTags.Items.INGOTS_TERRASTEEL)
				.pattern("I")
				.pattern("I")
				.pattern("S")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_TERRASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.terraPick)
				.define('T', BotaniaItems.manaTablet)
				.define('I', BotaniaTags.Items.INGOTS_TERRASTEEL)
				.define('L', BotaniaItems.livingwoodTwig)
				.pattern("ITI")
				.pattern("ILI")
				.pattern(" L ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_TERRASTEEL))
				.save(WrapperResult.ofType(ManaUpgradeRecipe.SERIALIZER, consumer));
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.terraAxe)
				.define('S', BotaniaItems.livingwoodTwig)
				.define('T', BotaniaTags.Items.INGOTS_TERRASTEEL)
				.define('G', Items.GLOWSTONE)
				.pattern("TTG")
				.pattern("TST")
				.pattern(" S ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_TERRASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BotaniaItems.starSword)
				.define('A', BotaniaItems.enderAirBottle)
				.define('D', BotaniaTags.Items.GEMS_DRAGONSTONE)
				.define('T', BotaniaItems.terraSword)
				.define('I', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.pattern("  I")
				.pattern("AD ")
				.pattern("TA ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.terraAxe))
				.unlockedBy("has_terrasteel", conditionsFromTag(BotaniaTags.Items.INGOTS_TERRASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BotaniaItems.thunderSword)
				.define('A', BotaniaItems.enderAirBottle)
				.define('D', BotaniaTags.Items.GEMS_MANA_DIAMOND)
				.define('T', BotaniaItems.terraSword)
				.define('I', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.pattern("  I")
				.pattern("AD ")
				.pattern("TA ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.terraAxe))
				.unlockedBy("has_terrasteel", conditionsFromTag(BotaniaTags.Items.INGOTS_TERRASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.glassPick)
				.define('T', BotaniaItems.livingwoodTwig)
				.define('G', Items.GLASS)
				.define('I', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern("GIG")
				.pattern(" T ")
				.pattern(" T ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BotaniaItems.livingwoodBow)
				.define('S', BotaniaItems.manaString)
				.define('T', BotaniaItems.livingwoodTwig)
				.pattern(" TS")
				.pattern("T S")
				.pattern(" TS")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaString))
				.unlockedBy("has_twig", conditionsFromItem(BotaniaItems.livingwoodTwig))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BotaniaItems.crystalBow)
				.define('S', BotaniaItems.manaString)
				.define('T', BotaniaItems.livingwoodTwig)
				.define('D', BotaniaTags.Items.GEMS_DRAGONSTONE)
				.pattern(" DS")
				.pattern("T S")
				.pattern(" DS")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.GEMS_DRAGONSTONE))
				.save(consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BotaniaItems.enderDagger)
				.define('P', BotaniaItems.manaPearl)
				.define('S', BotaniaTags.Items.INGOTS_MANASTEEL)
				.define('T', BotaniaItems.livingwoodTwig)
				.pattern("P")
				.pattern("S")
				.pattern("T")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaPearl))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.enderHand)
				.define('P', BotaniaItems.manaPearl)
				.define('E', Items.ENDER_CHEST)
				.define('L', Items.LEATHER)
				.define('O', Items.OBSIDIAN)
				.pattern("PLO")
				.pattern("LEL")
				.pattern("OL ")
				.unlockedBy("has_item", conditionsFromItem(Items.ENDER_CHEST))
				.unlockedBy("has_alt_item", conditionsFromItem(Items.ENDER_EYE))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.placeholder, 32)
				.requires(Items.CRAFTING_TABLE)
				.requires(BotaniaBlocks.livingrock)
				.unlockedBy("has_dreamwood", conditionsFromTag(BotaniaTags.Items.DREAMWOOD_LOGS))
				.unlockedBy("has_crafty_crate", conditionsFromItem(BotaniaBlocks.craftCrate))
				.save(consumer);

		for (CraftyCratePattern pattern : CraftyCratePattern.values()) {
			if (pattern == CraftyCratePattern.NONE) {
				continue;
			}
			Item item = getItemOrThrow(prefix(LibItemNames.CRAFT_PATTERN_PREFIX + pattern.getSerializedName().split("_", 2)[1]));
			String s = pattern.openSlots.stream().map(bool -> bool ? "R" : "P").collect(Collectors.joining());
			ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, item)
					.define('P', BotaniaItems.placeholder)
					.define('R', Items.REDSTONE)
					.pattern(s.substring(0, 3))
					.pattern(s.substring(3, 6))
					.pattern(s.substring(6, 9))
					.group("botania:craft_pattern")
					.unlockedBy("has_item", conditionsFromItem(BotaniaItems.placeholder))
					.unlockedBy("has_crafty_crate", conditionsFromItem(BotaniaBlocks.craftCrate))
					.save(consumer);
		}

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.manaGun)
				.define('S', BotaniaBlocks.redstoneSpreader)
				.define('D', BotaniaTags.Items.GEMS_MANA_DIAMOND)
				.define('T', Items.TNT)
				.define('W', BotaniaTags.Items.LIVINGWOOD_LOGS)
				.define('M', BotaniaItems.runeMana)
				.pattern("SMD")
				.pattern(" WT")
				.pattern("  W")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.GEMS_MANA_DIAMOND))
				.save(consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.dirtRod)
				.define('D', Items.DIRT)
				.define('T', BotaniaItems.livingwoodTwig)
				.define('E', BotaniaItems.runeEarth)
				.pattern("  D")
				.pattern(" T ")
				.pattern("E  ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.runeEarth))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.terraformRod)
				.define('A', BotaniaItems.runeAutumn)
				.define('R', BotaniaItems.dirtRod)
				.define('S', BotaniaItems.runeSpring)
				.define('T', BotaniaTags.Items.INGOTS_TERRASTEEL)
				.define('G', BotaniaItems.grassSeeds)
				.define('W', BotaniaItems.runeWinter)
				.define('M', BotaniaItems.runeSummer)
				.pattern(" WT")
				.pattern("ARS")
				.pattern("GM ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_TERRASTEEL))
				.save(consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.waterRod)
				.define('B', Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)))
				.define('R', BotaniaItems.runeWater)
				.define('T', BotaniaItems.livingwoodTwig)
				.pattern("  B")
				.pattern(" T ")
				.pattern("R  ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.runeWater))
				.save(WrapperResult.ofType(WaterBottleMatchingRecipe.SERIALIZER, consumer));

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.rainbowRod)
				.define('P', BotaniaItems.pixieDust)
				.define('D', BotaniaTags.Items.GEMS_DRAGONSTONE)
				.define('E', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.pattern(" PD")
				.pattern(" EP")
				.pattern("E  ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.GEMS_DRAGONSTONE))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.tornadoRod)
				.define('R', BotaniaItems.runeAir)
				.define('T', BotaniaItems.livingwoodTwig)
				.define('F', Items.FEATHER)
				.pattern("  F")
				.pattern(" T ")
				.pattern("R  ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.runeAir))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BotaniaItems.fireRod)
				.define('R', BotaniaItems.runeFire)
				.define('T', BotaniaItems.livingwoodTwig)
				.define('F', Items.BLAZE_POWDER)
				.pattern("  F")
				.pattern(" T ")
				.pattern("R  ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.runeFire))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, BotaniaItems.skyDirtRod)
				.requires(BotaniaItems.dirtRod)
				.requires(BotaniaItems.pixieDust)
				.requires(BotaniaItems.runeAir)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.pixieDust))
				.save(consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.diviningRod)
				.define('T', BotaniaItems.livingwoodTwig)
				.define('D', BotaniaTags.Items.GEMS_MANA_DIAMOND)
				.pattern(" TD")
				.pattern(" TT")
				.pattern("T  ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.GEMS_MANA_DIAMOND))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.gravityRod)
				.define('T', BotaniaItems.dreamwoodTwig)
				.define('D', BotaniaTags.Items.GEMS_DRAGONSTONE)
				.define('W', Items.WHEAT)
				.pattern(" TD")
				.pattern(" WT")
				.pattern("T  ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.GEMS_DRAGONSTONE))
				.save(consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BotaniaItems.missileRod)
				.define('D', BotaniaTags.Items.GEMS_DRAGONSTONE)
				.define('T', BotaniaItems.dreamwoodTwig)
				.define('G', BotaniaItems.lifeEssence)
				.pattern("GDD")
				.pattern(" TD")
				.pattern("T G")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.cobbleRod)
				.define('C', Items.COBBLESTONE)
				.define('T', BotaniaItems.livingwoodTwig)
				.define('F', BotaniaItems.runeFire)
				.define('W', BotaniaItems.runeWater)
				.pattern(" FC")
				.pattern(" TW")
				.pattern("T  ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.runeFire))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaItems.runeWater))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.smeltRod)
				.define('B', Items.BLAZE_ROD)
				.define('T', BotaniaItems.livingwoodTwig)
				.define('F', BotaniaItems.runeFire)
				.pattern(" BF")
				.pattern(" TB")
				.pattern("T  ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.runeFire))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.exchangeRod)
				.define('R', BotaniaItems.runeSloth)
				.define('S', Items.STONE)
				.define('T', BotaniaItems.livingwoodTwig)
				.pattern(" SR")
				.pattern(" TS")
				.pattern("T  ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.runeSloth))
				.save(consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.laputaShard)
				.define('P', Items.PRISMARINE_CRYSTALS)
				.define('A', BotaniaItems.runeAir)
				.define('S', BotaniaItems.lifeEssence)
				.define('D', BotaniaTags.Items.GEMS_DRAGONSTONE)
				.define('E', BotaniaItems.runeEarth)
				.define('F', BotaniaTags.Items.MUNDANE_FLOATING_FLOWERS)
				.pattern("SFS")
				.pattern("PDP")
				.pattern("ASE")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.craftingHalo)
				.define('P', BotaniaItems.manaPearl)
				.define('C', Items.CRAFTING_TABLE)
				.define('I', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern(" P ")
				.pattern("ICI")
				.pattern(" I ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaPearl))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.clip)
				.define('D', BotaniaTags.Items.DREAMWOOD_LOGS)
				.pattern(" D ")
				.pattern("D D")
				.pattern("DD ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.DREAMWOOD_LOGS))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.spellCloth)
				.define('P', BotaniaItems.manaPearl)
				.define('C', BotaniaItems.manaweaveCloth)
				.pattern(" C ")
				.pattern("CPC")
				.pattern(" C ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaweaveCloth))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.flowerBag)
				.define('P', BotaniaTags.Items.PETALS)
				.define('W', ItemTags.WOOL)
				.pattern("WPW")
				.pattern("W W")
				.pattern(" W ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.MYSTICAL_FLOWERS))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, BotaniaItems.poolMinecart)
				.requires(Items.MINECART)
				.requires(BotaniaBlocks.manaPool)
				.unlockedBy("has_item", conditionsFromItem(Items.MINECART))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.blackHoleTalisman)
				.define('A', BotaniaItems.enderAirBottle)
				.define('E', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.define('G', BotaniaItems.lifeEssence)
				.pattern(" G ")
				.pattern("EAE")
				.pattern(" E ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.temperanceStone)
				.define('R', BotaniaItems.runeEarth)
				.define('S', Items.STONE)
				.pattern(" S ")
				.pattern("SRS")
				.pattern(" S ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.runeEarth))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.incenseStick)
				.define('B', Items.BLAZE_POWDER)
				.define('T', BotaniaItems.livingwoodTwig)
				.define('G', Items.GHAST_TEAR)
				.pattern("  G")
				.pattern(" B ")
				.pattern("T  ")
				.unlockedBy("has_item", conditionsFromItem(Items.GHAST_TEAR))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.obedienceStick)
				.define('T', BotaniaItems.livingwoodTwig)
				.define('M', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern("  M")
				.pattern(" T ")
				.pattern("T  ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.slimeBottle)
				.define('S', Items.SLIME_BALL)
				.define('E', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.define('G', BotaniaBlocks.elfGlass)
				.pattern("EGE")
				.pattern("ESE")
				.pattern(" E ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_ELEMENTIUM))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, BotaniaItems.autocraftingHalo)
				.requires(BotaniaItems.craftingHalo)
				.requires(BotaniaTags.Items.GEMS_MANA_DIAMOND)
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.GEMS_MANA_DIAMOND))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.sextant)
				.define('T', BotaniaItems.livingwoodTwig)
				.define('I', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern(" TI")
				.pattern(" TT")
				.pattern("III")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.astrolabe)
				.define('D', BotaniaTags.Items.DREAMWOOD_LOGS)
				.define('E', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.define('G', BotaniaItems.lifeEssence)
				.pattern(" EG")
				.pattern("EEE")
				.pattern("GED")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer);

	}

	private void registerTrinkets(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.tinyPlanet)
				.define('P', BotaniaItems.manaPearl)
				.define('S', Items.STONE)
				.define('L', BotaniaBlocks.livingrock)
				.pattern("LSL")
				.pattern("SPS")
				.pattern("LSL")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaPearl))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.manaRing)
				.define('T', BotaniaItems.manaTablet)
				.define('I', BotaniaItems.manaSteel)
				.pattern("TI ")
				.pattern("I I")
				.pattern(" I ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaTablet))
				.save(WrapperResult.ofType(ManaUpgradeRecipe.SERIALIZER, consumer));
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.auraRing)
				.define('R', BotaniaItems.runeMana)
				.define('I', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern("RI ")
				.pattern("I I")
				.pattern(" I ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.runeMana))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, BotaniaItems.manaRingGreater)
				.requires(BotaniaTags.Items.INGOTS_TERRASTEEL)
				.requires(BotaniaItems.manaRing)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.terrasteel))
				.save(WrapperResult.ofType(ShapelessManaUpgradeRecipe.SERIALIZER, consumer));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, BotaniaItems.auraRingGreater)
				.requires(BotaniaTags.Items.INGOTS_TERRASTEEL)
				.requires(BotaniaItems.auraRing)
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_TERRASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.travelBelt)
				.define('A', BotaniaItems.runeAir)
				.define('S', BotaniaTags.Items.INGOTS_MANASTEEL)
				.define('E', BotaniaItems.runeEarth)
				.define('L', Items.LEATHER)
				.pattern("EL ")
				.pattern("L L")
				.pattern("SLA")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.knockbackBelt)
				.define('A', BotaniaItems.runeFire)
				.define('S', BotaniaTags.Items.INGOTS_MANASTEEL)
				.define('E', BotaniaItems.runeEarth)
				.define('L', Items.LEATHER)
				.pattern("AL ")
				.pattern("L L")
				.pattern("SLE")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.icePendant)
				.define('R', BotaniaItems.runeWater)
				.define('S', BotaniaItems.manaString)
				.define('W', BotaniaItems.runeWinter)
				.define('M', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern("WS ")
				.pattern("S S")
				.pattern("MSR")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.lavaPendant)
				.define('S', BotaniaItems.manaString)
				.define('D', BotaniaTags.Items.INGOTS_MANASTEEL)
				.define('F', BotaniaItems.runeFire)
				.define('M', BotaniaItems.runeSummer)
				.pattern("MS ")
				.pattern("S S")
				.pattern("DSF")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.magnetRing)
				.define('L', BotaniaItems.lensMagnet)
				.define('M', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern("LM ")
				.pattern("M M")
				.pattern(" M ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.waterRing)
				.define('P', Items.PUFFERFISH)
				.define('C', Items.COD)
				.define('H', Items.HEART_OF_THE_SEA)
				.define('W', BotaniaItems.runeWater)
				.define('M', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern("WMP")
				.pattern("MHM")
				.pattern("CM ")
				.unlockedBy("has_item", conditionsFromItem(Items.HEART_OF_THE_SEA))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.miningRing)
				.define('P', Items.GOLDEN_PICKAXE)
				.define('E', BotaniaItems.runeEarth)
				.define('M', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern("EMP")
				.pattern("M M")
				.pattern(" M ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.divaCharm)
				.define('P', BotaniaItems.tinyPlanet)
				.define('G', Items.GOLD_INGOT)
				.define('H', BotaniaItems.runePride)
				.define('L', BotaniaItems.lifeEssence)
				.pattern("LGP")
				.pattern(" HG")
				.pattern(" GL")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.flightTiara)
				.define('E', BotaniaItems.enderAirBottle)
				.define('F', Items.FEATHER)
				.define('I', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.define('L', BotaniaItems.lifeEssence)
				.pattern("LLL")
				.pattern("ILI")
				.pattern("FEF")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer, "botania:flighttiara_0");

		// Normal quartz and not Tags.Items.QUARTZ because the recipes conflict.
		Item[] items = { Items.QUARTZ, BotaniaItems.darkQuartz, BotaniaItems.manaQuartz, BotaniaItems.blazeQuartz,
				BotaniaItems.lavenderQuartz, BotaniaItems.redQuartz, BotaniaItems.elfQuartz, BotaniaItems.sunnyQuartz };
		for (int i = 0; i < items.length; i++) {
			int tiaraType = i + 1;
			ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, BotaniaItems.flightTiara)
					.requires(BotaniaItems.flightTiara)
					.requires(items[i])
					.group("botania:flight_tiara_wings")
					.unlockedBy("has_item", conditionsFromItem(BotaniaItems.flightTiara))
					.save(NbtOutputResult.with(consumer, tag -> tag.putInt("variant", tiaraType)),
							"botania:flighttiara_" + tiaraType);
		}
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BotaniaItems.pixieRing)
				.define('D', BotaniaItems.pixieDust)
				.define('E', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.pattern("DE ")
				.pattern("E E")
				.pattern(" E ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.pixieDust))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.superTravelBelt)
				.define('S', BotaniaItems.travelBelt)
				.define('E', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.define('L', BotaniaItems.lifeEssence)
				.pattern("E  ")
				.pattern(" S ")
				.pattern("L E")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.reachRing)
				.define('R', BotaniaItems.runePride)
				.define('E', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.pattern("RE ")
				.pattern("E E")
				.pattern(" E ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_ELEMENTIUM))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.itemFinder)
				.define('E', Items.EMERALD)
				.define('I', Items.IRON_INGOT)
				.define('Y', Items.ENDER_EYE)
				.pattern(" I ")
				.pattern("IYI")
				.pattern("IEI")
				.unlockedBy("has_item", conditionsFromItem(Items.ENDER_EYE))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.superLavaPendant)
				.define('P', BotaniaItems.lavaPendant)
				.define('B', Items.BLAZE_ROD)
				.define('G', BotaniaItems.lifeEssence)
				.define('N', Items.NETHER_BRICK)
				.pattern("BBB")
				.pattern("BPB")
				.pattern("NGN")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.bloodPendant)
				.define('P', Items.PRISMARINE_CRYSTALS)
				.define('D', BotaniaTags.Items.GEMS_MANA_DIAMOND)
				.define('G', Items.GHAST_TEAR)
				.pattern(" P ")
				.pattern("PGP")
				.pattern("DP ")
				.unlockedBy("has_item", conditionsFromItem(Items.GHAST_TEAR))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BotaniaItems.holyCloak)
				.define('S', BotaniaItems.lifeEssence)
				.define('W', Items.WHITE_WOOL)
				.define('G', Items.GLOWSTONE_DUST)
				.pattern("WWW")
				.pattern("GWG")
				.pattern("GSG")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BotaniaItems.unholyCloak)
				.define('R', Items.REDSTONE)
				.define('S', BotaniaItems.lifeEssence)
				.define('W', Items.BLACK_WOOL)
				.pattern("WWW")
				.pattern("RWR")
				.pattern("RSR")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, BotaniaItems.balanceCloak)
				.define('R', Items.EMERALD)
				.define('S', BotaniaItems.lifeEssence)
				.define('W', Items.LIGHT_GRAY_WOOL)
				.pattern("WWW")
				.pattern("RWR")
				.pattern("RSR")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.monocle)
				.define('G', BotaniaBlocks.manaGlass)
				.define('I', BotaniaTags.Items.INGOTS_MANASTEEL)
				.define('N', Items.GOLD_NUGGET)
				.pattern("GN")
				.pattern("IN")
				.pattern(" N")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.swapRing)
				.define('C', Items.CLAY)
				.define('M', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern("CM ")
				.pattern("M M")
				.pattern(" M ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, BotaniaItems.magnetRingGreater)
				.requires(BotaniaTags.Items.INGOTS_TERRASTEEL)
				.requires(BotaniaItems.magnetRing)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.magnetRing))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.speedUpBelt)
				.define('P', BotaniaItems.grassSeeds)
				.define('B', BotaniaItems.travelBelt)
				.define('S', Items.SUGAR)
				.define('M', Items.MAP)
				.pattern(" M ")
				.pattern("PBP")
				.pattern(" S ")
				.unlockedBy("has_item", conditionsFromItem(Items.MAP))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaItems.travelBelt))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.dodgeRing)
				.define('R', BotaniaItems.runeAir)
				.define('E', Items.EMERALD)
				.define('M', BotaniaTags.Items.INGOTS_MANASTEEL)
				.pattern("EM ")
				.pattern("M M")
				.pattern(" MR")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.invisibilityCloak)
				.define('P', BotaniaItems.manaPearl)
				.define('C', Items.PRISMARINE_CRYSTALS)
				.define('W', Items.WHITE_WOOL)
				.define('G', BotaniaBlocks.manaGlass)
				.pattern("CWC")
				.pattern("GWG")
				.pattern("GPG")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaPearl))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.cloudPendant)
				.define('S', BotaniaItems.manaString)
				.define('D', BotaniaTags.Items.INGOTS_MANASTEEL)
				.define('F', BotaniaItems.runeAir)
				.define('M', BotaniaItems.runeAutumn)
				.pattern("MS ")
				.pattern("S S")
				.pattern("DSF")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaString))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.superCloudPendant)
				.define('P', BotaniaItems.cloudPendant)
				.define('B', Items.GHAST_TEAR)
				.define('E', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.define('G', BotaniaItems.lifeEssence)
				.define('N', Items.WHITE_WOOL)
				.pattern("BEB")
				.pattern("BPB")
				.pattern("NGN")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.cloudPendant))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaItems.lifeEssence))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.thirdEye)
				.define('Q', Items.QUARTZ_BLOCK)
				.define('R', Items.GOLDEN_CARROT)
				.define('S', BotaniaItems.runeEarth)
				.define('D', BotaniaTags.Items.GEMS_MANA_DIAMOND)
				.define('E', Items.ENDER_EYE)
				.pattern("RSR")
				.pattern("QEQ")
				.pattern("RDR")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.GEMS_MANA_DIAMOND))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, BotaniaItems.goddessCharm)
				.define('P', BotaniaTags.Items.PETALS_PINK)
				.define('A', BotaniaItems.runeWater)
				.define('S', BotaniaItems.runeSpring)
				.define('D', BotaniaTags.Items.GEMS_MANA_DIAMOND)
				.pattern(" P ")
				.pattern(" P ")
				.pattern("ADS")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.GEMS_MANA_DIAMOND))
				.save(consumer);

	}

	private void registerCorporeaAndRedString(Consumer<FinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.redString)
				.requires(Items.STRING)
				.requires(Items.REDSTONE_BLOCK)
				.requires(BotaniaItems.pixieDust)
				.requires(BotaniaItems.enderAirBottle)
				.group("botania:red_string")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.enderAirBottle))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.redString)
				.requires(Items.STRING)
				.requires(Items.REDSTONE_BLOCK)
				.requires(BotaniaItems.pixieDust)
				.requires(BotaniaItems.enderAirBottle)
				.requires(Items.PUMPKIN)
				.group("botania:red_string")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.enderAirBottle))
				.save(consumer, "botania:red_string_alt");
		registerRedStringBlock(consumer, BotaniaBlocks.redStringDispenser, Ingredient.of(Items.DISPENSER), conditionsFromItem(Items.DISPENSER));
		registerRedStringBlock(consumer, BotaniaBlocks.redStringFertilizer, Ingredient.of(BotaniaItems.fertilizer), conditionsFromItem(BotaniaItems.fertilizer));
		registerRedStringBlock(consumer, BotaniaBlocks.redStringComparator, Ingredient.of(Items.COMPARATOR), conditionsFromItem(Items.COMPARATOR));
		registerRedStringBlock(consumer, BotaniaBlocks.redStringRelay, Ingredient.of(BotaniaBlocks.manaSpreader), conditionsFromItem(BotaniaBlocks.manaSpreader));
		registerRedStringBlock(consumer, BotaniaBlocks.redStringInterceptor, Ingredient.of(Items.STONE_BUTTON), conditionsFromItem(Items.STONE_BUTTON));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, BotaniaItems.corporeaSpark, 4)
				.requires(BotaniaItems.spark)
				.requires(BotaniaItems.pixieDust)
				.requires(BotaniaItems.enderAirBottle)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.enderAirBottle))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaItems.pixieDust))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, BotaniaItems.corporeaSparkMaster)
				.requires(BotaniaItems.corporeaSpark)
				.requires(BotaniaTags.Items.GEMS_DRAGONSTONE)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.corporeaSpark))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BotaniaBlocks.corporeaIndex)
				.define('A', BotaniaItems.enderAirBottle)
				.define('S', BotaniaItems.corporeaSpark)
				.define('D', BotaniaTags.Items.GEMS_DRAGONSTONE)
				.define('O', Items.OBSIDIAN)
				.pattern("AOA")
				.pattern("OSO")
				.pattern("DOD")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.corporeaSpark))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, BotaniaBlocks.corporeaFunnel)
				.requires(Items.DROPPER)
				.requires(BotaniaItems.corporeaSpark)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.corporeaSpark))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, BotaniaBlocks.corporeaInterceptor)
				.requires(Items.REDSTONE_BLOCK)
				.requires(BotaniaItems.corporeaSpark)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.corporeaSpark))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, BotaniaBlocks.corporeaCrystalCube)
				.define('C', BotaniaItems.corporeaSpark)
				.define('G', BotaniaBlocks.elfGlass)
				.define('W', BotaniaTags.Items.DREAMWOOD_LOGS)
				.pattern("C")
				.pattern("G")
				.pattern("W")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.corporeaSpark))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.corporeaBlock, 8)
				.requires(BotaniaBlocks.livingrockPolished)
				.requires(BotaniaItems.corporeaSpark)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.corporeaSpark))
				.save(consumer);
		slabShape(BotaniaBlocks.corporeaSlab, BotaniaBlocks.corporeaBlock).save(consumer);
		stairs(BotaniaBlocks.corporeaStairs, BotaniaBlocks.corporeaBlock).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.corporeaBrick, 4)
				.define('R', BotaniaBlocks.corporeaBlock)
				.pattern("RR")
				.pattern("RR")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.corporeaBlock))
				.save(consumer);
		slabShape(BotaniaBlocks.corporeaBrickSlab, BotaniaBlocks.corporeaBrick).save(consumer);
		stairs(BotaniaBlocks.corporeaBrickStairs, BotaniaBlocks.corporeaBrick).save(consumer);
		wallShape(BotaniaBlocks.corporeaBrickWall, BotaniaBlocks.corporeaBrick, 6).save(consumer);
	}

	private void registerLenses(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.lensNormal)
				.define('S', BotaniaTags.Items.INGOTS_MANASTEEL)
				.define('G', Ingredient.of(Items.GLASS, Items.GLASS_PANE))
				.pattern(" S ")
				.pattern("SGS")
				.pattern(" S ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_MANASTEEL))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensSpeed)
				.requires(BotaniaItems.lensNormal)
				.requires(BotaniaItems.runeAir)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensPower)
				.requires(BotaniaItems.lensNormal)
				.requires(BotaniaItems.runeFire)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensTime)
				.requires(BotaniaItems.lensNormal)
				.requires(BotaniaItems.runeEarth)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensEfficiency)
				.requires(BotaniaItems.lensNormal)
				.requires(BotaniaItems.runeWater)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensBounce)
				.requires(BotaniaItems.lensNormal)
				.requires(BotaniaItems.runeSummer)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensGravity)
				.requires(BotaniaItems.lensNormal)
				.requires(BotaniaItems.runeWinter)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.lensMine)
				.define('P', Items.PISTON)
				.define('A', Items.LAPIS_LAZULI)
				.define('R', Items.REDSTONE)
				.define('L', BotaniaItems.lensNormal)
				.pattern(" P ")
				.pattern("ALA")
				.pattern(" R ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensDamage)
				.requires(BotaniaItems.lensNormal)
				.requires(BotaniaItems.runeWrath)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensPhantom)
				.requires(BotaniaItems.lensNormal)
				.requires(BotaniaBlocks.abstrusePlatform)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensMagnet)
				.requires(BotaniaItems.lensNormal)
				.requires(Items.IRON_INGOT)
				.requires(Items.GOLD_INGOT)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensExplosive)
				.requires(BotaniaItems.lensNormal)
				.requires(BotaniaItems.runeEnvy)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.lensInfluence)
				.define('P', Items.PRISMARINE_CRYSTALS)
				.define('R', BotaniaItems.runeAir)
				.define('L', BotaniaItems.lensNormal)
				.pattern("PRP")
				.pattern("PLP")
				.pattern("PPP")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.lensWeight)
				.define('P', Items.PRISMARINE_CRYSTALS)
				.define('R', BotaniaItems.runeWater)
				.define('L', BotaniaItems.lensNormal)
				.pattern("PPP")
				.pattern("PLP")
				.pattern("PRP")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.lensPaint)
				.define('E', BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.define('W', ItemTags.WOOL)
				.define('L', BotaniaItems.lensNormal)
				.pattern(" E ")
				.pattern("WLW")
				.pattern(" E ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensFire)
				.requires(BotaniaItems.lensNormal)
				.requires(Items.FIRE_CHARGE)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensPiston)
				.requires(BotaniaItems.lensNormal)
				.requires(BotaniaBlocks.pistonRelay)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.lensLight)
				.define('F', Items.FIRE_CHARGE)
				.define('G', Items.GLOWSTONE)
				.define('L', BotaniaItems.lensNormal)
				.pattern("GFG")
				.pattern("FLF")
				.pattern("GFG")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.lensLight)
				.define('F', Items.FIRE_CHARGE)
				.define('G', Items.GLOWSTONE)
				.define('L', BotaniaItems.lensNormal)
				.pattern("FGF")
				.pattern("GLG")
				.pattern("FGF")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer, "botania:lens_light_alt");
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, BotaniaItems.lensMessenger)
				.define('P', Items.PAPER)
				.define('L', BotaniaItems.lensNormal)
				.pattern(" P ")
				.pattern("PLP")
				.pattern(" P ")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.lensNormal))
				.save(consumer);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensWarp)
				.requires(BotaniaItems.lensNormal)
				.requires(BotaniaItems.pixieDust)
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.pixieDust))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensRedirect)
				.requires(BotaniaItems.lensNormal)
				.requires(BotaniaTags.Items.LIVINGWOOD_LOGS)
				.requires(BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_ELEMENTIUM))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensFirework)
				.requires(BotaniaItems.lensNormal)
				.requires(Items.FIREWORK_ROCKET)
				.requires(BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_ELEMENTIUM))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensFlare)
				.requires(BotaniaItems.lensNormal)
				.requires(BotaniaBlocks.elfGlass)
				.requires(BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_ELEMENTIUM))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, BotaniaItems.lensTripwire)
				.requires(BotaniaItems.lensNormal)
				.requires(Items.TRIPWIRE_HOOK)
				.requires(BotaniaTags.Items.INGOTS_ELEMENTIUM)
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_ELEMENTIUM))
				.save(consumer);
	}

	private void registerFloatingFlowers(Consumer<FinishedRecipe> consumer) {
		for (Block block : new Block[] {
				BotaniaFlowerBlocks.pureDaisy, BotaniaFlowerBlocks.manastar, BotaniaFlowerBlocks.hydroangeas, BotaniaFlowerBlocks.endoflame,
				BotaniaFlowerBlocks.thermalily, BotaniaFlowerBlocks.rosaArcana, BotaniaFlowerBlocks.munchdew, BotaniaFlowerBlocks.entropinnyum,
				BotaniaFlowerBlocks.kekimurus, BotaniaFlowerBlocks.gourmaryllis, BotaniaFlowerBlocks.narslimmus, BotaniaFlowerBlocks.spectrolus,
				BotaniaFlowerBlocks.dandelifeon, BotaniaFlowerBlocks.rafflowsia, BotaniaFlowerBlocks.shulkMeNot, BotaniaFlowerBlocks.bellethorn,
				BotaniaFlowerBlocks.bellethornChibi, BotaniaFlowerBlocks.bergamute, BotaniaFlowerBlocks.dreadthorn, BotaniaFlowerBlocks.heiseiDream,
				BotaniaFlowerBlocks.tigerseye, BotaniaFlowerBlocks.jadedAmaranthus, BotaniaFlowerBlocks.orechid, BotaniaFlowerBlocks.fallenKanade,
				BotaniaFlowerBlocks.exoflame, BotaniaFlowerBlocks.agricarnation, BotaniaFlowerBlocks.agricarnationChibi, BotaniaFlowerBlocks.hopperhock,
				BotaniaFlowerBlocks.hopperhockChibi, BotaniaFlowerBlocks.tangleberrie, BotaniaFlowerBlocks.tangleberrieChibi,
				BotaniaFlowerBlocks.jiyuulia, BotaniaFlowerBlocks.jiyuuliaChibi, BotaniaFlowerBlocks.rannuncarpus, BotaniaFlowerBlocks.rannuncarpusChibi,
				BotaniaFlowerBlocks.hyacidus, BotaniaFlowerBlocks.pollidisiac, BotaniaFlowerBlocks.clayconia,
				BotaniaFlowerBlocks.clayconiaChibi, BotaniaFlowerBlocks.loonium, BotaniaFlowerBlocks.daffomill, BotaniaFlowerBlocks.vinculotus,
				BotaniaFlowerBlocks.spectranthemum, BotaniaFlowerBlocks.medumone, BotaniaFlowerBlocks.marimorphosis, BotaniaFlowerBlocks.marimorphosisChibi,
				BotaniaFlowerBlocks.bubbell, BotaniaFlowerBlocks.bubbellChibi, BotaniaFlowerBlocks.solegnolia, BotaniaFlowerBlocks.solegnoliaChibi,
				BotaniaFlowerBlocks.orechidIgnem, BotaniaFlowerBlocks.labellia }) {
			createFloatingFlowerRecipe(consumer, block);
		}
	}

	private void registerConversions(Consumer<FinishedRecipe> consumer) {
		compression(BotaniaItems.manaSteel, BotaniaTags.Items.NUGGETS_MANASTEEL)
				.save(consumer, prefix("conversions/manasteel_from_nuggets"));
		compression(BotaniaItems.elementium, BotaniaTags.Items.NUGGETS_ELEMENTIUM)
				.save(consumer, prefix("conversions/elementium_from_nuggets"));
		compression(BotaniaItems.terrasteel, BotaniaTags.Items.NUGGETS_TERRASTEEL)
				.save(consumer, prefix("conversions/terrasteel_from_nugget"));
		compression(BotaniaBlocks.manasteelBlock, BotaniaTags.Items.INGOTS_MANASTEEL).save(consumer);
		compression(BotaniaBlocks.terrasteelBlock, BotaniaTags.Items.INGOTS_TERRASTEEL).save(consumer);
		compression(BotaniaBlocks.elementiumBlock, BotaniaTags.Items.INGOTS_ELEMENTIUM).save(consumer);
		compression(BotaniaBlocks.manaDiamondBlock, BotaniaTags.Items.GEMS_MANA_DIAMOND).save(consumer);
		compression(BotaniaBlocks.dragonstoneBlock, BotaniaTags.Items.GEMS_DRAGONSTONE).save(consumer);

		MutableObject<FinishedRecipe> base = new MutableObject<>();
		MutableObject<FinishedRecipe> gog = new MutableObject<>();
		compression(BotaniaBlocks.blazeBlock, Items.BLAZE_ROD).save(base::setValue);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.blazeBlock)
				.define('I', Items.BLAZE_POWDER)
				.pattern("III")
				.pattern("III")
				.pattern("III")
				.unlockedBy("has_item", conditionsFromItem(Items.BLAZE_POWDER))
				.save(gog::setValue);
		consumer.accept(new GogAlternationResult(gog.getValue(), base.getValue()));

		deconstructPetalBlock(consumer, BotaniaItems.whitePetal, BotaniaBlocks.petalBlockWhite);
		deconstructPetalBlock(consumer, BotaniaItems.orangePetal, BotaniaBlocks.petalBlockOrange);
		deconstructPetalBlock(consumer, BotaniaItems.magentaPetal, BotaniaBlocks.petalBlockMagenta);
		deconstructPetalBlock(consumer, BotaniaItems.lightBluePetal, BotaniaBlocks.petalBlockLightBlue);
		deconstructPetalBlock(consumer, BotaniaItems.yellowPetal, BotaniaBlocks.petalBlockYellow);
		deconstructPetalBlock(consumer, BotaniaItems.limePetal, BotaniaBlocks.petalBlockLime);
		deconstructPetalBlock(consumer, BotaniaItems.pinkPetal, BotaniaBlocks.petalBlockPink);
		deconstructPetalBlock(consumer, BotaniaItems.grayPetal, BotaniaBlocks.petalBlockGray);
		deconstructPetalBlock(consumer, BotaniaItems.lightGrayPetal, BotaniaBlocks.petalBlockSilver);
		deconstructPetalBlock(consumer, BotaniaItems.cyanPetal, BotaniaBlocks.petalBlockCyan);
		deconstructPetalBlock(consumer, BotaniaItems.purplePetal, BotaniaBlocks.petalBlockPurple);
		deconstructPetalBlock(consumer, BotaniaItems.bluePetal, BotaniaBlocks.petalBlockBlue);
		deconstructPetalBlock(consumer, BotaniaItems.brownPetal, BotaniaBlocks.petalBlockBrown);
		deconstructPetalBlock(consumer, BotaniaItems.greenPetal, BotaniaBlocks.petalBlockGreen);
		deconstructPetalBlock(consumer, BotaniaItems.redPetal, BotaniaBlocks.petalBlockRed);
		deconstructPetalBlock(consumer, BotaniaItems.blackPetal, BotaniaBlocks.petalBlockBlack);

		deconstruct(base::setValue, Items.BLAZE_ROD, BotaniaBlocks.blazeBlock, "blazeblock_deconstruct");
		deconstruct(gog::setValue, Items.BLAZE_POWDER, BotaniaBlocks.blazeBlock, "blazeblock_deconstruct");
		consumer.accept(new GogAlternationResult(gog.getValue(), base.getValue()));
		deconstruct(consumer, BotaniaItems.manaSteel, BotaniaTags.Items.BLOCKS_MANASTEEL, "manasteel_block_deconstruct");
		deconstruct(consumer, BotaniaItems.manaDiamond, BotaniaBlocks.manaDiamondBlock, "manadiamond_block_deconstruct");
		deconstruct(consumer, BotaniaItems.terrasteel, BotaniaTags.Items.BLOCKS_TERRASTEEL, "terrasteel_block_deconstruct");
		deconstruct(consumer, BotaniaItems.elementium, BotaniaTags.Items.BLOCKS_ELEMENTIUM, "elementium_block_deconstruct");
		deconstruct(consumer, BotaniaItems.dragonstone, BotaniaBlocks.dragonstoneBlock, "dragonstone_block_deconstruct");
		deconstruct(consumer, BotaniaItems.manasteelNugget, BotaniaTags.Items.INGOTS_MANASTEEL, "manasteel_to_nuggets");
		deconstruct(consumer, BotaniaItems.terrasteelNugget, BotaniaTags.Items.INGOTS_TERRASTEEL, "terrasteel_to_nugget");
		deconstruct(consumer, BotaniaItems.elementiumNugget, BotaniaTags.Items.INGOTS_ELEMENTIUM, "elementium_to_nuggets");

		recombineSlab(consumer, BotaniaBlocks.livingrock, BotaniaBlocks.livingrockSlab);
		recombineSlab(consumer, BotaniaBlocks.livingrockPolished, BotaniaBlocks.livingrockPolishedSlab);
		recombineSlab(consumer, BotaniaBlocks.livingrockBrick, BotaniaBlocks.livingrockBrickSlab);
		recombineSlab(consumer, BotaniaBlocks.livingwood, BotaniaBlocks.livingwoodSlab);
		recombineSlab(consumer, BotaniaBlocks.livingwoodPlanks, BotaniaBlocks.livingwoodPlankSlab);
		recombineSlab(consumer, BotaniaBlocks.dreamwood, BotaniaBlocks.dreamwoodSlab);
		recombineSlab(consumer, BotaniaBlocks.dreamwoodPlanks, BotaniaBlocks.dreamwoodPlankSlab);
		recombineSlab(consumer, BotaniaBlocks.shimmerrock, BotaniaBlocks.shimmerrockSlab);
		recombineSlab(consumer, BotaniaBlocks.shimmerwoodPlanks, BotaniaBlocks.shimmerwoodPlankSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeStoneForest, BotaniaBlocks.biomeStoneForestSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeBrickForest, BotaniaBlocks.biomeBrickForestSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeCobblestoneForest, BotaniaBlocks.biomeCobblestoneForestSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeStonePlains, BotaniaBlocks.biomeStonePlainsSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeBrickPlains, BotaniaBlocks.biomeBrickPlainsSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeCobblestonePlains, BotaniaBlocks.biomeCobblestonePlainsSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeStoneMountain, BotaniaBlocks.biomeStoneMountainSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeBrickMountain, BotaniaBlocks.biomeBrickMountainSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeCobblestoneMountain, BotaniaBlocks.biomeCobblestoneMountainSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeStoneFungal, BotaniaBlocks.biomeStoneFungalSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeBrickFungal, BotaniaBlocks.biomeBrickFungalSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeCobblestoneFungal, BotaniaBlocks.biomeCobblestoneFungalSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeStoneSwamp, BotaniaBlocks.biomeStoneSwampSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeBrickSwamp, BotaniaBlocks.biomeBrickSwampSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeCobblestoneSwamp, BotaniaBlocks.biomeCobblestoneSwampSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeStoneDesert, BotaniaBlocks.biomeStoneDesertSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeBrickDesert, BotaniaBlocks.biomeBrickDesertSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeCobblestoneDesert, BotaniaBlocks.biomeCobblestoneDesertSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeStoneTaiga, BotaniaBlocks.biomeStoneTaigaSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeBrickTaiga, BotaniaBlocks.biomeBrickTaigaSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeCobblestoneTaiga, BotaniaBlocks.biomeCobblestoneTaigaSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeStoneMesa, BotaniaBlocks.biomeStoneMesaSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeBrickMesa, BotaniaBlocks.biomeBrickMesaSlab);
		recombineSlab(consumer, BotaniaBlocks.biomeCobblestoneMesa, BotaniaBlocks.biomeCobblestoneMesaSlab);
		recombineSlab(consumer, BotaniaBlocks.whitePavement, BotaniaBlocks.whitePavementSlab);
		recombineSlab(consumer, BotaniaBlocks.blackPavement, BotaniaBlocks.blackPavementSlab);
		recombineSlab(consumer, BotaniaBlocks.bluePavement, BotaniaBlocks.bluePavementSlab);
		recombineSlab(consumer, BotaniaBlocks.yellowPavement, BotaniaBlocks.yellowPavementSlab);
		recombineSlab(consumer, BotaniaBlocks.redPavement, BotaniaBlocks.redPavementSlab);
		recombineSlab(consumer, BotaniaBlocks.greenPavement, BotaniaBlocks.greenPavementSlab);
		recombineSlab(consumer, BotaniaBlocks.darkQuartz, BotaniaBlocks.darkQuartzSlab);
		recombineSlab(consumer, BotaniaBlocks.manaQuartz, BotaniaBlocks.manaQuartzSlab);
		recombineSlab(consumer, BotaniaBlocks.blazeQuartz, BotaniaBlocks.blazeQuartzSlab);
		recombineSlab(consumer, BotaniaBlocks.lavenderQuartz, BotaniaBlocks.lavenderQuartzSlab);
		recombineSlab(consumer, BotaniaBlocks.redQuartz, BotaniaBlocks.redQuartzSlab);
		recombineSlab(consumer, BotaniaBlocks.elfQuartz, BotaniaBlocks.elfQuartzSlab);
		recombineSlab(consumer, BotaniaBlocks.sunnyQuartz, BotaniaBlocks.sunnyQuartzSlab);
		recombineSlab(consumer, BotaniaBlocks.corporeaBlock, BotaniaBlocks.corporeaSlab);
		recombineSlab(consumer, BotaniaBlocks.corporeaBrick, BotaniaBlocks.corporeaBrickSlab);
	}

	private void registerDecor(Consumer<FinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.livingrockPolished, 4)
				.define('R', BotaniaBlocks.livingrock)
				.pattern("RR")
				.pattern("RR")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.livingrock))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.livingrockSlate)
				.define('R', BotaniaBlocks.livingrockSlab)
				.pattern("R")
				.pattern("R")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.livingrock))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.livingrockBrick, 4)
				.define('R', BotaniaBlocks.livingrockPolished)
				.pattern("RR")
				.pattern("RR")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.livingrockPolished))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.livingrockBrickChiseled)
				.define('R', BotaniaBlocks.livingrockBrickSlab)
				.pattern("R")
				.pattern("R")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.livingrockBrick))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.livingrockBrickMossy)
				.requires(BotaniaBlocks.livingrockBrick)
				.requires(Items.MOSS_BLOCK)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.livingrockBrick))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.livingrockBrickMossy)
				.requires(BotaniaBlocks.livingrockBrick)
				.requires(Items.VINE)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.livingrockBrick))
				.save(consumer, "botania:mossy_livingrock_bricks_vine");
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.shimmerrock)
				.requires(BotaniaBlocks.livingrock)
				.requires(BotaniaBlocks.bifrostPerm)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.bifrostPerm))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaItems.rainbowRod))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, BotaniaBlocks.shimmerwoodPlanks)
				.requires(BotaniaBlocks.dreamwoodPlanks)
				.requires(BotaniaBlocks.bifrostPerm)
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.bifrostPerm))
				.unlockedBy("has_alt_item", conditionsFromItem(BotaniaItems.rainbowRod))
				.save(consumer);

		registerForQuartz(consumer, LibBlockNames.QUARTZ_DARK, BotaniaItems.darkQuartz);
		registerForQuartz(consumer, LibBlockNames.QUARTZ_MANA, BotaniaItems.manaQuartz);
		registerForQuartz(consumer, LibBlockNames.QUARTZ_BLAZE, BotaniaItems.blazeQuartz);
		registerForQuartz(consumer, LibBlockNames.QUARTZ_LAVENDER, BotaniaItems.lavenderQuartz);
		registerForQuartz(consumer, LibBlockNames.QUARTZ_RED, BotaniaItems.redQuartz);
		registerForQuartz(consumer, LibBlockNames.QUARTZ_ELF, BotaniaItems.elfQuartz);
		registerForQuartz(consumer, LibBlockNames.QUARTZ_SUNNY, BotaniaItems.sunnyQuartz);

		registerForWood(consumer, LibBlockNames.LIVING_WOOD);
		registerForWood(consumer, LibBlockNames.DREAM_WOOD);

		stairs(BotaniaBlocks.livingrockStairs, BotaniaBlocks.livingrock).save(consumer);
		slabShape(BotaniaBlocks.livingrockSlab, BotaniaBlocks.livingrock).save(consumer);
		wallShape(BotaniaBlocks.livingrockWall, BotaniaBlocks.livingrock, 6).save(consumer);

		stairs(BotaniaBlocks.livingrockPolishedStairs, BotaniaBlocks.livingrockPolished).save(consumer);
		slabShape(BotaniaBlocks.livingrockPolishedSlab, BotaniaBlocks.livingrockPolished).save(consumer);
		wallShape(BotaniaBlocks.livingrockPolishedWall, BotaniaBlocks.livingrockPolished, 6).save(consumer);

		stairs(BotaniaBlocks.livingrockBrickStairs, BotaniaBlocks.livingrockBrick).save(consumer);
		slabShape(BotaniaBlocks.livingrockBrickSlab, BotaniaBlocks.livingrockBrick).save(consumer);
		wallShape(BotaniaBlocks.livingrockBrickWall, BotaniaBlocks.livingrockBrick, 6).save(consumer);

		stairs(BotaniaBlocks.livingrockBrickMossyStairs, BotaniaBlocks.livingrockBrickMossy).save(consumer);
		slabShape(BotaniaBlocks.livingrockBrickMossySlab, BotaniaBlocks.livingrockBrickMossy).save(consumer);
		wallShape(BotaniaBlocks.livingrockBrickMossyWall, BotaniaBlocks.livingrockBrickMossy, 6).save(consumer);

		stairs(BotaniaBlocks.shimmerrockStairs, BotaniaBlocks.shimmerrock).save(consumer);
		slabShape(BotaniaBlocks.shimmerrockSlab, BotaniaBlocks.shimmerrock).save(consumer);
		stairs(BotaniaBlocks.shimmerwoodPlankStairs, BotaniaBlocks.shimmerwoodPlanks).save(consumer);
		slabShape(BotaniaBlocks.shimmerwoodPlankSlab, BotaniaBlocks.shimmerwoodPlanks).save(consumer);

		for (String variant : LibBlockNames.METAMORPHIC_VARIANTS) {
			registerForMetamorphic(consumer, variant);
		}

		Item[] pavementIngredients = { Items.AIR, Items.COAL, Items.LAPIS_LAZULI, Items.REDSTONE, Items.WHEAT, Items.SLIME_BALL };
		for (int i = 0; i < pavementIngredients.length; i++) {
			registerForPavement(consumer, LibBlockNames.PAVEMENT_VARIANTS[i], pavementIngredients[i]);
		}

		wallShape(BotaniaBlocks.managlassPane, BotaniaBlocks.manaGlass, 16).save(consumer);
		wallShape(BotaniaBlocks.alfglassPane, BotaniaBlocks.elfGlass, 16).save(consumer);
		wallShape(BotaniaBlocks.bifrostPane, BotaniaBlocks.bifrostPerm, 16).save(consumer);

		// azulejo0 recipe in loader-specific datagen

		List<Item> allAzulejos = IntStream.range(0, 16).mapToObj(i -> "azulejo_" + i)
				.map(ResourceLocationHelper::prefix)
				.map(BuiltInRegistries.ITEM::get)
				.toList();
		for (int i = 0; i < allAzulejos.size(); i++) {
			int resultIndex = (i + 1) % allAzulejos.size();
			String recipeName = "azulejo_" + resultIndex;
			if (resultIndex == 0) {
				recipeName += "_alt";
			}
			ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, allAzulejos.get(resultIndex))
					.requires(allAzulejos.get(i))
					.unlockedBy("has_azulejo", conditionsFromItem(BotaniaBlocks.azulejo0))
					.group("botania:azulejo_cycling")
					.save(consumer, prefix(recipeName));
		}

		cosmeticBauble(consumer, BotaniaItems.blackBowtie, BotaniaItems.whitePetal);
		cosmeticBauble(consumer, BotaniaItems.blackTie, BotaniaItems.orangePetal);
		cosmeticBauble(consumer, BotaniaItems.redGlasses, BotaniaItems.magentaPetal);
		cosmeticBauble(consumer, BotaniaItems.puffyScarf, BotaniaItems.lightBluePetal);
		cosmeticBauble(consumer, BotaniaItems.engineerGoggles, BotaniaItems.yellowPetal);
		cosmeticBauble(consumer, BotaniaItems.eyepatch, BotaniaItems.limePetal);
		cosmeticBauble(consumer, BotaniaItems.wickedEyepatch, BotaniaItems.pinkPetal);
		cosmeticBauble(consumer, BotaniaItems.redRibbons, BotaniaItems.grayPetal);
		cosmeticBauble(consumer, BotaniaItems.pinkFlowerBud, BotaniaItems.lightGrayPetal);
		cosmeticBauble(consumer, BotaniaItems.polkaDottedBows, BotaniaItems.cyanPetal);
		cosmeticBauble(consumer, BotaniaItems.blueButterfly, BotaniaItems.purplePetal);
		cosmeticBauble(consumer, BotaniaItems.catEars, BotaniaItems.bluePetal);
		cosmeticBauble(consumer, BotaniaItems.witchPin, BotaniaItems.brownPetal);
		cosmeticBauble(consumer, BotaniaItems.devilTail, BotaniaItems.greenPetal);
		cosmeticBauble(consumer, BotaniaItems.kamuiEye, BotaniaItems.redPetal);
		cosmeticBauble(consumer, BotaniaItems.googlyEyes, BotaniaItems.blackPetal);
		cosmeticBauble(consumer, BotaniaItems.fourLeafClover, Items.WHITE_DYE);
		cosmeticBauble(consumer, BotaniaItems.clockEye, Items.ORANGE_DYE);
		cosmeticBauble(consumer, BotaniaItems.unicornHorn, Items.MAGENTA_DYE);
		cosmeticBauble(consumer, BotaniaItems.devilHorns, Items.LIGHT_BLUE_DYE);
		cosmeticBauble(consumer, BotaniaItems.hyperPlus, Items.YELLOW_DYE);
		cosmeticBauble(consumer, BotaniaItems.botanistEmblem, Items.LIME_DYE);
		cosmeticBauble(consumer, BotaniaItems.ancientMask, Items.PINK_DYE);
		cosmeticBauble(consumer, BotaniaItems.eerieMask, Items.GRAY_DYE);
		cosmeticBauble(consumer, BotaniaItems.alienAntenna, Items.LIGHT_GRAY_DYE);
		cosmeticBauble(consumer, BotaniaItems.anaglyphGlasses, Items.CYAN_DYE);
		cosmeticBauble(consumer, BotaniaItems.orangeShades, Items.PURPLE_DYE);
		cosmeticBauble(consumer, BotaniaItems.grouchoGlasses, Items.BLUE_DYE);
		cosmeticBauble(consumer, BotaniaItems.thickEyebrows, Items.BROWN_DYE);
		cosmeticBauble(consumer, BotaniaItems.lusitanicShield, Items.GREEN_DYE);
		cosmeticBauble(consumer, BotaniaItems.tinyPotatoMask, Items.RED_DYE);
		cosmeticBauble(consumer, BotaniaItems.questgiverMark, Items.BLACK_DYE);
		cosmeticBauble(consumer, BotaniaItems.thinkingHand, BotaniaBlocks.tinyPotato);
	}

	protected void registerSimpleArmorSet(Consumer<FinishedRecipe> consumer, Ingredient item, String variant,
			CriterionTriggerInstance criterion) {
		Item helmet = getItemOrThrow(prefix(variant + "_helmet"));
		Item chestplate = getItemOrThrow(prefix(variant + "_chestplate"));
		Item leggings = getItemOrThrow(prefix(variant + "_leggings"));
		Item boots = getItemOrThrow(prefix(variant + "_boots"));
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, helmet)
				.define('S', item)
				.pattern("SSS")
				.pattern("S S")
				.unlockedBy("has_item", criterion)
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, chestplate)
				.define('S', item)
				.pattern("S S")
				.pattern("SSS")
				.pattern("SSS")
				.unlockedBy("has_item", criterion)
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, leggings)
				.define('S', item)
				.pattern("SSS")
				.pattern("S S")
				.pattern("S S")
				.unlockedBy("has_item", criterion)
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, boots)
				.define('S', item)
				.pattern("S S")
				.pattern("S S")
				.unlockedBy("has_item", criterion)
				.save(consumer);
	}

	protected void registerToolSetRecipes(Consumer<FinishedRecipe> consumer, Ingredient item, Ingredient stick,
			CriterionTriggerInstance criterion, ItemLike sword, ItemLike pickaxe,
			ItemLike axe, ItemLike hoe, ItemLike shovel, ItemLike shears) {
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, pickaxe)
				.define('S', item)
				.define('T', stick)
				.pattern("SSS")
				.pattern(" T ")
				.pattern(" T ")
				.unlockedBy("has_item", criterion)
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, shovel)
				.define('S', item)
				.define('T', stick)
				.pattern("S")
				.pattern("T")
				.pattern("T")
				.unlockedBy("has_item", criterion)
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, axe)
				.define('S', item)
				.define('T', stick)
				.pattern("SS")
				.pattern("ST")
				.pattern(" T")
				.unlockedBy("has_item", criterion)
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, hoe)
				.define('S', item)
				.define('T', stick)
				.pattern("SS")
				.pattern(" T")
				.pattern(" T")
				.unlockedBy("has_item", criterion)
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, sword)
				.define('S', item)
				.define('T', stick)
				.pattern("S")
				.pattern("S")
				.pattern("T")
				.unlockedBy("has_item", criterion)
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, shears)
				.define('S', item)
				.pattern(" S")
				.pattern("S ")
				.unlockedBy("has_item", criterion)
				.save(consumer);

	}

	protected void registerTerrasteelUpgradeRecipe(Consumer<FinishedRecipe> consumer, ItemLike output,
			ItemLike upgradedInput, ItemLike runeInput) {
		ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, output)
				.define('T', BotaniaItems.livingwoodTwig)
				.define('S', BotaniaTags.Items.INGOTS_TERRASTEEL)
				.define('R', runeInput)
				.define('A', upgradedInput)
				.pattern("TRT")
				.pattern("SAS")
				.pattern(" S ")
				.unlockedBy("has_item", conditionsFromTag(BotaniaTags.Items.INGOTS_TERRASTEEL))
				.unlockedBy("has_prev_tier", conditionsFromItem(upgradedInput))
				.save(WrapperResult.ofType(ArmorUpgradeRecipe.SERIALIZER, consumer));
	}

	public static void registerRedStringBlock(Consumer<FinishedRecipe> consumer, ItemLike output, Ingredient input, CriterionTriggerInstance criterion) {
		ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, output)
				.define('R', BotaniaBlocks.livingrock)
				.define('S', BotaniaItems.redString)
				.define('M', input)
				.pattern("RRR")
				.pattern("RMS")
				.pattern("RRR")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.redString))
				.unlockedBy("has_base_block", criterion)
				.save(consumer);
	}

	protected void createFloatingFlowerRecipe(Consumer<FinishedRecipe> consumer, ItemLike input) {
		ResourceLocation inputName = BuiltInRegistries.ITEM.getKey(input.asItem());
		Item output = getItemOrThrow(new ResourceLocation(inputName.getNamespace(), "floating_" + inputName.getPath()));
		ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, output)
				.requires(BotaniaTags.Items.FLOATING_FLOWERS)
				.requires(input)
				.group("botania:floating_flower")
				.unlockedBy("has_item", conditionsFromItem(input))
				.save(consumer);
	}

	protected void deconstruct(Consumer<FinishedRecipe> consumer, ItemLike output, ItemLike input, String name) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, output, 9)
				.unlockedBy("has_item", conditionsFromItem(output))
				.requires(input)
				.save(consumer, prefix("conversions/" + name));
	}

	protected void deconstruct(Consumer<FinishedRecipe> consumer, ItemLike output, TagKey<Item> input, String name) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, output, 9)
				.unlockedBy("has_item", conditionsFromItem(output))
				.requires(input)
				.save(consumer, prefix("conversions/" + name));
	}

	protected void deconstructPetalBlock(Consumer<FinishedRecipe> consumer, ItemLike output, ItemLike input) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, output, 9)
				.unlockedBy("has_item", conditionsFromItem(output))
				.requires(input).group("botania:petal_block_deconstruct")
				.save(consumer, prefix("conversions/" + BuiltInRegistries.ITEM.getKey(input.asItem()).getPath() + "_deconstruct"));
	}

	protected void recombineSlab(Consumer<FinishedRecipe> consumer, ItemLike fullBlock, ItemLike slab) {
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, fullBlock)
				.define('Q', slab)
				.pattern("QQ")
				.unlockedBy("has_item", conditionsFromItem(slab))
				.save(consumer, prefix("slab_recombine/" + BuiltInRegistries.ITEM.getKey(fullBlock.asItem()).getPath()));
	}

	protected ShapedRecipeBuilder petalApothecary(ItemLike block, ItemLike apothecary) {
		return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, apothecary)
				.define('P', BotaniaTags.Items.PETALS)
				.define('C', block)
				.pattern("CPC")
				.pattern(" C ")
				.pattern("CCC");
	}

	protected void registerForQuartz(Consumer<FinishedRecipe> consumer, String variant, ItemLike baseItem) {
		Block base = getBlockOrThrow(prefix(variant));
		Block slab = getBlockOrThrow(prefix(variant + LibBlockNames.SLAB_SUFFIX));
		Block stairs = getBlockOrThrow(prefix(variant + LibBlockNames.STAIR_SUFFIX));
		Block chiseled = getBlockOrThrow(prefix("chiseled_" + variant));
		Block pillar = getBlockOrThrow(prefix(variant + "_pillar"));

		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, base)
				.define('Q', baseItem)
				.pattern("QQ")
				.pattern("QQ")
				.group("botania:quartz_block")
				.unlockedBy("has_item", conditionsFromItem(baseItem))
				.save(consumer);
		stairs(stairs, base).group("botania:quartz_stairs").save(consumer);
		slabShape(slab, base).group("botania:quartz_slab").save(consumer);
		pillar(pillar, base).group("botania:quartz_pillar").save(consumer);
		chiseled(chiseled, slab).group("botania:quartz_chiseled")
				.unlockedBy("has_base_item", conditionsFromItem(base)).save(consumer);
	}

	protected void registerForWood(Consumer<FinishedRecipe> consumer, String variant) {

		TagKey<Item> tag = variant.contains("livingwood") ? BotaniaTags.Items.LIVINGWOOD_LOGS : BotaniaTags.Items.DREAMWOOD_LOGS;
		Block log = getBlockOrThrow(prefix(variant + "_log"));
		Block wood = getBlockOrThrow(prefix(variant));
		Block strippedLog = getBlockOrThrow(prefix("stripped_" + variant + "_log"));
		Block strippedWood = getBlockOrThrow(prefix("stripped_" + variant));
		Block glimmeringLog = getBlockOrThrow(prefix("glimmering_" + variant + "_log"));
		Block glimmeringWood = getBlockOrThrow(prefix("glimmering_" + variant));
		Block glimmeringStrippedLog = getBlockOrThrow(prefix("glimmering_stripped_" + variant + "_log"));
		Block glimmeringStrippedWood = getBlockOrThrow(prefix("glimmering_stripped_" + variant));
		Block stairs = getBlockOrThrow(prefix(variant + "_stairs"));
		Block slab = getBlockOrThrow(prefix(variant + "_slab"));
		Block wall = getBlockOrThrow(prefix(variant + "_wall"));
		Block strippedStairs = getBlockOrThrow(prefix("stripped_" + variant + "_stairs"));
		Block strippedSlab = getBlockOrThrow(prefix("stripped_" + variant + "_slab"));
		Block strippedWall = getBlockOrThrow(prefix("stripped_" + variant + "_wall"));

		Block planks = getBlockOrThrow(prefix(variant + "_planks"));
		Block planksStairs = getBlockOrThrow(prefix(variant + "_planks_stairs"));
		Block planksSlab = getBlockOrThrow(prefix(variant + "_planks_slab"));
		Block mossyPlanks = getBlockOrThrow(prefix("mossy_" + variant + "_planks"));
		Block framed = getBlockOrThrow(prefix("framed_" + variant));
		Block patternFramed = getBlockOrThrow(prefix("pattern_framed_" + variant));
		Block fence = getBlockOrThrow(prefix(variant + "_fence"));
		Block fenceGate = getBlockOrThrow(prefix(variant + "_fence_gate"));

		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, planks, 4).requires(tag).group("planks")
				.unlockedBy("has_item", conditionsFromTag(tag)).save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, wood, 3).group("wood").unlockedBy("has_log", conditionsFromItem(log))
				.define('#', log)
				.pattern("##")
				.pattern("##")
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, strippedWood, 3).group("wood").unlockedBy("has_log", conditionsFromItem(strippedLog))
				.define('#', strippedLog)
				.pattern("##")
				.pattern("##")
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, glimmeringLog).group("botania:glimmering_" + variant)
				.requires(log)
				.requires(Items.GLOWSTONE_DUST)
				.unlockedBy("has_item", conditionsFromItem(log))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, glimmeringWood).group("botania:glimmering_" + variant)
				.requires(wood)
				.requires(Items.GLOWSTONE_DUST)
				.unlockedBy("has_item", conditionsFromItem(wood))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, glimmeringStrippedLog).group("botania:glimmering_" + variant)
				.requires(strippedLog)
				.requires(Items.GLOWSTONE_DUST)
				.unlockedBy("has_item", conditionsFromItem(strippedLog))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, glimmeringStrippedWood).group("botania:glimmering_" + variant)
				.requires(strippedWood)
				.requires(Items.GLOWSTONE_DUST)
				.unlockedBy("has_item", conditionsFromItem(strippedWood))
				.save(consumer);
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, glimmeringWood, 3).group("botania:glimmering_" + variant)
				.unlockedBy("has_log", conditionsFromItem(glimmeringLog))
				.define('#', glimmeringLog)
				.pattern("##")
				.pattern("##")
				.save(consumer, prefix("glimmering_" + variant + "_from_log"));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, glimmeringStrippedWood, 3).group("botania:glimmering_" + variant)
				.unlockedBy("has_log", conditionsFromItem(glimmeringStrippedLog))
				.define('#', glimmeringStrippedLog)
				.pattern("##")
				.pattern("##")
				.save(consumer, prefix("glimmering_stripped_" + variant + "_from_log"));

		stairs(stairs, wood).save(consumer);
		slabShape(slab, wood).save(consumer);
		wallShape(wall, wood, 6).save(consumer);
		fence(fence, planks).save(consumer);
		fenceGate(fenceGate, planks).save(consumer);

		stairs(strippedStairs, strippedWood).save(consumer);
		slabShape(strippedSlab, strippedWood).save(consumer);
		wallShape(strippedWall, strippedWood, 6).save(consumer);

		stairs(planksStairs, planks).save(consumer);
		slabShape(planksSlab, planks).save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, mossyPlanks)
				.requires(planks)
				.requires(Items.MOSS_BLOCK)
				.unlockedBy("has_item", conditionsFromItem(planks))
				.save(consumer);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, mossyPlanks)
				.requires(planks)
				.requires(Items.VINE)
				.unlockedBy("has_item", conditionsFromItem(planks))
				.save(consumer, prefix("mossy_" + variant + "_planks_vine"));
		ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, framed, 4)
				.define('W', planks)
				.pattern("W W")
				.pattern("W W")
				.unlockedBy("has_item", conditionsFromItem(planks))
				.save(consumer);
		ringShape(patternFramed, planks).save(consumer);
	}

	private void registerForPavement(Consumer<FinishedRecipe> consumer, String color, Item mainInput) {
		String baseName = color + LibBlockNames.PAVEMENT_SUFFIX;
		Block base = getBlockOrThrow(prefix(baseName));
		Block stair = getBlockOrThrow(prefix(baseName + LibBlockNames.STAIR_SUFFIX));
		Block slab = getBlockOrThrow(prefix(baseName + LibBlockNames.SLAB_SUFFIX));

		ShapelessRecipeBuilder builder = ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, base, 3)
				.requires(BotaniaBlocks.livingrock)
				.requires(Items.COBBLESTONE)
				.requires(Items.GRAVEL)
				.group("botania:pavement")
				.unlockedBy("has_item", conditionsFromItem(BotaniaBlocks.livingrock));
		if (mainInput != Items.AIR) {
			builder.requires(mainInput);
		}
		builder.save(consumer);

		slabShape(slab, base).group("botania:pavement_slab").save(consumer);
		stairs(stair, base).group("botania:pavement_stairs").save(consumer);
	}

	private void registerForMetamorphic(Consumer<FinishedRecipe> consumer, String variant) {
		Block base = getBlockOrThrow(prefix(LibBlockNames.METAMORPHIC_PREFIX + variant + "_stone"));
		Block slab = getBlockOrThrow(prefix(LibBlockNames.METAMORPHIC_PREFIX + variant + "_stone" + LibBlockNames.SLAB_SUFFIX));
		Block stair = getBlockOrThrow(prefix(LibBlockNames.METAMORPHIC_PREFIX + variant + "_stone" + LibBlockNames.STAIR_SUFFIX));
		Block wall = getBlockOrThrow(prefix(LibBlockNames.METAMORPHIC_PREFIX + variant + "_stone" + LibBlockNames.WALL_SUFFIX));
		Block brick = getBlockOrThrow(prefix(LibBlockNames.METAMORPHIC_PREFIX + variant + "_bricks"));
		Block brickSlab = getBlockOrThrow(prefix(LibBlockNames.METAMORPHIC_PREFIX + variant + "_bricks" + LibBlockNames.SLAB_SUFFIX));
		Block brickStair = getBlockOrThrow(prefix(LibBlockNames.METAMORPHIC_PREFIX + variant + "_bricks" + LibBlockNames.STAIR_SUFFIX));
		Block brickWall = getBlockOrThrow(prefix(LibBlockNames.METAMORPHIC_PREFIX + variant + "_bricks" + LibBlockNames.WALL_SUFFIX));
		Block chiseledBrick = getBlockOrThrow(prefix("chiseled_" + LibBlockNames.METAMORPHIC_PREFIX + variant + "_bricks"));
		Block cobble = getBlockOrThrow(prefix(LibBlockNames.METAMORPHIC_PREFIX + variant + "_cobblestone"));
		Block cobbleSlab = getBlockOrThrow(prefix(LibBlockNames.METAMORPHIC_PREFIX + variant + "_cobblestone" + LibBlockNames.SLAB_SUFFIX));
		Block cobbleStair = getBlockOrThrow(prefix(LibBlockNames.METAMORPHIC_PREFIX + variant + "_cobblestone" + LibBlockNames.STAIR_SUFFIX));
		Block cobbleWall = getBlockOrThrow(prefix(LibBlockNames.METAMORPHIC_PREFIX + variant + "_cobblestone" + LibBlockNames.WALL_SUFFIX));

		InventoryChangeTrigger.TriggerInstance marimorphosis = conditionsFromItem(BotaniaFlowerBlocks.marimorphosis);
		slabShape(slab, base).group("botania:metamorphic_stone_slab")
				.unlockedBy("has_flower_item", marimorphosis).save(consumer);
		stairs(stair, base).group("botania:metamorphic_stone_stairs")
				.unlockedBy("has_flower_item", marimorphosis).save(consumer);
		wallShape(wall, base, 6).group("botania:metamorphic_stone_wall")
				.unlockedBy("has_flower_item", marimorphosis).save(consumer);

		brick(brick, base).group("botania:metamorphic_brick")
				.unlockedBy("has_flower_item", marimorphosis).save(consumer);
		slabShape(brickSlab, brick).group("botania:metamorphic_brick_slab")
				.unlockedBy("has_flower_item", marimorphosis).save(consumer);
		stairs(brickStair, brick).group("botania:metamorphic_brick_stairs")
				.unlockedBy("has_flower_item", marimorphosis).save(consumer);
		wallShape(brickWall, brick, 6).group("botania:metamorphic_brick_wall")
				.unlockedBy("has_flower_item", marimorphosis).save(consumer);
		chiseled(chiseledBrick, brickSlab).unlockedBy("has_base_item", conditionsFromItem(brick))
				.unlockedBy("has_flower_item", marimorphosis).save(consumer);

		slabShape(cobbleSlab, cobble).group("botania:metamorphic_cobble_slab")
				.unlockedBy("has_flower_item", marimorphosis).save(consumer);
		stairs(cobbleStair, cobble).group("botania:metamorphic_cobble_stairs")
				.unlockedBy("has_flower_item", marimorphosis).save(consumer);
		wallShape(cobbleWall, cobble, 6).group("botania:metamorphic_cobble_wall")
				.unlockedBy("has_flower_item", marimorphosis).save(consumer);
	}

	private ShapedRecipeBuilder compression(ItemLike output, TagKey<Item> input) {
		return ShapedRecipeBuilder.shaped(output instanceof Block ? RecipeCategory.BUILDING_BLOCKS : RecipeCategory.MISC, output)
				.define('I', input)
				.pattern("III")
				.pattern("III")
				.pattern("III")
				.unlockedBy("has_item", conditionsFromTag(input));
	}

	protected ShapedRecipeBuilder compression(ItemLike output, ItemLike input) {
		return ShapedRecipeBuilder.shaped(output instanceof Block ? RecipeCategory.BUILDING_BLOCKS : RecipeCategory.MISC, output)
				.define('I', input)
				.pattern("III")
				.pattern("III")
				.pattern("III")
				.unlockedBy("has_item", conditionsFromItem(input));
	}

	protected ShapedRecipeBuilder brick(ItemLike output, ItemLike input) {
		return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 4)
				.unlockedBy("has_item", conditionsFromItem(input))
				.define('Q', input)
				.pattern("QQ")
				.pattern("QQ");
	}

	protected ShapedRecipeBuilder stairs(ItemLike output, ItemLike input) {
		return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 4)
				.unlockedBy("has_item", conditionsFromItem(input))
				.define('Q', input)
				.pattern("Q  ")
				.pattern("QQ ")
				.pattern("QQQ");
	}

	protected ShapedRecipeBuilder slabShape(ItemLike output, ItemLike input) {
		return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 6)
				.unlockedBy("has_item", conditionsFromItem(input))
				.define('Q', input)
				.pattern("QQQ");
	}

	protected ShapedRecipeBuilder pillar(ItemLike output, ItemLike input) {
		return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 2)
				.unlockedBy("has_item", conditionsFromItem(input))
				.define('Q', input)
				.pattern("Q")
				.pattern("Q");
	}

	protected ShapedRecipeBuilder chiseled(ItemLike output, ItemLike input) {
		return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output)
				.unlockedBy("has_item", conditionsFromItem(input))
				.define('Q', input)
				.pattern("Q")
				.pattern("Q");
	}

	protected ShapedRecipeBuilder wallShape(ItemLike output, ItemLike input, int amount) {
		return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, amount)
				.unlockedBy("has_item", conditionsFromItem(input))
				.define('B', input)
				.pattern("BBB")
				.pattern("BBB");
	}

	protected ShapedRecipeBuilder fence(ItemLike output, ItemLike input) {
		return ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output, 3)
				.unlockedBy("has_item", conditionsFromItem(input))
				.group("wooden_fence")
				.define('B', input)
				.define('S', Items.STICK)
				.pattern("BSB")
				.pattern("BSB");
	}

	protected ShapedRecipeBuilder fenceGate(ItemLike output, ItemLike input) {
		return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, output, 1)
				.unlockedBy("has_item", conditionsFromItem(input))
				.group("wooden_fence_gate")
				.define('B', input)
				.define('S', Items.STICK)
				.pattern("SBS")
				.pattern("SBS");
	}

	protected ShapedRecipeBuilder ringShape(ItemLike output, ItemLike input) {
		return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 4)
				.define('W', input)
				.pattern(" W ")
				.pattern("W W")
				.pattern(" W ")
				.unlockedBy("has_item", conditionsFromItem(input));
	}

	protected void cosmeticBauble(Consumer<FinishedRecipe> consumer, ItemLike output, ItemLike input) {
		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, output)
				.define('P', input)
				.define('S', BotaniaItems.manaString)
				.pattern("PPP")
				.pattern("PSP")
				.pattern("PPP")
				.group("botania:cosmetic_bauble")
				.unlockedBy("has_item", conditionsFromItem(BotaniaItems.manaString))
				.save(consumer);
	}

	protected void specialRecipe(Consumer<FinishedRecipe> consumer, NoOpRecipeSerializer<? extends CraftingRecipe> serializer) {
		ResourceLocation name = BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer);
		SpecialRecipeBuilder.special(serializer).save(consumer, prefix("dynamic/" + name.getPath()).toString());
	}

	protected Block getBlockOrThrow(ResourceLocation location) {
		return BuiltInRegistries.BLOCK.getOrThrow(ResourceKey.create(Registries.BLOCK, location));
	}

	protected Item getItemOrThrow(ResourceLocation location) {
		return BuiltInRegistries.ITEM.getOrThrow(ResourceKey.create(Registries.ITEM, location));
	}

	@Override
	public String getName() {
		return "Botania crafting recipes";
	}
}
