package vazkii.botania.test.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;

import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.test.TestingUtil;

public class ManaSpreaderTest {
	private static final String[] SPREADERS = { "Pulse Spreader", "Mana Spreader", "Elven Spreader", "Gaia Spreader" };
	private static final int NUM_SPREADERS = 4;
	private static final BlockPos SPREADER_POS = new BlockPos(2, 1, 1);
	private static final BlockPos POOL_POS = new BlockPos(3, 1, 1);
	private static final BlockPos SINK_POS = new BlockPos(4, 1, 1);
	private static final BlockPos LEVER_POS = new BlockPos(1, 2, 2);

	@GameTest(template = "botania:block/spreader_pool_default_aim")
	public void testSpreaderDefaultAimHittingPool(GameTestHelper helper) {
		var player = helper.makeMockPlayer(GameType.CREATIVE);

		// point the spreaders at their corresponding sink block to aim them exactly horizontally
		for (int i = 0; i < NUM_SPREADERS; i++) {
			var spreaderName = SPREADERS[i];
			var pool = TestingUtil.assertBlockEntity(helper, POOL_POS.south(i), BotaniaBlockEntities.POOL);
			TestingUtil.assertEquals(pool.getCurrentMana(), 0, () -> String.format("Pool for %s does not start empty", spreaderName));
			var spreader = TestingUtil.assertBlockEntity(helper, SPREADER_POS.south(i), BotaniaBlockEntities.SPREADER);
			var bindPos = SINK_POS.south(i);
			TestingUtil.assertThat(spreader.bindTo(player, new ItemStack(BotaniaItems.twigWand), helper.absolutePos(bindPos), Direction.UP),
					() -> "Failed to bind spreader");
		}

		helper.startSequence()
				.thenExecute(() -> helper.pullLever(LEVER_POS))
				.thenExecuteAfter(10, () -> {
					for (int i = 0; i < NUM_SPREADERS; i++) {
						var spreaderName = SPREADERS[i];
						var pool = TestingUtil.assertBlockEntity(helper, POOL_POS.south(i), BotaniaBlockEntities.POOL);
						var sink = TestingUtil.assertBlockEntity(helper, SINK_POS.south(i), BotaniaBlockEntities.FLUXFIELD);
						TestingUtil.assertThat(pool.getCurrentMana() > 0, () -> String.format("Pool for %s did not receive mana", spreaderName));
						TestingUtil.assertEquals(sink.getCurrentMana(), 0, () -> String.format("Sink for %s should not have received", spreaderName));
					}
				})
				.thenSucceed();
	}
}
