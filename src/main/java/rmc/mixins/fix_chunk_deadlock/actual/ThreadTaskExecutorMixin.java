package rmc.mixins.fix_chunk_deadlock.actual;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.concurrent.ThreadTaskExecutor;
import rmc.mixins.fix_chunk_deadlock.extend.IThreadTaskExecutorEx;

/**
 * Developed by RMC Team, 2021
 */
@Mixin(value = ThreadTaskExecutor.class)
public abstract class ThreadTaskExecutorMixin
implements IThreadTaskExecutorEx {

    @Shadow
    private int drivers;

    @Override
    public void incDrivers() {
        ++this.drivers;
    }

    @Override
    public void decDrivers() {
        --this.drivers;
    }

}