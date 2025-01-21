/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data.recipes;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.crafting.ElvenTradeRecipe;
import vazkii.botania.common.crafting.LexiconElvenTradeRecipe;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.lib.BotaniaTags;

import java.util.concurrent.CompletableFuture;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class ElvenTradeProvider extends BotaniaRecipeProvider {
	public ElvenTradeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider);
	}

	@Override
	public void buildRecipes(RecipeOutput consumer) {
		consumer.accept(id("dreamwood_log"), new ElvenTradeRecipe(singleOutput(BotaniaBlocks.dreamwoodLog), Ingredient.of(BotaniaBlocks.livingwoodLog)), null);
		consumer.accept(id("dreamwood"), new ElvenTradeRecipe(singleOutput(BotaniaBlocks.dreamwood), Ingredient.of(BotaniaBlocks.livingwood)), null);

		Ingredient manaSteel = Ingredient.of(BotaniaTags.Items.INGOTS_MANASTEEL);
		Ingredient manaSteelBlock = Ingredient.of(BotaniaBlocks.manasteelBlock);
		consumer.accept(id("elementium"), new ElvenTradeRecipe(singleOutput(BotaniaItems.elementium), manaSteel, manaSteel), null);
		consumer.accept(id("elementium_block"), new ElvenTradeRecipe(singleOutput(BotaniaBlocks.elementiumBlock), manaSteelBlock, manaSteelBlock), null);

		consumer.accept(id("pixie_dust"), new ElvenTradeRecipe(singleOutput(BotaniaItems.pixieDust), Ingredient.of(BotaniaItems.manaPearl)), null);
		consumer.accept(id("dragonstone"), new ElvenTradeRecipe(singleOutput(BotaniaItems.dragonstone), Ingredient.of(BotaniaItems.manaDiamond)), null);
		consumer.accept(id("dragonstone_block"), new ElvenTradeRecipe(singleOutput(BotaniaBlocks.dragonstoneBlock), Ingredient.of(BotaniaBlocks.manaDiamondBlock)), null);

		consumer.accept(id("elf_quartz"), new ElvenTradeRecipe(singleOutput(BotaniaItems.elfQuartz), Ingredient.of(Items.QUARTZ)), null);
		consumer.accept(id("elf_glass"), new ElvenTradeRecipe(singleOutput(BotaniaBlocks.elfGlass), Ingredient.of(BotaniaBlocks.manaGlass)), null);

		consumer.accept(id("iron_return"), new ElvenTradeRecipe(singleOutput(Items.IRON_INGOT), Ingredient.of(Items.IRON_INGOT)), null);
		consumer.accept(id("iron_block_return"), new ElvenTradeRecipe(singleOutput(Blocks.IRON_BLOCK), Ingredient.of(Blocks.IRON_BLOCK)), null);
		consumer.accept(id("ender_pearl_return"), new ElvenTradeRecipe(singleOutput(Items.ENDER_PEARL), Ingredient.of(Items.ENDER_PEARL)), null);
		consumer.accept(id("diamond_return"), new ElvenTradeRecipe(singleOutput(Items.DIAMOND), Ingredient.of(Items.DIAMOND)), null);
		consumer.accept(id("diamond_block_return"), new ElvenTradeRecipe(singleOutput(Blocks.DIAMOND_BLOCK), Ingredient.of(Blocks.DIAMOND_BLOCK)), null);

		consumer.accept(id("lexicon_elven"), new LexiconElvenTradeRecipe(), null);
	}

	private static ItemStack[] singleOutput(ItemLike output) {
		return new ItemStack[] { new ItemStack(output) };
	}

	private static ResourceLocation id(String path) {
		return botaniaRL("elven_trade/" + path);
	}

	@Override
	public String getName() {
		return "Botania elven trade recipes";
	}
}
