/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.fabric.integration.rei;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;

import vazkii.botania.common.crafting.*;

public class BotaniaREICategoryIdentifiers {
	public static final CategoryIdentifier<BreweryREIDisplay> BREWERY = CategoryIdentifier.of(BotanicalBreweryRecipe.TYPE_ID);
	public static final CategoryIdentifier<ElvenTradeREIDisplay> ELVEN_TRADE = CategoryIdentifier.of(ElvenTradeRecipe.TYPE_ID);
	public static final CategoryIdentifier<ManaPoolREIDisplay> MANA_INFUSION = CategoryIdentifier.of(ManaInfusionRecipe.TYPE_ID);
	public static final CategoryIdentifier<OrechidREIDisplay> MARIMORPHOSIS = CategoryIdentifier.of(OrechidRecipe.MARIMORPHOSIS_TYPE_ID);
	public static final CategoryIdentifier<OrechidREIDisplay> ORECHID = CategoryIdentifier.of(OrechidRecipe.TYPE_ID);
	public static final CategoryIdentifier<OrechidIgnemREIDisplay> ORECHID_IGNEM = CategoryIdentifier.of(OrechidRecipe.IGNEM_TYPE_ID);
	public static final CategoryIdentifier<PetalApothecaryREIDisplay> PETAL_APOTHECARY = CategoryIdentifier.of(
			PetalApothecaryRecipe.TYPE_ID);
	public static final CategoryIdentifier<PureDaisyREIDisplay> PURE_DAISY = CategoryIdentifier.of(PureDaisyRecipe.TYPE_ID);
	public static final CategoryIdentifier<RunicAltarREIDisplay> RUNE_ALTAR = CategoryIdentifier.of(RunicAltarRecipe.TYPE_ID);
	public static final CategoryIdentifier<TerrestrialAgglomerationREIDisplay> TERRA_PLATE = CategoryIdentifier.of(
			TerrestrialAgglomerationRecipe.TYPE_ID);

	private BotaniaREICategoryIdentifiers() {

	}
}
