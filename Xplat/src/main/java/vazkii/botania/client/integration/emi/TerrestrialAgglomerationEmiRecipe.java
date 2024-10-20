package vazkii.botania.client.integration.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import vazkii.botania.api.recipe.TerrestrialAgglomerationRecipe;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;

import java.util.List;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class TerrestrialAgglomerationEmiRecipe extends BotaniaEmiRecipe {
	private static final ResourceLocation TEXTURE = botaniaRL("textures/gui/terrasteel_jei_overlay.png");
	private static final EmiStack PLATE = EmiStack.of(BotaniaBlocks.terraPlate);
	public static final int CENTER_X = 45;
	public static final int CENTER_Y = 30;
	private final int mana;

	public TerrestrialAgglomerationEmiRecipe(RecipeHolder<? extends TerrestrialAgglomerationRecipe> recipe) {
		super(BotaniaEmiPlugin.TERRESTRIAL_AGGLOMERATION, recipe);
		this.input = recipe.value().getIngredients().stream().map(EmiIngredient::of).toList();
		// TODO 1.19.4 figure out the proper way to get a registry access
		this.output = List.of(EmiStack.of(recipe.value().getResultItem(RegistryAccess.EMPTY)));
		this.mana = recipe.value().getMana();
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
		widgets.add(new ManaWidget(2, 100, mana, ManaPoolBlockEntity.MAX_MANA));
		double step = 360.0 / input.size();
		widgets.add(new BlendTextureWidget(TEXTURE, CENTER_X - 23, CENTER_Y - 23, 64, 64, 42, 29));
		for (int i = 0; i < input.size(); i++) {
			EmiIngredient ing = input.get(i);
			widgets.addSlot(ing, BotaniaEmiPlugin.rotateXAround(CENTER_X, CENTER_Y - 30, CENTER_X, CENTER_Y, step * i),
					BotaniaEmiPlugin.rotateYAround(CENTER_X, CENTER_Y - 30, CENTER_X, CENTER_Y, step * i)).drawBack(false);
		}
		widgets.addSlot(PLATE, CENTER_X, 80).drawBack(false).catalyst(true);
		widgets.addSlot(output.get(0), CENTER_X, CENTER_Y).drawBack(false).recipeContext(this);
	}
}
