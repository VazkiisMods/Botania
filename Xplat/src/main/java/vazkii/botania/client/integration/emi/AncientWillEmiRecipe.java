package vazkii.botania.client.integration.emi;

import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;

import net.minecraft.world.item.ItemStack;

import vazkii.botania.api.item.AncientWillContainer;
import vazkii.botania.common.item.AncientWillItem;

import java.util.List;

public class AncientWillEmiRecipe extends EmiPatternCraftingRecipe {
	private final EmiStack container;
	private final List<EmiStack> wills;

	public AncientWillEmiRecipe(EmiStack container, EmiIngredient will) {
		super(List.of(container, will), container, null);
		this.container = container;
		wills = will.getEmiStacks();
	}

	@Override
	public SlotWidget getInputWidget(int slot, int x, int y) {
		if (slot == 0) {
			return new SlotWidget(container, x, y);
		} else if (slot == 1) {
			return new GeneratedSlotWidget(r -> wills.get(r.nextInt(wills.size())), unique, x, y);
		} else {
			return new SlotWidget(EmiStack.EMPTY, x, y);
		}
	}

	@Override
	public SlotWidget getOutputWidget(int x, int y) {
		return new GeneratedSlotWidget(r -> {
			ItemStack stack = container.getItemStack().copy();
			ItemStack will = wills.get(r.nextInt(wills.size())).getItemStack().copy();
			AncientWillContainer.addAncientWill(stack, ((AncientWillItem) will.getItem()).type);
			return EmiStack.of(stack);
		}, unique, x, y);
	}
}
