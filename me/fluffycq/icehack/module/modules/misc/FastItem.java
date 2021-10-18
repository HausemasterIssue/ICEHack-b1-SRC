package me.fluffycq.icehack.module.modules.misc;

import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerDigging.Action;
import net.minecraft.util.math.BlockPos;

public class FastItem extends Module {

    public Setting delay = new Setting("Delay", this, 1.0D, 0.0D, 20.0D, true);
    public Setting xp = new Setting("XP", this, true);
    public Setting bow = new Setting("Bow", this, true);
    private static long tick = 0L;

    public FastItem() {
        super("FastItem", 0, Category.MISC);
    }

    public void onUpdate() {
        if (this.bow.getValBoolean() && this.isHolding(Items.BOW) && FastItem.mc.player.isHandActive() && FastItem.mc.player.getItemInUseMaxCount() >= 3) {
            FastItem.mc.player.connection.sendPacket(new CPacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, FastItem.mc.player.getHorizontalFacing()));
            FastItem.mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(FastItem.mc.player.getActiveHand()));
            FastItem.mc.player.stopActiveHand();
        }

        if (this.delay.getValDouble() > 0.0D) {
            if (FastItem.tick > 0L) {
                --FastItem.tick;
                FastItem.mc.rightClickDelayTimer = 1;
                return;
            }

            FastItem.tick = (long) Math.round((float) (2 * Math.round((float) this.delay.getValDouble() / 2.0F)));
        }

        if (this.isHolding(Items.EXPERIENCE_BOTTLE) && this.xp.getValBoolean()) {
            FastItem.mc.rightClickDelayTimer = 0;
        }

    }

    public boolean isHolding(Item i) {
        return FastItem.mc.player.getHeldItemMainhand().getItem().equals(i);
    }
}
