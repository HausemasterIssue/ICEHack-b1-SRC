package me.fluffycq.icehack.module.modules.hud;

import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import net.minecraft.client.gui.ScaledResolution;

public class GUI extends Module {

    public Setting topline;
    public Setting rainbow;
    public Setting red;
    public Setting green;
    public Setting blue;
    public Setting snapX;
    public Setting snapY;
    ScaledResolution resolution;

    public GUI() {
        super("ClickGUI", 205, Category.HUD);
        this.resolution = new ScaledResolution(GUI.mc);
        this.topline = new Setting("TopLine", this, true);
        this.rainbow = new Setting("Rainbow", this, false);
        this.red = new Setting("Red", this, 255.0D, 3.0D, 255.0D, true);
        this.green = new Setting("Green", this, 26.0D, 3.0D, 255.0D, true);
        this.blue = new Setting("Blue", this, 42.0D, 3.0D, 255.0D, true);
        this.snapX = new Setting("Snap X", this, 5.0D, 1.0D, 20.0D, true);
        this.snapY = new Setting("Snap Y", this, 5.0D, 1.0D, 20.0D, true);
    }

    public void onToggle(boolean state) {
        GUI.mc.displayGuiScreen(ICEHack.clickgui);
    }
}
