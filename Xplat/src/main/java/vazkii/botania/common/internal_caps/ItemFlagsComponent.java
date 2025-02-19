/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.internal_caps;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

// Component for misc internal Botania flags
public class ItemFlagsComponent extends SerializableComponent {
	public static final ResourceLocation ID = botaniaRL("iitem");
	public boolean elvenPortalSpawned = false;
	public boolean apothecarySpawned = false;
	public boolean manaInfusionSpawned = false;
	public boolean runicAltarSpawned = false;
	/**
	 * Similar to the age field on the actual entity, but always increases by 1 every tick,
	 * no magic values like vanilla -32768, etc.
	 * Initialized to zero by default, but may still be initialized to values less
	 * than zero in certain scenarios.
	 */
	public int timeCounter = 0;

	private static final String TAG_PORTAL_SPAWNED = "ElvenPortalSpawned";
	private static final String TAG_APOTHECARY_SPAWNED = "ApothecarySpawned";
	private static final String TAG_INFUSION_SPAWNED = "ManaInfusionSpawned";
	private static final String TAG_ALTAR_SPAWNED = "RunicAltarSpawned";

	private static final String TAG_TIME_COUNTER = "timeCounter";

	@Override
	public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		elvenPortalSpawned = tag.getBoolean(TAG_PORTAL_SPAWNED);
		apothecarySpawned = tag.getBoolean(TAG_APOTHECARY_SPAWNED);
		manaInfusionSpawned = tag.getBoolean(TAG_INFUSION_SPAWNED);
		runicAltarSpawned = tag.getBoolean(TAG_ALTAR_SPAWNED);
		timeCounter = tag.getInt(TAG_TIME_COUNTER);
		// legacy tags
		if (tag.getBoolean("_elvenPortal")) {
			elvenPortalSpawned = true;
		}
		if (tag.getInt("manaInfusionCooldown") > 0) {
			manaInfusionSpawned = true;
		}
		if (tag.getInt("runicAltarCooldown") > 0) {
			runicAltarSpawned = true;
		}
	}

	@Override
	public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		tag.putBoolean(TAG_PORTAL_SPAWNED, elvenPortalSpawned);
		tag.putBoolean(TAG_APOTHECARY_SPAWNED, apothecarySpawned);
		tag.putBoolean(TAG_INFUSION_SPAWNED, runicAltarSpawned);
		tag.putBoolean(TAG_ALTAR_SPAWNED, manaInfusionSpawned);
		tag.putInt(TAG_TIME_COUNTER, timeCounter);
	}

	public void tick() {
		timeCounter++;
	}

	public boolean spawnedByInWorldRecipe() {
		return elvenPortalSpawned || apothecarySpawned || runicAltarSpawned || manaInfusionSpawned;
	}
}
