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
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.item.lens.LensItem;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.common.lib.LibMisc;

import java.util.concurrent.CompletableFuture;

import static vazkii.botania.common.item.BotaniaItems.*;

public class ItemTagProvider extends ItemTagsProvider {
	public ItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagsProvider.TagLookup<Block>> blockTagProvider) {
		super(packOutput, lookupProvider, blockTagProvider);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(ItemTags.AXES).add(manasteelAxe, elementiumAxe, terraAxe);
		this.tag(ItemTags.HOES).add(manasteelHoe, elementiumHoe);
		this.tag(ItemTags.PICKAXES).add(manasteelPick, elementiumPick, terraPick, glassPick);
		this.tag(ItemTags.SHOVELS).add(manasteelShovel, elementiumShovel);
		this.tag(ItemTags.SWORDS).add(manasteelSword, elementiumSword, terraSword, thunderSword, starSword);

		this.copy(BlockTags.RAILS, ItemTags.RAILS);
		this.copy(BlockTags.SLABS, ItemTags.SLABS);
		this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
		this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
		this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
		this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
		this.copy(BlockTags.WALLS, ItemTags.WALLS);
		this.copy(BlockTags.FENCES, ItemTags.FENCES);
		this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);

		this.copy(BotaniaTags.Blocks.MUNDANE_FLOATING_FLOWERS, BotaniaTags.Items.MUNDANE_FLOATING_FLOWERS);
		this.copy(BotaniaTags.Blocks.SPECIAL_FLOATING_FLOWERS, BotaniaTags.Items.SPECIAL_FLOATING_FLOWERS);
		this.copy(BotaniaTags.Blocks.FLOATING_FLOWERS, BotaniaTags.Items.FLOATING_FLOWERS);
		this.copy(BotaniaTags.Blocks.DOUBLE_MYSTICAL_FLOWERS, BotaniaTags.Items.DOUBLE_MYSTICAL_FLOWERS);
		this.copy(BotaniaTags.Blocks.MYSTICAL_FLOWERS, BotaniaTags.Items.MYSTICAL_FLOWERS);

		this.copy(BotaniaTags.Blocks.MISC_SPECIAL_FLOWERS, BotaniaTags.Items.MISC_SPECIAL_FLOWERS);
		this.copy(BotaniaTags.Blocks.GENERATING_SPECIAL_FLOWERS, BotaniaTags.Items.GENERATING_SPECIAL_FLOWERS);
		this.copy(BotaniaTags.Blocks.FUNCTIONAL_SPECIAL_FLOWERS, BotaniaTags.Items.FUNCTIONAL_SPECIAL_FLOWERS);
		this.copy(BotaniaTags.Blocks.SPECIAL_FLOWERS, BotaniaTags.Items.SPECIAL_FLOWERS);
		this.copy(BotaniaTags.Blocks.MINI_FLOWERS, BotaniaTags.Items.MINI_FLOWERS);
		this.tag(BotaniaTags.Items.CONTRIBUTOR_HEADFLOWERS)
				.addTag(BotaniaTags.Items.SPECIAL_FLOWERS)
				.add(BotaniaBlocks.motifDaybloom.asItem(), BotaniaBlocks.motifNightshade.asItem());

		this.tag(ItemTags.TALL_FLOWERS).addTag(BotaniaTags.Items.DOUBLE_MYSTICAL_FLOWERS);
		this.tag(ItemTags.SMALL_FLOWERS).addTag(BotaniaTags.Items.MYSTICAL_FLOWERS).addTag(BotaniaTags.Items.SPECIAL_FLOWERS)
				.add(BotaniaBlocks.motifDaybloom.asItem(), BotaniaBlocks.motifNightshade.asItem(), BotaniaBlocks.motifHydroangeas.asItem());

		this.tag(BotaniaTags.Items.BURST_VIEWERS).add(monocle);
		this.tag(BotaniaTags.Items.TERRA_PICK_BLACKLIST).add(auraRing, auraRingGreater, terrasteelHelm, spark);
		TagsProvider.TagAppender<Item> builder = this.tag(BotaniaTags.Items.LENS);
		BuiltInRegistries.ITEM.stream().filter(i -> i instanceof LensItem && BuiltInRegistries.ITEM.getKey(i).getNamespace().equals(LibMisc.MOD_ID))
				.map(BuiltInRegistries.ITEM::getKey)
				.sorted()
				.forEach(item -> builder.add(ResourceKey.create(Registries.ITEM, item)));

		this.tag(BotaniaTags.Items.LENS_GLUE).add(Items.SLIME_BALL).add(Items.HONEY_BOTTLE);

		this.tag(ItemTags.PIGLIN_LOVED).add(BotaniaBlocks.alchemyCatalyst.asItem(), divaCharm,
				BotaniaBlocks.hourglass.asItem(), BotaniaBlocks.manaPylon.asItem(), monocle);
		this.tag(ItemTags.MUSIC_DISCS).add(recordGaia1, recordGaia2);
		this.tag(ItemTags.CLUSTER_MAX_HARVESTABLES).add(manasteelPick, elementiumPick, terraPick, glassPick);
		this.tag(ItemTags.LECTERN_BOOKS).add(lexicon);
		this.tag(ItemTags.BOOKSHELF_BOOKS).add(lexicon);

		this.tag(BotaniaTags.Items.DUSTS_MANA).add(manaPowder);

		this.tag(BotaniaTags.Items.GEMS_DRAGONSTONE).add(dragonstone);
		this.tag(BotaniaTags.Items.GEMS_MANA_DIAMOND).add(manaDiamond);

		this.tag(BotaniaTags.Items.INGOTS_ELEMENTIUM).add(elementium);
		this.tag(BotaniaTags.Items.INGOTS_MANASTEEL).add(manaSteel);
		this.tag(BotaniaTags.Items.INGOTS_TERRASTEEL).add(terrasteel);

		this.tag(BotaniaTags.Items.NUGGETS_ELEMENTIUM).add(elementiumNugget);
		this.tag(BotaniaTags.Items.NUGGETS_MANASTEEL).add(manasteelNugget);
		this.tag(BotaniaTags.Items.NUGGETS_TERRASTEEL).add(terrasteelNugget);

		this.copy(BotaniaTags.Blocks.BLOCKS_ELEMENTIUM, BotaniaTags.Items.BLOCKS_ELEMENTIUM);
		this.copy(BotaniaTags.Blocks.BLOCKS_MANASTEEL, BotaniaTags.Items.BLOCKS_MANASTEEL);
		this.copy(BotaniaTags.Blocks.BLOCKS_TERRASTEEL, BotaniaTags.Items.BLOCKS_TERRASTEEL);

		this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
		this.copy(BotaniaTags.Blocks.LIVINGWOOD_LOGS, BotaniaTags.Items.LIVINGWOOD_LOGS);
		this.copy(BotaniaTags.Blocks.LIVINGWOOD_LOGS_GLIMMERING, BotaniaTags.Items.LIVINGWOOD_LOGS_GLIMMERING);
		this.copy(BotaniaTags.Blocks.DREAMWOOD_LOGS, BotaniaTags.Items.DREAMWOOD_LOGS);
		this.copy(BotaniaTags.Blocks.DREAMWOOD_LOGS_GLIMMERING, BotaniaTags.Items.DREAMWOOD_LOGS_GLIMMERING);

		this.tag(ItemTags.SAND);
		this.tag(BotaniaTags.Items.DISPOSABLE).add(Items.DIRT, Items.GRAVEL, Items.COBBLESTONE,
				Items.NETHERRACK, Items.COBBLED_DEEPSLATE, Items.END_STONE)
				.addTag(ItemTags.SAND);
		this.tag(BotaniaTags.Items.SEMI_DISPOSABLE).add(Items.ANDESITE, Items.DIORITE, Items.GRANITE,
				Items.TUFF, Items.CALCITE, Items.STONE, Items.BASALT, Items.BLACKSTONE,
				Items.DEEPSLATE, Items.DRIPSTONE_BLOCK, Items.POINTED_DRIPSTONE, Items.MOSS_BLOCK,
				Items.SANDSTONE, Items.RED_SANDSTONE)
				.addOptional(new ResourceLocation("quark", "jasper"))
				.addOptional(new ResourceLocation("quark", "limestone"))
				.addOptional(new ResourceLocation("quark", "marble"))
				.addOptional(new ResourceLocation("quark", "slate"));

		this.tag(BotaniaTags.Items.RUNES).add(
				runeWater, runeFire, runeEarth, runeAir,
				runeSpring, runeSummer, runeAutumn, runeWinter,
				runeMana, runeLust, runeGluttony, runeGreed,
				runeSloth, runeWrath, runeEnvy, runePride
		);

		TagAppender<Item> allPetals = this.tag(BotaniaTags.Items.PETALS);
		for (DyeColor color : DyeColor.values()) {
			var petalTag = BotaniaTags.Items.getPetalTag(color);
			this.tag(petalTag).add(getPetal(color), BotaniaBlocks.getMushroom(color).asItem());
			allPetals.addTag(petalTag);
		}

		this.tag(BotaniaTags.Items.LOONIUM_BLACKLIST)
				.add(lexicon, overgrowthSeed, blackLotus, blackerLotus)
				.addTag(ItemTags.MUSIC_DISCS);
		this.tag(ItemTags.ARROWS);
		this.tag(BotaniaTags.Items.LOONIUM_OFFHAND_EQUIPMENT)
				.add(Items.FIREWORK_ROCKET, Items.TOTEM_OF_UNDYING)
				.addTag(ItemTags.ARROWS);
		this.tag(BotaniaTags.Items.MAGNET_RING_BLACKLIST);
		this.tag(BotaniaTags.Items.RODS).add(
				dirtRod,
				skyDirtRod,
				terraformRod,
				cobbleRod,
				waterRod,
				tornadoRod,
				fireRod,
				diviningRod,
				smeltRod,
				exchangeRod,
				rainbowRod,
				gravityRod,
				missileRod);
		this.tag(BotaniaTags.Items.MANA_USING_ITEMS).add(
				bloodPendant,
				cobbleRod,
				crystalBow,
				dirtRod,
				divaCharm,
				diviningRod,
				elementiumAxe,
				elementiumBoots,
				elementiumChest,
				elementiumHelm,
				elementiumHoe,
				elementiumLegs,
				elementiumPick,
				elementiumShears,
				elementiumShovel,
				elementiumSword,
				enderHand,
				exchangeRod,
				fireRod,
				flightTiara,
				flugelEye,
				glassPick,
				goddessCharm,
				gravityRod,
				infiniteFruit,
				invisibilityCloak,
				kingKey,
				livingwoodBow,
				lokiRing,
				manasteelAxe,
				manasteelBoots,
				manasteelChest,
				manasteelHelm,
				manasteelHoe,
				manasteelLegs,
				manasteelPick,
				manasteelShears,
				manasteelShovel,
				manasteelSword,
				manaweaveBoots,
				manaweaveChest,
				manaweaveHelm,
				manaweaveLegs,
				miningRing,
				missileRod,
				rainbowRod,
				skyDirtRod,
				smeltRod,
				starSword,
				superTravelBelt,
				terraAxe,
				terraPick,
				terraSword,
				terraformRod,
				terrasteelBoots,
				terrasteelChest,
				terrasteelHelm,
				terrasteelLegs,
				thirdEye,
				thunderSword,
				tornadoRod,
				travelBelt,
				waterRing,
				waterRod
		);
		this.tag(ItemTags.FREEZE_IMMUNE_WEARABLES).add(icePendant,
				manaweaveHelm, manaweaveChest, manaweaveLegs, manaweaveBoots);

		this.tag(BotaniaTags.Items.SEED_APOTHECARY_REAGENT)
				.add(Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS)
				.addOptionalTag(new ResourceLocation("forge", "seeds"));
	}
}
