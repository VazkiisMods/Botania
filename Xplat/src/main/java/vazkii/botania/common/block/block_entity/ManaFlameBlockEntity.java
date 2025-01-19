/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class ManaFlameBlockEntity extends BotaniaBlockEntity {
	private static final String TAG_COLOR = "color";

	private int color = 0x20FF20;

	public ManaFlameBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.MANA_FLAME, pos, state);
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	@Override
	public void writePacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		cmp.putInt(TAG_COLOR, color);
	}

	@Override
	public void readPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		color = cmp.getInt(TAG_COLOR);
	}

}
