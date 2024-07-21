/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.integration.rei;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import me.shedaniel.math.Point;
import me.shedaniel.math.impl.PointHelper;
import me.shedaniel.rei.api.client.REIRuntime;
import me.shedaniel.rei.api.client.gui.screen.DisplayScreen;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.overlay.OverlayListWidget;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.common.BuiltinPlugin;
import me.shedaniel.rei.plugin.common.displays.DefaultStrippingDisplay;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomDisplay;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.item.AncientWillContainer;
import vazkii.botania.client.core.handler.CorporeaInputHandler;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.crafting.*;
import vazkii.botania.common.item.AncientWillItem;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.equipment.tool.terrasteel.TerraShattererItem;
import vazkii.botania.common.item.lens.LensItem;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.fabric.xplat.FabricXplatImpl;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

public class BotaniaREIPlugin implements REIClientPlugin {
	private static final Supplier<ItemStack> HOVERED_STACK_GETTER = BotaniaREIPlugin::getHoveredREIStack;

	public BotaniaREIPlugin() {
		if (!CorporeaInputHandler.hoveredStackGetters.contains(HOVERED_STACK_GETTER)) {
			CorporeaInputHandler.hoveredStackGetters.add(HOVERED_STACK_GETTER);
		}
		CorporeaInputHandler.supportedGuiFilter = CorporeaInputHandler.supportedGuiFilter.or(s -> s instanceof DisplayScreen);
	}

	@Override
	public void registerCategories(CategoryRegistry helper) {
		helper.add(List.of(
				new PetalApothecaryREICategory(),
				new PureDaisyREICategory(),
				new ManaPoolREICategory(),
				new RunicAltarREICategory(),
				new ElvenTradeREICategory(),
				new BreweryREICategory(),
				new TerrestrialAgglomerationREICategory(),
				new OrechidREICategory(BotaniaREICategoryIdentifiers.ORECHID, BotaniaFlowerBlocks.orechid),
				new OrechidREICategory(BotaniaREICategoryIdentifiers.ORECHID_IGNEM, BotaniaFlowerBlocks.orechidIgnem),
				new MarimorphosisREICategory()
		));

		helper.addWorkstations(BuiltinPlugin.CRAFTING, EntryStacks.of(BotaniaItems.craftingHalo), EntryStacks.of(BotaniaItems.autocraftingHalo));
		for (Block apothecary : BotaniaBlocks.ALL_APOTHECARIES) {
			helper.addWorkstations(BotaniaREICategoryIdentifiers.PETAL_APOTHECARY, EntryStacks.of(apothecary));
		}
		helper.addWorkstations(BotaniaREICategoryIdentifiers.BREWERY, EntryStacks.of(BotaniaBlocks.brewery));
		helper.addWorkstations(BotaniaREICategoryIdentifiers.ELVEN_TRADE, EntryStacks.of(BotaniaBlocks.alfPortal));
		Set<Block> manaPools = ImmutableSet.of(
				BotaniaBlocks.manaPool,
				BotaniaBlocks.dilutedPool,
				BotaniaBlocks.fabulousPool
		);
		for (Block pool : manaPools) {
			helper.addWorkstations(BotaniaREICategoryIdentifiers.MANA_INFUSION, EntryStacks.of(pool));
		}
		helper.addWorkstations(BotaniaREICategoryIdentifiers.ORECHID, EntryStacks.of(BotaniaFlowerBlocks.orechid), EntryStacks.of(BotaniaFlowerBlocks.orechidFloating));
		helper.addWorkstations(BotaniaREICategoryIdentifiers.ORECHID_IGNEM, EntryStacks.of(BotaniaFlowerBlocks.orechidIgnem), EntryStacks.of(BotaniaFlowerBlocks.orechidIgnemFloating));
		helper.addWorkstations(BotaniaREICategoryIdentifiers.MARIMORPHOSIS, EntryStacks.of(BotaniaFlowerBlocks.marimorphosis), EntryStacks.of(BotaniaFlowerBlocks.marimorphosisFloating),
				EntryStacks.of(BotaniaFlowerBlocks.marimorphosisChibi), EntryStacks.of(BotaniaFlowerBlocks.marimorphosisChibiFloating));
		helper.addWorkstations(BotaniaREICategoryIdentifiers.PURE_DAISY, EntryStacks.of(BotaniaFlowerBlocks.pureDaisy), EntryStacks.of(BotaniaFlowerBlocks.pureDaisyFloating));
		helper.addWorkstations(BotaniaREICategoryIdentifiers.RUNE_ALTAR, EntryStacks.of(BotaniaBlocks.runeAltar));
		helper.addWorkstations(BotaniaREICategoryIdentifiers.TERRA_PLATE, EntryStacks.of(BotaniaBlocks.terraPlate));

		helper.setPlusButtonArea(BotaniaREICategoryIdentifiers.PETAL_APOTHECARY, null);
		helper.setPlusButtonArea(BotaniaREICategoryIdentifiers.BREWERY, null);
		helper.setPlusButtonArea(BotaniaREICategoryIdentifiers.ELVEN_TRADE, null);
		helper.setPlusButtonArea(BotaniaREICategoryIdentifiers.MANA_INFUSION, null);
		helper.setPlusButtonArea(BotaniaREICategoryIdentifiers.ORECHID, null);
		helper.setPlusButtonArea(BotaniaREICategoryIdentifiers.ORECHID_IGNEM, null);
		helper.setPlusButtonArea(BotaniaREICategoryIdentifiers.MARIMORPHOSIS, null);
		helper.setPlusButtonArea(BotaniaREICategoryIdentifiers.PURE_DAISY, null);
		helper.setPlusButtonArea(BotaniaREICategoryIdentifiers.RUNE_ALTAR, null);
		helper.setPlusButtonArea(BotaniaREICategoryIdentifiers.TERRA_PLATE, null);
	}

	@Override
	public void registerDisplays(DisplayRegistry helper) {
		registerAncientWillRecipeWrapper(helper);
		registerCompositeLensRecipeWrapper(helper);
		registerTerraPickTippingRecipeWrapper(helper);

		helper.registerFiller(PetalApothecaryRecipe.class, PetalApothecaryREIDisplay::new);
		helper.registerFiller(BotanicalBreweryRecipe.class, BreweryREIDisplay::new);
		Predicate<? extends ElvenTradeRecipe> pred = recipe -> !recipe.isReturnRecipe();
		helper.registerFiller(ElvenTradeRecipe.class, pred, ElvenTradeREIDisplay::new);
		helper.registerFiller(LexiconElvenTradeRecipe.class, ElvenTradeREIDisplay::new);
		helper.registerFiller(ManaInfusionRecipe.class, ManaPoolREIDisplay::new);
		helper.registerFiller(PureDaisyRecipe.class, PureDaisyREIDisplay::new);
		helper.registerFiller(RunicAltarRecipe.class, RunicAltarREIDisplay::new);
		helper.registerFiller(TerrestrialAgglomerationRecipe.class, TerrestrialAgglomerationREIDisplay::new);

		try {
			for (var entry : FabricXplatImpl.CUSTOM_STRIPPING.entrySet()) {
				helper.add(new DefaultStrippingDisplay(EntryStacks.of(entry.getKey()), EntryStacks.of(entry.getValue())));
			}
		} catch (Exception e) {
			BotaniaAPI.LOGGER.error("Error adding strippable entry to REI", e);
		}

		helper.registerRecipeFiller(OrechidRecipe.class, BotaniaRecipeTypes.ORECHID_TYPE,
				OrechidREIDisplay::new);
		helper.registerRecipeFiller(OrechidIgnemRecipe.class, BotaniaRecipeTypes.ORECHID_IGNEM_TYPE,
				OrechidIgnemREIDisplay::new);
		helper.registerRecipeFiller(MarimorphosisRecipe.class, BotaniaRecipeTypes.MARIMORPHOSIS_TYPE,
				MarimorphosisREIDisplay::new);
	}

	void registerAncientWillRecipeWrapper(DisplayRegistry helper) {
		ImmutableList.Builder<EntryIngredient> input = ImmutableList.builder();
		ImmutableList.Builder<EntryStack<ItemStack>> output = ImmutableList.builder();
		Set<ItemStack> wills = ImmutableSet.of(new ItemStack(BotaniaItems.ancientWillAhrim), new ItemStack(BotaniaItems.ancientWillDharok), new ItemStack(BotaniaItems.ancientWillGuthan), new ItemStack(BotaniaItems.ancientWillKaril), new ItemStack(BotaniaItems.ancientWillTorag), new ItemStack(BotaniaItems.ancientWillVerac));
		AncientWillContainer container = (AncientWillContainer) BotaniaItems.terrasteelHelm;

		ItemStack helmet = new ItemStack(BotaniaItems.terrasteelHelm);
		input.add(EntryIngredients.of(helmet));
		input.add(EntryIngredients.ofItemStacks(wills));
		for (ItemStack will : wills) {
			ItemStack copy = helmet.copy();
			container.addAncientWill(copy, ((AncientWillItem) will.getItem()).type);
			output.add(EntryStacks.of(copy));
		}
		helper.add(new DefaultCustomDisplay(null, input.build(), Collections.singletonList(EntryIngredient.of(output.build()))));
	}

	void registerCompositeLensRecipeWrapper(DisplayRegistry helper) {
		List<ItemStack> lensStacks =
				StreamSupport.stream(BuiltInRegistries.ITEM.getTagOrEmpty(BotaniaTags.Items.LENS).spliterator(), false)
						.map(ItemStack::new)
						.filter(s -> !((LensItem) s.getItem()).isControlLens(s))
						.filter(s -> ((LensItem) s.getItem()).isCombinable(s))
						.toList();
		List<Item> lenses = lensStacks.stream().map(ItemStack::getItem).toList();
		List<EntryIngredient> inputs = Arrays.asList(EntryIngredients.ofItemStacks(lensStacks), EntryIngredients.of(new ItemStack(Items.SLIME_BALL)), EntryIngredients.ofItemStacks(lensStacks));
		int end = lenses.size() - 1;

		List<ItemStack> firstInput = new ArrayList<>();
		List<ItemStack> secondInput = new ArrayList<>();
		List<ItemStack> outputs = new ArrayList<>();
		for (int i = 1; i <= end; i++) {
			ItemStack firstLens = new ItemStack(lenses.get(i));
			for (Item secondLens : lenses) {
				if (secondLens == firstLens.getItem()) {
					continue;
				}

				ItemStack secondLensStack = new ItemStack(secondLens);
				if (((LensItem) firstLens.getItem()).canCombineLenses(firstLens, secondLensStack)) {
					firstInput.add(firstLens);
					secondInput.add(secondLensStack);
					outputs.add(((LensItem) firstLens.getItem()).setCompositeLens(firstLens.copy(), secondLensStack));
				}
			}
		}
		inputs.set(0, EntryIngredients.ofItemStacks(firstInput));
		inputs.set(2, EntryIngredients.ofItemStacks(secondInput));

		helper.add(new DefaultCustomDisplay(null, inputs, Collections.singletonList(EntryIngredients.ofItemStacks(outputs))));
	}

	void registerTerraPickTippingRecipeWrapper(DisplayRegistry helper) {
		List<EntryIngredient> inputs = ImmutableList.of(EntryIngredients.of(BotaniaItems.terraPick), EntryIngredients.of(BotaniaItems.elementiumPick));
		ItemStack output = new ItemStack(BotaniaItems.terraPick);
		TerraShattererItem.setTipped(output);

		helper.add(new DefaultCustomDisplay(null, inputs, Collections.singletonList(EntryIngredients.of(output))));
	}

	private static ItemStack getHoveredREIStack() {
		return REIRuntime.getInstance().getOverlay().map(o -> {
			ItemStack stack;
			if (REIRuntime.getInstance().isOverlayVisible()) {
				stack = unwrapEntry(o.getEntryList().getFocusedStack());
				if (!stack.isEmpty()) {
					return stack;
				}
				stack = o.getFavoritesList()
						.map(OverlayListWidget::getFocusedStack)
						.map(BotaniaREIPlugin::unwrapEntry)
						.orElse(ItemStack.EMPTY);
				if (!stack.isEmpty()) {
					return stack;
				}
			}
			if (Minecraft.getInstance().screen instanceof DisplayScreen) {
				Point point = PointHelper.ofMouse();
				for (var child : Minecraft.getInstance().screen.children()) {
					if (child.isMouseOver(point.x, point.y) && child instanceof Slot slot) {
						stack = unwrapEntry(slot.getCurrentEntry());
						if (!stack.isEmpty()) {
							return stack;
						}
					}
				}
			}
			return ItemStack.EMPTY;
		}).orElse(ItemStack.EMPTY);
	}

	private static ItemStack unwrapEntry(@Nullable EntryStack<?> stack) {
		if (stack != null && !stack.isEmpty() && stack.getType() == VanillaEntryTypes.ITEM) {
			return (ItemStack) stack.getValue();
		}
		return ItemStack.EMPTY;
	}
}
