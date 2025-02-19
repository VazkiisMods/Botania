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
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class LooniumComponent extends SerializableComponent {
	public static final ResourceLocation ID = botaniaRL("loonium_drop");
	protected static final String TAG_TO_DROP = "toDrop";
	protected static final String TAG_OVERRIDE_DROP = "overrideDrop";
	protected static final String TAG_SLOW_DESPAWN = "slowDespawn";
	private ItemStack toDrop = ItemStack.EMPTY;
	private boolean overrideDrop;
	private boolean slowDespawn;

	public ItemStack getDrop() {
		return toDrop;
	}

	public void setDrop(ItemStack stack) {
		this.toDrop = stack;
	}

	public boolean isOverrideDrop() {
		return overrideDrop;
	}

	public void setOverrideDrop(boolean overrideDrop) {
		this.overrideDrop = overrideDrop;
	}

	public boolean isSlowDespawn() {
		return slowDespawn;
	}

	public void setSlowDespawn(boolean slowDespawn) {
		this.slowDespawn = slowDespawn;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return obj instanceof LooniumComponent component && ItemStack.matches(component.toDrop, toDrop)
				&& component.overrideDrop == overrideDrop && component.slowDespawn == slowDespawn;
	}

	@Override
	public int hashCode() {
		return Objects.hash(toDrop.hashCode(), overrideDrop, slowDespawn);
	}

	@Override
	public void readFromNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		if (tag.contains(TAG_TO_DROP)) {
			//todo setDrop(ItemStack.of(tag.getCompound(TAG_TO_DROP)));
		} else {
			setDrop(ItemStack.EMPTY);
		}
		if (tag.contains(TAG_OVERRIDE_DROP)) {
			setOverrideDrop(tag.getBoolean(TAG_OVERRIDE_DROP));
		}
		if (tag.contains(TAG_SLOW_DESPAWN)) {
			setSlowDespawn(tag.getBoolean(TAG_SLOW_DESPAWN));
		}
	}

	@Override
	public void writeToNbt(CompoundTag tag, HolderLookup.Provider registryLookup) {
		if (isOverrideDrop()) {
			if (!getDrop().isEmpty()) {
				//todo tag.put(TAG_TO_DROP, getDrop().save(new CompoundTag()));
			}
			tag.putBoolean(TAG_OVERRIDE_DROP, true);
		}
		if (isSlowDespawn()) {
			tag.putBoolean(TAG_SLOW_DESPAWN, true);
		}
	}
}
