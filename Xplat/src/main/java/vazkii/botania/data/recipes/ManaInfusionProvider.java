/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data.recipes;

import com.google.gson.JsonObject;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.botania.common.crafting.StateIngredientHelper;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.BotaniaItems;

import java.util.function.Consumer;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class ManaInfusionProvider extends BotaniaRecipeProvider {
	public ManaInfusionProvider(PackOutput packOutput) {
		super(packOutput);
	}

	@Override
	public String getName() {
		return "Botania mana pool recipes";
	}

	@Override
	public void buildRecipes(Consumer<net.minecraft.data.recipes.FinishedRecipe> consumer) {
		consumer.accept(new FinishedRecipe(id("manasteel"), new ItemStack(BotaniaItems.manaSteel), Ingredient.of(Items.IRON_INGOT), 3000));
		consumer.accept(new FinishedRecipe(id("manasteel_block"), new ItemStack(BotaniaBlocks.manasteelBlock), ingr(Blocks.IRON_BLOCK), 27000));

		consumer.accept(new FinishedRecipe(id("mana_pearl"), new ItemStack(BotaniaItems.manaPearl), ingr(Items.ENDER_PEARL), 6000));

		consumer.accept(new FinishedRecipe(id("mana_diamond"), new ItemStack(BotaniaItems.manaDiamond), Ingredient.of(Items.DIAMOND), 10000));
		consumer.accept(new FinishedRecipe(id("mana_diamond_block"), new ItemStack(BotaniaBlocks.manaDiamondBlock), ingr(Blocks.DIAMOND_BLOCK), 90000));

		Ingredient dust = Ingredient.of(Items.GUNPOWDER, Items.REDSTONE, Items.GLOWSTONE_DUST, Items.SUGAR);
		consumer.accept(new FinishedRecipe(id("mana_powder_dust"), new ItemStack(BotaniaItems.manaPowder), dust, 500));
		Ingredient dyeIngredient = Ingredient.of(ColorHelper.supportedColors().map(DyeItem::byColor).toArray(Item[]::new));
		consumer.accept(new FinishedRecipe(id("mana_powder_dye"), new ItemStack(BotaniaItems.manaPowder), dyeIngredient, 400));

		consumer.accept(new FinishedRecipe(id("piston_relay"), new ItemStack(BotaniaBlocks.pistonRelay), ingr(Blocks.PISTON), 15000));
		consumer.accept(new FinishedRecipe(id("mana_cookie"), new ItemStack(BotaniaItems.manaCookie), ingr(Items.COOKIE), 20000));
		consumer.accept(new FinishedRecipe(id("grass_seeds"), new ItemStack(BotaniaItems.grassSeeds), ingr(Blocks.GRASS), 2500));
		consumer.accept(new FinishedRecipe(id("podzol_seeds"), new ItemStack(BotaniaItems.podzolSeeds), ingr(Blocks.DEAD_BUSH), 2500));

		consumer.accept(new FinishedRecipe(id("mycel_seeds"), new ItemStack(BotaniaItems.mycelSeeds), Ingredient.of(Blocks.RED_MUSHROOM, Blocks.BROWN_MUSHROOM), 6500));

		consumer.accept(new FinishedRecipe(id("mana_quartz"), new ItemStack(BotaniaItems.manaQuartz), ingr(Items.QUARTZ), 250));
		consumer.accept(new FinishedRecipe(id("tiny_potato"), new ItemStack(BotaniaBlocks.tinyPotato), ingr(Items.POTATO), 1337));

		consumer.accept(new FinishedRecipe(id("mana_glass"), new ItemStack(BotaniaBlocks.manaGlass), ingr(Blocks.GLASS), 150));
		consumer.accept(new FinishedRecipe(id("mana_string"), new ItemStack(BotaniaItems.manaString), ingr(Items.STRING), 1250));

		consumer.accept(new FinishedRecipe(id("mana_bottle"), new ItemStack(BotaniaItems.manaBottle), ingr(Items.GLASS_BOTTLE), 5000));

		consumer.accept(FinishedRecipe.alchemy(id("rotten_flesh_to_leather"), new ItemStack(Items.LEATHER), ingr(Items.ROTTEN_FLESH), 600));

		cycle(consumer, 40, "botania:log_cycle", Blocks.OAK_LOG, Blocks.SPRUCE_LOG, Blocks.BIRCH_LOG, Blocks.JUNGLE_LOG, Blocks.ACACIA_LOG, Blocks.DARK_OAK_LOG, Blocks.MANGROVE_LOG, Blocks.CHERRY_LOG);
		cycle(consumer, 40, "botania:froglight_cycle", Blocks.OCHRE_FROGLIGHT, Blocks.VERDANT_FROGLIGHT, Blocks.PEARLESCENT_FROGLIGHT);
		cycle(consumer, 120, "botania:sapling_cycle", Blocks.OAK_SAPLING, Blocks.SPRUCE_SAPLING, Blocks.BIRCH_SAPLING, Blocks.JUNGLE_SAPLING, Blocks.ACACIA_SAPLING, Blocks.DARK_OAK_SAPLING, Blocks.MANGROVE_PROPAGULE, Blocks.CHERRY_SAPLING);

		consumer.accept(deconstruct("glowstone_deconstruct", Items.GLOWSTONE_DUST, Blocks.GLOWSTONE));
		consumer.accept(deconstruct("quartz_deconstruct", Items.QUARTZ, Blocks.QUARTZ_BLOCK));
		consumer.accept(deconstruct("dark_quartz_deconstruct", BotaniaItems.darkQuartz, BotaniaBlocks.darkQuartz));
		consumer.accept(deconstruct("mana_quartz_deconstruct", BotaniaItems.manaQuartz, BotaniaBlocks.manaQuartz));
		consumer.accept(deconstruct("blaze_quartz_deconstruct", BotaniaItems.blazeQuartz, BotaniaBlocks.blazeQuartz));
		consumer.accept(deconstruct("lavender_quartz_deconstruct", BotaniaItems.lavenderQuartz, BotaniaBlocks.lavenderQuartz));
		consumer.accept(deconstruct("red_quartz_deconstruct", BotaniaItems.redQuartz, BotaniaBlocks.redQuartz));
		consumer.accept(deconstruct("elf_quartz_deconstruct", BotaniaItems.elfQuartz, BotaniaBlocks.elfQuartz));
		consumer.accept(deconstruct("sunny_quartz_deconstruct", BotaniaItems.sunnyQuartz, BotaniaBlocks.sunnyQuartz));

		consumer.accept(FinishedRecipe.alchemy(id("chiseled_stone_bricks"), new ItemStack(Blocks.CHISELED_STONE_BRICKS, 1), ingr(Blocks.STONE_BRICKS), 150));
		consumer.accept(FinishedRecipe.alchemy(id("ice"), new ItemStack(Blocks.ICE), ingr(Blocks.SNOW_BLOCK), 2250));

		final String vineLilypadGroup = "botania:vine_and_lily_pad_cycle";
		consumer.accept(FinishedRecipe.alchemy(id("vine_to_lily_pad"), new ItemStack(Blocks.LILY_PAD), ingr(Blocks.VINE), 320, vineLilypadGroup));
		consumer.accept(FinishedRecipe.alchemy(id("lily_pad_to_vine"), new ItemStack(Blocks.VINE), ingr(Blocks.LILY_PAD), 320, vineLilypadGroup));

		cycle(consumer, 200, "botania:fish_cycle", Items.COD, Items.SALMON, Items.TROPICAL_FISH, Items.PUFFERFISH);
		cycle(consumer, 6000, "botania:crop_cycle", Items.COCOA_BEANS, Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS);

		consumer.accept(FinishedRecipe.alchemy(id("potato_unpoison"), new ItemStack(Items.POTATO), ingr(Items.POISONOUS_POTATO), 1200));
		consumer.accept(FinishedRecipe.alchemy(id("blaze_rod_to_nether_wart"), new ItemStack(Items.NETHER_WART), ingr(Items.BLAZE_ROD), 4000));

		cycle(consumer, 200, "", Items.GUNPOWDER, Items.FLINT);

		consumer.accept(FinishedRecipe.alchemy(id("book_to_name_tag"), new ItemStack(Items.NAME_TAG), ingr(Items.WRITABLE_BOOK), 6000));

		consumer.accept(FinishedRecipe.alchemy(id("wool_deconstruct"), new ItemStack(Items.STRING, 3), Ingredient.of(ItemTags.WOOL), 100));

		final String cactusSlimeGroup = "botania:cactus_and_slime_cycle";
		consumer.accept(FinishedRecipe.alchemy(id("cactus_to_slime"), new ItemStack(Items.SLIME_BALL), ingr(Blocks.CACTUS), 1200, cactusSlimeGroup));
		consumer.accept(FinishedRecipe.alchemy(id("slime_to_cactus"), new ItemStack(Blocks.CACTUS), ingr(Items.SLIME_BALL), 1200, cactusSlimeGroup));

		consumer.accept(FinishedRecipe.alchemy(id("ender_pearl_from_ghast_tear"), new ItemStack(Items.ENDER_PEARL), ingr(Items.GHAST_TEAR), 28000));

		cycle(consumer, 300, "botania:glowstone_and_redstone_cycle", Items.GLOWSTONE_DUST, Items.REDSTONE);

		consumer.accept(FinishedRecipe.alchemy(id("cobble_to_sand"), new ItemStack(Blocks.SAND), ingr(Blocks.COBBLESTONE), 50));
		consumer.accept(FinishedRecipe.alchemy(id("terracotta_to_red_sand"), new ItemStack(Blocks.RED_SAND), ingr(Blocks.TERRACOTTA), 50));

		consumer.accept(deconstruct("clay_deconstruct", Items.CLAY_BALL, Blocks.CLAY));
		consumer.accept(deconstruct("brick_deconstruct", Items.BRICK, Blocks.BRICKS));

		consumer.accept(FinishedRecipe.alchemy(id("coarse_dirt"), new ItemStack(Blocks.COARSE_DIRT), ingr(Blocks.DIRT), 120));
		consumer.accept(FinishedRecipe.alchemy(id("soul_soil"), new ItemStack(Blocks.SOUL_SOIL), ingr(Blocks.SOUL_SAND), 120));

		consumer.accept(FinishedRecipe.alchemy(id("stone_to_andesite"), new ItemStack(Blocks.ANDESITE), ingr(Blocks.STONE), 200));
		cycle(consumer, 200, "botania:stone_cycle", Blocks.DIORITE, Blocks.GRANITE, Blocks.ANDESITE);

		cycle(consumer, 200, "botania:117_stone_cycle", Blocks.TUFF, Blocks.CALCITE, Blocks.DEEPSLATE);

		cycle(consumer, 500, "botania:shrub_cycle", Blocks.FERN, Blocks.DEAD_BUSH, Blocks.GRASS);

		// NB: No wither rose is intentional
		cycle(consumer, 400, "botania:flower_cycle", Blocks.DANDELION, Blocks.POPPY, Blocks.BLUE_ORCHID, Blocks.ALLIUM, Blocks.AZURE_BLUET, Blocks.RED_TULIP, Blocks.ORANGE_TULIP,
				Blocks.WHITE_TULIP, Blocks.PINK_TULIP, Blocks.OXEYE_DAISY, Blocks.CORNFLOWER, Blocks.LILY_OF_THE_VALLEY,
				Blocks.SUNFLOWER, Blocks.LILAC, Blocks.ROSE_BUSH, Blocks.PEONY);

		consumer.accept(FinishedRecipe.alchemy(id("dripleaf_shrinking"), new ItemStack(Blocks.SMALL_DRIPLEAF), ingr(Items.BIG_DRIPLEAF), 500));
		consumer.accept(FinishedRecipe.alchemy(id("chorus_fruit_to_flower"), new ItemStack(Blocks.CHORUS_FLOWER), ingr(Items.POPPED_CHORUS_FRUIT), 10000));

		cycle(consumer, 240, "botania:berry_cycle", Items.APPLE, Items.SWEET_BERRIES, Items.GLOW_BERRIES);

		consumer.accept(mini(BotaniaFlowerBlocks.agricarnationChibi, BotaniaFlowerBlocks.agricarnation));
		consumer.accept(mini(BotaniaFlowerBlocks.clayconiaChibi, BotaniaFlowerBlocks.clayconia));
		consumer.accept(mini(BotaniaFlowerBlocks.bellethornChibi, BotaniaFlowerBlocks.bellethorn));
		consumer.accept(mini(BotaniaFlowerBlocks.bubbellChibi, BotaniaFlowerBlocks.bubbell));
		consumer.accept(mini(BotaniaFlowerBlocks.hopperhockChibi, BotaniaFlowerBlocks.hopperhock));
		consumer.accept(mini(BotaniaFlowerBlocks.jiyuuliaChibi, BotaniaFlowerBlocks.jiyuulia));
		consumer.accept(mini(BotaniaFlowerBlocks.tangleberrieChibi, BotaniaFlowerBlocks.tangleberrie));
		consumer.accept(mini(BotaniaFlowerBlocks.marimorphosisChibi, BotaniaFlowerBlocks.marimorphosis));
		consumer.accept(mini(BotaniaFlowerBlocks.rannuncarpusChibi, BotaniaFlowerBlocks.rannuncarpus));
		consumer.accept(mini(BotaniaFlowerBlocks.solegnoliaChibi, BotaniaFlowerBlocks.solegnolia));

		consumer.accept(FinishedRecipe.alchemy(id("hydroangeas_motif"), new ItemStack(BotaniaBlocks.motifHydroangeas), ingr(BotaniaFlowerBlocks.hydroangeas), 2500));

		consumer.accept(FinishedRecipe.conjuration(id("redstone_dupe"), new ItemStack(Items.REDSTONE, 2), ingr(Items.REDSTONE), 5000));
		consumer.accept(FinishedRecipe.conjuration(id("glowstone_dupe"), new ItemStack(Items.GLOWSTONE_DUST, 2), ingr(Items.GLOWSTONE_DUST), 5000));
		consumer.accept(FinishedRecipe.conjuration(id("quartz_dupe"), new ItemStack(Items.QUARTZ, 2), ingr(Items.QUARTZ), 2500));
		consumer.accept(FinishedRecipe.conjuration(id("coal_dupe"), new ItemStack(Items.COAL, 2), ingr(Items.COAL), 2100));
		consumer.accept(FinishedRecipe.conjuration(id("snowball_dupe"), new ItemStack(Items.SNOWBALL, 2), ingr(Items.SNOWBALL), 200));
		consumer.accept(FinishedRecipe.conjuration(id("netherrack_dupe"), new ItemStack(Blocks.NETHERRACK, 2), ingr(Blocks.NETHERRACK), 200));
		consumer.accept(FinishedRecipe.conjuration(id("soul_sand_dupe"), new ItemStack(Blocks.SOUL_SAND, 2), ingr(Blocks.SOUL_SAND), 1500));
		consumer.accept(FinishedRecipe.conjuration(id("gravel_dupe"), new ItemStack(Blocks.GRAVEL, 2), ingr(Blocks.GRAVEL), 720));

		consumer.accept(FinishedRecipe.conjuration(id("oak_leaves_dupe"), new ItemStack(Blocks.OAK_LEAVES, 2), ingr(Blocks.OAK_LEAVES), 2000));
		consumer.accept(FinishedRecipe.conjuration(id("birch_leaves_dupe"), new ItemStack(Blocks.BIRCH_LEAVES, 2), ingr(Blocks.BIRCH_LEAVES), 2000));
		consumer.accept(FinishedRecipe.conjuration(id("spruce_leaves_dupe"), new ItemStack(Blocks.SPRUCE_LEAVES, 2), ingr(Blocks.SPRUCE_LEAVES), 2000));
		consumer.accept(FinishedRecipe.conjuration(id("jungle_leaves_dupe"), new ItemStack(Blocks.JUNGLE_LEAVES, 2), ingr(Blocks.JUNGLE_LEAVES), 2000));
		consumer.accept(FinishedRecipe.conjuration(id("acacia_leaves_dupe"), new ItemStack(Blocks.ACACIA_LEAVES, 2), ingr(Blocks.ACACIA_LEAVES), 2000));
		consumer.accept(FinishedRecipe.conjuration(id("dark_oak_leaves_dupe"), new ItemStack(Blocks.DARK_OAK_LEAVES, 2), ingr(Blocks.DARK_OAK_LEAVES), 2000));
		consumer.accept(FinishedRecipe.conjuration(id("azalea_leaves_dupe"), new ItemStack(Blocks.AZALEA_LEAVES, 2), ingr(Blocks.AZALEA_LEAVES), 2000));
		consumer.accept(FinishedRecipe.conjuration(id("flowering_azalea_leaves_dupe"), new ItemStack(Blocks.FLOWERING_AZALEA_LEAVES, 2), ingr(Blocks.FLOWERING_AZALEA_LEAVES), 2000));
		consumer.accept(FinishedRecipe.conjuration(id("mangrove_leaves_dupe"), new ItemStack(Blocks.MANGROVE_LEAVES, 2), ingr(Blocks.MANGROVE_LEAVES), 2000));
		consumer.accept(FinishedRecipe.conjuration(id("cherry_leaves_dupe"), new ItemStack(Blocks.CHERRY_LEAVES, 2), ingr(Blocks.CHERRY_LEAVES), 2000));

		consumer.accept(FinishedRecipe.conjuration(id("grass"), new ItemStack(Blocks.GRASS, 2), ingr(Blocks.GRASS), 800));
	}

	protected void cycle(Consumer<net.minecraft.data.recipes.FinishedRecipe> consumer, int cost, String group, ItemLike... items) {
		for (int i = 0; i < items.length; i++) {
			Ingredient in = ingr(items[i]);
			ItemStack out = new ItemStack(i == items.length - 1 ? items[0] : items[i + 1]);
			String id = String.format("%s_to_%s", BuiltInRegistries.ITEM.getKey(items[i].asItem()).getPath(), BuiltInRegistries.ITEM.getKey(out.getItem()).getPath());
			consumer.accept(FinishedRecipe.alchemy(id(id), out, in, cost, group));
		}
	}

	protected FinishedRecipe mini(ItemLike mini, ItemLike full) {
		return FinishedRecipe.alchemy(id(BuiltInRegistries.ITEM.getKey(mini.asItem()).getPath()), new ItemStack(mini), ingr(full), 2500, "botania:flower_shrinking");
	}

	protected FinishedRecipe deconstruct(String id, ItemLike items, ItemLike block) {
		return FinishedRecipe.alchemy(id(id), new ItemStack(items, 4), ingr(block), 25, "botania:block_deconstruction");
	}

	protected ResourceLocation id(String s) {
		return prefix("mana_infusion/" + s);
	}

	protected static Ingredient ingr(ItemLike i) {
		return Ingredient.of(i);
	}

	protected static class FinishedRecipe implements net.minecraft.data.recipes.FinishedRecipe {
		private static final StateIngredient CONJURATION = StateIngredientHelper.of(BotaniaBlocks.conjurationCatalyst);
		private static final StateIngredient ALCHEMY = StateIngredientHelper.of(BotaniaBlocks.alchemyCatalyst);

		private final ResourceLocation id;
		private final Ingredient input;
		private final ItemStack output;
		private final int mana;
		private final String group;
		@Nullable
		private final StateIngredient catalyst;

		public static FinishedRecipe conjuration(ResourceLocation id, ItemStack output, Ingredient input, int mana) {
			return new FinishedRecipe(id, output, input, mana, "", CONJURATION);
		}

		public static FinishedRecipe alchemy(ResourceLocation id, ItemStack output, Ingredient input, int mana) {
			return alchemy(id, output, input, mana, "");
		}

		public static FinishedRecipe alchemy(ResourceLocation id, ItemStack output, Ingredient input, int mana, String group) {
			return new FinishedRecipe(id, output, input, mana, group, ALCHEMY);
		}

		public FinishedRecipe(ResourceLocation id, ItemStack output, Ingredient input, int mana) {
			this(id, output, input, mana, "");
		}

		public FinishedRecipe(ResourceLocation id, ItemStack output, Ingredient input, int mana, String group) {
			this(id, output, input, mana, group, null);
		}

		public FinishedRecipe(ResourceLocation id, ItemStack output, Ingredient input, int mana, String group, @Nullable StateIngredient catalyst) {
			this.id = id;
			this.input = input;
			this.output = output;
			this.mana = mana;
			this.group = group;
			this.catalyst = catalyst;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			json.add("input", input.toJson());
			json.add("output", ItemNBTHelper.serializeStack(output));
			json.addProperty("mana", mana);
			if (!group.isEmpty()) {
				json.addProperty("group", group);
			}
			if (catalyst != null) {
				json.add("catalyst", catalyst.serialize());
			}
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return BotaniaRecipeTypes.MANA_INFUSION_SERIALIZER;
		}

		@Nullable
		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}
}
