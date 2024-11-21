/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.client.fx;
/*
 * This class is derived from the Mekanism project.
 * See ALTERNATE_LICENSES.txt.
 * Imported October 2021 and heavily modified.
 */

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.apache.commons.lang3.tuple.Pair;
import org.joml.Matrix4f;

import vazkii.botania.client.core.helper.RenderHelper;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

// todo(williewillus) integrate this properly into the particle system
public class BoltRenderer {

	public static final BoltRenderer INSTANCE = new BoltRenderer();
	/** Amount of times per tick we refresh. 3 implies 60 Hz. */
	private static final float REFRESH_TIME = 3F;
	/** After there are no more bolts in an emitter, how much longer in ticks it should stay alive. */
	private static final double LIFETIME_AFTER_LAST_BOLT = 100;

	private Timestamp refreshTimestamp = Timestamp.ZERO;

	private final Random random = new Random();

	private final List<BoltEmitter> boltEmitters = new LinkedList<>();

	public static void onWorldRenderLast(Camera camera, float partialTicks, PoseStack ps, RenderBuffers buffers) {
		ps.pushPose();
		// here we translate based on the inverse position of the client viewing camera to get back to 0, 0, 0
		Vec3 camVec = camera.getPosition();
		ps.translate(-camVec.x, -camVec.y, -camVec.z);
		var bufferSource = buffers.bufferSource();
		BoltRenderer.INSTANCE.render(partialTicks, ps, bufferSource);
		bufferSource.endBatch(RenderHelper.LIGHTNING);
		ps.popPose();
	}

	public void render(float partialTicks, PoseStack matrixStack, MultiBufferSource buffers) {
		VertexConsumer buffer = buffers.getBuffer(RenderHelper.LIGHTNING);
		Matrix4f matrix = matrixStack.last().pose();
		Timestamp timestamp = new Timestamp(Minecraft.getInstance().level.getGameTime(), partialTicks);
		boolean refresh = timestamp.isPassed(refreshTimestamp, (1 / REFRESH_TIME));
		if (refresh) {
			refreshTimestamp = timestamp;
		}

		for (Iterator<BoltEmitter> iter = boltEmitters.iterator(); iter.hasNext();) {
			BoltEmitter emitter = iter.next();
			emitter.renderTick(timestamp, refresh, matrix, buffer);
			if (emitter.shouldRemove(timestamp)) {
				iter.remove();
			}
		}
	}

	public void add(Level level, BoltParticleOptions options, float partialTicks) {
		if (!level.isClientSide) {
			return;
		}
		var emitter = new BoltEmitter(options);
		Timestamp timestamp = new Timestamp(level.getGameTime(), partialTicks);
		if ((!emitter.options.getSpawnFunction().isConsecutive() || emitter.bolts.isEmpty()) && timestamp.isPassed(emitter.lastBoltTimestamp, emitter.lastBoltDelay)) {
			emitter.addBolt(new BoltInstance(options, timestamp), timestamp);
		}
		emitter.lastUpdateTimestamp = timestamp;
		boltEmitters.add(emitter);
	}

	public class BoltEmitter {

		private final Set<BoltInstance> bolts = new ObjectOpenHashSet<>();
		private final BoltParticleOptions options;
		private Timestamp lastBoltTimestamp = Timestamp.ZERO;
		private Timestamp lastUpdateTimestamp = Timestamp.ZERO;
		private double lastBoltDelay;

		public BoltEmitter(BoltParticleOptions options) {
			this.options = options;
		}

		private void addBolt(BoltInstance instance, Timestamp timestamp) {
			bolts.add(instance);
			lastBoltDelay = instance.options.getSpawnFunction().getSpawnDelay(random);
			lastBoltTimestamp = timestamp;
		}

		public void renderTick(Timestamp timestamp, boolean refresh, Matrix4f matrix, VertexConsumer buffer) {
			// tick our bolts based on the refresh rate, removing if they're now finished
			if (refresh) {
				bolts.removeIf(bolt -> bolt.tick(timestamp));
			}
			if (bolts.isEmpty() && options != null && options.getSpawnFunction().isConsecutive()) {
				addBolt(new BoltInstance(options, timestamp), timestamp);
			}
			bolts.forEach(bolt -> bolt.render(matrix, buffer, timestamp));
		}

		public boolean shouldRemove(Timestamp timestamp) {
			return bolts.isEmpty() && timestamp.isPassed(lastUpdateTimestamp, LIFETIME_AFTER_LAST_BOLT);
		}
	}

	private static class BoltInstance {

		private final BoltParticleOptions options;
		private final List<BoltParticleOptions.BoltQuads> renderQuads;
		private final Timestamp createdTimestamp;

		public BoltInstance(BoltParticleOptions options, Timestamp timestamp) {
			this.options = options;
			this.renderQuads = options.generate();
			this.createdTimestamp = timestamp;
		}

		public void render(Matrix4f matrix, VertexConsumer buffer, Timestamp timestamp) {
			float lifeScale = timestamp.subtract(createdTimestamp).value() / options.getLifespan();
			Pair<Integer, Integer> bounds = options.getFadeFunction().getRenderBounds(renderQuads.size(), lifeScale);
			for (int i = bounds.getLeft(); i < bounds.getRight(); i++) {
				renderQuads.get(i).getVecs().forEach(v -> buffer.addVertex(matrix, (float) v.x, (float) v.y, (float) v.z)
						.setColor(options.getColor().x(), options.getColor().y(), options.getColor().z(), options.getColor().w()));
			}
		}

		public boolean tick(Timestamp timestamp) {
			return timestamp.isPassed(createdTimestamp, options.getLifespan());
		}
	}

	private static class Timestamp {

		public static final Timestamp ZERO = new Timestamp(0, 0);
		private final long ticks;
		private final float partial;

		public Timestamp(long ticks, float partial) {
			this.ticks = ticks;
			this.partial = partial;
		}

		public Timestamp subtract(Timestamp other) {
			long newTicks = ticks - other.ticks;
			float newPartial = partial - other.partial;
			if (newPartial < 0) {
				newPartial += 1;
				newTicks -= 1;
			}
			return new Timestamp(newTicks, newPartial);
		}

		public float value() {
			return ticks + partial;
		}

		public boolean isPassed(Timestamp prev, double duration) {
			long ticksPassed = ticks - prev.ticks;
			if (ticksPassed > duration) {
				return true;
			}
			duration -= ticksPassed;
			if (duration >= 1) {
				return false;
			}
			return (partial - prev.partial) >= duration;
		}
	}
}
