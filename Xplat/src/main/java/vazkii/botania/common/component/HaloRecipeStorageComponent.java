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

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Map;

import io.netty.buffer.ByteBuf;

public class HaloRecipeStorageComponent {
	public static final Codec<HaloRecipeStorageComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("slots").forGetter(HaloRecipeStorageComponent::getNumSlots),
			Codec.unboundedMap(Codec.INT, ResourceLocation.CODEC).fieldOf("recipes").forGetter(HaloRecipeStorageComponent::getPopulatedSlots)
	).apply(instance, HaloRecipeStorageComponent::create));

	public static final StreamCodec<ByteBuf, HaloRecipeStorageComponent> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, HaloRecipeStorageComponent::getNumSlots,
			ByteBufCodecs.map(Int2ObjectArrayMap::new, ByteBufCodecs.VAR_INT, ResourceLocation.STREAM_CODEC), HaloRecipeStorageComponent::getPopulatedSlots,
			HaloRecipeStorageComponent::create
	);

	private static HaloRecipeStorageComponent create(Integer numSlots, Map<Integer, ResourceLocation> recipesMap) {
		var component = new HaloRecipeStorageComponent(numSlots);
		recipesMap.forEach((slot, id) -> {
			if (slot >= 0 && slot < numSlots) {
				component.setSlot(slot, id);
			}
		});
		return component;
	}

	private static Int2ObjectMap<ResourceLocation> getPopulatedSlots(HaloRecipeStorageComponent component) {
		var map = new Int2ObjectArrayMap<ResourceLocation>(component.getNumSlots());
		for (int slot = 0; slot < component.getNumSlots(); slot++) {
			var id = component.getSlot(slot);
			if (id != null) {
				map.put(slot, id);
			}
		}
		return map;
	}

	@Nullable
	private final ResourceLocation[] recipeIds;

	public HaloRecipeStorageComponent(int slots) {
		this.recipeIds = new ResourceLocation[slots];
	}

	public int getNumSlots() {
		return recipeIds.length;
	}

	public void setSlot(int slot, @Nullable ResourceLocation recipeId) {
		recipeIds[slot] = recipeId;
	}

	@Nullable
	public ResourceLocation getSlot(int slot) {
		return recipeIds[slot];
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof HaloRecipeStorageComponent that)) {
			return false;
		}
		return Arrays.equals(recipeIds, that.recipeIds);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(recipeIds);
	}
}
