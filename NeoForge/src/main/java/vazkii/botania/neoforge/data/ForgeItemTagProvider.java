package vazkii.botania.neoforge.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.common.lib.LibMisc;

import java.util.concurrent.CompletableFuture;

import static vazkii.botania.common.item.BotaniaItems.*;

public class ForgeItemTagProvider extends ItemTagsProvider {
	public ForgeItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider,
			CompletableFuture<TagsProvider.TagLookup<Block>> blockTagProvider, ExistingFileHelper helper) {
		super(packOutput, lookupProvider, blockTagProvider, LibMisc.MOD_ID, helper);
	}

	@Override
	public String getName() {
		return "Botania item tags (Forge-specific)";
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		this.tag(common("dusts/mana")).addTag(BotaniaTags.Items.DUSTS_MANA);
		this.tag(common("dusts")).addTag(common("dusts/mana"));

		this.tag(common("gems/dragonstone")).addTag(BotaniaTags.Items.GEMS_DRAGONSTONE);
		this.tag(common("gems/mana_diamond")).addTag(BotaniaTags.Items.GEMS_MANA_DIAMOND);
		this.tag(common("gems")).addTag(common("gems/dragonstone")).addTag(common("gems/mana_diamond"));

		this.tag(common("ingots/elementium")).addTag(BotaniaTags.Items.INGOTS_ELEMENTIUM);
		this.tag(common("ingots/manasteel")).addTag(BotaniaTags.Items.INGOTS_MANASTEEL);
		this.tag(common("ingots/terrasteel")).addTag(BotaniaTags.Items.INGOTS_TERRASTEEL);
		this.tag(Tags.Items.INGOTS).addTag(common("ingots/elementium"))
				.addTag(common("ingots/manasteel"))
				.addTag(common("ingots/terrasteel"));

		this.tag(common("nuggets/elementium")).addTag(BotaniaTags.Items.NUGGETS_ELEMENTIUM);
		this.tag(common("nuggets/manasteel")).addTag(BotaniaTags.Items.NUGGETS_MANASTEEL);
		this.tag(common("nuggets/terrasteel")).addTag(BotaniaTags.Items.NUGGETS_TERRASTEEL);
		this.tag(common("nuggets")).addTag(common("nuggets/elementium"))
				.addTag(common("nuggets/manasteel"))
				.addTag(common("nuggets/terrasteel"));

		this.copyToSameName(ForgeBlockTagProvider.ELEMENTIUM);
		this.copyToSameName(ForgeBlockTagProvider.MANASTEEL);
		this.copyToSameName(ForgeBlockTagProvider.TERRASTEEL);
		this.copyToSameName(ForgeBlockTagProvider.MANA_DIAMOND);
		this.copyToSameName(ForgeBlockTagProvider.DRAGONSTONE);
		this.copy(ForgeBlockTagProvider.MUSHROOMS, Tags.Items.MUSHROOMS);
		this.copy(Tags.Blocks.STORAGE_BLOCKS, Tags.Items.STORAGE_BLOCKS);
		this.copy(Tags.Blocks.GLASS_BLOCKS, Tags.Items.GLASS_BLOCKS);
		this.copy(Tags.Blocks.GLASS_PANES, Tags.Items.GLASS_PANES);

		this.generateToolTags();
		this.generateAccessoryTags();
	}

	private void generateToolTags() {
		this.tag(Tags.Items.TOOLS_SHEAR).add(manasteelShears, elementiumShears);
	}

	private void generateAccessoryTags() {
		tag(accessory("belt")).add(
				knockbackBelt, speedUpBelt, superTravelBelt, travelBelt
		);
		tag(accessory("body")).add(
				balanceCloak, holyCloak, invisibilityCloak, thirdEye, unholyCloak
		);
		tag(accessory("charm")).add(
				divaCharm, goddessCharm, monocle, tinyPlanet
		);
		tag(accessory("head")).add(flightTiara, itemFinder);
		tag(accessory("necklace")).add(
				bloodPendant, cloudPendant, icePendant, lavaPendant,
				superCloudPendant, superLavaPendant
		);
		tag(accessory("ring")).add(
				auraRing, auraRingGreater, dodgeRing, lokiRing, magnetRing, magnetRingGreater,
				manaRing, manaRingGreater, miningRing, odinRing, pixieRing, reachRing,
				swapRing, thorRing, waterRing
		);
		tag(accessory("curio")).add(
				blackBowtie, blackTie,
				redGlasses, puffyScarf,
				engineerGoggles, eyepatch,
				wickedEyepatch, redRibbons,
				pinkFlowerBud, polkaDottedBows,
				blueButterfly, catEars,
				witchPin, devilTail,
				kamuiEye, googlyEyes,
				fourLeafClover, clockEye,
				unicornHorn, devilHorns,
				hyperPlus, botanistEmblem,
				ancientMask, eerieMask,
				alienAntenna, anaglyphGlasses,
				orangeShades, grouchoGlasses,
				thickEyebrows, lusitanicShield,
				tinyPotatoMask, questgiverMark,
				thinkingHand
		);
	}

	private static TagKey<Item> accessory(String name) {
		return ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", name));
	}

	private static TagKey<Item> common(String name) {
		return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", name));
	}

	private void copyToSameName(TagKey<Block> source) {
		this.copy(source, ItemTags.create(source.location()));
	}
}
