package cf.rfl890.t4mc.mixins;

import cf.rfl890.t4mc.overrides.GuiConnectingThread;
import net.minecraft.client.multiplayer.GuiConnecting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.util.concurrent.atomic.AtomicInteger;

@Mixin(GuiConnecting.class)
public abstract class GuiConnectingMixin {
    @Shadow @Final private static AtomicInteger CONNECTION_ID;

    @Inject(method = "connect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/GuiConnecting$1;<init>(Lnet/minecraft/client/multiplayer/GuiConnecting;Ljava/lang/String;Ljava/lang/String;I)V"), cancellable = true)
    private void onConnect(String ip, int port, CallbackInfo ci) {
        new GuiConnectingThread(((GuiConnecting) (Object) this), "Server Connector #" + CONNECTION_ID.incrementAndGet(), ip, port).start();
        ci.cancel();
    }
}