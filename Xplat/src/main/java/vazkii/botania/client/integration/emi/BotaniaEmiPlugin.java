package vazkii.botania.client.integration.emi;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import org.apache.commons.lang3.StringUtils;

import vazkii.botania.client.core.handler.CorporeaInputHandler;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.crafting.*;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;
import vazkii.botania.common.item.lens.LensItem;
import vazkii.botania.common.lib.BotaniaTags;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

@EmiEntrypoint
public class BotaniaEmiPlugin implements EmiPlugin {
	private static final Comparator<EmiRecipe> BY_ID = Comparator.comparing(EmiRecipe::getId);
	private static final Comparator<EmiRecipe> BY_GROUP =
			Comparator.comparing(emiRecipe -> emiRecipe instanceof BotaniaEmiRecipe ber ? ber.getGroup() : "");
	private static final Comparator<EmiRecipe> BY_CATALYST =
			Comparator.comparing(emiRecipe -> emiRecipe.getCatalysts()
					.stream()
					.flatMap(emiIngredient -> emiIngredient.getEmiStacks().stream())
					.map(emiStack -> emiStack.getId().toString())
					.filter(StringUtils::isNotEmpty)
					.findFirst()
					.orElse(""));
	private static final Comparator<EmiRecipe> BY_WEIGHT = Comparator.<EmiRecipe, Integer>comparing(
			emiRecipe -> emiRecipe instanceof OrechidEmiRecipe orechidEmiRecipe ? orechidEmiRecipe.getWeight() : 0).reversed();
	private static final Comparator<EmiRecipe> ORECHID_COMPARATOR = BY_WEIGHT.thenComparing(BY_ID);

	public static final EmiRecipeCategory PETAL_APOTHECARY = createCategory("petal_apothecary",
			EmiStack.of(BotaniaBlocks.defaultAltar), BY_ID);
	public static final EmiRecipeCategory MANA_INFUSION = createCategory("mana_infusion",
			EmiStack.of(BotaniaBlocks.manaPool), BY_CATALYST.thenComparing(BY_GROUP).thenComparing(BY_ID));
	public static final EmiRecipeCategory RUNIC_ALTAR = createCategory("runic_altar",
			EmiStack.of(BotaniaBlocks.runeAltar), BY_ID);
	public static final EmiRecipeCategory TERRESTRIAL_AGGLOMERATION = createCategory("terrestrial_agglomeration",
			EmiStack.of(BotaniaBlocks.terraPlate), BY_ID);
	public static final EmiRecipeCategory ELVEN_TRADE = createCategory("elven_trade",
			EmiStack.of(BotaniaBlocks.alfPortal), BY_ID);
	public static final EmiRecipeCategory BOTANICAL_BREWERY = createCategory("botanical_brewery",
			EmiStack.of(BotaniaBlocks.brewery), BY_ID);
	public static final EmiRecipeCategory PURE_DAISY = createCategory("pure_daisy",
			EmiStack.of(BotaniaFlowerBlocks.pureDaisy), BY_ID);
	public static final EmiRecipeCategory ORECHID = createCategory("orechid",
			EmiStack.of(BotaniaFlowerBlocks.orechid), ORECHID_COMPARATOR);
	public static final EmiRecipeCategory ORECHID_IGNEM = createCategory("orechid_ignem",
			EmiStack.of(BotaniaFlowerBlocks.orechidIgnem), ORECHID_COMPARATOR);
	public static final EmiRecipeCategory MARIMORPHOSIS = createCategory("marimorphosis",
			EmiStack.of(BotaniaFlowerBlocks.marimorphosis), ORECHID_COMPARATOR);

	private static EmiRecipeCategory createCategory(String idPath, EmiRenderable icon, Comparator<EmiRecipe> comp) {
		return new EmiRecipeCategory(prefix(idPath), icon, icon, comp);
	}

	private static final Supplier<ItemStack> HOVERED_STACK_GETTER = () -> {
		EmiIngredient ingr = EmiApi.getHoveredStack(true).getStack();
		if (!ingr.getEmiStacks().isEmpty()) {
			var stack = ingr.getEmiStacks().get(0).getItemStack();
			if (!stack.isEmpty()) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	};

	public BotaniaEmiPlugin() {
		CorporeaInputHandler.supportedGuiFilter = CorporeaInputHandler.supportedGuiFilter.or(screen -> {
			final var handledScreen = EmiApi.getHandledScreen();
			// should apply to EMI's recipe and BOM screens:
			return handledScreen != null && Minecraft.getInstance().screen != handledScreen;
		});
	}

	@Override
	public void register(EmiRegistry registry) {
		if (!CorporeaInputHandler.hoveredStackGetters.contains(HOVERED_STACK_GETTER)) {
			CorporeaInputHandler.hoveredStackGetters.add(HOVERED_STACK_GETTER);
		}
		registry.addCategory(PETAL_APOTHECARY);
		registry.addCategory(MANA_INFUSION);
		registry.addCategory(RUNIC_ALTAR);
		registry.addCategory(TERRESTRIAL_AGGLOMERATION);
		registry.addCategory(ELVEN_TRADE);
		registry.addCategory(BOTANICAL_BREWERY);
		registry.addCategory(PURE_DAISY);
		registry.addCategory(ORECHID);
		registry.addCategory(ORECHID_IGNEM);
		registry.addCategory(MARIMORPHOSIS);

		registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(BotaniaItems.craftingHalo));
		registry.addWorkstation(VanillaEmiRecipeCategories.CRAFTING, EmiStack.of(BotaniaItems.autocraftingHalo));

		for (Block apothecary : BotaniaBlocks.ALL_APOTHECARIES) {
			registry.addWorkstation(PETAL_APOTHECARY, EmiStack.of(apothecary));
		}
		registry.addWorkstation(MANA_INFUSION, EmiStack.of(BotaniaBlocks.manaPool));
		registry.addWorkstation(MANA_INFUSION, EmiStack.of(BotaniaBlocks.dilutedPool));
		registry.addWorkstation(MANA_INFUSION, EmiStack.of(BotaniaBlocks.fabulousPool));
		registry.addWorkstation(RUNIC_ALTAR, EmiStack.of(BotaniaBlocks.runeAltar));
		registry.addWorkstation(TERRESTRIAL_AGGLOMERATION, EmiStack.of(BotaniaBlocks.terraPlate));
		registry.addWorkstation(ELVEN_TRADE, EmiStack.of(BotaniaBlocks.alfPortal));
		registry.addWorkstation(BOTANICAL_BREWERY, EmiStack.of(BotaniaBlocks.brewery));

		registry.addWorkstation(PURE_DAISY, EmiStack.of(BotaniaFlowerBlocks.pureDaisy));
		registry.addWorkstation(PURE_DAISY, EmiStack.of(BotaniaFlowerBlocks.pureDaisyFloating));
		registry.addWorkstation(ORECHID, EmiStack.of(BotaniaFlowerBlocks.orechid));
		registry.addWorkstation(ORECHID, EmiStack.of(BotaniaFlowerBlocks.orechidFloating));
		registry.addWorkstation(ORECHID_IGNEM, EmiStack.of(BotaniaFlowerBlocks.orechidIgnem));
		registry.addWorkstation(ORECHID_IGNEM, EmiStack.of(BotaniaFlowerBlocks.orechidIgnemFloating));
		registry.addWorkstation(MARIMORPHOSIS, EmiStack.of(BotaniaFlowerBlocks.marimorphosis));
		registry.addWorkstation(MARIMORPHOSIS, EmiStack.of(BotaniaFlowerBlocks.marimorphosisFloating));
		registry.addWorkstation(MARIMORPHOSIS, EmiStack.of(BotaniaFlowerBlocks.marimorphosisChibi));
		registry.addWorkstation(MARIMORPHOSIS, EmiStack.of(BotaniaFlowerBlocks.marimorphosisChibiFloating));

		registry.setDefaultComparison(BotaniaItems.lexicon, Comparison.compareNbt());
		registry.setDefaultComparison(BotaniaItems.brewFlask, Comparison.compareNbt());
		registry.setDefaultComparison(BotaniaItems.brewVial, Comparison.compareNbt());
		registry.setDefaultComparison(BotaniaItems.bloodPendant, Comparison.compareNbt());
		registry.setDefaultComparison(BotaniaItems.incenseStick, Comparison.compareNbt());
		// Disables the ability to see the no wings tiara recipe, probably an nbt mismatch
		//registry.setDefaultComparison(BotaniaItems.flightTiara, compareNbt);

		registry.addRecipe(new AncientWillEmiRecipe(EmiStack.of(BotaniaItems.terrasteelHelm), EmiIngredient.of(List.of(
				EmiStack.of(BotaniaItems.ancientWillAhrim),
				EmiStack.of(BotaniaItems.ancientWillDharok),
				EmiStack.of(BotaniaItems.ancientWillGuthan),
				EmiStack.of(BotaniaItems.ancientWillKaril),
				EmiStack.of(BotaniaItems.ancientWillTorag),
				EmiStack.of(BotaniaItems.ancientWillVerac)
		))));

		registry.addRecipe(new CompositeLensEmiRecipe(
				StreamSupport.stream(BuiltInRegistries.ITEM.getOrCreateTag(BotaniaTags.Items.LENS).spliterator(), false)
						.map(ItemStack::new)
						.filter(s -> !((LensItem) s.getItem()).isControlLens(s))
						.filter(s -> ((LensItem) s.getItem()).isCombinable(s))
						.map(EmiStack::of)
						.toList()));

		ItemStack tipped = new ItemStack(BotaniaItems.terraPick);
		TerraShattererItem.setTipped(tipped);
		registry.addRecipe(new EmiCraftingRecipe(List.of(EmiStack.of(BotaniaItems.terraPick),
				EmiStack.of(BotaniaItems.elementiumPick)), EmiStack.of(tipped), null));

		for (var recipe : registry.getRecipeManager().getAllRecipesFor(BotaniaRecipeTypes.PETAL_TYPE)) {
			registry.addRecipe(new PetalApothecaryEmiRecipe(recipe));
		}
		for (var recipe : registry.getRecipeManager().getAllRecipesFor(BotaniaRecipeTypes.MANA_INFUSION_TYPE)) {
			registry.addRecipe(new ManaInfusionEmiRecipe(recipe));
		}
		for (var recipe : registry.getRecipeManager().getAllRecipesFor(BotaniaRecipeTypes.RUNE_TYPE)) {
			registry.addRecipe(new RunicAltarEmiRecipe(recipe));
		}
		for (var recipe : registry.getRecipeManager().getAllRecipesFor(BotaniaRecipeTypes.TERRA_PLATE_TYPE)) {
			registry.addRecipe(new TerrestrialAgglomerationEmiRecipe(recipe));
		}
		for (var recipe : registry.getRecipeManager().getAllRecipesFor(BotaniaRecipeTypes.ELVEN_TRADE_TYPE)) {
			registry.addRecipe(new ElvenTradeEmiRecipe(recipe));
		}
		List<ItemStack> containers = List.of(BotaniaItems.vial, BotaniaItems.flask, BotaniaItems.incenseStick, BotaniaItems.bloodPendant)
				.stream().map(ItemStack::new).toList();
		for (var recipe : registry.getRecipeManager().getAllRecipesFor(BotaniaRecipeTypes.BREW_TYPE)) {
			for (ItemStack container : containers) {
				if (!recipe.value().getOutput(container.copy()).isEmpty()) {
					registry.addRecipe(new BotanicalBreweryEmiRecipe(recipe, container));
				}
			}
		}
		for (var recipe : registry.getRecipeManager().getAllRecipesFor(BotaniaRecipeTypes.PURE_DAISY_TYPE)) {
			registry.addRecipe(new PureDaisyEmiRecipe(recipe));
		}

		EmiIngredient orechid = EmiStack.of(BotaniaFlowerBlocks.orechid);
		for (var recipe : registry.getRecipeManager().getAllRecipesFor(BotaniaRecipeTypes.ORECHID_TYPE)) {
			registry.addRecipe(new OrechidEmiRecipe(ORECHID, recipe, orechid));
		}
		EmiIngredient orechidIgnem = EmiStack.of(BotaniaFlowerBlocks.orechidIgnem);
		for (var recipe : registry.getRecipeManager().getAllRecipesFor(BotaniaRecipeTypes.ORECHID_IGNEM_TYPE)) {
			registry.addRecipe(new OrechidEmiRecipe(ORECHID_IGNEM, recipe, orechidIgnem));
		}
		EmiIngredient marimorphosis = EmiStack.of(BotaniaFlowerBlocks.marimorphosis);
		for (var recipe : registry.getRecipeManager().getAllRecipesFor(BotaniaRecipeTypes.MARIMORPHOSIS_TYPE)) {
			registry.addRecipe(new OrechidEmiRecipe(MARIMORPHOSIS, recipe, marimorphosis));
		}
	}

	public static int rotateXAround(int x, int y, int cx, int cy, double degrees) {
		double rad = Math.toRadians(degrees);
		return (int) (Math.cos(rad) * (x - cx) - Math.sin(rad) * (y - cy) + cx);
	}

	public static int rotateYAround(int x, int y, int cx, int cy, double degrees) {
		double rad = Math.toRadians(degrees);
		return (int) (Math.sin(rad) * (x - cx) - Math.cos(rad) * (y - cy) + cy);
	}
}
