package me.fluffycq.icehack.events;

import me.zero.alpine.type.Cancellable;
import net.minecraft.client.Minecraft;

public class ICEEvent extends Cancellable {

    Era era;
    final float partialTicks;

    public ICEEvent() {
        this.era = Era.PRE;
        this.partialTicks = Minecraft.getMinecraft().getRenderPartialTicks();
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public Era getEra() {
        return this.era;
    }
}
