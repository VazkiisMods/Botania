/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.crafting;

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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Specifies a state ingredient with one or more sub-ingredients.
 * Valid states must match at least one of the specified ingredients.
 */
public class AnyOfStateIngredient implements StateIngredient {
	private final ImmutableSet<StateIngredient> ingredients;

	public AnyOfStateIngredient(Collection<StateIngredient> blocks) {
		this.ingredients = ImmutableSet.copyOf(blocks);
	}

	@Override
	public boolean test(BlockState state) {
		return ingredients.stream().anyMatch(ingredient -> ingredient.test(state));
	}

	@Override
	public BlockState pick(RandomSource random) {
		var states = streamBlockStates().filter(state -> state.is(Blocks.AIR)).toList();
		return states.isEmpty() ? Blocks.AIR.defaultBlockState() : states.get(random.nextInt(states.size()));
	}

	@Override
	public StateIngredientType getType() {
		return StateIngredients.ANY_OF;
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
		return streamBlockStates().toList();
	}

	@Override
	public Stream<BlockState> streamBlockStates() {
		return ingredients.stream()
				.flatMap(StateIngredient::streamBlockStates)
				.distinct();
	}

	public ImmutableList<StateIngredient> getIngredients() {
		return ingredients.asList();
	}

	@Override
	public String toString() {
		return "AnyOfStateIngredient{" + ingredients.toString() + "}";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		return ingredients.equals(((AnyOfStateIngredient) o).ingredients);
	}

	@Override
	public int hashCode() {
		return Objects.hash(ingredients);
	}

	public static class Type implements StateIngredientType<AnyOfStateIngredient> {
		public static final Codec<AnyOfStateIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ExtraCodecs.nonEmptyList(StateIngredients.TYPED_CODEC.listOf())
						.fieldOf("ingredients").forGetter(AnyOfStateIngredient::getIngredients)
		).apply(instance, AnyOfStateIngredient::new));

		@Override
		public Codec<AnyOfStateIngredient> codec() {
			return CODEC;
		}

		@Override
		public AnyOfStateIngredient fromNetwork(FriendlyByteBuf buffer) {
			int numIngredients = buffer.readInt();
			NonNullList<StateIngredient> ingredients = NonNullList.withSize(numIngredients, StateIngredients.NONE);
			for (int i = 0; i < numIngredients; i++) {
				ingredients.set(i, StateIngredients.fromNetwork(buffer));
			}
			return new AnyOfStateIngredient(ingredients);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buffer, AnyOfStateIngredient anyOfStateIngredient) {
			buffer.writeInt(anyOfStateIngredient.getIngredients().size());
			for (var ingredient : anyOfStateIngredient.getIngredients()) {
				StateIngredients.toNetwork(buffer, ingredient);
			}
		}
	}
}
