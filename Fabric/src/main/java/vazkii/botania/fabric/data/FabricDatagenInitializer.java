/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.damagesource.DamageType;

import vazkii.botania.data.*;
import vazkii.botania.data.recipes.*;

import java.io.DataOutput;

import static vazkii.botania.common.BotaniaDamageTypes.*;

public class FabricDatagenInitializer implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		if (System.getProperty("botania.xplat_datagen") != null) {
			configureXplatDatagen(generator.createPack());
		} else {
			configureFabricDatagen(generator.createPack());
		}
	}

	private static void configureFabricDatagen(FabricDataGenerator.Pack pack) {
		pack.addProvider(FabricBlockLootProvider::new);
		var blockTagProvider = pack.addProvider(FabricBlockTagProvider::new);
		pack.addProvider((output, registriesFuture) -> new FabricItemTagProvider(output, registriesFuture, blockTagProvider.contentsGetter()));
		pack.addProvider(FabricRecipeProvider::new);
		pack.addProvider(FabricBiomeTagProvider::new);
	}

	private static void configureXplatDatagen(FabricDataGenerator.Pack pack) {
		pack.addProvider(BlockLootProvider::new);
		pack.addProvider(LooniumStructureLootProvider::new);
		pack.addProvider(LooniumStructureConfigurationProvider::new);
		pack.addProvider(LooniumEquipmentLootProvider::new);
		BlockTagProvider blockTagProvider = pack.addProvider(BlockTagProvider::new);
		pack.addProvider((output, registriesFuture) -> new ItemTagProvider(output, registriesFuture, blockTagProvider.contentsGetter()));
		pack.addProvider(EntityTagProvider::new);
		pack.addProvider(BannerPatternTagsProvider::new);
		pack.addProvider(BiomeTagProvider::new);
		pack.addProvider(BotaniaDynamicRegistryProvider::new);
		pack.addProvider(DamageTypeTagProvider::new);
		pack.addProvider(StonecuttingProvider::new);
		pack.addProvider(CraftingRecipeProvider::new);
		pack.addProvider(SmeltingProvider::new);
		pack.addProvider(ElvenTradeProvider::new);
		pack.addProvider(ManaInfusionProvider::new);
		pack.addProvider(PureDaisyProvider::new);
		pack.addProvider(BrewProvider::new);
		pack.addProvider(PetalApothecaryProvider::new);
		pack.addProvider(RunicAltarProvider::new);
		pack.addProvider(TerrestrialAgglomerationProvider::new);
		pack.addProvider(OrechidProvider::new);
		pack.addProvider((PackOutput output) -> new BlockstateProvider(output));
		pack.addProvider((PackOutput output) -> new FloatingFlowerModelProvider(output));
		pack.addProvider((PackOutput output) -> new ItemModelProvider(output));
		pack.addProvider((PackOutput output) -> new PottedPlantModelProvider(output));
		pack.addProvider(AdvancementProvider::create);
	}

	@Override
	public void buildRegistry(RegistrySetBuilder builder) {
		builder.add(Registries.DAMAGE_TYPE, FabricDatagenInitializer::damageTypeBC);
	}

	protected static void damageTypeBC(BootstrapContext<DamageType> context) {
		context.register(RELIC_DAMAGE, RELIC);
		context.register(PLAYER_ATTACK_ARMOR_PIERCING, PLAYER_AP);
		context.register(KEY_EXPLOSION, KEY);
	}
}
