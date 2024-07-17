/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.*;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.gui.crafting.AssemblyHaloContainer;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.annotations.SoftImplement;
import vazkii.botania.common.crafting.BotaniaRecipeTypes;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.helper.VecHelper;
import vazkii.botania.network.EffectType;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.xplat.XplatAbstractions;

public class AssemblyHaloItem extends Item {

	private static final ResourceLocation glowTexture = new ResourceLocation(ResourcesLib.MISC_GLOW_GREEN);
	private static final ItemStack craftingTable = new ItemStack(Blocks.CRAFTING_TABLE);

	public static final int SEGMENTS = 12;

	private static final String TAG_LAST_CRAFTING = "lastCrafting";
	private static final String TAG_STORED_RECIPE_PREFIX = "storedRecipe";
	private static final String TAG_EQUIPPED = "equipped";
	private static final String TAG_ROTATION_BASE = "rotationBase";

	public AssemblyHaloItem(Properties props) {
		super(props);
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!world.isClientSide) {
			int segment = getSegmentLookedAt(stack, player);
			Recipe<?> recipe = getSavedRecipe(world, stack, segment);

			if (segment == 0) {
				// Pos is never used by workbench, so use origin.
				// But this cannot be the static dummy one in the interface, we have to pass an actual one for the
				// crafting matrix to update properly.
				ContainerLevelAccess wp = ContainerLevelAccess.create(world, BlockPos.ZERO);
				player.openMenu(new SimpleMenuProvider(
						(windowId, playerInv, p) -> new AssemblyHaloContainer(windowId, playerInv, wp),
						stack.getHoverName()));
			} else {
				if (recipe == null) {
					Recipe<?> lastRecipe = getLastRecipe(world, stack);
					if (lastRecipe != null) {
						saveRecipe(stack, lastRecipe.getId(), segment);
					}
				} else {
					tryCraft(player, stack, segment, true);
				}
			}
		}

		return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int pos, boolean equipped) {
		if (!(entity instanceof LivingEntity living)) {
			return;
		}

		boolean eqLastTick = wasEquipped(stack);

		if (!equipped && living.getOffhandItem() == stack) {
			equipped = true;
		}

		if (eqLastTick != equipped) {
			setEquipped(stack, equipped);
		}

		if (!equipped) {
			int angles = 360;
			int segAngles = angles / SEGMENTS;
			float shift = segAngles / 2.0F;
			setRotationBase(stack, getCheckingAngle((LivingEntity) entity) - shift);
		}
	}

	private static boolean hasRoomFor(Inventory inv, ItemStack stack) {
		Inventory dummy = new Inventory(inv.player);
		for (int i = 0; i < inv.items.size(); i++) {
			dummy.items.set(i, inv.items.get(i).copy());
		}
		// warning: must be careful to not cause side effects / dupes with dummy here
		return dummy.add(stack.copy());
	}

	private static boolean canCraftHeuristic(Player player, Recipe<CraftingContainer> recipe) {
		StackedContents accounter = new StackedContents();
		player.getInventory().fillStackedContents(accounter);
		return accounter.canCraft(recipe, null);
	}

	void tryCraft(Player player, ItemStack halo, int slot, boolean particles) {
		Recipe<CraftingContainer> recipe = getSavedRecipe(player.level(), halo, slot);
		if (recipe == null) {
			return;
		}

		CraftingMenu dummy = new CraftingMenu(-999, player.getInventory());
		CraftingContainer craftInv = (CraftingContainer) dummy.getSlot(1).container;
		RecipePlacer placer = new RecipePlacer(dummy);

		// Try placing the recipe into the dummy workbench, extracting items from player's inventory to do so
		if (!placer.place((ServerPlayer) player, recipe)) {
			return;
		}

		// Double check that the recipe matches
		if (!recipe.matches(craftInv, player.level())) {
			// If the placer worked but the recipe still didn't, this might be a dynamic recipe with special conditions.
			// Return items to the inventory and bail.
			placer.clearGrid();
			return;
		}

		ItemStack result = recipe.assemble(craftInv, player.level().registryAccess());

		// Check if we have room for the result
		if (!hasRoomFor(player.getInventory(), result)) {
			placer.clearGrid();
			return;
		}

		// Now we are good to go. Give the result
		player.getInventory().add(result);

		// Give or toss all byproducts
		NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(craftInv);
		remainingItems.forEach(s -> player.getInventory().placeItemBackInInventory(s));

		// The items we consumed will stay in the dummy workbench and get deleted

		if (particles) {
			XplatAbstractions.INSTANCE.sendToTracking(player, new BotaniaEffectPacket(EffectType.HALO_CRAFT,
					player.getX(), player.getY(), player.getZ(), player.getId()));
		}
	}

	@SoftImplement("IItemExtension")
	public boolean onEntitySwing(ItemStack stack, LivingEntity living) {
		int segment = getSegmentLookedAt(stack, living);
		if (segment == 0) {
			return false;
		}

		Recipe<?> recipe = getSavedRecipe(living.level(), stack, segment);
		if (recipe != null && living.isShiftKeyDown()) {
			saveRecipe(stack, null, segment);
			return true;
		}

		return false;
	}

	protected static int getSegmentLookedAt(ItemStack stack, LivingEntity living) {
		float yaw = getCheckingAngle(living, getRotationBase(stack));

		int angles = 360;
		int segAngles = angles / SEGMENTS;
		for (int seg = 0; seg < SEGMENTS; seg++) {
			float calcAngle = (float) seg * segAngles;
			if (yaw >= calcAngle && yaw < calcAngle + segAngles) {
				return seg;
			}
		}
		return -1;
	}

	private static float getCheckingAngle(LivingEntity living) {
		return getCheckingAngle(living, 0F);
	}

	// Screw the way minecraft handles rotation
	// Really...
	private static float getCheckingAngle(LivingEntity living, float base) {
		float yaw = Mth.wrapDegrees(living.getYRot()) + 90F;
		int angles = 360;
		int segAngles = angles / SEGMENTS;
		float shift = segAngles / 2;

		if (yaw < 0) {
			yaw = 180F + (180F + yaw);
		}
		yaw -= 360F - base;
		float angle = 360F - yaw + shift;

		if (angle < 0) {
			angle = 360F + angle;
		}

		return angle;
	}

	@Nullable
	private static Recipe<CraftingContainer> getSavedRecipe(Level world, ItemStack halo, int position) {
		String savedId = ItemNBTHelper.getString(halo, TAG_STORED_RECIPE_PREFIX + position, "");
		ResourceLocation id = savedId.isEmpty() ? null : ResourceLocation.tryParse(savedId);

		if (position <= 0 || position >= SEGMENTS || id == null) {
			return null;
		} else {
			return BotaniaRecipeTypes.getRecipes(world, RecipeType.CRAFTING).get(id);
		}
	}

	private static void saveRecipe(ItemStack halo, @Nullable ResourceLocation id, int position) {
		if (id == null) {
			ItemNBTHelper.removeEntry(halo, TAG_STORED_RECIPE_PREFIX + position);
		} else {
			ItemNBTHelper.setString(halo, TAG_STORED_RECIPE_PREFIX + position, id.toString());
		}
	}

	private static ItemStack getDisplayItem(Level world, ItemStack stack, int position) {
		if (position == 0) {
			return craftingTable;
		} else if (position >= SEGMENTS) {
			return ItemStack.EMPTY;
		} else {
			Recipe<?> recipe = getSavedRecipe(world, stack, position);
			if (recipe != null) {
				return recipe.getResultItem(world.registryAccess());
			} else {
				return ItemStack.EMPTY;
			}
		}
	}

	public static void onItemCrafted(Player player, Container inv) {
		AbstractContainerMenu container = player.containerMenu;

		if (!(container instanceof AssemblyHaloContainer) ||
				!(inv instanceof CraftingContainer cc)) {
			return;
		}

		player.level().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, cc, player.level()).ifPresent(recipe -> {
			for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
				ItemStack stack = player.getInventory().getItem(i);
				if (!stack.isEmpty() && stack.getItem() instanceof AssemblyHaloItem) {
					rememberLastRecipe(recipe.getId(), stack);
				}
			}
		});
	}

	private static void rememberLastRecipe(ResourceLocation recipeId, ItemStack halo) {
		ItemNBTHelper.setString(halo, TAG_LAST_CRAFTING, recipeId.toString());
	}

	@Nullable
	private static Recipe<CraftingContainer> getLastRecipe(Level world, ItemStack halo) {
		String savedId = ItemNBTHelper.getString(halo, TAG_LAST_CRAFTING, "");
		ResourceLocation id = savedId.isEmpty() ? null : ResourceLocation.tryParse(savedId);

		return BotaniaRecipeTypes.getRecipes(world, RecipeType.CRAFTING).get(id);
	}

	private static boolean wasEquipped(ItemStack stack) {
		return ItemNBTHelper.getBoolean(stack, TAG_EQUIPPED, false);
	}

	private static void setEquipped(ItemStack stack, boolean equipped) {
		ItemNBTHelper.setBoolean(stack, TAG_EQUIPPED, equipped);
	}

	private static float getRotationBase(ItemStack stack) {
		return ItemNBTHelper.getFloat(stack, TAG_ROTATION_BASE, 0F);
	}

	private static void setRotationBase(ItemStack stack, float rotation) {
		ItemNBTHelper.setFloat(stack, TAG_ROTATION_BASE, rotation);
	}

	public ResourceLocation getGlowResource(ItemStack stack) {
		return glowTexture;
	}

	public static class Rendering {
		public static void onRenderWorldLast(Camera camera, float partialTicks, PoseStack ms, RenderBuffers buffers) {
			Player player = Minecraft.getInstance().player;
			ItemStack stack = PlayerHelper.getFirstHeldItemClass(player, AssemblyHaloItem.class);
			if (stack.isEmpty()) {
				return;
			}

			MultiBufferSource.BufferSource bufferSource = buffers.bufferSource();

			double renderPosX = camera.getPosition().x();
			double renderPosY = camera.getPosition().y();
			double renderPosZ = camera.getPosition().z();

			ms.pushPose();
			float alpha = ((float) Math.sin((ClientTickHandler.ticksInGame + partialTicks) * 0.2F) * 0.5F + 0.5F) * 0.4F + 0.3F;

			double posX = player.xo + (player.getX() - player.xo) * partialTicks;
			double posY = player.yo + (player.getY() - player.yo) * partialTicks + player.getEyeHeight();
			double posZ = player.zo + (player.getZ() - player.zo) * partialTicks;

			ms.translate(posX - renderPosX, posY - renderPosY, posZ - renderPosZ);

			float base = getRotationBase(stack);
			int angles = 360;
			int segAngles = angles / SEGMENTS;
			float shift = base - segAngles / 2.0F;

			float u = 1F;
			float v = 0.25F;

			float s = 3F;
			float m = 0.8F;
			float y = v * s * 2;
			float y0 = 0;

			int segmentLookedAt = getSegmentLookedAt(stack, player);
			AssemblyHaloItem item = (AssemblyHaloItem) stack.getItem();
			RenderType layer = RenderHelper.getHaloLayer(item.getGlowResource(stack));

			for (int seg = 0; seg < SEGMENTS; seg++) {
				boolean inside = false;
				float rotationAngle = (seg + 0.5F) * segAngles + shift;
				ms.pushPose();
				ms.mulPose(VecHelper.rotateY(rotationAngle));
				ms.translate(s * m, -0.75F, 0F);

				if (segmentLookedAt == seg) {
					inside = true;
				}

				ItemStack slotStack = getDisplayItem(player.level(), stack, seg);
				if (!slotStack.isEmpty()) {
					float scale = seg == 0 ? 0.9F : 0.8F;
					ms.scale(scale, scale, scale);
					ms.mulPose(VecHelper.rotateY(180F));
					ms.translate(seg == 0 ? 0.5F : 0F, seg == 0 ? -0.1F : 0.6F, 0F);

					ms.mulPose(VecHelper.rotateY(90.0F));
					Minecraft.getInstance().getItemRenderer().renderStatic(slotStack, ItemDisplayContext.GUI,
							0xF000F0, OverlayTexture.NO_OVERLAY, ms, bufferSource, player.level(), player.getId());
				}
				ms.popPose();

				ms.pushPose();
				ms.mulPose(VecHelper.rotateX(180));
				float r = 1, g = 1, b = 1, a = alpha;
				if (inside) {
					a += 0.3F;
					y0 = -y;
				}

				if (seg % 2 == 0) {
					r = g = b = 0.6F;
				}

				VertexConsumer buffer = bufferSource.getBuffer(layer);
				for (int i = 0; i < segAngles; i++) {
					Matrix4f mat = ms.last().pose();
					float ang = i + seg * segAngles + shift;
					float xp = (float) Math.cos(ang * Math.PI / 180F) * s;
					float zp = (float) Math.sin(ang * Math.PI / 180F) * s;

					buffer.vertex(mat, xp * m, y, zp * m).color(r, g, b, a).uv(u, v).endVertex();
					buffer.vertex(mat, xp, y0, zp).color(r, g, b, a).uv(u, 0).endVertex();

					xp = (float) Math.cos((ang + 1) * Math.PI / 180F) * s;
					zp = (float) Math.sin((ang + 1) * Math.PI / 180F) * s;

					buffer.vertex(mat, xp, y0, zp).color(r, g, b, a).uv(0, 0).endVertex();
					buffer.vertex(mat, xp * m, y, zp * m).color(r, g, b, a).uv(0, v).endVertex();
				}
				y0 = 0;
				ms.popPose();
			}
			ms.popPose();
			bufferSource.endBatch();
		}

		public static void renderHUD(GuiGraphics gui, Player player, ItemStack stack) {
			Minecraft mc = Minecraft.getInstance();
			int slot = getSegmentLookedAt(stack, player);

			if (slot == 0) {
				String name = craftingTable.getHoverName().getString();
				int l = mc.font.width(name);
				int x = mc.getWindow().getGuiScaledWidth() / 2 - l / 2;
				int y = mc.getWindow().getGuiScaledHeight() / 2 - 65;

				gui.fill(x - 6, y - 6, x + l + 6, y + 37, 0x22000000);
				gui.fill(x - 4, y - 4, x + l + 4, y + 35, 0x22000000);
				gui.renderItem(craftingTable, mc.getWindow().getGuiScaledWidth() / 2 - 8, mc.getWindow().getGuiScaledHeight() / 2 - 52);

				gui.drawString(mc.font, name, x, y, 0xFFFFFF);
			} else {
				Recipe<CraftingContainer> recipe = getSavedRecipe(player.level(), stack, slot);
				Component label;
				boolean setRecipe = false;

				if (recipe == null) {
					label = Component.translatable("botaniamisc.unsetRecipe");
					recipe = getLastRecipe(player.level(), stack);
				} else {
					label = recipe.getResultItem(player.level().registryAccess()).getHoverName();
					setRecipe = true;
				}

				renderRecipe(gui, label, recipe, player, setRecipe);
			}
		}

		private static void renderRecipe(GuiGraphics gui, Component label, @Nullable Recipe<CraftingContainer> recipe, Player player, boolean isSavedRecipe) {
			Minecraft mc = Minecraft.getInstance();

			ItemStack recipeResult;
			if (recipe != null && !(recipeResult = recipe.getResultItem(player.level().registryAccess())).isEmpty()) {
				int x = mc.getWindow().getGuiScaledWidth() / 2 - 45;
				int y = mc.getWindow().getGuiScaledHeight() / 2 - 90;

				gui.fill(x - 6, y - 6, x + 90 + 6, y + 60, 0x22000000);
				gui.fill(x - 4, y - 4, x + 90 + 4, y + 58, 0x22000000);

				gui.fill(x + 66, y + 14, x + 92, y + 40, 0x22000000);
				gui.fill(x - 2, y - 2, x + 56, y + 56, 0x22000000);

				int wrap = recipe instanceof ShapedRecipe shaped ? shaped.getWidth() : 3;
				for (int i = 0; i < recipe.getIngredients().size(); i++) {
					Ingredient ingr = recipe.getIngredients().get(i);
					if (ingr != Ingredient.EMPTY) {
						ItemStack stack = ingr.getItems()[ClientTickHandler.ticksInGame / 20 % ingr.getItems().length];
						int xpos = x + i % wrap * 18;
						int ypos = y + i / wrap * 18;
						gui.fill(xpos, ypos, xpos + 16, ypos + 16, 0x22000000);

						gui.renderItem(stack, xpos, ypos);
					}
				}

				gui.renderItem(recipeResult, x + 72, y + 18);
				gui.renderItemDecorations(mc.font, recipeResult, x + 72, y + 18);

			}

			int yoff = 110;
			if (isSavedRecipe && recipe != null && !canCraftHeuristic(player, recipe)) {
				String warning = ChatFormatting.RED + I18n.get("botaniamisc.cantCraft");
				gui.drawCenteredString(mc.font, warning, mc.getWindow().getGuiScaledWidth() / 2, mc.getWindow().getGuiScaledHeight() / 2 - yoff, 0xFFFFFF);
				yoff += 12;
			}

			gui.drawCenteredString(mc.font, label, mc.getWindow().getGuiScaledWidth() / 2, mc.getWindow().getGuiScaledHeight() / 2 - yoff, 0xFFFFFF);
		}
	}

	public static class RecipePlacer extends ServerPlaceRecipe<CraftingContainer> {
		public RecipePlacer(RecipeBookMenu<CraftingContainer> container) {
			super(container);
		}

		// [VanillaCopy] Based on super.recipeClicked
		public boolean place(ServerPlayer player, @Nullable Recipe<CraftingContainer> recipe) {
			if (recipe != null) {
				this.inventory = player.getInventory();
				this.stackedContents.clear();
				player.getInventory().fillStackedContents(this.stackedContents);
				this.menu.fillCraftSlotsStackedContents(this.stackedContents);

				boolean ret;
				if (this.stackedContents.canCraft(recipe, null)) {
					this.handleRecipeClicked(recipe, false);
					ret = true;
				} else {
					this.clearGrid();
					ret = false;
				}

				player.getInventory().setChanged();
				return ret;
			}
			return false;
		}

		// Make public
		@Override
		public void clearGrid() {
			super.clearGrid();
		}
	}
}
