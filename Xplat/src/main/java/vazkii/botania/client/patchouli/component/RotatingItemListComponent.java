/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.patchouli.component;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.crafting.Ingredient;

import vazkii.patchouli.api.IVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Patchouli custom component that draws a rotating circle of items from a provided list.
 */
public class RotatingItemListComponent extends RotatingItemListComponentBase {
	public List<IVariable> ingredients;

	private transient final List<Ingredient> theIngredients = new ArrayList<>();

	@Override
	protected List<Ingredient> makeIngredients() {
		return theIngredients;
	}

	@Override
	public void onVariablesAvailable(UnaryOperator<IVariable> lookup, HolderLookup.Provider registries) {
		theIngredients.clear();
		for (IVariable ingredient : ingredients) {
			theIngredients.add(lookup.apply(ingredient).as(Ingredient.class));
		}
	}
}
