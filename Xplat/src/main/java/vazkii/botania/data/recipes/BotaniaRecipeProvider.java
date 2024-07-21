/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.data.recipes;

import com.google.common.collect.Sets;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

// [VanillaCopy] RecipeProvider's non-static implementation, except getName() is not final
public abstract class BotaniaRecipeProvider implements DataProvider {
	private final PackOutput.PathProvider recipePathProvider;
	private final PackOutput.PathProvider advancementPathProvider;

	public BotaniaRecipeProvider(PackOutput packOutput) {
		this.recipePathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "recipes");
		this.advancementPathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
	}

	@Override
	public CompletableFuture<?> run(CachedOutput output) {
		final Set<ResourceLocation> set = Sets.<ResourceLocation>newHashSet();
		final List<CompletableFuture<?>> list = new ArrayList();
		this.buildRecipes(new RecipeOutput() {
			@Override
			public void accept(ResourceLocation location, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
				if (!set.add(location)) {
					throw new IllegalStateException("Duplicate recipe " + location);
				} else {
					list.add(DataProvider.saveStable(output, Recipe.CODEC, recipe, recipePathProvider.json(location)));
					if (advancement != null) {
						list.add(DataProvider.saveStable(output, Advancement.CODEC, advancement.value(), advancementPathProvider.json(advancement.id())));
					}
				}
			}

			@Override
			public Advancement.Builder advancement() {
				//noinspection removal
				return Advancement.Builder.recipeAdvancement().parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
			}
		});
		return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
	}

	public CompletableFuture<?> buildAdvancement(CachedOutput output, AdvancementHolder advancementBuilder) {
		return DataProvider.saveStable(output, Advancement.CODEC, advancementBuilder.value(), this.advancementPathProvider.json(advancementBuilder.id()));
	}

	public abstract void buildRecipes(RecipeOutput recipeOutput);
}
