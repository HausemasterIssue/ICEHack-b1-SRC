package me.fluffycq.icehack.module.modules.misc;

import java.util.Iterator;
import java.util.function.Predicate;
import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.EventHook;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerListItem.Action;
import net.minecraft.network.play.server.SPacketPlayerListItem.AddPlayerData;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;

public class AntiRodarg extends Module {

    @EventHandler
    private Listener recListener = new Listener((event) -> {
        if (event.getPacket() instanceof SPacketPlayerListItem && ((SPacketPlayerListItem) event.getPacket()).getAction() == Action.ADD_PLAYER) {
            SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();
            Iterator iterator = packet.getEntries().iterator();

            while (iterator.hasNext()) {
                AddPlayerData data = (AddPlayerData) iterator.next();
                String player = data.getProfile() != null ? data.getProfile().getName() : "null";

                if (player.equalsIgnoreCase("Rodarg")) {
                    FMLClientHandler.instance().getClientToServerNetworkManager().closeChannel(new TextComponentString("REMO IS COMING!!! HIDE YOUR BASES!"));
                }
            }
        }

    }, new Predicate[0]);

    public AntiRodarg() {
        super("AntiRodarg", 0, Category.MISC);
    }
}
