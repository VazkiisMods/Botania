package vazkii.botania.forge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.common.block.PlatformBlock;
import vazkii.botania.common.block.block_entity.PlatformBlockEntity;

import java.util.List;

public class ForgePlatformModel extends BakedModelWrapper<BakedModel> {
	public static final ModelProperty<PlatformBlockEntity.PlatformData> PROPERTY = new ModelProperty<>();

	public ForgePlatformModel(BakedModel originalModel) {
		super(originalModel);
	}

	@NotNull
	@Override
	public ModelData getModelData(@NotNull BlockAndTintGetter world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData tileData) {
		if (world.getBlockEntity(pos) instanceof PlatformBlockEntity platform) {
			return ModelData.builder()
					.with(PROPERTY, new PlatformBlockEntity.PlatformData(platform))
					.build();
		}
		return tileData;
	}

	@NotNull
	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side,
			@NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
		var data = extraData.get(PROPERTY);
		if (state == null || !(state.getBlock() instanceof PlatformBlock) || data == null) {
			return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper()
					.getModelManager().getMissingModel().getQuads(state, side, rand, extraData, renderType);
		}

		BlockState heldState = data.state();

		if (heldState == null) {
			// No camo
			return super.getQuads(state, side, rand, extraData, renderType);
		} else {
			BakedModel model = Minecraft.getInstance().getBlockRenderer()
					.getBlockModelShaper().getBlockModel(heldState);
			return model.getQuads(heldState, side, rand, ModelData.EMPTY, renderType);
		}
	}

	@NotNull
	@Override
	public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData extraData) {
		var data = extraData.get(PROPERTY);
		if (!(state.getBlock() instanceof PlatformBlock) || data == null) {
			return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper()
					.getModelManager().getMissingModel().getRenderTypes(state, rand, extraData);
		}

		BlockState heldState = data.state();
		if (heldState == null) {
			return super.getRenderTypes(state, rand, extraData);
		} else {
			BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(heldState);
			return model.getRenderTypes(heldState, rand, ModelData.EMPTY);
		}
	}
}
