package vazkii.botania.forge.mixin;

import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ToolAction;
import net.neoforged.neoforge.common.ToolActions;
import net.neoforged.neoforge.common.extensions.IForgeBlock;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import vazkii.botania.common.block.BotaniaGrassBlock;

// Self-mixin to implement a method which has a forge-only param
@Mixin(BotaniaGrassBlock.class)
public abstract class BotaniaGrassBlockForgeMixin implements IForgeBlock {
	@Override
	public @Nullable BlockState getToolModifiedState(BlockState state, UseOnContext context,
			ToolAction toolAction, boolean simulate) {
		if (toolAction == ToolActions.HOE_TILL && HoeItem.onlyIfAirAbove(context)) {
			return Blocks.FARMLAND.defaultBlockState();
		} else if (toolAction == ToolActions.SHOVEL_FLATTEN) {
			return Blocks.DIRT_PATH.defaultBlockState();
		}
		return null;
	}
}
