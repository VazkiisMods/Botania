/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.mixin.client;

import net.minecraft.client.resources.SplashManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(SplashManager.class)
public class SplashManagerMixin {
	@Shadow
	@Final
	private List<String> splashes;

	/**
	 * Adds splash texts if enabled
	 */
	@Inject(at = @At("RETURN"), method = "apply(Ljava/util/List;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V")
	public void addSplashes(List<String> splashes, ResourceManager resourceManager, ProfilerFiller profiler, CallbackInfo ci) {
		/* FIXME: causes "java.lang.IllegalStateException: Cannot get config value before config is loaded." on NeoForge
		if (BotaniaConfig.client() != null && BotaniaConfig.client().splashesEnabled()) {
			this.splashes.add("Do not feed bread to elves!");
		}
		*/
	}
}
