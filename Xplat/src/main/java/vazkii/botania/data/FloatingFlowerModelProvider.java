/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.block.decor.FloatingFlowerBlock;
import vazkii.botania.common.lib.LibMisc;
import vazkii.botania.xplat.ClientXplatAbstractions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class FloatingFlowerModelProvider implements DataProvider {
	private final PackOutput packOutput;

	public FloatingFlowerModelProvider(PackOutput packOutput) {
		this.packOutput = packOutput;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		List<Tuple<String, JsonElement>> jsons = new ArrayList<>();
		for (Block b : BuiltInRegistries.BLOCK) {
			ResourceLocation id = BuiltInRegistries.BLOCK.getKey(b);
			if (LibMisc.MOD_ID.equals(id.getNamespace()) && b instanceof FloatingFlowerBlock) {
				String name = id.getPath();
				String nonFloat;
				if (name.endsWith("_floating_flower")) {
					nonFloat = name.replace("_floating_flower", "_mystical_flower");
				} else {
					nonFloat = name.replace("floating_", "");
				}

				JsonObject obj = new JsonObject();
				obj.addProperty("parent", "minecraft:block/block");
				obj.addProperty("loader", ClientXplatAbstractions.FLOATING_FLOWER_MODEL_LOADER_ID.toString());
				JsonObject flower = new JsonObject();
				flower.addProperty("parent", ResourcesLib.PREFIX_MOD + "block/" + nonFloat);
				obj.add("flower", flower);
				jsons.add(new Tuple<>(name, obj));
			}
		}
		List<CompletableFuture<?>> output = new ArrayList<>();
		PackOutput.PathProvider blocks = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/block");
		PackOutput.PathProvider items = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");
		for (Tuple<String, JsonElement> pair : jsons) {
			output.add(DataProvider.saveStable(cache, pair.getB(), blocks.json(botaniaRL(pair.getA()))));
			output.add(DataProvider.saveStable(cache, pair.getB(), items.json(botaniaRL(pair.getA()))));
		}

		return CompletableFuture.allOf(output.toArray(CompletableFuture[]::new));
	}

	@NotNull
	@Override
	public String getName() {
		return "Botania floating flower models";
	}
}
