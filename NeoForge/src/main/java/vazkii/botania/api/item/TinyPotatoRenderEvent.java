/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.api.item;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.Event;

/**
 * Why would you ever want this ._.
 */
public class TinyPotatoRenderEvent extends Event {
	public final BlockEntity tile;
	public final Component name;
	public final float partTicks;
	public final PoseStack ms;
	public final MultiBufferSource buffers;
	public final int light;
	public final int overlay;

	public TinyPotatoRenderEvent(BlockEntity tile, Component name, float partTicks, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		this.tile = tile;
		this.name = name;
		this.partTicks = partTicks;
		this.ms = ms;
		this.buffers = buffers;
		this.light = light;
		this.overlay = overlay;
	}
}
