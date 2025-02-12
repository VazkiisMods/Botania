package vazkii.botania.common.helper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.mixin.BundleItemAccessor;

import java.util.ArrayList;
import java.util.List;

public class FilterHelper {

	public static final String ITEMS_TAG = "Items";
	public static final String TAG_EXPAND_FILTERS = "expandFilters";

	/**
	 * Expands the given filter item into the list of filter items it represents. This is NOT recursive.
	 * Non-empty container items stand for their contents, not for themselves.
	 */
	public static List<ItemStack> getFilterItems(@Nullable ItemStack filterStack) {
		if (filterStack == null) {
			return List.of();
		}
		if (filterStack.is(Items.BUNDLE)) {
			// get bundle content
			List<ItemStack> bundledItems = BundleItemAccessor.call_getContents(filterStack).toList();
			if (!bundledItems.isEmpty()) {
				return bundledItems;
			}
		} else {
			// BlockItems (especially shulker boxes) can contain BlockEntity data, which may include an inventory.
			// Otherwise, items may represent an inventory themselves (e.g. Flower Pouch or Bauble Box)
			CompoundTag tag = filterStack.getItem() instanceof BlockItem
					? BlockItem.getBlockEntityData(filterStack)
					: filterStack.getTag();
			if (tag != null && tag.contains(ITEMS_TAG, Tag.TAG_LIST)) {
				// item might contain an inventory
				List<ItemStack> items = getItemStacks(tag);
				if (items != null) {
					return items;
				}
			}
		}
		return List.of(filterStack);
	}

	@Nullable
	private static List<ItemStack> getItemStacks(CompoundTag tag) {
		try {
			ListTag contents = tag.getList(ITEMS_TAG, CompoundTag.TAG_COMPOUND);
			List<ItemStack> items = new ArrayList<>(contents.size());
			for (int i = 0; i < contents.size(); i++) {
				CompoundTag entry = contents.getCompound(i);
				ItemStack stack = ItemStack.of(entry);
				if (!stack.isEmpty()) {
					items.add(stack);
				}
			}
			if (!items.isEmpty()) {
				return items;
			}
		} catch (ClassCastException ce) {
			// apparently not a typical container inventory
		}
		return null;
	}

	public record WeightedItemStack(ItemStack stack, Weight weight) implements WeightedEntry {
		public static WeightedItemStack of(ItemStack stack, int weight) {
			return new WeightedItemStack(stack, Weight.of(weight));
		}

		@NotNull
		@Override
		public Weight getWeight() {
			return weight;
		}
	}
}
