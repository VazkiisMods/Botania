/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block;

import com.google.common.base.Preconditions;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.internal_caps.SpectralRailComponent;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.xplat.XplatAbstractions;

public class SpectralRailBlock extends BaseRailBlock {

	public static final String TAG_FLOAT_TICKS = "botania:float_ticks";

	public SpectralRailBlock(Properties builder) {
		super(true, builder);
		registerDefaultState(defaultBlockState().setValue(BlockStateProperties.RAIL_SHAPE_STRAIGHT, RailShape.NORTH_SOUTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.RAIL_SHAPE_STRAIGHT, WATERLOGGED);
	}

	private void updateFloating(AbstractMinecart cart) {
		cart.level().getProfiler().push("cartFloating");
		SpectralRailComponent persistentData = XplatAbstractions.INSTANCE.ghostRailComponent(cart);
		int floatTicks = persistentData.floatTicks;
		Preconditions.checkState(floatTicks > 0);

		BlockPos entPos = cart.blockPosition();
		BlockState state = cart.level().getBlockState(entPos);
		boolean air = state.isAir();

		if (state.is(BotaniaTags.Blocks.GHOST_RAIL_BARRIER)
				|| (!state.is(BotaniaBlocks.ghostRail) && state.is(BlockTags.RAILS))) {
			cart.level().levelEvent(LevelEvent.PARTICLES_EYE_OF_ENDER_DEATH, entPos, 0);
			persistentData.floatTicks = 0;
		} else {
			BlockPos down = entPos.below();
			BlockState stateBelow = cart.level().getBlockState(down);
			boolean airBelow = stateBelow.isAir();
			if (air && airBelow || !air && !airBelow) {
				cart.noPhysics = true;
			}
			cart.setDeltaMovement(cart.getDeltaMovement().x() * 1.4, 0.2, cart.getDeltaMovement().z() * 1.4);
			persistentData.floatTicks--;
			cart.level().levelEvent(LevelEvent.PARTICLES_SHOOT, entPos, 0);
		}

		cart.level().getProfiler().pop();
	}

	@SoftImplement("IBaseRailBlockExtension")
	public void onMinecartPass(BlockState state, Level world, BlockPos pos, AbstractMinecart cart) {
		if (!world.isClientSide) {
			XplatAbstractions.INSTANCE.ghostRailComponent(cart).floatTicks = 20;
			updateFloating(cart);
		}
	}

	public void tickCart(AbstractMinecart c) {
		if (c.level().isClientSide || c.isRemoved()) {
			return;
		}

		SpectralRailComponent persistentData = XplatAbstractions.INSTANCE.ghostRailComponent(c);
		if (!c.isAlive() || persistentData.floatTicks <= 0) {
			c.noPhysics = false;
			return;
		}

		updateFloating(c);

		if (persistentData.floatTicks <= 0) {
			c.noPhysics = false;
		}
	}

	@NotNull
	@Override
	public Property<RailShape> getShapeProperty() {
		return BlockStateProperties.RAIL_SHAPE_STRAIGHT;
	}
}
