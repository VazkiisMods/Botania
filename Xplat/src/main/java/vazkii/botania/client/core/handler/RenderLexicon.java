/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.core.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import org.joml.Matrix4f;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.helper.VecHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.LexicaBotaniaItem;
import vazkii.botania.common.lib.LibMisc;
import vazkii.botania.xplat.BotaniaConfig;

import java.util.ArrayList;
import java.util.List;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

// Hacky way to render 3D lexicon, will be reevaluated in the future.
public class RenderLexicon {
	private static BookModel model = null;
	private static final boolean SHOULD_MISSPELL = Math.random() < 0.004;
	public static final Material TEXTURE = new Material(InventoryMenu.BLOCK_ATLAS, botaniaRL("item/lexicon_3d"));
	public static final Material ELVEN_TEXTURE = new Material(InventoryMenu.BLOCK_ATLAS, botaniaRL("item/lexicon_elven_3d"));

	private static final String[] QUOTES = new String[] {
			"\"Neat!\" - Direwolf20",
			"\"It's pretty ledge.\" - Haighyorkie",
			"\"I don't really like it.\" - CrustyMustard",
			"\"It's a very thinky mod.\" - AdamG3691",
			"\"You must craft the tiny potato.\" - TheFractangle",
			"\"Vazkii did a thing.\" - cpw"
	};

	private static final String[] MISSPELLINGS = {
			"Bonito", "Bonita", "Bonitia", "Botnaia", "Bontonio",
			"Botnia", "Bonitaaaaaaaaaa", "Botonio", "Botonia", "Botnetia",
			"Banana", "Brotania", "Botanica", "Boat", "Batania", "Bosnia"
	};

	private static int quote = -1;
	private static int misspelling = -1;

	private static BookModel getModel() {
		if (model == null) {
			model = new BookModel(Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.BOOK));
		}
		return model;
	}

	public static boolean renderHand(ItemStack stack, ItemDisplayContext type,
			boolean leftHanded, PoseStack ms, MultiBufferSource buffers, int light) {
		if (!BotaniaConfig.client().lexicon3dModel()
				|| !type.firstPerson()
				|| stack.isEmpty()
				|| !stack.is(BotaniaItems.lexicon)) {
			return false;
		}
		try {
			doRender(stack, leftHanded, ms, buffers, light, ClientTickHandler.partialTicks);
			return true;
		} catch (Throwable throwable) {
			BotaniaAPI.LOGGER.warn("Failed to render lexicon", throwable);
			return false;
		}
	}

	private static void doRender(ItemStack stack, boolean leftHanded, PoseStack ms, MultiBufferSource buffers, int light, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();

		ms.pushPose();

		float ticks = ClientTickHandler.ticksWithLexicaOpen;
		if (ticks > 0 && ticks < 10) {
			if (LexicaBotaniaItem.isOpen()) {
				ticks += partialTicks;
			} else {
				ticks -= partialTicks;
			}
		}

		if (!leftHanded) {
			ms.translate(0.3F + 0.02F * ticks, 0.125F + 0.01F * ticks, -0.2F - 0.035F * ticks);
			ms.mulPose(VecHelper.rotateY(180F + ticks * 6));
		} else {
			ms.translate(0.1F - 0.02F * ticks, 0.125F + 0.01F * ticks, -0.2F - 0.035F * ticks);
			ms.mulPose(VecHelper.rotateY(200F + ticks * 10));
		}
		ms.mulPose(VecHelper.rotateZ(-0.3F + ticks * 2.85F));
		float opening = Mth.clamp(ticks / 12F, 0, 1);

		float pageFlipTicks = ClientTickHandler.pageFlipTicks;
		if (pageFlipTicks > 0) {
			pageFlipTicks -= ClientTickHandler.partialTicks;
		}

		float pageFlip = pageFlipTicks / 5F;

		float leftPageAngle = Mth.frac(pageFlip + 0.25F) * 1.6F - 0.3F;
		float rightPageAngle = Mth.frac(pageFlip + 0.75F) * 1.6F - 0.3F;
		var model = getModel();
		model.setupAnim(ClientTickHandler.total(), Mth.clamp(leftPageAngle, 0.0F, 1.0F), Mth.clamp(rightPageAngle, 0.0F, 1.0F), opening);

		Material mat = LexicaBotaniaItem.isElven(stack) ? ELVEN_TEXTURE : TEXTURE;
		VertexConsumer buffer = mat.buffer(buffers, RenderType::entitySolid);
		model.renderToBuffer(ms, buffer, light, OverlayTexture.NO_OVERLAY, 0xffffff);

		if (ticks < 3) {
			Font font = Minecraft.getInstance().font;
			ms.mulPose(VecHelper.rotateZ(180F));
			ms.translate(-0.30F, -0.24F, -0.07F);
			ms.scale(0.0030F, 0.0030F, -0.0030F);

			if (misspelling == -1 && mc.level != null) {
				misspelling = mc.level.random.nextInt(MISSPELLINGS.length);
			}

			String title = LexicaBotaniaItem.getTitle(stack).getString();
			if (SHOULD_MISSPELL) {
				title = title.replaceAll(LibMisc.MOD_NAME, MISSPELLINGS[misspelling]);
			}
			font.drawInBatch(font.plainSubstrByWidth(title, 80), 0, 0, 0xD69700, false, ms.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, light);

			ms.translate(0F, 10F, 0F);
			ms.scale(0.6F, 0.6F, 0.6F);
			Component edition = LexicaBotaniaItem.getEdition().copy().withStyle(ChatFormatting.ITALIC, ChatFormatting.BOLD);
			font.drawInBatch(edition, 0, 0, 0xA07100, false, ms.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, light);

			if (quote == -1 && mc.level != null) {
				quote = mc.level.random.nextInt(QUOTES.length);
			}

			String quoteStr = QUOTES[quote];

			ms.translate(-5F, 15F, 0F);
			renderText(0, 0, 140, 0, 0x79ff92, quoteStr, ms.last().pose(), buffers, light);

			ms.translate(8F, 110F, 0F);
			String blurb = I18n.get("botaniamisc.lexiconcover0");
			font.drawInBatch(blurb, 0, 0, 0x79ff92, false, ms.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, light);

			ms.translate(0F, 10F, 0F);
			String blurb2 = ChatFormatting.UNDERLINE + "" + ChatFormatting.ITALIC + I18n.get("botaniamisc.lexiconcover1");
			font.drawInBatch(blurb2, 0, 0, 0x79ff92, false, ms.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, light);

			ms.translate(0F, -30F, 0F);

			String authorTitle = I18n.get("botaniamisc.lexiconcover2");
			int len = font.width(authorTitle);
			font.drawInBatch(authorTitle, 58 - len / 2F, -8, 0xD69700, false, ms.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, light);
		}

		ms.popPose();
	}

	@SuppressWarnings("SameParameterValue")
	private static void renderText(int x, int y, int width, int paragraphSize, int color, String unlocalizedText, Matrix4f matrix, MultiBufferSource buffers, int light) {
		x += 2;
		y += 10;
		width -= 4;

		Font font = Minecraft.getInstance().font;
		String text = I18n.get(unlocalizedText).replaceAll("&", "\u00a7");
		String[] textEntries = text.split("<br>");

		List<List<String>> lines = new ArrayList<>();

		String controlCodes;
		for (String s : textEntries) {
			List<String> words = new ArrayList<>();
			String lineStr = "";
			String[] tokens = s.split(" ");
			for (String token : tokens) {
				String prev = lineStr;
				String spaced = token + " ";
				lineStr += spaced;

				controlCodes = toControlCodes(getControlCodes(prev));
				if (font.width(lineStr) > width) {
					lines.add(words);
					lineStr = controlCodes + spaced;
					words = new ArrayList<>();
				}

				words.add(controlCodes + token);
			}

			if (!lineStr.isEmpty()) {
				lines.add(words);
			}
			lines.add(new ArrayList<>());
		}

		for (List<String> words : lines) {
			int xi = x;
			int spacing = 4;

			for (String s : words) {
				int extra = 0;
				font.drawInBatch(s, xi, y, color, false, matrix, buffers, Font.DisplayMode.NORMAL, 0, light);
				xi += font.width(s) + spacing + extra;
			}

			y += words.isEmpty() ? paragraphSize : 10;
		}
	}

	private static String getControlCodes(String s) {
		String controls = s.replaceAll("(?<!\u00a7)(.)", "");
		return controls.replaceAll(".*r", "r");
	}

	private static String toControlCodes(String s) {
		return s.replaceAll(".", "\u00a7$0");
	}
}
