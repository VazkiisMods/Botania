/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.helper;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

public class RegistryHelper {
	@SuppressWarnings("unchecked")
	public static <T> Registry<T> getRegistry(ResourceKey<Registry<T>> resourceKey) {
		return (Registry<T>) BuiltInRegistries.REGISTRY.get(resourceKey.location());
	}
}
