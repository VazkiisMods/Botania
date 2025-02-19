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

import vazkii.botania.common.block.SpectralRailBlock;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class SpectralRailComponent extends SerializableComponent {
	public static final ResourceLocation ID = botaniaRL("ghost_rail");
	public int floatTicks = 0;

	@Override
	public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		floatTicks = tag.getInt(SpectralRailBlock.TAG_FLOAT_TICKS);
	}

	@Override
	public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		tag.putInt(SpectralRailBlock.TAG_FLOAT_TICKS, floatTicks);
	}
}
