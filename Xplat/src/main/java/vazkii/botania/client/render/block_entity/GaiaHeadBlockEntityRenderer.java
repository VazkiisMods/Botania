/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.render.block_entity;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import vazkii.botania.client.core.helper.CoreShaders;
import vazkii.botania.client.render.entity.GaiaGuardianRenderer;

import java.util.Collections;
import java.util.Map;

public class GaiaHeadBlockEntityRenderer extends SkullBlockRenderer {
	public static Map<SkullBlock.Type, SkullModelBase> models = Collections.emptyMap();

	public GaiaHeadBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		super(ctx);
		models = SkullBlockRenderer.createSkullRenderers(ctx.getModelSet());
	}

	public static SkullBlock.Type getViewType() {
		Entity view = Minecraft.getInstance().getCameraEntity();
		SkullBlock.Type type = SkullBlock.Types.PLAYER;

		if (view instanceof Skeleton) {
			type = SkullBlock.Types.SKELETON;
		} else if (view instanceof WitherSkeleton) {
			type = SkullBlock.Types.WITHER_SKELETON;
		} else if (view instanceof WitherBoss) {
			type = SkullBlock.Types.WITHER_SKELETON;
		} else if (view instanceof Zombie) {
			type = SkullBlock.Types.ZOMBIE;
		} else if (view instanceof Creeper) {
			type = SkullBlock.Types.CREEPER;
		} else if (view instanceof EnderDragon) {
			type = SkullBlock.Types.DRAGON;
		}
		return type;
	}

	public static void hookGetRenderType(CallbackInfoReturnable<RenderType> cir) {
		SkullBlock.Type type = getViewType();
		ResolvableProfile profile = null;
		if (type == SkullBlock.Types.PLAYER && Minecraft.getInstance().getCameraEntity() instanceof Player player) {
			profile = new ResolvableProfile(player.getGameProfile());
		}

		RenderType layer = SkullBlockRenderer.getRenderType(type, profile);
		cir.setReturnValue(new WrapperLayer(layer));
	}

	private static class WrapperLayer extends RenderType {
		public WrapperLayer(RenderType compose) {
			super(compose.toString(), compose.format(), compose.mode(), compose.bufferSize(), compose.affectsCrumbling(), false,
					() -> {
						compose.setupRenderState();
						// Override the shader to ours
						ShaderInstance shader = CoreShaders.doppleganger();
						if (shader != null) {
							RenderSystem.setShader(CoreShaders::doppleganger);
							shader.safeGetUniform("BotaniaDisfiguration").set(GaiaGuardianRenderer.DEFAULT_DISFIGURATION);
							shader.safeGetUniform("BotaniaGrainIntensity").set(GaiaGuardianRenderer.DEFAULT_GRAIN_INTENSITY);
						}
					}, compose::clearRenderState);
		}
	}
}
