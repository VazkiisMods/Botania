/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.forge;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;

import org.apache.commons.lang3.tuple.Pair;

import vazkii.botania.common.lib.LibMisc;
import vazkii.botania.xplat.BotaniaConfig;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(modid = LibMisc.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ForgeBotaniaConfig {

	private static class Client implements BotaniaConfig.ClientConfigAccess {
		public final ModConfigSpec.BooleanValue useShaders;
		public final ModConfigSpec.BooleanValue lexiconRotatingItems;
		public final ModConfigSpec.BooleanValue subtlePowerSystem;
		public final ModConfigSpec.BooleanValue staticWandBeam;
		public final ModConfigSpec.BooleanValue boundBlockWireframe;
		public final ModConfigSpec.BooleanValue lexicon3dModel;
		public final ModConfigSpec.DoubleValue flowerParticleFrequency;
		public final ModConfigSpec.BooleanValue elfPortalParticlesEnabled;
		public final ModConfigSpec.BooleanValue renderAccessories;
		public final ModConfigSpec.BooleanValue enableSeasonalFeatures;
		public final ModConfigSpec.BooleanValue enableFancySkybox;
		public final ModConfigSpec.BooleanValue enableFancySkyboxInNormalWorlds;

		public final ModConfigSpec.IntValue manaBarHeight;

		public final ModConfigSpec.BooleanValue staticFloaters;
		public final ModConfigSpec.BooleanValue debugInfo;
		public final ModConfigSpec.BooleanValue referencesEnabled;
		public final ModConfigSpec.BooleanValue splashesEnabled;

		public Client(ModConfigSpec.Builder builder) {
			builder.push("rendering");
			useShaders = builder
					.comment("Set this to false to disable the use of shaders for some of the mod's renders. (Requires game restart)")
					.define("shaders", true);
			boundBlockWireframe = builder
					.comment("Set this to false to disable the wireframe when looking a block bound to something (spreaders, flowers, etc).")
					.define("boundBlockWireframe", true);
			renderAccessories = builder
					.comment("Set this to false to disable rendering of accessories in the player.")
					.define("accessories", true);
			manaBarHeight = builder
					.comment("The height of the mana display bar in above the XP bar. You can change this if you have a mod that changes where the XP bar is.")
					.defineInRange("manaBarHeight", 29, 0, Integer.MAX_VALUE);
			staticFloaters = builder
					.comment("Set this to true if you use lots of floating flowers and are experiencing rendering lag. Will disable the floating flowers' animations and render them statically for a major performance boost. Hit F3+A in-world after toggling this.")
					.define("staticFloaters", false);

			builder.push("fancySkybox");
			enableFancySkybox = builder
					.comment("Set this to false to disable the fancy skybox in Garden of Glass")
					.define("enabled", true);
			enableFancySkyboxInNormalWorlds = builder
					.comment("Set this to true to enable the fancy skybox in non Garden of Glass worlds. (Does not require Garden of Glass loaded to use, needs 'fancySkybox.enabled' to be true as well)")
					.define("normalWorlds", false);
			builder.pop();

			builder.push("lexicon");
			lexiconRotatingItems = builder
					.comment("Set this to false to disable the rotating items in the petal and rune entries in the Lexica Botania.")
					.define("rotatingItems", true);
			lexicon3dModel = builder
					.comment("Set this to false to disable the animated 3D render for the Lexica Botania.")
					.define("render_3d", true);
			builder.pop(2);

			builder.push("particles");
			subtlePowerSystem = builder
					.comment("Set this to true to set the power system's particles to be a lot more subtle. Good for low-end systems, if the particles are causing lag.")
					.define("powerSystem", false);
			staticWandBeam = builder
					.comment("Set this to true to use a static wand beam that shows every single position of the burst, similar to the way it used to work on old Botania versions. Warning: Disabled by default because it may be laggy.")
					.define("staticWandBeam", false);
			flowerParticleFrequency = builder
					.comment("The frequency in which particles spawn from normal (worldgen) mystical flowers")
					.defineInRange("flowerFrequency", 0.75, Double.MIN_VALUE, Double.MAX_VALUE);
			elfPortalParticlesEnabled = builder
					.comment("Set this to false to disable the particles in the elven portal.")
					.define("elvenPortal", true);
			builder.pop();

			enableSeasonalFeatures = builder
					.comment("Set this to false to disable seasonal features, such as halloween and christmas.")
					.define("seasonalFeatures", true);
			debugInfo = builder
					.comment("Set to false to disable Botania's messages in the F3 debug screen")
					.define("debugInfo", true);
			referencesEnabled = builder
					.comment("Set this to false to disable the references in the flower tooltips. (You monster D:)")
					.define("references", true);
			splashesEnabled = builder
					.comment("Set this to false to disable Botania's splashes in the main menu.")
					.define("splashes", true);
		}

		@Override
		public boolean lexiconRotatingItems() {
			return lexiconRotatingItems.get();
		}

		@Override
		public boolean subtlePowerSystem() {
			return subtlePowerSystem.get();
		}

		@Override
		public boolean staticWandBeam() {
			return staticWandBeam.get();
		}

		@Override
		public boolean boundBlockWireframe() {
			return boundBlockWireframe.get();
		}

		@Override
		public boolean lexicon3dModel() {
			return lexicon3dModel.get();
		}

		@Override
		public double flowerParticleFrequency() {
			return flowerParticleFrequency.get();
		}

		@Override
		public boolean elfPortalParticlesEnabled() {
			return elfPortalParticlesEnabled.get();
		}

		@Override
		public boolean renderAccessories() {
			return renderAccessories.get();
		}

		@Override
		public boolean enableSeasonalFeatures() {
			return enableSeasonalFeatures.get();
		}

		@Override
		public boolean enableFancySkybox() {
			return enableFancySkybox.get();
		}

		@Override
		public boolean enableFancySkyboxInNormalWorlds() {
			return enableFancySkyboxInNormalWorlds.get();
		}

		@Override
		public int manaBarHeight() {
			return manaBarHeight.get();
		}

		@Override
		public boolean staticFloaters() {
			return staticFloaters.get();
		}

		@Override
		public boolean debugInfo() {
			return debugInfo.get();
		}

		@Override
		public boolean referencesEnabled() {
			return referencesEnabled.get();
		}

		@Override
		public boolean splashesEnabled() {
			return splashesEnabled.get();
		}

		@Override
		public boolean useShaders() {
			return this.useShaders.get();
		}
	}

	public static final Client CLIENT;
	public static final ModConfigSpec CLIENT_SPEC;
	static {
		final Pair<Client, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = specPair.getRight();
		CLIENT = specPair.getLeft();
	}

	private static class Common implements BotaniaConfig.ConfigAccess {
		public final ModConfigSpec.BooleanValue blockBreakParticles;
		public final ModConfigSpec.BooleanValue blockBreakParticlesTool;
		public final ModConfigSpec.BooleanValue chargingAnimationEnabled;
		public final ModConfigSpec.BooleanValue silentSpreaders;
		public final ModConfigSpec.IntValue spreaderTraceTime;
		public final ModConfigSpec.BooleanValue flowerForceCheck;
		public final ModConfigSpec.BooleanValue enderPickpocketEnabled;

		public final ModConfigSpec.BooleanValue enchanterEnabled;
		public final ModConfigSpec.BooleanValue relicsEnabled;
		public final ModConfigSpec.BooleanValue invertMagnetRing;

		public final ModConfigSpec.IntValue harvestLevelWeight;
		public final ModConfigSpec.IntValue harvestLevelBore;

		public final ModConfigSpec.BooleanValue gogSpawnWithLexicon;
		public final ModConfigSpec.IntValue gogIslandScaleMultiplier;

		public final ModConfigSpec.ConfigValue<List<? extends String>> rannuncarpusItemBlacklist;
		public final ModConfigSpec.ConfigValue<List<? extends String>> rannuncarpusModBlacklist;

		public Common(ModConfigSpec.Builder builder) {
			builder.push("blockBreakingParticles");
			blockBreakParticles = builder
					.comment("Set this to false to remove the block breaking particles from the flowers and other items in the mod.")
					.define("enabled", true);
			blockBreakParticlesTool = builder
					.comment("Set this to false to remove the block breaking particles from the Terra Shatterer, as there can be a good amount in higher levels.")
					.define("toolEnabled", true);
			builder.pop();

			builder.push("manaSpreaders");
			silentSpreaders = builder
					.comment("Set this to true to disable the mana spreader shooting sound")
					.define("silent", false);
			spreaderTraceTime = builder
					.comment("How many ticks into the future will mana spreaders attempt to predict where mana bursts go? Setting this lower will improve spreader performance, but will cause them to not fire at targets that are too far away.")
					.defineInRange("traceTime", 400, 1, Integer.MAX_VALUE);
			builder.pop();

			builder.push("harvestLevels");
			harvestLevelWeight = builder
					.comment("The harvest level of the Mana Lens: Weight. 3 is diamond level. Defaults to 2 (iron level)")
					.defineInRange("weightLens", 2, 0, Integer.MAX_VALUE);
			harvestLevelBore = builder
					.comment("The harvest level of the Mana Lens: Bore. 3 is diamond level. Defaults to 3")
					.defineInRange("boreLens", 3, 0, Integer.MAX_VALUE);
			builder.pop();

			chargingAnimationEnabled = builder
					.comment("Set this to false to disable the animation when an item is charging on top of a mana pool")
					.define("chargeAnimation", true);
			flowerForceCheck = builder
					.comment("Turn this off ONLY IF you're on an extremely large world with an exaggerated count of Mana Spreaders/Mana Pools and are experiencing TPS lag. This toggles whether flowers are strict with their checking for connecting to pools/spreaders or just check whenever possible.")
					.define("flowerBindingForceCheck", true);
			enderPickpocketEnabled = builder
					.comment("Set to false to disable the ability for the Hand of Ender to pickpocket other players' ender chests")
					.define("enderPickpocket", true);
			enchanterEnabled = builder
					.comment("Set this to false to disable the Mana Enchanter. Since some people find it OP or something. This only disables the entry and creation. Old ones that are already in the world will stay.")
					.define("manaEnchanter", true);
			relicsEnabled = builder
					.comment("Set this to false to disable the Relic System. This only disables the entries, drops and achievements. Old ones that are already in the world will stay.")
					.define("relics", true);
			invertMagnetRing = builder
					.comment("Set this to true to invert the Ring of Magnetization's controls (from shift to stop to shift to work)")
					.define("invertMagnetRing", false);
			gogSpawnWithLexicon = builder
					.comment("Set this to false to disable spawning with a Lexica Botania in Garden of Glass worlds, if you are modifying the modpack's progression to not start with Botania.")
					.define("gardenOfGlass.spawnWithLexicon", true);
			gogIslandScaleMultiplier = builder
					.comment("""
						The multiplier for island distances for multiplayer Garden of Glass worlds.
						Islands are placed on a grid with 256 blocks between points, with the spawn island always being placed on 256, 256.
						By default, the scale is 8, putting each island on points separated by 2048 blocks.
						Values below 4 (1024 block spacing) are not recommended due to Nether portal collisions.""")
					.defineInRange("gardenOfGlass.islandScaleMultiplier", 8, 1, 512);
			rannuncarpusItemBlacklist = builder
					.comment("List of item registry names that will be ignored by rannuncarpuses when placing blocks.")
					.defineList("rannuncarpus.itemBlacklist", Collections.emptyList(), o -> o instanceof String s && ResourceLocation.tryParse(s) != null);
			rannuncarpusModBlacklist = builder
					.comment("List of mod names for rannuncarpuses to ignore.\n" +
							"Ignores Storage Drawers by default due to crashes with placing drawer blocks without player involvement.")
					.defineList("rannuncarpus.modBlacklist", Collections.singletonList("storagedrawers"), o -> o instanceof String s && ResourceLocation.tryParse(s + ":test") != null);
		}

		@Override
		public boolean blockBreakParticles() {
			return blockBreakParticles.get();
		}

		@Override
		public boolean blockBreakParticlesTool() {
			return blockBreakParticlesTool.get();
		}

		@Override
		public boolean chargingAnimationEnabled() {
			return chargingAnimationEnabled.get();
		}

		@Override
		public boolean silentSpreaders() {
			return silentSpreaders.get();
		}

		@Override
		public int spreaderTraceTime() {
			return spreaderTraceTime.get();
		}

		@Override
		public boolean enderPickpocketEnabled() {
			return enderPickpocketEnabled.get();
		}

		@Override
		public boolean enchanterEnabled() {
			return enchanterEnabled.get();
		}

		@Override
		public boolean relicsEnabled() {
			return relicsEnabled.get();
		}

		@Override
		public boolean invertMagnetRing() {
			return invertMagnetRing.get();
		}

		@Override
		public int harvestLevelWeight() {
			return harvestLevelWeight.get();
		}

		@Override
		public int harvestLevelBore() {
			return harvestLevelBore.get();
		}

		@Override
		public boolean gogSpawnWithLexicon() {
			return gogSpawnWithLexicon.get();
		}

		@Override
		public int gogIslandScaleMultiplier() {
			return gogIslandScaleMultiplier.get();
		}

		@SuppressWarnings("unchecked") // NightConfig's types are weird
		@Override
		public List<String> rannuncarpusItemBlacklist() {
			return (List<String>) rannuncarpusItemBlacklist.get();
		}

		@SuppressWarnings("unchecked") // NightConfig's types are weird
		@Override
		public List<String> rannuncarpusModBlacklist() {
			return (List<String>) rannuncarpusModBlacklist.get();
		}
	}

	private static final Common COMMON;
	private static final ModConfigSpec COMMON_SPEC;
	static {
		final Pair<Common, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = specPair.getRight();
		COMMON = specPair.getLeft();
	}

	public static void setup() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_SPEC);
		BotaniaConfig.setCommon(COMMON);

		if (XplatAbstractions.INSTANCE.isPhysicalClient()) {
			ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_SPEC);
			BotaniaConfig.setClient(CLIENT);
		}
	}

	@SubscribeEvent
	public static void onConfigLoad(ModConfigEvent.Loading evt) {
		var config = evt.getConfig();
		if (config.getType() == ModConfig.Type.COMMON && config.getModId().equals(LibMisc.MOD_ID)) {
			BotaniaConfig.resetPatchouliFlags();
		}
	}

	@SubscribeEvent
	public static void onConfigLoad(ModConfigEvent.Reloading evt) {
		var config = evt.getConfig();
		if (config.getType() == ModConfig.Type.COMMON && config.getModId().equals(LibMisc.MOD_ID)) {
			BotaniaConfig.resetPatchouliFlags();
		}
	}
}
