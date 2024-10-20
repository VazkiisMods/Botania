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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.block.Avatar;
import vazkii.botania.api.item.AvatarWieldable;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.entity.BotaniaEntities;
import vazkii.botania.common.entity.FlameRingEntity;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.xplat.XplatAbstractions;

public class HellsRodItem extends Item {

	private static final ResourceLocation avatarOverlay = ResourceLocation.parse(ResourcesLib.MODEL_AVATAR_FIRE);

	private static final int COST = 900;
	private static final int COOLDOWN = 1200;

	public HellsRodItem(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level world = ctx.getLevel();
		Player player = ctx.getPlayer();
		ItemStack stack = ctx.getItemInHand();
		BlockPos pos = ctx.getClickedPos();

		if (player != null && ManaItemHandler.instance().requestManaExactForTool(stack, player, COST, false)) {
			if (!world.isClientSide()) {
				FlameRingEntity entity = BotaniaEntities.FLAME_RING.create(world);
				entity.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
				world.addFreshEntity(entity);

				if (!player.isCreative()) {
					player.getCooldowns().addCooldown(this, ManaItemHandler.instance().hasProficiency(player, stack) ? COOLDOWN / 2 : COOLDOWN);
				}
				ManaItemHandler.instance().requestManaExactForTool(stack, player, COST, true);

				world.gameEvent(player, GameEvent.PROJECTILE_SHOOT, pos);
				ctx.getLevel().playSound(null, pos.getX(), pos.getY(), pos.getZ(), BotaniaSounds.fireRod, SoundSource.PLAYERS, 1F, 1F);
			}
		}

		return InteractionResult.sidedSuccess(world.isClientSide());
	}

	public static class AvatarBehavior implements AvatarWieldable {
		@Override
		public void onAvatarUpdate(Avatar tile) {
			BlockEntity te = (BlockEntity) tile;
			Level world = te.getLevel();
			BlockPos pos = te.getBlockPos();
			ManaReceiver receiver = XplatAbstractions.INSTANCE.findManaReceiver(world, pos, te.getBlockState(), te, null);

			if (!world.isClientSide && receiver.getCurrentMana() >= COST && tile.getElapsedFunctionalTicks() % 300 == 0 && tile.isEnabled()) {
				FlameRingEntity entity = BotaniaEntities.FLAME_RING.create(world);
				entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
				world.addFreshEntity(entity);
				receiver.receiveMana(-COST);
				world.gameEvent(null, GameEvent.PROJECTILE_SHOOT, pos);
			}
		}

		@Override
		public ResourceLocation getOverlayResource(Avatar tile) {
			return avatarOverlay;
		}
	}

}
