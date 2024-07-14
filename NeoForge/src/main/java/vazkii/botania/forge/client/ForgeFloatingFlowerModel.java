package vazkii.botania.forge.client;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
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
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import vazkii.botania.api.BotaniaAPIClient;
import vazkii.botania.api.block.FloatingFlower;
import vazkii.botania.api.block_entity.SpecialFlowerBlockEntity;
import vazkii.botania.common.block.block_entity.FloatingFlowerBlockEntity;

import java.util.*;
import java.util.function.Function;

public class ForgeFloatingFlowerModel implements IUnbakedGeometry<ForgeFloatingFlowerModel> {
	public static final ModelProperty<FloatingFlower> FLOATING_PROPERTY = new ModelProperty<>();
	private final UnbakedModel unbakedFlower;
	private final Map<FloatingFlower.IslandType, UnbakedModel> unbakedIslands = new HashMap<>();

	private ForgeFloatingFlowerModel(UnbakedModel flower) {
		this.unbakedFlower = flower;
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
		this.unbakedFlower.resolveParents(modelGetter);
		for (var e : BotaniaAPIClient.instance().getRegisteredIslandTypeModels().entrySet()) {
			UnbakedModel islandModel = modelGetter.apply(e.getValue());
			islandModel.resolveParents(modelGetter);
			this.unbakedIslands.put(e.getKey(), islandModel);
		}
	}

	@Nullable
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter,
			ModelState transform, ItemOverrides overrides, ResourceLocation name) {
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
		BakedModel bakedFlower = unbakedFlower.bake(baker, spriteGetter, newTransform, name);

		Map<FloatingFlower.IslandType, BakedModel> bakedIslands = new HashMap<>();
		for (Map.Entry<FloatingFlower.IslandType, UnbakedModel> e : unbakedIslands.entrySet()) {
			BakedModel bakedIsland = e.getValue().bake(baker, spriteGetter, transform, name);
			bakedIslands.put(e.getKey(), bakedIsland);
		}
		return new Baked(bakedFlower, bakedIslands);
	}

	@SuppressWarnings("deprecation") // shut up forge
	public static class Baked implements BakedModel {
		private final BakedModel flower;
		private final Map<FloatingFlower.IslandType, BakedModel> islands;

		Baked(BakedModel flower, Map<FloatingFlower.IslandType, BakedModel> islands) {
			this.flower = flower;
			this.islands = islands;
		}

		@Override
		public boolean useAmbientOcclusion() {
			return flower.useAmbientOcclusion();
		}

		@Override
		public boolean isGui3d() {
			return flower.isGui3d();
		}

		@Override
		public boolean usesBlockLight() {
			return flower.usesBlockLight();
		}

		@Override
		public boolean isCustomRenderer() {
			return false;
		}

		@NotNull
		@Override
		public TextureAtlasSprite getParticleIcon() {
			return flower.getParticleIcon();
		}

		@NotNull
		@Override
		public ItemOverrides getOverrides() {
			return flower.getOverrides();
		}

		@NotNull
		@Override
		public ItemTransforms getTransforms() {
			return flower.getTransforms();
		}

		@NotNull
		@Override
		public ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
			var be = level.getBlockEntity(pos);
			if (be instanceof FloatingFlowerBlockEntity floating) {
				return ModelData.builder()
						.with(FLOATING_PROPERTY, floating.getFloatingData())
						.build();
			} else if (be instanceof SpecialFlowerBlockEntity special && special.isFloating()) {
				return ModelData.builder()
						.with(FLOATING_PROPERTY, special.getFloatingData())
						.build();
			}
			return modelData;
		}

		@NotNull
		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
			// This shouldn't be called from anywhere on forge (blocks use the other overload,
			// items call getRenderPasses), but implement a default just in case
			return getQuads(state, side, rand, ModelData.EMPTY, RenderType.cutout());
		}

		@NotNull
		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side,
				@NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
			FloatingFlower.IslandType type = FloatingFlower.IslandType.GRASS;
			if (extraData.has(FLOATING_PROPERTY)) {
				type = extraData.get(FLOATING_PROPERTY).getIslandType();
			}

			if (renderType != null && state != null && !this.flower.getRenderTypes(state, rand, extraData).contains(renderType)) {
				return islands.get(type).getQuads(state, side, rand, ModelData.EMPTY, renderType);
			}
			List<BakedQuad> flower = this.flower.getQuads(state, side, rand, ModelData.EMPTY, renderType);
			List<BakedQuad> island = islands.get(type).getQuads(state, side, rand, ModelData.EMPTY, renderType);
			List<BakedQuad> ret = new ArrayList<>(flower.size() + island.size());
			ret.addAll(flower);
			ret.addAll(island);
			return ret;
		}

		@NotNull
		@Override
		public List<BakedModel> getRenderPasses(@NotNull ItemStack stack, boolean fabulous) {
			return List.of(flower, islands.get(FloatingFlower.IslandType.GRASS));
		}

		@NotNull
		@Override
		public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
			return ChunkRenderTypeSet.union(BakedModel.super.getRenderTypes(state, rand, data), this.flower.getRenderTypes(state, rand, data));
		}
	}

	public enum Loader implements IGeometryLoader<ForgeFloatingFlowerModel> {
		INSTANCE;

		@NotNull
		@Override
		public ForgeFloatingFlowerModel read(JsonObject model, JsonDeserializationContext ctx) {
			BlockModel flower = ctx.deserialize(model.getAsJsonObject("flower"), BlockModel.class);
			return new ForgeFloatingFlowerModel(flower);
		}
	}
}
