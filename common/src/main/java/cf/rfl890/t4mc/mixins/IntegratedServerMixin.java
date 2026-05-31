package cf.rfl890.t4mc.mixins;

import cf.rfl890.iroh.IrohBridge;
import cf.rfl890.t4mc.interfaces.TicketAccessor;
import net.minecraft.client.server.IntegratedServer;

import net.minecraft.world.level.GameType;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.locks.ReentrantLock;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin implements TicketAccessor {
    @Shadow
    @Final
    private static Logger LOGGER;
    @Unique
    private final ReentrantLock t4mc$shutdownLock = new ReentrantLock();
    @Shadow
    private int publishedPort;
    @Unique
    private volatile long t4mc$shutdownToken = 0;
    @Unique
    private String t4mc$ticket = null;

    @Override
    public String t4mc$getTicket() {
        return this.t4mc$ticket;
    }

    @Inject(method = "publishServer", at = @At(value = "RETURN", target = "Lnet/minecraft/client/server/IntegratedServer;publishServer(Lnet/minecraft/world/level/GameType;ZI)Z", ordinal = 0))
    private void publishServer(GameType gameType, boolean bl, int i, CallbackInfoReturnable<Boolean> cir) {
        String ticket_grouped = IrohBridge.tcp2Iroh("127.0.0.1", (short) this.publishedPort);
        String[] ticket_components = ticket_grouped.split("_");
        this.t4mc$shutdownToken = Long.parseLong(ticket_components[0]);
        this.t4mc$ticket = ticket_components[1];
        LOGGER.info("Iroh bridge started, proxyPort={}, ticket={}, shutdownToken={}", i, this.t4mc$ticket, this.t4mc$shutdownToken);
    }

    @Inject(method = "stopServer", at = @At("TAIL"))
    private void stopServer(CallbackInfo ci) {
        t4mc$shutdownLock.lock();
        if (this.t4mc$shutdownToken != 0) {
            LOGGER.info("Shutting down Iroh SERVER thread, token={}", this.t4mc$shutdownToken);
            IrohBridge.shutdown(this.t4mc$shutdownToken);
            this.t4mc$shutdownToken = 0;
        }
        t4mc$shutdownLock.unlock();
    }
}

