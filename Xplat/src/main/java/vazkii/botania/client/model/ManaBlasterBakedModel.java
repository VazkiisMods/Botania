/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.model;

import com.google.common.base.Preconditions;
import com.mojang.math.Transformation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import vazkii.botania.common.helper.VecHelper;
import vazkii.botania.common.item.ManaBlasterItem;

import java.util.*;

public class ManaBlasterBakedModel extends DelegatedModel {
	// key is (lens, hasClip). If no lens, the Item is null
	private final Map<Pair<Item, Boolean>, BakedModel> models;

	private ManaBlasterBakedModel(Map<Pair<Item, Boolean>, BakedModel> models) {
		super(models.get(Pair.of(null, false)));
		this.models = models;
	}

	public static ManaBlasterBakedModel create(ModelBaker baker,
			ResourceLocation gunNoClip, ResourceLocation gunClip,
			ModelState state) {
		BakedModel gunNoClipModel = Preconditions.checkNotNull(baker.bake(gunNoClip, state));
		BakedModel gunClipModel = Preconditions.checkNotNull(baker.bake(gunClip, state));
		Map<Pair<Item, Boolean>, BakedModel> models = new HashMap<>();
		models.put(Pair.of(null, false), gunNoClipModel);
		models.put(Pair.of(null, true), gunClipModel);

		for (var item : BuiltInRegistries.ITEM) {
			var lens = item.getDefaultInstance();
			if (ManaBlasterItem.isValidLens(lens)) {
				models.put(Pair.of(item, false), new ManaBlasterBakedModel.CompositeBakedModel(baker, lens, gunNoClipModel));
				models.put(Pair.of(item, true), new ManaBlasterBakedModel.CompositeBakedModel(baker, lens, gunClipModel));
			}
		}
		return new ManaBlasterBakedModel(models);
	}

	private final ItemOverrides itemHandler = new ItemOverrides() {
		@NotNull
		@Override
		public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel worldIn, @Nullable LivingEntity entityIn, int seed) {
			boolean clip = ManaBlasterItem.hasClip(stack);

			ItemStack lens = ManaBlasterItem.getLens(stack);
			Pair<Item, Boolean> key = Pair.of(lens.isEmpty() ? null : lens.getItem(), clip);

			return ManaBlasterBakedModel.this.models.getOrDefault(key,
					Minecraft.getInstance().getModelManager().getMissingModel());
		}
	};

	@NotNull
	@Override
	public ItemOverrides getOverrides() {
		return itemHandler;
	}

	@NotNull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
		return originalModel.getQuads(state, side, rand);
	}

	private static class CompositeBakedModel extends DelegatedModel {
		private final BakedModel lensModel;

		CompositeBakedModel(ModelBaker baker, ItemStack lens, BakedModel gun) {
			super(gun);
			ResourceLocation lensId = BuiltInRegistries.ITEM.getKey(lens.getItem());
			ModelState transform = new ModelState() {
				@Override
				public Transformation getRotation() {
					return new Transformation(new Vector3f(-0.4F, 0.2F, 0.0F), VecHelper.rotateY(90), new Vector3f(0.625F, 0.625F, 0.625F), null);
				}
			};
			this.lensModel = baker.bake(lensId, transform); //todo confirm this is correct
		}

		@NotNull
		@Override
		public List<BakedQuad> getQuads(BlockState state, Direction face, @NotNull RandomSource rand) {
			List<BakedQuad> gunQuads = this.originalModel.getQuads(state, face, rand);
			List<BakedQuad> lensQuads = this.lensModel.getQuads(state, face, rand);
			List<BakedQuad> ret = new ArrayList<>(gunQuads.size() + lensQuads.size());
			ret.addAll(gunQuads);
			ret.addAll(lensQuads);
			return ret;
		}
	}

}
