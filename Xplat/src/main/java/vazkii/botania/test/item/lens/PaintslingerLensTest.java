package vazkii.botania.test.item.lens;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;

import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.test.TestingUtil;

public class PaintslingerLensTest {

	@GameTest(template = "botania:item/lens/paintslinger_lens")
	public void testPainting(GameTestHelper helper) {
		BlockPos spreaderPos = new BlockPos(2, 5, 2);
		BlockPos buttonPos = new BlockPos(2, 5, 3);
		BlockPos bindPos = new BlockPos(2, 2, 2);
		Player player = helper.makeMockPlayer(GameType.CREATIVE);

		var spreader = TestingUtil.assertBlockEntity(helper, spreaderPos, BotaniaBlockEntities.SPREADER);
		TestingUtil.assertThat(spreader.bindTo(player, new ItemStack(BotaniaItems.twigWand), helper.absolutePos(bindPos), Direction.UP),
				() -> "Failed to bind spreader");

		helper.pressButton(buttonPos);
		helper.startSequence()
				.thenWaitUntil(() -> helper.assertBlockPresent(Blocks.BLUE_CANDLE, bindPos))
				.thenExecute(() -> {
					// check candle colors and properties
					for (BlockPos pos : BlockPos.betweenClosed(1, 2, 1, 3, 4, 3)) {
						helper.assertBlockPresent(Blocks.BLUE_CANDLE, pos);
						boolean expectedLit = pos.getY() == 4;
						boolean expectedWaterlogged = pos.getY() == 2;
						int expectedCandles = pos.getZ() + 1;
						helper.assertBlockProperty(pos, CandleBlock.LIT, expectedLit);
						helper.assertBlockProperty(pos, CandleBlock.WATERLOGGED, expectedWaterlogged);
						helper.assertBlockProperty(pos, CandleBlock.CANDLES, expectedCandles);
					}
					// ensure terracotta was not painted
					for (int x = 0; x < 5; x++) {
						for (int z = 0; z < 5; z++) {
							int y;
							if (x == 0 || x == 4 || z == 0 || z == 4) {
								if ((x == 0 || x == 4) && (z == 0 || z == 4)) {
									// skip corners
									continue;
								}
								y = 2;
							} else {
								y = 1;
							}
							helper.assertBlockPresent(Blocks.TERRACOTTA, new BlockPos(x, y, z));
						}
					}
				})
				.thenSucceed();
	}
}
