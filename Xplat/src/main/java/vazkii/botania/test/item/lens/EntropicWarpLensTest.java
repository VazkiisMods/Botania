package vazkii.botania.test.item.lens;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.RedstoneLampBlock;

import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.test.TestingUtil;

public class EntropicWarpLensTest {

	@GameTest(template = "botania:item/lens/entropic_warp_lens")
	public void testWarp(GameTestHelper helper) {
		var spreaderPos = new BlockPos(3, 3, 3);
		var lampPos = new BlockPos(2, 4, 3);
		var buttonPos = new BlockPos(4, 3, 3);
		var relayPos = new BlockPos(3, 2, 3);
		var bindPos = new BlockPos(3, 10, 3);
		var player = helper.makeMockPlayer(GameType.CREATIVE);

		TestingUtil.bindForceRelayTarget(helper, relayPos, bindPos);

		var spreader = TestingUtil.assertBlockEntity(helper, spreaderPos, BotaniaBlockEntities.SPREADER);

		TestingUtil.assertThat(spreader.bindTo(player, new ItemStack(BotaniaItems.twigWand), helper.absolutePos(relayPos), Direction.DOWN),
				() -> "Failed to bind spreader");

		helper.startSequence()
				.thenExecute(() -> helper.pressButton(buttonPos))
				.thenWaitUntil(() -> helper.assertBlockProperty(lampPos, RedstoneLampBlock.LIT, true))
				.thenSucceed();

	}

}
