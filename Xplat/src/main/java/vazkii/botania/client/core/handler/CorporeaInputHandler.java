/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.core.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import vazkii.botania.client.core.RecipeBookAccess;
import vazkii.botania.client.core.proxy.ClientProxy;
import vazkii.botania.common.block.block_entity.corporea.CorporeaIndexBlockEntity;
import vazkii.botania.mixin.client.AbstractContainerScreenAccessor;
import vazkii.botania.mixin.client.RecipeBookComponentAccessor;
import vazkii.botania.mixin.client.RecipeBookPageAccessor;
import vazkii.botania.network.serverbound.IndexKeybindRequestPacket;
import vazkii.botania.xplat.ClientXplatAbstractions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CorporeaInputHandler {

	public static final List<Supplier<ItemStack>> hoveredStackGetters = new ArrayList<>();
	public static Predicate<Screen> supportedGuiFilter = gui -> gui instanceof AbstractContainerScreen;

	public static boolean buttonPressed(int keyCode, int scanCode) {
		Minecraft mc = Minecraft.getInstance();

		if (mc.level == null || !supportedGuiFilter.test(mc.screen)
				|| !ClientProxy.CORPOREA_REQUEST.matches(keyCode, scanCode)
				|| CorporeaIndexBlockEntity.getNearbyValidIndexes(mc.player).isEmpty()) {
			return false;
		}

		ItemStack stack = getStackUnderMouse();
		if (stack != null && !stack.isEmpty()) {
			int count = 1;
			int max = stack.getMaxStackSize();

			if (Screen.hasShiftDown()) {
				count = max;
				if (Screen.hasControlDown()) {
					count /= 4;
				}
			} else if (Screen.hasControlDown()) {
				count = max / 2;
			}

			if (count > 0) {
				ItemStack requested = stack.copyWithCount(count);
				ClientXplatAbstractions.INSTANCE.sendToServer(new IndexKeybindRequestPacket(requested));
				return true;
			}
		}
		return false;
	}

	private static ItemStack getStackUnderMouse() {
		Screen screen = Minecraft.getInstance().screen;
		if (screen instanceof AbstractContainerScreen) {
			Slot slotUnderMouse = ((AbstractContainerScreenAccessor) screen).getHoveredSlot();
			if (slotUnderMouse != null && slotUnderMouse.hasItem()) {
				return slotUnderMouse.getItem().copy();
			}

			if (screen instanceof RecipeUpdateListener recipeScreen && recipeScreen.getRecipeBookComponent().isVisible()) {
				RecipeBookComponent recipeBook = recipeScreen.getRecipeBookComponent();
				RecipeBookPage page = ((RecipeBookComponentAccessor) recipeBook).getRecipesArea();
				RecipeButton widget = ((RecipeBookPageAccessor) page).getHoveredButton();
				if (widget != null) {
					return widget.getRecipe().value().getResultItem(Minecraft.getInstance().level.registryAccess());
				}
				ItemStack stack = ((RecipeBookAccess) recipeBook).getHoveredGhostRecipeStack();
				if (stack != null) {
					return stack;
				}
			}
		}

		for (var getter : CorporeaInputHandler.hoveredStackGetters) {
			ItemStack stack = getter.get();
			if (!stack.isEmpty()) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}
}
