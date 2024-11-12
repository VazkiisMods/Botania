/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.helper;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

public final class ColorHelper {
	public static final Function<DyeColor, Block> STAINED_GLASS_MAP = color -> BuiltInRegistries.BLOCK.get(new ResourceLocation(color.getSerializedName() + "_stained_glass"));
	public static final Function<DyeColor, Block> STAINED_GLASS_PANE_MAP = color -> BuiltInRegistries.BLOCK.get(new ResourceLocation(color.getSerializedName() + "_stained_glass_pane"));
	public static final Function<DyeColor, Block> TERRACOTTA_MAP = color -> BuiltInRegistries.BLOCK.get(new ResourceLocation(color.getSerializedName() + "_terracotta"));
	public static final Function<DyeColor, Block> GLAZED_TERRACOTTA_MAP = color -> BuiltInRegistries.BLOCK.get(new ResourceLocation(color.getSerializedName() + "_glazed_terracotta"));
	public static final Function<DyeColor, Block> WOOL_MAP = color -> BuiltInRegistries.BLOCK.get(new ResourceLocation(color.getSerializedName() + "_wool"));
	public static final Function<DyeColor, Block> CARPET_MAP = color -> BuiltInRegistries.BLOCK.get(new ResourceLocation(color.getSerializedName() + "_carpet"));
	public static final Function<DyeColor, Block> CONCRETE_MAP = color -> BuiltInRegistries.BLOCK.get(new ResourceLocation(color.getSerializedName() + "_concrete"));
	public static final Function<DyeColor, Block> CONCRETE_POWDER_MAP = color -> BuiltInRegistries.BLOCK.get(new ResourceLocation(color.getSerializedName() + "_concrete_powder"));
	public static final Function<DyeColor, Block> CANDLE_MAP = color -> BuiltInRegistries.BLOCK.get(new ResourceLocation(color.getSerializedName() + "_candle"));
	public static final Function<DyeColor, Block> CANDLE_CAKE_MAP = color -> BuiltInRegistries.BLOCK.get(new ResourceLocation(color.getSerializedName() + "_candle_cake"));

	@Nullable
	public static DyeColor getWoolColor(Block b) {
		ResourceLocation name = BuiltInRegistries.BLOCK.getKey(b);
		if ("minecraft".equals(name.getNamespace()) && name.getPath().endsWith("_wool")) {
			String color = name.getPath().substring(0, name.getPath().length() - "_wool".length());
			return DyeColor.valueOf(color.toUpperCase(Locale.ROOT));
		}
		return null;
	}

	public static boolean isWool(Block b) {
		return getWoolColor(b) != null;
	}

	public static int getColorValue(DyeColor color) {
		float[] colors = color.getTextureDiffuseColors();
		int r = (int) (colors[0] * 255.0F);
		int g = (int) (colors[1] * 255.0F);
		int b = (int) (colors[2] * 255.0F);
		return (r << 16) | (g << 8) | b;
	}

	public static int getColorLegibleOnGrayBackground(DyeColor color) {
		return switch (color) {
			case BLACK -> 0x808080;
			case GRAY -> 0xA0A0A0;
			case BLUE -> 0x6666FF;
			case BROWN -> 0x8B6543;
			default -> color.getTextColor();
		};
	}

	public static Stream<DyeColor> supportedColors() {
		return Stream.of(
				DyeColor.WHITE,
				DyeColor.ORANGE,
				DyeColor.MAGENTA,
				DyeColor.LIGHT_BLUE,
				DyeColor.YELLOW,
				DyeColor.LIME,
				DyeColor.PINK,
				DyeColor.GRAY,
				DyeColor.LIGHT_GRAY,
				DyeColor.CYAN,
				DyeColor.PURPLE,
				DyeColor.BLUE,
				DyeColor.BROWN,
				DyeColor.GREEN,
				DyeColor.RED,
				DyeColor.BLACK
		);
	}

	private ColorHelper() {}
}
