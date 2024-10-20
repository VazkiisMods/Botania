/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.render.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.inventory.InventoryMenu;

import vazkii.botania.common.entity.CorporeaSparkEntity;

import java.util.Objects;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class CorporeaSparkRenderer extends BaseSparkRenderer<CorporeaSparkEntity> {
	private final TextureAtlasSprite corporeaWorldSprite;
	private final TextureAtlasSprite corporeaMasterWorldSprite;
	private final TextureAtlasSprite corporeaCreativeWorldSprite;

	public CorporeaSparkRenderer(EntityRendererProvider.Context ctx) {
		super(ctx);
		var atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
		this.corporeaWorldSprite = Objects.requireNonNull(atlas.apply(botaniaRL("item/corporea_spark")));
		this.corporeaMasterWorldSprite = Objects.requireNonNull(atlas.apply(botaniaRL("item/corporea_spark_master")));
		this.corporeaCreativeWorldSprite = Objects.requireNonNull(atlas.apply(botaniaRL("item/corporea_spark_creative")));
	}

	@Override
	public TextureAtlasSprite getBaseIcon(CorporeaSparkEntity entity) {
		if (entity.isCreative()) {
			return this.corporeaCreativeWorldSprite;
		} else if (entity.isMaster()) {
			return this.corporeaMasterWorldSprite;
		} else {
			return this.corporeaWorldSprite;
		}
	}

}
