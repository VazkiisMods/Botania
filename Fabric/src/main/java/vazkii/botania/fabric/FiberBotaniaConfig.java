/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.lib.LibMisc;
import vazkii.botania.xplat.BotaniaConfig;
import vazkii.botania.xplat.XplatAbstractions;

import java.io.*;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;

import static io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes.*;

import io.github.fablabsmc.fablabs.api.fiber.v1.builder.ConfigTreeBuilder;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;

public final class FiberBotaniaConfig {
	private static void writeDefaultConfig(ConfigTree config, Path path, JanksonValueSerializer serializer) {
		try (OutputStream s = new BufferedOutputStream(Files.newOutputStream(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW))) {
			FiberSerialization.serialize(config, s, serializer);
		} catch (FileAlreadyExistsException ignored) {} catch (IOException e) {
			BotaniaAPI.LOGGER.error("Error writing default config", e);
		}
	}

	private static void setupConfig(ConfigTree config, Path p, JanksonValueSerializer serializer) {
		writeDefaultConfig(config, p, serializer);

		try (InputStream s = new BufferedInputStream(Files.newInputStream(p, StandardOpenOption.READ, StandardOpenOption.CREATE))) {
			FiberSerialization.deserialize(config, s, serializer);
		} catch (IOException | ValueDeserializationException e) {
			BotaniaAPI.LOGGER.error("Error loading config from {}", p, e);
		}
	}

	public static void setup() {
		try {
			Files.createDirectory(Paths.get("config"));
		} catch (FileAlreadyExistsException ignored) {} catch (IOException e) {
			BotaniaAPI.LOGGER.warn("Failed to make config dir", e);
		}

		JanksonValueSerializer serializer = new JanksonValueSerializer(false);
		ConfigTree common = COMMON.configure(ConfigTree.builder());
		setupConfig(common, Paths.get("config", LibMisc.MOD_ID + "-common.json5"), serializer);
		BotaniaConfig.setCommon(COMMON);

		if (XplatAbstractions.INSTANCE.isPhysicalClient()) {
			ConfigTree client = CLIENT.configure(ConfigTree.builder());
			setupConfig(client, Paths.get("config", LibMisc.MOD_ID + "-client.json5"), serializer);
			BotaniaConfig.setClient(CLIENT);
		}
		BotaniaConfig.resetPatchouliFlags();
	}

	private static class Client implements BotaniaConfig.ClientConfigAccess {
		public final PropertyMirror<Boolean> lexiconRotatingItems = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> subtlePowerSystem = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> staticWandBeam = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> boundBlockWireframe = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> lexicon3dModel = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Double> flowerParticleFrequency = PropertyMirror.create(DOUBLE);
		public final PropertyMirror<Boolean> elfPortalParticlesEnabled = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> renderAccessories = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> enableSeasonalFeatures = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> enableFancySkybox = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> enableFancySkyboxInNormalWorlds = PropertyMirror.create(BOOLEAN);

		public final PropertyMirror<Integer> manaBarHeight = PropertyMirror.create(NATURAL);

		public final PropertyMirror<Boolean> staticFloaters = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> debugInfo = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> referencesEnabled = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> splashesEnabled = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> useShaders = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> opaqueParticles = PropertyMirror.create(BOOLEAN);

		public ConfigTree configure(ConfigTreeBuilder builder) {
			builder.fork("rendering")
					.beginValue("shaders", BOOLEAN, true)
					.withComment("Set this to false to disable the use of shaders for some of the mod's renders. (Requires game restart)")
					.finishValue(useShaders::mirror)

					.beginValue("opaqueParticles", BOOLEAN, false)
					.withComment("Set this to true to disable translucent particles, for shader compatibility with some shaders which implement a deferred lighting pass. (Enabling without shaders may impact aesthetics)")
					.finishValue(opaqueParticles::mirror)

					.beginValue("boundBlockWireframe", BOOLEAN, true)
					.withComment("Set this to false to disable the wireframe when looking a block bound to something (spreaders, flowers, etc).")
					.finishValue(boundBlockWireframe::mirror)

					.beginValue("accessories", BOOLEAN, true)
					.withComment("Set this to false to disable rendering of accessories in the player.")
					.finishValue(renderAccessories::mirror)

					.beginValue("manaBarHeight", NATURAL, 29)
					.withComment("The height of the mana display bar in above the XP bar. You can change this if you have a mod that changes where the XP bar is.")
					.finishValue(manaBarHeight::mirror)

					.beginValue("staticFloaters", BOOLEAN, false)
					.withComment("Set this to true if you use lots of floating flowers and are experiencing rendering lag. Will disable the floating flowers' animations and render them statically for a major performance boost. Hit F3+A in-world after toggling this.")
					.finishValue(staticFloaters::mirror)
					.finishBranch()

					.fork("fancySkybox")
					.beginValue("enabled", BOOLEAN, true)
					.withComment("Set this to false to disable the fancy skybox in Garden of Glass")
					.finishValue(enableFancySkybox::mirror)

					.beginValue("normalWorlds", BOOLEAN, false)
					.withComment("Set this to true to enable the fancy skybox in non Garden of Glass worlds. (Does not require Garden of Glass loaded to use, needs 'fancySkybox.enabled' to be true as well)")
					.finishValue(enableFancySkyboxInNormalWorlds::mirror)
					.finishBranch()

					.fork("lexicon")
					.beginValue("rotatingItems", BOOLEAN, true)
					.withComment("Set this to false to disable the rotating items in the petal and rune entries in the Lexica Botania.")
					.finishValue(lexiconRotatingItems::mirror)

					.beginValue("render_3d", BOOLEAN, true)
					.withComment("Set this to false to disable the animated 3D render for the Lexica Botania.")
					.finishValue(lexicon3dModel::mirror)
					.finishBranch()

					.fork("particles")
					.beginValue("powerSystem", BOOLEAN, false)
					.withComment("Set this to true to set the power system's particles to be a lot more subtle. Good for low-end systems, if the particles are causing lag.")
					.finishValue(subtlePowerSystem::mirror)

					.beginValue("staticWandBeam", BOOLEAN, false)
					.withComment("Set this to true to use a static wand beam that shows every single position of the burst, similar to the way it used to work on old Botania versions. Warning: Disabled by default because it may be laggy.")
					.finishValue(staticWandBeam::mirror)

					.beginValue("flowerFrequency", DOUBLE, 0.75)
					.withComment("The frequency in which particles spawn from normal (worldgen) mystical flowers")
					.finishValue(flowerParticleFrequency::mirror)

					.beginValue("elvenPortal", BOOLEAN, true)
					.withComment("Set this to false to disable the particles in the elven portal.")
					.finishValue(elfPortalParticlesEnabled::mirror)
					.finishBranch()

					.beginValue("seasonalFeatures", BOOLEAN, true)
					.withComment("Set this to false to disable seasonal features, such as halloween and christmas.")
					.finishValue(enableSeasonalFeatures::mirror)

					.beginValue("debugInfo", BOOLEAN, true)
					.withComment("Set to false to disable Botania's messages in the F3 debug screen")
					.finishValue(debugInfo::mirror)

					.beginValue("references", BOOLEAN, true)
					.withComment("Set this to false to disable the references in the flower tooltips. (You monster D:)")
					.finishValue(referencesEnabled::mirror)

					.beginValue("splashes", BOOLEAN, true)
					.withComment("Set this to false to disable Botania's splashes in the main menu.")
					.finishValue(splashesEnabled::mirror);

			return builder.build();
		}

		@Override
		public boolean lexiconRotatingItems() {
			return lexiconRotatingItems.getValue();
		}

		@Override
		public boolean subtlePowerSystem() {
			return subtlePowerSystem.getValue();
		}

		@Override
		public boolean staticWandBeam() {
			return staticWandBeam.getValue();
		}

		@Override
		public boolean boundBlockWireframe() {
			return boundBlockWireframe.getValue();
		}

		@Override
		public boolean lexicon3dModel() {
			return lexicon3dModel.getValue();
		}

		@Override
		public double flowerParticleFrequency() {
			return flowerParticleFrequency.getValue();
		}

		@Override
		public boolean elfPortalParticlesEnabled() {
			return elfPortalParticlesEnabled.getValue();
		}

		@Override
		public boolean renderAccessories() {
			return renderAccessories.getValue();
		}

		@Override
		public boolean enableSeasonalFeatures() {
			return enableSeasonalFeatures.getValue();
		}

		@Override
		public boolean enableFancySkybox() {
			return enableFancySkybox.getValue();
		}

		@Override
		public boolean enableFancySkyboxInNormalWorlds() {
			return enableFancySkyboxInNormalWorlds.getValue();
		}

		@Override
		public int manaBarHeight() {
			return manaBarHeight.getValue();
		}

		@Override
		public boolean staticFloaters() {
			return staticFloaters.getValue();
		}

		@Override
		public boolean debugInfo() {
			return debugInfo.getValue();
		}

		@Override
		public boolean referencesEnabled() {
			return referencesEnabled.getValue();
		}

		@Override
		public boolean splashesEnabled() {
			return splashesEnabled.getValue();
		}

		@Override
		public boolean useShaders() {
			return this.useShaders.getValue();
		}

		@Override
		public boolean opaqueParticles() {
			return this.opaqueParticles.getValue();
		}

	}

	private static final Client CLIENT = new Client();

	private static class Common implements BotaniaConfig.ConfigAccess {
		public final PropertyMirror<Boolean> blockBreakParticles = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> blockBreakParticlesTool = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> chargingAnimationEnabled = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> silentSpreaders = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Integer> spreaderTraceTime = PropertyMirror.create(INTEGER);
		public final PropertyMirror<Boolean> enderPickpocketEnabled = PropertyMirror.create(BOOLEAN);

		public final PropertyMirror<Boolean> enchanterEnabled = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> relicsEnabled = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Boolean> invertMagnetRing = PropertyMirror.create(BOOLEAN);

		public final PropertyMirror<Integer> harvestLevelWeight = PropertyMirror.create(NATURAL.withMinimum(0).withMaximum(4));
		public final PropertyMirror<Integer> harvestLevelBore = PropertyMirror.create(NATURAL.withMinimum(0).withMaximum(4));

		public final PropertyMirror<Boolean> gogSpawnWithLexicon = PropertyMirror.create(BOOLEAN);
		public final PropertyMirror<Integer> gogIslandScaleMultiplier = PropertyMirror.create(INTEGER);
		public final PropertyMirror<List<String>> rannuncarpusItemBlacklist = PropertyMirror.create(ConfigTypes.makeList(STRING));
		public final PropertyMirror<List<String>> rannuncarpusModBlacklist = PropertyMirror.create(ConfigTypes.makeList(STRING));

		public ConfigTree configure(ConfigTreeBuilder builder) {
			builder.fork("blockBreakingParticles")
					.beginValue("enabled", BOOLEAN, true)
					.withComment("Set this to false to remove the block breaking particles from the flowers and other items in the mod.")
					.finishValue(blockBreakParticles::mirror)

					.beginValue("toolEnabled", BOOLEAN, true)
					.withComment("Set this to false to remove the block breaking particles from the Terra Shatterer, as there can be a good amount in higher levels.")
					.finishValue(blockBreakParticlesTool::mirror)
					.finishBranch()

					.fork("manaSpreaders")
					.beginValue("silent", BOOLEAN, false)
					.withComment("Set this to true to disable the mana spreader shooting sound")
					.finishValue(silentSpreaders::mirror)

					.beginValue("traceTime", NATURAL, 400)
					.withComment("How many ticks into the future will mana spreaders attempt to predict where mana bursts go? Setting this lower will improve spreader performance, but will cause them to not fire at targets that are too far away.")
					.finishValue(spreaderTraceTime::mirror)
					.finishBranch()

					.fork("harvestLevels")
					.beginValue("weightLens", NATURAL, 2)
					.withComment("The harvest level of the Mana Lens: Weight. 3 is diamond level. Defaults to 2 (iron level)")
					.finishValue(harvestLevelWeight::mirror)

					.beginValue("boreLens", NATURAL, 3)
					.withComment("The harvest level of the Mana Lens: Bore. 3 is diamond level. Defaults to 3")
					.finishValue(harvestLevelBore::mirror)
					.finishBranch()

					.beginValue("chargeAnimation", BOOLEAN, true)
					.withComment("Set this to false to disable the animation when an item is charging on top of a mana pool")
					.finishValue(chargingAnimationEnabled::mirror)

					.beginValue("enderPickpocket", BOOLEAN, true)
					.withComment("Set to false to disable the ability for the Hand of Ender to pickpocket other players' ender chests")
					.finishValue(enderPickpocketEnabled::mirror)

					.beginValue("manaEnchanter", BOOLEAN, true)
					.withComment("Set this to false to disable the Mana Enchanter. Since some people find it OP or something. This only disables the entry and creation. Old ones that are already in the world will stay.")
					.finishValue(enchanterEnabled::mirror)

					.beginValue("relics", BOOLEAN, true)
					.withComment("Set this to false to disable the Relic System. This only disables the entries, drops and achievements. Old ones that are already in the world will stay.")
					.finishValue(relicsEnabled::mirror)

					.beginValue("invertMagnetRing", BOOLEAN, false)
					.withComment("Set this to true to invert the Ring of Magnetization's controls (from shift to stop to shift to work)")
					.finishValue(invertMagnetRing::mirror)

					.beginValue("gardenOfGlass.spawnWithLexicon", BOOLEAN, true)
					.withComment("Set this to false to disable spawning with a Lexica Botania in Garden of Glass worlds, if you are modifying the modpack's progression to not start with Botania.")
					.finishValue(gogSpawnWithLexicon::mirror)

					.beginValue("gardenOfGlass.islandScaleMultiplier", INTEGER.withMinimum(1).withMaximum(512), 8)
					.withComment("""
						The multiplier for island distances for multiplayer Garden of Glass worlds.
						Islands are placed on a grid with 256 blocks between points, with the spawn island always being placed on 256, 256.
						By default, the scale is 8, putting each island on points separated by 2048 blocks.
						Values below 4 (1024 block spacing) are not recommended due to Nether portal collisions.""")
					.finishValue(gogIslandScaleMultiplier::mirror)

					.beginValue("rannuncarpusItemBlackList", ConfigTypes.makeList(STRING), Collections.emptyList())
					.withComment("List of item registry names that will be ignored by rannuncarpuses when placing blocks.")
					.finishValue(rannuncarpusItemBlacklist::mirror)

					.beginValue("rannuncarpusModBlacklist", ConfigTypes.makeList(STRING), Collections.singletonList("storagedrawers"))
					.withComment("List of mod names for rannuncarpuses to ignore.\n" +
							"Ignores Storage Drawers by default due to crashes with placing drawer blocks without player involvement.")
					.finishValue(rannuncarpusModBlacklist::mirror);

			return builder.build();
		}

		@Override
		public boolean blockBreakParticles() {
			return blockBreakParticles.getValue();
		}

		@Override
		public boolean blockBreakParticlesTool() {
			return blockBreakParticlesTool.getValue();
		}

		@Override
		public boolean chargingAnimationEnabled() {
			return chargingAnimationEnabled.getValue();
		}

		@Override
		public boolean silentSpreaders() {
			return silentSpreaders.getValue();
		}

		@Override
		public int spreaderTraceTime() {
			return spreaderTraceTime.getValue();
		}

		@Override
		public boolean enderPickpocketEnabled() {
			return enderPickpocketEnabled.getValue();
		}

		@Override
		public boolean enchanterEnabled() {
			return enchanterEnabled.getValue();
		}

		@Override
		public boolean relicsEnabled() {
			return relicsEnabled.getValue();
		}

		@Override
		public boolean invertMagnetRing() {
			return invertMagnetRing.getValue();
		}

		@Override
		public int harvestLevelWeight() {
			return harvestLevelWeight.getValue();
		}

		@Override
		public int harvestLevelBore() {
			return harvestLevelBore.getValue();
		}

		@Override
		public boolean gogSpawnWithLexicon() {
			return gogSpawnWithLexicon.getValue();
		}

		@Override
		public int gogIslandScaleMultiplier() {
			return gogIslandScaleMultiplier.getValue();
		}

		@Override
		public List<String> rannuncarpusItemBlacklist() {
			return rannuncarpusItemBlacklist.getValue();
		}

		@Override
		public List<String> rannuncarpusModBlacklist() {
			return rannuncarpusModBlacklist.getValue();
		}
	}

	private static final Common COMMON = new Common();
}
