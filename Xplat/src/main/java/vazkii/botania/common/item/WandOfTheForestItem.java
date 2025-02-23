/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item;

import com.mojang.datafixers.util.Pair;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.*;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.block.Bound;
import vazkii.botania.api.block.WandBindable;
import vazkii.botania.api.item.CoordBoundItem;
import vazkii.botania.api.state.BotaniaStateProperties;
import vazkii.botania.client.core.proxy.ClientProxy;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.BotaniaBlocks;
import vazkii.botania.common.block.ForceRelayBlock;
import vazkii.botania.common.block.block_entity.ManaEnchanterBlockEntity;
import vazkii.botania.common.component.BotaniaDataComponents;
import vazkii.botania.common.handler.BotaniaSounds;
import vazkii.botania.common.helper.DataComponentHelper;
import vazkii.botania.common.helper.PlayerHelper;
import vazkii.botania.common.lib.BotaniaTags;
import vazkii.botania.common.proxy.Proxy;
import vazkii.botania.network.EffectType;
import vazkii.botania.network.clientbound.BotaniaEffectPacket;
import vazkii.botania.xplat.BotaniaConfig;
import vazkii.botania.xplat.XplatAbstractions;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static vazkii.botania.api.BotaniaAPI.botaniaRL;

public class WandOfTheForestItem extends Item implements CustomCreativeTabContents {

	public final ChatFormatting modeChatFormatting;

	public WandOfTheForestItem(ChatFormatting formatting, Item.Properties builder) {
		super(builder);
		this.modeChatFormatting = formatting;
	}

	private static boolean tryCompleteBinding(GlobalPos src, ItemStack stack, UseOnContext ctx) {
		BlockPos dest = ctx.getClickedPos();
		if (!dest.equals(src.pos()) && src.dimension().equals(ctx.getLevel().dimension())) {
			setBindingAttempt(stack, null);

			// TODO: WandBindable should be a capability (Fabric and NeoForge support both blocks and block entities)
			BlockEntity srcTile = ctx.getLevel().getBlockEntity(src.pos());
			if (srcTile instanceof WandBindable bindable) {
				if (bindable.bindTo(ctx.getPlayer(), stack, dest, ctx.getClickedFace())) {
					doParticleBeamWithOffset(ctx.getLevel(), src.pos(), dest);
					setBindingAttempt(stack, null);
				}
				return true;
			}
		}
		return false;
	}

	// TODO: maybe attach Wandable capability to lapis block?
	private static boolean tryFormEnchanter(UseOnContext ctx) {
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Direction.Axis axis = ManaEnchanterBlockEntity.canEnchanterExist(world, pos);

		if (axis != null) {
			if (!world.isClientSide) {
				world.setBlockAndUpdate(pos, BotaniaBlocks.enchanter.defaultBlockState().setValue(BotaniaStateProperties.ENCHANTER_DIRECTION, axis));
				world.playSound(null, pos, BotaniaSounds.enchanterForm, SoundSource.BLOCKS, 1F, 1F);
				PlayerHelper.grantCriterion((ServerPlayer) ctx.getPlayer(), botaniaRL("main/enchanter_make"), "code_triggered");
			} else {
				for (int i = 0; i < 50; i++) {
					float red = (float) Math.random();
					float green = (float) Math.random();
					float blue = (float) Math.random();

					double x = (Math.random() - 0.5) * 6;
					double y = (Math.random() - 0.5) * 6;
					double z = (Math.random() - 0.5) * 6;

					float velMul = 0.07F;

					float motionx = (float) -x * velMul;
					float motiony = (float) -y * velMul;
					float motionz = (float) -z * velMul;
					WispParticleData data = WispParticleData.wisp((float) Math.random() * 0.15F + 0.15F, red, green, blue);
					world.addParticle(data, pos.getX() + 0.5 + x, pos.getY() + 0.5 + y, pos.getZ() + 0.5 + z, motionx, motiony, motionz);
				}
			}

			return true;
		}
		return false;
	}

	// TODO: force relays should use WandBindable as a block capability for this
	private static boolean tryCompletePistonRelayBinding(UseOnContext ctx) {
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		Player player = ctx.getPlayer();

		GlobalPos bindPos = ((ForceRelayBlock) BotaniaBlocks.pistonRelay).activeBindingAttempts.get(player.getUUID());
		if (bindPos != null && bindPos.dimension() == world.dimension()) {
			((ForceRelayBlock) BotaniaBlocks.pistonRelay).activeBindingAttempts.remove(player.getUUID());
			ForceRelayBlock.WorldData data = ForceRelayBlock.WorldData.get(world);
			data.mapping.put(bindPos.pos(), pos.immutable());
			data.setDirty();

			XplatAbstractions.INSTANCE.sendToNear(world, pos, new BotaniaEffectPacket(EffectType.PARTICLE_BEAM,
					bindPos.pos().getX() + 0.5, bindPos.pos().getY() + 0.5, bindPos.pos().getZ() + 0.5,
					pos.getX(), pos.getY(), pos.getZ()));

			world.playSound(null, player.getX(), player.getY(), player.getZ(), BotaniaSounds.ding, SoundSource.PLAYERS, 1F, 1F);
			return true;
		}
		return false;
	}

	@NotNull
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		ItemStack stack = ctx.getItemInHand();
		Level world = ctx.getLevel();
		Player player = ctx.getPlayer();
		BlockPos pos = ctx.getClickedPos();
		GlobalPos globalPos = GlobalPos.of(world.dimension(), pos);
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		Direction side = ctx.getClickedFace();
		Optional<GlobalPos> boundPos = getBindingAttempt(stack);

		if (player == null) {
			return InteractionResult.PASS;
		}

		if (player.isSecondaryUseActive()) {
			if (boundPos.filter(loc -> tryCompleteBinding(loc, stack, ctx)).isPresent()) {
				return InteractionResult.SUCCESS;
			}

			if (player.mayUseItemAt(pos, side, stack)
					&& (!(block instanceof CommandBlock) || player.canUseGameMasterBlocks())) {
				BlockState newState = manipulateBlockstate(state, side, blockState -> blockState.canSurvive(world, pos));
				if (newState != state) {
					world.setBlockAndUpdate(pos, newState);
					ctx.getLevel().playSound(
							ctx.getPlayer(), ctx.getClickedPos(), newState.getBlock().asItem().getBreakingSound()/*.getSoundType(newState).getPlaceSound()*/,
							SoundSource.BLOCKS, 1F, 1F
					);
					return InteractionResult.SUCCESS;
				}
			}
		}

		if (state.is(Blocks.LAPIS_BLOCK) && BotaniaConfig.common().enchanterEnabled() && tryFormEnchanter(ctx)) {
			return InteractionResult.SUCCESS;
		}

		BlockEntity tile = world.getBlockEntity(pos);

		if (getBindMode(stack) && tile instanceof WandBindable bindable && player.isShiftKeyDown() && bindable.canSelect(player, stack, pos, side)) {
			if (boundPos.filter(globalPos::equals).isPresent()) {
				setBindingAttempt(stack, null);
			} else {
				setBindingAttempt(stack, globalPos);
			}

			if (world.isClientSide) {
				player.playSound(BotaniaSounds.ding, 0.11F, 1F);
			}

			return InteractionResult.SUCCESS;
		} else {
			var wandable = XplatAbstractions.INSTANCE.findWandable(world, pos, state, tile);
			if (wandable != null) {
				return wandable.onUsedByWand(player, stack, side) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
			}
		}

		if (!world.isClientSide && getBindMode(stack) && tryCompletePistonRelayBinding(ctx)) {
			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	private static BlockState manipulateBlockstate(BlockState oldState, Direction side, Predicate<BlockState> canSurvive) {
		if (oldState.is(BotaniaTags.Blocks.UNWANDABLE)) {
			return oldState;
		}

		if (oldState.getBlock() instanceof RotatedPillarBlock) {
			return iterateToNextValidPropertyValue(oldState, BlockStateProperties.AXIS, BlockStateProperties.AXIS.getPossibleValues(), oldState.getValue(BlockStateProperties.AXIS), canSurvive);
		}

		if (oldState.hasProperty(BlockStateProperties.ROTATION_16)) {
			// standing sign, ceiling-hanging sign or similar block
			return iterateToNextValidPropertyValue(oldState, BlockStateProperties.ROTATION_16, BlockStateProperties.ROTATION_16.getPossibleValues(), oldState.getValue(BlockStateProperties.ROTATION_16), canSurvive);
		}

		// mostly intended for HugeMushroomBlock, but might be useful for certain modded blocks as well:
		BooleanProperty directionPropertyFromSide = PipeBlock.PROPERTY_BY_DIRECTION.get(side);
		if (oldState.hasProperty(directionPropertyFromSide) && oldState.getProperties().containsAll(PipeBlock.PROPERTY_BY_DIRECTION.values())) {
			boolean oldValue = oldState.getValue(directionPropertyFromSide);
			BlockState newState = oldState.setValue(directionPropertyFromSide, !oldValue);
			return canSurvive.test(newState) ? newState : oldState;
		}

		if (side.getAxis() != Direction.Axis.Y) {
			if (oldState.getBlock() instanceof SlabBlock) {
				// toggle between top and bottom slab
				switch (oldState.getValue(BlockStateProperties.SLAB_TYPE)) {
					case TOP:
						return oldState.setValue(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM);
					case BOTTOM:
						return oldState.setValue(BlockStateProperties.SLAB_TYPE, SlabType.TOP);
					default:
						// ignore double slabs
				}
			} else if (oldState.hasProperty(BlockStateProperties.HALF)) {
				// flip stairs or trapdoors upside down
				BlockState newState = oldState.cycle(BlockStateProperties.HALF);
				return canSurvive.test(newState) ? newState : oldState;
			}
		}

		// blocks with a "facing" property are subject to special rotation rules
		Optional<Property<?>> facingPropOptional = oldState.getProperties().stream()
				.filter(prop -> prop.getName().equals("facing") && prop.getValueClass() == Direction.class).findFirst();
		if (facingPropOptional.isPresent()) {
			@SuppressWarnings("unchecked")
			Property<Direction> facingProp = (Property<Direction>) facingPropOptional.get();
			return rotateFacingDirection(oldState, side, canSurvive, facingProp);
		}

		// fallback: let the block itself figure it out
		for (Rotation rot : new Rotation[] { Rotation.CLOCKWISE_90, Rotation.CLOCKWISE_180, Rotation.COUNTERCLOCKWISE_90 }) {
			BlockState newState = oldState.rotate(rot);
			if (canSurvive.test(newState)) {
				return newState;
			}
		}
		return oldState;
	}

	private static BlockState rotateFacingDirection(BlockState oldState, Direction side, Predicate<BlockState> canSurvive, Property<Direction> facingProp) {
		if (oldState.hasProperty(BlockStateProperties.CHEST_TYPE) && !oldState.getValue(BlockStateProperties.CHEST_TYPE).equals(ChestType.SINGLE)
				|| oldState.hasProperty(BlockStateProperties.EXTENDED) && oldState.getValue(BlockStateProperties.EXTENDED).equals(Boolean.TRUE)
				|| oldState.hasProperty(BlockStateProperties.BED_PART)) {
			// rotating double chests would be nice, but seems beyond the scope of this feature; same goes for beds and extended pistons
			return oldState;
		}

		Direction oldDir = oldState.getValue(facingProp);
		if (oldState.hasProperty(BlockStateProperties.ATTACH_FACE) && oldState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			// FaceAttachedHorizontalDirectionalBlock or equivalent block, rotate around clicked side
			if (side.getAxis() == Direction.Axis.Y) {
				// clicked vertically attached block from top or bottom, just rotate on that face
				return rotateClockwiseAroundSideDirect(oldState, side, canSurvive, facingProp, oldDir);
			}

			AttachFace attachFace = oldState.getValue(BlockStateProperties.ATTACH_FACE);
			if (attachFace == AttachFace.WALL && oldDir.getAxis() == side.getAxis()) {
				// clicked wall-attached block on attachment axis, just flip to other side, if possible
				BlockState newState = oldState.setValue(facingProp, oldDir.getOpposite());
				return canSurvive.test(newState) ? newState : oldState;
			}

			// operate on an implied direction, rotate that, and eventually translate it back at the end
			Direction impliedDir = switch (attachFace) {
				case FLOOR -> Direction.DOWN;
				case CEILING -> Direction.UP;
				case WALL -> oldDir;
			};

			Function<Direction, BlockState> newStateFunction = dir -> switch (dir) {
				case UP -> oldState.setValue(BlockStateProperties.ATTACH_FACE, AttachFace.CEILING);
				case DOWN -> oldState.setValue(BlockStateProperties.ATTACH_FACE, AttachFace.FLOOR);
				default -> oldState.setValue(BlockStateProperties.ATTACH_FACE, AttachFace.WALL).setValue(facingProp, dir);
			};

			return rotateClockwiseAroundSide(side, impliedDir, newStateFunction, canSurvive);
		}

		List<Direction> possibleFacingValues = new ArrayList<>(BlockStateProperties.FACING.getPossibleValues());
		if (possibleFacingValues.retainAll(facingProp.getPossibleValues())) {
			// doesn't support all possible directions
			if (possibleFacingValues.isEmpty()) {
				// How did we get here?
				return oldState;
			}

			// iterate over values in the order defined by BlockStateProperties.FACING,
			// because it makes more sense than the native order of the Direction enum values
			return iterateToNextValidPropertyValue(oldState, facingProp, possibleFacingValues, oldDir, canSurvive);
		}

		if (oldDir.getAxis() != side.getAxis()) {
			// rotate clockwise around clicked side
			return rotateClockwiseAroundSideDirect(oldState, side, canSurvive, facingProp, oldDir);
		}

		// facing towards or away from clicked side, flip around
		BlockState newState = oldState.setValue(facingProp, oldDir.getOpposite());
		return canSurvive.test(newState) ? newState : oldState;
	}

	@NotNull
	private static BlockState rotateClockwiseAroundSideDirect(BlockState oldState, Direction side, Predicate<BlockState> canSurvive, Property<Direction> facingProp, Direction oldDir) {
		return rotateClockwiseAroundSide(side, oldDir, dir -> oldState.setValue(facingProp, dir), canSurvive);
	}

	@NotNull
	private static BlockState rotateClockwiseAroundSide(Direction side, Direction oldDir, Function<Direction, BlockState> newStateFunction, Predicate<BlockState> canSurvive) {
		BlockState newState;
		Direction newDir = oldDir;
		do {
			newDir = getClockwiseDirectionForSide(side, newDir);
			newState = newStateFunction.apply(newDir);
		} while (newDir != oldDir && !canSurvive.test(newState));

		return newState;
	}

	@NotNull
	private static Direction getClockwiseDirectionForSide(Direction side, Direction oldDir) {
		return side.getAxisDirection() == Direction.AxisDirection.NEGATIVE
				? oldDir.getCounterClockWise(side.getAxis())
				: oldDir.getClockWise(side.getAxis());
	}

	private static <T extends Comparable<T>> BlockState iterateToNextValidPropertyValue(BlockState oldState, Property<T> property, Collection<T> orderedValues, T oldValue, Predicate<BlockState> canSurvive) {
		Iterator<T> it = orderedValues.iterator();
		while (it.hasNext() && !it.next().equals(oldValue)) {
			// look for current value
		}
		// now find next value that results in a valid block state in the given context
		while (it.hasNext()) {
			BlockState newState = oldState.setValue(property, it.next());
			if (canSurvive.test(newState)) {
				return newState;
			}
		}
		// failed to find valid state after the current state, look before
		it = orderedValues.iterator();
		while (it.hasNext()) {
			T newValue = it.next();
			if (newValue.equals(oldValue)) {
				// no valid values
				return oldState;
			}
			BlockState newState = oldState.setValue(property, newValue);
			if (canSurvive.test(newState)) {
				return newState;
			}
		}
		// nothing worked, leave it as is
		return oldState;
	}

	public static void doParticleBeamWithOffset(Level world, BlockPos orig, BlockPos end) {
		Vec3 origOffset = world.getBlockState(orig).getOffset(world, orig);
		Vec3 vorig = new Vec3(orig.getX() + origOffset.x() + 0.5, orig.getY() + origOffset.y() + 0.5, orig.getZ() + origOffset.z() + 0.5);
		Vec3 endOffset = world.getBlockState(end).getOffset(world, end);
		Vec3 vend = new Vec3(end.getX() + endOffset.x() + 0.5, end.getY() + endOffset.y() + 0.5, end.getZ() + endOffset.z() + 0.5);
		doParticleBeam(world, vorig, vend);
	}

	public static void doParticleBeam(Level world, Vec3 orig, Vec3 end) {
		if (!world.isClientSide) {
			return;
		}

		Vec3 diff = end.subtract(orig);
		Vec3 movement = diff.normalize().scale(0.05);
		int iters = (int) (diff.length() / movement.length());
		float huePer = 1F / iters;
		float hueSum = (float) Math.random();

		Vec3 currentPos = orig;
		for (int i = 0; i < iters; i++) {
			float hue = i * huePer + hueSum;
			int color = Mth.hsvToRgb(Mth.frac(hue), 1F, 1F);
			float r = (color >> 16 & 0xFF) / 255F;
			float g = (color >> 8 & 0xFF) / 255F;
			float b = (color & 0xFF) / 255F;

			SparkleParticleData data = SparkleParticleData.noClip(0.5F, r, g, b, 4);
			Proxy.INSTANCE.addParticleForceNear(world, data, currentPos.x, currentPos.y, currentPos.z, 0, 0, 0);
			currentPos = currentPos.add(movement);
		}
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		getBindingAttempt(stack).ifPresent(pos -> {
			if (!pos.dimension().equals(world.dimension())
					|| !(world.getBlockEntity(pos.pos()) instanceof WandBindable)) {
				setBindingAttempt(stack, null);
			}
		});
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (player.isSecondaryUseActive()) {
			if (!world.isClientSide) {
				setBindMode(stack, !getBindMode(stack));
			} else {
				player.playSound(BotaniaSounds.ding, 0.1F, 1F);
			}
		}

		return InteractionResultHolder.success(stack);
	}

	@Override
	public void addToCreativeTab(Item me, CreativeModeTab.Output output) {
		output.accept(me);
		List<Pair<DyeColor, DyeColor>> colorPairs = Arrays.asList(
				new Pair<>(DyeColor.WHITE, DyeColor.LIGHT_BLUE),
				new Pair<>(DyeColor.WHITE, DyeColor.PINK),
				new Pair<>(DyeColor.LIGHT_BLUE, DyeColor.PINK),
				new Pair<>(DyeColor.PURPLE, DyeColor.BLUE),
				new Pair<>(DyeColor.RED, DyeColor.RED),
				new Pair<>(DyeColor.BLUE, DyeColor.BLUE),
				new Pair<>(DyeColor.ORANGE, DyeColor.ORANGE),
				new Pair<>(DyeColor.BLACK, DyeColor.BLACK),
				new Pair<>(DyeColor.GRAY, DyeColor.LIGHT_GRAY),
				new Pair<>(DyeColor.PINK, DyeColor.PINK),
				new Pair<>(DyeColor.YELLOW, DyeColor.LIME),
				new Pair<>(DyeColor.WHITE, DyeColor.BLACK)
		);
		Collections.shuffle(colorPairs);
		for (int i = 0; i < 7; i++) {
			Pair<DyeColor, DyeColor> pair = colorPairs.get(i);
			if (Math.random() < 0.5) {
				pair = new Pair<>(pair.getSecond(), pair.getFirst());
			}
			output.accept(setColors(new ItemStack(me), pair.getFirst(), pair.getSecond()));
		}
	}

	@Override
	public Component getName(ItemStack stack) {
		Component mode = Component.literal(" (")
				.append(Component.translatable(getModeString(stack)).withStyle(modeChatFormatting))
				.append(")");
		return super.getName(stack).plainCopy().append(mode);
	}

	public static ItemStack setColors(ItemStack wand, DyeColor color1, DyeColor color2) {
		wand.set(BotaniaDataComponents.WAND_COLOR1, color1);
		wand.set(BotaniaDataComponents.WAND_COLOR2, color2);

		return wand;
	}

	public static DyeColor getColor1(ItemStack stack) {
		return stack.getOrDefault(BotaniaDataComponents.WAND_COLOR1, DyeColor.WHITE);
	}

	public static DyeColor getColor2(ItemStack stack) {
		return stack.getOrDefault(BotaniaDataComponents.WAND_COLOR2, DyeColor.WHITE);
	}

	public static void setBindingAttempt(ItemStack stack, @Nullable GlobalPos pos) {
		DataComponentHelper.setOptional(stack, BotaniaDataComponents.BINDING_POS, pos);
	}

	public static Optional<GlobalPos> getBindingAttempt(ItemStack stack) {
		return Optional.ofNullable(stack.get(BotaniaDataComponents.BINDING_POS));
	}

	public static boolean getBindMode(ItemStack stack) {
		return stack.has(BotaniaDataComponents.WAND_BIND_MODE);
	}

	public static void setBindMode(ItemStack stack, boolean bindMode) {
		DataComponentHelper.setFlag(stack, BotaniaDataComponents.WAND_BIND_MODE, bindMode);
	}

	public static String getModeString(ItemStack stack) {
		return "botaniamisc.wandMode." + (getBindMode(stack) ? "bind" : "function");
	}

	public static class CoordBoundItemImpl implements CoordBoundItem {
		private final ItemStack stack;

		public CoordBoundItemImpl(ItemStack stack) {
			this.stack = stack;
		}

		@Nullable
		@Override
		public BlockPos getBinding(Level world) {
			Optional<GlobalPos> bound = getBindingAttempt(stack);
			if (bound.isPresent() && bound.get().dimension().equals(world.dimension())) {
				return bound.get().pos();
			}

			var pos = ClientProxy.INSTANCE.getClientHit();
			if (pos instanceof BlockHitResult bHit && pos.getType() == HitResult.Type.BLOCK) {
				BlockEntity tile = world.getBlockEntity(bHit.getBlockPos());
				if (tile instanceof Bound boundTile) {
					return boundTile.getBinding();
				}
			}

			return null;
		}
	}

}
