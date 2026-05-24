package cf.rfl890.t4mc;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import java.util.Objects;

@Mod(modid="t4mc", version = "1.1.0", useMetadata=true)
public class T4mc {
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        String os = System.getProperty("os.name").toLowerCase();

        if ((!(os.contains("win") || os.contains("linux"))) || !Objects.equals(System.getProperty("sun.arch.data.model"), "64")) {
            throw new RuntimeException("This mod is only supported on 64-bit Linux or Windows!");
        }
    }
}
