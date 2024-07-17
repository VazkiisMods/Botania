package vazkii.botania.forge.integration;

import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;

import vazkii.botania.common.item.BotaniaItems;

public class InventorySorterIntegration {
	public static void init() {
		FMLJavaModLoadingContext.get().getModEventBus()
				.addListener(InventorySorterIntegration::sendImc);
	}

	private static void sendImc(InterModEnqueueEvent evt) {
		// Botania issue 4068, cpw/InventorySorter issue 139
		InterModComms.sendTo("inventorysorter", "containerblacklist",
				() -> BuiltInRegistries.MENU.getKey(BotaniaItems.FLOWER_BAG_CONTAINER));
	}
}
