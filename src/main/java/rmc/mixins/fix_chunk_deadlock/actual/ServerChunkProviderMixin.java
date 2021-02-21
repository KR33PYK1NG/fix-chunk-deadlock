package rmc.mixins.fix_chunk_deadlock.actual;

import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerChunkProvider;
import rmc.mixins.fix_chunk_deadlock.ChunkDeadlockFix;

/**
 * Developed by RMC Team, 2021
 */
@Mixin(value = ServerChunkProvider.class)
public abstract class ServerChunkProviderMixin {

    @Inject(method = "Lnet/minecraft/world/server/ServerChunkProvider;getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/IChunk;",
            at = @At(value = "HEAD"))
    private void storeChunkInfo(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load, CallbackInfoReturnable<?> mixin) {
        ChunkDeadlockFix.chunkX = chunkX;
        ChunkDeadlockFix.chunkZ = chunkZ;
        ChunkDeadlockFix.load = load;
    }

    @ModifyVariable(method = "Lnet/minecraft/world/server/ServerChunkProvider;getChunk(IILnet/minecraft/world/chunk/ChunkStatus;Z)Lnet/minecraft/world/chunk/IChunk;",
                    at = @At(value = "INVOKE",
                             target = "Lorg/spigotmc/CustomTimingsHandler;stopTiming()V"))
    private CompletableFuture<?> regenBadChunk(CompletableFuture<?> original) {
        if (!original.isDone()) {
            original = ChunkHolder.MISSING_CHUNK_FUTURE;
            System.out.println(String.format("Chunk failed to load after 5 seconds! [%d, %d]",
                ChunkDeadlockFix.chunkX, ChunkDeadlockFix.chunkZ));
        }
        return original;
    }

}