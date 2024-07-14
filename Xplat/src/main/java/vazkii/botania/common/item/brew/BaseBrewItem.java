/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.item.brew;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.brew.Brew;
import vazkii.botania.api.brew.BrewItem;
import vazkii.botania.common.brew.BotaniaBrews;
import vazkii.botania.common.helper.ItemNBTHelper;
import vazkii.botania.common.item.CustomCreativeTabContents;

import java.util.List;
import java.util.function.Supplier;

import static vazkii.botania.common.lib.ResourceLocationHelper.prefix;

public class BaseBrewItem extends Item implements BrewItem, CustomCreativeTabContents {

	private static final String TAG_BREW_KEY = "brewKey";
	private static final String TAG_SWIGS_LEFT = "swigsLeft";

	private final int swigs;
	private final int drinkSpeed;
	private final Supplier<Item> baseItem;

	public BaseBrewItem(Properties builder, int swigs, int drinkSpeed, Supplier<Item> baseItem) {
		super(builder);
		this.swigs = swigs;
		this.drinkSpeed = drinkSpeed;
		this.baseItem = baseItem;
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return drinkSpeed;
	}

	@NotNull
	@Override
	public UseAnim getUseAnimation(ItemStack stack) {
		return UseAnim.DRINK;
	}

	@NotNull
	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, @NotNull InteractionHand hand) {
		return ItemUtils.startUsingInstantly(world, player, hand);
	}

	@NotNull
	@Override
	public ItemStack finishUsingItem(@NotNull ItemStack stack, Level world, LivingEntity living) {
		if (!world.isClientSide) {
			for (MobEffectInstance effect : getBrew(stack).getPotionEffects(stack)) {
				MobEffectInstance newEffect = new MobEffectInstance(effect.getEffect(), effect.getDuration(), effect.getAmplifier(), true, true);
				if (effect.getEffect().isInstantenous()) {
					effect.getEffect().applyInstantenousEffect(living, living, living, newEffect.getAmplifier(), 1F);
				} else {
					living.addEffect(newEffect);
				}
			}

			if (world.random.nextBoolean()) {
				world.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 1F, 1F);
			}

			int swigs = getSwigsLeft(stack);
			if (living instanceof Player player && !player.getAbilities().instabuild) {
				if (swigs == 1) {
					ItemStack result = getBaseStack();
					if (!player.getInventory().add(result)) {
						return result;
					} else {
						return ItemStack.EMPTY;
					}
				}

				setSwigsLeft(stack, swigs - 1);
			}
		}

		return stack;
	}

	@Override
	public void addToCreativeTab(Item me, CreativeModeTab.Output output) {
		for (Brew brew : BotaniaAPI.instance().getBrewRegistry()) {
			if (brew == BotaniaBrews.fallbackBrew) {
				continue;
			}
			ItemStack stack = new ItemStack(this);
			setBrew(stack, brew);
			output.accept(stack);

		}
	}

	@NotNull
	@Override
	public Component getName(@NotNull ItemStack stack) {
		return Component.translatable(getDescriptionId(), Component.translatable(getBrew(stack).getTranslationKey(stack)),
				Component.literal(Integer.toString(getSwigsLeft(stack))).withStyle(ChatFormatting.BOLD));
	}

	@Override
	public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag flags) {
		PotionUtils.addPotionTooltip(getBrew(stack).getPotionEffects(stack), list, 1, world == null ? 20 : world.tickRateManager().tickrate());
	}

	@Override
	public Brew getBrew(ItemStack stack) {
		String key = ItemNBTHelper.getString(stack, TAG_BREW_KEY, "");
		return BotaniaAPI.instance().getBrewRegistry().get(ResourceLocation.tryParse(key));
	}

	public static void setBrew(ItemStack stack, @Nullable Brew brew) {
		ResourceLocation id;
		if (brew != null) {
			id = BotaniaAPI.instance().getBrewRegistry().getKey(brew);
		} else {
			id = prefix("fallback");
		}
		setBrew(stack, id);
	}

	public static void setBrew(ItemStack stack, ResourceLocation brew) {
		ItemNBTHelper.setString(stack, TAG_BREW_KEY, brew.toString());
	}

	@NotNull
	public static String getSubtype(ItemStack stack) {
		return stack.hasTag() ? ItemNBTHelper.getString(stack, TAG_BREW_KEY, "none") : "none";
	}

	public int getSwigs() {
		return swigs;
	}

	public int getSwigsLeft(ItemStack stack) {
		return ItemNBTHelper.getInt(stack, TAG_SWIGS_LEFT, swigs);
	}

	public void setSwigsLeft(ItemStack stack, int swigs) {
		ItemNBTHelper.setInt(stack, TAG_SWIGS_LEFT, swigs);
	}

	public ItemStack getBaseStack() {
		return new ItemStack(baseItem.get());
	}
}
