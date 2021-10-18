package me.fluffycq.icehack.module.modules.misc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Predicate;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.events.PacketEvent;
import me.fluffycq.icehack.events.PopTotemEvent;
import me.fluffycq.icehack.message.Messages;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.EventHook;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;

public class TotemPopAlert extends Module {

    public Setting cName = new Setting("ICEHack", this, true);
    HashMap popped = new HashMap();
    @EventHandler
    public Listener popEvent = new Listener((event) -> {
        // $FF: Couldn't be decompiled
    }, new Predicate[0]);
    @EventHandler
    public Listener totemPopListener = new Listener((event) -> {
        if (TotemPopAlert.mc.world != null && TotemPopAlert.mc.player != null) {
            if (this.isEnabled() && event.getPacket() instanceof SPacketEntityStatus && ((SPacketEntityStatus) event.getPacket()).getOpCode() == 35) {
                Entity entity = ((SPacketEntityStatus) event.getPacket()).getEntity(TotemPopAlert.mc.world);

                ICEHack.EVENT_BUS.post(new PopTotemEvent(entity));
            }

        }
    }, new Predicate[0]);

    public TotemPopAlert() {
        super("TotemPopAlert", 0, Category.MISC);
    }

    public void onEnable() {
        this.popped.clear();
    }

    public void onUpdate() {
        Iterator iterator = TotemPopAlert.mc.world.playerEntities.iterator();

        while (iterator.hasNext()) {
            EntityPlayer player = (EntityPlayer) iterator.next();

            if (player.getHealth() <= 0.0F && this.popped.containsKey(player.getName())) {
                this.sendMessage("&3" + player.getName() + " &4died after they popped &6" + this.popped.get(player.getName()) + " totems");
                this.popped.remove(player.getName());
            }
        }

    }

    public void onDisable() {
        this.popped.clear();
    }

    private void sendMessage(String msg) {
        if (this.cName.getValBoolean()) {
            Messages.sendChatMessage(msg);
        } else {
            Messages.sendMessage(msg);
        }

    }
}
