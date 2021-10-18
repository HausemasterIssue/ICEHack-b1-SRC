package me.fluffycq.icehack.clickgui.frame;

import java.awt.Color;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.util.GuiUtil;
import me.fluffycq.icehack.module.Module;

public class ArrayListFrame extends Frame {

    public ArrayListFrame(String title, int x, int y, Module parent) {
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

        this.height = 15;
        this.width = 100;
        int guiColor;

        if (ICEHack.setmgr.getSettingByMod("Rainbow", ICEHack.fevents.moduleManager.getModule("ClickGUI")).getValBoolean()) {
            guiColor = Color.getHSBColor((float) (System.currentTimeMillis() % 7500L) / 7500.0F, 0.8F, 0.8F).getRGB();
        } else {
            guiColor = (new Color((int) ICEHack.setmgr.getSettingByName("Red").getValDouble(), (int) ICEHack.setmgr.getSettingByName("Green").getValDouble(), (int) ICEHack.setmgr.getSettingByName("Blue").getValDouble())).getRGB();
        }

        GuiUtil.drawHorizontalLine(this.x, this.x + 100 - 1, this.y, guiColor);
        GuiUtil.drawHorizontalLine(this.x, this.x + 100 - 1, this.y + 14, guiColor);
        GuiUtil.drawVerticalLine(this.x, this.y + 15, this.y, guiColor);
        GuiUtil.drawVerticalLine(this.x + 100 - 1, this.y + 15, this.y, guiColor);
        GuiUtil.drawRect(this.x + 1, this.y + 1, this.x + 100 - 1, this.y + 14, this.defaultBG);
        GuiUtil.drawCenteredString("ArrayList", this.x + 50, this.y + 3, -1);
    }
}
