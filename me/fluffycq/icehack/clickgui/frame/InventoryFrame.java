package me.fluffycq.icehack.clickgui.frame;

import java.awt.Color;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.util.GuiUtil;
import me.fluffycq.icehack.module.Module;

public class InventoryFrame extends Frame {

    public InventoryFrame(String title, int x, int y, Module parent) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.parent = parent;
        this.dragging = false;
        this.extended = true;
    }

    public void drawFrame(int mouseX, int mouseY) {
        if (this.dragging) {
            this.x = mouseX;
            this.y = mouseY;
        }

        this.width = 162;
        this.height = 54;
        int guiColor;

        if (ICEHack.setmgr.getSettingByMod("Rainbow", ICEHack.fevents.moduleManager.getModule("ClickGUI")).getValBoolean()) {
            guiColor = Color.getHSBColor((float) (System.currentTimeMillis() % 7500L) / 7500.0F, 0.8F, 0.8F).getRGB();
        } else {
            guiColor = (new Color((int) ICEHack.setmgr.getSettingByName("Red").getValDouble(), (int) ICEHack.setmgr.getSettingByName("Green").getValDouble(), (int) ICEHack.setmgr.getSettingByName("Blue").getValDouble())).getRGB();
        }

        GuiUtil.drawHorizontalLine(this.x, this.x + 162 - 1, this.y, guiColor);
        GuiUtil.drawHorizontalLine(this.x, this.x + 162 - 1, this.y + 53, guiColor);
        GuiUtil.drawVerticalLine(this.x, this.y + 54, this.y, guiColor);
        GuiUtil.drawVerticalLine(this.x + 162 - 1, this.y + 54, this.y, guiColor);
    }
}
