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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.PylonBlockEntity;

public class PylonBlock extends BotaniaWaterloggedBlock implements EntityBlock {
	private static final VoxelShape SHAPE = box(2, 0, 2, 14, 21, 14);

	public enum Variant {
		// Note: The enchantment power values are duplicated in the following file. Make sure to keep them in sync:
		// Fabric/src/main/resources/data/quilt/attachments/minecraft/block/enchanting_boosters.json
		MANA(8f, 0.5f, 0.5f, 1f),
		NATURA(15f, 0.5f, 1f, 0.5f),
		GAIA(15f, 1f, 0.5f, 1f);

		public final float enchantPowerBonus;
		public final float r, g, b;

		Variant(float epb, float r, float g, float b) {
			enchantPowerBonus = epb;
			this.r = r;
			this.g = g;
			this.b = b;
		}

		public Block getTargetBlock() {
			return this == MANA ? BotaniaBlocks.enchanter : BotaniaBlocks.alfPortal;
		}
	}

	public final Variant variant;

	public PylonBlock(@NotNull Variant v, Properties builder) {
		super(builder);
		this.variant = v;
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

	@NotNull
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new PylonBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BotaniaBlockEntities.PYLON, PylonBlockEntity::commonTick);
	}

	@SoftImplement("IBlockExtension")
	public float getEnchantPowerBonus(BlockState state, LevelReader world, BlockPos pos) {
		return variant.enchantPowerBonus;
	}
}
