package cf.rfl890.iroh;

public class IrohBridge {
    public static native String tcp2Iroh(String address, short port);
    public static native long iroh2Tcp(String address, String ticket, short port);
    public static native void shutdown(long handle);

    static {
        IrohNativeLoader.init();
    }
}