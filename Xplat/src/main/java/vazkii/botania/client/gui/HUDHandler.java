/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.gui;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import org.lwjgl.opengl.GL11;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.api.recipe.ManaInfusionRecipe;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.block_entity.PetalApothecaryBlockEntity;
import vazkii.botania.common.block.block_entity.RunicAltarBlockEntity;
import vazkii.botania.common.block.block_entity.corporea.CorporeaCrystalCubeBlockEntity;
import vazkii.botania.common.block.block_entity.corporea.CorporeaIndexBlockEntity;
import vazkii.botania.common.block.block_entity.mana.ManaPoolBlockEntity;
import vazkii.botania.common.handler.EquipmentHandler;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.item.AssemblyHaloItem;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.WandOfTheForestItem;
import vazkii.botania.common.item.WorldshaperssSextantItem;
import vazkii.botania.common.item.equipment.bauble.FlugelTiaraItem;
import vazkii.botania.common.item.equipment.bauble.ManaseerMonocleItem;
import vazkii.botania.common.item.equipment.bauble.RingOfDexterousMotionItem;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.xplat.BotaniaConfig;
import vazkii.botania.xplat.ClientXplatAbstractions;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.List;

public final class HUDHandler {

	private HUDHandler() {}

	public static final ResourceLocation manaBar = ResourceLocation.parse(ResourcesLib.GUI_MANA_HUD);

	private static boolean didOptifineDetection = false;

	public static void tryOptifineWarning() {
		if (!didOptifineDetection) {
			try {
				Class.forName("optifine.Installer");
				Minecraft.getInstance().player.sendSystemMessage(Component.translatable("botaniamisc.optifine_warning"));
			} catch (ClassNotFoundException ignored) {}
			didOptifineDetection = true;
		}
	}

	public static void onDrawScreenPost(GuiGraphics gui, float partialTicks) {
		PoseStack ms = gui.pose();
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.hideGui) {
			return;
		}
		ProfilerFiller profiler = mc.getProfiler();
		ItemStack main = mc.player.getMainHandItem();
		ItemStack offhand = mc.player.getOffhandItem();

		profiler.push("botania-hud");

		if (Minecraft.getInstance().gameMode.canHurtPlayer()) {
			ItemStack tiara = EquipmentHandler.findOrEmpty(BotaniaItems.flightTiara, mc.player);
			if (!tiara.isEmpty()) {
				profiler.push("flugelTiara");
				FlugelTiaraItem.ClientLogic.renderHUD(gui, partialTicks, mc.player, tiara);
				profiler.pop();
			}

			ItemStack dodgeRing = EquipmentHandler.findOrEmpty(BotaniaItems.dodgeRing, mc.player);
			if (!dodgeRing.isEmpty()) {
				profiler.push("dodgeRing");
				RingOfDexterousMotionItem.ClientLogic.renderHUD(gui, mc.player, dodgeRing, partialTicks);
				profiler.pop();
			}
		}

		HitResult pos = mc.hitResult;

		if (pos instanceof BlockHitResult result) {
			BlockPos bpos = result.getBlockPos();

			BlockState state = mc.level.getBlockState(bpos);
			BlockEntity tile = mc.level.getBlockEntity(bpos);

			if (PlayerHelper.hasAnyHeldItem(mc.player)) {
				boolean alternateRecipeHudPosition = false;
				if (PlayerHelper.hasHeldItemClass(mc.player, WandOfTheForestItem.class)) {
					tryOptifineWarning();
					var hud = ClientXplatAbstractions.INSTANCE.findWandHud(mc.level, bpos, state, tile);
					if (hud != null) {
						alternateRecipeHudPosition = true;
						profiler.push("wandItem");
						hud.renderHUD(gui, mc);
						profiler.pop();
					}
				}
				if (tile instanceof ManaPoolBlockEntity pool && !mc.player.getMainHandItem().isEmpty()) {
					renderPoolRecipeHUD(gui, pool, mc.player.getMainHandItem(), alternateRecipeHudPosition);
				}
			}
			if (!PlayerHelper.hasHeldItem(mc.player, BotaniaItems.lexicon)) {
				if (tile instanceof PetalApothecaryBlockEntity altar) {
					PetalApothecaryBlockEntity.Hud.render(altar, gui, mc);
				} else if (tile instanceof RunicAltarBlockEntity runeAltar) {
					RunicAltarBlockEntity.Hud.render(runeAltar, gui, mc);
				} else if (tile instanceof CorporeaCrystalCubeBlockEntity cube) {
					CorporeaCrystalCubeBlockEntity.Hud.render(gui, cube);
				}
			}
		} else if (pos instanceof EntityHitResult result) {
			var hud = ClientXplatAbstractions.INSTANCE.findWandHud(result.getEntity());
			if (hud != null && PlayerHelper.hasHeldItemClass(mc.player, WandOfTheForestItem.class)) {
				profiler.push("wandItemEntityHud");
				hud.renderHUD(gui, mc);
				profiler.pop();
			}
		}

		if (!CorporeaIndexBlockEntity.getNearbyValidIndexes(mc.player).isEmpty() && mc.screen instanceof ChatScreen) {
			profiler.push("nearIndex");
			renderNearIndexDisplay(gui);
			profiler.pop();
		}

		if (!main.isEmpty() && main.getItem() instanceof AssemblyHaloItem) {
			profiler.push("craftingHalo_main");
			AssemblyHaloItem.Rendering.renderHUD(gui, mc.player, main);
			profiler.pop();
		} else if (!offhand.isEmpty() && offhand.getItem() instanceof AssemblyHaloItem) {
			profiler.push("craftingHalo_off");
			AssemblyHaloItem.Rendering.renderHUD(gui, mc.player, offhand);
			profiler.pop();
		}

		if (!main.isEmpty() && main.getItem() instanceof WorldshaperssSextantItem) {
			profiler.push("sextant");
			WorldshaperssSextantItem.Hud.render(gui, mc.player, main);
			profiler.pop();
		}

		/*if(equippedStack != null && equippedStack.is(BotaniaItems.flugelEye)) {
			profiler.startSection("flugelEye");
			EyeOfTheFlugelItem.renderHUD(event.getResolution(), mc.player, equippedStack);
			profiler.endSection();
		}*/

		if (ManaseerMonocleItem.hasMonocle(mc.player)) {
			profiler.push("monocle");
			ManaseerMonocleItem.Hud.render(gui, mc.player);
			profiler.pop();
		}

		profiler.push("manaBar");

		Player player = mc.player;
		if (!player.isSpectator()) {
			int totalMana = 0;
			int totalMaxMana = 0;
			boolean anyRequest = false;

			Container mainInv = player.getInventory();
			Container accInv = BotaniaAPI.instance().getAccessoriesInventory(player);

			int invSize = mainInv.getContainerSize();
			int size = invSize + accInv.getContainerSize();

			for (int i = 0; i < size; i++) {
				boolean useAccessories = i >= invSize;
				Container inv = useAccessories ? accInv : mainInv;
				ItemStack stack = inv.getItem(i - (useAccessories ? invSize : 0));

				if (!stack.isEmpty()) {
					anyRequest = anyRequest || stack.is(BotaniaTags.Items.MANA_USING_ITEMS);
				}
			}

			List<ItemStack> items = ManaItemHandler.instance().getManaItems(player);
			List<ItemStack> acc = ManaItemHandler.instance().getManaAccesories(player);
			for (ItemStack stack : Iterables.concat(items, acc)) {
				var manaItem = XplatAbstractions.INSTANCE.findManaItem(stack);
				if (!manaItem.isNoExport()) {
					totalMana += manaItem.getMana();
					totalMaxMana += manaItem.getMaxMana();
				}
			}

			if (anyRequest) {
				renderManaInvBar(gui, totalMana, totalMaxMana);
			}
		}

		profiler.popPush("itemsRemaining");
		ItemsRemainingRenderHandler.render(gui, partialTicks);
		profiler.pop();
		profiler.pop();

		RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
	}

	private static void renderManaInvBar(GuiGraphics gui, int totalMana, int totalMaxMana) {
		Minecraft mc = Minecraft.getInstance();
		int width = 182;
		int x = mc.getWindow().getGuiScaledWidth() / 2 - width / 2;
		int y = mc.getWindow().getGuiScaledHeight() - BotaniaConfig.client().manaBarHeight();

		if (totalMaxMana == 0) {
			width = 0;
		} else {
			width *= (double) totalMana / (double) totalMaxMana;
		}

		if (width == 0) {
			if (totalMana > 0) {
				width = 1;
			} else {
				return;
			}
		}

		int color = Mth.hsvToRgb(0.55F, (float) Math.min(1F, Math.sin(Util.getMillis() / 200D) * 0.5 + 1F), 1F);
		int r = (color >> 16 & 0xFF);
		int g = (color >> 8 & 0xFF);
		int b = color & 0xFF;
		RenderSystem.setShaderColor(r / 255F, g / 255F, b / 255F, 1 - (r / 255F));

		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderHelper.drawTexturedModalRect(gui, manaBar, x, y, 0, 251, width, 5);
		RenderSystem.disableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

	private static void renderPoolRecipeHUD(GuiGraphics gui, ManaPoolBlockEntity tile, ItemStack stack, boolean alternateRecipeHudPosition) {
		Minecraft mc = Minecraft.getInstance();
		ProfilerFiller profiler = mc.getProfiler();

		profiler.push("poolRecipe");
		ManaInfusionRecipe recipe = tile.getMatchingRecipe(stack, tile.getLevel().getBlockState(tile.getBlockPos().below()));
		if (recipe != null) {
			int x = mc.getWindow().getGuiScaledWidth() / 2 - 11;
			int y = mc.getWindow().getGuiScaledHeight() / 2 + (alternateRecipeHudPosition ? -25 : 10);

			int u = tile.getCurrentMana() >= recipe.getManaToConsume() ? 0 : 22;
			int v = mc.player.getName().getString().equals("haighyorkie") && mc.player.isShiftKeyDown() ? 23 : 8;

			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			RenderHelper.drawTexturedModalRect(gui, manaBar, x, y, u, v, 22, 15);
			RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

			gui.renderItem(stack, x - 20, y);
			ItemStack result = recipe.getResultItem(mc.level.registryAccess());
			gui.renderItem(result, x + 26, y);
			gui.renderItemDecorations(mc.font, result, x + 26, y);

			RenderSystem.disableBlend();
		}
		profiler.pop();
	}

	private static void renderNearIndexDisplay(GuiGraphics gui) {
		Minecraft mc = Minecraft.getInstance();
		String txt0 = I18n.get("botaniamisc.nearIndex0");
		String txt1 = ChatFormatting.GRAY + I18n.get("botaniamisc.nearIndex1");
		String txt2 = ChatFormatting.GRAY + I18n.get("botaniamisc.nearIndex2");

		int l = Math.max(mc.font.width(txt0), Math.max(mc.font.width(txt1), mc.font.width(txt2))) + 20;
		int x = mc.getWindow().getGuiScaledWidth() - l - 20;
		int y = mc.getWindow().getGuiScaledHeight() - 60;

		RenderHelper.renderHUDBox(gui, x - 4, y - 4, x + l + 4, y + 35);
		gui.renderItem(new ItemStack(BotaniaBlocks.corporeaIndex), x, y + 10);

		gui.drawString(mc.font, txt0, x + 20, y, 0xFFFFFF);
		gui.drawString(mc.font, txt1, x + 20, y + 14, 0xFFFFFF);
		gui.drawString(mc.font, txt2, x + 20, y + 24, 0xFFFFFF);
	}

	/**
	 * Renders a mana HUD below the crosshair, containing just a mana bar and a name above
	 */
	public static void drawSimpleManaHUD(GuiGraphics gui, int color, int mana, int maxMana, String name) {
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Minecraft mc = Minecraft.getInstance();
		int x = mc.getWindow().getGuiScaledWidth() / 2 - mc.font.width(name) / 2;
		int y = mc.getWindow().getGuiScaledHeight() / 2 + 10;

		gui.drawString(mc.font, name, x, y, color);

		x = mc.getWindow().getGuiScaledWidth() / 2 - 51;
		y += 10;

		renderManaBar(gui, x, y, color, 1F, mana, maxMana);

		RenderSystem.disableBlend();
	}

	/**
	 * Renders a mana HUD below the crosshair, containing a mana bar, a name above, and a bound item status to the right
	 */
	public static void drawComplexManaHUD(int color, GuiGraphics gui, int mana, int maxMana, String name, ItemStack bindDisplay, boolean properlyBound) {
		PoseStack ms = gui.pose();
		drawSimpleManaHUD(gui, color, mana, maxMana, name);

		Minecraft mc = Minecraft.getInstance();

		int x = mc.getWindow().getGuiScaledWidth() / 2 + Math.max(51, mc.font.width(name) / 2) + 4;
		int y = mc.getWindow().getGuiScaledHeight() / 2 + 12;

		gui.renderItem(bindDisplay, x, y);

		RenderSystem.disableDepthTest();
		ms.pushPose();
		// Magic number to get the string above the item we just rendered.
		ms.translate(0, 0, 200);
		if (properlyBound) {
			gui.drawString(mc.font, "✔", x + 10, y + 9, 0x004C00);
			gui.drawString(mc.font, "✔", x + 10, y + 8, 0x0BD20D);
		} else {
			gui.drawString(mc.font, "✘", x + 10, y + 9, 0x4C0000);
			gui.drawString(mc.font, "✘", x + 10, y + 8, 0xD2080D);
		}
		ms.popPose();
		RenderSystem.enableDepthTest();
	}

	public static void renderManaBar(GuiGraphics gui, int x, int y, int color, float alpha, int mana, int maxMana) {
		RenderSystem.setShaderColor(1F, 1F, 1F, alpha);
		RenderHelper.drawTexturedModalRect(gui, manaBar, x, y, 0, 0, 102, 5);

		int manaPercentage = Math.max(0, (int) ((double) mana / (double) maxMana * 100));

		if (manaPercentage == 0 && mana > 0) {
			manaPercentage = 1;
		}

		RenderHelper.drawTexturedModalRect(gui, manaBar, x + 1, y + 1, 0, 5, 100, 3);

		float red = (color >> 16 & 0xFF) / 255F;
		float green = (color >> 8 & 0xFF) / 255F;
		float blue = (color & 0xFF) / 255F;
		RenderSystem.setShaderColor(red, green, blue, alpha);
		RenderHelper.drawTexturedModalRect(gui, manaBar, x + 1, y + 1, 0, 5, Math.min(100, manaPercentage), 3);
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}
}
