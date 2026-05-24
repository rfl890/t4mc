package cf.rfl890.t4mc.mixins;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiShareToLan;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GuiShareToLan.class)
public class GuiShareToLanMixin {
    @Inject(method = "actionPerformed", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/server/integrated/IntegratedServer;shareToLAN(Lnet/minecraft/world/GameType;Z)Ljava/lang/String;"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    private void actionPerformed(GuiButton button, CallbackInfo ci, String string) {
        ITextComponent itextcomponent = new TextComponentString("[Click here to copy server address]");
        Style style = itextcomponent.getStyle();
        style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "iroh://" + string));
        style.setColor(TextFormatting.GREEN);
        ((GuiScreen)(Object)this).mc.ingameGUI.getChatGUI().printChatMessage(itextcomponent);
        ci.cancel();
    }
}
