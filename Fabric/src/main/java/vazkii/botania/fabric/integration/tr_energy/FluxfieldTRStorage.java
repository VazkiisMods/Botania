package vazkii.botania.fabric.integration.tr_energy;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import vazkii.botania.common.block.block_entity.BotaniaBlockEntities;
import vazkii.botania.common.block.block_entity.mana.PowerGeneratorBlockEntity;

import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class FluxfieldTRStorage extends SimpleEnergyStorage {
	public static void register() {
		EnergyStorage.SIDED.registerForBlockEntity((be, dir) -> new FluxfieldTRStorage(be), BotaniaBlockEntities.FLUXFIELD);
	}

	public static int transferEnergyToNeighbors(Level level, BlockPos pos, int energy) {
		for (Direction e : Direction.values()) {
			BlockPos neighbor = pos.relative(e);
			if (!level.hasChunkAt(neighbor)) {
				continue;
			}

			BlockEntity be = level.getBlockEntity(neighbor);

			EnergyStorage storage = EnergyStorage.SIDED.find(level, neighbor, level.getBlockState(neighbor), be, e.getOpposite());
			if (storage != null && storage.supportsInsertion()) {
				try (Transaction transaction = Transaction.openOuter()) {
					energy -= storage.insert(energy, transaction);
					transaction.commit();
					if (energy <= 0) {
						return 0;
					}
				}
			}
		}
		return energy;
	}

	private final PowerGeneratorBlockEntity generator;

	public FluxfieldTRStorage(PowerGeneratorBlockEntity generator) {
		super(PowerGeneratorBlockEntity.MAX_ENERGY, 0, 0);
		this.generator = generator;
	}

	@Override
	public long getAmount() {
		return generator.getEnergy();
	}
}
