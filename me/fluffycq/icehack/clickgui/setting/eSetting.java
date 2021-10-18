package me.fluffycq.icehack.clickgui.setting;

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.util.GuiUtil;
import me.fluffycq.icehack.setting.Setting;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

public class eSetting {

    public int x;
    public int y;
    public int width;
    public int height;
    public Setting setting;
    public boolean visible;
    public boolean dragging;
    public boolean listening;

    public eSetting(Setting setting, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.setting = setting;
        this.visible = false;
        this.dragging = false;
        this.listening = false;
    }

    public void drawSetting(int mouseX, int mouseY) {
        int guiColor;

        if (ICEHack.setmgr.getSettingByMod("Rainbow", ICEHack.fevents.moduleManager.getModule("ClickGUI")).getValBoolean()) {
            guiColor = Color.getHSBColor((float) (System.currentTimeMillis() % 7500L) / 7500.0F, 0.8F, 0.8F).getRGB();
        } else {
            guiColor = (new Color((int) ICEHack.setmgr.getSettingByName("Red").getValDouble(), (int) ICEHack.setmgr.getSettingByName("Green").getValDouble(), (int) ICEHack.setmgr.getSettingByName("Blue").getValDouble())).getRGB();
        }

        if (this.visible) {
            double percent;

            if (this.dragging) {
                percent = (double) Math.min(100, Math.max(0, mouseX - this.x));
                BigDecimal sliderX = new BigDecimal(percent / 100.0D * (this.setting.getMax() - this.setting.getMin()) + this.setting.getMin());

                sliderX = sliderX.setScale(2, RoundingMode.HALF_UP);
                double val = sliderX.doubleValue();

                this.setting.setValDouble(val);
            }

            if (this.setting.isCheck()) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GuiUtil.drawRect(this.x + 1, this.y, this.x + this.width - 1, this.y + this.height, -11382190);
                GuiUtil.drawVerticalLine(this.x, this.y + 15, this.y - 1, guiColor);
                GuiUtil.drawVerticalLine(this.x + 99, this.y + 15, this.y - 1, guiColor);
                GuiUtil.drawCenteredString(this.setting.getName() + ": " + this.setting.getValBoolean(), this.x + this.width / 2, this.y + (this.height - 8) / 2, -1);
                if (ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).indexOf(this.setting) == ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).size() - 1) {
                    GuiUtil.drawHorizontalLine(this.x, this.x + this.width - 1, this.y + this.height - 1, guiColor);
                }
            }

            if (this.setting.isSlider()) {
                percent = this.setting.getValDouble() / this.setting.getMax();
                int sliderX1 = (int) ((double) this.width * percent);

                GuiUtil.drawRect(this.x + 1, this.y, this.x + this.width - 1, this.y + this.height, -11382190);
                if (this.setting.getValDouble() < 0.0D || this.setting.getValDouble() > 0.1D) {
                    GuiUtil.drawRect(this.x + 1, this.y, this.x + sliderX1 - 1, this.y + this.height, guiColor);
                }

                GuiUtil.drawVerticalLine(this.x, this.y + 15, this.y - 1, guiColor);
                GuiUtil.drawVerticalLine(this.x + 99, this.y + 15, this.y - 1, guiColor);
                GuiUtil.drawCenteredString(this.setting.getName() + ": " + this.setting.getValDouble(), this.x + this.width / 2, this.y + (this.height - 8) / 2, -1);
                if (ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).indexOf(this.setting) == ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).size() - 1) {
                    GuiUtil.drawHorizontalLine(this.x, this.x + this.width - 1, this.y + this.height - 1, guiColor);
                }
            }

            if (this.setting.isBind()) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                if (((Setting) ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).get(0)).equals(this.setting) && ICEHack.setmgr.getSettingByMod("TopLine", ICEHack.fevents.moduleManager.getModule("ClickGUI")).getValBoolean()) {
                    GuiUtil.drawHorizontalLine(this.x, this.x + this.width - 1, this.y, guiColor);
                    GuiUtil.drawRect(this.x + 1, this.y + 1, this.x + this.width - 1, this.y + this.height, -11382190);
                } else {
                    GuiUtil.drawRect(this.x + 1, this.y, this.x + this.width - 1, this.y + this.height, -11382190);
                }

                GuiUtil.drawVerticalLine(this.x, this.y + 15, this.y - 1, guiColor);
                GuiUtil.drawVerticalLine(this.x + 99, this.y + 15, this.y - 1, guiColor);
                if (ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).indexOf(this.setting) == ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).size() - 1) {
                    GuiUtil.drawHorizontalLine(this.x, this.x + this.width - 1, this.y + this.height - 1, guiColor);
                }

                if (!this.listening) {
                    GuiUtil.drawCenteredString("Bind: " + (this.setting.getKeyBind() > -1 ? Keyboard.getKeyName(this.setting.getKeyBind()) : ""), this.x + this.width / 2, this.y + (this.height - 8) / 2, -1);
                } else {
                    GuiUtil.drawCenteredString("Bind: ...", this.x + this.width / 2, this.y + (this.height - 8) / 2, -1);
                }
            }

            if (this.setting.isCombo()) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GuiUtil.drawRect(this.x + 1, this.y, this.x + this.width - 1, this.y + this.height, -11382190);
                GuiUtil.drawVerticalLine(this.x, this.y + 15, this.y - 1, guiColor);
                GuiUtil.drawVerticalLine(this.x + 99, this.y + 15, this.y - 1, guiColor);
                GuiUtil.drawCenteredString(this.setting.getName() + ": " + this.setting.getValString(), this.x + this.width / 2, this.y + (this.height - 8) / 2, -1);
                if (ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).indexOf(this.setting) == ICEHack.setmgr.getSettingsByMod(this.setting.getParentMod()).size() - 1) {
                    GuiUtil.drawHorizontalLine(this.x, this.x + this.width - 1, this.y + this.height - 1, guiColor);
                }
            }
        }

    }

    public void mouseClicked(int mouseX, int mouseY, int mButton) {
        if (this.visible && this.isHovering(mouseX, mouseY) && mButton == 0) {
            if (this.setting.isCheck()) {
                this.setting.setValBoolean(!this.setting.getValBoolean());
            }

            if (this.setting.isSlider()) {
                this.dragging = true;
            }

            if (this.setting.isBind() && !this.listening) {
                this.listening = true;
            }

            if (this.setting.isCombo()) {
                if (this.setting.getOptions().indexOf(this.setting.getValString()) == this.setting.getOptions().size() - 1) {
                    this.setting.setValString((String) this.setting.getOptions().get(0));
                } else {
                    this.setting.setValString((String) this.setting.getOptions().get(this.setting.getOptions().indexOf(this.setting.getValString()) + 1));
                }
            }
        }

    }

    public void mouseRelease(int mouseX, int mouseY) {
        if (this.setting.isSlider()) {
            this.dragging = false;
        }

    }

    public boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.listening) {
            if (keyCode == 42) {
                this.setting.getParentMod().setKey(0);
                this.listening = false;
            } else if (keyCode == 1) {
                this.listening = false;
            } else {
                this.setting.getParentMod().setKey(keyCode);
                this.listening = false;
            }
        }

    }
}
