/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.brew;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import vazkii.botania.common.brew.effect.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class BotaniaMobEffects {
	private static final Map<String, MobEffect> toRegister = new HashMap<>();

	public static final Holder<MobEffect> SOUL_CROSS = schedule("soul_cross", new SoulCrossMobEffect());
	public static final Holder<MobEffect> FEATHER_FEET = schedule("feather_feet", new FeatherfeetMobEffect());
	public static final Holder<MobEffect> EMPTINESS = schedule("emptiness", new EmptinessMobEffect());
	public static final Holder<MobEffect> BLOODTHRST = schedule("bloodthrst", new BloodthirstMobEffect());
	public static final Holder<MobEffect> ALLURE = schedule("allure", new AllureMobEffect());
	public static final Holder<MobEffect> CLEAR = schedule("clear", new AbsolutionMobEffect());

	private static Holder<MobEffect> schedule(String name, MobEffect effect) {
		toRegister.put(name, effect);
		return Holder.direct(effect);
	}

	public static void registerPotions(BiConsumer<MobEffect, ResourceLocation> r) {
		for (Map.Entry<String, MobEffect> entry : toRegister.entrySet()) {
			r.accept(entry.getValue(), botaniaRL(entry.getKey()));
		}
	}
}
