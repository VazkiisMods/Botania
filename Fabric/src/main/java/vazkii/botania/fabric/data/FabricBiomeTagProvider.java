package vazkii.botania.fabric.data;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
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
		tag(ConventionalBiomeTags.DESERT);
		tag(ConventionalBiomeTags.SAVANNA);
		tag(ConventionalBiomeTags.FOREST);
		tag(ConventionalBiomeTags.MUSHROOM);
		tag(ConventionalBiomeTags.UNDERGROUND);
		tag(ConventionalBiomeTags.MESA);
		tag(ConventionalBiomeTags.MOUNTAIN);
		tag(ConventionalBiomeTags.PLAINS);
		tag(ConventionalBiomeTags.BEACH);
		tag(ConventionalBiomeTags.SWAMP);
		tag(ConventionalBiomeTags.JUNGLE);
		tag(ConventionalBiomeTags.TREE_CONIFEROUS);
		tag(ConventionalBiomeTags.CLIMATE_COLD);
		tag(ConventionalBiomeTags.SNOWY);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_DESERT_BONUS).addTag(ConventionalBiomeTags.DESERT).addTag(ConventionalBiomeTags.SAVANNA);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_FOREST_BONUS).addTag(ConventionalBiomeTags.FOREST);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_FUNGAL_BONUS).addTag(ConventionalBiomeTags.MUSHROOM).addTag(ConventionalBiomeTags.UNDERGROUND);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_MESA_BONUS).addTag(ConventionalBiomeTags.MESA).addTag(ConventionalBiomeTags.SAVANNA);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_MOUNTAIN_BONUS).addTag(ConventionalBiomeTags.MOUNTAIN);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_PLAINS_BONUS).addTag(ConventionalBiomeTags.PLAINS).addTag(ConventionalBiomeTags.BEACH);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_SWAMP_BONUS).addTag(ConventionalBiomeTags.SWAMP).addTag(ConventionalBiomeTags.JUNGLE);
		tag(BotaniaTags.Biomes.MARIMORPHOSIS_TAIGA_BONUS).addTag(ConventionalBiomeTags.TREE_CONIFEROUS).addTag(ConventionalBiomeTags.CLIMATE_COLD).addTag(ConventionalBiomeTags.SNOWY);
	}
}
