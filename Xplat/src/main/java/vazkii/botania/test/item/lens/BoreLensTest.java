package vazkii.botania.test.item.lens;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;

import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.test.DelegatingConfigAccess;
import vazkii.botania.test.TestingUtil;
import vazkii.botania.xplat.BotaniaConfig;

public class BoreLensTest {
	private static final String ARENA = "botania:item/lens/lens_mine";
	private static final String WOOD_LEVEL_BATCH = "botania:lens_mine_test_mining_level_0";
	private static final String STONE_LEVEL_BATCH = "botania:lens_mine_test_mining_level_1";
	private static final String IRON_LEVEL_BATCH = "botania:lens_mine_test_mining_level_2";
	private static final String DIAMOND_LEVEL_BATCH = "botania:lens_mine_test_mining_level_3";

	private void mockMiningLevel(int level) {
		BotaniaConfig.setCommon(new DelegatingConfigAccess(BotaniaConfig.common()) {
			@Override
			public int harvestLevelBore() {
				return level;
			}
		});
	}

	private void restoreConfig() {
		BotaniaConfig.setCommon(((DelegatingConfigAccess) BotaniaConfig.common()).getInner());
	}

	@BeforeBatch(batch = WOOD_LEVEL_BATCH)
	public void beforeWoodBatch(ServerLevel level) {
		mockMiningLevel(0);
	}

	@BeforeBatch(batch = STONE_LEVEL_BATCH)
	public void beforeStoneBatch(ServerLevel level) {
		mockMiningLevel(1);
	}

	@BeforeBatch(batch = IRON_LEVEL_BATCH)
	public void beforeIronBatch(ServerLevel level) {
		mockMiningLevel(2);
	}

	@BeforeBatch(batch = DIAMOND_LEVEL_BATCH)
	public void beforeDiamondBatch(ServerLevel level) {
		mockMiningLevel(3);
	}

	@AfterBatch(batch = WOOD_LEVEL_BATCH)
	public void afterWoodBatch(ServerLevel level) {
		restoreConfig();
	}

	@AfterBatch(batch = STONE_LEVEL_BATCH)
	public void afterStoneBatch(ServerLevel level) {
		restoreConfig();
	}

	@AfterBatch(batch = IRON_LEVEL_BATCH)
	public void afterIronBatch(ServerLevel level) {
		restoreConfig();
	}

	@AfterBatch(batch = DIAMOND_LEVEL_BATCH)
	public void afterDiamondBatch(ServerLevel level) {
		restoreConfig();
	}

	private void testMine(GameTestHelper helper, Block toMine, boolean expectSuccess) {
		var spreaderPos = new BlockPos(1, 2, 0);
		var manaVoidPos = new BlockPos(1, 2, 3);
		var lampPos = new BlockPos(2, 2, 2);
		var buttonPos = new BlockPos(2, 2, 0);
		var testPos = new BlockPos(1, 2, 1);
		var player = helper.makeMockPlayer(GameType.CREATIVE);

		// Because Mojang removed the block entity rotation methods, we need to ensure
		// that the spreader is pointed at the mana void properly, in case the test
		// structure is placed rotated.
		var spreader = TestingUtil.assertBlockEntity(helper, spreaderPos, BotaniaBlockEntities.SPREADER);
		TestingUtil.assertThat(spreader.bindTo(player, new ItemStack(BotaniaItems.twigWand), helper.absolutePos(manaVoidPos), Direction.UP),
				() -> "Failed to bind spreader");
		helper.setBlock(testPos, toMine);

		if (expectSuccess) {
			helper.startSequence()
					.thenExecute(() -> helper.pressButton(buttonPos))
					.thenWaitUntil(() -> helper.assertBlockProperty(lampPos, RedstoneLampBlock.LIT, true))
					.thenSucceed();
		} else {
			// todo can we use failIf or something else that doesn't hardcode a tick count?
			helper.startSequence()
					.thenExecute(() -> helper.pressButton(buttonPos))
					.thenExecuteFor(60, () -> helper.assertBlockProperty(lampPos, RedstoneLampBlock.LIT, false))
					.thenSucceed();
		}
	}

	@GameTest(template = ARENA, batch = WOOD_LEVEL_BATCH)
	public void testSnow(GameTestHelper helper) {
		testMine(helper, Blocks.SNOW_BLOCK, true);
	}

	@GameTest(template = ARENA, batch = WOOD_LEVEL_BATCH)
	public void testStone(GameTestHelper helper) {
		testMine(helper, Blocks.STONE, true);
	}

	@GameTest(template = ARENA, batch = WOOD_LEVEL_BATCH)
	public void testLog(GameTestHelper helper) {
		testMine(helper, Blocks.OAK_LOG, true);
	}

	@GameTest(template = ARENA, batch = WOOD_LEVEL_BATCH)
	public void testIronBlockTooLow(GameTestHelper helper) {
		testMine(helper, Blocks.IRON_BLOCK, false);
	}

	@GameTest(template = ARENA, batch = STONE_LEVEL_BATCH)
	public void testIronBlock(GameTestHelper helper) {
		testMine(helper, Blocks.IRON_BLOCK, true);
	}

	@GameTest(template = ARENA, batch = STONE_LEVEL_BATCH)
	public void testGoldOreTooLow(GameTestHelper helper) {
		testMine(helper, Blocks.GOLD_ORE, false);
	}

	@GameTest(template = ARENA, batch = IRON_LEVEL_BATCH)
	public void testGoldOre(GameTestHelper helper) {
		testMine(helper, Blocks.GOLD_ORE, true);
	}

	@GameTest(template = ARENA, batch = IRON_LEVEL_BATCH)
	public void testObsidianTooLow(GameTestHelper helper) {
		testMine(helper, Blocks.OBSIDIAN, false);
	}

	@GameTest(template = ARENA, batch = DIAMOND_LEVEL_BATCH)
	public void testObsidian(GameTestHelper helper) {
		testMine(helper, Blocks.OBSIDIAN, true);
	}
}
