/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.flower.generating;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.lib.BotaniaTags;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class RafflowsiaBlockEntity extends GeneratingFlowerBlockEntity {
	public static final String TAG_LAST_FLOWERS = "lastFlowers";
	public static final String TAG_LAST_FLOWER_TIMES = "lastFlowerTimes";
	public static final String TAG_STREAK_LENGTH = "streakLength";

	private final List<ResourceLocation> lastFlowers = new LinkedList<>();
	private int streakLength = -1;
	private int lastFlowerCount = 0;

	private static final int RANGE = 5;

	// Below table generated from this spreadsheet:
	// https://docs.google.com/spreadsheets/d/1D5qvYRrwm6-czKnXVIEjakt93I0asICgxnPJ_q6UCc0
	// Function created from a best-fit approximation on the sorted raw mana costs of production of each flower.
	private static final int[] STREAK_OUTPUTS = { 2000, 2100, 2200, 2300, 3280, 4033, 4657, 5150, 6622, 7860, 10418, 12600, 14769, 16671, 19000, 25400, 33471, 40900, 47579, 53600, 59057, 64264, 69217, 74483, 79352, 83869, 88059, 92129, 96669, 100940, 105239, 112044, 118442, 124612, 130583, 136228, 141703, 178442, 213959, 247725, 279956, 313671, 345833, 377227, 437689, 495526, 553702, 638554 };

	public RafflowsiaBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaFlowerBlocks.RAFFLOWSIA, pos, state);
	}

	private int getMaxStreak() {
		return STREAK_OUTPUTS.length - 1;
	}

	private int getValueForStreak(int index) {
		// special-case repeated first flowers
		if (index != 0) {
			lastFlowerCount = 0;
		}
		return STREAK_OUTPUTS[index] / ++lastFlowerCount;
	}

	/**
	 * Processes a flower, placing it in the appropriate place in the history.
	 * 
	 * @return the last time the flower showed up in history.
	 */
	private int processFlower(Block flower) {
		ResourceLocation flowerKey = BuiltInRegistries.BLOCK.getKey(flower);
		for (ListIterator<ResourceLocation> it = lastFlowers.listIterator(); it.hasNext();) {
			int index = it.nextIndex();
			ResourceLocation streakFlower = it.next();
			if (streakFlower.equals(flowerKey)) {
				it.remove();
				lastFlowers.add(0, streakFlower);
				return index;
			}
		}
		lastFlowers.add(0, flowerKey);
		if (lastFlowers.size() >= getMaxStreak()) {
			lastFlowers.remove(lastFlowers.size() - 1);
		}
		return getMaxStreak();
	}

	@Override
	public void tickFlower() {
		super.tickFlower();

		int mana = 2100;

		if (getMaxMana() - this.getMana() >= mana && !getLevel().isClientSide && ticksExisted % 40 == 0) {
			for (int i = 0; i < RANGE * 2 + 1; i++) {
				for (int j = 0; j < RANGE * 2 + 1; j++) {
					for (int k = 0; k < RANGE * 2 + 1; k++) {
						BlockPos pos = getEffectivePos().offset(i - RANGE, j - RANGE, k - RANGE);

						BlockState state = getLevel().getBlockState(pos);
						if (state.is(BotaniaTags.Blocks.SPECIAL_FLOWERS) && !state.is(BotaniaFlowerBlocks.rafflowsia)) {
							streakLength = Math.min(streakLength + 1, processFlower(state.getBlock()));

							getLevel().destroyBlock(pos, false);
							addMana(getValueForStreak(streakLength));
							sync();
							return;
						}
					}
				}
			}
		}
	}

	@Override
	public void writeToPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.writeToPacketNBT(cmp, registries);

		ListTag flowerList = new ListTag();
		for (ResourceLocation flower : lastFlowers) {
			flowerList.add(StringTag.valueOf(flower.toString()));
		}
		cmp.put(TAG_LAST_FLOWERS, flowerList);
		cmp.putInt(TAG_LAST_FLOWER_TIMES, lastFlowerCount);
		cmp.putInt(TAG_STREAK_LENGTH, streakLength);
	}

	@Override
	public void readFromPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.readFromPacketNBT(cmp, registries);

		lastFlowers.clear();
		ListTag flowerList = cmp.getList(TAG_LAST_FLOWERS, Tag.TAG_STRING);
		for (int i = 0; i < flowerList.size(); i++) {
			ResourceLocation blockID = ResourceLocation.tryParse(flowerList.getString(i));
			if (blockID == null) {
				continue;
			}
			lastFlowers.add(blockID);
		}
		lastFlowerCount = cmp.getInt(TAG_LAST_FLOWER_TIMES);
		streakLength = cmp.getInt(TAG_STREAK_LENGTH);
	}

	@Override
	public RadiusDescriptor getRadius() {
		return RadiusDescriptor.Rectangle.square(getEffectivePos(), RANGE);
	}

	@Override
	public int getColor() {
		return 0x502C76;
	}

	@Override
	public int getMaxMana() {
		return STREAK_OUTPUTS[STREAK_OUTPUTS.length - 1];
	}

}
