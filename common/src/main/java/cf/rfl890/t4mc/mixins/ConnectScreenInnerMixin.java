package cf.rfl890.t4mc.mixins;

import cf.rfl890.iroh.IrohBridge;
import cf.rfl890.t4mc.interfaces.ShutdownTokenAccessor;
import com.mojang.logging.LogUtils;
import io.netty.channel.ChannelFuture;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.Connection;
import net.minecraft.util.HttpUtil;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

@Mixin(targets = "net/minecraft/client/gui/screens/ConnectScreen$1")
public abstract class ConnectScreenInnerMixin {
    @Unique
    private static final Logger t4mc$LOGGER = LogUtils.getLogger();
    @Shadow(aliases = {"val$server", "val$p_252078_"})
    @Final
    ServerData server;
    @Unique
    private int t4mc$port = -1;

    @Unique
    private long t4mc$shutdownToken = 0;

    @Inject(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setBandwidthLogger(Lnet/minecraft/util/debugchart/LocalSampleLogger;)V", shift = At.Shift.AFTER))
    private void runHead(CallbackInfo ci) {
        int chosen_port = HttpUtil.getAvailablePort();

        if (this.server.ip.startsWith("iroh://")) {
            String ticket = this.server.ip.substring(this.server.ip.lastIndexOf("iroh://") + 7);
            long token = IrohBridge.iroh2Tcp("127.0.0.1", ticket, (short) chosen_port);

            this.t4mc$port = chosen_port;
            this.t4mc$shutdownToken = token;

            t4mc$LOGGER.info("Creating Iroh connection, ticket={}, port={}, shutdownToken={}", ticket, chosen_port, token);
        }
    }

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;connect(Ljava/net/InetSocketAddress;ZLnet/minecraft/network/Connection;)Lio/netty/channel/ChannelFuture;"))
    private ChannelFuture connectRedirector(InetSocketAddress inetSocketAddress, boolean bl, Connection arg) {
        if (this.t4mc$port != -1) {
            t4mc$LOGGER.info("Connecting to Iroh via localhost bridge on port {}", this.t4mc$port);
            ((ShutdownTokenAccessor) arg).t4mc$setShutdownToken(this.t4mc$shutdownToken);
            return Connection.connect(new InetSocketAddress("127.0.0.1", this.t4mc$port), bl, arg);
        }
        return Connection.connect(inetSocketAddress, bl, arg);
    }
}

