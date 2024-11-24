/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

import vazkii.botania.common.advancements.*;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.block.block_entity.corporea.CorporeaIndexBlockEntity;
import vazkii.botania.common.block.flower.functional.LooniumBlockEntity;
import vazkii.botania.common.entity.BotaniaEntities;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.LexicaBotaniaItem;
import vazkii.botania.common.item.equipment.bauble.FlugelTiaraItem;
import vazkii.botania.common.lib.BotaniaTags;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class AdvancementProvider {
	public static net.minecraft.data.advancements.AdvancementProvider create(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
		return new net.minecraft.data.advancements.AdvancementProvider(packOutput, lookupProvider, List.of(new BotaniaStoryAdvancements(), new BotaniaChallengeAdvancements()));
	}

	public static class BotaniaStoryAdvancements implements AdvancementSubProvider {

		@Override
		public void generate(HolderLookup.Provider lookup, Consumer<Advancement> consumer) {
			var elvenLexiconUnlock = new CompoundTag();
			elvenLexiconUnlock.putBoolean(LexicaBotaniaItem.TAG_ELVEN_UNLOCK, true);
			InventoryChangeTrigger.TriggerInstance elvenLexicon = InventoryChangeTrigger.TriggerInstance.hasItems(
					ItemPredicate.Builder.item().of(BotaniaItems.lexicon).hasNbt(elvenLexiconUnlock).build()
			);

			// Main progression line
			Advancement root = Advancement.Builder.advancement()
					.display(rootDisplay(BotaniaItems.lexicon, "itemGroup.botania",
							"botania.desc", prefix("textures/block/livingwood_log.png")))
					.addCriterion("flower", onPickup(BotaniaTags.Items.MYSTICAL_FLOWERS))
					.save(consumer, mainId("root"));

			Advancement flowerPickup = Advancement.Builder.advancement()
					.display(simple(BotaniaBlocks.pinkFlower, "flowerPickup", FrameType.TASK))
					.parent(root)
					.addCriterion("flower", onPickup(BotaniaTags.Items.MYSTICAL_FLOWERS))
					.addCriterion("double_flower", onPickup(BotaniaTags.Items.DOUBLE_MYSTICAL_FLOWERS))
					.requirements(RequirementsStrategy.OR)
					.save(consumer, mainId("flower_pickup"));

			Advancement manaPoolPickup = Advancement.Builder.advancement()
					.display(simple(BotaniaBlocks.manaPool, "manaPoolPickup", FrameType.TASK))
					.parent(flowerPickup)
					.addCriterion("pickup", onPickup(BotaniaBlocks.manaPool, BotaniaBlocks.creativePool, BotaniaBlocks.dilutedPool, BotaniaBlocks.fabulousPool))
					.save(consumer, mainId("mana_pool_pickup"));

			Advancement runePickup = Advancement.Builder.advancement()
					.display(simple(BotaniaBlocks.runeAltar, "runePickup", FrameType.TASK))
					.parent(manaPoolPickup)
					.addCriterion("rune", onPickup(BotaniaTags.Items.RUNES))
					.save(consumer, mainId("rune_pickup"));

			Advancement terrasteelPickup = Advancement.Builder.advancement()
					.display(simple(BotaniaItems.terrasteel, "terrasteelPickup", FrameType.TASK))
					.parent(runePickup)
					.addCriterion("terrasteel", onPickup(BotaniaItems.terrasteel))
					.save(consumer, mainId("terrasteel_pickup"));

			Advancement elfPortalOpen = Advancement.Builder.advancement()
					.display(simple(BotaniaBlocks.alfPortal, "elfPortalOpen", FrameType.TASK))
					.parent(terrasteelPickup)
					.addCriterion("portal", new AlfheimPortalTrigger.Instance(ContextAwarePredicate.ANY, ItemPredicate.ANY, LocationPredicate.ANY))
					.save(consumer, mainId("elf_portal_open"));

			Advancement gaiaGuardianKill = Advancement.Builder.advancement()
					.display(simple(BotaniaBlocks.gaiaHead, "gaiaGuardianKill", FrameType.TASK))
					.parent(elfPortalOpen)
					.addCriterion("guardian", KilledTrigger.TriggerInstance
							.playerKilledEntity(EntityPredicate.Builder.entity().of(BotaniaEntities.DOPPLEGANGER)))
					.save(consumer, mainId("gaia_guardian_kill"));

			Advancement enderAirMake = Advancement.Builder.advancement()
					.display(simple(BotaniaItems.enderAirBottle, "enderAirMake", FrameType.TASK))
					.parent(elfPortalOpen)
					.addCriterion("air", onPickup(BotaniaItems.enderAirBottle))
					.save(consumer, mainId("ender_air_make"));

			// Parent: root
			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.lexicon, "lexiconUse", FrameType.TASK))
					.parent(root)
					.addCriterion("use_lexicon", new UseItemSuccessTrigger.Instance(ContextAwarePredicate.ANY,
							ItemPredicate.Builder.item().of(BotaniaItems.lexicon).build(), LocationPredicate.ANY))
					.save(consumer, mainId("lexicon_use"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.cacophonium, "cacophoniumCraft", FrameType.TASK))
					.parent(root)
					.addCriterion("cacophonium", onPickup(BotaniaItems.cacophonium))
					.save(consumer, mainId("cacophonium_craft"));

			// Parent: mystical flowers
			Advancement.Builder.advancement()
					.display(simple(BotaniaFlowerBlocks.endoflame, "generatingFlower", FrameType.TASK))
					.parent(flowerPickup)
					.addCriterion("flower", onPickup(BotaniaTags.Items.GENERATING_SPECIAL_FLOWERS))
					.save(consumer, mainId("generating_flower"));

			// Parent: mana pool
			Advancement.Builder.advancement()
					.display(simple(BotaniaBlocks.enchanter, "enchanterMake", FrameType.TASK))
					.parent(manaPoolPickup)
					.addCriterion("code_triggered", new ImpossibleTrigger.TriggerInstance())
					.save(consumer, mainId("enchanter_make"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaFlowerBlocks.bellethorn, "functionalFlower", FrameType.TASK))
					.parent(manaPoolPickup)
					.addCriterion("flower", onPickup(BotaniaTags.Items.FUNCTIONAL_SPECIAL_FLOWERS))
					.save(consumer, mainId("functional_flower"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.poolMinecart, "manaCartCraft", FrameType.TASK))
					.parent(manaPoolPickup)
					.addCriterion("poolcart", onPickup(BotaniaItems.poolMinecart))
					.save(consumer, mainId("mana_cart_craft"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.manaweaveCloth, "manaweaveArmorCraft", FrameType.TASK))
					.parent(manaPoolPickup)
					.addCriterion("head", onPickup(BotaniaItems.manaweaveHelm, BotaniaItems.manaweaveChest, BotaniaItems.manaweaveLegs, BotaniaItems.manaweaveBoots))
					.save(consumer, mainId("manaweave_armor_craft"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.spark, "sparkCraft", FrameType.TASK))
					.parent(manaPoolPickup)
					.addCriterion("spark", onPickup(BotaniaItems.spark))
					.save(consumer, mainId("spark_craft"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.manaCookie, "manaCookieEat", FrameType.TASK))
					.parent(manaPoolPickup)
					.addCriterion("cookie", ConsumeItemTrigger.TriggerInstance.usedItem(BotaniaItems.manaCookie))
					.save(consumer, mainId("mana_cookie_eat"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.craftingHalo, "craftingHaloCraft", FrameType.TASK))
					.parent(manaPoolPickup)
					.addCriterion("pool", onPickup(BotaniaItems.craftingHalo))
					.save(consumer, mainId("crafting_halo_craft"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.manaRing, "baubleWear", FrameType.TASK))
					.parent(manaPoolPickup)
					.addCriterion("code_triggered", new ImpossibleTrigger.TriggerInstance())
					.save(consumer, mainId("bauble_wear"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaBlocks.tinyPotato, "tinyPotatoPet", FrameType.TASK))
					.parent(manaPoolPickup)
					.addCriterion("code_triggered", new ImpossibleTrigger.TriggerInstance())
					.save(consumer, mainId("tiny_potato_pet"));

			// Parent: runes
			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.manaGun, "manaBlasterShoot", FrameType.TASK))
					.parent(runePickup)
					.addCriterion("shoot", new ManaBlasterTrigger.Instance(ContextAwarePredicate.ANY, ItemPredicate.ANY, EntityPredicate.ANY))
					.save(consumer, mainId("mana_blaster_shoot"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaFlowerBlocks.pollidisiac, "pollidisiacPickup", FrameType.TASK))
					.parent(runePickup)
					.addCriterion("pollidisiac", onPickup(BotaniaFlowerBlocks.pollidisiac))
					.save(consumer, mainId("pollidisiac_pickup"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.dirtRod, "dirtRodCraft", FrameType.TASK))
					.parent(runePickup)
					.addCriterion("dirtrod", onPickup(BotaniaItems.dirtRod))
					.save(consumer, mainId("dirt_rod_craft"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaBlocks.brewery, "brewPickup", FrameType.TASK))
					.parent(runePickup)
					.addCriterion("pickup", onPickup(BotaniaItems.brewFlask, BotaniaItems.brewVial))
					.save(consumer, mainId("brew_pickup"));

			// Parent: terrasteel
			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.terraSword, "terrasteelWeaponCraft", FrameType.TASK))
					.parent(terrasteelPickup)
					.addCriterion("terrablade", onPickup(BotaniaItems.terraSword, BotaniaItems.thornChakram))
					.save(consumer, mainId("terrasteel_weapon_craft"));

			// Parent: elven portal
			Advancement.Builder.advancement()
					.display(simple(BotaniaFlowerBlocks.heiseiDream, "heiseiDreamPickup", FrameType.TASK))
					.parent(elfPortalOpen)
					.addCriterion("heisei_dream", onPickup(BotaniaFlowerBlocks.heiseiDream))
					.save(consumer, mainId("heisei_dream_pickup"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaFlowerBlocks.kekimurus, "kekimurusPickup", FrameType.TASK))
					.parent(elfPortalOpen)
					.addCriterion("kekimurus", onPickup(BotaniaFlowerBlocks.kekimurus))
					.save(consumer, mainId("kekimurus_pickup"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaFlowerBlocks.bubbell, "bubbellPickup", FrameType.TASK))
					.parent(elfPortalOpen)
					.addCriterion("bubbell", onPickup(BotaniaFlowerBlocks.bubbell))
					.save(consumer, mainId("bubbell_pickup"));

			// Parent: gaia guardian
			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.spawnerMover, "spawnerMoverUse", FrameType.TASK))
					.parent(gaiaGuardianKill)
					.addCriterion("use_spawner_mover", new UseItemSuccessTrigger.Instance(ContextAwarePredicate.ANY,
							ItemPredicate.Builder.item().of(BotaniaItems.spawnerMover).build(), LocationPredicate.ANY))
					.save(consumer, mainId("spawner_mover_use"));
			DisplayInfo tiaraWings = simple(BotaniaItems.flightTiara, "tiaraWings", FrameType.TASK);
			tiaraWings.getIcon().getOrCreateTag().putInt("variant", 1);
			InventoryChangeTrigger.TriggerInstance[] variants = IntStream.range(1, FlugelTiaraItem.WING_TYPES)
					.mapToObj(i -> {
						CompoundTag tag = new CompoundTag();
						tag.putInt("variant", i);
						return tag;
					})
					.map(nbt -> ItemPredicate.Builder.item().of(BotaniaItems.flightTiara).hasNbt(nbt).build())
					.map(InventoryChangeTrigger.TriggerInstance::hasItems)
					.toArray(InventoryChangeTrigger.TriggerInstance[]::new);
			var builder = Advancement.Builder.advancement()
					.display(tiaraWings)
					.parent(gaiaGuardianKill)
					.requirements(RequirementsStrategy.OR);
			for (int i = 0; i < variants.length; i++) {
				var variant = variants[i];
				builder.addCriterion("tiara_" + (i + 1), variant);
			}
			builder.save(consumer, mainId("tiara_wings"));

			Advancement.Builder.advancement()
					.display(simple(BotaniaFlowerBlocks.dandelifeon, "dandelifeonPickup", FrameType.TASK))
					.parent(gaiaGuardianKill)
					.addCriterion("dandelifeon", onPickup(BotaniaFlowerBlocks.dandelifeon))
					.save(consumer, mainId("dandelifeon_pickup"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaBlocks.manaBomb, "manaBombIgnite", FrameType.TASK))
					.parent(gaiaGuardianKill)
					.addCriterion("bomb", onPickup(BotaniaBlocks.manaBomb))
					.save(consumer, mainId("mana_bomb_ignite"));

			// Parent: ender air
			Advancement.Builder.advancement()
					.display(simple(BotaniaBlocks.lightRelayDefault, "luminizerRide", FrameType.TASK))
					.parent(enderAirMake)
					.addCriterion("code_triggered", new ImpossibleTrigger.TriggerInstance())
					.save(consumer, mainId("luminizer_ride"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaBlocks.corporeaCrystalCube, "corporeaCraft", FrameType.TASK))
					.parent(enderAirMake)
					.addCriterion("pickup", onPickup(BotaniaBlocks.corporeaCrystalCube, BotaniaBlocks.corporeaFunnel,
							BotaniaBlocks.corporeaIndex, BotaniaBlocks.corporeaInterceptor, BotaniaBlocks.corporeaRetainer))
					.save(consumer, mainId("corporea_craft"));

			// Lexicon locks
			Advancement.Builder.advancement()
					.parent(root)
					.addCriterion("flower", onPickup(BotaniaTags.Items.MYSTICAL_FLOWERS))
					.addCriterion("elven_lexicon", elvenLexicon)
					.requirements(RequirementsStrategy.OR)
					.save(consumer, mainId("flower_pickup_lexicon"));
			Advancement.Builder.advancement()
					.parent(flowerPickup)
					.addCriterion("apothecary", onPickup(BotaniaBlocks.defaultAltar))
					.addCriterion("elven_lexicon", elvenLexicon)
					.requirements(RequirementsStrategy.OR)
					.save(consumer, mainId("apothecary_pickup"));
			Advancement.Builder.advancement()
					.parent(flowerPickup)
					.addCriterion("daisy", onPickup(BotaniaFlowerBlocks.pureDaisy))
					.addCriterion("elven_lexicon", elvenLexicon)
					.requirements(RequirementsStrategy.OR)
					.save(consumer, mainId("pure_daisy_pickup"));
			Advancement.Builder.advancement()
					.parent(root)
					.addCriterion("pickup", onPickup(BotaniaBlocks.manaPool, BotaniaBlocks.creativePool, BotaniaBlocks.dilutedPool, BotaniaBlocks.fabulousPool))
					.addCriterion("elven_lexicon", elvenLexicon)
					.requirements(RequirementsStrategy.OR)
					.save(consumer, mainId("mana_pool_pickup_lexicon"));
			Advancement.Builder.advancement()
					.parent(flowerPickup)
					.addCriterion("altar", onPickup(BotaniaBlocks.runeAltar))
					.addCriterion("rune", onPickup(BotaniaTags.Items.RUNES))
					.addCriterion("elven_lexicon", elvenLexicon)
					.requirements(RequirementsStrategy.OR)
					.save(consumer, mainId("runic_altar_pickup"));
			Advancement.Builder.advancement()
					.parent(flowerPickup)
					.addCriterion("terrasteel", onPickup(BotaniaItems.terrasteel))
					.addCriterion("elven_lexicon", elvenLexicon)
					.requirements(RequirementsStrategy.OR)
					.save(consumer, mainId("terrasteel_pickup_lexicon"));
			Advancement.Builder.advancement()
					.parent(elfPortalOpen)
					.addCriterion("lexicon", elvenLexicon)
					.save(consumer, mainId("elf_lexicon_pickup"));
		}
	}

	public static class BotaniaChallengeAdvancements implements AdvancementSubProvider {

		private static final EntityType<?>[] LOONIUM_MOBS_TO_KILL = {
				EntityType.BLAZE,
				EntityType.CAVE_SPIDER,
				EntityType.CREEPER,
				EntityType.DROWNED,
				EntityType.ENDERMAN,
				EntityType.EVOKER,
				EntityType.GUARDIAN,
				EntityType.HOGLIN,
				EntityType.HUSK,
				EntityType.PIGLIN,
				EntityType.PIGLIN_BRUTE,
				EntityType.PILLAGER,
				EntityType.SHULKER,
				EntityType.SILVERFISH,
				EntityType.SKELETON,
				EntityType.STRAY,
				EntityType.VINDICATOR,
				EntityType.WITHER_SKELETON,
				EntityType.ZOGLIN,
				EntityType.ZOMBIE_VILLAGER,
				EntityType.ZOMBIE,
				EntityType.ZOMBIFIED_PIGLIN
		};

		@Override
		public void generate(HolderLookup.Provider lookup, Consumer<Advancement> consumer) {
			Advancement root = Advancement.Builder.advancement()
					.display(rootDisplay(BotaniaItems.dice, "advancement.botania_challenge",
							"advancement.botania_challenge.desc", prefix("textures/block/livingrock_bricks.png")))
					.addCriterion("flower", onPickup(BotaniaTags.Items.MYSTICAL_FLOWERS))
					.save(consumer, challengeId("root"));

			// hardmode Gaia Guardian related
			CompoundTag hardmodeNbt = new CompoundTag();
			hardmodeNbt.putBoolean("hardMode", true);
			Advancement hardMode = Advancement.Builder.advancement()
					.display(simple(BotaniaItems.lifeEssence, "gaiaGuardianHardmode", FrameType.CHALLENGE))
					.parent(root)
					.rewards(AdvancementRewards.Builder.experience(100))
					.addCriterion("guardian", KilledTrigger.TriggerInstance.playerKilledEntity(
							EntityPredicate.Builder.entity()
									.of(BotaniaEntities.DOPPLEGANGER)
									.nbt(new NbtPredicate(hardmodeNbt)).build()))
					.save(consumer, challengeId("gaia_guardian_hardmode"));

			relicBindAdvancement(consumer, hardMode, BotaniaItems.infiniteFruit, "infiniteFruit", "fruit");
			relicBindAdvancement(consumer, hardMode, BotaniaItems.kingKey, "kingKey", "key");
			relicBindAdvancement(consumer, hardMode, BotaniaItems.flugelEye, "flugelEye", "eye");
			relicBindAdvancement(consumer, hardMode, BotaniaItems.thorRing, "thorRing", "ring");
			relicBindAdvancement(consumer, hardMode, BotaniaItems.odinRing, "odinRing", "ring");
			Advancement lokiRing = relicBindAdvancement(consumer, hardMode, BotaniaItems.lokiRing, "lokiRing", "ring");

			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.lokiRing, "lokiRingMany", FrameType.CHALLENGE))
					.parent(lokiRing)
					.rewards(AdvancementRewards.Builder.experience(85))
					.addCriterion("place_blocks", new LokiPlaceTrigger.Instance(
							ContextAwarePredicate.ANY, EntityPredicate.ANY, ItemPredicate.ANY, MinMaxBounds.Ints.atLeast(255)
					))
					.save(consumer, challengeId("loki_ring_many"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.pinkinator, "pinkinator", FrameType.CHALLENGE))
					.parent(hardMode)
					.rewards(AdvancementRewards.Builder.experience(40))
					.addCriterion("use_pinkinator", new UseItemSuccessTrigger.Instance(
							ContextAwarePredicate.ANY, matchItems(BotaniaItems.pinkinator), LocationPredicate.ANY))
					.save(consumer, challengeId("pinkinator"));

			// Misc challenges
			Advancement.Builder.advancement()
					.display(simple(Blocks.PLAYER_HEAD, "gaiaGuardianNoArmor", FrameType.CHALLENGE))
					.parent(root)
					.rewards(AdvancementRewards.Builder.experience(1000))
					.addCriterion("no_armor", new GaiaGuardianNoArmorTrigger.Instance(
							ContextAwarePredicate.ANY, EntityPredicate.ANY, DamageSourcePredicate.ANY))
					.save(consumer, challengeId("gaia_guardian_no_armor"));
			Advancement.Builder.advancement()
					.display(hidden(BotaniaBlocks.motifDaybloom, "old_flower_pickup", FrameType.CHALLENGE))
					.parent(root)
					.rewards(AdvancementRewards.Builder.experience(40))
					.addCriterion("flower", onPickup(BotaniaBlocks.motifDaybloom, BotaniaBlocks.motifNightshade))
					.requirements(RequirementsStrategy.OR)
					.save(consumer, challengeId("old_flower_pickup"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaBlocks.corporeaIndex, "superCorporeaRequest", FrameType.CHALLENGE))
					.parent(root)
					.rewards(AdvancementRewards.Builder.experience(85))
					.addCriterion("big_request", new CorporeaRequestTrigger.Instance(
							ContextAwarePredicate.ANY, MinMaxBounds.Ints.atLeast(CorporeaIndexBlockEntity.MAX_REQUEST), LocationPredicate.ANY))
					.save(consumer, challengeId("super_corporea_request"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.terraPick, "rankSSPick", FrameType.CHALLENGE))
					.parent(root)
					.rewards(AdvancementRewards.Builder.experience(500))
					.addCriterion("code_triggered", new ImpossibleTrigger.TriggerInstance())
					.save(consumer, challengeId("rank_ss_pick"));
			CompoundTag level20Shard = new CompoundTag();
			level20Shard.putInt("level", 19);
			Advancement.Builder.advancement()
					.display(simple(BotaniaItems.laputaShard, "l20ShardUse", FrameType.CHALLENGE))
					.parent(root)
					.rewards(AdvancementRewards.Builder.experience(65))
					.addCriterion("use_l20_shard", InventoryChangeTrigger.TriggerInstance.hasItems(
							ItemPredicate.Builder.item().of(BotaniaItems.laputaShard).hasNbt(level20Shard).build()))
					.save(consumer, challengeId("l20_shard_use"));
			Advancement.Builder.advancement()
					.display(hidden(Items.BREAD, "alfPortalBread", FrameType.CHALLENGE))
					.parent(root)
					.rewards(AdvancementRewards.Builder.experience(40))
					.addCriterion("bread", new AlfheimPortalBreadTrigger.Instance(ContextAwarePredicate.ANY, LocationPredicate.ANY))
					.save(consumer, challengeId("alf_portal_bread"));
			Advancement.Builder.advancement()
					.display(simple(BotaniaBlocks.tinyPotato, "tinyPotatoBirthday", FrameType.CHALLENGE))
					.parent(root)
					.rewards(AdvancementRewards.Builder.experience(40))
					.addCriterion("code_triggered", new ImpossibleTrigger.TriggerInstance())
					.save(consumer, challengeId("tiny_potato_birthday"));
			addLooniumMobsToKill(Advancement.Builder.advancement())
					.display(simple(BotaniaFlowerBlocks.loonium, "allLooniumMobs", FrameType.CHALLENGE))
					.parent(root)
					.requirements(RequirementsStrategy.AND)
					.save(consumer, challengeId("all_loonium_mobs"));
		}

		private static Advancement.Builder addLooniumMobsToKill(Advancement.Builder builder) {
			for (EntityType<?> entityType : LOONIUM_MOBS_TO_KILL) {
				builder.addCriterion(
						BuiltInRegistries.ENTITY_TYPE.getKey(entityType).toString(),
						KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity()
								.of(entityType).team(LooniumBlockEntity.LOONIUM_TEAM_NAME))
				);
			}

			return builder;
		}
	}

	private static Advancement relicBindAdvancement(Consumer<Advancement> consumer, Advancement parent, Item relicItem,
			String titleKey, String criterionName) {
		String id = challengeId(BuiltInRegistries.ITEM.getKey(relicItem).getPath());
		return Advancement.Builder.advancement()
				.display(simple(relicItem, titleKey, FrameType.CHALLENGE))
				.parent(parent)
				.rewards(AdvancementRewards.Builder.experience(50))
				.addCriterion(criterionName, new RelicBindTrigger.Instance(ContextAwarePredicate.ANY,
						ItemPredicate.Builder.item().of(relicItem).build()))
				.save(consumer, id);
	}

	protected static InventoryChangeTrigger.TriggerInstance onPickup(TagKey<Item> tag) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(
				ItemPredicate.Builder.item().of(tag).build());
	}

	protected static InventoryChangeTrigger.TriggerInstance onPickup(ItemLike... items) {
		return InventoryChangeTrigger.TriggerInstance.hasItems(matchItems(items));
	}

	protected static ItemPredicate matchItems(ItemLike... items) {
		return ItemPredicate.Builder.item().of(items).build();
	}

	protected static DisplayInfo simple(ItemLike icon, String name, FrameType frameType) {
		String expandedName = "advancement.botania:" + name;
		return new DisplayInfo(new ItemStack(icon.asItem()),
				Component.translatable(expandedName),
				Component.translatable(expandedName + ".desc"),
				null, frameType, true, true, false);
	}

	protected static DisplayInfo hidden(ItemLike icon, String name, FrameType frameType) {
		String expandedName = "advancement.botania:" + name;
		return new DisplayInfo(new ItemStack(icon.asItem()),
				Component.translatable(expandedName),
				Component.translatable(expandedName + ".desc"),
				null, frameType, true, true, true);
	}

	protected static DisplayInfo rootDisplay(ItemLike icon, String titleKey, String descKey, ResourceLocation background) {
		return new DisplayInfo(new ItemStack(icon.asItem()),
				Component.translatable(titleKey),
				Component.translatable(descKey),
				background, FrameType.TASK, false, false, false);
	}

	private static String mainId(String name) {
		return prefix("main/" + name).toString();
	}

	private static String challengeId(String name) {
		return prefix("challenge/" + name).toString();
	}
}
