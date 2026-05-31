package cf.rfl890.t4mc.mixins;

import cf.rfl890.iroh.IrohBridge;
import cf.rfl890.t4mc.interfaces.ShutdownTokenAccessor;
import net.minecraft.network.Connection;
import net.minecraft.network.DisconnectionDetails;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.concurrent.locks.ReentrantLock;

@Mixin(Connection.class)
public abstract class ConnectionMixin implements ShutdownTokenAccessor {
    @Shadow
    @Final
    private static Logger LOGGER;

    @Unique
    private long t4mc$shutdownToken = 0;

    @Unique
    private final ReentrantLock t4mc$shutdownLock = new ReentrantLock();

    @Override
    public void t4mc$setShutdownToken(long token) {
        this.t4mc$shutdownToken = token;
    }

    @Inject(method = "disconnect(Lnet/minecraft/network/DisconnectionDetails;)V", at = @At("TAIL"))
    private void disconnect(DisconnectionDetails disconnectionDetails, CallbackInfo ci) {
        t4mc$shutdownLock.lock();
        if (this.t4mc$shutdownToken != 0) {
            LOGGER.info("Shutting down Iroh CLIENT thread, token={}", this.t4mc$shutdownToken);
            IrohBridge.shutdown(this.t4mc$shutdownToken);
            this.t4mc$shutdownToken = 0;
        }
        t4mc$shutdownLock.unlock();
    }
}
