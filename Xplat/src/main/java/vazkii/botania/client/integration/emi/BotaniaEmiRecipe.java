package vazkii.botania.client.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BotaniaEmiRecipe implements EmiRecipe {
	private final EmiRecipeCategory category;
	private final ResourceLocation id;
	protected List<EmiIngredient> input = List.of();
	protected List<EmiIngredient> catalysts = List.of();
	protected List<EmiStack> output = List.of();
	private final String group;

	public BotaniaEmiRecipe(EmiRecipeCategory category, RecipeHolder<?> recipe) {
		this.category = category;
		this.id = recipe.id();
		this.group = recipe.value().getGroup();
	}

	@Override
	public EmiRecipeCategory getCategory() {
		return category;
	}

	@Override
	public @Nullable ResourceLocation getId() {
		return id;
	}

	@Override
	public List<EmiIngredient> getInputs() {
		return input;
	}

	@Override
	public List<EmiIngredient> getCatalysts() {
		return catalysts;
	}

	@Override
	public List<EmiStack> getOutputs() {
		return output;
	}

	public String getGroup() {
		return group;
	}
}
