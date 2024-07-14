package vazkii.botania.test.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import vazkii.botania.common.block.BotaniaFlowerBlocks;
import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.item.BotaniaItems;
import vazkii.botania.mixin.MushroomCowAccessor;
import vazkii.botania.test.TestingUtil;

import java.util.List;

public class DrumBlockTest {
	private static final String TEMPLATE = "botania:block/drum_gathering";

	private static final BlockPos POSITION_BUTTON = new BlockPos(10, 10, 9);
	private static final BlockPos POSITION_SPREADER = new BlockPos(10, 10, 10);
	private static final BlockPos POSITION_DRUM = new BlockPos(10, 11, 10);
	private static final BlockPos POSITION_BERGAMUTE = new BlockPos(11, 2, 11);
	private static final BlockPos POSITION_MOB = new BlockPos(10, 2, 10);
	private static final Vec3 VECTOR_MOB = POSITION_MOB.getCenter();

	private static <T extends Mob> T setup(GameTestHelper helper, EntityType<T> entityType, @Nullable Item item) {
		var player = helper.makeMockPlayer();
		var spreader = TestingUtil.assertBlockEntity(helper, POSITION_SPREADER, BotaniaBlockEntities.SPREADER);
		TestingUtil.assertThat(spreader.bindTo(player, new ItemStack(BotaniaItems.twigWand),
				helper.absolutePos(POSITION_DRUM), Direction.UP),
				() -> "Failed to bind spreader");
		if (item != null) {
			helper.spawnItem(item, (float) VECTOR_MOB.x(), (float) VECTOR_MOB.y(), (float) VECTOR_MOB.z());
		}
		return helper.spawn(entityType, VECTOR_MOB);
	}

	private static <T extends Mob> void testMilkingAdultAnimal(GameTestHelper helper, EntityType<T> entityType, Item inputItem, Item outputItem) {
		setup(helper, entityType, inputItem);
		helper.startSequence()
				.thenExecute(() -> helper.pressButton(POSITION_BUTTON))
				.thenWaitUntil(() -> helper.assertItemEntityPresent(outputItem, POSITION_MOB, 1.0))
				.thenSucceed();
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 20)
	public void testMilkingGoat(GameTestHelper helper) {
		testMilkingAdultAnimal(helper, EntityType.GOAT, Items.BUCKET, Items.MILK_BUCKET);
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 20)
	public void testMilkingCow(GameTestHelper helper) {
		testMilkingAdultAnimal(helper, EntityType.COW, Items.BUCKET, Items.MILK_BUCKET);
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 20)
	public void testMilkingMooshroom(GameTestHelper helper) {
		testMilkingAdultAnimal(helper, EntityType.MOOSHROOM, Items.BUCKET, Items.MILK_BUCKET);
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 20)
	public void testMilkingMooshroomSoup(GameTestHelper helper) {
		testMilkingAdultAnimal(helper, EntityType.MOOSHROOM, Items.BOWL, Items.MUSHROOM_STEW);
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 20)
	public void testMilkingBrownMooshroomSuspiciousStew(GameTestHelper helper) {
		var cow = setup(helper, EntityType.MOOSHROOM, Items.BOWL);
		cow.setVariant(MushroomCow.MushroomType.BROWN);
		var cowAccessor = (MushroomCowAccessor) cow;
		cowAccessor.setStewEffects(List.of(new SuspiciousEffectHolder.EffectEntry(MobEffects.BLINDNESS, 15)));
		helper.startSequence()
				.thenExecute(() -> helper.pressButton(POSITION_BUTTON))
				.thenWaitUntil(() -> helper.assertItemEntityPresent(Items.SUSPICIOUS_STEW, POSITION_MOB, 1.0))
				.thenExecute(() -> {
					final var item = helper.getEntities(EntityType.ITEM, POSITION_MOB, 1.0).stream().findFirst();
					helper.assertTrue(item.isPresent() && item.get().getItem().is(Items.SUSPICIOUS_STEW), "Item not found or not suspicious stew");
					final var nbt = item.orElseThrow().getItem().getTag();
					helper.assertTrue(nbt != null && nbt.contains(SuspiciousStewItem.EFFECTS_TAG, Tag.TAG_LIST), "Missing effects list tag");
					final var effects = nbt.getList(SuspiciousStewItem.EFFECTS_TAG, Tag.TAG_COMPOUND);
					final var parsedEffects = SuspiciousEffectHolder.EffectEntry.LIST_CODEC.parse(NbtOps.INSTANCE, effects).result();
					helper.assertTrue(parsedEffects.isPresent() && parsedEffects.get().size() == 1, "Exactly one effect expected");
					final var effectEntry = parsedEffects.orElseThrow().get(0);
					helper.assertTrue(effectEntry.effect() == MobEffects.BLINDNESS && effectEntry.duration() == 15, "Unexpected effect type or duration");
				})
				.thenSucceed();
	}

	private static <T extends Mob> void testMilkingBabyAnimal(GameTestHelper helper, EntityType<T> entityType, Item item) {
		var baby = setup(helper, entityType, item);
		baby.setBaby(true);
		helper.startSequence()
				.thenExecute(() -> helper.pressButton(POSITION_BUTTON))
				// ensure the empty container item is still there:
				.thenExecuteAfter(20, () -> helper.assertItemEntityPresent(item, POSITION_MOB, 1.0))
				.thenSucceed();
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 25)
	public void testMilkingBabyGoat(GameTestHelper helper) {
		testMilkingBabyAnimal(helper, EntityType.GOAT, Items.BUCKET);
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 25)
	public void testMilkingBabyMooshroom(GameTestHelper helper) {
		testMilkingBabyAnimal(helper, EntityType.MOOSHROOM, Items.BOWL);
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 25)
	public void testMilkingDeadAnimal(GameTestHelper helper) {
		var cow = setup(helper, EntityType.COW, Items.BUCKET);
		helper.startSequence()
				// mobs play 20 ticks of dying animation when killed, so the cow is still there when the drum goes off
				.thenExecute(cow::kill)
				.thenExecute(() -> helper.pressButton(POSITION_BUTTON))
				.thenExecuteAfter(20, () -> helper.assertItemEntityPresent(Items.BUCKET, POSITION_MOB, 1.0))
				.thenSucceed();
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 25)
	public void testMilkingNearBergamute(GameTestHelper helper) {
		setup(helper, EntityType.COW, Items.BUCKET);
		// Bergamute should protect from drum interactions
		helper.setBlock(POSITION_BERGAMUTE, BotaniaFlowerBlocks.bergamuteFloating);
		helper.startSequence()
				.thenExecute(() -> helper.pressButton(POSITION_BUTTON))
				.thenExecuteAfter(20, () -> helper.assertItemEntityPresent(Items.BUCKET, POSITION_MOB, 1.0))
				.thenSucceed();
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 20)
	public void testShearingSheep(GameTestHelper helper) {
		var sheep = setup(helper, EntityType.SHEEP, null);
		sheep.setColor(DyeColor.LIME);
		helper.startSequence()
				.thenExecute(() -> helper.pressButton(POSITION_BUTTON))
				.thenWaitUntil(() -> helper.assertItemEntityPresent(Items.LIME_WOOL, POSITION_MOB, 1.0))
				.thenExecute(() -> helper.assertTrue(sheep.isAlive() && sheep.isSheared(), "Sheep should be sheared"))
				.thenSucceed();
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 25)
	public void testShearingBabySheep(GameTestHelper helper) {
		var sheep = setup(helper, EntityType.SHEEP, null);
		sheep.setBaby(true);
		helper.startSequence()
				.thenExecute(() -> helper.pressButton(POSITION_BUTTON))
				.thenExecuteAfter(20, () -> helper.assertTrue(sheep.isAlive() && !sheep.isSheared(), "Baby sheep should not be sheared"))
				.thenSucceed();
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 20)
	public void testShearingSnowGolem(GameTestHelper helper) {
		var golem = setup(helper, EntityType.SNOW_GOLEM, null);
		helper.startSequence()
				.thenExecute(() -> helper.pressButton(POSITION_BUTTON))
				.thenWaitUntil(() -> helper.assertItemEntityPresent(Items.CARVED_PUMPKIN, POSITION_MOB, 1.0))
				.thenExecute(() -> helper.assertTrue(golem.isAlive() && !golem.hasPumpkin(), "Snow golem should not wear a pumpkin"))
				.thenSucceed();
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 20)
	public void testShearingPumpkinlessSnowGolem(GameTestHelper helper) {
		var golem = setup(helper, EntityType.SNOW_GOLEM, null);
		golem.setPumpkin(false);
		helper.startSequence()
				.thenExecute(() -> helper.pressButton(POSITION_BUTTON))
				.thenExecuteAfter(20, () -> helper.assertTrue(golem.isAlive() && !golem.hasPumpkin(), "Snow golem should not wear a pumpkin"))
				.thenExecute(() -> helper.assertItemEntityNotPresent(Items.CARVED_PUMPKIN, POSITION_MOB, 1.0))
				.thenSucceed();
	}

	@GameTest(template = TEMPLATE, timeoutTicks = 25)
	public void testShearingMooshroom(GameTestHelper helper) {
		setup(helper, EntityType.MOOSHROOM, null);
		helper.startSequence()
				.thenExecute(() -> helper.pressButton(POSITION_BUTTON))
				.thenExecuteAfter(20, () -> helper.assertEntityNotPresent(EntityType.COW))
				.thenSucceed();
	}
}
