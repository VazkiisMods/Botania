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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.api.block.FloatingFlower;
import vazkii.botania.api.block.FloatingFlowerImpl;
import vazkii.botania.api.block.FloatingFlowerProvider;
import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.decor.FloatingFlowerBlock;

public class FloatingFlowerBlockEntity extends BotaniaBlockEntity implements FloatingFlowerProvider {
	private static final String TAG_FLOATING_DATA = "floating";
	private final FloatingFlower floatingData = new FloatingFlowerImpl() {
		@Override
		public ItemStack getDisplayStack() {
			Block b = getBlockState().getBlock();
			if (b instanceof FloatingFlowerBlock floatingFlower) {
				return new ItemStack(BotaniaBlocks.getShinyFlower(floatingFlower.color));
			} else {
				return ItemStack.EMPTY;
			}
		}
	};

	public FloatingFlowerBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.MINI_ISLAND, pos, state);
	}

	@Override
	public FloatingFlower getFloatingData() {
		return floatingData;
	}

	@Override
	public void writePacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		cmp.put(TAG_FLOATING_DATA, floatingData.writeNBT());
	}

	@Override
	public void readPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		FloatingFlower.IslandType oldType = floatingData.getIslandType();
		floatingData.readNBT(cmp.getCompound(TAG_FLOATING_DATA));
		if (oldType != floatingData.getIslandType() && level != null && level.isClientSide) {
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
		}
	}

	@SoftImplement("RenderDataBlockEntity")
	public Object getRenderData() {
		return floatingData.getIslandType();
	}
}
