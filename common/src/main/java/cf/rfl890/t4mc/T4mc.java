package cf.rfl890.t4mc;

import java.util.Objects;

public final class T4mc {
    public static final String MOD_ID = "t4mc";

    public static void init() {
        String os = System.getProperty("os.name").toLowerCase();

        if ((!(os.contains("win") || os.contains("linux"))) || !Objects.equals(System.getProperty("sun.arch.data.model"), "64")) {
            throw new RuntimeException("This mod is only supported on 64-bit Linux or Windows!");
        }
    }
}