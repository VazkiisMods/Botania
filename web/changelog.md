---
layout: "default"
title: "Changelog"
---
<div class='section-header'>
	<span class='glyphicon glyphicon-tag'></span>
	Botania Changelog
</div>

Note: A name in parentheses at the end of a line item means that it
was contributed externally by that member of the community.

<!--
Before each release, rename the version below to the released version,
and start a new "Upcoming" section.
-->

{% include changelog_header.html version="Upcoming" %}

* Add: Looniums can now give different loot and mob spawns based on which
structure it's placed in, also further customizable with datapacks. Huuuuge
thanks to Wormbo for a lot of work on this feature!
* Add: Dispenser planting supports torchflower seeds and pitcher pods
* Change: More readable color in Loonium tooltip
* Change: Pasture seed spread is no longer restricted to a single Y-level, and replacing existing dirt-like blocks is handled by block tag `botania:pasture_seed_replaceable`
* Change: Hopperhocks now also apply delay from being planted on podzol or mycelium to picking up recipe outputs, such as from mana infusion
* Change: Blocks that are not covered by the default unethical TNT check, but can produce TNT the Entropinnyum recognizes, can be specified in the new `botania:unethical_tnt_check` block tag to detect when TNT is duplicated from them (contains `ae2:tiny_tnt` by default)
* Fix: Akashic Tome name is rendered correctly again on the Lexica Botania
* Fix: Slightly improved tick performance impact of mana/corporea sparks
* Fix: Halloween Easter egg on Tiny Potato also works in item form
* Fix: Dandelifeon no longer sometimes consumes blocks that aren't cellular blocks
* Fix: Protection effect of the Benevolent Goddess Charm also works on Fabric when the player dies from the explosion
* Fix: Redstone comparators no longer read signal strength 0 from special flowers other than the Thermalily
* Fix: A comparator reading from a Thermalily through a solid block no longer keeps outputting a value after breaking the Thermalily
* Language update:
  * de_de completed (thanks to Kartabass and PssbleTrngle for reviewing)
  * ko_kr updated (UnineVesiKass)
  * ru_ru updated (krumplerban)
  * zh_cn updated (Dawnwalker666)

---

{% include changelog_header.html version="1.19.2-440.2" %}

* Add: Holding a Floral Obedience Stick while placing a special flower prevents it from automatically binding to a spreader or pool, and using the stick on a special flower unbinds it from its spreader or pool
* Change: Cellular blocks around the boundary of a Dandelifeon's simulation area are ignored, unless they belong to another active Dandelifeon (NEstoll)
* Change: Filling or emptying a Petal Apothecary in creative mode on Forge no longer changes the bucket item in hand (vanilla/Fabric parity)
* Change: Terrasteel tool speed and damage was updated to match corresponding netherite values
* Fix: JEI memory leak
* Fix: Rosa arcana not taking into account stacked xp orbs
* Fix: Crash with Akashic Tome
* Fix: Rafflowsia occasionally losing its binding
* Fix: Off-by-one-degree typo when rendering progress pies in the HUD

---

{% include changelog_header.html version="1.19.2-440.1" %}

*This version was never released due to an issue with the build. See 1.19.2-440.2.*

---

{% include changelog_header.html version="1.20.1-446" %}

* Add: Various block/item interactions can be detected with sculk sensors
* Add: Drum of the Gathering shortens the time until chickens lay their next egg
* Change: Filling or emptying a Petal Apothecary in creative mode on Forge no longer changes the bucket item in hand (vanilla/Fabric parity)
* Change: Various lexicon adjustments
  * Mentioned dripleaf shrinking
  * Changed various mentions of "Manaweave armor" to "Manaweave robes"
  * Information on how to make the Teru Teru Bozu sad (so you can avoid it, you wouldn't want it to be sad, right?)
* Fix: Certain structures never generated in GoG, while others were placed at or below the bottom of the world (GoG might not place any blocks, but does create structure bounding boxes)
* Fix: Mixin-related crash on startup with BedrockIfy or Enhanced Celestials
* Fix: Improved the Spectator's performance impact in unusually restrictive chunk-loading conditions, but also somewhat improved its overall scanning performance
* Fix: Rafflowsia no longer forgets its binding (among other things) after unload due to deserialization error
* Fix: Alfheim portal effect planes were swapped
* Fix: When a pixie applies the wither effect, it will actually cause damage now
* Fix: Force relays getting moved directly, e.g. via force lens burst or direct binding from another force relay, correctly move their target and update the binding accordingly
* Language update:
  * ru_ru updated (krumplerban)

---

{% include changelog_header.html version="1.20.1-445" %}

* Add: Horn and Drum of the Wild can break moss carpet, with the option to add more blocks through a block tag
* Add: Bergamute occludes vibrations within its range
* Add: Holding a Floral Obedience Stick while placing a special flower prevents it from automatically binding to a spreader or pool, and using the stick on a special flower unbinds it from its spreader or pool
* Add: Big dripleaf plants can be shrunk back to their small form using mana infusion with an alchemy catalyst
* Add: Tectonic Girdle also nullifies explosion knockback
* Add: Block tag support for Agricarnation, allowing data/mod pack authors to customize which plants can/cannot receive a growth boost
  * Block tag `#botania:agricarnation/growth_excluded` never receives growth boosts, even if the block is in any of the other tags as well; contains mangrove leaves, and red and brown vanilla mushrooms by default
  * Block tag `#botania:agricarnation/growth_candiate` designates a block as a candidate for growth boosting, but only works if the block is technically able to receive growth boosts; grass/mycelium/nylium blocks are excluded by default, unless they are tagged as growth candidates, because they would otherwise fit the default criteria for being selected
  * Block tag `#botania:agricarnation/apply_bonemeal` defines that the block should be boosted through its bone mealing logic instead of random ticks; contains azalea bushes by default, and is included in the growth candidates
    * Bone meal boosting has a built-in 50% success chance, which is multiplicative with the block's own bone meal success test
  * To receive growth boosts, a block must not have been excluded, and it must be both a "target candidate" and "accept growth boosts"
    * A block is a "target candidate" if it could accept bone meal, its block type extends `BushBlock`, or is tagged as growth candidate (this does not auto-include sugarcane or cactus, but does cover nether wart by default)
    * A block "accepts growth boosts" if it can receive random ticks, or if it could accept bone meal and is tagged for applying bone meal (this does not auto-include nether fungi, short grass/fern, small mystical flowers, or buried petals by default)
  * When finding a `GrowingPlantBodyBlock` (kelp, glowberry vines, nether vines), the corresponding head block is targeted instead
* Add: Pollidisiac has a new mode switch to feed baby animals
* Change: Cellular blocks around the boundary of a Dandelifeon's simulation area are ignored, unless they belong to another active Dandelifeon (NEstoll)
* Change: Charm of the Diva also supports charming or targeting neutral mobs that are angry at the player or attacking one of the player's tamed animals, and properly prevents the player's tamed animals from being affected or targeted by the charm
* Change: Dandelifeon recipe also requires a redstone root (zacharybarbanell)
* Change: Terrasteel tool speed and damage was updated to match corresponding netherite values
* Change: Bumped up the amount of mana contained in bursts from Manastorm Charges; this doesn't change their behavior, but maybe you can find a new use for them in less destructive ways
* Change: Various crafting recipes moved to more fitting categories in the vanilla recipe book
* Change: Several book entries were updated, including various recipe presentations:
  * Petal Apothecary and Runic Altar recipes show the reagent, similar to how they do in recipe viewer mods already
  * Multi-crafting page templates are now used more widely where it makes sense, like when alternative items in recipes need to be implemented as individual recipes (e.g. the mana pearl and mana diamond variants of the runic altar and mana tablet recipes)
  * Missing lookup references for derivatives of certain decorative blocks (e.g. quartz variants) have been added for the quick-lookup feature while holding the book
  * Lexica Botania acknowledges that Trinkets are used on Fabric instead of Curios
  * Various entries were updated to match recent changes and fix old errors
* Change: The Ring of Loki now has a (reasonably high) selection limit to prevent players from accidentally "NBT-banning" themselves from their world or server by going too hard with it
* Change: Blackstone is considered "semi-disposable" for Elementium pickaxe
* Change: Botania advancements of the "challenge" type (not the category tab, but the icon shape) grant an experience reward
* Remove: Config option `orechidPriorityMods` didn't have any effect since 1.16, never made it to Fabric, and is now gone in the Forge version as well; pack authors should use data pack recipes to define Orechid outputs and weights
* Fix: Flight bar for Flügel Tiara no longer overlaps with the refilling air bubbles indicator or the mount health bar, if that uses more than one row
* Fix: The Manaseer Monocle's flower radius indicator no longer jumps around if you are very far from the world origin, and should also not Z-fight with the binding radius indicator of luminizers anymore
* Fix: The air bubble created by Bubbells no longer flickers with inflowing water
* Fix: Charm of the Diva works on Forge again
* Fix: Translation key names for potted glimmering flowers were wrong in the English language file
* Fix: Corporea Index on Fabric no longer ignores chat text sources other than the vanilla chat console, fixing compatibility with e.g. the "Microphone Text Input" mod
* Fix: Pollidisiac gives animals their deserved break after breading again
* Fix: Shard of Laputa no longer breaks *all* the 2-tall grass/flower blocks (only some of them)
* Fix: Floating flower islands properly show the soil type on Forge, and are no longer invisible in Patchouli multiblock visualizations (although that latter part is merely a workaround for a Patchouli issue at the moment)
* Fix: Position of the binding information icon on flowers' wand HUD has been adjusted to not overlap with longer flower names
* Fix: Suspicious stew effect from Pure Daisy actually works now, clearing all active status effects
* Fix: Potential integer overflow in Ring of Loki mana calculation
* Fix: Removed outdated Malum compatibility code from Ring of Magnetization, which should fix a crash (unilock)
* Internal: Loader-agnostic ("Xplat") code is now built with [minivan](https://github.com/CrackedPolishedBlackstoneBricksMC/minivan) instead of VanillaGradle (quat)
* Language updates:
  * en_gb added (Adarsh)
  * zh_cn updated (Dawnwalker666)

---

{% include changelog_header.html version="1.20.1-444" %}

* Add: Stonecutting and crafting recipe for livingrock slate (it was previously uncraftable, oops)
* Add: Motif flowers can be used to make suspicious stew, and are generally recognized as "small flowers"
* Add: The Spectator also scans the inventories of donkeys, mules, llamas, and allays
* Add: Native EMI support on Forge
* Add: Missing slab recombination recipes for quartz and corporea variants
* Add: The Lexica Botania can be put into chiseled bookshelves and onto lecterns (Note: reading it from a lectern does not currently grant the advancement)
* Add: Corporea request key now also works in the EMI item list (it still doesn't work in recipe displays due to technical limitations of the EMI API)
* Add: All Botania flowers and mushrooms can be put into flower pots
* Change: Elementium Axe is now treated as a weapon regarding durability damage from attacking things
* Change: Various Botania blocks finally make noteblocks use the instrument you would likely expect
  * blocks made from Livingwood or Dreamwood (except drums) -> bass
    * This also means Livingwood and Dreamwood should now be recognized by Every Compat
  * Stone-like blocks (biome stones, livingrock, corporea blocks, azulejos) and drums -> basedrum
  * Manasteel, Elementium, or Terrasteel blocks -> vibraphone ("iron xylophone")
  * Teru Teru Bozu -> guitar
  * Blaze Mesh -> pling
* Change: Some crafting recipes have been changed to better match similar recipes from other mods and VanillaTweaks
  * Chiseled livingrock bricks and chiseled metamorphic stone bricks are crafted from the corresponding brick slabs arranged vertically
  * Slab recombination recipes have been changed to arrange the slabs horizontally in the crafting grid
  * Unlock conditions for slab recombination recipes now require the slab, not the full block
* Change: Recipe displays for Petal Apothecary and Runic Altar show required reagents (e.g. seeds, livingrock)
* Change: Recipe display for Terrestrial Agglomeration Plate in EMI looks more like it does on JEI or REI now
* Change: Phantom ink is now used to hide abstruse and spectral platforms instead of managlass. (zacharybarbanell)
* Change: Ring of Correction no longer attempts to correct tool right-click usage
* Fix: Corporea Crystal Cube and Abstruse/Spectral Platform no longer forget their internal state after unloading
* Fix: Quick-moving (shift+click) Botania pattern items in the loom UI properly puts them in the pattern slot
* Fix: Animated Torch powers blocks consistently, and properly updates necessary blocks when placed, rotated, or removed
* Fix: Elven Spreader no longer misses mana pool
* Fix: Flügel Tiara detects Eye of the Flügel for infinite flight again
* Fix: Using a sapling on the Mana Flame block (GoG) only produces a new lexicon if the player doesn't have one already
* Fix: Item icon offset in Corporea Crystal Cube's HUD overlay
* Fix: Left forearm guard of the elementium chestplate is rendered properly (Partonetrain)
* Fix: Drinking mana from a bottle will no longer delete a block or grant an extra swig in certain conditions
* Fix: The Spectator no longer attempts to scan unopened loot containers, and its performance has been improved
* Fix: Fake player detection for the gaia fight has been improved, so you no longer get extra loot for e.g. a nearby running RFTools builder
* Fix: The Cirrus and Nimbus amulets got math lessons and should be able to properly count the number of mid-air jumps now
* Fix: Botania advancement tab and root advancement title
* Fix: Warp+Bore bursts can now teleport via a force relay without breaking it
* Fix: Other mods should have fewer issues with broken or doubled loot table evaluation in GoG
* Fix: Disguising Abstruse or Spectral Platforms works again on Fabric, and some rendering issues with them on Forge were fixed as well
* Fix: Ring of the Mantle no longer suppresses stronger sources of Haste, or any other Haste sources if the player has no mana
* Fix: Players or Tiny Potatoes holding (other) Tiny Potatoes uses proper render offsets again
* Fix: Upgrading Manasteel armor to Terrasteel no longer removes all non-enchantment NBT data (e.g. custom names)
* Internal: Updated Fabric minimum dependencies to loader 0.15.6 and API 0.92
* Language updates:
  * ko_kr completed (Tareun3406)
  * uk_ua added (fasero1)
  * zh_cn updated (Dawnwalker666)

---

{% include changelog_header.html version="1.20.1-443" %}

* Change: Manaweave boots and Snowflake pendant now let you walk on powder snow (Wormbo)
* Fix: Crash related to PistonStructureResolverMixin on Forge (Wormbo)
* Fix: Cellular blocks can no longer be placed with age. Prevents a way of cheesing the dandelifeon using create (Wormbo)

---

{% include changelog_header.html version="1.20.1-442" %}

* Relaxed forge version requirement to 47.1.3
  * This is for compatibility with many other mods who have this as their highest supported version
* Add: Vitreous pickaxe can now silk touch glass panes (Wormbo)
* Add: Mystical flowers can now be composted (Wormbo)
* Add: Improved rotation support for various blocks. This makes the following blocks able to be rotated by wand of the forest better, as well as by other mods' wands (Wormbo):
  * Manatide Bellows
  * Fel Pumpkin
  * Incense Plate
  * Mana Pump
  * Tiny Potato
* Add: Bowl of water now has a crafting remainder of empty bowl
* Add: Pollidisiac can feed flowers to brown mooshrooms so they produce suspicious stew (Wormbo)
* Add: Soul soil can be made from soul sand with an alchemy catalyst
* Change: Updates to Chinese translation (Dawnwalker666)
* Change: Reduced narslimmus mana output to 25% in Garden of Glass (Wormbo)
* Change: Rewrote how wand of the forest rotates blocks, more details in [this commit](https://github.com/VazkiiMods/Botania/commit/3dc86fe79ea4f67325a50b6da65337a3735d08b4) (Wormbo)
* Change: Runic altar and mana pool-spawned items no longer have a cooldown before they can enter the pool/altar again, and instead do not re-enter the altar or pool until picked up.
* Change: Hopperhocks' 5 tick pickup delay now applies to items spawned from alfheim portals and petal apothecaries, not just runic altars and mana pools.
* Change: The mana pool recipe HUD is now above the crosshair if holding a wand of the forest in your off-hand, to not collide with the wand HUD (Wormbo)
* Change: Garden of Glass pebbles now drop 100% of the time, instead of 80%
* Change: Living root now drops 4 instead of 2-4
* Change: Fel blazes now always drop 10 blaze powder when killed by a player (up to 16 with looting), and 6 when not
* Fix: Change gold to copper in the elven mana description (Elitemagikarp)
* Fix: Force relay fixes (Wormbo):
  * Fix the force relay pushing the wrong way if it is part of a retracting sticky piston's block structure
  * Don't assume that the location where the force push seemingly originates from contains an unmovable block
* Fix: Corporea crystal cube crash (Wormbo)
* Fix: Bad link to orechid entry in Garden of Glass entry

---

{% include changelog_header.html version="1.20.1-441" %}

* Port to 1.20.1 (team, assisted by extclp and Abbie5)
  * Note: In this release, we are still targeting legacy MinecraftForge ("LexForge") in 1.20.1. NeoForge has been briefly tested to work but support will not be prioritized for it yet.
  * In 1.20.2+, we will be switching to target NeoForge, the continuation of the Forge project.
* Add: Manufactory Halo shows active status in its icon (Wormbo)
* Add: Tuff now convertable by Marimorphosis
* Add: Cherry to log and sapling alchemy cycles
* Add: Missing leaf mana duplication recipes
* Add: Manaweave armor is now in the `freeze_immune_wearables` tag
* Add: Mana alchemy cycle for froglights
* Add: Ring of Odin protects against freeze damage
* Add: Livingrock slate, a new decorative block
* Add: New textures for catalysts
* Add: Piglin Head is now dropped from the Elementium Axe
* Add: Camel is now part of the Cocoon of Caprice's rare pool
* Add: Potion effect descriptions for EMIEffect and Just Enough Effect Descriptions (Adarsh)
* Change: Key of the King's Law no longer hurts items or most other nonliving entities (zacharybarbanell)
* Change: The internal mana buffer of the Gourmaryllis is now large enough to benefit from streak bonus even with very nutritious food like Rabbit Stew or high quality modded foods (it still caps the food value at 12 points or 6 haunches)
* Change: Removed desu gun and its associated advancement. The reference has overstayed its welcome.
* Change: Drum of the Gathering now uses entity tags to determine mobs that can be milked (`botania:drum/milkable`, which now also includes goats by default) and which mobs NOT to shear even though that's technically possible (`botania:drum/no_shearing`, by default that only includes mooshrooms); it can also produce suspicious stew from brown mooshrooms, and no longer finds a way to somehow milk baby cows (Wormbo)
* Change: Mirror axe/shear recipes so they match vanilla (Adarsh)
* Change: Fallen Kanade now works in the End dimension
* Change: Base mana spreader is now crafted with copper
* Change: Mana String is now approximately 5x cheaper to infuse (Adarsh)
* Change: Small buff to Manaweave chestplate's protection value (Adarsh)
* Change: Small buff to Manaweave helmet's mana discount (Adarsh)
* Change: Slight buffs to the Thermalily (Adarsh)
* Change: Adjust Trinket Box and Incense Plate recipes (Adarsh)
* Change: Improve the performance of Mana Sparks (anonymous123-code)
* Change: Improve the performance of Mana Enchanter (Wormbo)
* Change: Improve the performance of Alfheim Portal (Wormbo)
* Change: Corporea Index response message no longer has quotation marks around ItemStack-based requests
* Fix: Ender Air emission not being mentioned in Pure Daisy entry (Wormbo)
* Fix: NPE in Rod of the Unstable Reservoir (sandtechnology)
* Fix: JEI integration leaking memory
* Fix: Rosa Arcana not accounting stacked experience orbs
* Fix: Solid Vines not being solid
* Fix: Off-by-one-degree typo when rendering progress pies in the HUD
* Fix: Botania gates now yield 1 like vanilla (Adarsh)
* Fix: Terra Blade firing in spectator mode
* Fix: CME crashes with certain Fabric mods (e.g. Extended Drawers) when Corporea is used on their inventories
* Fix: Crystal Bow not letting you pick up special arrows that were consumed
* Lexicon updates:
  * Mention the stack size limit of the Ring of Loki (Wormbo)
  * Alchemy Catalyst entry tweaks (Adarsh)
  * Mention semi-disposability concept in Elementium Pick entry (Adarsh)
  * Document new Bergamute behaviour (Adarsh)
  * Document that grass seeds can be used in a Dispenser (Adarsh)
  * Reworded Hovering Hourglass sand docs
  * Document Sextant fractional radius and sphere modes (Wormbo)
* Language updates:
  * fr_fr updated (Aegide)
  * zh_cn updated (Dawnwalker666)

---

{% include changelog_header.html version="1.19.2-440" %}

* Add: The Worldshaper's Sextant can now generate spheres as well (Wormbo)
* Add: Grass Seeds can now be placed by dispensers.
* Add: Manufactory Halo's auto-crafting can be toggled by shift+right-clicking the
  crafting table segment or right-clicking the item in an inventory screen
* Add: Mana lenses can now be merged using either honey bottles or slime balls
* Add: Conversion chances for Orechid, Orechid Ignem, and Marimorphosis (including biome-specific chances for player's location) are now displayed in the recipe listings of JEI, REI, and EMI (Wormbo)
* Add: Reintroduce the useShaders config option to disable Botania's special shaders
* Add: zh_cn updates (Dawnwalker666)
* Change: The Worldshaper's Sextant provides more control over the exact shape of circles
  by having the radius selection snap to the block grind instead of only allowing integers
  (Wormbo)
* Change: The Rosa Arcana's XP orb mana yield now matches the player-based mana yield
* Change: The Worldshaper's Sextant provides more control over the exact shape of circles by having the radius selection snap to the block grind instead of only allowing integers (Wormbo)
* Change: Worldshaper's Astrolabe now places blocks more like a player would, e.g. waterlogging blocks where appropriate, adding more candles to existing ones, or rotating directional blocks according to the player's view direction, targeted block side, or available attachment options
* Fix: Made Teru Teru Bozu truely happy during clear weather again (Wormbo)
* Fix: Leaves placed by Worldshaper's Astrolabe no longer decay
* Fix: Some items being missing from Forge equipment tags and Fabric bow tag
* Fix: Using an empty bottle in the end will not attempt to pick up Ender Air if the player is aiming at a liquid (Wormbo)
* Fix: Horn of the Canopy breaking persistent leaves
* Fix: Thermalily not updating comparators properly
* Fix: Kindle Lens not creating Soul Fire when it should
* Fix: Fake bursts triggering game events
* Fix: Biomes not populating properly in Garden of Glass
* Fix: Some structures showing up in Garden of Glass
  * Note: These two fixes do not require a GoG jar update, but will only cover
	newly-generated terrain.


---

{% include changelog_header.html version="1.19.2-439" %}

* Add: Mana pools with pylons on top of them will now play a small particle effect when trading items in the alfheim portal
* Add: Looking at a Spark while holding a Wand of the Forest will now render a HUD showing information like augments (for mana sparks) and network color (both mana and corporea sparks)
* Add: Terra Truncator can now propagate its chopping through Mangrove Roots
* Add: Trinket case uses special open icon while its GUI is open 
* Add: All wand HUDs now have backgrounds for better readability, along with some other changes on some HUDs. Their design is not finalized, let us know on discord/reddit/irc/email if you have suggestions for how they can be better!
* Add: Bound flowers now show the type of spreader/pool they're bound to in the tooltip, instead of a generic spreader/pool
* Add: Rannuncarpus can now add blocks onto existing blocks like candles and sea pickles, which accept multiple placements of itself in the same block space
* Add: The game now warns you if you hold a wand of the forest with Optifine installed, as it breaks the entire mod. We recommend Sodium instead if you need a performance boost
* Change: Instead of always cooling down for 5 minutes, thermalilies now have a random cooldown between 20 seconds and 5 minutes. For automation, you can read how long the cooldown is with a comparator
* Change: Entropinnyum's "unethical TNT" detection was changed, it's now more accurate and shouldn't cause any false positives
* Change: Flower pouches now render missing flowers transparently, and render a "1" next to stacks with one item
* Change: Rod of the Shifting Crust now delays processing neighbor updates until the block is replaced, meaning torches or other blocks attached to exchanged blocks will no longer break as long as the new block can still support it
* Change: Runic altar now doesn't immideatly pick up its own crafting outputs, and lets hopperhocks pick them up quicker
* Change: Terra truncator now propagates through Mangrove Roots
* Change: Sort recipe categories in EMI
* Change: The trinket case now shows an opened texture when opened
* Change: Force relay/lens pushing now reuses vanilla piston pushing logic instead of copying it. Should lead to better compatibility with mods that let you move block entities
* Fix: Mana spreaders now update their mana levels in real time, like pools do
* Fix: Red stringed containers now always report and accessible inventory, so that corporea sparks will not pop off them
* Fix: The fallback vanilla item transfer implementation in corporea now correctly updates inventories after taking items (this bug was not directly accessible, since neither fabric nor forge use this fallback implementation)
* Fix: Metamorphic stone chiseled brick recipe is now consistent with chiseled livingrock recipe
* Fix: Sky of Beginning being granted only when *both* a Mystical Flower and Tall Mystical Flower were picked up, instead of either one
* Fix: The JEI category for mana infusion no longer shows the creative mana pool, to be consistent with EMI and REI
* Fix: Livingrock and deepslate apothecaries now show as workstations in the apothecary recipe category in EMI/REI/JEI
* Fix: Corporea crystal cube now shortens long numbers correctly.
* Fix: Hopperhocks and corporea funnels now interact correctly with composters on forge
* Fix: Paintslinger lens now works with candle and candle cake blocks
* Fix: Paintslinger lens will now transform all adjacent blocks of the same block, not just those with the exact same blockstate
* Fix: Paintslinger lens no longer resets blockstate of colored blocks
* Internal: Changes to BlockstateProvider, see [these commits](https://github.com/VazkiiMods/Botania/compare/d1c3b3ae4f231d5e4e20677fac043bb315c4ef7e..a149645f8a3fc1d876600995eb7285654d358adc) for more details
* Internal: Allow the flower model for floating flowers to use forges model extensions. See [this commit](https://github.com/VazkiiMods/Botania/commit/e3588c21f2540058931f81322ca336b2535f1100) for more details

---

{% include changelog_header.html version="1.19.2-438" %}

* Change: Tweaks to the Dandelifeon's rules; if you space them well, you can make very large setups :)
* Change: The Corporea Spark recipe makes 4 now. Good riddance.
* Fix: zh_CN updates (Clayblockunova)
* Fix: Banner patterns being broken on Forge
* Fix: Missing Sunny Quartz destruction recipe
* Fix: Incenses not rendering properly on Forge

---

{% include changelog_header.html version="1.19.2-437" %}

* Add: Polished Livingrock recipe for the Stonecutter
* Add: Support the Fabric Transfer API in Corporea
* Add: Livingrock and Deepslate Apothecaries
* Change: Livingwood and Dreamwood Twigs are now crafted diagonally rather than vertically, to fix a conflict with a common utility recipe.
* Change: Re-drew textures for Lunite, Gneiss, and Talc from the bottom to be less messy and have more vibrant palettes.
* Change: Some palettes to use the colors of the new Mystical Petals
* Change: fr_fr updates (Aegide)
* Change: Allow Tall Mystical Flowers to count for Sky of Beginning
* Change: Petal apothecary and mana pool hitboxes now work like the vanilla composter; if you click anywhere in the "hole", a block will be placed above it instead of adjacent to the face you were targeting
* Fix: Pylons now work as enchantment boosters on Quilt with QSL
* Fix: Lexicon referring to Stone as its old colloquial name Smooth Stone
* Fix: Simulated mana bursts living longer than they should, causing extreme client lag
* Fix: Botania Grasses not playing a sound or emitting Game Events when being tilled
* Fix: Botania Grasses not being flattenable into Dirt Paths

--- 

{% include changelog_header.html version="1.19.2-436" %}

* Info: Ported to Minecraft 1.19.2
  * We will reassess in a few weeks what the situation is with regards to 1.19.3.
* Add: NEW TEXTURES! Most all of the magical flowers and livingrock-based blocks have been retextured by ArtemisSystem and Falkory220, and several new decor variants have been added
* Add: Document the 1.17 stone infusion cycle
* Add: Enabled JEI integration on Fabric as well (shartte)
* Add: Mangrove propagules to sapling infusion ring
* Add: Frog to aquatic pool of Cocoon of Caprice
* Add: Wand of the Forest can now toggle PipeBlock-like faces, such as those of the Huge Mushroom
* Add: Hovering Hourglass HUD now shows the exact current time within the cycle
* Add: Tectonic Girdle now prevents Mana Blaster's recoil (LeoBeliik)
* Add: Petal Apothecary recipes' reagent item (seeds) can now be customized in JSON. This has implications on how the Apothecary matches items, see commit [3149ffd02cce6e972fbe04ef949ebb2790924dc1](https://github.com/VazkiiMods/Botania/commit/3149ffd02cce6e972fbe04ef949ebb2790924dc1) for more information.
* Add: When rotating or modifying a block with the Wand of the Forest, play a sound
* Add: Cobbled Deepslate, End Stone, Red Sand to Disposable blocks
* Add: Tuff, Calcite, Stone, Basalt, Deepslate, Dripstone, Pointed Dripstone, Moss, Sandstone, and Red Sandstone to semi-disposable blocks
* Add: Orechid, Orechid Ignem, and Marimorphosis recipes may now specify a function that is run on success
* Add: Stone of Temperance can now be right-clicked in the inventory to toggle it on and off (LeoBeliik)
* Add: Orechid, Orechid Ignem, and Marimorphosis recipes may now specify tags and blockstate properties for their inputs
* Add: Incense Plate now plays a sound when ignited
* Change: Marimorphosis now uses biome tags for its bonus boost
* Change: Move world generation to JSON
* Change: Some Introduction section entries are now marked priority to emphasize their importance (Aegide)
* Change: ja_JP updates (RakuGaki-MC)
* Change: Munchdew prioritizes eating further away and disconnected leaves first (Wormbo)
* Change: Horn of Canopy and Covering now use tags to decide what to break
* Change: Endoflame no longer requires a valid spreader binding to start burning fuel
* Change: Forge builds now include the suffix `-FORGE` like the Fabric ones
* Change: Pure Daisy no longer runs if the input of a recipe is the same as the output
* Change: Uncap the Spectranthemum distance limit (but the mana cost scales with distance)
* Change: Unstreamify parts of the code for better performance
* Change: After placing a torch with the Manasteel Pickaxe, there is a 5-tick cooldown in which another cannot be placed (LeoBeliik)
* Change: Spoofed Hopperhocks, Labellias, Pollidisiacs, and Rannuncarpuses now take input at their true position instead of their spoofed position
* Change: Mana pool's dye color is expressed in the blockstate, allowing models to be different for each. Separate the "white" and "undyed" cases.
* Change: Mana pool's dyeing now uses mana petals for dyeing, and a clay ball to remove the color
* Change: Marimorphosis now takes 1.8 stones and deepslate as inputs as well
* Change: Missiles from the Rod of Unstable Reservoir no longer try to target entities that are invulnerable to the missiles
* Change: Updated the sounds of Buried Petals and Petal Blocks
* Change: Updated lexicon picture for Mystical Flowers
* Change: Terra Shatterer only toggles its active state when sneaking (LeoBeliik)
* Fix: Many, many lexicon fixes (Aegide)
* Fix: Soul Cross not working on Forge
* Fix: Wrong REI entrypoint being declared on Fabric (YerinAlexey)
* Fix: Bound block highlight no longer renders every individual part of the VoxelShape
* Fix: Loonium spawned mobs not being at max health
* Fix: Delete orphaned and duplicate lang entries (Quezler)
* Fix: Configs behaving strangely on Forge due to not checking if events were for Botania
* Fix: Revert making Petal Apothecary a LiquidBlockContainer from 1.18.2-435, it interacted poorly in-world
* Fix: Exclude any items with NBT from the Black Hole Talisman, to prevent data loss
* Fix: Thundercaller lightning effects not showing up
* Fix: Desyncs in Dandelifeon, Hydroangeas, Thermalily
* Fix: Spell Cloth being repairable
* Fix: Red String Spoofer binding to huge mushrooms blocks instead of small mushrooms
* Fix: A bunch of dynamic recipes not matching as strictly as they should, leading to item loss
* Fix: Hovering Hourglass HUD for Mana Powder being nonsensical
* Fix: Red String Spoofer not binding to nether mushrooms
* Fix: Corporea Crystal Cube "Locked" HUD overflowing the background
* Fix: Rod of the Seas not making a sound when filling Petal Apothecary (LeoBeliik)
* Fix: Item inside Corporea Crystal Cube render going outside the cube (LeoBeliik)
* Fix: Bursts becoming visible after warping in some cases (LeoBeliik)
* Fix: Livingrock item entity not resyncing when Runic Altar completes
* Fix: Crafty Crate deleting items if the recipe returns air (KJP12)
* Fix: Black Hole Talisman extraction recipe matching empty talismans (KJP12)
* Fix: Withdrawing fluids from Petal Apothecary with stacked buckets not working on Forge
* Fix: Z-Fighting on Mana Prism with some lenses
* Fix: Z-Fighting on Tripwire Lens
* Fix: Mana Enchanter's rune effect animating choppily

---

{% include changelog_header.html version="1.18.2-435" %}

* Add: Support for Ears mod (TJT01):
  * If phantom-inked armor is worn; Ears will act as if there isn't anything in that slot
    (feet claws will show with invisible boots, for example).
  * If a Flügel tiara with wings is worn, Ears' wings will be hidden.
* Add: Bottled Mana can now be eaten by foxes
* Add: Terra Pick can now AOE mine blocks meant for the Hoe
* Add: Petal Apothecaries now make sounds when being filled or emptied
* Fix: Documentation being inconsistent about the operation ordering of retainers and
  interceptors. Documentation now matches the existing behaviour that later incoming
  requests overwrite the currently held one (artemisSystem)
* Fix: Blacklist flower pouch from cpw InventorySorter, which deletes everything when
  trying to sort
* Fix: Multiple lexicon fixes (Aegide):
  * Fix Botania not being highlighted with $(thing) macro in some places
  * Fix "Elves", "Horse", "Mana", and "Rain" being highlighted with
    $(item) macro in some places
  * Ensure the "Introduction" entry is at the top of its category
* Fix: Mana Blaster model using excessive amounts of memory
* Fix: Hopperhocks not using a flat 5-tick timer for newly infused items
* Fix: Some cases of Heisei Dream not working on mobs. There are other known cases left.
* Fix: Botania inventories spilling their contents when being replaced by /fill and other
  commands that should overwrite without drops
* Fix: Rod of the Highlands not counting as a source of dirt
* Fix: Rod of the Hells' AOE being completely broken (it used XY plane instead of XZ)
* Fix: Mana-level filtering not working in Hopperhocks
* Fix: Potential crashes on Forge when Forge light pipeline is enabled
* Fix: Solid Vines not using the new vanilla vine sounds
* Fix: Item entities not visually updating when they are manipulated by Botania
  contraptions
* Fix: Vine ball lexicon entry claiming vanilla vines cannot be climbed without support
  when they can be (artemisSystem)
* Fix: Mossy Apothecaries dropping the default Petal Apothecary instead of themselves
* Change: Update ja_jp translations (RakuGaki-MC)
* Change: The selection of wand colors in the creative menu is now
  randomized (artemisSystem)
* Change: Increased the size of the Eerie Mask cosmetic render
* Change: Improved the right click behaviour of multiple blocks (artemisSystem):
  * Spreaders and prisms now require an empty hand to remove lenses/wool.
  * Right clicking a spreader/prism with an identical lens/wool removes it
  * Lenses and wool will now be swapped with the one in the player's hand if the
    spreader/prism has a different one
  * Added a sound for applying and removing a lens from a mana prism
  * Removing an item from an apothecary/altar/brewery now only requires your main hand
   	to be empty
  * Right clicking to repeat a recipe in the altar or apothecary now only requires the
    main hand to be empty
* Change: Players that have the "Blessing" advancement can now see the event repeatedly
* Change: Bore-warp lenses now spawn the drops behind the spreader's facing, instead of
  inside the spreader (artemisSystem)
* Change: Retexture the mystical flowers (falkory220)
* Change: Petal Apothecary now only renders center 10x10 square of the liquid texture,
  instead of rendering the entire 16x16 texture and squashing it into a 10x10 space
* Change: Vine ball tweaks (artemisSystem)
  * If a vine ball entity can't place any vines where it hits, it drops itself
  * Vine balls now only place up to a maximum of 9 vines per hit
  * Hitting a Solid Vine with a vine ball will extend the current chain of vines downward
    (by up to 9 blocks)
* Change: Metamorphic stones overhaul (artemisSystem):
  * Retexture and remodel all existing blocks
  * Adjust the Petal Apothecary's hitbox to fit its new model
  * Rename all the families:
    Forest -> Fuchsite, Plains -> Talc, Mountain -> Gneiss, Fungal -> Mycelite,
	Swamp -> Cataclasite, Desert -> Solite, Taiga -> Lunite, Mesa -> Rosy Talc
  * Changed the biomes each family spawns in, check Lexicon for details
  * Add walls for each family
  * Mushrooms can now grow on Mycelite
* Change: NBT matching tweaks (artemisSystem)
  * Corporea requests now match exact NBT, instead of doing a "fuzzy match" that could
    grab items with extra NBT
  * This also means items with no tags will only match other items with no tags
  * Extended the hopperhock's special casing of mana items to corporea requests as well.
    Also made it not ignore other NBT data on the mana items.
  * Removed the 10-mana "fuzz" when determining mana item fullness.
  * All mana items are changed to never store a `Mana:0` tag, having a missing `Mana`
    entry represent the no mana state, to have more consistent behavior with other
	mods that compare NBT.
  * Fix ItemNBTHelper's `removeEntry` method. Previously it would leave behind an empty
    compound tag instead of removing it entirely, causing unexpected behavior
	(it now just calls the vanilla method, which handles this properly)
* API: Petal Apothecary is now a LiquidBlockContainer, meaning it can be filled by
  other mods that look for that interface (such as Hex)
* API: Make the entity using the horn available in IHornHarvestable

---

{% include changelog_header.html version="1.18.2-434" %}

* Add: EMI integration. EMI is a new recipe viewer mod
  that brings innovative new features such as
  crafting trees and material lists (emiploszaj)
* Add: New challenge achievement, Blessing. Get it by celebrating Tiny Potato's
  birthday with it on July 19th! Any candle cake will do.
* Change: Downmix Gaia music to mono so it is properly positional
* Change: Redirective lens can now make spreaders aim towards an entity that shot the
  burst (such as a player shooting a Redirective blaster)
* Change: Redirective lens bursts now hit entities and cause them to face the shooter
* Change: Remove quasiconnectivity from the Cacophonium, Luminizer Launcher, Spark
  Tinkerer, Corporea Funnel, Corporea Retainer, Mana Prism, and Red String Dispenser.
  Most of these weren't intentional and were found to be getting in the way of builds.
  If you have opinions about this, discuss with us on #botania_engineering on Discord. (artemisSystem)
* Fix: Greater Mana Ring having wrong capacity on Fabric
* Fix: Fluxfield documentation now mentions Tech Reborn Energy
* Fix: Fix Tech Reborn energy not being sent to blocks without a block entity on Fabric
* Fix: Add a fix to prevent crashes when the pixie spawn chance attribute isn't registered properly

---

{% include changelog_header.html version="1.18.2-433" %}

* Add: Mana Flames are now waterloggable, and the flash lens will place them in water
* Add: Wand of the Elven Forest (artemisSystem)
* Change: Mana Detector can now be crafted with a Target Block in the middle
* Fix: Redirect lenses crashing when hitting an entity
* Fix: Mana splitters working on things other than pools, causing crashes
* Fix: CraftTweaker docs for brews (BlameJared)
* Fix: Mana Prism not being waterlogged when placed directly into water
* Fix: Fix luminizer particles being wrong (quat)

---

{% include changelog_header.html version="1.18.2-432" %}

* Add: Reintroduce CraftTweaker support (Jared)
* Add: Tiny Potato can now be read by comparators (quat)
* Fix: Snowflake Pendant crashing the game
* Fix: Docs for tall flowers not being updated for the shears change
* Fix: Some Patchouli page titles being too specific (only showing one color of a multicolor item, e.g.) (Aegide)
* Fix: Brewery JEI stack positioning being slightly off
* Fix: Step height boost not working on Forge
* Fix: Pollidisiac wasting food when animals are already in love mode
* Fix: Empty stacks appearing in Corporea results, causing crashes on Fabric
* Change: Hopperhocks now respect the preventRemoteMovement flag on Forge
* Change: Tweaks to livingwood plank and derived textures (artemisSystem)
* Change: Tiny Potatos can only store one item per side instead of 64 (quat)

---

{% include changelog_header.html version="1.18.2-431" %}

* Add: Flower pouch has been expanded to hold tall mystical flowers
* Add: Tiny Potatoes that have the "enchanted" name prefix also glint in hand
* Change: Tall mystical flowers no longer require shears and simply drop themselves
* Change: On Fabric, add one hand/ring and one offhand/ring slot, instead of two hand/ring
* Change: Stop using internal Fabric creative tab details (quat1024)
* Fix: Flower pouch 0 tooltips rendering under the flower items
* Fix: Some NPE crashes
* Fix: Crashes on newer Fabric API versions
* Fix: Elementium pick mixin is less invasive and less likely to conflict with other mods
* Fix: Certain recipes not taking modded chests
* Fix: Force Relays would move their destination binding even if the destination block was unable to be moved
* API: ISparkAttachable is now a capability (ramidzkh)

---

{% include changelog_header.html version="1.18.2-430" %}

* Add: Update to 1.18.2
* Fix: Timeless Ivy lang fixes (Aegide)
* Fix: Starcaller not having a cooldown (aquila-zyy)
* Fix: Flower pouch picking up glimmering flowers
* Fix: Rod of Plentiful Mantle and Falling Stars not showing their particles in minimal mode
* Fix: Corporea Index logic being inconsistent as to when it would consume chat messages
* Fix: zh_cn and zh_tw lang entries for livingwood planks being wrong
* Fix: Floating flower islands not rendering properly on Forge
* Fix: Capabilities being invalidated too early on Forge, causing Curios to not drop
* Change: Mana Void is no longer a block entity and thus is now movable
* Change: Improve the lexicon page titles where multiple recipes appear (Aegide)
* Change: Petal apothecaries are now crafted with full blocks (artemisSystem)
* Change: Mossy petal apothecary is now crafted instead of being made by throwing a vine (artemisSystem)
* Change: Metamorphic petal apothecary recipes made consistent with the default apothecary recipe (artemisSystem)
* Change: Falling stars are now noclip until they touch air or liquid for the first time (aquila-zyy)
* API: A bunch of interfaces are now capabilities, exposed via the Forge capability system or Fabric API lookup system
* API: Reshuffle a bunch of the mana block interfaces
* API: RadiusDescriptor is now sealed
* API: Add StateIngredientCompound, a StateIngredient that composes two other ones (BlameJared)

---

{% include changelog_header.html version="1.18.1-429" %}

* Add: You can now filter what the Rannuncarpus picks up by placing item frames on the block below the dirt
* Change: Corporea requests are now capped at 65536 items
* Change: "Invader Girl" advancement now requires the maximum of 65536 items
* Change: Remove Cacophonium from piglin_loved tag as it's now crafted with copper
* Fix: Terrasteel being :uncraftable: on Fabric
* Fix: Rare client crash when using the Exchange rod
* Dev: Fix xplat code not being included into loader-specific source jars

---

{% include changelog_header.html version="1.18.1-428" %}

* **Initial release for Forge on 1.18.1**, supporting both Fabric and Forge using the groundwork laid by Jared and Darkhax in the <a href="https://github.com/jaredlll08/MultiLoader-Template">MultiLoader template</a>.
* This involved a lot of internal changes - please report any regression from the previous 1.18 Fabric release and from 1.16 releases (team)
* Add: Livingwood and Dreamwood now have log variants and a set of new textures (artemisSystem)
* Add: Spreaders now have smaller hitboxes, which can be expanded by adding scaffolding onto them to allow placing redstone on top of them (artemisSystem)
* Add: Creative Corporea Spark, which provides its items by copying them
* Add: Fallen Kanade's regen applies to pets
* Add: Rod of the Seas can now be used like bundles to refill empty buckets in inventory
* Change: Orechid now produces rare ores more often from deepslate, which can now be produced from end stone purification (artemisSystem)
* Change: Rebalance Rafflowsia around Orechid diamond production and extend the max streak to the maximum amount of existing flowers (artemisSystem)
* Change: Remove Pestle and Mortar (artemisSystem)
* Change: Allow Corporea Interceptors to intercept any failed request (artemisSystem)
* Change: Blacklisted some items from the Modern Industrialization duplicator
* Change: Make recipes for cracked and mossy things vanilla-consistent (artemisSystem)
* Change: Most inventory interactions now use loader-specifc inventory APIs
* Change: Managlass, Alfglass and Bifrost blocks and panes are now tagged as glass
* Change: Move Elementium Axe beheading to a loot table, and also allow it to receive Looting
* Change: Obtaining an elven Lexica Botania bypasses most book unlocks, and picking up a later step unlocks everything the previous ones did
* Change: On Fabric, Mana Fluxfield now produces Tech Reborn energy if that API is present
* Change: Prioritize blocks held in offhand over mana tools' block placement
* Change: Rod of Shaded Mesa now works on players
* Change: Soulscribe can now be repaired in anvils
* Fix: A few lenses refusing to autofire bursts in certain positions
* Fix: Add Botania pickaxes to "cluster_max_harvestables" tag (artemisSystem)
* Fix: Alfheim portal not allowing waterlogged pylons
* Fix: Bergamute horn prevention not working on servers
* Fix: Bergamute muting effect not distinguishing dimensions
* Fix: Change insertion check for Lithium compat
* Fix: Endoflames using the wrong delay check
* Fix: Fail the interaction properly when Shard of Laputa checks don't pass
* Fix: GUI overlays (eg. mana bar) rendering if the GUI is hidden in some circumstances
* Fix: Gaia Guardian not using the custom boss bar (on Fabric, the bar may still overlap with other boss-bars on screen)
* Fix: GoG skybox planet rendering (UltrusBot)
* Fix: Hopperhock inserting into the wrong side (atakle)
* Fix: Life Aggregator not working
* Fix: Luminizer bounding boxes never updating
* Fix: Mana Prism eating right clicks even it nothing happened (artemisSystem)
* Fix: Many particles on flowers not using the block offset
* Fix: No sound/particles when mining with the shatterer/truncator
* Fix: One Mana Bottle effect now picks a health value dependent on the entity max health
* Fix: REI catalysts for crafting missing the crafting halos
* Fix: Rafflowsia NBT writing full registry keys instead of just flower block IDs
* Fix: Rannuncarpus trying to place even if IFlowerPlaceable worked
* Fix: Requesting 'all *' with the Corporea Index
* Fix: Rod of Shifting Crust wireframe mismatching the actual area sometimes
* Fix: Some buggy behaviors of fake mana bursts
* Fix: Some item interactions not ignoring dead entities
* Fix: Some miscellaneous block entity state changes not marking the chunk as changed
* Fix: Some trinket/curio renderers missing
* Fix: Soulscribe repairing itself with mana
* Fix: Spawning strays with the loonium crashing
* Fix: Spreader binding inconsistency when binding to blocks with smaller hitboxes, eg. skulls attached to different block faces
* Fix: Terra Blade burst spawning being very unreliable
* Fix: Terra Shatterer AoE voiding blocks it can't mine
* Fix: Various world height related bugs
* Fix: Weight and Bore lens harvest level checks
* Fix: Weight lens working on blocks that cannot be silk touched. (Blocks can be added to the tag "botania:weight_lens_whitelist" to bypass this check.)
* Fix: World overlays not rendering on fabulous mode

---

{% include changelog_header.html version="1.18.1-427" %}

* Fix: Crash on mixin application on client
* Fix: Crash with Rod of Unstable Reservoir targetting certain entities

---

{% include changelog_header.html version="1.18.1-426" %}

* Info: Update to 1.18.1
* Add: Horn of the Canopy now breaks vines
* Fix: Flower syncing
* Fix: Ender Air Cloud entity not having a name
* Fix: Ender Air Cloud entity not being pickupable by hand
* Fix: Spam from advancements with certain Discord bridge mods
* Fix: Mana blaster lens recipe voiding extra blasters or lenses
* Fix: Mod name looking ugly in creative tab
* Change: Mixin cleanups

---

{% include changelog_header.html version="1.18-425" %}

* Fix: Fix block entity update packets not being fired
* Fix: Mixin conflict with Enhanced Celestials

---

{% include changelog_header.html version="1.18-424" %}

* Hotfix: Fix crash on startup caused by lack of Fabric datagen api in non-dev environments

---

{% include changelog_header.html version="1.18-423" %}

* Info: Update to 1.18. Trivia: 2 builds for 1.17 is the least Botania has ever had for an mc version. Next is 1.11, with 4 public releases
* Info: Forge work will commence soon
* Fix: Replace various internal workarounds with standard Fabric API's

---

{% include changelog_header.html version="1.17.1-422" %}

* Info: This changelog also includes changes from the previous version (421)
* Info: Port to 1.17.1 Fabric. Forge will also be coming soon.
* Add: Luminizers can now be phantom-inked to suppress their particles
* Add: Influence Lens now affects primed TNT
* Add: Trinket Case and Flower Bag have Bundle-like interaction in the inventory menu
* Add: Snowflake Pendant is in the vanilla #freeze_immune_wearables tag
* Add: Structure files for Botania's multiblocks. These are just for convenience while testing and are not used for gameplay purposes
* Add: Pure Daisy recipe from End Stone -> Cobblestone. After completion, a cloud of Ender Air is released above the End Stone that can then be bottled by a dispenser.
* Add: GameTests for various internal features (team, quat)
* Change: Mana Detector is no longer a block entity and thus can be pushed by pistons
* Change: Cacophonium is now crafted with copper
* Change: Destroyed Trinket Boxes and Flower Pouches drop their contents like Bundles do
* Change: All lenses now stack to 16
* Change: Wand of the Forest now shows mode in the item name itself
* Change: Remove shader taters
* Change: Tater nameplates are hidden in F1 like vanilla ones
* Change: Botania sounds use their own soundevents, allowing them to be overridden independently of vanilla sounds (quat)
* Change: Orechid and Marimorphosis now use the JSON recipe system
* Change: Remove fluxfield config option. Modpacks that don't like it can simply remove the recipe theirself.
* Change: Remove obscure "fragile" mapmaker tag for pools
* Change: Kindle lens actually lights the burst on fire internally, so it can participate in vanilla mechanics like lighting the campfire
* Change: Any burning projectile can light the Incense Plate, similar to the campfire
* Change: Update Cacophonium Block texture to match new Note Block texture (quat)
* Change: Launching an entity with the Rod of Shaded Mesa counts as a player kill if the entity dies as a result
* Change: Force Relay and Force Lens now use vanilla piston logic, allowing slime block structures to be pushed. This is subject to further change as the implementation is quite tricky.
* Fix: Fix bursts being unable to collide with their source block at all. Now they can collide with their source block after leaving it for the first time
* Fix: After years of being broken, reimplement lightning bolts using Mekanism's code. Still some rough edges to work out.
* Fix: Adjust some lens models to prevent Z-fighting (artemisSystem)
* Fix: Refactor and overall improve binding logic, preventing various directionality bugs with flower binding range (quat)

---

{% include changelog_header.html version="1.16.5-420.3" %}

* Add: Horn of the Canopy now breaks vines (from 1.18.1-426)
* Add: Luminizers can now be phantom-inked to suppress their particles (from 1.17.1-422)
* Change: Update Cacophonium Block texture to match new Note Block texture (from 1.17.1-422) (quat)
* Change: Tater nameplates are hidden in F1 like vanilla ones (from 1.17.1-422)
* Fix: Some buggy behaviors of fake mana bursts (from 1.18.1-428)
* Fix: Bergamute muting effect not distinguishing dimensions (from 1.18.1-428)
* Fix: Potential crashes in Bergamute code
* Fix: Terra Shatterer AoE voiding blocks it can’t mine (from 1.18.1-428)
* Fix: Rod of Shifting Crust wireframe mismatching the actual area sometimes (from 1.18.1-428)
* Fix: Duplication glitch when mossifying a Petal Apothecary
* Fix: Refactor and overall improve binding logic, preventing various directionality bugs with flower binding range (partial backport from 1.17.1-422) (quat)

---

{% include changelog_header.html version="1.16.5-420.2" %}

* Fix: Mixin conflict with SpongeForge
* Fix: Bergamute horn prevention not working on servers

---

{% include changelog_header.html version="1.16.5-420.1" %}

* Info: Barring hotfixes, this will be the final update for Botania Forge 1.16. Thanks for your support!
* Change: Horns and Drums will not break blocks when in range of an active Bergamute (Alwinfy)
* Change/Fix: Narslimmus buffer was too small to hold all the mana from large slimes. Reported by million09.
* Change: Mass camoflage changes for platforms no longer spread to different platform types
* Fix: Bellows sometimes messing up modded furnaces. Reported bloche1871.
* Fix: Warp Lens documentation didn't explain properly that mana is not transferred
* Fix: Using too fast a Haste effect would make the Mana Blaster get stuck. Reported by DrTasslehoff.
* Fix: Armor rotations not declared properly causing incompatibilities with mods such as Epic Fight. Reported by dylan-jara.
* Fix: Mana items would not drain into a full pool with a Mana Void below. Reported by artemisSystem.
* Fix: Sojourner' Sash name being misspelled. Reported by rsaihe.
* API: IElvenItem deprecated

---

{% include changelog_header.html version="1.16.5-420" %}

* Haha: funny number
* Add: tomater (Eutro)
* Add: Spreader (de)wooling now makes sounds (quat)
* Add: Support for CraftTweaker recipe replacing and printing
* Add: Jiyuulia and Tangleberrie Petites (artemisSystem)
* Add: Niter to Orechid (iTitus)
* Change: Make Rannuncarpus wand operation consistent with other flowers
* Change: Mana Enchanter now allows crying obsidian alongside obsidian
* Change: Cache failed-to-match state of crafty crate, which improves lag of idle Crafty Crates that have an invalid recipe
* Change: Blacklist special flowers from Buzzier Bees bonemeal duping
* Change: Make Corporea Crystal Cube look better (better AO, break particle, item render) (quat)
* Change: Remove data related to 2021 summer build contest (quat)
* Change: Spreaders interact with right clicks more smartly (quat)
* Change: API: ManaItemHandler.request/requestExact can now pull from multiple items to satisfy one request (noeppi)
* Fix: Mana Enchanter's form and fade sound effect subtitles were swapped
* Fix: Bifrost Rods breaking at y = 0
* Fix: Rare crash when spawning charged creepers from Loonium
* Fix: Floral Obedience Stick desyncs flowers when used from Dispenser
* Fix: Improve performance of large corporea spark networks
* Fix: Exoflame bugging out on modded furnaces sometimes by moving past the maximum cook time
* Fix: Mobs spawning on platforms
* Fix: Corporea Crystal Cube used null ItemStacks, precise effects unknown but definitely a bug :)
* Fix: Village chest loot not spawning properly
* Fix: Sparks sometimes kept acting as if they were still there after being removed
* Fix: Incorrect logic check in TileExposedSimpleInventory.canInsertItem
* Fix: Cirrus Pendant not firing jump events and thus breaking with mods that expect it (StrikerRocker)

---

{% include changelog_header.html version="1.16.5-419" %}

* Add: eutrotater (Eutro)
* Change: Buff bellethorne
* Change: Rannuncarpus is now blockstate-agnostic by default. You can recover the old behaviour by toggling with a wand. Existing flowers remain blockstate-sensitive.
* Change: Fel blazes no longer despawn
* Change: A single Bergamute now mutes less (0.5x instead of 0.15x). However, Bergamutes also now stack with each other.
* Fix: When a fel blaze is spawned and the iron bars are waterlogged, the water now stays behind
* Fix: Floating labellia name was missing
* Fix: "Star" effect as used by the runic altar and manastorm charge having weird brightness flickering
* Fix: Avatars' activation being inverted (supposed to be power-to-deactivate, was mistakenly power-to-activate

---

{% include changelog_header.html version="1.16.5-418" %}

* Fix server crash

---

{% include changelog_header.html version="1.16.5-417" %}

* Info: The Summer 2021 Botania Build Contest is in progress! Submit a Botania contraption for the chance to win prizes, bragging rights, and a spot on the Botania homepage!
* Info: See https://botaniamod.net/contest_2021.php for more information.
* Add: The Labellia, a functional flower that takes a name tag plus mana and renames all items and entities in a small area around itself. Texture provided by gamma-delta.
* Add: Rod of the Skies can now be used to boost elytra flight at the cost of mana. It provides a boost around that of the lowest tier firework. (00-Steven)
* Add: Avatars holding Rod of the Skies now boost elytra flying players nearby. This boost is slightly stronger than the handheld boost. (00-Steven)
* Add: Added compatibility with the Catalogue and Configured mods
* Add: [API] IHornHarvestable no longer needs to be implemented directly on the block, a separate handler can now be registered (InfinityRaider)
* Revert logo in mod list to previous one
* Improved CraftTweaker documentation
* Botania recipes in JEI are now sorted
* Particles that appear when mana flows between sparks are now colored according to the spark's network
* Tweaks to particles:
* - Cacophonium emits note particles when activated
* - Prism emits redstone particles when powered
* - Functional flowers that can be disabled show redstone particles when powered
* - Avatar, Open and Crafty Crate, Mana Pump, and Spreader Turntable show redstone particles when powered
* Tweaks to Gaia Guardian:
* - Check for fake players when rolling loot table
* - Increase damage cap slightly and fix it not being applied sometimes
* - Prevent multihits
* - Nerf health amount for multiplayer fights
* - All players now get the related advancements from a Gaia Guardian kill
* - When the fight is completed, kill all straggling pixies and poison clouds, and cure wither effect
* - Dice of Fate now gives extra goodies from a random pool once you've acquired all the relics
* Fix method name conflict with Mojmap
* Fix mod metadata being incorrect in places
* Fix Terra shatterer charging from dispersive sparks
* Fix Rod of the Shaded Mesa duping items sometimes
* Fix Clayconia not recognizing modded sand blocks
* Fix floating flowers not being linked to their base flower's page in the lexicon
* Fix Prism not acting powered if placed in a powered initial position
* Fix Corporea + Storage Drawers dupe bug
* Fix glow effect on Avatars jumping around in brightness
* Fix orechid trying to use empty tags sometimes
* Fix there being a slight stutter every time you pass through a luminizer when on a multi-luminizer track
* Fix corporea crystal cube comparator output not updating after a relog

---

{% include changelog_header.html version="1.16.5-416" %}

* Add: Rod of the Depths, Rod of the Lands, and Black Hole Talisman now support Quark reacharound
* Add: API support for dynamic mana infusion recipes (eutro)
* Add: Potted plants are now valid targets for the Red String Spoofer
* Add: Vine balls can now be shot from dispensers
* Add: Orechid entries can now be removed using datapacks
* Add: CraftTweaker dumper for orechid outputs
* Change: Rod of the Hells and Rod of the Bifrost cooldowns dramatically lowered in Creative Mode
* Change: Reorder categories in the Lexicon to be more logically consistent (DasGerippe)
* Change: Avatars using Rod of the Bifrost now consume about half as much mana as before
* Change: Reallow swapping glass and glass panes using Rod of the Shifting Crust
* Change: Flugel Tiara no longer consumes mana in creative
* Fix: Solid vines not being in vanilla climbable tag
* Fix: Rod of Shifting Crust not activating until the block is broken in survival mode
* Fix: Rod of Bifrost general improvements to bridge placing
* Fix: Rod of Bifrost not consuming mana (for 7 years)
* Fix: Terra pick models being positioned incorrectly
* Fix: Fel pumpkins not rotating properly

---

{% include changelog_header.html version="1.16.5-415" %}

* Change: Mana infusion recipes can now specify their catalysts with state ingredients
* Change: Rods can now be put into the Trinket Case (Alwinfy)
* Change: Rod of Shifting Crust now operates by simulating placement instead of directly placing blocks
* Change: Rod of Shifting Crust now only works on solid blocks again, in consistency with lexicon
* Change: Rod of Shifting Crust can now be limited to a 7x7 plane by the Stone of Temperance
* Change: Rod of Shifting Crust now applies a mana cost penalty when breaking very hard blocks
* Change: Add mushrooms to vanilla tags (Patrigan)
* Change: Orechids now use JSON to declare what ores they can produce
* Change: Ring of Correction now supports hoes and shears
* Change: Add a recipe cache for the Crafty Crate to reduce lag
* Fix: King Key weapons rendering weirdly
* Fix: Attempt to reduce the frequency at which item NBT syncs happen, which can cause as ghost duplicate items clientside
* Fix: Pylon crash on OpenJ9 JVM
* Fix: sash and jump amulet not reducing damage properly
* Fix: luminizer glow having a square background
* Fix: King Key weapon spell circle transparency being weird
* Fix: Tiny Potato positioning when in head slot
* Fix: Tiny Potato wiggle when jumping being off (_0Steven)
* Fix: Medumone ignoring redstone
* Fix: Terra Pick not mining some stone like blocks like AS Marble
* Fix: Laputa Shard bursts infinitely retrying if denied by e.g. permissions plugins
* Fix: entity tracking ranges being way too big
* Integration: Make criterion instances public (noeppi_noeppi)
* Integration: CraftTweaker pure daisy time argument is now optional
* Integration: Added CraftTweaker compatibility for orechid registrations

---

{% include changelog_header.html version="1.16.4-414" %}

* Fix tiny potatoes named anything other than a-z, 0-9, _, or - crashing on render

---

{% include changelog_header.html version="1.16.4-413" %}

* Add: Recipe to split composite lenses apart, losing the slime ball (Eutro)
* Add: Recipe to upgrade a Mana Pool to a Fabulous Pool (BlueAgent)
* Add: Extra tiny potato models (SuicidalSteve)
* Add: Allow Glimmering and Floating Flowers to be used with the Mana Enchanter (BlueAgent)
* Add: Manasteel and Elementium Hoes, since vanilla is starting to emphasize the importance of hoes
* Add: Any models within the /assets/botania/models/tiny_potato/ folder is automatically loaded as a custom tiny potato model (Eutro)
* Add: JEI integration for Terra Plate recipes
* Change: Most items no longer have "Shift to expand tooltip" as their tooltips were too short anyways
* Change: Magical flowers are no longer in the vanilla small_flowers block tag due to the multitude of issues it causes. They remain in the corresponding item tag.
* Change: Garden of Glass overrides of Botania recipes are moved back to Botania itself. Botania now requires Garden of Glass 1.7 or higher.
* Change: Crafting Botania banners no longer consumes the pattern item, consistent with vanilla
* Fix: Terra Plate right click only accepting Manasteel/mana pearl/mana diamond instead of any item used in a terra plate recipe
* Fix: Botania shears not working in dispensers and being unbreakable
* Fix: Solegnolia distance calculation being based on block corner instead of center
* Fix: Solegnolia effects lingering for a short while after removal
* Fix: Bamboo not being plantable on Botania grasses
* Fix: Solegnolia lexicon entry being confusing
* Fix: Lexicon elven trade recipe not working sometimes
* Fix: Exoflame materializing free mana out of thin air
* Fix: Hydroangeas un-waterlog waterlogged blocks instead of deleting them entirely
* Fix: More instances of the lighting bug where entire chunks go dark
* Fix: Floating pure daisies not saving what kind of island they're on
* Fix: Several dupe glitches when Botania is played with broken mods that allow stacking items that should not be stacked
* Fix: Garden of Glass skybox crashing with other mods sometimes
* Fix: Lexicon recipes being broken when custom recipes are added by CraftTweaker
* Fix: Locked hourglass message displaying twice
* Fix: Items disabled in the config no longer show up in the lexicon
* Fix: Lexicon can now display any terra plate recipe
* Fix: Crimson Pendant not preventing the damage sound and effects
* Fix: Loki Ring and Terrasteel Pick being broken together
* Fix: Floating Flowers destroy farmland immediately below themselves
* I18n: ru_ru changes (pgkrol)
* Cleanup: Optional cleanup (Alwinfy)

---

{% include changelog_header.html version="1.16.4-411" %}

* Add CraftTweaker support for Botania (jaredlll08)
* Odin Ring now protects against magma blocks and flying into walls
* Ender Air is now dispensable
* Ghasts hit by Ender Air now drop ghast tears in the Overworld
* Fix rod of the plentiful mantle's subtitle
* Fix serverside gog support. Use level type "botania:gardenofglass"
* Fix vanilla recipe log spam
* Fix gog island command not teleporting to existing islands if ran in a command block
* Fix corporea indexes sometimes hanging the game if you switch between multiple saves rapidly
* Ring of Correction will now prioritize the Vitreous Pickaxe when breaking glass
* Fix Entropic lens damage behaviour not matching TNT
* Headflower yellow glint now works with Charm's glint enchantment as well
* Fix default spreader angle sometimes not binding to pools
* Fix Terrasteel Pickaxe AOE destroying bedrock
* Reimplement the Bergamute in a cleaner and hopefully more compatible manner
* The Bergamute is now classified as a Misc Flower (like the Pure Daisy and Manastar)
* Fix grammar error in lexicon (dylantompkins)
* Add a "Radius: N" tooltip to Laputa Shards so it's more clear they do something to your world
* Add recipe for merging partially-filled brew vials/flasks together (eutropius225)
* Lexicon cleanups (Alwinfy)
* Fix IBlockProvider not being fully accurate (Penrif)
* Fix Bloodthirst potion effect not working (Alwinfy)
* Fix Vitreous Pickaxe incompatibility with Astral Sorcery prisms
* Fix Lexicon name when inside an Akashic Tome
* Fix banner patterns being broken
* Fix flower worldgen being too clumped together
* zh_TW updates (Jeffku0107, ttdyce)
* Fix ancient will critical hit effects not working
* Fix daffomill desyncing when powered by comparator
* Add a mcfunction that unlocks the entire lexicon: /function botania:full_book

---

{% include changelog_header.html version="1.16.4-410" %}

* NOTE: If you use Garden of Glass, you *must* update to GoG 1.6+!
* Open and crafty crates check collision more snugly against their bottom face, meaning that they can now output on top of a hopper without a one block gap
* Third Eye uses the Magic Missile's targeting system (Alwinfy)
* Rannuncarpus places directional blocks facing itself, instead of randomly (Eutro)
* Expose terra plate recipe's getIngredients() (MelanX)
* ru_ru fixes (BetaCarotina)
* Fix spectrolus HUD not smoothly blending between colors
* Fix laputa shard recipes being missing in the lexicon
* Mana pools act like a full solid slab when colliding with bursts, fixes bursts sometimes phasing through pools
* Add ancient debris to orechid ignem pool
* Fix fence gates not being added to the vanilla tag
* Add config blacklist for rannuncarpus, blacklist Storage Drawers
* Fix crash in spectator
* Fix metamorphic stones still dropping cobble version with silk touch
* Add corporea stairs + brick slabs, stairs, walls (ToMe25)
* Bread explosions now drop all blocks they broke
* Remove GoVoteHandler
* Add Quark stones to elementium pick disposables
* Remove old armor models, fix pixie halloween texture
* Fix double pants rendering
* Add infrangible platform to the dragon_immune and wither_immune tags
* Headflowers now use a normal enchantment glint instead of a washed out flat yellow shader. If Quark is installed, a yellow glint is used as if a yellow rune was applied.
* Fix pools not emptying fully when using sparks (Alwinfy)
* Add walls for Botania bricks (ToMe25)
* Fix water bowl texture (MoriyaShiine, cybercat5555)
* Make Astrolabe horizontal placement easier
* Usual internal cleanups and improvements

---

{% include changelog_header.html version="1.16.3-409" %}

* Fix GoG world type showing up when GoG isn't installed (accidentally excluded from last build)
* Work around platform bugs to fix "missing keybind" crashes

---

{% include changelog_header.html version="1.16.3-408" %}

* Fix prisms not colliding with bursts (and thus not working at all)
* Clarification: Since 1.16.2, mana bursts will always collide with blocks based on their *collision* shape. In the past, this used to be the block's main shape, but this has been changed to more closely match how other entities behave. Contraptions using special blocks that have differring main and collision shapes should be rechecked.
* [API] Deprecate IClientManaHandler
* Fix GoG world type showing up in create world menu even when GoG isn't installed

---

{% include changelog_header.html version="1.16.3-407" %}

* Cocoon of Caprice mob pools can now be customized with tags
* Waterlogged cocoons can now place mobs in their own blockspace after disappearing
* Fix feeding cocoons gaia spirits not shortening the wait time
* Terra axe and pick only break loki-selected positions when sneaking, to match placement behaviour
* Fix avatar and life imbuer wasting a lot of mana
* Obedience stick is now usable from a dispenser
* Dispensers can now place sparks
* Make Gaia Spreader recipe shapeless b/c why was it shaped before
* Manastorm charges inherit the color of the igniting burst
* Fix feeding teru-teru-bozu with right click instead of toss crashing
* Update to 1.16.3
* Go vote screen, in case you somehow manage to run the mod without Patchouli.
* Fix red string blocks affecting rendering performance even when not holding a Wand
* Add Corporea Block/Brick/Slab, a totally original feature not stolen from Incorporeal
* Fix crashes between terra axes and vein-mining mods
* Update the Gaia fight music to the 2018 remasters
* Terra plate recipes can now be customized using JSON

---

{% include changelog_header.html version="1.16.2-406" %}

* Buff Fruit of Grisaia
* Fix TNT ethical check hanging worlds
* Fix crashes on Forge 33.0.23+
* Fix fallback brew showing on brew vials
* Fix some important particle effects being hidden by reduced/minimal particles, which made for a bad experience
* Prevent trinket case from being picked up in the gui
* Fix orechid priority config not working on servers

---

{% include changelog_header.html version="1.16.2-405" %}

* Update to 1.16.2. Where did 404 go?...
* Rosa Arcana now disenchants items passing near it, releasing XP orbs (that it then absorbs)
* Clean up sound event ID's
* Readd old motif flowers and an advancement
* Fix some incorrect tooltip colors
* Add specific tag for flowers that can be contributor headflowers, add the motifs to this tag under their old names.
* Fix loki ring resetting if you run out of blocks with an empty offhand
* Mooshrooms are now "milked" by the Drum of the Gathering instead of sheared (use dispensers as they can now shear mooshrooms in 1.16)
* Vazkii name typo now 100% more reliable
* Fix out of bounds in Corporea causing some queries to not return feedback in the chat
* Hopperhocks special-case mana-storing items, matching (fuzzily) how much mana they have: empty, partially full, full
* Laputa shards can now place blocks into any replaceable material instead of requiring air

---

{% include changelog_header.html version="1.16.1-403.1" %}

* Backport some fixes from 1.16.2:
* Fix loki ring resetting if you run out of blocks with empty offhand (from 405)
* Fix out of bounds in Corporea causing some queries to not return feedback to chat (from 405)
* Fix ethical TNT check hanging worlds (from 406)
* Fix orechid priority config crashing servers (from 406)

---

{% include changelog_header.html version="1.16.1-403" %}

* Extend luminizer smart dismount from last build to all living entities
* Fix dispensers voiding stacks of size 1 when filling glass bottles
* Fix weight lens requirements for dropping a block not matching vanilla's requirements (Alwinfy)
* Update banner and crafty crate lexicon screenshots (Alwinfy)
* Another null check workaround for Optifine
* Move laputa shard upgrading to custom/dynamic recipe
* Rod of the Unstable Reservoir fired by an avatar now targets all hostile mobs
* Rod of the Unstable Reservoir fired by a player now targets mobs hostile to other players, when PVP is off
* Fix gaia guardian not spawning pixies during the mob spawn phase (Keralop)
* Fix invisibility cloak not actually consuming mana (Keralop)
* Add a HUD for the Prism similar to the spreader's
* Lens tinted tooltip is now colored based on the tint
* Fix bore lens having the wrong flags and thus being uncombinable/combinable with the wrong lenses
* Fix fork and toggle luminizers having switched effects and rendering in some cases
* Add Minecraft minor version to mod version to help disambiguate

---

{% include changelog_header.html version="1.16-402" %}

* Fix a crash on Forge 1.16.1 107+
* Add some gold-based items to the piglin_loved tag
* Add custom grasses to forge dirt tag, fixes trees not growing on them
* Fix mana flashes catching on fire
* Fix life imbuer being broken (again)
* Flowers with multiple areas of effect (e.g. rannuncarpus has different pickup vs place ranges) will now render both of them when the monocle is worn.

---

{% include changelog_header.html version="1.16-401" %}

* Fix dyed corporea sparks hanging the game on load

---

{% include changelog_header.html version="1.16-400" %}

* This is the 101st build of Botania since the first release of Botania Unofficial 1.8 (299) I made in 2015. Thanks for all the support over the years! - williewillus
* Reorder block and item registration order so creative tab and JEI are more organized
* Fix being able to extract things from open and crafty crates' internal inventories
* Open crate no longer claims to have a comparator output
* Fix some inventory blocks not exposing their inventories to forge
* Corporea Index now parses "*" as a shell-style glob (Alwinfy)
* Unbreaking enchantments on mana gear now confers a minor mana discount instead of having no effect
* Floating Flowers now break faster with a shovel
* Bonus Chests are now guaranteed to contain a Lexicon
* Lower chance of manasteel in dungeon loot
* Tweak item rarities (colors)
* Ender Air can now be obtained by dispensers
* Paintslinger Lens can now dye sparks and corporea sparks. Sparks now rest slightly lower in their blockspace to aid in burst collision. (Alwinfy)
* Fix Gaia Head not being equippable by dispenser
* Fix rod of the (sky)lands not swinging in creative
* Fix a bunch of tile entities crashing when moved by Quark pistons
* Workaround for an optifine crash
* Fix agricarnations not accelerated many plants even though it looked like it was
* Fix some flugel tiara wings looking weird
* Add config that makes the orechid prefer certain mods over others if the ores tag contains multiple entries. This can be used by packs that prefer a single mod handle all ores.
* Luminizers no longer mount the player if they are trying to trigger its hidden feature. Detection box for hidden feature expanded slightly.
* Fix some desyncs where the Alfheim Portal could appear disabled clientside
* Add statistics for items requested by corporea, tiny potatoes petted, and distance by luminizer
* Luminizers now perform smart-dismounting for players, like pigs/boats/carts
* Pixies lose their raytrace hitbox as soon as they reach 0 health. This makes fighting the guardian less annoying as dying pixies won't reflect your arrows.
* Mana Flame reworked to no longer require ticking, for a minor performance boost.
* Work around a Mixin issue with Invokers conflicting with other mods such as Charm
* Clean up JEI integration layouts (centered things properly, etc.)

---

{% include changelog_header.html version="1.16 399" %}

* Significant internal cleanup of corporea. Fix interceptors not working
* Fix pink withers dropping nether stars
* Fix some tooltips being miscolored
* Flugel Eye now has a unique binding per-dimension, which is shown in the tooltip
* Fix resolute ivy not disappearing from an item after death
* [API] Minor breakages in corporea. Detection of corporea nodes can now be registered in the API, meaning you can now write your own integration exposing your custom inventories to corporea.
* [API] A reminder that if your storage already efficiently operates with IItemHandler or IInventory, then corporea already handles it and you don't have to do anything.

---

{% include changelog_header.html version="1.16 398" %}

* Welcome to Botania on 1.16.1, brought to you by williewillus and Hubry
* Special thanks to the patrons and players on the patron server for helping test
* Note: 1.16.2 is coming soon and we know it to have some minor breakage. This version may not be directly usable in 1.16.2, but worlds should be upgradable to it perfectly.
* Petal Apothecary stores its fluid using blockstates, meaning fluid state changes can now be detected with an observer
* A bunch of translucent things now support the new fabulous graphics
* Fix runic altar cube particles
* Floral powders have been removed in favor of directly using the generic vanilla dyes
* Crafting Halos have been modernized -- instead of saving the exact items used in a past crafting operation, they now save the recipe ID that was used. This means the halos now work on recipes that take tags
* Fix king key wonkiness and rendering wonkiness
* Magical flowers no longer always claim to output redstone. This means that redstone dust no longer points into them by default, consistent with other vanilla devices like dispensers.
* Add suspicious stew effects for all magical flowers (Alwinfy and Discord #general)
* Fix life imbuer being broken
* Tigerseye now has auditory and visual feedback when it takes effect
* Fix gaia head rendering when worn
* botania-skyblock-spread command and Garden of Glass island tracking has been renovated:
* - Now callable with /gog or /gardenofglass
* - Player island locations are now saved, meaning no more traffic jams where someone's island spawns on top of someone else's
* - The command includes subcommands to teleport to other players' islands and regenerating the starting setup
* Remove signal flare entity
* Readd magical flowers to #small_flowers tag
* Entropinnyums now prefer organic, naturally-sourced (non-duped) TNT. Existing contraptions are safe from explosions, but will not generate nearly as much mana anymore :)
* Terrasteel armor is neutral towards piglins like gold armor
* Mystical flowers no longer spawn on mushroom biomes
* Rod of the Unstable Reservoir no longer targets tamed mobs that aren't actively attacing the shooter
* Fix flugel tiara wing renders (about time)
* Corporea Retainers can be set to only remember the missing items from the intercepted request, by right clicking with the wand
* Known issues:
* - Shears are still wonky
* - GoG skybox will not automatically activate on GoG worlds. To see it, you'll have to enable the "fancySkybox.normalWorlds" config option.

---

{% include changelog_header.html version="r1.15 388" %}

* This is the final build for 1.15. See you in 1.16.1 very soon!
* Fix melon and pumpkin seeds not spawning in Garden of Glass (*requires GoG update*)
* Fix duplicate model loader registration when resources are reloaded
* [API] Add constructor for Brews that takes a supplier (Alan19)
* Rafflowsia now has a streak-like system awarding many diverse flowers, similar to the overhauled Gourmaryllis. Mana output is dramatically increased for very long streaks. Try to automate it to 15 or more unique flowers! (Alwinfy)
* Partial fix for Soujourner's Sash and Cirrus Amulet causing fall damage

---

{% include changelog_header.html version="r1.15 387" %}

* Update critical hit logic in ancient wills to match vanilla
* Fix crashes with flugel tiara when running on Java 11
* Many internal changes in *preparation* for portability to other mod loaders (dev team, MoriyaShiine)
* Fix typo in Bifrost rod description
* Restore Garden of Glass sky renderer
* Gourmaryllis mana formula has changed: it now rewards a longer chain of diverse foods. Alternating blindly between two foods is no longer the most optimal way, but should still work as a baseline starting point. Try to feed it many different foods! (Alwinfy)
* Barring no major catastrophes, this will likely be the final 1.15 build. 1.16 awaits!

---

{% include changelog_header.html version="r1.15 386" %}

* Fix dedicated server crash

---

{% include changelog_header.html version="r1.15 385" %}

* Clean up magnet lens code and performance (Eutro)
* Fix "found a broken recipe" for pure daisy recipes
* Remove justified lexicon config as the lexicon is handled fully by Patchouli now
* zh_cn updates (Determancer)
* Many Botania blocks can now be waterlogged. This should work fine, but please back up your worlds before updating just in case (Eutro)
* ru_ru updates (Zinkorrum)
* Fix tall mystical flowers not being in the #tall_flowers tag
* Remove special flowers from the #small_flowers tag and only put them in #flowers. Fixes endermen picking them up.
* New easter egg
* zh_cn updates (friendlyhj)
* Code cleanups (Alwinfy)
* Brighten the hopperhock HUD
* Fix coloring being incorrect for "Press SHIFT for more details" tooltips
* Performance and network consumption improvements
* Fix pure daisy not showing particles when a recipe completes

---

{% include changelog_header.html version="r1.15 384" %}

* Fix burst collision bugs introduced by last build (bore lenses should behave as they did in build 382 and before again)
* Minor lexicon wording fixes
* Only create shader render layers when botania shaders are enabled. May fix some conflicts with shader mods?
* Fix Gaia Head crashing when worn. It still will not render correctly, but it won't crash anymore.
* Loki Ring will now exit binding mode when you take it off, to prevent situations where you forget where you set the binding center and can't exit binding mode
* Fix patron flowers not rendering if the player's display name was modified (for example by other mods or vanilla scoreboard teams)

---

{% include changelog_header.html version="r1.15 383" %}

* zh_CN updates (ppoozl)
* Add GoG-specific cocoon entry
* Fix phantom-lensed bursts triggering blocks they pass through too many times
* Fix incorrect corporea spark range documentation
* Fix Terra Shatterer, Mana Tablet/Band tooltips (eutropius225)
* Fix Rod of the Shaded Mesa
* Fix too-high-level Laputa Shards being available in creative
* Fix Laputa Shard upgrade recipe being broken again
* Fix Pink wither losing its docility after reloading the world
* Bifrost blocks now color beacons a rainbow color
* Add Botania shears to the (unofficial) forge:shears tag (cech12)
* Improve apothecary compatibility with fluid containers (cech12)
* Fix botania glass blocks (mana/alfglass, bifrost) not being in impermeable tag, fix them showing the waterfall texture when adjacent to water. It should now match vanilla glass.
* Fix brewery renderer sometimes going crazy when items are on the plates
* Refine open/crafting crate's "is there room to drop" check to be more precise
* Fix ender air not being collectible from right clicking in air. Dragon's breath nearby will still inhibit ender air collection.
* Fix rune of winter recipe being incorrect
* Items on Tiny Potato now face the correct way (Partonetrain)
* Fix cobweb recipe in lexicon being broken (Partonetrain)

---

{% include changelog_header.html version="r1.15 382" %}

* Welcome to Botania on 1.15.2
* r-version will always match the major Minecraft release now
* API cleaned and reworked (there are some missing pieces that will be filled in)
* All Botania recipes are sourced from JSON for faster loading times and better customizability
* All accessory renders are fixed besides Known Issues
* Lots of behind-the-scenes cleaning so the mod will be maintainable for years to come
* Known Issue: Flugel Tiara renders don't look right
* Known Issue: Botania shears don't act like shears

---

{% include changelog_header.html version="r1.11 379" %}

* This is the final build for 1.14.x. See you in 1.15.2 very soon!
* Add additional checks so that crashes unrelated to Botania don't blame Botania
* Fix dyeing fabulous pools not adjusting the color
* Dispensers can now use the Wand of the Forest's sneak right click actions, consult the lexicon for more. (eutropius225)
* Runic Altar and Petal Apothecary fast-recraft no longer consumes items in creative mode
* Fix mana tablet recipe not accepting mana pearls
* Fix bifrost pane being unbreakable
* Fix floating endoflame recipe being missing
* Fix entropinnyum accepting TNT resting in waterlogged blocks
* Fix mana clip attachment destroying lenses
* Fix Resolute Ivy not working on curios
* Fix crash when a minecart that is Spectral Rail boosted lands directly on another Spectral Rail then runs into a normal rail
* Items that generate mana and provide it to other items in inventory only do so serverside. May fix some desyncs.
* Remove "Status Unknown". The current mana levels of all blocks now automatically update.
* Cocoons can now be forced to spawn rare mobs using Gaia Spirits
* Fix rannuncarpus crashing when placing certain blocks
* Fix some mechanisms like the Force Relay/Lens allowing you to move waterlogged blocks
* Fix minecart sounds being silent when Botania is installed
* Fix Life Aggregator not placing down spawners correctly

---

{% include changelog_header.html version="r1.11 378" %}

* Fix narslimmus eating spawner-spawned slimes
* Fix exoflames trying to smelt unsmeltable items in some cases
* Hydroangeas now show a durability bar for their decay time
* Fix agricarnation not speeding up sweet berries and bamboo
* Fix a crash in the Platforms
* Cocoon-spawned entities no longer despawn
* Fix Loki Ring being completely broken. There are still some edge-cases left but it should work mostly.
* Loki Ring grid now keeps the center position highlighted in bold for consistency with the building phase
* Replace slots in the Trinket Case texture with a player render (what else would I put there?)
* (Internal) Move recipe jsons to data generator
* Remove old Thaumcraft recipes and integration pages (F to pay respects)
* Fix Rod of Plentiful Mantle showing same color particles for all ores
* Add temporary recipes so the Botania banner patterns are accessible again
* Azulejo can now be crafted with any blue dye
* New Advancement - Hand in Hand - place over 255 blocks at once with a ring of loki
* New Hidden Advancement - That's Why I Baked Bread. Try to figure this one out! It shouldn't be very hard if you know the Botania memes.
* Fix non-English advancement names being broken
* Remove a bunch of dead lang entries
* Fix a bunch of animations stopping and/or jittering when the pause menu is open (even on servers)
* Fix force relays not moving properly when retracted indirectly using slime blocks
* Fix at least one known case of the "chunk loses all light" vanilla bug happening
* Lexicon typo fixes and tweaks (Alwinfy)
* Temporary fix for force relays in non-overworld dimensions not saving

---

{% include changelog_header.html version="r1.11 377" %}

* Fix cell blocks not respecting red string spoofers
* Potential fix for lighting issues. Please reset/relight any affected areas and continue monitoring.
* Fix desu gun advancement requirements not being consistent with the name requirements
* Fix desu gun model not using the handheld transforms
* Fix mossifying an apothecary losing the fluid inside
* Delay burst entity creation until we're sure there's enough mana, should improve performance
* Entropic and Damaging bursts fired by a player now deal player damage
* Add Heart of the Sea recipe for GoG (ToMe25)
* Allow T.A. Plate to be built with shimmerrock
* Readd lexicon konami code Easter egg
* Add mana infusion recipe for sweet berries (ToMe25)
* Fix terra blade burst logic error
* Fix wand of the forest recipe spam
* Fix dispensers not planting cocoa beans properly
* Fix more cases where warped bursts transfer mana
* Fix the flugel tiara flight meter draining in spectator and creative
* Fix redirective lens pointing spreaders to 0, 0

---

{% include changelog_header.html version="r1.11 376" %}

* Big round of bugfixes and enhancements. 1.15 progress can be tracked on github, and we are close!
* Snowflake pendant now has a snow effect (ToMe25)
* Fix Rod of Plentiful Mantle's particles being blocked by other blocks
* Fix Rannuncarpus slowing down as available positions decrease
* Add GoG-specific Orechid entry
* Show creative tablets and tipped terra shatterer in JEI
* Fix GoG seed drops not working and move them to a loot table
* Sleep check event now fired with side (stfwi)
* Fix Tangleberrie not pushing back strong enough
* Add some block tags and change some recipes to use them (ToMe25)
* Fix botania axes being unable to strip logs
* Add stonecutting recipes to convert between Azulejos
* Fix GL state leak in pylon renders
* Check packet directions
* Ensure lexicon alfportal recipe is visible in JEI
* Fix orechid ignem JEI not working
* Fix JEI for Botania stuff not working until you F3+T
* Fix Chakrams being invisible
* Fix Terra Blade not damaging mobs if PVP is off
* Cocoon of Caprice now spawns aquatic mobs separately if it is adjacent to water, spawned mobs are placed in the water
* Fix HUD bars (Tiara, etc) being weird
* Fix missing cobblestone pure daisy recipe
* Fix Rod of Shaded Mesa not launching entities
* Orechid now filters out empty tags before picking one
* Restore some orechid compatibility with other mods
* Ring of Dexterous Motion can now be used in forward and backward directions
* Flugel Tiara gliding now interacts properly with the Tornado Rod
* Fix a bunch of lexicon formatting errors in non-English languages
* Call for contribution: the lexicon has changed quite a bit and some of the translations are getting quite a bit behind. If you can speak English and a non-English language we welcome your translation efforts. Contact us on Discord or Github for further information.

---

{% include changelog_header.html version="r1.11 375" %}

* Hotfix: Fix botania-skyblock-spread applying to the player running the command, not the argument (also fixes it not working in command blocks)

---

{% include changelog_header.html version="r1.11 374" %}

* Fix mana mirror counting as a tool (thus being enchantable)
* Fix crash when shift clicking into flower bag (Vaelzan)
* Fix GL state leak when rendering particles, fixes darkened water/etc
* Fix non-overworld dimensions also being voided in GoG mode
* Add option to disable the lexicon given by default in GoG mode
* Fix flugel tiara gliding
* Fix flugel tiara recipe conflicts
* Fix terra firma rod not working
* Make paintslinger lens able to dye concrete, concrete powder, glazed terracotta, as well as uncolored glass, glass pane, and terracotta (ToMe25)
* Fix cocoon of caprice not working
* Lexica Botania english text overhaul (Alwinfy)
* Fix axe damage and shovel speed (ToMe25)
* GoG worlds now use a dummied-out overworld generator, meaning they now retain biome and structure information. This will allow location-based farms to work.
* Fix laputa shard upgrade recipes
* Allow any two vanilla dyes to be used to craft fertilizer instead of just red or yellow
* Add some new 1.13-14 stuff to mana alchemy recipe loops
* Make new mobs available in cocoon
* Add a bunch of crosslinks to the lexicon since Patchouli supports them (Alwinfy)
* Fix brews not returning the container after finishing
* Fix brew vials looking like flasks when partially full
* Fix mana infusion slowing down after a while
* Fix cacophonium acting like a shield and snowflake pendant working when sneaking (ToMe25)
* Add livingwood and dreamwood fences and fencegates (ToMe25)
* Fix pure daisy active particles desyncing

---

{% include changelog_header.html version="r1.11 373" %}

* Fix Life Aggregator placing pig spawners instead of the actual mob
* Fix mana flame block being unnamed
* (!) Fix loot tables not applying properly, which affects downstream mods like CraftTweaker
* Corporea keybind requests no longer uses chat
* Corporea keybind requests can now use the vanilla recipe book
* Fix mana/elven glass having weird culling
* Fix red string blocks not rendering their effect when holding a wand
* Fix various bugs with flowers on red string spoofers (not rendering, particles in the wrong place, etc.)
* Fix thermalilies not saving their cooldown when broken
* Fix various items being missing from the creative tab

---

{% include changelog_header.html version="r1.11 372" %}

* Welcome to Botania on Minecraft 1.14.x, almost an entire year in the making (efforts began around January for 1.13)
* Shoutout to williewillus and Hubry for contributing significant chunks of effort to this port
* Port to 1.14.4. All older worlds should be treated as incompatible, though it is possible items in chests can be carried forward.
* Completely replace the old Lexica Botania with a book made with Vazkii's Patchouli mod
* Replace Baubles integration with Curios integration
* Spectrolus has a new mysterious feature...
* Mana sparks can now be colored like corporea sparks to create separate networks
* Stonecutting recipes for all Botania stone-like blocks
* Tiny potato petting/jumping now show to others in multiplayer (very important)
* Force relays now show a particle beam when being bound, as well as their existing binding if clicked in function mode
* Radius highlights are now visible from below, useful for floating flowers
* Rework Ring of Chordata for 1.13 swimming changes
* [API NOTE] Modders: The API is not fully stable and breaking changes may still occur, as it is unknown how JAR-in-JAR or api distribution will work in 1.13+. Create addons at your own risk (join #modders in Vazkii's Discord to stay in touch with us).
* Known Issue: Various cosmetics and on-body renders are positioned incorrectly
* Known Issue: Lenses do not render on the Mana Blaster
* Known Issue: Miscellaneous things in the Lexicon are missing or inconsistent
* Known Issue: Double flowers can be collected without shears
* Known Issue: Botania shears don't act as such

---

{% include changelog_header.html version="r1.10 364" %}

* This one-off **1.12.2 backport release** is dedicated to the users and the developer of the Floramancer modpack.
* Change: Cache lookups for Corporea sparks on inventories, *massively* increasing runtime performance of large Corporea networks
* Change: The invalid flower advancement no longer asks to report it
* Change: Rods can now be put into the Bauble Case (from 1.16.5-415)
* Change: Obedience stick is now usable from a dispenser (from 1.16.3-407)
* Change: Clean up JEI integration layouts (centered things properly, etc.) (from 1.16-400)
* Change: Rod of the Unstable Reservoir targetting tweaks (from 1.16-398)
* Fix: Avatar and life imbuer wasting a lot of mana (from 1.16.3-407)

---

{% include changelog_header.html version="r1.10 363" %}

* This is the final release for Minecraft 1.12.x. Thanks for all the support!
* Diva Charm no longer brainwashes boss mobs
* Remove GregTech blacklist from Orechid since it's no longer necessary
* Fix Albedo compatibility (Hubry)
* Fix shedding page crash (Hubry)
* Fix error when equipping Botania baubles over other new Baubles (Hubry)
* Fix lexicon sharing crashing when in off hand (Hubry)
* Add Quartz blocks to Ore Dictionary (nekosune)

---

{% include changelog_header.html version="r1.10 362" %}

* When matching recipe tags, also match the first items of nested lists. (codewarrior0)
* Fix magnet ring working in spectator mode
* Made Gaia Guardian floor check a bit more lenient. (quat)
* Gaia arena check now shows where pylons should be. (quat)
* Fix armor exploit with Astral Sorcery and Monk. (MoriyaShiine)
* Magnet ring respects PreventRemoteMovement tag. (code500)
* Gaia Guardian break blacklist now also serves as an arena whitelist. This fixes Natural Pledge. (quat)
* Fix Flugel Tiara's secret code not working on Linux.
* ru_ru language fixes. (Ggglitch)
* Add hook for hidden lexicon categories. (Hubry)
* Fix patron headflowers showing when invisible.

---

{% include changelog_header.html version="r1.10 361" %}

* Corporea cubes can now be locked (similar to the Hovering Hourglass) to prevent accidental changing of the item. (Hubry)
* Fixed Daffomill desyncing clientside when items are dropped by powered open crates.
* Fixed Drum of the Wild always choosing the same positions to harvest.
* Fixed magnet ring blacklist not working quite right.
* Fixed placeholders being consumed by crafty crates.
* Fixed server crashing if a Mana Enchanter picks up modded enchantments that have been removed from the save.
* Fixed triggering a corporea funnel with a corporea interceptor overwriting request counts. (Hubry)
* Fixed wrong Baubles API method being used. (zabi94)
* Update ru_ru lang file. (iPopstop)
* Various Gaia Guardian anti-cheese measures. (quat)

---

{% include changelog_header.html version="r1.10 360" %}

* Fixed a crash on moving floating flowers with the static model config with quark's pistons. (Hubry)
* Fixed a dupe with shears.
* Fixed cloaks blocking /kill damage
* Fixed floating flowers with the static model config not showing special grass types. (Hubry)
* Fixed hoes not being usable on special grasses. (quat)
* Fixed hopperhocks pulling from the wrong sides. (quat)
* Fixed mushrooms being usable to craft petal blocks. (quat)
* Fixed pumpkins and melons not growing on special grasses. (quat)
* Fixed vine balls placing invalid vines. (quat)
* Made NBT matching faster and work better. (quat)

---

{% include changelog_header.html version="r1.10 359" %}

* Fix Lexica rotating item config not swapping usable items in recipes (Hubry)
* Fix mana prism automation changes not causing spreaders to recalculate
* Fix redirection lenses having phantom lens functionality as well (Hubry)
* Fixed a bunch of typos (trunksbomb)
* Fixed a dupe with spellbinding cloth
* Fixed botania shears not dropping apples and saplings
* Fixed Corporea Sparks not staying on Red Stringed Containers (Hubry)
* Fixed crafted flowers entering back the petal apothecary (quat)
* Fixed ender air bottles not being acquirable in custom end dimensions
* Fixed force relays not being bindable to lapis blocks
* Fixed spellbinding cloth deleting items
* Fixed the botanical brewery destroying invalid brew containers
* Fixed the red stringed spoofer not affecting the vinculotus
* Fixed the worldshaper's sextant being able to softlock the game if you look far enough
* Improved the detection for the heisei dream and the diva charm
* Made magnet ring respect pickup delays and tweak blacklist (Hubry)

---

{% include changelog_header.html version="r1.10 358" %}

* Added an Offline Mode config. (Hubry)
* All Garden of Glass features now depend on the GoG mod being loaded, rather than the GoG world type.
* Buffed the Spectrolus by 8x. (quat)
* Fixed Botania glasses not being usable with Chisels&Bits. (Hubry)
* Fixed horse viruses breaking attributes and turning horses into babies. (Hubry)
* Fixed the Bauble Case not supporting baubles that use capabilities.
* Fixed the Life Aggregator having the wrong tooltip. (Hubry)
* Optimized Mana Spreaders a bunch, they'll be more performant now. (quat)
* Removed a pointless bit of debug text.
* The gunpowder to flint recipe is now the same price as flint to gunpowder. (quat)
* The Petal Apothecary now accepts all items and can NBT match then. Rejoice pack makers! (quat)
* Tweaked Thaumcraft integration and aspects around a bunch. (Hubry)
* [API] Deprecated IFlowerComponent and made canFit() default to true. (quat)
* [API] Increased Version Number to 92.

---

{% include changelog_header.html version="r1.10 357" %}

* Brought back BuildCraft integration.
* Brought back the ability to request items from corporea systems in JEI (prev NEI). (Hubry)
* Fixed a crash when opening the Flower Pouch.
* Fixed a crash when putting things that shouldn't go in the Abstruse Platform in the Abstruse Platform.
* Fixed a dupe with the Bore Lens and Shulker Boxes.
* Fixed the Bore lens and some other items of the sort not respecting forge's drops event.
* Fixed the Tiny Potato's equipment not syncing to clients. (codewarrior0)
* Fixed withdrawing items from the Botanical Brewery taking two at a time.
* Force Relays now give some visual feedback when bound.
* Implemented profiler entries for flower sub-tiles.
* Improved handing of Brewing recipes in JEI. (Hubry)
* Made the Fallen Kanade not heal you during Gaia Guardian fights. (codewarrior0)
* Metamorphic Stones can now be silk-dropped by the Weight Lens.
* Optimized the Pure Daisy's code a bit.

---

{% include changelog_header.html version="r1.10 356" %}

* Fixed Elven Knowledge not being unlockable because you wouldn't get the book back.
* Fixed the Gaia Guardian not dropping the right amount of Gaia Spirits if killed by a bow.

---

{% include changelog_header.html version="r1.10 355" %}

* Added a Thinking Hand cosmetic bauble (quat)
* Added back the ability to request items from the Corporea Index with a keybind from JEI. (Hubry)
* Added JEI support for the Orechid and Orechid Ignem. (codewarrior0)
* Added Thaumcraft Aspects for all major Botania base things. (Hubry)
* Added the ability to request a nice amount of items from the Coporea Index. (quat)
* Corporea Retainers use the same logarithmic signal scale as Crystal Cubes.
* Fixed a crash when creating Terrasteel. 
* Fixed a crash when trying to activate the elven portal with a dispenser.
* Fixed a crash with other modders rendering tooltips before a world exists.
* Fixed a network crash. (codewarrior0)
* Fixed Elementium tools being repaired with Manasteel instead of Elementium. (ALongStringOfNumbers)
* Fixed Googly Eyes rendering inside the player's head. (quat)
* Fixed some typos.
* Fixed Sparks' vertical distance being inconsistent between the up and down directions. 
* Fixed the Alfheim Portal storing useless items internally. (Hubry)
* Fixed the Entropynnium absorbing multiple TNTs at once.
* Fixed the Gaia Guardian being immune to /kill.
* Fixed the Manufactory Halo crashing the game if you use a Thaumcraft Thaumatorium. (Hubry)
* Fixed the Orechid misbehaving around Granite, Andesite, and Diorite.
* Fixed the Tainted Blood Pendant with Night Vision causing horrible flickering. (codycoolwaffle)
* Flowers added by other mods will now show the name of the mod that adds them in the tooltip instead of Botania if you have a mod that adds that feature.
* Going under sea level in Garden of Glass will no longer make the sky black. (Xaphiosis)
* Llamas and parrots can now spawn from the Cocoon of Caprice. (Hubry)
* Made some internal bounding boxes smaller so which improves performance a tiny bit. 
* Removed Railcraft support for the Mana Poll with Minecart because the API for it isn't stable yet and was causing crashes. (matthijsvdmeulen)
* Rewrote a bit of internal Gaia Guardian logic.
* The Gaia Guardian's music now repeats while the battle goes on.
* Tiny Potatoes being held by other Tiny Potatoes look better now. Very important fix. (codewarrior0)
* Updated a bunch of recipes to use ore dictionary tags. (Hubry)
* Updated Thaumcraft Integration for TC6. (Hubry)
* [API] Added in a bunch of hooks for corporea stuff. (quat)
* [API] Updated the wiki links to new ones. (Hubry)
* [API] Increased Version Number to 91.

---

{% include changelog_header.html version="r1.10 354" %}

* Added a config to disable botania's F3 info.
* Fixed baubles disappearing when they're right clicked with one already equipped.
* Fixed chisels&bits complaining about some blocks not being chisel-able.
* Fixed crashing with Thaumcraft 6.
* Fixed feather feet increasing fall distance.
* Fixed JEI integration for floating special flowers.
* Fixed mana bursts not being properly blacklisted from the Project-E interdiction torch.
* Fixed munchdews going on cooldown too often.
* Fixed particles snapping into place.
* Fixed some flower particles not accounting for the vanilla random flower shift.
* Fixed some petal apothecary crashes.
* Fixed sparks not transfering mana properly to pools with mana void.
* Fixed the functional flower advancement having a broken icon.
* Fixed the tiny potato banner not being craftable.
* Fixed thermalilies not penalizing if lava is given on cooldown.
* Improved model loading performance for special flowers.
* Mana blaster bursts now spawn off the correct hand.
* Mana spreaders can now be rotated properly in structures.
* The extrapolated bucket can now pick up mod fluids.
* The Life Agreggator now uses the vanilla cooldown system.
* [API] Fixed a dependency on the mod itself.
* [API] Increased Version Number to 90.

---

{% include changelog_header.html version="r1.10 353" %}

* Blazes spawned from a Fel Pumpkin now get their drops from a loot table.
* Changed a bunch of stuff internally that you won't really notice.
* Detector rails can now accept a signal from mana pool minecarts.
* Fixed a crash when unequipping active baubles.
* Fixed a crash while holding the Lexica Botania and the sneak key.
* Fixed Blaze Meshes turning into Blaze Powder instead of Blaze Rods. (TheWhiteWolves)
* Fixed Hopperhocks not sorting properly with quark variant chests. (quat)
* Fixed Loonium spawning creepers with infinitely long potion effects that are propagated to the player.
* Fixed mana pools with sparks constantly sending network packets if there's nothing going on.
* Fixed Mana Splitters and Manastars loading chunks when placed in chunk borders.
* Fixed some Portuguese Pavement double slabs having the wrong color.
* Fixed the animated torch not causing block updates in some cases.
* Fixed the config GUI being broken.
* Fixed the crafty crate textures for the 3x2 and 2x3 patterns being the same. (quat)
* Fixed the flugel tiara flight bar conflicting with the breath indicator while under the effect of a water breathing potion.
* Fixed the Hovering Hourglass sending a needless amount of network packets and causing needless chunk redraws.
* Fixed the lexica recipe pages for toggle and fork luminizer being switched. (quat)
* Fixed the Planestrider's Sash recipe not being shown properly. (TheWhiteWolves)
* Hid the helments of revealing from the recipe book since TC isn't here yet.
* Improved network usage by using vanilla block events where applicable.
* Made the Tainted Blood Pendant update the potion effect sooner to prevent possible network desync.
* Removed the tickable property from some tile entities for optimization.
* The Manastar now uses server side information.
* The Ring of Far Reach now uses the new forge reach attribute instead of being a massive hack.
* Updated TC interface references to the new lowercase mod ID in case it updates anytime soon.
* Updated the baubles dependency to the latest version, fixing crashes when used with it.
* [API] Pure Daisy recipe inputs can now be state sensitive.
* [API] Increased Version Number to 89.

---

{% include changelog_header.html version="r1.10 352" %}

* **Another big round of fixes provided by williewillus**
* Fixed a division by zero crash when entering some entries with bad recipes.
* Fixed a potential race condition in corporea indexes, which would cause them to not function properly.
* Fixed a potential race condition in the gaia guardian boss bar, which would cause it to not function properly.
* Fixed manastorm charge and corporea index star animations being choppy.
* Fixed Manastorm Charges not giving mana.
* Fixed pools and spreaders sometimes not getting removed from the network properly.
* Fixed pressing any key twice while strafing left or right causing the ring of dexterous motion to activate.
* Fixed requesting items from a corporea index showing the incorrect amount of items in the chat.
* Fixed step assist height being incorrect. (InsomniaKitten)
* Fixed the loonium not drawing items properly out of the loot table.
* Improved the way advancements work by using actual data-driven triggers.
* Removed DSU integration since MFR isn't around.
* Removed reference to the scrapped BotaniaV2 in the mcmod.info file.

---

{% include changelog_header.html version="r1.10 351" %}

* Absolutely nothing because I'm stupid and forgot to pull the changes from the repository before building \:D/

---

{% include changelog_header.html version="r1.10 350" %}

* Added beetroot to the full crop farm challenge text. (codewarrior0)
* Fixed a crash when moving a mana pool with a quark piston.
* Fixed botania armor having twice as much armor as it should have.
* Fixed botania chiseled quartz slabs not being craftable.
* Fixed shaders loading twice on startup.
* Fixed the rannuncarpus eating tile entity data, such as shulker boxes.
* Fixed the ring of far reach desyncing blocks.
* Fixed the Worldshaper's Astrolabe being able to place blocks outside the world border.
* The Gourmaryllis now has audiovisual feedback based on the food it's eating. (quat)

---

{% include changelog_header.html version="r1.10 349" %}

* Botania shaders will no longer turn off if Albedo is loaded, so you can use it without compromising on visual effects.
* Fixed another startup crash. Hopefully, because I couldn't reproduce it.

---

{% include changelog_header.html version="r1.10 348" %}

* Added new Azulejo variants.
* Added the Shulk Me Not, a new endgame generating flower! *Only took me almost 2 years!*
* Added the Shulk Me Not to the challenges as a Hard level challenge.
* Changed the recipe for the Cocoon of Caprice.
* Daffomills and Animated Torches will now face away from the player when placed. (quat)
* Fixed a crash when using Red String.
* Fixed a server crash when using the Mana in a Bottle.
* Fixed Dreadthornes killing baby animals instead of adult animals.
* Fixed Hopperhocks never running out of mana, even if their source is removed. (codycoolwaffle)
* Fixed ladders, fences, and other stuff connecting to a lot of botania blocks they shouldn't connect to.
* Fixed mana bursts piling up and causing horrible lag.
* Fixed Mushrooms not fitting in the Petal Apothecary.
* Fixed some weird crash on startup only some people were able to reproduce? I think I did.
* Fixed switching a Corporea Spark to another network causing it to not connect until the chunk is reloaded.
* Fixed the Corporea Crystal Cube not rendering the item inside.
* Fixed the Lexica Botania not calling forge tooltip events in its item tooltips, causing mods like Quark to not show their stuff.
* Fixed the Mana Fluxfield not reporting its max energy properly. (Infinoid)
* Fixed the Matrix Wings.
* Fixed the Open Crate delaying item pickup even if not powered.
* Fixed the Petal Apothecary not having any sparkles when there's stuff inside.
* Fixed the Ring of Loki not offsetting the mining with the Terra Shatterer properly.
* Fixed the Third Eye not highlighting all mobs properly.
* Fixed the Thorn Chakram being able to be killed by explosions.
* Made the Cocoon of Caprice accept dropped items as modifiers.
* Re-enabled the tutorial video in the lexica botania, using the new <a href="https://www.youtube.com/watch?v=D75Aad-5QgQ">Bit by Bit video</a> from Mischief of Mice. *Only took me almost 2 years!*
* Removed references to the previously removed Ring of the Aesir.
* Removed the version checker and ingame updater because it's 2017 and nobody needs those.
* The Lexica Botania can now swap between items in recipes that have multiple valid items in one slot (like ore dictionary recipes). *Only took me 3 and a half years!*
* The Lexica Botania will now have different textures before elven knowledge is unlocked.
* The Rune Crafter challenge is now categorized as "Lunatic+" instead of "Lunatic" because it should probably stand apart from the others.
* Updated a lot more textures and models and stuff to the new art style.

---

{% include changelog_header.html version="r1.10 347" %}

* Fixed crashes when viewing the Dandelifeon and Elven Mana Lenses pages.
* Updated more textures and models to the new style.

---

{% include changelog_header.html version="r1.10 346" %}

* **Updated to Minecraft 1.12.1** (williewillus)
* **Retextured and remodeled a major part of the mod with a new art style by wiiv.**
* Added support for <a href="https://twitter.com/Vazkii/status/904017697775767552">colored lights</a> with the <a href="https://minecraft.curseforge.com/projects/albedo">Albedo</a> mod.
* Buffed the mana production of the Gourmaryllis by a bit.
* Buffed the mana production of the Narslimmus by a lot.
* Fixed a crash when clicking an Abstruse Platform disguised as a Chisel mod block. (williewillus)
* Fixed being able to skip the Gaia Guardian mob wave by healing it. (williewillus)
* Fixed particle motion having a bias for diagonals. (codewarrior0)
* Fixed the Dreadthorne killing baby animals from the Animania mod. (williewillus)
* Fixed the Eye of the Flugel animation and particles and made binding less intrusive. (williewillus)
* Moved from Achievements to Advancements. (williewillus)
* Relics now bind by UUID instead of names. (williewillus)
* Removed the Ring of the Aesir.
* Renamed the Blaze Lamp and Mana Distributor to Blaze Mesh and Mana Splitter, respectively.
* Shaders now automatically disable themselves if Albedo or Optifine are loaded to prevent OpenGL errors.
* Shimmering Mushrooms can now be used as if they were petals.
* The Dice of Fate will no longer fizzle if there are still relics to be given.
* The Gourmaryllis is now capped at 12 food points (6 chicken legs), any food higher than that is counted as 12.
* The Gourmaryllis will now take diminishing returns if it eats the same type of food item multiple times in a row.
* The Munchdew now has visual feedback while it's on cooldown. (williewillus)
* The Rafflowsia can now eat any flower, not just generating ones. (williewillus)
* [API] Added receiveClientEvent() to SubTileEntity as a hook for TE client events. (williewillus)
* [API] Increased version number to 88. I think a few were skipped in the changelog >_>

---

{% include changelog_header.html version="r1.9 345" %}

* Fixed a bad pixel in the checkmark for mana infision.
* Fixed a dupe when interacting with sparks just as they're leaving the world.
* Fixed Managlass and Alfglass transparency (palaster).
* Fixed the Pure Daisy running its logic client sided as well.

---

{% include changelog_header.html version="r1.9 344" %}

* Fixed a lava dupe with the Petal Apothecary.
* Fixed a Mana Enchanter game freeze.
* Fixed an item dupe when dropping items in the edges of a Mana Pool.
* Fixed double plants not being randomly shifted like vanilla ones.
* Fixed the Mana Clip showing Air for empty slots.
* Fixed the Ring of Loki's wireframes not working.
* Updated Storage Drawers integration (palaster)
* *More williewilus builds up in here!*

---

{% include changelog_header.html version="r1.9 343" %}

* Fixed a crash with the Marimorphosis.
* Fixed baubles equipped by right click destroying items already in their slots.
* Fixed botania armor sets animating on armor stands.
* Fixed FPS lag when holding the Wand of the Forest.
* Fixed right click to refill not working in the Petal Apothecary.
* Fixed right clicking the Mana Enchanter not doing the item's action even if there's nothing the item can do.
* Fixed shimmering mushrooms popping off at strange times.
* Fixed some log errors.
* Fixed the animated torch not animating smoothly when toggled serverside.
* Fixed the Overload brew being nerfed by the 1.9 update.
* Fixed the Wand of the Forest being able to rotate command blocks in survival.
* Fixed the Wand of the Forest changing mode when rotating blocks.
* Made some rune recipes use the ore dictionary.
* Mod flowers' bounding boxes now match their shift, like vanilla flowers.
* The Lexica Botania now shows the keybind for sneaking rather than always Shift.
* *This build is all williewillus, again!*

---

{% include changelog_header.html version="r1.9 342" %}

* **Updated the mod to 1.11.2**
* Removed the Golden Laurel Crown, as it overlaps with the Totem of Undying.
* Using Chorus Fruit on a Cocoon of Caprice allows it to spawn shulkers.
* *Yet another build provided by williewillus :D*

---

{% include changelog_header.html version="r1.9 341" %}

* **Unless otherwise stated, this entire update was kindly provided by williewillus' assistance.**
* Fixed a crash when placing pistons with a arannuncarpus.
* Fixed a crash with the orechid when an ore is registered to the ore dictionary that isn't a block. (smbarbour)
* Fixed AoE mining not firing break events and not dropping xp.
* Fixed body and leg armor stats being swapped.
* Fixed crashes with the drum of the gathering and the mod's shears.
* Fixed the botania banner lexicon entry still requiring EtFuturum.
* Fixed the crystal bow not using mana.
* Fixed the gaia guardian not looking like the player in dedicated servers.
* Fixed the horn of the wild, bore lens, and rod of the shifting crust manually dropping block drops and not allowing for block rollbacks.
* Fixed the marimorphosis running clientside and desyncing.
* Fixed the soulscribe not dealing extra damage.
* Fixed the spectator not highlighting all relevant inventories.
* Removed lexicon references to blocks that have been removed or moved to quark.

---

{% include changelog_header.html version="r1.9 340" %}

* Fixed a crash regarding tile entities and block states not playing together. (williewillus)
* Fixed crash on unequipping the Ring of Loki.
* Fixed the Crafty Crate destroying items if there's a block under it.
* Fixed the Gourmaryllis spamming packets and causing ridiculous network usage. (williewillus)
* Fixed the Key of the King's Law stacking portals together if the player is looking straight down.
* Fixed the Life Imbuer spawning mobs inside blocks. (williewillus)
* Fixed the livingwood bow not using 1.9 arrow priority and using arrows even if they have infinity.
* Fixed the Mana Enchanter not accepting floating flowers.
* Fixed the Ring of Loki checking your offhand item and thus negating the cursor addition if you have no item in either hand.
* Fixed the T.A. Plate not showing particles. (williewillus)
* The Gourmaryllis now plays a burp sound when it's done eating. (williewillus)

---

{% include changelog_header.html version="r1.9 339" %}

* Fixed a crash if a burst from a Manastorm Charge hits a mana container block.
* Fixed a potential crash when crafting with baubles.
* Minor text fixes.

---

{% include changelog_header.html version="r1.9 338" %}

* Fixed a crash when breaking redstone ore with an Elementium Pickaxe or Terra Shatterer with Elementium tips.
* Fixed a crash when using the Worldshaper's Astrolabe without a block set.
* The Lexica Botania can now always be thrown into an elven portal, even if it already has elven knowledge. (Shadowfacts)

---

{% include changelog_header.html version="r1.9 337" %}

* Fixed a client crash with the Worldshaper's Astrolabe.
* Fixed a crash when explosions happen in a server because someone at mojang thought having a client only constructor was a good idea.
* Garden of Glass's sea level is now set to 64 during world creation.  As of 1.8, the sea level is set to a really low value for flat chunk providers, so this fixes squids not spawning in garden of glass worlds. 

---

{% include changelog_header.html version="r1.9 336" %}

* Added an alchemy recipe for Chorus Flowers.
* Added grass paths, farmland and botania alt grasses to the blocks that can be dug for pebbles in garden of glass.
* Added the Benevolent Goddess' Charm, it stops nearby explosions from breaking blocks.
* Added the Worldshaper's Astrolabe, a new fancy tool to place a lot of blocks really quick.
* Changed the crafting recipe for Cellular Blocks to include Beetroot.
* Changed the secret code for the <span class="censored">------ -----</span>.
* Fixed a texture being poorly sized and breaking mipmapping.
* Fixed log spam when the Loonium is doing its thing.
* Fixed Resolute Ivy not working.
* Fixed the Invisibility Cloak not using up mana properly.
* Lowered how much the Dandelifeon will produce per iteration, but increased the maximum mana iteration to 100 (from 60).
* Mana Pumps will now output a comparator signal if they're turned off.
* Minecarts with Mana Pools now have the correct amount of drag.
* Removed the WIP page from the Dandelifeon page. Balance is still up in the air but I don't think I'll be changing mechanics now.
* There is no longer a limit in how many iterations a Dandelifeon can do, but the amount of mana a cell produces peaks at 100.
* Updated the Gourmaryllis' documentation to be clearer.

---

{% include changelog_header.html version="r1.9 335" %}

* Fixed the Terra Shatterer and other AoE tools not breaking some mod blocks properly due to removing the tile entity before capturing the drops. (iPencil)
* The Lexica Botania now autodetects your max gui scale and sets its allowed scale values accordingly.

---

{% include changelog_header.html version="r1.9 334" %}

* **WARNING: If you have an enabled loonium in your world DISABLE IT before updating, as it can spawn mobs, including creepers, now.**
* Added a config option to disable shedding entirely. This improves load times by not checking every mob.
* Added the Cirrus and Nimbus Amulets, which give you double and triple jump, respectively.
* Added the Invisibility Cloak. It's pretty self explanatory.
* Added the Third Eye, a nwe body slot bauble that makes mobs spectral.
* Changed the recipes for the Pyroclast and Snowflake Pendants to use Mana Infused String.
* Fixed the floating flower cache log not being formatted properly.
* Fixed the teru teru bozu not resetting the rain time.
* Mana Bursts that have been teleported by a Warp Lens can no longer deliver mana to receiving blocks.
* Removed the ability for the Invisibility brew to be added to a Tainted Blood Pendant.
* The /botania-download-latest command can now be ran on singleplayer without cheats enabled.
* The Bauble Box and Corporea Retainer recipes now use the chest ore dictionary tag.
* The Loonium now spawns mobs that drop dungeon loot instead of spawning dungeon loot outright.
* The Manasteel and Elementium shovels can now make grass paths like vanilla shovels can. They can turn grass paths into farmland now.
* Updated the Force Relay lexicon entry to be less ambiguous.
* You can no longer summon a Guardian of Gaia if there's already one nearby.
* [API] Added functionality to ILensEffect to decide how much mana goes into an IManaReceiver.
* [API] Increased version number to 85.

---

{% include changelog_header.html version="r1.9 333" %}

* Added a mana infusion recipe for beetroot seeds.
* Added osmium from mekanism and black quartz from actually additions to the orechid table.
* Fixed a crash when mana pools are broken or removed from the world. Actually has to do with a bug in forge, so this is a bit of a band aid.
* Fixed mana tablets and mirrors not having their insides colorized properly. 
* Fixed the Godlen Laurel Crown destroying your amulet slot item rather than itself.
* Updated the reset step height for the sashes to the new step height in 1.8+. Fixes them breaking stairs made with chisels and bits.
* [API] Added oreOsmium and oreQuartzBlack to the orechid table.
* [API] Increased version number to 84.

---

{% include changelog_header.html version="r1.9 332" %}

* Fixed the manaseer monocle not working since its slot was changed. Turns out I had a lot of things that only cared about the first 4 slots.

---

{% include changelog_header.html version="r1.9 331" %}

* Added a null check to the Thorn Chakram to prevent it from crashing if the entity that threw it is unloaded.
* Added the Cloak of Balance, a new cloak bauble that splits damage between the target and the attacker.
* Fixed a crash when removing a Ring of the Aesir, Odin, or Tectonic Girdle. This is actually a baubles work and the fix here is a workaround. These items won't reset their state properly until it's fixed in baubles, but they at least won't crash.
* Fixed Infrangible Platforms being able to be exploded.
* Fixed munchdews eating leaves below them.
* Fixed the bauble box GUI not removing the bauble icons for the new slots. 
* Fixed the Key of the King's Law producing a bad shape when looking northeast.
* Fixed the Kindle lens not being able to close nether portals.
* Sylph wings on the Flugel Tiara now glow.
* Updated the documentation for the Cloaks of Judgement to reflect the proper slot.
* [API] Updated IManaDiscountArmor, ManaDiscountEvent, IManaProficiencyArmor, ManaItemHandler with ItemStack specific methods. (Flaxbeard)
* [API] Added ManaProficiencyEvent. (Flaxbeard)
* [API] Increased the version number to 83.

---

{% include changelog_header.html version="r1.9 330" %}

* Fixed some baubles not working after their slots were changed.

---

{% include changelog_header.html version="r1.9 329" %}

* Updated for the latest version of baubles. It's now required.
* Buffed the Great Fairy Ring's chance to spawn a pixie by a bunch.
* Changed the slots of a few of the mod's baubles to some of the new bauble slots. More baubles will come soon to fix the thin spread.
* Fixed a graphical attribute bleed in the Hovering Hourglass renderer. This should fix chisel blocks messing up, maybe.
* Fixed lexica gui elements breaking if you rescale it with the new button.
* Fixed Luminizers making players no-clip into the block below them.
* Fixed Luminizers not working.
* The Bauble Box now has all 7 bauble slots, the player render had to go through.

---

{% include changelog_header.html version="r1.9 328" %}

* Fixed Hydroangeas not being passive.
* The Lexica Botania now detects if it's in an Akashic Tome and updates its display name (in the cover and landing page) accordingly.
* Removed an .exe file that was used to compress the sprites and was mistakenly packaged in.

---

{% include changelog_header.html version="r1.9 327" %}

* Removed Mana Powder from the Hydorangeas recipe because I forgot to remove it before so I removed it now so you can actually do it earlygame :D

---

{% include changelog_header.html version="r1.9 326" %}

* Added the ability to use Mana Powder in the Hovering Hourglass to make it a counter.
* Added the Animated Torch, a new redstone helper block.
* Added the Fork Luminizer which allows for a fork in the luminizer path.
* Added the Mana Lens: Messenger, which carries a lot less mana but over a long distance and faster for when you need the burst but not the mana.
* Added the Mana Lens: Tripwire, which only allows the burst to fire if there's an entity in the burst's path.
* Added the Toggle Luminizer, which drops entities off right there if it has a redstone signal.
* Changed the Cloak of Virtue and Cloak of Sin's models and made them shine.
* Changed the floating flower models to a simpler design. This also makes them a lot less FPS intensive when used in large numbers.
* Fixed a flower dupe with the Flower Pouch.
* Fixed the command to download updates ingame not working.
* Fixed the Flugel Tiara breaking the lighting in other bauble renders.
* Fixed the Garden of Glass entry not showing up in the tutorial if it's loaded.
* Fixed the Thorn Chakram not rendering.
* Made the Gourmaryllis have an upper bound in how much mana it can make from a single piece of food.
* Made the Hovering Hourglass sand falling animation a lot smoother.
* Removed all needless metadata from Botania textures (of which there are a lot). Enjoy the smaller filesize. I'll probably fit it with some more music later on.
* Updated secret codes.
* [API] Added IHourglassTrigger, for blocks that have stuff happen when an adjacent Hovering Hourglass does something.
* [API] Added runBurstSimulation() to IManaSpreader.
* [API] Increased version number to 82.

---

{% include changelog_header.html version="r1.9 325" %}

* Fixed a crash when picking up a flower that was removed.
* Fixed the Rafflowsia being able to eat itself.
* [API] Added ManaItemsEvent (Flaxbeard)
* [API] Increased Version Number to 81.

---

{% include changelog_header.html version="r1.9 324" %}

* Added mana alchemy recipes for Prismarine Shards and Crystals if you're in Garden of Glass.
* Minor text fixes.
* r

---

{% include changelog_header.html version="r1.9 323" %}

* Fixed conflicting packet IDs.
* Fixed the endoflame recipe being impossible to make because I'm a nub and forgot to remove the Mana Powder.

---

{% include changelog_header.html version="r1.9 322" %}

* **Please read the notes at end of this changelog for some context and important information. Thanks.**
* Added a button to the Lexica Botania's landing page to rescale it without rescaling the rest of the GUI.
* Added the Ring of Dexterous Motion, a new bauble that allows you to dodge to the sides using the A and D keys.
* Attempting to summon a Gaia Guardian with an invalid arena now emits particles on the offending blocks.
* Buffed the gaia guardian's wither effect due to it being easier to dodge now.
* Changed the Lexica Botania's cover layout a bit. It now has a random quote on it. Quote pool will expand as I find more cool quotes.
* Changed the special flower breaking particles from the poppy particles to generic green particles like in Botania 1.7.10.
* Disabled Natural Shedding (by default, the config is still present and fully functional).
* Fix pylon and petal apothecary models not being placed properly in the inventory.
* Fixed a crash when placing slabs.
* Fixed a crash with the vitreous pickaxe.
* Fixed improper resolution for the flugel tiara wing sprites causing framedrops.
* Fixed Petal Blocks having no color.
* Fixed stack sizes not showing up in a lot of places, including the lexicon recipe pages and the mana pool HUD.
* Fixed the Lexica Botania's notes accepting localization keys and localizing them.
* Fixed the spreader wool sleeves not being slim fit on top and z-fighting on some sides.
* Fixed the Teru Teru Bozu not being able to create rain.
* Increased the amount of Mana required to open the Elven Portal.
* Made the Mod's armor bulkier to fix z-fighting with the new skin layers. This is a temporary solution until we can redo the armor.
* Managlass, Alfglass and Bifrost Blocks are now valid targets for Chisels&Bits.
* Moved Stone Transmutation to the main alchemy page since they're no longer new blocks.
* Moved the side buttons in the Lexica Botania's landing page to the bottom of the interface and changed them all to be green.
* Re-implemented the update checker and ingame downloader built in to Botania 1.7.10.
* Rebalanced the Gaia Guardian's health and damage cap to make more sense with the damage changes in 1.9.
* Removed avatar decoration on Vazkii's player because they weren't stylish enough.
* Removed Dayblooms and Nightshades. Endoflames no longer require Mana Powder.
* Removed mana loss from spreader to spreader.
* Removed Motif flowers.
* Removed Needless Fluff: Roof Tiles, Hellish, Soul, and Frost Bricks, Trodden Dirt, Unstable Blocks, and Signal Flares.
* Removed Primus Loci.
* Removed the passive decay config again.
* Removed the prismarine alchemy recipe.
* Removed the Pylons and Mana Pools from the Elven Portal multiblock. They can now be placed anywhere in an 11x11 area. You can also have more than 2 Pylons, if you desire.
* Removed Timless Ivy (needless because of the existance of Mending in vanilla).
* Replaced a lot of cringy memes and terribly placed references with some less cringy memes and terribly placed references.
* The Elven Portal now takes mana per transaction rather than per tick. It will also no longer close itself if there's no mana.
* The Gaia Guardian's attacks now display as an overlay in the world rather than as particles only.
* The Livingwood Avatar can now place down dirt using the Rod of the Lands.
* The Rosa Arcana can now get its XP from orbs in the world.
* The Tiny Potato can now have items given to it. Many of the "easter egg" names for it have been removed as you can replicate them with this feature.
* Updated Corporea textures and some those of some of the livingrock blocks.
* Updated the dispenser planting's entry and feature set. It now duplicates Quark's dispenser planting so it can plant Cocoa Beans as well. Quark's disabels itself if Botania is present.
* [API] Removed a bunch of stuff that is no longer used.
* [API] Added a getFacing() method to IAvatarTile, this is used for the Rod of the Lands.
* [API] Increased version number to 80.
* **This version is for Minecraft 1.10.2, and carries on from the latest release of <a href="https://minecraft.curseforge.com/projects/botania-unofficial">Botania Unofficial</a>.**
* **A massive thanks is due to <a href="https://twitter.com/williewillus">williewillus</a>, who took it upon himself to port and maintain the mod all the way through 1.8, 1.9 and to 1.10. Please go send him love and hugs, as us being here would not be possible without him.**
* **If you want to know what changed in between, have a look through the Releases page on <a href="https://github.com/williewillus/Botania/releases">williewillus/Botania.</a>**
* **This is the first release for 1.10.2, which sets up for further content expansion, while removing obsolete and bloated pieces of content. More content is planned and will be coming soon.**
* **You can mitigate your worlds from Botania Unofficial to Botania just fine, any of the removed pieces of content you had in your world will just disappear.**
* **A few pieces of content from Botania's 1.7.10 releases was removed in Botania Unofficial due to them being added to <a href="http://quark.vazkii.net/">Quark</a>, another mod by me which I highly recommend you install alongside Botania.**
* **If you were looking for BotaniaV2, most of that project was scrapped in favour of this update instead. I decided that instead of reinventing a mod that's already good, I'd use the concepts there to make a new mod instead, which I am planning, but want to get some new stuff onto Botania and Quark before I move onto that.**
* **And that's it for my important note. I'm back to Botania, and I hope you carry on enjoying it.**

---
---

## Botania Unofficial Era
During this era of Botania's life, the mod was ported externally by williewillus
(who was not yet a member of the team at the time), through Minecraft versions
1.8 up to 1.10.2, encompassing all versions after 249 up to and including 321.
This section of the page is an archive of the changelogs of those versions of Botania
Unofficial for posterity. Hope you enjoy the history!

{% include changelog_header.html version="r1.8 321" %}

Changes:
* Code/asset cleanups

Fix:
* Fix avatar and cocoon being invisible in inventory
* Fix bergamute making custom sound classes infinite loop (RFTools, Dynamic Surroundings)

---

{% include changelog_header.html version="r1.8 320" %}

This is getting ridiculous ><

Fixes:
* Crash when putting petal in petal apothecary
* Stars not showing in GoG skybox

---

{% include changelog_header.html version="r1.8 319" %}

Important: If you use Natural Pledge, be sure to update to Natural Pledge 1.5 or above!

Hotfix on the hotfix, ugh. See build 318/317 for full changelog.

Fix:
* Fix crash due to particels spawning serverside
* Fix Botania's banner patterns not showing up on shields (LGcommisar)
* Floral powder is now registered to oredict name "dye" (pau101)

Enjoy

---

{% include changelog_header.html version="r1.8 318" %}

Once again, use the next build :/

Important: If you use Natural Pledge, be sure to update to Natural Pledge 1.5 or above!

Hotfix. See previous build 317 for full release notes.

Fix:
* Gaia Guardian crashing with ConcurrentModificationException

---

{% include changelog_header.html version="r1.8 317" %}

Use next build pls

Important: If you use Natural Pledge, be sure to update to Natural Pledge 1.5 or above!

Small build, but school is starting tomorrow so I want to get this out before a drop in activity.

Java 8, Baubles, Forge 2053+, JEI 3.7.8

Changes:
* zh_CN lang updates (3TUSK)
* Lexicon picture for crafty crate shows updated Crafting Placeholder texture (Luxor5393)
* Internal cleanups
* Optimized floating flower model, it should render faster now when animated, I will continue to explore options to optimize the animated ones
* Also added a config option to render floating flowers statically. This means they don't animate but render not just faster but A LOT faster. This option makes them render as fast as normal blocks like fences.

Fixes:
* Fixed Bergamute having flower particle colour not matching what the flower looks like (this doesn't affect the particles it makes when it absorbs a sound)
* Fixed Bergamute not working on ticking sounds like minecarts
* Fix orechid running clientside and being weird
* Fix pixies despawning immediately clientside, meaning they were invisible and couldn't be attacked
* Fix pixies flying too slowly towards their targets
* Fix banner back-textures not being reversed like they should be

Enjoy!

---

{% include changelog_header.html version="r1.8 316" %}

new Build!

Java 8, Baubles, Forge 2053+, JEI 3.7.8

Changes:

* [API] Elven trade recipes now can output multiple (shadowfacts)
* Added a special tiny potato for me :D
* Bubbell now less laggy
* Magic flower recipes now show properly in JEI
* Mana blaster now shows the active lens on the barrel
* Corporea no longer runs clientside
* Mana Enchanter is now more lax about what can go on an item:
  Before, used Enchanting Table rules to determine if item can be enchanted, now uses anvil rules
  Lets you apply unbreaking to shields, etc.
* Remove vineball + cobble -> moss stone recipe (there's a cheaper vanilla one)
* Remove old tooltips from Coarse Dirt and Mob Spawner

Fixes:
* All special tiny potatoes should now render properly
* Fix CTRL+SHIFT crash for some blocks
* Fix dupe glitches due to old code assuming player only has one hand
* Fix shaded-mesa-thrown items not having proper pickup delay
* Fix crash when digging flowers
* Fix spark animation when requesting items from Corporea being missing
* Fix flare lens not working
* Fix comparator not working with corporea retainer
* Fix livingroot blocks looking weird in world
* Fix things like the mana tooltip bar not matching with the item's actual tooltip
* Fix Ender Hand being desync-y
* Fix some missing sideonly's causing crashes with Sponge or other reflecting mods

Enjoy!

---

{% include changelog_header.html version="r1.8 315" %}

Small build but again I need some fires to get put out

Java 8, JEI 3.7.8, Forge 2044+

IMPORTANT

* Fix blocks disappearing, transforming, or becoming missing models on chunk reload
* Forge fixed a pretty serious vanilla bug in the blockstate ID map in 2027, that a lot of mods were unintentionally relying upon. However, that fix was incomplete and was not completely resolved until 2044. If you are running the mod in a version in this range of buggy Forges from 2027-2044 (I'm looking at you Unstable >.>) be aware that I've had users report blocks disappearing or transforming when upgrading out of that range. You've been warned, and I'm very sorry for the bugs.

Changes:

* More json and model registration cleanup
* Lexicon 3D model is back! Looks a bit wonky in the left hand but works good enough
* Removed option to "disable 1.8 stones" (they were removed). The mana infusion recipes for the vanilla versions are now always-on
* Black Hole Talisman uses capabilities to deposit things.
* Version checker now just piggybacks off the Forge version checker
* Small performance boost to the very laggy floating flower rendering. A more effective solution is being worked on

Fixes:

* Fix said 1.8 stone mana infusion recipes acting weird in the presence of the Ore Dict

---

{% include changelog_header.html version="r1.8 314" %}

Small build, but necessary to put out some fires.

For 1.10.2 only!

Built on Java 8, Forge 2041 (anything above 2018 should work), JEI 3.7.8

Changes:

* Made a bunch of eventhandlers static. That probably means nothing to most of you, other than things will run negligibly faster :P
* The day is here: All blocks duplicated from vanilla/Quark have been removed. The nag is gone as well - you're welcome ;). PLEASE ensure you have heeded the nag if you've built with these blocks as they'll disappear!
* Added ManaDiscountEvent (Flaxbeard)
* Mana Enchanter costs more to apply treasure enchantments.
* Remove some log spam from Mana Enchanter.

Fixes:

* Fix blocks turning into other blocks when being placed (Forge fixed a vanilla bug I was unintentionally relying on)
* Fix cacophonium not working at all
* Fix slingshot not working properly
* Fix walls being flipped around in inventory
* model cleanups ongoing
* code cleanups as obfuscated lambdas work now

---

{% include changelog_header.html version="r1.8 313" %}

This will be the final build for 1.9.4.

Built on Java 8, Forge 1965, Baubles 1.2, and JEI 3.6

Changes:

* Blacklist TC aura nodes from Rod of Shaded Mesa
* Added the Bergamute, a sound muffler. Will be tweaked in the future (texture: Tiktalik)

Fixes:

* Fix two Rings of Chordata behaving weirdly (howtonotwin)
* Fix starcaller not working
* Fix halos crashing
* Fix rod of bifrost going batshit and generating thousands of chunks out
* Fix crash when using a Wand of the Forest on a Crafty Crate from a dispenser
* Fix sparks duping when they unload
* Fix bows being OP in 1.10
* Made secret feature X more reliable
* Fix some particle effects being missing
* Fix ancient wills not working

---

{% include changelog_header.html version="r1.8 312" %}

Small build for the update to 1.10.2

This jar supports both 1.9.4 and 1.10.2.

Not tested nor supported on 1.10.0 though it should in theory work fine there.

Built on Java 8, Forge 1965, Baubles 1.2, and JEI 3.6

Changes:

* Pick block for pool cart, sparks
* Can now fill an apothecary using a fluid cap supporting containers

Fixes:

* Fix burst ranges being much shorter than they should really be
* Fix magic flowers emitting light
* Fix glitch flower achievement showing a missing texture instead of poppy
* Fix desync when withdrawing from runic altar
* Fix drums having the wrong sound
* Fix weapons in Key of King's Law aiming weird
* Fix some items looking awkward in 3rd person

Enjoy!

---

{% include changelog_header.html version="r1.8 311" %}

This build _should_ work in BOTH 1.9.4 AND 1.10! Woohoo!

Main dev will continue on 1.9.4 but if you have 1.10 problems please let me know.

Built on Java 8, Forge 1965, Baubles 1.2, and JEI 3.6

Changes:
- Made compatible with MC 1.10
- Made Quats and Vector3's immutable (they were already being copied around like crazy)
- Gaia Guardian now spawns each players' drops at that player
- Gaia Guardian's magic landmines now deal magic damage
- Gaia Guardian now deals physical damage if it teleports on a path that goes through you
- Gaia Guardian can now be damaged by non-melee player damage sources. Damage cap still applies.
- Gaia Guardian can no longer be shoved when spawning
- Optimize Spectator network traffic even more
- Mana Enchanter no longer runs logic clientside
- The Soulscribe is only effective when 1.9 attack bar is fully charged

Fixes:
- Fix some broken color fading
- Fix flare chakrams reverting to normal ones on world reload / dimension change
- Fix flugel tiara conflicting with spectator mode
- Fix threading issues in special flower models
- Remove some checks that forge now has
- Fix TE's sending too much stuff to the client 
- Fix GG spawned mobs not getting fire resistance for as long as they should be
- Fix potion effects the GG removed not syncing to clients
- Fix format error in armor tooltips
- Fix items on runic altar being too far away and small
- Fix terrasteel pick tooltip darkening screen sometimes
- Fix some of the tools of the Key of King's Law bouncing back
- Fix bad portuguese brick stairs/slabs recipes
- Fix solegnolia not working on dedicated servers
- Fix elementium pick not voiding 1.8 stones

Enjoy!

---

{% include changelog_header.html version="r1.8 310" %}

I need sleep edition

Hotfix -.-

Fix:
- Fix crash with other mods
- Tweak some models
- Fix flowers popping off relay and mycelium

---

{% include changelog_header.html version="r1.8 309" %}

please use the next build due to a crash with other mods

New build!

Forge 1965, JEI 3.4+, Java 8, Baubles 1.1.2.0

Changes:
- Added support for Forge's lightweight update-checker system
- Gaia Head and Gaia Guardian now truly look like whoever's looking at it. Spoopy.
- [API] Lexicon recipe mappings now accept wildcard meta (yrsegal)
- Some technical blocks like lit redstone ore and lit redstone lamp now drop their non-technical counterpart (again?)
- Removed ColoredLights support (simply for clean code. will be readded if coloredlights ever updates)
- Lang file cleanup
- Apothecary now respects new fluid capability items (no, you can't pipe into them)
- Decrease log spam
- buff terrasteel armor slightly
- Some tweaks to early lexicon entries
- **new mana blaster model** (wiiv)
  - Not fully done - the eventual idea is that you can see the lens you have equipped on the gun and the backup lenses on the clip
  - No new fancy model for the desu gun - bug wiiv if you want that ;)
- fr_FR lang update (isambourgg)
- Mana sparks now run logic only on the server
- Gaia Head can now be worn
- Terra Blade can no longer be spam clicked (only fires mana burst when your 1.9 attack bar is full)

Fixes:
- Fix Gaia Head render looking weird
- Fix exploit with Mending spell cloths
- Fix bad spawnpoint being set in GoG
- Fix chakrams behaving weirdly due to desyncs
- Fix pool crafting particles not quite matching 1.7
- Fix BH talisman consuming blocks even when placement was invalid (howtonotwin)
- Fix minor blending issue in lexicon
- Fix some armor being too strong
- Fix crash in assembly halo, and tweak the visuals in item/block rendering slightly
- Fix contributors that have a addon-added flower showing missing model when that addon is absent
- Fix helmets looking strange on armorstands
- Fix floating flowers looking weird in inventory
- Fix Baubles looking wrong when sneaking (yrsegal)
- Fix Bauble renders (yrsegal)
- Fix flower pouch and bauble box crashing in offhand
- Fix crafty crate creating ghost items
- Fix pylons being shifted in inventory
- Fix buffer overflow when too many lightning particles are being rendered
- Fix tooltip crash with some keybindings
- Fix crash with overgrowth seed
- Fix solegnolia having a lower range than shown

---

{% include changelog_header.html version="r1.8 308" %}

Hotfix

Fix:
- Fix chest carts being wiped and loot regenerated on open due to a left-in debug statement

See previous build notes for more info 

---

{% include changelog_header.html version="r1.8 307" %}

Quick bugfix build for 1.9.4.

Req: Forge 1922+, Baubles 1.2.1.0+, Java 8

Opt: JEI 3.4+

 IMPORTANT: Do NOT open your minecart chests in this or any earlier 1.9.x version! I accidentally left debug code active that deletes everything inside and respawns the loot. A hotfix is now out (build 308)

Changes:
- Lang updates for ja_JP, zh_TW
- Mana pools now try catalyst recipes first before noncatalyst ones. Fixes not being able to register infusion recipes that match one of Botania's but adds a catalyst.

Fixes:
- Actually fix the log spam from last time
- Fix TE's not syncing to clientside properly
- Don't reinvent the wheel with packet syncing
- Fix mana cookie lasting 1 tick instead of 1 second (yrsegal)
- Fix flower pouches not playing sound and animation on pickup
- Workaround for particles being culled at render distance for starfield, beacon, flares - the particles just spawn at the edge of the player's render distance, whatever that may be
- Fix crash with VarInt bits
- Cleanup some deprecated stuff
- Fix ender air bottle affecting too few blocks
- Fix axes not digging fast
- Cleanups

---

{% include changelog_header.html version="r1.8 306" %}

We're on 1.9.4 now!

Req: Forge 1909+, Baubles 1.2.1.0+, Java 8

Opt: JEI 3.4+

Changes:
- Update to 1.9.4
- API: Move IManaCollisionGhost to be on Blocks instead of Tiles

Fixes:
- Fix some log spam

---

{% include changelog_header.html version="r1.8 305" %}

(Most likely) the last 1.9.0 build 

Req: Forge 1901+, Java 8, Baubles 1.2.0.0+

Opt: JEI 3.3.3+

Changes:
- Fix botania dungeon loot
- Loonium subtile NBT now has a field for a custom loot table (the item form doesn't have this yet)
- Optimize network traffic for The Spectator heavily
- Make clientside boss collection weak to protect against memory leaks
- Many flowers no longer run extraneous logic clientside
- Minor memory optimizations

Fixes:
- Fix petal burying crashing
- Fix hopperhock not working on vanilla single chests
- Fix picking root block giving missing texture item
- Fix terra axe icon not changing when disabled by temperance / crouching
- Fix GOG pebble-ing not working on a dedicated server
- Fix dirt path not having proper bounding box
- Fix binding spreader to something with no bounding box crashing

---

{% include changelog_header.html version="r1.8 304" %}

Changes:
- Fix server crash on startup

See previous changelog for detailed changes

---

{% include changelog_header.html version="r1.8 303" %}

First 1.9 curse build!

Requires Forge 1898 (VERY recent at time of writing!) and Java 8

Built on JEI 3.3.3 and Baubles 1.2.0.0

Changes:
- Added subtitles for Botania sounds
- Some balance changes
  - Glass pick: Boost attack speed, Lower attack damage
  - Thundercaller: Boost attack speed, boosted number of lightning bolts spawned during a storm
  - Starcaller: 
    - Removed random chance for stars summoning (you always get them now). The "not getting a star half the time" felt like a bug to me.
    - Stars now deal half as much dmg (2.5 hearts), with a 25% chance to deal 5 hearts
    - On clear skies near midnight, have a small chance to get an extra star
    - Decrease drift of summoned star. Hopefully this will make it more useful when moving, though you still need skill to line things up right
  - Soulscribe: Boost attack speed
  - More to come!
- Make some items use the new 1.9-style cooldowns
- Platforms now go fully invisible with managlass again. All other translucent blocks still work directly.
- TilePool no longer runs logic clientside
- Non-fabulous pools now render like a normal block -> performance improvements!
- GG no longer runs extraneous logic clientside
- Buffed GG slightly: mobs spawned in spawning phase now receive 30s of fire resistance if they do not resist fire already
- Update futureazoo resource pack to 1.9
- Remove useless synchronization in Mana Network -> minor performance improvement
- Assembly halos now support JEI [+] button
- Removed item forms of buried petals and mana flame - seeing a missing item screen on update is OKAY
- Update JEI dependency, add support for recipe category crafting items
- API: Fold some interfaces together. There's wayyy too many .-.
- Add reverse petal block recipes from block -> petals
- Preliminary work on new loot system (not done yet)
- Pool cart now emits mana particles like an actual pool

Fixes:
- Platforms now respect direction sensitive blocks
  - May break with some mods that are doing things wrong, report if that happens.
- Fix kekimurus crash
- Fix GG not being marked as a boss
- Fix incorrect sound on the horns
- Fix missing texture on fungal apothecary's base
- Fix version checker links
- Fix paint lens particles not showing up
- Fix Corporea Crystal Cube rendering/crashing
- Fix several blocks looking weird in hand/inventory
- Resync the Hovering Hourglass whenever it flips over. Hopefully this will help avoid desyncs.
- Fix digging some blocks in survival crashing
- Fix potion rendering in the ingame HUD
- Fix floating flower models breaking terribly when doing F3+T
- Fix Jaded Amaranthus not working
- Fix incense looking weird on incense plate
- Get rid of some log spam
- Fix pump rendering
- Fix pool cart rendering

Upcoming:
- Possible: Livingwood/dreamwood chests
- New mana blaster model
- Possible: 3D lexicon model reimplementation
- Possible: new items to experiment with 1.9 combat
- More optimizations and cleanups!

---

{% include changelog_header.html version="r1.8 302" %}

Note: Exams for school are coming up, and I have a full time job this summer.
I expect to be able to mod on the weekends and in evenings in the summer, but for the next couple weeks I will be busy with exams, hang in there and keep reporting bugs!

Another alpha build for MC 1.9!

Built on Java 8, Baubles 1.2.0.0, Forge 1865

**MAJOR CHANGE:**
- Decorative blocks duplicated by vanilla and Quark will be REMOVED in a future build
- Migration recipes have been added to vanilla and Quark
- In the next build, I might add an item that you can use to convert blocks in-place in the world so you don't have to dig them all up

Changes:
- Gaia Guardian now uses loot tables! (see https://github.com/williewillus/Botania/tree/MC19/src/main/resources/assets/botania/loot_tables and http://minecraft.gamepedia.com/Loot_tables for more information)
- Armor retains enchants when upgraded (yrsegal)
- Ice Pendant now uses an effect equivalent to Frost Walker VIII
- Change armor equip sounds 
- Make thundercaller attack speed faster
- Code cleanups
- Mana Band and Terra Pick now retain mana used to craft them (yrsegal)

Bug fixes:
- Fix stairs rendering weirdly in some cases
- Fix vine balls and other throwable items not working
- Fix solid vines appearing gray
- Fix rebinding things using the wand of the forest not working
- Fix boss bars
- Fix GG music not playing
- Fix gaia spirit ingot and gaia spirit not being rainbow
- Fix Livingwood Bow not firing
- Fix crash with Fruit of Grisaia
- Fix some duplicate drops when using with Psi
- Potions show on the proper row in the ingame HUD
- Fix overlapping of tooltip in lexicon's ingame crosshair HUD

Enjoy!

---

{% include changelog_header.html version="r1.8 301" %}

Another alpha build for MC 1.9!

Built on Java 8, Baubles 1.2.0.0, Forge 1841

Changes:
- Update lang files
- Some better textures (wiiv)
- Fix server crash
- Fix pane models
- Fix walls appearing in vanilla creative tab
- Fix burst coloring discrepancies between 1.7 (wiresegal)
- Kekimurus now eats the new slice of cake added in 1.8+
- API cleanup (Incremented version)
- Fix contributor renders
- Fix multiblock renders being ugly af (alpha not working) (thanks Paleocrafter)
- Mana guns now allow the offhand item to be used when recoiling, so you can dual wield them pretty effectively!
- All interact events are back
- Fix spreader item model
- Fix munchdew not working
- Fix mana tablet on player render
- Fix Terra rod not working
- API: Allow any catalyst for infusion recipes
- Added vanilla flowers + mortar and pestle -> floral powder recipes
- Fix top half of double mystical flowers not emitting the right colored particle
- Fix shifting in bauble box gui
- Fix mana pool's north wall being too tall
- Fix pump model (mostly)
- Horses now drop everything in inventory except their saddle when being virused
- Fix red string interceptor not working at all
- JEI/Lexicon now render pool as full, like in 1.7
- Some brews now compute their color by averaging all the potion colors
- Experimental conversion of mana network to use a set instead of list (avoids linear time lookups)
- Tweak heisei dream internals

Enjoy and bugtest!

---

{% include changelog_header.html version="r1.8 300" %}

This is an alpha build for MC 1.9
Built on Forge 1811, Baubles 1.2.0.0

Changes since build 299:
- Fix red string comparator not binding
- Minor memory optimization
- Fixed JEI integration for magic flowers
- Fix pump trying to animate serverside
- Make water bowl look fancier
- Fix drinking a brew with Sponge installed spamming tons of vials/flasks
- Minor cleanups

Known issues:
- Axes don't work properly
- Gaia guardian is probably extremely broken
- Several interact events are out waiting on one of my forge PR's
- Auxiliary renders are wrong again
- Lexicon 3D model out again
- Some things look weird in inventory (TESRs and floating flowers)
- Cannot open the lexicon right now because I'm a dumbass

Enjoy!

---

{% include changelog_header.html version="r1.8 299" %}

Welcome to Botania unofficial 1.8! This is my unofficial port and continuation of Vazkii's wonderful mod Botania.
This will serve as our "release candidate" for the real 1.8 release. I've incremented the build number by 50. This is if there are any more patches to 1.7.10, they won't run into our numbers.

Built on Java 8, MinecraftForge 1763, Baubles 1.1.3.0

Changes since last build:

* Fix corporea funnel auto output
* Fix corporea index stars being too bright
* Rabbits can now spawn from cocoon of caprice
* Storage drawers now work with corporea again
* Fix mana prism redstone control being inverted
* Fix spreaders not animating smoothly on turntable
* Fix mana prism not syncing when its inventory changes
* Possible fixes for crashes when entering chunks with botania blocks in them
* Workaround JEI integration where things showed up in the wrong category
* Fix corporea sparks crashing when attempting to be placed on invalid blocks
* Fix smokey quartz slab model
* Fix endoflame crash
* Fix mana pump textures
* Fix terra pick mining the wrong block positions
* Fix itemfinder crashes
* Bring back the version checker from upstream and revert to upstream versioning style
* Bring back the 3d lexicon model (!!)


---

Note: In the time before build 299 was released, I used a bespoke decimal
version-numbering scheme that didn't match with the Botania numbering style. Here I've
just called them `r1.8 298-<0.0.xxx>` to fit in the changelog.

{% include changelog_header.html version="r1.8 298-0.6.4.2" %}

Hotfix on the hotfix

Changes:
- Retrofit corporea to work with forge capabilities
- Corporea will query for "TOP" side capability first and fall back on null side.
- Fix spark icon not being colored based on which network it's on
- Added a reference to our favourite magician

Note: Some semi-major code shuffling is going on internally, especially pertaining to interaction with inventories. Please report any bugs you find, and thanks for helping out!

Enjoy!

---

{% include changelog_header.html version="r1.8 298-0.6.4.1" %}

Don't use this release, corporea is broken. Please wait for the next. Sorry for the inconvenience!

Hotfix

Changes:
- Fix bad entries in shedding config crashing the lexicon
- Fix multiple tile entities crashing clientside when unloading. Because Minecraft is dumb.

---

{% include changelog_header.html version="r1.8 298-0.6.4" %}

Don't use this release, corporea is broken. Please wait for the next (0.6.4.2). Sorry for the inconvenience!

Your next weekly build

Changes:
- Fix pools and other te's suddenly getting wiped clientside when lots of blocks change (e.g. when a tree grows)
- This should mean that that entire "receiveMana" crash should no longer happen (\o/)
  - The hacky guard against that fix has been removed, if the crash doesn't happen this release then that means it's been fixed
- Fel pumpkins can now be placed from dispensers to form blazes, like vanilla 1.8 allows for pumpkins and golems
- Mobs can no longer ride the mana pool cart :P
- Work around a massive memory consumption issue in vanilla (8 million objects created every few seconds per player). Servers should run more smoothly now
- Update lang file (translators please see issue #159 
- [API] Misc cleanups, bump version
- Spreaders have their item models back
- Fix pools looking weird when held in third person
- Revert first person lexicon model to the normal one for now until I figure the fancy one out
- [experimental] Made many tile entities use the new Forge capabilities. Please report if any of your automation seems to be going weird (hopper/dropper interactions mainly)
- [experimental] Cleaned up tile entity code to no longer use BlockContainer (legacy vanilla stuff). Please report if any tile entities are horribly broken/don't work

---

{% include changelog_header.html version="r1.8 298-0.6.3" %}

New build!

Changes:
- Merge upstream final release
  - Corporea support for StorageDrawers (Vindex)
  - Lang updates
  - ...
- [API] Pure Daisy now allows the time to be set for the recipe
- Floating flowers now spin correctly again (tterrag, gigaherz)
- Fix Planestrider's sash being broken
- Update mana ring to use NBT instead of meta (blacksmithgu)
- Fix pink wither being aggressive
- Fix armor models not animating when breaking blocks
- Fix brews/incense crashing or randomly getting other potion effects
- Bore + warp lens should no longer break the warp destination
- Fix platform crashes under some situations
- Fix chiseled brick recipe conflicts for metamorphic stone and future stone
  - Chiseled bricks craftable now
  - Four bricks to make chiseled bricks
  - Two of every slab always recombines into original block
- Fix corporea funnel lexicon entry running off the page
- Fix glass panes looking a bit weird (vanilla bug)

Enjoy!

---

{% include changelog_header.html version="r1.8 298-0.6.2" %}

Small build this time

Built on latest recommended Forge 1.8.9, Java 8, Baubles 1.1.3.0

Changes:
- Merge changes from 1.7 branch
  - Some entities blacklisted from gravity rod
  - Update langs (NatsuArashi)
  - Botanurgist's Inkwell now shows bar immediately after being infused (howtonotwin)
  - Fix marimorphosis range (PikaDudeNo1)
  - Add Clayconia Petite
- Fix vineball vines being unclimbable on some sides (kevsgrove)
- Fix some UUID crashes (let me know if they still occur or if other ownership issues arise)
- Fix life aggregator (spawner mover) being completely broken
- Update lots of things to use methodhandles instead of reflection. Things should run a bit faster.
- Slightly tweak corporea cube rendering
- Minor cleanups

Enjoy!

---

{% include changelog_header.html version="r1.8 298-0.6.1" %}

Minor build to upload to curse:

Also new naming scheme, no more commit IDs
- Fix rare crash in F3 debug
- Tweak corporea cube renderer
- Fix dark quartz being disabled crashing the client (Renari)

Enjoy!

PS: I want to start testing backward compat. If you play Botania alone, please try copying your 1.7 world and loading it in 1.8 and seeing if things still work. If something's broken, drop me an issue report. Thanks!

---

{% include changelog_header.html version="r1.8 298-0.6" %}

New build!

Java 8, Baubles 1.1.3.0, Forge 1722

Changes:
- Relics use UUIDs to identify their owners, and fall back on usernames (Katrix-)
- Update lexicon entry to reflect new item frame corporea values
- Partial fix for platforms crashing with chisel blocks
- Very slightly nerf path block speed boost
- Fix star particle not rendering (corporea index in range, manastorm charge)
- A bit of the pump textures
- Update to official RF API, should work with other mods now (Girafi, KaseiFR)
- Migrated Corporea Crystal Cube to JSON. Animation doesn't quite line up right now, but it'll be tweaked
- All apothecaries have their textures back 
- Minor code cleanups

---

{% include changelog_header.html version="r1.8 298-0.5.4.1" %}

Hotfix

Changes:
- Fix server crashing on startup
- Mossy apothecary has textures back
- Fix floating flowers getLightLevel crash.

---

{% include changelog_header.html version="r1.8 298-0.5.4" %}

Please use the next build

New build! 

Based on latest recommended Forge for Minecraft 1.8.9, Botania 1.7 b248, Java 8, Baubles 1.1.3.0

Changes:
- Bump up to recommended forge
- Fix trodden dirt being wonky
- Tweak corporea item frame filters again. In clockwise order, they are now **1**, 2, **4**, 8, **16**, 32, **48**, 64
- Fix heisei dream being completely broken
- All baubles should now render on player (they look weird when crouching still)
- Fix thorn chakram rendering as white box
- Made thorn chakrams remember the stack used to throw them (i.e. they will now retain their name when you throw then catch it)
- Fix some color leaks
- Cleanups to life imbuer code
- Cleanups to some jsons
- Cleanups to code (get rid of most unchecked warnings)
- Fully restore Thaumcraft integration and fix crashes in the lexicon related to that (update thaumcraft to latest)
- Fix lexicon crashes when looking at certain kinds of blocks (BC pipes, etc.)
- Fix OreDictionary crashes in multiple places
- Fix multiblock renderer crashing in rare cases
- Fix Rod of the Skies not working on an avatar
- Rods should no longer spam the reequip animation
- Made terraforming rod respect the world-specific sea level instead of hardcoding it at 62
- Mesa apothecary has its texture back

Info:
- Lexicon will look weird in the next few builds - I'm starting to make attempts to bring back the 3d animated model. Thanks for sticking with it!
- Futureazoo's alternate flower textures have been split out into a resource pack here: https://github.com/williewillus/Botania/tree/MC18/alt_resources. Unfortunately, the way rendering has changed it's too complicated to switch the textures out using a config option. Sorry!

---

{% include changelog_header.html version="r1.8 298-0.5.3" %}

New build with some various bugfixes

Built on Java 8, MinecraftForge 1722 (latest stable 1.8.9, REQUIRED), Baubles 1.1.2.0, Botania 1.7.10 b248

Changes:
- Minor code cleanups
- Optimize incense plate and red string TESRs
- Fix pylons not showing their fancy particles
- Minor optimization to hourglass
- Fix rod of the seas crashing
- Fix water bowl being unobtainable
- Fix hovering over unbound functional flowers crashing
- Fix pick block on tall mystical flowers
- MAJOR: Corporea Funnel change to accomodate 1.8 item frames
  - Item frames in 1.7 had four possible states, up right down and left. This corresponded with the funnel pulling 1, 16, 32, and 64 items. Going above four rotations crashes in previous 1.8 builds
  - In 1.8, the intermediate directions are also available, so the funnel's item frame filters have changed to accommodate this. In clock wise order, the items pulled by the funnel are now ~~**1**, 16, **24**, 32, **40**, 48, **56**, and 64.~~ These values will be changed the next release, don't get comfortable with them

Known issues:
- Some apothecaries missing texture, spreaders missing texture
- Pump missing texture right now, I'm working on migrating it to JSON, which is what you see ;)

---

{% include changelog_header.html version="r1.8 298-0.5.2.2" %}

_sighs_

Changes:

The stupid receiveMana crash should no longer happen ever, but only because I've suppressed the symptoms, not the cause. <b>I need your help</b>:

Where it would've crashed in past builds, I've now put an obnoxious message saying "CLIENT TICK FAILED". If you see this, PLEASE REPORT it to this thread: https://github.com/williewillus/Botania/issues/71. Please include if you are on SP or MP, and what kind of machine (as in what kind of in game contraption) it errored on. If you notice a recurring pattern in what coordinates this occurs at, a certain type of flower, etc. please include that as well

Thank you so much for helping me hunt this down!

Enjoy

---

{% include changelog_header.html version="r1.8 298-0.5.2.1" %}

Bonus release today!

Built on Forge 1.8.9 build 1710, Java 8, Botania MC1.7.10 b248, Baubles 1.1.2.0

Same as last one, just had some more fixes that I thought were worth a new build

Changes:
- Updated Garden of Glass
- Fix GoG worldgen crash
- Fix GoG sky renderer leaking state
- Fix GoG worldgen error spam

In other words, Garden of Ass should now be playable in 1.8.9!  <sup>huehuehue</sup>

The updated GoG jar can be found here and can be used with all future 1.8 builds  (click "GardenOfGlass.jar"): https://github.com/williewillus/Botania/tree/MC18

Enjoy!

---

{% include changelog_header.html version="r1.8 298-0.5.2" %}

Based on Forge 1.8.9 build 1710 (bumped!)

Botania 1.7.10 build 248, Java 8

Changes:
- Merge upstream
- Actually fix the thorn chakram crash
- Fix many recipes not taking vanilla prismarine
- Another hopeful bandaid for the client crashes
- Make the distributor server side only, fixing a client crash
- Fix fab pools all having the same color at the same time
- A bunch of more baubles should render properly
- Placeholder model for spreader so it shows up in JEI
- Monocle now uses blockstates to read redstone
- Improve brewery JEI integration
- Bandaid for crash with Thaumcraft - for now the warp ward brew will not be available.

Enjoy!

---

{% include changelog_header.html version="r1.8 298-0.5.1" %}

Quick fix for some issues
- Fix crash with magic missiles in GG 2
- Fix thorn chakram crash
- Fix divining rod looking up blocks with no item form in the OD (Crash)
- (!) Fix dependency cycle with latest Thaumcraft.

School has just started for me, so builds might become more sporadic. I'll do my best to keep up the work on this though. Test test, report report!

Enjoy!

---

{% include changelog_header.html version="r1.8 298-0.5" %}

Beta!

Based on Java 8, Forge 1688 for Minecraft 1.8.9, and Botania 1.7.10 b248

Changes:
- Desert, forest, and fungal apothecaries have their textures back
- Fix flash lens desync and crash
- Fix piston lens not working at all
- Fully fix contributor special renders and mana tablet renders on belt
- Fix mana gun held weird in third person
- Fix more client chunk unload crashes (please tell me if they keep happening)
- Fix two multiblock crashes, preventing the WorldShaper's Sextant from working properly
- All non-cosmetic baubles now render properly on the player again, cosmetic ones still in progress.

Enjoy!

Shameless Plugs:
- Vazkii and wiiv, creators of this awesome mod: https://patreon.com/Vazkii | https://patreon.com/wiiv
- me! I spent my entire winter break porting this mod. I don't seek to gain anything from it, but if you like, you can support me at https://patreon.com/williewillus . If you prefer not having recurring donations, https://www.paypal.me/williewillus will work too. Again, please don't feel obliged, I'm just throwing this up :p

---

{% include changelog_header.html version="r1.8 298-0.4.1" %}

Hotfix
- Fix texture atlas being dumped every time you fire an arrow. This was a debug feature, sorry!
- If you want to see the atlas for fun, it's located in your root minecraft folder/atlas.png

Changes
- Fix some easter egg item models I missed
- Fully fix pool model lighting. They should look nice and pretty again!
- Fix mana pool cart rendering
- Fix gourmaryllis spawning eating particles infinitely but not deleting the item when a powered open crate releases that item.
- Fix passive flowers decaying directly into nothing instead of to a dead bush
- Fix gourmaryllis spamming packets once it's done eating.

Sorry, again!

Shameless Plugs:
- Vazkii and wiiv, creators of this awesome mod: https://patreon.com/Vazkii | https://patreon.com/wiiv
- me! I spent my entire winter break porting this mod. I don't seek to gain anything from it, but if you like, you can support me at https://patreon.com/williewillus . If you prefer not having recurring donations, https://www.paypal.me/williewillus will work too. Again, please don't feel obliged, I'm just throwing this up in the shameless plug section :p

---

{% include changelog_header.html version="r1.8 298-0.4" %}

Please use the next build

New build!

Based on: Java 8, Botania b248, MinecraftForge 1.8.9 b1688, should work on 1.8.8.

Changes:
- Changes in upstream since release b248 (check upstream)
- Fix tiny potato special renders (thanks Girafi!)
- Fix drum crash
- Cocoon of caprice model
- Fix bellows crash
- Fix tossing sunflower at teru teru bozu not work
- Give a bunch of TE's mining particles
- Fix terra shatterer tooltip darkening whole screen
- Now use potion registry internally
- Fix hopperhock putting items in inventories one block further away than it should
- Gaia TESR no longer registered on vanilla skulls
- Possible fix for particles losing their alpha transparency 
- Fix items colliding awkwardly with the petal apothecary
- Made Laputa shard use string block ID's instead of integer ID's
- Fix many crashes and many possible crashes related to [MOP's](https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Mop.svg/2000px-Mop.svg.png)

Known issues:
- I am currently working on destitching the textures for the petal apothecary. Thus, all apothecaries besides the default cobblestone one have temporarily lost their textures. They'll be back within the next 1-2 builds. Sorry about that!

Note:
- No more deobf jars, since FML is now able to load obf jars in dev fully without need for CCC or any of that.

---

{% include changelog_header.html version="r1.8 298-0.3.3" %}

Hotfix on the hotfix, yay.

Built on Java 8, Botania 1.7.10 build 248, Baubles 1.8.8-1.1.2.0, MinecraftForge 1.8.9 build 1656, should work in 1.8.8

- Fixes item renderers either crashing the game or causing JEI to kick you out of an SP game.

My deepest apologies

Enjoy!!

---

{% include changelog_header.html version="r1.8 298-0.3.2" %}

EDIT: DO NOT USE THIS RELEASE. It doesn't launch due to an issue reobfuscating lambdas. Use the next one, thanks!

Hotfix on previous build

Based on Botania 248, MinecraftForge 1.8.9 b1656, should run on 1.8.8.

- Fix Ender Air Bottle looking like manasteel ingot when thrown
- (!!!) The mod now requires Java 8
- Added a shitton of checks so no weird things happen when chunks unload and TE's get extra ticks.

Enjoy! Please report if you continue to get crashes on chunk unload/reload

---

{% include changelog_header.html version="r1.8 298-0.3.1" %}

New build!

Based on Botania 1.7.10 Build 248

Built on MinecraftForge 1.8.9 1656, should work on 1.8.8.

Changes 
- Mana pool now renders using JSON model
  - Ambient Occlusion is not working on it yet, will be fixed soon
- All Platforms should work again, and better than they did in 1.7! (Translucent blocks now mimicked properly)
- Fix incorrect wand of the forest model
- Fix double flowers not dropping when sheared on the bottom
- Fix pylon and pool rendering in multiblock highlights
- Fix recipes for 1.9 ender brick (purpur) stairs and slabs not being registered (upstream issue)
- Fix a variety of blockstate and lighting crashes
- Fix Craft Crate world models for when it has a pattern.
- Fix wool sleeves not rendering on spreaders.

---

{% include changelog_header.html version="r1.8 298-0.3" %}

New build!

Based on fresh out of the oven Botania 1.7.10 build 248, read all about that upstream! (http://botaniamod.net)

Built on MinecraftForge 1.8.9 build 1656, should run on 1.8.8.

Changes:
- Fix hourglass having inverted redstone signal. How come nobody caught this?!!
- Life Imbuer moved to JSON
- Spreader turntable model
- Item models for a bunch of TESRs (Hourglass, avatar, teru teru bozu, brewery, gaia head, bellows, pump). These are temporary while JSONs are created for the ones that we can convert. More will become JSON after Fry finishes his animation API
- Fix the pylon models rendering weirdly in world
- Fix fabulous pool leaking its fabulousness to others. MY FABULOUSNESS IS MINE.
- Mana flash, mana inkwell, water bowl item models
- Fix AlfPortal not rendering
- Incense plate moved to JSON
- (!!) Revert relics back to using usernames, as a better solution will be pulled upstream later
- Update InteralMethodHandler to modernify old icon methods

Again, sorry for filenamegore.

---

{% include changelog_header.html version="r1.8 298-0.2" %}

New build

Based on Botania 1.7.10 build 247

Built on MinecraftForge 1.8.9 build 1656, should run on 1.8.8.

Changes:
- Fix santaweave inventory icons
- Fix all armor rendering on-player. Thanks primetoxinz!
- Fix all crashy Thaumcraft integration
- Fix helmets of revealing models
- Item models for Corporea Crystal Cube and Corporea Index
- Item models for mana pools
- Item model for Tiny Potato
- [REGRESSION] Tiny Potato special renders in wrong place. If anyone wants to help, find me on #ProjectE or #vazkii
- Fix pylon crash with wawla 
- Add 1:1 botania -> vanilla conversion recipes for all botania-added vanilla items
- Remove botania crafting recipes for prismarine
- Made all botania-added recipes (stairs/slabs/walls/etc) take vanilla 1.8 items and blocks as well as botania ones.
- Fix some tessellator bugs and a possible alfportal crash
- Fix some GL coloring mistakes, fix flower particles because I'm dumb
- Tweak Crystal Bow pullback animation
- Made Starfield item model match upstream
- Fix dedicated server crash getting age of items
- Update API: now dedicated-server safe. Icon methods removed from SubTileSignature
- **FULL** integration for mezz's wonderful mod [JustEnoughItems](https://github.com/mezz/JustEnoughItems), an inventory editor and recipe viewer that is faster, simpler, and less hacky than NEI.

Enjoy!

Sorry, the filenamegore will stay for now. I don't want to use build numbers since it will be confused with upstream build numbers, so it's going to be commit ID's. The filename on last build was also wrong, should've been 247 not 248.

---

{% include changelog_header.html version="r1.8 298-0.1" %}

Based on Botania 1.7.10, build 247.

See https://www.reddit.com/r/feedthebeast/comments/3yy7u4/my_new_years_gift_to_you_botania_18x_first_alpha/  for more info.

Sorry for the titlegore in the file name :p

Enjoy, and Happy New Year!

---

This concludes the release notes for the Botania Unofficial releases made by williewillus
in the Minecraft 1.8-1.10 era.

---

---


{% include changelog_header.html version="r1.8 249" %}

* *The "LEAVE ME THE #%!& ALONE" update! Powered by the community's Pull Requests and TheWhiteWolves' help. <s>Also the last "official" update. Unless it breaks awfully. In that case there'll be another to fix this one. Hopefully not.</s>*
* Added back the config for passive flower decay. *I don't care any more. You broke me, you win. Have your config back.*
* Added corporea support for Storage Drawers and the DSU API (used by JABBA too, and the TE Caches, I think) (Vindex)
* Added Petite Clayconia. (TheWhiteWolves)
* Added quartz variants to the ore dictionary. (nekosune)
* Changed how Pasture Seeds grow. I don't even know how they changed but apparently they did. (Michael Brenan)
* Fixed accessories not rendering in the proper position. (Michael Brenan)
* Fixed Botania axes not breaking pumpkins and other things of the sort faster. (TheWhiteWolves)
* Fixed items that shouldn't be able to go in the Petal Apothecary being able to. (yrsegal)
* Fixed Mana Tablets losing durability in a storage drawer. (Michael Brenan)
* Fixed multiparts not working on servers. (yrsegal)
* Fixed the marimorphosis having an inccorect vertical range. (Pikadude)
* Fixed the Planestrider's Sash not resetting properly sometimes. (Michael Brenan)
* Made the Terra Truncator better. Not sure how but I guess the algorithm is better now. (Michael Brenan)
* Newly created Botanurgist's Inkwells will show the durability bar. (Rasheeq)
* Slabs and snow now prevent placement by the Rod of the Shifting Crust. (Michael Brenan)
* The Rod of the Shaded Mesa can no longer move some entities it shouldn't, like the Enderdragon, or Wither. (TheWhiteWolves)
* (Internal) Baubles can now provide blocks for the rod of the Shifting Crust. (yrsegal).
* [API] Added a blacklist for the Rod of the Shaded Mesa. (TheWhiteWolves)
* [API] Disposable blocks are now in the API (TheWhiteWolves)
* [API] Floating island types can be registered by addons. (yrsegal)

---

{% include changelog_header.html version="r1.8 248" %}

* Botania panes now have light. (Lazersmoke)
* Botania's backported 1.8 stones now support the ore dictionary for variant recipes. (Lazersmoke)
* Changed a bit the Lexica Botania's cover.
* Fixed alternate grass types not being able to sustain sugar cane.
* Fixed alternate grass types not breaking faster with a shovel.
* Fixed Mana Flashes not having a lexicon binding.
* Fixed Mana Lenses having a possible invalid recipe. (Lazersmoke)
* Fixed the Hopperhock calling inventory methods on the client side, and thus, crashing when used alongside a BetterStorage Crate.
* Fixed the Ring of Odin Fire Resistance config not changing its extinguish behaviour.
* Platforms now handle block render colors via metadata. (yrsegal)

---

{% include changelog_header.html version="r1.8 247" %}

* Added a config option to disable thaumcraft infusion stabilizing because some people are sadists, apparently.
* Added config options to change the harvest level of the Weight and Bore Lenses and defined said harvest levels properly in the check for what they can break/displace.
* Added the ability for Flowers that interact with items in the world to be placed on Podzol or Mycellium to add a delay to when they can pick the items up. You can read more on the Modulating Delay entry under Functional Flora.
* Fixed the A Message From Alfheim entry not loading up properly after you throw the book in the portal.
* Red Stringed Containers will no longer bind to blocks that can't have automation handling.
* Searching in the lexicon now also search the names of the items whose recipes are shown in the relevant entries. Also added a "Press ENTER to view" indicator thingy.
* Special flowers can now be planted in Mycellium.
* [API] Added ISubTileSlowableContainer.
* [API] Increased version number to 75.

---

{% include changelog_header.html version="r1.8 246" %}

* Added NEI integration for Corporea. Pressing C on an item in an inventory or the NEI panel while near a Corporea Index will request 1 of it. Shift-C will request a full stack.
* Fixed a crash when autocompleting a player's name near a Corporea Index.
* Fixed the Hopperhock spamming smoke particles when it can't pick up an item properly.
* Fixed the special sky renderer being super dark in superflat worlds.
* Fixed the special sky renderer still showing the planets in the void.
* Luminizers are now shiny.
* Replaced incentive to not lose your way.
* The Hopperhock can now split stacks properly between inventories if one is full. (howtonotwin)
* The Solegnolia no longer uses mana.
* Tweaked and fancified the Gaia Guardian fight(s) a bit.

---

{% include changelog_header.html version="r1.8 245" %}

* Added a new fancy skybox effect to Garden of Glass. <a href="https://imgur.com/a/0ecOV">Here's an album.</a> It looks cooler ingame. (there are config options to disable it and enable it in normal worlds as well, if you want)
* Added configs to move the Flugel Tiara's flight bar as apparently I'm not the only person that thought it was cute to put a bar there.
* Changed the period in which the mod enabled Christmas features to be in sync with Winter's Veil in World of Warcraft. *Priorities!*
* Fixed a bunch of stuff in the mod not rendering properly (going all gray) if some mod disables GL_LIGHTING (See: NEI, Neat).
* Fixed Luminizers that have been bound to another Luminizer that has then been removed taking you to the position where the removed one was.
* Increased the drop rate of Melon and Pumpkin seeds on Garden of Glass. Again.
* Manaweave armor is now Santaweave during Christmas.

---

{% include changelog_header.html version="r1.8 244" %}

* Added a "Preventing Decay" entry to the Miscellaneous section. Something seems a bit off with it though.
* Fixed a crash with NEI when some mods think it's a good idea to use stacks without a valid item.
* Fixed Resolute Ivy not working.
* Fixed the Ring of Chordata stopping the player from drowning if they have no mana. (howtonotwin)
* Polished a few things all with completely inconsequential particle effects.
    *   Hopperhock makes smoke
    *   Extrapolated Bucket makes smoke
    *   Mana Pump makes smoke
    *   Endoflame makes fire and smoke
    *   Gourmaryllismakes food bits go all over the place
    *   Spectrolus makes wool bits go all over the place
    *   Ender Overseer has redstone particles when looked at
* [API] Added a fallback for LexiconRecipeMappings.stackToString().
* [API] Increased version number to 74

---

{% include changelog_header.html version="r1.8 243" %}

* Added NEI integration for the Lexica Botania, it sometiles also tells you which entry something is in and gives you little tidbits of info like how to make livingwood/livingrock or terrasteel.
* Added recipe indicators (the items that appear when you hover shift on an entry's name) to a few entries that don't have direct recipes. (like pure daisy or ender air)
* Fixed a crash if Red Stringed Interceptors are triggered too fast.
* Fixed double tall flowers not respecting alternate textures. (yrsegal)
* Fixed the Corporea Funnel ignoring an inventory 2 blocks below it if there's another Corporea Funnel directly below it. (yrsegal)
* Fixed the Gaia Guardian not being able to be summoned with Et Futurum and Chisel beacons. (yrsegal)
* Mana Pools are now dyed by right clicking with Floral Powder, not dropping it. On other news, you'll no longer dye your pool while making Mana Powder, yay!
* NEI integration for Elven Trade no longer requires a Lexica Botania with Elven Knowledge on your inventory.
* (Internal) ItemBlockSpecialFlower lexicon mappings are now prefixed by "flower.".
* (Internal) The extra info feature for the lexica botania NEI integration can be used by adding a key to a lang file, in the following format: "botania.nei.quickInfo:" + the lexicon mapping key. You can find it by pressing CTRL+SHIFT in the NEI Lexica Botania entry for an item if F3+H is enabled.
* [API] Added addExtraDisplayedRecipe() to LexiconEntry so more recipe indicators can be added without being linked to a page.
* [API] Increased version number to 73.

---

{% include changelog_header.html version="r1.8 242" %}

* Fixed the Terra Shatterer not working when the player has full Terrasteel Armor and their first available Mana Tablet isn't full and is in a hotbar slot before the Terra Shatterer.
* [API] Added IManaGivingItem to help track which items should be blacklisted from the Terra Shatterer.
* [API] Increased version number to 72.

---

{% include changelog_header.html version="r1.8 241" %}

* Fixed a crash when some odd items are dropped around a Ring of Magnetization.
* Fixed the Config Adaptor trying to modify correct values and spamming you about it in the chat.
* [API] Implemented a recursion limiter to the magnet ring's blacklist functions for pesky items that intercept getItemDamage().
* [API] Increased version number to 71.

---

{% include changelog_header.html version="r1.8 240" %}

* Fixed the Mana Pool achievement. Since it's one of the first achievements it's really annyoing if it's broken :V

---

{% include changelog_header.html version="r1.8 239" %}

* Fixed a crash when right clicking a spark with an empty hand.
* Fixed patreon flowers not rendering on players properly unless there's a named tiny potato around. *For real.*
* Fixed the Rune of Air recipe not being adapted fully to the new pattern.
* Fixed using a Rod of the Unstable Reservoir with a Livingwood Avatar crashing the game if it kills a named mob while CoFHCore's "alert named mob kills" config is enabled. *And people ask me why compatibility is so hard...*

---

{% include changelog_header.html version="r1.8 238" %}

* **This update warrants a major version increase due to the sheer number of tweaks and changes that overall make the game better. This update's objective is to polish any roughness around the edges the mod might have had and to get it to a great state for newer and older players alike. Despite it being somewhat long, I would advise you to read it all, lest some small change trip you up.**
* Added a "SHIFT to stop animation" tidbit to Rune and Petal pages.
* Added a config option to invert the Ring of Magnetization's controls.
* Added a proper HUD to the Petal Apothecary and Runic Altar that shows you what is currently in it and what you're crafting.
* Added an Adaptative Config system, which changes your config options for you if you are using default values from old versions. helping you get the new changes without having to delete configs. You can turn this off in the config if you don't like it.
* Added Mana Powder as a new crafting material.
* Added the Fabulous Mana Pool. A new rainbow Mana Pool variant crafted with Shimmerrock. *Yes that is its name.*
* Blazes spawned by a Fel Pumpkin (Garden of Glass) drop more Blaze Powder now.
* Changed a bunch of details in the lexicon all over the place to make it a lot easier to understand.
* Changed almost all flower recipes to follow a new "no more than 4 different petals per flower" rule, effectively simplifying flower crafting.
* Changed how crossmod/addon entries' titles look.
* Changed the Bore Lens recipe to be a lot easier to make.
* Changed the mana costs for runes in the Runic Altar.
* Changed the recipes for Terrasteel Armor.
* Changed tier 1 rune recipes to use Mana Powder and only make 2 per.
* Changed tier 3 rune recipes to only make 1 per.
* Danked some memes.
* Dayblooms and Nightshades about twice as strong now.
* Disabled the tutorial video until there's one that's updated with the new changes.
* Documented properly the fact that buried petals can be used to grow tall flowers.
* Fixed a crash when removing bookmarks.
* Fixed creating Manasteel in bulk blocks taking 10x as much instead of 9x as much. (TheWhiteWolves)
* Fixed Mana Pools not updating their comparator output properly when mana is being taken from them.
* Fixed the Manaseer Monocle's range viewing functionality rendering as a gray square when used alongside other mods that use GL_LIGHTING, like Neat.
* Fixed the new grass types not having a properly lexicon linking. (TheWhiteWolves)
* Fixed the Petal Apothecary sometimes desyncing with the client.
* Fixed Thermalilies not writing their cooldown to NBT properly.
* Flowers' tooltips now tell you what type of flower they are.
* Increased the Thermalily's production rate, time and lowered its cooldown (to 5 minutes).
* Lowered (by a bunch) the amount of flowers that are generated in the world.
* Lowered the Pure Daisy's craft time to 60 seconds (from 80).
* Made a bunch of entries priority entries (italicized) to help lead new players into the important stuff without bogging down the tutorial.
* Made the runic altar's floaty cube animation smoother. *144hz master race.*
* Made the Zoom text in Rune and Mana Infusion pages more clear. 
* Removed a bunch of needless entries from the Tutorial to make it faster to get started.
* Removed diminishing returns for passive flowers.
* Removed Mana Petals. Any recipes that used them previously have been tweaked.
* Rewrote the "Welcome to Botania" entry to be a lot more concise and help new players better.
* Right clicking a Runic Altar with Livingrock now places it on top perfectly centered. (same for the T.A. Plate and relevant items)
* Runes used for recipes in the Runic Altar are now given back upon the recipe's completion.
* Sparks can now be dyed with Phantom Ink to make them highly translucent.
* Terrasteel armor now has Knockback Resistance as an attribute and Passive Mana Generation (like a Band of Aura) as a set bonus.
* The Diluted Mana Pool can now infuse items just like the normal one.
* The Diluted Mana Pool is no longer a required part of progression, you can craft the normal one with the old diluted recipe and the diluted one with slabs instead.
* The Fel Pumpkin is now part of botania itself and not Garden of Glass.
* The Petal Apothecary now fills up with rain like vanilla Cauldrons do.
* The Ring of Magnetization will no longer pull items that are on top of Mana Pools or Runic Altars.
* The Runic Altar no longer needs a wand interaction to be started.
* The Thermalily now resets its cooldown back to 5 minutes if it absorbs any lava while it's in cooldown.
* The Wand of the Forest now comes in Bind Mode when crafted.
* [API] Added a proper blacklist for the ring of magnetization.
* [API] Added BotaniaTutorialStartEvent. It does exactly what you think it does.
* [API] Added a @PassiveFlower annotation for detection of passives without creating an instance. Used for SubTileSignatures.
* [API] Changed BasicSignature to include a getType() function for the tootlip.
* [API] Exposed SubTileSolegnolia.hasSolegnoliaAround() to the internal method handler.
* [API] Increased version number to 70.

---

{% include changelog_header.html version="r1.7 237" %}

* Fixed the Gaia Guardian spawning on the client side when it shouldn't.
* The Brew of Overload has a larger Weakness period now.
* The Damage Lens now deals more damage.
* The Fruit of Grisaia uses less mana now.
* The Gaia Guardian's damage cap now is higher to accommodate for Overload Brews. HP remains the same.
* The Rosa Arcana now produces and holds more mana.
* The Spectrolus now produces and holds more mana.

---

{% include changelog_header.html version="r1.7 236" %}

* Added Chisel stone support to the Rod of the Terra Firma.
* Added floating flower variants to all the new pasture seed types.
* Added the Bauble Case, a way of storing baubles on your person.
* Added the Planestrider's Sash, a new belt item that gives you more speed the more you walk.
* Buffed the amount of damage the Key of the King's Law does.
* Fixed Botania armor models not resetting the "using a bow" state.
* Fixed Botania grass types not spreading.
* Fixed Featherfall disabling the Flugel Tiara's glide. (TheWhiteWolves)
* Fixed items getting destroyed if they're left in a crafting table when the lexicon's Ctrl lookup feature is used. 
* Fixed lexicon bookmarks being weird if you do multiple in the same entry.
* Fixed Mana Bursts' position in the client sometimes going above the block they're inside.
* Fixed multiblock previews rendering in dimensions other than the one they were created in.
* Fixed raytracing ignoring the player's height.
* Fixed the Gaia Guardian head not breaking properly or having the right drops.
* Fixed the Halos not rendering multipassed items properly.
* Fixed the travel sashes using mana even if the player isn't moving.
* Fixed the weapons spawned by the Key of the King's Law not despawning if the player that created them dies.
* Fixed Witchery wiki lookup not working because google sites requires lowercase URLs. (TheWhiteWolves)
* Glimmering flowers now have slightly altered textures from the normal ones.
* [API] Added ICompositableLens for API level lens composition handling. (WireSegal)
* [API] Added lowercase conversion support to SimpleWikiProvider. (TheWhiteWolves)
* [API] Increased version number to 69.

---

{% include changelog_header.html version="r1.7 235" %}

* Fixed the Crafty Crate not syncing properly, and by extension, the Wand HUD for it not being accurate.
* Fixed the Hopperhock not updating inventories properly.
* Fixed the Mutated Grass having a missing texture.
* If an arena for the gaia guardian fight is obstructed or has holes, there'll be a quick particle display showing you how large it needs to be.

---

{% include changelog_header.html version="r1.7 234" %}

* Added 3 new lenses:
  *   Mana Lens: Redirectory - Bursts that have this lens fired from a spreader will, when colliding with another spreader, rotate the collided spreader to point towards the one that shot. It also gives bursts the Phantom effect.
  *   Mana Lens: Celebratory - When it collides with a block it'll make a firework of the color of the burst.
  *   Mana Lens: Flare - Spreader only. Prevents the spreader from firing any mana bursts. Instead, the spreader fires a continuous beam of particles of the color of the lens. ([demo 1](https://twitter.com/Vazkii/status/666012115900190720), [demo 2](https://twitter.com/Vazkii/status/666013977089060864))
* Added 6 new types of decorative grass (and six new types of pasture seeds to make them) that don't turn into dirt if there's anything above.
* Bellethornes now kill Witches properly.
* Changed armor item icons to match the new armor models.
* Condensed the Paintslinger Lens and Warp Lens entries as well as the 3 new lenses in this version into an "Elven Lenses" page.
* Fixed botania Panes having weird lighting in corners.
* Fixed bursts with the Entropic lens exploding in contact with a full spreader.
* Fixed the Exoflame not using mana properly on interfaced blocks.
* Fixed white tinted mana lens not being able to be crafted.
* Holding shift on a recipe page with multiple recipes will pause the recipe changing. It'll also stop petals/rune pieces from rotating if it's a petal/rune recipe.
* Luminizers and Agricarnations are now quieter.
* Pasture Seeds for blocks other than Grass can now be used on Grass.
* Removed the "lore" pages after some flowers' recipes, as interesting as they are, they served no practical purpose and only confused people.
* Renamed the Talent Shredder achievement to a much more fitting name.
* The bottom side of Mana Pools is now solid (so you can put stuff like levers now, if you want to do that for some reason :V)
* Tweaked armor textures and models a bit.
* [API] Added IDirectioned and IRedirectable to work with the new lens.
* [API] Added IFlowerPlaceable for items that can be placed by the Rannuncarpus. 
* [API] Added ILensControl for lenses that control a spreader's behaviour.
* [API] Increased version number to 68.
* [API] Stripped getRotationX() and getRotationY from IManaSpreader and moved it to IDirectioned (which IManaSpreader extends now).

---

{% include changelog_header.html version="r1.7 233" %}

* Fixed items that take damage when used in a crafting recipe taking damage purely by being put in the grid.
* Fixed the Lexica Botania's main page making the screen dark on lower end (and integrated) graphics cards.

---

{% include changelog_header.html version="r1.7 232" %}

* Added the botania version to the F3 menu next to the network monitors.
* Changed the way multitexture is handled on graphics cards that don't support OpenGL 1.3 (using ARB instead).
* Fixed Corporea Interceptors not intercepting requests from Corporea Funnels for more than item and not checking NBT properly.
* Fixed the Corporea Index working between dimensions.
* Holding CTRL+SHIFT on the F3 menu will show you the OpenGL Context and relevant config settings.
* Luminizers now synergize with Ender Pearls.
* Primus Loci are a bit less rare now.
* Using Phantom Ink on an item that already has it will now remove it.
* You can stand inside mana pools now. *update of the year 10/10*

---

{% include changelog_header.html version="r1.7 231" %}

* Added new models to all botania sets of armor. You can turn these off in the config if you're mean and hate everything that's fun.
* Added the Solegnolia Petite. Not much to explain here.
* Changed the Lexica Botania's Category Icon Shader to use GL Texture Unit 7 rather than 15. Also added a config option to change which unit is used, just in case. This should fix GL errors with old and integrated graphics cards.
* Fixed a crash with the Worldshaper's Sextant if CodeChickenCore isn't installed. (TheWhiteWolves)
* Fixed a minor grammar derp. (shadowfacts)
* Fixed Shimmerrock and Shimmerwood Planks not having microblocks.
* Fixed the Mana Prism accepting non lens items through automation and crashing the game if any make it in.
* Fixed the Ring of Loki's entry not telling the player they need to sneak.
* Looking at a Crafty Crate with a Wand of the Forest now shows you the items inside.
* Looking at an Applied Energistics Dense Cable while having their config to turn off channels set to false will no longer crash the game. *fix yer game ae*
* The Flugel Tiara's Glide ability now only starts if you've fallen at least 2 blocks.
* The Solegnolia now negates any rings worn by players inside it.

---

{% include changelog_header.html version="r1.7 230" %}

* **2 MILLION DOWNLOAD HYPE**
* Fixed the Dandelifeon not properly making mana.
* The Teru Teru Bozu can now be made sad. :(

---

{% include changelog_header.html version="r1.7 229" %}

* Added 15 new update flavour messages.
* Added the Worlshaper's Sextant, a new item to help you make circles.
* Changed the Flugel Tiara's flight time from 10 seconds to 30 seconds.
* Fixed Shimmerwood Plank Slabs having the wrong step sound. (SnowShock35)
* Fixed the Dandelifeon being able to destroy Bedrock and pretty much any other block. 
* Fixed the Dandelifeon being able to generate mana from cells ticked by other Dandelifeons.
* Fixed the Enchanting with Mana page missing two pages. (AlterAkima)
* Fixed the Lexica Botania Quick Lookup key showing up as Ctrl in mac instead of Cmd.
* Fixed the Lexica Botania Quick Lookup time being dependant on fps, also made it 1 second rather than 1.5.
* Fixed the Rod of the Skies being able to be used for earlygame infinite flight by item switching.
* Increased the Narslimmus' mana buffer to not have it overflow when it eats a big slime.
* Mana Pools should sync better when receiving or sending out mana.
* Red String Blocks can now be rotated with a Wand of the Forest.
* Sparks are now cheaper.
* The Gaia Guardian's arena checking is now less retarded, circular and doesn't require skylight.
* [API] Added AnyComponent, a MultiblockComponent that matches any non air block.
* [API] Added support for MultiblockSets with an array with length < 4.
* [API] Increased version number to 67.

---

{% include changelog_header.html version="r1.7 228" %}

* Fixed the Gaia Guardian being able to ride minecarts.
* Fixed the Livingwood Avatar's render taking over other mod item renders.
* Fixed typos.

---

{% include changelog_header.html version="r1.7 227" %}

* Added a "Quick Lookup" feature to the Lexica Botania. You can hold CTRL on an item in your inventory while you have a Lexica Botania in your hotbar to open its entry.
* Added the Livingwood Avatar, a new block you can give a few rods to and will have effects depending on which one. These are the current effects:
  *   Rod of the Bifrost: Creates temporary bifrost bridges in the area directly in front of it where the player would walk to.
  *   Rod of the Hells: Creates a perpetual fire ring around it, like the rod does.
  *   Rod of the Plentiful Mantle: Minims the rod's effect of showing nearby ores, the colors stay the same as long as it stays in the same place.
  *   Rod of the Skies: Any players that jump near it will jump as high as if they had used the rod, as well as get temporary fall damage resistance.
  *   Rod of the Unstable Reservoir: Shoots magic missiles at nearby mobs, like the rod does.
* Added Primus Loci. Rare worldgen flower patches with Primus variants of Dayblooms and Nightshades that don't decay.
* Added Railcraft support to the Mana Pool with Minecart.
* Buffed the Rafflowsia, again.
* Fixed Isolated and Recessive Spark banners (Et Futurum Integration) having the wrong recipes.
* Fixed the Mana Pump's render crashing the game if it's placed with an invalid metadata somehow.
* Fixed the Terra Shatterer's mana bar being slightly off center. Phew.
* Fixed transparent blocks not rendering properly under the mana enchanter.
* Looking at any entry in a Lexica Botania category page and holding shift shows you a preview with a tagline of what it is and what crafting recipes are in the entry.
* Mana Tablets, Rings and Mirrors now have a similar mana bar to that of the Terra Shatterer.
* Merged the "A Tutorial of the Basics" and the "An Introductory Video" entries.
* Redesigned the Lexica Botania main page with smaller, bolder icons and fancier animations.
* The "missiles" fired by the Rod of the Unstable Reservoir are a bit less choppy now.
* The Manaseer Monocle's Lexicon Entry now has priority (is in italics).
* The Terra Truncator now properly breaks around diagonals.
* [API] Added IHornHarvestable for usage with the various horns. *Get on it, Agricraft.*
* [API] Deprecated IGrassHornExcempt in favor of IHornHarvestable.
* [API] Added IManaTooltipDisplay for items that display a mana bar on the tooltip.
* [API] Added getTagline() to LexiconEntry.
* [API] Added paintableBlocks and registerPaintableBlock() to BotaniaAPI for use with the Paintslinger lens. (L0neKitsune)
* [API] Increased version number to 66.

---

{% include changelog_header.html version="r1.7 226" %}

* Added a config option to disable seasonal (halloween and christmas) features.
* Fixed the Spellbinding Cloth taking damage simply by putting it in the crafting grid.
* Phantom Ink and Timless Ivy can no longer be applied to items that have a container item to prevent dupes.
* Spookied up the place.
* The Mana Cookie was renamed to Biscuit of Totality.
* The Rafflowsia produces more mana than before.

---

{% include changelog_header.html version="r1.7 225" %}

* Added Shimmerrock and Shimmerwood Planks, rainbow blocks made with Bifrost Blocks.
* Added the Rafflowsia, a new flower that eats passive flowers for mana. And you thought they were useless!
* Fixed some entries telling you to go to "Magical Apparatus" when the section is actually called "Natural Apparatus".
* Flowers can now be dyed using Floral Powder.
* The Dandelifeon produces a lot more mana per cell now, however, whenever a cell is eaten, the board is wiped. Also added an "warning: experimental" page to the entry.*As always, this is still not final.*
* [API] Fixed API dependency on non-API Botania classes.
* [API] Increased version number to 65

---

{% include changelog_header.html version="r1.7 224" %}

* Added "Motif" variants of passive flowers. Same as Petite, but instead of being smaller, they don't decay nor produce mana. Since they use the same system, you can find them in the Flower Shrinking page.
* Added "Redstone Helper" features to the Manaseer Monocle. Looking at redstone things will show you some data about them.
* Added a recipe counter to the recipe registing code just for fun. You can now see how many recipes botania adds, woohoo.
* Added comparator support to the Corporea Crystal Cube, it can now detect how many items are in a corporea network with exponential output.
* Added Managlass, Alfglass and Bifrost Panes.
* Added the Corporea Retainer, a new corporea block you can place next to a Corporea Interceptor, it'll remember the last intercepted request and have the requestor redo it if it gets a redstone signal.
* Added the Teru Teru Bozu, a new (cute as hell) block that can send the rain away.
* Added two new Portuguese Pavement Colors, yellow and green.
* Changed some checks to use Material instead of Block (such as Hydroangeas and Thermalilies). Should make compatibility with TerraFirmaCraft or other mods' different water and lava blocks work better.
* Changed the "no mana killzone" of the Dandelifeon from a 5x5 to a 7x7. *To be honest, I really don't know where I'm going with this one. It's seeming to me like it's a failed experiment, as the optimal build always ends up being a really simple thing, which I'm not a big fan of. I'm just trying various things and seeing what sticks. I might end up removing it alltogether.*
* Changed the Gaia Guardian's health to 800 (from 300) and the damage cap to 35 (from 12), 50 if it's a crit. In a way, if you're properly prepared, the boss is actually easier now as it's definitely possible to hit that hard with the proper enchants and potions, it just requires preparation.
* Fixed the Corporea Crystal Cube and Hovering Hourglass not rendering every model piece in the inventory.
* Fixed the Elementium Axe dropping Zombie Heads from Zombie Pigmen.
* Fixed typos.
* The Corporea Crystal Cube now shows how many items are in the network in its render without you having to hover on it.
* The Corporea Index is now less stingy on how close you need to be to it in the Y axis.
* The Manaseer Monocle can now be used as a Cosmetic Override to other baubles. Its benefits are not removed when you do this, so you can use the monocle without taking up a slot.
* The Rannuncarpus can now place botania flowers that do stuff as well as redstone dust.
* [API] Added ICorporeaRequestor for use with the Corporea Retainer.
* [API] Increased version number to 64.

---

{% include changelog_header.html version="r1.7 223" %}

* Added code to safely crash the game if someone overrides the Botania API's Internal Method Handler. Overriding the Internal Method Handler is a sure fire way to crash the game or cause incompatibility problems later down the line, so at least you're getting informed about it with a proper error message. It's sad that I even had to makXe this, to be honest.
* Fixed a circular dependency chain with DragonAPI not allowing for the game to load.
* Fixed Red Stringed Conparators only seeing half of a double chest.
* Reused old botania shaders as new tiny potato variants. *You know, this whole potato variant thing is starting to become a good stress relief measure.*

---

{% include changelog_header.html version="r1.7 222" %}

* Added 12 new achievements, moved some old ones around to make space.
* Added arena checking code to the Gaia Guardian to prevent it from being killable with little to no effort underground.
* Added Comparator Support to the Spark Tinkerer.
* Added the ability to get the Gaia Guardian's Head. How to do it though...
* Added the Red Stringed Interceptor, a new block to the Red String arsenal that intercepts right clicks.
* Buffed hydroangeas' mana production rate.
* Enchanted Soil now has an interpolated texture, like Prismarine and Bifrost.
* Fixed a wrong error message for the /botania-skyblock-spread command.
* Fixed Mana Spreaders being able to pull mana from pools before even being added to the mana network.
* Fixed the Dandelifeon not having an alt texture.
* Fixed the Mana in a Bottle allowing for water to be placed in the nether and for the roof of the nether being accessed.
* Fixed the Weight Lens being able to drop blocks that normally can't be Silk Touched.
* Overgrowth Seeds no longer speed up passive flowers, instead they prevent their decay.
* Potion ID configs now work properly with DragonAPI's potion array extending.
* Redstone Mana Spreaders can now pull mana from their target pools.
* Removed passive wither config and locked it to 72000 ticks (3 ingame days, 1 IRL hour). Also added an entry to the Generating Flora section to explain it better. You can read about this change and why I made it <a href="https://vazkii.net/#blog/sins-of-a-solar-empire">right here</a>.
* Some Dandelifeon tweaks, the 5x5 area around it will no longer kill generation 1 cells, the output rate has been lowered a bit and the recipe for Cellular Blocks has been changed.
* Sparks now produce less, but bigger, particles to make it less likely to reach the particle limit.
* The Mana Pool's "water" icon is now a different icon and not the one used for water. (it still looks the same, it's just in a different file)
* The Ring of Magnetism now has a limit of 200 entities it can pull so it doesn't kill your FPS if you manage to get a lot of items in one place for whatever reason.
* The Thorn and Flare Chakrams now pass through leaves and folliage.
* [API] Added an "overgrowth" and "overgrowthBoost" variables to SubTileEntity for when it's working on Enchanted Soil.
* [API] Added isOvergrowthAffected() to SubTileEntity to allow for flowers to opt out of Enchanted Soil.
* [API] Separated ticksExisted from passiveDecayTicks and moved the internal drop handling code from SubTileGeneratingPassive to SubTileGenerating.
* [API] Increased version number to 63.

---

{% include changelog_header.html version="r1.7 221" %}

* Added some extra rules to the Dandelifeon to prevent some exploits. Also lowered the mana value of each cellular block.
* Double slabs are now properly localized. (MrKunji)
* Made the Dandelifeon's texture not be as horrid as the one I made while wiiv was away :V

---

{% include changelog_header.html version="r1.7 220" %}

* Added more taters.
* Added the Dandelifeon, a new generating flower for maniacs that utilizes Conway's Game of Life.
* Fixed a flower dupe with the Flower Pouch.
* Fixed BotaniaVars.dat not generating in the right directory when using symlinks. (shadowfacts)
* Fixed removing a Master Corporea Spark not properly resetting the corporea network.
* Fixed the Mana Blaster with a Lens Clip showing that you have infinite mana lenses instead of showing you the name of the lens.
* Fixed the Pink Wither not being able to be named.
* Holding a Wand of the Forest in bind mode while wearing a Manaseer Monocle shows you the range of the flower the wand is bound to, even if you're not looking at it.
* Luminizers can now move items.
* Manastorm Charge mana bursts have more mana.
* Upgraded goldfish.
* [API] Added acceptsRedstone() to SubTileGenerating.
* [API] Bunch of work done on multiblocks to support a custom IBlockAccess for renders like stairs or redstone. (SoundLogic)
* [API] Changed the IMultiblockRenderHook.renderBlockForMultiblock() signature and added nedsTranslate(). (SoundLogic)
* [API] Increased version number to 62.

---

{% include changelog_header.html version="r1.7 219" %}

* Added comparator support to the T.A. Plate.
* Added ForgeMultipart microblock support.
* Added Portuguese Pavement building blocks, they're good for roads (duh) and come in white, black, blue and red, as they do in the streets of Portugal.
* Added the Manufactory Halo, effectively an automatic Assembly Halo.
* Capped the flower patch size config option to 8 blocks to prevent worldgen crashes when the generator seeps into other chunks and forces the world to generate a new chunk while already generating one.
* Changed the Assembly Halo's glow to be green rather than pink, it also properly renders on the outside now.
* Fixed an exploit that made the Exoflame ridiculously fuel efficient.

---

{% include changelog_header.html version="r1.7 218" %}

* Fixed the Black Hole Talisman crashing the game if used on lit redstone.
* Fixed the Flugel Tiara HUD not rendering if you have Tinkers Construct and Blood Arsenal. Maybe.
* Fixed the mana bursts created by the Shard of Laputa and Manastorm Charge being able to be converted to normal ones with prisms.
* Fixed the Rod of the Shfiting Crust not using mana for the left click function.
* Fixed the snowflake pendant having no documentation. (MrKunji) *I deleted it by accident, ok? D:*
* Having a Haste buff enabled now lowers the Mana Blaster's cooldown.
* Increased the incense plate range to 30 blocks (from 24).

---

{% include changelog_header.html version="r1.7 217" %}

* Fixed server crash.

---

{% include changelog_header.html version="r1.7 216" %}

* Added Manweave Cloth and Armor. Manaweave Armor's set bonus gives a 35% discount on tools and rods and makes rods be more powerful or have larger range.
* Added the Manatide Bellows, a block that increases the speed of Mana Pools' mana output and Furnaces' smelting.
* Bifrost Blocks are now craftable using a Rod of the Bifrost and Alfglass (you don't lose the rod). They also have a smoother animation now thanks to the interpolated icon (same as the one used for prismarine) and emit light.
* Fixed Mana Pools leaving sometimes a tiny sliver of mana in the client side if used to charge a mana tablet.
* Fixed the Flare Chakram returning a Thorn Chakram when picked up instead of the proper Flare Chakram.
* Fixed the Mana Pump dispatching needless network packets.
* Increased the amount of time the bridges created by the Rod of the Bifrost to double of what it was before.
* Made the Exoflame a bit cheaper.
* Rods are now affected by Armor Sets' mana discounts (like the Manasteel armor set).
* Spellbinding Cloth is now crafted using Manaweave Cloth.
* [API] Added IManaProficiencyArmor.
* [API] Added manaweaveArmorMaterial to the API main class.
* [API] Increased version number to 61.

---

{% include changelog_header.html version="r1.7 215" %}

* Added the Flare Chakram, a fire version of the Thorn Chakram.
* Added the Thundercaller, a new Terra Blade sidegrade that calls upon chain lightning.
* Botania slab recipes are now revertable.
* Changed the way the gaia guardian counts players to try to fix the bug where it would scale for more players than those present.
* Fixed the Slime in a Bottle not detecting slime chunks properly as it was ticking in the client where it doesn't know the world seed.
* Petal and Rune recipe pages now animate a lot smoother.
* Pixies (friendly and hostile) are now forced to despawn if they exist for more than 10 seconds.
* The Head Creation page now links to the <a href="http://heads.freshcoal.com/usernames.php">player head database</a> to give you ideas on what cool stuff you can do with heads.
* The high council of Elven Garde, after having received countless unintended "gifts" of unenchanted Iron Ingots, Diamonds and Ender Pearls from unskilled skim-reading botanists are now returning those back to the senders.
* Tweaked the Elven Portal's render a bit. The lighting on it doesn't spazz out and it renders properly when you're in it now.

---

{% include changelog_header.html version="r1.7 214" %}

* Added a little bar to the Flugel Tiara's flight HUD that shows the dash cooldown.
* Added a notification to the lexicon's main page when the mod updates to let you know things have changed and not to panic if something doesn't work as it used to.
* Added support for the 1.8 Banners backported by Et Futurum by adding 15 new banner icons. They're documented in the Misc part of the book if you have Et Futurum installed (which you should!).
* Lowered goldfish.
* Lowered the Gaia Guardian's arena player search diameter to 31 blocks (from 65)
* The Orechid is now more expensive in Garden of Glass to prevent it from being used to infinitely generate ores and mana using the Coal it produces without an external mana supply.

---

{% include changelog_header.html version="r1.7 213" %}

* Added a config option to disable the 1.9 ender blocks for if you're using Et Futurum.
* Added the ability to use a Cacophonium on a Note Block to make a Cacophonium Block.
* Fixed the Mana Fluxfield not calculating the mana value properly. (LethalRes)
* Patreon supports now have their head flower on flowers named after them.
* Strengthened the ranks of the potato army. Nobody expects the potato inquisition!
* Updated recipes to use the newer ore dictionary keys. (Adaptivity)

---

{% include changelog_header.html version="r1.7 212" %}

* Added the Greater Ring of Magnetization with a larger range. Big magnet!
* Fixed giving another player a relic ring crashing their client.
* Fixed the Rod of the Depths not showing infinite cobblestone when used with the Rod of the Shifting Crust, but rather, infinite dirt.
* Fixed the Rod of the Shifting Crust placing fake blocks in the client side.
* Mana Lenses can now be made with Glass as well as Glass Panes, because if you're low enough that you can't make panes it becomes a pain. *Ha, I made a funny.*

---

{% include changelog_header.html version="r1.7 211" %}

* Added even MORE tiny potato variants :D
* Added Grass conjuration recipe.
* Added Mithril and Platinum ores to the orechid's list. (phantamanta44)
* Added the Rod of the Shifting Crust, effectively a copy of the Thaumcraft Wand of Equal trade with a few new things added on.
* Changed the Rune of Sloth in the Orechid recipe to a Rune of Greed. *Turns out I planned the rune recipes really poorly, the only thing you could make with the greed rune for the longest time was the bore lens. Since the new rod uses a sloth rune I moved this around and now all is balanced :D*
* Fixed a crash when you /give yourself a botania flower with the wrong metadata.
* Fixed more typos.
* Fixed the Corporea Index kicking you out of the server if you try requesting an item with control codes in the name (such as brews).
* Fixed the Flugel Tiara rubberbanding in multiplayer. Maybe. Hopefully. Probably not.
* Fixed the Flugel Tiara using CodeChickenLib Vector3 rather than Botania Vector3, causing crashes if the item is worn without NEI loaded.
* Fixed the Flugel Tiara's sprint delay being 1.5 seconds rather than the intended 3 seconds, also changed it to 2 seconds, so the intended is now 2 seconds rather than 3 seconds. So this line should read "Fixed the Flugel Tiara's sprint delay being 1.5 seconds rather than the intended 2 seconds", but the intended was 3 seconds before and 2 seconds now because 2 seconds feels better than 3 seconds. I don't even know.
* Fixed the Hand of Ender being stackable.
* Fixed the Hovering Hourglass accepting any item through automation and not just sand and not syncing its contents properly when its inventory is changed through automation.
* Fixed the name suffix of the Black Hole Talisman not using the block's damage value to get the name.
* Fixed the Tiara flight bar not rendering with Tinkers Construct. (TheWhiteWolves)
* Rewrote the way the conversion in the Mana Fluxfield works to prevent a dupe with EnderIO capacitors banks.
* The "Items Remaining" display now has colors to make it easier to read if there's multiple stacks left.
* Trying to infuse an item with a Diluted Mana Pool now shows a "Upgrade your Mana Pool to infuse this item" indicator.
* Updated the Force Relay entry to say how to break the block properly.
* [API] Added IBlockProvider, an interface for items that can provide blocks for the Rod of the Shifting Crust or other things.
* [API] Added oreMithril and orePlatinum to the orechid weight table. (phantamanta44)
* [API] IWireframeCoordinateListProvider.getWireframesToDraw is now @SideOnly(Side.CLIENT), because that just makes sense, really.
* [API] Increased version number to 60.

---

{% include changelog_header.html version="r1.7 210" %}

* Added the ability to Dash and Glide to the Flugel Tiara, read on these new abilities in the book!
* Fixed a major dupe with the Resolute Ivy and container items.
* Fixed more typos.
* Fixed the recipe for the spectral platform crashing the client if used with the Shift-? feature in NEI. 
* Hopperhocks now check all 6 sides instead of only the 4 cardinal directions. (iambob314)
* The Flugel Tiara no longer sets you on fire if you run out of charge, that was a cool thematic idea but it ended up just being annoying and dumb.

---

{% include changelog_header.html version="r1.7 209" %}

* Added a recipe for cobwebs.
* Added factorization's new dark iron ore dictionary key to the orechid's list. (TheWhiteWolves)
* Added Relic Knowledge, a new knowledge type for relics. This knowledge is unlocked by opening the lexicon with a relic in the inventory. The contents update dynamically depending on what relic achievements have so far been acquired. This replaces the poems in the relics' tooltips.
* Added the ability for botania armor and baubles to have Thaumcraft runic shielding. (ruifung)
* Added the Slime in a Bottle, effectively a slime chunk detector.
* Added the Starcaller by very popular request. A new sword that makes shooting stars. Yes, it's the Starfury/Star Wrath from terraria.
* Changed the 1.9 brick textures to the actual ones from the 1.9 snapshot jar.
* Changed the element and season runes' textures to new versions in Sosho by miznib.
* Changed the hardcore passive generation value to 48000 by default instead of 24000. *It was supposed to be that to begin with but I can't do math.*
* Dice of Fate dropped by the Gaia Guardian now bind on drop rather than on pickup.
* Fixed baubles that store mana not updating the mana bar above the XP bar in real time.
* Fixed being able to remove water from the Petal Apothecary with a bucket in GoG, allowing for water to be acquired a lot earlier.
* Fixed fake players locking the multiblock display.
* Fixed more typos.
* Fixed some botania entities not having proper localizations.
* Fixed some of the double tall flowers having secondary drops.
* Fixed the Ritual of Gaia's multiblock display only accepting iron rather than all valid beacon blocks.
* Fixed the sashes not being counted as mana using items despite using mana.
* Fixed there being duped text in the runic altar entry.
* Hydroangeas are now quieter.
* Increased the Hopperhock's vertical range by one block.
* Lowered the default and max radius of /botania-skyblock-spread by a tenfold to prevent floating point errors such as hopperhocks not recognising item frames at extreme distances.
* Made the Rod of the Molten Core's sound a lot less infuriating.
* Passive flowers now get replaced with Dead Bushes when they despawn rather than getting removed altogether.
* Prismarine now animates properly.
* Removed the Holy Sword Excaliber and replaced it with something far cooler.
* Removed the regen from the Ring of Odin. It already gives you a full heart bar, come on.
* Replaced the Mana Pool HUD's Sparing/Accepting text with a more pleasing visual.
* Repurposed the Eye of the Flugel a bit, I wasn't happy with what it did before.
* The Dice of Fate no longer hurts players if they're not the right owner.
* The elven portal is now much faster at outputting the stuff you put in.
* The Flugel Tiara now has a limit of how much you can fly with it.
* The Fruit of Grisaia, Ring of Loki and Eye of the Flugel now use mana.
* The Gaia Guardian's difficulty and rewards now scale on the amount of players participating.
* The hardmode Gaia Guardian now drops a lot more runes.
* The Pink Wither no longer drops Nether Stars.
* The Wither effect the Gaia Guardian's purple fire creates can no longer be healed by milk or brew of absorption.
* [API] Add oreFzDarkIron to the orechid weight table.
* [API] Addded relic knowledge to the main class, it's there for completeness sake and shouldn't be used.
* [API] Added LexiconEntry.isVisible(), currently used for the relics but can also be used for other types of dynamic entries.
* [API] Changed the SubTileGenerating despawn to place a dead bush instead of air.
* [API] IBotaniaBoss.bossBarRenderCallback now has some params.
* [API] Increased version number to 59.

---

{% include changelog_header.html version="r1.7 208" %}

* Added the ability to transmute between all vanilla flowers with the alchemy catalyst.
* Added the vanilla Coarse Dirt to the botania creative tab, because ya know, it's craftable with botania and stuff.
* Changed the Blaze Lamp's burn time to be 10x that of a blaze rod rather than 9x. Also made that value half if Garden of Glass is enabled.
* Changed the Cocoon of Caprice's texture to be 64x32 rather than 48x32, that apparently was making it not display properly in some setups.
* Changed the endoflame cap to be that of a Block of Coal Coke. That should encompass all non ridiculous fuels.
* Fixed buried petals displayed an unlocalized name on waila or other name viewing things of the sort.
* Fixed the Cocoon of Caprice not saving its data properly. (Titanium237458)
* The Black Hole Talisman no longer uses up items in creative.
* The Black Hole Talisman now tells you what block is in it in the item's name, it also shows how many are remaining when you place a block.
* The Ghast Tear to Ender Pearl recipe is now a 1:1 ratio conversion.
* The Hopperhock now ignores damage on items that stack up to 1 and can be damaged, it'll also ignore the mana value on anything that can store mana.

---

{% include changelog_header.html version="r1.7 207" %}

* Fixed another crash when opening the Lexicon Index >_>

---

{% include changelog_header.html version="r1.7 206" %}

* Fixed a crash when opening the Lexicon Index.

---

{% include changelog_header.html version="r1.7 205" %}

* Addded a Brew of Absolution that clears all potion effects.
* Addded some tiny potato variants :3
* Added "update notification message" to the version checker config option's description for noobs who just CTRL-F.
* Added the ability to add Emeralds to the Cocoon of Caprice to influence the outcome towards something different.
* Added the ability to take notes in the lexica botania.
* Added the Manastorm Charge, a new fun explosive thing that also happens to have another use...
* Change the Cacophonium texture to not be horrible, thanks wiiv :D
* Changed the color of the Brew of Vanity's Emptiness to be different from the new Brew of Absolution.
* Disabled the number keys in the Floral Pouch to fix a dupe.
* Fixed a visual bug with the Petal Apothecary when a seed is thrown in the middle of two or more Petal Apothecaries that have a complete recipe. *I swear, how do people find these things?*
* Fixed ender brick slabs breaking in one hit.
* Fixed flowers being able to pull mana from and put mana into mana pools or spreaders that no longer exist.
* Fixed the Agricarnation Petite having the wrong range.
* Fixed the tiny potato's jump animation being somewhat jaggy.
* Fixed there being a "botania.sign-1" slot in the Eye of the Flugel under some occasions.
* If a tiny potato is named, the name will only show up when you're looking at it.
* If you lose your Lexica Botania in Garden of Glass you can now exchange a Sapling for it by right clicking with it on the Mana Flame.
* The Endoflame now caps how much each fuel is worth to the value of a Coal Block. It can still accept larger fuels, but they are capped to a Coal Block. *Now, before you start raging and raising your pitchforks around at this, please think about what botania tries to do, and how much super condensed fuels like Aeternalis Fuel Blocks negate that. Ok, you chill now?*
* The Influence lens can now move mana bursts. Have fun with this one :3
* Unclipped secret pretty dev things.
* [API] Added TinyPotatoRenderEvent, because Vexatos would not stop annoying me.
* [API] Changed how isValidBinding() checks for SubTileGenerating/SubTileFunctional.
* [API] SubTileGenerating and SubTileFunctional instances now check isValidBinding() before interacting with the bound collector or pool.
* [API] Increased version number to 58.

---

{% include changelog_header.html version="r1.7 204" %}

* Added an indicator to the generating and functional flower HUDs that shows if they're properly bound or not.
* Added the Cacophonium, a <a href="https://www.youtube.com/watch?v=PwXAcA1M_m0">trolly</a> item :3 (don't hate on the terrible icon, wiiv wasn't around :C)
* Added the Floral Obedience Stick, a way to mass bind flowers, since that's more relevant now.
* Doubled the Pebble drop rate in Garden of Glass. (TheWhiteWolves)
* Fixed a crash with the Force Relay.
* Fixed lens being consumed when attached to a prism in creative mode. (TheWhiteWolves)
* Fixed luminizers sometimes getting unbound on chunkload.
* Fixed torches not being able to be placed on top of botania walls. (TheWhiteWolves)
* Functional and Generating Flowers no longer rebind to the closest spreader/pool if the one they were bound to stops existing. Instead, they'll keep the binding in the position and wait for a new spreader/pool to appear there. Of course you can still rebind manaully. This is a workaround for the bug where flowers would lose or change their binding on chunk unload, which is hopefully fixed now :T
* [API] Added drawComplexManaHUD() and getBindDisplayForFlowerType() to the internal method handler.
* [API] Added functions to force bind SubTileGenerating and SubTileFunctional instances.
* [API] Made the range for SubTileGenerating and SubTileFunctional visible as a constant.
* [API] Rewote SubTileGenerating/SubTileFunctional binding logic, again.
* [API] Increased version number to 57.

---

{% include changelog_header.html version="r1.7 203" %}

* Added the ability to define the range of /botania-skyblock-spread with a second parameter.
* Added Walls to the following botania blocks: Livingrock, Livingwood, Dreamwood, Prismarine, Reed Block, Metamorphoic and 1.8 Stones. *About time...*
* Doubled the damage the Thorn Chakram does. They can also be stacked up to 6 in a single slot, making them much more usable. The recipe makes 2 instead of 1 as well.
* Fixed a dupe with the Spark Tinkerer (TheWhiteWolves).
* Fixed Goldfish ungoldfishifying when it should be goldfishified.
* Fixed the Blood Magic armor and possible other non-enchantable armors being enchantable with the Mana Enchanter.
* Fixed the Guardian of Gaia's music resetting if you die to it.
* The /botania-skyblock-spread command will now check for where the island would be created to prevent it from being created within a 100 block radius of the spawn point.

---

{% include changelog_header.html version="r1.7 202" %}

* Added a /botania-skyblock-spread command for server owners. It sends the target player randomly into the world and created a GoG skyblock for them.
* Added Luminizers, new star like blocks that let you transport players and other things around. <a href="https://www.youtube.com/watch?v=jLeKuUeune0">Here's</a> a demo.
* Added the Detector Luminizer for redstone output on Luminizer paths.
* Added the Luminizer Launcher to get mobs on the Luminizer paths.
* Fixed the Ring of Loki being broken, again. (TheWhiteWolves)
* Retextured the Livingrock Bricks to look less horrible.

---

{% include changelog_header.html version="r1.7 201" %}

* Added an end portal recipe to Garden of Glass.
* Fixed a crash when leaving the nether in a Garden of Glass world.

---

{% include changelog_header.html version="r1.7 200" %}

* In Garden of Glass using a Bowl in water will create a Bowl of Water that can be used to fill the Petal Apothecary. *I can't believe I forgot that >_>*
* *Happy 200th build... yay...*

---

{% include changelog_header.html version="r1.7 199" %}

* **Added the Garden of Glass**. A new skyblock world type with recipe tweaks and some new blocks. This requires a separate jar file to be installed. Check <a href="gardenofglass.php">the page</a> for more info.
* Added a recipe to create snow blocks using water and the Pure Daisy.
* Added Alchemy Recipes to switch between all types of tall grass.
* Added the 1.9 Ender Bricks to the Decorative Ender Blocks section. Textures may not be 100% percise as they were reproduced from the low quality screenshots available.
* Added the Cocoon of Caprice, a new block that can spawn baby animals.
* Changed the End Stone Bricks' textures to be similar to the ones announced for 1.9.
* Fixed a crash with the Pure Daisy's NEI lookup when a recipe with a Block input is registered.
* Made the Pure Daisy more performant.
* Platform blocks now accept any solid block. This includes chisel's but they won't have connected textures.
* [API] Rewrote some code in RecipePureDaisy to be more performant. The matches() signature changed, mod authors must adapt if overriding.
* [API] Increased version number to 56.

---

{% include changelog_header.html version="r1.6 198" %}

* Fixed a crash related to the Elven Portal that someone managed to get and I'm still astounded as to how they managed to do so.
* Fixed the Gaia Guardian breaking blocks on the client side.

---

{% include changelog_header.html version="r1.6 197" %}

* Fixed a crash when clicking the Info button in the gui for a challenge. 

---

{% include changelog_header.html version="r1.6 196" %}

* *I wanted to get this version out today because Terraria 1.3 is coming out and I want to play that. Since the new FTB pack with botania is coming out I wanted to sneak in this feature before I allowed for Terraria to eat up all of my time.*
* Added a challenge system, a new section in the lexicon with suggestions for challenges you can try to test your expertise of redstone and botania mechanics. These should be done using vanilla and botania only, of course.
* Added the ability to duplicate Leaves using the Conjuration Catalyst.
* Removed a debug print I left behind in the Rod of the Skies' code.
* Some of the more important entries in the Misc section are now prioritized.
* The Entropinnyum will no longer make mana if the TNT falls in water.
* [API] Added multiblockPage() to the internal method handler because I forgot last time D:
* [API] Increased version number to 55.

---

{% include changelog_header.html version="r1.6 195" %}

* Added a config to change the height of the mana bar for people who use Dual Hotbars or other mods that change the HUD.
* Added a Multiblock Preview system, that lets you view botania multiblocks in the world as a guideline for building.
* Added the Spark Tinkerer, a new block that can change the augments in Sparks.
* Fixed the Narslimmus being able to absorb slimes that have been absorbed by other Narslimmus already.
* Fixed the Rod of the Skies being abusable for infinite flight.
* Fixed the Soujouner's Sash jump feature not requiring mana. (TheWhiteWolves)
* Replaced every multiblock instruction picture in the lexicon with a 3D render of the multiblock.
* The Gaia Guardian now breaks whatever block its on, it if happens to be inside any. Sorry Bear Trap users.
* When next to a Corporea Index, using TAB will autocomplete the name of the item being written in chat. (SoundLogic)
* [API] Added corporea autocompletion functions to CorporeaHelper and ICorporeaAutoCompleteController. (SoundLogic)
* [API] Added getWorldElapsedTicks() and isBotaniaFlower() to the internal method handler.
* [API] Added Multiblock framework.
* [API] Increased version number to 54.

---

{% include changelog_header.html version="r1.6 194" %}

* Added some new cache handling methods for generating/functional flowers. Trying to fix the good old chunkloading bugs where the flowers change their bindings on chunk unload >_>
* Bookmarks now presist through sessions. *(this is saved in BotaniaVars.dat in your minecraft directory root)*
* Capped the amount of damage the Gaia Guardian can take from a single hit to 12 so you can't just curbstomp it with OP weapons. Sorry!
* Crossmod lexicon entries now have a little display to tell you about them.
* Excaliber's beams no longer home on boss mobs.
* Fixed BoP Malachite getting generated by the Orechid Ignem.
* Fixed the Force Lens being able to move Bedrock.
* Ores from Nether Ores will no longer be generated by the Orechid Ignem if the overworld equivalent is not in the pack.
* The first time you load up the game, you'll be taken to the "Welcome to Botania" entry.
* The Spectrolus now produces double the amount of mana as before.
* [API] Changed the linking methods of SubTileGenerating/Functional a bit.
* [API] Removed oreMalachite from the Orechid's nether table.
* [API] Write SubTileGenerating/Functional's cache to NBT if it exists rather than defaulting to (0, -1, 0).
* [API] Increased version number to 53.

---

{% include changelog_header.html version="r1.6 193" %}

* Added evil dev stuff. And more goldfish.
* Added Nuggets for Manasteel, Terrasteel and Elementium.
* Added the Drum of the Canopy. (TheWhiteWolves)
* Changed some lexicon text about the decay mechanic a bit to be more obvious how it works.
* Changed the procedure for flower binding a little. This may fix flowers sometimes being unable to bind to spreaders after a chunk unload or world reload.
* Changed the way Mana Spreaders and the bursts they fire communicate. This may fix spreaders sometimes being unable to fire after a chunk unload or world reload.
* Fixed the Ring of Loki not working properly. (TheWhiteWolves)
* Hardcore Passive Generation is now enabled by default at 2 days (24000 ticks). This is the config that makes passive flowers decay after some time. Old configs won't be affected, but you should change yours to reflect this, especially if you're a modpack author (please D:).
* Made the Elven Portal's components' recipes to be easier to make.
* Made the Endoflame produce a bit less.
* Mana Petals are now cheaper, making starting off a lot easier.
* The Force Relay now gives a ding sound when properly bound. (TheWhiteWolves)
* (Technical) Introduced a <a href="https://github.com/Vazkii/Botania/commit/cb113050a7aaa8ac88e3326cf59859b33cab9e2d">Pinging system</a> between Mana Spreaders and Mana Bursts. Addon authors must adapt.
* [API] Added IIdentifiable and IPingable.
* [API] IManaSpreader now extends IPingable for the new Pinging system. Addon authors must adapt.
* [API] Modified IManaBurst with new methods for the Pinging system. Addon authors must adapt.
* [API] SubTileGenerating and SubTileFunctional's NBT reads now set the cached spreader/pool to null if the pos Y tag is -1.
* [API] Increased version number to 52.

---

{% include changelog_header.html version="r1.6 192" %}

* Added the Spectral Rail. A new rail that makes carts fly. Woooosh~
* Changed the Spectranthemum to have a limited range and not be chainable. Use minecarts for long range yo.
* Fixed a memory leak with the Hovering Hourglass' render.
* Fixed Corporea requests not updating inventories.
* Fixed lenses being consumed when adding them to spreaders in creative.
* Fixed the Black Hole Talisman being able to place blocks inside the player.
* Fixed the Terra Shatterer's mana display overlapping tooltips when they would go off screen.
* Fixed the yellow quartz Flugel Tiara having pink particles.
* Made the Clayconia's texture more visible.
* Made the mana network, spreaders, pools and flowers more aware of TileEntity invalidation. This may or may not fix the chunkloading issues.
* [API] Added a bunch of isInvalid() checks to SubTileGenerating and SubTileFunctional.
* [API] Corporea properly calls markDirty().
* [API] Increased version number to 51.

---

{% include changelog_header.html version="r1.6 191" %}

* Added a "Welcome To Botania" entry written by GideonSeymour, it's the first entry in the book and sets the tone for new players.
* Added icons to the tutorial pages, they deserve them, good pages.
* Added the Hovering Hourglass, a new sand based timer.
* Fixed 1.8 Stones' Chiselsed Bricks not being craftable.
* Fixed the Terra Shatterer's rank bar displaying if the player has an item in their cursor.
* Fixed the text highlighter being bad at highlighting text in multiple lines. (the message from alfheim page will no longer only be half italicized)
* Made the Soujourner's Sash require a little bit of Mana to work.
* Shift right clicking a special Floating Flower with the Lexica Botania takes you the flower's page rather than the Glimmering Flowers page. (TheWhiteWolves)

---

{% include changelog_header.html version="r1.6 190" %}

* Added a bit more info to the Flower Pouch page, it should've been there to begin with but I forgot >_>
* Added the ability to light an Incense Plate with a Flash Lens. It also has a comparator output now.
* Fixed being able to re-do petal apothecary recipes if the apothecary doesn't have water.
* Fixed Incense Stick missing texture log spam.
* Fixed the Flower Pouch showing a single chest interface when depositing the items in a double chest.
* Fixed the Incense Plate not syncing its inventory to the client when it receives an item from a hopper.
* Lowered goldfish.
* Mana Bursts with the Flash lens can now create flashes in tall grass and other replaceable blocks.
* Retextured the Terra Blade, Crafting Patterns and Placeholders.
* [API] Added hooks for SubTiles to control redstone behavior and light. (SoundLogic)
* [API] Increased version number to 50.

---

{% include changelog_header.html version="r1.6 189" %}

* Added Incense Sticks and an Incense Plate to burn them on. They provide AoE brew effects. *It's as they say, Incense is Wincense.*
* Added the Terra Truncator (Terrasteel Axe), as you'd expect, it breaks whole trees.
* Excaliber's beams now only home on enemy mobs.
* Fixed an exploit involving the Ring of the Aesir and Ring of Odin that allowed the player to get infinite health. Due to its nature, damage already done can't be reversed without save editing tools.
* Fixed botania armorsets supposedly protecting against unblockable damage. (mDiyo)
* Fixed Excaliber's beams only homing on players.
* Fixed more NEI crashes. I think, I can't reproduce them.
* Right clicking a Petal Apothecary or Runic Altar with an empty hand after a recipe is completed places the components of that recipe from your inventory into the block again for another crafting. <a href="https://www.youtube.com/watch?v=GjjZGyYcH9E">And there was much rejoicing</a>, again.
* Terrasteel tools now take more mana to maintain than Manasteel ones.
* The Horn of the Canopy now has a better way to check for leaves. It might even pick up Thaumcraft leaves now, who knows. It also reaches a bit higher.
* The Red Stringed Relay can now connect to double tall flowers.
* [API] Added a function to Brew to prevent it from being infused into incense.
* [API] Increased version number to 49.

---

{% include changelog_header.html version="r1.6 188" %}

* Added Simple Ores and Netherrocks support for the Orechid. (Sinhika)
* Added tall and petite flower alternate textures by Futureazoo.
* Added the Stone of Temperance. A new "upgrade" item that will make the Terra Shatterer not break more than a 3x3 area.
* Beams from Excaliber no longer home on players.
* Dropping the Ring of the Aesir reverts it into its base components.
* Fixed a crash when Corporea Interceptors or Funnels with an empty item frame are triggered.
* Fixed Capricorn and Aquarius being backwards in the Eye of the Flugel. (Lazersmoke)
* Fixed crafting mana Helmets of Revealing removing enchants and ancient wills. (Lazersmoke)
* Fixed petal apothecary not checking properly for the item's name. KitchenCraft seeds now work with it. (tterag1098)
* Fixed the Drum of the Gathering working on the client side and spawning fake cows when used on mooshrooms.
* Fixed the Ring of Loki and Aesir rendering a wireframe below bedrock. (Lazersmoke)
* Fixed the Ring of Loki and Ring of the Aesir displaying the source bounding box when in the inventory and not equipped.
* Fixed the Talent Shredder achievement being achievable with rank S with a specific item. (Lazersmoke)
* Made the Dice of Fate be biased towards positive results.
* Red Stringed Spoofers can't connect to botania special flowers any more.
* The Crafty Crate now automatically ejects its contents if full and no matching recipe is found.
* The Gaia Guardian now only removes ambient potion effects (the ones with transparent particles).
* Typo fixes. (MrKunji)
* [API] Added ore weights to the orechid table for Simple Ores and Netherrocks. (Sinhika)
* [API] Increased version number to 48.

---

{% include changelog_header.html version="r1.6 187" %}

* Added an achievement for breaking the game.
* Fixed a crash when placing down a flower.
* [API] Added a null check to the target collectors or pools in the flower code to prevent NPEs.
* [API] Increased version number to 47.

---

{% include changelog_header.html version="r1.6 186" %}

* Fixed a horrible horrible typo in the "Elven Lore - The Shattering" entry. Previously it would say that Muspelheim collided with Asgard. That's **wrong**, Muspelheim collided with Midgard. Muspelheim is the Nether, Midgard is the overworld, that's why players can travel between the two with a simple portal.
* Fixed flowers sometimes binding to the wrong pool or spreader when their chunk is loaded.
* Fixed the Exoflame's radius being off one block.
* Fixed the Ring of the Aesir not displaying the center wireframe.
* The Terra Shatterer now only places torches when sneaking. Clicking normally toggles it on and off.
* Tweaked the Spark bounding box a bit so it doesn't get in the way as much.
* [API] Fixed broken logic in collector/pool position caching that caused flowers to bind to the wrong collector or pool thanks to chunkloading.
* [API] Increased version number to 46.

---

{% include changelog_header.html version="r1.6 185" %}

* Added M4/M5 (the buttons of the side of the mouse) support to the Lexica Botania.
* Added the Corporea Crystal Cube, a new corporea block that allows for visualization and quick extraction of items from the network.
* Added the Orechid Ignem, a nether version of the Orechid.
* Changed the wireframes produced by the Ring of Loki so that the center one is thicker and easier to distinguish.
* Fake players can no longer spawn Gaia Guardians.
* Fixed a crash when viewing item uses on NEI sometimes.
* Fixed looking up blocks in the FTB wiki redirecting to the Gamepedia homepage.
* Fixed Petite Flowers not being craftable.
* Fixed shovels not being effective against Trodden Dirt.
* Fixed the Eye of the Flugel not working when acquired and needing to be deselected and selected again.
* Fixed the Force Relay's bindings not properly saving to the world's data if not pushed by a piston.
* Fixed the Solegnolia consuming mana even if disabled with redstone.
* Rotating the items in the Item Frames around a Corporea Funnel now changes how many are pulled per request. (1, 16, 32 and 64 for each of the possible rotations)
* The Ring of Loki will now only place blocks when the player is sneaking and the bindings have been set to follow the cursor.
* Tweaked the textures for the cloaks a bit.
* [API] Added an ore table for nether ores for the Orechid Ignem.
* [API] Added CorporeaRequestEvent.
* [API] Added IExtendedWireframeCoordinateListProvider. These names are getting ridiculous.
* [API] Changed the wiki fallback to ftb.gamepedia.com rather than wiki.feed-the-beast.com.
* [API] Fixed the check in RecipeMiniFlower targetting the output stack rather than the output subtile name.
* [API] Removed GregTech ores from the Orechid ore table.
* [API] Using -1 as the itemCount parameter for CorporeaHelper.requestItem() will find every single item it can that matches the matcher passed in.
* [API] Incrased version number to 45.

---

{% include changelog_header.html version="r1.6 184" %}

* Baubles no longer render on players with the invisibility effect enabled.
* Fixed tossing items in mana pools when there's no mana ending up in the items not being stackable with other items.
* Pixies no longer apply potion effects to players. Easiest fix for a crash when a player takes damage from a ComputerCraft turtle.
* Setting an entity's shedding item to an empty value while not setting the rate to -1 will no longer crash the game when opening that entity's page in the shedding entry.
* The Petal Apothecary can now be crafted with any ore dictionary cobblestone slab. Took me long enough.
* Tweaked the way double flowers are placed a bit. This won't have any effect in gameplay but should fix a rare crash on worldgen.

---

{% include changelog_header.html version="r1.6 183" %}

* Added Petal Apothecary variants craftable using the stone acquired with the Marimorphosis.
* Fixed a crash when looking at an item in the lexicon that doesn't have a recipe present elsewhere.
* Fixed Petite Rannuncarpus's range not being affected by mana. (xaeroverse)
* Fixed the sashes resetting step height when changing dimensions, again >_>
* Hashed all the codes.
* Hovering over a Terra Shatterer now shows a bar that displays how much mana it has relative to the next level.

---

{% include changelog_header.html version="r1.6 182" %}

* Added Petite flowers, small variants of functional flowers that have a smaller range. Currently there's 6: Agricarnation, Bellethorne, Bubbell, Hopperhock, Marimorphosis and Rannuncarpus.
* Baubles can now have Phantom Ink.
* Fixed Corporea Funnels destroying items if there's no inventory for them to put stuff in.
* Fixed the Corporea Interceptor crashing the game if an invalid request is thrown.
* Fixed the Minecart with a Mana Pool returning a normal Minecart when right clicked.
* Fixed the Soujourner's and Globetrotter's sashes needing to be re-equipped on login and dimension change.
* Shift-right clicking a flower pouch into a chest or other container drops all of its contents into it.
* The Lexica Botania will no longer prompt you to shift click to view a recipe if that recipe lies in an entry from a knowledge source you haven't unlocked yet.
* Tyopo fixes. (MrKunji)
* Typo fixes 2: Electric Boogaloo. (Xottab-DUTY)
* Typpo fixes 3: Letter Revolution. (Nincodedo)
* (Technical) All SubTileEntities registered now get their signature's registerIcons method called, not only those that were set to show up in the creative menu.
* [API] Added getAllSubTiles().
* [API] Added getStackSubTileKey() to the internal method handler.
* [API] Added support for petite flowers in the form of a mapping, recipe list and a new RecipeMiniFlower class.
* [API] Increased version number to 44.

---

{% include changelog_header.html version="r1.6 181" %}

* Added the Corporea Interceptor, a new corporea block that intercepts requests and emits a redstone signal if they're not successful. Automation~
* Changed Corporea Funnel texture sides.
* Fixed Dreamwood Slab and Dreamwood Plank Slab's pick block in creative being flipped.
* Flowers that come from addon mods will now have a line in the tooltip saying so.
* Made the Soujourner's Sash not aggressively set the player's step height again. This fixes it not resetting when it's unequipped.
* [API] Added ICorporeaInterceptor for corporea blocks that wish to intercept requests.
* [API] Added IManaSpreader for Mana Spreader blocks and mana burst interaction. (SoundLogic).
* [API] Added setFake() to IManaBurst. (SoundLogic)
* [API] Fixed VanillaPacketDispatcher depending on the internal MathHelper class.
* [API] LexiconEntries now have a getWebLink() function that can be used to have a custom "View Online" button. This removes the need to check for IAddonEntry when adding the button.
* [API] Registering a SubTile now binds the current mod ID to the key.
* [API] SubTileSignatures now have access to the tooltip list.
* [API] Increased version number to 43

---

{% include changelog_header.html version="r1.6 180" %}

* Added "*" as a corporea wildcard. (shadowfacts)
* Added vuvuzela.
* Fixed a crash when pushing blocks towards the pure daisy with a piston under some odd circumstances I don't properly know.
* Fixed Thermalilies getting the same diminishing returns as hydroangeas.
* (Technical) Fixed ElvenPortalUpdateEvent never getting posted to the event bus.
* [API] Added "*" to the WILDCARD_STRINGS array in CorporeaHelper. (shadowfacts)
* [API] Added a null check the ItemStack and its Item for RecipePureDaisy.isOreDict.
* [API] Increased version number to 42.

---

{% include changelog_header.html version="r1.6 179" %}

* Fixed the Pure Daisy not working >_>
* [API] Flipped world.isRemote check in RecipePureDaisy.
* [API] Increased version number to 41.

---

{% include changelog_header.html version="r1.6 178" %}

* Added a Blaze Light, a light source block that also works as fuel and can be put next to a Pure Daisy to make Obsidian.
* Added a Pure Daisy NEI recipe handler.
* Aside from "...", "~", "+" and "?" can now also be used as Corporea Index wildcards.
* Disabling capes in Multiplayer Options disables the golden head flowers for contributors too.
* Fixed a bug relating to weird chunkloading behaviour where spreaders would create thousands of burst entities in the same place that would simply exist, not update, and nom at the server's tickrate, a lot.
* The Pure Daisy now uses a proper recipe system instead of the two hardcoded values it had previously. To that there's a few new recipes such as Netherrack to Cobblestone, Soul Sand to Sand or Ice to Packed Ice.
* The Ring of Magnetization will no longer pull Relics.
* Vine Balls can now be used to craft Mossy Stone.
* [API] Added a RecipePureDaisy and a list of these. I don't really need to explain what it does, right?
* [API] Added a WILDCARD_STRINGS array to CorporeaHelper and changed the wildcard check to reference it.
* [API] Added ElvenPortalUpdateEvent.
* [API] Increased version number to 40.

---

{% include changelog_header.html version="r1.6 177" %}

* Added a config option to disable the 1.8 stones.
* Added oredict entries for the 1.8 stone and prismarine blocks for better compatibility with Gany's Surface.
* Fixed Ancient Wills only rendering on the Terrasteel Helm if it has Phantom Ink, rather than only rendering when it doesn't have Phantom Ink.
* Fixed Enchanted Soil being able to be picked up with Silk Touch.
* Fixed a crash when shift-clicking damanged non-flower items into the Flower Pouch.
* Fixed a dupe with the Ring of Loki that allowed for seeds, and potentially other items of the sort, to be infinitely generated.
* Fixed crash with Corporea Sparks and buildcraft gates.
* Fixed the Mana Pool sending way more packets than it should because I forgot to reset the flag >_>
* Fixed the Ring of Loki not syncing properly in multiplayer.
* Fixed the Solegnolia not being shift-right clickable to open its lexicon entry.
* Fixed the wireframes for the Ring of Loki rendering when it's not equipped.
* Hydroangeas now get Daybloom/Nightshade like diminishing returns in all 8 directions.
* Made some lexicon text and relic poem changes.
* Relics kill you slower if you're not their owner.
* The Elven Portal will now stop functioning if any of its components are in unloaded chunks, it will not dismantle itself like it did before.
* The following items can no longer drop from the Loonium: Lexica Botania, Black Lotus, Overgrowth Seed and all records.

---

{% include changelog_header.html version="r1.6 176" %}

* Added some code to atempt to fix chunkloading conundrums with the Mana Spreader. This might fix the bug where a bunch of bursts pile up in one position when there's a chunkloader there. I can't tell for sure.
* Fixed Mana Spreaders dropping wool every time when broken.
* Hydroangeas now detect when they have an infinite water source and don't remove the water or cause block updates in that case. Better performance!
* Mana Pool packet load when receiving bursts is now throttled to one packet per 10 ticks. Previously it would send a packet per burst.
* Mana Spreaders no longer send packets when they receive bursts. That wasn't needed since it doesn't have a visual change.
* Right clicking with a water bottle on a water block in the end will no longer create a bottle of ender air.
* Tweaked the packet dispatcher to maybe work with GalacticCraft when some other mod that changes stuff is loaded. Don't ask me if it works or not because I have no clue. There was a weird crash somewhere in there.
* [API] Added IThrottledPacket.
* [API] Removed generic type from the world.playerEntities list in VanillaPacketDispatcher with hopes to please some mod's dumb ASM nonsense.
* [API] Increased version number to 39.

---

{% include changelog_header.html version="r1.6 175" %}

* Added 1.8 stones (Andesite, Diorite, Granite and an extra, Basalt) as mana infusion recipes alongside with some variants and stairs/slabs.
* Added a config option to disable Relics.
* Added a config option to disable the fire resist for the Ring of Odin.
* Added shiny golden flower renders to the heads of contributors.
* Added Sunny Quartz, new yellow quartz made with Sunflowers. It has corresponding wings too.
* Added the Bubbell, a new flower that makes underwater bubbles of air where you can build.
* Added the Solegnolia, a flower that can disable the Ring of Magnetization's effect.
* Added two awesome tracks of battle music by Kain Vinosec to the Gaia Guardian fight. You can also get these tracks in music disc form as a drop.
* Brazier Petal Apotecaries now emit light.
* Changed the Marimorphosis' biome dictionary tag for the Taiga stone from SNOWY to COLD so it works properly in Taiga biomes.
* Changed the text in the Lexica Botania's landing page a bit.
* Changed the url in the mcmod.info file from vazkii.us to botaniamod.net.
* Corporea Sparks will now properly respect the ISidedInventory protocol and not pull items from slots that can't be extracted from the top.
* Fixed a Stack Overflow crash when a player with a Cloak of Sin attacks or is attacked by a mob from Infernal Mobs with the Vengance attribute.
* Fixed being able to add Timeless or Resolute Ivy to an item that already has it.
* Fixed Books being able to go in the Mana Enchanter.
* Fixed Corporea Sparks not behaving properly with JABBA Barrels and Storage Drawers (from the mod that name). They'll only see 64 of the item but will not destroy items any more like they used to.
* Fixed Double Flowers sometimes dropping Tallgrass and other things like that when sheared.
* Fixed Mana Spreaders not dropping their lens or sleeve when broken.
* Fixed the Changelog and Website buttons in the auto-updater using the old website instead of the new one.
* Fixed the Flugel Tiara's tooltip not having the "SHIFT for more info" that all the other baubles have.
* Fixed the Lexicon's lookup feature picking up liquids and fake air blocks.
* Fixed the Loonium not having a display when looked at with the Manaseer Monocle.
* Fixed the Manasteel/Elementium Pick and Axe not removing the last item from the inventory but a ghost stack instead when their right click ability is used.
* Fixed the Resolute Ivy being able to dupe items in the crafting grid.
* Fixed the Ring of Odin displaying a fake player death animation when unequipped.
* Having the <span class="censored">----</span> <span class="censored">--</span> <span class="censored">----</span> equipped increases the Terra Shatterer's level by 1.
* Increased <span class="censored">---</span> <span class="censored">-----</span> <span class="censored">--</span> <span class="censored">-------</span>'s saturation output.
* Increased Thermalily cooldown from 6 minutes to 7 minutes.
* Made the <span class="censored">----</span> <span class="censored">--</span> <span class="censored">----</span>'s regen effect slower.
* Optimized flower (and other botania tile entities) client load on syncing by not marking blocks for update and rather directly sending packets to nearby players.
* Optimized spark network usage and processing. They should work a lot better, both fps and tps wise.
* Replaced Flugel Tiara Firelord Wings with Phoenix Wings.
* The Black Hole Talisman will no longer keep a stack of the specified block in the inventory, block placing is done through right clicking with it exclusively now. Putting the talisman in a crafting table will allow one to extract a stack of the block at once.
* The Hardmode Gaia Guardian can now drop records.
* The ingame downloader now downloads the file as a .jar.dl and renames it to a .jar when done to prevent loading of broken mods if any interruptions happen.
* [API] Added a blacklist for the Loonium.
* [API] Added canExtractItem check to CorporeaHelper.isValidSlot.
* [API] Added VanillaPacketDispatcher to the internal namespace.
* [API] Increased version number to 38.
* [API] Replaced dumb stack size subtractor from CorporeaHelper in favor of a decrStackSize() call.
* [API] SubTileEntity.sync() now calls the method from VanillaPacketDispatcher rather then using markBlockForUpdate().

---

{% include changelog_header.html version="r1.6 174" %}

* Added an achievement button to the lexicon main page.
* Agricarnations will only fertilize blocks that can normally be grown with bone meal.
* Changed the documentation for Daybloom diminishing returns to say that it doesn't apply to other flowers, since people don't seem to get it :V
* Fixed a dependency on codechickencore when using the Excaliber.
* Fixed the Excaliber not being enchantable.
* Fixed the Eye of the Flugel not binding to a player.
* Fixed the Ring of Loki not working past the 4.5 block range of survival.
* Relic items no longer despawn naturally. They can still be destroyed by explosions, lava, etc, so you should still be careful.
* Shift-right clicking a cursor while editing with the Ring of Loki will now remove it.
* Subtle change to the ring of thor's tooltip to make it a bit more obvious.

---

{% include changelog_header.html version="r1.6 173" %}

* **Added Relics.** New unique, soulbound items with really powerful effects. There's currently 6 of them along with 2 extras.
* <span class='april-fools'>**Added the ability to enter the Alfheim dimension through the portal if you have a specific item...**</span>
* <span class='april-fools'>Added fluid mana that you can store in railcraft, buildcraft, and so on, tanks. The conversion rate isn't 1:1 if you do it both ways.</span>
* <span class='april-fools'>Added multiblock mana pools, 3x3, 5x5 and even 7x7. They store way more than normal ones, as expected.</span>
* Added radius viewer functionality to the Manaseer Monocle. While looking at a flower with it equipped one will be able to see the flower's effective range visually.
* Added the Black Hole Talisman. It's the unlimited cobblestone works. *Wait, that's from thaumic tinkerer. STOP STEALING CODE FROM YOURSELF VAZKII YOU SCUMLORD*
* Added the Resolute Ivy, an Ivy item similar to the Timeless Ivy that lets you keep the item it's attached to through death.
* <span class='april-fools'>Added the Reverse Mana Fluxfield that transforms RF to mana. Again, the conversion rate isn't too generous so get your big reactors dusted.</span>
* <span class='april-fools'>Added the Spriggan Mana Tablet, an upgrade to the normal Mana Tablet using Dragonstones that holds 10x as much.</span>
* Botania tile entities no longer register non-prefixed names (eg, "pump" rather than "botania:pump"). This fixes incompatibilities with mods that have similar names. Since both names were registered previously, transition will work with no problems.
* <span class='april-fools'>Changed the Gaia Guardian to be able to be killed by fake players after I got spammed with requests for it.</span>
* Edited the Thaumcraft Integration page to tell the user that the Botanurgist's Inkwell needs to be charged when created.
* Entries that come from botania addons will not have the "View Online" button.
* Fixed Ancient Wills rendering on a Terrasteel Helmet that has Phantom Ink.
* Fixed double flowers being able to be duplicated through bone meal.
* Fixed glimmering flowers being able to be bonemealed to create double flowers.
* Fixed the Hyacidus, Pollidisiac, Tigerseye, Narslimmus and Medumone having their range be lower in one side.
* Fixed the Phantom Ink recipe working if more than one piece of armor is put in the grid.
* Floral Powder can now dye sheep.
* Moved the Life Aggregator to the Ender Artifacts section and made it require two Bottles of Ender Air.
* <span class='april-fools'>Redid all the flowers, spreaders and pool HUDs. They now show exactly (in numbers) how much mana the blocks have, are producing and are receiving.</span>
* <span class='april-fools'>Removed the cooldown on Thermalilies and returned their production rate to the original speed they were at before.</span>
* The Mana Pump will not stop outputting a comparator signal once it stops pumping as long as it has a cart on the rail.
* <span class='april-fools'>[API] Added IManaFluidContainer and IRFManaAcceptor for compatibility.</span>
* [API] Added IRelic, IWireframeCoordinateListProvider, ISequentialBreaker.
* [API] Added RELIC EnumRarity.
* [API] Added breakOnAllCursors() to the internal method handler.
* [API] Increased version number to 37.

---

{% include changelog_header.html version="r1.5 172" %}

* Fixed a crash when right clicking a special Floating Flower.
* Fixed a crash when using a Corporea Funnel with an empty Item Frame.
* Fixed a localization issue with the double flowers that ticked WAILA off. (61352151511)

---

{% include changelog_header.html version="r1.5 171" %}

* Added 4 new achievements.
* Added Double Flowers. They're tall and pretty, can be made into double the petals and can be grown with Bone Meal on normal flowers. They can only be harvested with shears, please understand.
* Added The Pinkinator. What does it do? How do you get it? Heck if I know...
* Changed the flower world generator to generate flowers in patches and also generate the double flowers. There's 3 new config options to tweak this new method. Old configs will still work but results will be different, modpack makers should check that the results are still satisfactory.
* Fixed a bucket dupe in the petal apothecary.
* Fixed possible division by zero when justified text is enabled.
* Right clicking on a Floating Flower with Pasture Seeds, Boreal Seeds, Infestation Spores or Snowballs will change its look.
* The Exoflame will now ignore other mods' Furnace blocks unless they specifically opt in with the API interface. Fixes a dupe with Natura Nether Furnaces.
* The Mana Pump can now be controlled with Redstone and will output through a Comparator the amount of mana in the cart.
* The Pure Daisy will only place blocks in server worlds, fixes it happening too soon in clients connected to servers with subpar tps.
* (Technical) Added an IMC handler for blacklisting items from the Ring of Magnetization. (Lomeli12)

---

{% include changelog_header.html version="r1.5 170" %}

* Added a Minecart with Mana Pool and a Mana Pump to go along with it.
* Added Phantom Ink. Can be crafted with a piece of botania armor to hide the armor model in the player.
* Added the Flower Pouch. You guessed it, it stores flowers. <a href="https://www.youtube.com/watch?v=GjjZGyYcH9E">And there was much rejoicing.</a>
* Added the Horn of the Covering. An alternate version of the Horn of the Wild that clears snow.
* Fixed a crash when crafting with a bauble that has a cosmetic override.
* Fixed brew vials and flasks being repairable.
* Fixed Mana Pools being able to go into negative mana under some circumstances.
* Fixed the Corporea Index being finnicky with Y distance.
* Fixed the Jaded Amarathus not being inclusive of other mods' soil blocks.
* Fixed the Orechid spawning GregTech ores by making it ignore GregTech ores altogether.
* Floating Rannuncarpus flowers will now use the block directly below them rather than the block 2 blocks below.
* Removed a debug print in the Ring of Correction.
* Removed unecessary updates from the Rosa Arcana. Should make it not lag.
* Renamed the Void Lotus to Blacker Lotus.
* Separated the Mana Mirror and Mana Tablet entries.
* The decay time in passive flowers will now be saved to the droped stacks if the decay option is enabled.
* The Spectranthemum can no longer teleport Mana Tablets and other mana containing items.
* [API] Added IPhantomInkable.
* [API] Increased version number to 36.

---

{% include changelog_header.html version="r1.5 169" %}

* **Rebalanced mana generation.** All values are relative to the values previous to this release. These numbers may change back slightly if any of the changes proves too drastic.
  *   Dayblooms had their delay reduced, now 20% faster overall.
  *   Nightshades produce exactly as much as Dayblooms. So 140% faster overall.
  *   Hydroangeas will instantly grab water after they're done with the last drink. A single block of water lasts for 4x as long to prevent sound/update spam. 127% faster overall.
  *   Rosa Arcanas will produce twice as much mana per XP point. They also have 4x as much of an internal buffer. 100% faster overall.
  *   Endoflames had no changes.
  *   Munchdews had the amount that a leaf produces increased tenfold and their internal buffer increased fiftyfold. If they spend more than 5 ticks without eating any leaves they'll go in a 80 second cooldown period. 900% faster overall.
  *   Gourmaryllises have a higher multiplier for the amount that a piece of food produces and a larger internal buffer. 255% faster overall.
  *   Narslimmuses have a higher multiplier for the amount that a slime produces. 412% faster overall.
  *   Thermalilies had their cooldown lowered from 8 minutes to 6 minutes. 31% (+ a negligible number thanks to inheriting the dilligence upgrade from the Hydroangeas) faster overall.
  *   Entropinnyums had no changes.
  *   Kekimuruses had their cooldown increased and amount of mana per slice drastically increased. 75% faster and 181 more resource efficient.
  *   Spectrolususe had no changes.
* Added an options button to the Lexica Botania's main page.
* Added justified text to the Lexica Botania's text pages as a config option.
* Entropinnyums now muffle the sound of an absorbed TNT explosion.
* Fixed an exploit where Thermalilies could be broken before the cooldown period starts, skipping the cooldown entirely.
* Fixed an exploit with the  Gourmaryllis where more than one can eat a single item at once, producing much more mana than intended.
* Fixed Mana Bursts with Bore Warp lenses fired from a Mana Spreader destroying dropped items.
* Rewrote the text rendering algorithm for the Lexica Botania's text pages to be less of a mess and not bug out around some formatting codes.

---

{% include changelog_header.html version="r1.5 168" %}

* Fixed a (recurring) crash with the Gaia Guardian that would happen if there were no players around it. Naturally this only affected servers, but it's a big problem so I decided to make a build to fix it. *And it was also crashing forgecraft :T*

---

{% include changelog_header.html version="r1.5 167" %}

* Added a Word Counter thing that gets written to the log saying how many words the Lexica Botania has. *Don't ask me why, I just felt like it >.<*
* Added feature creep to the Lexica Botania in the form of a "View Online" button as well as a Lookup History (similar to your browser's history).
* Fixed a crash with looking at a block that doesn't have a mod registered to it somehow with the Lexica Botania.
* Fixed Dayblooms and Nightshades not showing the particles for diminishing returns.
* Fixed the Pure Daisy having the wrong icon in the lexicon index.
* Fixed the recipe for Metamorphic Stone Bricks outputting 1 rather than 4.
* Removed debug print in the lexicon.
* Sparks no longer emit particle beams when placed in the world, they still do when right clicked with a wand.
* The Terra Shatterer can now mine Ardite and Cobalt from Tinkers Construct as well as other ores of harvest level 4.
* [API] Added failsafe for WikiHooks.getWikiFor
* [API] Increased TERRASTEEL armor material's harvest level to 4.
* [API] The passive generation on SubTileGenerating will now only work in !World.isRemote. Furthermore, overrides of canGeneratePassively() should no longer check for World.isRemote.
* [API] Increased version number to 35.

---

{% include changelog_header.html version="r1.5 166" %}

* Added a little note to the bottom of the lexicon pointing to the fact that you can search. The search bar has been there for ages but it's often overlooked.
* Added NEI recipe handling for floating special flower recipes. (Tonius)
* Added some secret codes. *(these are client only, server owners don't need to worry :3)*
* Fixed a crash when the alfheim portal is clicked with a wand of the forest from a dispenser.
* Fixed all sounds depending on the "Friendly Creatures" volume bar and actually split them properly.
* Fixed some typos. *I can into english*.
* Fixed the Gaia Guardian's health bar flickering weirdly when it's spawning in multiplayer.
* Fixed the Hardmode Gaia Guardian's drop table having Mana Pearls twice rather than Mana Pearls and Mana Diamonds.
* Fixed the Livingwood and Crystal bows not having a proper render.
* Fixed the Mana Mirror trying to update on the server side, causing some desyncs sometimes. I think. I can't reproduce this issue so this is just a wild guess.
* Fixed the tutorial arrows never pointing properly to the next/prev page buttons in the index pages, causing the tutorial to get stuck if the book had elven knowledge.
* Implemented regex based fake player checking. Not 100%, but should catch most of them.
* Mana Spreaders can now be sleeved with wool by right clicking them with a wool block. This makes them colorful and fancy and also muffles the shooting sound.
* Pylons now give out some light.
* Renamed some achievements.
* Replaced instances of ticksExisted with an internal timer in the magic missile class used by the hardmode Gaia Guardian and the Rod of the Unstable Reservoir. May or may not fix hangs with cauldron servers.

---

{% include changelog_header.html version="r1.5 165" %}

* Fixed a spark crash when trying to remove an augment.
* Fixed Livingwood Bows not consuming arrows.
* Fixed missing icons "mushroom16" and "corporeaIndex" being thrown to the console. *I take this very seriously, it's annoying >.<*
* Fixed the config option for mushrooms being documented as having the 10 as the default rather than 40. Also added "botania" as a prefix to the worldgen options' descriptions to prevent confusion.

---

{% include changelog_header.html version="r1.5 164" %}

* Added **32** cosmetic override items that can be crafted with baubles to replace the bauble's look with the one of the cosmetic item.
* Added a Crystal Bow, a bow that creates arrows with mana and fires faster than normal.
* Added a Livingwood Bow that acts as a normal bow but repairs with mana.
* Added decorative glowing mushrooms of all 16 colors. You can also find them underground. (Yes there's a config, get off my lawn!)
* Added Mana Infused String as a crafting component.
* Added the Ring of Correction. A ring that will swap your active tool (the one you have in hand) with the correct one for the block you're trying to break, works for mana tools only.
* Changed the Terrasteel crafting page to say that there actually is a piece of livingrock below the plate, since nobody seems to get it.
* Fixed fake players being able to equip baubles via right click, which led to a crash with Autonomous Activators from TE.
* Fixed the background of lexicon entry buttons being black if the entry is priority elven knowledge.
* Fixed the Elementium Sword giving a 50% boost to pixie spawn chance rather than 5%.
* Fixed the Petal Apothecary eating Buckets. Also it being able to have water AND lava at once, what's up with that?
* Fixed the Terra Blade not being able to actively repair with mana.
* Increased the priority of the Corporea Index chat interceptor so it happens before serverside ForgeIRC has a chance to broadcast the message over to the channel.
* Optimized Spark code.
* [API] Added ICosmeticBauble, ICosmeticAttachable, ISortableTool.
* [API] Increased version number to 34.

---

{% include changelog_header.html version="r1.5 163" %}

* Added kawaii.
* Fancified the lexicon index pages.
* Fixed Metamorphic Stone Slabs breaking in one click.
* Fixed the Force Relay being able to destroy blocks in the world (Bedrock included). Also fixed a rare crash that happened randomly it seems.
* Right clicking on a Petal Apothecary with an empty bucket removes the water. (gr8pefish)
* [API] Added an icon ItemStack to LexiconEntry. This is automatically initialized through recipe outputs through LexiconRecipeMappings.
* [API] Added oreDark (from Evilcraft) to the orechid weight map.
* [API] Increased version number to 33.

---

{% include changelog_header.html version="r1.5 162" %}

* *This line is here only to make the changelog look larger than it actually is. Did it work?*
* Fixed the Corporea Funnel not being in the creative tab.
* Fixed the Ring of Magnetization not ignoring AE2 crystal seeds. *Fireball tricked me with the wrong IDs last time but this time I foiled his plans to foil my plans by getting them right, muahahaha! ...I'm tired.*
* Reordered some things in the mana part of the creative tab to make more sense. *I'm very perfectionist.*

---

{% include changelog_header.html version="r1.5 161" %}

* Fixed server crash >.<

---

{% include changelog_header.html version="r1.5 160" %}

* *Huh, 1.4 was shortlived.*
* **Added a new "Ender Artefacts" category to the Lexica Botania**, it contains new and old ender related things. Some entries from other categories have been moved over to this.
* **Added a Tutorial Mode** which guides you through the pages you should read, in order, to get a good understanding of the mod's basics.
* **Added Corporea, a new mechanic that allows for item verbal requesting.** <a href="https://www.youtube.com/watch?v=1HcUMVJKoX8">Here</a>'s a video of it in action while it was still in development, it looks a bit cooler now.*This is by no means meant to be an AE replacement or competitor, just throwing it out there.*
* Added a Bottle of Ender Air, used as a crafting material and as means to create renewable End Stone.
* Added End Stone Bricks and Chiseled End Stone Bricks. *It's totally a good addition, stop judging me!*
* Added more alternate textures in the Unity texture pack style by Tobbvald to join those by Futureazoo, you can enable them in the config file.
* Added some disclaimers to the lexicon that shears made of mana materials will trigger tripwire and that blood magic messes with the fallen kanade (if BM is loaded).
* Changed some recipes to use a Bottle of Ender Air.
* Changed the creative tab so all the fluff (decorative blocks and the like) is right at the end rather than in the middle.
* Changed the texture for Livingwood, Dreamwood and the Lexica Botania to new, more detailed textures also by Tobbvald.
* Fixed a timing related dupe with Force Relays.
* Fixed the barrier display of the Gaia Guardian being 1 block off.
* Fixed the Hardmode Gaia Guardian's missiles never despawning and causing a load on the server.
* Fixed the Thorn Chakram and Enchanted Soil not being in the creative tab.
* [API] Added hooks for corporea stuff.
* [API] Added the new ender category to the api.
* [API] Fixed the tools category being priority 0 rather than 5.

---

{% include changelog_header.html version="r1.4 159" %}

* Added Petal Blocks of all 16 colors, purely decorative blocks made out of petals.
* Changed the textures of the elfen rods to be made of dreamwood instead of livingwood.
* Fixed a crash with the Force Relay and Warp Lens.
* Fixed particles being able to spawn in the client world and chunkloading somehow.

---

{% include changelog_header.html version="r1.4 158" %}

* Added a new Mana Lens: Warp. It teleports bursts around. It lets you make quarries (good luck with that).
* Fixed potions IDs being set to a value over 127 by default and added a check to change the old configs to a new proper value, networking passes the values as a byte, which means anything over 127 got messed up and crashed the game.
* Fixed the particle render messing up other mods' renders when very far from 0,0. (Gwani)
* Made the Rod of the Unstable Reservoir and Rod of the Shaded Mesa use Dreamwood Twigs in the crafting recipe.
* [API] Added setCollidedAt to IManaBurst
* [API] Updated version number to 31

---

{% include changelog_header.html version="r1.4 157" %}

* Added a sanity check in the case that a brew is null for some reason, previously it would crash the nei lookup.
* Changed the film grain shader again to fix AMD cards not working, again. I don't have an AMD card to test so fingers crossed it works this time.
* Fixed Overgrowth Seeds not generating as chest loot.

---

{% include changelog_header.html version="r1.4 156" %}

* Fixed a crash when the Elementium Axe killed a player and dropped a skull.
* Fixed the game completely freezing on the mojang splash screen on some AMD graphics cards. Technical details can be found <a href="https://github.com/Vazkii/Botania/commit/f8b0f2155d97944fcec7ab315864bb5f86ed9f54">here</a>.
* Fixed the Mana Enchanter adding enchantments twice to items that already have an NBT compound for some reason.
* Fixed the Trodden Dirt Slabs giving the wrong block when picked in creative.

---

{% include changelog_header.html version="r1.4 155" %}

* Added a proper config for potion IDs rather than letting them auto-assign.
* Changed the Ring of Chordata to check for air levels a bit better so it works properly with Mariculture.
* Fixed Ancient Wills working without the full armor set.
* Fixed Black Lotuses spawning on jungle temple dispensers rather than chests.
* Fixed Mobs' AI getting stuck in the terrasteel plate.
* Fixed the floating flowers with effects being affected by Enchanted Soil.
* Fixed the mana cost bar rendering more than once in the lexicon or NEI if a recipe with more than 100k mana cost was inserted via the api or minetweaker.
* Moved the Crafting Table HUD icon on the Assembly Halo down a bit so it doesn't render behind the text.
* Moved the Greater Band of Aura's output back a bit, it's not as strong as it used to be, but it's a bit better than last update.
* Removed the text that states Terrasteel Armor has HP boost as that was removed last version.
* The Gaia Guardian now has slighty better fake player checker code and can no longer be killed by TT dynamism tablets. Sorry!
* The Greater Bands of Aura and Mana are now crafted both with a single Terrasteel Ingot rather than two.
* The Soujourner's and Globetrotter's Sashes now actively try to change step height rather than on equip only.
* Thermalilies are now faster and make more mana per bucket of lava. However, after they go in a cooldown period after consuming a bucket. Also removed the obsidian config option as it's pretty useless anyway and made it non-elven knowledge again.
* (Technical) The lexicon's GUI now keeps the ItemStack used to open it to render the name, rather than pulling it from the current equipped stack.

---

{% include changelog_header.html version="r1.4 154" %}

* *Holy moley, that's a long changelog. I apologize in advance if anything in this build is broken. I don't do "exclusive beta testing", and since this is a large version there'll probably be some kinks. I'll try my best to iron them out over the next couple of days!*
* Added 25 achievements, 20 are progression/side things and the other 4 are special challenges. *There's also one more achievement but we don't talk about that one.*
* Added 5 new flowers.
  *   Spectranthemum, functional, teleports items from around it to a defined point in the world. (Elven Knowledge)
  *   Medumone, functional, freezes mobs in place.
  *   Marimorphosis, functional, turns standard vanilla Stone into colored, decorative, stone variants depending on biome.
  *   Narslimmus, generating, kills natural spawned slimes in slime chunks for mana, doesn't drop slimeballs or split them in two.
  *   Spectrolus, generating, uses all 16 colors of wool sequentually to create mana.
* Added a "Complex Brews" page, with 6 new brews, 5 featuring new potion effects.
  *   Overload: A bunch of effects, Strength, Speed, Weakneess and Hunger.
  *   Crossed Souls: Killing a mob gives the killer a bit of their HP.
  *   Feather Feet: Fall damage immunity.
  *   Vanity's Emptiness: Prevents mob spawns in a 128 block radius.
  *   Crimson Shade: Removes mob spawn restrictions in a 64 block radius. (eg. Mobs can spawn in broad daylight, slimes in non-slime chunks, etc)
  *   Marine Allure: Doubles the speed at which fishing rods work.
* Added a Gaia Spirit Ingot, used to summon a hard mode Gaia Guardian, it's harder, better, faster and stronger. It also drops moar lootz.
* Added Ancient Wills. Items that can crafted with a Terrasteel Helmet to provide for very powerful abilities. How do you get them? Who knows.
* Added armor set bonuses to sets of mana armor that apply when all 4 pieces are equipped.
  *   Manasteel: 10% Mana Discount for Mana Tools.
  *   Elementium: 10% Mana Discount for Mana Tools, pixies apply random potion effects.
  *   Terrasteel: 10% Mana Discount for Mana Tools, regeneration still works even when the food bar isn't topped off.
* Added Crafting Patterns, upgrades of sorts to the Crafty Crate which prevent items from going into slots. There's 9 basic ones that can be used for simple recipes: 1x1, 2x2, 1x2, 2x1, 1x3, 3x1, 2x3, 3x2 and Donut.
* Added Dreamwood Twigs, used to craft Elementium Tools instead of Livingwood Twigs.
* Added Mana Diamond and Dragonstone storage blocks.
* Added Overgrowth Seeds. A new dungeon loot item that allows you to enchant a piece of grass, making any flowers on it work at twice the speed.
* Added Sanity Checks to the lexicon GUIs to prevent crashing if one tries to open a page that doesn't exist. This is important for third party minetweaker support. (SoundLogic)
* Added Spellbinding Cloth. Combine it with an item to remove any enchants it has. *This one actually is a carbon copy of the one in Thaumic Tinkerer, this item is like a legacy in my mods. Surprised I hadn't stolen it from TT yet :V*
* Added the Lens Clip, an addon to the Mana Blaster that allows it to carry 6 lenses rather than just 1.
* Added the Manaseer Monocle, a new amulet bauble that allows the wearer to see mana bursts even if there's something blocking the view.
* Added the Rod of the Depths, a variant to the Rod of the Lands that places cobblestone.
* Added the Rod of the Molten Core. Almost a carbon copy of the Efreet's Flame Wand Focus from Thaumic Tinkerer. Look at a block and hold it to smelt it.
* Added the Thorn Chakram. A throwable weapon that bounces off blocks. *Yes, it's from terraria, shut up.*
* Added the World Seed. A new (stackable) item that can be used to instantly teleport to the world's spawn point case the user is at least 24 blocks away.
* Added Trodden Dirt Slabs.
* Baubles now detect the "first tick" using hash codes. This is a lot more reliable than using the old entity time method.
* Changed some lexicon text to make more sense on the small details.
* Changed the Gaia Guardian's Landmine particles to look a bit better and not be as tricky to spot.
* Changed the Trodden Dirt recipe to use Sand instead of Gravel.
* Fixed a crash regarding dominant sparks when there's no other sparks available for transfer.
* Fixed dying Composite Lenses removing the composite component.*Took me long enough.*
* Fixed the Abstruse Platform recipe having stack size 0 for the components and causing divide by zero crashes with the NEI shift-[?] feature. *(Why does that even check the stack size? O.o)*
* Fixed the Assembly Halo consuming Blood Magic orbs and potentially other items whose container item is the item itself.
* Fixed the Life Imbuer spawning client-side only mobs.
* Fixed the Mana Enchanter and Mana Flash blocks appearing in the search creative tab.
* Fixed the Red String Container not passing markDirty() calls to the bound block and causing it not to sync to the client.
* Fixed the Ring of Far Reach adding insane amounts of reach if one travels between dimensions.
* Fixed the Ring of Far Reach removing flight *(and breaking whatever arcane sorcery was behind the Angel Ring in Extra Utils)*.
* Fixed the Soulscribe being able to automatically repair with mana like a Manasteel Sword can.
* Fixed the Terra Shatterer not breaking blocks in AoE at X=0 or Z=0.
* Fixed the Terrestrial Agglomeration Plate desyncing if the server is lagging and potentially destroying all the mana used if one picks up the item too early in those circumstances.
* Made the Black Lotus rarely appear in Bonus Chests.
* Made the Fallen Kanade not work in the end to prevent cheesing the Ender Dragon.
* Mana Sparks now try to update the list of sparks they can transfer to after they're done ticking. Dominant Sparks will also attempt to pull mana from sparks at random rather than iteratively. When a Mana Pool with a Dominant Spark is charging an item and is full, instead of pulling from one Mana Pool exclusively it randomly pulls from all possible pools around.
* Mana Tablets now render on the player's belt, if there's one.
* Mana tools now have right click abilities. The Pickaxe places a torch from the inventory, the Axe does the same for a sapling and the Shovel works as a Hoe.
* Nerfed the Bands of Aura. A lot.
* Removed the health boost from Terrasteel Armor.
* Removed the item name remapper. It's completely useless at this point unless by some reason you're updating from a 10 month old build. It was also breaking downgrading as it was trying to remap items that didn't exist. 
* Removed the transparency in the HUD mana bars, as it looked absolutely god awful in higher resolutions/scales.
* The Elementium Pickaxe (and Elementium Tipped Terra Shatterer) will now look in the ore dictionary for what blocks not to break. Should help with Underground Biomes and other mods that add new stones.
* The Lexica Botania's in hand animation uses partial ticks now and isn't locked to 20fps any more. It looks so much smoother. *GLORIOUS 60FPS PC MASTER RACE*
* The Mana Prism can now be turned off with a redstone signal.
* The Tiny Potato can now be named in an anvil. It can also sing.
* Trodden Dirt now expands to a full block if there's a block above it.
* [API] Added IBurstViewerBauble for a bauble that acts as the Manaseer Monocle.
* [API] Added IManaDiscoutArmor. Pretty self explanatory.
* [API] Added methods to ManaItemHandler for handling mana discount for tools.
* [API] Added remove() to LexiconRecipeMappings. (SoundLogic)
* [API] Updated version number to 30.

---

{% include changelog_header.html version="r1.3 153" %}

* Fixed the Terra Shatterer being able to break bedrock and other unbreakable blocks.

---

{% include changelog_header.html version="r1.3 152" %}

* Added the Black Lotus, a dungeon loot consumable that can be tossed onto a Mana Pool for a boost of mana.
* Added the Mana Prism, a new block that can change the properties of a Mana Burst on the fly.
* Added Trodden Dirt, a new block that looks great for <a href="https://en.wikipedia.org/wiki/Desire_path">desire paths</a>. It also gives a slight speed boost. The texture and idea come from <a href="http://redd.it/2sqwu6">/u/LupusX's post on reddit</a>.
* Red String Spoofers will now also connect to mushrooms.
* Shifted the Assembly Halo HUD down 10px so it fits on smaller resolution/render scales.
* The Brewery and Enchanter now have the same progress pie as the Runic Altar.
* The Magnet Ring will no longer pull items that can contain mana, items that are on a terrasteel plate or AE2 crystal seeds.
* (Technical) IManaTrigger blocks with IManaCollisionGhost tile entities will still have their effect triggered. Furthermore IManaTrigger triggers for fake bursts too so that should be checked against.
* [API] Added IManaDissolvable and IDyablePool.
* [API] Increased version number to 29.

---

{% include changelog_header.html version="r1.3 151" %}

* Fixed the Terra Shatterer.

---

{% include changelog_header.html version="r1.3 150" %}

* Fixed a crash when using the crafting halo to make extra utils unstable ingots.
* Fixed more tpyos. *This is becoming a dank meme.*
* Fixed other mods' GUIs turning blue while the mana bar is being rendered. *They really should reset the glColor before rendering...*
* Fixed the Mana Fluxfield not sending energy to every RF receiver. (yueh)
* Fixed the Mana Fluxfield requiring the CoFH API to be present to work rather than the CoFH Energy API. (Vexatos)
* Fixed tools that break multiple blocks (manasteel shovel, terra shatterer) causing dupes with thermal expansion tanks and strongboxes and potentially other blocks that require exact harvest method call order.

---

{% include changelog_header.html version="r1.3 149" %}

* Fixed Dominant Sparks being able to pull from blocks other than Mana Pools.
* Fixed Flashes dropping themselves.
* Moved the following flowers to elven knowledge: Thermalily, Kekimurus, Heisei Dream, Orechid, Loonium.
* Open Crates will now eject into the block below not only if it's an air block but also if it has no collision box. So yes, you can drop stuff on water now.

---

{% include changelog_header.html version="r1.3 148" %}

* *I could have fit this on 147 but I only decided to do it later :V*
* Added support for [Colored Lights](http://www.minecraftforum.net/forums/mapping-and-modding/minecraft-mods/wip-mods/1445251-1-7-10-beta-wip-colored-light-progress-and). ([Picture 1](http://puu.sh/dKo8o/7c34ca4d3d.png)) ([Picture 2](http://puu.sh/dKozh/323db4f589.png)) You can find it in the following blocks:
  *   Glimmering Flowers
  *   Floating Flowers (colored normal ones only)
  *   Buried Petals
  *   Sea Lanterns
  *   Flashes
* Buried petals now emit a faint light.
* Fixed mobs' pathfinding getting stuck in Flashes.

---

{% include changelog_header.html version="r1.3 147" %}

* *Just a small update to fix a major issue and a major annoyance.*
* Fixed a crash when placing a comparator next to a brewery.
* Fixed flowers sometimes rendering as floating due to the Y position shift in the render.

---

{% include changelog_header.html version="r1.3 146" %}

* *Added some christmas spark. ARE YOU JINGLING YOUR BELLS YET?!*
* Fixed a major dupe with the Assembly Halo where right clicking a recipe would allow for more than one item to be crafted at once using only one set of resources.
* Fixed the render for the flowers not randomizing the position in the Z axis like vanilla flowers do. It still randomizes in the Y axis unlike vanilla flowers because I think it looks good.
* Sparks now properly check against how much space is left on the target block and don't try to put in more mana than available, actually making spark networks viable. *You can all point and laugh now.*
* The Assembly Halo is now more forceful with checking the validity of a recipe. It can no longer craft recipes purely from memory (and allowing as a way to craft things that have been changed since)
* The Mana Mirror now checks for the pool on update and has a backlog for calls inbetween. It also works cross dimension a lot better.
* [API] Added getAvailableSpaceForMana() to ISparkAttachable. This is very important and breaks backwards compatibility. If anyone is using that interface please adapt ASAP.
* [API] Increased version number to 28.

---

{% include changelog_header.html version="r1.3 145" %}

* Added the Mana Lens: Flash. It makes fancy lights!
* Botania Flowers now shift a bit depending on position similarly to vanilla flowers since 1.7. *About time.*
* Fixed Botania slabs breaking lighting on adjacent blocks.
* Increased the endoflame's radius by one block northwest so it's a proper centered square now.
* The potion effects brews (and the tainted blood pendant) create now have semi transparent particles smilarly to beacons.
* The Rod of Terra Firma now works with Podzol.
* Tweaked the Assembly Halo so that the crafting table always faces the player when the item is equipped.
* [API] Added getSubTileAsFloatingFlowerStack() to the internal method handler.
* [API] Increased Version Number to 27.

---

{% include changelog_header.html version="r1.3 144" %}

* Fixed a crash with The Spectator and the Helpful Villagers mod.
* Fixed some entities not being named.
* Fixed the Assembly Halo getting recipe events from mod blocks and creating cheaty recipes.
* Fixed the Rod of the Unstable Reservoir not dealing player damage.
* Livingwood can now support leaves. (SpitefulFox)
* [API] Added a IFlowerlessBiome interface for biomes. (SpitefulFox)
* [API] Increased version number to 26.

---

{% include changelog_header.html version="r1.3 143" %}

* Fixed Dedicated Server crashing.

---

{% include changelog_header.html version="r1.3 142" %}

* Added the Assembly Halo, a portable crafting solution.
* Adding lava to the petal apothecary turns it into a brazier/incinerator.
* Changed Hydroangeas' recipe to use Cyan petals rather than Light Blue ones.
* Fixed the Signal Flare item crashing if an invalid color is loaded somehow.
* Hydroangeas are now more dilligent at picking up water blocks.
* INTENSIFIED GOLDFISH
* Renamed a bunch of rods so they all are in the same name scheme and theme.
* [API] Added aditional SubTileEntity interaction methods. onBlockPlacedBy(), onBlockActivated(), onBlockAdded(), onBlockHarvested() and getDrops() (LiamEarle)
* [API] Increased version number to 25.

---

{% include changelog_header.html version="r1.3 141" %}

* Added Alchemy recipes to break down Clay and Brick blocks.
* Moved Terrasteel Armor hp boost values around so the amount is relative to the amount of materials used for crafting. Total is still 20.
* The Tiny Potato now emits heart particles when it's right cli- *...what am I doing with my life?*

---

{% include changelog_header.html version="r1.3 140" %}

* Added two new Baubles, the Cloaks of Virtue and Sin. Their effects trigger when the player takes damage.
* All non-ring baubles now render on the player, fancy!
* Fixed flowers not ticking properly if the doDaylightCycle gamerule is set to false.
* Fixed the Halo on the Flugel wings rendering 1.62 blocks higher than normal on other players in multiplayer.
* Fixed the Rod of the Arcane Barrage crashing servers >_>
* Fixed the Terra Shatterer and Elementium Shovel having not working if the player's reach is above 4.5 (with the ring of far reach for example).
* Gave some more damage to the Rod of the Arcane Barrage.
* [API] Added IBaubleRender.
* [API] SubTileEntities now have an internal ticker.
* [API] Updated version number to 24.

---

{% include changelog_header.html version="r1.3 139" %}

* Added the Rod of the Arcane Barrage. The thing from the last update.
* Fixed BuildCraft gates not being localized. (sb023612)
* Fixed the Petal Apothecary consuming water bottles and added support for Forge IFluidContainerItem. (ganymedes01)
* Made the color for the floating island texture a bit more saturated. (Yulife)
* The shedding pages now properly use the world instance for creating entities. Should fix crashes with Fossils & Archeology. (SoundLogic)

---

{% include changelog_header.html version="r1.3 138" %}

* "Added" a new item that works but is creative only for now, I'll work on it some more later >_>
* Added a proper, registered dependency on Baubles. *Only took over 100 versions to do it /o/*
* Fixed dreamwood not going to the right page when shift-right clicked with the lexicon.
* Fixed the color of Rainbow bursts and Gaia Spirits not changing in Mystcraft ages.
* Fixed the Rod of the Skies and Hells not showing a mana bar. 
* The enchanter will now always properly reset the pylons' particle emitting status.

---

{% include changelog_header.html version="r1.3 137" %}

* Fixed RedStringRenderer looking for the codechicken Vector3 rather than the botania one. On other words, Fixed the game crashing if you have a wand of the forest equipped while you have a red string block in the world and codechickencore isn't installed.
* Fixed the Red Stringed Spoofer not being in the creative tab.
* Fixed the Terra Shatterer not actively restoring its damage like the manasteel tools do.
* Fixed the recipes for breaking back Reed and Thatch blocks giving items with metadata 1, and thus unusable.

---

{% include changelog_header.html version="r1.3 136" %}

* **Added Red String and Red Stringed Blocks**. These can interact with other blocks remotely.
* Calling setWater on the petal apothecary now calls for a block update. *This is technical stuff for other modders only.*
* Changed some minor details in some lexicon entries so they are accurate to recent changes.
* Changed the brewery texture to a more appropriate one. (Yulife)
* Combining a Floating (any color) Flower with any botania flower with an effect (manastar, pure daisy, any generating or functional...) will replace the flower there with the one you put. The flower will still act like normally, but has the visual and technical properties of a Floating Flower.
* Fixed mod slabs letting light pass through.
* Fixed some tpyos in the elven lore page. *I desperately need someone to proofread the nonsense I write.*
* Fixed the game crashing if one tries to look up uses for the Tainted Blood Pendant in NEI.
* Fixed the runic altar not saving the amount of mana it should get (and thus causing the amount of mana it has to reset to 0 when it loads back).
* Fixed there being a missing texture on the terra plate, resulting in some unneeded log errors.
* Having Night Vision on a Tainted Blood Pendant will no longer make the brightness blink obnoxiously.
* Hopefully fixed entry sharing being somewhat fiddly in SMP.
* Removed the text that says that Bursts would work in the Terrasteel crafting, because it seems everyone gets confused and decides not to use sparks even though it clearly says it's the best way >.<
* The Terra Shatterer should no longer break blocks on the client side only and require a relog.

---

{% include changelog_header.html version="r1.3 135" %}

* Added some more info to the Spark page.
* Fixed the terrasteel plate not being in the creative tab.
* Fixed the terrasteel plate not taking the player to the Terrasteel entry when shift-right clicked with the lexicon.
* Removing sparks is now done via shift-right click, normal right click now shows which sparks it can connect to.
* The Gaia Guardian now spawns a few less mobs and spawns them in a smaller radius. It won't spawn pig zombies or spiders any more, but you might get witches.
* When a spark is placed in the world it displays what sparks it can connect to, this can be duplicated by right clicking it with a wand.

---

{% include changelog_header.html version="r1.3 134" %}

* Fixed crash when looking at a mana pool if dark quartz is disabled in the config.
* Fixed the terrasteel plate taking mana from spreaders even if it isn't active.
* Spark particles are now a bit more subtle and have more color variation.
* Thatch and Reed blocks can now be broken down into their components.

---

{% include changelog_header.html version="r1.3 133" %}

* **Build 132 was supposed to be 1.3, not this, but this one works too.** *All this stuff was to be in 132 but I wanted to play The Binding of Isaac: Rebirth, since that came out, y'know...*
* **1.7.2 is no longer supported.**
* Added a Brew of Restoration, it's like the brew of Revitalization but it's Regeneration I for 2 minutes and uses redstone in the crafting.
* Added new lore/backstory pages for the events that separated the elves' world from the overworld. It's worth a read, but not required. Some text in the original entries was also changed to reflect.
* Added the Tainted Blood Pendant, it can store potions and give the player their effects while it's worn, for the cost of mana.
* Added the Terrestrial Agglomeration Plate, which serves as the new method to create Terrasteel. You'd be wise to look at the Terrasteel page to check out how it works. It's pretty similar to the old method but allows for more control and can be done earlier in the game.
* Changed the Gaia Guardian fight a whole bunch, it's harder now to compensate for the new items and the fact that it can take damage from the Terra Blade now. Brews and a Terra Blade are a must now. The entry in the lexicon now describes the recommended gear. You can see what it does for yourself ;)
* Fixed the Alfglass recipe using Glass instead of Managlass as it should.
* Fixed the Brew of Mending not working.
* Fixed the Brewery breaking instantly. Added added the proper block texture for breaking.
* Fixed the Brewery's rendering going all over the place if you placed a block on it.
* Fixed the console getting spammed when using the Rod of the Black Mesa. *smh flaxbeard*
* Fixed the Gaia Guardian being able to teleport outside the bounds of the arena.
* Fixed the Gaia Guardian despwaning if the last player in the world logged out.
* Fixed the Gaia Guardian's HP bar showing the sinusoid for the first guardian spawned if multiple guardians were spawned in the same play session.
* Fixed the lexicon gui showing the recipe for an item that's not the one being hovered above if the two happen to intersect. (see Terrasteel page)
* Fixed the Mana Pool (with Alchemy Catalyst) not being able to break down quartz blocks into their components.
* Fixed the Ring of Magnetization pulling items in the cilent side only when in a dedicated server.
* Fixed tucking fypos. Again.
* If the summoning of a Gaia Guardian fails, a chat message is now sent describing why.
* Increased the max amount of mana a Terra Shatterer can hold up to <a href="http://docs.oracle.com/javase/7/docs/api/java/lang/Integer.html#MAX_VALUE">2147483647</a>. If you manage to reach this value you also get a fancy congratulatory message.
* Lexicon Entries containing documents from Elven Garde now have a special type of paper.
* The arena for the Gaia Guardian is now round, not square.
* Trying to infuse an item that isn't a Diluted Mana Pool in a Diluted Mana Pool will show a yellow check with a line rather than just a green check.
* When fighting the Gaia Guardian there's now an indicator to where the arena ends.
* [API] Addded setNotBloodPendantInfusable() to Brew to allow brews to not be infused in the Tainted Blood Pendant.
* [API] Added elfPaperTextPage() to IInternalMethodHandler.
* [API] Exposed all of the categories from internals to the API side. They're available after Botania's PreInit.
* [API] Fixed Mana Infusion Recipes that have an ItemStack input and Short.MAX_VALUE (32767) metadata as a wildcard never being considered valid.
* [API] Increased version number to 23.
* [API] LexiconCategory now implements Comparable, has a sorting priority int and keeps count on how many were added for addon makers to be able to have their entries be sorted. The default (and used by botania categories) is 5, entries like basics and misc use 9 and 0, respectively. Use setPriority() to set.

---

{% include changelog_header.html version="r1.2 132" %}

* **Added the new Brewing system: Botanical Brewery, Vials, Brews and more.** And it comes with NEI integration too thanks to Tonius /o/
* Added a Warp Ward potion type thing to the thaumcraft integration section.
* Added Buildcraft Gate integration. (asiekierka)
* Added Managlass, very clear blue glass, made by tossing glass on a mana pool.
* Fixed sparks not rotating properly along the player's Y position. (Flaxbeard)
* The Alfglass trade recipe now uses Managlass instead of regular glass.
* [API] Added Brew stuffs.
* [API] Added isBuildcraftPipe() to the internal method handler.
* [API] Added ITwoNamedPage for LexiconPage subtypes with two unlocalized names.
* [API] Increased version number to 22.

---

{% include changelog_header.html version="r1.2 131" %}

* Fixed spawning the gaia guardian crashing servers.
* Fixed the boss bar shader not compiling on some GPUs.

---

{% include changelog_header.html version="r1.2 130" %}

* Added 16 new update flavour messages, up to 50 different ones!
* Added a "progress pie" to the runic altar's HUD, when hovering over it with a wand of the forest.
* Added a Brewery block, it's not functional at the moment, it'll do something later, but it just looks pretty for now.
* Blowing the Horn of the Wild will no longer unroot glimmering flowers.
* Changed the mana HUD texture. It now is less noisy and has points indicating the 25%, 50% and 75% marks.
* Fixed not being able to look up petal apothecary recipes in NEI. (Tonius)
* Manasteel, Terrasteel and Elementium tools now repair using mana in the inventory at double the cost of normal damage suppression.
* The Gaia Guardian now has a new, much fancier, health bar. <a href="http://i.imgur.com/jHwDGVe.gif">Here's a gif</a> (endgame spoilers, I guess).
* The Runic Altar's render now has a smoother trail rather than a bunch of cubes all with the same alpha value.
* [API] Added IGrassHornExcempt, for blocks that extend BlockBush but shouldn't be uprooted by the Horn of the Wild. Note that ISpecialFlower implementers will be excempt by default.
* [API] Added stuff for bosses, IBotaniaBoss, IBotaniaBossWithShader interfaces and getDefaultBossBarTexture and setBossStatus to the Internal Method Handler.
* [API] Exposed ShaderCallback.
* [API] Increased version number to 21.

---

{% include changelog_header.html version="r1.2 129" %}

* Added NEI Integration (Tonius):
  *   Petal Apothecary
  *   Runic Altar
  *   Mana Pool
  *   Elven Trade

---

{% include changelog_header.html version="r1.2 128" %}

* Added a bunch of references.
* Added some entries to the game profiler (Shift-F3):
  *   root.gameRenderer.gui.botania-hud
  *   root.gameRenderer.level.FRenderLast.botania-particles
* Added some text to the left of the debug menu (F3) that shows particle count and mana network status.
* Fixed the text for Nightshades and Hydroangeas going off the book if passive flower decay is enabled.
* Updated the Daybloom and Endoflame entries to have better conveyance. The Daybloom entry tells you to invest in Endoflames and the Endoflame entry tells you to check out the Crafty Crate.
* [API] Added IFlowerlessWorld for WorldProviders. (Thelonedevil)
* [API] Increased version number to 20.

---

{% include changelog_header.html version="r1.2 127" %}

* Fixed the category buttons using Scala arrays rather than Java arrays (and crashing the game thanks to that). How does that even happen? O.o

---

{% include changelog_header.html version="r1.2 126" %}

* Changed the "introductory clip" page to use a button rather than the enter key.
* Fixed hopperhocks having weird interactions with sided inventories, fixes issues with the Storage Drawers mod.
* Hydroangeas are now less noisy.
* Replaced the main page of the Lexica Botania with a new, icon driven one. This also increases the max amount of possible categories from 9 to 19, so addon authors have more space.
* [API] Added getButtonList(), getElapsedTicks(), getPartialTicks() and getTickDelta() to IGuiLexiconEntry.
* [API] Added IGuiLexiconEntry sensitive updateScreen(), onOpened(), onClosed() and onActionPerformed() to LexiconPage. Buttons in lexicon pages can now be done as well as other things.
* [API] Fixed any instances of "tile." returned by SubTileSignature.getUnlocalizedNameForStack() being replaced with "tile.botania:".
* [API] Fixed SubTileSignature.getUnlocalizedLoreTextForStack() not having any effect.
* [API] LexiconCategory now has a ResourceLocation with the icon to be rendered on the main page.
* [API] Increased version number to 19.

---

{% include changelog_header.html version="r1.2 125" %}

* Lowered the maximum amount of bookmarks from 10 to 8, so the searchbar fits.
* You can now type in a index page to search, pressing enter if there's only one entry visible opens it.
* [API] Added SubTileSignatures. Used for overrides of the initialization of a SubTileEntity.
* [API] Expose PageText.renderText() through the IInternalMethodHandler.
* [API] Fixed usage of internal classes.
* [API] Increased version number to 18.

---

{% include changelog_header.html version="r1.2 124" %}

* Added a config option for silent mana spreaders.
* Added support for all 3 Gany's Mods wikis for shift-right clicking with the lexica botania.
* All variants of quartz blocks can be properly returned to quartz item format with the alchemy catalyst.
* Blocks from Ars Magica will no longer display to be opened "AE2 Wiki" when hovered with the lexica. The behaviour worked as intended before, just the display was wrong.
* Fixed lag when mana pools are (dis)charging an item.
* Fixed Mana Pools not registering the catalyst below them on the first tick, so if an item was transformed upon loading the world it could've been transformed to the non-catalyst recipe.
* Fixed the default flower spawn rates being wrong (3 and 32). They're now 2 and 16. *Funny story, this was supposed to have been changed in build 90, but wasn't due to an oversight on my part. Regardless, it's now :V*
* Horses infected with a virus no longer take fall damage, this includes the riders.
* Mana Bursts will no longer be affected by ProjectE Interdiction Torches.
* Mana tools/armor will now not try to pull mana from tablets that don't have enough and fail, causing them to get damaging.
* Sparks are now immune to fire.
* The Agricarnation now uses a redstone root and can be controlled a redstone signal.
* [API] Increased version number to 17 as per wiki changes in the main class.

---

{% include changelog_header.html version="r1.2 123" %}

* Fixed some wireframes rendering out of place.
* Fixed the Exoflame appearing full even when it's empty.
* Fixed the pool craft check HUD displaying recipes that require catalysts even if they're not installed.
* Fixed the pool craft check HUD not displaying stack sizes (relevant for the conjuration catalyst).
* Generating and functional flowers can now be manually bound to spreaders or pools, respectively, by shift right clicking with the wand of the forest on them while in bind mode.
* Mana Spreaders now unbind themselves from the wand when bound to something, no need to do it manually.
* Reorienting spreaders (and binding flowers now) has a beam effect as a visual confirmation.
* Slightly modified the regular expression used in the divining rod algorithm, it should be actually accurate rather than accepting anything that starts with "ore".
* Swapped the ring of magnetization's controls, again. It now picks up items when *not* sneaking, again.
* The following things now make a sound:
  *   Mana Spreaders firing.
  *   Endoflames consuming fuel.
  *   Hydroangeas consuming water.
  *   Thermalilies consuming lava.
  *   Gorumaryllis eating food.
  *   Kekimuruses eating cake.
  *   Orechids making ores.
  *   Agricarnations fertilizing crops.
  *   Divination rods divining.
  *   *And a botania update on a pine tree~*
* The ring of magnetization now goes into a 5 second cooldown period whenver the player wearing it tosses an item, this should make using it a lot less hazardous.
* The Spectator now has fancier particles and they display on top of blocks.
* The Wand of the Forest now has two modes, Function Mode and Bind Mode. They can be swapped with shift-right click. Bind Mode is reqired to orient Mana Spreaders.
* [API] Added IWandBindable for the ability to bind blocks to other blocks using the Wand of the Forest
* [API] Increased version number to 16.
* [API] SubTileEntities have the methods for IWandBindable built in. SubTileGenerating and SubTileFunctional use them already.

---

{% include changelog_header.html version="r1.2 122" %}

* Added an alchemy recipe to change Hardened Clay into Red Sand.
* Fixed botania particles rendering white if the NEI spawn overlay is enabled.
* Flower generation is now done in patches of the same color rather than fully randomly. Here's a <a href="http://i.imgur.com/CnOFssk.png">Before</a> & <a href="http://i.imgur.com/ggICjfM.png">After</a> (with flower spawn rates turned higher than by default)
* Removing lenses from the Mana Blaster is now done via placing it in a crafting grid rather than shift-right click.
* The Ring of Magnetization now only works when sneaking rather than when not sneaking.

---

{% include changelog_header.html version="r1.2 121" %}

* Fixed the Mana Mirror crashing the game if bound to a pool in the nether or any other dimension with ID < 0.
* Fixed the share commands not working without OP permissions (cheats enabled in singleplayer).
* Mana Infusion recipes are now properly labeled with "Drop" and also have a tooltip giving tips on drop shortcuts.
* Wiki linking now has proper capitalization. Should fix websites that are case sensitive (*cough* FTB Wiki *cough*) not showing the proper page at times.
* [API] Increased version number to 15 due to the wiki stuff.

---

{% include changelog_header.html version="r1.2 120" %}

* Added desu.
* Added squirt.
* Removed unnecessary update ticks. More likely than not you'll notice an FPS increase, the larger your generation plant (no pun intended) the more noticeable this should be.
* The Flugel Tiara is now a bit cheaper.
* The Mana Enchanter will now function properly if an enchantment is more common than Efficiency or Sharpness.
* The Rosa Arcana now produces a bit less mana.
* The Globetrotter's Sash now is a bit slower.
* [API] Increased version number to 13, since the performance changes involve api class changes. No adaption is required.

---

{% include changelog_header.html version="r1.2 119" %}

* Added a proper entry documenting the Thaumcraft Integration in the mod (under Miscellaneous), also added the Botanurgist's Inkwell, that uses mana to make ink for the research table.
* Added fancy side halo to the Flugel Tiara with Flugel wings (Nether Quartz). (Victorious3)
* Blocks rendered in the HUD for the Rannuncarpus or any Mana Spreader now have proper shading.
* Changed the "X:1 Ratio" display to "Zoom: Xx", just so it's a bit clearer.
* Fixed crashes with the Rod of the Skies when Hunger Overhaul is installed. (squeek502)
* Renders now use partial ticks and should look a lot smoother than before, provided you have high enough FPS, of course.
* Updated the lore text for the Mana Tablet's crafting recipe in the lexicon to be more referring to modern times.

---

{% include changelog_header.html version="r1.2 118" %}

* Fixed random crashes.
* Fixed the Nightshade documentation being inaccurate.
* The Hand of Ender is no longer elven knowledge.

---

{% include changelog_header.html version="r1.2 117" %}

* Added a HUD element when hovering over a mana pool with an item that can be infused that shows if there's enough mana in the pool or not. If you're a goldfish you should sneak on it too.
* Added some Lore/Quote pages (all text in italics) to some entries in the Lexica Botania. They aren't gameplay focused, just little homages and small distractions.
  *   Daybloom
  *   Fallen Kanade
  *   Gourmaryllis
  *   Heisei Dream
  *   Jaded Amaranthus
* Added the ability to share lexicon entries through a button on the top-right corner of the gui.
* Changed some text to fit better on screen on low resolutions/high gui scale.
* Changed the "X:10 Ratio" display on mana infusion and runic altar recipes to be more visible.
* Fixed Mana Enchanters crashing the world if somehow spawned in with an invalid metadata.
* Fixed the "An Introdutory Clip" page misbehaving. That totally wasn't intentional, nope.
* Fixed the Timeless Ivy's repair recipe not being completely shapeless and not working if the repair item isn't in the end. (nekosune)
* Fixed the wiki display having two spaces behind my @. *MY OCD*
* Shift-right clicking on a block from unknown knowledge school in the world doesn't work any more.

---

{% include changelog_header.html version="r1.2 116" %}

* Added Alfglass, fancy new glass. You can find it in the Resources of Alfheim entry.
* Added the Gaia Mana Spreader, a "tier 3" Mana Spreader. You can find it in the Elven Spreaders entry.
* Doubled the amount of mana the Kekimurus produces.
* Fixed any Mana Blasters that aren't the default ones having wrong particles when broken.
* Fixed trying to look at the recipe for any variant of quartz taking you to the block->quartz recipe instead of the acutal recipe.
* Increased the minimum visual width of a Mana Burst, you no longer have super thin visual bursts.
* Intensified Goldfish.
* Removed unnecessary random ticking from botania blocks. Might help with server optimization.
* The Ring of Mana and Terra Shatterer can now be crafted with a Mana Tablet of any durability.
* The Terra Blade's beam now deals player damage, works in PVP and respects enchants.
* The wings of the Flugel Tiara can now be swapped without having to create a new tiara.
* [API] Added setSkipRegistry() to LexiconPage.
* [API] Increased version number to 13.

---

{% include changelog_header.html version="r1.2 115" %}

* Actually fixed the NEI and InvTweaks derps.

---

{% include changelog_header.html version="r1.2 114" %}

* Trying to fix NEI or Invtweaks derping. I can't reproduce that issue at all. This may or may not fix it >_>

---

{% include changelog_header.html version="r1.2 113" %}

* Added a <a href="https://www.youtube.com/watch?v=rx0xyejC6fI">video guide</a> you can access from the lexica botania, courtesy of <a href="https://www.youtube.com/channel/UCBFiQsdlPYROelxYELr0usw">WTFG33ks</a>!
* Added Hardcore passive generation. You can now set a number in the config file in which a passive flower (daybloom, nightshade, hydroangeas) will die after that time, in ticks. This is off by default.
* Added the Timeless Ivy, add it to a tool and it can pull mana from your inventory just as if it's made of Manasteel.
* Changed the textures for the Brown Mystical Flower, powered Ender Overseer and Redstone Root.
* Fixed the Starfield Creator not working in multiplayer.
* Fixed the Terra Shatterer and Vitreous Pickaxe not being able to be repaired in the anvil.
* Made the Loonium more expensive in terms of mana.
* [API] Added onKeyPressed() to LexiconPage.
* [API] Added ticksExisted to SubTileGenerating.
* [API] Increased version number to 12.

---

{% include changelog_header.html version="r1.2 112" %}

* Added the Rod of the Black Mesa (Flaxbeard) *Half Life 3 Confirmed.*
* Added the ability to shift-right click on virtually any block in the game with the Lexica Botania to open up a wiki page for that block. A few wikis are supported right now, more will be added in the future. In case there's a wiki for a mod not supported it'll fallback to the FTB Wiki. Other modders can register their own wikis through the API.
* Sneaking with a Lexica Botania in hand now shows the name of the entry/wiki page that will be opened instead of a ? all the time.
* [API] Added a wiki package and classes respective to them.
* [API] Increased version number to 11.

---

{% include changelog_header.html version="r1.2 111" %}

* Added the Mana Fluxfield, a way to convert from Mana to RF. *And I'm sure as all hell not doing it backwards.*
* Fixed more typos and nonsense. (johnnydickpants)
* [API] Added IPetalApothecary and IExoflameHeatable. **stares at Azanor* Your move, buddy.*
* [API] Increased version number to 10.

---

{% include changelog_header.html version="r1.2 110" %}

* Added the ability to customize the wings the Flugel Tiara gives you, using Quartz. *Confirmed, Botania is a cosplay mod now.*
* Fixed Sparks making pools losing mana even if there's no place for it to go.
* Fixed wings' rendering being affected by the color of the item being held.

---

{% include changelog_header.html version="r1.2 109" %}

* Fixed dedicated server crash. (tterrag1098)

---

{% include changelog_header.html version="r1.2 108" %}

* *(This build has no changes, I triggered it by accident :D)*

---

{% include changelog_header.html version="r1.2 107" %}

* Added a custom texture to the Botania creative tab, it also has a search bar now.
* Added Wings to the Flugel Tiara. You even get devil wings in the Nether!
* Fixed Diluted Mana Pools draining tablets and equivalent even when they're full.
* Fixed Sparks never outputting their mana into non-pool blocks. *I'm gonna go hang myself now >_>*
* *Is it fixed yet? Is it fixed yet? Is it fixed yet? Is it fixed yet? Is it fixed yet? Is it fixed yet?*

---

{% include changelog_header.html version="r1.2 106" %}

* Fixed Dispersive Sparks putting mana into a Terra Shatterer.
* Fixed the Clayconia, Exoflame and Rannuncarpus having -1 range in one corner.
* Fixed the Rod of Divination being uncraftable and crashing the game when trying to look up the recipe.
* [API] Added ISubTileProvider.
* [API] Documented classes under vazkii.botania.api.mana.spark.
* [API] Increased version number to 9.
* *holds up spork*

---

{% include changelog_header.html version="r1.2 105" %}

* Added the Rod of Divining.
* Fixed an exploit with Sparks where a dominant one can pull from others if the upgrade is added a posteriori. (mattcr11)
* Fixed another Spark related crash. (mattcr11)
* If you're a goldfish, you get a new thing.
* Since Warp was such a success, if you have Thaumcraft loaded, you'll now have Wrap too.

---

{% include changelog_header.html version="r1.2 104" %}

* Added the Horn of the Canopy. It breaks leaves. Look for it in the Horn of the Wild entry.
* Fixed server crash. *And this, ladies and gentleman, is why you don't trust pull requested code.*
* The Gaia Guardian now can't be spawned if the world is in peaceful, it also despawns in this case.

---

{% include changelog_header.html version="r1.2 103" %}

* Added the ability to fully customize mob shedding in the config file, which mobs drop what and how often. The lexicon entry for the shedding now dynamically updates depending on which mobs are set. (SoundLogic)
* Fixed crashes when wanding platforms, again.

---

{% include changelog_header.html version="r1.2 102" %}

* Fixed Recessive sparks pumping mana into other pools even if they are full.
* Fixed Sparks sometimes vanishing on the client side after a world reload.
* Fixed Terra Shatterer crashes in 1.7.2.
* Fixed crash if a Mana Pool is broken while a Spark is interacting with it.
* Swapped Generating and Functional Flora in the lexicon. Because it just makes sense.
* Tweaked the Sparks' particles a bit, they're less but larger now.

---

{% include changelog_header.html version="r1.2 101" %}

* **Added Sparks.**
* Added the ability to mix an Elementium Pickaxe with a Terra Shatterer.
* Any water containers (water cans, cells, etc from other mods) can now fill up the petal apothecary. (ganymedes01)
* Changed some flower textures lightly. You probably won't notice much of a difference.
* Elementium Armor now has double the chance for spawning pixies.
* Fixed Floating Flowers not stabilizing TC infusion stability. (viliml)
* Fixed lots of typos. (Adaptivity)
* Fixed the Kindle Lens being able to replace blocks (liquids included).
* Fixed the Mana Enchanter ignoring enchantment types (Power on swords, Sharpness on armor, etc).
* Fixed the Platforms crashing if swapped when there's an adjacent Tile Entity.
* Fixed the Ring of Far Reach not always working due to issues with baubles.
* Fixed the Runic Altar not crafting if the piece of livingrock tossed in desyncs and looks like it's on top of the altar even though it isn't.
* Fixed the Vitreous Pickaxe duplicating glowstone and other glass blocks of the same type.
* Highly optimized the block finding code for the Shard of Laputa. Larger ones should no longer eat up your tickrate.
* [API] Added classes to do with Sparks, ISparkAttachable, ISparkEntity and SparkHelper.
* [API] Increased version number to 8.

---

{% include changelog_header.html version="r1.1 100" %}

* Fixed crash on startup if thaumcraft isn't loaded. *I'm an noob :V*
* *Build 100, that's a milestone or something I guess...*

---

{% include changelog_header.html version="r1.1 99" %}

* Endoflames now  take priority over Hopperhocks. They'll try to pick the items up before the Hopperhock has a chance.
* Fixed Mana Pools with a custom mana cap set through NBT not rendering properly.
* Fixed crashes with the Force Relay with the Little Blocks Mod.
* Glimmering Flowers, Floating Flowers and Mana Pylons now lessen Thaumcraft Infusion instability. *More hypocrisy!*
* Mana Pools in the Lexica Botania now render with mana in them, that just makes sense right?
* Mana Pools now come in two tiers. Diluted and [normal]. The diluted one only stores 1% of what the normal one can and is what you get by crafting it now, it can't infuse any items but a normal mana pool, which is acquired by tossing a Diluted Mana Pool into one with mana.
* The Shard of Laputa doesn't scale vertically now, fixes it pulling blocks twice on higher levels. (SoundLogic)

---

{% include changelog_header.html version="r1.1 98" %}

* Fixed the Shard of Laputa only ever pulling a single block >_>

---

{% include changelog_header.html version="r1.1 97" %}

* Added a "hey noob, the pool holds a lot of mana, k?" info page to the Mana Pool entry.
* Added a bunch of sounds, no more rand.levelup everywhere with different pitches!
* Added a config to make the Thermalily only take Lava if there's Obsidian below it, for the Blood Magic balance freaks. (disabled by default)
* Added a new category to the Lexica Botania. The "Lexicon Index" category, which shows every single entry in alphabetical order.
* Added the Starfield Creator, a new block that makes twinkly stars :3
* Added the Vitreous Pickaxe, it's like cheap silk touch but only for glass.
* Baubles can now be swapped on the fly, by right clicking one it replaces it with the one currently there.
* Botania particles now respect the vanilla "Particles" setting. You can make it so it ignores it again in the config if you want.
* Changed the recipe for the Platforms, they now use Mana Pearls/Pixie Dust and craft into 2.
* Elemental Runes are now crafted in sets of 3.
* Fixed more typos. (Adaptivity)
* Fixed the Lexica Botania's cover text rendering backwards (or not at all) on some setups.
* Fixed The Spectator doing particles for every player, not just the client player.
* Removed manual .lang file assigning, let forge do all the work now.
* Right clicking a platform with a wand will change all adjacent ones to that block, shift right clicking removes all masks.
* The Crafty Crate now has Comparator support, it emits power equal to the amount of items in it.
* The Gaia Traps (purple spots the Gaia Guardian summons) now only blind for a fraction of a second instead of four seconds.
* The Rod of the Lands no longer works below sea level (Y = 62), this is to prevent it from being used as a ghetto underground quarry.
* The Runic Altar now has Comparator support, it emits power 1 if it's taking mana, power 2 if it's ready for the Livingrock.
* The Shard of Laputa is now upgradable using aditional Gaia Spirits, each one increases the radius by 1 block, up to 20 can be used.
* The Soujourner's and Globetrotter's Sashes no longer work underwater, so you can't go at hyperspeed underwater. *Godspeed, Godspeed, Godspeed, speed us away*
* The Tangleberrie and Jyullia now ignore bosses. *soz.*
* Updating botania through the ingame downloader now has a link to my <a href="http://www.patreon.com/Vazkii">Patreon.</a> *Dollah dollah bills y'all.*
* [API] Added a function to get all registered lexicon entries.
* [API] Added IExtendedPlayerController.
* [API] Exposed IPixieSpawner from internal classes.
* [API] Increased version number to 7.

---

{% include changelog_header.html version="r1.1 96" %}

* Fixed crashes when an invalid PlayerInteractEvent is fired (mostly because Cauldron throws them around like they're free samples).

---

{% include changelog_header.html version="r1.1 95" %}

* Added "dammit reddit".
* Added a config option to disable the Mana Enchanter.
* Added the Crimson Pendant, a "tier 2" Pyroclast Pendant that does what the old one used to pre-nerf.
* Added the Hand of Ender, an item that allows not only for access of one's ender chest, but of others' via right clicking on them. (the last feature can be toggled in the config).
* Added The Spectator, a new bauble that lets you search for items in inventories, dropped around, in players/horses' inventories, equipped by mobs and that villagers trade.
* Changed the recipe for the Pyroclast Pendant to make more sense with the recent nerf.
* The Pixies the Gaia Guardian spawns no longer take non-player damage, so you can't cheese the battle with Tiny Planet + Terra Blade. *Sorry Lewis :3*

---

{% include changelog_header.html version="r1.1 94" %}

* Added Mob Spawners to the Botania creative tab. Using a spawn egg on them changes the mob it's spawning, similar to MC:PE.
* Added the Ender Overseer, a new block that detects when players are looking at it and gives out a redstone signal.
* Nerfed the Pyroclast Pendant, now it only protects the wearer from fire damage if they are not standing on fire or lava.

---

{% include changelog_header.html version="r1.1 93" %}

* Actually fix the skull renderer because I'm an undereducated idiot who has no clue what they're doing or how the java compiler works :V

---

{% include changelog_header.html version="r1.1 92" %}

* Doubled the range in which the Mana Enchanter can pull from Mana Pools. The range it was before was stupidly low anyway.
* Fixed Azulejo blocks pointing to the wrong lexicon entry.
* The new skull renderer only registers on 1.7.10 since they changed the skin system and I can't compile for both functions.
* [API] Added oreZinc to the list of ores the Orechid can spawn.
* [API] Increased version number to 6.

---

{% include changelog_header.html version="r1.1 91" %}

* Added new building blocks: Reed Block, Thatch, Netherrack Bricks, Soul Bricks, Snow Bricks, <a href="http://www.madeireirafalsarella.com.br/arquivos/produtos/telhas/portuguesa/01-g.jpg">Roof Tiles</a> (all with stair and slab variants) and 12 types of <a href="http://en.wikipedia.org/wiki/Azulejo">Azulejos</a> for use with white Quartz.
* Hopperhocks will no longer pick items up right after a mana pool crafts them.
* I was playing with TC and realized I wanted Botania armor mixed in with the Goggles of Revealing, so you can do that now.
* Lowered the chance for the Lexica Botania to spawn in the Bonus Chest, according to almighty reddit it was too high.
* Player heads now render with the hat/hair layer as they do in the normal model, so your crafted heads won't look weird now. Thanks to Kihira for this!
* Removed the Baubles Button from the inventory as that feature is provided by Baubles itself now.

---

{% include changelog_header.html version="r1.1 90" %}

* Added a new generating flower, the Gourmaryllis, it eats food to make mana, but there's a catch...
* Added a new recipe for Mana Alchemy, Coarse Dirt (just vanilla dirt with metadata value 1, grass can't spread to it, the stuff that spawns in the Mega Taiga).
* Added Rainbows for Nemsun.
* Added the Crafty Crate (and a Placeholder item for use with it), a counterpart to the Open Crate that can autocraft items.
* Fixed a crash when binding the Wand of the Forest to a spreader and right clicking on something with no collision box (tall grass, flowers, etc).
* Fixed chat spam when using a Wand of the Forest or spawning pixies.
* Fixed the Kindle Lens destroying blocks.
* Fixed the Wand of the Forest trying to interact with objects on the client when it's bound to a spreader. So when bound to a spreader, mana pools will no longer change their mode in the client but update when clicked normally.
* Lowered default flower quantity and density to 2 and 16 respectively (was 3 and 32).
* Lowered default villager shed avg interval from 82000 to 226000 (this won't affect old configs).
* The Hopperhock is now more efficient at picking up items and picks up any it can find instead of just one at a time, this makes automating systems that require timing a lot easier.
* The Hopperhock now respects inventory max stack sizes.
* The Mana Enchanter is more expensive and puts less empahsis in the rarity of the enchantment.
* The Mana Pool no longer has a cooldown between being able to craft items but rather a timer on when it can "re-craft" an item after it crafted it (see wood log transmutation for example).
* The Mana Pool will no longer emit charging/discharging FX if no transfer happened (when either the pool or the item is full/empty).
* The Snowflake Pendant no longer creates ghost ice blocks in the client and is now properly centered on the player.
* Toned down the mana pool charging/discharging fx for sake of performance.
* [API] Added ILaputaImmobile for blocks that shouldn't be able to be moved by the Shard of Laputa. For Victorious3 for BetterStorage crates.
* [API] Incremented version to 5.

---

{% include changelog_header.html version="r1.1 89" %}

* Added a means of creating players heads (elven knowledge).
* Added some Illustrations to the Daybloom and Hydroangeas entries to explain better how they work.
* Added the Life Imbuer, a means of allowing spawners to work without players nearby.
* If a Wand of the Forest is bound to a spreader, it'll always show that spreader's bounding box instead of any other, to make it more obvious.
* Spreaders now aim to the center of blocks taking their collision box in mind and not a 1x1x1 box.
* The islands created by the Shard of Laputa are now somewhat randomized and pickup more blocks above the source. (SoundLogic)

---

{% include changelog_header.html version="r1.1 88" %}

* Added a "If you have this, it means something went wrong, you should report it" message to the fallback flower (The Poppy textured "Botania Flower"), in case somebody gets it somehow.
* Added the Rod of the Highlands, an upgrade to the Rod of the Lands that lets you place blocks in midair.
* Changed some documentation.
  *   The Runic Altar's entry now has a small preface telling players they should look into Mana Manipulation first.
  *   The Mana Spreader's entry now has clearer instructions for how the rotating is done and documentation for the fact that you can disable it with a redstone signal.
  *   The disclaimer that lenses only function when there's a proper block to shoot at has been changed and moved to a single page rather than being on various lenses' pages.
* Fixed Mana Burst particles going above the spreader on the first few ticks.
* Fixed shaders not compiling sometimes and causing crashes/log spam. *I have no idea what I'm doing :V*
* Fixed the Gaia Guardian being able to freeze the game if it falls in a ravine or equivalent (found this while fighting it in an island made by the Shard of Laputa).
* Fixed the Hyacidus wasting mana by trying to poison undead.
* The Influence Lens no longer moves other Mana Bursts. *I designed the thing at 5AM that time I had insomnia, gimme a break.*

---

{% include changelog_header.html version="r1.1 87" %}

* Fixed Mana Spreaders freezing your game, woot!
* Removed the ability for the Rod of the Seas to place water in the nether, after some thought it wasn't that great of an idea after all.

---

{% include changelog_header.html version="r1.1 86" %}

* Fixed compatibility with 1.7.2.

---

{% include changelog_header.html version="r1.1 85" %}

* **Botania should now work properly in a developer environment** and not complain about sand in it's eyes or something.
* Added <a href="http://redd.it/2b3o3f">Futureazoo's Textures</a> as a config option. A <a href="http://www.curse.com/texture-packs/minecraft/222614-botania-retexture">Full Resource Pack</a> with other textures is also available.
* Added the Infrangible Platform, a counterpart to the Abstruse and Spectral Platforms only available in creative, its' collision box is similar to a regular block and it's unbreakable, it's meant for mapmakers, see.
* Added the Ring of Far Reach (Elven Knowledge), it increases your reach by 3 blocks.
* Changed the Terra Shatterer texture to Futureazoo's. See previous link.
* Fixed the Drum of the Gathering dropping the Drum of the Wild instead.
* Fixed the Mana Lens crafting recipe only allowing you to use invalid combinations instead of valid ones.
* Fixed the Shard of Laputa...
  *   Being allowed to go above world height.
  *   Sometimes having an offset of 1 block.
  *   Being able to be used in the nether
  *   Being able to mobilize Bedrock and other unbreakable blocks.
  *   Overriding any blocks already in the area.
  *   Stopping if the player relogs.
  *   Creating bursts that can be pulled by the Tiny Planet *(lol)*.
* Fixed the Tiny Planet creating way too many particles when used with the Terra Blade.
* Mana Bursts now properly save the time they've been in the world and the lens they were spawned with when you leave the game.
* Removed Capes. *Just so mojang doesn't throw a hissy fit, I'll find something else.*
* Removed the *feature* where the Tiny Potato would take over other mods' item renders.
* Removed the technical quantityDropped override for special flowers at request of mDiyo, might fix some stuff with tcon. */shrug*
* [API] Added getTicksExisted() to IManaBurst.
* [API] Added ITinyPlanetExempt.
* [API] Incremented Version number to 4.

---

{% include changelog_header.html version="r1.1 84" %}

* Added a tooltip to the Mana Bottle.
* Added the <a href="https://www.youtube.com/watch?v=8bNEIOkUuH0">Shard of Laputa</a>, endgame item, generates a floating island.
* Added the Drum of the Gathering. Kinda like the Drum of the Wild, but for animals.
* Added the Necrodermal Virus and the Nullodermal Virus, when used on Horses they make them Skeleton or Zombie Horses and make them harder, better, faster, stronger.
* Added the Tiny Potato, it believes in you, you can do the thing!
* Added two new lenses: The Kindle and Force lenses, which, respectively, set blocks on fire and move them like a piston.
* Changed the recipe for the Phantom Lens since that recipe is now used for the Force Lens.
* Changed the wording for the explanation for Daybloom/Nightshade dimishing returns, as it was somewhat ambiguous.
* Fixed Elementium Armor having a 100% chance to spawn pixies.
* Items that come out of the Elven Portal get asigned an NBT tag to prevent them from going back in, in case some other mod adds recipes (like Modtweaker).
* Lowered the amount of Gaia Spirits the Gaia Guardian drops (from 12 to 8).
* Renamed "Mystical Instruments" to "Mystical Items", because it makes more sense and is less confusing.
* The Petal Apothecary now has a smarter checking for what it considers seeds. Read: Natura Hop*seed* wood won't work now :p
* Tweaked the Mana Burst particles, now they never leave any empty space between them, so you get a proper straight burst when you fire it with a mana blaster instead of a bunch of loose particles. The color and size also randomizes a bit to make each burst distint, even if just by a bit.

---

{% include changelog_header.html version="r1.1 83" %}

* Changed the Lexicon HUD icon to be a lot less intrusive.
* Fixed Gaia Pylons' in-world lexicon click going to the wrong entry.
* Shaders now use GLSL version 120 instead of whatever they feel like. Should fix issues with some GPUs having lag with them enabled. If your GPU doesn't support GLSL version 120 you should consider upgrading (or turning shaders off in the config I guess).
* The Charm of the Diva is now a lot more reliable.

---

{% include changelog_header.html version="r1.1 82" %}

* Added a config option to disable the animation when an item is charging on a mana pool.
* Added some Botania loot to the Bonus Chest, Stronghold and Dungeon chests.
* Added the Vinculotus, a new flower that interecepts enderman teleportation and forces them to teleport to it.
* Botania armor now pulls mana from the inventory to repair damage, not only nullify it. If repairing, it costs twice as much.
* Fixed pixie console spam.
* Fixed pixies being able to be killed by the player who spawned them.
* Fixed the daffomill (sometimes?) creating a rubberbanding effect on the client. (I couldn't reproduce this issue so I can only assume that fix did it)
* The Gaia Guardian now blocks flight.

---

{% include changelog_header.html version="r1.1 81" %}

* Fixed a crash when breaking a mana pool, since the fix for the crash when placing a mana spreader broke that. >_>
* Fixed the Gaia Guardian being able to be hurt by Fake Players.
* Lowered the volume of the Terra Blade's proc effect because Marty was complaining.

---

{% include changelog_header.html version="r1.1 80" %}

* Fixed an exploit with the Fallen Kanade that would trivialize the Gaia Guardian fight.
* Fixed crash when breaking a mana pool and placing a spreader/flower in it's place.
* Fixed the Fallen Kanade sometimes leaving behind a residual 0:00 time regen effect that wouldn't disappear.
* Slightly increased the delay between Gaia Guardian teleportations.
* The Gaia Spirits now have a subtle color overlay.

---

{% include changelog_header.html version="r1.1 79" %}

* Fixed Natura Pylons being uncraftable.

---

{% include changelog_header.html version="r1.1 78" %}

* All renders that relied on world time now use an internal client ticker, this fixes animations for those renders being rubberbanding on servers. (Pylons, floating flowers, etc.)
* Changed the crafting method of the Natura Pylons to not require Essence of Eternal Life/Gaia Spirits.
* Fixed Mana Spreader redstone control not being functional.
* Fixed the Baubles button opening an empty baubles gui.
* Fixed the Baubles button shifting if there's potion effects active.
* Rebalanced the elemental rune recipes.
* Removed Essence of Eternal Life. Added Gaia Spirits to replace it. Anything crafted with Essence of Eternal Life now requires Gaia Spirits and is now elven knowledge. Gaia Spirits are acquired through the **Ritual of Gaia** (see in the Alfhomancy section, requires elven knowledge, *hint, it's a new boss*).

---

{% include changelog_header.html version="r1.1 77" %}

* Added NBT tags for mapmakers to allow control of whether Mana Pools can input/output mana to items.
* Added an IO Key system for mapmakers that allows them to define which pools/spreaders a spreader is allowed to shoot mana to.
* Changed the Mana Mirror texture.
* Changed the way the remote spreader rotation works, it now rotates instantly and doesn't derp around all over the place.
* Fixed crash on opening inventory on 1.7.10.
* Fixed the effects of the Mana in a Bottle being out of sync on servers.
* Rainbow Tinted Paintslinger Lenses will no longer create jeb_ sheep but rather randomly colored sheep.
* [API] Added IKeyLocked.
* [API] Increased apiVersion to 3.

---

{% include changelog_header.html version="r1.1 76" %}

* Added a Baubles button to the inventory that allows you to swap between the Baubles and Normal inventories instead of using the Keybind. (This can be toggled in the config).
* Fixed Bifrost blocks being pickupable in creative.
* Fixed some seeds not getting picked up by the petal apothecary (any registered with "Seed" in the name rather than "seed" because the check was case sensitive). (ganymedes01)
* The burst from a Damaging Lens or Terra Blade now has a larger collision box and hits mobs in a small radius around it.
* The Flugel Tiara is now made with Elementium.
* The Mana Usage display for Mana Infusion and Runic Crafting recipes in the Lexica Botania now shows that it's on a 10:1 ratio. Hovering over the bar changes it to 1:1 ratio.
* The Paintslinger Lens can now dye sheep.
* The Paintslinger Lens now works on Carpet and Botania Unstable Blocks/Beacons.

---

{% include changelog_header.html version="r1.1 75" %}

* Added Fancy floating flowers.
* Fixed Rainbow Tinted lenses not rendering when there's a block above the spreader.
* Fixed the Force Relay being allowed to push bedrock and blocks of the like.
* Increased the range in which the terrasteel crafting process can pull mana from.

---

{% include changelog_header.html version="r1.1 74" %}

* Added Shedding. All sorts of mobs now randomly drop components of their body (eg, feathers on chickens, bones on skeletons, ghast tears, etc). This feature duplicates the mod ChickenShed and adds variants to other mobs. It can be tweaked in the config.
* Fixed Glimmering Flowers working below pylons for the enchanter.
* Fixed the Life Aggregator crashing servers.
* Fixed the Snowflake Pendant not placing blocks in the correct location under some circumstances.
* Increased the fall resistance of the Soujourner's Sash.
* Reverted a recent change where particles would stay in the world for less time.
* The Globetrotter's Sash now gives you a higher jump height and fall resistance.

---

{% include changelog_header.html version="r1.1 73" %}

* Fixed Mana Pools that were placed before the build 72 update being drained of all their mana and becoming unable to receive any.

---

{% include changelog_header.html version="r1.1 72" %}

* Added a "Mana Pool Cap before Voiding" NBT tag for mapmakers.
* Added a "One use Mana Tablet" NBT tag for mapmakers.
* Added a bunch of NBT tags to the Mana Spreader for mapmakers to be able to customize the properties of the mana burst the spreader fires.
* Fixed Boreal Seeds being uncraftable.
* Fixed Crash with rainbow tinted paintslinger lens.
* Fixed Livingrock and Livingrock Brick Slabs' drops being swapped.
* Fixed rainbow tinted paintslinger lenses changing the blocks' colors twice.

---

{% include changelog_header.html version="r1.1 71" %}

* Added a visual representation of Daybloom/Nightshade diminishing returns.
* Fixed an exploit with Mana Spreaders that allowed them to function at twice the speed if one was to log out while a burst was still in the world.
* Fixed the block breaking particles of the creative mana pool being of the Livingrock Bricks.
* Having a Mana Void or any Catalyst below the Mana Pool changes it's texture.
* Mana Pools and Spreaders now add themselves back to the mana network case they get removed for whatever reason. *If this change doesn't fix the whole "flowers sometimes unbind from spreaders/pools" issue once and for all I'm punching a loli.*
* The Mana Blaster now has a subtle flicker effect on it's texture.
* The Mana Detector now emits some red particles while it's emiting a redstone signal.
* The Mana Spreader's render now has an animated crystal inside rather than a static one. Very subtle change, you can see if you look inside.
* The Mana Void now emits some dark sparkle particles if a mana burst collides with it.
* The Paintslinger lens can now use the Rainbow color *>:D*
* The Paintslinger lens now has particles on collision.
* Vines grown through Vine Balls will no longer grow.
* [API] Added IClientManaHandler and IPoolOverlayProvider.
* [API] Increased apiVersion to 2.

---

{% include changelog_header.html version="r1.1 70" %}

* **Massively optimized the particle render code.** Botania particles will no longer kill your fps. In doing this they also render properly over water now, so that's neat. *Yo Azanor, you should do this too for nitor.*
* Added <a href="https://twitter.com/Vazkii/status/484136285251309568">Matrix Mode</a> as a config option. This was a visual glitch I got while changing the particle code that I decided to leave in because it was funny.

---

{% include changelog_header.html version="r1.1 69" %}

* Added a new lens (that requires elven knowledge), the Paintslinger Lens.
* Changed the Pixie Dust texture to be pink to match the other elven resources and the pixie mob texture. *You were waiting for it, confess.*
* Changed the text when hovering over a mana pool from "Accepting/Sparing Power" to "Accepting/Sparing Mana", to keep things concise.
* Fixed Prismarine slabs and stairs not having recipes.
* Right clicking on a piece of wool/carpet with Floral Powder now dyes it.
* The Terra Shatterer can no longer receive mana from Aura Bands. Fixes Terra Shatterers not being able to break blocks if one is equipped.

---

{% include changelog_header.html version="r1.1 68" %}

* Fixed Mana Lenses displaying weird on item frames.
* Fixed the Weight lens being able to move Tile Entities and unbreaking blocks like bedrock.
* Removed examplemod. Again. *aflsdjxfnsdxkhdoxu*
* Tweaked the lens entry text a bit.

---

{% include changelog_header.html version="r1.1 67" %}

* Added a config option to disable elf portal particles.
* Added two new Mana Lenses, the Influence and the Weight lens.
* Fixed lens z-fighting when on a spreader.
* Fixed Solid Vines being iffy in multiplayer.
* Fixed some improbable lens combinations being possible (like bore+entropy).
* Fixed the Forge Recipe Sorter complaining about non registered botania recipes.
* Nerfed the Agricarnation. Sorry :<
* The Terra Firma Rod now uses ore dictionary names rather than block references.

---

{% include changelog_header.html version="r1.1 66" %}

* Added Prismarine. Just like the one in the 1.8 snapshots but won't corrupt your world, yeah!
* Fixed Elven Quartz Blocks not being able to be broken back using the alchemy catalyst.
* Petals can now be buried via right clicking and emit particles when done so.
* Tossing Vines in a Petal Apothecary causes it to get an overgrown look. This is visual only.
* Updated a bunch of textures.

---

{% include changelog_header.html version="r1.1 65" %}

* Fixed the Alfheim portal renderer flickering when in the presence of a Beacon.
* Fixed the Mana Bottle always doing the same effect.
* Fixed the Terra Shatterer doubling up on block particles and sound while in multiplayer.
* Removed Vector Pool references to ensure 1.7.10 compatibility.

---

{% include changelog_header.html version="r1.1 64" %}

* Added an explanation to the lexica as to why some entries are in italics for a better start.
* Added better textures for Rings and Shears. *They don't look like penises now. Dammit reddit.*
* Added Ingame Configs using the new forge config system. You can access them via the Mods menu. **Botania now requires forge 1147 at least for this.**
* Added Mana in a Bottle. What does it do? How do you get it? Who knows.
* Added two new rods, the Rod of the Skies and the Rod of the Hells.
* Added Vine Balls and a Slingshot for them.
* Changed the Botania creative tab's icon to the Lexica Botania.
* Fixed a major Bauble dupe where equipping a bauble via right click would create a fully functional duplicate.
* Fixed Pixies spamming your console with their target.
* Fixed the Daffomill not being controllable with redstone.
* Fixed the inventory key not closing the Lexica Botania GUIs.
* Removed all the entries for Mana Lenses and squashed them in one fat entry.
* Renamed the Basics section to "Basics and Mechanics" for better understanding.
* *I stayed up till 4:20 (no joke intended, for real, I did) to finish this update, it might spontaneously combust. You have been warned.*

---

{% include changelog_header.html version="r1.1 63" %}

* Fixed generating flowers in the corners of chunks (and in general) causing lots of unnecessary chunk updates.
* Fixed lack of canUpdate() override on TileManaVoid, in theory this shouldn't impact performance unless you somehow had hundreds of them, but it fixes MCPC+ complaining. */shrug.*
* Flowers now force checks if they're not bound, this should, once again, in theory, fix flowers unbinding from pools/spreaders. */sigh*

---

{% include changelog_header.html version="r1.1 62" %}

* Fixed a dupe with the Alfheim portal involving Manasteel Blocks.
* Fixed Elven Quartz recipes being borked.

---

{% include changelog_header.html version="r1.1 61" %}

* Added Elven Quartz, it's like the other quartz variants but green and acquired by throwing quartz in the portal.
* Added some better instructions to the Mana Spreader entry regarding Mana Spreader unbinding and rotation. 
* Added the ability to mass trade Manasteel to the elves via blocks.
* Fixed all the Botania stairs having improper lighting.
* Fixed the Conjuration Catalyst not working with quartz because of the Mana Quartz recipe.
* Fixed the Kekimurus not eating from all sides.
* The Flugel Tiara is now under Elven Knowledge.
* The Ring of Chordata no longer gives a speed boost when the player is flying.
* The Wand of the Forest can rotate blocks now. This should work with all vanilla blocks and any mod blocks that use the forge rotation hook (Block.rotateBlock).

---

{% include changelog_header.html version="r1.1 60" %}

* Fixed Dreamwood variants not being craftable, which in turn prevented the Spectral Platform from being craftable too.
* Fixed the Elementium Shears dropping ghost wool items.
* Some small accuracy changes to the letter entry, because I no English.

---

{% include changelog_header.html version="r1.1 59" %}

* Fixed Daffomill entry typo.
* Fixed posh quartz variants (chiseled, pillar) not being craftable.
* The Bifrost blocks no longer explode into particles when they expire, that was causing lagspikes on large structures.

---

{% include changelog_header.html version="r1.1 58" %}

* **Added Alfhomancy** and lots of new *funky* items and blocks to go with it. *Cool beans.*
* Added 20 new update messages, gotta find em all.
* Added a little easter egg if you're Bevo. ;)
* Added some checks to the special flowers' code, to prevent potential crashes (mostly in multiplayer).
* Added some new textures for ingots, armor, shears, soulscribe, redstone spreader.
* Added the Daffomill, a new functional flower that moves things with wind.
* Added the Rod of the Seas, a water counterpart to the Rod of the Lands.
* Added the ability to unlock new types of knowledge to the Lexica Botania.
* Added utility controls to the Lexica Botania. You can now navigate through the pages with the cursor keys, pgup/pgdn, mouse wheel and swiping left/right with touchscreen mode enabled. Pressing backspace or clicking the right mouse button takes you back. Pressing the home key takes you to the main index.
* Buffed the range of the Hopperhock. *I apologize in advance if this causes your perfectly sized systems to conflict :(*
* Fixed Hopperhocks ignoring ISidedInventory for checking if there's space.
* Fixed Hopperhocks never putting any items in an inventory with an empty frame.
* Fixed String acquired from mana alchemy being metadata 1 and not being usable in most recipes.
* Fixed baubles that do effects on being equipped (such as the Soujourner's Sash) not working if they're equipped via right clicking in the hotbar.
* Fixed the Abstruse Platform being typoed as "Abtruse Platform".
* Fixed the Agricarnation not working with saplings.
* Fixed the Agricarnation trying to fertilize Vines.
* Fixed the Soulscribe being unbreakable.
* Removed the warning in the Thermalily's entry saying it produces a lot of power, as it's been nerfed a lot since it was introduced :p
* Small makeover to the Lexica Botania, it no longer has weird green floating text in the top now, but a fancier header instead.
* The Floral Fertilizer now spawns one more flower than before at all times.
* The Snowflake Pendant now only freezes water source blocks.
* You can now add up to 10 bookmarks to the Lexica Botania that allow you to go back to any entry.
* [API] Major changes, adaptation is required.

---

{% include changelog_header.html version="r1.0 57" %}

* Fixed moar tpyos (CatDany)
* Fixed server crash.
* Mana Spreaders can now be <a href="http://gfycat.com/EmotionalDisloyalCanary">remotely oriented</a> to a block rather than just by holding right click. There's some kinks to it, this is the best I can do.
* Reworked mana network internals to, hopefully, fix the issue of flowers disconnecting once and for all. *Crosses fingers*

---

{% include changelog_header.html version="r1.0 56" %}

* Fixed Mana Spreaders not showing the prediction beam, another thing broken by 54...

---

{% include changelog_header.html version="r1.0 55" %}

* Added some sanity checking to the flowers' connection code to reflect the changes in 54, be it for Spreaders or Pools. In theory, this ought to fix the bug where flowers lose their binding upon relog. I can't exactly tell you if that is the case though, because this issue is completely sporadic and nigh-on impossible to reproduce with consistency. ***Warning**: This change modifies API files SubTileGenerating.java and SubTileFunctional.java. Third time's the charm, right? Please be so.*
* Mana Mirrors no longer show their binding in the tooltip, but rather in world.
* [API] Added ICoordBoundItem.

---

{% include changelog_header.html version="r1.0 54" %}

* Added some checks to the adding of mana spreaders and mana pools to the world. In theory, this ought to fix the bug where flowers lose their binding upon relog. I can't exactly tell you if that is the case though, because this issue is completely sporadic and nigh-on impossible to reproduce with consistency. *Deja vu, I hate this bug.*
* Added some new items that don't do anything yet, don't mind them, I just happened to have them done before I pushed this update.

---

{% include changelog_header.html version="r1.0 53" %}

* Fixed compatibility with the latest forge versions with ore dictionary changes. **The mod now requires forge 1117 at minimum**.

---

{% include changelog_header.html version="r1.0 52" %}

* Added sound effects to the Petal Apothecary.
* Re-enabled the ingame downloader for new versions. Many thanks to Scottwears for providing web hosting that actually lets me do this :)
* Shift right clicking a Petal Apothecary or Runic Altar with an empty hand now removes the last item put in.
* Throwing a piece of Floral Powder in a Mana Pool now colors it.

---

{% include changelog_header.html version="r1.0 51" %}

* Added a config to disable Smokey Quartz for TT users.
* Added fancy versioning to the Lexica Botania.
* Added some sanity checking to the flowers' connection code, be it for Spreaders or Pools. In theory, this ought to fix the bug where flowers lose their binding upon relog. I can't exactly tell you if that is the case though, because this issue is completely sporadic and nigh-on impossible to reproduce with consistency. ***Warning**: This change modifies API files SubTileGenerating.java and SubTileFunctional.java.*
* Fixed the Soulscribe being able to be blocked with. *How does that even work anyway?*
* Fixed the config file re-saving if nothing changed.
* Renaming the Lexica Botania changes it's cover and title.

---

{% include changelog_header.html version="r1.0 50" %}

* Added a config option to disable the Fallen Kanade, for Blood Magic users.
* Added an alchemy recipe to turn slimeballs back into cactus. Basiscally the same as the other one, just backwards.
* Added some checks to the Rod of the Lands to prevent players from suffocating themselves with it.
* Added some new quartz types (might have gone overboard), find them in the Decorative Blocks entry under Miscellaneous.
* Added the Soulscribe, a weapon to kill Endermen.
* Some more work on <a href="http://gfycat.com/ShamefulGlitteringCentipede"><span class="censored">----------</span></a>.
* *I wonder what happens if I throw sixteen pink petals in an apothecary...*
* *I apologize for any possible issues in this patch, as I stayed up till 3 to finish it :')*

---

{% include changelog_header.html version="r1.0 49" %}

* Artificially increased the accuracy of the Damaging Lens and Terra Blade.
* Commented the flower quantity and density config options.
* Fixed the Kekimurus eating only from the south east.
* Some behind the scenes work on <a href="http://gfycat.com/DarkUnhealthyEthiopianwolf"><span class="censored">----------</span></a>.

---

{% include changelog_header.html version="r1.0 48" %}

* Fixed Generating Flowers never outputting any mana whatsoever to spreaders.
* **sigh* *

---

{% include changelog_header.html version="r1.0 47" %}

* Added a config option to disable the references in the flowers' tooltips. *Though, be warned, I hate you if you do <3*
* Added a tease for <span class="censored">----------</span>.
* Added two new generating flowers, the Entropinnyum and the Kekimurus. *Help, I'm running out of names :c*
* Buffed the output of the Rosa Arcana.
* Fixed Composite lenses not being localized.
* Fixed a crash with right clicking the Abtruse Platform with an empty hand.
* Fixed generating flowers outputting ALL their power to spreaders even if they can't take all of it. ***Warning**: This change modifies API files SubTileGenerating.java and IManaCollector.java.*
* [API] Added IAddonEntry for subtitle support on entries, mostly to prevent the whole [TT] or the like thing in Thaumcraft addons.

---

{% include changelog_header.html version="r1.0 46" %}

* Added the Abtruse Platform, a block to help you get around. *Totally not a copy paste from Thaumic Tinkerer, nope.*
* Added the Manastar flower (under the Mana Manipulation section), which lets you measure if you're getting a profit or loss of mana.
* Droped the nerfhammer on Dayblooms and Nightshades. To start off, they're a bit slower by default now. They also suffer diminshing returns from having any flowers of the same type adjacent. Go get creative with better mana generation methods now.
* Moar references!

---

{% include changelog_header.html version="r1.0 45" %}

* Temporarilly removed the auto downloader, the reason can be found <a href="http://www.minecraftforum.net/topic/2440071-botania-an-innovative-natural-magic-themed-tech-mod-not-in-beta-any-more/page__st__420#entry31815845">here</a>.

---

{% include changelog_header.html version="r1.0 44" %}

* Fixed a mana dupe with mana pools and hoppers below them.
* Fixed mana containing items being able to be repaired in an anvil.
* Fixed the mana alchmey page being wrongly localized.
* The Mana Enchanter can be constructed with Glimmering Flowers now.

---

{% include changelog_header.html version="r1.0 43" %}

* Fixed the auto downloader blocking your IP from my website for an hour instead of downloading a file because of a stupid coding mistake.
* *I'm an insufferable twat that should die off in a pit. I hate everything.*

---

{% include changelog_header.html version="r1.0 42" %}

* Added a slimeball alchemy recipe.
* Added Glimmering Flowers, which are basically normal flowers but shiny.
* Changed the recipes for the Loonium and the Charm of the Diva. Particularly, the last one was able to be crafted with any flower.
* Special (non-worldgen) flowers now won't break in one click.
* The Mana Blaster now gives some recoil. *such real much next gen very dakka wow*

---

{% include changelog_header.html version="r1.0 41" %}

* Added a new functional flower, the Loonium.
* Anything with "seed" in it's name is now useable in the Petal Apothecary.
* Botania now features the best version checker ever (tm), it can be disabled in the config, it lets you download the mod from inside the game, it even removes your old one so all you need to do is restart the game and you have it updated. It also comes with cool flavour messages.
* Fixed terrasteel blocks dropping manasteel blocks.
* Nerfed the thermalily, a lot. Go get creative and not rely on lava power now.
* *The mod is finally out of Beta! Rejoice!*

---

{% include changelog_header.html version="beta 40" %}

* Fixed crafting Terrasteel with more than one item at once using up all of them.
* Fixed incorrect particle positions for Terrasteel crafting.
* Fixed the Agricarnation trying to fertilize wood blocks (Fences for example).
* Fixed the Ring of Chordata disconnecting players in SMP.
* Fixed the Runic Altar and Mana Enchanter not rendering at some angles.
* Reworked the ore dictionary check for petal and rune recipes, should fix them not working with some mods.
* Reworked the ore dictionary check for the Pure Daisy, petal and rune, should fix it not working with some mods.
* The Runic Altar now has a cooldown, this should prevent newly crafted runes getting stuck in it.

---

{% include changelog_header.html version="beta 39" %}

* Added pink.
* I'm literally hitler.
* Mana Spreaders will no longer fire into the pool they're bound to.

---

{% include changelog_header.html version="beta 38" %}

* Added a Mana Bar display above the XP bar when there's items in the inventory that use mana.
* Added two new Baubles, the Charm of the Diva and the Flügel Tiara.
* Changed how Hydroangeas pick water, it's more random over a set pattern now. (Pokefenn)
* Fixed a mana dupe regarding mana storing items.
* Fixed pages in the Lexica Botania not showing stack sizes.
* Fixed the Mana Mirror not updating the pool properly in the client side.
* Fixed the Terra Shatterer having a debug print.
* Force Relays will not push the block they're bound to if pushed by a sticky piston. Remote Controlled Frames anybody?
* The Rannuncarpus will no longer place technical flowers at all, it was placing them as the placeholder.
* The Terra Shatterer can no longer receive mana from bands of aura.
* The Tiny Planet now has a limit on how far it can orbit bursts.
* Tweaked the wireframe bounding box of the spreader when a flower bound to it is hovered with a wand.
* [API] Added IManaUser and IManaCreativeItem.
* [API] Added isNoExport(ItemStack) to IManaItem

---

{% include changelog_header.html version="beta 37" %}

* Fixed a crash with the Mana Enchanter with invalid blocks around.
* Fixed the Band of Mana despawning like normal items.
* Fixed the Band of Mana not having a damage bar.

---

{% include changelog_header.html version="beta 36" %}

* Added an extra little bit of info to the Terra Shatterer's entry.
* Fixed the Jaded Amaranthus being able to place flowers in running water, basically creating instant farms.
* Fixed the Terra Shatterer not breaking blocks when enabled. *This is what I get for coding at 1AM.*

---

{% include changelog_header.html version="beta 35" %}

* Added a config option to remove block breaking particles.
* Added some new alchemy recipes - Cocoa Beans in the crop rotation and Wool to String.
* Added the Terra Shatterer, a new tool that increases power with the amount of mana you give it.
* Fixed a crash with the Wand of the Forest when placing it in a block from a mod that does not sync NBT tags (eg. Blood Magic).
* Fixed a small visual issue with Unstable Beacons displaying particles under the block.
* Lowered the amount of particles Unstable Beacons emit, they no longer eat your FPS as much.
* Mana spreaders with the bore lens will no longer break other full pools or spreaders.
* Swapped the aliases of the Tile Entities to prevent tiles with the same name from other mods conflicting, this comes with no backwards compatibility issues, you're fine.
* The magnetizing ring now pulls items to the center of the player, not the corner.
* [API] Added a LinkedHashSet for the SubTileEntity keys to be displayed on the creative menu for the getSubBlocks call for BlockSpecialFlower.

---

{% include changelog_header.html version="beta 34" %}

* Changed Ring Textures.
* Fixed a crash with illegal blocks around the pure daisy.
* Fixed a crash with non-lens items in the mana spreader.

---

{% include changelog_header.html version="beta 33" %}

* Added alternative names to Tile Entity names, fixes any tile entities (most flowers included) getting corrupted when updating from beta 31 to beta 32.

---

{% include changelog_header.html version="beta 32" %}

* Added a new Generating Flower, the Munchdew.
* Added a system to remap item/blocks keys into the new format, no more console spam regarding invalid prefixes.
* Added Manasteel and Terrasteel blocks, Manasteel Blocks can be crafted directly by throwing Iron Blocks in the Mana Pool.
* Added new Functional Flowers: Dreadthorne, Pollidisiac, Hyacidus and Clayconia.
* Added some dispenser behaviours, dispensers can now use the wand of the forest and plant all sorts of seeds.
* Added some new rings, Ring of Chordata, Ring of the Mantle and Ring of Magnetization.
* Added the Redstone Root, a crafting component that allows redstone control of Functional Flowers.
* Changed the rune textures from gag icons to actual symbols and japanese characters.
* Fixed a rare crash that happened randomly when a flower is placed.
* Fixed blocks in the lexicon not rendering with the proper shading. (duke605)
* Fixed redstone mana spreaders firing a mana burst once if loaded into the world with a redstone current.
* Fixed Runes of Air and Winter not being craftable with colored carpet or wool.
* Fixed some generating flowers acting like Dayblooms in which they'd generate mana passively.
* Fixed the Orechid's range being 1 higher in the negative direction than the positive direction.
* Fixed the Terra Blade not working when the player has the Haste effect.
* Nerfed the Fallen Kanade, lower range, higher cost, weaker regen.
* Removed the Miner's Ring from Baubles in order to allow for the Ring of the Mantle to actually be a thing.
* Rewrote the Hydroangeas' lexicon entry to not be so terrible *Points at Pokefenn*.
* The Bellethorne now hurts mobs one at a time, but faster.
* The Hydroangeas now function faster during rain.
* The Open Crate now puts a cooldown on items before they can be picked up by flowers if given a redstone signal.
* The Runic Altar now accepts items tossed in.
* [API] Added IManaTrigger.
* [API] Moved some classes around.

---

{% include changelog_header.html version="beta 31" %}

* Fixed Botania Tile Entities requesting client world rerenderers.
* Fixed a dupe with the Hoppperhock and Rannuncarpus flowers.
* Fixed moar tpyos.
* Fixed some misinformation regarding the Eye of the Ancients' lexicon entry.
* Fixed the Petal Apothecary sometimes taking dropped water buckets and not filling up. (hea3ven)
* Removed native ForgeMultipart support. You can still add the blocks in the config. It was disabled since the 1.7 port anyway.

---

{% include changelog_header.html version="beta 30" %}

* Added an alchemy recipe to convert redstone to glowstone.
* Added the Eye of the Ancients.
* Finally, once and for all, fixed the bug that has been plaguing the mod ever single 1.7 came out. Mana Spreaders and Terrasteel crafting will now both function regardless of the X and Z coordinates being positive or negative. *You have no idea how good it feels to get that one out of my shoulders.*
* Fixed a dupe with the petal apothecary.
* Fixed some generating flora stopping to output power to spreaders if they (the flowers) ever got full.

---

{% include changelog_header.html version="beta 29" %}

* Added the Open Crate.
* Fixed dedicated servers crashing on startup.
* The Mana Pool now spawns items that can be picked up by a Hopperhock before the pool crafts with them again.

---

{% include changelog_header.html version="beta 28" %}

* Fixed spreaders sometimes erroring on load.
* Removed the example mod forge comes with from the package.
* The Mana Blaster now has a color correspondent to the lens it has.

---

{% include changelog_header.html version="beta 27" %}

* Fixed a massive memory leak with shaders.

---

{% include changelog_header.html version="beta 26" %}

* Added the Alchemical Catalyst and some recipes to make use of it.
* Added the Rannuncarpus, a new functional flower that can place blocks.
* Changed internals of the Mana Network to use weak world references, thus preventing world leaks through TileEntities that are not invalidated before being removed. In theory, this should also fix flowers sometimes losing their bindings to spreaders or pools.
* Fixed a weird crash relating to metadata on colored items.
* Fixed Mana Spreaders being broken once again.
* Made the mod dependant on Forge 1056 or later due to inconsistencies in old versions and internals of the mod.
* Some tests are available for curious minds on github under /vazkii/botania/test
* Special flowers now emit less particles (even when full on mana).
* The Mana Pool now has a small cooldown between crafting items, it also plays a less obnoxious sound.
* The Tiny Planet can now be turned into a block and placed in world.
* *For any Katawa Shoujo fans reading this, check the Pyroclast Pendant entry to see how horrible of a person I am :'D*

---

{% include changelog_header.html version="beta 25" %}

* Added some particles and/or sound to the Jaded Amaranthus and the Agricarnation.
* Fixed a typo that caused some particles (pure daisy, mana pool, etc) to display in the wrong color.
* Fixed the creative mana pool giving a regular one with pick block (middle click).
* Force double checking with recurion for Terrasteel crafting, this should make it always work, there was some weird issue with position syncing, which caused it to not work on some cases.
* The Agricarnation now grows normal crops (Wheat, Potatos, Carrots) fine, this broke in 1.7.
* The Jaded Amaranthus can now place flowers on snow covered blocks.

---

{% include changelog_header.html version="beta 24" %}

* Buffed the range of generating flowers back to 6 blocks.
* Changed the way the checking for the mana infusion recipes work, this hopefully fixes IC2 woes.
* Fixed Terrasteel crafting having been broken by newer versions of forge, also made it fancier.
* The Bellethorne now deals four times more damage, but requires more mana.
* The Fallen Kanade now has lower range and is more expensive, but applies Regeneration IV.
* The Orechid now has some particles and sound when it places a block.
* *I swear to God, if this version does not fix manasteel with IC2 installed I'm going to punch a cat.*

---

{% include changelog_header.html version="beta 23" %}

* Fixed Mana Mirror and Floral Fertilizer recipes not taking any metadata.
* Fixed Pyroclast Pendant losing fire resistance on dimension change.
* *If you still can't craft Manasteel with IC2 installed, update to 2.2.1.470.*

---

{% include changelog_header.html version="beta 22" %}

* Added a failsafe for the Manasteel Ingot recipe with IC2 installed.
* Added a new "Baubles and Accessories" tab to the lexica and a bunch of stuff to it.
* Added Baubles API integration, Baubles is now required. 
* Added Essence of Eternal Life.
* Added The Everlasting Guilty Pool (infinite Mana Pool for creative, totally not a GC reference)
* Fixed looking up the recipe for some items going some levels lower and showing the recipe for one of the components, e.g. runes.
* Fixed the tooltip text for the items in the lexica botania always being in white rather than gray.
* Manasteel and Terrasteel tools can now be repaired in the vanilla anvil.
* Some texture changes.
* The Wand of the Forest is crafted with Livingwood sticks now.

---

{% include changelog_header.html version="beta 21" %}

* Damaging lenses now do more damage and don't hurt players.
* Fixed pylon_glow.frag not compiling and causing issues with the pylon renderer.
* Fixed the Terra Firma Rod not working at all since beta 20, it's what I get for "quick fixing". Sorry!

---

{% include changelog_header.html version="beta 20" %}

* Added more blocks to the list of valid Terra Firma Rod blocks (Sandstone, Mycellium, Hardened Clay, etc)
* Changed the Terra Blade texture to look a bit better in hand.
* Implemented IWireframeAABBProvider and added a null check to the wireframe render.
* Removed usage of the GLSL pow function, for lower end GPU support with shaders.
* Renamed Infection Seeds to Infestation Spores

---

{% include changelog_header.html version="beta 19" %}

* Added Mana Mirrors.
* Added Manasteel tools and armor.
* Added Pasture, Boreal and Infection Seeds.
* Added Redstone Mana Spreaders.
* Added Terrasteel, Terrasteel armor and the Terra Blade.
* Added the Horn of the Wild.
* Added the Rod of the Lands and the Terra Firma Rod.
* Added two new functional flowers, the Jiyuulia and the Tangleberrie.
* Changed some textures (Mana Spreader, Mana Pool, Livingrock, All Livingrock bricks, Mana Petals).
* Drastically cut the speed of the Hydroangeas.
* Fixed Mana Spreaders being awfully broken in some axis and conditions, gotta love what 1.7 did.
* Fixed Signal Flares using a sound that had been removed with 1.7.
* Fixed a bug caused by 1.7 where transparent objects would randomly cease to render.
* Fixed the Signal Flare fuse entity being under the same ID as the mana burst, and playing "ssssss" sounds when the world loads even if you had none around.
* Fixed the visual bug in multiplayer with placing petals in the apothecary.
* Fixed turntables not registering the new receiver.
* Increased the max distance of functional flowers to the pools from 6 to 10 blocks.
* Lowered the frequency of particles on regular (worldgen) mystical flowers and added a config option for it.
* Lowered the max distance of generating flowers to the spreaders from 6 to 4 blocks.
* Lowered the total amount of mana a Thermalily produces per lava block.
* Mana Bursts now travel through tall grass and flowers like they weren't there.
* Some renders (Mana Pylon, Mana Enchanter, Mana Pool) now utilize shaders and look a lot better than before thanks to that, this can be turned off in the config.
* Untinted mana bursts now have a bit of a lighter color.

---

{% include changelog_header.html version="beta 18" %}

* Fixed Mana Spreaders being all sorts of wrong, blame 1.7.
* Added a new Pylon model (you can still use the old one from the config file if you want).
* Changed the radius of the Floral Fertilizer.

---

{% include changelog_header.html version="beta 17" %}

* **Updated the mod to 1.7.2**
* Added Floral Fertilizer.
* Changed some lexicon entries.
* Fixed the Pestle and Mortar leaving the crafting table when used.
* Removed the ability to use Bone Meal to get Botania flowers.
* Renamed Floral Dye to Floral Powder.

---

{% include changelog_header.html version="beta 16" %}

* Fixed a typo in the config which lead to insane amounts of flowers in newly generated worlds.
* Gave a little makeover to the Lexica Botania, you'll notice it.

---

{% include changelog_header.html version="beta 15" %}

* Added a new decorating Livingwood variant.
* Added config options for flower generation. Cmat me TPPI.
* Added wireframes for blocks with bindings.
* Fixed crash with LiteLoader.
* Fixed flowers losing which collectors and pools they're bound to in world load, any old flowers need to be broken and replaced, though.
* Fixed the Fallen Kanade giving you JUST enough regeneration time for it not to kick in. Also made it regeneration II.
* Having a Mana Void under a Mana Pool makes the pool accept mana and dispose of any extra now.
* The Mana Spreader now takes a list of all positions and blocks visited by the scan beam and checks against that before doing another simulation, on layman's terms, fixed mana spreaders eating tickrate.

---

{% include changelog_header.html version="beta 14" %}

* Overriden vanilla blocks now relay the registerIcons call to the original block, should fix issues with mods that depend on them losing their textures (e.g. Witchery).

---

{% include changelog_header.html version="beta 13" %}

* Added Mana Cookies, nom.
* Fixed a bunch of gramatical issues in the lexica (Mierzen)
* The Petal Apothecary can now output to comparators.

---

{% include changelog_header.html version="beta 12" %}

* Added Composite Lenses.
* Added Mana Tablet creative version.
* Added recipe for Earth Rune using a Red Mushroom
* Added some new functional flowers: Orechid, Fallen Kanade, Exoflame, Agricarnation and Hopperhock.
* Added some new generating flowers: Thermalily, Nightshade and Rosa Arcana.
* Added the Mana Blaster.
* Added the Mana Detector.
* Added the Mana Enchanter.
* Added the Mana Lenses: Phantom, Entropic and Magnetizing.
* Added the Spreader Turntable.
* Botania and vanilla flowers now have visual snow under them if there's snow on any side. Makes snow biomes much more pleasing.
* Changed quite a few things with the API.
* Changed the Hydroangeas and Endoflame textures.
* Changed the cost for the Jaded Amaranthus and the Hydroanges, crafting wise. They're more expensive now.
* Clipping checks for the Mana Burst entity are now handled internally, might fix some crashes.
* Endoflames will no longer absorb items with container item data (eg Lava Buckets)
* Fixed Dayblooms producing mana even at night.
* Fixed Mana Pools never getting removed from the mana network, causing functional flowers trying to bind to pools that don't exist and never getting any mana.
* Fixed Rune Altar pulling in mana even after the recipe has been concluded.
* Fixed bursts with a Gravity lens having different arcs in prevision and reality.
* Fixed crafting Mana Lenses outputting size 0 stacks.
* Fixed some ambiguity in the Lexica Botania regarding some items (Heisei Dream and Rune Altar).
* Fixed the Mana Spreader shooting a client only burst in singleplayer, having it shoot twice as much as it should.
* Fixed tpyos.
* Items in the petal and rune crafting pages of the Lexica Botania spin slower now, if enabled.
* Made the Damaging lens more powerful.
* Mana Lenses no longer display their dyed versions in the creative inventory, too much clutter.
* Mana Tablets dropped on the ground won't ever despawn.
* Normalized the sound for the functional flowers mana reading to be the same volume as other mana blocks.
* Rebalanced the cost for tier 3 runes.
* Redid the way flower generation works, it's much less sporadic and random and much more uniform over parts of the world.
* Right clicking on Force Relays now has a sound effect.
* The Mana Void now resists explosions as much as obsidian does.
* The Petal Apothecary can now get water from dropped water buckets in item form.
* The client prediction beam now has a larger than normal point that always displays, indicating where mana loss starts to happen.
* The client prediction beam with the Wand of the Forest is no longer continuous, but rather, a moving lance. This effectively removes the lag caused by it on a large scale. There's a config option to use the continuous beam instead.
* Tweaked the costs and power for some lenses.

---

{% include changelog_header.html version="beta 11" %}

* Added some null checks to the client HUD for the Spreader, fixes a weird crash.
* Added functionaility to the Lexica Botania to redirect to other entries when clicking an item, in order to see it's recipe.
* Shift clicking the back button in the Lexica Botania now goes back to the index, regardless of where one would be.
* Documented the fact that there is mana loss for the spreaders.
* API Rework for SubTileEntities and the Mana Network classes. SubTileGenerating and SubTileFunctional are now available API side, as well as all the methods from the Mana Network (getClosestCollector/Pool, getAllCollectors/Pools).
* Fixed the Signal Flare ID not being configurable.
* Added the Mana Tablet item.

---

{% include changelog_header.html version="beta 10" %}

* Added Hydroangeas. (Pokefenn)
* Added the Mana Void.
* Bound the lexicon entry for the Jaded Amaranthus to the block.
* Fixed a ConcurrentModificationException just waiting to happen with the Wand of the Forest.
* Fixed Livingwood and Livingrock blocks dropping the wrong metadata value.
* Fixed a typo in the config. (May need to set subtle power system to true again after you install this version if you had it on)
* Fixed Endoflames eating full stacks of items.
* Endoflames will now only burn items if they have somewhere to output the power.
* Endoflames will now wait 1 second after the item can be picked up to burn it.
* Endoflames no longer burn Mana Spreaders, ever.
* Mana Spreaders now lose 5% of mana put into it by other spreaders. This change is done to combat infinite lenght transportation with no power loss or adverse effects.
* Added the Force Relay to the mod's creative tab, forgot it last patch :p
* Added comparator support to Mana Pools.
* Added a check to the Mana HUD to display at least one pixel of colored bar if there's ANY mana at all in the block.
* Tweaked the super update call on the Mana Burst. Should fix some potential issues or crashes with vector pool sizes.
* All crafted flowers now have some flavor text.

---

{% include changelog_header.html version="beta 9" %}

* Fixed Livingwood and Livingrock blocks having 0 stack size in creative.
* Fixed Mana Pools double dipping for resources.

---

{% include changelog_header.html version="beta 8" %}

* Added the Forge Relay, Mana Pylon, Unstable Beacon, Signal Flare, Mana Distributor and Jaded Amaranthus.
* Endoflame particles are a lot more subtle and a lot less resource intensive.
* Tweaked the look of the Mana Bursts a bit, also added a config option to reduce the density of those.
* Added a bunch of decorative blocks (with forge multipart support).
* Added an alt recipe for the runic altar that uses a mana diamond.
* Tweaked the Daybloom's values a bit and added a hint to the lexicon entry.

---

{% include changelog_header.html version="beta 7" %}

* Fixed a client crash when breaking flowers with a wand in hand.
* Runes take less time to make now.
* More recipe balance changes.
* Fixed the Rune of Wrath not being in the lexicon.
* The mana network gets cleared when there's no world loaded. Fixes "phantom beams" in multiplayer.
* The rune altar calculates positions during the render now, fixes weird multiplayer stacking.

---

{% include changelog_header.html version="beta 6" %}

* Fixed a server crash when putting lens in a spreader.
* Endoflame now generates at twice the speed.
* Changed the lexicon's HUD to be a bit more opaque.
* The Wand of the Forest is now Full 3D.
* Rebalanced some recipes.
* Fixed a critical issue where the flowers wouldn't write to NBT (or packets) properly.

---

{% include changelog_header.html version="beta 5" %}

* Minor texture changes.
* Fixed the Rune Altar not working in singleplayer (lol).
* All special flowers have the same breaking particles now.
* Added 3 new flowers.
* Added a HUD to when a block that can be looked up in the lexicon is being hovered.

---

{% include changelog_header.html version="beta 4" %}

* The mod works in multiplayer now, or it should, at least.
* Fixed the earth rune using mushroom blocks (the ones in giant mushrooms).
* Fixed the rune recipes being out of order.
* Fixed not being able to craft Bellethrone.
* Looking up something via world right click now requires the player to be sneaking.
* Fancified a bunch of things.

---

{% include changelog_header.html version="alpha 3" %}

* Fixed the Rune Altar not accepting any recipes.
* Fixed the recipes for runes being out of order.

---

{% include changelog_header.html version="alpha 2" %}

* Documented all the things in the lexicon.
* All the blocks/items have recipes now, you can play it in survival
* Petal Apothecary no longer removes water buckets in creative
* Mana Spreader now faces one of 6 directions when placed
* Daybloom only produces during daytime
* Bellethorne has an interval between activation
* The Lexicon GUI doesn't pause the game

---

{% include changelog_header.html version="alpha 1" %}

* Initial realese.
