package vazkii.botania.forge.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.common.lib.LibMisc;

import java.util.concurrent.CompletableFuture;

public class ForgeBlockTagProvider extends IntrinsicHolderTagsProvider<Block> {
	public static final TagKey<Block> MUSHROOMS = forge("mushrooms");
	public static final TagKey<Block> ELEMENTIUM = forge("storage_blocks/elementium");
	public static final TagKey<Block> MANASTEEL = forge("storage_blocks/manasteel");
	public static final TagKey<Block> TERRASTEEL = forge("storage_blocks/terrasteel");

	public ForgeBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider,
			ExistingFileHelper existingFileHelper) {
		super(output, Registries.BLOCK, provider, (block) -> block.builtInRegistryHolder().key(), LibMisc.MOD_ID, existingFileHelper);
	}

	@Override
	public String getName() {
		return "Botania block tags (Forge-specific)";
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(Tags.Blocks.STORAGE_BLOCKS_QUARTZ).add(
				BotaniaBlocks.darkQuartz, BotaniaBlocks.manaQuartz, BotaniaBlocks.blazeQuartz,
				BotaniaBlocks.lavenderQuartz, BotaniaBlocks.redQuartz, BotaniaBlocks.elfQuartz, BotaniaBlocks.sunnyQuartz
		);

		ColorHelper.supportedColors().forEach(color -> {
			this.tag(MUSHROOMS).add(BotaniaBlocks.getMushroom(color));
		});

		tag(TagKey.create(Registries.BLOCK, new ResourceLocation("buzzier_bees", "flower_blacklist")))
				.addTag(BotaniaTags.Blocks.MYSTICAL_FLOWERS)
				.addTag(BotaniaTags.Blocks.SPECIAL_FLOWERS);

		tag(ELEMENTIUM).addTag(BotaniaTags.Blocks.BLOCKS_ELEMENTIUM);
		tag(MANASTEEL).addTag(BotaniaTags.Blocks.BLOCKS_MANASTEEL);
		tag(TERRASTEEL).addTag(BotaniaTags.Blocks.BLOCKS_TERRASTEEL);
		tag(Tags.Blocks.STORAGE_BLOCKS).addTag(ELEMENTIUM).addTag(MANASTEEL).addTag(TERRASTEEL);
		tag(Tags.Blocks.GLASS).add(BotaniaBlocks.manaGlass, BotaniaBlocks.elfGlass, BotaniaBlocks.bifrostPerm);
		tag(Tags.Blocks.GLASS_PANES).add(BotaniaBlocks.managlassPane, BotaniaBlocks.alfglassPane, BotaniaBlocks.bifrostPane);
	}

	private static TagKey<Block> forge(String name) {
		return TagKey.create(Registries.BLOCK, new ResourceLocation("forge", name));
	}
}
