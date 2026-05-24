package cf.rfl890.t4mc.mixins;

import cf.rfl890.iroh.IrohBridge;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.GameType;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(IntegratedServer.class)
public abstract class IntegratedServerMixin {
    @Final @Shadow private static Logger LOGGER;
    @Unique private long t4mc$shutdownToken = 0;
    @Unique private String t4mc$ticket;

    @Inject(method = "shareToLAN", at = @At(value = "FIELD", target = "Lnet/minecraft/server/integrated/IntegratedServer;isPublic:Z", opcode = Opcodes.PUTFIELD), locals = LocalCapture.CAPTURE_FAILHARD)
    private void shareToLAN(GameType type, boolean allowCheats, CallbackInfoReturnable<String> cir, int i) {
        String ticket_grouped = IrohBridge.tcp2Iroh("127.0.0.1", (short)i);
        String[] ticket_components = ticket_grouped.split("_");
        this.t4mc$ticket = ticket_components[1];
        this.t4mc$shutdownToken = Long.parseLong(ticket_components[0]);
        LOGGER.info("Iroh bridge started, proxyPort={}, ticket={}, shutdownToken={}", i, this.t4mc$ticket, this.t4mc$shutdownToken);
    }

    @Inject(method = "shareToLAN", at = @At(value = "RETURN"), cancellable = true)
    private void shareToLAN_return(GameType type, boolean allowCheats, CallbackInfoReturnable<String> cir) {
        if (cir.getReturnValue() != null) {
            cir.setReturnValue(this.t4mc$ticket);
        }
    }

    @Inject(method="stopServer", at = @At("TAIL"))
    private void stopServer(CallbackInfo ci)
    {
        if (this.t4mc$shutdownToken != 0) {
            LOGGER.info("Shutting down Iroh SERVER thread, token={}", this.t4mc$shutdownToken);
            IrohBridge.shutdown(this.t4mc$shutdownToken);
            this.t4mc$shutdownToken = 0;
        }
    }
}
