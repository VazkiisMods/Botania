/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

/**
 * An item that has this capability counts as a Relic item. This is purely for interaction
 * and other mod items should not reuse this capability.
 */
public interface Relic {

	ResourceLocation ID = botaniaRL("relic");

	/**
	 * Binds to the UUID passed in.
	 */
	void bindToUUID(UUID uuid);

	/**
	 * Gets the UUID of the person this relic is bound to, or null if a well-formed UUID could not be found
	 */
	@Nullable
	UUID getSoulbindUUID();

	/**
	 * Attempts to bind to a player, or damage them if it's not theirs
	 */
	void tickBinding(Player player);

	/**
	 * Get the advancement granted when this relic binds
	 */
	@Nullable
	default ResourceLocation getAdvancement() {
		return null;
	}

	default boolean shouldDamageWrongPlayer() {
		return true;
	}

	boolean isRightPlayer(Player player);

}
