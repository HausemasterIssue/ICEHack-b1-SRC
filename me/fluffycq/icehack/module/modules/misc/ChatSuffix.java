package me.fluffycq.icehack.module.modules.misc;

import java.util.function.Predicate;
import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.EventHook;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.play.client.CPacketChatMessage;

public class ChatSuffix extends Module {

    public Setting commands = new Setting("OnCommand", this, false);
    String ICEHACK = " �?? ɪᴄᴇʜᴀᴄᴋ";
    @EventHandler
    public Listener listener = new Listener((event) -> {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();

            if (s.startsWith("/") && !this.commands.getValBoolean()) {
                return;
            }

            s = s + this.ICEHACK;
            if (s.length() >= 256) {
                s = s.substring(0, 256);
            }

            ((CPacketChatMessage) event.getPacket()).message = s;
        }

    }, new Predicate[0]);

    public ChatSuffix() {
        super("ChatSuffix", 0, Category.MISC);
    }
}
