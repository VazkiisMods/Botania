/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.mixin;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor {
	@Accessor("items")
	NonNullList<ItemStack> getItems();

	@Accessor("quickCheck")
	RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> getQuickCheck();

	@Accessor
	int getLitTime();

	@Accessor
	void setLitTime(int burnTime);

	@Accessor
	int getCookingProgress();

	@Accessor
	int getCookingTotalTime();

	@Accessor
	void setCookingProgress(int cookTime);
}
