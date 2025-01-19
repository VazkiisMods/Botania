/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.record;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.JukeboxSong;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class BotaniaJukeboxSongs {
	public static final ResourceKey<JukeboxSong> GAIA_MUSIC_1 = ResourceKey.create(Registries.JUKEBOX_SONG, botaniaRL("gaia_1"));
	public static final ResourceKey<JukeboxSong> GAIA_MUSIC_2 = ResourceKey.create(Registries.JUKEBOX_SONG, botaniaRL("gaia_2"));
}
