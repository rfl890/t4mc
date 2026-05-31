package cf.rfl890.iroh;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;

public class IrohNativeLoader {
    private static volatile boolean loaded = false;

    public static synchronized void init() {
        if (loaded) return;

        String os = System.getProperty("os.name").toLowerCase();
        boolean linux = !os.contains("win");

        SecureRandom r = new SecureRandom();
        byte[] salt = new byte[16];
        r.nextBytes(salt);

        StringBuilder sb = new StringBuilder();
        for (byte b : salt) {
            sb.append(String.format("%02x", b));
        }

        String hex = sb.toString();
        String path_constructed = System.getProperty("java.io.tmpdir") + ("\\iroh_bridge" + hex + (linux ? ".so" : ".dll"));

        try (InputStream is = IrohBridge.class.getResourceAsStream("/iroh_bridge" + (linux ? ".so" : ".dll"))) {
            assert is != null;
            Files.copy(is, Paths.get(path_constructed), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.load(path_constructed);

        loaded = true;
    }
}