/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.render.block_entity;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

import vazkii.botania.api.internal.ManaBurst;
import vazkii.botania.client.core.handler.ClientTickHandler;

import java.util.function.Supplier;

public class BlockEntityItemRenderer {
	private final Block block;
	private final Supplier<BlockEntity> dummy;

	public BlockEntityItemRenderer(Block block) {
		Preconditions.checkArgument(block instanceof EntityBlock);
		this.block = block;
		this.dummy = Suppliers.memoize(() -> ((EntityBlock) block).newBlockEntity(ManaBurst.NO_SOURCE, block.defaultBlockState()));
	}

	public void render(ItemStack stack, ItemDisplayContext mode, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		if (stack.is(block.asItem())) {
			BlockEntityRenderer<?> r = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(dummy.get());
			if (r != null) {
				r.render(null, ClientTickHandler.partialTicks, ms, buffers, light, overlay);
			}
		}
	}
}
