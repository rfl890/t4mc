# t4mc
## Build instructions
- Build the JAR as normal using gradle
- Compile [t4mc-iroh-bridge](https://github.com/rfl890/t4mc-iroh-bridge) for `x86_64-pc-windows-msvc` and `x86_64-unknown-linux-gnu`
- Copy the shared libraries (`iroh_bridge.dll` and `iroh_bridge.so` respectively) to the root of your compiled JAR
- You're done
