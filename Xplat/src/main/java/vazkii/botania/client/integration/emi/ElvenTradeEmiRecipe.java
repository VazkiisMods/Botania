package vazkii.botania.client.integration.emi;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import dev.emi.emi.api.widget.WidgetHolder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.crafting.RecipeHolder;

import org.joml.Matrix3f;
import org.joml.Matrix4f;

import vazkii.botania.api.recipe.ElvenTradeRecipe;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class ElvenTradeEmiRecipe extends BotaniaEmiRecipe {
	private static final ResourceLocation TEXTURE = botaniaRL("textures/gui/elven_trade_overlay.png");

	public ElvenTradeEmiRecipe(RecipeHolder<? extends ElvenTradeRecipe> recipe) {
		super(BotaniaEmiPlugin.ELVEN_TRADE, recipe);
		this.input = recipe.value().getIngredients().stream().map(EmiIngredient::of).toList();
		this.output = recipe.value().getOutputs().stream().map(EmiStack::of).toList();
	}

	@Override
	public int getDisplayHeight() {
		return 90;
	}

	@Override
	public int getDisplayWidth() {
		return 120;
	}

	@Override
	public void addWidgets(WidgetHolder widgets) {
		widgets.add(new BlendTextureWidget(TEXTURE, 10, 5, 71, 75, 20, 19));
		widgets.add(new ElevenTradeWidget(12, 22));
		int sx = 35;
		for (EmiIngredient ing : input) {
			widgets.addSlot(ing, sx, 0).drawBack(false);
			sx += 18;
		}
		sx = 83;
		for (EmiStack stack : output) {
			widgets.addSlot(stack, sx, 40).drawBack(false).recipeContext(this);
			sx += 18;
		}
	}

	private static class ElevenTradeWidget extends Widget {
		private final int x, y;

		public ElevenTradeWidget(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public Bounds getBounds() {
			return new Bounds(x, y, 0, 0);
		}

		@Override
		public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {
			PoseStack matrices = gui.pose();
			TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
					.apply(botaniaRL("block/alfheim_portal_swirl"));
			MultiBufferSource.BufferSource immediate = Minecraft.getInstance().renderBuffers().bufferSource();
			VertexConsumer v = immediate.getBuffer(RenderType.solid());
			int startX = x;
			int startY = y;
			int stopX = x + 48;
			int stopY = y + 48;
			Matrix4f mat = matrices.last().pose();
			Matrix3f n = matrices.last().normal();
			v.vertex(mat, startX, startY, 0).color(1f, 1f, 1f, 1f).uv(sprite.getU0(), sprite.getV0()).uv2(0xF000F0).normal(n, 1, 0, 0).endVertex();
			v.vertex(mat, startX, stopY, 0).color(1f, 1f, 1f, 1f).uv(sprite.getU0(), sprite.getV1()).uv2(0xF000F0).normal(n, 1, 0, 0).endVertex();
			v.vertex(mat, stopX, stopY, 0).color(1f, 1f, 1f, 1f).uv(sprite.getU1(), sprite.getV1()).uv2(0xF000F0).normal(n, 1, 0, 0).endVertex();
			v.vertex(mat, stopX, startY, 0).color(1f, 1f, 1f, 1f).uv(sprite.getU1(), sprite.getV0()).uv2(0xF000F0).normal(n, 1, 0, 0).endVertex();
			immediate.endBatch();
		}

	}
}
