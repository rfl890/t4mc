package cf.rfl890.iroh;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Arrays;

public class IrohNativeLoader {
    private static volatile boolean loaded = false;

    public static synchronized void init() {
        if (loaded) return;

        String os = System.getProperty("os.name").toLowerCase();
        boolean linux = !os.contains("win");

        try {
            InputStream is = IrohBridge.class.getResourceAsStream("/iroh_bridge" + (linux ? ".so" : ".dll"));
            assert is != null;

            byte[] shared_library = IOUtils.toByteArray(is);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(shared_library);

            Path temp_directory = Paths.get(System.getProperty("java.io.tmpdir"));
            Path target_path = temp_directory.resolve(Hex.encodeHexString(hash) + (linux ? ".so" : ".dll"));

            if (Files.exists(target_path) && Files.isRegularFile(target_path)) {
                byte[] cached_library = Files.readAllBytes(target_path);
                byte[] cached_library_hash = digest.digest(cached_library);

                if (Arrays.equals(hash, cached_library_hash)) {
                    System.load(target_path.toString());
                    loaded = true;
                    return;
                } else {
                    Files.delete(target_path);
                }
            }

            Files.write(target_path, shared_library, StandardOpenOption.CREATE_NEW);
            System.load(target_path.toString());
            loaded = true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}