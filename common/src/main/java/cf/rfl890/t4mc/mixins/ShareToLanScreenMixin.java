package cf.rfl890.t4mc.mixins;

import cf.rfl890.t4mc.interfaces.TicketAccessor;
import net.minecraft.client.gui.screens.ShareToLanScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.commands.PublishCommand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShareToLanScreen.class)
public abstract class ShareToLanScreenMixin {
    @Redirect(method = "method_19851", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/commands/PublishCommand;getSuccessMessage(I)Lnet/minecraft/network/chat/MutableComponent;"))
    private MutableComponent getSuccessMessage(int i, IntegratedServer integratedServer) {
        String ticket = ((TicketAccessor) integratedServer).t4mc$getTicket();

        if (ticket != null) {
            return Component.literal("[Click here to copy server address]").withStyle(style -> style.withColor(-16711936).withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, "iroh://" + ticket)));
        }

        return PublishCommand.getSuccessMessage(i);
    }
}
