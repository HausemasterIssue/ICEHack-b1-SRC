package me.fluffycq.icehack.mixin.client;

import io.netty.channel.ChannelHandlerContext;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.PacketEvent;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
    value = { NetworkManager.class},
    priority = Integer.MAX_VALUE
)
public class MixinNetworkManager {

    @Inject(
        method = { "sendPacket(Lnet/minecraft/network/Packet;)V"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    private void onSendPacket(Packet packet, CallbackInfo callbackInfo) {
        PacketEvent.Send event = new PacketEvent.Send(packet);

        ICEHack.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }

    }

    @Inject(
        method = { "channelRead0"},
        at = {             @At("HEAD")},
        cancellable = true
    )
    private void onChannelRead(ChannelHandlerContext context, Packet packet, CallbackInfo callbackInfo) {
        PacketEvent.Receive event = new PacketEvent.Receive(packet);

        ICEHack.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }

    }
}
