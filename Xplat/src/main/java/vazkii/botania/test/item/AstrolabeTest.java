package vazkii.botania.test.item;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.common.item.AstrolabeItem;
import vazkii.botania.common.item.BlackHoleTalismanItem;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.ManaTabletItem;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.rod.DepthsRodItem;
import vazkii.botania.test.TestingUtil;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.Objects;

public class AstrolabeTest {
	//region Tests with candles
	private static final String TEMPLATE_CANDLES = "botania:item/astrolabe_candles";
	private static final Block BLOCK_CANDLES = Blocks.YELLOW_CANDLE;
	private static final BlockPos POS_WATER_SLAB = new BlockPos(2, 2, 3);
	private static final BlockPos POS_SEAGRASS = new BlockPos(2, 2, 1);
	private static final BlockPos POS_SUPPORTED_CANDLE = new BlockPos(2, 4, 1);
	private static final BlockPos POS_FLOATING_CANDLE = new BlockPos(2, 4, 3);
	private static final Vec3 POS_PLAYER_CANDLES = new Vec3(0.9, 3.0, 2.5);
	private static final Vec3 LOOK_TARGET_CANDLES_HORIZONTAL = new Vec3(2.9, 2.0, 2.5);
	private static final Vec3 LOOK_TARGET_CANDLES_VERTICAL = new Vec3(3.5, 2.0, 2.5);
	private static final int EXPECTED_REMAINING_CANDLES_HORIZONTAL_3X3 = 56;
	private static final int EXPECTED_REMAINING_CANDLES_VERTICAL_3X3 = 60;
	private static final int EXPECTED_REMAINING_CANDLES_VERTICAL_5X5 = 58;
	private static final int SLOT_FIRST_PROVIDER = 2;
	private static final int SLOT_SECOND_PROVIDER = 3;

	@GameTest(template = TEMPLATE_CANDLES)
	public void testCandlesHorizontal3x3(final GameTestHelper helper) {
		final Player player = mockPlayerWithAstrolabe(helper, POS_PLAYER_CANDLES, LOOK_TARGET_CANDLES_HORIZONTAL,
				ManaTabletItem.DEFAULT_MAX_MANA);
		player.addItem(new ItemStack(BLOCK_CANDLES.asItem(), 64));
		final ItemStack stack = getAstrolabeForBlockType(player, BLOCK_CANDLES);

		useAstrolabe(player, stack, InteractionHand.MAIN_HAND);
		checkWaterSlabAndSeaGrass(helper);

		helper.assertBlockState(POS_WATER_SLAB.above(), BlockBehaviour.BlockStateBase::isAir,
				() -> "Expected air above water slab, but found " + helper.getBlockState(POS_WATER_SLAB.above()));
		helper.assertBlockState(POS_SEAGRASS.above(), state -> state.is(Blocks.SMOOTH_STONE_SLAB),
				() -> "Expected slab above sea grass, but found " + helper.getBlockState(POS_SEAGRASS.above()));

		for (var pos : BlockPos.betweenClosed(1, 2, 1, 3, 2, 3)) {
			if (!pos.equals(POS_WATER_SLAB)) {
				helper.assertBlockState(pos, block -> block.is(BLOCK_CANDLES),
						() -> "Missing candle, found " + helper.getBlockState(pos));
				helper.assertBlockProperty(pos, CandleBlock.WATERLOGGED, true);
			}
		}

		checkRemainingCandles(player, EXPECTED_REMAINING_CANDLES_HORIZONTAL_3X3, SLOT_FIRST_PROVIDER);
		checkRemainingMana(player, ManaTabletItem.DEFAULT_MAX_MANA - 3 * AstrolabeItem.BASE_COST);

		helper.succeed();
	}

	@GameTest(template = TEMPLATE_CANDLES)
	public void testCandlesHorizontal3x3Offhand(final GameTestHelper helper) {
		final Player player = mockPlayerWithAstrolabe(helper, POS_PLAYER_CANDLES, LOOK_TARGET_CANDLES_HORIZONTAL,
				ManaTabletItem.DEFAULT_MAX_MANA);
		player.addItem(new ItemStack(BLOCK_CANDLES.asItem(), 64));
		final ItemStack stack = getAstrolabeForBlockType(player, BLOCK_CANDLES);
		player.setItemInHand(InteractionHand.OFF_HAND, stack);
		player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

		useAstrolabe(player, stack, InteractionHand.OFF_HAND);

		checkRemainingCandles(player, EXPECTED_REMAINING_CANDLES_HORIZONTAL_3X3, SLOT_FIRST_PROVIDER);
		checkRemainingMana(player, ManaTabletItem.DEFAULT_MAX_MANA - 3 * AstrolabeItem.BASE_COST);

		helper.succeed();
	}

	@GameTest(template = TEMPLATE_CANDLES)
	public void testCandlesHorizontal3x3MultipleSourcesTalismanLast(final GameTestHelper helper) {
		final Player player = mockPlayerWithAstrolabe(helper, POS_PLAYER_CANDLES, LOOK_TARGET_CANDLES_HORIZONTAL,
				ManaTabletItem.DEFAULT_MAX_MANA);
		player.addItem(new ItemStack(BLOCK_CANDLES.asItem(), 5));
		final ItemStack blackHoleTalisman = new ItemStack(BotaniaItems.blackHoleTalisman);
		BlackHoleTalismanItem.setBlock(blackHoleTalisman, BLOCK_CANDLES);
		BlackHoleTalismanItem.setCount(blackHoleTalisman, 5);
		player.addItem(blackHoleTalisman);

		final ItemStack stack = getAstrolabeForBlockType(player, BLOCK_CANDLES);

		useAstrolabe(player, stack, InteractionHand.MAIN_HAND);

		checkRemainingCandles(player, 0, SLOT_FIRST_PROVIDER);
		checkRemainingTalismanCandles(player, 2, SLOT_SECOND_PROVIDER);
		checkRemainingMana(player, ManaTabletItem.DEFAULT_MAX_MANA - 3 * AstrolabeItem.BASE_COST);

		helper.succeed();
	}

	@GameTest(template = TEMPLATE_CANDLES)
	public void testCandlesHorizontal3x3MultipleSourcesTalismanFirst(final GameTestHelper helper) {
		final Player player = mockPlayerWithAstrolabe(helper, POS_PLAYER_CANDLES, LOOK_TARGET_CANDLES_HORIZONTAL,
				ManaTabletItem.DEFAULT_MAX_MANA);
		final ItemStack blackHoleTalisman = new ItemStack(BotaniaItems.blackHoleTalisman);
		BlackHoleTalismanItem.setBlock(blackHoleTalisman, BLOCK_CANDLES);
		BlackHoleTalismanItem.setCount(blackHoleTalisman, 5);
		player.addItem(blackHoleTalisman);
		player.addItem(new ItemStack(BLOCK_CANDLES.asItem(), 5));

		final ItemStack stack = getAstrolabeForBlockType(player, BLOCK_CANDLES);

		useAstrolabe(player, stack, InteractionHand.MAIN_HAND);

		checkRemainingCandles(player, 2, SLOT_SECOND_PROVIDER);
		checkRemainingTalismanCandles(player, 0, SLOT_FIRST_PROVIDER);
		checkRemainingMana(player, ManaTabletItem.DEFAULT_MAX_MANA - 3 * AstrolabeItem.BASE_COST);

		helper.succeed();
	}

	@GameTest(template = TEMPLATE_CANDLES)
	public void testCandlesVertical3x3(final GameTestHelper helper) {
		final Player player = mockPlayerWithAstrolabe(helper, POS_PLAYER_CANDLES, LOOK_TARGET_CANDLES_VERTICAL,
				ManaTabletItem.DEFAULT_MAX_MANA);
		player.addItem(new ItemStack(BLOCK_CANDLES.asItem(), 64));
		final ItemStack stack = getAstrolabeForBlockType(player, BLOCK_CANDLES);

		useAstrolabe(player, stack, InteractionHand.MAIN_HAND);
		checkThreeByThreeVerticalCandleArea(helper);

		checkRemainingCandles(player, EXPECTED_REMAINING_CANDLES_VERTICAL_3X3, SLOT_FIRST_PROVIDER);
		checkRemainingMana(player, ManaTabletItem.DEFAULT_MAX_MANA - 3 * AstrolabeItem.BASE_COST);

		helper.succeed();
	}

	@GameTest(template = TEMPLATE_CANDLES)
	public void testCandlesVertical5x5(final GameTestHelper helper) {
		final Player player = mockPlayerWithAstrolabe(helper, POS_PLAYER_CANDLES, LOOK_TARGET_CANDLES_VERTICAL,
				ManaTabletItem.DEFAULT_MAX_MANA);
		player.addItem(new ItemStack(BLOCK_CANDLES.asItem(), 64));
		final ItemStack stack = getAstrolabeForBlockType(player, BLOCK_CANDLES);
		AstrolabeItem.setSize(stack, 5);

		useAstrolabe(player, stack, InteractionHand.MAIN_HAND);
		checkThreeByThreeVerticalCandleArea(helper);

		helper.assertBlockState(POS_WATER_SLAB.above().south(), block -> block.is(BLOCK_CANDLES),
				() -> "Expected candle on rim next to water slab, but found "
						+ helper.getBlockState(POS_WATER_SLAB.above().south()));
		helper.assertBlockState(POS_SEAGRASS.above().north(), block -> block.is(BLOCK_CANDLES),
				() -> "Expected candle on rim next to seagrass, but found "
						+ helper.getBlockState(POS_WATER_SLAB.above().north()));

		checkRemainingCandles(player, EXPECTED_REMAINING_CANDLES_VERTICAL_5X5, SLOT_FIRST_PROVIDER);
		checkRemainingMana(player, ManaTabletItem.DEFAULT_MAX_MANA - 5 * AstrolabeItem.BASE_COST);

		helper.succeed();
	}

	private void checkRemainingCandles(final Player player, final int expectedRemaining, final int candlesSlot) {
		final ItemStack remainingCandles = player.getInventory().getItem(candlesSlot);
		TestingUtil.assertThat(expectedRemaining == 0 || remainingCandles.is(Items.YELLOW_CANDLE),
				() -> "Unexpected item in candles slot: " + remainingCandles);
		TestingUtil.assertEquals(expectedRemaining, remainingCandles.getCount(),
				() -> String.format("Incorrect number of remaining candles in inventory, expected %d, found %d",
						expectedRemaining, remainingCandles.getCount()));
	}

	private void checkRemainingTalismanCandles(final Player player, final int expectedRemaining, final int talismanSlot) {
		final ItemStack remainingTalisman = player.getInventory().getItem(talismanSlot);
		TestingUtil.assertThat(expectedRemaining == 0 || remainingTalisman.is(BotaniaItems.blackHoleTalisman),
				() -> "Unexpected item in talisman slot: " + remainingTalisman);
		final int count = BlackHoleTalismanItem.getBlockCount(remainingTalisman);
		TestingUtil.assertEquals(expectedRemaining, count,
				() -> String.format("Incorrect number of remaining candles in talisman, expected %d, found %d",
						expectedRemaining, count));
	}

	private void checkThreeByThreeVerticalCandleArea(final GameTestHelper helper) {
		checkWaterSlabAndSeaGrass(helper);

		helper.assertBlockState(POS_WATER_SLAB.above(), block -> block.is(BLOCK_CANDLES),
				() -> "Expected candle on water slab, but found " + helper.getBlockState(POS_WATER_SLAB.above()));
		helper.assertBlockProperty(POS_WATER_SLAB.above(), CandleBlock.WATERLOGGED, false);
		helper.assertBlockState(POS_SEAGRASS.above(), state -> state.is(Blocks.SMOOTH_STONE_SLAB),
				() -> "Expected slab above sea grass, but found " + helper.getBlockState(POS_SEAGRASS.above()));

		helper.assertBlockState(POS_FLOATING_CANDLE, block -> block.is(BLOCK_CANDLES),
				() -> "Floating candle was replaced with " + helper.getBlockState(POS_FLOATING_CANDLE));
		helper.assertBlockProperty(POS_FLOATING_CANDLE, CandleBlock.CANDLES, 1);
		helper.assertBlockProperty(POS_FLOATING_CANDLE, CandleBlock.LIT, true);
		helper.assertBlockProperty(POS_FLOATING_CANDLE, CandleBlock.WATERLOGGED, false);

		helper.assertBlockState(POS_SUPPORTED_CANDLE, block -> block.is(BLOCK_CANDLES),
				() -> "Supported candle was replaced with " + helper.getBlockState(POS_FLOATING_CANDLE));
		helper.assertBlockProperty(POS_SUPPORTED_CANDLE, CandleBlock.CANDLES, 2);
		helper.assertBlockProperty(POS_SUPPORTED_CANDLE, CandleBlock.LIT, true);
		helper.assertBlockProperty(POS_SUPPORTED_CANDLE, CandleBlock.WATERLOGGED, false);

		for (var pos : BlockPos.betweenClosed(1, 2, 1, 3, 2, 3)) {
			if (pos.getX() != 2) {
				helper.assertBlockState(pos, block -> block.is(Blocks.WATER),
						() -> "Water was replaced with " + helper.getBlockState(pos));
			} else if (!pos.equals(POS_WATER_SLAB)) {
				helper.assertBlockState(pos, block -> block.is(BLOCK_CANDLES),
						() -> "Missing candle, found " + helper.getBlockState(pos));
				helper.assertBlockProperty(pos, CandleBlock.WATERLOGGED, true);
			}
		}
	}

	private void checkWaterSlabAndSeaGrass(final GameTestHelper helper) {
		helper.assertBlockState(POS_WATER_SLAB, block -> block.is(Blocks.SMOOTH_STONE_SLAB),
				() -> "Slab was replaced with " + helper.getBlockState(POS_WATER_SLAB));
		helper.assertBlockState(POS_SEAGRASS, block -> !block.is(Blocks.SEAGRASS), () -> "Seagrass was not replaced");
	}
	//endregion

	//region Tests with directional blocks
	private static final String TEMPLATE_DIRECTIONAL = "botania:item/astrolabe_directional";
	private static final Block BLOCK_LOG = Blocks.OAK_LOG;
	private static final Block BLOCK_PISTON = Blocks.PISTON;
	private static final Vec3 POS_PLAYER_DIRECTIONAL = new Vec3(1.0, 2.0, 0.5);
	private static final Vec3 LOOK_TARGET_DIRECTIONAL_X = new Vec3(2.5, 2.0, 3.0);
	private static final Vec3 LOOK_TARGET_DIRECTIONAL_Y = new Vec3(2.1, 2.0, 2.5);
	private static final Vec3 LOOK_TARGET_DIRECTIONAL_Z = new Vec3(2.5, 2.0, 2.5);

	// Logs are placed according to targeted surface orientation
	@GameTest(template = TEMPLATE_DIRECTIONAL)
	public void testDirectionalLogsAxisX(GameTestHelper helper) {
		testDirectionalBlockWithProperty(helper, LOOK_TARGET_DIRECTIONAL_X,
				BLOCK_LOG, BlockStateProperties.AXIS, Direction.Axis.X);
	}

	@GameTest(template = TEMPLATE_DIRECTIONAL)
	public void testDirectionalLogsAxisY(GameTestHelper helper) {
		testDirectionalBlockWithProperty(helper, LOOK_TARGET_DIRECTIONAL_Y,
				BLOCK_LOG, BlockStateProperties.AXIS, Direction.Axis.Y);
	}

	@GameTest(template = TEMPLATE_DIRECTIONAL)
	public void testDirectionalLogsAxisZ(GameTestHelper helper) {
		testDirectionalBlockWithProperty(helper, LOOK_TARGET_DIRECTIONAL_Z,
				BLOCK_LOG, BlockStateProperties.AXIS, Direction.Axis.Z);
	}

	// Pistons are placed according to player look direction
	@GameTest(template = TEMPLATE_DIRECTIONAL)
	public void testDirectionalPistonAxisX(GameTestHelper helper) {
		testDirectionalBlockWithProperty(helper, LOOK_TARGET_DIRECTIONAL_X,
				BLOCK_PISTON, BlockStateProperties.FACING, Direction.NORTH);
	}

	@GameTest(template = TEMPLATE_DIRECTIONAL)
	public void testDirectionalPistonAxisY(GameTestHelper helper) {
		testDirectionalBlockWithProperty(helper, LOOK_TARGET_DIRECTIONAL_Y,
				BLOCK_PISTON, BlockStateProperties.FACING, Direction.NORTH);
	}

	@GameTest(template = TEMPLATE_DIRECTIONAL)
	public void testDirectionalPistonAxisZ(GameTestHelper helper) {
		testDirectionalBlockWithProperty(helper, LOOK_TARGET_DIRECTIONAL_Z,
				BLOCK_PISTON, BlockStateProperties.FACING, Direction.NORTH);
	}

	private <P extends Property<V>, V extends Comparable<V>> void testDirectionalBlockWithProperty(
			GameTestHelper helper, Vec3 lookTarget, Block block, P property, V value) {
		final Player player = mockPlayerWithAstrolabe(helper, POS_PLAYER_DIRECTIONAL, lookTarget,
				ManaTabletItem.DEFAULT_MAX_MANA);
		player.addItem(new ItemStack(block.asItem(), 64));
		final ItemStack stack = getAstrolabeForBlockType(player, block);

		useAstrolabe(player, stack, InteractionHand.MAIN_HAND);

		for (BlockPos pos : BlockPos.betweenClosed(1, 2, 2, 3, 4, 2)) {
			helper.assertBlockState(pos, state -> state.is(block),
					() -> String.format("Expected %s but found %s", block, helper.getBlockState(pos).getBlock()));
			helper.assertBlockProperty(pos, property, prop -> prop.equals(value),
					String.format("Expected %s to be %s, but found %s", property.getName(), value,
							helper.getBlockState(pos).getValue(property)));
		}

		helper.succeed();
	}
	//endregion

	// region Mana consumption tests
	private static final String TEMPLATE_MANA = "botania:item/astrolabe_mana";
	private static final Vec3 POS_PLAYER_MANA = new Vec3(6.5, 2.0, 6.5);
	private static final int SIZE_ASTROLABE = 11;
	private static final int COST_ASTROLABE = AstrolabeItem.BASE_COST * SIZE_ASTROLABE;
	private static final int MANA_NOT_ENOUGH = DepthsRodItem.COST * 100;
	private static final int MANA_BARELY_ENOUGH = DepthsRodItem.COST * 121 + COST_ASTROLABE / 2;

	@GameTest(template = TEMPLATE_MANA)
	public void testNotEnoughMana(GameTestHelper helper) {
		// should be too little mana to conjure enough blocks:
		final Player player = mockAstrolabeAndUseWithCobbleRod(helper, MANA_NOT_ENOUGH);

		// should not have attempted to use mana or place any blocks at all:
		checkRemainingMana(player, MANA_NOT_ENOUGH);
		helper.forEveryBlockInStructure(blockPos -> helper.assertBlockNotPresent(Blocks.COBBLESTONE, blockPos));

		helper.succeed();
	}

	@GameTest(template = TEMPLATE_MANA)
	public void testBarelyNotEnoughMana(GameTestHelper helper) {
		// should be enough mana to conjure required blocks, but not enough left to also operate the Astrolabe
		final Player player = mockAstrolabeAndUseWithCobbleRod(helper, MANA_BARELY_ENOUGH);

		// should have consumed almost all mana and placed most, but not all, blocks:
		final int expectedMana = (MANA_BARELY_ENOUGH - COST_ASTROLABE) % DepthsRodItem.COST;
		checkRemainingMana(player, expectedMana);

		final int expectedNumberOfBlocks = (MANA_BARELY_ENOUGH - COST_ASTROLABE) / DepthsRodItem.COST;
		final MutableInt actualNumberOfBlocks = new MutableInt();
		helper.forEveryBlockInStructure(blockPos -> {
			if (helper.getBlockState(blockPos).is(Blocks.COBBLESTONE)) {
				actualNumberOfBlocks.increment();
			}
		});
		TestingUtil.assertEquals(expectedNumberOfBlocks, actualNumberOfBlocks.intValue(),
				() -> String.format("Expected %d placed blocks, found %d",
						expectedNumberOfBlocks, actualNumberOfBlocks.intValue()));

		helper.succeed();
	}

	private Player mockAstrolabeAndUseWithCobbleRod(GameTestHelper helper, int mana) {
		final Player player = mockPlayerWithAstrolabe(helper, POS_PLAYER_MANA, POS_PLAYER_MANA, mana);
		player.addItem(new ItemStack(BotaniaItems.cobbleRod));
		final ItemStack stack = getAstrolabeForBlockType(player, Blocks.COBBLESTONE);
		AstrolabeItem.setSize(stack, SIZE_ASTROLABE);

		useAstrolabe(player, stack, InteractionHand.MAIN_HAND);

		return player;
	}
	//endregion

	private static final int SLOT_TABLET = 1;

	private void useAstrolabe(Player player, ItemStack stack, InteractionHand hand) {
		BlockHitResult hitResult = ToolCommons.raytraceFromEntity(player, 5, true);
		UseOnContext useOnContext = new UseOnContext(player, hand, hitResult);
		stack.useOn(useOnContext);
	}

	private void checkRemainingMana(Player player, int expectedMana) {
		final ItemStack tablet = player.getInventory().getItem(SLOT_TABLET);
		final ManaItem manaItem = XplatAbstractions.INSTANCE.findManaItem(tablet);
		TestingUtil.assertThat(manaItem != null, () -> "Missing mana tablet");
		TestingUtil.assertEquals(expectedMana, manaItem.getMana(),
				() -> String.format("Expected %d remaining mana, but found %d", expectedMana, manaItem.getMana()));
	}

	@NotNull
	private Player mockPlayerWithAstrolabe(final GameTestHelper helper, Vec3 posPlayer, final Vec3 lookTarget, int mana) {
		final Player player = helper.makeMockPlayer(GameType.CREATIVE);
		player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(BotaniaItems.astrolabe));
		final ItemStack tablet = new ItemStack(BotaniaItems.manaTablet);
		Objects.requireNonNull(XplatAbstractions.INSTANCE.findManaItem(tablet)).addMana(mana);
		player.getInventory().add(tablet);
		player.setPos(helper.absoluteVec(posPlayer));
		final Vec3 targetVec = helper.absoluteVec(lookTarget);
		player.lookAt(EntityAnchorArgument.Anchor.EYES, targetVec);
		return player;
	}

	@NotNull
	private ItemStack getAstrolabeForBlockType(final Player player, Block block) {
		final ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
		AstrolabeItem.setBlock(stack, block.defaultBlockState());
		return stack;
	}
}
