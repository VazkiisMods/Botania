/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.core.helper;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.client.render.block_entity.PylonBlockEntityRenderer;
import vazkii.botania.common.helper.VecHelper;
import vazkii.botania.common.item.equipment.bauble.FlugelTiaraItem;
import vazkii.botania.mixin.client.ItemRendererAccessor;
import vazkii.botania.mixin.client.RenderTypeAccessor;

import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Random;
import java.util.function.Function;

public final class RenderHelper extends RenderType {
	private static final RenderType STAR;
	public static final RenderType RECTANGLE;
	public static final RenderType CIRCLE;
	public static final RenderType RED_STRING;
	public static final RenderType LINE_1_NO_DEPTH;
	public static final RenderType LINE_4_NO_DEPTH;
	public static final RenderType LINE_5_NO_DEPTH;
	public static final RenderType LINE_8_NO_DEPTH;
	public static final RenderType SPARK;
	public static final RenderType LIGHT_RELAY;
	public static final RenderType ICON_OVERLAY;
	public static final RenderType BABYLON_ICON;
	public static final RenderType MANA_POOL_WATER;
	public static final RenderType TERRA_PLATE;
	public static final RenderType ENCHANTER;
	public static final RenderType HALO;
	public static final RenderType MANA_PYLON_GLOW = getPylonGlow("mana_pylon_glow", PylonBlockEntityRenderer.MANA_TEXTURE);
	public static final RenderType NATURA_PYLON_GLOW = getPylonGlow("natura_pylon_glow", PylonBlockEntityRenderer.NATURA_TEXTURE);
	public static final RenderType GAIA_PYLON_GLOW = getPylonGlow("gaia_pylon_glow", PylonBlockEntityRenderer.GAIA_TEXTURE);
	public static final RenderType MANA_PYLON_GLOW_DIRECT = getPylonGlowDirect("mana_pylon_glow_direct", PylonBlockEntityRenderer.MANA_TEXTURE);
	public static final RenderType NATURA_PYLON_GLOW_DIRECT = getPylonGlowDirect("natura_pylon_glow_direct", PylonBlockEntityRenderer.NATURA_TEXTURE);
	public static final RenderType GAIA_PYLON_GLOW_DIRECT = getPylonGlowDirect("gaia_pylon_glow_direct", PylonBlockEntityRenderer.GAIA_TEXTURE);

	public static final RenderType ASTROLABE_PREVIEW = new AstrolabeLayer();
	public static final RenderType STARFIELD;
	public static final RenderType LIGHTNING;
	public static final RenderType TRANSLUCENT;

	private static final int ITEM_AND_PADDING_WIDTH = 20;

	private static final double INITIAL_OFFSET = 0.005;
	private static final double OFFSET_INCREMENT = 0.001;
	// Global y offset so that overlapping landmines or radius descriptors do not Z-fight
	private static double offY = INITIAL_OFFSET;

	public static double getOffY() {
		return offY;
	}

	public static void incrementOffY() {
		offY += OFFSET_INCREMENT;
	}

	public static void onWorldRenderLast() {
		offY = INITIAL_OFFSET;
	}

	private static RenderType makeLayer(String name, VertexFormat format, VertexFormat.Mode mode,
			int bufSize, boolean hasCrumbling, boolean sortOnUpload, CompositeState glState) {
		return RenderTypeAccessor.create(name, format, mode, bufSize, hasCrumbling, sortOnUpload, glState);
	}

	private static RenderType makeLayer(String name, VertexFormat format, VertexFormat.Mode mode,
			int bufSize, CompositeState glState) {
		return makeLayer(name, format, mode, bufSize, false, false, glState);
	}

	static {
		RenderType.CompositeState glState = RenderType.CompositeState.builder()
				.setShaderState(POSITION_COLOR_SHADER)
				.setWriteMaskState(COLOR_WRITE)
				.setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
				.createCompositeState(false);
		STAR = makeLayer(ResourcesLib.PREFIX_MOD + "star", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 256, false, false, glState);

		glState = RenderType.CompositeState.builder()
				.setShaderState(POSITION_COLOR_SHADER)
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(ITEM_ENTITY_TARGET)
				.setCullState(NO_CULL)
				.createCompositeState(false);
		RECTANGLE = makeLayer(ResourcesLib.PREFIX_MOD + "rectangle_highlight", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, true, glState);
		CIRCLE = makeLayer(ResourcesLib.PREFIX_MOD + "circle_highlight", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 256, false, false, glState);

		RED_STRING = makeLayer(ResourcesLib.PREFIX_MOD + "red_string", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 128, lineState(1, false, false));
		LINE_1_NO_DEPTH = makeLayer(ResourcesLib.PREFIX_MOD + "line_1_no_depth", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 128, lineState(1, true, true));
		LINE_4_NO_DEPTH = makeLayer(ResourcesLib.PREFIX_MOD + "line_4_no_depth", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 128, lineState(4, true, true));
		LINE_5_NO_DEPTH = makeLayer(ResourcesLib.PREFIX_MOD + "line_5_no_depth", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 64, lineState(5, true, true));
		LINE_8_NO_DEPTH = makeLayer(ResourcesLib.PREFIX_MOD + "line_8_no_depth", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, 64, lineState(8, true, true));

		glState = RenderType.CompositeState.builder()
				.setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
				.setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(ITEM_ENTITY_TARGET)
				.setLightmapState(LIGHTMAP).createCompositeState(true);
		SPARK = makeLayer(ResourcesLib.PREFIX_MOD + "spark", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, glState);
		glState = RenderType.CompositeState.builder()
				.setShaderState(new ShaderStateShard(CoreShaders::halo))
				.setTextureState(RenderStateShard.BLOCK_SHEET_MIPPED)
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(ITEM_ENTITY_TARGET)
				.createCompositeState(true);
		LIGHT_RELAY = makeLayer(ResourcesLib.PREFIX_MOD + "light_relay", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 64, glState);

		glState = RenderType.CompositeState.builder().setTextureState(BLOCK_SHEET_MIPPED)
				.setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(ITEM_ENTITY_TARGET)
				.setLightmapState(LIGHTMAP).createCompositeState(true);
		ICON_OVERLAY = makeLayer(ResourcesLib.PREFIX_MOD + "icon_overlay", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 128, glState);
		glState = RenderType.CompositeState.builder().setTextureState(BLOCK_SHEET_MIPPED)
				.setShaderState(new ShaderStateShard(CoreShaders::manaPool))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(ITEM_ENTITY_TARGET)
				.setLightmapState(LIGHTMAP).createCompositeState(false);
		MANA_POOL_WATER = makeLayer(ResourcesLib.PREFIX_MOD + "mana_pool_water", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 128, glState);
		glState = RenderType.CompositeState.builder().setTextureState(BLOCK_SHEET_MIPPED)
				.setShaderState(new ShaderStateShard(CoreShaders::terraPlate))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(ITEM_ENTITY_TARGET)
				.setLightmapState(LIGHTMAP).createCompositeState(false);
		TERRA_PLATE = makeLayer(ResourcesLib.PREFIX_MOD + "terra_plate_rune", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 128, glState);
		glState = RenderType.CompositeState.builder().setTextureState(BLOCK_SHEET_MIPPED)
				.setShaderState(new ShaderStateShard(CoreShaders::enchanter))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(ITEM_ENTITY_TARGET)
				.setLightmapState(LIGHTMAP).createCompositeState(false);
		ENCHANTER = makeLayer(ResourcesLib.PREFIX_MOD + "enchanter_rune", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 128, glState);

		RenderStateShard.TextureStateShard babylonTexture = new RenderStateShard.TextureStateShard(ResourceLocation.parse(ResourcesLib.MISC_BABYLON), false, true);
		glState = RenderType.CompositeState.builder().setTextureState(babylonTexture)
				.setShaderState(new ShaderStateShard(CoreShaders::halo))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(ITEM_ENTITY_TARGET)
				.setCullState(NO_CULL)
				.createCompositeState(true);
		BABYLON_ICON = makeLayer(ResourcesLib.PREFIX_MOD + "babylon", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 64, glState);

		RenderStateShard.TextureStateShard haloTexture = new RenderStateShard.TextureStateShard(FlugelTiaraItem.textureHalo, false, true);
		glState = RenderType.CompositeState.builder().setTextureState(haloTexture)
				.setShaderState(new ShaderStateShard(CoreShaders::halo))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.createCompositeState(true);
		HALO = makeLayer(ResourcesLib.PREFIX_MOD + "halo", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 64, glState);

		// [VanillaCopy] End portal, with own shader
		glState = RenderType.CompositeState.builder()
				.setShaderState(new ShaderStateShard(CoreShaders::starfield))
				.setTextureState(RenderStateShard.MultiTextureStateShard.builder()
						.add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
						.add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build())
				.createCompositeState(false);
		STARFIELD = makeLayer(ResourcesLib.PREFIX_MOD + "starfield", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256, false, false, glState);
		glState = RenderType.CompositeState.builder()
				.setShaderState(POSITION_COLOR_SHADER)
				.setTransparencyState(LIGHTNING_TRANSPARENCY)
				.createCompositeState(false);
		LIGHTNING = makeLayer(ResourcesLib.PREFIX_MOD + "lightning", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, true, glState);
		TRANSLUCENT = RenderType.entityTranslucentCull(TextureAtlas.LOCATION_BLOCKS);
	}

	private RenderHelper(String string, VertexFormat vertexFormat, VertexFormat.Mode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
		super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
		throw new UnsupportedOperationException("Should not be instantiated");
	}

	private static RenderType getPylonGlowDirect(String name, ResourceLocation texture) {
		return getPylonGlow(name, texture, true);
	}

	private static RenderType getPylonGlow(String name, ResourceLocation texture) {
		return getPylonGlow(name, texture, false);
	}

	private static RenderType getPylonGlow(String name, ResourceLocation texture, boolean direct) {
		RenderType.CompositeState.CompositeStateBuilder glState = RenderType.CompositeState.builder()
				.setShaderState(new ShaderStateShard(CoreShaders::pylon))
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY);
		if (!direct) {
			glState = glState.setOutputState(RenderStateShard.ITEM_ENTITY_TARGET);
		}
		return makeLayer(ResourcesLib.PREFIX_MOD + name, DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 128, glState.createCompositeState(false));
	}

	private static CompositeState lineState(double width, boolean direct, boolean noDepth) {
		// [VanillaCopy] vanilla LINES layer with line width defined (and optionally depth disabled)
		var builder = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_LINES_SHADER)
				.setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(width)))
				.setLayeringState(VIEW_OFFSET_Z_LAYERING)
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setWriteMaskState(noDepth ? COLOR_WRITE : COLOR_DEPTH_WRITE)
				.setCullState(NO_CULL);
		if (!direct) {
			builder = builder.setOutputState(ITEM_ENTITY_TARGET);
		}
		if (noDepth) {
			builder = builder.setDepthTestState(NO_DEPTH_TEST);
		}
		return builder.createCompositeState(false);
	}

	public static RenderType getHaloLayer(ResourceLocation texture) {
		RenderType.CompositeState glState = RenderType.CompositeState.builder()
				.setShaderState(RenderStateShard.POSITION_COLOR_TEX_SHADER)
				.setTextureState(new RenderStateShard.TextureStateShard(texture, true, false))
				.setCullState(new RenderStateShard.CullStateShard(false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY).createCompositeState(false);
		return makeLayer(ResourcesLib.PREFIX_MOD + "crafting_halo", DefaultVertexFormat.POSITION_COLOR_TEX, VertexFormat.Mode.QUADS, 64, false, true, glState);
	}

	private static final Function<ResourceLocation, RenderType> DOPPLEGANGER = Util.memoize(texture -> {
		// [VanillaCopy] entity_translucent, with own shader
		CompositeState glState = RenderType.CompositeState.builder()
				.setShaderState(new ShaderStateShard(CoreShaders::doppleganger))
				.setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(true);
		return makeLayer(ResourcesLib.PREFIX_MOD + "doppleganger", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, glState);
	});

	public static RenderType getDopplegangerLayer(ResourceLocation texture) {
		return DOPPLEGANGER.apply(texture);
	}

	public static void drawTexturedModalRect(GuiGraphics gui, ResourceLocation textureId, int x, int y, int u, int v, int width, int height) {
		gui.blit(textureId, x, y, u, v, width, height, 256, 256);
	}

	public static void renderStar(PoseStack ms, MultiBufferSource buffers, int color, float xScale, float yScale, float zScale, long seed) {
		VertexConsumer buffer = buffers.getBuffer(STAR);

		float ticks = ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks;
		float semiPeriodTicks = 200;
		float f1 = Mth.abs(Mth.sin((float) Math.PI / semiPeriodTicks * ticks))
				* 0.9F + 0.1F; // shift to [0.1, 1.0]

		float f2 = f1 > 0.F ? (f1 - 0.7F) / 0.2F : 0;
		Random random = new Random(seed);

		ms.pushPose();
		ms.scale(xScale, yScale, zScale);

		for (int i = 0; i < (f1 + f1 * f1) / 2F * 90F + 30F; i++) {
			ms.mulPose(VecHelper.rotateX(random.nextFloat() * 360F));
			ms.mulPose(VecHelper.rotateY(random.nextFloat() * 360F));
			ms.mulPose(VecHelper.rotateZ(random.nextFloat() * 360F));
			ms.mulPose(VecHelper.rotateX(random.nextFloat() * 360F));
			ms.mulPose(VecHelper.rotateY(random.nextFloat() * 360F));
			ms.mulPose(VecHelper.rotateZ(random.nextFloat() * 360F + f1 * 90F));
			float f3 = random.nextFloat() * 20F + 5F + f2 * 10F;
			float f4 = random.nextFloat() * 2F + 1F + f2 * 2F;
			float r = ((color & 0xFF0000) >> 16) / 255F;
			float g = ((color & 0xFF00) >> 8) / 255F;
			float b = (color & 0xFF) / 255F;
			Matrix4f mat = ms.last().pose();
			Runnable center = () -> buffer.addVertex(mat, 0, 0, 0).setColor(r, g, b, f1);
			Runnable[] vertices = {
					() -> buffer.addVertex(mat, -0.866F * f4, f3, -0.5F * f4).setColor(0, 0, 0, 0),
					() -> buffer.addVertex(mat, 0.866F * f4, f3, -0.5F * f4).setColor(0, 0, 0, 0),
					() -> buffer.addVertex(mat, 0, f3, 1F * f4).setColor(0, 0, 0, 0),
					() -> buffer.addVertex(mat, -0.866F * f4, f3, -0.5F * f4).setColor(0, 0, 0, 0)
			};
			triangleFan(center, vertices);
		}

		ms.popPose();
	}

	public static void triangleFan(Runnable center, Runnable... vertices) {
		triangleFan(center, Arrays.asList(vertices));
	}

	/**
	 * With a buffer in GL_TRIANGLES mode, emulates GL_TRIANGLE_FAN on the CPU.
	 * This is because batching of GL_TRIANGLE_FAN makes no sense (the vertices would bleed into one massive fan)
	 */
	public static void triangleFan(Runnable center, List<Runnable> vertices) {
		for (int i = 0; i < vertices.size() - 1; i++) {
			center.run();
			vertices.get(i).run();
			vertices.get(i + 1).run();
		}
	}

	public static void flatRectangle(VertexConsumer buffer, Matrix4f mat,
			float xMin, float xMax, float y, float zMin, float zMax,
			int r, int g, int b, int a) {
		buffer.addVertex(mat, xMax, y, zMin).setColor(r, g, b, a);
		buffer.addVertex(mat, xMin, y, zMin).setColor(r, g, b, a);
		buffer.addVertex(mat, xMin, y, zMax).setColor(r, g, b, a);
		buffer.addVertex(mat, xMax, y, zMax).setColor(r, g, b, a);
	}

	public static void renderProgressPie(GuiGraphics gui, int x, int y, float progress, ItemStack stack) {
		PoseStack ms = gui.pose();
		Minecraft mc = Minecraft.getInstance();
		gui.renderItem(stack, x, y);

		RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, true);
		GL11.glEnable(GL11.GL_STENCIL_TEST);
		RenderSystem.colorMask(false, false, false, false);
		RenderSystem.depthMask(false);
		RenderSystem.stencilFunc(GL11.GL_NEVER, 1, 0xFF);
		RenderSystem.stencilOp(GL11.GL_REPLACE, GL11.GL_KEEP, GL11.GL_KEEP);
		RenderSystem.stencilMask(0xFF);
		gui.renderItem(stack, x, y);

		int r = 10;
		int centerX = x + 8;
		int centerY = y + 8;
		int degs = (int) (360 * progress);
		float a = 0.5F + 0.2F * ((float) Math.cos((double) (ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks) / 10) * 0.5F + 0.5F);

		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.depthMask(true);
		RenderSystem.stencilMask(0x00);
		RenderSystem.stencilFunc(GL11.GL_EQUAL, 1, 0xFF);

		Matrix4f mat = ms.last().pose();
		BufferBuilder buf = Tesselator.getInstance().getBuilder();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		buf.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
		buf.addVertex(mat, centerX, centerY, 0).setColor(0, 0.5F, 0.5F, a);

		for (int i = degs; i >= 0; i--) {
			float rad = (i - 90) / 180F * (float) Math.PI;
			buf.addVertex(mat, centerX + Mth.cos(rad) * r, centerY + Mth.sin(rad) * r, 0).setColor(0F, 1F, 0.5F, a);
		}

		buf.addVertex(mat, centerX, centerY, 0).setColor(0F, 1F, 0.5F, a);
		Tesselator.getInstance().end();

		RenderSystem.disableBlend();
		GL11.glDisable(GL11.GL_STENCIL_TEST);
	}

	/**
	 * @param color Must include alpha
	 */
	// [VanillaCopy] ItemRenderer.renderItem with simplifications + color support + custom model
	public static void renderItemCustomColor(LivingEntity entity, ItemStack stack, int color, PoseStack ms, MultiBufferSource buffers, int light, int overlay, @Nullable BakedModel model) {
		ms.pushPose();
		if (model == null) {
			model = Minecraft.getInstance().getItemRenderer().getModel(stack, entity.level(), entity, entity.getId());
		}
		model.getTransforms().getTransform(ItemDisplayContext.NONE).apply(false, ms);
		ms.translate(-0.5D, -0.5D, -0.5D);

		if (!model.isCustomRenderer() && !stack.is(Items.TRIDENT)) {
			RenderType rendertype = ItemBlockRenderTypes.getRenderType(stack, true);
			VertexConsumer ivertexbuilder = ItemRenderer.getFoilBufferDirect(buffers, rendertype, true, stack.hasFoil());
			renderBakedItemModel(model, stack, color, light, overlay, ms, ivertexbuilder);
		} else {
			throw new IllegalArgumentException("Custom renderer items not supported");
		}

		ms.popPose();
	}

	public static void renderItemCustomColor(LivingEntity entity, ItemStack stack, int color, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		renderItemCustomColor(entity, stack, color, ms, buffers, light, overlay, null);
	}

	// [VanillaCopy] ItemRenderer with custom color
	private static void renderBakedItemModel(BakedModel model, ItemStack stack, int color, int light, int overlay, PoseStack ms, VertexConsumer buffer) {
		var random = RandomSource.create();
		long i = 42L;

		for (Direction direction : Direction.values()) {
			random.setSeed(42L);
			renderBakedItemQuads(ms, buffer, color, model.getQuads(null, direction, random), stack, light, overlay);
		}

		random.setSeed(42L);
		renderBakedItemQuads(ms, buffer, color, model.getQuads(null, null, random), stack, light, overlay);
	}

	// Wraps ItemRenderer#renderQuadList for custom color support
	private static void renderBakedItemQuads(PoseStack ms, VertexConsumer buffer, int color, List<BakedQuad> quads, ItemStack stack, int light, int overlay) {
		float a = ((color >> 24) & 0xFF) / 255.0F;
		float r = (float) (color >> 16 & 0xFF) / 255.0F;
		float g = (float) (color >> 8 & 0xFF) / 255.0F;
		float b = (float) (color & 0xFF) / 255.0F;

		buffer = new DelegatedVertexConsumer(buffer) {
			@Override
			public @NotNull VertexConsumer setColor(float red, float green, float blue, float alpha) {
				return super.setColor(r, g, b, a);
			}
		};
		((ItemRendererAccessor) Minecraft.getInstance().getItemRenderer())
				.callRenderQuadList(ms, buffer, quads, stack, light, overlay);
	}

	/**
	 * Draw an icon into the buffer, using the {@link RenderHelper#ICON_OVERLAY} vertex format
	 *
	 * @param startX   Start x position in blocks
	 * @param startY   Start position in blocks
	 * @param endX     End x position in blocks
	 * @param endY     End y position in blocks
	 *
	 * @param uvStartX UV start x position in "pixels" (1/16th sprite size)
	 * @param uvStartY UV start position in "pixels" (1/16th sprite size)
	 * @param uvEndX   UV end x position in "pixels" (1/16th sprite size)
	 * @param uvEndY   UV end y position in "pixels" (1/16th sprite size)
	 */
	public static void renderIconFullBright(
			PoseStack ms, VertexConsumer buffer,
			float startX, float startY, float endX, float endY,
			int uvStartX, int uvStartY, int uvEndX, int uvEndY,
			TextureAtlasSprite icon, int color, float alpha, int light) {
		Matrix4f mat = ms.last().pose();
		float red = ((color >> 16) & 0xFF) / 255F;
		float green = ((color >> 8) & 0xFF) / 255F;
		float blue = (color & 0xFF) / 255F;

		buffer.addVertex(mat, startX, endY, 0).setColor(red, green, blue, alpha).setUv(icon.getU(uvStartX), icon.getV(uvEndY)).uv2(light);
		buffer.addVertex(mat, endX, endY, 0).setColor(red, green, blue, alpha).setUv(icon.getU(uvEndX), icon.getV(uvEndY)).uv2(light);
		buffer.addVertex(mat, endX, startY, 0).setColor(red, green, blue, alpha).setUv(icon.getU(uvEndX), icon.getV(uvStartY)).uv2(light);
		buffer.addVertex(mat, startX, startY, 0).setColor(red, green, blue, alpha).setUv(icon.getU(uvStartX), icon.getV(uvStartY)).uv2(light);
	}

	/**
	 * Draw an icon into the buffer, using the {@link RenderHelper#ICON_OVERLAY} vertex format
	 *
	 * @param uvStartX UV start x position in "pixels" (1/16th sprite size)
	 * @param uvStartY UV start position in "pixels" (1/16th sprite size)
	 * @param uvEndX   UV end x position in "pixels" (1/16th sprite size)
	 * @param uvEndY   UV end y position in "pixels" (1/16th sprite size)
	 */
	public static void renderIconCropped(
			PoseStack ms, VertexConsumer buffer,
			int uvStartX, int uvStartY, int uvEndX, int uvEndY,
			TextureAtlasSprite icon, int color, float alpha, int light) {
		renderIconFullBright(
				ms, buffer,
				uvStartX / 16F, uvStartY / 16F, uvEndX / 16F, uvEndY / 16F,
				uvStartX, uvStartY, uvEndX, uvEndY,
				icon, color, alpha, light
		);
	}

	/**
	 * Draw an icon into the buffer, using the {@link RenderHelper#ICON_OVERLAY} vertex format
	 * Renders the icon at a 1 block size with a full 16x16 UV
	 */
	public static void renderIconFullBright(
			PoseStack ms, VertexConsumer buffer,
			TextureAtlasSprite icon, int color, float alpha, int light) {
		renderIconCropped(
				ms, buffer,
				0, 0, 16, 16,
				icon, color, alpha, light
		);
	}

	/**
	 * Draw an icon into the buffer, using the {@link RenderHelper#ICON_OVERLAY} vertex format
	 * Renders the icon in fullbright, at a 1 block size with a full 16x16 UV
	 */
	public static void renderIconFullBright(
			PoseStack ms, VertexConsumer buffer,
			TextureAtlasSprite icon, int color, float alpha) {
		int fullbright = 0xF000F0;
		renderIconFullBright(ms, buffer, icon, color, alpha, fullbright);
	}

	/**
	 * Draw an icon into the buffer, using the {@link RenderHelper#ICON_OVERLAY} vertex format
	 * Renders the icon in fullbright, with no color modification, at a 1 block size with a full 16x16 UV
	 */
	public static void renderIconFullBright(
			PoseStack ms, VertexConsumer buffer,
			TextureAtlasSprite icon, float alpha) {
		renderIconFullBright(ms, buffer, icon, 0xFFFFFF, alpha);
	}

	private static class AstrolabeLayer extends RenderType {
		public AstrolabeLayer() {
			super(ResourcesLib.PREFIX_MOD + "astrolabe", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true,
					() -> {
						Sheets.translucentCullBlockSheet().setupRenderState();
						RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.4F);
					}, () -> {
						Sheets.translucentCullBlockSheet().clearRenderState();
						RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
					});
		}
	}

	public static void renderGuiItemAlpha(ItemStack stack, int x, int y, int alpha, ItemRenderer renderer) {
		renderGuiItemAlpha(stack, x, y, alpha, renderer.getModel(stack, null, null, 0), renderer);
	}

	/**
	 * Like {@link ItemRenderer::renderGuiItem} but with alpha
	 */
	// [VanillaCopy] with a small change
	public static void renderGuiItemAlpha(ItemStack stack, int x, int y, int alpha, BakedModel model, ItemRenderer renderer) {
		Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);

		RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		PoseStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushPose();
		modelViewStack.translate(x, y, 100.0F);
		modelViewStack.translate(8.0D, 8.0D, 0.0D);
		modelViewStack.scale(1.0F, -1.0F, 1.0F);
		modelViewStack.scale(16.0F, 16.0F, 16.0F);
		RenderSystem.applyModelViewMatrix();

		boolean flatLight = !model.usesBlockLight();
		if (flatLight) {
			Lighting.setupForFlatItems();
		}

		MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
		renderer.render(
				stack,
				ItemDisplayContext.GUI,
				false,
				new PoseStack(),
				// This part differs from vanilla. We wrap the buffer to allow drawing translucently
				wrapBuffer(buffer, alpha, alpha < 255),
				LightTexture.FULL_BRIGHT,
				OverlayTexture.NO_OVERLAY,
				model
		);
		buffer.endBatch();

		RenderSystem.enableDepthTest();

		if (flatLight) {
			Lighting.setupFor3DItems();
		}

		modelViewStack.popPose();
		RenderSystem.applyModelViewMatrix();
	}

	private static MultiBufferSource wrapBuffer(MultiBufferSource buffer, int alpha, boolean forceTranslucent) {
		return renderType -> new GhostVertexConsumer(buffer.getBuffer(forceTranslucent ? TRANSLUCENT : renderType), alpha);
	}

	/*
	* Renders a transparent black box with a soft border. The parameters describe the inner box, there will also be drawn
	* another box that is 2px bigger in each direction
	*/
	public static void renderHUDBox(GuiGraphics gui, int startX, int startY, int endX, int endY) {
		gui.fill(startX, startY, endX, endY, 0x44000000);
		gui.fill(startX - 2, startY - 2, endX + 2, endY + 2, 0x44000000);
	}

	/*
	* Renders an item and its name, vertically centered next to it. Renders nothing if the stack is empty
	* Note: The item renderer does not respect the PoseStack
	*/
	public static void renderItemWithName(GuiGraphics gui, Minecraft mc, ItemStack itemStack, int startX, int startY, int color) {
		if (!itemStack.isEmpty()) {
			gui.drawString(mc.font, itemStack.getHoverName(), startX + ITEM_AND_PADDING_WIDTH, startY + 4, color);
			gui.renderItem(itemStack, startX, startY);
		}
	}

	public static void renderItemWithNameCentered(GuiGraphics gui, Minecraft mc, ItemStack itemStack, int startY, int color) {
		int centerX = mc.getWindow().getGuiScaledWidth() / 2;
		int startX = centerX - (ITEM_AND_PADDING_WIDTH + mc.font.width(itemStack.getHoverName())) / 2;
		renderItemWithName(gui, mc, itemStack, startX, startY, color);
	}

	/*
	* Returns the width of an item and its text, as rendered by renderItemWithName
	*/
	public static int itemWithNameWidth(Minecraft mc, ItemStack itemStack) {
		return ITEM_AND_PADDING_WIDTH + mc.font.width(itemStack.getHoverName());
	}

	// Borrowed with permission from https://github.com/XFactHD/FramedBlocks/blob/14f468810fc416b39447512810f0aa86e1012335/src/main/java/xfacthd/framedblocks/client/util/GhostVertexConsumer.java
	public record GhostVertexConsumer(VertexConsumer wrapped, int alpha) implements VertexConsumer {
		@Override
		public @NotNull VertexConsumer addVertex(float x, float y, float z) {
			return wrapped.addVertex(x, y, z);
		}

		@Override
		public @NotNull VertexConsumer setColor(int red, int green, int blue, int alpha) {
			return wrapped.setColor(red, green, blue, (alpha * this.alpha) / 0xFF);
		}

		@Override
		public @NotNull VertexConsumer setUv(float u, float v) {
			return wrapped.setUv(u, v);
		}

		@Override
		public @NotNull VertexConsumer setUv1(int u, int v) {
			return wrapped.setUv1(u, v);
		}

		@Override
		public VertexConsumer overlayCoords(int u, int v) {
			return wrapped.overlayCoords(u, v);
		}

		@Override
		public @NotNull VertexConsumer setUv2(int u, int v) {
			return wrapped.setUv2(u, v);
		}

		@Override
		public @NotNull VertexConsumer setNormal(float x, float y, float z) {
			return wrapped.setNormal(x, y, z);
		}

		@Override
		public void endVertex() {
			wrapped.endVertex();
		}

		@Override
		public void defaultColor(int r, int g, int b, int a) {
			wrapped.defaultColor(r, g, b, a);
		}

		@Override
		public void unsetDefaultColor() {
			wrapped.unsetDefaultColor();
		}
	}
}
