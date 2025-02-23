/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.advancements.UseItemSuccessTrigger;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.helper.PlayerHelper;

import java.util.List;

public class LifeAggregatorItem extends Item {

	public LifeAggregatorItem(Properties props) {
		super(props);
	}

	public static boolean hasData(ItemStack stack) {
		return stack.has(DataComponents.BLOCK_ENTITY_DATA);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> infoList, TooltipFlag flags) {
		ResourceLocation id = stack.get(BotaniaDataComponents.MOB_TYPE);
		if (id != null) {
			BuiltInRegistries.ENTITY_TYPE.getOptional(id).ifPresent(
					type -> infoList.add(type.getDescription().copy().withStyle(ChatFormatting.GRAY)));
		}
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		if (hasData(ctx.getItemInHand())) {
			return placeSpawner(ctx);
		} else {
			return captureSpawner(ctx)
					? InteractionResult.sidedSuccess(ctx.getLevel().isClientSide())
					: InteractionResult.PASS;
		}
	}

	private InteractionResult placeSpawner(UseOnContext ctx) {
		ItemStack useStack = new ItemStack(Blocks.SPAWNER);
		Pair<InteractionResult, BlockPos> res = PlayerHelper.substituteUseTrackPos(ctx, useStack);

		if (res.getFirst().consumesAction()) {
			Level world = ctx.getLevel();
			BlockPos pos = res.getSecond();
			ItemStack mover = ctx.getItemInHand();

			if (!world.isClientSide) {
				Player player = ctx.getPlayer();
				if (player != null) {
					player.onEquippedItemBroken(this, LivingEntity.getSlotForHand(ctx.getHand()));
				}

				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity instanceof SpawnerBlockEntity) {
					blockEntity.loadWithComponents(mover.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag(),
							world.registryAccess());
				}
				// must empty stack last, as otherwise components become inaccessible
				mover.shrink(1);
			} else {
				for (int i = 0; i < 100; i++) {
					SparkleParticleData data = SparkleParticleData.sparkle(0.45F + 0.2F * (float) Math.random(), (float) Math.random(), (float) Math.random(), (float) Math.random(), 6);
					world.addParticle(data, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0, 0, 0);
				}
			}
		}

		return res.getFirst();
	}

	private boolean captureSpawner(UseOnContext ctx) {
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		ItemStack stack = ctx.getItemInHand();
		Player player = ctx.getPlayer();

		// TODO: support trial spawners
		if (world.getBlockState(pos).is(Blocks.SPAWNER)) {
			if (!world.isClientSide) {
				SpawnerBlockEntity spawnerBlockEntity = (SpawnerBlockEntity) world.getBlockEntity(pos);
				Entity displayEntity = spawnerBlockEntity.getSpawner().getOrCreateDisplayEntity(world, pos);
				if (displayEntity != null) {
					// TODO: does this make sense or is it better to navigate the NBT data for the entity ID?
					stack.set(BotaniaDataComponents.MOB_TYPE, BuiltInRegistries.ENTITY_TYPE.getKey(displayEntity.getType()));
				}
				stack.set(DataComponents.BLOCK_ENTITY_DATA,
						// black-box serialized data; no need for metadata, or component transfer to this item
						CustomData.of(spawnerBlockEntity.saveWithoutMetadata(world.registryAccess())));
				world.destroyBlock(pos, false);
				if (player != null) {
					player.getCooldowns().addCooldown(this, 20);
					if (player instanceof ServerPlayer serverPlayer) {
						UseItemSuccessTrigger.INSTANCE.trigger(serverPlayer, stack, serverPlayer.serverLevel(),
								pos.getX(), pos.getY(), pos.getZ());
					}
					player.onEquippedItemBroken(this, LivingEntity.getSlotForHand(ctx.getHand()));
				}
			} else {
				for (int i = 0; i < 50; i++) {
					float red = (float) Math.random();
					float green = (float) Math.random();
					float blue = (float) Math.random();
					WispParticleData data = WispParticleData.wisp((float) Math.random() * 0.1F + 0.05F, red, green, blue);
					world.addParticle(data, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, (float) (Math.random() - 0.5F) * 0.15F, (float) (Math.random() - 0.5F) * 0.15F, (float) (Math.random() - 0.5F) * 0.15F);
				}
			}
			return true;
		} else {
			return false;
		}
	}
}
