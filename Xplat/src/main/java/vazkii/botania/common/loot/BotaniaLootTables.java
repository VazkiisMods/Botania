package vazkii.botania.common.loot;

import com.google.common.collect.Sets;

import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.Set;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class BotaniaLootTables {
	private static final Set<ResourceLocation> LOCATIONS = Sets.newHashSet();
	private static final Set<ResourceLocation> IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LOCATIONS);

	public static final ResourceLocation LOONIUM_DEFAULT_LOOT = register("loonium/default");

	// TODO 1.21: embed armor set and weapon equipment tables
	public static final ResourceLocation LOONIUM_ARMORSET_COAST_CHAIN = register("equipment/loonium/armorset/coast_chain");
	public static final ResourceLocation LOONIUM_ARMORSET_COAST_IRON = register("equipment/loonium/armorset/coast_iron");
	public static final ResourceLocation LOONIUM_ARMORSET_COAST_DIAMOND = register("equipment/loonium/armorset/coast_diamond");

	public static final ResourceLocation LOONIUM_ARMORSET_DUNE_GOLD = register("equipment/loonium/armorset/dune_gold");
	public static final ResourceLocation LOONIUM_ARMORSET_DUNE_IRON = register("equipment/loonium/armorset/dune_iron");
	public static final ResourceLocation LOONIUM_ARMORSET_DUNE_DIAMOND = register("equipment/loonium/armorset/dune_diamond");

	public static final ResourceLocation LOONIUM_ARMORSET_EYE_GOLD = register("equipment/loonium/armorset/eye_gold");
	public static final ResourceLocation LOONIUM_ARMORSET_EYE_IRON = register("equipment/loonium/armorset/eye_iron");
	public static final ResourceLocation LOONIUM_ARMORSET_EYE_DIAMOND = register("equipment/loonium/armorset/eye_diamond");

	public static final ResourceLocation LOONIUM_ARMORSET_HOST_CHAIN = register("equipment/loonium/armorset/host_chain");
	public static final ResourceLocation LOONIUM_ARMORSET_HOST_IRON = register("equipment/loonium/armorset/host_iron");

	public static final ResourceLocation LOONIUM_ARMORSET_RAISER_IRON = register("equipment/loonium/armorset/raiser_iron");
	public static final ResourceLocation LOONIUM_ARMORSET_RAISER_GOLD = register("equipment/loonium/armorset/raiser_gold");

	public static final ResourceLocation LOONIUM_ARMORSET_RIB_IRON = register("equipment/loonium/armorset/rib_iron");
	public static final ResourceLocation LOONIUM_ARMORSET_RIB_GOLD = register("equipment/loonium/armorset/rib_gold");
	public static final ResourceLocation LOONIUM_ARMORSET_RIB_DIAMOND = register("equipment/loonium/armorset/rib_diamond");

	public static final ResourceLocation LOONIUM_ARMORSET_SENTRY_CHAIN = register("equipment/loonium/armorset/sentry_chain");
	public static final ResourceLocation LOONIUM_ARMORSET_SENTRY_IRON = register("equipment/loonium/armorset/sentry_iron");
	public static final ResourceLocation LOONIUM_ARMORSET_SENTRY_DIAMOND = register("equipment/loonium/armorset/sentry_diamond");

	public static final ResourceLocation LOONIUM_ARMORSET_SHAPER_GOLD = register("equipment/loonium/armorset/shaper_gold");
	public static final ResourceLocation LOONIUM_ARMORSET_SHAPER_DIAMOND = register("equipment/loonium/armorset/shaper_diamond");

	public static final ResourceLocation LOONIUM_ARMORSET_SILENCE_GOLD = register("equipment/loonium/armorset/silence_gold");
	public static final ResourceLocation LOONIUM_ARMORSET_SILENCE_DIAMOND = register("equipment/loonium/armorset/silence_diamond");

	public static final ResourceLocation LOONIUM_ARMORSET_SNOUT_GOLD = register("equipment/loonium/armorset/snout_gold");
	public static final ResourceLocation LOONIUM_ARMORSET_SNOUT_NETHERITE = register("equipment/loonium/armorset/snout_netherite");

	public static final ResourceLocation LOONIUM_ARMORSET_SPIRE_IRON = register("equipment/loonium/armorset/spire_iron");
	public static final ResourceLocation LOONIUM_ARMORSET_SPIRE_GOLD = register("equipment/loonium/armorset/spire_gold");
	public static final ResourceLocation LOONIUM_ARMORSET_SPIRE_DIAMOND = register("equipment/loonium/armorset/spire_diamond");

	public static final ResourceLocation LOONIUM_ARMORSET_TIDE_LEATHER = register("equipment/loonium/armorset/tide_leather");
	public static final ResourceLocation LOONIUM_ARMORSET_TIDE_GOLD = register("equipment/loonium/armorset/tide_iron");
	public static final ResourceLocation LOONIUM_ARMORSET_TIDE_DIAMOND = register("equipment/loonium/armorset/tide_diamond");

	public static final ResourceLocation LOONIUM_ARMORSET_WARD_IRON = register("equipment/loonium/armorset/ward_iron");
	public static final ResourceLocation LOONIUM_ARMORSET_WARD_DIAMOND = register("equipment/loonium/armorset/ward_diamond");

	public static final ResourceLocation LOONIUM_ARMORSET_WAYFINDER_CHAIN = register("equipment/loonium/armorset/wayfinder_chain");
	public static final ResourceLocation LOONIUM_ARMORSET_WAYFINDER_DIAMOND = register("equipment/loonium/armorset/wayfinder_diamond");

	public static final ResourceLocation LOONIUM_ARMORSET_WILD_CHAIN = register("equipment/loonium/armorset/wild_chain");
	public static final ResourceLocation LOONIUM_ARMORSET_WILD_GOLD = register("equipment/loonium/armorset/wild_gold");
	public static final ResourceLocation LOONIUM_ARMORSET_WILD_DIAMOND = register("equipment/loonium/armorset/wild_diamond");

	public static final ResourceLocation LOONIUM_ARMORSET_COSTUME_ENDERMAN = register("equipment/loonium/armorset/costume_enderman");
	public static final ResourceLocation LOONIUM_ARMORSET_COSTUME_EVOKER = register("equipment/loonium/armorset/costume_evoker");
	public static final ResourceLocation LOONIUM_ARMORSET_COSTUME_VINDICATOR = register("equipment/loonium/armorset/costume_vindicator");
	public static final ResourceLocation LOONIUM_ARMORSET_COSTUME_ILLUSIONER = register("equipment/loonium/armorset/costume_illusioner");
	public static final ResourceLocation LOONIUM_ARMORSET_COSTUME_VEX = register("equipment/loonium/armorset/costume_vex");

	public static final ResourceLocation LOONIUM_WEAPON_AXE = register("equipment/loonium/weapon_axe");
	public static final ResourceLocation LOONIUM_WEAPON_AXE_GOLD = register("equipment/loonium/weapon_axe_gold");
	public static final ResourceLocation LOONIUM_WEAPON_BOW = register("equipment/loonium/weapon_bow");
	public static final ResourceLocation LOONIUM_WEAPON_CROSSBOW = register("equipment/loonium/weapon_crossbow");
	public static final ResourceLocation LOONIUM_WEAPON_SWORD = register("equipment/loonium/weapon_sword");
	public static final ResourceLocation LOONIUM_WEAPON_SWORD_GOLD = register("equipment/loonium/weapon_sword_gold");
	public static final ResourceLocation LOONIUM_WEAPON_TRIDENT = register("equipment/loonium/weapon_trident");
	public static final ResourceLocation LOONIUM_WEAPON_BY_PROFESSION = register("equipment/loonium/weapon_by_profession");
	public static final ResourceLocation LOONIUM_WEAPON_FOR_PIGLIN = register("equipment/loonium/weapon_for_piglin");
	public static final ResourceLocation LOONIUM_WEAPON_FOR_WITHER_SKELETON = register("equipment/loonium/weapon_for_wither_skeleton");

	public static final ResourceLocation LOONIUM_ARMOR_ANCIENT_CITY = register("equipment/loonium/armor_ancient_city");
	public static final ResourceLocation LOONIUM_ARMOR_BASTION_REMNANT = register("equipment/loonium/armor_bastion_remnant");
	public static final ResourceLocation LOONIUM_ARMOR_DESERT_PYRAMID = register("equipment/loonium/armor_desert_pyramid");
	public static final ResourceLocation LOONIUM_ARMOR_END_CITY = register("equipment/loonium/armor_end_city");
	public static final ResourceLocation LOONIUM_ARMOR_FORTRESS = register("equipment/loonium/armor_fortress");
	public static final ResourceLocation LOONIUM_ARMOR_JUNGLE_TEMPLE = register("equipment/loonium/armor_jungle_temple");
	public static final ResourceLocation LOONIUM_ARMOR_MANSION = register("equipment/loonium/armor_mansion");
	public static final ResourceLocation LOONIUM_ARMOR_MONUMENT = register("equipment/loonium/armor_monument");
	public static final ResourceLocation LOONIUM_ARMOR_OUTPOST = register("equipment/loonium/armor_outpost");
	public static final ResourceLocation LOONIUM_ARMOR_PORTAL = register("equipment/loonium/armor_portal");
	public static final ResourceLocation LOONIUM_ARMOR_SHIPWRECK = register("equipment/loonium/armor_shipwreck");
	public static final ResourceLocation LOONIUM_ARMOR_STRONGHOLD = register("equipment/loonium/armor_stronghold");
	public static final ResourceLocation LOONIUM_ARMOR_TRAIL_RUINS = register("equipment/loonium/armor_trail_ruins");

	public static final ResourceLocation LOONIUM_DROWNED_ANCIENT_CITY = register("equipment/loonium/drowned_ancient_city");
	public static final ResourceLocation LOONIUM_DROWNED_JUNGLE_TEMPLE = register("equipment/loonium/drowned_jungle_temple");
	public static final ResourceLocation LOONIUM_DROWNED_MONUMENT = register("equipment/loonium/drowned_monument");
	public static final ResourceLocation LOONIUM_DROWNED_PORTAL = register("equipment/loonium/drowned_portal");
	public static final ResourceLocation LOONIUM_DROWNED_SHIPWRECK = register("equipment/loonium/drowned_shipwreck");
	public static final ResourceLocation LOONIUM_DROWNED_STRONGHOLD = register("equipment/loonium/drowned_stronghold");
	public static final ResourceLocation LOONIUM_DROWNED_TRAIL_RUINS = register("equipment/loonium/drowned_trail_ruins");

	public static final ResourceLocation LOONIUM_PIGLIN_BASTION_REMNANT = register("equipment/loonium/piglin_bastion_remnant");
	public static final ResourceLocation LOONIUM_PIGLIN_PORTAL = register("equipment/loonium/piglin_ruined_portal");

	public static final ResourceLocation LOONIUM_SKELETON_ANCIENT_CITY = register("equipment/loonium/skeleton_ancient_city");
	public static final ResourceLocation LOONIUM_SKELETON_DESERT_PYRAMID = register("equipment/loonium/skeleton_desert_pyramid");
	public static final ResourceLocation LOONIUM_SKELETON_JUNGLE_TEMPLE = register("equipment/loonium/skeleton_jungle_temple");
	public static final ResourceLocation LOONIUM_SKELETON_END_CITY = register("equipment/loonium/skeleton_end_city");
	public static final ResourceLocation LOONIUM_SKELETON_FORTRESS = register("equipment/loonium/skeleton_fortress");
	public static final ResourceLocation LOONIUM_SKELETON_MONUMENT = register("equipment/loonium/skeleton_monument");
	public static final ResourceLocation LOONIUM_SKELETON_OUTPOST = register("equipment/loonium/skeleton_outpost");
	public static final ResourceLocation LOONIUM_SKELETON_PORTAL = register("equipment/loonium/skeleton_portal");
	public static final ResourceLocation LOONIUM_SKELETON_SHIPWRECK = register("equipment/loonium/skeleton_shipwreck");
	public static final ResourceLocation LOONIUM_SKELETON_STRONGHOLD = register("equipment/loonium/skeleton_stronghold");
	public static final ResourceLocation LOONIUM_SKELETON_TRAIL_RUINS = register("equipment/loonium/skeleton_trail_ruins");

	public static final ResourceLocation LOONIUM_ZOMBIE_ANCIENT_CITY = register("equipment/loonium/zombie_ancient_city");
	public static final ResourceLocation LOONIUM_ZOMBIE_DESERT_PYRAMID = register("equipment/loonium/zombie_desert_pyramid");
	public static final ResourceLocation LOONIUM_ZOMBIE_END_CITY = register("equipment/loonium/zombie_end_city");
	public static final ResourceLocation LOONIUM_ZOMBIE_FORTRESS = register("equipment/loonium/zombie_fortress");
	public static final ResourceLocation LOONIUM_ZOMBIE_JUNGLE_TEMPLE = register("equipment/loonium/zombie_jungle_temple");
	public static final ResourceLocation LOONIUM_ZOMBIE_MONUMENT = register("equipment/loonium/zombie_monument");
	public static final ResourceLocation LOONIUM_ZOMBIE_OUTPOST = register("equipment/loonium/zombie_outpost");
	public static final ResourceLocation LOONIUM_ZOMBIE_PORTAL = register("equipment/loonium/zombie_portal");
	public static final ResourceLocation LOONIUM_ZOMBIE_SHIPWRECK = register("equipment/loonium/zombie_shipwreck");
	public static final ResourceLocation LOONIUM_ZOMBIE_STRONGHOLD = register("equipment/loonium/zombie_stronghold");
	public static final ResourceLocation LOONIUM_ZOMBIE_TRAIL_RUINS = register("equipment/loonium/zombie_trail_ruins");

	private static ResourceLocation register(String path) {
		return register(prefix(path));
	}

	private static ResourceLocation register(ResourceLocation location) {
		if (LOCATIONS.add(location)) {
			return location;
		} else {
			throw new IllegalArgumentException(location + " is already a registered built-in loot table");
		}
	}

	public static Set<ResourceLocation> all() {
		return IMMUTABLE_LOCATIONS;
	}
}
