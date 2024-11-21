/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import com.google.common.annotations.VisibleForTesting;

import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.gameevent.GameEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.CacophoniumBlockEntity;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.mixin.MobAccessor;

import java.util.List;

public class CacophoniumItem extends Item {

	private static final String TAG_SOUND = "sound";
	private static final String TAG_SOUND_NAME = "soundName";

	public CacophoniumItem(Properties props) {
		super(props);
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity entity, InteractionHand hand) {
		if (entity instanceof Mob living) {
			SoundEvent sound = null;

			if (living instanceof Creeper) {
				sound = SoundEvents.CREEPER_PRIMED;
			} else if (living instanceof Slime slime) {
				sound = slime.isTiny() ? SoundEvents.SLIME_SQUISH_SMALL : SoundEvents.SLIME_SQUISH;
			} else {
				sound = ((MobAccessor) living).botania_getAmbientSound();
			}

			if (sound != null) {
				if (!player.level().isClientSide) {
					ItemNBTHelper.setString(stack, TAG_SOUND, BuiltInRegistries.SOUND_EVENT.getKey(sound).toString());
					ItemNBTHelper.setString(stack, TAG_SOUND_NAME, entity.getType().getDescriptionId());
					player.setItemInHand(hand, stack);
				}

				return InteractionResult.sidedSuccess(player.level().isClientSide);
			}
		}

		return InteractionResult.PASS;
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		ItemStack stack = ctx.getItemInHand();
		if (getSound(stack) != null) {
			Level world = ctx.getLevel();
			BlockPos pos = ctx.getClickedPos();

			Block block = world.getBlockState(pos).getBlock();
			if (block instanceof NoteBlock) {
				if (!world.isClientSide()) {
					world.setBlockAndUpdate(pos, BotaniaBlocks.cacophonium.defaultBlockState());
					((CacophoniumBlockEntity) world.getBlockEntity(pos)).stack = stack.copy();
					stack.shrink(1);
				}
				return InteractionResult.sidedSuccess(world.isClientSide());
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag flags) {
		if (isDOIT(stack)) {
			list.add(Component.translatable("botaniamisc.justDoIt").withStyle(ChatFormatting.GRAY));
		} else if (getSound(stack) != null) {
			list.add(Component.translatable(ItemNBTHelper.getString(stack, TAG_SOUND_NAME, "")).withStyle(ChatFormatting.GRAY));
		}
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (getSound(stack) != null) {
			return ItemUtils.startUsingInstantly(world, player, hand);
		}
		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void onUseTick(Level world, @NotNull LivingEntity living, @NotNull ItemStack stack, int count) {
		if (!world.isClientSide && count % (isDOIT(stack) ? 20 : 6) == 0) {
			playSound(living.level(), stack, living.getX(), living.getY(), living.getZ(), living.getSoundSource(), 0.9F);
			living.gameEvent(GameEvent.INSTRUMENT_PLAY);
		}
	}

	public static void playSound(Level world, ItemStack stack, double x, double y, double z, SoundSource category, float volume) {
		if (stack.isEmpty()) {
			return;
		}

		SoundEvent sound = getSound(stack);

		if (sound != null) {
			world.playSound(null, x, y, z, sound, category, volume, sound == BotaniaSounds.doit ? 1F : (world.random.nextFloat() - world.random.nextFloat()) * 0.2F + 1.0F);
		}
	}

	@Nullable
	@VisibleForTesting
	public static SoundEvent getSound(ItemStack stack) {
		if (isDOIT(stack)) {
			return BotaniaSounds.doit;
		} else {
			try {
				return BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse(ItemNBTHelper.getString(stack, TAG_SOUND, "")));
			} catch (ResourceLocationException ex) {
				return null;
			}
		}
	}

	private static boolean isDOIT(ItemStack stack) {
		return !stack.isEmpty() && stack.getHoverName().getString().equalsIgnoreCase("shia labeouf");
	}
}
