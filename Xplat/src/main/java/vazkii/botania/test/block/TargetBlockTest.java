package vazkii.botania.test.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.RedstoneLampBlock;

import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.test.TestingUtil;

public class TargetBlockTest {
	public static final String ARENA = "botania:block/target_block_trigger";

	@GameTest(template = ARENA)
	public void testFakeBurstNoShooty(GameTestHelper helper) {
		var spreaderPos = new BlockPos(5, 2, 5);
		var lampPos = new BlockPos(3, 3, 5);
		var bindPos = new BlockPos(3, 2, 5);
		var player = helper.makeMockPlayer(GameType.CREATIVE);

		// Because Mojang removed the block entity rotation methods, we need to ensure
		// that the spreader is pointed at the mana void properly, in case the test
		// structure is placed rotated.
		var spreader = TestingUtil.assertBlockEntity(helper, spreaderPos, BotaniaBlockEntities.SPREADER);
		TestingUtil.assertThat(spreader.bindTo(player, new ItemStack(BotaniaItems.twigWand), helper.absolutePos(bindPos), Direction.UP),
				() -> "Failed to bind spreader");

		helper.startSequence()
				.thenExecuteFor(60, () -> helper.assertBlockProperty(lampPos, RedstoneLampBlock.LIT, false))
				.thenSucceed();
	}
}
