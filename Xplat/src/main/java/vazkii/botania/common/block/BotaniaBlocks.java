/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block;

import net.minecraft.core.*;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.block.PetalApothecary;
import vazkii.botania.api.state.BotaniaStateProperties;
import vazkii.botania.api.state.enums.AlfheimPortalState;
import vazkii.botania.api.state.enums.LuminizerVariant;
import vazkii.botania.common.block.corporea.*;
import vazkii.botania.common.block.decor.*;
import vazkii.botania.common.block.decor.panes.BotaniaPaneBlock;
import vazkii.botania.common.block.decor.stairs.BotaniaStairBlock;
import vazkii.botania.common.block.dispenser.*;
import vazkii.botania.common.block.mana.*;
import vazkii.botania.common.block.red_string.*;
import vazkii.botania.common.entity.EnderAirBottleEntity;
import vazkii.botania.common.entity.VineBallEntity;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.block.BlockItemWithSpecialRenderer;
import vazkii.botania.common.item.block.TinyPotatoBlockItem;
import vazkii.botania.common.lib.LibBlockNames;
import vazkii.botania.mixin.DispenserBlockAccessor;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static vazkii.botania.common.lib.LibBlockNames.*;
import static vazkii.botania.common.lib.LibBlockNames.SLAB_SUFFIX;
import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public final class BotaniaBlocks {
	private static final BlockBehaviour.StateArgumentPredicate<EntityType<?>> NO_SPAWN = (state, world, pos, et) -> false;
	private static final BlockBehaviour.StatePredicate NO_SUFFOCATION = (state, world, pos) -> false;

	public static final Block whiteFlower = new BotaniaFlowerBlock(DyeColor.WHITE, BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollission().instabreak().offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(PushReaction.DESTROY).strength(0).sound(SoundType.GRASS));
	public static final Block orangeFlower = new BotaniaFlowerBlock(DyeColor.ORANGE, BlockBehaviour.Properties.copy(whiteFlower));
	public static final Block magentaFlower = new BotaniaFlowerBlock(DyeColor.MAGENTA, BlockBehaviour.Properties.copy(whiteFlower));
	public static final Block lightBlueFlower = new BotaniaFlowerBlock(DyeColor.LIGHT_BLUE, BlockBehaviour.Properties.copy(whiteFlower));
	public static final Block yellowFlower = new BotaniaFlowerBlock(DyeColor.YELLOW, BlockBehaviour.Properties.copy(whiteFlower));
	public static final Block limeFlower = new BotaniaFlowerBlock(DyeColor.LIME, BlockBehaviour.Properties.copy(whiteFlower));
	public static final Block pinkFlower = new BotaniaFlowerBlock(DyeColor.PINK, BlockBehaviour.Properties.copy(whiteFlower));
	public static final Block grayFlower = new BotaniaFlowerBlock(DyeColor.GRAY, BlockBehaviour.Properties.copy(whiteFlower));
	public static final Block lightGrayFlower = new BotaniaFlowerBlock(DyeColor.LIGHT_GRAY, BlockBehaviour.Properties.copy(whiteFlower));
	public static final Block cyanFlower = new BotaniaFlowerBlock(DyeColor.CYAN, BlockBehaviour.Properties.copy(whiteFlower));
	public static final Block purpleFlower = new BotaniaFlowerBlock(DyeColor.PURPLE, BlockBehaviour.Properties.copy(whiteFlower));
	public static final Block blueFlower = new BotaniaFlowerBlock(DyeColor.BLUE, BlockBehaviour.Properties.copy(whiteFlower));
	public static final Block brownFlower = new BotaniaFlowerBlock(DyeColor.BROWN, BlockBehaviour.Properties.copy(whiteFlower));
	public static final Block greenFlower = new BotaniaFlowerBlock(DyeColor.GREEN, BlockBehaviour.Properties.copy(whiteFlower));
	public static final Block redFlower = new BotaniaFlowerBlock(DyeColor.RED, BlockBehaviour.Properties.copy(whiteFlower));
	public static final Block blackFlower = new BotaniaFlowerBlock(DyeColor.BLACK, BlockBehaviour.Properties.copy(whiteFlower));

	public static final Block whiteShinyFlower = new GlimmeringFlowerBlock(DyeColor.WHITE, BlockBehaviour.Properties.copy(whiteFlower).lightLevel(s -> 15));
	public static final Block orangeShinyFlower = new GlimmeringFlowerBlock(DyeColor.ORANGE, BlockBehaviour.Properties.copy(whiteShinyFlower));
	public static final Block magentaShinyFlower = new GlimmeringFlowerBlock(DyeColor.MAGENTA, BlockBehaviour.Properties.copy(whiteShinyFlower));
	public static final Block lightBlueShinyFlower = new GlimmeringFlowerBlock(DyeColor.LIGHT_BLUE, BlockBehaviour.Properties.copy(whiteShinyFlower));
	public static final Block yellowShinyFlower = new GlimmeringFlowerBlock(DyeColor.YELLOW, BlockBehaviour.Properties.copy(whiteShinyFlower));
	public static final Block limeShinyFlower = new GlimmeringFlowerBlock(DyeColor.LIME, BlockBehaviour.Properties.copy(whiteShinyFlower));
	public static final Block pinkShinyFlower = new GlimmeringFlowerBlock(DyeColor.PINK, BlockBehaviour.Properties.copy(whiteShinyFlower));
	public static final Block grayShinyFlower = new GlimmeringFlowerBlock(DyeColor.GRAY, BlockBehaviour.Properties.copy(whiteShinyFlower));
	public static final Block lightGrayShinyFlower = new GlimmeringFlowerBlock(DyeColor.LIGHT_GRAY, BlockBehaviour.Properties.copy(whiteShinyFlower));
	public static final Block cyanShinyFlower = new GlimmeringFlowerBlock(DyeColor.CYAN, BlockBehaviour.Properties.copy(whiteShinyFlower));
	public static final Block purpleShinyFlower = new GlimmeringFlowerBlock(DyeColor.PURPLE, BlockBehaviour.Properties.copy(whiteShinyFlower));
	public static final Block blueShinyFlower = new GlimmeringFlowerBlock(DyeColor.BLUE, BlockBehaviour.Properties.copy(whiteShinyFlower));
	public static final Block brownShinyFlower = new GlimmeringFlowerBlock(DyeColor.BROWN, BlockBehaviour.Properties.copy(whiteShinyFlower));
	public static final Block greenShinyFlower = new GlimmeringFlowerBlock(DyeColor.GREEN, BlockBehaviour.Properties.copy(whiteShinyFlower));
	public static final Block redShinyFlower = new GlimmeringFlowerBlock(DyeColor.RED, BlockBehaviour.Properties.copy(whiteShinyFlower));
	public static final Block blackShinyFlower = new GlimmeringFlowerBlock(DyeColor.BLACK, BlockBehaviour.Properties.copy(whiteShinyFlower));

	public static final Block whiteBuriedPetals = new BuriedPetalBlock(DyeColor.WHITE, BlockBehaviour.Properties.copy(whiteFlower).sound(SoundType.MOSS).lightLevel(s -> 4));
	public static final Block orangeBuriedPetals = new BuriedPetalBlock(DyeColor.ORANGE, BlockBehaviour.Properties.copy(whiteBuriedPetals));
	public static final Block magentaBuriedPetals = new BuriedPetalBlock(DyeColor.MAGENTA, BlockBehaviour.Properties.copy(whiteBuriedPetals));
	public static final Block lightBlueBuriedPetals = new BuriedPetalBlock(DyeColor.LIGHT_BLUE, BlockBehaviour.Properties.copy(whiteBuriedPetals));
	public static final Block yellowBuriedPetals = new BuriedPetalBlock(DyeColor.YELLOW, BlockBehaviour.Properties.copy(whiteBuriedPetals));
	public static final Block limeBuriedPetals = new BuriedPetalBlock(DyeColor.LIME, BlockBehaviour.Properties.copy(whiteBuriedPetals));
	public static final Block pinkBuriedPetals = new BuriedPetalBlock(DyeColor.PINK, BlockBehaviour.Properties.copy(whiteBuriedPetals));
	public static final Block grayBuriedPetals = new BuriedPetalBlock(DyeColor.GRAY, BlockBehaviour.Properties.copy(whiteBuriedPetals));
	public static final Block lightGrayBuriedPetals = new BuriedPetalBlock(DyeColor.LIGHT_GRAY, BlockBehaviour.Properties.copy(whiteBuriedPetals));
	public static final Block cyanBuriedPetals = new BuriedPetalBlock(DyeColor.CYAN, BlockBehaviour.Properties.copy(whiteBuriedPetals));
	public static final Block purpleBuriedPetals = new BuriedPetalBlock(DyeColor.PURPLE, BlockBehaviour.Properties.copy(whiteBuriedPetals));
	public static final Block blueBuriedPetals = new BuriedPetalBlock(DyeColor.BLUE, BlockBehaviour.Properties.copy(whiteBuriedPetals));
	public static final Block brownBuriedPetals = new BuriedPetalBlock(DyeColor.BROWN, BlockBehaviour.Properties.copy(whiteBuriedPetals));
	public static final Block greenBuriedPetals = new BuriedPetalBlock(DyeColor.GREEN, BlockBehaviour.Properties.copy(whiteBuriedPetals));
	public static final Block redBuriedPetals = new BuriedPetalBlock(DyeColor.RED, BlockBehaviour.Properties.copy(whiteBuriedPetals));
	public static final Block blackBuriedPetals = new BuriedPetalBlock(DyeColor.BLACK, BlockBehaviour.Properties.copy(whiteBuriedPetals));

	public static final BlockBehaviour.Properties FLOATING_PROPS = BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(0.5F).sound(SoundType.GRAVEL).lightLevel(s -> 15);
	public static final Block whiteFloatingFlower = new FloatingFlowerBlock(DyeColor.WHITE, FLOATING_PROPS);
	public static final Block orangeFloatingFlower = new FloatingFlowerBlock(DyeColor.ORANGE, FLOATING_PROPS);
	public static final Block magentaFloatingFlower = new FloatingFlowerBlock(DyeColor.MAGENTA, FLOATING_PROPS);
	public static final Block lightBlueFloatingFlower = new FloatingFlowerBlock(DyeColor.LIGHT_BLUE, FLOATING_PROPS);
	public static final Block yellowFloatingFlower = new FloatingFlowerBlock(DyeColor.YELLOW, FLOATING_PROPS);
	public static final Block limeFloatingFlower = new FloatingFlowerBlock(DyeColor.LIME, FLOATING_PROPS);
	public static final Block pinkFloatingFlower = new FloatingFlowerBlock(DyeColor.PINK, FLOATING_PROPS);
	public static final Block grayFloatingFlower = new FloatingFlowerBlock(DyeColor.GRAY, FLOATING_PROPS);
	public static final Block lightGrayFloatingFlower = new FloatingFlowerBlock(DyeColor.LIGHT_GRAY, FLOATING_PROPS);
	public static final Block cyanFloatingFlower = new FloatingFlowerBlock(DyeColor.CYAN, FLOATING_PROPS);
	public static final Block purpleFloatingFlower = new FloatingFlowerBlock(DyeColor.PURPLE, FLOATING_PROPS);
	public static final Block blueFloatingFlower = new FloatingFlowerBlock(DyeColor.BLUE, FLOATING_PROPS);
	public static final Block brownFloatingFlower = new FloatingFlowerBlock(DyeColor.BROWN, FLOATING_PROPS);
	public static final Block greenFloatingFlower = new FloatingFlowerBlock(DyeColor.GREEN, FLOATING_PROPS);
	public static final Block redFloatingFlower = new FloatingFlowerBlock(DyeColor.RED, FLOATING_PROPS);
	public static final Block blackFloatingFlower = new FloatingFlowerBlock(DyeColor.BLACK, FLOATING_PROPS);

	public static final Block petalBlockWhite = new PetalBlock(DyeColor.WHITE, BlockBehaviour.Properties.of().mapColor(DyeColor.WHITE).strength(0.4F).sound(SoundType.MOSS));
	public static final Block petalBlockOrange = new PetalBlock(DyeColor.ORANGE, BlockBehaviour.Properties.copy(petalBlockWhite).mapColor(DyeColor.ORANGE));
	public static final Block petalBlockMagenta = new PetalBlock(DyeColor.MAGENTA, BlockBehaviour.Properties.copy(petalBlockWhite).mapColor(DyeColor.MAGENTA));
	public static final Block petalBlockLightBlue = new PetalBlock(DyeColor.LIGHT_BLUE, BlockBehaviour.Properties.copy(petalBlockWhite).mapColor(DyeColor.LIGHT_BLUE));
	public static final Block petalBlockYellow = new PetalBlock(DyeColor.YELLOW, BlockBehaviour.Properties.copy(petalBlockWhite).mapColor(DyeColor.YELLOW));
	public static final Block petalBlockLime = new PetalBlock(DyeColor.LIME, BlockBehaviour.Properties.copy(petalBlockWhite).mapColor(DyeColor.LIME));
	public static final Block petalBlockPink = new PetalBlock(DyeColor.PINK, BlockBehaviour.Properties.copy(petalBlockWhite).mapColor(DyeColor.PINK));
	public static final Block petalBlockGray = new PetalBlock(DyeColor.GRAY, BlockBehaviour.Properties.copy(petalBlockWhite).mapColor(DyeColor.GRAY));
	public static final Block petalBlockSilver = new PetalBlock(DyeColor.LIGHT_GRAY, BlockBehaviour.Properties.copy(petalBlockWhite).mapColor(DyeColor.LIGHT_GRAY));
	public static final Block petalBlockCyan = new PetalBlock(DyeColor.CYAN, BlockBehaviour.Properties.copy(petalBlockWhite).mapColor(DyeColor.CYAN));
	public static final Block petalBlockPurple = new PetalBlock(DyeColor.PURPLE, BlockBehaviour.Properties.copy(petalBlockWhite).mapColor(DyeColor.PURPLE));
	public static final Block petalBlockBlue = new PetalBlock(DyeColor.BLUE, BlockBehaviour.Properties.copy(petalBlockWhite).mapColor(DyeColor.BLUE));
	public static final Block petalBlockBrown = new PetalBlock(DyeColor.BROWN, BlockBehaviour.Properties.copy(petalBlockWhite).mapColor(DyeColor.BROWN));
	public static final Block petalBlockGreen = new PetalBlock(DyeColor.GREEN, BlockBehaviour.Properties.copy(petalBlockWhite).mapColor(DyeColor.GREEN));
	public static final Block petalBlockRed = new PetalBlock(DyeColor.RED, BlockBehaviour.Properties.copy(petalBlockWhite).mapColor(DyeColor.RED));
	public static final Block petalBlockBlack = new PetalBlock(DyeColor.BLACK, BlockBehaviour.Properties.copy(petalBlockWhite).mapColor(DyeColor.BLACK));

	public static final Block whiteMushroom = new BotaniaMushroomBlock(DyeColor.WHITE, BlockBehaviour.Properties.copy(whiteFlower).lightLevel(s -> 3).offsetType(BlockBehaviour.OffsetType.NONE));
	public static final Block orangeMushroom = new BotaniaMushroomBlock(DyeColor.ORANGE, BlockBehaviour.Properties.copy(whiteMushroom));
	public static final Block magentaMushroom = new BotaniaMushroomBlock(DyeColor.MAGENTA, BlockBehaviour.Properties.copy(whiteMushroom));
	public static final Block lightBlueMushroom = new BotaniaMushroomBlock(DyeColor.LIGHT_BLUE, BlockBehaviour.Properties.copy(whiteMushroom));
	public static final Block yellowMushroom = new BotaniaMushroomBlock(DyeColor.YELLOW, BlockBehaviour.Properties.copy(whiteMushroom));
	public static final Block limeMushroom = new BotaniaMushroomBlock(DyeColor.LIME, BlockBehaviour.Properties.copy(whiteMushroom));
	public static final Block pinkMushroom = new BotaniaMushroomBlock(DyeColor.PINK, BlockBehaviour.Properties.copy(whiteMushroom));
	public static final Block grayMushroom = new BotaniaMushroomBlock(DyeColor.GRAY, BlockBehaviour.Properties.copy(whiteMushroom));
	public static final Block lightGrayMushroom = new BotaniaMushroomBlock(DyeColor.LIGHT_GRAY, BlockBehaviour.Properties.copy(whiteMushroom));
	public static final Block cyanMushroom = new BotaniaMushroomBlock(DyeColor.CYAN, BlockBehaviour.Properties.copy(whiteMushroom));
	public static final Block purpleMushroom = new BotaniaMushroomBlock(DyeColor.PURPLE, BlockBehaviour.Properties.copy(whiteMushroom));
	public static final Block blueMushroom = new BotaniaMushroomBlock(DyeColor.BLUE, BlockBehaviour.Properties.copy(whiteMushroom));
	public static final Block brownMushroom = new BotaniaMushroomBlock(DyeColor.BROWN, BlockBehaviour.Properties.copy(whiteMushroom));
	public static final Block greenMushroom = new BotaniaMushroomBlock(DyeColor.GREEN, BlockBehaviour.Properties.copy(whiteMushroom));
	public static final Block redMushroom = new BotaniaMushroomBlock(DyeColor.RED, BlockBehaviour.Properties.copy(whiteMushroom));
	public static final Block blackMushroom = new BotaniaMushroomBlock(DyeColor.BLACK, BlockBehaviour.Properties.copy(whiteMushroom));

	public static final Block doubleFlowerWhite = new BotaniaDoubleFlowerBlock(DyeColor.WHITE, BlockBehaviour.Properties.copy(whiteFlower));
	public static final Block doubleFlowerOrange = new BotaniaDoubleFlowerBlock(DyeColor.ORANGE, BlockBehaviour.Properties.copy(doubleFlowerWhite));
	public static final Block doubleFlowerMagenta = new BotaniaDoubleFlowerBlock(DyeColor.MAGENTA, BlockBehaviour.Properties.copy(doubleFlowerWhite));
	public static final Block doubleFlowerLightBlue = new BotaniaDoubleFlowerBlock(DyeColor.LIGHT_BLUE, BlockBehaviour.Properties.copy(doubleFlowerWhite));
	public static final Block doubleFlowerYellow = new BotaniaDoubleFlowerBlock(DyeColor.YELLOW, BlockBehaviour.Properties.copy(doubleFlowerWhite));
	public static final Block doubleFlowerLime = new BotaniaDoubleFlowerBlock(DyeColor.LIME, BlockBehaviour.Properties.copy(doubleFlowerWhite));
	public static final Block doubleFlowerPink = new BotaniaDoubleFlowerBlock(DyeColor.PINK, BlockBehaviour.Properties.copy(doubleFlowerWhite));
	public static final Block doubleFlowerGray = new BotaniaDoubleFlowerBlock(DyeColor.GRAY, BlockBehaviour.Properties.copy(doubleFlowerWhite));
	public static final Block doubleFlowerLightGray = new BotaniaDoubleFlowerBlock(DyeColor.LIGHT_GRAY, BlockBehaviour.Properties.copy(doubleFlowerWhite));
	public static final Block doubleFlowerCyan = new BotaniaDoubleFlowerBlock(DyeColor.CYAN, BlockBehaviour.Properties.copy(doubleFlowerWhite));
	public static final Block doubleFlowerPurple = new BotaniaDoubleFlowerBlock(DyeColor.PURPLE, BlockBehaviour.Properties.copy(doubleFlowerWhite));
	public static final Block doubleFlowerBlue = new BotaniaDoubleFlowerBlock(DyeColor.BLUE, BlockBehaviour.Properties.copy(doubleFlowerWhite));
	public static final Block doubleFlowerBrown = new BotaniaDoubleFlowerBlock(DyeColor.BROWN, BlockBehaviour.Properties.copy(doubleFlowerWhite));
	public static final Block doubleFlowerGreen = new BotaniaDoubleFlowerBlock(DyeColor.GREEN, BlockBehaviour.Properties.copy(doubleFlowerWhite));
	public static final Block doubleFlowerRed = new BotaniaDoubleFlowerBlock(DyeColor.RED, BlockBehaviour.Properties.copy(doubleFlowerWhite));
	public static final Block doubleFlowerBlack = new BotaniaDoubleFlowerBlock(DyeColor.BLACK, BlockBehaviour.Properties.copy(doubleFlowerWhite));

	public static final Block pottedWhiteFlower = flowerPot(whiteFlower, 0);
	public static final Block pottedOrangeFlower = flowerPot(orangeFlower, 0);
	public static final Block pottedMagentaFlower = flowerPot(magentaFlower, 0);
	public static final Block pottedLightBlueFlower = flowerPot(lightBlueFlower, 0);
	public static final Block pottedYellowFlower = flowerPot(yellowFlower, 0);
	public static final Block pottedLimeFlower = flowerPot(limeFlower, 0);
	public static final Block pottedPinkFlower = flowerPot(pinkFlower, 0);
	public static final Block pottedGrayFlower = flowerPot(grayFlower, 0);
	public static final Block pottedLightGrayFlower = flowerPot(lightGrayFlower, 0);
	public static final Block pottedCyanFlower = flowerPot(cyanFlower, 0);
	public static final Block pottedPurpleFlower = flowerPot(purpleFlower, 0);
	public static final Block pottedBlueFlower = flowerPot(blueFlower, 0);
	public static final Block pottedBrownFlower = flowerPot(brownFlower, 0);
	public static final Block pottedGreenFlower = flowerPot(greenFlower, 0);
	public static final Block pottedRedFlower = flowerPot(redFlower, 0);
	public static final Block pottedBlackFlower = flowerPot(blackFlower, 0);

	public static final Block pottedWhiteShinyFlower = flowerPot(whiteShinyFlower, 15);
	public static final Block pottedOrangeShinyFlower = flowerPot(orangeShinyFlower, 15);
	public static final Block pottedMagentaShinyFlower = flowerPot(magentaShinyFlower, 15);
	public static final Block pottedLightBlueShinyFlower = flowerPot(lightBlueShinyFlower, 15);
	public static final Block pottedYellowShinyFlower = flowerPot(yellowShinyFlower, 15);
	public static final Block pottedLimeShinyFlower = flowerPot(limeShinyFlower, 15);
	public static final Block pottedPinkShinyFlower = flowerPot(pinkShinyFlower, 15);
	public static final Block pottedGrayShinyFlower = flowerPot(grayShinyFlower, 15);
	public static final Block pottedLightGrayShinyFlower = flowerPot(lightGrayShinyFlower, 15);
	public static final Block pottedCyanShinyFlower = flowerPot(cyanShinyFlower, 15);
	public static final Block pottedPurpleShinyFlower = flowerPot(purpleShinyFlower, 15);
	public static final Block pottedBlueShinyFlower = flowerPot(blueShinyFlower, 15);
	public static final Block pottedBrownShinyFlower = flowerPot(brownShinyFlower, 15);
	public static final Block pottedGreenShinyFlower = flowerPot(greenShinyFlower, 15);
	public static final Block pottedRedShinyFlower = flowerPot(redShinyFlower, 15);
	public static final Block pottedBlackShinyFlower = flowerPot(blackShinyFlower, 15);

	public static final Block pottedWhiteMushroom = flowerPot(whiteMushroom, 3);
	public static final Block pottedOrangeMushroom = flowerPot(orangeMushroom, 3);
	public static final Block pottedMagentaMushroom = flowerPot(magentaMushroom, 3);
	public static final Block pottedLightBlueMushroom = flowerPot(lightBlueMushroom, 3);
	public static final Block pottedYellowMushroom = flowerPot(yellowMushroom, 3);
	public static final Block pottedLimeMushroom = flowerPot(limeMushroom, 3);
	public static final Block pottedPinkMushroom = flowerPot(pinkMushroom, 3);
	public static final Block pottedGrayMushroom = flowerPot(grayMushroom, 3);
	public static final Block pottedLightGrayMushroom = flowerPot(lightGrayMushroom, 3);
	public static final Block pottedCyanMushroom = flowerPot(cyanMushroom, 3);
	public static final Block pottedPurpleMushroom = flowerPot(purpleMushroom, 3);
	public static final Block pottedBlueMushroom = flowerPot(blueMushroom, 3);
	public static final Block pottedBrownMushroom = flowerPot(brownMushroom, 3);
	public static final Block pottedGreenMushroom = flowerPot(greenMushroom, 3);
	public static final Block pottedRedMushroom = flowerPot(redMushroom, 3);
	public static final Block pottedBlackMushroom = flowerPot(blackMushroom, 3);

	public static final Block defaultAltar = new PetalApothecaryBlock(PetalApothecaryBlock.Variant.DEFAULT, BlockBehaviour.Properties.of()
			.strength(3.5F).sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().mapColor(MapColor.STONE)
			.lightLevel(s -> s.getValue(PetalApothecaryBlock.FLUID) == PetalApothecary.State.LAVA ? 15 : 0));
	public static final Block deepslateAltar = new PetalApothecaryBlock(PetalApothecaryBlock.Variant.DEEPSLATE, BlockBehaviour.Properties.copy(defaultAltar).sound(SoundType.DEEPSLATE).mapColor(MapColor.DEEPSLATE));
	public static final Block livingrockAltar = new PetalApothecaryBlock(PetalApothecaryBlock.Variant.LIVINGROCK, BlockBehaviour.Properties.copy(defaultAltar).sound(SoundType.STONE).mapColor(MapColor.TERRACOTTA_WHITE));
	public static final Block mossyAltar = new PetalApothecaryBlock(PetalApothecaryBlock.Variant.MOSSY, BlockBehaviour.Properties.copy(defaultAltar));
	public static final Block forestAltar = new PetalApothecaryBlock(PetalApothecaryBlock.Variant.FOREST, BlockBehaviour.Properties.copy(defaultAltar).sound(SoundType.TUFF).mapColor(MapColor.PLANT));
	public static final Block plainsAltar = new PetalApothecaryBlock(PetalApothecaryBlock.Variant.PLAINS, BlockBehaviour.Properties.copy(defaultAltar).sound(SoundType.CALCITE).mapColor(DyeColor.WHITE));
	public static final Block mountainAltar = new PetalApothecaryBlock(PetalApothecaryBlock.Variant.MOUNTAIN, BlockBehaviour.Properties.copy(defaultAltar).sound(SoundType.DEEPSLATE_TILES).mapColor(DyeColor.LIGHT_GRAY));
	public static final Block fungalAltar = new PetalApothecaryBlock(PetalApothecaryBlock.Variant.FUNGAL, BlockBehaviour.Properties.copy(defaultAltar).sound(SoundType.DEEPSLATE_BRICKS).mapColor(MapColor.CRIMSON_STEM));
	public static final Block swampAltar = new PetalApothecaryBlock(PetalApothecaryBlock.Variant.SWAMP, BlockBehaviour.Properties.copy(defaultAltar).sound(SoundType.DEEPSLATE_TILES).mapColor(MapColor.TERRACOTTA_BROWN));
	public static final Block desertAltar = new PetalApothecaryBlock(PetalApothecaryBlock.Variant.DESERT, BlockBehaviour.Properties.copy(defaultAltar).sound(SoundType.DEEPSLATE).mapColor(MapColor.TERRACOTTA_ORANGE));
	public static final Block taigaAltar = new PetalApothecaryBlock(PetalApothecaryBlock.Variant.TAIGA, BlockBehaviour.Properties.copy(defaultAltar).sound(SoundType.DEEPSLATE).mapColor(DyeColor.BLUE));
	public static final Block mesaAltar = new PetalApothecaryBlock(PetalApothecaryBlock.Variant.MESA, BlockBehaviour.Properties.copy(defaultAltar).sound(SoundType.CALCITE).mapColor(MapColor.TERRACOTTA_WHITE));
	public static final Block[] ALL_APOTHECARIES = new Block[] { defaultAltar, deepslateAltar, livingrockAltar, mossyAltar, forestAltar, plainsAltar, mountainAltar, fungalAltar, swampAltar, desertAltar, taigaAltar, mesaAltar };

	public static final Block livingrock = new BotaniaBlock(BlockBehaviour.Properties.of().strength(2, 10).sound(SoundType.STONE)
			.instrument(NoteBlockInstrument.BASEDRUM).mapColor(MapColor.TERRACOTTA_WHITE).requiresCorrectToolForDrops());
	public static final Block livingrockPolished = new BotaniaBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block livingrockSlate = new BotaniaBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block livingrockBrick = new BotaniaBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block livingrockBrickChiseled = new BotaniaBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block livingrockBrickCracked = new BotaniaBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block livingrockBrickMossy = new BotaniaBlock(BlockBehaviour.Properties.copy(livingrock));

	public static final Block livingwoodLog = new RotatedPillarBlock(BlockBehaviour.Properties.of().strength(2)
			.sound(SoundType.WOOD).instrument(NoteBlockInstrument.BASS).mapColor(state -> state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y ? MapColor.TERRACOTTA_RED : MapColor.TERRACOTTA_BROWN));
	public static final Block livingwoodLogStripped = new RotatedPillarBlock(BlockBehaviour.Properties.copy(livingwoodLog).mapColor(MapColor.TERRACOTTA_RED));
	public static final Block livingwoodLogGlimmering = new RotatedPillarBlock(BlockBehaviour.Properties.copy(livingwoodLog).lightLevel(b -> 12));
	public static final Block livingwoodLogStrippedGlimmering = new RotatedPillarBlock(BlockBehaviour.Properties.copy(livingwoodLogStripped).lightLevel(b -> 8));
	public static final Block livingwood = new RotatedPillarBlock(BlockBehaviour.Properties.copy(livingwoodLog).mapColor(MapColor.TERRACOTTA_BROWN));
	public static final Block livingwoodStripped = new RotatedPillarBlock(BlockBehaviour.Properties.copy(livingwoodLogStripped));
	public static final Block livingwoodGlimmering = new RotatedPillarBlock(BlockBehaviour.Properties.copy(livingwoodLogGlimmering).mapColor(MapColor.TERRACOTTA_BROWN));
	public static final Block livingwoodStrippedGlimmering = new RotatedPillarBlock(BlockBehaviour.Properties.copy(livingwoodLogStrippedGlimmering).mapColor(MapColor.TERRACOTTA_BROWN));
	public static final Block livingwoodPlanks = new BotaniaBlock(BlockBehaviour.Properties.copy(livingwoodLog).mapColor(MapColor.TERRACOTTA_RED));
	public static final Block livingwoodPlanksMossy = new BotaniaBlock(BlockBehaviour.Properties.copy(livingwoodPlanks));
	public static final Block livingwoodFramed = new RotatedPillarBlock(BlockBehaviour.Properties.copy(livingwoodPlanks));
	public static final Block livingwoodPatternFramed = new BotaniaBlock(BlockBehaviour.Properties.copy(livingwoodPlanks));

	public static final Block dreamwoodLog = new RotatedPillarBlock(BlockBehaviour.Properties.copy(livingwoodLog).mapColor(MapColor.QUARTZ));
	public static final Block dreamwoodLogStripped = new RotatedPillarBlock(BlockBehaviour.Properties.copy(dreamwoodLog));
	public static final Block dreamwoodLogGlimmering = new RotatedPillarBlock(BlockBehaviour.Properties.copy(livingwoodLogGlimmering).mapColor(MapColor.QUARTZ));
	public static final Block dreamwoodLogStrippedGlimmering = new RotatedPillarBlock(BlockBehaviour.Properties.copy(livingwoodLogStrippedGlimmering).mapColor(MapColor.QUARTZ));
	public static final Block dreamwood = new RotatedPillarBlock(BlockBehaviour.Properties.copy(dreamwoodLog));
	public static final Block dreamwoodStripped = new RotatedPillarBlock(BlockBehaviour.Properties.copy(dreamwoodLog));
	public static final Block dreamwoodGlimmering = new RotatedPillarBlock(BlockBehaviour.Properties.copy(dreamwoodLogGlimmering));
	public static final Block dreamwoodStrippedGlimmering = new RotatedPillarBlock(BlockBehaviour.Properties.copy(dreamwoodLogStrippedGlimmering));
	public static final Block dreamwoodPlanks = new BotaniaBlock(BlockBehaviour.Properties.copy(dreamwoodLog));
	public static final Block dreamwoodPlanksMossy = new BotaniaBlock(BlockBehaviour.Properties.copy(dreamwoodPlanks));
	public static final Block dreamwoodFramed = new RotatedPillarBlock(BlockBehaviour.Properties.copy(dreamwoodPlanks));
	public static final Block dreamwoodPatternFramed = new BotaniaBlock(BlockBehaviour.Properties.copy(dreamwoodPlanks));

	public static final Block manaSpreader = new ManaSpreaderBlock(ManaSpreaderBlock.Variant.MANA, BlockBehaviour.Properties.copy(livingwood).isValidSpawn(NO_SPAWN));
	public static final Block redstoneSpreader = new ManaSpreaderBlock(ManaSpreaderBlock.Variant.REDSTONE, BlockBehaviour.Properties.copy(livingwood).isValidSpawn(NO_SPAWN));
	public static final Block elvenSpreader = new ManaSpreaderBlock(ManaSpreaderBlock.Variant.ELVEN, BlockBehaviour.Properties.copy(dreamwood).isValidSpawn(NO_SPAWN));
	public static final Block gaiaSpreader = new ManaSpreaderBlock(ManaSpreaderBlock.Variant.GAIA, BlockBehaviour.Properties.copy(dreamwood).isValidSpawn(NO_SPAWN));

	public static final Block manaPool = new ManaPoolBlock(ManaPoolBlock.Variant.DEFAULT, BlockBehaviour.Properties.copy(livingrock));
	public static final Block creativePool = new ManaPoolBlock(ManaPoolBlock.Variant.CREATIVE, BlockBehaviour.Properties.copy(livingrock));
	public static final Block dilutedPool = new ManaPoolBlock(ManaPoolBlock.Variant.DILUTED, BlockBehaviour.Properties.copy(livingrock));
	public static final Block fabulousPool = new ManaPoolBlock(ManaPoolBlock.Variant.FABULOUS, BlockBehaviour.Properties.copy(livingrock));
	public static final Block alchemyCatalyst = new AlchemyCatalystBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block conjurationCatalyst = new ConjurationCatalystBlock(BlockBehaviour.Properties.copy(livingrock));

	public static final Block manasteelBlock = new BotaniaBlock(BlockBehaviour.Properties.of().strength(3, 10).mapColor(MapColor.LAPIS)
			.sound(SoundType.METAL).instrument(NoteBlockInstrument.IRON_XYLOPHONE).requiresCorrectToolForDrops());
	public static final Block terrasteelBlock = new BotaniaBlock(BlockBehaviour.Properties.copy(manasteelBlock).mapColor(MapColor.EMERALD));
	public static final Block elementiumBlock = new BotaniaBlock(BlockBehaviour.Properties.copy(manasteelBlock).mapColor(MapColor.COLOR_PINK));
	public static final Block manaDiamondBlock = new BotaniaBlock(BlockBehaviour.Properties.copy(manasteelBlock).mapColor(MapColor.DIAMOND));
	public static final Block dragonstoneBlock = new BotaniaBlock(BlockBehaviour.Properties.copy(manasteelBlock).instrument(NoteBlockInstrument.HARP).mapColor(MapColor.COLOR_PINK));

	public static final Block manaGlass = new BotaniaGlassBlock(BlockBehaviour.Properties.copy(Blocks.GLASS).lightLevel(s -> 15).isViewBlocking(NO_SUFFOCATION).isSuffocating(NO_SUFFOCATION).isValidSpawn(NO_SPAWN));
	public static final Block elfGlass = new BotaniaGlassBlock(BlockBehaviour.Properties.copy(manaGlass).isViewBlocking(NO_SUFFOCATION).isSuffocating(NO_SUFFOCATION).isValidSpawn(NO_SPAWN));
	public static final Block bifrost = new BifrostBlock(BlockBehaviour.Properties.of().strength(-1, 0.3F)
			.lightLevel(s -> 15).sound(SoundType.GLASS).instrument(NoteBlockInstrument.HAT).noOcclusion()
			.isViewBlocking(NO_SUFFOCATION).isSuffocating(NO_SUFFOCATION).isValidSpawn(NO_SPAWN));
	public static final Block bifrostPerm = new PermanentBifrostBlock(BlockBehaviour.Properties.of().strength(0.3F)
			.lightLevel(s -> 15).sound(SoundType.GLASS).instrument(NoteBlockInstrument.HAT).noOcclusion()
			.isViewBlocking(NO_SUFFOCATION).isSuffocating(NO_SUFFOCATION).isValidSpawn(NO_SPAWN));

	public static final Block runeAltar = new RunicAltarBlock(BlockBehaviour.Properties.copy(livingrock).requiresCorrectToolForDrops());
	public static final Block enchanter = new ManaEnchanterBlock(BlockBehaviour.Properties.of().mapColor(MapColor.LAPIS).strength(3, 5).lightLevel(s -> 15).sound(SoundType.STONE));
	public static final Block brewery = new BotanicalBreweryBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block terraPlate = new TerrestrialAgglomerationPlateBlock(BlockBehaviour.Properties.of().mapColor(MapColor.LAPIS).strength(3, 10).sound(SoundType.METAL).requiresCorrectToolForDrops());
	public static final Block alfPortal = new AlfheimPortalBlock(BlockBehaviour.Properties.copy(livingwood).strength(10).sound(SoundType.WOOD)
			.lightLevel(s -> s.getValue(BotaniaStateProperties.ALFPORTAL_STATE) != AlfheimPortalState.OFF ? 15 : 0));

	public static final Block manaPylon = new PylonBlock(PylonBlock.Variant.MANA, BlockBehaviour.Properties.of().mapColor(DyeColor.LIGHT_BLUE).strength(5.5F).sound(SoundType.METAL).lightLevel(s -> 7).requiresCorrectToolForDrops());
	public static final Block naturaPylon = new PylonBlock(PylonBlock.Variant.NATURA, BlockBehaviour.Properties.copy(manaPylon).mapColor(MapColor.EMERALD));
	public static final Block gaiaPylon = new PylonBlock(PylonBlock.Variant.GAIA, BlockBehaviour.Properties.copy(manaPylon).mapColor(DyeColor.PINK));

	public static final Block distributor = new ManaSplitterBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block manaVoid = new ManaVoidBlock(BlockBehaviour.Properties.copy(livingrock).strength(2, 2000));
	public static final Block manaDetector = new ManaDetectorBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block pistonRelay = new ForceRelayBlock(BlockBehaviour.Properties.of().strength(2, 10).sound(SoundType.METAL).mapColor(MapColor.COLOR_PURPLE).isValidSpawn(NO_SPAWN));
	public static final Block turntable = new SpreaderTurntableBlock(BlockBehaviour.Properties.copy(livingwood));
	public static final Block tinyPlanet = new TinyPlanetBlock(BlockBehaviour.Properties.of().mapColor(MapColor.DEEPSLATE).strength(20, 100)
			.sound(SoundType.DEEPSLATE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops());
	public static final Block wildDrum = new DrumBlock(DrumBlock.Variant.WILD, BlockBehaviour.Properties.copy(livingwood).instrument(NoteBlockInstrument.BASEDRUM));
	public static final Block gatheringDrum = new DrumBlock(DrumBlock.Variant.GATHERING, BlockBehaviour.Properties.copy(wildDrum));
	public static final Block canopyDrum = new DrumBlock(DrumBlock.Variant.CANOPY, BlockBehaviour.Properties.copy(wildDrum));
	public static final Block spawnerClaw = new LifeImbuerBlock(BlockBehaviour.Properties.of().sound(SoundType.METAL).strength(3).requiresCorrectToolForDrops());
	public static final Block rfGenerator = new PowerGeneratorBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block prism = new ManaPrismBlock(BlockBehaviour.Properties.copy(elfGlass).noCollission());
	public static final Block pump = new ManaPumpBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block sparkChanger = new SparkTinkererBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block manaBomb = new ManastormChargeBlock(BlockBehaviour.Properties.copy(livingwood).strength(12));
	public static final Block bellows = new BellowsBlock(BlockBehaviour.Properties.copy(livingwood));

	public static final Block openCrate = new OpenCrateBlock(BlockBehaviour.Properties.copy(livingwood));
	public static final Block craftCrate = new CraftyCrateBlock(BlockBehaviour.Properties.copy(livingwood));
	public static final Block forestEye = new EyeOfTheAncientsBlock(BlockBehaviour.Properties.of().strength(5, 10).sound(SoundType.METAL).requiresCorrectToolForDrops());
	public static final Block solidVines = new SolidVineBlock(BlockBehaviour.Properties.copy(Blocks.VINE));
	public static final Block abstrusePlatform = new PlatformBlock(PlatformBlock.Variant.ABSTRUSE, BlockBehaviour.Properties.copy(livingwood).strength(2, 5).isValidSpawn(NO_SPAWN).noOcclusion().isViewBlocking(NO_SUFFOCATION).isSuffocating(NO_SUFFOCATION));
	public static final Block spectralPlatform = new PlatformBlock(PlatformBlock.Variant.SPECTRAL, BlockBehaviour.Properties.copy(abstrusePlatform));
	public static final Block infrangiblePlatform = new PlatformBlock(PlatformBlock.Variant.INFRANGIBLE, BlockBehaviour.Properties.copy(abstrusePlatform).strength(-1, Float.MAX_VALUE).isValidSpawn(NO_SPAWN).noOcclusion());
	public static final Block tinyPotato = new TinyPotatoBlock(BlockBehaviour.Properties.of().strength(0.25F).mapColor(DyeColor.PINK));
	public static final Block enderEye = new EnderOverseerBlock(BlockBehaviour.Properties.copy(manasteelBlock));
	public static final Block redStringContainer = new RedStringContainerBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block redStringDispenser = new RedStringDispenserBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block redStringFertilizer = new RedStringNutrifierBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block redStringComparator = new RedStringComparatorBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block redStringRelay = new RedStringSpooferBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block redStringInterceptor = new RedStringInterceptorBlock(BlockBehaviour.Properties.copy(livingrock));

	public static final Block corporeaFunnel = new CorporeaFunnelBlock(BlockBehaviour.Properties.of().strength(5.5F)
			.mapColor(DyeColor.PURPLE).sound(SoundType.METAL).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops());
	public static final Block corporeaInterceptor = new CorporeaInterceptorBlock(BlockBehaviour.Properties.copy(corporeaFunnel));
	public static final Block corporeaIndex = new CorporeaIndexBlock(BlockBehaviour.Properties.copy(corporeaFunnel).noOcclusion());
	public static final Block corporeaCrystalCube = new CorporeaCrystalCubeBlock(BlockBehaviour.Properties.copy(corporeaFunnel));
	public static final Block corporeaRetainer = new CorporeaRetainerBlock(BlockBehaviour.Properties.copy(corporeaFunnel));

	public static final Block corporeaBlock = new BotaniaBlock(BlockBehaviour.Properties.copy(corporeaFunnel));
	public static final Block corporeaBrick = new BotaniaBlock(BlockBehaviour.Properties.copy(corporeaBlock));
	public static final SlabBlock corporeaSlab = new SlabBlock(BlockBehaviour.Properties.copy(corporeaBlock));
	public static final StairBlock corporeaStairs = new BotaniaStairBlock(corporeaBlock.defaultBlockState(), BlockBehaviour.Properties.copy(corporeaBlock));
	public static final SlabBlock corporeaBrickSlab = new SlabBlock(BlockBehaviour.Properties.copy(corporeaBrick));
	public static final StairBlock corporeaBrickStairs = new BotaniaStairBlock(corporeaBrick.defaultBlockState(), BlockBehaviour.Properties.copy(corporeaBrick));
	public static final Block corporeaBrickWall = new WallBlock(BlockBehaviour.Properties.copy(corporeaBrick));

	public static final Block incensePlate = new IncensePlateBlock(BlockBehaviour.Properties.copy(livingwood));
	public static final Block hourglass = new HoveringHourglassBlock(BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(2).sound(SoundType.METAL));
	public static final Block ghostRail = new SpectralRailBlock(BlockBehaviour.Properties.copy(Blocks.RAIL));
	public static final Block lightRelayDefault = new LuminizerBlock(LuminizerVariant.DEFAULT, BlockBehaviour.Properties.of().noCollission());
	public static final Block lightRelayDetector = new LuminizerBlock(LuminizerVariant.DETECTOR, BlockBehaviour.Properties.copy(lightRelayDefault));
	public static final Block lightRelayFork = new LuminizerBlock(LuminizerVariant.FORK, BlockBehaviour.Properties.copy(lightRelayDefault));
	public static final Block lightRelayToggle = new LuminizerBlock(LuminizerVariant.TOGGLE, BlockBehaviour.Properties.copy(lightRelayDefault));
	public static final Block lightLauncher = new LuminizerLauncherBlock(BlockBehaviour.Properties.copy(livingwood));
	public static final Block cacophonium = new CacophoniumBlock(BlockBehaviour.Properties.copy(Blocks.NOTE_BLOCK).strength(0.8F));
	public static final Block cellBlock = new CellularBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).sound(SoundType.WOOL));
	public static final Block teruTeruBozu = new TeruTeruBozuBlock(BlockBehaviour.Properties.of().sound(SoundType.WOOL).instrument(NoteBlockInstrument.GUITAR).mapColor(DyeColor.WHITE));
	public static final Block avatar = new AvatarBlock(BlockBehaviour.Properties.copy(livingwood));
	public static final Block fakeAir = new FakeAirBlock(BlockBehaviour.Properties.of().replaceable().noCollission().noLootTable().air().randomTicks());
	public static final Block root = new LivingRootBlock(BlockBehaviour.Properties.of().strength(1.2F).sound(SoundType.WOOD));
	public static final Block felPumpkin = new FelPumpkinBlock(BlockBehaviour.Properties.copy(Blocks.CARVED_PUMPKIN));
	public static final Block cocoon = new CocoonBlock(BlockBehaviour.Properties.of().strength(3, 60).sound(SoundType.WOOL));
	public static final Block enchantedSoil = new EnchantedSoilBlock(BlockBehaviour.Properties.of().strength(0.6F).sound(SoundType.GRASS).mapColor(MapColor.GRASS));
	public static final Block animatedTorch = new AnimatedTorchBlock(BlockBehaviour.Properties.of().lightLevel(s -> 7).noOcclusion());
	public static final Block starfield = new StarfieldCreatorBlock(BlockBehaviour.Properties.of().mapColor(DyeColor.PINK).strength(5, 2000).sound(SoundType.METAL));

	public static final Block azulejo0 = new BotaniaBlock(BlockBehaviour.Properties.of().mapColor(MapColor.LAPIS).strength(2, 5)
			.sound(SoundType.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops());
	public static final Block azulejo1 = new BotaniaBlock(BlockBehaviour.Properties.copy(azulejo0));
	public static final Block azulejo2 = new BotaniaBlock(BlockBehaviour.Properties.copy(azulejo0));
	public static final Block azulejo3 = new BotaniaBlock(BlockBehaviour.Properties.copy(azulejo0));
	public static final Block azulejo4 = new BotaniaBlock(BlockBehaviour.Properties.copy(azulejo0));
	public static final Block azulejo5 = new BotaniaBlock(BlockBehaviour.Properties.copy(azulejo0));
	public static final Block azulejo6 = new BotaniaBlock(BlockBehaviour.Properties.copy(azulejo0));
	public static final Block azulejo7 = new BotaniaBlock(BlockBehaviour.Properties.copy(azulejo0));
	public static final Block azulejo8 = new BotaniaBlock(BlockBehaviour.Properties.copy(azulejo0));
	public static final Block azulejo9 = new BotaniaBlock(BlockBehaviour.Properties.copy(azulejo0));
	public static final Block azulejo10 = new BotaniaBlock(BlockBehaviour.Properties.copy(azulejo0));
	public static final Block azulejo11 = new BotaniaBlock(BlockBehaviour.Properties.copy(azulejo0));
	public static final Block azulejo12 = new BotaniaBlock(BlockBehaviour.Properties.copy(azulejo0));
	public static final Block azulejo13 = new BotaniaBlock(BlockBehaviour.Properties.copy(azulejo0));
	public static final Block azulejo14 = new BotaniaBlock(BlockBehaviour.Properties.copy(azulejo0));
	public static final Block azulejo15 = new BotaniaBlock(BlockBehaviour.Properties.copy(azulejo0));
	public static final Block manaFlame = new ManaFlameBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).sound(SoundType.WOOL).lightLevel(s -> 15).noCollission());
	public static final Block blazeBlock = new BotaniaBlock(BlockBehaviour.Properties.copy(manasteelBlock).instrument(NoteBlockInstrument.PLING).lightLevel(s -> 15).mapColor(MapColor.GOLD));
	public static final Block gaiaHead = new GaiaHeadBlock(BlockBehaviour.Properties.of().pushReaction(PushReaction.DESTROY).strength(1));
	public static final Block gaiaHeadWall = new WallGaiaHeadBlock(BlockBehaviour.Properties.copy(gaiaHead));
	public static final Block shimmerrock = new BotaniaBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block shimmerwoodPlanks = new BotaniaBlock(BlockBehaviour.Properties.copy(livingwood));
	public static final Block dryGrass = new BotaniaGrassBlock(BotaniaGrassBlock.Variant.DRY, BlockBehaviour.Properties.of().strength(0.6F).randomTicks().sound(SoundType.GRASS).mapColor(MapColor.TERRACOTTA_LIGHT_GREEN));
	public static final Block goldenGrass = new BotaniaGrassBlock(BotaniaGrassBlock.Variant.GOLDEN, BlockBehaviour.Properties.copy(dryGrass).mapColor(MapColor.GOLD));
	public static final Block vividGrass = new BotaniaGrassBlock(BotaniaGrassBlock.Variant.VIVID, BlockBehaviour.Properties.copy(dryGrass).mapColor(MapColor.PLANT));
	public static final Block scorchedGrass = new BotaniaGrassBlock(BotaniaGrassBlock.Variant.SCORCHED, BlockBehaviour.Properties.copy(dryGrass).mapColor(MapColor.NETHER));
	public static final Block infusedGrass = new BotaniaGrassBlock(BotaniaGrassBlock.Variant.INFUSED, BlockBehaviour.Properties.copy(dryGrass).mapColor(MapColor.COLOR_CYAN));
	public static final Block mutatedGrass = new BotaniaGrassBlock(BotaniaGrassBlock.Variant.MUTATED, BlockBehaviour.Properties.copy(dryGrass).mapColor(MapColor.WARPED_HYPHAE));

	public static final Block motifDaybloom = new FlowerMotifBlock(MobEffects.BLINDNESS, 15, BlockBehaviour.Properties.copy(Blocks.POPPY), true);
	public static final Block motifNightshade = new FlowerMotifBlock(MobEffects.POISON, 20, BlockBehaviour.Properties.copy(Blocks.POPPY), true);
	public static final Block motifHydroangeas = new FlowerMotifBlock(MobEffects.UNLUCK, 10, BlockBehaviour.Properties.copy(Blocks.POPPY), false);

	public static final Block pottedMotifDaybloom = flowerPot(motifDaybloom, 0);
	public static final Block pottedMotifNightshade = flowerPot(motifNightshade, 0);
	public static final Block pottedMotifHydroangeas = flowerPot(motifHydroangeas, 0);

	public static final Block livingwoodStairs = new BotaniaStairBlock(livingwood.defaultBlockState(), BlockBehaviour.Properties.copy(livingwood));
	public static final Block livingwoodSlab = new SlabBlock(BlockBehaviour.Properties.copy(livingwood));
	public static final Block livingwoodWall = new WallBlock(BlockBehaviour.Properties.copy(livingwood));
	public static final Block livingwoodFence = new FenceBlock(BlockBehaviour.Properties.copy(livingwood));
	public static final Block livingwoodFenceGate = new FenceGateBlock(BlockBehaviour.Properties.copy(livingwood), BotaniaBlockSetTypes.LIVINGWOOD);
	public static final Block livingwoodStrippedStairs = new BotaniaStairBlock(livingwoodStripped.defaultBlockState(), BlockBehaviour.Properties.copy(livingwood));
	public static final Block livingwoodStrippedSlab = new SlabBlock(BlockBehaviour.Properties.copy(livingwood));
	public static final Block livingwoodStrippedWall = new WallBlock(BlockBehaviour.Properties.copy(livingwood));
	public static final Block livingwoodPlankStairs = new BotaniaStairBlock(livingwoodPlanks.defaultBlockState(), BlockBehaviour.Properties.copy(livingwoodPlanks));
	public static final Block livingwoodPlankSlab = new SlabBlock(BlockBehaviour.Properties.copy(livingwoodPlanks));

	public static final Block livingrockStairs = new BotaniaStairBlock(livingrock.defaultBlockState(), BlockBehaviour.Properties.copy(livingrock));
	public static final Block livingrockSlab = new SlabBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block livingrockWall = new WallBlock(BlockBehaviour.Properties.copy(livingrock));
	public static final Block livingrockPolishedStairs = new BotaniaStairBlock(livingrock.defaultBlockState(), BlockBehaviour.Properties.copy(livingrockPolished));
	public static final Block livingrockPolishedSlab = new SlabBlock(BlockBehaviour.Properties.copy(livingrockPolished));
	public static final Block livingrockPolishedWall = new WallBlock(BlockBehaviour.Properties.copy(livingrockPolished));
	public static final Block livingrockBrickStairs = new BotaniaStairBlock(livingrockBrick.defaultBlockState(), BlockBehaviour.Properties.copy(livingrockBrick));
	public static final Block livingrockBrickSlab = new SlabBlock(BlockBehaviour.Properties.copy(livingrockBrick));
	public static final Block livingrockBrickWall = new WallBlock(BlockBehaviour.Properties.copy(livingrockBrick));
	public static final Block livingrockBrickMossyStairs = new BotaniaStairBlock(livingrockBrickMossy.defaultBlockState(), BlockBehaviour.Properties.copy(livingrockBrickMossy));
	public static final Block livingrockBrickMossySlab = new SlabBlock(BlockBehaviour.Properties.copy(livingrockBrickMossy));
	public static final Block livingrockBrickMossyWall = new WallBlock(BlockBehaviour.Properties.copy(livingrockBrickMossy));

	public static final Block dreamwoodStairs = new BotaniaStairBlock(dreamwood.defaultBlockState(), BlockBehaviour.Properties.copy(dreamwood));
	public static final Block dreamwoodSlab = new SlabBlock(BlockBehaviour.Properties.copy(dreamwood));
	public static final Block dreamwoodWall = new WallBlock(BlockBehaviour.Properties.copy(dreamwood));
	public static final Block dreamwoodFence = new FenceBlock(BlockBehaviour.Properties.copy(dreamwood));
	public static final Block dreamwoodFenceGate = new FenceGateBlock(BlockBehaviour.Properties.copy(dreamwood), BotaniaBlockSetTypes.DREAMWOOD);
	public static final Block dreamwoodStrippedStairs = new BotaniaStairBlock(dreamwoodStripped.defaultBlockState(), BlockBehaviour.Properties.copy(dreamwood));
	public static final Block dreamwoodStrippedSlab = new SlabBlock(BlockBehaviour.Properties.copy(dreamwood));
	public static final Block dreamwoodStrippedWall = new WallBlock(BlockBehaviour.Properties.copy(dreamwood));
	public static final Block dreamwoodPlankStairs = new BotaniaStairBlock(dreamwoodPlanks.defaultBlockState(), BlockBehaviour.Properties.copy(dreamwoodPlanks));
	public static final Block dreamwoodPlankSlab = new SlabBlock(BlockBehaviour.Properties.copy(dreamwoodPlanks));

	public static final Block darkQuartz = new BotaniaBlock(BlockBehaviour.Properties.copy(Blocks.QUARTZ_BLOCK).mapColor(MapColor.COLOR_BLACK));
	public static final Block darkQuartzChiseled = new BotaniaBlock(BlockBehaviour.Properties.copy(darkQuartz));
	public static final Block darkQuartzPillar = new RotatedPillarBlock(BlockBehaviour.Properties.copy(darkQuartz));
	public static final Block darkQuartzSlab = new SlabBlock(BlockBehaviour.Properties.copy(darkQuartz));
	public static final Block darkQuartzStairs = new BotaniaStairBlock(darkQuartz.defaultBlockState(), BlockBehaviour.Properties.copy(darkQuartz));

	public static final Block manaQuartz = new BotaniaBlock(BlockBehaviour.Properties.copy(darkQuartz).mapColor(MapColor.DIAMOND));
	public static final Block manaQuartzChiseled = new BotaniaBlock(BlockBehaviour.Properties.copy(manaQuartz));
	public static final Block manaQuartzPillar = new RotatedPillarBlock(BlockBehaviour.Properties.copy(manaQuartz));
	public static final Block manaQuartzSlab = new SlabBlock(BlockBehaviour.Properties.copy(manaQuartz));
	public static final Block manaQuartzStairs = new BotaniaStairBlock(manaQuartz.defaultBlockState(), BlockBehaviour.Properties.copy(darkQuartz));

	public static final Block blazeQuartz = new BotaniaBlock(BlockBehaviour.Properties.copy(darkQuartz).mapColor(MapColor.SAND));
	public static final Block blazeQuartzChiseled = new BotaniaBlock(BlockBehaviour.Properties.copy(blazeQuartz));
	public static final Block blazeQuartzPillar = new RotatedPillarBlock(BlockBehaviour.Properties.copy(blazeQuartz));
	public static final Block blazeQuartzSlab = new SlabBlock(BlockBehaviour.Properties.copy(blazeQuartz));
	public static final Block blazeQuartzStairs = new BotaniaStairBlock(blazeQuartz.defaultBlockState(), BlockBehaviour.Properties.copy(blazeQuartz));

	public static final Block lavenderQuartz = new BotaniaBlock(BlockBehaviour.Properties.copy(darkQuartz).mapColor(MapColor.COLOR_PINK));
	public static final Block lavenderQuartzChiseled = new BotaniaBlock(BlockBehaviour.Properties.copy(lavenderQuartz));
	public static final Block lavenderQuartzPillar = new RotatedPillarBlock(BlockBehaviour.Properties.copy(lavenderQuartz));
	public static final Block lavenderQuartzSlab = new SlabBlock(BlockBehaviour.Properties.copy(lavenderQuartz));
	public static final Block lavenderQuartzStairs = new BotaniaStairBlock(lavenderQuartz.defaultBlockState(), BlockBehaviour.Properties.copy(lavenderQuartz));

	public static final Block redQuartz = new BotaniaBlock(BlockBehaviour.Properties.copy(darkQuartz).mapColor(MapColor.TERRACOTTA_WHITE));
	public static final Block redQuartzChiseled = new BotaniaBlock(BlockBehaviour.Properties.copy(redQuartz));
	public static final Block redQuartzPillar = new RotatedPillarBlock(BlockBehaviour.Properties.copy(redQuartz));
	public static final Block redQuartzSlab = new SlabBlock(BlockBehaviour.Properties.copy(redQuartz));
	public static final Block redQuartzStairs = new BotaniaStairBlock(redQuartz.defaultBlockState(), BlockBehaviour.Properties.copy(redQuartz));

	public static final Block elfQuartz = new BotaniaBlock(BlockBehaviour.Properties.copy(darkQuartz).mapColor(MapColor.COLOR_LIGHT_GREEN));
	public static final Block elfQuartzChiseled = new BotaniaBlock(BlockBehaviour.Properties.copy(elfQuartz));
	public static final Block elfQuartzPillar = new RotatedPillarBlock(BlockBehaviour.Properties.copy(elfQuartz));
	public static final Block elfQuartzSlab = new SlabBlock(BlockBehaviour.Properties.copy(elfQuartz));
	public static final Block elfQuartzStairs = new BotaniaStairBlock(elfQuartz.defaultBlockState(), BlockBehaviour.Properties.copy(elfQuartz));

	public static final Block sunnyQuartz = new BotaniaBlock(BlockBehaviour.Properties.copy(darkQuartz).mapColor(MapColor.COLOR_YELLOW));
	public static final Block sunnyQuartzChiseled = new BotaniaBlock(BlockBehaviour.Properties.copy(sunnyQuartz));
	public static final Block sunnyQuartzPillar = new RotatedPillarBlock(BlockBehaviour.Properties.copy(sunnyQuartz));
	public static final Block sunnyQuartzSlab = new SlabBlock(BlockBehaviour.Properties.copy(sunnyQuartz));
	public static final Block sunnyQuartzStairs = new BotaniaStairBlock(sunnyQuartz.defaultBlockState(), BlockBehaviour.Properties.copy(sunnyQuartz));

	public static final Block whitePavement = new BotaniaBlock(BlockBehaviour.Properties.copy(livingrock).mapColor(MapColor.TERRACOTTA_WHITE));
	public static final Block whitePavementStair = new BotaniaStairBlock(whitePavement.defaultBlockState(), BlockBehaviour.Properties.copy(whitePavement));
	public static final Block whitePavementSlab = new SlabBlock(BlockBehaviour.Properties.copy(whitePavement));

	public static final Block blackPavement = new BotaniaBlock(BlockBehaviour.Properties.copy(whitePavement).mapColor(MapColor.COLOR_GRAY));
	public static final Block blackPavementStair = new BotaniaStairBlock(blackPavement.defaultBlockState(), BlockBehaviour.Properties.copy(blackPavement));
	public static final Block blackPavementSlab = new SlabBlock(BlockBehaviour.Properties.copy(blackPavement));

	public static final Block bluePavement = new BotaniaBlock(BlockBehaviour.Properties.copy(whitePavement).mapColor(MapColor.COLOR_BLUE));
	public static final Block bluePavementStair = new BotaniaStairBlock(bluePavement.defaultBlockState(), BlockBehaviour.Properties.copy(bluePavement));
	public static final Block bluePavementSlab = new SlabBlock(BlockBehaviour.Properties.copy(bluePavement));

	public static final Block yellowPavement = new BotaniaBlock(BlockBehaviour.Properties.copy(whitePavement).mapColor(MapColor.TERRACOTTA_YELLOW));
	public static final Block yellowPavementStair = new BotaniaStairBlock(yellowPavement.defaultBlockState(), BlockBehaviour.Properties.copy(yellowPavement));
	public static final Block yellowPavementSlab = new SlabBlock(BlockBehaviour.Properties.copy(yellowPavement));

	public static final Block redPavement = new BotaniaBlock(BlockBehaviour.Properties.copy(whitePavement).mapColor(MapColor.TERRACOTTA_RED));
	public static final Block redPavementStair = new BotaniaStairBlock(redPavement.defaultBlockState(), BlockBehaviour.Properties.copy(redPavement));
	public static final Block redPavementSlab = new SlabBlock(BlockBehaviour.Properties.copy(redPavement));

	public static final Block greenPavement = new BotaniaBlock(BlockBehaviour.Properties.copy(whitePavement).mapColor(MapColor.TERRACOTTA_GREEN));
	public static final Block greenPavementStair = new BotaniaStairBlock(greenPavement.defaultBlockState(), BlockBehaviour.Properties.copy(greenPavement));
	public static final Block greenPavementSlab = new SlabBlock(BlockBehaviour.Properties.copy(greenPavement));

	public static final Block biomeStoneForest = new BotaniaBlock(BlockBehaviour.Properties.of().strength(1.5F, 10)
			.sound(SoundType.TUFF).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().mapColor(MapColor.WARPED_NYLIUM));
	public static final Block biomeStoneForestSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneForest));
	public static final Block biomeStoneForestStairs = new BotaniaStairBlock(biomeStoneForest.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneForest));
	public static final Block biomeStoneForestWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneForest));
	public static final Block biomeCobblestoneForest = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneForest));
	public static final Block biomeCobblestoneForestSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneForest));
	public static final Block biomeCobblestoneForestStairs = new BotaniaStairBlock(biomeStoneForest.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneForest));
	public static final Block biomeCobblestoneForestWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneForest));
	public static final Block biomeBrickForest = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneForest));
	public static final Block biomeBrickForestSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneForest));
	public static final Block biomeBrickForestStairs = new BotaniaStairBlock(biomeStoneForest.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneForest));
	public static final Block biomeBrickForestWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneForest));
	public static final Block biomeChiseledBrickForest = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneForest));

	public static final Block biomeStonePlains = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneForest)
			.sound(SoundType.CALCITE).mapColor(MapColor.QUARTZ));
	public static final Block biomeStonePlainsSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStonePlains));
	public static final Block biomeStonePlainsStairs = new BotaniaStairBlock(biomeStonePlains.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStonePlains));
	public static final Block biomeStonePlainsWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStonePlains));
	public static final Block biomeCobblestonePlains = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStonePlains));
	public static final Block biomeCobblestonePlainsSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStonePlains));
	public static final Block biomeCobblestonePlainsStairs = new BotaniaStairBlock(biomeStonePlains.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStonePlains));
	public static final Block biomeCobblestonePlainsWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStonePlains));
	public static final Block biomeBrickPlains = new RotatedPillarBlock(BlockBehaviour.Properties.copy(biomeStonePlains));
	public static final Block biomeBrickPlainsSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStonePlains));
	public static final Block biomeBrickPlainsStairs = new BotaniaStairBlock(biomeStonePlains.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStonePlains));
	public static final Block biomeBrickPlainsWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStonePlains));
	public static final Block biomeChiseledBrickPlains = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStonePlains));

	public static final Block biomeStoneMountain = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneForest)
			.sound(SoundType.DEEPSLATE_TILES).mapColor(MapColor.GLOW_LICHEN));
	public static final Block biomeStoneMountainSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneMountain));
	public static final Block biomeStoneMountainStairs = new BotaniaStairBlock(biomeStoneMountain.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneMountain));
	public static final Block biomeStoneMountainWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneMountain));
	public static final Block biomeCobblestoneMountain = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneMountain));
	public static final Block biomeCobblestoneMountainSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneMountain));
	public static final Block biomeCobblestoneMountainStairs = new BotaniaStairBlock(biomeStoneMountain.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneMountain));
	public static final Block biomeCobblestoneMountainWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneMountain));
	public static final Block biomeBrickMountain = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneMountain));
	public static final Block biomeBrickMountainSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneMountain));
	public static final Block biomeBrickMountainStairs = new BotaniaStairBlock(biomeStoneMountain.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneMountain));
	public static final Block biomeBrickMountainWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneMountain));
	public static final Block biomeChiseledBrickMountain = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneMountain));

	public static final Block biomeStoneFungal = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneForest)
			.sound(SoundType.DEEPSLATE_BRICKS).mapColor(MapColor.TERRACOTTA_PURPLE));
	public static final Block biomeStoneFungalSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneFungal));
	public static final Block biomeStoneFungalStairs = new BotaniaStairBlock(biomeStoneFungal.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneFungal));
	public static final Block biomeStoneFungalWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneFungal));
	public static final Block biomeCobblestoneFungal = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneFungal));
	public static final Block biomeCobblestoneFungalSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneFungal));
	public static final Block biomeCobblestoneFungalStairs = new BotaniaStairBlock(biomeStoneFungal.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneFungal));
	public static final Block biomeCobblestoneFungalWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneFungal));
	public static final Block biomeBrickFungal = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneFungal));
	public static final Block biomeBrickFungalSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneFungal));
	public static final Block biomeBrickFungalStairs = new BotaniaStairBlock(biomeStoneFungal.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneFungal));
	public static final Block biomeBrickFungalWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneFungal));
	public static final Block biomeChiseledBrickFungal = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneFungal));

	public static final Block biomeStoneSwamp = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneForest)
			.sound(SoundType.DEEPSLATE_TILES).mapColor(MapColor.TERRACOTTA_LIGHT_GRAY));
	public static final Block biomeStoneSwampSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneSwamp));
	public static final Block biomeStoneSwampStairs = new BotaniaStairBlock(biomeStoneSwamp.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneSwamp));
	public static final Block biomeStoneSwampWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneSwamp));
	public static final Block biomeCobblestoneSwamp = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneSwamp));
	public static final Block biomeCobblestoneSwampSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneSwamp));
	public static final Block biomeCobblestoneSwampStairs = new BotaniaStairBlock(biomeStoneSwamp.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneSwamp));
	public static final Block biomeCobblestoneSwampWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneSwamp));
	public static final Block biomeBrickSwamp = new BotaniaDirectionalBlock(BlockBehaviour.Properties.copy(biomeStoneSwamp));
	public static final Block biomeBrickSwampSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneSwamp));
	public static final Block biomeBrickSwampStairs = new BotaniaStairBlock(biomeStoneSwamp.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneSwamp));
	public static final Block biomeBrickSwampWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneSwamp));
	public static final Block biomeChiseledBrickSwamp = new BotaniaDirectionalBlock(BlockBehaviour.Properties.copy(biomeStoneSwamp));

	public static final Block biomeStoneDesert = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneForest)
			.sound(SoundType.DEEPSLATE).mapColor(MapColor.DIRT));
	public static final Block biomeStoneDesertSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneDesert));
	public static final Block biomeStoneDesertStairs = new BotaniaStairBlock(biomeStoneDesert.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneDesert));
	public static final Block biomeStoneDesertWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneDesert));
	public static final Block biomeCobblestoneDesert = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneDesert));
	public static final Block biomeCobblestoneDesertSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneDesert));
	public static final Block biomeCobblestoneDesertStairs = new BotaniaStairBlock(biomeStoneDesert.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneDesert));
	public static final Block biomeCobblestoneDesertWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneDesert));
	public static final Block biomeBrickDesert = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneDesert));
	public static final Block biomeBrickDesertSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneDesert));
	public static final Block biomeBrickDesertStairs = new BotaniaStairBlock(biomeStoneDesert.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneDesert));
	public static final Block biomeBrickDesertWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneDesert));
	public static final Block biomeChiseledBrickDesert = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneDesert));

	public static final Block biomeStoneTaiga = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneForest)
			.sound(SoundType.DEEPSLATE).mapColor(MapColor.TERRACOTTA_BLUE));
	public static final Block biomeStoneTaigaSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneTaiga));
	public static final Block biomeStoneTaigaStairs = new BotaniaStairBlock(biomeStoneTaiga.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneTaiga));
	public static final Block biomeStoneTaigaWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneTaiga));
	public static final Block biomeCobblestoneTaiga = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneTaiga));
	public static final Block biomeCobblestoneTaigaSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneTaiga));
	public static final Block biomeCobblestoneTaigaStairs = new BotaniaStairBlock(biomeStoneTaiga.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneTaiga));
	public static final Block biomeCobblestoneTaigaWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneTaiga));
	public static final Block biomeBrickTaiga = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneTaiga));
	public static final Block biomeBrickTaigaSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneTaiga));
	public static final Block biomeBrickTaigaStairs = new BotaniaStairBlock(biomeStoneTaiga.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneTaiga));
	public static final Block biomeBrickTaigaWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneTaiga));
	public static final Block biomeChiseledBrickTaiga = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneTaiga));

	public static final Block biomeStoneMesa = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneForest)
			.sound(SoundType.CALCITE).mapColor(MapColor.TERRACOTTA_WHITE));
	public static final Block biomeStoneMesaSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneMesa));
	public static final Block biomeStoneMesaStairs = new BotaniaStairBlock(biomeStoneMesa.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneMesa));
	public static final Block biomeStoneMesaWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneMesa));
	public static final Block biomeCobblestoneMesa = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneMesa));
	public static final Block biomeCobblestoneMesaSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneMesa));
	public static final Block biomeCobblestoneMesaStairs = new BotaniaStairBlock(biomeStoneMesa.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneMesa));
	public static final Block biomeCobblestoneMesaWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneMesa));
	public static final Block biomeBrickMesa = new BotaniaBlock(BlockBehaviour.Properties.copy(biomeStoneMesa));
	public static final Block biomeBrickMesaSlab = new SlabBlock(BlockBehaviour.Properties.copy(biomeStoneMesa));
	public static final Block biomeBrickMesaStairs = new BotaniaStairBlock(biomeStoneMesa.defaultBlockState(), BlockBehaviour.Properties.copy(biomeStoneMesa));
	public static final Block biomeBrickMesaWall = new WallBlock(BlockBehaviour.Properties.copy(biomeStoneMesa));
	public static final Block biomeChiseledBrickMesa = new RotatedPillarBlock(BlockBehaviour.Properties.copy(biomeStoneMesa));

	public static final Block shimmerrockSlab = new SlabBlock(BlockBehaviour.Properties.copy(shimmerrock));
	public static final Block shimmerrockStairs = new BotaniaStairBlock(shimmerrock.defaultBlockState(), BlockBehaviour.Properties.copy(shimmerrock));

	public static final Block shimmerwoodPlankSlab = new SlabBlock(BlockBehaviour.Properties.copy(shimmerwoodPlanks));
	public static final Block shimmerwoodPlankStairs = new BotaniaStairBlock(shimmerwoodPlanks.defaultBlockState(), BlockBehaviour.Properties.copy(shimmerwoodPlanks));

	public static final Block managlassPane = new BotaniaPaneBlock(BlockBehaviour.Properties.copy(manaGlass));
	public static final Block alfglassPane = new BotaniaPaneBlock(BlockBehaviour.Properties.copy(elfGlass));
	public static final Block bifrostPane = new BotaniaPaneBlock(BlockBehaviour.Properties.copy(bifrostPerm));

	static FlowerPotBlock flowerPot(Block block, int lightLevel) {
		BlockBehaviour.Properties properties = BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY);
		return new FlowerPotBlock(block, lightLevel > 0 ? properties.lightLevel(blockState -> lightLevel) : properties);
	}

	public static void registerBlocks(BiConsumer<Block, ResourceLocation> r) {
		// triples of: block getter from dye color, block ID prefix, block ID suffix
		Stream.<Triple<Function<DyeColor, Block>, String, String>>of(
				Triple.of(BotaniaBlocks::getFlower, "", LibBlockNames.MYSTICAL_FLOWER_SUFFIX),
				Triple.of(BotaniaBlocks::getShinyFlower, "", LibBlockNames.SHINY_FLOWER_SUFFIX),
				Triple.of(BotaniaBlocks::getBuriedPetal, "", LibBlockNames.BURIED_PETALS_SUFFIX),
				Triple.of(BotaniaBlocks::getFloatingFlower, "", LibBlockNames.FLOATING_FLOWER_SUFFIX),
				Triple.of(BotaniaBlocks::getPetalBlock, "", LibBlockNames.PETAL_BLOCK_SUFFIX),
				Triple.of(BotaniaBlocks::getMushroom, "", LibBlockNames.MUSHROOM_SUFFIX),
				Triple.of(BotaniaBlocks::getDoubleFlower, "", LibBlockNames.DOUBLE_FLOWER_SUFFIX),
				Triple.of(BotaniaBlocks::getPottedFlower, LibBlockNames.POTTED_PREFIX, LibBlockNames.MYSTICAL_FLOWER_SUFFIX),
				Triple.of(BotaniaBlocks::getPottedShinyFlower, LibBlockNames.POTTED_PREFIX, LibBlockNames.SHINY_FLOWER_SUFFIX),
				Triple.of(BotaniaBlocks::getPottedMushroom, LibBlockNames.POTTED_PREFIX, LibBlockNames.MUSHROOM_SUFFIX)
		).forEach(coloredBlockRegistration -> {
			ColorHelper.supportedColors().forEach(dyeColor -> {
				r.accept(coloredBlockRegistration.getLeft().apply(dyeColor),
						prefix(coloredBlockRegistration.getMiddle() + dyeColor.getName() + coloredBlockRegistration.getRight()));
			});
		});

		r.accept(defaultAltar, prefix(LibBlockNames.APOTHECARY_PREFIX + PetalApothecaryBlock.Variant.DEFAULT.name().toLowerCase(Locale.ROOT)));
		r.accept(forestAltar, prefix(LibBlockNames.APOTHECARY_PREFIX + PetalApothecaryBlock.Variant.FOREST.name().toLowerCase(Locale.ROOT)));
		r.accept(plainsAltar, prefix(LibBlockNames.APOTHECARY_PREFIX + PetalApothecaryBlock.Variant.PLAINS.name().toLowerCase(Locale.ROOT)));
		r.accept(mountainAltar, prefix(LibBlockNames.APOTHECARY_PREFIX + PetalApothecaryBlock.Variant.MOUNTAIN.name().toLowerCase(Locale.ROOT)));
		r.accept(fungalAltar, prefix(LibBlockNames.APOTHECARY_PREFIX + PetalApothecaryBlock.Variant.FUNGAL.name().toLowerCase(Locale.ROOT)));
		r.accept(swampAltar, prefix(LibBlockNames.APOTHECARY_PREFIX + PetalApothecaryBlock.Variant.SWAMP.name().toLowerCase(Locale.ROOT)));
		r.accept(desertAltar, prefix(LibBlockNames.APOTHECARY_PREFIX + PetalApothecaryBlock.Variant.DESERT.name().toLowerCase(Locale.ROOT)));
		r.accept(taigaAltar, prefix(LibBlockNames.APOTHECARY_PREFIX + PetalApothecaryBlock.Variant.TAIGA.name().toLowerCase(Locale.ROOT)));
		r.accept(mesaAltar, prefix(LibBlockNames.APOTHECARY_PREFIX + PetalApothecaryBlock.Variant.MESA.name().toLowerCase(Locale.ROOT)));
		r.accept(mossyAltar, prefix(LibBlockNames.APOTHECARY_PREFIX + PetalApothecaryBlock.Variant.MOSSY.name().toLowerCase(Locale.ROOT)));
		r.accept(livingrockAltar, prefix(LibBlockNames.APOTHECARY_PREFIX + PetalApothecaryBlock.Variant.LIVINGROCK.name().toLowerCase(Locale.ROOT)));
		r.accept(deepslateAltar, prefix(LibBlockNames.APOTHECARY_PREFIX + PetalApothecaryBlock.Variant.DEEPSLATE.name().toLowerCase(Locale.ROOT)));

		r.accept(livingrock, prefix(LibBlockNames.LIVING_ROCK));
		r.accept(livingrockStairs, prefix(LibBlockNames.LIVING_ROCK + STAIR_SUFFIX));
		r.accept(livingrockSlab, prefix(LibBlockNames.LIVING_ROCK + SLAB_SUFFIX));
		r.accept(livingrockWall, prefix(LibBlockNames.LIVING_ROCK + WALL_SUFFIX));
		r.accept(livingrockPolished, prefix(LibBlockNames.LIVING_ROCK_POLISHED));
		r.accept(livingrockPolishedStairs, prefix(LibBlockNames.LIVING_ROCK_POLISHED + STAIR_SUFFIX));
		r.accept(livingrockPolishedSlab, prefix(LibBlockNames.LIVING_ROCK_POLISHED + SLAB_SUFFIX));
		r.accept(livingrockPolishedWall, prefix(LibBlockNames.LIVING_ROCK_POLISHED + WALL_SUFFIX));
		r.accept(livingrockSlate, prefix(LibBlockNames.LIVING_ROCK_SLATE));
		r.accept(livingrockBrick, prefix(LibBlockNames.LIVING_ROCK_BRICK));
		r.accept(livingrockBrickStairs, prefix(LibBlockNames.LIVING_ROCK_BRICK + STAIR_SUFFIX));
		r.accept(livingrockBrickSlab, prefix(LibBlockNames.LIVING_ROCK_BRICK + SLAB_SUFFIX));
		r.accept(livingrockBrickWall, prefix(LibBlockNames.LIVING_ROCK_BRICK + WALL_SUFFIX));
		r.accept(livingrockBrickMossy, prefix(LibBlockNames.LIVING_ROCK_BRICK_MOSSY));
		r.accept(livingrockBrickMossyStairs, prefix(LibBlockNames.LIVING_ROCK_BRICK_MOSSY + STAIR_SUFFIX));
		r.accept(livingrockBrickMossySlab, prefix(LibBlockNames.LIVING_ROCK_BRICK_MOSSY + SLAB_SUFFIX));
		r.accept(livingrockBrickMossyWall, prefix(LibBlockNames.LIVING_ROCK_BRICK_MOSSY + WALL_SUFFIX));
		r.accept(livingrockBrickChiseled, prefix(LibBlockNames.LIVING_ROCK_BRICK_CHISELED));
		r.accept(livingrockBrickCracked, prefix(LibBlockNames.LIVING_ROCK_BRICK_CRACKED));

		r.accept(livingwoodLog, prefix(LibBlockNames.LIVING_WOOD_LOG));
		r.accept(livingwood, prefix(LibBlockNames.LIVING_WOOD));
		r.accept(livingwoodStairs, prefix(LibBlockNames.LIVING_WOOD + STAIR_SUFFIX));
		r.accept(livingwoodSlab, prefix(LibBlockNames.LIVING_WOOD + SLAB_SUFFIX));
		r.accept(livingwoodWall, prefix(LibBlockNames.LIVING_WOOD + WALL_SUFFIX));
		r.accept(livingwoodLogStripped, prefix(LibBlockNames.LIVING_WOOD_LOG_STRIPPED));
		r.accept(livingwoodStripped, prefix(LibBlockNames.LIVING_WOOD_STRIPPED));
		r.accept(livingwoodStrippedStairs, prefix(LibBlockNames.LIVING_WOOD_STRIPPED + STAIR_SUFFIX));
		r.accept(livingwoodStrippedSlab, prefix(LibBlockNames.LIVING_WOOD_STRIPPED + SLAB_SUFFIX));
		r.accept(livingwoodStrippedWall, prefix(LibBlockNames.LIVING_WOOD_STRIPPED + WALL_SUFFIX));
		r.accept(livingwoodLogGlimmering, prefix(LibBlockNames.LIVING_WOOD_LOG_GLIMMERING));
		r.accept(livingwoodGlimmering, prefix(LibBlockNames.LIVING_WOOD_GLIMMERING));
		r.accept(livingwoodLogStrippedGlimmering, prefix(LibBlockNames.LIVING_WOOD_LOG_GLIMMERING_STRIPPED));
		r.accept(livingwoodStrippedGlimmering, prefix(LibBlockNames.LIVING_WOOD_GLIMMERING_STRIPPED));
		r.accept(livingwoodPlanks, prefix(LibBlockNames.LIVING_WOOD_PLANKS));
		r.accept(livingwoodPlankStairs, prefix(LibBlockNames.LIVING_WOOD_PLANKS + STAIR_SUFFIX));
		r.accept(livingwoodPlankSlab, prefix(LibBlockNames.LIVING_WOOD_PLANKS + SLAB_SUFFIX));
		r.accept(livingwoodFence, prefix(LibBlockNames.LIVING_WOOD + FENCE_SUFFIX));
		r.accept(livingwoodFenceGate, prefix(LibBlockNames.LIVING_WOOD + FENCE_GATE_SUFFIX));
		r.accept(livingwoodPlanksMossy, prefix(LibBlockNames.LIVING_WOOD_PLANKS_MOSSY));
		r.accept(livingwoodFramed, prefix(LibBlockNames.LIVING_WOOD_FRAMED));
		r.accept(livingwoodPatternFramed, prefix(LibBlockNames.LIVING_WOOD_PATTERN_FRAMED));

		r.accept(dreamwoodLog, prefix(LibBlockNames.DREAM_WOOD_LOG));
		r.accept(dreamwood, prefix(LibBlockNames.DREAM_WOOD));
		r.accept(dreamwoodStairs, prefix(LibBlockNames.DREAM_WOOD + STAIR_SUFFIX));
		r.accept(dreamwoodSlab, prefix(LibBlockNames.DREAM_WOOD + SLAB_SUFFIX));
		r.accept(dreamwoodWall, prefix(LibBlockNames.DREAM_WOOD + WALL_SUFFIX));
		r.accept(dreamwoodLogStripped, prefix(LibBlockNames.DREAM_WOOD_LOG_STRIPPED));
		r.accept(dreamwoodStripped, prefix(LibBlockNames.DREAM_WOOD_STRIPPED));
		r.accept(dreamwoodStrippedStairs, prefix(LibBlockNames.DREAM_WOOD_STRIPPED + STAIR_SUFFIX));
		r.accept(dreamwoodStrippedSlab, prefix(LibBlockNames.DREAM_WOOD_STRIPPED + SLAB_SUFFIX));
		r.accept(dreamwoodStrippedWall, prefix(LibBlockNames.DREAM_WOOD_STRIPPED + WALL_SUFFIX));
		r.accept(dreamwoodLogGlimmering, prefix(LibBlockNames.DREAM_WOOD_LOG_GLIMMERING));
		r.accept(dreamwoodGlimmering, prefix(LibBlockNames.DREAM_WOOD_GLIMMERING));
		r.accept(dreamwoodLogStrippedGlimmering, prefix(LibBlockNames.DREAM_WOOD_LOG_GLIMMERING_STRIPPED));
		r.accept(dreamwoodStrippedGlimmering, prefix(LibBlockNames.DREAM_WOOD_GLIMMERING_STRIPPED));
		r.accept(dreamwoodPlanks, prefix(LibBlockNames.DREAM_WOOD_PLANKS));
		r.accept(dreamwoodPlankStairs, prefix(LibBlockNames.DREAM_WOOD_PLANKS + STAIR_SUFFIX));
		r.accept(dreamwoodPlankSlab, prefix(LibBlockNames.DREAM_WOOD_PLANKS + SLAB_SUFFIX));
		r.accept(dreamwoodFence, prefix(LibBlockNames.DREAM_WOOD + FENCE_SUFFIX));
		r.accept(dreamwoodFenceGate, prefix(LibBlockNames.DREAM_WOOD + FENCE_GATE_SUFFIX));
		r.accept(dreamwoodPlanksMossy, prefix(LibBlockNames.DREAM_WOOD_PLANKS_MOSSY));
		r.accept(dreamwoodFramed, prefix(LibBlockNames.DREAM_WOOD_FRAMED));
		r.accept(dreamwoodPatternFramed, prefix(LibBlockNames.DREAM_WOOD_PATTERN_FRAMED));

		r.accept(manaSpreader, prefix(LibBlockNames.SPREADER));
		r.accept(redstoneSpreader, prefix(LibBlockNames.SPREADER_REDSTONE));
		r.accept(elvenSpreader, prefix(LibBlockNames.SPREADER_ELVEN));
		r.accept(gaiaSpreader, prefix(LibBlockNames.SPREADER_GAIA));
		r.accept(manaPool, prefix(LibBlockNames.POOL));
		r.accept(creativePool, prefix(LibBlockNames.POOL_CREATIVE));
		r.accept(dilutedPool, prefix(LibBlockNames.POOL_DILUTED));
		r.accept(fabulousPool, prefix(LibBlockNames.POOL_FABULOUS));
		r.accept(alchemyCatalyst, prefix(LibBlockNames.ALCHEMY_CATALYST));
		r.accept(conjurationCatalyst, prefix(LibBlockNames.CONJURATION_CATALYST));
		r.accept(manasteelBlock, prefix(LibBlockNames.MANASTEEL_BLOCK));
		r.accept(terrasteelBlock, prefix(LibBlockNames.TERRASTEEL_BLOCK));
		r.accept(elementiumBlock, prefix(LibBlockNames.ELEMENTIUM_BLOCK));
		r.accept(manaDiamondBlock, prefix(LibBlockNames.MANA_DIAMOND_BLOCK));
		r.accept(dragonstoneBlock, prefix(LibBlockNames.DRAGONSTONE_BLOCK));
		r.accept(manaGlass, prefix(LibBlockNames.MANA_GLASS));
		r.accept(elfGlass, prefix(LibBlockNames.ELF_GLASS));
		r.accept(bifrost, prefix(LibBlockNames.BIFROST));
		r.accept(bifrostPerm, prefix(LibBlockNames.BIFROST_PERM));
		r.accept(runeAltar, prefix(LibBlockNames.RUNE_ALTAR));
		r.accept(enchanter, prefix(LibBlockNames.ENCHANTER));
		r.accept(brewery, prefix(LibBlockNames.BREWERY));
		r.accept(terraPlate, prefix(LibBlockNames.TERRA_PLATE));
		r.accept(alfPortal, prefix(LibBlockNames.ALF_PORTAL));
		r.accept(manaPylon, prefix(LibBlockNames.PYLON));
		r.accept(naturaPylon, prefix(LibBlockNames.PYLON_NATURA));
		r.accept(gaiaPylon, prefix(LibBlockNames.PYLON_GAIA));
		r.accept(distributor, prefix(LibBlockNames.DISTRIBUTOR));
		r.accept(manaVoid, prefix(LibBlockNames.MANA_VOID));
		r.accept(manaDetector, prefix(LibBlockNames.MANA_DETECTOR));
		r.accept(pistonRelay, prefix(LibBlockNames.PISTON_RELAY));
		r.accept(turntable, prefix(LibBlockNames.TURNTABLE));
		r.accept(tinyPlanet, prefix(LibBlockNames.TINY_PLANET));
		r.accept(wildDrum, prefix(LibBlockNames.DRUM_WILD));
		r.accept(gatheringDrum, prefix(LibBlockNames.DRUM_GATHERING));
		r.accept(canopyDrum, prefix(LibBlockNames.DRUM_CANOPY));
		r.accept(spawnerClaw, prefix(LibBlockNames.SPAWNER_CLAW));
		r.accept(rfGenerator, prefix(LibBlockNames.FLUXFIELD));
		r.accept(prism, prefix(LibBlockNames.PRISM));
		r.accept(pump, prefix(LibBlockNames.PUMP));
		r.accept(sparkChanger, prefix(LibBlockNames.SPARK_CHANGER));
		r.accept(manaBomb, prefix(LibBlockNames.MANA_BOMB));
		r.accept(bellows, prefix(LibBlockNames.BELLOWS));
		r.accept(openCrate, prefix(LibBlockNames.OPEN_CRATE));
		r.accept(craftCrate, prefix(LibBlockNames.CRAFT_CRATE));
		r.accept(forestEye, prefix(LibBlockNames.FOREST_EYE));
		r.accept(solidVines, prefix(LibBlockNames.SOLID_VINE));
		r.accept(abstrusePlatform, prefix(LibBlockNames.PLATFORM_ABSTRUSE));
		r.accept(spectralPlatform, prefix(LibBlockNames.PLATFORM_SPECTRAL));
		r.accept(infrangiblePlatform, prefix(LibBlockNames.PLATFORM_INFRANGIBLE));
		r.accept(tinyPotato, prefix(LibBlockNames.TINY_POTATO));
		r.accept(enderEye, prefix(LibBlockNames.ENDER_EYE_BLOCK));
		r.accept(redStringContainer, prefix(LibBlockNames.RED_STRING_CONTAINER));
		r.accept(redStringDispenser, prefix(LibBlockNames.RED_STRING_DISPENSER));
		r.accept(redStringFertilizer, prefix(LibBlockNames.RED_STRING_FERTILIZER));
		r.accept(redStringComparator, prefix(LibBlockNames.RED_STRING_COMPARATOR));
		r.accept(redStringRelay, prefix(LibBlockNames.RED_STRING_RELAY));
		r.accept(redStringInterceptor, prefix(LibBlockNames.RED_STRING_INTERCEPTOR));
		r.accept(corporeaIndex, prefix(LibBlockNames.CORPOREA_INDEX));
		r.accept(corporeaFunnel, prefix(LibBlockNames.CORPOREA_FUNNEL));
		r.accept(corporeaInterceptor, prefix(LibBlockNames.CORPOREA_INTERCEPTOR));
		r.accept(corporeaCrystalCube, prefix(LibBlockNames.CORPOREA_CRYSTAL_CUBE));
		r.accept(corporeaRetainer, prefix(LibBlockNames.CORPOREA_RETAINER));
		r.accept(corporeaBlock, prefix(LibBlockNames.CORPOREA_BLOCK));
		r.accept(corporeaSlab, prefix(LibBlockNames.CORPOREA_SLAB));
		r.accept(corporeaStairs, prefix(LibBlockNames.CORPOREA_STAIRS));
		r.accept(corporeaBrick, prefix(LibBlockNames.CORPOREA_BRICK));
		r.accept(corporeaBrickSlab, prefix(LibBlockNames.CORPOREA_BRICK + LibBlockNames.SLAB_SUFFIX));
		r.accept(corporeaBrickStairs, prefix(LibBlockNames.CORPOREA_BRICK + LibBlockNames.STAIR_SUFFIX));
		r.accept(corporeaBrickWall, prefix(LibBlockNames.CORPOREA_BRICK + LibBlockNames.WALL_SUFFIX));
		r.accept(incensePlate, prefix(LibBlockNames.INCENSE_PLATE));
		r.accept(hourglass, prefix(LibBlockNames.HOURGLASS));
		r.accept(ghostRail, prefix(LibBlockNames.GHOST_RAIL));
		r.accept(lightRelayDefault, prefix(LibBlockNames.LIGHT_RELAY));
		r.accept(lightRelayDetector, prefix("detector" + LibBlockNames.LIGHT_RELAY_SUFFIX));
		r.accept(lightRelayFork, prefix("fork" + LibBlockNames.LIGHT_RELAY_SUFFIX));
		r.accept(lightRelayToggle, prefix("toggle" + LibBlockNames.LIGHT_RELAY_SUFFIX));
		r.accept(lightLauncher, prefix(LibBlockNames.LIGHT_LAUNCHER));
		r.accept(cacophonium, prefix(LibBlockNames.CACOPHONIUM));
		r.accept(cellBlock, prefix(LibBlockNames.CELL_BLOCK));
		r.accept(teruTeruBozu, prefix(LibBlockNames.TERU_TERU_BOZU));
		r.accept(avatar, prefix(LibBlockNames.AVATAR));
		r.accept(fakeAir, prefix(LibBlockNames.FAKE_AIR));
		r.accept(root, prefix(LibBlockNames.ROOT));
		r.accept(felPumpkin, prefix(LibBlockNames.FEL_PUMPKIN));
		r.accept(cocoon, prefix(LibBlockNames.COCOON));
		r.accept(enchantedSoil, prefix(LibBlockNames.ENCHANTED_SOIL));
		r.accept(animatedTorch, prefix(LibBlockNames.ANIMATED_TORCH));
		r.accept(starfield, prefix(LibBlockNames.STARFIELD));
		r.accept(azulejo0, prefix(LibBlockNames.AZULEJO_PREFIX + 0));
		r.accept(azulejo1, prefix(LibBlockNames.AZULEJO_PREFIX + 1));
		r.accept(azulejo2, prefix(LibBlockNames.AZULEJO_PREFIX + 2));
		r.accept(azulejo3, prefix(LibBlockNames.AZULEJO_PREFIX + 3));
		r.accept(azulejo4, prefix(LibBlockNames.AZULEJO_PREFIX + 4));
		r.accept(azulejo5, prefix(LibBlockNames.AZULEJO_PREFIX + 5));
		r.accept(azulejo6, prefix(LibBlockNames.AZULEJO_PREFIX + 6));
		r.accept(azulejo7, prefix(LibBlockNames.AZULEJO_PREFIX + 7));
		r.accept(azulejo8, prefix(LibBlockNames.AZULEJO_PREFIX + 8));
		r.accept(azulejo9, prefix(LibBlockNames.AZULEJO_PREFIX + 9));
		r.accept(azulejo10, prefix(LibBlockNames.AZULEJO_PREFIX + 10));
		r.accept(azulejo11, prefix(LibBlockNames.AZULEJO_PREFIX + 11));
		r.accept(azulejo12, prefix(LibBlockNames.AZULEJO_PREFIX + 12));
		r.accept(azulejo13, prefix(LibBlockNames.AZULEJO_PREFIX + 13));
		r.accept(azulejo14, prefix(LibBlockNames.AZULEJO_PREFIX + 14));
		r.accept(azulejo15, prefix(LibBlockNames.AZULEJO_PREFIX + 15));
		r.accept(manaFlame, prefix(LibBlockNames.MANA_FLAME));
		r.accept(blazeBlock, prefix(LibBlockNames.BLAZE_BLOCK));
		r.accept(gaiaHeadWall, prefix(LibBlockNames.GAIA_WALL_HEAD));
		r.accept(gaiaHead, prefix(LibBlockNames.GAIA_HEAD));
		r.accept(shimmerrock, prefix(LibBlockNames.SHIMMERROCK));
		r.accept(shimmerrockSlab, prefix(LibBlockNames.SHIMMERROCK + SLAB_SUFFIX));
		r.accept(shimmerrockStairs, prefix(LibBlockNames.SHIMMERROCK + STAIR_SUFFIX));
		r.accept(shimmerwoodPlanks, prefix(LibBlockNames.SHIMMERWOOD_PLANKS));
		r.accept(shimmerwoodPlankSlab, prefix(LibBlockNames.SHIMMERWOOD_PLANKS + SLAB_SUFFIX));
		r.accept(shimmerwoodPlankStairs, prefix(LibBlockNames.SHIMMERWOOD_PLANKS + STAIR_SUFFIX));
		r.accept(dryGrass, prefix(BotaniaGrassBlock.Variant.DRY.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX));
		r.accept(goldenGrass, prefix(BotaniaGrassBlock.Variant.GOLDEN.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX));
		r.accept(vividGrass, prefix(BotaniaGrassBlock.Variant.VIVID.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX));
		r.accept(scorchedGrass, prefix(BotaniaGrassBlock.Variant.SCORCHED.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX));
		r.accept(infusedGrass, prefix(BotaniaGrassBlock.Variant.INFUSED.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX));
		r.accept(mutatedGrass, prefix(BotaniaGrassBlock.Variant.MUTATED.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX));
		r.accept(motifDaybloom, prefix(LibBlockNames.MOTIF_DAYBLOOM));
		r.accept(motifNightshade, prefix(LibBlockNames.MOTIF_NIGHTSHADE));
		r.accept(motifHydroangeas, prefix(LibBlockNames.MOTIF_HYDROANGEAS));
		r.accept(pottedMotifDaybloom, prefix(LibBlockNames.POTTED_PREFIX + LibBlockNames.MOTIF_DAYBLOOM));
		r.accept(pottedMotifNightshade, prefix(LibBlockNames.POTTED_PREFIX + LibBlockNames.MOTIF_NIGHTSHADE));
		r.accept(pottedMotifHydroangeas, prefix(LibBlockNames.POTTED_PREFIX + LibBlockNames.MOTIF_HYDROANGEAS));

		r.accept(darkQuartz, prefix(QUARTZ_DARK));
		r.accept(darkQuartzChiseled, prefix("chiseled_" + QUARTZ_DARK));
		r.accept(darkQuartzPillar, prefix(QUARTZ_DARK + "_pillar"));
		r.accept(darkQuartzSlab, prefix(QUARTZ_DARK + SLAB_SUFFIX));
		r.accept(darkQuartzStairs, prefix(QUARTZ_DARK + STAIR_SUFFIX));

		r.accept(manaQuartz, prefix(QUARTZ_MANA));
		r.accept(manaQuartzChiseled, prefix("chiseled_" + QUARTZ_MANA));
		r.accept(manaQuartzPillar, prefix(QUARTZ_MANA + "_pillar"));
		r.accept(manaQuartzSlab, prefix(QUARTZ_MANA + SLAB_SUFFIX));
		r.accept(manaQuartzStairs, prefix(QUARTZ_MANA + STAIR_SUFFIX));

		r.accept(blazeQuartz, prefix(QUARTZ_BLAZE));
		r.accept(blazeQuartzChiseled, prefix("chiseled_" + QUARTZ_BLAZE));
		r.accept(blazeQuartzPillar, prefix(QUARTZ_BLAZE + "_pillar"));
		r.accept(blazeQuartzSlab, prefix(QUARTZ_BLAZE + SLAB_SUFFIX));
		r.accept(blazeQuartzStairs, prefix(QUARTZ_BLAZE + STAIR_SUFFIX));

		r.accept(lavenderQuartz, prefix(QUARTZ_LAVENDER));
		r.accept(lavenderQuartzChiseled, prefix("chiseled_" + QUARTZ_LAVENDER));
		r.accept(lavenderQuartzPillar, prefix(QUARTZ_LAVENDER + "_pillar"));
		r.accept(lavenderQuartzSlab, prefix(QUARTZ_LAVENDER + SLAB_SUFFIX));
		r.accept(lavenderQuartzStairs, prefix(QUARTZ_LAVENDER + STAIR_SUFFIX));

		r.accept(redQuartz, prefix(QUARTZ_RED));
		r.accept(redQuartzChiseled, prefix("chiseled_" + QUARTZ_RED));
		r.accept(redQuartzPillar, prefix(QUARTZ_RED + "_pillar"));
		r.accept(redQuartzSlab, prefix(QUARTZ_RED + SLAB_SUFFIX));
		r.accept(redQuartzStairs, prefix(QUARTZ_RED + STAIR_SUFFIX));

		r.accept(elfQuartz, prefix(QUARTZ_ELF));
		r.accept(elfQuartzChiseled, prefix("chiseled_" + QUARTZ_ELF));
		r.accept(elfQuartzPillar, prefix(QUARTZ_ELF + "_pillar"));
		r.accept(elfQuartzSlab, prefix(QUARTZ_ELF + SLAB_SUFFIX));
		r.accept(elfQuartzStairs, prefix(QUARTZ_ELF + STAIR_SUFFIX));

		r.accept(sunnyQuartz, prefix(QUARTZ_SUNNY));
		r.accept(sunnyQuartzChiseled, prefix("chiseled_" + QUARTZ_SUNNY));
		r.accept(sunnyQuartzPillar, prefix(QUARTZ_SUNNY + "_pillar"));
		r.accept(sunnyQuartzSlab, prefix(QUARTZ_SUNNY + SLAB_SUFFIX));
		r.accept(sunnyQuartzStairs, prefix(QUARTZ_SUNNY + STAIR_SUFFIX));

		r.accept(whitePavement, prefix("white" + PAVEMENT_SUFFIX));
		r.accept(whitePavementStair, prefix("white" + PAVEMENT_SUFFIX + STAIR_SUFFIX));
		r.accept(whitePavementSlab, prefix("white" + PAVEMENT_SUFFIX + SLAB_SUFFIX));

		r.accept(blackPavement, prefix("black" + PAVEMENT_SUFFIX));
		r.accept(blackPavementStair, prefix("black" + PAVEMENT_SUFFIX + STAIR_SUFFIX));
		r.accept(blackPavementSlab, prefix("black" + PAVEMENT_SUFFIX + SLAB_SUFFIX));

		r.accept(bluePavement, prefix("blue" + PAVEMENT_SUFFIX));
		r.accept(bluePavementStair, prefix("blue" + PAVEMENT_SUFFIX + STAIR_SUFFIX));
		r.accept(bluePavementSlab, prefix("blue" + PAVEMENT_SUFFIX + SLAB_SUFFIX));

		r.accept(yellowPavement, prefix("yellow" + PAVEMENT_SUFFIX));
		r.accept(yellowPavementStair, prefix("yellow" + PAVEMENT_SUFFIX + STAIR_SUFFIX));
		r.accept(yellowPavementSlab, prefix("yellow" + PAVEMENT_SUFFIX + SLAB_SUFFIX));

		r.accept(redPavement, prefix("red" + PAVEMENT_SUFFIX));
		r.accept(redPavementStair, prefix("red" + PAVEMENT_SUFFIX + STAIR_SUFFIX));
		r.accept(redPavementSlab, prefix("red" + PAVEMENT_SUFFIX + SLAB_SUFFIX));

		r.accept(greenPavement, prefix("green" + PAVEMENT_SUFFIX));
		r.accept(greenPavementStair, prefix("green" + PAVEMENT_SUFFIX + STAIR_SUFFIX));
		r.accept(greenPavementSlab, prefix("green" + PAVEMENT_SUFFIX + SLAB_SUFFIX));

		r.accept(biomeStoneForest, prefix(METAMORPHIC_PREFIX + "forest_stone"));
		r.accept(biomeStoneForestSlab, prefix(METAMORPHIC_PREFIX + "forest_stone" + SLAB_SUFFIX));
		r.accept(biomeStoneForestStairs, prefix(METAMORPHIC_PREFIX + "forest_stone" + STAIR_SUFFIX));
		r.accept(biomeStoneForestWall, prefix(METAMORPHIC_PREFIX + "forest_stone" + WALL_SUFFIX));
		r.accept(biomeCobblestoneForest, prefix(METAMORPHIC_PREFIX + "forest_cobblestone"));
		r.accept(biomeCobblestoneForestSlab, prefix(METAMORPHIC_PREFIX + "forest_cobblestone" + SLAB_SUFFIX));
		r.accept(biomeCobblestoneForestStairs, prefix(METAMORPHIC_PREFIX + "forest_cobblestone" + STAIR_SUFFIX));
		r.accept(biomeCobblestoneForestWall, prefix(METAMORPHIC_PREFIX + "forest_cobblestone" + WALL_SUFFIX));
		r.accept(biomeBrickForest, prefix(METAMORPHIC_PREFIX + "forest_bricks"));
		r.accept(biomeBrickForestSlab, prefix(METAMORPHIC_PREFIX + "forest_bricks" + SLAB_SUFFIX));
		r.accept(biomeBrickForestStairs, prefix(METAMORPHIC_PREFIX + "forest_bricks" + STAIR_SUFFIX));
		r.accept(biomeBrickForestWall, prefix(METAMORPHIC_PREFIX + "forest_bricks" + WALL_SUFFIX));
		r.accept(biomeChiseledBrickForest, prefix("chiseled_" + METAMORPHIC_PREFIX + "forest_bricks"));

		r.accept(biomeStonePlains, prefix(METAMORPHIC_PREFIX + "plains_stone"));
		r.accept(biomeStonePlainsSlab, prefix(METAMORPHIC_PREFIX + "plains_stone" + SLAB_SUFFIX));
		r.accept(biomeStonePlainsStairs, prefix(METAMORPHIC_PREFIX + "plains_stone" + STAIR_SUFFIX));
		r.accept(biomeStonePlainsWall, prefix(METAMORPHIC_PREFIX + "plains_stone" + WALL_SUFFIX));
		r.accept(biomeCobblestonePlains, prefix(METAMORPHIC_PREFIX + "plains_cobblestone"));
		r.accept(biomeCobblestonePlainsSlab, prefix(METAMORPHIC_PREFIX + "plains_cobblestone" + SLAB_SUFFIX));
		r.accept(biomeCobblestonePlainsStairs, prefix(METAMORPHIC_PREFIX + "plains_cobblestone" + STAIR_SUFFIX));
		r.accept(biomeCobblestonePlainsWall, prefix(METAMORPHIC_PREFIX + "plains_cobblestone" + WALL_SUFFIX));
		r.accept(biomeBrickPlains, prefix(METAMORPHIC_PREFIX + "plains_bricks"));
		r.accept(biomeBrickPlainsSlab, prefix(METAMORPHIC_PREFIX + "plains_bricks" + SLAB_SUFFIX));
		r.accept(biomeBrickPlainsStairs, prefix(METAMORPHIC_PREFIX + "plains_bricks" + STAIR_SUFFIX));
		r.accept(biomeBrickPlainsWall, prefix(METAMORPHIC_PREFIX + "plains_bricks" + WALL_SUFFIX));
		r.accept(biomeChiseledBrickPlains, prefix("chiseled_" + METAMORPHIC_PREFIX + "plains_bricks"));

		r.accept(biomeStoneMountain, prefix(METAMORPHIC_PREFIX + "mountain_stone"));
		r.accept(biomeStoneMountainSlab, prefix(METAMORPHIC_PREFIX + "mountain_stone" + SLAB_SUFFIX));
		r.accept(biomeStoneMountainStairs, prefix(METAMORPHIC_PREFIX + "mountain_stone" + STAIR_SUFFIX));
		r.accept(biomeStoneMountainWall, prefix(METAMORPHIC_PREFIX + "mountain_stone" + WALL_SUFFIX));
		r.accept(biomeCobblestoneMountain, prefix(METAMORPHIC_PREFIX + "mountain_cobblestone"));
		r.accept(biomeCobblestoneMountainSlab, prefix(METAMORPHIC_PREFIX + "mountain_cobblestone" + SLAB_SUFFIX));
		r.accept(biomeCobblestoneMountainStairs, prefix(METAMORPHIC_PREFIX + "mountain_cobblestone" + STAIR_SUFFIX));
		r.accept(biomeCobblestoneMountainWall, prefix(METAMORPHIC_PREFIX + "mountain_cobblestone" + WALL_SUFFIX));
		r.accept(biomeBrickMountain, prefix(METAMORPHIC_PREFIX + "mountain_bricks"));
		r.accept(biomeBrickMountainSlab, prefix(METAMORPHIC_PREFIX + "mountain_bricks" + SLAB_SUFFIX));
		r.accept(biomeBrickMountainStairs, prefix(METAMORPHIC_PREFIX + "mountain_bricks" + STAIR_SUFFIX));
		r.accept(biomeBrickMountainWall, prefix(METAMORPHIC_PREFIX + "mountain_bricks" + WALL_SUFFIX));
		r.accept(biomeChiseledBrickMountain, prefix("chiseled_" + METAMORPHIC_PREFIX + "mountain_bricks"));

		r.accept(biomeStoneFungal, prefix(METAMORPHIC_PREFIX + "fungal_stone"));
		r.accept(biomeStoneFungalSlab, prefix(METAMORPHIC_PREFIX + "fungal_stone" + SLAB_SUFFIX));
		r.accept(biomeStoneFungalStairs, prefix(METAMORPHIC_PREFIX + "fungal_stone" + STAIR_SUFFIX));
		r.accept(biomeStoneFungalWall, prefix(METAMORPHIC_PREFIX + "fungal_stone" + WALL_SUFFIX));
		r.accept(biomeCobblestoneFungal, prefix(METAMORPHIC_PREFIX + "fungal_cobblestone"));
		r.accept(biomeCobblestoneFungalSlab, prefix(METAMORPHIC_PREFIX + "fungal_cobblestone" + SLAB_SUFFIX));
		r.accept(biomeCobblestoneFungalStairs, prefix(METAMORPHIC_PREFIX + "fungal_cobblestone" + STAIR_SUFFIX));
		r.accept(biomeCobblestoneFungalWall, prefix(METAMORPHIC_PREFIX + "fungal_cobblestone" + WALL_SUFFIX));
		r.accept(biomeBrickFungal, prefix(METAMORPHIC_PREFIX + "fungal_bricks"));
		r.accept(biomeBrickFungalSlab, prefix(METAMORPHIC_PREFIX + "fungal_bricks" + SLAB_SUFFIX));
		r.accept(biomeBrickFungalStairs, prefix(METAMORPHIC_PREFIX + "fungal_bricks" + STAIR_SUFFIX));
		r.accept(biomeBrickFungalWall, prefix(METAMORPHIC_PREFIX + "fungal_bricks" + WALL_SUFFIX));
		r.accept(biomeChiseledBrickFungal, prefix("chiseled_" + METAMORPHIC_PREFIX + "fungal_bricks"));

		r.accept(biomeStoneSwamp, prefix(METAMORPHIC_PREFIX + "swamp_stone"));
		r.accept(biomeStoneSwampSlab, prefix(METAMORPHIC_PREFIX + "swamp_stone" + SLAB_SUFFIX));
		r.accept(biomeStoneSwampStairs, prefix(METAMORPHIC_PREFIX + "swamp_stone" + STAIR_SUFFIX));
		r.accept(biomeStoneSwampWall, prefix(METAMORPHIC_PREFIX + "swamp_stone" + WALL_SUFFIX));
		r.accept(biomeCobblestoneSwamp, prefix(METAMORPHIC_PREFIX + "swamp_cobblestone"));
		r.accept(biomeCobblestoneSwampSlab, prefix(METAMORPHIC_PREFIX + "swamp_cobblestone" + SLAB_SUFFIX));
		r.accept(biomeCobblestoneSwampStairs, prefix(METAMORPHIC_PREFIX + "swamp_cobblestone" + STAIR_SUFFIX));
		r.accept(biomeCobblestoneSwampWall, prefix(METAMORPHIC_PREFIX + "swamp_cobblestone" + WALL_SUFFIX));
		r.accept(biomeBrickSwamp, prefix(METAMORPHIC_PREFIX + "swamp_bricks"));
		r.accept(biomeBrickSwampSlab, prefix(METAMORPHIC_PREFIX + "swamp_bricks" + SLAB_SUFFIX));
		r.accept(biomeBrickSwampStairs, prefix(METAMORPHIC_PREFIX + "swamp_bricks" + STAIR_SUFFIX));
		r.accept(biomeBrickSwampWall, prefix(METAMORPHIC_PREFIX + "swamp_bricks" + WALL_SUFFIX));
		r.accept(biomeChiseledBrickSwamp, prefix("chiseled_" + METAMORPHIC_PREFIX + "swamp_bricks"));

		r.accept(biomeStoneDesert, prefix(METAMORPHIC_PREFIX + "desert_stone"));
		r.accept(biomeStoneDesertSlab, prefix(METAMORPHIC_PREFIX + "desert_stone" + SLAB_SUFFIX));
		r.accept(biomeStoneDesertStairs, prefix(METAMORPHIC_PREFIX + "desert_stone" + STAIR_SUFFIX));
		r.accept(biomeStoneDesertWall, prefix(METAMORPHIC_PREFIX + "desert_stone" + WALL_SUFFIX));
		r.accept(biomeCobblestoneDesert, prefix(METAMORPHIC_PREFIX + "desert_cobblestone"));
		r.accept(biomeCobblestoneDesertSlab, prefix(METAMORPHIC_PREFIX + "desert_cobblestone" + SLAB_SUFFIX));
		r.accept(biomeCobblestoneDesertStairs, prefix(METAMORPHIC_PREFIX + "desert_cobblestone" + STAIR_SUFFIX));
		r.accept(biomeCobblestoneDesertWall, prefix(METAMORPHIC_PREFIX + "desert_cobblestone" + WALL_SUFFIX));
		r.accept(biomeBrickDesert, prefix(METAMORPHIC_PREFIX + "desert_bricks"));
		r.accept(biomeBrickDesertSlab, prefix(METAMORPHIC_PREFIX + "desert_bricks" + SLAB_SUFFIX));
		r.accept(biomeBrickDesertStairs, prefix(METAMORPHIC_PREFIX + "desert_bricks" + STAIR_SUFFIX));
		r.accept(biomeBrickDesertWall, prefix(METAMORPHIC_PREFIX + "desert_bricks" + WALL_SUFFIX));
		r.accept(biomeChiseledBrickDesert, prefix("chiseled_" + METAMORPHIC_PREFIX + "desert_bricks"));

		r.accept(biomeStoneTaiga, prefix(METAMORPHIC_PREFIX + "taiga_stone"));
		r.accept(biomeStoneTaigaSlab, prefix(METAMORPHIC_PREFIX + "taiga_stone" + SLAB_SUFFIX));
		r.accept(biomeStoneTaigaStairs, prefix(METAMORPHIC_PREFIX + "taiga_stone" + STAIR_SUFFIX));
		r.accept(biomeStoneTaigaWall, prefix(METAMORPHIC_PREFIX + "taiga_stone" + WALL_SUFFIX));
		r.accept(biomeCobblestoneTaiga, prefix(METAMORPHIC_PREFIX + "taiga_cobblestone"));
		r.accept(biomeCobblestoneTaigaSlab, prefix(METAMORPHIC_PREFIX + "taiga_cobblestone" + SLAB_SUFFIX));
		r.accept(biomeCobblestoneTaigaStairs, prefix(METAMORPHIC_PREFIX + "taiga_cobblestone" + STAIR_SUFFIX));
		r.accept(biomeCobblestoneTaigaWall, prefix(METAMORPHIC_PREFIX + "taiga_cobblestone" + WALL_SUFFIX));
		r.accept(biomeBrickTaiga, prefix(METAMORPHIC_PREFIX + "taiga_bricks"));
		r.accept(biomeBrickTaigaSlab, prefix(METAMORPHIC_PREFIX + "taiga_bricks" + SLAB_SUFFIX));
		r.accept(biomeBrickTaigaStairs, prefix(METAMORPHIC_PREFIX + "taiga_bricks" + STAIR_SUFFIX));
		r.accept(biomeBrickTaigaWall, prefix(METAMORPHIC_PREFIX + "taiga_bricks" + WALL_SUFFIX));
		r.accept(biomeChiseledBrickTaiga, prefix("chiseled_" + METAMORPHIC_PREFIX + "taiga_bricks"));

		r.accept(biomeStoneMesa, prefix(METAMORPHIC_PREFIX + "mesa_stone"));
		r.accept(biomeStoneMesaSlab, prefix(METAMORPHIC_PREFIX + "mesa_stone" + SLAB_SUFFIX));
		r.accept(biomeStoneMesaStairs, prefix(METAMORPHIC_PREFIX + "mesa_stone" + STAIR_SUFFIX));
		r.accept(biomeStoneMesaWall, prefix(METAMORPHIC_PREFIX + "mesa_stone" + WALL_SUFFIX));
		r.accept(biomeCobblestoneMesa, prefix(METAMORPHIC_PREFIX + "mesa_cobblestone"));
		r.accept(biomeCobblestoneMesaSlab, prefix(METAMORPHIC_PREFIX + "mesa_cobblestone" + SLAB_SUFFIX));
		r.accept(biomeCobblestoneMesaStairs, prefix(METAMORPHIC_PREFIX + "mesa_cobblestone" + STAIR_SUFFIX));
		r.accept(biomeCobblestoneMesaWall, prefix(METAMORPHIC_PREFIX + "mesa_cobblestone" + WALL_SUFFIX));
		r.accept(biomeBrickMesa, prefix(METAMORPHIC_PREFIX + "mesa_bricks"));
		r.accept(biomeBrickMesaSlab, prefix(METAMORPHIC_PREFIX + "mesa_bricks" + SLAB_SUFFIX));
		r.accept(biomeBrickMesaStairs, prefix(METAMORPHIC_PREFIX + "mesa_bricks" + STAIR_SUFFIX));
		r.accept(biomeBrickMesaWall, prefix(METAMORPHIC_PREFIX + "mesa_bricks" + WALL_SUFFIX));
		r.accept(biomeChiseledBrickMesa, prefix("chiseled_" + METAMORPHIC_PREFIX + "mesa_bricks"));

		r.accept(managlassPane, prefix(LibBlockNames.MANA_GLASS + "_pane"));
		r.accept(alfglassPane, prefix(LibBlockNames.ELF_GLASS + "_pane"));
		r.accept(bifrostPane, prefix(LibBlockNames.BIFROST + "_pane"));
	}

	public static void registerItemBlocks(BiConsumer<Item, ResourceLocation> r) {
		Item.Properties props = BotaniaItems.defaultBuilder();
		Stream.<Function<DyeColor, Block>>of(
				BotaniaBlocks::getFlower,
				BotaniaBlocks::getShinyFlower,
				BotaniaBlocks::getFloatingFlower,
				BotaniaBlocks::getPetalBlock,
				BotaniaBlocks::getMushroom,
				BotaniaBlocks::getDoubleFlower
		).forEach(
				blockGetter -> ColorHelper.supportedColors().map(blockGetter).forEach(
						block -> r.accept(new BlockItem(block, props), BuiltInRegistries.BLOCK.getKey(block))
				)
		);
		r.accept(new BlockItem(defaultAltar, props), BuiltInRegistries.BLOCK.getKey(defaultAltar));
		r.accept(new BlockItem(forestAltar, props), BuiltInRegistries.BLOCK.getKey(forestAltar));
		r.accept(new BlockItem(plainsAltar, props), BuiltInRegistries.BLOCK.getKey(plainsAltar));
		r.accept(new BlockItem(mountainAltar, props), BuiltInRegistries.BLOCK.getKey(mountainAltar));
		r.accept(new BlockItem(fungalAltar, props), BuiltInRegistries.BLOCK.getKey(fungalAltar));
		r.accept(new BlockItem(swampAltar, props), BuiltInRegistries.BLOCK.getKey(swampAltar));
		r.accept(new BlockItem(desertAltar, props), BuiltInRegistries.BLOCK.getKey(desertAltar));
		r.accept(new BlockItem(taigaAltar, props), BuiltInRegistries.BLOCK.getKey(taigaAltar));
		r.accept(new BlockItem(mesaAltar, props), BuiltInRegistries.BLOCK.getKey(mesaAltar));
		r.accept(new BlockItem(mossyAltar, props), BuiltInRegistries.BLOCK.getKey(mossyAltar));
		r.accept(new BlockItem(livingrockAltar, props), BuiltInRegistries.BLOCK.getKey(livingrockAltar));
		r.accept(new BlockItem(deepslateAltar, props), BuiltInRegistries.BLOCK.getKey(deepslateAltar));

		r.accept(new BlockItem(livingrock, props), BuiltInRegistries.BLOCK.getKey(livingrock));
		r.accept(new BlockItem(livingrockStairs, props), BuiltInRegistries.BLOCK.getKey(livingrockStairs));
		r.accept(new BlockItem(livingrockSlab, props), BuiltInRegistries.BLOCK.getKey(livingrockSlab));
		r.accept(new BlockItem(livingrockWall, props), BuiltInRegistries.BLOCK.getKey(livingrockWall));
		r.accept(new BlockItem(livingrockPolished, props), BuiltInRegistries.BLOCK.getKey(livingrockPolished));
		r.accept(new BlockItem(livingrockPolishedStairs, props), BuiltInRegistries.BLOCK.getKey(livingrockPolishedStairs));
		r.accept(new BlockItem(livingrockPolishedSlab, props), BuiltInRegistries.BLOCK.getKey(livingrockPolishedSlab));
		r.accept(new BlockItem(livingrockPolishedWall, props), BuiltInRegistries.BLOCK.getKey(livingrockPolishedWall));
		r.accept(new BlockItem(livingrockSlate, props), BuiltInRegistries.BLOCK.getKey(livingrockSlate));
		r.accept(new BlockItem(livingrockBrick, props), BuiltInRegistries.BLOCK.getKey(livingrockBrick));
		r.accept(new BlockItem(livingrockBrickStairs, props), BuiltInRegistries.BLOCK.getKey(livingrockBrickStairs));
		r.accept(new BlockItem(livingrockBrickSlab, props), BuiltInRegistries.BLOCK.getKey(livingrockBrickSlab));
		r.accept(new BlockItem(livingrockBrickWall, props), BuiltInRegistries.BLOCK.getKey(livingrockBrickWall));
		r.accept(new BlockItem(livingrockBrickMossy, props), BuiltInRegistries.BLOCK.getKey(livingrockBrickMossy));
		r.accept(new BlockItem(livingrockBrickMossyStairs, props), BuiltInRegistries.BLOCK.getKey(livingrockBrickMossyStairs));
		r.accept(new BlockItem(livingrockBrickMossySlab, props), BuiltInRegistries.BLOCK.getKey(livingrockBrickMossySlab));
		r.accept(new BlockItem(livingrockBrickMossyWall, props), BuiltInRegistries.BLOCK.getKey(livingrockBrickMossyWall));
		r.accept(new BlockItem(livingrockBrickChiseled, props), BuiltInRegistries.BLOCK.getKey(livingrockBrickChiseled));
		r.accept(new BlockItem(livingrockBrickCracked, props), BuiltInRegistries.BLOCK.getKey(livingrockBrickCracked));

		r.accept(new BlockItem(livingwoodLog, props), BuiltInRegistries.BLOCK.getKey(livingwoodLog));
		r.accept(new BlockItem(livingwood, props), BuiltInRegistries.BLOCK.getKey(livingwood));
		r.accept(new BlockItem(livingwoodStairs, props), BuiltInRegistries.BLOCK.getKey(livingwoodStairs));
		r.accept(new BlockItem(livingwoodSlab, props), BuiltInRegistries.BLOCK.getKey(livingwoodSlab));
		r.accept(new BlockItem(livingwoodWall, props), BuiltInRegistries.BLOCK.getKey(livingwoodWall));
		r.accept(new BlockItem(livingwoodLogStripped, props), BuiltInRegistries.BLOCK.getKey(livingwoodLogStripped));
		r.accept(new BlockItem(livingwoodStripped, props), BuiltInRegistries.BLOCK.getKey(livingwoodStripped));
		r.accept(new BlockItem(livingwoodStrippedStairs, props), BuiltInRegistries.BLOCK.getKey(livingwoodStrippedStairs));
		r.accept(new BlockItem(livingwoodStrippedSlab, props), BuiltInRegistries.BLOCK.getKey(livingwoodStrippedSlab));
		r.accept(new BlockItem(livingwoodStrippedWall, props), BuiltInRegistries.BLOCK.getKey(livingwoodStrippedWall));
		r.accept(new BlockItem(livingwoodLogGlimmering, props), BuiltInRegistries.BLOCK.getKey(livingwoodLogGlimmering));
		r.accept(new BlockItem(livingwoodGlimmering, props), BuiltInRegistries.BLOCK.getKey(livingwoodGlimmering));
		r.accept(new BlockItem(livingwoodLogStrippedGlimmering, props), BuiltInRegistries.BLOCK.getKey(livingwoodLogStrippedGlimmering));
		r.accept(new BlockItem(livingwoodStrippedGlimmering, props), BuiltInRegistries.BLOCK.getKey(livingwoodStrippedGlimmering));
		r.accept(new BlockItem(livingwoodPlanks, props), BuiltInRegistries.BLOCK.getKey(livingwoodPlanks));
		r.accept(new BlockItem(livingwoodPlankStairs, props), BuiltInRegistries.BLOCK.getKey(livingwoodPlankStairs));
		r.accept(new BlockItem(livingwoodPlankSlab, props), BuiltInRegistries.BLOCK.getKey(livingwoodPlankSlab));
		r.accept(new BlockItem(livingwoodFence, props), BuiltInRegistries.BLOCK.getKey(livingwoodFence));
		r.accept(new BlockItem(livingwoodFenceGate, props), BuiltInRegistries.BLOCK.getKey(livingwoodFenceGate));
		r.accept(new BlockItem(livingwoodPlanksMossy, props), BuiltInRegistries.BLOCK.getKey(livingwoodPlanksMossy));
		r.accept(new BlockItem(livingwoodFramed, props), BuiltInRegistries.BLOCK.getKey(livingwoodFramed));
		r.accept(new BlockItem(livingwoodPatternFramed, props), BuiltInRegistries.BLOCK.getKey(livingwoodPatternFramed));

		r.accept(new BlockItem(dreamwoodLog, props), BuiltInRegistries.BLOCK.getKey(dreamwoodLog));
		r.accept(new BlockItem(dreamwood, props), BuiltInRegistries.BLOCK.getKey(dreamwood));
		r.accept(new BlockItem(dreamwoodStairs, props), BuiltInRegistries.BLOCK.getKey(dreamwoodStairs));
		r.accept(new BlockItem(dreamwoodSlab, props), BuiltInRegistries.BLOCK.getKey(dreamwoodSlab));
		r.accept(new BlockItem(dreamwoodWall, props), BuiltInRegistries.BLOCK.getKey(dreamwoodWall));
		r.accept(new BlockItem(dreamwoodLogStripped, props), BuiltInRegistries.BLOCK.getKey(dreamwoodLogStripped));
		r.accept(new BlockItem(dreamwoodStripped, props), BuiltInRegistries.BLOCK.getKey(dreamwoodStripped));
		r.accept(new BlockItem(dreamwoodStrippedStairs, props), BuiltInRegistries.BLOCK.getKey(dreamwoodStrippedStairs));
		r.accept(new BlockItem(dreamwoodStrippedSlab, props), BuiltInRegistries.BLOCK.getKey(dreamwoodStrippedSlab));
		r.accept(new BlockItem(dreamwoodStrippedWall, props), BuiltInRegistries.BLOCK.getKey(dreamwoodStrippedWall));
		r.accept(new BlockItem(dreamwoodLogGlimmering, props), BuiltInRegistries.BLOCK.getKey(dreamwoodLogGlimmering));
		r.accept(new BlockItem(dreamwoodGlimmering, props), BuiltInRegistries.BLOCK.getKey(dreamwoodGlimmering));
		r.accept(new BlockItem(dreamwoodLogStrippedGlimmering, props), BuiltInRegistries.BLOCK.getKey(dreamwoodLogStrippedGlimmering));
		r.accept(new BlockItem(dreamwoodStrippedGlimmering, props), BuiltInRegistries.BLOCK.getKey(dreamwoodStrippedGlimmering));
		r.accept(new BlockItem(dreamwoodPlanks, props), BuiltInRegistries.BLOCK.getKey(dreamwoodPlanks));
		r.accept(new BlockItem(dreamwoodPlankStairs, props), BuiltInRegistries.BLOCK.getKey(dreamwoodPlankStairs));
		r.accept(new BlockItem(dreamwoodPlankSlab, props), BuiltInRegistries.BLOCK.getKey(dreamwoodPlankSlab));
		r.accept(new BlockItem(dreamwoodFence, props), BuiltInRegistries.BLOCK.getKey(dreamwoodFence));
		r.accept(new BlockItem(dreamwoodFenceGate, props), BuiltInRegistries.BLOCK.getKey(dreamwoodFenceGate));
		r.accept(new BlockItem(dreamwoodPlanksMossy, props), BuiltInRegistries.BLOCK.getKey(dreamwoodPlanksMossy));
		r.accept(new BlockItem(dreamwoodFramed, props), BuiltInRegistries.BLOCK.getKey(dreamwoodFramed));
		r.accept(new BlockItem(dreamwoodPatternFramed, props), BuiltInRegistries.BLOCK.getKey(dreamwoodPatternFramed));

		r.accept(new BlockItem(manaSpreader, props), BuiltInRegistries.BLOCK.getKey(manaSpreader));
		r.accept(new BlockItem(redstoneSpreader, props), BuiltInRegistries.BLOCK.getKey(redstoneSpreader));
		r.accept(new BlockItem(elvenSpreader, props), BuiltInRegistries.BLOCK.getKey(elvenSpreader));
		r.accept(new BlockItem(gaiaSpreader, props), BuiltInRegistries.BLOCK.getKey(gaiaSpreader));
		r.accept(new BlockItem(manaPool, props), BuiltInRegistries.BLOCK.getKey(manaPool));
		r.accept(new BlockItem(creativePool, BotaniaItems.defaultBuilder().rarity(Rarity.EPIC)), BuiltInRegistries.BLOCK.getKey(creativePool));
		r.accept(new BlockItem(dilutedPool, props), BuiltInRegistries.BLOCK.getKey(dilutedPool));
		r.accept(new BlockItem(fabulousPool, props), BuiltInRegistries.BLOCK.getKey(fabulousPool));
		r.accept(new BlockItem(alchemyCatalyst, props), BuiltInRegistries.BLOCK.getKey(alchemyCatalyst));
		r.accept(new BlockItem(conjurationCatalyst, props), BuiltInRegistries.BLOCK.getKey(conjurationCatalyst));
		r.accept(new BlockItem(manasteelBlock, props), BuiltInRegistries.BLOCK.getKey(manasteelBlock));
		r.accept(new BlockItem(terrasteelBlock, BotaniaItems.defaultBuilder().rarity(Rarity.UNCOMMON)), BuiltInRegistries.BLOCK.getKey(terrasteelBlock));
		r.accept(new BlockItem(elementiumBlock, props), BuiltInRegistries.BLOCK.getKey(elementiumBlock));
		r.accept(new BlockItem(manaDiamondBlock, props), BuiltInRegistries.BLOCK.getKey(manaDiamondBlock));
		r.accept(new BlockItem(dragonstoneBlock, props), BuiltInRegistries.BLOCK.getKey(dragonstoneBlock));
		r.accept(new BlockItem(manaGlass, props), BuiltInRegistries.BLOCK.getKey(manaGlass));
		r.accept(new BlockItem(elfGlass, props), BuiltInRegistries.BLOCK.getKey(elfGlass));
		r.accept(new BlockItem(bifrostPerm, props), BuiltInRegistries.BLOCK.getKey(bifrostPerm));
		r.accept(new BlockItem(runeAltar, props), BuiltInRegistries.BLOCK.getKey(runeAltar));
		r.accept(new BlockItem(enchanter, props), BuiltInRegistries.BLOCK.getKey(enchanter));
		r.accept(new BlockItemWithSpecialRenderer(brewery, props), BuiltInRegistries.BLOCK.getKey(brewery));
		r.accept(new BlockItem(terraPlate, props), BuiltInRegistries.BLOCK.getKey(terraPlate));
		r.accept(new BlockItem(alfPortal, BotaniaItems.defaultBuilder().rarity(Rarity.UNCOMMON)), BuiltInRegistries.BLOCK.getKey(alfPortal));

		r.accept(new BlockItemWithSpecialRenderer(manaPylon, props), BuiltInRegistries.BLOCK.getKey(manaPylon));
		r.accept(new BlockItemWithSpecialRenderer(naturaPylon, props), BuiltInRegistries.BLOCK.getKey(naturaPylon));
		r.accept(new BlockItemWithSpecialRenderer(gaiaPylon, props), BuiltInRegistries.BLOCK.getKey(gaiaPylon));
		r.accept(new BlockItem(distributor, props), BuiltInRegistries.BLOCK.getKey(distributor));
		r.accept(new BlockItem(manaVoid, props), BuiltInRegistries.BLOCK.getKey(manaVoid));
		r.accept(new BlockItem(manaDetector, props), BuiltInRegistries.BLOCK.getKey(manaDetector));
		r.accept(new BlockItem(pistonRelay, props), BuiltInRegistries.BLOCK.getKey(pistonRelay));
		r.accept(new BlockItem(turntable, props), BuiltInRegistries.BLOCK.getKey(turntable));
		r.accept(new BlockItem(tinyPlanet, props), BuiltInRegistries.BLOCK.getKey(tinyPlanet));
		r.accept(new BlockItem(wildDrum, props), BuiltInRegistries.BLOCK.getKey(wildDrum));
		r.accept(new BlockItem(gatheringDrum, props), BuiltInRegistries.BLOCK.getKey(gatheringDrum));
		r.accept(new BlockItem(canopyDrum, props), BuiltInRegistries.BLOCK.getKey(canopyDrum));
		r.accept(new BlockItem(spawnerClaw, props), BuiltInRegistries.BLOCK.getKey(spawnerClaw));
		r.accept(new BlockItem(rfGenerator, props), BuiltInRegistries.BLOCK.getKey(rfGenerator));
		r.accept(new BlockItem(prism, props), BuiltInRegistries.BLOCK.getKey(prism));
		r.accept(new BlockItem(pump, props), BuiltInRegistries.BLOCK.getKey(pump));
		r.accept(new BlockItem(sparkChanger, props), BuiltInRegistries.BLOCK.getKey(sparkChanger));
		r.accept(new BlockItem(manaBomb, props), BuiltInRegistries.BLOCK.getKey(manaBomb));
		r.accept(new BlockItemWithSpecialRenderer(bellows, props), BuiltInRegistries.BLOCK.getKey(bellows));
		r.accept(new BlockItem(openCrate, props), BuiltInRegistries.BLOCK.getKey(openCrate));
		r.accept(new BlockItem(craftCrate, props), BuiltInRegistries.BLOCK.getKey(craftCrate));
		r.accept(new BlockItem(forestEye, props), BuiltInRegistries.BLOCK.getKey(forestEye));
		r.accept(new BlockItem(abstrusePlatform, props), BuiltInRegistries.BLOCK.getKey(abstrusePlatform));
		r.accept(new BlockItem(spectralPlatform, props), BuiltInRegistries.BLOCK.getKey(spectralPlatform));
		r.accept(new BlockItem(infrangiblePlatform, BotaniaItems.defaultBuilder().rarity(Rarity.EPIC)), BuiltInRegistries.BLOCK.getKey(infrangiblePlatform));
		r.accept(new TinyPotatoBlockItem(tinyPotato, BotaniaItems.defaultBuilder().rarity(Rarity.UNCOMMON)), BuiltInRegistries.BLOCK.getKey(tinyPotato));
		r.accept(new BlockItem(enderEye, props), BuiltInRegistries.BLOCK.getKey(enderEye));
		r.accept(new BlockItem(redStringContainer, props), BuiltInRegistries.BLOCK.getKey(redStringContainer));
		r.accept(new BlockItem(redStringDispenser, props), BuiltInRegistries.BLOCK.getKey(redStringDispenser));
		r.accept(new BlockItem(redStringFertilizer, props), BuiltInRegistries.BLOCK.getKey(redStringFertilizer));
		r.accept(new BlockItem(redStringComparator, props), BuiltInRegistries.BLOCK.getKey(redStringComparator));
		r.accept(new BlockItem(redStringRelay, props), BuiltInRegistries.BLOCK.getKey(redStringRelay));
		r.accept(new BlockItem(redStringInterceptor, props), BuiltInRegistries.BLOCK.getKey(redStringInterceptor));
		r.accept(new BlockItemWithSpecialRenderer(corporeaIndex, props), BuiltInRegistries.BLOCK.getKey(corporeaIndex));
		r.accept(new BlockItem(corporeaFunnel, props), BuiltInRegistries.BLOCK.getKey(corporeaFunnel));
		r.accept(new BlockItem(corporeaInterceptor, props), BuiltInRegistries.BLOCK.getKey(corporeaInterceptor));
		r.accept(new BlockItem(corporeaCrystalCube, props), BuiltInRegistries.BLOCK.getKey(corporeaCrystalCube));
		r.accept(new BlockItem(corporeaRetainer, props), BuiltInRegistries.BLOCK.getKey(corporeaRetainer));
		r.accept(new BlockItem(corporeaBlock, props), BuiltInRegistries.BLOCK.getKey(corporeaBlock));
		r.accept(new BlockItem(corporeaSlab, props), BuiltInRegistries.BLOCK.getKey(corporeaSlab));
		r.accept(new BlockItem(corporeaStairs, props), BuiltInRegistries.BLOCK.getKey(corporeaStairs));
		r.accept(new BlockItem(corporeaBrick, props), BuiltInRegistries.BLOCK.getKey(corporeaBrick));
		r.accept(new BlockItem(corporeaBrickSlab, props), BuiltInRegistries.BLOCK.getKey(corporeaBrickSlab));
		r.accept(new BlockItem(corporeaBrickStairs, props), BuiltInRegistries.BLOCK.getKey(corporeaBrickStairs));
		r.accept(new BlockItem(corporeaBrickWall, props), BuiltInRegistries.BLOCK.getKey(corporeaBrickWall));
		r.accept(new BlockItem(incensePlate, props), BuiltInRegistries.BLOCK.getKey(incensePlate));
		r.accept(new BlockItemWithSpecialRenderer(hourglass, props), BuiltInRegistries.BLOCK.getKey(hourglass));
		r.accept(new BlockItem(ghostRail, props), BuiltInRegistries.BLOCK.getKey(ghostRail));
		r.accept(new BlockItem(lightRelayDefault, props), BuiltInRegistries.BLOCK.getKey(lightRelayDefault));
		r.accept(new BlockItem(lightRelayDetector, props), BuiltInRegistries.BLOCK.getKey(lightRelayDetector));
		r.accept(new BlockItem(lightRelayFork, props), BuiltInRegistries.BLOCK.getKey(lightRelayFork));
		r.accept(new BlockItem(lightRelayToggle, props), BuiltInRegistries.BLOCK.getKey(lightRelayToggle));
		r.accept(new BlockItem(lightLauncher, props), BuiltInRegistries.BLOCK.getKey(lightLauncher));
		r.accept(new BlockItem(cacophonium, props), BuiltInRegistries.BLOCK.getKey(cacophonium));
		r.accept(new BlockItem(cellBlock, props), BuiltInRegistries.BLOCK.getKey(cellBlock));
		r.accept(new BlockItemWithSpecialRenderer(teruTeruBozu, props), BuiltInRegistries.BLOCK.getKey(teruTeruBozu));
		r.accept(new BlockItemWithSpecialRenderer(avatar, props), BuiltInRegistries.BLOCK.getKey(avatar));
		r.accept(new BlockItem(root, props), BuiltInRegistries.BLOCK.getKey(root));
		r.accept(new BlockItem(felPumpkin, props), BuiltInRegistries.BLOCK.getKey(felPumpkin));
		r.accept(new BlockItem(cocoon, props), BuiltInRegistries.BLOCK.getKey(cocoon));
		r.accept(new BlockItem(enchantedSoil, BotaniaItems.defaultBuilder().rarity(Rarity.RARE)), BuiltInRegistries.BLOCK.getKey(enchantedSoil));
		r.accept(new BlockItem(animatedTorch, props), BuiltInRegistries.BLOCK.getKey(animatedTorch));
		r.accept(new BlockItem(starfield, props), BuiltInRegistries.BLOCK.getKey(starfield));
		r.accept(new BlockItem(azulejo0, props), BuiltInRegistries.BLOCK.getKey(azulejo0));
		r.accept(new BlockItem(azulejo1, props), BuiltInRegistries.BLOCK.getKey(azulejo1));
		r.accept(new BlockItem(azulejo2, props), BuiltInRegistries.BLOCK.getKey(azulejo2));
		r.accept(new BlockItem(azulejo3, props), BuiltInRegistries.BLOCK.getKey(azulejo3));
		r.accept(new BlockItem(azulejo4, props), BuiltInRegistries.BLOCK.getKey(azulejo4));
		r.accept(new BlockItem(azulejo5, props), BuiltInRegistries.BLOCK.getKey(azulejo5));
		r.accept(new BlockItem(azulejo6, props), BuiltInRegistries.BLOCK.getKey(azulejo6));
		r.accept(new BlockItem(azulejo7, props), BuiltInRegistries.BLOCK.getKey(azulejo7));
		r.accept(new BlockItem(azulejo8, props), BuiltInRegistries.BLOCK.getKey(azulejo8));
		r.accept(new BlockItem(azulejo9, props), BuiltInRegistries.BLOCK.getKey(azulejo9));
		r.accept(new BlockItem(azulejo10, props), BuiltInRegistries.BLOCK.getKey(azulejo10));
		r.accept(new BlockItem(azulejo11, props), BuiltInRegistries.BLOCK.getKey(azulejo11));
		r.accept(new BlockItem(azulejo12, props), BuiltInRegistries.BLOCK.getKey(azulejo12));
		r.accept(new BlockItem(azulejo13, props), BuiltInRegistries.BLOCK.getKey(azulejo13));
		r.accept(new BlockItem(azulejo14, props), BuiltInRegistries.BLOCK.getKey(azulejo14));
		r.accept(new BlockItem(azulejo15, props), BuiltInRegistries.BLOCK.getKey(azulejo15));
		r.accept(new BlazeItemBlock(blazeBlock, props), BuiltInRegistries.BLOCK.getKey(blazeBlock));
		r.accept(new StandingAndWallBlockItem(gaiaHead, gaiaHeadWall, BotaniaItems.defaultBuilder().rarity(Rarity.UNCOMMON), Direction.DOWN), BuiltInRegistries.BLOCK.getKey(gaiaHead));
		r.accept(new BlockItem(shimmerrock, props), BuiltInRegistries.BLOCK.getKey(shimmerrock));
		r.accept(new BlockItem(shimmerrockSlab, props), BuiltInRegistries.BLOCK.getKey(shimmerrockSlab));
		r.accept(new BlockItem(shimmerrockStairs, props), BuiltInRegistries.BLOCK.getKey(shimmerrockStairs));
		r.accept(new BlockItem(shimmerwoodPlanks, props), BuiltInRegistries.BLOCK.getKey(shimmerwoodPlanks));
		r.accept(new BlockItem(shimmerwoodPlankSlab, props), BuiltInRegistries.BLOCK.getKey(shimmerwoodPlankSlab));
		r.accept(new BlockItem(shimmerwoodPlankStairs, props), BuiltInRegistries.BLOCK.getKey(shimmerwoodPlankStairs));
		r.accept(new BlockItem(dryGrass, props), BuiltInRegistries.BLOCK.getKey(dryGrass));
		r.accept(new BlockItem(goldenGrass, props), BuiltInRegistries.BLOCK.getKey(goldenGrass));
		r.accept(new BlockItem(vividGrass, props), BuiltInRegistries.BLOCK.getKey(vividGrass));
		r.accept(new BlockItem(scorchedGrass, props), BuiltInRegistries.BLOCK.getKey(scorchedGrass));
		r.accept(new BlockItem(infusedGrass, props), BuiltInRegistries.BLOCK.getKey(infusedGrass));
		r.accept(new BlockItem(mutatedGrass, props), BuiltInRegistries.BLOCK.getKey(mutatedGrass));
		r.accept(new BlockItem(motifDaybloom, props), BuiltInRegistries.BLOCK.getKey(motifDaybloom));
		r.accept(new BlockItem(motifNightshade, props), BuiltInRegistries.BLOCK.getKey(motifNightshade));
		r.accept(new BlockItem(motifHydroangeas, props), BuiltInRegistries.BLOCK.getKey(motifHydroangeas));

		r.accept(new BlockItem(darkQuartz, props), BuiltInRegistries.BLOCK.getKey(darkQuartz));
		r.accept(new BlockItem(darkQuartzPillar, props), BuiltInRegistries.BLOCK.getKey(darkQuartzPillar));
		r.accept(new BlockItem(darkQuartzChiseled, props), BuiltInRegistries.BLOCK.getKey(darkQuartzChiseled));
		r.accept(new BlockItem(darkQuartzSlab, props), BuiltInRegistries.BLOCK.getKey(darkQuartzSlab));
		r.accept(new BlockItem(darkQuartzStairs, props), BuiltInRegistries.BLOCK.getKey(darkQuartzStairs));

		r.accept(new BlockItem(manaQuartz, props), BuiltInRegistries.BLOCK.getKey(manaQuartz));
		r.accept(new BlockItem(manaQuartzPillar, props), BuiltInRegistries.BLOCK.getKey(manaQuartzPillar));
		r.accept(new BlockItem(manaQuartzChiseled, props), BuiltInRegistries.BLOCK.getKey(manaQuartzChiseled));
		r.accept(new BlockItem(manaQuartzSlab, props), BuiltInRegistries.BLOCK.getKey(manaQuartzSlab));
		r.accept(new BlockItem(manaQuartzStairs, props), BuiltInRegistries.BLOCK.getKey(manaQuartzStairs));

		r.accept(new BlockItem(blazeQuartz, props), BuiltInRegistries.BLOCK.getKey(blazeQuartz));
		r.accept(new BlockItem(blazeQuartzPillar, props), BuiltInRegistries.BLOCK.getKey(blazeQuartzPillar));
		r.accept(new BlockItem(blazeQuartzChiseled, props), BuiltInRegistries.BLOCK.getKey(blazeQuartzChiseled));
		r.accept(new BlockItem(blazeQuartzSlab, props), BuiltInRegistries.BLOCK.getKey(blazeQuartzSlab));
		r.accept(new BlockItem(blazeQuartzStairs, props), BuiltInRegistries.BLOCK.getKey(blazeQuartzStairs));

		r.accept(new BlockItem(lavenderQuartz, props), BuiltInRegistries.BLOCK.getKey(lavenderQuartz));
		r.accept(new BlockItem(lavenderQuartzPillar, props), BuiltInRegistries.BLOCK.getKey(lavenderQuartzPillar));
		r.accept(new BlockItem(lavenderQuartzChiseled, props), BuiltInRegistries.BLOCK.getKey(lavenderQuartzChiseled));
		r.accept(new BlockItem(lavenderQuartzSlab, props), BuiltInRegistries.BLOCK.getKey(lavenderQuartzSlab));
		r.accept(new BlockItem(lavenderQuartzStairs, props), BuiltInRegistries.BLOCK.getKey(lavenderQuartzStairs));

		r.accept(new BlockItem(redQuartz, props), BuiltInRegistries.BLOCK.getKey(redQuartz));
		r.accept(new BlockItem(redQuartzPillar, props), BuiltInRegistries.BLOCK.getKey(redQuartzPillar));
		r.accept(new BlockItem(redQuartzChiseled, props), BuiltInRegistries.BLOCK.getKey(redQuartzChiseled));
		r.accept(new BlockItem(redQuartzSlab, props), BuiltInRegistries.BLOCK.getKey(redQuartzSlab));
		r.accept(new BlockItem(redQuartzStairs, props), BuiltInRegistries.BLOCK.getKey(redQuartzStairs));

		r.accept(new BlockItem(elfQuartz, props), BuiltInRegistries.BLOCK.getKey(elfQuartz));
		r.accept(new BlockItem(elfQuartzPillar, props), BuiltInRegistries.BLOCK.getKey(elfQuartzPillar));
		r.accept(new BlockItem(elfQuartzChiseled, props), BuiltInRegistries.BLOCK.getKey(elfQuartzChiseled));
		r.accept(new BlockItem(elfQuartzSlab, props), BuiltInRegistries.BLOCK.getKey(elfQuartzSlab));
		r.accept(new BlockItem(elfQuartzStairs, props), BuiltInRegistries.BLOCK.getKey(elfQuartzStairs));

		r.accept(new BlockItem(sunnyQuartz, props), BuiltInRegistries.BLOCK.getKey(sunnyQuartz));
		r.accept(new BlockItem(sunnyQuartzPillar, props), BuiltInRegistries.BLOCK.getKey(sunnyQuartzPillar));
		r.accept(new BlockItem(sunnyQuartzChiseled, props), BuiltInRegistries.BLOCK.getKey(sunnyQuartzChiseled));
		r.accept(new BlockItem(sunnyQuartzSlab, props), BuiltInRegistries.BLOCK.getKey(sunnyQuartzSlab));
		r.accept(new BlockItem(sunnyQuartzStairs, props), BuiltInRegistries.BLOCK.getKey(sunnyQuartzStairs));

		r.accept(new BlockItem(biomeStoneForest, props), BuiltInRegistries.BLOCK.getKey(biomeStoneForest));
		r.accept(new BlockItem(biomeStoneForestSlab, props), BuiltInRegistries.BLOCK.getKey(biomeStoneForestSlab));
		r.accept(new BlockItem(biomeStoneForestStairs, props), BuiltInRegistries.BLOCK.getKey(biomeStoneForestStairs));
		r.accept(new BlockItem(biomeStoneForestWall, props), BuiltInRegistries.BLOCK.getKey(biomeStoneForestWall));
		r.accept(new BlockItem(biomeBrickForest, props), BuiltInRegistries.BLOCK.getKey(biomeBrickForest));
		r.accept(new BlockItem(biomeBrickForestSlab, props), BuiltInRegistries.BLOCK.getKey(biomeBrickForestSlab));
		r.accept(new BlockItem(biomeBrickForestStairs, props), BuiltInRegistries.BLOCK.getKey(biomeBrickForestStairs));
		r.accept(new BlockItem(biomeBrickForestWall, props), BuiltInRegistries.BLOCK.getKey(biomeBrickForestWall));
		r.accept(new BlockItem(biomeCobblestoneForest, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneForest));
		r.accept(new BlockItem(biomeCobblestoneForestSlab, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneForestSlab));
		r.accept(new BlockItem(biomeCobblestoneForestStairs, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneForestStairs));
		r.accept(new BlockItem(biomeCobblestoneForestWall, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneForestWall));
		r.accept(new BlockItem(biomeChiseledBrickForest, props), BuiltInRegistries.BLOCK.getKey(biomeChiseledBrickForest));

		r.accept(new BlockItem(biomeStonePlains, props), BuiltInRegistries.BLOCK.getKey(biomeStonePlains));
		r.accept(new BlockItem(biomeStonePlainsSlab, props), BuiltInRegistries.BLOCK.getKey(biomeStonePlainsSlab));
		r.accept(new BlockItem(biomeStonePlainsStairs, props), BuiltInRegistries.BLOCK.getKey(biomeStonePlainsStairs));
		r.accept(new BlockItem(biomeStonePlainsWall, props), BuiltInRegistries.BLOCK.getKey(biomeStonePlainsWall));
		r.accept(new BlockItem(biomeBrickPlains, props), BuiltInRegistries.BLOCK.getKey(biomeBrickPlains));
		r.accept(new BlockItem(biomeBrickPlainsSlab, props), BuiltInRegistries.BLOCK.getKey(biomeBrickPlainsSlab));
		r.accept(new BlockItem(biomeBrickPlainsStairs, props), BuiltInRegistries.BLOCK.getKey(biomeBrickPlainsStairs));
		r.accept(new BlockItem(biomeBrickPlainsWall, props), BuiltInRegistries.BLOCK.getKey(biomeBrickPlainsWall));
		r.accept(new BlockItem(biomeCobblestonePlains, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestonePlains));
		r.accept(new BlockItem(biomeCobblestonePlainsSlab, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestonePlainsSlab));
		r.accept(new BlockItem(biomeCobblestonePlainsStairs, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestonePlainsStairs));
		r.accept(new BlockItem(biomeCobblestonePlainsWall, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestonePlainsWall));
		r.accept(new BlockItem(biomeChiseledBrickPlains, props), BuiltInRegistries.BLOCK.getKey(biomeChiseledBrickPlains));

		r.accept(new BlockItem(biomeStoneMountain, props), BuiltInRegistries.BLOCK.getKey(biomeStoneMountain));
		r.accept(new BlockItem(biomeStoneMountainSlab, props), BuiltInRegistries.BLOCK.getKey(biomeStoneMountainSlab));
		r.accept(new BlockItem(biomeStoneMountainStairs, props), BuiltInRegistries.BLOCK.getKey(biomeStoneMountainStairs));
		r.accept(new BlockItem(biomeStoneMountainWall, props), BuiltInRegistries.BLOCK.getKey(biomeStoneMountainWall));
		r.accept(new BlockItem(biomeBrickMountain, props), BuiltInRegistries.BLOCK.getKey(biomeBrickMountain));
		r.accept(new BlockItem(biomeBrickMountainSlab, props), BuiltInRegistries.BLOCK.getKey(biomeBrickMountainSlab));
		r.accept(new BlockItem(biomeBrickMountainStairs, props), BuiltInRegistries.BLOCK.getKey(biomeBrickMountainStairs));
		r.accept(new BlockItem(biomeBrickMountainWall, props), BuiltInRegistries.BLOCK.getKey(biomeBrickMountainWall));
		r.accept(new BlockItem(biomeCobblestoneMountain, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneMountain));
		r.accept(new BlockItem(biomeCobblestoneMountainSlab, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneMountainSlab));
		r.accept(new BlockItem(biomeCobblestoneMountainStairs, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneMountainStairs));
		r.accept(new BlockItem(biomeCobblestoneMountainWall, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneMountainWall));
		r.accept(new BlockItem(biomeChiseledBrickMountain, props), BuiltInRegistries.BLOCK.getKey(biomeChiseledBrickMountain));

		r.accept(new BlockItem(biomeStoneFungal, props), BuiltInRegistries.BLOCK.getKey(biomeStoneFungal));
		r.accept(new BlockItem(biomeStoneFungalSlab, props), BuiltInRegistries.BLOCK.getKey(biomeStoneFungalSlab));
		r.accept(new BlockItem(biomeStoneFungalStairs, props), BuiltInRegistries.BLOCK.getKey(biomeStoneFungalStairs));
		r.accept(new BlockItem(biomeStoneFungalWall, props), BuiltInRegistries.BLOCK.getKey(biomeStoneFungalWall));
		r.accept(new BlockItem(biomeBrickFungal, props), BuiltInRegistries.BLOCK.getKey(biomeBrickFungal));
		r.accept(new BlockItem(biomeBrickFungalSlab, props), BuiltInRegistries.BLOCK.getKey(biomeBrickFungalSlab));
		r.accept(new BlockItem(biomeBrickFungalStairs, props), BuiltInRegistries.BLOCK.getKey(biomeBrickFungalStairs));
		r.accept(new BlockItem(biomeBrickFungalWall, props), BuiltInRegistries.BLOCK.getKey(biomeBrickFungalWall));
		r.accept(new BlockItem(biomeCobblestoneFungal, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneFungal));
		r.accept(new BlockItem(biomeCobblestoneFungalSlab, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneFungalSlab));
		r.accept(new BlockItem(biomeCobblestoneFungalStairs, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneFungalStairs));
		r.accept(new BlockItem(biomeCobblestoneFungalWall, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneFungalWall));
		r.accept(new BlockItem(biomeChiseledBrickFungal, props), BuiltInRegistries.BLOCK.getKey(biomeChiseledBrickFungal));

		r.accept(new BlockItem(biomeStoneSwamp, props), BuiltInRegistries.BLOCK.getKey(biomeStoneSwamp));
		r.accept(new BlockItem(biomeStoneSwampSlab, props), BuiltInRegistries.BLOCK.getKey(biomeStoneSwampSlab));
		r.accept(new BlockItem(biomeStoneSwampStairs, props), BuiltInRegistries.BLOCK.getKey(biomeStoneSwampStairs));
		r.accept(new BlockItem(biomeStoneSwampWall, props), BuiltInRegistries.BLOCK.getKey(biomeStoneSwampWall));
		r.accept(new BlockItem(biomeBrickSwamp, props), BuiltInRegistries.BLOCK.getKey(biomeBrickSwamp));
		r.accept(new BlockItem(biomeBrickSwampSlab, props), BuiltInRegistries.BLOCK.getKey(biomeBrickSwampSlab));
		r.accept(new BlockItem(biomeBrickSwampStairs, props), BuiltInRegistries.BLOCK.getKey(biomeBrickSwampStairs));
		r.accept(new BlockItem(biomeBrickSwampWall, props), BuiltInRegistries.BLOCK.getKey(biomeBrickSwampWall));
		r.accept(new BlockItem(biomeCobblestoneSwamp, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneSwamp));
		r.accept(new BlockItem(biomeCobblestoneSwampSlab, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneSwampSlab));
		r.accept(new BlockItem(biomeCobblestoneSwampStairs, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneSwampStairs));
		r.accept(new BlockItem(biomeCobblestoneSwampWall, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneSwampWall));
		r.accept(new BlockItem(biomeChiseledBrickSwamp, props), BuiltInRegistries.BLOCK.getKey(biomeChiseledBrickSwamp));

		r.accept(new BlockItem(biomeStoneDesert, props), BuiltInRegistries.BLOCK.getKey(biomeStoneDesert));
		r.accept(new BlockItem(biomeStoneDesertSlab, props), BuiltInRegistries.BLOCK.getKey(biomeStoneDesertSlab));
		r.accept(new BlockItem(biomeStoneDesertStairs, props), BuiltInRegistries.BLOCK.getKey(biomeStoneDesertStairs));
		r.accept(new BlockItem(biomeStoneDesertWall, props), BuiltInRegistries.BLOCK.getKey(biomeStoneDesertWall));
		r.accept(new BlockItem(biomeBrickDesert, props), BuiltInRegistries.BLOCK.getKey(biomeBrickDesert));
		r.accept(new BlockItem(biomeBrickDesertSlab, props), BuiltInRegistries.BLOCK.getKey(biomeBrickDesertSlab));
		r.accept(new BlockItem(biomeBrickDesertStairs, props), BuiltInRegistries.BLOCK.getKey(biomeBrickDesertStairs));
		r.accept(new BlockItem(biomeBrickDesertWall, props), BuiltInRegistries.BLOCK.getKey(biomeBrickDesertWall));
		r.accept(new BlockItem(biomeCobblestoneDesert, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneDesert));
		r.accept(new BlockItem(biomeCobblestoneDesertSlab, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneDesertSlab));
		r.accept(new BlockItem(biomeCobblestoneDesertStairs, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneDesertStairs));
		r.accept(new BlockItem(biomeCobblestoneDesertWall, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneDesertWall));
		r.accept(new BlockItem(biomeChiseledBrickDesert, props), BuiltInRegistries.BLOCK.getKey(biomeChiseledBrickDesert));

		r.accept(new BlockItem(biomeStoneTaiga, props), BuiltInRegistries.BLOCK.getKey(biomeStoneTaiga));
		r.accept(new BlockItem(biomeStoneTaigaSlab, props), BuiltInRegistries.BLOCK.getKey(biomeStoneTaigaSlab));
		r.accept(new BlockItem(biomeStoneTaigaStairs, props), BuiltInRegistries.BLOCK.getKey(biomeStoneTaigaStairs));
		r.accept(new BlockItem(biomeStoneTaigaWall, props), BuiltInRegistries.BLOCK.getKey(biomeStoneTaigaWall));
		r.accept(new BlockItem(biomeBrickTaiga, props), BuiltInRegistries.BLOCK.getKey(biomeBrickTaiga));
		r.accept(new BlockItem(biomeBrickTaigaSlab, props), BuiltInRegistries.BLOCK.getKey(biomeBrickTaigaSlab));
		r.accept(new BlockItem(biomeBrickTaigaStairs, props), BuiltInRegistries.BLOCK.getKey(biomeBrickTaigaStairs));
		r.accept(new BlockItem(biomeBrickTaigaWall, props), BuiltInRegistries.BLOCK.getKey(biomeBrickTaigaWall));
		r.accept(new BlockItem(biomeCobblestoneTaiga, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneTaiga));
		r.accept(new BlockItem(biomeCobblestoneTaigaSlab, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneTaigaSlab));
		r.accept(new BlockItem(biomeCobblestoneTaigaStairs, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneTaigaStairs));
		r.accept(new BlockItem(biomeCobblestoneTaigaWall, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneTaigaWall));
		r.accept(new BlockItem(biomeChiseledBrickTaiga, props), BuiltInRegistries.BLOCK.getKey(biomeChiseledBrickTaiga));

		r.accept(new BlockItem(biomeStoneMesa, props), BuiltInRegistries.BLOCK.getKey(biomeStoneMesa));
		r.accept(new BlockItem(biomeStoneMesaSlab, props), BuiltInRegistries.BLOCK.getKey(biomeStoneMesaSlab));
		r.accept(new BlockItem(biomeStoneMesaStairs, props), BuiltInRegistries.BLOCK.getKey(biomeStoneMesaStairs));
		r.accept(new BlockItem(biomeStoneMesaWall, props), BuiltInRegistries.BLOCK.getKey(biomeStoneMesaWall));
		r.accept(new BlockItem(biomeBrickMesa, props), BuiltInRegistries.BLOCK.getKey(biomeBrickMesa));
		r.accept(new BlockItem(biomeBrickMesaSlab, props), BuiltInRegistries.BLOCK.getKey(biomeBrickMesaSlab));
		r.accept(new BlockItem(biomeBrickMesaStairs, props), BuiltInRegistries.BLOCK.getKey(biomeBrickMesaStairs));
		r.accept(new BlockItem(biomeBrickMesaWall, props), BuiltInRegistries.BLOCK.getKey(biomeBrickMesaWall));
		r.accept(new BlockItem(biomeCobblestoneMesa, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneMesa));
		r.accept(new BlockItem(biomeCobblestoneMesaSlab, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneMesaSlab));
		r.accept(new BlockItem(biomeCobblestoneMesaStairs, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneMesaStairs));
		r.accept(new BlockItem(biomeCobblestoneMesaWall, props), BuiltInRegistries.BLOCK.getKey(biomeCobblestoneMesaWall));
		r.accept(new BlockItem(biomeChiseledBrickMesa, props), BuiltInRegistries.BLOCK.getKey(biomeChiseledBrickMesa));

		r.accept(new BlockItem(whitePavement, props), BuiltInRegistries.BLOCK.getKey(whitePavement));
		r.accept(new BlockItem(whitePavementStair, props), BuiltInRegistries.BLOCK.getKey(whitePavementStair));
		r.accept(new BlockItem(whitePavementSlab, props), BuiltInRegistries.BLOCK.getKey(whitePavementSlab));

		r.accept(new BlockItem(blackPavement, props), BuiltInRegistries.BLOCK.getKey(blackPavement));
		r.accept(new BlockItem(blackPavementSlab, props), BuiltInRegistries.BLOCK.getKey(blackPavementSlab));
		r.accept(new BlockItem(blackPavementStair, props), BuiltInRegistries.BLOCK.getKey(blackPavementStair));

		r.accept(new BlockItem(bluePavement, props), BuiltInRegistries.BLOCK.getKey(bluePavement));
		r.accept(new BlockItem(bluePavementStair, props), BuiltInRegistries.BLOCK.getKey(bluePavementStair));
		r.accept(new BlockItem(bluePavementSlab, props), BuiltInRegistries.BLOCK.getKey(bluePavementSlab));

		r.accept(new BlockItem(yellowPavement, props), BuiltInRegistries.BLOCK.getKey(yellowPavement));
		r.accept(new BlockItem(yellowPavementStair, props), BuiltInRegistries.BLOCK.getKey(yellowPavementStair));
		r.accept(new BlockItem(yellowPavementSlab, props), BuiltInRegistries.BLOCK.getKey(yellowPavementSlab));

		r.accept(new BlockItem(redPavement, props), BuiltInRegistries.BLOCK.getKey(redPavement));
		r.accept(new BlockItem(redPavementStair, props), BuiltInRegistries.BLOCK.getKey(redPavementStair));
		r.accept(new BlockItem(redPavementSlab, props), BuiltInRegistries.BLOCK.getKey(redPavementSlab));

		r.accept(new BlockItem(greenPavement, props), BuiltInRegistries.BLOCK.getKey(greenPavement));
		r.accept(new BlockItem(greenPavementStair, props), BuiltInRegistries.BLOCK.getKey(greenPavementStair));
		r.accept(new BlockItem(greenPavementSlab, props), BuiltInRegistries.BLOCK.getKey(greenPavementSlab));

		r.accept(new BlockItem(managlassPane, props), BuiltInRegistries.BLOCK.getKey(managlassPane));
		r.accept(new BlockItem(alfglassPane, props), BuiltInRegistries.BLOCK.getKey(alfglassPane));
		r.accept(new BlockItem(bifrostPane, props), BuiltInRegistries.BLOCK.getKey(bifrostPane));
	}

	public static void addDispenserBehaviours() {
		DispenserBlock.registerBehavior(BotaniaItems.twigWand, new WandBehavior());
		DispenserBlock.registerBehavior(BotaniaItems.dreamwoodWand, new WandBehavior());
		DispenserBlock.registerBehavior(BotaniaItems.obedienceStick, new StickBehavior());
		DispenserBlock.registerBehavior(BotaniaItems.poolMinecart, new ManaPoolMinecartBehavior());
		DispenserBlock.registerBehavior(BotaniaBlocks.felPumpkin, new FelPumpkinBehavior());
		DispenserBlock.registerBehavior(BotaniaItems.spark, new ManaSparkBehavior());
		DispenserBlock.registerBehavior(BotaniaBlocks.gaiaHead, new OptionalDispenseItemBehavior() {
			@NotNull
			@Override
			protected ItemStack execute(@NotNull BlockSource source, @NotNull ItemStack stack) {
				setSuccess(ArmorItem.dispenseArmor(source, stack));
				return stack;
			}
		});

		DispenseItemBehavior behavior = new CorporeaSparkBehavior();
		DispenserBlock.registerBehavior(BotaniaItems.corporeaSpark, behavior);
		DispenserBlock.registerBehavior(BotaniaItems.corporeaSparkMaster, behavior);
		DispenserBlock.registerBehavior(BotaniaItems.corporeaSparkCreative, behavior);
		DispenserBlock.registerBehavior(BotaniaItems.enderAirBottle, new AbstractProjectileDispenseBehavior() {
			@NotNull
			@Override
			protected Projectile getProjectile(@NotNull Level world, @NotNull Position pos, @NotNull ItemStack stack) {
				return new EnderAirBottleEntity(pos.x(), pos.y(), pos.z(), world);
			}
		});

		behavior = DispenserBlockAccessor.getDispenserRegistry().get(Items.GLASS_BOTTLE);
		DispenserBlock.registerBehavior(Items.GLASS_BOTTLE, new EnderAirBottlingBehavior(behavior));

		behavior = new GrassSeedsBehavior();
		Item[] seedItems = {
				BotaniaItems.grassSeeds,
				BotaniaItems.podzolSeeds,
				BotaniaItems.mycelSeeds,
				BotaniaItems.drySeeds,
				BotaniaItems.goldenSeeds,
				BotaniaItems.vividSeeds,
				BotaniaItems.scorchedSeeds,
				BotaniaItems.infusedSeeds,
				BotaniaItems.mutatedSeeds,
		};
		for (Item seed : seedItems) {
			DispenserBlock.registerBehavior(seed, behavior);
		}

		DispenserBlock.registerBehavior(BotaniaItems.manasteelShears, new ShearsDispenseItemBehavior());
		DispenserBlock.registerBehavior(BotaniaItems.elementiumShears, new ShearsDispenseItemBehavior());
		DispenserBlock.registerBehavior(BotaniaItems.vineBall, new AbstractProjectileDispenseBehavior() {
			@NotNull
			@Override
			protected Projectile getProjectile(@NotNull Level world, @NotNull Position pos, @NotNull ItemStack stack) {
				return new VineBallEntity(pos.x(), pos.y(), pos.z(), world);
			}
		});

		SeedBehaviors.init();
	}

	public static void addAxeStripping() {
		XplatAbstractions xplat = XplatAbstractions.INSTANCE;
		xplat.addAxeStripping(livingwoodLog, livingwoodLogStripped);
		xplat.addAxeStripping(livingwoodLogGlimmering, livingwoodLogStrippedGlimmering);
		xplat.addAxeStripping(livingwood, livingwoodStripped);
		xplat.addAxeStripping(livingwoodGlimmering, livingwoodStrippedGlimmering);
		xplat.addAxeStripping(dreamwoodLog, dreamwoodLogStripped);
		xplat.addAxeStripping(dreamwoodLogGlimmering, dreamwoodLogStrippedGlimmering);
		xplat.addAxeStripping(dreamwood, dreamwoodStripped);
		xplat.addAxeStripping(dreamwoodGlimmering, dreamwoodStrippedGlimmering);

		xplat.addAxeStripping(BotaniaBlocks.livingwoodStairs, BotaniaBlocks.livingwoodStrippedStairs);
		xplat.addAxeStripping(BotaniaBlocks.livingwoodSlab, BotaniaBlocks.livingwoodStrippedSlab);
		xplat.addAxeStripping(BotaniaBlocks.livingwoodWall, BotaniaBlocks.livingwoodStrippedWall);
		xplat.addAxeStripping(BotaniaBlocks.dreamwoodStairs, BotaniaBlocks.dreamwoodStrippedStairs);
		xplat.addAxeStripping(BotaniaBlocks.dreamwoodSlab, BotaniaBlocks.dreamwoodStrippedSlab);
		xplat.addAxeStripping(BotaniaBlocks.dreamwoodWall, BotaniaBlocks.dreamwoodStrippedWall);
	}

	public static Block getFlower(DyeColor color) {
		return switch (color) {
			case WHITE -> whiteFlower;
			case ORANGE -> orangeFlower;
			case MAGENTA -> magentaFlower;
			case LIGHT_BLUE -> lightBlueFlower;
			case YELLOW -> yellowFlower;
			case LIME -> limeFlower;
			case PINK -> pinkFlower;
			case GRAY -> grayFlower;
			case LIGHT_GRAY -> lightGrayFlower;
			case CYAN -> cyanFlower;
			case PURPLE -> purpleFlower;
			case BLUE -> blueFlower;
			case BROWN -> brownFlower;
			case GREEN -> greenFlower;
			case RED -> redFlower;
			case BLACK -> blackFlower;
		};
	}

	public static Block getMushroom(DyeColor color) {
		return switch (color) {
			case WHITE -> whiteMushroom;
			case ORANGE -> orangeMushroom;
			case MAGENTA -> magentaMushroom;
			case LIGHT_BLUE -> lightBlueMushroom;
			case YELLOW -> yellowMushroom;
			case LIME -> limeMushroom;
			case PINK -> pinkMushroom;
			case GRAY -> grayMushroom;
			case LIGHT_GRAY -> lightGrayMushroom;
			case CYAN -> cyanMushroom;
			case PURPLE -> purpleMushroom;
			case BLUE -> blueMushroom;
			case BROWN -> brownMushroom;
			case GREEN -> greenMushroom;
			case RED -> redMushroom;
			case BLACK -> blackMushroom;
		};
	}

	public static Block getBuriedPetal(DyeColor color) {
		return switch (color) {
			case WHITE -> whiteBuriedPetals;
			case ORANGE -> orangeBuriedPetals;
			case MAGENTA -> magentaBuriedPetals;
			case LIGHT_BLUE -> lightBlueBuriedPetals;
			case YELLOW -> yellowBuriedPetals;
			case LIME -> limeBuriedPetals;
			case PINK -> pinkBuriedPetals;
			case GRAY -> grayBuriedPetals;
			case LIGHT_GRAY -> lightGrayBuriedPetals;
			case CYAN -> cyanBuriedPetals;
			case PURPLE -> purpleBuriedPetals;
			case BLUE -> blueBuriedPetals;
			case BROWN -> brownBuriedPetals;
			case GREEN -> greenBuriedPetals;
			case RED -> redBuriedPetals;
			case BLACK -> blackBuriedPetals;
		};
	}

	public static Block getShinyFlower(DyeColor color) {
		return switch (color) {
			case WHITE -> whiteShinyFlower;
			case ORANGE -> orangeShinyFlower;
			case MAGENTA -> magentaShinyFlower;
			case LIGHT_BLUE -> lightBlueShinyFlower;
			case YELLOW -> yellowShinyFlower;
			case LIME -> limeShinyFlower;
			case PINK -> pinkShinyFlower;
			case GRAY -> grayShinyFlower;
			case LIGHT_GRAY -> lightGrayShinyFlower;
			case CYAN -> cyanShinyFlower;
			case PURPLE -> purpleShinyFlower;
			case BLUE -> blueShinyFlower;
			case BROWN -> brownShinyFlower;
			case GREEN -> greenShinyFlower;
			case RED -> redShinyFlower;
			case BLACK -> blackShinyFlower;
		};
	}

	public static Block getFloatingFlower(DyeColor color) {
		return switch (color) {
			case WHITE -> whiteFloatingFlower;
			case ORANGE -> orangeFloatingFlower;
			case MAGENTA -> magentaFloatingFlower;
			case LIGHT_BLUE -> lightBlueFloatingFlower;
			case YELLOW -> yellowFloatingFlower;
			case LIME -> limeFloatingFlower;
			case PINK -> pinkFloatingFlower;
			case GRAY -> grayFloatingFlower;
			case LIGHT_GRAY -> lightGrayFloatingFlower;
			case CYAN -> cyanFloatingFlower;
			case PURPLE -> purpleFloatingFlower;
			case BLUE -> blueFloatingFlower;
			case BROWN -> brownFloatingFlower;
			case GREEN -> greenFloatingFlower;
			case RED -> redFloatingFlower;
			case BLACK -> blackFloatingFlower;
		};
	}

	public static Block getDoubleFlower(DyeColor color) {
		return switch (color) {
			case WHITE -> doubleFlowerWhite;
			case ORANGE -> doubleFlowerOrange;
			case MAGENTA -> doubleFlowerMagenta;
			case LIGHT_BLUE -> doubleFlowerLightBlue;
			case YELLOW -> doubleFlowerYellow;
			case LIME -> doubleFlowerLime;
			case PINK -> doubleFlowerPink;
			case GRAY -> doubleFlowerGray;
			case LIGHT_GRAY -> doubleFlowerLightGray;
			case CYAN -> doubleFlowerCyan;
			case PURPLE -> doubleFlowerPurple;
			case BLUE -> doubleFlowerBlue;
			case BROWN -> doubleFlowerBrown;
			case GREEN -> doubleFlowerGreen;
			case RED -> doubleFlowerRed;
			case BLACK -> doubleFlowerBlack;
		};
	}

	public static Block getPetalBlock(DyeColor color) {
		return switch (color) {
			case WHITE -> petalBlockWhite;
			case ORANGE -> petalBlockOrange;
			case MAGENTA -> petalBlockMagenta;
			case LIGHT_BLUE -> petalBlockLightBlue;
			case YELLOW -> petalBlockYellow;
			case LIME -> petalBlockLime;
			case PINK -> petalBlockPink;
			case GRAY -> petalBlockGray;
			case LIGHT_GRAY -> petalBlockSilver;
			case CYAN -> petalBlockCyan;
			case PURPLE -> petalBlockPurple;
			case BLUE -> petalBlockBlue;
			case BROWN -> petalBlockBrown;
			case GREEN -> petalBlockGreen;
			case RED -> petalBlockRed;
			case BLACK -> petalBlockBlack;
		};
	}

	public static Block getPottedFlower(DyeColor color) {
		return switch (color) {
			case WHITE -> pottedWhiteFlower;
			case ORANGE -> pottedOrangeFlower;
			case MAGENTA -> pottedMagentaFlower;
			case LIGHT_BLUE -> pottedLightBlueFlower;
			case YELLOW -> pottedYellowFlower;
			case LIME -> pottedLimeFlower;
			case PINK -> pottedPinkFlower;
			case GRAY -> pottedGrayFlower;
			case LIGHT_GRAY -> pottedLightGrayFlower;
			case CYAN -> pottedCyanFlower;
			case PURPLE -> pottedPurpleFlower;
			case BLUE -> pottedBlueFlower;
			case BROWN -> pottedBrownFlower;
			case GREEN -> pottedGreenFlower;
			case RED -> pottedRedFlower;
			case BLACK -> pottedBlackFlower;
		};
	}

	public static Block getPottedShinyFlower(DyeColor color) {
		return switch (color) {
			case WHITE -> pottedWhiteShinyFlower;
			case ORANGE -> pottedOrangeShinyFlower;
			case MAGENTA -> pottedMagentaShinyFlower;
			case LIGHT_BLUE -> pottedLightBlueShinyFlower;
			case YELLOW -> pottedYellowShinyFlower;
			case LIME -> pottedLimeShinyFlower;
			case PINK -> pottedPinkShinyFlower;
			case GRAY -> pottedGrayShinyFlower;
			case LIGHT_GRAY -> pottedLightGrayShinyFlower;
			case CYAN -> pottedCyanShinyFlower;
			case PURPLE -> pottedPurpleShinyFlower;
			case BLUE -> pottedBlueShinyFlower;
			case BROWN -> pottedBrownShinyFlower;
			case GREEN -> pottedGreenShinyFlower;
			case RED -> pottedRedShinyFlower;
			case BLACK -> pottedBlackShinyFlower;
		};
	}

	public static Block getPottedMushroom(DyeColor color) {
		return switch (color) {
			case WHITE -> pottedWhiteMushroom;
			case ORANGE -> pottedOrangeMushroom;
			case MAGENTA -> pottedMagentaMushroom;
			case LIGHT_BLUE -> pottedLightBlueMushroom;
			case YELLOW -> pottedYellowMushroom;
			case LIME -> pottedLimeMushroom;
			case PINK -> pottedPinkMushroom;
			case GRAY -> pottedGrayMushroom;
			case LIGHT_GRAY -> pottedLightGrayMushroom;
			case CYAN -> pottedCyanMushroom;
			case PURPLE -> pottedPurpleMushroom;
			case BLUE -> pottedBlueMushroom;
			case BROWN -> pottedBrownMushroom;
			case GREEN -> pottedGreenMushroom;
			case RED -> pottedRedMushroom;
			case BLACK -> pottedBlackMushroom;
		};
	}

	public static void registerFlowerPotPlants(BiConsumer<ResourceLocation, Supplier<? extends Block>> consumer) {
		ColorHelper.supportedColors().forEach(dyeColor -> {
			consumer.accept(prefix(dyeColor.getName() + MYSTICAL_FLOWER_SUFFIX), () -> getPottedFlower(dyeColor));
			consumer.accept(prefix(dyeColor.getName() + SHINY_FLOWER_SUFFIX), () -> getPottedShinyFlower(dyeColor));
			consumer.accept(prefix(dyeColor.getName() + MUSHROOM_SUFFIX), () -> getPottedMushroom(dyeColor));
		});
		consumer.accept(prefix(LibBlockNames.MOTIF_DAYBLOOM), () -> pottedMotifDaybloom);
		consumer.accept(prefix(LibBlockNames.MOTIF_NIGHTSHADE), () -> pottedMotifNightshade);
		consumer.accept(prefix(LibBlockNames.MOTIF_HYDROANGEAS), () -> pottedMotifHydroangeas);
	}
}
