package vazkii.botania.forge.mixin;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BuiltInRegistries.class)
public interface BuiltInRegistriesForgeAccessor {
	@Invoker("registerDefaulted")
	static <T> DefaultedRegistry<T> callRegisterDefaulted(ResourceKey<? extends Registry<T>> registryName,
			String defaultId,
			BuiltInRegistries.RegistryBootstrap<T> bootstrap) {
		throw new IllegalStateException();
	}
}
