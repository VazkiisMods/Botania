/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.render.block_entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import vazkii.botania.client.core.handler.MiscellaneousModels;
import vazkii.botania.client.core.proxy.ClientProxy;
import vazkii.botania.client.lib.ResourcesLib;
import vazkii.botania.common.block.block_entity.TinyPotatoBlockEntity;
import vazkii.botania.common.handler.ContributorList;
import vazkii.botania.common.helper.VecHelper;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.common.item.block.TinyPotatoBlockItem;
import vazkii.botania.common.item.equipment.bauble.FlugelTiaraItem;
import vazkii.botania.mixin.client.ModelManagerAccessor;
import vazkii.botania.xplat.ClientXplatAbstractions;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class TinyPotatoBlockEntityRenderer implements BlockEntityRenderer<TinyPotatoBlockEntity> {
	public static final String DEFAULT = "default";
	public static final String HALLOWEEN = "halloween";
	private static final Pattern ESCAPED = Pattern.compile("[^a-z0-9/._-]");
	private final BlockRenderDispatcher blockRenderDispatcher;

	public TinyPotatoBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
		this.blockRenderDispatcher = ctx.getBlockRenderDispatcher();
	}

	public static BakedModel getModelFromDisplayName(Component displayName) {
		var nameBuilder = new StringBuilder();
		TinyPotatoBlockItem.isEnchantedName(displayName, nameBuilder);
		return getModel(nameBuilder.toString().toLowerCase(Locale.ROOT));
	}

	private static BakedModel getModel(String name) {
		ModelManager bmm = Minecraft.getInstance().getModelManager();
		Map<ResourceLocation, BakedModel> mm = ((ModelManagerAccessor) bmm).getBakedRegistry();
		BakedModel missing = bmm.getMissingModel();
		ResourceLocation location = taterLocation(name);
		BakedModel model = mm.get(location);
		if (model == null) {
			if (ClientProxy.dootDoot) {
				return mm.getOrDefault(taterLocation(HALLOWEEN), missing);
			} else {
				return mm.getOrDefault(taterLocation(DEFAULT), missing);
			}
		}
		return model;
	}

	private static ResourceLocation taterLocation(String name) {
		return botaniaRL(ResourcesLib.PREFIX_TINY_POTATO + "/" + normalizeName(name));
	}

	private static String normalizeName(String name) {
		return ESCAPED.matcher(name).replaceAll("_").toLowerCase(Locale.ROOT);
	}

	@Override
	public void render(@NotNull TinyPotatoBlockEntity potato, float partialTicks, PoseStack ms, @NotNull MultiBufferSource buffers, int light, int overlay) {
		ms.pushPose();

		StringBuilder nameBuilder = new StringBuilder();
		boolean enchanted = TinyPotatoBlockItem.isEnchantedName(potato.name, nameBuilder);
		String name = nameBuilder.toString().toLowerCase(Locale.ROOT);
		RenderType layer = Sheets.translucentCullBlockSheet();
		BakedModel model = getModel(name);

		ms.translate(0.5F, 0F, 0.5F);
		Direction potatoFacing = potato.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		float rotY = 0;
		switch (potatoFacing) {
			default:
			case SOUTH:
				rotY = 180F;
				break;
			case NORTH:
				break;
			case EAST:
				rotY = 90F;
				break;
			case WEST:
				rotY = 270F;
				break;
		}
		ms.mulPose(VecHelper.rotateY(-rotY));

		float jump = potato.jumpTicks;
		if (jump > 0) {
			jump -= partialTicks;
		}

		float up = (float) Math.abs(Math.sin(jump / 10 * Math.PI)) * 0.2F;
		float rotZ = (float) Math.sin(jump / 10 * Math.PI) * 2;
		float wiggle = (float) Math.sin(jump / 10 * Math.PI) * 0.05F;

		ms.translate(wiggle, up, 0F);
		ms.mulPose(VecHelper.rotateZ(rotZ));

		boolean render = !(name.equals("mami") || name.equals("soaryn") || name.equals("eloraam") && jump != 0);
		if (render) {
			ms.pushPose();
			ms.translate(-0.5F, 0, -0.5F);
			VertexConsumer buffer = ItemRenderer.getFoilBuffer(buffers, layer, true, enchanted);

			renderModel(ms, buffer, light, overlay, model);
			ms.popPose();
		}

		ms.translate(0F, 1.5F, 0F);
		ms.pushPose();
		ms.mulPose(VecHelper.rotateZ(180F));
		renderItems(potato, potatoFacing, name, partialTicks, ms, buffers, light, overlay);

		ms.pushPose();
		ClientXplatAbstractions.INSTANCE.fireRenderTinyPotato(potato, potato.name, partialTicks, ms, buffers, light, overlay);
		ms.popPose();
		ms.popPose();

		ms.mulPose(VecHelper.rotateZ(-rotZ));
		ms.mulPose(VecHelper.rotateY(rotY));

		renderName(potato, name, ms, buffers, light);
		ms.popPose();
	}

	private void renderName(TinyPotatoBlockEntity potato, String name, PoseStack ms, MultiBufferSource buffers, int light) {
		Minecraft mc = Minecraft.getInstance();
		HitResult pos = mc.hitResult;
		if (Minecraft.renderNames()
				&& !name.isEmpty() && pos != null && pos.getType() == HitResult.Type.BLOCK
				&& potato.getBlockPos().equals(((BlockHitResult) pos).getBlockPos())) {
			ms.pushPose();
			ms.translate(0F, -0.6F, 0F);
			ms.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
			float f1 = 0.016666668F * 1.6F;
			ms.scale(-f1, -f1, f1);
			int halfWidth = mc.font.width(potato.name.getString()) / 2;

			float opacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
			int opacityRGB = (int) (opacity * 255.0F) << 24;
			mc.font.drawInBatch(potato.name, -halfWidth, 0, 0x20FFFFFF, false, ms.last().pose(), buffers, Font.DisplayMode.SEE_THROUGH, opacityRGB, light);
			mc.font.drawInBatch(potato.name, -halfWidth, 0, 0xFFFFFFFF, false, ms.last().pose(), buffers, Font.DisplayMode.NORMAL, 0, light);
			if (name.equals("pahimar") || name.equals("soaryn")) {
				ms.translate(0F, 14F, 0F);
				String str = name.equals("pahimar") ? "[WIP]" : "(soon)";
				halfWidth = mc.font.width(str) / 2;

				mc.font.drawInBatch(str, -halfWidth, 0, 0x20FFFFFF, false, ms.last().pose(), buffers, Font.DisplayMode.SEE_THROUGH, opacityRGB, light);
				mc.font.drawInBatch(str, -halfWidth, 0, 0xFFFFFFFF, false, ms.last().pose(), buffers, Font.DisplayMode.SEE_THROUGH, 0, light);
			}

			ms.popPose();
		}
	}

	private void renderItems(TinyPotatoBlockEntity potato, Direction facing, String name, float partialTicks, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		ms.pushPose();
		ms.mulPose(VecHelper.rotateZ(180F));
		ms.translate(0F, -1F, 0F);
		float s = 1F / 3.5F;
		ms.scale(s, s, s);

		for (int i = 0; i < potato.inventorySize(); i++) {
			ItemStack stack = potato.getItemHandler().getItem(i);
			if (stack.isEmpty()) {
				continue;
			}

			ms.pushPose();
			Direction side = Direction.values()[i];
			if (side.getAxis() != Axis.Y) {
				float sideAngle = side.toYRot() - facing.toYRot();
				side = Direction.fromYRot(sideAngle);
			}

			boolean block = stack.getItem() instanceof BlockItem;
			boolean mySon = stack.getItem() instanceof TinyPotatoBlockItem;

			switch (side) {
				case UP -> {
					if (mySon) {
						ms.translate(0F, -0.375F, 0.5F);
					} else if (block) {
						ms.translate(0F, 0.3F, 0.5F);
					}
					ms.translate(0F, -0.5F, -0.4F);
				}
				case DOWN -> {
					ms.translate(0F, -2.3F, -0.88F);
					if (mySon) {
						ms.translate(0F, 1.25F, 0.5F);
					} else if (block) {
						ms.translate(0F, 1F, 0.6F);
					}
				}
				case NORTH -> {
					ms.translate(0F, -1.9F, 0.02F);
					if (mySon) {
						ms.translate(0F, 0.2F, 0.57F);
					} else if (block) {
						ms.translate(0F, 1F, 0.6F);
					}
				}
				case SOUTH -> {
					ms.translate(0F, -1.6F, -0.89F);
					if (mySon) {
						ms.translate(0F, -0.59F, 0.26F);
					} else if (block) {
						ms.translate(0F, 1F, 0.5F);
					}
				}
				case EAST -> {
					if (mySon) {
						ms.translate(-0.35F, -0.29F, -0.06F);
					} else if (block) {
						ms.translate(-0.4F, 0.8F, 0F);
					} else {
						ms.mulPose(VecHelper.rotateY(-90F));
					}
					ms.translate(-0.3F, -1.9F, 0.04F);
				}
				case WEST -> {
					if (mySon) {
						ms.translate(0.95F, -0.29F, 0.9F);
						if (stack.has(DataComponents.CUSTOM_NAME)) {
							var childNameBuilder = new StringBuilder();
							TinyPotatoBlockItem.isEnchantedName(stack.getHoverName(), childNameBuilder);
							if (childNameBuilder.toString().equals("kingdaddydmac")) {
								ms.translate(0.55F, 0, 0);
							}
						}
					} else if (block) {
						ms.translate(1F, 0.8F, 1F);
					} else {
						ms.mulPose(VecHelper.rotateY(-90F));
					}
					ms.translate(-0.3F, -1.9F, -0.92F);
				}
			}

			if (mySon) {
				ms.scale(1.1F, 1.1F, 1.1F);
			} else if (block) {
				ms.scale(0.5F, 0.5F, 0.5F);
			}
			if (block && side == Direction.NORTH) {
				ms.mulPose(VecHelper.rotateY(180F));
			}
			renderItem(ms, buffers, potato.getLevel(), light, overlay, stack);
			ms.popPose();
		}
		ms.popPose();

		ms.pushPose();
		if (!name.isEmpty()) {
			ContributorList.firstStart();

			float scale = 1F / 4F;
			ms.translate(0F, 1F, 0F);
			ms.scale(scale, scale, scale);
			switch (name) {
				case "phi", "vazkii" -> {
					ms.pushPose();
					ms.translate(-0.08, 0.1, 0.4);
					ms.mulPose(VecHelper.rotateY(90F));
					ms.mulPose(new Quaternionf().rotateAxis(VecHelper.toRadians(20), 1, 0, 1));
					renderModel(ms, buffers, light, overlay, MiscellaneousModels.INSTANCE.phiFlowerModel);
					ms.popPose();
					if (name.equals("vazkii")) {
						ms.scale(1.25F, 1.25F, 1.25F);
						ms.mulPose(VecHelper.rotateX(180F));
						ms.mulPose(VecHelper.rotateY(-90F));
						ms.translate(0.2, -1.25, -0.075);
						renderModel(ms, buffers, light, overlay, MiscellaneousModels.INSTANCE.nerfBatModel);
					}
				}
				case "haighyorkie" -> {
					ms.scale(1.25F, 1.25F, 1.25F);
					ms.mulPose(VecHelper.rotateZ(180F));
					ms.mulPose(VecHelper.rotateY(-90F));
					ms.translate(-0.5F, -1.2F, -0.075F);
					renderModel(ms, buffers, light, overlay, MiscellaneousModels.INSTANCE.goldfishModel);
				}
				case "martysgames", "marty" -> {
					ms.scale(0.7F, 0.7F, 0.7F);
					ms.mulPose(VecHelper.rotateZ(180F));
					ms.translate(-0.3F, -2.7F, -1.2F);
					ms.mulPose(VecHelper.rotateZ(15F));
					ItemStack stack = new ItemStack(BotaniaItems.infiniteFruit);
					stack.set(DataComponents.CUSTOM_NAME, Component.literal("das boot"));
					renderItem(ms, buffers, potato.getLevel(),
							light, overlay, stack);
				}
				case "jibril" -> {
					ms.scale(1.5F, 1.5F, 1.5F);
					ms.translate(0F, 0.8F, 0F);
					FlugelTiaraItem.ClientLogic.renderHalo(null, null, ms, buffers, partialTicks);
				}
				case "kingdaddydmac" -> {
					ms.scale(0.5F, 0.5F, 0.5F);
					ms.mulPose(VecHelper.rotateZ(180));
					ms.mulPose(VecHelper.rotateY(90));
					ms.pushPose();
					ms.translate(0F, -2.5F, 0.65F);
					ItemStack ring = new ItemStack(BotaniaItems.manaRing);
					renderItem(ms, buffers, potato.getLevel(), light, overlay, ring);
					ms.translate(0F, 0F, -4F);
					renderItem(ms, buffers, potato.getLevel(), light, overlay, ring);
					ms.popPose();
					ms.translate(1.5, -4, -2.5);
					renderBlock(ms, buffers, light, overlay, Blocks.CAKE);
				}
				default -> {
					ItemStack icon = ContributorList.getFlower(name);
					if (!icon.isEmpty()) {
						ms.mulPose(VecHelper.rotateX(180));
						ms.mulPose(VecHelper.rotateY(180));
						ms.translate(0, -0.78, -0.5);
						Minecraft.getInstance().getItemRenderer().renderStatic(icon, ItemDisplayContext.HEAD,
								light, overlay, ms, buffers, potato.getLevel(), 0);
					}
				}
			}
		}
		ms.popPose();
	}

	private void renderModel(PoseStack ms, MultiBufferSource buffers, int light, int overlay, BakedModel model) {
		renderModel(ms, buffers.getBuffer(Sheets.translucentCullBlockSheet()), light, overlay, model);
	}

	private void renderModel(PoseStack ms, VertexConsumer buffer, int light, int overlay, BakedModel model) {
		blockRenderDispatcher.getModelRenderer().renderModel(ms.last(), buffer, null, model, 1, 1, 1, light, overlay);
	}

	private void renderItem(PoseStack ms, MultiBufferSource buffers, @Nullable Level level, int light, int overlay, ItemStack stack) {
		Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.HEAD,
				light, overlay, ms, buffers, level, 0);
	}

	private void renderBlock(PoseStack ms, MultiBufferSource buffers, int light, int overlay, Block block) {
		blockRenderDispatcher.renderSingleBlock(block.defaultBlockState(), ms, buffers, light, overlay);
	}
}
