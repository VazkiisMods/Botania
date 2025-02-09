/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.rod;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.block.Avatar;
import vazkii.botania.api.item.AvatarWieldable;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.api.mana.ManaReceiver;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.brew.BotaniaMobEffects;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.network.EffectType;
import vazkii.botania.network.clientbound.AvatarSkiesRodPacket;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SkiesRodItem extends Item {

	private static final ResourceLocation avatarOverlay = ResourceLocation.parse(ResourcesLib.MODEL_AVATAR_TORNADO);

	private static final int FLY_TIME = 20;
	private static final int FALL_MULTIPLIER = 3;
	private static final int MAX_COUNTER = FLY_TIME * FALL_MULTIPLIER;
	private static final int COST = 350;

	private static final String TAG_FLYING = "flying";
	private static final String TAG_FLYCOUNTER = "flyCounter";

	public SkiesRodItem(Properties props) {
		super(props);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity ent, int slot, boolean active) {
		if (ent instanceof Player player) {
			boolean damaged = getFlyCounter(stack) > 0;
			boolean held = player.getMainHandItem() == stack || player.getOffhandItem() == stack;

			if (damaged && !isFlying(stack)) {
				setFlyCounter(stack, getFlyCounter(stack) - 1);
			}

			if (getFlyCounter(stack) >= MAX_COUNTER) {
				setFlying(stack, false);
			} else if (isFlying(stack)) {
				if (held) {
					player.fallDistance = 0F;
					double my = ManaItemHandler.instance().hasProficiency(player, stack) ? 1.6 : 1.25;
					Vec3 oldMot = player.getDeltaMovement();
					if (player.isFallFlying()) {
						Vec3 lookDir = player.getLookAngle();
						player.setDeltaMovement(new Vec3(lookDir.x() * my, lookDir.y() * my, lookDir.z() * my));
					} else {
						player.setDeltaMovement(new Vec3(oldMot.x(), my, oldMot.z()));
					}

					player.playSound(BotaniaSounds.airRod, 1F, 1F);
					if (getFlyCounter(stack) % 3 == 0) {
						player.gameEvent(GameEvent.FLAP);
					}
					for (int i = 0; i < 5; i++) {
						WispParticleData data = WispParticleData.wisp(0.35F + (float) Math.random() * 0.1F, 0.25F, 0.25F, 0.25F);
						world.addParticle(data, player.getX(), player.getY(), player.getZ(),
								0.2F * (float) (Math.random() - 0.5),
								-0.01F * (float) Math.random(),
								0.2F * (float) (Math.random() - 0.5));
					}
				}

				setFlyCounter(stack, getFlyCounter(stack) + FALL_MULTIPLIER);
				if (getFlyCounter(stack) == MAX_COUNTER) {
					setFlying(stack, false);
				}
			}

			if (damaged) {
				player.fallDistance = 0;
			}
		}
	}

	@Override
	public boolean isBarVisible(@NotNull ItemStack stack) {
		return getFlyCounter(stack) > 0;
	}

	@Override
	public int getBarWidth(@NotNull ItemStack stack) {
		float frac = 1 - (getFlyCounter(stack) / (float) MAX_COUNTER);
		return Math.round(13 * frac);
	}

	@Override
	public int getBarColor(@NotNull ItemStack stack) {
		float frac = 1 - (getFlyCounter(stack) / (float) MAX_COUNTER);
		return Mth.hsvToRgb(frac / 3.0F, 1.0F, 1.0F);
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		int fly = getFlyCounter(stack);
		if (fly == 0 && ManaItemHandler.instance().requestManaExactForTool(stack, player, COST, false)) {
			ManaItemHandler.instance().requestManaExactForTool(stack, player, COST, true);
			setFlying(stack, true);
			player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
			return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
		}

		return InteractionResultHolder.pass(stack);
	}

	public static boolean isFlying(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_FLYING, false);
	}

	private void setFlying(ItemStack stack, boolean flying) {
		ItemNBTHelper.setBoolean(stack, TAG_FLYING, flying);
	}

	private int getFlyCounter(ItemStack stack) {
		return /*todo stack.getOrCreateTag().getInt(TAG_FLYCOUNTER)*/ 0;
	}

	private void setFlyCounter(ItemStack stack, int counter) {
		//todo stack.getOrCreateTag().putInt(TAG_FLYCOUNTER, counter);
	}

	public static class AvatarBehavior implements AvatarWieldable {
		@Override
		public void onAvatarUpdate(Avatar tile) {
			BlockEntity te = (BlockEntity) tile;
			Level world = te.getLevel();
			Map<UUID, Integer> cooldowns = tile.getBoostCooldowns();
			ManaReceiver receiver = XplatAbstractions.INSTANCE.findManaReceiver(world, te.getBlockPos(), te.getBlockState(), te, null);

			if (!world.isClientSide) {
				decAvatarCooldowns(cooldowns);
			}
			if (!world.isClientSide && receiver.getCurrentMana() >= COST && tile.isEnabled()) {
				double range = 5.5;
				double rangeY = 3.5;
				List<Player> players = world.getEntitiesOfClass(Player.class, new AABB(
						te.getBlockPos().getCenter().add(-range, -rangeY, -range),
						te.getBlockPos().getCenter().add(range, rangeY, range)
				));
				for (Player p : players) {
					int cooldown = 0;
					if (cooldowns.containsKey(p.getUUID())) {
						cooldown = cooldowns.get(p.getUUID());
					}
					if (!p.isShiftKeyDown() && cooldown <= 0) {
						if (p.getDeltaMovement().length() > 0.2 && p.getDeltaMovement().length() < 5 && p.isFallFlying()) {
							doAvatarElytraBoost(p, world);
							doAvatarMiscEffects(p, receiver);
							cooldowns.put(p.getUUID(), 20);
							te.setChanged();
						} else if (p.getDeltaMovement().y() > 0.3 && p.getDeltaMovement().y() < 2 && !p.isFallFlying()) {
							doAvatarJump(p, world);
							doAvatarMiscEffects(p, receiver);
						}
					}
				}
			}
		}

		@Override
		public ResourceLocation getOverlayResource(Avatar tile) {
			return avatarOverlay;
		}
	}

	public static void doAvatarElytraBoost(Player p, Level world) {
		Vec3 lookDir = p.getLookAngle();
		double mult = 1.25 * Math.pow(Math.E, -0.5 * p.getDeltaMovement().length());
		p.setDeltaMovement(p.getDeltaMovement().x() + lookDir.x() * mult,
				p.getDeltaMovement().y() + lookDir.y() * mult,
				p.getDeltaMovement().z() + lookDir.z() * mult);

		if (!world.isClientSide) {
			XplatAbstractions.INSTANCE.sendToPlayer(p, new AvatarSkiesRodPacket(true));
			XplatAbstractions.INSTANCE.sendToTracking(p,
					new BotaniaEffectPacket(EffectType.AVATAR_TORNADO_BOOST,
							p.getX(), p.getY(), p.getZ(),
							p.getId()));
		}
	}

	public static void doAvatarJump(Player p, Level world) {
		p.setDeltaMovement(p.getDeltaMovement().x(), 2.8, p.getDeltaMovement().z());

		if (!world.isClientSide) {
			XplatAbstractions.INSTANCE.sendToPlayer(p, new AvatarSkiesRodPacket(false));
			XplatAbstractions.INSTANCE.sendToTracking(p,
					new BotaniaEffectPacket(EffectType.AVATAR_TORNADO_JUMP,
							p.getX(), p.getY(), p.getZ(),
							p.getId())
			);
		}
	}

	private static void doAvatarMiscEffects(Player p, ManaReceiver tile) {
		p.level().playSound(null, p.getX(), p.getY(), p.getZ(), BotaniaSounds.dash, SoundSource.PLAYERS, 1F, 1F);
		p.gameEvent(GameEvent.FLAP);
		p.addEffect(new MobEffectInstance(BotaniaMobEffects.FEATHER_FEET, 100, 0));
		tile.receiveMana(-COST);
	}

	private static void decAvatarCooldowns(Map<UUID, Integer> cooldownTag) {
		for (UUID key : cooldownTag.keySet()) {
			int val = cooldownTag.get(key);
			if (val > 0) {
				cooldownTag.put(key, val - 1);
			} else {
				cooldownTag.remove(key);
			}
		}
	}

	@SoftImplement("IItemExtension")
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return reequipAnimation(oldStack, newStack);
	}

	@SoftImplement("FabricItem")
	public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack oldStack, ItemStack newStack) {
		return reequipAnimation(oldStack, newStack);
	}

	private boolean reequipAnimation(ItemStack before, ItemStack after) {
		return !before.is(this) || isFlying(before) != isFlying(after);
	}

}
