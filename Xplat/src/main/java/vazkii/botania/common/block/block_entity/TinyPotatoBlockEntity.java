/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.block_entity;

import it.unimi.dsi.fastutil.objects.ObjectArrays;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.Nameable;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.BotaniaStats;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.decor.TinyPotatoBlock;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.helper.VecHelper;
import vazkii.botania.common.item.block.TinyPotatoBlockItem;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class TinyPotatoBlockEntity extends ExposedSimpleInventoryBlockEntity implements Nameable {
	private static final ResourceLocation BIRTHDAY_ADVANCEMENT = botaniaRL("challenge/tiny_potato_birthday");
	private static final boolean IS_BIRTHDAY = isTinyPotatoBirthday();
	private static final String TAG_NAME = "name";
	private static final int JUMP_EVENT = 0;
	private static final Map<String, String> GENDER = new HashMap<>(Map.ofEntries(
			Map.entry("girlstater", "daughter"),
			Map.entry("lesbiabtater", "daughter"),
			Map.entry("lesbiamtater", "daughter"),
			Map.entry("lesbiantater", "daughter"),
			Map.entry("lesbitater", "daughter"),
			Map.entry("lessbientater", "daughter"),

			Map.entry("agendertater", "child"),
			Map.entry("enbytater", "child"),
			Map.entry("nbtater", "child"),
			Map.entry("nonbinarytater", "child"),
			Map.entry("robotater", "child"),
			Map.entry("wiretater", "child"),
			Map.entry("eutrotater", "child"),
			Map.entry("bob", "child"),
			Map.entry("snences", "child"),

			Map.entry("genderfluidtater", "child"),
			Map.entry("taterfluid", "child"),
			Map.entry("eggtater", "child"),
			Map.entry("tategg", "child"),
			Map.entry("transtater", "child"),

			Map.entry("manytater", "children"),
			Map.entry("pluraltater", "children"),
			Map.entry("snorps", "children"),
			Map.entry("systater", "children"),
			Map.entry("systemtater", "children"),

			// The best gender
			Map.entry("tomater", "tomato")
	));

	public int jumpTicks = 0;
	public Component name = Component.literal("");
	private int nextDoIt = 0;
	private int birthdayTick = 0;

	public TinyPotatoBlockEntity(BlockPos pos, BlockState state) {
		super(BotaniaBlockEntities.TINY_POTATO, pos, state);
	}

	public void interact(Player player, InteractionHand hand, ItemStack stack, Direction side) {
		if (!level.isClientSide) {
			int index = side.get3DDataValue();
			ItemStack stackAt = getItemHandler().getItem(index);
			if (!stackAt.isEmpty() && stack.isEmpty()) {
				player.setItemInHand(hand, stackAt);
				getItemHandler().setItem(index, ItemStack.EMPTY);
			} else if (!stack.isEmpty()) {
				ItemStack copy = stack.split(1);

				if (stack.isEmpty()) {
					player.setItemInHand(hand, stackAt);
				} else if (!stackAt.isEmpty()) {
					player.getInventory().placeItemBackInInventory(stackAt);
				}

				getItemHandler().setItem(index, copy);
			}

			jump();

			if (name.getString().toLowerCase(Locale.ROOT).trim().endsWith("shia labeouf") && nextDoIt == 0) {
				nextDoIt = 40;
				level.playSound(null, worldPosition, BotaniaSounds.doit, SoundSource.BLOCKS, 1F, 1F);
			}

			ItemStack tater = ItemStack.EMPTY;
			boolean manyTater = false;
			for (int i = 0; i < getContainerSize(); i++) {
				ItemStack otherStack = getItem(i);
				if (!otherStack.isEmpty() && otherStack.is(BotaniaBlocks.tinyPotato.asItem())) {
					if (tater.isEmpty()) {
						tater = otherStack;
					} else {
						manyTater = true;
						break;
					}
				}
			}
			if (!tater.isEmpty()) {
				String taterGender = manyTater ? "children" : "son";
				if (!manyTater && tater.has(DataComponents.CUSTOM_NAME)) {
					StringBuilder childNameBuilder = new StringBuilder();
					TinyPotatoBlockItem.isEnchantedName(tater.getHoverName(), childNameBuilder);
					taterGender = GENDER.getOrDefault(childNameBuilder.toString(), taterGender);
				}
				if (player instanceof ServerPlayer serverPlayer) {
					serverPlayer.sendSystemMessage(Component.translatable("botania.tater.my_" + taterGender), true);
				}
			}

			player.awardStat(BotaniaStats.TINY_POTATOES_PETTED);
			PlayerHelper.grantCriterion((ServerPlayer) player, botaniaRL("main/tiny_potato_pet"), "code_triggered");
		}
	}

	private void jump() {
		if (jumpTicks == 0) {
			level.blockEvent(getBlockPos(), getBlockState().getBlock(), JUMP_EVENT, 20);
		}
	}

	@Override
	public boolean triggerEvent(int id, int param) {
		if (id == JUMP_EVENT) {
			jumpTicks = param;
			return true;
		} else {
			return super.triggerEvent(id, param);
		}
	}

	public static void commonTick(Level level, BlockPos pos, BlockState state, TinyPotatoBlockEntity self) {
		if (self.jumpTicks > 0) {
			self.jumpTicks--;
		}

		if (!level.isClientSide) {
			if (level.random.nextInt(100) == 0) {
				self.jump();
			}
			if (self.nextDoIt > 0) {
				self.nextDoIt--;
			}
			if (IS_BIRTHDAY) {
				self.tickBirthday();
			}
		}
	}

	private void tickBirthday() {
		var facing = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
		var facingPos = getBlockPos().relative(facing);

		if (level.hasChunkAt(facingPos)) {
			var facingState = level.getBlockState(facingPos);
			var cakeColor = getLitCakeColor(facingState, level.getRandom());
			var players = PlayerHelper.getRealPlayersIn(level,
					VecHelper.boxForRange(Vec3.atCenterOf(getBlockPos()), 8));

			if (cakeColor != null && !players.isEmpty()) {
				birthdayTick++;

				// 3.5s per message, initial delay of 5s
				var messageTimes = List.of(100, 170, 240, 310, 380);
				var messageIndex = messageTimes.indexOf(birthdayTick);
				if (messageIndex != -1) {
					Object[] args = messageIndex == 1 ? new Object[] { getTinyPotatoAge() } : ObjectArrays.EMPTY_ARRAY;
					var message = Component.literal("<")
							.append(getDisplayName())
							.append("> ")
							.append(Component.translatable("botania.tater_birthday." + messageIndex, args));

					for (var player : players) {
						player.sendSystemMessage(message);
					}
					jump();
					TinyPotatoBlock.spawnHearts((ServerLevel) level, getBlockPos());
				}

				if (messageIndex == messageTimes.size() - 1) {
					CompoundTag explosion = new CompoundTag();
					explosion.putByte("Type", (byte) FireworkRocketItem.Shape.LARGE_BALL.getId());
					explosion.putBoolean("Flicker", true);
					explosion.putBoolean("Trail", true);
					explosion.putIntArray("Colors", List.of(
							cakeColor.getFireworkColor(),
							0xD260A5, 0xE4AFCD, 0xFEFEFE, 0x57CEF8
					));

					ListTag explosions = new ListTag();
					explosions.add(explosion);

					ItemStack rocket = new ItemStack(Items.FIREWORK_ROCKET);
					CompoundTag rocketFireworks = rocket.getOrCreateTagElement("Fireworks");
					rocketFireworks.putByte("Flight", (byte) 0);
					rocketFireworks.put("Explosions", explosions);

					level.addFreshEntity(new FireworkRocketEntity(level, facingPos.getX() + 0.5, facingPos.getY() + 0.5, facingPos.getZ() + 0.5, rocket));
					level.removeBlock(facingPos, false);
					level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, facingPos, Block.getId(facingState));
					// Usage of vanilla sound event: Subtitle is "Eating", generic sounds are meant to be reused.
					level.playSound(null, getBlockPos(), SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1F, 0.5F + (float) Math.random() * 0.5F);

					for (var player : players) {
						PlayerHelper.grantCriterion((ServerPlayer) player, BIRTHDAY_ADVANCEMENT, "code_triggered");
					}
				}
			}
		}
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (level != null && !level.isClientSide) {
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}

	@Override
	public void writePacketNBT(CompoundTag cmp) {
		super.writePacketNBT(cmp);
		cmp.putString(TAG_NAME, Component.Serializer.toJson(name));
	}

	@Override
	public void readPacketNBT(CompoundTag cmp) {
		super.readPacketNBT(cmp);
		name = Component.Serializer.fromJson(cmp.getString(TAG_NAME));
	}

	@Override
	protected SimpleContainer createItemHandler() {
		return new SimpleContainer(6) {
			@Override
			public int getMaxStackSize() {
				return 1;
			}
		};
	}

	@NotNull
	@Override
	public Component getName() {
		return BotaniaBlocks.tinyPotato.getName();
	}

	@Nullable
	@Override
	public Component getCustomName() {
		return name.getString().isEmpty() ? null : name;
	}

	@NotNull
	@Override
	public Component getDisplayName() {
		return hasCustomName() ? getCustomName() : getName();
	}

	private static final List<Block> ALL_CANDLE_CAKES = List.of(
			Blocks.WHITE_CANDLE_CAKE, Blocks.ORANGE_CANDLE_CAKE, Blocks.MAGENTA_CANDLE_CAKE, Blocks.LIGHT_BLUE_CANDLE_CAKE,
			Blocks.YELLOW_CANDLE_CAKE, Blocks.LIME_CANDLE_CAKE, Blocks.PINK_CANDLE_CAKE, Blocks.GRAY_CANDLE_CAKE,
			Blocks.LIGHT_GRAY_CANDLE_CAKE, Blocks.CYAN_CANDLE_CAKE, Blocks.PURPLE_CANDLE_CAKE, Blocks.BLUE_CANDLE_CAKE,
			Blocks.BROWN_CANDLE_CAKE, Blocks.GREEN_CANDLE_CAKE, Blocks.RED_CANDLE_CAKE, Blocks.BLACK_CANDLE_CAKE,
			Blocks.CANDLE_CAKE
	);

	@Nullable
	private static DyeColor getLitCakeColor(BlockState state, RandomSource rand) {
		var idx = ALL_CANDLE_CAKES.indexOf(state.getBlock());
		if (idx == -1) {
			return null;
		}

		if (!state.getValue(CandleCakeBlock.LIT)) {
			return null;
		}

		if (idx == 16) { // Uncolored candle cake, choose a random color
			return DyeColor.byId(rand.nextInt(16));
		}

		return DyeColor.byId(idx);
	}

	private static boolean isTinyPotatoBirthday() {
		// Tiny Potato was added in commit c225a134043922724e6ff141ff26f31097d4d9d0,
		// created on July 19, 2014
		var now = LocalDateTime.now();
		return now.getMonth() == Month.JULY && now.getDayOfMonth() == 19;
	}

	private static int getTinyPotatoAge() {
		var now = LocalDateTime.now();
		return now.getYear() - 2014;
	}
}
