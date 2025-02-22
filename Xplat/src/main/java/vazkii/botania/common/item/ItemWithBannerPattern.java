package vazkii.botania.common.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.entity.BannerPattern;

/**
 * @deprecated Have dedicated banner pattern items instead
 */
@Deprecated
public interface ItemWithBannerPattern {
	TagKey<BannerPattern> getBannerPattern();
}
