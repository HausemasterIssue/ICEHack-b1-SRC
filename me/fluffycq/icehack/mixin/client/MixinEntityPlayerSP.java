package me.fluffycq.icehack.mixin.client;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.PlayerMoveEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = { EntityPlayerSP.class},
    priority = Integer.MAX_VALUE
)
public class MixinEntityPlayerSP {

    @Inject(
        method = { "move"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    public void move(MoverType type, double x, double y, double z, CallbackInfo info) {
        PlayerMoveEvent event = new PlayerMoveEvent(x, y, z, Minecraft.getMinecraft().player.onGround);

        ICEHack.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            info.cancel();
        }

    }
}
