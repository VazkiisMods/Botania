/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.integration.rei;

import com.google.common.collect.ImmutableList;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.recipe.PureDaisyRecipe;

import java.util.Collections;

public class PureDaisyREIDisplay extends BotaniaRecipeDisplay<PureDaisyRecipe> {

	public PureDaisyREIDisplay(RecipeHolder<? extends PureDaisyRecipe> recipe) {
		super(recipe);
		ImmutableList.Builder<EntryStack<?>> inputs = ImmutableList.builder();
		for (BlockState state : recipe.value().getInput().getDisplayed()) {
			if (!state.getFluidState().isEmpty()) {
				inputs.add(EntryStacks.of(state.getFluidState().getType()));
			} else {
				inputs.add(EntryStacks.of(state.getBlock()));
			}
		}
		this.inputs = Collections.singletonList(EntryIngredient.of(inputs.build()));

		ImmutableList.Builder<EntryStack<?>> outputs = ImmutableList.builder();
		for (BlockState state : recipe.value().getOutput().getDisplayed()) {
			if (!state.getFluidState().isEmpty()) {
				outputs.add(EntryStacks.of(state.getFluidState().getType()));
			} else {
				outputs.add(EntryStacks.of(state.getBlock()));
			}
		}
		this.outputs = EntryIngredient.of(outputs.build());
	}

	/*todo implement time-based hints?
	public int getProcessingTime() {
		return recipe.getTime();
	}
	*/

	@Override
	public int getManaCost() {
		return 0;
	}

	@Override
	public @NotNull CategoryIdentifier<?> getCategoryIdentifier() {
		return BotaniaREICategoryIdentifiers.PURE_DAISY;
	}
}
