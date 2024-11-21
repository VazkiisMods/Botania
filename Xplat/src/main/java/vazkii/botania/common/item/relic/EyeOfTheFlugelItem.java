/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.relic;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.item.CoordBoundItem;
import vazkii.botania.api.item.Relic;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.helper.MathHelper;
import vazkii.botania.network.EffectType;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;
import java.util.Optional;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class EyeOfTheFlugelItem extends RelicItem {

	public EyeOfTheFlugelItem(Properties props) {
		super(props);
	}

	private static final String TAG_TARGET_PREFIX = "target_";

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Player player = ctx.getPlayer();

		if (player != null && player.isSecondaryUseActive()) {
			if (world.isClientSide) {
				for (int i = 0; i < 10; i++) {
					float x1 = (float) (pos.getX() + Math.random());
					float y1 = pos.getY() + 1;
					float z1 = (float) (pos.getZ() + Math.random());
					WispParticleData data = WispParticleData.wisp((float) Math.random() * 0.5F, (float) Math.random(), (float) Math.random(), (float) Math.random(), 1);
					world.addParticle(data, x1, y1, z1, 0, 0.05F - (float) Math.random() * 0.05F, 0);
				}
			} else {
				ItemStack stack = ctx.getItemInHand();
				Tag nbt = BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).get().orThrow();
				ItemNBTHelper.set(stack, TAG_TARGET_PREFIX + world.dimension().location(), nbt);
				world.playSound(null, player.getX(), player.getY(), player.getZ(), BotaniaSounds.flugelEyeBind, SoundSource.PLAYERS, 1F, 1F);
			}

			return InteractionResult.sidedSuccess(world.isClientSide());
		}

		return InteractionResult.PASS;
	}

	@Override
	public void onUseTick(Level world, LivingEntity living, ItemStack stack, int count) {
		if (world.isClientSide) {
			float x = (float) (living.getX() - Math.random() * living.getBbWidth());
			float y = (float) (living.getY() + Math.random());
			float z = (float) (living.getZ() - Math.random() * living.getBbWidth());
			WispParticleData data = WispParticleData.wisp((float) Math.random() * 0.7F, (float) Math.random(), (float) Math.random(), (float) Math.random(), 1);
			world.addParticle(data, x, y, z, 0, 0.05F + (float) Math.random() * 0.05F, 0);
		}
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		return ItemUtils.startUsingInstantly(world, player, hand);
	}

	@NotNull
	@Override
	public ItemStack finishUsingItem(@NotNull ItemStack stack, Level world, LivingEntity living) {
		String tag = TAG_TARGET_PREFIX + world.dimension().location();
		Tag nbt = ItemNBTHelper.get(stack, tag);
		if (nbt == null) {
			return stack;
		}
		Optional<BlockPos> maybeLoc = BlockPos.CODEC.parse(NbtOps.INSTANCE, nbt).result();
		if (!maybeLoc.isPresent()) {
			ItemNBTHelper.removeEntry(stack, tag);
			return stack;
		}
		BlockPos loc = maybeLoc.get();
		int x = loc.getX();
		int y = loc.getY();
		int z = loc.getZ();

		int cost = (int) (MathHelper.pointDistanceSpace(x + 0.5, y + 0.5, z + 0.5, living.getX(), living.getY(), living.getZ()) * 10);

		if (!(living instanceof Player player) || ManaItemHandler.instance().requestManaExact(stack, player, cost, true)) {
			moveParticlesAndSound(living);
			Vec3 sourcePos = living.position();
			living.teleportTo(x + 0.5, y + 1.5, z + 0.5);
			world.gameEvent(living, GameEvent.TELEPORT, sourcePos);
			moveParticlesAndSound(living);
		}

		return stack;
	}

	private static void moveParticlesAndSound(Entity entity) {
		XplatAbstractions.INSTANCE.sendToTracking(entity, new BotaniaEffectPacket(EffectType.FLUGEL_EFFECT, entity.getX(), entity.getY(), entity.getZ(), entity.getId()));
		entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), BotaniaSounds.flugelEyeTeleport, SoundSource.PLAYERS, 1F, 1F);
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 40;
	}

	@NotNull
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	public static class CoordBoundItemImpl implements CoordBoundItem {
		private final ItemStack stack;

		public CoordBoundItemImpl(ItemStack stack) {
			this.stack = stack;
		}

		@Nullable
		@Override
		public BlockPos getBinding(Level world) {
			String tag = TAG_TARGET_PREFIX + world.dimension().location();
			Tag nbt = ItemNBTHelper.get(stack, tag);
			if (nbt != null) {
				return BlockPos.CODEC.parse(NbtOps.INSTANCE, nbt).result()
						.orElse(null);
			}
			return null;
		}
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flags) {
		super.appendHoverText(stack, context, tooltip, flags);

		if (context.registries() == null) {
			return;
		}

		var coordBoundItem = XplatAbstractions.INSTANCE.findCoordBoundItem(stack);
		if (coordBoundItem == null) {
			return;
		}

		BlockPos binding = coordBoundItem.getBinding(world);
		Component worldText = Component.literal(world.dimension().location().toString()).withStyle(ChatFormatting.GREEN);

		if (binding == null) {
			tooltip.add(Component.translatable("botaniamisc.flugelUnbound", worldText).withStyle(ChatFormatting.GRAY));
		} else {
			Component bindingText = Component.literal("[").withStyle(ChatFormatting.WHITE)
					.append(Component.literal(Integer.toString(binding.getX())).withStyle(ChatFormatting.GOLD))
					.append(", ")
					.append(Component.literal(Integer.toString(binding.getY())).withStyle(ChatFormatting.GOLD))
					.append(", ")
					.append(Component.literal(Integer.toString(binding.getZ())).withStyle(ChatFormatting.GOLD))
					.append("]");

			tooltip.add(Component.translatable("botaniamisc.flugelBound", bindingText, worldText).withStyle(ChatFormatting.GRAY));
		}
	}

	public static Relic makeRelic(ItemStack stack) {
		return new RelicImpl(stack, botaniaRL("challenge/flugel_eye"));
	}

}
