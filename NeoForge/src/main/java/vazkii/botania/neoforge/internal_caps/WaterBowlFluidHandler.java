/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 */

package vazkii.botania.neoforge.internal_caps;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStackSimple;

public class WaterBowlFluidHandler extends FluidHandlerItemStackSimple.SwapEmpty {
	public WaterBowlFluidHandler(ItemStack stack) {
		super(null /* TODO: define fluid content component */, stack, new ItemStack(Items.BOWL),
				FluidType.BUCKET_VOLUME);
		setFluid(new FluidStack(Fluids.WATER, FluidType.BUCKET_VOLUME));
	}

	@Override
	public boolean canFillFluidType(FluidStack fluid) {
		return false;
	}

	@Override
	public boolean canDrainFluidType(FluidStack fluid) {
		return fluid.getFluid() == Fluids.WATER;
	}
}
