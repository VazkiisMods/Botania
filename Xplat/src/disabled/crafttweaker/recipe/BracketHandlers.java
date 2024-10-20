package vazkii.botania.common.integration.crafttweaker.recipe;

import com.blamejared.crafttweaker.api.CraftTweakerAPI;
import com.blamejared.crafttweaker.api.annotation.BracketDumper;
import com.blamejared.crafttweaker.api.annotation.BracketResolver;
import com.blamejared.crafttweaker.api.annotation.ZenRegister;
import com.blamejared.crafttweaker_annotations.annotations.Document;

import net.minecraft.resources.ResourceLocation;

import org.openzen.zencode.java.ZenCodeType;

import vazkii.botania.api.brew.Brew;
import vazkii.botania.common.brew.ModBrews;
import vazkii.botania.common.integration.crafttweaker.natives.ExpandBrew;

import java.util.Collection;
import java.util.Locale;
import java.util.stream.Collectors;

@ZenRegister
@ZenCodeType.Name("mods.botania.BracketHandlers")
@Document("mods/Botania/BracketHandlers")
public final class BracketHandlers {

	/**
	 * Gets a list of brew values.
	 *
	 * @return A list of brew values.
	 */
	@ZenCodeType.Method
	@BracketDumper("brew")
	public static Collection<String> getBrewDump() {

		return ModBrews.registry
				.stream()
				.map(ExpandBrew::getCommandString)
				.collect(Collectors.toSet());
	}

	/**
	 * Gets a brew from its registry name
	 *
	 * @param tokens The name of the brew
	 * @return The Brew from the name if found.
	 *
	 * @docParam tokens "botania:healing"
	 */
	@ZenCodeType.Method
	@BracketResolver("brew")
	public static Brew getBrew(String tokens) {

		if (!tokens.toLowerCase(Locale.ENGLISH).equals(tokens)) {
			CraftTweakerAPI.LOGGER.warn("Brew BEP <brew:{}> does not seem to be lower-cased!", tokens);
		}

		final String[] split = tokens.split(":");
		if (split.length != 2) {
			throw new IllegalArgumentException("Could not get brew with name: <brew:" + tokens + ">! Syntax is <brew:modid:name>");
		}
		ResourceLocation key = ResourceLocation.fromNamespaceAndPath(split[0], split[1]);

		return ModBrews.registry.getOptional(key)
				.orElseThrow(() -> new IllegalArgumentException("Could not get brew with name: <brew:" + tokens + ">! Brew does not appear to exist!"));
	}
}
