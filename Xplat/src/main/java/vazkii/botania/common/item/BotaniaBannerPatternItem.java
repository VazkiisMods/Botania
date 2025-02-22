/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;

/**
 * @deprecated Have dedicated banner pattern items instead
 */
@Deprecated
public class BotaniaBannerPatternItem extends Item implements ItemWithBannerPattern {
	private final TagKey<BannerPattern> pattern;

	public BotaniaBannerPatternItem(TagKey<BannerPattern> pattern, Properties settings) {
		super(settings);
		this.pattern = pattern;
	}

	@Override
	public TagKey<BannerPattern> getBannerPattern() {
		return pattern;
	}
}
