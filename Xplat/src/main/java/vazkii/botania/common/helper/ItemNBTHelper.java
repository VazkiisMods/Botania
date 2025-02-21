/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.helper;

import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.xplat.XplatAbstractions;

// TODO literally just everything here
public final class ItemNBTHelper {

	private static final int[] EMPTY_INT_ARRAY = new int[0];
	private static final long[] EMPTY_LONG_ARRAY = new long[0];

	// SETTERS ///////////////////////////////////////////////////////////////////

	@Deprecated(forRemoval = true)
	public static void set(ItemStack stack, String tag, Tag nbt) {
		//stack.getOrCreateTag().put(tag, nbt);
	}

	@Deprecated(forRemoval = true)
	public static void setBoolean(ItemStack stack, String tag, boolean b) {
		//stack.getOrCreateTag().putBoolean(tag, b);
	}

	@Deprecated(forRemoval = true)
	public static void setByte(ItemStack stack, String tag, byte b) {
		//stack.getOrCreateTag().putByte(tag, b);
	}

	@Deprecated(forRemoval = true)
	public static void setShort(ItemStack stack, String tag, short s) {
		//stack.getOrCreateTag().putShort(tag, s);
	}

	@Deprecated(forRemoval = true)
	public static void setInt(ItemStack stack, String tag, int i) {
		//stack.getOrCreateTag().putInt(tag, i);
	}

	@Deprecated(forRemoval = true)
	public static void setIntArray(ItemStack stack, String tag, int[] val) {
		//stack.getOrCreateTag().putIntArray(tag, val);
	}

	@Deprecated(forRemoval = true)
	public static void setLong(ItemStack stack, String tag, long l) {
		//stack.getOrCreateTag().putLong(tag, l);
	}

	@Deprecated(forRemoval = true)
	public static void setLongArray(ItemStack stack, String tag, long[] val) {
		//stack.getOrCreateTag().putLongArray(tag, val);
	}

	@Deprecated(forRemoval = true)
	public static void setFloat(ItemStack stack, String tag, float f) {
		//stack.getOrCreateTag().putFloat(tag, f);
	}

	@Deprecated(forRemoval = true)
	public static void setDouble(ItemStack stack, String tag, double d) {
		//stack.getOrCreateTag().putDouble(tag, d);
	}

	@Deprecated(forRemoval = true)
	public static void setCompound(ItemStack stack, String tag, CompoundTag cmp) {
		if (!tag.equalsIgnoreCase("ench")) // not override the enchantments
		{
			//stack.getOrCreateTag().put(tag, cmp);
		}
	}

	@Deprecated(forRemoval = true)
	public static void setString(ItemStack stack, String tag, String s) {
		//stack.getOrCreateTag().putString(tag, s);
	}

	@Deprecated(forRemoval = true)
	public static void setList(ItemStack stack, String tag, ListTag list) {
		//stack.getOrCreateTag().put(tag, list);
	}

	@Deprecated(forRemoval = true)
	public static void removeEntry(ItemStack stack, String tag) {
		//stack.removeTagKey(tag);
	}

	// GETTERS ///////////////////////////////////////////////////////////////////

	@Deprecated(forRemoval = true)
	public static boolean verifyExistance(ItemStack stack, String tag) {
		return !stack.isEmpty() /*&& stack.hasTag() && stack.getOrCreateTag().contains(tag)*/;
	}

	@Deprecated(forRemoval = true)
	public static boolean verifyType(ItemStack stack, String tag, Class<? extends Tag> tagClass) {
		return !stack.isEmpty() /*&& stack.hasTag() && tagClass.isInstance(stack.getOrCreateTag().get(tag))*/;
	}

	@Deprecated(forRemoval = true)
	@Nullable
	public static Tag get(ItemStack stack, String tag) {
		return /*verifyExistance(stack, tag) ? stack.getOrCreateTag().get(tag) :*/ null;
	}

	@Deprecated(forRemoval = true)
	public static boolean getBoolean(ItemStack stack, String tag, boolean defaultExpected) {
		return /*verifyExistance(stack, tag) ? stack.getOrCreateTag().getBoolean(tag) :*/ defaultExpected;
	}

	@Deprecated(forRemoval = true)
	public static byte getByte(ItemStack stack, String tag, byte defaultExpected) {
		return /*verifyExistance(stack, tag) ? stack.getOrCreateTag().getByte(tag) :*/ defaultExpected;
	}

	@Deprecated(forRemoval = true)
	public static short getShort(ItemStack stack, String tag, short defaultExpected) {
		return /*verifyExistance(stack, tag) ? stack.getOrCreateTag().getShort(tag) :*/ defaultExpected;
	}

	@Deprecated(forRemoval = true)
	public static int getInt(ItemStack stack, String tag, int defaultExpected) {
		return /*verifyExistance(stack, tag) ? stack.getOrCreateTag().getInt(tag) :*/ defaultExpected;
	}

	@Deprecated(forRemoval = true)
	public static int[] getIntArray(ItemStack stack, String tag) {
		return /*verifyExistance(stack, tag) ? stack.getOrCreateTag().getIntArray(tag) :*/ EMPTY_INT_ARRAY;
	}

	@Deprecated(forRemoval = true)
	public static long getLong(ItemStack stack, String tag, long defaultExpected) {
		return /*verifyExistance(stack, tag) ? stack.getOrCreateTag().getLong(tag) :*/ defaultExpected;
	}

	@Deprecated(forRemoval = true)
	public static long[] getLongArray(ItemStack stack, String tag) {
		return /*verifyExistance(stack, tag) ? stack.getOrCreateTag().getLongArray(tag) :*/ EMPTY_LONG_ARRAY;
	}

	@Deprecated(forRemoval = true)
	public static float getFloat(ItemStack stack, String tag, float defaultExpected) {
		return /*verifyExistance(stack, tag) ? stack.getOrCreateTag().getFloat(tag) :*/ defaultExpected;
	}

	@Deprecated(forRemoval = true)
	public static double getDouble(ItemStack stack, String tag, double defaultExpected) {
		return /*verifyExistance(stack, tag) ? stack.getOrCreateTag().getDouble(tag) :*/ defaultExpected;
	}

	/**
	 * If nullifyOnFail is true it'll return null if it doesn't find any
	 * compounds, otherwise it'll return a new one.
	 **/
	@Deprecated(forRemoval = true)
	@Nullable
	@Contract("_, _, false -> !null")
	public static CompoundTag getCompound(ItemStack stack, String tag, boolean nullifyOnFail) {
		return /*verifyExistance(stack, tag) ? stack.getOrCreateTag().getCompound(tag) : */nullifyOnFail ? null : new CompoundTag();
	}

	@Deprecated(forRemoval = true)
	@Nullable
	@Contract("_, _, !null -> !null")
	public static String getString(ItemStack stack, String tag, String defaultExpected) {
		return /*verifyExistance(stack, tag) ? stack.getOrCreateTag().getString(tag) :*/ defaultExpected;
	}

	@Deprecated(forRemoval = true)
	@Nullable
	@Contract("_, _, _, false -> !null")
	public static ListTag getList(ItemStack stack, String tag, int objtype, boolean nullifyOnFail) {
		return /*verifyExistance(stack, tag) ? stack.getOrCreateTag().getList(tag, objtype) :*/ nullifyOnFail ? null : new ListTag();
	}

	// OTHER ///////////////////////////////////////////////////////////////////

	/**
	 * Returns the fullness of the mana item:
	 * 0 if empty, 1 if partially full, 2 if full.
	 */
	public static int getFullness(ManaItem item) {
		int mana = item.getMana();
		if (mana == 0) {
			return 0;
		} else if (mana == item.getMaxMana()) {
			return 2;
		} else {
			return 1;
		}
	}

	public static ItemStack duplicateAndClearMana(ItemStack stack) {
		ItemStack copy = stack.copy();
		ManaItem manaItem = XplatAbstractions.INSTANCE.findManaItem(copy);
		if (manaItem != null) {
			manaItem.addMana(-manaItem.getMana());
		}
		return copy;
	}

	/**
	 * Checks if two items are the same and have the same NBT. If they are `IManaItems`, their mana property is matched
	 * on whether they are empty, partially full, or full.
	 */
	public static boolean matchTagAndManaFullness(ItemStack stack1, ItemStack stack2) {
		if (!ItemStack.isSameItem(stack1, stack2)) {
			return false;
		}
		ManaItem manaItem1 = XplatAbstractions.INSTANCE.findManaItem(stack1);
		ManaItem manaItem2 = XplatAbstractions.INSTANCE.findManaItem(stack2);
		if (manaItem1 != null && manaItem2 != null) {
			if (getFullness(manaItem1) != getFullness(manaItem2)) {
				return false;
			} else {
				return ItemStack.matches(duplicateAndClearMana(stack1), duplicateAndClearMana(stack2));
			}
		}
		return ItemStack.isSameItemSameComponents(stack1, stack2);
	}

	/**
	 * Serializes the given stack such that {@link net.minecraft.world.item.crafting.ShapedRecipe#CODEC}
	 * would be able to read the result back
	 * TODO: probably not required anymore
	 */
	@Deprecated(forRemoval = true)
	public static JsonObject serializeStack(ItemStack stack) {
		CompoundTag nbt = new CompoundTag(); //stack.save(new CompoundTag());
		byte c = nbt.getByte("Count");
		if (c != 1) {
			nbt.putByte("count", c);
		}
		nbt.remove("Count");
		renameTag(nbt, "id", "item");
		renameTag(nbt, "tag", "nbt");
		Dynamic<Tag> dyn = new Dynamic<>(NbtOps.INSTANCE, nbt);
		return dyn.convert(JsonOps.INSTANCE).getValue().getAsJsonObject();
	}

	@Deprecated(forRemoval = true)
	public static void renameTag(CompoundTag nbt, String oldName, String newName) {
		Tag tag = nbt.get(oldName);
		if (tag != null) {
			nbt.remove(oldName);
			nbt.put(newName, tag);
		}
	}
}
