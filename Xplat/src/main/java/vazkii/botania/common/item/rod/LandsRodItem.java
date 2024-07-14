/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.rod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.block.Avatar;
import vazkii.botania.api.item.AvatarWieldable;
import vazkii.botania.api.item.BlockProvider;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.xplat.XplatAbstractions;

public class LandsRodItem extends Item {

	private static final ResourceLocation avatarOverlay = new ResourceLocation(ResourcesLib.MODEL_AVATAR_DIRT);

	static final int COST = 75;

	public LandsRodItem(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		return place(ctx, Blocks.DIRT, COST, 0.35F, 0.2F, 0.05F);
	}

	public static InteractionResult place(UseOnContext ctx, Block block, int cost, float r, float g, float b) {
		Player player = ctx.getPlayer();
		ItemStack stack = ctx.getItemInHand();
		Level world = ctx.getLevel();
		Direction side = ctx.getClickedFace();
		BlockPos pos = ctx.getClickedPos();

		if (player != null && ManaItemHandler.instance().requestManaExactForTool(stack, player, cost, false)) {
			int entities = world.getEntitiesOfClass(LivingEntity.class, new AABB(pos.relative(side))).size();

			if (entities == 0) {
				InteractionResult result = PlayerHelper.substituteUse(ctx, new ItemStack(block));

				if (result.consumesAction()) {
					ManaItemHandler.instance().requestManaExactForTool(stack, player, cost, true);
					SparkleParticleData data = SparkleParticleData.sparkle(1F, r, g, b, 5);
					for (int i = 0; i < 6; i++) {
						world.addParticle(data, pos.getX() + side.getStepX() + Math.random(), pos.getY() + side.getStepY() + Math.random(), pos.getZ() + side.getStepZ() + Math.random(), 0, 0, 0);
					}
					return result;
				}
			}

			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	public static class BlockProviderImpl implements BlockProvider {
		private final ItemStack stack;

		public BlockProviderImpl(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		public boolean provideBlock(Player player, ItemStack requestor, Block block, boolean doit) {
			if (block == Blocks.DIRT) {
				return (doit && ManaItemHandler.instance().requestManaExactForTool(requestor, player, COST, true)) ||
						(!doit && ManaItemHandler.instance().requestManaExactForTool(requestor, player, COST, false));
			}
			return false;
		}

		@Override
		public int getBlockCount(Player player, ItemStack requestor, Block block) {
			if (block == Blocks.DIRT) {
				return ManaItemHandler.instance().getInvocationCountForTool(requestor, player, COST);
			}
			return 0;
		}
	}

	public static class AvatarBehavior implements AvatarWieldable {
		@Override
		public void onAvatarUpdate(Avatar tile) {
			BlockEntity te = (BlockEntity) tile;
			Level world = te.getLevel();
			ManaReceiver receiver = XplatAbstractions.INSTANCE.findManaReceiver(world, te.getBlockPos(), te.getBlockState(), te, null);
			if (!world.isClientSide && receiver.getCurrentMana() >= COST && tile.getElapsedFunctionalTicks() % 4 == 0 && world.random.nextInt(8) == 0 && tile.isEnabled()) {
				BlockPos pos = te.getBlockPos().relative(tile.getAvatarFacing());
				BlockState state = world.getBlockState(pos);
				if (state.isAir()) {
					world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState());
					world.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(Blocks.DIRT.defaultBlockState()));
					receiver.receiveMana(-COST);
				}
			}
		}

		@Override
		public ResourceLocation getOverlayResource(Avatar tile) {
			return avatarOverlay;
		}
	}

}
