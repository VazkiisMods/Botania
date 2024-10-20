/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.mixin.MobAccessor;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class FelPumpkinBlock extends BotaniaBlock {
	private static final ResourceLocation LOOT_TABLE = botaniaRL("fel_blaze");

	public FelPumpkinBlock(Properties builder) {
		super(builder);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING);
	}

	@Override
	public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
		super.onPlace(state, world, pos, oldState, isMoving);

		if (!world.isClientSide && world.getBlockState(pos.below()).is(Blocks.IRON_BARS) && world.getBlockState(pos.below(2)).is(Blocks.IRON_BARS)) {
			world.removeBlock(pos, false);
			world.removeBlock(pos.below(), false);
			world.removeBlock(pos.below(2), false);
			Blaze blaze = EntityType.BLAZE.create(world);
			blaze.moveTo(pos.getX() + 0.5D, pos.getY() - 1.95D, pos.getZ() + 0.5D, 0.0F, 0.0F);
			blaze.setPersistenceRequired();
			((MobAccessor) blaze).setLootTable(LOOT_TABLE);
			blaze.finalizeSpawn((ServerLevelAccessor) world, world.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null, null);
			world.addFreshEntity(blaze);

			for (ServerPlayer player : world.getEntitiesOfClass(ServerPlayer.class, blaze.getBoundingBox().inflate(5.0))) {
				CriteriaTriggers.SUMMONED_ENTITY.trigger(player, blaze);
			}
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
	}

	@NotNull
	@Override
	public BlockState mirror(@NotNull BlockState state, Mirror mirror) {
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, mirror.mirror(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}

	@NotNull
	@Override
	public BlockState rotate(@NotNull BlockState state, Rotation rot) {
		return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rot.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
	}
}
