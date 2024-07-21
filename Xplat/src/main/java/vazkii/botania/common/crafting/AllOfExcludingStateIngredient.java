/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.api.recipe.StateIngredientType;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A dual-purpose state ingredient variant.
 * Valid states must match all the specified ingredients, but none of the exclusions.
 */
public class AllOfExcludingStateIngredient implements StateIngredient {
	private final ImmutableSet<StateIngredient> ingredients;
	private final ImmutableSet<StateIngredient> exclusions;

	private final Supplier<ImmutableList<BlockState>> resolvedBlocksStates = Suppliers.memoize(this::resolve);

	public AllOfExcludingStateIngredient(Collection<StateIngredient> ingredients, Collection<StateIngredient> exclusions) {
		this.ingredients = ImmutableSet.copyOf(ingredients);
		this.exclusions = ImmutableSet.copyOf(exclusions);
	}

	private ImmutableList<BlockState> resolve() {
		var set = new HashSet<>(this.ingredients.stream()
				.map(StateIngredient::streamBlockStates)
				.map(stream -> (Set<BlockState>) new HashSet<>(stream.collect(Collectors.toSet())))
				.reduce((a, b) -> {
					a.retainAll(b);
					return a;
				})
				.orElse(Collections.emptySet()));
		set.removeIf(state -> exclusions.stream().anyMatch(exclusion -> exclusion.test(state)));
		return ImmutableList.copyOf(set);
	}

	@Override
	public boolean test(BlockState state) {
		return resolvedBlocksStates.get().contains(state);
	}

	@Override
	public BlockState pick(RandomSource random) {
		var states = resolvedBlocksStates.get();
		return states.isEmpty() ? Blocks.AIR.defaultBlockState() : states.get(random.nextInt(states.size()));
	}

	@Override
	public StateIngredientType<AllOfExcludingStateIngredient> getType() {
		return StateIngredients.ALL_OF_EXCLUDING;
	}

	@Override
	public List<ItemStack> getDisplayedStacks() {
		return streamBlockStates()
				.filter(state -> state.is(Blocks.AIR))
				.map(BlockState::getBlock)
				.map(ItemStack::new)
				.toList();
	}

	@Override
	public List<BlockState> getDisplayed() {
		return resolvedBlocksStates.get();
	}

	@Override
	public Stream<BlockState> streamBlockStates() {
		return resolvedBlocksStates.get().stream();
	}

	public ImmutableList<StateIngredient> getIngredients() {
		return ingredients.asList();
	}

	public ImmutableList<StateIngredient> getExclusions() {
		return exclusions.asList();
	}

	@Override
	public String toString() {
		return "AllOfExcludingStateIngredient{" + getIngredients() + "}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		AllOfExcludingStateIngredient that = (AllOfExcludingStateIngredient) o;

		return Objects.equals(this.ingredients, that.ingredients)
				&& Objects.equals(this.exclusions, that.exclusions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ingredients, exclusions);
	}

	public static class Type implements StateIngredientType<AllOfExcludingStateIngredient> {
		public static final Codec<AllOfExcludingStateIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.nonEmptyList(StateIngredients.TYPED_CODEC.listOf())
						.fieldOf("ingredients").forGetter(AllOfExcludingStateIngredient::getIngredients),
				StateIngredients.TYPED_CODEC.listOf()
						.optionalFieldOf("excluding", List.of()).forGetter(AllOfExcludingStateIngredient::getExclusions)
		).apply(instance, AllOfExcludingStateIngredient::new));

		@Override
		public Codec<AllOfExcludingStateIngredient> codec() {
			return CODEC;
		}

		@Override
		public AllOfExcludingStateIngredient fromNetwork(FriendlyByteBuf buffer) {
			int numIngredients = buffer.readInt();
			NonNullList<StateIngredient> ingredients = NonNullList.withSize(numIngredients, StateIngredients.NONE);
			for (int i = 0; i < numIngredients; i++) {
				ingredients.set(i, StateIngredients.fromNetwork(buffer));
			}
			int numExclusions = buffer.readInt();
			NonNullList<StateIngredient> exclusions = NonNullList.withSize(numExclusions, StateIngredients.NONE);
			for (int i = 0; i < numExclusions; i++) {
				exclusions.set(i, StateIngredients.fromNetwork(buffer));
			}
			return new AllOfExcludingStateIngredient(ingredients, exclusions);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, AllOfExcludingStateIngredient allOfExcludingStateIngredient) {
			buffer.writeInt(allOfExcludingStateIngredient.getIngredients().size());
			for (var ingredient : allOfExcludingStateIngredient.getIngredients()) {
				StateIngredients.toNetwork(buffer, ingredient);
			}
			buffer.writeInt(allOfExcludingStateIngredient.getExclusions().size());
			for (var ingredient : allOfExcludingStateIngredient.getExclusions()) {
				StateIngredients.toNetwork(buffer, ingredient);
			}
		}
	}
}
