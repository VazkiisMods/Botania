/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.mana;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.state.BotaniaStateProperties.OptionalDyeColor;
import vazkii.botania.common.block.BotaniaWaterloggedBlock;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;
import vazkii.botania.common.block.decor.BotaniaMushroomBlock;
import vazkii.botania.common.entity.ManaBurstEntity;
import vazkii.botania.common.item.material.MysticalPetalItem;

import java.util.List;
import java.util.Optional;

import static vazkii.botania.api.state.BotaniaStateProperties.OPTIONAL_DYE_COLOR;

public class ManaPoolBlock extends BotaniaWaterloggedBlock implements EntityBlock {
	private static final VoxelShape NORMAL_SHAPE;
	private static final VoxelShape DILUTED_SHAPE;
	private static final VoxelShape CREATIVE_SHAPE;
	private static final VoxelShape NORMAL_SHAPE_INTERACT;
	private static final VoxelShape DILUTED_SHAPE_INTERACT;
	private static final VoxelShape CREATIVE_SHAPE_INTERACT;
	static {
		NORMAL_SHAPE_INTERACT = box(0, 0, 0, 16, 8, 16);
		DILUTED_SHAPE_INTERACT = box(0, 0, 0, 16, 6, 16);
		CREATIVE_SHAPE_INTERACT = box(0, 0, 0, 16, 10, 16);

		VoxelShape cutout = box(2, 2, 2, 14, 16, 14);
		VoxelShape dilutedCutout = box(1, 1, 1, 15, 6, 15);

		NORMAL_SHAPE = Shapes.join(NORMAL_SHAPE_INTERACT, cutout, BooleanOp.ONLY_FIRST);
		DILUTED_SHAPE = Shapes.join(DILUTED_SHAPE_INTERACT, dilutedCutout, BooleanOp.ONLY_FIRST);
		CREATIVE_SHAPE = Shapes.join(CREATIVE_SHAPE_INTERACT, cutout, BooleanOp.ONLY_FIRST);
	}

	public enum Variant {
		DEFAULT,
		CREATIVE,
		DILUTED,
		FABULOUS
	}

	public final Variant variant;

	public ManaPoolBlock(Variant v, Properties builder) {
		super(builder);
		this.variant = v;
		registerDefaultState(defaultBlockState().setValue(OPTIONAL_DYE_COLOR, OptionalDyeColor.NONE));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(OPTIONAL_DYE_COLOR);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltip, flag);
		if (variant == ManaPoolBlock.Variant.CREATIVE) {
			for (int i = 0; i < 2; i++) {
				tooltip.add(Component.translatable("botaniamisc.creativePool" + i).withStyle(ChatFormatting.GRAY));
			}
		}
	}

	@NotNull
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return switch (this.variant) {
			case DILUTED -> DILUTED_SHAPE;
			case CREATIVE -> CREATIVE_SHAPE;
			case DEFAULT, FABULOUS -> NORMAL_SHAPE;
		};
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (context instanceof EntityCollisionContext econtext
				&& econtext.getEntity() instanceof ManaBurstEntity) {
			// Sometimes the pool's collision box is too thin for bursts shot straight up.
			return switch (this.variant) {
				case DILUTED -> DILUTED_SHAPE_INTERACT;
				case CREATIVE -> CREATIVE_SHAPE_INTERACT;
				case DEFAULT, FABULOUS -> NORMAL_SHAPE_INTERACT;
			};
		} else {
			return super.getCollisionShape(state, world, pos, context);
		}
	}

	@NotNull
	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		return switch (this.variant) {
			case DILUTED -> DILUTED_SHAPE_INTERACT;
			case CREATIVE -> CREATIVE_SHAPE_INTERACT;
			case DEFAULT, FABULOUS -> NORMAL_SHAPE_INTERACT;
		};
	}

	@NotNull
	@Override
	public InteractionResult use(@NotNull BlockState state, Level world, @NotNull BlockPos pos, Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
		BlockEntity be = world.getBlockEntity(pos);
		ItemStack stack = player.getItemInHand(hand);
		Optional<DyeColor> itemColor = Optional.empty();
		if (stack.getItem() instanceof MysticalPetalItem petalItem) {
			itemColor = Optional.of(petalItem.color);
		}
		if (Block.byItem(stack.getItem()) instanceof BotaniaMushroomBlock mushroomBlock) {
			itemColor = Optional.of(mushroomBlock.color);
		}
		if (itemColor.isPresent() && be instanceof ManaPoolBlockEntity pool) {
			if (!itemColor.equals(pool.getColor())) {
				pool.setColor(itemColor);
				if (!player.getAbilities().instabuild) {
					stack.shrink(1);
				}
				return InteractionResult.sidedSuccess(world.isClientSide());
			}
		}
		if (stack.is(Items.CLAY_BALL) && be instanceof ManaPoolBlockEntity pool && pool.getColor().isPresent()) {
			pool.setColor(Optional.empty());
			if (!player.getAbilities().instabuild) {
				stack.shrink(1);
			}
			return InteractionResult.sidedSuccess(world.isClientSide());
		}
		return super.use(state, world, pos, player, hand, hit);
	}

	@NotNull
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new ManaPoolBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BotaniaBlockEntities.POOL, level.isClientSide ? ManaPoolBlockEntity::clientTick : ManaPoolBlockEntity::serverTick);
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (entity instanceof ItemEntity item) {
			ManaPoolBlockEntity tile = (ManaPoolBlockEntity) world.getBlockEntity(pos);
			tile.collideEntityItem(item);
		}
	}

	@NotNull
	@Override
	public RenderShape getRenderShape(BlockState state) {
		if (variant == Variant.FABULOUS) {
			return RenderShape.ENTITYBLOCK_ANIMATED;
		} else {
			return RenderShape.MODEL;
		}
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		ManaPoolBlockEntity pool = (ManaPoolBlockEntity) world.getBlockEntity(pos);
		return ManaPoolBlockEntity.calculateComparatorLevel(pool.getCurrentMana(), pool.getMaxMana());
	}
}
