package vazkii.botania.fabric.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.data.BlockTagProvider;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FabricBlockTagProvider extends BlockTagProvider {
	public static final TagKey<Block> LAPIS_BLOCKS = blockTag(new ResourceLocation("c", "lapis_blocks"));
	public static final TagKey<Block> QUARTZ_BLOCKS = blockTag(new ResourceLocation("c", "quartz_blocks"));
	public static final TagKey<Block> MUSHROOMS = blockTag(new ResourceLocation("c", "mushrooms"));
	public static final TagKey<Block> GLASS = blockTag(new ResourceLocation("c", "glass"));
	public static final TagKey<Block> GLASS_ALT = blockTag(new ResourceLocation("c", "glass_blocks"));
	public static final TagKey<Block> GLASS_PANE = blockTag(new ResourceLocation("c", "glass_pane"));
	public static final TagKey<Block> GLASS_PANE_ALT = blockTag(new ResourceLocation("c", "glass_panes"));

	private static TagKey<Block> blockTag(ResourceLocation location) {
		return TagKey.create(Registries.BLOCK, location);
	}

	public FabricBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		super(packOutput, lookupProvider);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		tag(LAPIS_BLOCKS).add(Blocks.LAPIS_BLOCK);
		tag(QUARTZ_BLOCKS).add(
				BotaniaBlocks.darkQuartz, BotaniaBlocks.manaQuartz, BotaniaBlocks.blazeQuartz,
				BotaniaBlocks.lavenderQuartz, BotaniaBlocks.redQuartz, BotaniaBlocks.elfQuartz, BotaniaBlocks.sunnyQuartz
		);
		List.of(GLASS, GLASS_ALT).forEach(t -> tag(t).add(BotaniaBlocks.manaGlass, BotaniaBlocks.elfGlass, BotaniaBlocks.bifrostPerm));
		List.of(GLASS_PANE, GLASS_PANE_ALT).forEach(t -> tag(t).add(BotaniaBlocks.managlassPane, BotaniaBlocks.alfglassPane, BotaniaBlocks.bifrostPane));

		ColorHelper.supportedColors().forEach(color -> {
			this.tag(MUSHROOMS).add(BotaniaBlocks.getMushroom(color));
		});

		var vanillaTags = List.of(
				BlockTags.COAL_ORES,
				BlockTags.IRON_ORES,
				BlockTags.GOLD_ORES,
				BlockTags.LAPIS_ORES,
				BlockTags.REDSTONE_ORES,
				BlockTags.DIAMOND_ORES,
				BlockTags.COPPER_ORES,
				BlockTags.EMERALD_ORES
		);
		// We aren't calling vanilla's generation, so need to add dummy calls so that using them below doesn't error out.
		vanillaTags.forEach(this::tag);

		var oreTag = tag(XplatAbstractions.INSTANCE.getOreTag());
		vanillaTags.forEach(oreTag::addTag);
	}
}
