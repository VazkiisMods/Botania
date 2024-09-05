/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.internal_caps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class NarslimmusComponent extends SerializableComponent {
	public static final String TAG_WORLD_SPAWNED = "botania:world_spawned";
	public static final ResourceLocation ID = prefix("narslimmus");
	private boolean naturalSpawned = false;

	@Override
	public void readFromNbt(CompoundTag tag) {
		naturalSpawned = tag.getBoolean(TAG_WORLD_SPAWNED);
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		tag.putBoolean(TAG_WORLD_SPAWNED, naturalSpawned);
	}

	public boolean isNaturalSpawned() {
		return naturalSpawned;
	}

	public void setNaturalSpawn(boolean naturalSpawned) {
		this.naturalSpawned = naturalSpawned;
	}
}
