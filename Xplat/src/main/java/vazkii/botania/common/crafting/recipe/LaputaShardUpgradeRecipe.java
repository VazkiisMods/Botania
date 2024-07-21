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
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.LaputaShardItem;

public class LaputaShardUpgradeRecipe extends CustomRecipe {
	public static final RecipeSerializer<LaputaShardUpgradeRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(LaputaShardUpgradeRecipe::new);

	public LaputaShardUpgradeRecipe(CraftingBookCategory craftingBookCategory) {
		super(craftingBookCategory);
	}

	@Override
	public boolean matches(@NotNull CraftingContainer inv, @NotNull Level worldIn) {
		boolean foundShard = false;
		boolean foundSpirit = false;
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.isEmpty()) {
				continue;
			}
			if (stack.is(BotaniaItems.laputaShard) && !foundShard
					&& LaputaShardItem.getShardLevel(stack) < 19) {
				foundShard = true;
			} else if (stack.is(BotaniaItems.lifeEssence) && !foundSpirit) {
				foundSpirit = true;
			} else {
				return false;
			}
		}
		return foundShard && foundSpirit;
	}

	@NotNull
	@Override
	public ItemStack getResultItem(@NotNull RegistryAccess registries) {
		return new ItemStack(BotaniaItems.laputaShard);
	}

	@NotNull
	@Override
	public NonNullList<Ingredient> getIngredients() {
		return NonNullList.of(Ingredient.EMPTY,
				Ingredient.of(BotaniaItems.laputaShard),
				Ingredient.of(BotaniaItems.lifeEssence));
	}

	@NotNull
	@Override
	public ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess registries) {
		for (int i = 0; i < inv.getContainerSize(); i++) {
			ItemStack stack = inv.getItem(i);
			if (stack.is(BotaniaItems.laputaShard)) {
				ItemStack result = stack.copy();
				result.getOrCreateTag().putInt(LaputaShardItem.TAG_LEVEL, LaputaShardItem.getShardLevel(stack) + 1);
				return result;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canCraftInDimensions(int width, int height) {
		return width * height >= 2;
	}

	@NotNull
	@Override
	public RecipeSerializer<?> getSerializer() {
		return SERIALIZER;
	}
}
