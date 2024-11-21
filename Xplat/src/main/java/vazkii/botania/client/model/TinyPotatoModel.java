/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.model;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.client.render.block_entity.TinyPotatoBlockEntityRenderer;

public class TinyPotatoModel extends DelegatedModel {
	public TinyPotatoModel(BakedModel originalModel) {
		super(originalModel);
	}

	@Override
	public ItemOverrides getOverrides() {
		return new ItemOverrides() {
			@Override
			public BakedModel resolve(@NotNull BakedModel model, @NotNull ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity livingEntity, int seed) {
				if (stack.has(DataComponents.CUSTOM_NAME)) {
					return TinyPotatoBlockEntityRenderer.getModelFromDisplayName(stack.getHoverName());
				}
				return originalModel;
			}
		};
	}
}
