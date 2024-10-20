/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.brew;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.PotionUtils;

import vazkii.botania.api.brew.Brew;
import vazkii.botania.common.lib.LibBrewNames;

import java.util.Arrays;
import java.util.function.BiConsumer;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class BotaniaBrews {

	public static final Brew fallbackBrew = new Brew(0, 0).setNotBloodPendantInfusable().setNotIncenseInfusable();
	public static final Brew speed = new Brew(0x59B7FF, 4000, new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1800, 1));
	public static final Brew strength = new Brew(0xEE3F3F, 4000, new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1800, 1));
	public static final Brew haste = new Brew(0xF4A432, 4000, new MobEffectInstance(MobEffects.DIG_SPEED, 1800, 1));
	public static final Brew healing = new Brew(0xFF5ECC, 6000, new MobEffectInstance(MobEffects.HEAL, 1, 1));
	public static final Brew jumpBoost = new Brew(0x32F46D, 4000, new MobEffectInstance(MobEffects.JUMP, 1800, 1));
	public static final Brew regen = new Brew(0xFD6488, 7000, new MobEffectInstance(MobEffects.REGENERATION, 500, 1));
	public static final Brew regenWeak = new Brew(0xFD6488, 9000, new MobEffectInstance(MobEffects.REGENERATION, 2400, 0));
	public static final Brew resistance = new Brew(0xB44E17, 4000, new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1800, 1));
	public static final Brew fireResistance = new Brew(0xF86900, 4000, new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 9600, 0));
	public static final Brew waterBreathing = new Brew(0x84A7CF, 4000, new MobEffectInstance(MobEffects.WATER_BREATHING, 9600, 0));
	public static final Brew invisibility = new Brew(0xAEAEAE, 8000, new MobEffectInstance(MobEffects.INVISIBILITY, 9600, 0)).setNotBloodPendantInfusable();
	public static final Brew nightVision = new Brew(0x7C4BEB, 4000, new MobEffectInstance(MobEffects.NIGHT_VISION, 9600, 0));
	public static final Brew absorption = new Brew(0xF2EB23, 7000, new MobEffectInstance(MobEffects.ABSORPTION, 1800, 3)).setNotBloodPendantInfusable().setNotIncenseInfusable();

	public static final Brew allure = make(2000, new MobEffectInstance(BotaniaMobEffects.allure, 4800, 0));
	public static final Brew soulCross = make(10000, new MobEffectInstance(BotaniaMobEffects.soulCross, 1800, 0));
	public static final Brew featherfeet = make(7000, new MobEffectInstance(BotaniaMobEffects.featherfeet, 1800, 0));
	public static final Brew emptiness = make(30000, new MobEffectInstance(BotaniaMobEffects.emptiness, 7200, 0));
	public static final Brew bloodthirst = make(20000, new MobEffectInstance(BotaniaMobEffects.bloodthrst, 7200, 0));
	public static final Brew overload = new Brew(0x232323, 12000, new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1800, 3), new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1800, 2), new MobEffectInstance(MobEffects.WEAKNESS, 3600, 1), new MobEffectInstance(MobEffects.HUNGER, 200, 2));
	public static final Brew clear = make(4000, new MobEffectInstance(BotaniaMobEffects.clear, 0, 0));

	public static void submitRegistrations(BiConsumer<Brew, ResourceLocation> r) {
		r.accept(fallbackBrew, botaniaRL("fallback"));
		r.accept(speed, botaniaRL(LibBrewNames.SPEED));
		r.accept(strength, botaniaRL(LibBrewNames.STRENGTH));
		r.accept(haste, botaniaRL(LibBrewNames.HASTE));
		r.accept(healing, botaniaRL(LibBrewNames.HEALING));
		r.accept(jumpBoost, botaniaRL(LibBrewNames.JUMP_BOOST));
		r.accept(regen, botaniaRL(LibBrewNames.REGEN));
		r.accept(regenWeak, botaniaRL(LibBrewNames.REGEN_WEAK));
		r.accept(resistance, botaniaRL(LibBrewNames.RESISTANCE));
		r.accept(fireResistance, botaniaRL(LibBrewNames.FIRE_RESISTANCE));
		r.accept(waterBreathing, botaniaRL(LibBrewNames.WATER_BREATHING));
		r.accept(invisibility, botaniaRL(LibBrewNames.INVISIBILITY));
		r.accept(nightVision, botaniaRL(LibBrewNames.NIGHT_VISION));
		r.accept(absorption, botaniaRL(LibBrewNames.ABSORPTION));
		r.accept(overload, botaniaRL(LibBrewNames.OVERLOAD));
		r.accept(soulCross, botaniaRL(LibBrewNames.SOUL_CROSS));
		r.accept(featherfeet, botaniaRL(LibBrewNames.FEATHER_FEET));
		r.accept(emptiness, botaniaRL(LibBrewNames.EMPTINESS));
		r.accept(bloodthirst, botaniaRL(LibBrewNames.BLOODTHIRST));
		r.accept(allure, botaniaRL(LibBrewNames.ALLURE));
		r.accept(clear, botaniaRL(LibBrewNames.CLEAR));
	}

	private static Brew make(int cost, MobEffectInstance... effects) {
		return new Brew(PotionUtils.getColor(Arrays.asList(effects)), cost, effects);
	}

}
