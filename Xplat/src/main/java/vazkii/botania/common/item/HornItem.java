/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.block.HornHarvestable;
import vazkii.botania.api.block.HornHarvestable.EnumHornType;
import vazkii.botania.common.block.flower.functional.BergamuteBlockEntity;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HornItem extends Item {
	public HornItem(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.BOW;
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity entity) {
		return 72000;
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		return ItemUtils.startUsingInstantly(world, player, hand);
	}

	@Override
	public void onUseTick(Level world, @NotNull LivingEntity living, @NotNull ItemStack stack, int time) {
		if (!world.isClientSide) {
			if (time != getUseDuration(stack, living) && time % 5 == 0) {
				living.gameEvent(GameEvent.INSTRUMENT_PLAY);
				breakGrass(world, stack, living.blockPosition(), living);
			}
			world.playSound(null, living.getX(), living.getY(), living.getZ(), BotaniaSounds.hornDoot, SoundSource.BLOCKS, 1F, 1F);
		}
	}

	private static boolean canHarvest(Level level, ItemStack stack, BlockPos pos,
			@Nullable LivingEntity user, EnumHornType type) {
		BlockState state = level.getBlockState(pos);
		BlockEntity be = level.getBlockEntity(pos);

		HornHarvestable harvestable = XplatAbstractions.INSTANCE.findHornHarvestable(level, pos, state, be);
		if (harvestable != null) {
			return harvestable.canHornHarvest(level, pos, stack, type, user);
		}
		return switch (type) {
			default -> state.getBlock() instanceof BushBlock && !state.is(BotaniaTags.Blocks.SPECIAL_FLOWERS)
					|| state.is(BotaniaTags.Blocks.HORN_OF_THE_WILD_BREAKABLE);
			case CANOPY -> state.is(BotaniaTags.Blocks.HORN_OF_THE_CANOPY_BREAKABLE)
					&& !(state.getBlock() instanceof LeavesBlock && state.getValue(LeavesBlock.PERSISTENT));
			case COVERING -> state.is(BotaniaTags.Blocks.HORN_OF_THE_COVERING_BREAKABLE);
		};
	}

	public static void breakGrass(Level world, ItemStack stack, BlockPos srcPos, @Nullable LivingEntity user) {
		EnumHornType type = null;
		if (stack.is(BotaniaItems.grassHorn)) {
			type = EnumHornType.WILD;
		} else if (stack.is(BotaniaItems.leavesHorn)) {
			type = EnumHornType.CANOPY;
		} else if (stack.is(BotaniaItems.snowHorn)) {
			type = EnumHornType.COVERING;
		}

		int range = 12 - type.ordinal() * 3;
		int rangeY = 3 + type.ordinal() * 4;
		List<BlockPos> coords = new ArrayList<>();

		for (BlockPos pos : BlockPos.betweenClosed(srcPos.offset(-range, -rangeY, -range),
				srcPos.offset(range, rangeY, range))) {
			if (BergamuteBlockEntity.isBergamuteNearby(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)) {
				continue;
			}

			if (HornItem.canHarvest(world, stack, pos, user, type)) {
				coords.add(pos.immutable());
			}
		}

		Collections.shuffle(coords);

		int count = Math.min(coords.size(), 32 + type.ordinal() * 16);
		for (int i = 0; i < count; i++) {
			BlockPos currCoords = coords.get(i);
			BlockState state = world.getBlockState(currCoords);
			BlockEntity be = world.getBlockEntity(currCoords);
			HornHarvestable harvestable = XplatAbstractions.INSTANCE.findHornHarvestable(world, currCoords, state, be);

			if (harvestable != null && harvestable.hasSpecialHornHarvest(world, currCoords, stack, type, user)) {
				harvestable.harvestByHorn(world, currCoords, stack, type, user);
			} else {
				world.destroyBlock(currCoords, true);
			}
		}
	}

}
