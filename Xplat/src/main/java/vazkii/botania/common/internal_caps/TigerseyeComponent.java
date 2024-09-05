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

public class TigerseyeComponent extends SerializableComponent {
	public static final ResourceLocation ID = prefix("tigerseye_pacified");
	private static final String TAG_PACIFIED = "botania:tigerseye_pacified";
	private boolean pacified = false;

	public boolean isPacified() {
		return pacified;
	}

	public void setPacified() {
		this.pacified = true;
	}

	@Override
	public void readFromNbt(CompoundTag tag) {
		this.pacified = tag.getBoolean(TAG_PACIFIED);
	}

	@Override
	public void writeToNbt(CompoundTag tag) {
		if (pacified) {
			tag.putBoolean(TAG_PACIFIED, true);
		}
	}
}
