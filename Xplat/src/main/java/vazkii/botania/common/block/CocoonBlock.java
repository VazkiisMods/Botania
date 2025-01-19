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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.CocoonBlockEntity;
import vazkii.botania.common.item.BotaniaItems;

public class CocoonBlock extends BotaniaWaterloggedBlock implements EntityBlock {

	private static final VoxelShape SHAPE = box(3, 0, 3, 13, 14, 13);

	protected CocoonBlock(Properties builder) {
		super(builder);
	}

	@NotNull
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
	}

	@NotNull
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void entityInside(BlockState state, Level world, BlockPos pos, Entity e) {
		if (!world.isClientSide && e instanceof ItemEntity item) {
			ItemStack stack = item.getItem();
			addStack(world, pos, stack, false);

			if (stack.isEmpty()) {
				item.discard();
			}
		}
	}

	@Override
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		return addStack(level, pos, stack, player.getAbilities().instabuild);
	}

	private ItemInteractionResult addStack(Level world, BlockPos pos, ItemStack stack, boolean creative) {
		CocoonBlockEntity cocoon = (CocoonBlockEntity) world.getBlockEntity(pos);

		if (cocoon != null && (stack.is(Items.EMERALD) || stack.is(Items.CHORUS_FRUIT) || stack.is(BotaniaItems.lifeEssence))) {
			if (!world.isClientSide) {
				if (stack.is(Items.EMERALD) && cocoon.emeraldsGiven < CocoonBlockEntity.MAX_EMERALDS) {
					if (!creative) {
						stack.shrink(1);
					}
					cocoon.emeraldsGiven++;
					((ServerLevel) world).sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 1, 0.1, 0.05, 0.1, 0.5);
				} else if (stack.is(Items.CHORUS_FRUIT) && cocoon.chorusFruitGiven < CocoonBlockEntity.MAX_CHORUS_FRUITS) {
					if (!creative) {
						stack.shrink(1);
					}
					cocoon.chorusFruitGiven++;
					((ServerLevel) world).sendParticles(ParticleTypes.PORTAL, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 32, 0, 0, 0, 0.5);
				} else if (stack.is(BotaniaItems.lifeEssence) && !cocoon.gaiaSpiritGiven) {
					if (!creative) {
						stack.shrink(1);
					}
					cocoon.forceRare();
					WispParticleData data = WispParticleData.wisp(0.6F, 0F, 1F, 0F);
					((ServerLevel) world).sendParticles(data, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 8, 0.1, 0.1, 0.1, 0.04);
				}
			}

			return ItemInteractionResult.sidedSuccess(world.isClientSide());
		}

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@NotNull
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new CocoonBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BotaniaBlockEntities.COCOON, CocoonBlockEntity::commonTick);
	}
}
