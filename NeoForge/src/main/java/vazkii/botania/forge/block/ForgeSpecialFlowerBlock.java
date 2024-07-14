package vazkii.botania.forge.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.block_entity.SpecialFlowerBlockEntity;
import vazkii.botania.common.block.BotaniaBlock;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.FloatingSpecialFlowerBlock;

import java.util.function.Supplier;

// Note: Keep in sync with FabricSpecialFlowerBlock. The only reason they are split is
// because Forge patches the superclass constructor in stupid, incompatible ways.
public class ForgeSpecialFlowerBlock extends FlowerBlock implements EntityBlock {
	private static final VoxelShape SHAPE = box(4.8, 0, 4.8, 12.8, 16, 12.8);
	private final Supplier<BlockEntityType<? extends SpecialFlowerBlockEntity>> blockEntityType;
	private final boolean hasComparatorOutput;

	public ForgeSpecialFlowerBlock(MobEffect stewEffect, int stewDuration, Properties props, Supplier<BlockEntityType<? extends SpecialFlowerBlockEntity>> blockEntityType) {
		this(stewEffect, stewDuration, props, blockEntityType, false);
	}

	public ForgeSpecialFlowerBlock(MobEffect stewEffect, int stewDuration, Properties props, Supplier<BlockEntityType<? extends SpecialFlowerBlockEntity>> blockEntityType, boolean hasComparatorOutput) {
		super(/* the only godforsaken reason why this class needs to be duplicated for each loader
				is so that we can add a "() ->" here. Amazing. */
				() -> stewEffect, stewDuration, props);
		this.blockEntityType = blockEntityType;
		this.hasComparatorOutput = hasComparatorOutput;
	}

	@NotNull
	@Override
	public VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, CollisionContext ctx) {
		Vec3 shift = state.getOffset(world, pos);
		return SHAPE.move(shift.x, shift.y, shift.z);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter worldIn, BlockPos pos) {
		return state.is(BotaniaBlocks.redStringRelay)
				|| super.mayPlaceOn(state, worldIn, pos);
	}

	@Override
	public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int event, int param) {
		super.triggerEvent(state, world, pos, event, param);
		BlockEntity tileentity = world.getBlockEntity(pos);
		return tileentity != null && tileentity.triggerEvent(event, param);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return blockEntityType.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return BotaniaBlock.createTickerHelper(type, blockEntityType.get(), SpecialFlowerBlockEntity::commonTick);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		((SpecialFlowerBlockEntity) level.getBlockEntity(pos)).setPlacedBy(level, pos, state, placer, stack);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
		if (hasComparatorOutput && !newState.hasAnalogOutputSignal()) {
			level.updateNeighbourForOutputSignal(pos, newState.getBlock());
		}
		super.onRemove(state, level, pos, newState, movedByPiston);
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
		FloatingSpecialFlowerBlock.redstoneParticlesIfPowered(state, world, pos, rand);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState bs) {
		return hasComparatorOutput;
	}

	@Override
	public int getAnalogOutputSignal(BlockState bs, Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof SpecialFlowerBlockEntity flower) {
			return flower.getComparatorSignal();
		}
		return 0;
	}

}
