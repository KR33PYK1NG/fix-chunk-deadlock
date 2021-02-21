package rmc.mixins.fix_chunk_deadlock.extend;

/**
 * Developed by RMC Team, 2021
 */
public interface IThreadTaskExecutorEx {

    public void incDrivers();
    public void decDrivers();

}