/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity;

import com.google.common.base.Suppliers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * Version of {@link SimpleInventoryBlockEntity} where the backing inventory is exposed to automation
 */
public abstract class ExposedSimpleInventoryBlockEntity extends SimpleInventoryBlockEntity implements WorldlyContainer {
	private final Supplier<int[]> slots = Suppliers.memoize(() -> IntStream.range(0, getContainerSize()).toArray());

	protected ExposedSimpleInventoryBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public boolean isEmpty() {
		return getItemHandler().isEmpty();
	}

	@Override
	public int getContainerSize() {
		return inventorySize();
	}

	@Override
	public ItemStack getItem(int index) {
		return getItemHandler().getItem(index);
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		return getItemHandler().removeItem(index, count);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return getItemHandler().removeItemNoUpdate(index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		getItemHandler().setItem(index, stack);
	}

	@Override
	public boolean stillValid(Player player) {
		return getItemHandler().stillValid(player);
	}

	@Override
	public int getMaxStackSize() {
		return getItemHandler().getMaxStackSize();
	}

	@Override
	public void startOpen(Player player) {
		getItemHandler().startOpen(player);
	}

	@Override
	public void stopOpen(Player player) {
		getItemHandler().stopOpen(player);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return getItemHandler().canPlaceItem(index, stack);
	}

	@Override
	public int countItem(Item item) {
		return getItemHandler().countItem(item);
	}

	@Override
	public boolean hasAnyOf(Set<Item> set) {
		return getItemHandler().hasAnyOf(set);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return slots.get();
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack stack, @Nullable Direction direction) {
		if (canPlaceItem(index, stack)) {
			// Vanilla hoppers do not check the inventory's stack limit, so do so here.
			// We don't have to check anything else like stackability because the hopper logic will do it
			ItemStack existing = getItem(index);
			return existing.getCount() < getMaxStackSize();
		}

		return false;
	}

	@Override
	public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @Nullable Direction direction) {
		return true;
	}
}
