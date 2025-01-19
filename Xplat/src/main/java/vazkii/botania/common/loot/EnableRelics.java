/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.loot;

import com.mojang.serialization.MapCodec;

import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import org.jetbrains.annotations.NotNull;

import vazkii.botania.xplat.BotaniaConfig;

public class EnableRelics implements LootItemCondition {
	public static final EnableRelics INSTANCE = new EnableRelics();
	public static final MapCodec<EnableRelics> CODEC = MapCodec.unit(INSTANCE);

	private EnableRelics() {}

	@Override
	public boolean test(@NotNull LootContext context) {
		return BotaniaConfig.common().relicsEnabled();
	}

	@Override
	public LootItemConditionType getType() {
		return BotaniaLootModifiers.ENABLE_RELICS;
	}
}
