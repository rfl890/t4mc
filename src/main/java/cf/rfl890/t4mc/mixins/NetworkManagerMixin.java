package cf.rfl890.t4mc.mixins;

import cf.rfl890.iroh.IrohBridge;
import cf.rfl890.t4mc.interfaces.NetworkManagerAccess;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.locks.ReentrantLock;

@Mixin(NetworkManager.class)
public abstract class NetworkManagerMixin implements NetworkManagerAccess {
    @Final @Shadow private static Logger LOGGER;
    @Unique private long t4mc$shutdownToken = 0;
    @Unique private ReentrantLock t4mc$shutdownLock = new ReentrantLock();

    @Override
    public void t4mc$setShutdownToken(long shutdownToken) {
        t4mc$shutdownToken = shutdownToken;
    }

    @Inject(method="handleDisconnection", at=@At("TAIL"))
    private void handleDisconnection(CallbackInfo ci) {
        this.t4mc$shutdownLock.lock();
        if (this.t4mc$shutdownToken != 0) {
            LOGGER.info("Shutting down Iroh CLIENT thread, token={}", this.t4mc$shutdownToken);
            IrohBridge.shutdown(this.t4mc$shutdownToken);
            this.t4mc$shutdownToken = 0;
        }
        this.t4mc$shutdownLock.unlock();
    }

    @Inject(method="closeChannel", at=@At("TAIL"))
    private void closeChannel(ITextComponent message, CallbackInfo ci) {
        this.t4mc$shutdownLock.lock();
        if (this.t4mc$shutdownToken != 0) {
            LOGGER.info("Shutting down Iroh CLIENT thread, token={}", this.t4mc$shutdownToken);
            IrohBridge.shutdown(this.t4mc$shutdownToken);
            this.t4mc$shutdownToken = 0;
        }
        this.t4mc$shutdownLock.unlock();
    }

}
