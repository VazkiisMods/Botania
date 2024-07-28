/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.handler;

import com.google.common.collect.ImmutableList;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.recipe.OrechidRecipe;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.*;
import java.util.function.ToIntFunction;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class OrechidManager implements ResourceManagerReloadListener {
	private static final Map<RecipeType<? extends OrechidRecipe>, Map<BlockState, List<? extends OrechidRecipe>>> BY_TYPE = new IdentityHashMap<>();
	private static final Map<RecipeType<? extends OrechidRecipe>, Object2IntOpenHashMap<BlockState>> TOTAL_WEIGHTS_WITHOUT_POSITION = new IdentityHashMap<>();

	public static void registerListener() {
		XplatAbstractions.INSTANCE.registerReloadListener(PackType.SERVER_DATA, prefix("orechid"), new OrechidManager());
	}

	@Override
	public void onResourceManagerReload(@NotNull ResourceManager manager) {
		BY_TYPE.clear();
		TOTAL_WEIGHTS_WITHOUT_POSITION.clear();
	}

	public static <T extends OrechidRecipe> Collection<T> getMatchingRecipes(
			RecipeManager manager,
			RecipeType<T> type,
			BlockState state) {
		final var byState = BY_TYPE.computeIfAbsent(type, t -> new IdentityHashMap<>());
		final var list = byState.computeIfAbsent(state, s -> {
			var builder = ImmutableList.<T>builder();
			for (var recipe : manager.getAllRecipesFor(type)) {
				if (recipe.value().getInput().test(state)) {
					builder.add(recipe.value());
				}
			}
			return builder.build();
		});

		@SuppressWarnings("unchecked") // we only add T's to this list in the above loop
		List<T> result = (List<T>) list;
		return result;
	}

	public static int getTotalDisplayWeightAt(Level level, RecipeType<? extends OrechidRecipe> type, BlockState state, @Nullable BlockPos pos) {
		return pos == null
				? getCachedTotalDisplayWeightWithoutPosition(level, type, state)
				: calculateTotalDisplayWeightAtPosition(level, type, state, pos);
	}

	private static int getCachedTotalDisplayWeightWithoutPosition(Level level, RecipeType<? extends OrechidRecipe> type, BlockState state) {
		final var byState = TOTAL_WEIGHTS_WITHOUT_POSITION.computeIfAbsent(type, t -> new Object2IntOpenHashMap<>());
		return byState.computeIfAbsent(state, s -> calculateTotalDisplayWeightAtPosition(level, type, state, null));
	}

	private static int calculateTotalDisplayWeightAtPosition(Level level, RecipeType<? extends OrechidRecipe> type, BlockState state, @Nullable BlockPos pos) {
		final var recipeList = getMatchingRecipes(level.getRecipeManager(), type, state);
		if (recipeList.isEmpty()) {
			return 0;
		}

		ToIntFunction<OrechidRecipe> weightFunction = pos != null
				? r -> r.getWeight(level, pos)
				: OrechidRecipe::getWeight;
		return recipeList.stream().mapToInt(weightFunction).sum();
	}

}
