package cf.rfl890.t4mc.overrides;

import cf.rfl890.iroh.IrohBridge;
import cf.rfl890.t4mc.interfaces.NetworkManagerAccess;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;

import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.logging.log4j.Logger;

public class GuiConnectingThread extends Thread {
    final GuiConnecting self;
    final int port;
    final String ip;

    public GuiConnectingThread(GuiConnecting self, String threadArg, String ip, int port) {
        super(threadArg);
        this.self = self;
        this.ip = ip;
        this.port = port;
    }

    // And now, I present to you, the viewer,
    // The Worst Piece Of Code I Have Ever Written!

    @Override
    public void run() {
        InetAddress inetaddress = null;

        Method access_000;
        Method access_102;
        Method access_100;
        Method access_200;
        Method access_300;


        try {
            access_000 = GuiConnecting.class.getDeclaredMethod("access$000", GuiConnecting.class); // Returns: Boolean
            access_102 = GuiConnecting.class.getDeclaredMethod("access$102", GuiConnecting.class, NetworkManager.class); // Returns: NetworkManager
            access_100 = GuiConnecting.class.getDeclaredMethod("access$100", GuiConnecting.class); // Returns: NetworkManager
            access_200 = GuiConnecting.class.getDeclaredMethod("access$200", GuiConnecting.class); // Returns: GuiScreen
            access_300 = GuiConnecting.class.getDeclaredMethod("access$300"); // Returns: Logger

            access_000.setAccessible(true);
            access_102.setAccessible(true);
            access_100.setAccessible(true);
            access_200.setAccessible(true);
            access_300.setAccessible(true);

        } catch (Exception e) {
            return;
        }

        try {
            if ((boolean) access_000.invoke(null, this.self)) {
                return;
            }

            if (this.ip.startsWith("iroh://")) {
                int chosen_port = -1;

                try {
                    chosen_port = HttpUtil.getSuitableLanPort();
                } catch (IOException ignored) {}

                if (chosen_port < 0) {
                    chosen_port = 25595;
                }

                String ticket = this.ip.substring(this.ip.lastIndexOf("iroh://") + 7);

                long shutdown_token = IrohBridge.iroh2Tcp("127.0.0.1", ticket, (short) chosen_port);

                inetaddress = InetAddress.getByName("127.0.0.1");
                access_102.invoke(null, this.self, NetworkManager.createNetworkManagerAndConnect(inetaddress, chosen_port, this.self.mc.gameSettings.isUsingNativeTransport()));
                ((NetworkManagerAccess) access_100.invoke(null, this.self)).t4mc$setShutdownToken(shutdown_token);
                ((Logger) access_300.invoke(null)).info("Creating Iroh connection, ticket={}, port={}, shutdownToken={}", ticket, chosen_port, shutdown_token);
            } else {
                inetaddress = InetAddress.getByName(this.ip);
                access_102.invoke(null, this.self, NetworkManager.createNetworkManagerAndConnect(inetaddress, this.port, this.self.mc.gameSettings.isUsingNativeTransport()));
            }

            ((NetworkManager) access_100.invoke(null, this.self)).setNetHandler(new NetHandlerLoginClient((NetworkManager) access_100.invoke(null, this.self), this.self.mc, (GuiScreen) access_200.invoke(null, this.self)));
            ((NetworkManager) access_100.invoke(null, this.self)).sendPacket(new C00Handshake(this.ip, this.port, EnumConnectionState.LOGIN, true));
            ((NetworkManager) access_100.invoke(null, this.self)).sendPacket(new CPacketLoginStart(this.self.mc.getSession().getProfile()));
        } catch (UnknownHostException var5) {
            try {
                if ((boolean) access_000.invoke(null, this.self)) {
                    return;
                }
            } catch (Exception e) {
                return;
            }

            try {
                ((Logger) access_300.invoke(null)).error("Couldn't connect to server", var5);
                this.self.mc.displayGuiScreen(new GuiDisconnected((GuiScreen) access_200.invoke(null, this.self), "connect.failed", new TextComponentTranslation("disconnect.genericReason", new Object[]{"Unknown host"})));
            } catch (Exception e) {
                return;
            }
        } catch (Exception var6) {
            try {
                if ((boolean) access_000.invoke(null, this.self)) {
                    return;
                }
            } catch (Exception e) {
                return;
            }
            try {
                ((Logger) access_300.invoke(null)).error("Couldn't connect to server", var6);
            } catch (Exception e) {
                return;
            }

            String s = var6.toString();
            if (inetaddress != null) {
                String s1 = inetaddress + ":" + this.port;
                s = s.replaceAll(s1, "");
            }

            try {
                this.self.mc.displayGuiScreen(new GuiDisconnected((GuiScreen) access_200.invoke(null, this.self), "connect.failed", new TextComponentTranslation("disconnect.genericReason", new Object[]{s})));
            } catch (Exception e) {
                return;
            }
        }
    }
}
