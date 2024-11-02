package vazkii.botania.client.integration.emi;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.recipe.BotanicalBreweryRecipe;

import java.util.List;
import java.util.stream.Stream;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class BotanicalBreweryEmiRecipe extends BotaniaEmiRecipe {
	private static final ResourceLocation TEXTURE = botaniaRL("textures/gui/nei_brewery.png");
	private final List<EmiIngredient> ingredients;
	private final ResourceLocation id;

	public BotanicalBreweryEmiRecipe(RecipeHolder<? extends BotanicalBreweryRecipe> recipe, ItemStack container) {
		super(BotaniaEmiPlugin.BOTANICAL_BREWERY, recipe);
		this.ingredients = recipe.value().getIngredients().stream().map(EmiIngredient::of).toList();
		this.input = Stream.concat(Stream.of(EmiStack.of(container)), ingredients.stream()).toList();
		this.output = List.of(EmiStack.of(recipe.value().getOutput(container.copy())));
		ResourceLocation id = recipe.id();
		ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(container.getItem());
		this.id = ResourceLocation.fromNamespaceAndPath("emi", "botania/botanical_brewery/"
				+ id.getNamespace() + "/" + id.getPath() + "/"
				+ itemId.getNamespace() + "/" + itemId.getPath());
	}

	@Override
	public @Nullable ResourceLocation getId() {
		return id;
	}

	@Override
	public int getDisplayHeight() {
		return 65;
	}

	@Override
	public int getDisplayWidth() {
		return 120;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.add(new BlendTextureWidget(TEXTURE, 0, 0, 86, 55, 28, 6));
		widgets.addSlot(input.get(output.size() - 1), 10, 35);
		int sx = 58 - (ingredients.size() - 1) * 9;
		for (EmiIngredient stack : ingredients) {
			widgets.addSlot(stack, sx, 1).drawBack(false);
			sx += 18;
		}
		widgets.addSlot(output.get(0), 58, 35).recipeContext(this);
	}
}
