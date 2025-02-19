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
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class KeptItemsComponent extends SerializableComponent {
	public static final ResourceLocation ID = botaniaRL("kept_items");
	private final List<ItemStack> stacks = new ArrayList<>();

	public void addAll(Collection<ItemStack> stack) {
		stacks.addAll(stack);
	}

	public List<ItemStack> getStacks() {
		return stacks;
	}

	@Override
	public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		stacks.clear();
		ListTag list = tag.getList("stacks", Tag.TAG_COMPOUND);
		for (Tag t : list) {
			stacks.add(/*ItemStack.of((CompoundTag) t)*/ ItemStack.EMPTY);
		}
	}

	@Override
	public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		ListTag list = new ListTag();
		for (ItemStack stack : stacks) {
			//todo list.add(stack.save(new CompoundTag()));
		}
		tag.put("stacks", list);
	}
}
