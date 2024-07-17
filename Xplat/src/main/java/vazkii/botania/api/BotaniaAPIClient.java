/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.api.block.FloatingFlower;

import java.util.Collections;
import java.util.Map;

/**
 * Class for API calls that must be made clientside
 */
public interface BotaniaAPIClient {
	BotaniaAPIClient INSTANCE = ServiceUtil.findService(BotaniaAPIClient.class, () -> new BotaniaAPIClient() {});

	static BotaniaAPIClient instance() {
		return INSTANCE;
	}

	/**
	 * Registers your model for island type islandType here.
	 * Call this during {@link net.neoforged.neoforge.client.event.ModelRegistryEvent}.
	 *
	 * @param islandType The islandtype to register
	 * @param model      The model, only {@link ResourceLocation} allowed, no {@link ModelResourceLocation} allowed.
	 */
	default void registerIslandTypeModel(FloatingFlower.IslandType islandType, ResourceLocation model) {}

	/**
	 * @return An immutable and live view of the registered island type model map
	 */
	default Map<FloatingFlower.IslandType, ResourceLocation> getRegisteredIslandTypeModels() {
		return Collections.emptyMap();
	}

	/**
	 * Draw a mana bar on the screen
	 */
	default void drawSimpleManaHUD(GuiGraphics gui, int color, int mana, int maxMana, String name) {}

	/**
	 * Performs the effects of {@link #drawSimpleManaHUD}, then renders {@code bindDisplay}, and a checkmark or x-mark
	 * dependong on the value of {@code properlyBound}.
	 */
	default void drawComplexManaHUD(GuiGraphics gui, int color, int mana, int maxMana, String name, ItemStack bindDisplay, boolean properlyBound) {}
}
