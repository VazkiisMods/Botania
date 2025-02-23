/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 */

package vazkii.botania.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import java.util.*;

import io.netty.buffer.ByteBuf;

public class StoredIds {
	public static final StoredIds EMPTY = new StoredIds(new ResourceLocation[0]);
	public static final int MAX_SLOTS = 128; // happens to be a single byte if written as VAR_INT

	public static final Codec<StoredIds> CODEC = Slot.CODEC.sizeLimitedListOf(MAX_SLOTS)
			.xmap(StoredIds::fromSlots, StoredIds::getPopulatedSlots);
	public static final StreamCodec<ByteBuf, StoredIds> STREAM_CODEC = Slot.STREAM_CODEC
			.apply(ByteBufCodecs.list(MAX_SLOTS))
			.map(StoredIds::fromSlots, StoredIds::getPopulatedSlots);

	private static StoredIds fromSlots(List<Slot> slots) {
		OptionalInt lastSlotIndex = slots.stream().mapToInt(Slot::index).max();
		if (lastSlotIndex.isEmpty()) {
			return EMPTY;
		}

		var ids = new ResourceLocation[lastSlotIndex.getAsInt() + 1];
		for (Slot slot : slots) {
			ids[slot.index] = slot.value;
		}

		return new StoredIds(ids);
	}

	private static List<Slot> getPopulatedSlots(StoredIds storedIds) {
		var ids = storedIds.ids;
		List<Slot> slots = new ArrayList<>(ids.length);
		for (int i = 0; i < ids.length; i++) {
			if (ids[i] != null) {
				slots.add(new Slot(i, ids[i]));
			}
		}
		return slots;
	}

	@Nullable
	private final ResourceLocation[] ids;
	private final int cachedHashCode;

	private StoredIds(ResourceLocation[] idsToStore) {
		ids = idsToStore;
		cachedHashCode = Arrays.hashCode(idsToStore);
	}

	public StoredIds store(int slot, @Nullable ResourceLocation newId) {
		if (slot < 0 || slot >= MAX_SLOTS) {
			throw new IndexOutOfBoundsException("Slot index must be positive and less than " + MAX_SLOTS + ", but was " + slot);
		}

		if (slot < ids.length && Objects.equals(ids[slot], newId)) {
			return this;
		}

		int newLength = getNewLength(slot, newId);

		if (newLength == 0) {
			return EMPTY;
		}

		var idsCopy = Arrays.copyOf(ids, newLength);
		if (slot < newLength) {
			idsCopy[slot] = newId;
		}

		return new StoredIds(idsCopy);
	}

	private int getNewLength(int slot, @Nullable ResourceLocation newId) {
		if (newId != null) {
			// setting a slot, array might need to grow
			return Math.max(ids.length, slot + 1);

		}

		if (slot == ids.length - 1) {
			// clearing last entry, trim array to have no empty slots at the end
			int newLastSlot = slot;
			//noinspection StatementWithEmptyBody
			while (--newLastSlot >= 0 && ids[newLastSlot] == null) {}
			return newLastSlot + 1;

		}

		// clearing a slot that is not the last one, array will keep its length
		return ids.length;
	}

	@Nullable
	public ResourceLocation getSlot(int slot) {
		return slot >= 0 && slot < ids.length ? ids[slot] : null;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof StoredIds that)) {
			return false;
		}
		return Arrays.equals(ids, that.ids);
	}

	@Override
	public int hashCode() {
		return cachedHashCode;
	}

	public record Slot(int index, ResourceLocation value) {
		public static final Codec<Slot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.intRange(0, MAX_SLOTS).fieldOf("index").forGetter(Slot::index),
				ResourceLocation.CODEC.fieldOf("value").forGetter(Slot::value)
		).apply(instance, Slot::new));

		public static final StreamCodec<ByteBuf, Slot> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, Slot::index,
				ResourceLocation.STREAM_CODEC, Slot::value,
				Slot::new
		);
	}
}
