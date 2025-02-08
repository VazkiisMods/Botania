/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.bauble;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.client.render.AccessoryRenderRegistry;
import vazkii.botania.client.render.AccessoryRenderer;
import vazkii.botania.common.block.flower.functional.HeiseiDreamBlockEntity;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.proxy.Proxy;
import vazkii.botania.mixin.CreeperAccessor;
import vazkii.botania.mixin.EntityAccessor;
import vazkii.botania.network.EffectType;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;
import java.util.function.Predicate;

public class CharmOfTheDivaItem extends BaubleItem {
	public static final int MANA_COST = 250;
	public static final int CHARM_RANGE = 20;

	public CharmOfTheDivaItem(Properties props) {
		super(props);
		Proxy.INSTANCE.runOnClient(() -> () -> AccessoryRenderRegistry.register(this, new Renderer()));
	}

	private static Predicate<Mob> getCharmTargetPredicate(Player player, Mob mobToCharm) {
		return mob -> mob != mobToCharm && mob.isAlive() && mob.canBeSeenAsEnemy() && !mob.isPassengerOfSameVehicle(mobToCharm)
				&& (!(mob instanceof TamableAnimal tamable) || !tamable.isOwnedBy(player))
				&& (mob instanceof Enemy || mob instanceof NeutralMob neutralMob && (neutralMob.isAngryAt(player)
						|| mob.getTarget() instanceof TamableAnimal targetTamable && targetTamable.isOwnedBy(player)));
	}

	private static void charmMobs(ItemStack amulet, Player player, Mob target) {
		if (!ManaItemHandler.instance().requestManaExact(amulet, player, MANA_COST, false)) {
			return;
		}
		if (target.isAlive()
				&& (target instanceof Enemy || target instanceof NeutralMob)
				// don't encourage being a bad pet owner
				&& (!(target instanceof TamableAnimal tamable) || !tamable.isOwnedBy(player))
				// check that target is still nearby, since it was marked some time earlier
				&& player.position().closerThan(target.position(), CHARM_RANGE)) {
			List<Mob> potentialTargets = player.level().getEntitiesOfClass(Mob.class,
					AABB.ofSize(target.position(), 2 * CHARM_RANGE, 2 * CHARM_RANGE, 2 * CHARM_RANGE),
					getCharmTargetPredicate(player, target));
			if (!potentialTargets.isEmpty() && HeiseiDreamBlockEntity.brainwashEntity(target, potentialTargets)) {
				target.heal(target.getMaxHealth());
				((EntityAccessor) target).callUnsetRemoved();
				if (target instanceof Creeper) {
					((CreeperAccessor) target).setCurrentFuseTime(2);
				}

				ManaItemHandler.instance().requestManaExact(amulet, player, MANA_COST, true);
				player.level().playSound(null, player.getX(), player.getY(), player.getZ(), BotaniaSounds.divaCharm, SoundSource.PLAYERS, 1F, 1F);
				XplatAbstractions.INSTANCE.sendToTracking(target, new BotaniaEffectPacket(EffectType.DIVA_EFFECT, target.getX(), target.getY(), target.getZ(), target.getId()));
			}
		}
	}

	public static void onEntityDamaged(Player player, LivingEntity entity) {
		if (entity instanceof Mob target
				&& !target.level().isClientSide
				// TODO 1.21: Use an actual boss identification method (likely via entity tag)
				&& target.canChangeDimensions()
				&& Math.random() < 0.6) {
			MinecraftServer server = player.level().getServer();
			ItemStack amulet = EquipmentHandler.findOrEmpty(BotaniaItems.divaCharm, player);

			if (server != null && !amulet.isEmpty()) {
				// schedule for immediate execution after everything else in this tick
				server.tell(new TickTask(0, () -> charmMobs(amulet, player, target)));
			}
		}
	}

	public static class Renderer implements AccessoryRenderer {
		@Override
		public void doRender(HumanoidModel<?> bipedModel, ItemStack stack, LivingEntity living, PoseStack ms, MultiBufferSource buffers, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
			bipedModel.head.translateAndRotate(ms);
			ms.translate(0.15, -0.42, -0.35);
			ms.scale(0.4F, -0.4F, -0.4F);
			Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE,
					light, OverlayTexture.NO_OVERLAY, ms, buffers, living.level(), living.getId());
		}
	}
}
