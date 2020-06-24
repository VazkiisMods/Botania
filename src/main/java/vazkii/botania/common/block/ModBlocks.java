/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import vazkii.botania.api.state.enums.LuminizerVariant;
import vazkii.botania.client.render.tile.RenderTilePylon;
import vazkii.botania.client.render.tile.TEISR;
import vazkii.botania.common.block.corporea.*;
import vazkii.botania.common.block.decor.*;
import vazkii.botania.common.block.dispenser.BehaviourFelPumpkin;
import vazkii.botania.common.block.dispenser.BehaviourPoolMinecart;
import vazkii.botania.common.block.dispenser.BehaviourWand;
import vazkii.botania.common.block.dispenser.SeedBehaviours;
import vazkii.botania.common.block.mana.*;
import vazkii.botania.common.block.string.*;
import vazkii.botania.common.item.ItemGaiaHead;
import vazkii.botania.common.item.ModItems;
import vazkii.botania.common.item.block.ItemBlockDreamwood;
import vazkii.botania.common.item.block.ItemBlockElven;
import vazkii.botania.common.item.block.ItemBlockPool;
import vazkii.botania.common.item.block.ItemBlockTinyPotato;
import vazkii.botania.common.lib.LibBlockNames;
import vazkii.botania.common.lib.LibMisc;

import java.util.Locale;

public final class ModBlocks {
	public static Block whiteFlower = new BlockModFlower(DyeColor.WHITE, Block.Properties.create(Material.PLANTS).doesNotBlockMovement().hardnessAndResistance(0).sound(SoundType.PLANT));
	public static Block orangeFlower = new BlockModFlower(DyeColor.ORANGE, Block.Properties.from(whiteFlower));
	public static Block magentaFlower = new BlockModFlower(DyeColor.MAGENTA, Block.Properties.from(whiteFlower));
	public static Block lightBlueFlower = new BlockModFlower(DyeColor.LIGHT_BLUE, Block.Properties.from(whiteFlower));
	public static Block yellowFlower = new BlockModFlower(DyeColor.YELLOW, Block.Properties.from(whiteFlower));
	public static Block limeFlower = new BlockModFlower(DyeColor.LIME, Block.Properties.from(whiteFlower));
	public static Block pinkFlower = new BlockModFlower(DyeColor.PINK, Block.Properties.from(whiteFlower));
	public static Block grayFlower = new BlockModFlower(DyeColor.GRAY, Block.Properties.from(whiteFlower));
	public static Block lightGrayFlower = new BlockModFlower(DyeColor.LIGHT_GRAY, Block.Properties.from(whiteFlower));
	public static Block cyanFlower = new BlockModFlower(DyeColor.CYAN, Block.Properties.from(whiteFlower));
	public static Block purpleFlower = new BlockModFlower(DyeColor.PURPLE, Block.Properties.from(whiteFlower));
	public static Block blueFlower = new BlockModFlower(DyeColor.BLUE, Block.Properties.from(whiteFlower));
	public static Block brownFlower = new BlockModFlower(DyeColor.BROWN, Block.Properties.from(whiteFlower));
	public static Block greenFlower = new BlockModFlower(DyeColor.GREEN, Block.Properties.from(whiteFlower));
	public static Block redFlower = new BlockModFlower(DyeColor.RED, Block.Properties.from(whiteFlower));
	public static Block blackFlower = new BlockModFlower(DyeColor.BLACK, Block.Properties.from(whiteFlower));

	public static Block defaultAltar = new BlockAltar(BlockAltar.Variant.DEFAULT, Block.Properties.create(Material.ROCK).hardnessAndResistance(3.5F).sound(SoundType.STONE));
	public static Block forestAltar = new BlockAltar(BlockAltar.Variant.FOREST, Block.Properties.from(defaultAltar));
	public static Block plainsAltar = new BlockAltar(BlockAltar.Variant.PLAINS, Block.Properties.from(defaultAltar));
	public static Block mountainAltar = new BlockAltar(BlockAltar.Variant.MOUNTAIN, Block.Properties.from(defaultAltar));
	public static Block fungalAltar = new BlockAltar(BlockAltar.Variant.FUNGAL, Block.Properties.from(defaultAltar));
	public static Block swampAltar = new BlockAltar(BlockAltar.Variant.SWAMP, Block.Properties.from(defaultAltar));
	public static Block desertAltar = new BlockAltar(BlockAltar.Variant.DESERT, Block.Properties.from(defaultAltar));
	public static Block taigaAltar = new BlockAltar(BlockAltar.Variant.TAIGA, Block.Properties.from(defaultAltar));
	public static Block mesaAltar = new BlockAltar(BlockAltar.Variant.MESA, Block.Properties.from(defaultAltar));
	public static Block mossyAltar = new BlockAltar(BlockAltar.Variant.MOSSY, Block.Properties.from(defaultAltar));

	public static Block livingrock = new BlockMod(Block.Properties.create(Material.ROCK).hardnessAndResistance(2, 10).sound(SoundType.STONE));
	public static Block livingrockBrick = new BlockMod(Block.Properties.from(livingrock));
	public static Block livingrockBrickChiseled = new BlockMod(Block.Properties.from(livingrock));
	public static Block livingrockBrickCracked = new BlockMod(Block.Properties.from(livingrock));
	public static Block livingrockBrickMossy = new BlockMod(Block.Properties.from(livingrock));

	public static Block livingwood = new BlockMod(Block.Properties.create(Material.WOOD).hardnessAndResistance(2).sound(SoundType.WOOD));
	public static Block livingwoodPlanks = new BlockMod(Block.Properties.from(livingwood));
	public static Block livingwoodPlanksMossy = new BlockMod(Block.Properties.from(livingwood));
	public static Block livingwoodFramed = new BlockMod(Block.Properties.from(livingwood));
	public static Block livingwoodPatternFramed = new BlockMod(Block.Properties.from(livingwood));
	public static Block livingwoodGlimmering = new BlockMod(Block.Properties.from(livingwood).lightValue(12));

	public static Block manaSpreader = new BlockSpreader(BlockSpreader.Variant.MANA, Block.Properties.from(livingwood));
	public static Block redstoneSpreader = new BlockSpreader(BlockSpreader.Variant.REDSTONE, Block.Properties.from(livingwood));
	public static Block elvenSpreader = new BlockSpreader(BlockSpreader.Variant.ELVEN, Block.Properties.from(livingwood));
	public static Block gaiaSpreader = new BlockSpreader(BlockSpreader.Variant.GAIA, Block.Properties.from(livingwood));

	public static Block manaPool = new BlockPool(BlockPool.Variant.DEFAULT, Block.Properties.from(livingrock));
	public static Block creativePool = new BlockPool(BlockPool.Variant.CREATIVE, Block.Properties.from(livingrock));
	public static Block dilutedPool = new BlockPool(BlockPool.Variant.DILUTED, Block.Properties.from(livingrock));
	public static Block fabulousPool = new BlockPool(BlockPool.Variant.FABULOUS, Block.Properties.from(livingrock));

	public static Block runeAltar = new BlockRuneAltar(Block.Properties.from(livingrock));
	public static Block manaPylon = new BlockPylon(BlockPylon.Variant.MANA, Block.Properties.create(Material.IRON).hardnessAndResistance(5.5F).sound(SoundType.METAL).lightValue(7));
	public static Block naturaPylon = new BlockPylon(BlockPylon.Variant.NATURA, Block.Properties.from(manaPylon));
	public static Block gaiaPylon = new BlockPylon(BlockPylon.Variant.GAIA, Block.Properties.from(manaPylon));
	
	public static Block pistonRelay = new BlockPistonRelay(Block.Properties.create(Material.GOURD).hardnessAndResistance(2, 10).sound(SoundType.METAL));
	public static Block distributor = new BlockDistributor(Block.Properties.create(Material.ROCK).hardnessAndResistance(2, 10).sound(SoundType.STONE));
	public static Block manaVoid = new BlockManaVoid(Block.Properties.create(Material.ROCK).hardnessAndResistance(2, 2000).sound(SoundType.STONE));
	public static Block manaDetector = new BlockManaDetector(Block.Properties.from(livingrock));
	public static Block enchanter = new BlockEnchanter(Block.Properties.create(Material.ROCK).hardnessAndResistance(3, 5).lightValue(15).sound(SoundType.STONE));
	public static Block turntable = new BlockTurntable(Block.Properties.from(livingwood));
	public static Block tinyPlanet = new BlockTinyPlanet(Block.Properties.create(Material.ROCK).hardnessAndResistance(20, 100).sound(SoundType.STONE));
	public static Block alchemyCatalyst = new BlockAlchemyCatalyst(Block.Properties.from(livingrock));
	public static Block openCrate = new BlockOpenCrate(Block.Properties.from(livingwood));
	public static Block craftCrate = new BlockCraftyCrate(Block.Properties.from(livingwood));
	public static Block forestEye = new BlockForestEye(Block.Properties.create(Material.IRON).hardnessAndResistance(5, 10).sound(SoundType.METAL));
	public static Block manasteelBlock = new BlockStorage(Block.Properties.create(Material.IRON).hardnessAndResistance(3, 10).sound(SoundType.METAL));
	public static Block terrasteelBlock = new BlockStorage(Block.Properties.from(manasteelBlock));
	public static Block elementiumBlock = new BlockStorage(Block.Properties.from(manasteelBlock));
	public static Block manaDiamondBlock = new BlockStorage(Block.Properties.from(manasteelBlock));
	public static Block dragonstoneBlock = new BlockStorage(Block.Properties.from(manasteelBlock));
	public static Block wildDrum = new BlockForestDrum(BlockForestDrum.Variant.WILD, Block.Properties.from(livingwood));
	public static Block gatheringDrum = new BlockForestDrum(BlockForestDrum.Variant.GATHERING, Block.Properties.from(livingwood));
	public static Block canopyDrum = new BlockForestDrum(BlockForestDrum.Variant.CANOPY, Block.Properties.from(livingwood));
	
	public static Block whiteShinyFlower = new BlockShinyFlower(DyeColor.WHITE, Block.Properties.from(whiteFlower).lightValue(15));
	public static Block orangeShinyFlower = new BlockShinyFlower(DyeColor.ORANGE, Block.Properties.from(whiteShinyFlower));
	public static Block magentaShinyFlower = new BlockShinyFlower(DyeColor.MAGENTA, Block.Properties.from(whiteShinyFlower));
	public static Block lightBlueShinyFlower = new BlockShinyFlower(DyeColor.LIGHT_BLUE, Block.Properties.from(whiteShinyFlower));
	public static Block yellowShinyFlower = new BlockShinyFlower(DyeColor.YELLOW, Block.Properties.from(whiteShinyFlower));
	public static Block limeShinyFlower = new BlockShinyFlower(DyeColor.LIME, Block.Properties.from(whiteShinyFlower));
	public static Block pinkShinyFlower = new BlockShinyFlower(DyeColor.PINK, Block.Properties.from(whiteShinyFlower));
	public static Block grayShinyFlower = new BlockShinyFlower(DyeColor.GRAY, Block.Properties.from(whiteShinyFlower));
	public static Block lightGrayShinyFlower = new BlockShinyFlower(DyeColor.LIGHT_GRAY, Block.Properties.from(whiteShinyFlower));
	public static Block cyanShinyFlower = new BlockShinyFlower(DyeColor.CYAN, Block.Properties.from(whiteShinyFlower));
	public static Block purpleShinyFlower = new BlockShinyFlower(DyeColor.PURPLE, Block.Properties.from(whiteShinyFlower));
	public static Block blueShinyFlower = new BlockShinyFlower(DyeColor.BLUE, Block.Properties.from(whiteShinyFlower));
	public static Block brownShinyFlower = new BlockShinyFlower(DyeColor.BROWN, Block.Properties.from(whiteShinyFlower));
	public static Block greenShinyFlower = new BlockShinyFlower(DyeColor.GREEN, Block.Properties.from(whiteShinyFlower));
	public static Block redShinyFlower = new BlockShinyFlower(DyeColor.RED, Block.Properties.from(whiteShinyFlower));
	public static Block blackShinyFlower = new BlockShinyFlower(DyeColor.BLACK, Block.Properties.from(whiteShinyFlower));
	
	public static Block abstrusePlatform = new BlockPlatform(BlockPlatform.Variant.ABSTRUSE, Block.Properties.create(Material.WOOD).hardnessAndResistance(2, 5).sound(SoundType.WOOD));
	public static Block spectralPlatform = new BlockPlatform(BlockPlatform.Variant.SPECTRAL, Block.Properties.from(abstrusePlatform));
	public static Block infrangiblePlatform = new BlockPlatform(BlockPlatform.Variant.INFRANGIBLE, Block.Properties.create(Material.WOOD).hardnessAndResistance(-1, Float.MAX_VALUE).sound(SoundType.WOOD));
	public static Block alfPortal = new BlockAlfPortal(Block.Properties.create(Material.WOOD).hardnessAndResistance(10).sound(SoundType.WOOD));
	public static Block dreamwood = new BlockMod(Block.Properties.from(livingwood));
	public static Block dreamwoodPlanks = new BlockMod(Block.Properties.from(livingwood));
	public static Block dreamwoodPlanksMossy = new BlockMod(Block.Properties.from(livingwood));
	public static Block dreamwoodFramed = new BlockMod(Block.Properties.from(livingwood));
	public static Block dreamwoodPatternFramed = new BlockMod(Block.Properties.from(livingwood));
	public static Block dreamwoodGlimmering = new BlockMod(Block.Properties.from(livingwoodGlimmering));
	public static Block conjurationCatalyst = new BlockConjurationCatalyst(Block.Properties.from(livingrock));
	public static Block bifrost = new BlockBifrost(Block.Properties.create(Material.GLASS).hardnessAndResistance(-1, 0.3F).lightValue(15).sound(SoundType.GLASS).notSolid());
	public static Block solidVines = new BlockSolidVines(Block.Properties.create(Material.TALL_PLANTS).hardnessAndResistance(0.2F).sound(SoundType.PLANT).notSolid());

	public static Block whiteBuriedPetals = new BlockBuriedPetals(DyeColor.WHITE, Block.Properties.from(whiteFlower).lightValue(4));
	public static Block orangeBuriedPetals = new BlockBuriedPetals(DyeColor.ORANGE, Block.Properties.from(whiteBuriedPetals));
	public static Block magentaBuriedPetals = new BlockBuriedPetals(DyeColor.MAGENTA, Block.Properties.from(whiteBuriedPetals));
	public static Block lightBlueBuriedPetals = new BlockBuriedPetals(DyeColor.LIGHT_BLUE, Block.Properties.from(whiteBuriedPetals));
	public static Block yellowBuriedPetals = new BlockBuriedPetals(DyeColor.YELLOW, Block.Properties.from(whiteBuriedPetals));
	public static Block limeBuriedPetals = new BlockBuriedPetals(DyeColor.LIME, Block.Properties.from(whiteBuriedPetals));
	public static Block pinkBuriedPetals = new BlockBuriedPetals(DyeColor.PINK, Block.Properties.from(whiteBuriedPetals));
	public static Block grayBuriedPetals = new BlockBuriedPetals(DyeColor.GRAY, Block.Properties.from(whiteBuriedPetals));
	public static Block lightGrayBuriedPetals = new BlockBuriedPetals(DyeColor.LIGHT_GRAY, Block.Properties.from(whiteBuriedPetals));
	public static Block cyanBuriedPetals = new BlockBuriedPetals(DyeColor.CYAN, Block.Properties.from(whiteBuriedPetals));
	public static Block purpleBuriedPetals = new BlockBuriedPetals(DyeColor.PURPLE, Block.Properties.from(whiteBuriedPetals));
	public static Block blueBuriedPetals = new BlockBuriedPetals(DyeColor.BLUE, Block.Properties.from(whiteBuriedPetals));
	public static Block brownBuriedPetals = new BlockBuriedPetals(DyeColor.BROWN, Block.Properties.from(whiteBuriedPetals));
	public static Block greenBuriedPetals = new BlockBuriedPetals(DyeColor.GREEN, Block.Properties.from(whiteBuriedPetals));
	public static Block redBuriedPetals = new BlockBuriedPetals(DyeColor.RED, Block.Properties.from(whiteBuriedPetals));
	public static Block blackBuriedPetals = new BlockBuriedPetals(DyeColor.BLACK, Block.Properties.from(whiteBuriedPetals));
	
	public static Block whiteFloatingFlower = new BlockFloatingFlower(DyeColor.WHITE, Block.Properties.create(Material.EARTH).hardnessAndResistance(0.5F).sound(SoundType.GROUND).lightValue(15));
	public static Block orangeFloatingFlower = new BlockFloatingFlower(DyeColor.ORANGE, Block.Properties.from(whiteFloatingFlower));
	public static Block magentaFloatingFlower = new BlockFloatingFlower(DyeColor.MAGENTA, Block.Properties.from(whiteFloatingFlower));
	public static Block lightBlueFloatingFlower = new BlockFloatingFlower(DyeColor.LIGHT_BLUE, Block.Properties.from(whiteFloatingFlower));
	public static Block yellowFloatingFlower = new BlockFloatingFlower(DyeColor.YELLOW, Block.Properties.from(whiteFloatingFlower));
	public static Block limeFloatingFlower = new BlockFloatingFlower(DyeColor.LIME, Block.Properties.from(whiteFloatingFlower));
	public static Block pinkFloatingFlower = new BlockFloatingFlower(DyeColor.PINK, Block.Properties.from(whiteFloatingFlower));
	public static Block grayFloatingFlower = new BlockFloatingFlower(DyeColor.GRAY, Block.Properties.from(whiteFloatingFlower));
	public static Block lightGrayFloatingFlower = new BlockFloatingFlower(DyeColor.LIGHT_GRAY, Block.Properties.from(whiteFloatingFlower));
	public static Block cyanFloatingFlower = new BlockFloatingFlower(DyeColor.CYAN, Block.Properties.from(whiteFloatingFlower));
	public static Block purpleFloatingFlower = new BlockFloatingFlower(DyeColor.PURPLE, Block.Properties.from(whiteFloatingFlower));
	public static Block blueFloatingFlower = new BlockFloatingFlower(DyeColor.BLUE, Block.Properties.from(whiteFloatingFlower));
	public static Block brownFloatingFlower = new BlockFloatingFlower(DyeColor.BROWN, Block.Properties.from(whiteFloatingFlower));
	public static Block greenFloatingFlower = new BlockFloatingFlower(DyeColor.GREEN, Block.Properties.from(whiteFloatingFlower));
	public static Block redFloatingFlower = new BlockFloatingFlower(DyeColor.RED, Block.Properties.from(whiteFloatingFlower));
	public static Block blackFloatingFlower = new BlockFloatingFlower(DyeColor.BLACK, Block.Properties.from(whiteFloatingFlower));
	
	public static Block tinyPotato = new BlockTinyPotato(Block.Properties.create(Material.WOOL).hardnessAndResistance(0.25F));
	public static Block spawnerClaw = new BlockSpawnerClaw(Block.Properties.create(Material.IRON).hardnessAndResistance(3));
	
	public static Block azulejo0 = new BlockMod(Block.Properties.create(Material.ROCK).hardnessAndResistance(2, 5).sound(SoundType.STONE));
	public static Block azulejo1 = new BlockMod(Block.Properties.from(azulejo0));
	public static Block azulejo2 = new BlockMod(Block.Properties.from(azulejo0));
	public static Block azulejo3 = new BlockMod(Block.Properties.from(azulejo0));
	public static Block azulejo4 = new BlockMod(Block.Properties.from(azulejo0));
	public static Block azulejo5 = new BlockMod(Block.Properties.from(azulejo0));
	public static Block azulejo6 = new BlockMod(Block.Properties.from(azulejo0));
	public static Block azulejo7 = new BlockMod(Block.Properties.from(azulejo0));
	public static Block azulejo8 = new BlockMod(Block.Properties.from(azulejo0));
	public static Block azulejo9 = new BlockMod(Block.Properties.from(azulejo0));
	public static Block azulejo10 = new BlockMod(Block.Properties.from(azulejo0));
	public static Block azulejo11 = new BlockMod(Block.Properties.from(azulejo0));
	public static Block azulejo12 = new BlockMod(Block.Properties.from(azulejo0));
	public static Block azulejo13 = new BlockMod(Block.Properties.from(azulejo0));
	public static Block azulejo14 = new BlockMod(Block.Properties.from(azulejo0));
	public static Block azulejo15 = new BlockMod(Block.Properties.from(azulejo0));
	public static Block enderEye = new BlockEnderEye(Block.Properties.from(manasteelBlock));
	public static Block starfield = new BlockStarfield(Block.Properties.create(Material.IRON).hardnessAndResistance(5, 2000).sound(SoundType.METAL));
	public static Block rfGenerator = new BlockRFGenerator(Block.Properties.from(livingrock));
	public static Block elfGlass = new BlockElfGlass(Block.Properties.from(Blocks.GLASS).lightValue(15));
	public static Block brewery = new BlockBrewery(Block.Properties.from(livingrock));
	public static Block manaGlass = new BlockModGlass(Block.Properties.from(elfGlass));
	public static Block terraPlate = new BlockTerraPlate(Block.Properties.create(Material.IRON).hardnessAndResistance(3, 10).sound(SoundType.METAL));
	public static Block redStringContainer = new BlockRedStringContainer(Block.Properties.from(livingrock));
	public static Block redStringDispenser = new BlockRedStringDispenser(Block.Properties.from(livingrock));
	public static Block redStringFertilizer = new BlockRedStringFertilizer(Block.Properties.from(livingrock));
	public static Block redStringComparator = new BlockRedStringComparator(Block.Properties.from(livingrock));
	public static Block redStringRelay = new BlockRedStringRelay(Block.Properties.from(livingrock));
	public static Block redStringInterceptor = new BlockRedStringInterceptor(Block.Properties.from(livingrock));
	public static Block manaFlame = new BlockManaFlame(Block.Properties.create(Material.WOOL).sound(SoundType.CLOTH).lightValue(15).doesNotBlockMovement());
	public static Block prism = new BlockPrism(Block.Properties.from(elfGlass).doesNotBlockMovement());
	public static Block enchantedSoil = new BlockEnchantedSoil(Block.Properties.create(Material.ORGANIC).hardnessAndResistance(0.6F).sound(SoundType.PLANT));
	
	public static Block petalBlockWhite = new BlockPetalBlock(DyeColor.WHITE, Block.Properties.create(Material.PLANTS).hardnessAndResistance(0.4F).sound(SoundType.PLANT));
	public static Block petalBlockOrange = new BlockPetalBlock(DyeColor.ORANGE, Block.Properties.from(petalBlockWhite));
	public static Block petalBlockMagenta = new BlockPetalBlock(DyeColor.MAGENTA, Block.Properties.from(petalBlockWhite));
	public static Block petalBlockLightBlue = new BlockPetalBlock(DyeColor.LIGHT_BLUE, Block.Properties.from(petalBlockWhite));
	public static Block petalBlockYellow = new BlockPetalBlock(DyeColor.YELLOW, Block.Properties.from(petalBlockWhite));
	public static Block petalBlockLime = new BlockPetalBlock(DyeColor.LIME, Block.Properties.from(petalBlockWhite));
	public static Block petalBlockPink = new BlockPetalBlock(DyeColor.PINK, Block.Properties.from(petalBlockWhite));
	public static Block petalBlockGray = new BlockPetalBlock(DyeColor.GRAY, Block.Properties.from(petalBlockWhite));
	public static Block petalBlockSilver = new BlockPetalBlock(DyeColor.LIGHT_GRAY, Block.Properties.from(petalBlockWhite));
	public static Block petalBlockCyan = new BlockPetalBlock(DyeColor.CYAN, Block.Properties.from(petalBlockWhite));
	public static Block petalBlockPurple = new BlockPetalBlock(DyeColor.PURPLE, Block.Properties.from(petalBlockWhite));
	public static Block petalBlockBlue = new BlockPetalBlock(DyeColor.BLUE, Block.Properties.from(petalBlockWhite));
	public static Block petalBlockBrown = new BlockPetalBlock(DyeColor.BROWN, Block.Properties.from(petalBlockWhite));
	public static Block petalBlockGreen = new BlockPetalBlock(DyeColor.GREEN, Block.Properties.from(petalBlockWhite));
	public static Block petalBlockRed = new BlockPetalBlock(DyeColor.RED, Block.Properties.from(petalBlockWhite));
	public static Block petalBlockBlack = new BlockPetalBlock(DyeColor.BLACK, Block.Properties.from(petalBlockWhite));

	public static Block corporeaIndex = new BlockCorporeaIndex(Block.Properties.create(Material.IRON).hardnessAndResistance(5.5F).sound(SoundType.METAL).notSolid());
	public static Block corporeaFunnel = new BlockCorporeaFunnel(Block.Properties.create(Material.IRON).hardnessAndResistance(5.5F).sound(SoundType.METAL));

	public static Block whiteMushroom = new BlockModMushroom(DyeColor.WHITE, Block.Properties.from(whiteFlower).lightValue(3));
	public static Block orangeMushroom = new BlockModMushroom(DyeColor.ORANGE, Block.Properties.from(whiteMushroom));
	public static Block magentaMushroom = new BlockModMushroom(DyeColor.MAGENTA, Block.Properties.from(whiteMushroom));
	public static Block lightBlueMushroom = new BlockModMushroom(DyeColor.LIGHT_BLUE, Block.Properties.from(whiteMushroom));
	public static Block yellowMushroom = new BlockModMushroom(DyeColor.YELLOW, Block.Properties.from(whiteMushroom));
	public static Block limeMushroom = new BlockModMushroom(DyeColor.LIME, Block.Properties.from(whiteMushroom));
	public static Block pinkMushroom = new BlockModMushroom(DyeColor.PINK, Block.Properties.from(whiteMushroom));
	public static Block grayMushroom = new BlockModMushroom(DyeColor.GRAY, Block.Properties.from(whiteMushroom));
	public static Block lightGrayMushroom = new BlockModMushroom(DyeColor.LIGHT_GRAY, Block.Properties.from(whiteMushroom));
	public static Block cyanMushroom = new BlockModMushroom(DyeColor.CYAN, Block.Properties.from(whiteMushroom));
	public static Block purpleMushroom = new BlockModMushroom(DyeColor.PURPLE, Block.Properties.from(whiteMushroom));
	public static Block blueMushroom = new BlockModMushroom(DyeColor.BLUE, Block.Properties.from(whiteMushroom));
	public static Block brownMushroom = new BlockModMushroom(DyeColor.BROWN, Block.Properties.from(whiteMushroom));
	public static Block greenMushroom = new BlockModMushroom(DyeColor.GREEN, Block.Properties.from(whiteMushroom));
	public static Block redMushroom = new BlockModMushroom(DyeColor.RED, Block.Properties.from(whiteMushroom));
	public static Block blackMushroom = new BlockModMushroom(DyeColor.BLACK, Block.Properties.from(whiteMushroom));

	public static Block pump = new BlockPump(Block.Properties.from(livingrock));
	
	public static Block doubleFlowerWhite = new BlockModDoubleFlower(DyeColor.WHITE, Block.Properties.from(whiteFlower));
	public static Block doubleFlowerOrange = new BlockModDoubleFlower(DyeColor.ORANGE, Block.Properties.from(doubleFlowerWhite));
	public static Block doubleFlowerMagenta = new BlockModDoubleFlower(DyeColor.MAGENTA, Block.Properties.from(doubleFlowerWhite));
	public static Block doubleFlowerLightBlue = new BlockModDoubleFlower(DyeColor.LIGHT_BLUE, Block.Properties.from(doubleFlowerWhite));
	public static Block doubleFlowerYellow = new BlockModDoubleFlower(DyeColor.YELLOW, Block.Properties.from(doubleFlowerWhite));
	public static Block doubleFlowerLime = new BlockModDoubleFlower(DyeColor.LIME, Block.Properties.from(doubleFlowerWhite));
	public static Block doubleFlowerPink = new BlockModDoubleFlower(DyeColor.PINK, Block.Properties.from(doubleFlowerWhite));
	public static Block doubleFlowerGray = new BlockModDoubleFlower(DyeColor.GRAY, Block.Properties.from(doubleFlowerWhite));
	public static Block doubleFlowerLightGray = new BlockModDoubleFlower(DyeColor.LIGHT_GRAY, Block.Properties.from(doubleFlowerWhite));
	public static Block doubleFlowerCyan = new BlockModDoubleFlower(DyeColor.CYAN, Block.Properties.from(doubleFlowerWhite));
	public static Block doubleFlowerPurple = new BlockModDoubleFlower(DyeColor.PURPLE, Block.Properties.from(doubleFlowerWhite));
	public static Block doubleFlowerBlue = new BlockModDoubleFlower(DyeColor.BLUE, Block.Properties.from(doubleFlowerWhite));
	public static Block doubleFlowerBrown = new BlockModDoubleFlower(DyeColor.BROWN, Block.Properties.from(doubleFlowerWhite));
	public static Block doubleFlowerGreen = new BlockModDoubleFlower(DyeColor.GREEN, Block.Properties.from(doubleFlowerWhite));
	public static Block doubleFlowerRed = new BlockModDoubleFlower(DyeColor.RED, Block.Properties.from(doubleFlowerWhite));
	public static Block doubleFlowerBlack = new BlockModDoubleFlower(DyeColor.BLACK, Block.Properties.from(doubleFlowerWhite));

	public static Block fakeAir = new BlockFakeAir(Block.Properties.create(Material.STRUCTURE_VOID).tickRandomly());
	public static Block blazeBlock = new BlockMod(Block.Properties.from(manasteelBlock).lightValue(15));
	public static Block corporeaInterceptor = new BlockCorporeaInterceptor(Block.Properties.create(Material.IRON).hardnessAndResistance(5.5F).sound(SoundType.METAL));
	public static Block corporeaCrystalCube = new BlockCorporeaCrystalCube(Block.Properties.from(corporeaInterceptor));
	public static Block incensePlate = new BlockIncensePlate(Block.Properties.from(livingwood));
	public static Block hourglass = new BlockHourglass(Block.Properties.create(Material.IRON).hardnessAndResistance(2).sound(SoundType.METAL));
	public static Block ghostRail = new BlockGhostRail(Block.Properties.from(Blocks.RAIL));
	public static Block sparkChanger = new BlockSparkChanger(Block.Properties.from(livingrock));
	public static Block root = new BlockRoot(Block.Properties.create(Material.PLANTS).hardnessAndResistance(1.2F).sound(SoundType.WOOD));
	public static Block felPumpkin = new BlockFelPumpkin(Block.Properties.from(Blocks.CARVED_PUMPKIN));
	public static Block cocoon = new BlockCocoon(Block.Properties.create(Material.WOOL).hardnessAndResistance(3, 60).sound(SoundType.CLOTH));
	public static Block lightRelayDefault = new BlockLightRelay(LuminizerVariant.DEFAULT, Block.Properties.create(Material.GLASS).doesNotBlockMovement());
	public static Block lightRelayDetector = new BlockLightRelay(LuminizerVariant.DETECTOR, Block.Properties.from(lightRelayDefault));
	public static Block lightRelayFork = new BlockLightRelay(LuminizerVariant.TOGGLE, Block.Properties.from(lightRelayDefault));
	public static Block lightRelayToggle = new BlockLightRelay(LuminizerVariant.FORK, Block.Properties.from(lightRelayDefault));
	public static Block lightLauncher = new BlockLightLauncher(Block.Properties.from(livingwood));
	public static Block manaBomb = new BlockManaBomb(Block.Properties.create(Material.WOOD).hardnessAndResistance(12).sound(SoundType.WOOD));
	public static Block cacophonium = new BlockCacophonium(Block.Properties.create(Material.WOOD).hardnessAndResistance(0.8F));
	public static Block bellows = new BlockBellows(Block.Properties.from(livingwood));
	public static Block bifrostPerm = new BlockBifrostPerm(Block.Properties.create(Material.GLASS).hardnessAndResistance(0.3F).lightValue(15).sound(SoundType.GLASS).notSolid());
	public static Block cellBlock = new BlockCell(Block.Properties.create(Material.GOURD).sound(SoundType.CLOTH));
	public static Block gaiaHeadWall = new BlockGaiaHeadWall(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(1));
	public static Block gaiaHead = new BlockGaiaHead(Block.Properties.create(Material.MISCELLANEOUS).hardnessAndResistance(1));
	public static Block corporeaRetainer = new BlockCorporeaRetainer(Block.Properties.from(corporeaInterceptor));
	public static Block teruTeruBozu = new BlockTeruTeruBozu(Block.Properties.create(Material.WOOL));
	public static Block shimmerrock = new BlockMod(Block.Properties.from(livingrock));
	public static Block shimmerwoodPlanks = new BlockMod(Block.Properties.from(livingwood));
	public static Block avatar = new BlockAvatar(Block.Properties.from(livingwood));
	public static Block dryGrass = new BlockAltGrass(BlockAltGrass.Variant.DRY, Block.Properties.create(Material.ORGANIC).hardnessAndResistance(0.6F).tickRandomly().sound(SoundType.PLANT));
	public static Block goldenGrass = new BlockAltGrass(BlockAltGrass.Variant.GOLDEN, Block.Properties.from(dryGrass));
	public static Block vividGrass = new BlockAltGrass(BlockAltGrass.Variant.VIVID, Block.Properties.from(dryGrass));
	public static Block scorchedGrass = new BlockAltGrass(BlockAltGrass.Variant.SCORCHED, Block.Properties.from(dryGrass));
	public static Block infusedGrass = new BlockAltGrass(BlockAltGrass.Variant.INFUSED, Block.Properties.from(dryGrass));
	public static Block mutatedGrass = new BlockAltGrass(BlockAltGrass.Variant.MUTATED, Block.Properties.from(dryGrass));
	public static Block animatedTorch = new BlockAnimatedTorch(Block.Properties.create(Material.MISCELLANEOUS).lightValue(7).notSolid());

	public static void registerBlocks(RegistryEvent.Register<Block> evt) {
		IForgeRegistry<Block> r = evt.getRegistry();
		register(r, "white" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, whiteFlower);
		register(r, "orange" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, orangeFlower);
		register(r, "magenta" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, magentaFlower);
		register(r, "light_blue" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, lightBlueFlower);
		register(r, "yellow" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, yellowFlower);
		register(r, "lime" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, limeFlower);
		register(r, "pink" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, pinkFlower);
		register(r, "gray" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, grayFlower);
		register(r, "light_gray" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, lightGrayFlower);
		register(r, "cyan" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, cyanFlower);
		register(r, "purple" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, purpleFlower);
		register(r, "blue" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, blueFlower);
		register(r, "brown" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, brownFlower);
		register(r, "green" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, greenFlower);
		register(r, "red" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, redFlower);
		register(r, "black" + LibBlockNames.MYSTICAL_FLOWER_SUFFIX, blackFlower);
		
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.DEFAULT.name().toLowerCase(Locale.ROOT), defaultAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.FOREST.name().toLowerCase(Locale.ROOT), forestAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.PLAINS.name().toLowerCase(Locale.ROOT), plainsAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.MOUNTAIN.name().toLowerCase(Locale.ROOT), mountainAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.FUNGAL.name().toLowerCase(Locale.ROOT), fungalAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.SWAMP.name().toLowerCase(Locale.ROOT), swampAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.DESERT.name().toLowerCase(Locale.ROOT), desertAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.TAIGA.name().toLowerCase(Locale.ROOT), taigaAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.MESA.name().toLowerCase(Locale.ROOT), mesaAltar);
		register(r, LibBlockNames.APOTHECARY_PREFIX + BlockAltar.Variant.MOSSY.name().toLowerCase(Locale.ROOT), mossyAltar);

		register(r, LibBlockNames.LIVING_ROCK, livingrock);
		register(r, LibBlockNames.LIVING_ROCK_BRICK, livingrockBrick);
		register(r, LibBlockNames.LIVING_ROCK_BRICK_MOSSY, livingrockBrickMossy);
		register(r, LibBlockNames.LIVING_ROCK_BRICK_CRACKED, livingrockBrickCracked);
		register(r, LibBlockNames.LIVING_ROCK_BRICK_CHISELED, livingrockBrickChiseled);

		// todo 1.13: livingwood should support leaves
		register(r, LibBlockNames.LIVING_WOOD, livingwood);
		register(r, LibBlockNames.LIVING_WOOD_PLANKS, livingwoodPlanks);
		register(r, LibBlockNames.LIVING_WOOD_PLANKS_MOSSY, livingwoodPlanksMossy);
		register(r, LibBlockNames.LIVING_WOOD_FRAMED, livingwoodFramed);
		register(r, LibBlockNames.LIVING_WOOD_PATTERN_FRAMED, livingwoodPatternFramed);
		register(r, LibBlockNames.LIVING_WOOD_GLIMMERING, livingwoodGlimmering);

		register(r, LibBlockNames.SPREADER, manaSpreader);
		register(r, LibBlockNames.SPREADER_REDSTONE, redstoneSpreader);
		register(r, LibBlockNames.SPREADER_ELVEN, elvenSpreader);
		register(r, LibBlockNames.SPREADER_GAIA, gaiaSpreader);

		register(r, LibBlockNames.POOL, manaPool);
		register(r, LibBlockNames.POOL_CREATIVE, creativePool);
		register(r, LibBlockNames.POOL_DILUTED, dilutedPool);
		register(r, LibBlockNames.POOL_FABULOUS, fabulousPool);

		register(r, LibBlockNames.RUNE_ALTAR, runeAltar);
		
		register(r, LibBlockNames.PYLON, manaPylon);
		register(r, LibBlockNames.PYLON_NATURA, naturaPylon);
		register(r, LibBlockNames.PYLON_GAIA, gaiaPylon);

		register(r, LibBlockNames.PISTON_RELAY, pistonRelay);

		register(r, LibBlockNames.DISTRIBUTOR, distributor);

		register(r, LibBlockNames.MANA_VOID, manaVoid);

		register(r, LibBlockNames.MANA_DETECTOR, manaDetector);

		register(r, LibBlockNames.ENCHANTER, enchanter);
		register(r, LibBlockNames.TURNTABLE, turntable);
		register(r, LibBlockNames.TINY_PLANET, tinyPlanet);
		register(r, LibBlockNames.ALCHEMY_CATALYST, alchemyCatalyst);

		register(r, LibBlockNames.OPEN_CRATE, openCrate);
		register(r, LibBlockNames.CRAFT_CRATE, craftCrate);

		register(r, LibBlockNames.FOREST_EYE, forestEye);
		
		register(r, LibBlockNames.MANASTEEL_BLOCK, manasteelBlock);
		register(r, LibBlockNames.TERRASTEEL_BLOCK, terrasteelBlock);
		register(r, LibBlockNames.ELEMENTIUM_BLOCK, elementiumBlock);
		register(r, LibBlockNames.MANA_DIAMOND_BLOCK, manaDiamondBlock);
		register(r, LibBlockNames.DRAGONSTONE_BLOCK, dragonstoneBlock);

		register(r, LibBlockNames.DRUM_WILD, wildDrum);
		register(r, LibBlockNames.DRUM_GATHERING, gatheringDrum);
		register(r, LibBlockNames.DRUM_CANOPY, canopyDrum);
		
		register(r, "white" + LibBlockNames.SHINY_FLOWER_SUFFIX, whiteShinyFlower);
		register(r, "orange" + LibBlockNames.SHINY_FLOWER_SUFFIX, orangeShinyFlower);
		register(r, "magenta" + LibBlockNames.SHINY_FLOWER_SUFFIX, magentaShinyFlower);
		register(r, "light_blue" + LibBlockNames.SHINY_FLOWER_SUFFIX, lightBlueShinyFlower);
		register(r, "yellow" + LibBlockNames.SHINY_FLOWER_SUFFIX, yellowShinyFlower);
		register(r, "lime" + LibBlockNames.SHINY_FLOWER_SUFFIX, limeShinyFlower);
		register(r, "pink" + LibBlockNames.SHINY_FLOWER_SUFFIX, pinkShinyFlower);
		register(r, "gray" + LibBlockNames.SHINY_FLOWER_SUFFIX, grayShinyFlower);
		register(r, "light_gray" + LibBlockNames.SHINY_FLOWER_SUFFIX, lightGrayShinyFlower);
		register(r, "cyan" + LibBlockNames.SHINY_FLOWER_SUFFIX, cyanShinyFlower);
		register(r, "purple" + LibBlockNames.SHINY_FLOWER_SUFFIX, purpleShinyFlower);
		register(r, "blue" + LibBlockNames.SHINY_FLOWER_SUFFIX, blueShinyFlower);
		register(r, "brown" + LibBlockNames.SHINY_FLOWER_SUFFIX, brownShinyFlower);
		register(r, "green" + LibBlockNames.SHINY_FLOWER_SUFFIX, greenShinyFlower);
		register(r, "red" + LibBlockNames.SHINY_FLOWER_SUFFIX, redShinyFlower);
		register(r, "black" + LibBlockNames.SHINY_FLOWER_SUFFIX, blackShinyFlower);

		register(r, LibBlockNames.PLATFORM_ABSTRUSE, abstrusePlatform);
		register(r, LibBlockNames.PLATFORM_SPECTRAL, spectralPlatform);
		register(r, LibBlockNames.PLATFORM_INFRANGIBLE, infrangiblePlatform);
		register(r, LibBlockNames.ALF_PORTAL, alfPortal);

		// todo 1.13: dreamwood should support leaves?
		register(r, LibBlockNames.DREAM_WOOD, dreamwood);
		register(r, LibBlockNames.DREAM_WOOD_PLANKS, dreamwoodPlanks);
		register(r, LibBlockNames.DREAM_WOOD_PLANKS_MOSSY, dreamwoodPlanksMossy);
		register(r, LibBlockNames.DREAM_WOOD_FRAMED, dreamwoodFramed);
		register(r, LibBlockNames.DREAM_WOOD_PATTERN_FRAMED, dreamwoodPatternFramed);
		register(r, LibBlockNames.DREAM_WOOD_GLIMMERING, dreamwoodGlimmering);

		register(r, LibBlockNames.CONJURATION_CATALYST, conjurationCatalyst);
		register(r, LibBlockNames.BIFROST, bifrost);
		register(r, LibBlockNames.SOLID_VINE, solidVines);
		
		register(r, "white" + LibBlockNames.BURIED_PETALS_SUFFIX, whiteBuriedPetals);
		register(r, "orange" + LibBlockNames.BURIED_PETALS_SUFFIX, orangeBuriedPetals);
		register(r, "magenta" + LibBlockNames.BURIED_PETALS_SUFFIX, magentaBuriedPetals);
		register(r, "light_blue" + LibBlockNames.BURIED_PETALS_SUFFIX, lightBlueBuriedPetals);
		register(r, "yellow" + LibBlockNames.BURIED_PETALS_SUFFIX, yellowBuriedPetals);
		register(r, "lime" + LibBlockNames.BURIED_PETALS_SUFFIX, limeBuriedPetals);
		register(r, "pink" + LibBlockNames.BURIED_PETALS_SUFFIX, pinkBuriedPetals);
		register(r, "gray" + LibBlockNames.BURIED_PETALS_SUFFIX, grayBuriedPetals);
		register(r, "light_gray" + LibBlockNames.BURIED_PETALS_SUFFIX, lightGrayBuriedPetals);
		register(r, "cyan" + LibBlockNames.BURIED_PETALS_SUFFIX, cyanBuriedPetals);
		register(r, "purple" + LibBlockNames.BURIED_PETALS_SUFFIX, purpleBuriedPetals);
		register(r, "blue" + LibBlockNames.BURIED_PETALS_SUFFIX, blueBuriedPetals);
		register(r, "brown" + LibBlockNames.BURIED_PETALS_SUFFIX, brownBuriedPetals);
		register(r, "green" + LibBlockNames.BURIED_PETALS_SUFFIX, greenBuriedPetals);
		register(r, "red" + LibBlockNames.BURIED_PETALS_SUFFIX, redBuriedPetals);
		register(r, "black" + LibBlockNames.BURIED_PETALS_SUFFIX, blackBuriedPetals);
		
		register(r, "white" + LibBlockNames.FLOATING_FLOWER_SUFFIX, whiteFloatingFlower);
		register(r, "orange" + LibBlockNames.FLOATING_FLOWER_SUFFIX, orangeFloatingFlower);
		register(r, "magenta" + LibBlockNames.FLOATING_FLOWER_SUFFIX, magentaFloatingFlower);
		register(r, "light_blue" + LibBlockNames.FLOATING_FLOWER_SUFFIX, lightBlueFloatingFlower);
		register(r, "yellow" + LibBlockNames.FLOATING_FLOWER_SUFFIX, yellowFloatingFlower);
		register(r, "lime" + LibBlockNames.FLOATING_FLOWER_SUFFIX, limeFloatingFlower);
		register(r, "pink" + LibBlockNames.FLOATING_FLOWER_SUFFIX, pinkFloatingFlower);
		register(r, "gray" + LibBlockNames.FLOATING_FLOWER_SUFFIX, grayFloatingFlower);
		register(r, "light_gray" + LibBlockNames.FLOATING_FLOWER_SUFFIX, lightGrayFloatingFlower);
		register(r, "cyan" + LibBlockNames.FLOATING_FLOWER_SUFFIX, cyanFloatingFlower);
		register(r, "purple" + LibBlockNames.FLOATING_FLOWER_SUFFIX, purpleFloatingFlower);
		register(r, "blue" + LibBlockNames.FLOATING_FLOWER_SUFFIX, blueFloatingFlower);
		register(r, "brown" + LibBlockNames.FLOATING_FLOWER_SUFFIX, brownFloatingFlower);
		register(r, "green" + LibBlockNames.FLOATING_FLOWER_SUFFIX, greenFloatingFlower);
		register(r, "red" + LibBlockNames.FLOATING_FLOWER_SUFFIX, redFloatingFlower);
		register(r, "black" + LibBlockNames.FLOATING_FLOWER_SUFFIX, blackFloatingFlower);

		register(r, LibBlockNames.TINY_POTATO, tinyPotato);
		register(r, LibBlockNames.SPAWNER_CLAW, spawnerClaw);
		
		register(r, LibBlockNames.AZULEJO_PREFIX + 0, azulejo0);
		register(r, LibBlockNames.AZULEJO_PREFIX + 1, azulejo1);
		register(r, LibBlockNames.AZULEJO_PREFIX + 2, azulejo2);
		register(r, LibBlockNames.AZULEJO_PREFIX + 3, azulejo3);
		register(r, LibBlockNames.AZULEJO_PREFIX + 4, azulejo4);
		register(r, LibBlockNames.AZULEJO_PREFIX + 5, azulejo5);
		register(r, LibBlockNames.AZULEJO_PREFIX + 6, azulejo6);
		register(r, LibBlockNames.AZULEJO_PREFIX + 7, azulejo7);
		register(r, LibBlockNames.AZULEJO_PREFIX + 8, azulejo8);
		register(r, LibBlockNames.AZULEJO_PREFIX + 9, azulejo9);
		register(r, LibBlockNames.AZULEJO_PREFIX + 10, azulejo10);
		register(r, LibBlockNames.AZULEJO_PREFIX + 11, azulejo11);
		register(r, LibBlockNames.AZULEJO_PREFIX + 12, azulejo12);
		register(r, LibBlockNames.AZULEJO_PREFIX + 13, azulejo13);
		register(r, LibBlockNames.AZULEJO_PREFIX + 14, azulejo14);
		register(r, LibBlockNames.AZULEJO_PREFIX + 15, azulejo15);

		register(r, LibBlockNames.ENDER_EYE_BLOCK, enderEye);
		register(r, LibBlockNames.STARFIELD, starfield);
		register(r, LibBlockNames.FLUXFIELD, rfGenerator);
		register(r, LibBlockNames.ELF_GLASS, elfGlass);
		register(r, LibBlockNames.BREWERY, brewery);
		register(r, LibBlockNames.MANA_GLASS, manaGlass);
		register(r, LibBlockNames.TERRA_PLATE, terraPlate);

		register(r, LibBlockNames.RED_STRING_CONTAINER, redStringContainer);
		register(r, LibBlockNames.RED_STRING_DISPENSER, redStringDispenser);
		register(r, LibBlockNames.RED_STRING_FERTILIZER, redStringFertilizer);
		register(r, LibBlockNames.RED_STRING_COMPARATOR, redStringComparator);
		register(r, LibBlockNames.RED_STRING_RELAY, redStringRelay);
		register(r, LibBlockNames.RED_STRING_INTERCEPTOR, redStringInterceptor);
		register(r, LibBlockNames.MANA_FLAME, manaFlame);
		register(r, LibBlockNames.PRISM, prism);
		register(r, LibBlockNames.ENCHANTED_SOIL, enchantedSoil);
		
		register(r, "white" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockWhite);
		register(r, "orange" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockOrange);
		register(r, "magenta" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockMagenta);
		register(r, "light_blue" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockLightBlue);
		register(r, "yellow" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockYellow);
		register(r, "lime" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockLime);
		register(r, "pink" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockPink);
		register(r, "gray" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockGray);
		register(r, "light_gray" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockSilver);
		register(r, "cyan" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockCyan);
		register(r, "purple" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockPurple);
		register(r, "blue" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockBlue);
		register(r, "brown" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockBrown);
		register(r, "green" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockGreen);
		register(r, "red" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockRed);
		register(r, "black" + LibBlockNames.PETAL_BLOCK_SUFFIX, petalBlockBlack);

		register(r, LibBlockNames.CORPOREA_INDEX, corporeaIndex);
		register(r, LibBlockNames.CORPOREA_FUNNEL, corporeaFunnel);
		
		register(r, "white" + LibBlockNames.MUSHROOM_SUFFIX, whiteMushroom);
		register(r, "orange" + LibBlockNames.MUSHROOM_SUFFIX, orangeMushroom);
		register(r, "magenta" + LibBlockNames.MUSHROOM_SUFFIX, magentaMushroom);
		register(r, "light_blue" + LibBlockNames.MUSHROOM_SUFFIX, lightBlueMushroom);
		register(r, "yellow" + LibBlockNames.MUSHROOM_SUFFIX, yellowMushroom);
		register(r, "lime" + LibBlockNames.MUSHROOM_SUFFIX, limeMushroom);
		register(r, "pink" + LibBlockNames.MUSHROOM_SUFFIX, pinkMushroom);
		register(r, "gray" + LibBlockNames.MUSHROOM_SUFFIX, grayMushroom);
		register(r, "light_gray" + LibBlockNames.MUSHROOM_SUFFIX, lightGrayMushroom);
		register(r, "cyan" + LibBlockNames.MUSHROOM_SUFFIX, cyanMushroom);
		register(r, "purple" + LibBlockNames.MUSHROOM_SUFFIX, purpleMushroom);
		register(r, "blue" + LibBlockNames.MUSHROOM_SUFFIX, blueMushroom);
		register(r, "brown" + LibBlockNames.MUSHROOM_SUFFIX, brownMushroom);
		register(r, "green" + LibBlockNames.MUSHROOM_SUFFIX, greenMushroom);
		register(r, "red" + LibBlockNames.MUSHROOM_SUFFIX, redMushroom);
		register(r, "black" + LibBlockNames.MUSHROOM_SUFFIX, blackMushroom);

		register(r, LibBlockNames.PUMP, pump);
		
		register(r, "white" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerWhite);
		register(r, "orange" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerOrange);
		register(r, "magenta" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerMagenta);
		register(r, "light_blue" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerLightBlue);
		register(r, "yellow" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerYellow);
		register(r, "lime" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerLime);
		register(r, "pink" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerPink);
		register(r, "gray" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerGray);
		register(r, "light_gray" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerLightGray);
		register(r, "cyan" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerCyan);
		register(r, "purple" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerPurple);
		register(r, "blue" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerBlue);
		register(r, "brown" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerBrown);
		register(r, "green" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerGreen);
		register(r, "red" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerRed);
		register(r, "black" + LibBlockNames.DOUBLE_FLOWER_SUFFIX, doubleFlowerBlack);

		register(r, LibBlockNames.FAKE_AIR, fakeAir);
		register(r, LibBlockNames.BLAZE_BLOCK, blazeBlock);
		register(r, LibBlockNames.CORPOREA_INTERCEPTOR, corporeaInterceptor);
		register(r, LibBlockNames.CORPOREA_CRYSTAL_CUBE, corporeaCrystalCube);
		register(r, LibBlockNames.INCENSE_PLATE, incensePlate);
		register(r, LibBlockNames.HOURGLASS, hourglass);
		register(r, LibBlockNames.GHOST_RAIL, ghostRail);
		register(r, LibBlockNames.SPARK_CHANGER, sparkChanger);
		register(r, LibBlockNames.ROOT, root);
		register(r, LibBlockNames.FEL_PUMPKIN, felPumpkin);
		register(r, LibBlockNames.COCOON, cocoon);

		register(r, LibBlockNames.LIGHT_RELAY, lightRelayDefault);
		register(r, "detector" + LibBlockNames.LIGHT_RELAY_SUFFIX, lightRelayDetector);
		register(r, "fork" + LibBlockNames.LIGHT_RELAY_SUFFIX, lightRelayFork);
		register(r, "toggle" + LibBlockNames.LIGHT_RELAY_SUFFIX, lightRelayToggle);

		register(r, LibBlockNames.LIGHT_LAUNCHER, lightLauncher);
		register(r, LibBlockNames.MANA_BOMB, manaBomb);
		register(r, LibBlockNames.CACOPHONIUM, cacophonium);
		register(r, LibBlockNames.BELLOWS, bellows);
		register(r, LibBlockNames.BIFROST_PERM, bifrostPerm);
		register(r, LibBlockNames.CELL_BLOCK, cellBlock);
		register(r, LibBlockNames.GAIA_WALL_HEAD, gaiaHeadWall);
		register(r, LibBlockNames.GAIA_HEAD, gaiaHead);
		register(r, LibBlockNames.CORPOREA_RETAINER, corporeaRetainer);
		register(r, LibBlockNames.TERU_TERU_BOZU, teruTeruBozu);
		register(r, LibBlockNames.SHIMMERROCK, shimmerrock);
		register(r, LibBlockNames.SHIMMERWOOD_PLANKS, shimmerwoodPlanks);
		register(r, LibBlockNames.AVATAR, avatar);

		register(r, BlockAltGrass.Variant.DRY.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX, dryGrass);
		register(r, BlockAltGrass.Variant.GOLDEN.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX, goldenGrass);
		register(r, BlockAltGrass.Variant.VIVID.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX, vividGrass);
		register(r, BlockAltGrass.Variant.SCORCHED.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX, scorchedGrass);
		register(r, BlockAltGrass.Variant.INFUSED.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX, infusedGrass);
		register(r, BlockAltGrass.Variant.MUTATED.name().toLowerCase(Locale.ROOT) + LibBlockNames.ALT_GRASS_SUFFIX, mutatedGrass);

		register(r, LibBlockNames.ANIMATED_TORCH, animatedTorch);
	}

	public static void registerItemBlocks(RegistryEvent.Register<Item> evt) {
		IForgeRegistry<Item> r = evt.getRegistry();
		Item.Properties props = ModItems.defaultBuilder();
		register(r, Registry.BLOCK.getKey(whiteFlower), new BlockItem(whiteFlower, props));
		register(r, Registry.BLOCK.getKey(orangeFlower), new BlockItem(orangeFlower, props));
		register(r, Registry.BLOCK.getKey(magentaFlower), new BlockItem(magentaFlower, props));
		register(r, Registry.BLOCK.getKey(lightBlueFlower), new BlockItem(lightBlueFlower, props));
		register(r, Registry.BLOCK.getKey(yellowFlower), new BlockItem(yellowFlower, props));
		register(r, Registry.BLOCK.getKey(limeFlower), new BlockItem(limeFlower, props));
		register(r, Registry.BLOCK.getKey(pinkFlower), new BlockItem(pinkFlower, props));
		register(r, Registry.BLOCK.getKey(grayFlower), new BlockItem(grayFlower, props));
		register(r, Registry.BLOCK.getKey(lightGrayFlower), new BlockItem(lightGrayFlower, props));
		register(r, Registry.BLOCK.getKey(cyanFlower), new BlockItem(cyanFlower, props));
		register(r, Registry.BLOCK.getKey(purpleFlower), new BlockItem(purpleFlower, props));
		register(r, Registry.BLOCK.getKey(blueFlower), new BlockItem(blueFlower, props));
		register(r, Registry.BLOCK.getKey(brownFlower), new BlockItem(brownFlower, props));
		register(r, Registry.BLOCK.getKey(greenFlower), new BlockItem(greenFlower, props));
		register(r, Registry.BLOCK.getKey(redFlower), new BlockItem(redFlower, props));
		register(r, Registry.BLOCK.getKey(blackFlower), new BlockItem(blackFlower, props));
		register(r, Registry.BLOCK.getKey(defaultAltar), new BlockItem(defaultAltar, props));
		register(r, Registry.BLOCK.getKey(forestAltar), new BlockItem(forestAltar, props));
		register(r, Registry.BLOCK.getKey(plainsAltar), new BlockItem(plainsAltar, props));
		register(r, Registry.BLOCK.getKey(mountainAltar), new BlockItem(mountainAltar, props));
		register(r, Registry.BLOCK.getKey(fungalAltar), new BlockItem(fungalAltar, props));
		register(r, Registry.BLOCK.getKey(swampAltar), new BlockItem(swampAltar, props));
		register(r, Registry.BLOCK.getKey(desertAltar), new BlockItem(desertAltar, props));
		register(r, Registry.BLOCK.getKey(taigaAltar), new BlockItem(taigaAltar, props));
		register(r, Registry.BLOCK.getKey(mesaAltar), new BlockItem(mesaAltar, props));
		register(r, Registry.BLOCK.getKey(mossyAltar), new BlockItem(mossyAltar, props));
		register(r, Registry.BLOCK.getKey(livingrock), new BlockItem(livingrock, props));
		register(r, Registry.BLOCK.getKey(livingrockBrick), new BlockItem(livingrockBrick, props));
		register(r, Registry.BLOCK.getKey(livingrockBrickChiseled), new BlockItem(livingrockBrickChiseled, props));
		register(r, Registry.BLOCK.getKey(livingrockBrickCracked), new BlockItem(livingrockBrickCracked, props));
		register(r, Registry.BLOCK.getKey(livingrockBrickMossy), new BlockItem(livingrockBrickMossy, props));
		register(r, Registry.BLOCK.getKey(livingwood), new BlockItem(livingwood, props));
		register(r, Registry.BLOCK.getKey(livingwoodPlanks), new BlockItem(livingwoodPlanks, props));
		register(r, Registry.BLOCK.getKey(livingwoodPlanksMossy), new BlockItem(livingwoodPlanksMossy, props));
		register(r, Registry.BLOCK.getKey(livingwoodFramed), new BlockItem(livingwoodFramed, props));
		register(r, Registry.BLOCK.getKey(livingwoodPatternFramed), new BlockItem(livingwoodPatternFramed, props));
		register(r, Registry.BLOCK.getKey(livingwoodGlimmering), new BlockItem(livingwoodGlimmering, props));
		register(r, Registry.BLOCK.getKey(manaSpreader), new BlockItem(manaSpreader, props));
		register(r, Registry.BLOCK.getKey(redstoneSpreader), new BlockItem(redstoneSpreader, props));
		register(r, Registry.BLOCK.getKey(elvenSpreader), new BlockItem(elvenSpreader, props));
		register(r, Registry.BLOCK.getKey(gaiaSpreader), new BlockItem(gaiaSpreader, props));
		register(r, Registry.BLOCK.getKey(manaPool), new ItemBlockPool(manaPool, props));
		register(r, Registry.BLOCK.getKey(creativePool), new ItemBlockPool(creativePool, props));
		register(r, Registry.BLOCK.getKey(dilutedPool), new ItemBlockPool(dilutedPool, props));
		register(r, Registry.BLOCK.getKey(fabulousPool), new ItemBlockPool(fabulousPool, props));
		register(r, Registry.BLOCK.getKey(runeAltar), new BlockItem(runeAltar, props));
		register(r, Registry.BLOCK.getKey(pistonRelay), new BlockItem(pistonRelay, props));
		register(r, Registry.BLOCK.getKey(distributor), new BlockItem(distributor, props));
		register(r, Registry.BLOCK.getKey(manaVoid), new BlockItem(manaVoid, props));
		register(r, Registry.BLOCK.getKey(manaDetector), new BlockItem(manaDetector, props));
		register(r, Registry.BLOCK.getKey(enchanter), new BlockItem(enchanter, props));
		register(r, Registry.BLOCK.getKey(turntable), new BlockItem(turntable, props));
		register(r, Registry.BLOCK.getKey(tinyPlanet), new BlockItem(tinyPlanet, props));
		register(r, Registry.BLOCK.getKey(alchemyCatalyst), new BlockItem(alchemyCatalyst, props));
		register(r, Registry.BLOCK.getKey(openCrate), new BlockItem(openCrate, props));
		register(r, Registry.BLOCK.getKey(craftCrate), new BlockItem(craftCrate, props));
		register(r, Registry.BLOCK.getKey(forestEye), new BlockItem(forestEye, props));
		register(r, Registry.BLOCK.getKey(manasteelBlock), new BlockItem(manasteelBlock, props));
		register(r, Registry.BLOCK.getKey(terrasteelBlock), new BlockItem(terrasteelBlock, props));
		register(r, Registry.BLOCK.getKey(elementiumBlock), new ItemBlockElven(elementiumBlock, props));
		register(r, Registry.BLOCK.getKey(manaDiamondBlock), new BlockItem(manaDiamondBlock, props));
		register(r, Registry.BLOCK.getKey(dragonstoneBlock), new BlockItem(dragonstoneBlock, props));
		register(r, Registry.BLOCK.getKey(wildDrum), new BlockItem(wildDrum, props));
		register(r, Registry.BLOCK.getKey(gatheringDrum), new BlockItem(gatheringDrum, props));
		register(r, Registry.BLOCK.getKey(canopyDrum), new BlockItem(canopyDrum, props));
		register(r, Registry.BLOCK.getKey(whiteShinyFlower), new BlockItem(whiteShinyFlower, props));
		register(r, Registry.BLOCK.getKey(orangeShinyFlower), new BlockItem(orangeShinyFlower, props));
		register(r, Registry.BLOCK.getKey(magentaShinyFlower), new BlockItem(magentaShinyFlower, props));
		register(r, Registry.BLOCK.getKey(lightBlueShinyFlower), new BlockItem(lightBlueShinyFlower, props));
		register(r, Registry.BLOCK.getKey(yellowShinyFlower), new BlockItem(yellowShinyFlower, props));
		register(r, Registry.BLOCK.getKey(limeShinyFlower), new BlockItem(limeShinyFlower, props));
		register(r, Registry.BLOCK.getKey(pinkShinyFlower), new BlockItem(pinkShinyFlower, props));
		register(r, Registry.BLOCK.getKey(grayShinyFlower), new BlockItem(grayShinyFlower, props));
		register(r, Registry.BLOCK.getKey(lightGrayShinyFlower), new BlockItem(lightGrayShinyFlower, props));
		register(r, Registry.BLOCK.getKey(cyanShinyFlower), new BlockItem(cyanShinyFlower, props));
		register(r, Registry.BLOCK.getKey(purpleShinyFlower), new BlockItem(purpleShinyFlower, props));
		register(r, Registry.BLOCK.getKey(blueShinyFlower), new BlockItem(blueShinyFlower, props));
		register(r, Registry.BLOCK.getKey(brownShinyFlower), new BlockItem(brownShinyFlower, props));
		register(r, Registry.BLOCK.getKey(greenShinyFlower), new BlockItem(greenShinyFlower, props));
		register(r, Registry.BLOCK.getKey(redShinyFlower), new BlockItem(redShinyFlower, props));
		register(r, Registry.BLOCK.getKey(blackShinyFlower), new BlockItem(blackShinyFlower, props));
		register(r, Registry.BLOCK.getKey(abstrusePlatform), new BlockItem(abstrusePlatform, props));
		register(r, Registry.BLOCK.getKey(spectralPlatform), new BlockItem(spectralPlatform, props));
		register(r, Registry.BLOCK.getKey(infrangiblePlatform), new BlockItem(infrangiblePlatform, props));
		register(r, Registry.BLOCK.getKey(alfPortal), new BlockItem(alfPortal, props));
		register(r, Registry.BLOCK.getKey(dreamwood), new ItemBlockDreamwood(dreamwood, props));
		register(r, Registry.BLOCK.getKey(dreamwoodPlanks), new ItemBlockDreamwood(dreamwoodPlanks, props));
		register(r, Registry.BLOCK.getKey(dreamwoodPlanksMossy), new ItemBlockDreamwood(dreamwoodPlanksMossy, props));
		register(r, Registry.BLOCK.getKey(dreamwoodFramed), new ItemBlockDreamwood(dreamwoodFramed, props));
		register(r, Registry.BLOCK.getKey(dreamwoodPatternFramed), new ItemBlockDreamwood(dreamwoodPatternFramed, props));
		register(r, Registry.BLOCK.getKey(dreamwoodGlimmering), new ItemBlockDreamwood(dreamwoodGlimmering, props));
		register(r, Registry.BLOCK.getKey(conjurationCatalyst), new BlockItem(conjurationCatalyst, props));
		register(r, Registry.BLOCK.getKey(bifrost), new BlockItem(bifrost, props));
		register(r, Registry.BLOCK.getKey(whiteFloatingFlower), new BlockItem(whiteFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(orangeFloatingFlower), new BlockItem(orangeFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(magentaFloatingFlower), new BlockItem(magentaFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(lightBlueFloatingFlower), new BlockItem(lightBlueFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(yellowFloatingFlower), new BlockItem(yellowFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(limeFloatingFlower), new BlockItem(limeFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(pinkFloatingFlower), new BlockItem(pinkFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(grayFloatingFlower), new BlockItem(grayFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(lightGrayFloatingFlower), new BlockItem(lightGrayFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(cyanFloatingFlower), new BlockItem(cyanFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(purpleFloatingFlower), new BlockItem(purpleFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(blueFloatingFlower), new BlockItem(blueFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(brownFloatingFlower), new BlockItem(brownFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(greenFloatingFlower), new BlockItem(greenFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(redFloatingFlower), new BlockItem(redFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(blackFloatingFlower), new BlockItem(blackFloatingFlower, props));
		register(r, Registry.BLOCK.getKey(tinyPotato), new ItemBlockTinyPotato(tinyPotato, props));
		register(r, Registry.BLOCK.getKey(spawnerClaw), new BlockItem(spawnerClaw, props));
		register(r, Registry.BLOCK.getKey(azulejo0), new BlockItem(azulejo0, props));
		register(r, Registry.BLOCK.getKey(azulejo1), new BlockItem(azulejo1, props));
		register(r, Registry.BLOCK.getKey(azulejo2), new BlockItem(azulejo2, props));
		register(r, Registry.BLOCK.getKey(azulejo3), new BlockItem(azulejo3, props));
		register(r, Registry.BLOCK.getKey(azulejo4), new BlockItem(azulejo4, props));
		register(r, Registry.BLOCK.getKey(azulejo5), new BlockItem(azulejo5, props));
		register(r, Registry.BLOCK.getKey(azulejo6), new BlockItem(azulejo6, props));
		register(r, Registry.BLOCK.getKey(azulejo7), new BlockItem(azulejo7, props));
		register(r, Registry.BLOCK.getKey(azulejo8), new BlockItem(azulejo8, props));
		register(r, Registry.BLOCK.getKey(azulejo9), new BlockItem(azulejo9, props));
		register(r, Registry.BLOCK.getKey(azulejo10), new BlockItem(azulejo10, props));
		register(r, Registry.BLOCK.getKey(azulejo11), new BlockItem(azulejo11, props));
		register(r, Registry.BLOCK.getKey(azulejo12), new BlockItem(azulejo12, props));
		register(r, Registry.BLOCK.getKey(azulejo13), new BlockItem(azulejo13, props));
		register(r, Registry.BLOCK.getKey(azulejo14), new BlockItem(azulejo14, props));
		register(r, Registry.BLOCK.getKey(azulejo15), new BlockItem(azulejo15, props));
		register(r, Registry.BLOCK.getKey(enderEye), new BlockItem(enderEye, props));
		register(r, Registry.BLOCK.getKey(starfield), new BlockItem(starfield, props));
		register(r, Registry.BLOCK.getKey(rfGenerator), new BlockItem(rfGenerator, props));
		register(r, Registry.BLOCK.getKey(elfGlass), new ItemBlockElven(elfGlass, props));
		register(r, Registry.BLOCK.getKey(manaGlass), new BlockItem(manaGlass, props));
		register(r, Registry.BLOCK.getKey(terraPlate), new BlockItem(terraPlate, props));
		register(r, Registry.BLOCK.getKey(redStringContainer), new BlockItem(redStringContainer, props));
		register(r, Registry.BLOCK.getKey(redStringDispenser), new BlockItem(redStringDispenser, props));
		register(r, Registry.BLOCK.getKey(redStringFertilizer), new BlockItem(redStringFertilizer, props));
		register(r, Registry.BLOCK.getKey(redStringComparator), new BlockItem(redStringComparator, props));
		register(r, Registry.BLOCK.getKey(redStringRelay), new BlockItem(redStringRelay, props));
		register(r, Registry.BLOCK.getKey(prism), new BlockItem(prism, props));
		register(r, Registry.BLOCK.getKey(enchantedSoil), new BlockItem(enchantedSoil, props));
		register(r, Registry.BLOCK.getKey(petalBlockWhite), new BlockItem(petalBlockWhite, props));
		register(r, Registry.BLOCK.getKey(petalBlockOrange), new BlockItem(petalBlockOrange, props));
		register(r, Registry.BLOCK.getKey(petalBlockMagenta), new BlockItem(petalBlockMagenta, props));
		register(r, Registry.BLOCK.getKey(petalBlockLightBlue), new BlockItem(petalBlockLightBlue, props));
		register(r, Registry.BLOCK.getKey(petalBlockYellow), new BlockItem(petalBlockYellow, props));
		register(r, Registry.BLOCK.getKey(petalBlockLime), new BlockItem(petalBlockLime, props));
		register(r, Registry.BLOCK.getKey(petalBlockPink), new BlockItem(petalBlockPink, props));
		register(r, Registry.BLOCK.getKey(petalBlockGray), new BlockItem(petalBlockGray, props));
		register(r, Registry.BLOCK.getKey(petalBlockSilver), new BlockItem(petalBlockSilver, props));
		register(r, Registry.BLOCK.getKey(petalBlockCyan), new BlockItem(petalBlockCyan, props));
		register(r, Registry.BLOCK.getKey(petalBlockPurple), new BlockItem(petalBlockPurple, props));
		register(r, Registry.BLOCK.getKey(petalBlockBlue), new BlockItem(petalBlockBlue, props));
		register(r, Registry.BLOCK.getKey(petalBlockBrown), new BlockItem(petalBlockBrown, props));
		register(r, Registry.BLOCK.getKey(petalBlockGreen), new BlockItem(petalBlockGreen, props));
		register(r, Registry.BLOCK.getKey(petalBlockRed), new BlockItem(petalBlockRed, props));
		register(r, Registry.BLOCK.getKey(petalBlockBlack), new BlockItem(petalBlockBlack, props));
		register(r, Registry.BLOCK.getKey(corporeaFunnel), new BlockItem(corporeaFunnel, props));
		register(r, Registry.BLOCK.getKey(whiteMushroom), new BlockItem(whiteMushroom, props));
		register(r, Registry.BLOCK.getKey(orangeMushroom), new BlockItem(orangeMushroom, props));
		register(r, Registry.BLOCK.getKey(magentaMushroom), new BlockItem(magentaMushroom, props));
		register(r, Registry.BLOCK.getKey(lightBlueMushroom), new BlockItem(lightBlueMushroom, props));
		register(r, Registry.BLOCK.getKey(yellowMushroom), new BlockItem(yellowMushroom, props));
		register(r, Registry.BLOCK.getKey(limeMushroom), new BlockItem(limeMushroom, props));
		register(r, Registry.BLOCK.getKey(pinkMushroom), new BlockItem(pinkMushroom, props));
		register(r, Registry.BLOCK.getKey(grayMushroom), new BlockItem(grayMushroom, props));
		register(r, Registry.BLOCK.getKey(lightGrayMushroom), new BlockItem(lightGrayMushroom, props));
		register(r, Registry.BLOCK.getKey(cyanMushroom), new BlockItem(cyanMushroom, props));
		register(r, Registry.BLOCK.getKey(purpleMushroom), new BlockItem(purpleMushroom, props));
		register(r, Registry.BLOCK.getKey(blueMushroom), new BlockItem(blueMushroom, props));
		register(r, Registry.BLOCK.getKey(brownMushroom), new BlockItem(brownMushroom, props));
		register(r, Registry.BLOCK.getKey(greenMushroom), new BlockItem(greenMushroom, props));
		register(r, Registry.BLOCK.getKey(redMushroom), new BlockItem(redMushroom, props));
		register(r, Registry.BLOCK.getKey(blackMushroom), new BlockItem(blackMushroom, props));
		register(r, Registry.BLOCK.getKey(pump), new BlockItem(pump, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerWhite), new BlockItem(doubleFlowerWhite, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerOrange), new BlockItem(doubleFlowerOrange, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerMagenta), new BlockItem(doubleFlowerMagenta, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerLightBlue), new BlockItem(doubleFlowerLightBlue, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerYellow), new BlockItem(doubleFlowerYellow, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerLime), new BlockItem(doubleFlowerLime, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerPink), new BlockItem(doubleFlowerPink, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerGray), new BlockItem(doubleFlowerGray, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerLightGray), new BlockItem(doubleFlowerLightGray, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerCyan), new BlockItem(doubleFlowerCyan, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerPurple), new BlockItem(doubleFlowerPurple, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerBlue), new BlockItem(doubleFlowerBlue, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerBrown), new BlockItem(doubleFlowerBrown, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerGreen), new BlockItem(doubleFlowerGreen, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerRed), new BlockItem(doubleFlowerRed, props));
		register(r, Registry.BLOCK.getKey(doubleFlowerBlack), new BlockItem(doubleFlowerBlack, props));
		register(r, Registry.BLOCK.getKey(blazeBlock), new ItemBlockBlaze(blazeBlock, props));
		register(r, Registry.BLOCK.getKey(corporeaInterceptor), new BlockItem(corporeaInterceptor, props));
		register(r, Registry.BLOCK.getKey(incensePlate), new BlockItem(incensePlate, props));
		register(r, Registry.BLOCK.getKey(ghostRail), new BlockItem(ghostRail, props));
		register(r, Registry.BLOCK.getKey(sparkChanger), new BlockItem(sparkChanger, props));
		register(r, Registry.BLOCK.getKey(root), new BlockItem(root, props));
		register(r, Registry.BLOCK.getKey(felPumpkin), new BlockItem(felPumpkin, props));
		register(r, Registry.BLOCK.getKey(cocoon), new BlockItem(cocoon, props));
		register(r, Registry.BLOCK.getKey(lightRelayDefault), new BlockItem(lightRelayDefault, props));
		register(r, Registry.BLOCK.getKey(lightRelayDetector), new BlockItem(lightRelayDetector, props));
		register(r, Registry.BLOCK.getKey(lightRelayFork), new BlockItem(lightRelayFork, props));
		register(r, Registry.BLOCK.getKey(lightRelayToggle), new BlockItem(lightRelayToggle, props));
		register(r, Registry.BLOCK.getKey(lightLauncher), new BlockItem(lightLauncher, props));
		register(r, Registry.BLOCK.getKey(manaBomb), new BlockItem(manaBomb, props));
		register(r, Registry.BLOCK.getKey(cacophonium), new BlockItem(cacophonium, props));
		register(r, Registry.BLOCK.getKey(bifrostPerm), new BlockItem(bifrostPerm, props));
		register(r, Registry.BLOCK.getKey(cellBlock), new BlockItem(cellBlock, props));
		register(r, Registry.BLOCK.getKey(redStringInterceptor), new BlockItem(redStringInterceptor, props));
		register(r, Registry.BLOCK.getKey(corporeaRetainer), new BlockItem(corporeaRetainer, props));
		register(r, Registry.BLOCK.getKey(shimmerrock), new BlockItem(shimmerrock, props));
		register(r, Registry.BLOCK.getKey(shimmerwoodPlanks), new BlockItem(shimmerwoodPlanks, props));
		register(r, Registry.BLOCK.getKey(dryGrass), new BlockItem(dryGrass, props));
		register(r, Registry.BLOCK.getKey(goldenGrass), new BlockItem(goldenGrass, props));
		register(r, Registry.BLOCK.getKey(vividGrass), new BlockItem(vividGrass, props));
		register(r, Registry.BLOCK.getKey(scorchedGrass), new BlockItem(scorchedGrass, props));
		register(r, Registry.BLOCK.getKey(infusedGrass), new BlockItem(infusedGrass, props));
		register(r, Registry.BLOCK.getKey(mutatedGrass), new BlockItem(mutatedGrass, props));
		register(r, Registry.BLOCK.getKey(animatedTorch), new BlockItem(animatedTorch, props));
		register(r, Registry.BLOCK.getKey(corporeaCrystalCube), new BlockItem(corporeaCrystalCube, props));

		DistExecutor.runForDist(() -> () -> registerWithTEISRS(r), () -> () -> registerWithoutTEISRS(r));
	}

	// Yay side-safety -.-

	@OnlyIn(Dist.DEDICATED_SERVER)
	private static Void registerWithoutTEISRS(IForgeRegistry<Item> r) {
		Item.Properties props = ModItems.defaultBuilder();
		register(r, Registry.BLOCK.getKey(manaPylon), new BlockItem(manaPylon, props));
		register(r, Registry.BLOCK.getKey(naturaPylon), new BlockItem(naturaPylon, props));
		register(r, Registry.BLOCK.getKey(gaiaPylon), new BlockItem(gaiaPylon, props));

		register(r, Registry.BLOCK.getKey(teruTeruBozu), new BlockItem(teruTeruBozu, props));
		register(r, Registry.BLOCK.getKey(avatar), new BlockItem(avatar, props));
		register(r, Registry.BLOCK.getKey(bellows), new BlockItem(bellows, props));
		register(r, Registry.BLOCK.getKey(brewery), new BlockItem(brewery, props));
		register(r, Registry.BLOCK.getKey(corporeaIndex), new BlockItem(corporeaIndex, props));
		register(r, Registry.BLOCK.getKey(hourglass), new BlockItem(hourglass, props));
		Item head = new ItemGaiaHead(gaiaHead, gaiaHeadWall, ModItems.defaultBuilder().rarity(Rarity.UNCOMMON));
		register(r, Registry.BLOCK.getKey(gaiaHead), head);
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	private static Void registerWithTEISRS(IForgeRegistry<Item> r) {
		Item.Properties pylonProps = ModItems.defaultBuilder().setISTER(() -> () -> new RenderTilePylon.TEISR());
		register(r, Registry.BLOCK.getKey(manaPylon), new BlockItem(manaPylon, pylonProps));
		register(r, Registry.BLOCK.getKey(naturaPylon), new BlockItem(naturaPylon, pylonProps));
		register(r, Registry.BLOCK.getKey(gaiaPylon), new BlockItem(gaiaPylon, pylonProps));

		register(r, Registry.BLOCK.getKey(teruTeruBozu), new BlockItem(teruTeruBozu, ModItems.defaultBuilder().setISTER(() -> () -> new TEISR(teruTeruBozu))));
		register(r, Registry.BLOCK.getKey(avatar), new BlockItem(avatar, ModItems.defaultBuilder().setISTER(() -> () -> new TEISR(avatar))));
		register(r, Registry.BLOCK.getKey(bellows), new BlockItem(bellows, ModItems.defaultBuilder().setISTER(() -> () -> new TEISR(bellows))));
		register(r, Registry.BLOCK.getKey(brewery), new BlockItem(brewery, ModItems.defaultBuilder().setISTER(() -> () -> new TEISR(brewery))));
		register(r, Registry.BLOCK.getKey(corporeaIndex), new BlockItem(corporeaIndex, ModItems.defaultBuilder().setISTER(() -> () -> new TEISR(corporeaIndex))));
		register(r, Registry.BLOCK.getKey(hourglass), new BlockItem(hourglass, ModItems.defaultBuilder().setISTER(() -> () -> new TEISR(hourglass))));
		Item head = new ItemGaiaHead(gaiaHead, gaiaHeadWall, ModItems.defaultBuilder().rarity(Rarity.UNCOMMON).setISTER(() -> () -> new TEISR(gaiaHead)));
		register(r, Registry.BLOCK.getKey(gaiaHead), head);
		return null;
	}

	public static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, ResourceLocation name, IForgeRegistryEntry<V> thing) {
		reg.register(thing.setRegistryName(name));
	}

	public static <V extends IForgeRegistryEntry<V>> void register(IForgeRegistry<V> reg, String name, IForgeRegistryEntry<V> thing) {
		register(reg, new ResourceLocation(LibMisc.MOD_ID, name), thing);
	}

	public static void addDispenserBehaviours() {
		DispenserBlock.registerDispenseBehavior(ModItems.twigWand, new BehaviourWand());
		DispenserBlock.registerDispenseBehavior(ModItems.poolMinecart, new BehaviourPoolMinecart());
		DispenserBlock.registerDispenseBehavior(ModBlocks.felPumpkin, new BehaviourFelPumpkin());

		SeedBehaviours.init();
	}

	public static Block getFlower(DyeColor color) {
		switch (color) {
		default:
		case WHITE:
			return whiteFlower;
		case ORANGE:
			return orangeFlower;
		case MAGENTA:
			return magentaFlower;
		case LIGHT_BLUE:
			return lightBlueFlower;
		case YELLOW:
			return yellowFlower;
		case LIME:
			return limeFlower;
		case PINK:
			return pinkFlower;
		case GRAY:
			return grayFlower;
		case LIGHT_GRAY:
			return lightGrayFlower;
		case CYAN:
			return cyanFlower;
		case PURPLE:
			return purpleFlower;
		case BLUE:
			return blueFlower;
		case BROWN:
			return brownFlower;
		case GREEN:
			return greenFlower;
		case RED:
			return redFlower;
		case BLACK:
			return blackFlower;
		}
	}

	public static Block getMushroom(DyeColor color) {
		switch (color) {
		default:
		case WHITE:
			return whiteMushroom;
		case ORANGE:
			return orangeMushroom;
		case MAGENTA:
			return magentaMushroom;
		case LIGHT_BLUE:
			return lightBlueMushroom;
		case YELLOW:
			return yellowMushroom;
		case LIME:
			return limeMushroom;
		case PINK:
			return pinkMushroom;
		case GRAY:
			return grayMushroom;
		case LIGHT_GRAY:
			return lightGrayMushroom;
		case CYAN:
			return cyanMushroom;
		case PURPLE:
			return purpleMushroom;
		case BLUE:
			return blueMushroom;
		case BROWN:
			return brownMushroom;
		case GREEN:
			return greenMushroom;
		case RED:
			return redMushroom;
		case BLACK:
			return blackMushroom;
		}
	}

	public static Block getBuriedPetal(DyeColor color) {
		switch (color) {
		default:
		case WHITE:
			return whiteBuriedPetals;
		case ORANGE:
			return orangeBuriedPetals;
		case MAGENTA:
			return magentaBuriedPetals;
		case LIGHT_BLUE:
			return lightBlueBuriedPetals;
		case YELLOW:
			return yellowBuriedPetals;
		case LIME:
			return limeBuriedPetals;
		case PINK:
			return pinkBuriedPetals;
		case GRAY:
			return grayBuriedPetals;
		case LIGHT_GRAY:
			return lightGrayBuriedPetals;
		case CYAN:
			return cyanBuriedPetals;
		case PURPLE:
			return purpleBuriedPetals;
		case BLUE:
			return blueBuriedPetals;
		case BROWN:
			return brownBuriedPetals;
		case GREEN:
			return greenBuriedPetals;
		case RED:
			return redBuriedPetals;
		case BLACK:
			return blackBuriedPetals;
		}
	}

	public static Block getShinyFlower(DyeColor color) {
		switch (color) {
		default:
		case WHITE:
			return whiteShinyFlower;
		case ORANGE:
			return orangeShinyFlower;
		case MAGENTA:
			return magentaShinyFlower;
		case LIGHT_BLUE:
			return lightBlueShinyFlower;
		case YELLOW:
			return yellowShinyFlower;
		case LIME:
			return limeShinyFlower;
		case PINK:
			return pinkShinyFlower;
		case GRAY:
			return grayShinyFlower;
		case LIGHT_GRAY:
			return lightGrayShinyFlower;
		case CYAN:
			return cyanShinyFlower;
		case PURPLE:
			return purpleShinyFlower;
		case BLUE:
			return blueShinyFlower;
		case BROWN:
			return brownShinyFlower;
		case GREEN:
			return greenShinyFlower;
		case RED:
			return redShinyFlower;
		case BLACK:
			return blackShinyFlower;
		}
	}

	public static Block getFloatingFlower(DyeColor color) {
		switch (color) {
		default:
		case WHITE:
			return whiteFloatingFlower;
		case ORANGE:
			return orangeFloatingFlower;
		case MAGENTA:
			return magentaFloatingFlower;
		case LIGHT_BLUE:
			return lightBlueFloatingFlower;
		case YELLOW:
			return yellowFloatingFlower;
		case LIME:
			return limeFloatingFlower;
		case PINK:
			return pinkFloatingFlower;
		case GRAY:
			return grayFloatingFlower;
		case LIGHT_GRAY:
			return lightGrayFloatingFlower;
		case CYAN:
			return cyanFloatingFlower;
		case PURPLE:
			return purpleFloatingFlower;
		case BLUE:
			return blueFloatingFlower;
		case BROWN:
			return brownFloatingFlower;
		case GREEN:
			return greenFloatingFlower;
		case RED:
			return redFloatingFlower;
		case BLACK:
			return blackFloatingFlower;
		}
	}

	public static Block getDoubleFlower(DyeColor color) {
		switch (color) {
		default:
		case WHITE:
			return doubleFlowerWhite;
		case ORANGE:
			return doubleFlowerOrange;
		case MAGENTA:
			return doubleFlowerMagenta;
		case LIGHT_BLUE:
			return doubleFlowerLightBlue;
		case YELLOW:
			return doubleFlowerYellow;
		case LIME:
			return doubleFlowerLime;
		case PINK:
			return doubleFlowerPink;
		case GRAY:
			return doubleFlowerGray;
		case LIGHT_GRAY:
			return doubleFlowerLightGray;
		case CYAN:
			return doubleFlowerCyan;
		case PURPLE:
			return doubleFlowerPurple;
		case BLUE:
			return doubleFlowerBlue;
		case BROWN:
			return doubleFlowerBrown;
		case GREEN:
			return doubleFlowerGreen;
		case RED:
			return doubleFlowerRed;
		case BLACK:
			return doubleFlowerBlack;
		}
	}

	public static Block getPetalBlock(DyeColor color) {
		switch (color) {
		default:
		case WHITE:
			return petalBlockWhite;
		case ORANGE:
			return petalBlockOrange;
		case MAGENTA:
			return petalBlockMagenta;
		case LIGHT_BLUE:
			return petalBlockLightBlue;
		case YELLOW:
			return petalBlockYellow;
		case LIME:
			return petalBlockLime;
		case PINK:
			return petalBlockPink;
		case GRAY:
			return petalBlockGray;
		case LIGHT_GRAY:
			return petalBlockSilver;
		case CYAN:
			return petalBlockCyan;
		case PURPLE:
			return petalBlockPurple;
		case BLUE:
			return petalBlockBlue;
		case BROWN:
			return petalBlockBrown;
		case GREEN:
			return petalBlockGreen;
		case RED:
			return petalBlockRed;
		case BLACK:
			return petalBlockBlack;
		}
	}
}
