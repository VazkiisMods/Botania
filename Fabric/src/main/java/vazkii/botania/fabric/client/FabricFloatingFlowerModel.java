/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import vazkii.botania.api.BotaniaAPIClient;
import vazkii.botania.api.block.FloatingFlower;
import vazkii.botania.xplat.ClientXplatAbstractions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/*
 * NB: We extend BlockModel only as an artifact of where we inject our mixin.
 * Pretty much all of the data of the superclass is ignored.
 */
public class FabricFloatingFlowerModel extends BlockModel {
	private final UnbakedModel unbakedFlower;
	private final Map<FloatingFlower.IslandType, UnbakedModel> unbakedIslands = new HashMap<>();

	private FabricFloatingFlowerModel(UnbakedModel flower) {
		super(null, Collections.emptyList(), Collections.emptyMap(), false, GuiLight.SIDE, ItemTransforms.NO_TRANSFORMS, Collections.emptyList());
		this.unbakedFlower = flower;
	}

	@NotNull
	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public void resolveParents(@NotNull Function<ResourceLocation, UnbakedModel> modelGetter) {
		this.unbakedFlower.resolveParents(modelGetter);
		for (var e : BotaniaAPIClient.instance().getRegisteredIslandTypeModels().entrySet()) {
			UnbakedModel islandModel = modelGetter.apply(e.getValue());
			islandModel.resolveParents(modelGetter);
			this.unbakedIslands.put(e.getKey(), islandModel);
		}
	}

	@NotNull
	@Override
	public BakedModel bake(ModelBaker baker, BlockModel model, Function<Material, TextureAtlasSprite> spriteGetter,
			ModelState transform, boolean guiLight3d) {
		final Transformation moveFlower = new Transformation(new Vector3f(0F, 0.2F, 0F), null, new Vector3f(0.5F, 0.5F, 0.5F), null);
		Transformation mul = moveFlower.compose(transform.getRotation());
		ModelState newTransform = new ModelState() {
			@Override
			public Transformation getRotation() {
				return mul;
			}

			@Override
			public boolean isUvLocked() {
				return transform.isUvLocked();
			}
		};
		BakedModel bakedFlower = unbakedFlower.bake(baker, spriteGetter, newTransform);

		Map<FloatingFlower.IslandType, BakedModel> bakedIslands = new HashMap<>();
		for (Map.Entry<FloatingFlower.IslandType, UnbakedModel> e : unbakedIslands.entrySet()) {
			BakedModel bakedIsland = e.getValue().bake(baker, spriteGetter, transform);
			bakedIslands.put(e.getKey(), bakedIsland);
		}
		return new Baked(bakedFlower, bakedIslands);
	}

	public static class Baked extends ForwardingBakedModel {
		private final Map<FloatingFlower.IslandType, BakedModel> islands;

		Baked(BakedModel flower, Map<FloatingFlower.IslandType, BakedModel> islands) {
			this.wrapped = flower;
			this.islands = islands;
		}

		@NotNull
		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
			List<BakedQuad> flower = wrapped.getQuads(null, null, rand);
			List<BakedQuad> island = islands.get(FloatingFlower.IslandType.GRASS).getQuads(null, null, rand);
			List<BakedQuad> ret = new ArrayList<>(flower.size() + island.size());
			ret.addAll(flower);
			ret.addAll(island);
			return ret;
		}

		@Override
		public boolean isVanillaAdapter() {
			return false;
		}

		@Override
		public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
			wrapped.emitItemQuads(stack, randomSupplier, context);
			islands.get(FloatingFlower.IslandType.GRASS).emitItemQuads(stack, randomSupplier, context);
		}

		@Override
		public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
			wrapped.emitBlockQuads(blockView, state, pos, randomSupplier, context);
			Object data = blockView.getBlockEntityRenderData(pos);
			islands.get(data instanceof FloatingFlower.IslandType type ? type : FloatingFlower.IslandType.GRASS)
					.emitBlockQuads(blockView, state, pos, randomSupplier, context);
		}
	}

	@Nullable
	public static FabricFloatingFlowerModel hookModelLoad(JsonElement jsonElement, JsonDeserializationContext context) {
		JsonObject json = jsonElement.getAsJsonObject();
		JsonElement loader = json.get("loader");
		if (loader != null && loader.isJsonPrimitive()
				&& loader.getAsString().equals(ClientXplatAbstractions.FLOATING_FLOWER_MODEL_LOADER_ID.toString())) {
			BlockModel flowerModel = context.deserialize(json.getAsJsonObject("flower"), BlockModel.class);
			return new FabricFloatingFlowerModel(flowerModel);
		}
		return null;
	}
}
