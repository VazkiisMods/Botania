package vazkii.botania.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerPotBlock;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.lib.LibBlockNames;
import vazkii.botania.common.lib.LibMisc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class PottedPlantModelProvider implements DataProvider {
	private final PackOutput packOutput;

	public PottedPlantModelProvider(PackOutput packOutput) {
		this.packOutput = packOutput;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cache) {
		List<Tuple<String, JsonElement>> jsons = new ArrayList<>();
		for (Block b : BuiltInRegistries.BLOCK) {
			ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(b);
			if (LibMisc.MOD_ID.equals(blockId.getNamespace()) && b instanceof FlowerPotBlock) {
				String name = blockId.getPath();
				String nonPotted = name.replace(LibBlockNames.POTTED_PREFIX, "");

				JsonObject obj = new JsonObject();
				obj.addProperty("parent", "minecraft:block/flower_pot_cross");
				JsonObject textures = new JsonObject();
				textures.addProperty("plant", ResourcesLib.PREFIX_MOD + "block/" + nonPotted);
				obj.add("textures", textures);
				jsons.add(new Tuple<>(name, obj));
			}
		}
		List<CompletableFuture<?>> output = new ArrayList<>();
		PackOutput.PathProvider blocks = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/block");
		for (Tuple<String, JsonElement> pair : jsons) {
			output.add(DataProvider.saveStable(cache, pair.getB(), blocks.json(botaniaRL(pair.getA()))));
		}

		return CompletableFuture.allOf(output.toArray(CompletableFuture[]::new));
	}

	static MultiVariantGenerator createSimpleBlock(Block block, ResourceLocation resourceLocation) {
		return MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, resourceLocation));
	}

	@NotNull
	@Override
	public String getName() {
		return "Botania potted plant models";
	}
}
