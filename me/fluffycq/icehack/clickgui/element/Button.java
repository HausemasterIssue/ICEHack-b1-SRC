package me.fluffycq.icehack.clickgui.element;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.setting.eSetting;
import me.fluffycq.icehack.clickgui.util.GuiUtil;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class Button {

    public Minecraft mc = Minecraft.getMinecraft();
    public ArrayList settings = new ArrayList();
    public Module parent;
    public int x;
    public int y;
    public int width;
    public int height;
    public String text;
    public boolean enabled;
    public boolean visible;
    public boolean extended;
    int idleBG = (new Color(84, 84, 84, 170)).getRGB();

    public Button(Module parent, int x, int y, int width, int height, String text) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.enabled = false;
        this.visible = true;
        this.extended = false;
        this.initSettings();
    }

    public void initSettings() {
        if (ICEHack.setmgr.getSettingsByMod(this.parent) != null) {
            Iterator iterator = ICEHack.setmgr.getSettingsByMod(this.parent).iterator();

            while (iterator.hasNext()) {
                Setting s = (Setting) iterator.next();

                this.settings.add(new eSetting(s, 0, 0, 100, 15));
            }
        }

    }

    public void drawButton(int mouseX, int mouseY) {
        Color guiColor;

        if (ICEHack.setmgr.getSettingByMod("Rainbow", ICEHack.fevents.moduleManager.getModule("ClickGUI")).getValBoolean()) {
            guiColor = Color.getHSBColor((float) (System.currentTimeMillis() % 7500L) / 7500.0F, 0.8F, 0.8F);
        } else {
            guiColor = new Color((int) ICEHack.setmgr.getSettingByName("Red").getValDouble(), (int) ICEHack.setmgr.getSettingByName("Green").getValDouble(), (int) ICEHack.setmgr.getSettingByName("Blue").getValDouble());
        }

        if (this.visible) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GuiUtil.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, this.isToggled() ? guiColor.darker().getRGB() : this.idleBG);
            GuiUtil.drawCenteredString(this.text, this.x + this.width / 2, this.y + (this.height - 8) / 2, -1);
        }

        if (this.extended && this.visible) {
            Iterator iterator = this.settings.iterator();

            while (iterator.hasNext()) {
                eSetting e = (eSetting) iterator.next();

                if (e.visible) {
                    e.drawSetting(mouseX, mouseY);
                }
            }
        }

    }

    public void setExtended(boolean state) {
        this.extended = state;
        eSetting e2;

        if (this.extended) {
            int bottomY = 15;

            for (Iterator e = this.settings.iterator(); e.hasNext(); bottomY += 15) {
                eSetting e1 = (eSetting) e.next();

                e1.x = this.x;
                e1.y = this.y + bottomY;
                e1.visible = true;
            }
        } else {
            for (Iterator bottomY1 = this.settings.iterator(); bottomY1.hasNext(); e2.visible = false) {
                e2 = (eSetting) bottomY1.next();
                e2.x = 0;
                e2.y = 0;
            }
        }

    }

    public int getSettingsSpace() {
        return this.settings.size() * 15;
    }

    public boolean isToggled() {
        return this.parent.getState();
    }

    public void mouseClicked(int mButton, int mouseX, int mouseY) {
        if (this.isHovering(mouseX, mouseY) && mButton == 0) {
            this.parent.setState(!this.parent.getState());
        }

    }

    public boolean isHovering(int mouseX, int mouseY) {
        return !this.visible ? false : mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
    }
}
