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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.block.Avatar;
import vazkii.botania.api.item.AvatarWieldable;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.Random;

public class PlentifulMantleRodItem extends Item {

	private static final ResourceLocation avatarOverlay = ResourceLocation.parse(ResourcesLib.MODEL_AVATAR_DIVINING);

	static final int COST = 3000;

	public PlentifulMantleRodItem(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player p, @NotNull InteractionHand hand) {
		ItemStack stack = p.getItemInHand(hand);
		if (ManaItemHandler.instance().requestManaExactForTool(stack, p, COST, true)) {
			if (world.isClientSide) {
				int range = ManaItemHandler.instance().hasProficiency(p, stack) ? 20 : 15;
				long seedxor = world.random.nextLong();
				doHighlight(world, p.blockPosition(), range, seedxor);
			} else {
				world.playSound(null, p.getX(), p.getY(), p.getZ(), BotaniaSounds.divinationRod, SoundSource.PLAYERS, 1F, 1F);
				p.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
			}
			return InteractionResultHolder.sidedSuccess(stack, world.isClientSide);
		}

		return InteractionResultHolder.pass(stack);
	}

	private static void doHighlight(Level world, BlockPos pos, int range, long seedxor) {
		for (BlockPos pos_ : BlockPos.betweenClosed(pos.offset(-range, -range, -range),
				pos.offset(range, range, range))) {
			BlockState state = world.getBlockState(pos_);

			Block block = state.getBlock();
			if (state.is(XplatAbstractions.INSTANCE.getOreTag())) {
				Random rand = new Random(BuiltInRegistries.BLOCK.getKey(block).hashCode() ^ seedxor);
				WispParticleData data = WispParticleData.wisp(0.25F, rand.nextFloat(), rand.nextFloat(), rand.nextFloat(), 8, false);
				world.addParticle(data, true, pos_.getX() + world.random.nextFloat(), pos_.getY() + world.random.nextFloat(), pos_.getZ() + world.random.nextFloat(), 0, 0, 0);
			}
		}
	}

	public static class AvatarBehavior implements AvatarWieldable {
		@Override
		public void onAvatarUpdate(Avatar tile) {
			BlockEntity te = (BlockEntity) tile;
			Level world = te.getLevel();
			ManaReceiver receiver = XplatAbstractions.INSTANCE.findManaReceiver(world, te.getBlockPos(), te.getBlockState(), te, null);
			if (receiver.getCurrentMana() >= COST && tile.getElapsedFunctionalTicks() % 200 == 0 && tile.isEnabled()) {
				PlentifulMantleRodItem.doHighlight(world, te.getBlockPos(), 18, te.getBlockPos().hashCode());
				receiver.receiveMana(-COST);
			}
		}

		@Override
		public ResourceLocation getOverlayResource(Avatar tile) {
			return avatarOverlay;
		}
	}
}
