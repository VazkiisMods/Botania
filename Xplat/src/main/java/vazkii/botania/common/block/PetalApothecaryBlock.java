/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.block.PetalApothecary;
import vazkii.botania.api.block.PetalApothecary.State;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.PetalApothecaryBlockEntity;
import vazkii.botania.common.block.block_entity.SimpleInventoryBlockEntity;
import vazkii.botania.common.helper.InventoryHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.rod.SeasRodItem;
import vazkii.botania.xplat.XplatAbstractions;

public class PetalApothecaryBlock extends BotaniaBlock implements EntityBlock {

	public static final EnumProperty<State> FLUID = EnumProperty.create("fluid", State.class);
	private static final VoxelShape BASE = Block.box(2, 0, 2, 14, 2, 14);
	private static final VoxelShape PILLAR = Block.box(4, 2, 4, 12, 11, 12);
	private static final VoxelShape TOP = Block.box(2, 11, 2, 14, 16, 14);
	private static final VoxelShape TOP_CUTOUT = Block.box(3, 12, 3, 13, 16, 13);
	private static final VoxelShape SHAPE_INTERACT = Shapes.or(Shapes.or(BASE, PILLAR), TOP);
	private static final VoxelShape SHAPE = Shapes.join(SHAPE_INTERACT, TOP_CUTOUT, BooleanOp.ONLY_FIRST);

	public enum Variant {
		DEFAULT,
		FOREST,
		PLAINS,
		MOUNTAIN,
		FUNGAL,
		SWAMP,
		DESERT,
		TAIGA,
		MESA,
		MOSSY,
		LIVINGROCK,
		DEEPSLATE
	}

	public final Variant variant;

	protected PetalApothecaryBlock(Variant v, BlockBehaviour.Properties builder) {
		super(builder);
		this.variant = v;
		registerDefaultState(defaultBlockState().setValue(FLUID, State.EMPTY));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FLUID);
	}

	@NotNull
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
	}

	@NotNull
	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return SHAPE_INTERACT;
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (!world.isClientSide && entity instanceof ItemEntity itemEntity) {
			PetalApothecaryBlockEntity tile = (PetalApothecaryBlockEntity) world.getBlockEntity(pos);
			if (tile.collideEntityItem(itemEntity)) {
				VanillaPacketDispatcher.dispatchTEToNearbyPlayers(tile);
			}
		}
	}

	/*OLD
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!(world.getBlockEntity(pos) instanceof PetalApothecaryBlockEntity apothecary)) {
			return InteractionResult.PASS;
		}
		boolean mainHandEmpty = player.getMainHandItem().isEmpty();
	
		if (apothecary.canAddLastRecipe() && mainHandEmpty) {
			return apothecary.trySetLastRecipe(player);
		} else if (!apothecary.isEmpty() && mainHandEmpty) {
			InventoryHelper.withdrawFromInventory(apothecary, player);
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(apothecary);
			return InteractionResult.sidedSuccess(world.isClientSide());
		} else if (tryWithdrawFluid(player, hand, apothecary, pos) || tryDepositFluid(player, hand, apothecary, pos)) {
			return InteractionResult.sidedSuccess(world.isClientSide());
		}
	
		return InteractionResult.PASS;
	}
	 */

	//TODO this could maybe be split into useItemOn and useWithoutItem
	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (!(world.getBlockEntity(pos) instanceof PetalApothecaryBlockEntity apothecary)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		boolean mainHandEmpty = player.getMainHandItem().isEmpty();

		if (apothecary.canAddLastRecipe() && mainHandEmpty) {
			return apothecary.trySetLastRecipe(player);
		} else if (!apothecary.isEmpty() && mainHandEmpty) {
			InventoryHelper.withdrawFromInventory(apothecary, player);
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(apothecary);
			return ItemInteractionResult.sidedSuccess(world.isClientSide());
		} else if (tryWithdrawFluid(player, hand, apothecary, pos) || tryDepositFluid(player, hand, apothecary, pos)) {
			return ItemInteractionResult.sidedSuccess(world.isClientSide());
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	public void handlePrecipitation(BlockState state, Level world, BlockPos pos, Biome.Precipitation precipitation) {
		if (world.random.nextInt(20) == 1) {
			if (state.getValue(FLUID) == State.EMPTY) {
				world.setBlockAndUpdate(pos, state.setValue(FLUID, State.WATER));
				world.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);
			}
		}
	}

	private boolean tryWithdrawFluid(Player player, InteractionHand hand, PetalApothecaryBlockEntity altar, BlockPos pos) {
		Fluid fluid = altar.getFluid().asVanilla();
		if (fluid == Fluids.EMPTY || fluid == Fluids.WATER && XplatAbstractions.INSTANCE.gogLoaded()) {
			return false;
		}

		boolean success = XplatAbstractions.INSTANCE.insertFluidIntoPlayerItem(player, hand, fluid);
		if (success) {
			altar.setFluid(PetalApothecary.State.EMPTY);
			// Usage of vanilla sound events: Subtitle is "Bucket fills"
			if (fluid == Fluids.WATER) {
				player.level().playSound(player, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1F, 1F);
			} else if (fluid == Fluids.LAVA) {
				player.level().playSound(player, pos, SoundEvents.BUCKET_FILL_LAVA, SoundSource.BLOCKS, 1F, 1F);
			}
		}
		return success;
	}

	private boolean tryDepositFluid(Player player, InteractionHand hand, PetalApothecaryBlockEntity altar, BlockPos pos) {
		if (altar.getFluid() != State.EMPTY) {
			return false;
		}

		ItemStack stack = player.getItemInHand(hand);
		if (!stack.isEmpty()
				&& stack.is(BotaniaItems.waterRod)
				&& ManaItemHandler.instance().requestManaExact(stack, player, SeasRodItem.COST, false)) {
			ManaItemHandler.instance().requestManaExact(stack, player, SeasRodItem.COST, true);
			altar.setFluid(State.WATER);
			player.level().playSound(player, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1F, 1F);
			return true;
		}

		if (XplatAbstractions.INSTANCE.extractFluidFromPlayerItem(player, hand, Fluids.WATER)) {
			altar.setFluid(State.WATER);
			// Usage of vanilla sound event: Subtitle is "Bucket empties"
			player.level().playSound(player, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1F, 1F);
			return true;
		} else if (XplatAbstractions.INSTANCE.extractFluidFromPlayerItem(player, hand, Fluids.LAVA)) {
			altar.setFluid(State.LAVA);
			// Usage of vanilla sound event: Subtitle is "Bucket empties"
			player.level().playSound(player, pos, SoundEvents.BUCKET_EMPTY_LAVA, SoundSource.BLOCKS, 1F, 1F);
			return true;
		}
		return false;
	}

	@NotNull
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new PetalApothecaryBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide) {
			return createTickerHelper(type, BotaniaBlockEntities.ALTAR, PetalApothecaryBlockEntity::clientTick);
		} else {
			return createTickerHelper(type, BotaniaBlockEntities.ALTAR, PetalApothecaryBlockEntity::serverTick);
		}
	}

	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
		boolean blockChanged = !state.is(newState.getBlock());
		if (blockChanged || newState.getValue(FLUID) != State.WATER) {
			BlockEntity be = world.getBlockEntity(pos);
			if (be instanceof SimpleInventoryBlockEntity inventory) {
				Containers.dropContents(world, pos, inventory.getItemHandler());
			}
			if (blockChanged) {
				super.onRemove(state, world, pos, newState, isMoving);
			}
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		return state.getValue(FLUID) == State.WATER ? 15 : 0;
	}
}
