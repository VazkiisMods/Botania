/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.decor;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.block.FloatingFlower;
import vazkii.botania.api.block.FloatingFlower.IslandType;
import vazkii.botania.api.block.FloatingFlowerProvider;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.patchouli.PatchouliUtils;
import vazkii.botania.common.block.BotaniaWaterloggedBlock;
import vazkii.botania.common.block.block_entity.FloatingFlowerBlockEntity;
import vazkii.botania.common.item.FloatingFlowerVariant;
import vazkii.botania.common.item.material.MysticalPetalItem;
import vazkii.botania.xplat.BotaniaConfig;
import vazkii.botania.xplat.XplatAbstractions;

public class FloatingFlowerBlock extends BotaniaWaterloggedBlock implements EntityBlock {

	private static final VoxelShape SHAPE = box(1.6, 1.6, 1.6, 14.4, 14.4, 14.4);
	public final DyeColor color;

	public FloatingFlowerBlock(DyeColor color, Properties props) {
		super(props);
		this.color = color;
	}

	@NotNull
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
		return SHAPE;
	}

	@NotNull
	@Override
	public RenderShape getRenderShape(BlockState state) {
		if (!XplatAbstractions.INSTANCE.isPhysicalClient()) {
			return RenderShape.ENTITYBLOCK_ANIMATED;
		}
		return BotaniaConfig.client().staticFloaters() || PatchouliUtils.isInVisualizer() ? RenderShape.MODEL : RenderShape.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
		int hex = MysticalPetalItem.getPetalLikeColor(color);
		int r = (hex & 0xFF0000) >> 16;
		int g = (hex & 0xFF00) >> 8;
		int b = hex & 0xFF;

		if (rand.nextDouble() < BotaniaConfig.client().flowerParticleFrequency()) {
			SparkleParticleData data = SparkleParticleData.sparkle(rand.nextFloat(), r / 255F, g / 255F, b / 255F, 5);
			world.addParticle(data, pos.getX() + 0.3 + rand.nextFloat() * 0.5, pos.getY() + 0.5 + rand.nextFloat() * 0.5, pos.getZ() + 0.3 + rand.nextFloat() * 0.5, 0, 0, 0);
		}
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hitResult) {
		BlockEntity te = level.getBlockEntity(pos);
		if (te instanceof FloatingFlowerProvider provider && provider.getFloatingData() != null) {
			FloatingFlower flower = provider.getFloatingData();
			IslandType type = null;
			if (stack.is(Items.SNOWBALL)) {
				type = IslandType.SNOW;
			} else if (stack.getItem() instanceof FloatingFlowerVariant floatingFlower) {
				IslandType newType = floatingFlower.getIslandType(stack);
				if (newType != null) {
					type = newType;
				}
			}

			if (type != null && type != flower.getIslandType()) {
				if (!level.isClientSide) {
					flower.setIslandType(type);
					VanillaPacketDispatcher.dispatchTEToNearbyPlayers(te);
				}

				if (!player.getAbilities().instabuild) {
					stack.shrink(1);
				}
				return ItemInteractionResult.sidedSuccess(level.isClientSide());
			}
		}
		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@NotNull
	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
		return new FloatingFlowerBlockEntity(pos, state);
	}
}
