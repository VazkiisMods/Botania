package vazkii.botania.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;

import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.configdata.LooniumMobAttributeModifier;
import vazkii.botania.api.configdata.LooniumMobEffectToApply;
import vazkii.botania.api.configdata.LooniumMobSpawnData;
import vazkii.botania.api.configdata.LooniumStructureConfiguration;
import vazkii.botania.common.loot.BotaniaLootTables;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class LooniumStructureConfigurationProvider implements DataProvider {

	public static final String LOONIUM_MODIFIER_DAMAGE = "Loonium Modifier Damage";
	public static final String LOONIUM_MODIFIER_HEALTH = "Loonium Modifier Health";
	private final PackOutput.PathProvider pathProvider;

	public LooniumStructureConfigurationProvider(PackOutput packOutput) {
		pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "loonium_config");
	}

	@NotNull
	@Override
	public CompletableFuture<?> run(@NotNull CachedOutput cache) {
		Map<ResourceLocation, LooniumStructureConfiguration> configs = new HashMap<>();
		addConfigs(configs);

		var output = new ArrayList<CompletableFuture<?>>(configs.size());
		for (Map.Entry<ResourceLocation, LooniumStructureConfiguration> e : configs.entrySet()) {
			Path path = pathProvider.json(e.getKey());
			LooniumStructureConfiguration config = e.getValue();
			JsonElement jsonTree = LooniumStructureConfiguration.CODEC.encodeStart(JsonOps.INSTANCE, config)
					.getOrThrow(false, BotaniaAPI.LOGGER::error);
			output.add(DataProvider.saveStable(cache, jsonTree, path));
		}
		return CompletableFuture.allOf(output.toArray(CompletableFuture<?>[]::new));
	}

	private void addConfigs(Map<ResourceLocation, LooniumStructureConfiguration> configs) {
		ResourceLocation defaultConfigId = LooniumStructureConfiguration.DEFAULT_CONFIG_ID;
		configs.put(defaultConfigId, getDefaultConfig());

		configs.put(BuiltinStructures.ANCIENT_CITY.location(), getConfigAncientCity(defaultConfigId));
		configs.put(BuiltinStructures.BASTION_REMNANT.location(), getConfigBastionRemnant(defaultConfigId));
		configs.put(BuiltinStructures.DESERT_PYRAMID.location(), getConfigDesertPyramid(defaultConfigId));
		configs.put(BuiltinStructures.END_CITY.location(), getConfigEndCity(defaultConfigId));
		configs.put(BuiltinStructures.FORTRESS.location(), getConfigFortress(defaultConfigId));
		configs.put(BuiltinStructures.JUNGLE_TEMPLE.location(), getConfigJungleTemple(defaultConfigId));
		configs.put(BuiltinStructures.OCEAN_MONUMENT.location(), getConfigOceanMonument(defaultConfigId));

		ResourceLocation oceanRuinId = prefix("ocean_ruins");
		configs.put(oceanRuinId,
				LooniumStructureConfiguration.forParent(defaultConfigId)
						.boundingBoxType(StructureSpawnOverride.BoundingBoxType.STRUCTURE).build()
		);
		configs.put(BuiltinStructures.OCEAN_RUIN_COLD.location(), getConfigOceanRuinCold(oceanRuinId));
		configs.put(BuiltinStructures.OCEAN_RUIN_WARM.location(), getConfigOceanRuinWarm(oceanRuinId));

		configs.put(BuiltinStructures.PILLAGER_OUTPOST.location(), getConfigPillagerOutpost(defaultConfigId));
		configs.put(BuiltinStructures.RUINED_PORTAL_DESERT.location(), getConfigRuinedPortalDesert(defaultConfigId));
		configs.put(BuiltinStructures.RUINED_PORTAL_JUNGLE.location(), getConfigRuinedPortalJungle(defaultConfigId));
		configs.put(BuiltinStructures.RUINED_PORTAL_MOUNTAIN.location(), getConfigRuinedPortalMountain(defaultConfigId));
		configs.put(BuiltinStructures.RUINED_PORTAL_NETHER.location(), getConfigRuinedPortalNether(defaultConfigId));
		configs.put(BuiltinStructures.RUINED_PORTAL_OCEAN.location(), getConfigRuinedPortalOcean(defaultConfigId));
		configs.put(BuiltinStructures.RUINED_PORTAL_STANDARD.location(), getConfigRuinedPortalStandard(defaultConfigId));
		configs.put(BuiltinStructures.RUINED_PORTAL_SWAMP.location(), getConfigRuinedPortalSwamp(defaultConfigId));

		configs.put(BuiltinStructures.SHIPWRECK.location(), getConfigShipwreck(defaultConfigId));
		configs.put(BuiltinStructures.SHIPWRECK_BEACHED.location(),
				LooniumStructureConfiguration.forParent(BuiltinStructures.SHIPWRECK.location()).build());

		configs.put(BuiltinStructures.STRONGHOLD.location(), getConfigStronghold(defaultConfigId));
		configs.put(BuiltinStructures.TRAIL_RUINS.location(), getConfigTrailRuins(defaultConfigId));

		ResourceLocation villageId = prefix("village");
		configs.put(villageId, LooniumStructureConfiguration.forParent(defaultConfigId)
				.boundingBoxType(StructureSpawnOverride.BoundingBoxType.STRUCTURE).build()
		);
		configs.put(BuiltinStructures.VILLAGE_DESERT.location(), getConfigVillageDesert(villageId));
		configs.put(BuiltinStructures.VILLAGE_PLAINS.location(), getConfigVillagePlains(villageId));
		configs.put(BuiltinStructures.VILLAGE_SAVANNA.location(), getConfigVillageSavanna(villageId));
		configs.put(BuiltinStructures.VILLAGE_SNOWY.location(), getConfigVillageSnowy(villageId));
		configs.put(BuiltinStructures.VILLAGE_TAIGA.location(), getConfigVillageTaiga(villageId));

		configs.put(BuiltinStructures.WOODLAND_MANSION.location(), getConfigWoodlandMansion(defaultConfigId));
	}

	public static LooniumStructureConfiguration getDefaultConfig() {
		return LooniumStructureConfiguration.builder()
				.manaCost(LooniumStructureConfiguration.DEFAULT_COST)
				.maxNearbyMobs(LooniumStructureConfiguration.DEFAULT_MAX_NEARBY_MOBS)
				.boundingBoxType(StructureSpawnOverride.BoundingBoxType.PIECE)
				.spawnedMobs(
						// weights roughly based on original Loonium mob selection logic
						LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 40).build(),
						getCreeperSpawnData(195, false, getCreeperEffects(false)),
						getCreeperSpawnData(1, true, getCreeperEffects(false)),
						LooniumMobSpawnData.entityWeight(EntityType.HUSK, 59)
								.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_SWORD).build(),
						LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 106)
								.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_TRIDENT).build(),
						LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 423)
								.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_SWORD).build(),
						LooniumMobSpawnData.entityWeight(EntityType.STRAY, 59)
								.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BOW).build(),
						LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 529)
								.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BOW).build(),
						LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 59).build(),
						LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 529).build()
				)
				.attributeModifiers(
						new LooniumMobAttributeModifier(LOONIUM_MODIFIER_HEALTH,
								Attributes.MAX_HEALTH, 2, AttributeModifier.Operation.MULTIPLY_BASE),
						new LooniumMobAttributeModifier(LOONIUM_MODIFIER_DAMAGE,
								Attributes.ATTACK_DAMAGE, 1.5, AttributeModifier.Operation.MULTIPLY_BASE)
				)
				.effectsToApply(getStandardEffects(false, true))
				.build();
	}

	public static LooniumStructureConfiguration getConfigAncientCity(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 30).build(),
				getCreeperSpawnData(99, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.HUSK, 40)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_ANCIENT_CITY).build(),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 80)
						.equipmentTable(BotaniaLootTables.LOONIUM_DROWNED_ANCIENT_CITY).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 410)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_ANCIENT_CITY).build(),
				LooniumMobSpawnData.entityWeight(EntityType.STRAY, 60)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_ANCIENT_CITY).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 440)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_ANCIENT_CITY).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 100).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 200).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigBastionRemnant(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 30).build(),
				getCreeperSpawnData(99, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				getPiglinSpawnData(450, BotaniaLootTables.LOONIUM_PIGLIN_BASTION_REMNANT, false, false),
				LooniumMobSpawnData.entityWeight(EntityType.PIGLIN_BRUTE, 50)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_AXE_GOLD)
						.attributeModifiers(
								new LooniumMobAttributeModifier(LOONIUM_MODIFIER_HEALTH,
										Attributes.MAX_HEALTH, 1.5, AttributeModifier.Operation.MULTIPLY_BASE),
								new LooniumMobAttributeModifier(LOONIUM_MODIFIER_DAMAGE,
										Attributes.ATTACK_DAMAGE, 1.5, AttributeModifier.Operation.MULTIPLY_BASE)
						)
						.build(),
				LooniumMobSpawnData.entityWeight(EntityType.HOGLIN, 300).spawnAsAdult().build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigDesertPyramid(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 50).build(),
				getCreeperSpawnData(149, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.HUSK, 450)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_DESERT_PYRAMID).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 50)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_DESERT_PYRAMID).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 500)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_DESERT_PYRAMID).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 40).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 360).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigEndCity(ResourceLocation parentId) {
		LooniumMobEffectToApply[] creeperEffects = {
				LooniumMobEffectToApply.effect(MobEffects.FIRE_RESISTANCE).duration(100).build(),
				LooniumMobEffectToApply.effect(MobEffects.REGENERATION).duration(100).build(),
				LooniumMobEffectToApply.effect(MobEffects.SLOW_FALLING).duration(400).build()
		};
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.SHULKER, 100)
						.effectsToApply(getStandardEffects(true, true)).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 300).build(),
				getCreeperSpawnData(99, false, creeperEffects),
				getCreeperSpawnData(1, true, creeperEffects),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 300)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_END_CITY).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 300)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_END_CITY).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 300).build()
		).effectsToApply(
				LooniumMobEffectToApply.effect(MobEffects.FIRE_RESISTANCE).build(),
				LooniumMobEffectToApply.effect(MobEffects.REGENERATION).build(),
				LooniumMobEffectToApply.effect(MobEffects.SLOW_FALLING).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigFortress(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 30).build(),
				getCreeperSpawnData(99, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.BLAZE, 300)
						.effectsToApply(getStandardEffects(false, false)).build(),
				LooniumMobSpawnData.entityWeight(EntityType.WITHER_SKELETON, 450)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_FORTRESS)
						.effectsToApply(getStandardEffects(false, false)).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 50)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_FORTRESS).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIFIED_PIGLIN, 400)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_FORTRESS)
						.effectsToApply(getStandardEffects(false, false)).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigJungleTemple(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 30).build(),
				getCreeperSpawnData(149, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 40)
						.equipmentTable(BotaniaLootTables.LOONIUM_DROWNED_JUNGLE_TEMPLE).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 360)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_JUNGLE_TEMPLE).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 500)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_JUNGLE_TEMPLE).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 300).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 300).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigOceanMonument(ResourceLocation parentId) {
		LooniumMobEffectToApply[] standardEffectsInWater = getStandardEffects(true, true);
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.GUARDIAN, 200).build(),
				getCreeperSpawnData(199, false, getCreeperEffects(true)),
				getCreeperSpawnData(1, true, getCreeperEffects(true)),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 540)
						.equipmentTable(BotaniaLootTables.LOONIUM_DROWNED_MONUMENT).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 60)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_MONUMENT).build(),
				LooniumMobSpawnData.entityWeight(EntityType.STRAY, 40)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_MONUMENT).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 360)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_MONUMENT).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 30)
						.effectsToApply(standardEffectsInWater).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 270)
						.effectsToApply(standardEffectsInWater).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigOceanRuinCold(ResourceLocation parentId) {
		LooniumMobEffectToApply[] standardEffectsInWater = getStandardEffects(true, true);
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				getCreeperSpawnData(199, false, getCreeperEffects(true)),
				getCreeperSpawnData(1, true, getCreeperEffects(true)),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 540)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_TRIDENT).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 60)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_SWORD).build(),
				LooniumMobSpawnData.entityWeight(EntityType.STRAY, 40)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BOW).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 360)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BOW).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 30)
						.effectsToApply(standardEffectsInWater).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 270)
						.effectsToApply(standardEffectsInWater).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigOceanRuinWarm(ResourceLocation parentId) {
		LooniumMobEffectToApply[] standardEffectsInWater = getStandardEffects(true, true);
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				getCreeperSpawnData(199, false, getCreeperEffects(true)),
				getCreeperSpawnData(1, true, getCreeperEffects(true)),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 540)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_TRIDENT).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 60)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_SWORD).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 400)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BOW).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 30)
						.effectsToApply(standardEffectsInWater).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 270)
						.effectsToApply(standardEffectsInWater).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigPillagerOutpost(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId)
				.boundingBoxType(StructureSpawnOverride.BoundingBoxType.STRUCTURE).spawnedMobs(
						LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 40).build(),
						getCreeperSpawnData(199, false, getCreeperEffects(false)),
						getCreeperSpawnData(1, true, getCreeperEffects(false)),
						LooniumMobSpawnData.entityWeight(EntityType.PILLAGER, 900)
								.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_CROSSBOW).build(),
						LooniumMobSpawnData.entityWeight(EntityType.VINDICATOR, 175)
								.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_AXE).build(),
						LooniumMobSpawnData.entityWeight(EntityType.EVOKER, 25).build(),
						LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 200)
								.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_OUTPOST).build(),
						LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 200)
								.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_OUTPOST).build(),
						LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 200).build()
				).build();
	}

	public static LooniumStructureConfiguration getConfigRuinedPortalDesert(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ZOGLIN, 25)
						.effectsToApply(getStandardEffects(false, false)).build(),
				getPiglinSpawnData(50, BotaniaLootTables.LOONIUM_PIGLIN_PORTAL, false, true),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIFIED_PIGLIN, 50)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 50).build(),
				getCreeperSpawnData(149, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.HUSK, 450)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 50)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 450)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 360).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigRuinedPortalJungle(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ZOGLIN, 25)
						.effectsToApply(getStandardEffects(false, false)).build(),
				getPiglinSpawnData(50, BotaniaLootTables.LOONIUM_PIGLIN_PORTAL, false, true),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIFIED_PIGLIN, 50)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 30).build(),
				getCreeperSpawnData(149, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 40)
						.equipmentTable(BotaniaLootTables.LOONIUM_DROWNED_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 360)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 500)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 250).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 50).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigRuinedPortalMountain(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ZOGLIN, 25)
						.effectsToApply(getStandardEffects(false, false)).build(),
				getPiglinSpawnData(50, BotaniaLootTables.LOONIUM_PIGLIN_PORTAL, false, true),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIFIED_PIGLIN, 50)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 40).build(),
				getCreeperSpawnData(195, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 529)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.STRAY, 59)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 529)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SILVERFISH, 59).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 529).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigRuinedPortalNether(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ZOGLIN, 125)
						.effectsToApply(getStandardEffects(false, false)).build(),
				getPiglinSpawnData(500, BotaniaLootTables.LOONIUM_PIGLIN_PORTAL, false, false),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIFIED_PIGLIN, 450)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 40).build(),
				getCreeperSpawnData(195, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 50)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 200)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 10).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 90).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigRuinedPortalOcean(ResourceLocation parentId) {
		LooniumMobEffectToApply[] standardEffectsInWater = getStandardEffects(true, true);
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ZOGLIN, 25)
						.effectsToApply(getStandardEffects(false, false)).build(),
				getPiglinSpawnData(50, BotaniaLootTables.LOONIUM_PIGLIN_PORTAL, true, true),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIFIED_PIGLIN, 50)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				getCreeperSpawnData(199, false, getCreeperEffects(true)),
				getCreeperSpawnData(1, true, getCreeperEffects(true)),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 540)
						.equipmentTable(BotaniaLootTables.LOONIUM_DROWNED_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 60)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 400)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 30)
						.effectsToApply(standardEffectsInWater).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 270)
						.effectsToApply(standardEffectsInWater).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigRuinedPortalStandard(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ZOGLIN, 25)
						.effectsToApply(getStandardEffects(false, false)).build(),
				getPiglinSpawnData(50, BotaniaLootTables.LOONIUM_PIGLIN_PORTAL, false, true),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIFIED_PIGLIN, 50)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 40).build(),
				getCreeperSpawnData(195, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.HUSK, 59)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 106)
						.equipmentTable(BotaniaLootTables.LOONIUM_DROWNED_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 423)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.STRAY, 59)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 529)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 59).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 529).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigRuinedPortalSwamp(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ZOGLIN, 25)
						.effectsToApply(getStandardEffects(false, false)).build(),
				getPiglinSpawnData(50, BotaniaLootTables.LOONIUM_PIGLIN_PORTAL, false, true),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIFIED_PIGLIN, 50)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 30).build(),
				getCreeperSpawnData(149, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 40)
						.equipmentTable(BotaniaLootTables.LOONIUM_DROWNED_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 360)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 500)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_PORTAL).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 50).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 250).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigShipwreck(ResourceLocation parentId) {
		LooniumMobEffectToApply[] standardEffectsInWater = getStandardEffects(true, true);
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				getCreeperSpawnData(199, false, getCreeperEffects(true)),
				getCreeperSpawnData(1, true, getCreeperEffects(true)),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 540)
						.equipmentTable(BotaniaLootTables.LOONIUM_DROWNED_SHIPWRECK).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 60)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_SHIPWRECK).build(),
				LooniumMobSpawnData.entityWeight(EntityType.STRAY, 40)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_SHIPWRECK).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 360)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_SHIPWRECK).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 30)
						.effectsToApply(standardEffectsInWater).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 270)
						.effectsToApply(standardEffectsInWater).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigStronghold(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 80).build(),
				getCreeperSpawnData(149, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.HUSK, 40)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_STRONGHOLD).build(),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 40)
						.equipmentTable(BotaniaLootTables.LOONIUM_DROWNED_STRONGHOLD).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 410)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_STRONGHOLD).build(),
				LooniumMobSpawnData.entityWeight(EntityType.STRAY, 60)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_STRONGHOLD).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 440)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_STRONGHOLD).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 100).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SILVERFISH, 100).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 400).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigTrailRuins(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 40).build(),
				getCreeperSpawnData(195, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.HUSK, 59)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_TRAIL_RUINS).build(),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 106)
						.equipmentTable(BotaniaLootTables.LOONIUM_DROWNED_TRAIL_RUINS).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 423)
						.equipmentTable(BotaniaLootTables.LOONIUM_ZOMBIE_TRAIL_RUINS).build(),
				LooniumMobSpawnData.entityWeight(EntityType.STRAY, 59)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_TRAIL_RUINS).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 529)
						.equipmentTable(BotaniaLootTables.LOONIUM_SKELETON_TRAIL_RUINS).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 59).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 529).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigVillageDesert(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 40).build(),
				getCreeperSpawnData(195, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.HUSK, 423)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_SWORD).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 59)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_SWORD).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE_VILLAGER, 106)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BY_PROFESSION).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 600)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BOW).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 59).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 529).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigVillagePlains(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 40).build(),
				getCreeperSpawnData(195, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 59)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_TRIDENT).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 423)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_SWORD).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE_VILLAGER, 106)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BY_PROFESSION).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 600)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BOW).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 59).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 529).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigVillageSavanna(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 40).build(),
				getCreeperSpawnData(195, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 30)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_TRIDENT).build(),
				LooniumMobSpawnData.entityWeight(EntityType.HUSK, 30)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_SWORD).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 423)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_SWORD).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE_VILLAGER, 106)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BY_PROFESSION).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 600)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BOW).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 59).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 529).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigVillageSnowy(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 40).build(),
				getCreeperSpawnData(195, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 59)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_TRIDENT).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 423)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_SWORD).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE_VILLAGER, 106)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BY_PROFESSION).build(),
				LooniumMobSpawnData.entityWeight(EntityType.STRAY, 529)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BOW).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 59)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BOW).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 59).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 529).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigVillageTaiga(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 40).build(),
				getCreeperSpawnData(195, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.DROWNED, 59)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_TRIDENT).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 423)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_SWORD).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE_VILLAGER, 106)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BY_PROFESSION).build(),
				LooniumMobSpawnData.entityWeight(EntityType.STRAY, 106)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BOW).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 423)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_BOW).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 59).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 529).build()
		).build();
	}

	public static LooniumStructureConfiguration getConfigWoodlandMansion(ResourceLocation parentId) {
		return LooniumStructureConfiguration.forParent(parentId).spawnedMobs(
				LooniumMobSpawnData.entityWeight(EntityType.ENDERMAN, 40).build(),
				getCreeperSpawnData(199, false, getCreeperEffects(false)),
				getCreeperSpawnData(1, true, getCreeperEffects(false)),
				LooniumMobSpawnData.entityWeight(EntityType.VINDICATOR, 600)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_AXE).build(),
				LooniumMobSpawnData.entityWeight(EntityType.PILLAGER, 200)
						.equipmentTable(BotaniaLootTables.LOONIUM_WEAPON_CROSSBOW).build(),
				LooniumMobSpawnData.entityWeight(EntityType.EVOKER, 100).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 150)
						.equipmentTable(BotaniaLootTables.LOONIUM_ARMOR_MANSION).build(),
				LooniumMobSpawnData.entityWeight(EntityType.ZOMBIE, 50).spawnAsBaby()
						.equipmentTable(BotaniaLootTables.LOONIUM_ARMOR_MANSION).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SKELETON, 200)
						.equipmentTable(BotaniaLootTables.LOONIUM_ARMOR_MANSION).build(),
				LooniumMobSpawnData.entityWeight(EntityType.CAVE_SPIDER, 30).build(),
				LooniumMobSpawnData.entityWeight(EntityType.SPIDER, 270).build()
		).build();
	}

	public static LooniumMobEffectToApply[] getStandardEffects(boolean withWaterBreathing, boolean withFireResistance) {
		return withFireResistance
				? (withWaterBreathing
						? new LooniumMobEffectToApply[] {
								LooniumMobEffectToApply.effect(MobEffects.FIRE_RESISTANCE).build(),
								LooniumMobEffectToApply.effect(MobEffects.REGENERATION).build(),
								LooniumMobEffectToApply.effect(MobEffects.WATER_BREATHING).build()
						}
						: new LooniumMobEffectToApply[] {
								LooniumMobEffectToApply.effect(MobEffects.FIRE_RESISTANCE).build(),
								LooniumMobEffectToApply.effect(MobEffects.REGENERATION).build()
						})
				: (withWaterBreathing
						? new LooniumMobEffectToApply[] {
								LooniumMobEffectToApply.effect(MobEffects.REGENERATION).build(),
								LooniumMobEffectToApply.effect(MobEffects.WATER_BREATHING).build()
						}
						: new LooniumMobEffectToApply[] {
								LooniumMobEffectToApply.effect(MobEffects.REGENERATION).build()
						});
	}

	public static LooniumMobEffectToApply[] getCreeperEffects(boolean withWaterBreathing) {
		return withWaterBreathing
				? new LooniumMobEffectToApply[] {
						LooniumMobEffectToApply.effect(MobEffects.FIRE_RESISTANCE).duration(100).build(),
						LooniumMobEffectToApply.effect(MobEffects.REGENERATION).duration(100).build(),
						LooniumMobEffectToApply.effect(MobEffects.WATER_BREATHING).build()
				}
				: new LooniumMobEffectToApply[] {
						LooniumMobEffectToApply.effect(MobEffects.FIRE_RESISTANCE).duration(100).build(),
						LooniumMobEffectToApply.effect(MobEffects.REGENERATION).duration(100).build()
				};
	}

	public static LooniumMobSpawnData getCreeperSpawnData(int weight, boolean charged,
			LooniumMobEffectToApply... creeperEffects) {
		CompoundTag chargedCreeperNbt;
		if (charged) {
			chargedCreeperNbt = new CompoundTag();
			chargedCreeperNbt.putBoolean("powered", true);
		} else {
			chargedCreeperNbt = null;
		}

		return LooniumMobSpawnData.entityWeight(EntityType.CREEPER, weight)
				.nbt(chargedCreeperNbt)
				.effectsToApply(creeperEffects)
				.build();
	}

	public static LooniumMobSpawnData getPiglinSpawnData(int weight, ResourceLocation equipmentTable,
			boolean needWaterBreathing, boolean zombificationImmune) {
		CompoundTag piglinNbt = new CompoundTag();
		if (zombificationImmune) {
			piglinNbt.putBoolean("IsImmuneToZombification", true);
		}

		Brain<LivingEntity> piglinBrain = Brain.provider(List.of(MemoryModuleType.UNIVERSAL_ANGER,
				MemoryModuleType.ADMIRING_DISABLED, MemoryModuleType.ATE_RECENTLY), List.of())
				.makeBrain(new Dynamic<>(NbtOps.INSTANCE));
		piglinBrain.setMemory(MemoryModuleType.UNIVERSAL_ANGER, true);
		piglinBrain.setMemory(MemoryModuleType.ADMIRING_DISABLED, true);
		piglinBrain.setMemory(MemoryModuleType.ATE_RECENTLY, true);

		DataResult<Tag> dataResult = piglinBrain.serializeStart(NbtOps.INSTANCE);
		dataResult.resultOrPartial(BotaniaAPI.LOGGER::error).ifPresent(tag -> piglinNbt.put("Brain", tag));

		return LooniumMobSpawnData.entityWeight(EntityType.PIGLIN, weight)
				.spawnAsAdult()
				.nbt(piglinNbt)
				.equipmentTable(equipmentTable)
				.effectsToApply(getStandardEffects(needWaterBreathing, true))
				.build();
	}

	@NotNull
	@Override
	public String getName() {
		return "Loonium structure configuration";
	}
}
