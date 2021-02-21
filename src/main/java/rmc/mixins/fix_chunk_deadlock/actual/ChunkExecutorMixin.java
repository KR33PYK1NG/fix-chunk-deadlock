package rmc.mixins.fix_chunk_deadlock.actual;

import java.util.function.BooleanSupplier;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.util.concurrent.ThreadTaskExecutor;
import rmc.mixins.fix_chunk_deadlock.ChunkDeadlockFix;
import rmc.mixins.fix_chunk_deadlock.extend.IThreadTaskExecutorEx;

/**
 * Developed by RMC Team, 2021
 */
@Mixin(targets = "net.minecraft.world.server.ServerChunkProvider$ChunkExecutor")
public abstract class ChunkExecutorMixin
extends ThreadTaskExecutor<Runnable> {

    @Override
    public void driveUntil(BooleanSupplier isDone) {
        ((IThreadTaskExecutorEx) this).incDrivers();
        try {
            long total = 0;
            while (!isDone.getAsBoolean()) {
                long start = System.currentTimeMillis();
                if (!this.driveOne()) {
                    this.threadYieldPark();
                }
                long end = System.currentTimeMillis();
                total += end - start;
                if (total > 5000 && !ChunkDeadlockFix.load) {
                    break;
                }
            }
        } finally {
            ((IThreadTaskExecutorEx) this).decDrivers();
        }
    }

    private ChunkExecutorMixin(String dummy) {
        super (dummy);
    }

}