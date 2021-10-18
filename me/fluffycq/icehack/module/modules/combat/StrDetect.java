package me.fluffycq.icehack.module.modules.combat;

import java.util.ArrayList;
import java.util.Iterator;
import me.fluffycq.icehack.message.Messages;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

public class StrDetect extends Module {

    ArrayList users = new ArrayList();

    public StrDetect() {
        super("StrDetect", 0, Category.COMBAT);
    }

    public void onEnable() {
        this.users.clear();
    }

    public void onUpdate() {
        Iterator iterator = StrDetect.mc.world.loadedEntityList.iterator();

        while (iterator.hasNext()) {
            Entity e = (Entity) iterator.next();

            if (e instanceof EntityLivingBase && e != StrDetect.mc.player && ((EntityLivingBase) e).getHealth() > 0.0F && e instanceof EntityPlayer) {
                EntityPlayer p = (EntityPlayer) e;

                if (p.getActivePotionMap() != null) {
                    if (p.isPotionActive(MobEffects.STRENGTH) && !this.users.contains(p.getName())) {
                        this.users.add(p.getName());
                        Messages.sendChatMessage("&3" + p.getName() + " &anow has Strength.");
                    }

                    if (!p.isPotionActive(MobEffects.STRENGTH) && this.users.contains(p.getName())) {
                        this.users.remove(p.getName());
                        Messages.sendChatMessage("&3" + p.getName() + " &4no longer has Strength.");
                    }
                }
            }
        }

    }

    public void onDisable() {
        this.users.clear();
    }
}
