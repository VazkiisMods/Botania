package vazkii.botania.api;

import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import vazkii.botania.api.block.WandHUD;

public final class BotaniaForgeClientCapabilities {
	public static final EntityCapability<WandHUD, Void> ENTITY_WAND_HUD = EntityCapability.createVoid(WandHUD.ID, WandHUD.class);
	public static final BlockCapability<WandHUD, Void> BLOCK_WAND_HUD = BlockCapability.createVoid(WandHUD.ID, WandHUD.class);

	private BotaniaForgeClientCapabilities() {}
}
