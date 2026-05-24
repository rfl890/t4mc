package cf.rfl890.t4mc.mixins;

import net.minecraft.client.multiplayer.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;


@Mixin(ServerAddress.class)
public abstract class ServerAddressMixin {
    @Unique private static final ThreadLocal<String> t4mc$addressOverride = new ThreadLocal<>();

    @Inject(method = "fromString", at = @At(value = "HEAD"))
    private static void beforeFromString(String addrString, CallbackInfoReturnable<ServerAddress> cir) {
        if (addrString != null) {
            if (addrString.startsWith("iroh://")) {
                t4mc$addressOverride.set(addrString);
            } else {
                t4mc$addressOverride.remove();
            }
        }
    }

    @ModifyArgs(method = "fromString", at = @At(value = "INVOKE", target = "net.minecraft.client.multiplayer.ServerAddress.<init>(Ljava/lang/String;I)V"))
    private static void fromString(Args args) {
        if (t4mc$addressOverride.get() != null) {
            args.set(0, t4mc$addressOverride.get());
            t4mc$addressOverride.remove();
        }
    }
}