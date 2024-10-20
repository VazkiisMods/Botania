/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.mana;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import vazkii.botania.api.mana.PoolOverlayProvider;
import vazkii.botania.common.block.BotaniaBlock;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class AlchemyCatalystBlock extends BotaniaBlock implements PoolOverlayProvider {
	private static final ResourceLocation OVERLAY_ICON = botaniaRL("block/alchemy_catalyst_overlay");

	public AlchemyCatalystBlock(Properties builder) {
		super(builder);
	}

	@Override
	public ResourceLocation getIcon(Level world, BlockPos pos) {
		return OVERLAY_ICON;
	}

}
