package vazkii.botania.api;

import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;

import vazkii.botania.api.block.WandHUD;

public final class BotaniaForgeClientCapabilities {
	public static final Capability<WandHUD> WAND_HUD = CapabilityManager.get(new CapabilityToken<>() {});

	private BotaniaForgeClientCapabilities() {}
}
