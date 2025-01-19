/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data.recipes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.crafting.ManaInfusionRecipe;
import vazkii.botania.common.crafting.StateIngredients;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.item.BotaniaItems;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class ManaInfusionProvider extends BotaniaRecipeProvider {
	public ManaInfusionProvider(PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	public String getName() {
		return "Botania mana pool recipes";
	}

	@Override
	public void buildRecipes(RecipeOutput consumer) {
		normal(consumer, id("manasteel"), new ItemStack(BotaniaItems.manaSteel), ingr(Items.IRON_INGOT), 3000);
		normal(consumer, id("manasteel_block"), new ItemStack(BotaniaBlocks.manasteelBlock), ingr(Blocks.IRON_BLOCK), 27000);

		normal(consumer, id("mana_pearl"), new ItemStack(BotaniaItems.manaPearl), ingr(Items.ENDER_PEARL), 6000);

		normal(consumer, id("mana_diamond"), new ItemStack(BotaniaItems.manaDiamond), Ingredient.of(Items.DIAMOND), 10000);
		normal(consumer, id("mana_diamond_block"), new ItemStack(BotaniaBlocks.manaDiamondBlock), ingr(Blocks.DIAMOND_BLOCK), 90000);

		// TODO: make this a tag
		// I think the tag exists, so EMI picks it up, but it might as well be in the recipe
		Ingredient dust = Ingredient.of(Items.GUNPOWDER, Items.REDSTONE, Items.GLOWSTONE_DUST, Items.SUGAR);
		normal(consumer, id("mana_powder_dust"), new ItemStack(BotaniaItems.manaPowder), dust, 500);
		// TODO: this one too
		Ingredient dyeIngredient = Ingredient.of(ColorHelper.supportedColors().map(DyeItem::byColor).toArray(Item[]::new));
		normal(consumer, id("mana_powder_dye"), new ItemStack(BotaniaItems.manaPowder), dyeIngredient, 400);

		normal(consumer, id("piston_relay"), new ItemStack(BotaniaBlocks.pistonRelay), ingr(Blocks.PISTON), 15000);
		normal(consumer, id("mana_cookie"), new ItemStack(BotaniaItems.manaCookie), ingr(Items.COOKIE), 20000);
		normal(consumer, id("grass_seeds"), new ItemStack(BotaniaItems.grassSeeds), ingr(Blocks.SHORT_GRASS), 2500);
		normal(consumer, id("podzol_seeds"), new ItemStack(BotaniaItems.podzolSeeds), ingr(Blocks.DEAD_BUSH), 2500);

		normal(consumer, id("mycel_seeds"), new ItemStack(BotaniaItems.mycelSeeds), Ingredient.of(Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM), 6500);

		normal(consumer, id("mana_quartz"), new ItemStack(BotaniaItems.manaQuartz), ingr(Items.QUARTZ), 250);
		normal(consumer, id("tiny_potato"), new ItemStack(BotaniaBlocks.tinyPotato), ingr(Items.POTATO), 1337);

		normal(consumer, id("mana_glass"), new ItemStack(BotaniaBlocks.manaGlass), ingr(Blocks.GLASS), 150);
		normal(consumer, id("mana_string"), new ItemStack(BotaniaItems.manaString), ingr(Items.STRING), 1250);

		normal(consumer, id("mana_bottle"), new ItemStack(BotaniaItems.manaBottle), ingr(Items.GLASS_BOTTLE), 5000);

		alchemy(consumer, id("rotten_flesh_to_leather"), new ItemStack(Items.LEATHER), ingr(Items.ROTTEN_FLESH), 600);

		cycle(consumer, 40, "botania:log_cycle", Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.MANGROVE_LOG, Blocks.CHERRY_LOG);
		cycle(consumer, 40, "botania:froglight_cycle", Blocks.OCHRE_FROGLIGHT, Blocks.VERDANT_FROGLIGHT, Blocks.PEARLESCENT_FROGLIGHT);
		cycle(consumer, 120, "botania:sapling_cycle", Blocks.OAK_SAPLING, Blocks.SPRUCE_SAPLING, Blocks.BIRCH_SAPLING, Blocks.JUNGLE_SAPLING, Blocks.ACACIA_SAPLING, Blocks.DARK_OAK_SAPLING, Blocks.MANGROVE_PROPAGULE, Blocks.CHERRY_SAPLING);

		deconstruct(consumer, id("glowstone_deconstruct"), Items.GLOWSTONE_DUST, Blocks.GLOWSTONE);
		deconstruct(consumer, id("quartz_deconstruct"), Items.QUARTZ, Blocks.QUARTZ_BLOCK);
		deconstruct(consumer, id("dark_quartz_deconstruct"), BotaniaItems.darkQuartz, BotaniaBlocks.darkQuartz);
		deconstruct(consumer, id("mana_quartz_deconstruct"), BotaniaItems.manaQuartz, BotaniaBlocks.manaQuartz);
		deconstruct(consumer, id("blaze_quartz_deconstruct"), BotaniaItems.blazeQuartz, BotaniaBlocks.blazeQuartz);
		deconstruct(consumer, id("lavender_quartz_deconstruct"), BotaniaItems.lavenderQuartz, BotaniaBlocks.lavenderQuartz);
		deconstruct(consumer, id("red_quartz_deconstruct"), BotaniaItems.redQuartz, BotaniaBlocks.redQuartz);
		deconstruct(consumer, id("elf_quartz_deconstruct"), BotaniaItems.elfQuartz, BotaniaBlocks.elfQuartz);
		deconstruct(consumer, id("sunny_quartz_deconstruct"), BotaniaItems.sunnyQuartz, BotaniaBlocks.sunnyQuartz);

		alchemy(consumer, id("chiseled_stone_bricks"), new ItemStack(Blocks.CHISELED_STONE_BRICKS, 1), ingr(Blocks.STONE_BRICKS), 150);
		alchemy(consumer, id("ice"), new ItemStack(Blocks.ICE), ingr(Blocks.SNOW_BLOCK), 2250);

		// TODO: These can use the `cycle` function, right?? check when datagen operational
		final String vineLilypadGroup = "botania:vine_and_lily_pad_cycle";
		alchemy(consumer, id("vine_to_lily_pad"), new ItemStack(Blocks.LILY_PAD), ingr(Blocks.VINE), 320, vineLilypadGroup);
		alchemy(consumer, id("lily_pad_to_vine"), new ItemStack(Blocks.VINE), ingr(Blocks.LILY_PAD), 320, vineLilypadGroup);

		cycle(consumer, 200, "botania:fish_cycle", Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH);
		cycle(consumer, 6000, "botania:crop_cycle", Items.COCOA_BEANS, Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS);

		alchemy(consumer, id("potato_unpoison"), new ItemStack(Items.POTATO), ingr(Items.POISONOUS_POTATO), 1200);
		alchemy(consumer, id("blaze_rod_to_nether_wart"), new ItemStack(Items.NETHER_WART), ingr(Items.BLAZE_ROD), 4000);

		cycle(consumer, 200, "", Items.GUNPOWDER, Items.FLINT);

		alchemy(consumer, id("book_to_name_tag"), new ItemStack(Items.NAME_TAG), ingr(Items.WRITABLE_BOOK), 6000);

		alchemy(consumer, id("wool_deconstruct"), new ItemStack(Items.STRING, 3), Ingredient.of(ItemTags.WOOL), 100);

		final String cactusSlimeGroup = "botania:cactus_and_slime_cycle";
		alchemy(consumer, id("cactus_to_slime"), new ItemStack(Items.SLIME_BALL), ingr(Blocks.CACTUS), 1200, cactusSlimeGroup);
		alchemy(consumer, id("slime_to_cactus"), new ItemStack(Blocks.CACTUS), ingr(Items.SLIME_BALL), 1200, cactusSlimeGroup);

		alchemy(consumer, id("ender_pearl_from_ghast_tear"), new ItemStack(Items.ENDER_PEARL), ingr(Items.GHAST_TEAR), 28000);

		cycle(consumer, 300, "botania:glowstone_and_redstone_cycle", Items.GLOWSTONE_DUST, Items.REDSTONE);

		alchemy(consumer, id("cobble_to_sand"), new ItemStack(Blocks.SAND), ingr(Blocks.COBBLESTONE), 50);
		alchemy(consumer, id("terracotta_to_red_sand"), new ItemStack(Blocks.RED_SAND), ingr(Blocks.TERRACOTTA), 50);

		deconstruct(consumer, id("clay_deconstruct"), Items.CLAY_BALL, Blocks.CLAY);
		deconstruct(consumer, id("brick_deconstruct"), Items.BRICK, Blocks.BRICKS);

		alchemy(consumer, id("coarse_dirt"), new ItemStack(Blocks.COARSE_DIRT), ingr(Blocks.DIRT), 120);
		alchemy(consumer, id("soul_soil"), new ItemStack(Blocks.SOUL_SOIL), ingr(Blocks.SOUL_SAND), 120);

		alchemy(consumer, id("stone_to_andesite"), new ItemStack(Blocks.ANDESITE), ingr(Blocks.STONE), 200);
		cycle(consumer, 200, "botania:stone_cycle", Blocks.DIORITE, Blocks.GRANITE, Blocks.ANDESITE);

		cycle(consumer, 200, "botania:117_stone_cycle", Blocks.TUFF, Blocks.CALCITE, Blocks.DEEPSLATE);

		cycle(consumer, 500, "botania:shrub_cycle", Blocks.FERN, Blocks.DEAD_BUSH, Blocks.SHORT_GRASS);

		// NB: No wither rose is intentional
		cycle(consumer, 400, "botania:flower_cycle", Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP,
				Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY, Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY,
				Blocks.SUNFLOWER, Blocks.LILAC, Blocks.ROSE_BUSH, Blocks.PEONY);

		alchemy(consumer, id("dripleaf_shrinking"), new ItemStack(Blocks.SMALL_DRIPLEAF), ingr(Items.BIG_DRIPLEAF), 500);
		alchemy(consumer, id("chorus_fruit_to_flower"), new ItemStack(Blocks.CHORUS_FLOWER), ingr(Items.POPPED_CHORUS_FRUIT), 10000);

		cycle(consumer, 240, "botania:berry_cycle", Items.APPLE, Items.SWEET_BERRIES, Items.GLOW_BERRIES);

		mini(consumer, BotaniaFlowerBlocks.agricarnationChibi, BotaniaFlowerBlocks.agricarnation);
		mini(consumer, BotaniaFlowerBlocks.clayconiaChibi, BotaniaFlowerBlocks.clayconia);
		mini(consumer, BotaniaFlowerBlocks.bellethornChibi, BotaniaFlowerBlocks.bellethorn);
		mini(consumer, BotaniaFlowerBlocks.bubbellChibi, BotaniaFlowerBlocks.bubbell);
		mini(consumer, BotaniaFlowerBlocks.hopperhockChibi, BotaniaFlowerBlocks.hopperhock);
		mini(consumer, BotaniaFlowerBlocks.jiyuuliaChibi, BotaniaFlowerBlocks.jiyuulia);
		mini(consumer, BotaniaFlowerBlocks.tangleberrieChibi, BotaniaFlowerBlocks.tangleberrie);
		mini(consumer, BotaniaFlowerBlocks.marimorphosisChibi, BotaniaFlowerBlocks.marimorphosis);
		mini(consumer, BotaniaFlowerBlocks.rannuncarpusChibi, BotaniaFlowerBlocks.rannuncarpus);
		mini(consumer, BotaniaFlowerBlocks.solegnoliaChibi, BotaniaFlowerBlocks.solegnolia);

		alchemy(consumer, id("hydroangeas_motif"), new ItemStack(BotaniaBlocks.motifHydroangeas), ingr(BotaniaFlowerBlocks.hydroangeas), 2500);

		conjuration(consumer, id("redstone_dupe"), new ItemStack(Items.REDSTONE, 2), ingr(Items.REDSTONE), 5000);
		conjuration(consumer, id("glowstone_dupe"), new ItemStack(Items.GLOWSTONE_DUST, 2), ingr(Items.GLOWSTONE_DUST), 5000);
		conjuration(consumer, id("quartz_dupe"), new ItemStack(Items.QUARTZ, 2), ingr(Items.QUARTZ), 2500);
		conjuration(consumer, id("coal_dupe"), new ItemStack(Items.COAL, 2), ingr(Items.COAL), 2100);
		conjuration(consumer, id("snowball_dupe"), new ItemStack(Items.SNOWBALL, 2), ingr(Items.SNOWBALL), 200);
		conjuration(consumer, id("netherrack_dupe"), new ItemStack(Blocks.NETHERRACK, 2), ingr(Blocks.NETHERRACK), 200);
		conjuration(consumer, id("soul_sand_dupe"), new ItemStack(Blocks.SOUL_SAND, 2), ingr(Blocks.SOUL_SAND), 1500);
		conjuration(consumer, id("gravel_dupe"), new ItemStack(Blocks.GRAVEL, 2), ingr(Blocks.GRAVEL), 720);

		conjuration(consumer, id("oak_leaves_dupe"), new ItemStack(Blocks.OAK_LEAVES, 2), ingr(Blocks.OAK_LEAVES), 2000);
		conjuration(consumer, id("birch_leaves_dupe"), new ItemStack(Blocks.BIRCH_LEAVES, 2), ingr(Blocks.BIRCH_LEAVES), 2000);
		conjuration(consumer, id("spruce_leaves_dupe"), new ItemStack(Blocks.SPRUCE_LEAVES, 2), ingr(Blocks.SPRUCE_LEAVES), 2000);
		conjuration(consumer, id("jungle_leaves_dupe"), new ItemStack(Blocks.JUNGLE_LEAVES, 2), ingr(Blocks.JUNGLE_LEAVES), 2000);
		conjuration(consumer, id("acacia_leaves_dupe"), new ItemStack(Blocks.ACACIA_LEAVES, 2), ingr(Blocks.ACACIA_LEAVES), 2000);
		conjuration(consumer, id("dark_oak_leaves_dupe"), new ItemStack(Blocks.DARK_OAK_LEAVES, 2), ingr(Blocks.DARK_OAK_LEAVES), 2000);
		conjuration(consumer, id("azalea_leaves_dupe"), new ItemStack(Blocks.AZALEA_LEAVES, 2), ingr(Blocks.AZALEA_LEAVES), 2000);
		conjuration(consumer, id("flowering_azalea_leaves_dupe"), new ItemStack(Blocks.FLOWERING_AZALEA_LEAVES, 2), ingr(Blocks.FLOWERING_AZALEA_LEAVES), 2000);
		conjuration(consumer, id("mangrove_leaves_dupe"), new ItemStack(Blocks.MANGROVE_LEAVES, 2), ingr(Blocks.MANGROVE_LEAVES), 2000);
		conjuration(consumer, id("cherry_leaves_dupe"), new ItemStack(Blocks.CHERRY_LEAVES, 2), ingr(Blocks.CHERRY_LEAVES), 2000);

		conjuration(consumer, id("grass"), new ItemStack(Blocks.SHORT_GRASS, 2), ingr(Blocks.SHORT_GRASS), 800);
	}

	private static final StateIngredient ALCHEMY = StateIngredients.of(BotaniaBlocks.alchemyCatalyst);
	private static final StateIngredient CONJURATION = StateIngredients.of(BotaniaBlocks.conjurationCatalyst);

	private static void normal(RecipeOutput consumer, ResourceLocation id, ItemStack output, Ingredient input, int mana) {
		consumer.accept(id, new ManaInfusionRecipe(output, input, mana, null, null), null);
	}

	private static void alchemy(RecipeOutput consumer, ResourceLocation id, ItemStack output, Ingredient input, int mana, String group) {
		consumer.accept(id, new ManaInfusionRecipe(output, input, mana, group, ALCHEMY), null);
	}

	private static void alchemy(RecipeOutput consumer, ResourceLocation id, ItemStack output, Ingredient input, int mana) {
		alchemy(consumer, id, output, input, mana, null);
	}

	private static void conjuration(RecipeOutput consumer, ResourceLocation id, ItemStack output, Ingredient input, int mana) {
		consumer.accept(id, new ManaInfusionRecipe(output, input, mana, "", CONJURATION), null);
	}

	protected void cycle(RecipeOutput consumer, int cost, String group, ItemLike... items) {
		for (int i = 0; i < items.length; i++) {
			Ingredient in = ingr(items[i]);
			ItemStack out = new ItemStack(i == items.length - 1 ? items[0] : items[i + 1]);
			String id = String.format("%s_to_%s", BuiltInRegistries.ITEM.getKey(items[i].asItem()).getPath(), BuiltInRegistries.ITEM.getKey(out.getItem()).getPath());
			alchemy(consumer, id(id), out, in, cost, group);
		}
	}

	protected void mini(RecipeOutput consumer, ItemLike mini, ItemLike full) {
		alchemy(consumer, id(BuiltInRegistries.ITEM.getKey(mini.asItem()).getPath()), new ItemStack(mini), ingr(full), 2500, "botania:flower_shrinking");
	}

	protected void deconstruct(RecipeOutput consumer, ResourceLocation id, ItemLike items, ItemLike block) {
		alchemy(consumer, id, new ItemStack(items, 4), ingr(block), 25, "botania:block_deconstruction");
	}

	protected ResourceLocation id(String s) {
		return botaniaRL("mana_infusion/" + s);
	}

	protected static Ingredient ingr(ItemLike i) {
		return Ingredient.of(i);
	}
}
