/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.block_entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaAPIClient;
import vazkii.botania.api.block.Bound;
import vazkii.botania.api.block.WandBindable;
import vazkii.botania.api.block.WandHUD;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.helper.MathHelper;
import vazkii.botania.common.item.BotaniaItems;

import java.util.Objects;

/**
 * Superclass of flowers that can be bound to some kind of target with the Wand of the Forest,
 * such as generating flowers to mana collectors, or functional flowers to pools.
 * Implements the bindability logic common to both types of flower.
 */
public abstract class BindableSpecialFlowerBlockEntity<T> extends SpecialFlowerBlockEntity implements WandBindable {
	/**
	 * Superclass (or interface) of all BlockEntities that this flower is able to bind to.
	 */
	private final Class<T> bindClass;

	protected @Nullable BlockPos bindingPos = null;
	private static final String TAG_BINDING = "binding";

	public BindableSpecialFlowerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Class<T> bindClass) {
		super(type, pos, state);
		this.bindClass = bindClass;
	}

	public abstract int getBindingRadius();

	/**
	 * Returns the BlockPos of the nearest target within the binding radius, or `null` if there aren't any.
	 */
	public abstract @Nullable BlockPos findClosestTarget();

	@Override
	protected void tickFlower() {
		super.tickFlower();

		//First time the flower has been placed. This is the best time to check it; /setblock and friends don't call
		//the typical setPlacedBy method that player-placements do.
		if (Bound.UNBOUND_POS.equals(bindingPos)) {
			setBindingPos(null);
		} else if (ticksExisted == 1 && !level.isClientSide) {
			//Situations to consider:
			// the flower has been placed in the void, and there is nothing for it to bind to;
			// the flower has been placed next to a bind target, and I want to automatically bind to it;
			// the flower already has a valid binding due to ctrl-pick placement, and I should keep it;
			// the flower already has a binding from ctrl-pick placement, but it's invalid (out of range etc) and I should delete it.
			if (bindingPos == null || !isValidBinding()) {
				setBindingPos(findClosestTarget());
			}
		}
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (placer != null && placer.isHolding(BotaniaItems.obedienceStick)) {
			setBindingPos(Bound.UNBOUND_POS);
		}
		super.setPlacedBy(level, pos, state, placer, stack);
	}

	public @Nullable BlockPos getBindingPos() {
		return bindingPos;
	}

	public void setBindingPos(@Nullable BlockPos bindingPos) {
		boolean changed = !Objects.equals(this.bindingPos, bindingPos);

		this.bindingPos = bindingPos;

		if (changed) {
			setChanged();
			sync();
		}
	}

	public @Nullable T findBindCandidateAt(BlockPos pos) {
		if (level == null || pos == null) {
			return null;
		}

		BlockEntity be = level.getBlockEntity(pos);
		return bindClass.isInstance(be) ? bindClass.cast(be) : null;
	}

	public @Nullable T findBoundTile() {
		return findBindCandidateAt(bindingPos);
	}

	public boolean wouldBeValidBinding(@Nullable BlockPos pos) {
		if (level == null || pos == null || !level.isLoaded(pos) || MathHelper.distSqr(getBlockPos(), pos) > (long) getBindingRadius() * getBindingRadius()) {
			return false;
		} else {
			return findBindCandidateAt(pos) != null;
		}
	}

	public boolean isValidBinding() {
		return wouldBeValidBinding(bindingPos);
	}

	@Override
	public BlockPos getBinding() {
		//Used for Wand of the Forest overlays; only return the binding if it's valid.
		return isValidBinding() ? bindingPos : null;
	}

	@Override
	public boolean canSelect(Player player, ItemStack wand, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public boolean bindTo(Player player, ItemStack wand, BlockPos pos, Direction side) {
		if (wouldBeValidBinding(pos)) {
			setBindingPos(pos);
			return true;
		}

		return false;
	}

	@Override
	public void writeToPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.writeToPacketNBT(cmp, registries);

		if (bindingPos != null) {
			cmp.put(TAG_BINDING, NbtUtils.writeBlockPos(bindingPos));
		}
	}

	@Override
	public void readFromPacketNBT(CompoundTag cmp, HolderLookup.Provider registries) {
		super.readFromPacketNBT(cmp, registries);

		if (cmp.contains(TAG_BINDING)) {
			bindingPos = NbtUtils.readBlockPos(cmp.getCompound(TAG_BINDING));
		} else {
			//In older versions of the mod (1.16, early 1.17), GeneratingFlowerBlockEntity and SpecialFlowerBlockEntity
			//implemented their own copies of the binding logic. Read data from the old locations.
			if (cmp.contains("collectorX")) {
				bindingPos = new BlockPos(cmp.getInt("collectorX"), cmp.getInt("collectorY"), cmp.getInt("collectorZ"));
			} else if (cmp.contains("poolX")) {
				bindingPos = new BlockPos(cmp.getInt("poolX"), cmp.getInt("poolY"), cmp.getInt("poolZ"));
			}
			//These versions of the mod also sometimes used a binding with a Y of -1 to signify an unbound flower.
			//Currently, `null` is always used for unbound flowers. Coerce these positions to `null`.
			if (bindingPos != null && bindingPos.getY() == -1) {
				bindingPos = null;
			}
		}
	}

	public abstract int getMana();

	public abstract void addMana(int mana);

	public abstract int getMaxMana();

	public abstract int getColor();

	public abstract ItemStack getDefaultHudIcon();

	public ItemStack getHudIcon() {
		T boundTile = findBoundTile();
		if (boundTile != null) {
			return new ItemStack(((BlockEntity) boundTile).getBlockState().getBlock().asItem());
		}
		return getDefaultHudIcon();
	}

	public static class BindableFlowerWandHud<F extends BindableSpecialFlowerBlockEntity<?>> implements WandHUD {
		protected final F flower;

		public BindableFlowerWandHud(F flower) {
			this.flower = flower;
		}

		public void renderHUD(GuiGraphics gui, Minecraft mc, int minLeft, int minRight, int minDown) {
			String name = I18n.get(flower.getBlockState().getBlock().getDescriptionId());
			int color = flower.getColor();

			int centerX = mc.getWindow().getGuiScaledWidth() / 2;
			int centerY = mc.getWindow().getGuiScaledHeight() / 2;
			int left = (Math.max(102, mc.font.width(name)) + 4) / 2;
			// padding + item
			int right = left + 20;

			left = Math.max(left, minLeft);
			right = Math.max(right, minRight);

			RenderHelper.renderHUDBox(gui, centerX - left, centerY + 8, centerX + right, centerY + Math.max(30, minDown));

			BotaniaAPIClient.instance().drawComplexManaHUD(gui, color, flower.getMana(), flower.getMaxMana(),
					name, flower.getHudIcon(), flower.isValidBinding());
		}

		@Override
		public void renderHUD(GuiGraphics gui, Minecraft mc) {
			renderHUD(gui, mc, 0, 0, 0);
		}
	}
}
