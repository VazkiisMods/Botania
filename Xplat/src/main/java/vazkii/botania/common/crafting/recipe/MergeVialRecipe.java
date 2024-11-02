/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.brew.Brew;
import vazkii.botania.common.item.brew.BaseBrewItem;

import java.util.Objects;

public class MergeVialRecipe extends CustomRecipe {
	public static final RecipeSerializer<MergeVialRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(MergeVialRecipe::new);

	public MergeVialRecipe(CraftingBookCategory craftingBookCategory) {
		super(craftingBookCategory);
	}

	@Override
	public boolean matches(CraftingContainer inv, @NotNull Level worldIn) {
		int count = 0;
		Brew brew = null;

		for (int i = 0; i < inv.getContainerSize(); ++i) {
			ItemStack stack = inv.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}

			if (!(stack.getItem() instanceof BaseBrewItem vial)) {
				return false;
			}

			if (brew == null) {
				brew = vial.getBrew(stack);
			} else if (brew != vial.getBrew(stack)) {
				return false;
			}
			count++;
		}

		return count > 1;
	}

	@NotNull
	@Override
	public ItemStack assemble(CraftingContainer inv, @NotNull RegistryAccess registries) {
		ItemStack firstStack = ItemStack.EMPTY;
		BaseBrewItem brew = null;
		int swigs = 0;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}

			if (brew == null) {
				firstStack = stack.copy();
				brew = ((BaseBrewItem) stack.getItem());
			}
			swigs += brew.getSwigsLeft(stack);
			if (swigs >= brew.getSwigs()) {
				swigs = brew.getSwigs();
				break;
			}
		}

		Objects.requireNonNull(brew).setSwigsLeft(firstStack, swigs);
		return firstStack;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer inv) {
		NonNullList<ItemStack> remaining = NonNullList.withSize(inv.getContainerSize(), ItemStack.EMPTY);

		boolean foundFirst = false;
		int swigs = 0;
		int maxSwigs = 0;

		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}

			BaseBrewItem brew = ((BaseBrewItem) stack.getItem());
			if (!foundFirst) {
				foundFirst = true;
				swigs = brew.getSwigsLeft(stack);
				maxSwigs = brew.getSwigs();
				continue;
			}

			swigs += brew.getSwigsLeft(stack);
			if (swigs > maxSwigs) {
				brew.setSwigsLeft(stack, swigs - maxSwigs);
				swigs = maxSwigs;
				remaining.set(i, stack.copy());
			} else {
				remaining.set(i, brew.getBaseStack());
			}
		}

		return remaining;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height > 2;
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
