package vazkii.botania.api.recipe;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;

public interface RecipeWithReagent extends Recipe<RecipeInput> {
	/**
	 * @return Ingredient matching the final item that needs to be thrown into the apothecary
	 *         to perform a craft after a matching recipe is in.
	 */
	Ingredient getReagent();

	@Override
	default boolean canCraftInDimensions(int width, int height) {
		return false;
	}

	@Override
	default boolean isSpecial() {
		return true;
	}
}
