/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.FriendlyByteBuf;

public interface StateIngredientType<T extends StateIngredient> {
	MapCodec<T> codec();

	T fromNetwork(FriendlyByteBuf buffer);

	void toNetwork(FriendlyByteBuf buffer, T ingredient);
}
