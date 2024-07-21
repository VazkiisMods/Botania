/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import vazkii.botania.common.crafting.recipe.HeadRecipe;
import vazkii.botania.mixin.RecipeManagerAccessor;

import java.util.Map;
import java.util.function.BiConsumer;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class BotaniaRecipeTypes {
	public static final RecipeType<vazkii.botania.api.recipe.ManaInfusionRecipe> MANA_INFUSION_TYPE = new ModRecipeType<>();
	public static final RecipeSerializer<ManaInfusionRecipe> MANA_INFUSION_SERIALIZER = new ManaInfusionRecipe.Serializer();

	public static final RecipeType<vazkii.botania.api.recipe.ElvenTradeRecipe> ELVEN_TRADE_TYPE = new ModRecipeType<>();
	public static final RecipeSerializer<ElvenTradeRecipe> ELVEN_TRADE_SERIALIZER = new ElvenTradeRecipe.Serializer();
	public static final RecipeSerializer<LexiconElvenTradeRecipe> LEXICON_ELVEN_TRADE_SERIALIZER = new LexiconElvenTradeRecipe.Serializer();

	public static final RecipeType<vazkii.botania.api.recipe.PureDaisyRecipe> PURE_DAISY_TYPE = new ModRecipeType<>();
	public static final RecipeSerializer<PureDaisyRecipe> PURE_DAISY_SERIALIZER = new PureDaisyRecipe.Serializer();

	public static final RecipeType<vazkii.botania.api.recipe.BotanicalBreweryRecipe> BREW_TYPE = new ModRecipeType<>();
	public static final RecipeSerializer<BotanicalBreweryRecipe> BREW_SERIALIZER = new BotanicalBreweryRecipe.Serializer();

	public static final RecipeType<vazkii.botania.api.recipe.PetalApothecaryRecipe> PETAL_TYPE = new ModRecipeType<>();
	public static final RecipeSerializer<PetalApothecaryRecipe> PETAL_SERIALIZER = new PetalApothecaryRecipe.Serializer();

	public static final RecipeType<vazkii.botania.api.recipe.RunicAltarRecipe> RUNE_TYPE = new ModRecipeType<>();
	public static final RecipeSerializer<RunicAltarRecipe> RUNE_SERIALIZER = new RunicAltarRecipe.Serializer();
	public static final RecipeSerializer<HeadRecipe> RUNE_HEAD_SERIALIZER = new HeadRecipe.Serializer();

	public static final RecipeType<vazkii.botania.api.recipe.TerrestrialAgglomerationRecipe> TERRA_PLATE_TYPE = new ModRecipeType<>();
	public static final RecipeSerializer<TerrestrialAgglomerationRecipe> TERRA_PLATE_SERIALIZER = new TerrestrialAgglomerationRecipe.Serializer();

	public static final RecipeType<OrechidRecipe> ORECHID_TYPE = new ModRecipeType<>();
	public static final RecipeSerializer<OrechidRecipe> ORECHID_SERIALIZER = new OrechidRecipe.Serializer();

	public static final RecipeType<OrechidIgnemRecipe> ORECHID_IGNEM_TYPE = new ModRecipeType<>();
	public static final RecipeSerializer<OrechidIgnemRecipe> ORECHID_IGNEM_SERIALIZER = new OrechidIgnemRecipe.Serializer();

	public static final RecipeType<MarimorphosisRecipe> MARIMORPHOSIS_TYPE = new ModRecipeType<>();
	public static final RecipeSerializer<MarimorphosisRecipe> MARIMORPHOSIS_SERIALIZER = new MarimorphosisRecipe.Serializer();

	public static void submitRecipeTypes(BiConsumer<RecipeType<?>, ResourceLocation> r) {
		r.accept(ELVEN_TRADE_TYPE, vazkii.botania.api.recipe.ElvenTradeRecipe.TYPE_ID);
		r.accept(MANA_INFUSION_TYPE, vazkii.botania.api.recipe.ManaInfusionRecipe.TYPE_ID);
		r.accept(PURE_DAISY_TYPE, vazkii.botania.api.recipe.PureDaisyRecipe.TYPE_ID);
		r.accept(BREW_TYPE, vazkii.botania.api.recipe.BotanicalBreweryRecipe.TYPE_ID);
		r.accept(PETAL_TYPE, vazkii.botania.api.recipe.PetalApothecaryRecipe.TYPE_ID);
		r.accept(RUNE_TYPE, vazkii.botania.api.recipe.RunicAltarRecipe.TYPE_ID);
		r.accept(TERRA_PLATE_TYPE, vazkii.botania.api.recipe.TerrestrialAgglomerationRecipe.TYPE_ID);
		r.accept(ORECHID_TYPE, vazkii.botania.api.recipe.OrechidRecipe.TYPE_ID);
		r.accept(ORECHID_IGNEM_TYPE, vazkii.botania.api.recipe.OrechidRecipe.IGNEM_TYPE_ID);
		r.accept(MARIMORPHOSIS_TYPE, vazkii.botania.api.recipe.OrechidRecipe.MARIMORPHOSIS_TYPE_ID);
	}

	public static void submitRecipeSerializers(BiConsumer<RecipeSerializer<?>, ResourceLocation> r) {
		r.accept(ELVEN_TRADE_SERIALIZER, vazkii.botania.api.recipe.ElvenTradeRecipe.TYPE_ID);
		r.accept(LEXICON_ELVEN_TRADE_SERIALIZER, vazkii.botania.api.recipe.ElvenTradeRecipe.TYPE_ID_LEXICON);
		r.accept(MANA_INFUSION_SERIALIZER, vazkii.botania.api.recipe.ManaInfusionRecipe.TYPE_ID);
		r.accept(PURE_DAISY_SERIALIZER, vazkii.botania.api.recipe.PureDaisyRecipe.TYPE_ID);
		r.accept(BREW_SERIALIZER, vazkii.botania.api.recipe.BotanicalBreweryRecipe.TYPE_ID);
		r.accept(PETAL_SERIALIZER, vazkii.botania.api.recipe.PetalApothecaryRecipe.TYPE_ID);
		r.accept(RUNE_SERIALIZER, vazkii.botania.api.recipe.RunicAltarRecipe.TYPE_ID);
		r.accept(RUNE_HEAD_SERIALIZER, prefix("runic_altar_head"));
		r.accept(TERRA_PLATE_SERIALIZER, vazkii.botania.api.recipe.TerrestrialAgglomerationRecipe.TYPE_ID);
		r.accept(ORECHID_SERIALIZER, vazkii.botania.api.recipe.OrechidRecipe.TYPE_ID);
		r.accept(ORECHID_IGNEM_SERIALIZER, vazkii.botania.api.recipe.OrechidRecipe.IGNEM_TYPE_ID);
		r.accept(MARIMORPHOSIS_SERIALIZER, vazkii.botania.api.recipe.OrechidRecipe.MARIMORPHOSIS_TYPE_ID);
	}

	private static class ModRecipeType<T extends Recipe<?>> implements RecipeType<T> {
		@Override
		public String toString() {
			return BuiltInRegistries.RECIPE_TYPE.getKey(this).toString();
		}
	}

	public static <C extends Container, T extends Recipe<C>> Map<ResourceLocation, T> getRecipes(Level world, RecipeType<T> type) {
		return ((RecipeManagerAccessor) world.getRecipeManager()).botania_getAll(type);
	}
}
