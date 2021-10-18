package me.fluffycq.icehack.module.modules.hud;

import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.module.modules.screen.ConsoleScreen;

public class Console extends Module {

    public Console() {
        super("Console", 0, Category.HUD);
    }

    public void onToggle(boolean state) {
        this.setState(false);
        Console.mc.displayGuiScreen(new ConsoleScreen());
    }
}
