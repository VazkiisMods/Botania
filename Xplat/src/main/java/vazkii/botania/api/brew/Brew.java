/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.brew;

import com.google.common.collect.ImmutableList;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;

import vazkii.botania.api.BotaniaAPI;

import java.util.List;
import java.util.function.Supplier;

/**
 * The class for a Brew definition, each one is a singleton.
 */
public class Brew {
	private final Supplier<Integer> color;
	private final int cost;
	private final Supplier<List<MobEffectInstance>> effects;
	private boolean canInfuseBloodPendant = true;
	private boolean canInfuseIncense = true;

	/**
	 * @param color   The color for the potion to be rendered in the bottle, note that it will get
	 *                changed a bit when it renders (for more or less brightness) to give a fancy effect.
	 *                See {@link PotionContents#getColor} for a method
	 *                to calculate this automatically.
	 * @param cost    The cost, in Mana for this brew.
	 * @param effects A list of effects to apply to the player when they drink it.
	 */
	public Brew(int color, int cost, MobEffectInstance... effects) {
		this.color = () -> color;
		this.cost = cost;
		List<MobEffectInstance> savedEffects = ImmutableList.copyOf(effects);
		this.effects = () -> savedEffects;
	}

	/**
	 * @param cost    The cost, in Mana for this brew.
	 * @param effects A supplier that supplies a list of effects to apply to the player when they drink it.
	 */
	public Brew(int cost, Supplier<List<MobEffectInstance>> effects) {
		this.color = () -> PotionContents.getColor(effects.get());
		this.cost = cost;
		this.effects = effects;
	}

	/**
	 * Sets this brew to not be able to be infused onto the Tainted Blood Pendant.
	 */
	public Brew setNotBloodPendantInfusable() {
		canInfuseBloodPendant = false;
		return this;
	}

	/**
	 * Sets this brew to not be able to be infused onto Incense Sticks.
	 */
	public Brew setNotIncenseInfusable() {
		canInfuseIncense = false;
		return this;
	}

	public boolean canInfuseBloodPendant() {
		return canInfuseBloodPendant;
	}

	public boolean canInfuseIncense() {
		return canInfuseIncense;
	}

	/**
	 * Gets the insensitive unlocalized name. This is used for the lexicon.
	 */
	public String getTranslationKey() {
		ResourceLocation id = BotaniaAPI.instance().getBrewRegistry().getKey(this);
		return String.format("%s.brew.%s", id.getNamespace(), id.getPath());
	}

	/**
	 * Gets the unlocalized name for the ItemStack passed in.
	 */
	public String getTranslationKey(ItemStack stack) {
		return getTranslationKey();
	}

	/**
	 * Gets the display color for the ItemStack passed in. Note that for
	 * the lexicon, this passes in a botania Managlass Vial or an
	 * Alfglass Flask at all times.
	 */
	public int getColor(ItemStack stack) {
		return color.get();
	}

	/**
	 * Gets the insensitive unlocalized mana cost. This is used for the lexicon.
	 */
	public int getManaCost() {
		return cost;
	}

	/**
	 * Gets the mana cost for the ItemStack passed in.
	 */
	public int getManaCost(ItemStack stack) {
		return getManaCost();
	}

	/**
	 * Gets the list of vanilla PotionEffects for the ItemStack passed in.
	 * Note that for the lexicon, this passes in a botania Managlass
	 * Vial or an Alfglass Flask at all times.
	 */
	public List<MobEffectInstance> getPotionEffects(ItemStack stack) {
		return effects.get();
	}

}
