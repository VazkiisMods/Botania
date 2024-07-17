/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.equipment.tool.terrasteel;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.SequentialBreaker;
import vazkii.botania.api.mana.ManaBarTooltip;
import vazkii.botania.api.mana.ManaItem;
import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.CustomCreativeTabContents;
import vazkii.botania.common.item.StoneOfTemperanceItem;
import vazkii.botania.common.item.equipment.tool.ToolCommons;
import vazkii.botania.common.item.equipment.tool.manasteel.ManasteelPickaxeItem;
import vazkii.botania.common.item.relic.RingOfThorItem;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class TerraShattererItem extends ManasteelPickaxeItem implements SequentialBreaker, CustomCreativeTabContents {

	private static final String TAG_ENABLED = "enabled";
	private static final String TAG_MANA = "mana";
	private static final String TAG_TIPPED = "tipped";

	private static final int MAX_MANA = Integer.MAX_VALUE;
	private static final int MANA_PER_DAMAGE = 100;

	public static final int[] LEVELS = new int[] {
			0, 10000, 1000000, 10000000, 100000000, 1000000000
	};

	private static final int[] CREATIVE_MANA = new int[] {
			10000 - 1, 1000000 - 1, 10000000 - 1, 100000000 - 1, 1000000000 - 1, MAX_MANA - 1
	};

	public TerraShattererItem(Properties props) {
		super(BotaniaAPI.instance().getTerrasteelItemTier(), props, -2.8F);
	}

	@Override
	public void addToCreativeTab(Item me, CreativeModeTab.Output output) {
		output.accept(this);
		for (int mana : CREATIVE_MANA) {
			ItemStack stack = new ItemStack(this);
			setMana(stack, mana);
			output.accept(stack);
		}
		ItemStack stack = new ItemStack(this);
		setMana(stack, CREATIVE_MANA[1]);
		setTipped(stack);
		output.accept(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> stacks, TooltipFlag flags) {
		Component rank = Component.translatable("botania.rank" + getLevel(stack));
		Component rankFormat = Component.translatable("botaniamisc.toolRank", rank);
		stacks.add(rankFormat);
		var manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
		if (manaItem != null && manaItem.getMana() == Integer.MAX_VALUE) {
			stacks.add(Component.translatable("botaniamisc.getALife").withStyle(ChatFormatting.RED));
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isSecondaryUseActive() && hand == InteractionHand.MAIN_HAND) {
			int tier = getLevel(stack);
			BlockHitResult blockhitresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);

			if (tier != 0 && blockhitresult.getType() != HitResult.Type.BLOCK) {
				setEnabled(stack, !isEnabled(stack));
				if (!level.isClientSide) {
					level.playSound(null, player.getX(), player.getY(), player.getZ(), BotaniaSounds.terraPickMode, SoundSource.PLAYERS, 1F, 1F);
				}
				return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
			}
		}
		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);
		if (isEnabled(stack)) {
			int level = getLevel(stack);

			if (level == 0) {
				setEnabled(stack, false);
			} else if (entity instanceof Player player && !player.swinging) {
				var manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
				manaItem.addMana(-level);
			}
		}
	}

	@Override
	public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
		int level = getLevel(stack);
		int max = LEVELS[Math.min(LEVELS.length - 1, level + 1)];
		int curr = getMana_(stack);
		float percent = level == 0 ? 0F : (float) curr / (float) max;

		return Optional.of(new ManaBarTooltip(percent, level));
	}

	@SoftImplement("IItemExtension")
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
		BlockHitResult raycast = ToolCommons.raytraceFromEntity(player, 10, false);
		if (!player.level().isClientSide && raycast.getType() == HitResult.Type.BLOCK) {
			Direction face = raycast.getDirection();
			breakOtherBlock(player, stack, pos, pos, face);
			if (player.isSecondaryUseActive()) {
				BotaniaAPI.instance().breakOnAllCursors(player, stack, pos, face);
			}
		}

		return false;
	}

	@Override
	public int getManaPerDamage() {
		return MANA_PER_DAMAGE;
	}

	@Override
	public void breakOtherBlock(Player player, ItemStack stack, BlockPos pos, BlockPos originPos, Direction side) {
		if (!isEnabled(stack)) {
			return;
		}

		Level world = player.level();
		Predicate<BlockState> canMine = state -> {
			boolean rightToolForDrops = !state.requiresCorrectToolForDrops() || stack.isCorrectToolForDrops(state);
			boolean rightToolForSpeed = stack.getDestroySpeed(state) > 1
					|| state.is(BlockTags.MINEABLE_WITH_SHOVEL)
					|| state.is(BlockTags.MINEABLE_WITH_HOE);
			return rightToolForDrops && rightToolForSpeed;
		};

		BlockState targetState = world.getBlockState(pos);
		if (!canMine.test(targetState)) {
			return;
		}

		if (world.isEmptyBlock(pos)) {
			return;
		}

		boolean thor = !RingOfThorItem.getThorRing(player).isEmpty();
		boolean doX = thor || side.getStepX() == 0;
		boolean doY = thor || side.getStepY() == 0;
		boolean doZ = thor || side.getStepZ() == 0;

		int origLevel = getLevel(stack);
		int level = origLevel + (thor ? 1 : 0);
		if (StoneOfTemperanceItem.hasTemperanceActive(player) && level > 2) {
			level = 2;
		}

		int range = level - 1;
		int rangeY = Math.max(1, range);

		if (range == 0 && level != 1) {
			return;
		}

		Vec3i beginDiff = new Vec3i(doX ? -range : 0, doY ? -1 : 0, doZ ? -range : 0);
		Vec3i endDiff = new Vec3i(doX ? range : 0, doY ? rangeY * 2 - 1 : 0, doZ ? range : 0);

		ToolCommons.removeBlocksInIteration(player, stack, world, pos, beginDiff, endDiff, canMine);

		if (origLevel == 5) {
			PlayerHelper.grantCriterion((ServerPlayer) player, prefix("challenge/rank_ss_pick"), "code_triggered");
		}
	}

	public static boolean isTipped(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_TIPPED, false);
	}

	public static void setTipped(ItemStack stack) {
		ItemNBTHelper.setBoolean(stack, TAG_TIPPED, true);
	}

	public static boolean isEnabled(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_ENABLED, false);
	}

	void setEnabled(ItemStack stack, boolean enabled) {
		ItemNBTHelper.setBoolean(stack, TAG_ENABLED, enabled);
	}

	protected static void setMana(ItemStack stack, int mana) {
		if (mana > 0) {
			ItemNBTHelper.setInt(stack, TAG_MANA, mana);
		} else {
			ItemNBTHelper.removeEntry(stack, TAG_MANA);
		}
	}

	public static int getMana_(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_MANA, 0);
	}

	public static int getLevel(ItemStack stack) {
		int mana = getMana_(stack);
		for (int i = LEVELS.length - 1; i > 0; i--) {
			if (mana >= LEVELS[i]) {
				return i;
			}
		}

		return 0;
	}

	public static class ManaItemImpl implements ManaItem {
		private final ItemStack stack;

		public ManaItemImpl(ItemStack stack) {
			this.stack = stack;
		}

		@Override
		public int getMana() {
			return getMana_(stack) * stack.getCount();
		}

		@Override
		public int getMaxMana() {
			return MAX_MANA * stack.getCount();
		}

		@Override
		public void addMana(int mana) {
			setMana(stack, Math.min(getMana() + mana, getMaxMana()) / stack.getCount());
		}

		@Override
		public boolean canReceiveManaFromPool(BlockEntity pool) {
			return true;
		}

		@Override
		public boolean canReceiveManaFromItem(ItemStack otherStack) {
			return !otherStack.is(BotaniaTags.Items.TERRA_PICK_BLACKLIST);
		}

		@Override
		public boolean canExportManaToPool(BlockEntity pool) {
			return false;
		}

		@Override
		public boolean canExportManaToItem(ItemStack otherStack) {
			return false;
		}

		@Override
		public boolean isNoExport() {
			return true;
		}
	}

	@SoftImplement("IItemExtension")
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return reequipAnimation(oldStack, newStack);
	}

	@SoftImplement("FabricItem")
	public boolean allowNbtUpdateAnimation(Player player, InteractionHand hand, ItemStack before, ItemStack after) {
		return reequipAnimation(before, after);
	}

	private boolean reequipAnimation(ItemStack before, ItemStack after) {
		return !after.is(this) || isEnabled(before) != isEnabled(after);
	}

	@NotNull
	@Override
	public Rarity getRarity(@NotNull ItemStack stack) {
		int level = getLevel(stack);
		if (stack.isEnchanted()) {
			level++;
		}
		if (level >= 5) { // SS rank/enchanted S rank
			return Rarity.EPIC;
		}
		if (level >= 3) { // A rank/enchanted B rank
			return Rarity.RARE;
		}
		return Rarity.UNCOMMON;
	}
}
