package cf.rfl890.t4mc.fabric;

import net.fabricmc.api.ModInitializer;

import cf.rfl890.t4mc.T4mc;

public final class T4mcFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        T4mc.init();
    }
}
