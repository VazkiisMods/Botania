/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.core.helper;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;

import vazkii.botania.network.TriConsumer;
import vazkii.botania.xplat.BotaniaConfig;

import java.util.function.Consumer;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class CoreShaders {
	private static ShaderInstance starfieldShaderInstance;
	private static ShaderInstance doppleganger;
	private static ShaderInstance manaPool;
	private static ShaderInstance terraPlate;
	private static ShaderInstance enchanter;
	private static ShaderInstance pylon;
	private static ShaderInstance halo;
	private static ShaderInstance filmGrainParticle;
	private static ShaderInstance dopplegangerBar;

	// This is abstracted this way instead of just directly constructing the ShaderInstance
	// Because Fabric is cute and hides the ResourceProvider from modders (why?)
	public static void init(TriConsumer<ResourceLocation, VertexFormat, Consumer<ShaderInstance>> registrations) {
		registrations.accept(
				botaniaRL("starfield"),
				DefaultVertexFormat.POSITION,
				inst -> starfieldShaderInstance = inst
		);
		registrations.accept(
				botaniaRL("doppleganger"),
				DefaultVertexFormat.NEW_ENTITY,
				inst -> doppleganger = inst
		);
		registrations.accept(
				botaniaRL("mana_pool"),
				DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
				inst -> manaPool = inst
		);
		registrations.accept(
				botaniaRL("terra_plate_rune"),
				DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
				inst -> terraPlate = inst
		);
		registrations.accept(
				botaniaRL("enchanter_rune"),
				DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
				inst -> enchanter = inst
		);
		registrations.accept(
				botaniaRL("pylon"),
				DefaultVertexFormat.NEW_ENTITY,
				inst -> pylon = inst
		);
		registrations.accept(
				botaniaRL("halo"),
				DefaultVertexFormat.POSITION_COLOR_TEX,
				inst -> halo = inst
		);
		registrations.accept(
				botaniaRL("film_grain_particle"),
				DefaultVertexFormat.PARTICLE,
				inst -> filmGrainParticle = inst
		);
		registrations.accept(
				botaniaRL("doppleganger_bar"),
				DefaultVertexFormat.POSITION_TEX,
				inst -> dopplegangerBar = inst
		);
	}

	public static ShaderInstance starfield() {
		// Intended to not respect useShaders config. The render kind of relies entirely
		// on the shader, like the end portal.
		return starfieldShaderInstance;
	}

	public static ShaderInstance doppleganger() {
		if (BotaniaConfig.client().useShaders()) {
			return doppleganger;
		} else {
			return GameRenderer.getRendertypeEntityTranslucentShader();
		}
	}

	public static ShaderInstance manaPool() {
		if (BotaniaConfig.client().useShaders()) {
			return manaPool;
		} else {
			return GameRenderer.getPositionColorTexLightmapShader();
		}
	}

	public static ShaderInstance terraPlate() {
		if (BotaniaConfig.client().useShaders()) {
			return terraPlate;
		} else {
			return GameRenderer.getPositionColorTexLightmapShader();
		}
	}

	public static ShaderInstance enchanter() {
		if (BotaniaConfig.client().useShaders()) {
			return enchanter;
		} else {
			return GameRenderer.getPositionColorTexLightmapShader();
		}
	}

	public static ShaderInstance pylon() {
		if (BotaniaConfig.client().useShaders()) {
			return pylon;
		} else {
			return GameRenderer.getRendertypeEntityTranslucentShader();
		}
	}

	public static ShaderInstance halo() {
		if (BotaniaConfig.client().useShaders()) {
			return halo;
		} else {
			return GameRenderer.getPositionColorTexShader();
		}
	}

	public static ShaderInstance filmGrainParticle() {
		if (BotaniaConfig.client().useShaders()) {
			return filmGrainParticle;
		} else {
			return GameRenderer.getParticleShader();
		}
	}

	public static ShaderInstance dopplegangerBar() {
		if (BotaniaConfig.client().useShaders()) {
			return dopplegangerBar;
		} else {
			return GameRenderer.getPositionTexShader();
		}
	}
}
