package me.fluffycq.icehack.module.modules.render;

import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;

public class ShulkerViewer extends Module {

    public Setting opacity = new Setting("Opacity", this, 200.0D, 5.0D, 255.0D, true);
    public Setting r = new Setting("Red", this, 200.0D, 5.0D, 255.0D, true);
    public Setting g = new Setting("Green", this, 200.0D, 5.0D, 255.0D, true);
    public Setting b = new Setting("Blue", this, 200.0D, 5.0D, 255.0D, true);

    public ShulkerViewer() {
        super("ShulkerViewer", 0, Category.RENDER);
    }
}
