/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IRecipesGui;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;

import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.recipe.*;
import vazkii.botania.client.core.handler.CorporeaInputHandler;
import vazkii.botania.client.gui.crafting.AssemblyHaloContainer;
import vazkii.botania.client.integration.jei.crafting.AncientWillRecipeWrapper;
import vazkii.botania.client.integration.jei.crafting.CompositeLensRecipeWrapper;
import vazkii.botania.client.integration.jei.crafting.TerraShattererTippingRecipeWrapper;
import vazkii.botania.client.integration.jei.orechid.MarimorphosisRecipeCategory;
import vazkii.botania.client.integration.jei.orechid.OrechidIgnemRecipeCategory;
import vazkii.botania.client.integration.jei.orechid.OrechidRecipeCategory;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.block.block_entity.AlfheimPortalBlockEntity;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.botania.common.crafting.LexiconElvenTradeRecipe;
import vazkii.botania.common.crafting.StateIngredients;
import vazkii.botania.common.crafting.recipe.AncientWillRecipe;
import vazkii.botania.common.crafting.recipe.CompositeLensRecipe;
import vazkii.botania.common.crafting.recipe.TerraShattererTippingRecipe;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.LaputaShardItem;
import vazkii.botania.common.item.LexicaBotaniaItem;
import vazkii.botania.common.item.ManaTabletItem;
import vazkii.botania.common.item.brew.BaseBrewItem;
import vazkii.botania.common.item.equipment.bauble.FlugelTiaraItem;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;
import vazkii.botania.xplat.XplatAbstractions;

import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

//TODO unsurpress
@SuppressWarnings("removal")
@JeiPlugin
public class JEIBotaniaPlugin implements IModPlugin {
	private static final ResourceLocation ID = botaniaRL("main");

	@Override
	public void registerItemSubtypes(@NotNull ISubtypeRegistration registry) {
		IIngredientSubtypeInterpreter<ItemStack> interpreter = (stack, ctx) -> BaseBrewItem.getSubtype(stack); //TODO use ISubtypeInterpreter instead
		registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, BotaniaItems.brewVial, interpreter);
		registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, BotaniaItems.brewFlask, interpreter);
		registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, BotaniaItems.incenseStick, interpreter);
		registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, BotaniaItems.bloodPendant, interpreter);

		registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, BotaniaItems.flightTiara, (stack, ctx) -> String.valueOf(FlugelTiaraItem.getVariant(stack)));
		//registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, BotaniaItems.lexicon, (stack, ctx) -> String.valueOf(ItemNBTHelper.getBoolean(stack, LexicaBotaniaItem.TAG_ELVEN_UNLOCK, false)));
		registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, BotaniaItems.laputaShard, (stack, ctx) -> String.valueOf(LaputaShardItem.getShardLevel(stack)));

		registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, BotaniaItems.terraPick, (stack, ctx) -> {
			if (ctx == UidContext.Recipe) {
				return String.valueOf(TerraShattererItem.isTipped(stack));
			}
			return String.valueOf(TerraShattererItem.getLevel(stack)) + TerraShattererItem.isTipped(stack);
		});
		registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, BotaniaItems.manaTablet, (stack, ctx) -> {
			int mana = XplatAbstractions.INSTANCE.findManaItem(stack).getMana();
			return String.valueOf(mana) + ManaTabletItem.isStackCreative(stack);
		});

		for (Item item : new Item[] { BotaniaItems.manaRing, BotaniaItems.manaRingGreater }) {
			registry.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, item, (stack, ctx) -> {
				int mana = XplatAbstractions.INSTANCE.findManaItem(stack).getMana();
				return String.valueOf(mana);
			});
		}
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(
				new PureDaisyRecipeCategory(registry.getJeiHelpers().getGuiHelper(), registry.getJeiHelpers().getPlatformFluidHelper()),
				new ManaPoolRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
				new PetalApothecaryRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
				new RunicAltarRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
				new ElvenTradeRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
				new BreweryRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
				new OrechidRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
				new OrechidIgnemRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
				new MarimorphosisRecipeCategory(registry.getJeiHelpers().getGuiHelper()),
				new TerrestrialAgglomerationRecipeCategory(registry.getJeiHelpers().getGuiHelper())
		);
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		registration.getCraftingCategory().addExtension(AncientWillRecipe.class, new AncientWillRecipeWrapper());
		registration.getCraftingCategory().addExtension(TerraShattererTippingRecipe.class, new TerraShattererTippingRecipeWrapper());
		registration.getCraftingCategory().addExtension(CompositeLensRecipe.class, new CompositeLensRecipeWrapper());
	}

	@Override
	public void registerRecipes(@NotNull IRecipeRegistration registry) {
		registry.addRecipes(BreweryRecipeCategory.TYPE, sortRecipes(BotaniaRecipeTypes.BREW_TYPE, BY_ID));
		registry.addRecipes(PureDaisyRecipeCategory.TYPE, sortRecipes(BotaniaRecipeTypes.PURE_DAISY_TYPE, BY_ID));
		registry.addRecipes(PetalApothecaryRecipeCategory.TYPE, sortRecipes(BotaniaRecipeTypes.PETAL_TYPE, BY_ID));
		registry.addRecipes(ElvenTradeRecipeCategory.TYPE, sortRecipes(BotaniaRecipeTypes.ELVEN_TRADE_TYPE, BY_ID));
		registry.addRecipes(RunicAltarRecipeCategory.TYPE, sortRecipes(BotaniaRecipeTypes.RUNE_TYPE, BY_ID));
		registry.addRecipes(ManaPoolRecipeCategory.TYPE, sortRecipes(BotaniaRecipeTypes.MANA_INFUSION_TYPE, BY_CATALYST.thenComparing(BY_GROUP).thenComparing(BY_ID)));
		registry.addRecipes(TerrestrialAgglomerationRecipeCategory.TYPE, sortRecipes(BotaniaRecipeTypes.TERRA_PLATE_TYPE, BY_ID));

		Comparator<RecipeHolder<? extends OrechidRecipe>> comp = BY_WEIGHT.thenComparing(BY_ID);
		registry.addRecipes(OrechidRecipeCategory.TYPE, sortRecipes(BotaniaRecipeTypes.ORECHID_TYPE, comp));
		registry.addRecipes(OrechidIgnemRecipeCategory.TYPE, sortRecipes(BotaniaRecipeTypes.ORECHID_IGNEM_TYPE, comp));
		registry.addRecipes(MarimorphosisRecipeCategory.TYPE, sortRecipes(BotaniaRecipeTypes.MARIMORPHOSIS_TYPE, comp));
	}

	private static final Comparator<RecipeHolder<? extends Recipe<?>>> BY_ID = Comparator.comparing(RecipeHolder::id);
	private static final Comparator<RecipeHolder<? extends Recipe<?>>> BY_GROUP = Comparator.comparing(holder -> holder.value().getGroup());
	private static final Comparator<RecipeHolder<? extends OrechidRecipe>> BY_WEIGHT =
			Comparator.<RecipeHolder<? extends OrechidRecipe>, Integer>comparing(holder -> holder.value().getWeight()).reversed();
	private static final Comparator<RecipeHolder<ManaInfusionRecipe>> BY_CATALYST = (l, r) -> {
		StateIngredient left = l.value().getRecipeCatalyst();
		StateIngredient right = r.value().getRecipeCatalyst();
		if (left == StateIngredients.NONE) {
			return right == StateIngredients.NONE ? 0 : -1;
		} else if (right == StateIngredients.NONE) {
			return 1;
		} else {
			return left.streamBlockStates().map(Object::toString).findFirst().orElse("")
					.compareTo(right.streamBlockStates().map(Object::toString).findFirst().orElse(""));
		}
	};

	private static <T extends Recipe<C>, C extends RecipeInput> List<T> sortRecipes(RecipeType<T> type, Comparator<? super RecipeHolder<T>> comparator) {
		return Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(type)
				.stream().sorted(comparator).map(RecipeHolder::value).toList();
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registry) {
		registry.addRecipeTransferHandler(AssemblyHaloContainer.class, null, RecipeTypes.CRAFTING, 1, 9, 10, 36);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registry) {
		registry.addRecipeCatalyst(new ItemStack(BotaniaBlocks.brewery), BreweryRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(BotaniaBlocks.alfPortal), ElvenTradeRecipeCategory.TYPE);

		registry.addRecipeCatalyst(new ItemStack(BotaniaBlocks.manaPool), ManaPoolRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(BotaniaBlocks.dilutedPool), ManaPoolRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(BotaniaBlocks.fabulousPool), ManaPoolRecipeCategory.TYPE);

		for (Block apothecary : BotaniaBlocks.ALL_APOTHECARIES) {
			registry.addRecipeCatalyst(new ItemStack(apothecary), PetalApothecaryRecipeCategory.TYPE);
		}

		registry.addRecipeCatalyst(new ItemStack(BotaniaFlowerBlocks.orechid), OrechidRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(BotaniaFlowerBlocks.orechidFloating), OrechidRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(BotaniaFlowerBlocks.orechidIgnem), OrechidIgnemRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(BotaniaFlowerBlocks.orechidIgnemFloating), OrechidIgnemRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(BotaniaFlowerBlocks.marimorphosis), MarimorphosisRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(BotaniaFlowerBlocks.marimorphosisChibi), MarimorphosisRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(BotaniaFlowerBlocks.marimorphosisFloating), MarimorphosisRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(BotaniaFlowerBlocks.marimorphosisChibiFloating), MarimorphosisRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(BotaniaFlowerBlocks.pureDaisy), PureDaisyRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(BotaniaFlowerBlocks.pureDaisyFloating), PureDaisyRecipeCategory.TYPE);

		registry.addRecipeCatalyst(new ItemStack(BotaniaBlocks.runeAltar), RunicAltarRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(BotaniaBlocks.terraPlate), TerrestrialAgglomerationRecipeCategory.TYPE);
		registry.addRecipeCatalyst(new ItemStack(BotaniaItems.autocraftingHalo), RecipeTypes.CRAFTING);
		registry.addRecipeCatalyst(new ItemStack(BotaniaItems.craftingHalo), RecipeTypes.CRAFTING);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		IRecipeManager recipeRegistry = jeiRuntime.getRecipeManager();
		// Hide the return recipes (iron ingot/diamond/ender pearl returns, not lexicon)
		for (RecipeHolder<ElvenTradeRecipe> recipe : AlfheimPortalBlockEntity.elvenTradeRecipes(Minecraft.getInstance().level)) {
			if (recipe.value() instanceof LexiconElvenTradeRecipe) {
				continue;
			}
			List<Ingredient> inputs = recipe.value().getIngredients();
			List<ItemStack> outputs = recipe.value().getOutputs();
			if (inputs.size() == 1 && outputs.size() == 1 && recipe.value().containsItem(outputs.getFirst())) {
				recipeRegistry.hideRecipes(ElvenTradeRecipeCategory.TYPE, List.of(recipe.value()));
			}
		}

		RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
		recipeManager.byKey(botaniaRL("petal_apothecary/daybloom_motif"))
				.ifPresent(r -> {
					if (r.value() instanceof PetalApothecaryRecipe pr) {
						recipeRegistry.hideRecipes(PetalApothecaryRecipeCategory.TYPE, List.of(pr));
					}
				});
		recipeManager.byKey(botaniaRL("petal_apothecary/nightshade_motif"))
				.ifPresent(r -> {
					if (r.value() instanceof PetalApothecaryRecipe pr) {
						recipeRegistry.hideRecipes(PetalApothecaryRecipeCategory.TYPE, List.of(pr));
					}
				});

		HOVERED_STACK_GETTER.jeiRuntimeRef = new WeakReference<>(jeiRuntime);
		if (!CorporeaInputHandler.hoveredStackGetters.contains(HOVERED_STACK_GETTER)) {
			CorporeaInputHandler.hoveredStackGetters.add(HOVERED_STACK_GETTER);
		}
		CorporeaInputHandler.supportedGuiFilter = CorporeaInputHandler.supportedGuiFilter.or(gui -> gui instanceof IRecipesGui);
	}

	@NotNull
	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	private static final HoveredStackGetter HOVERED_STACK_GETTER = new HoveredStackGetter();

	private static class HoveredStackGetter implements Supplier<ItemStack> {
		// Technically, this doesn't have to be WeakReference. The old one will be released and collected
		// when the new one is assigned. But we don't to keep hanging onto the memory if someone quits out
		// to the menu and then idles there, so use a WeakReference here.
		WeakReference<IJeiRuntime> jeiRuntimeRef;

		@Override
		public ItemStack get() {
			var jeiRuntime = jeiRuntimeRef.get();
			if (jeiRuntime == null) {
				return ItemStack.EMPTY;
			}
			return ObjectUtils.getFirstNonNull(
					() -> jeiRuntime.getIngredientListOverlay().getIngredientUnderMouse(VanillaTypes.ITEM_STACK),
					() -> jeiRuntime.getRecipesGui().getIngredientUnderMouse(VanillaTypes.ITEM_STACK).orElse(null),
					() -> jeiRuntime.getBookmarkOverlay().getIngredientUnderMouse(VanillaTypes.ITEM_STACK),
					() -> ItemStack.EMPTY
			);
		}
	}
}
