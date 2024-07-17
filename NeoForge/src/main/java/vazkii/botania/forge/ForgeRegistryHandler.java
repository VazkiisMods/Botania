package vazkii.botania.forge;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import vazkii.botania.api.BotaniaRegistries;
import vazkii.botania.common.lib.LibMisc;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

@Mod.EventBusSubscriber(modid = LibMisc.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeRegistryHandler {
	@SubscribeEvent
	public static void registerRegistry(NewRegistryEvent evt) {
		evt.create(new RegistryBuilder<>().setName(BotaniaRegistries.BREWS.location())
				.setDefaultKey(prefix("fallback")).hasTags()
				.disableSaving().disableSync());
	}
}
