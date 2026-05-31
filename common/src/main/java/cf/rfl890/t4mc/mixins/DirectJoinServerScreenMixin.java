package cf.rfl890.t4mc.mixins;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DirectJoinServerScreen.class)
public abstract class DirectJoinServerScreenMixin {
    @Shadow
    private EditBox ipEdit;

    @Shadow
    private Button selectButton;

    @Inject(method = "updateSelectButtonStatus", at = @At("HEAD"), cancellable = true)
    private void updateSelectButtonStatus(CallbackInfo ci) {
        if (this.ipEdit.getValue().startsWith("iroh://")) {
            this.selectButton.active = true;
            ci.cancel();
        }
    }
}
