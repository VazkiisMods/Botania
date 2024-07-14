/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.material.EnderAirItem;

public class EnderAirBottlingBehavior extends OptionalDispenseItemBehavior {
	private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();
	private final DispenseItemBehavior parent;

	public EnderAirBottlingBehavior(DispenseItemBehavior parent) {
		this.parent = parent;
	}

	@Override
	protected void playSound(BlockSource source) {
		if (this.isSuccess()) {
			super.playSound(source);
		}
	}

	@Override
	protected void playAnimation(BlockSource source, Direction facingIn) {
		if (this.isSuccess()) {
			super.playAnimation(source, facingIn);
		}
	}

	private static boolean pickupInEnd(Level world, BlockPos facingPos) {
		return world.dimension() == Level.END
				&& world.isEmptyBlock(facingPos) && world.isEmptyBlock(facingPos.above())
				&& EnderAirItem.isClearFromDragonBreath(world, new AABB(facingPos).inflate(2.0D));
	}

	@NotNull
	@Override
	protected ItemStack execute(BlockSource source, @NotNull ItemStack stack) {
		Level world = source.level();
		BlockPos blockpos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
		if (pickupInEnd(world, blockpos) || EnderAirItem.pickupFromEntity(world, new AABB(blockpos))) {
			this.setSuccess(true);
			return fillBottle(source, stack, new ItemStack(BotaniaItems.enderAirBottle));
		}
		this.setSuccess(false);
		return parent.dispense(source, stack);
	}

	private ItemStack fillBottle(BlockSource source, ItemStack input, ItemStack output) {
		input.shrink(1);
		if (input.isEmpty()) {
			return output.copy();
		} else {
			if (source.blockEntity().addItem(output.copy()) < 0) {
				this.defaultBehaviour.dispense(source, output.copy());
			}
			return input;
		}
	}
}
