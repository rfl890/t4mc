package cf.rfl890.t4mc.mixins;

import com.google.common.net.HostAndPort;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerAddress.class)
public abstract class ServerAddressMixin {
    @Inject(method = "parseString", at = @At("HEAD"), cancellable = true)
    private static void parseString(String string, CallbackInfoReturnable<ServerAddress> cir) {
        if (string.startsWith("iroh://")) {
            cir.setReturnValue(ServerAddress.parseString("127.0.0.1:0"));
            cir.cancel();
        }
    }
}
