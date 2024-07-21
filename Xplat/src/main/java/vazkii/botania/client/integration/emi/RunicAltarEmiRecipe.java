package vazkii.botania.client.integration.emi;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

import vazkii.botania.api.recipe.RunicAltarRecipe;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;
import vazkii.botania.common.item.material.RuneItem;

import java.util.List;
import java.util.stream.Stream;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class RunicAltarEmiRecipe extends BotaniaEmiRecipe {
	private static final ResourceLocation TEXTURE = prefix("textures/gui/petal_overlay.png");
	private static final EmiStack LIVINGROCK = EmiStack.of(BotaniaBlocks.livingrock);
	private static final EmiStack ALTAR = EmiStack.of(BotaniaBlocks.runeAltar);
	public static final int CENTER_X = 44;
	public static final int CENTER_Y = 40;
	public static final int POS_X_INGREDIENTS = CENTER_X;
	public static final int POS_Y_INGREDIENTS = 8;
	public static final int POS_X_REAGENTS = CENTER_X;
	public static final int POS_Y_REAGENTS = 30;
	private final List<EmiIngredient> ingredients;
	private final int mana;

	public RunicAltarEmiRecipe(RunicAltarRecipe recipe) {
		super(BotaniaEmiPlugin.RUNIC_ALTAR, recipe);
		this.ingredients = recipe.getIngredients().stream().map(EmiIngredient::of).toList();
		this.input = Stream.concat(ingredients.stream(), Stream.of(LIVINGROCK)).toList();
		// TODO classify these as catalysts instead?
		for (EmiIngredient ing : input) {
			for (EmiStack stack : ing.getEmiStacks()) {
				if (stack.getItemStack().getItem() instanceof RuneItem) {
					stack.setRemainder(stack.copy());
				}
			}
		}
		// TODO 1.19.4 figure out the proper way to get a registry access
		this.output = List.of(EmiStack.of(recipe.getResultItem(RegistryAccess.EMPTY)));
		this.mana = recipe.getMana();
	}

	@Override
	public int getDisplayHeight() {
		return 107;
	}

	@Override
	public int getDisplayWidth() {
		return 106;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.add(new ManaWidget(2, 100, mana, ManaPoolBlockEntity.MAX_MANA / 10));
		RunicAltarEmiRecipe.addRunicAltarWidgets(widgets, this, ingredients, ALTAR, output.get(0), LIVINGROCK);
	}

	public static void addRunicAltarWidgets(WidgetHolder widgets, EmiRecipe recipe,
			List<EmiIngredient> input, EmiIngredient altar, EmiStack output, EmiIngredient... reagents) {
		double step = 360.0 / input.size();
		widgets.add(new BlendTextureWidget(TEXTURE, 21, 0, 85, 82, 42, 11));
		for (int i = 0; i < input.size(); i++) {
			EmiIngredient ing = input.get(i);
			widgets.addSlot(ing, BotaniaEmiPlugin.rotateXAround(POS_X_INGREDIENTS, POS_Y_INGREDIENTS, CENTER_X, CENTER_Y, step * i),
					BotaniaEmiPlugin.rotateYAround(POS_X_INGREDIENTS, POS_Y_INGREDIENTS, CENTER_X, CENTER_Y, step * i)).drawBack(false);
		}
		if (reagents.length > 0) {
			double reagentStep = 360.0 / (reagents.length + 1);
			widgets.addSlot(altar, BotaniaEmiPlugin.rotateXAround(POS_X_REAGENTS, POS_Y_REAGENTS + 1, CENTER_X, CENTER_Y, 0),
					BotaniaEmiPlugin.rotateYAround(POS_X_REAGENTS, POS_Y_REAGENTS, CENTER_X, CENTER_Y, 0)).drawBack(false);
			for (int i = 0; i < reagents.length; i++) {
				EmiIngredient ing = reagents[i];
				widgets.addSlot(ing, BotaniaEmiPlugin.rotateXAround(POS_X_REAGENTS, POS_Y_REAGENTS, CENTER_X, CENTER_Y, reagentStep * (i + 1)),
						BotaniaEmiPlugin.rotateYAround(POS_X_REAGENTS, POS_Y_REAGENTS, CENTER_X, CENTER_Y, reagentStep * (i + 1))).drawBack(false);
			}
		} else {
			widgets.addSlot(altar, CENTER_X, CENTER_Y + 1).drawBack(false).catalyst(true);
		}
		widgets.addSlot(output, CENTER_X + 38, 5).drawBack(false).recipeContext(recipe);
	}
}
