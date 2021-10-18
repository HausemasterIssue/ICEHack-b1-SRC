package me.fluffycq.icehack.module.modules.movement;

import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;

public class Sprint extends Module {

    Setting popbob = new Setting("Popbob", this, false);
    Setting delay = new Setting("Delay", this, 3.0D, 1.0D, 10.0D, true);
    int tick = 0;

    public Sprint() {
        super("Sprint", 0, Category.MOVEMENT);
    }

    public void onToggle(boolean state) {
        this.tick = 0;
    }

    public void onUpdate() {
        super.onUpdate();
        if (Sprint.mc.player != null && Sprint.mc.world != null) {
            ++this.tick;
            if ((double) this.tick >= this.delay.getValDouble() * 20.0D && this.popbob.getValBoolean()) {
                Sprint.mc.player.sendChatMessage("popbob is sprinting");
                this.tick = 0;
            }

            if (this.canSprint() && !Sprint.mc.player.isSprinting()) {
                Sprint.mc.player.setSprinting(true);
            }

        }
    }

    private boolean canSprint() {
        return Sprint.mc.player.moveForward > 0.0F && !Sprint.mc.player.isActiveItemStackBlocking() && !Sprint.mc.player.isOnLadder() && !Sprint.mc.player.collidedHorizontally && Sprint.mc.player.getFoodStats().getFoodLevel() > 6;
    }
}
