/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.patchouli.processor;

import com.google.common.collect.ImmutableList;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.recipe.ManaInfusionRecipe;
import vazkii.botania.client.patchouli.PatchouliUtils;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ManaInfusionProcessor implements IComponentProcessor {
	private List<ManaInfusionRecipe> recipes;
	private boolean hasCustomHeading;

	@Override
	public void setup(Level level, IVariableProvider variables) {
		if (variables.has("recipes") && variables.has("group")) {
			BotaniaAPI.LOGGER.warn("Mana infusion template has both 'recipes' and 'group', ignoring 'recipes'");
		}

		ImmutableList.Builder<ManaInfusionRecipe> builder = ImmutableList.builder();
		if (variables.has("group")) {
			String group = variables.get("group", level.registryAccess()).asString();
			builder.addAll(PatchouliUtils.getRecipeGroup(BotaniaRecipeTypes.MANA_INFUSION_TYPE, group));
		} else {
			for (IVariable s : variables.get("recipes", level.registryAccess()).asListOrSingleton(level.registryAccess())) {
				ManaInfusionRecipe recipe = PatchouliUtils.getRecipe(level, BotaniaRecipeTypes.MANA_INFUSION_TYPE, ResourceLocation.parse(s.asString()));
				if (recipe != null) {
					builder.add(recipe);
				}
			}
		}

		this.recipes = builder.build();
		this.hasCustomHeading = variables.has("heading");
	}

	@Override
	public IVariable process(Level level, String key) {
		if (recipes.isEmpty()) {
			return null;
		}
		switch (key) {
			case "heading":
				if (!hasCustomHeading) {
					return IVariable.from(recipes.get(0).getResultItem(level.registryAccess()).getHoverName(), level.registryAccess());
				}
				return null;
			case "input":
				return PatchouliUtils.interweaveIngredients(recipes.stream().map(r -> r.getIngredients().get(0)).collect(Collectors.toList()), level);
			case "output":
				return IVariable.wrapList(recipes.stream().map(r -> r.getResultItem(level.registryAccess())).map(o -> IVariable.from(o, level.registryAccess())).collect(Collectors.toList()), level.registryAccess());
			case "catalyst":
				return IVariable.wrapList(recipes.stream().map(ManaInfusionRecipe::getRecipeCatalyst)
						.flatMap(ingr -> {
							if (ingr == null) {
								return Stream.of(ItemStack.EMPTY);
							}
							return ingr.getDisplayedStacks().stream();
						})
						.map(o -> IVariable.from(o, level.registryAccess()))
						.collect(Collectors.toList()), level.registryAccess());
			case "mana":
				return IVariable.wrapList(recipes.stream().mapToInt(ManaInfusionRecipe::getManaToConsume).mapToObj(IVariable::wrap).collect(Collectors.toList()), level.registryAccess());
			case "drop":
				Component q = Component.literal("(?)").withStyle(ChatFormatting.BOLD);
				return IVariable.from(Component.translatable("botaniamisc.drop").append(" ").append(q), level.registryAccess());
			case "dropTip2":
			case "dropTip1":
				Component drop = Component.keybind("key.drop").withStyle(ChatFormatting.GREEN);
				return IVariable.from(Component.translatable("botaniamisc." + key, drop), level.registryAccess());
			case "dropTip3":
				return IVariable.from(Component.translatable("botaniamisc." + key), level.registryAccess());
		}
		return null;
	}
}
