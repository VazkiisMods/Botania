/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.block;

import net.minecraft.resources.ResourceLocation;
import vazkii.botania.api.BotaniaAPI;

/**
 * A Block Entity that has this component can be heated by an Exoflame flower.
 * NOTE: Do not attach this component to subclasses of AbstractFurnaceBlockEntity, as Botania already does so.
 */
public interface ExoflameHeatable {
	ResourceLocation ID = new ResourceLocation(BotaniaAPI.MODID, "exoflame_heatable");

	/**
	 * Can this BlockEntity smelt its contents. If true, the Exoflame is allowed
	 * to fuel it.
	 */
	boolean canSmelt();

	/**
	 * Gets the amount of ticks left for the fuel. If below 2, the exoflame
	 * will call boostBurnTime.
	 */
	int getBurnTime();

	/**
	 * Called to increase the amount of time this furnace should be burning
	 * the fuel for. Even if it doesn't have any fuel.
	 */
	void boostBurnTime();

	/**
	 * Called once every two ticks to increase the speed of the furnace. Feel
	 * free to not do anything if all you want is to allow the exoflame to feed
	 * it, not make it faster.
	 */
	void boostCookTime();
}
