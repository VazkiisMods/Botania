package vazkii.botania.fabric.data;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.data.BiomeTagProvider;

import java.util.concurrent.CompletableFuture;

public class FabricBiomeTagProvider extends BiomeTagProvider {
	public FabricBiomeTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider);
	}

	//TODO maybe use v2 tags
	@Override
	protected void addTags(HolderLookup.Provider provider) {
		// need to do this so we can use them in addTag. It generates a dummy empty file,
		// but whatever.
		tag(ConventionalBiomeTags.IS_DESERT);
		tag(ConventionalBiomeTags.IS_SAVANNA);
		tag(ConventionalBiomeTags.IS_FOREST);
		tag(ConventionalBiomeTags.IS_MUSHROOM);
		tag(ConventionalBiomeTags.IS_UNDERGROUND);
		tag(ConventionalBiomeTags.IS_BADLANDS);
		tag(ConventionalBiomeTags.IS_MOUNTAIN);
		tag(ConventionalBiomeTags.IS_PLAINS);
		tag(ConventionalBiomeTags.IS_BEACH);
		tag(ConventionalBiomeTags.IS_SWAMP);
		tag(ConventionalBiomeTags.IS_JUNGLE);
		tag(ConventionalBiomeTags.IS_CONIFEROUS_TREE);
		tag(ConventionalBiomeTags.IS_COLD);
		tag(ConventionalBiomeTags.IS_SNOWY);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_DESERT_BONUS).addTag(ConventionalBiomeTags.IS_DESERT).addTag(ConventionalBiomeTags.IS_SAVANNA);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_FOREST_BONUS).addTag(ConventionalBiomeTags.IS_FOREST);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_FUNGAL_BONUS).addTag(ConventionalBiomeTags.IS_MUSHROOM).addTag(ConventionalBiomeTags.IS_UNDERGROUND);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_MESA_BONUS).addTag(ConventionalBiomeTags.IS_BADLANDS).addTag(ConventionalBiomeTags.IS_SAVANNA);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_MOUNTAIN_BONUS).addTag(ConventionalBiomeTags.IS_MOUNTAIN);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_PLAINS_BONUS).addTag(ConventionalBiomeTags.IS_PLAINS).addTag(ConventionalBiomeTags.IS_BEACH);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_SWAMP_BONUS).addTag(ConventionalBiomeTags.IS_SWAMP).addTag(ConventionalBiomeTags.IS_JUNGLE);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_TAIGA_BONUS).addTag(ConventionalBiomeTags.IS_CONIFEROUS_TREE).addTag(ConventionalBiomeTags.IS_COLD).addTag(ConventionalBiomeTags.IS_SNOWY);
	}
}
