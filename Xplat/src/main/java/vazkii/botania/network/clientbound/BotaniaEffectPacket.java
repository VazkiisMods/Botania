/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.network.clientbound;

import com.google.common.primitives.Ints;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import io.netty.buffer.ByteBuf;
import vazkii.botania.client.fx.SparkleParticleData;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.common.block.block_entity.TerrestrialAgglomerationPlateBlockEntity;
import vazkii.botania.common.entity.GaiaGuardianEntity;
import vazkii.botania.common.helper.ColorHelper;
import vazkii.botania.common.helper.VecHelper;
import vazkii.botania.common.item.GrassSeedsItem;
import vazkii.botania.common.item.WandOfTheForestItem;
import vazkii.botania.common.proxy.Proxy;
import vazkii.botania.network.BotaniaPacket;
import vazkii.botania.network.EffectType;

import java.util.ArrayList;

// Prefer using World.addBlockEvent/Block.eventReceived/TileEntity.receiveClientEvent where possible
// as those use less network bandwidth (~14 bytes), vs 26+ bytes here
public record BotaniaEffectPacket(EffectType effectType, double x, double y, double z, int... args) implements BotaniaPacket {

	public static final Type<BotaniaEffectPacket> ID = BotaniaPacket.createType("eff");
	private static final int MAX_VARIABLE_ARGS = 128;
	public static final StreamCodec<ByteBuf, BotaniaEffectPacket> STREAM_CODEC = StreamCodec.composite(
			EffectType.STREAM_CODEC, BotaniaEffectPacket::effectType,
			ByteBufCodecs.DOUBLE, BotaniaEffectPacket::x,
			ByteBufCodecs.DOUBLE, BotaniaEffectPacket::y,
			ByteBufCodecs.DOUBLE, BotaniaEffectPacket::z,
			ByteBufCodecs.collection(ArrayList::new, ByteBufCodecs.VAR_INT).map(Ints::toArray, is -> new ArrayList<>(Ints.asList((int[])is))), BotaniaEffectPacket::args,
			(type, x, y, z, args) -> new BotaniaEffectPacket(type, x, y, z, (int[])args)
	);

	@Override
	public Type<BotaniaEffectPacket> type() {
		return ID;
	}

	public static class Handler {
		public static void handle(BotaniaEffectPacket packet) {
			var type = packet.effectType();
			var x = packet.x();
			var y = packet.y();
			var z = packet.z();
			var args = packet.args();
			Minecraft mc = Minecraft.getInstance();
			// Lambda trips verifier on forge
			mc.execute(new Runnable() {
				@Override
				public void run() {
					Level world = mc.level;
					switch (type) {
						case PAINT_LENS -> {
							DyeColor placeColor = DyeColor.byId(args[0]);
							int hex = ColorHelper.getColorValue(placeColor);
							int r = (hex & 0xFF0000) >> 16;
							int g = (hex & 0xFF00) >> 8;
							int b = hex & 0xFF;
							for (int i = 0; i < 10; i++) {
								BlockPos pos = BlockPos.containing(x, y, z).relative(Direction.getRandom(world.random));
								SparkleParticleData data = SparkleParticleData.sparkle(0.6F + (float) Math.random() * 0.5F, r / 255F, g / 255F, b / 255F, 5);
								world.addParticle(data, pos.getX() + (float) Math.random(), pos.getY() + (float) Math.random(), pos.getZ() + (float) Math.random(), 0, 0, 0);
							}
						}
						case ARENA_INDICATOR -> {
							SparkleParticleData data = SparkleParticleData.sparkle(5F, 1, 0, 1, 120);
							for (int i = 0; i < 360; i += 8) {
								float rad = i * (float) Math.PI / 180F;
								double wx = x + 0.5 - Math.cos(rad) * GaiaGuardianEntity.ARENA_RANGE;
								double wy = y + 0.5;
								double wz = z + 0.5 - Math.sin(rad) * GaiaGuardianEntity.ARENA_RANGE;
								Proxy.INSTANCE.addParticleForceNear(world, data, wx, wy, wz, 0, 0, 0);
							}
						}
						case ITEM_SMOKE -> {
							Entity item = world.getEntity(args[0]);
							if (item == null) {
								return;
							}

							int p = args[1];

							for (int i = 0; i < p; i++) {
								double m = 0.01;
								double d0 = item.level().random.nextGaussian() * m;
								double d1 = item.level().random.nextGaussian() * m;
								double d2 = item.level().random.nextGaussian() * m;
								double d3 = 10.0D;
								item.level().addParticle(ParticleTypes.POOF,
										x + item.level().random.nextFloat() * item.getBbWidth() * 2.0F - item.getBbWidth() - d0 * d3, y + item.level().random.nextFloat() * item.getBbHeight() - d1 * d3,
										z + item.level().random.nextFloat() * item.getBbWidth() * 2.0F - item.getBbWidth() - d2 * d3, d0, d1, d2);
							}
						}
						case SPARK_NET_INDICATOR -> {
							Entity e1 = world.getEntity(args[0]);
							Entity e2 = world.getEntity(args[1]);

							if (e1 == null || e2 == null) {
								return;
							}

							Vec3 orig = new Vec3(e1.getX(), e1.getY() + 0.25, e1.getZ());
							Vec3 end = new Vec3(e2.getX(), e2.getY() + 0.25, e2.getZ());
							Vec3 diff = end.subtract(orig);
							Vec3 movement = diff.normalize().scale(0.1);
							int iters = (int) (diff.length() / movement.length());
							float huePer = 1F / iters;
							float hueSum = (float) Math.random();

							Vec3 currentPos = orig;
							for (int i = 0; i < iters; i++) {
								float hue = i * huePer + hueSum;
								int color = Mth.hsvToRgb(Mth.frac(hue), 1F, 1F);
								float r = Math.min(1F, (color >> 16 & 0xFF) / 255F + 0.4F);
								float g = Math.min(1F, (color >> 8 & 0xFF) / 255F + 0.4F);
								float b = Math.min(1F, (color & 0xFF) / 255F + 0.4F);

								SparkleParticleData data = SparkleParticleData.noClip(1, r, g, b, 12);
								world.addAlwaysVisibleParticle(data, true, currentPos.x, currentPos.y, currentPos.z, 0, 0, 0);
								currentPos = currentPos.add(movement);
							}

						}
						case SPARK_MANA_FLOW -> {
							Entity e1 = world.getEntity(args[0]);
							Entity e2 = world.getEntity(args[1]);

							if (e1 == null || e2 == null) {
								return;
							}

							double rc = 0.45;
							Vec3 thisVec = VecHelper.fromEntityCenter(e1).add((Math.random() - 0.5) * rc, (Math.random() - 0.5) * rc, (Math.random() - 0.5) * rc);
							Vec3 receiverVec = VecHelper.fromEntityCenter(e2).add((Math.random() - 0.5) * rc, (Math.random() - 0.5) * rc, (Math.random() - 0.5) * rc);

							Vec3 motion = receiverVec.subtract(thisVec).scale(0.04F);
							int color = args[2];
							float r = ((color >> 16) & 0xFF) / 255.0F;
							float g = ((color >> 8) & 0xFF) / 255.0F;
							float b = (color & 0xFF) / 255.0F;
							if (world.random.nextFloat() < 0.25) {
								r += 0.2F * (float) world.random.nextGaussian();
								g += 0.2F * (float) world.random.nextGaussian();
								b += 0.2F * (float) world.random.nextGaussian();
							}
							float size = 0.125F + 0.125F * (float) Math.random();

							WispParticleData data = WispParticleData.wisp(size, r, g, b).withNoClip(true);
							world.addAlwaysVisibleParticle(data, thisVec.x, thisVec.y, thisVec.z, motion.x, motion.y, motion.z);
						}
						case ENCHANTER_DESTROY -> {
							for (int i = 0; i < 50; i++) {
								float red = (float) Math.random();
								float green = (float) Math.random();
								float blue = (float) Math.random();
								WispParticleData data = WispParticleData.wisp((float) Math.random() * 0.15F + 0.15F, red, green, blue);
								world.addParticle(data, x, y, z, (float) (Math.random() - 0.5F) * 0.25F, (float) (Math.random() - 0.5F) * 0.25F, (float) (Math.random() - 0.5F) * 0.25F);
							}
						}
						case BLACK_LOTUS_DISSOLVE -> {
							for (int i = 0; i < 50; i++) {
								float r = (float) Math.random() * 0.35F;
								float g = 0F;
								float b = (float) Math.random() * 0.35F;
								float s = 0.45F * (float) Math.random() * 0.25F;

								float m = 0.045F;
								float mx = ((float) Math.random() - 0.5F) * m;
								float my = (float) Math.random() * m;
								float mz = ((float) Math.random() - 0.5F) * m;

								WispParticleData data = WispParticleData.wisp(s, r, g, b);
								world.addParticle(data, x, y, z, mx, my, mz);
							}

						}
						case TERRA_PLATE -> {
							BlockEntity te = world.getBlockEntity(BlockPos.containing(x, y, z));
							if (te instanceof TerrestrialAgglomerationPlateBlockEntity) {
								float percentage = Float.intBitsToFloat(args[0]);
								int ticks = (int) (100.0 * percentage);

								int totalSpiritCount = 3;
								double tickIncrement = 360D / totalSpiritCount;

								int speed = 5;
								double wticks = ticks * speed - tickIncrement;

								double r = Math.sin((ticks - 100) / 10D) * 2;
								double g = Math.sin(wticks * Math.PI / 180 * 0.55);

								for (int i = 0; i < totalSpiritCount; i++) {
									double wx = x + Math.sin(wticks * Math.PI / 180) * r + 0.5;
									double wy = y + 0.25 + Math.abs(r) * 0.7;
									double wz = z + Math.cos(wticks * Math.PI / 180) * r + 0.5;

									wticks += tickIncrement;
									float[] colorsfx = new float[] {
											0F, (float) ticks / (float) 100, 1F - (float) ticks / (float) 100
									};
									WispParticleData data = WispParticleData.wisp(0.85F, colorsfx[0], colorsfx[1], colorsfx[2], 0.25F);
									Proxy.INSTANCE.addParticleForceNear(world, data, wx, wy, wz, 0, (float) (-g * 0.05), 0);
									data = WispParticleData.wisp((float) Math.random() * 0.1F + 0.1F, colorsfx[0], colorsfx[1], colorsfx[2], 0.9F);
									world.addParticle(data, wx, wy, wz, (float) (Math.random() - 0.5) * 0.05F, (float) (Math.random() - 0.5) * 0.05F, (float) (Math.random() - 0.5) * 0.05F);

									if (ticks == 100) {
										for (int j = 0; j < 15; j++) {
											data = WispParticleData.wisp((float) Math.random() * 0.15F + 0.15F, colorsfx[0], colorsfx[1], colorsfx[2]);
											world.addParticle(data, x + 0.5, y + 0.5, z + 0.5, (float) (Math.random() - 0.5F) * 0.125F, (float) (Math.random() - 0.5F) * 0.125F, (float) (Math.random() - 0.5F) * 0.125F);
										}
									}
								}
							}
						}
						case FLUGEL_EFFECT -> {
							Entity entity = world.getEntity(args[0]);
							if (entity != null) {
								for (int i = 0; i < 15; i++) {
									float x1 = (float) (entity.getX() + Math.random());
									float y1 = (float) (entity.getY() + Math.random());
									float z1 = (float) (entity.getZ() + Math.random());
									WispParticleData data = WispParticleData.wisp((float) Math.random(), (float) Math.random(), (float) Math.random(), (float) Math.random(), 1);
									world.addParticle(data, x1, y1, z1, 0, -(-0.3F + (float) Math.random() * 0.2F), 0);
								}
							}
						}
						case PARTICLE_BEAM -> {
							WandOfTheForestItem.doParticleBeam(world,
									new Vec3(x, y, z),
									new Vec3(args[0] + 0.5, args[1] + 0.5, args[2] + 0.5));
						}
						case DIVA_EFFECT -> {
							Entity target = world.getEntity(args[0]);
							if (target == null) {
								break;
							}

							double x1 = target.getX();
							double y1 = target.getY();
							double z1 = target.getZ();

							SparkleParticleData data = SparkleParticleData.sparkle(1F, 1F, 1F, 0.25F, 3);
							for (int i = 0; i < 50; i++) {
								world.addParticle(data, x1 + Math.random() * target.getBbWidth(), y1 + Math.random() * target.getBbHeight(), z1 + Math.random() * target.getBbWidth(), 0, 0, 0);
							}
						}
						case HALO_CRAFT -> {
							Entity target = world.getEntity(args[0]);
							if (target != null) {
								Vec3 lookVec3 = target.getLookAngle();
								Vec3 centerVector = VecHelper.fromEntityCenter(target).add(lookVec3.x * 3, 1.3, lookVec3.z * 3);
								float m = 0.1F;
								for (int i = 0; i < 4; i++) {
									WispParticleData data = WispParticleData.wisp(0.2F + 0.2F * (float) Math.random(), 1F, 0F, 1F);
									target.level().addParticle(data, centerVector.x, centerVector.y, centerVector.z, ((float) Math.random() - 0.5F) * m, ((float) Math.random() - 0.5F) * m, ((float) Math.random() - 0.5F) * m);
								}
							}

						}
						case AVATAR_TORNADO_JUMP -> {
							Entity p = world.getEntity(args[0]);
							if (p != null) {
								for (int i = 0; i < 20; i++) {
									for (int j = 0; j < 5; j++) {
										WispParticleData data = WispParticleData.wisp(0.35F + (float) Math.random() * 0.1F, 0.25F, 0.25F, 0.25F);
										world.addParticle(data, p.getX(),
												p.getY() + i, p.getZ(),
												0.2F * (float) (Math.random() - 0.5),
												-0.01F * (float) Math.random(),
												0.2F * (float) (Math.random() - 0.5));
									}
								}
							}
						}
						case AVATAR_TORNADO_BOOST -> {
							Entity p = world.getEntity(args[0]);
							if (p != null) {
								Vec3 lookDir = p.getLookAngle();
								for (int i = 0; i < 20; i++) {
									for (int j = 0; j < 5; j++) {
										WispParticleData data = WispParticleData.wisp(0.35F + (float) Math.random() * 0.1F, 0.25F, 0.25F, 0.25F);
										world.addParticle(data, p.getX() + lookDir.x() * i,
												p.getY() + lookDir.y() * i,
												p.getZ() + lookDir.z() * i,
												0.2F * (float) (Math.random() - 0.5) * (Math.abs(lookDir.y()) + Math.abs(lookDir.z())) + -0.01F * (float) Math.random() * lookDir.x(),
												0.2F * (float) (Math.random() - 0.5) * (Math.abs(lookDir.x()) + Math.abs(lookDir.z())) + -0.01F * (float) Math.random() * lookDir.y(),
												0.2F * (float) (Math.random() - 0.5) * (Math.abs(lookDir.y()) + Math.abs(lookDir.x())) + -0.01F * (float) Math.random() * lookDir.z());
									}
								}
							}
						}
						case THUNDERCALLER_EFFECT -> {
							Vec3 source = new Vec3(x, y, z);
							for (int id : args) {
								var entity = world.getEntity(id);
								if (entity == null) {
									break;
								}
								var entityPos = VecHelper.fromEntityCenter(entity);
								Proxy.INSTANCE.lightningFX(world, source, entityPos, 1, 0x0179C4, 0xAADFFF);
								source = entityPos;
							}
						}
						case GRASS_SEED_PARTICLES -> {
							int color = args[0];
							GrassSeedsItem.spawnParticles(world, BlockPos.containing(x, y, z),
									GrassSeedsItem.extractR(color), GrassSeedsItem.extractG(color), GrassSeedsItem.extractB(color));
						}
					}
				}
			});
		}
	}

}
