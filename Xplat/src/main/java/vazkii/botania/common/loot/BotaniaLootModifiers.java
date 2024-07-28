/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.loot;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import java.util.function.BiConsumer;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class BotaniaLootModifiers {
	public static final LootItemConditionType TRUE_GUARDIAN_KILLER = new LootItemConditionType(TrueGuardianKiller.CODEC);
	public static final LootItemConditionType ENABLE_RELICS = new LootItemConditionType(EnableRelics.CODEC);
	public static final LootItemConditionType KILLED_BY_REAL_PLAYER = new LootItemConditionType(RealPlayerCondition.CODEC);
	public static final LootItemFunctionType BIND_UUID = new LootItemFunctionType(BindUuid.CODEC);

	public static void submitLootConditions(BiConsumer<LootItemConditionType, ResourceLocation> consumer) {
		consumer.accept(TRUE_GUARDIAN_KILLER, prefix("true_guardian_killer"));
		consumer.accept(ENABLE_RELICS, prefix("enable_relics"));
		consumer.accept(KILLED_BY_REAL_PLAYER, prefix("killed_by_player"));
	}

	public static void submitLootFunctions(BiConsumer<LootItemFunctionType, ResourceLocation> consumer) {
		consumer.accept(BIND_UUID, prefix("bind_uuid"));
	}
}
