package vazkii.botania.fabric.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.client.model.ManaBlasterBakedModel;
import vazkii.botania.xplat.ClientXplatAbstractions;

import java.util.*;
import java.util.function.Function;

/*
 * NB: We extend BlockModel only as an artifact of where we inject our mixin.
 * Pretty much all of the data of the superclass is ignored.
 */
public class FabricManaBlasterModel extends BlockModel {
	private final ResourceLocation gunNoClip, gunClip;

	public FabricManaBlasterModel(ResourceLocation gunNoClip, ResourceLocation gunClip) {
		super(null, Collections.emptyList(), Collections.emptyMap(), false, GuiLight.SIDE, ItemTransforms.NO_TRANSFORMS, Collections.emptyList());
		this.gunNoClip = gunNoClip;
		this.gunClip = gunClip;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return List.of(this.gunNoClip, this.gunClip);
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter) {
		modelGetter.apply(this.gunNoClip).resolveParents(modelGetter);
		modelGetter.apply(this.gunClip).resolveParents(modelGetter);
	}

	@Nullable
	@Override
	public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter,
			ModelState state) {

		return ManaBlasterBakedModel.create(
				baker,
				this.gunNoClip, this.gunClip,
				state);
	}

	@Nullable
	public static FabricManaBlasterModel hookModelLoad(JsonElement jsonElement) {
		JsonObject json = jsonElement.getAsJsonObject();
		JsonElement loader = json.get("loader");
		if (loader != null && loader.isJsonPrimitive()
				&& loader.getAsString().equals(ClientXplatAbstractions.MANA_GUN_MODEL_LOADER_ID.toString())) {
			return new FabricManaBlasterModel(
					ResourceLocation.parse(GsonHelper.getAsString(json, "gun_noclip")),
					ResourceLocation.parse(GsonHelper.getAsString(json, "gun_clip"))
			);
		}
		return null;
	}
}
