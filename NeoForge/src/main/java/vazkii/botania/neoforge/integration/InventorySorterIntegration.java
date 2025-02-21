package vazkii.botania.neoforge.integration;

import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

import vazkii.botania.common.item.BotaniaItems;

public class InventorySorterIntegration {
	public static void init(IEventBus modBus) {
		modBus.addListener(InventorySorterIntegration::sendImc);
	}

	private static void sendImc(InterModEnqueueEvent evt) {
		// Botania issue 4068, cpw/InventorySorter issue 139
		InterModComms.sendTo("inventorysorter", "containerblacklist",
				() -> BuiltInRegistries.MENU.getKey(BotaniaItems.FLOWER_BAG_CONTAINER));
	}
}
