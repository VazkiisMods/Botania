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
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.api.recipe.StateIngredientType;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
		public static final MapCodec<AllOfExcludingStateIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				ExtraCodecs.nonEmptyList(StateIngredients.TYPED_CODEC.listOf())
						.fieldOf("ingredients").forGetter(AllOfExcludingStateIngredient::getIngredients),
				StateIngredients.TYPED_CODEC.listOf()
						.optionalFieldOf("excluding", List.of()).forGetter(AllOfExcludingStateIngredient::getExclusions)
		).apply(instance, AllOfExcludingStateIngredient::new));
		public static final StreamCodec<RegistryFriendlyByteBuf, AllOfExcludingStateIngredient> STREAM_CODEC = StreamCodec.composite(
				StateIngredients.TYPED_STREAM_CODEC.apply(ByteBufCodecs.list()), AllOfExcludingStateIngredient::getIngredients,
				StateIngredients.TYPED_STREAM_CODEC.apply(ByteBufCodecs.list()), AllOfExcludingStateIngredient::getExclusions,
				AllOfExcludingStateIngredient::new
		);

		@Override
		public MapCodec<AllOfExcludingStateIngredient> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, AllOfExcludingStateIngredient> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
