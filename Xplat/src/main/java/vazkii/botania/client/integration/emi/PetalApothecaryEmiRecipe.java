package vazkii.botania.client.integration.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;

import vazkii.botania.api.recipe.PetalApothecaryRecipe;
import vazkii.botania.common.block.BotaniaBlocks;

import java.util.List;
import java.util.stream.Stream;

public class PetalApothecaryEmiRecipe extends BotaniaEmiRecipe {
	private static final EmiStack APOTHECARY = EmiStack.of(BotaniaBlocks.defaultAltar);
	private static final EmiStack WATER_BUCKET = EmiStack.of(Items.WATER_BUCKET);
	private static final EmiStack WATER = EmiStack.of(Fluids.WATER);

	private final List<EmiIngredient> ingredients;
	private final EmiIngredient reagent;

	public PetalApothecaryEmiRecipe(RecipeHolder<? extends PetalApothecaryRecipe> recipe) {
		super(BotaniaEmiPlugin.PETAL_APOTHECARY, recipe);
		this.ingredients = recipe.value().getIngredients().stream().map(EmiIngredient::of).toList();
		this.reagent = EmiIngredient.of(recipe.value().getReagent());
		this.input = Stream.concat(ingredients.stream(), Stream.of(WATER, reagent)).toList();
		// TODO 1.19.4 figure out the proper way to get a registry access
		this.output = List.of(EmiStack.of(recipe.value().getResultItem(RegistryAccess.EMPTY)));
	}

	@Override
	public int getDisplayHeight() {
		return 107;
	}

	@Override
	public int getDisplayWidth() {
		return 106;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		RunicAltarEmiRecipe.addRunicAltarWidgets(widgets, this, ingredients, APOTHECARY, output.get(0), reagent, WATER_BUCKET);
	}
}
