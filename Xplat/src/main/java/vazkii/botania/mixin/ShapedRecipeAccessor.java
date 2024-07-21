/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapedRecipe.class)
public interface ShapedRecipeAccessor {
	/**
	 * Raw pattern access, since this is not exposed directly.
	 */
	@Accessor("pattern")
	ShapedRecipePattern botania_getPattern();

	/**
	 * Raw accessor so we don't have to deal with janky empty registry access.
	 */
	@Accessor("result")
	ItemStack botania_getResult();
}
