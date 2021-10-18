package me.fluffycq.icehack.clickgui.element;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.setting.eSetting;
import me.fluffycq.icehack.clickgui.util.GuiUtil;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Panel {

    public Minecraft mc = Minecraft.getMinecraft();
    public ArrayList modButtons = new ArrayList();
    public Category category;
    public int x;
    public int y;
    public int bottomY = 15;
    public int cWidth;
    public boolean dragging;
    public boolean extended;
    public String title;
    int defaultBG = (new Color(56, 56, 56, 170)).getRGB();
    ScaledResolution resolution;

    public Panel(Category category, int x, int y) {
        this.resolution = new ScaledResolution(this.mc);
        this.category = category;
        this.x = x;
        this.y = y;
        this.dragging = false;
        this.extended = true;
        this.title = category.categoryName;
        this.initWidth();
    }

    public void drawPanel(int mouseX, int mouseY) {
        this.drawCategoryButton();
        Iterator oldy = this.modButtons.iterator();

        while (oldy.hasNext()) {
            Button e = (Button) oldy.next();

            e.drawButton(mouseX, mouseY);
        }

        if (this.dragging) {
            int oldy1 = this.y;

            this.x = mouseX;
            this.y = mouseY;
            Iterator e2 = this.modButtons.iterator();

            while (e2.hasNext()) {
                Button e1 = (Button) e2.next();

                e1.x = this.x;
                e1.y += this.y - oldy1;

                eSetting es;

                for (Iterator iterator = e1.settings.iterator(); iterator.hasNext(); es.y += this.y - oldy1) {
                    es = (eSetting) iterator.next();
                    es.x = this.x;
                }
            }
        }

    }

    public void setY(int y) {
        int oldy = this.y;

        this.y = y;
        Iterator iterator = this.modButtons.iterator();

        while (iterator.hasNext()) {
            Button e = (Button) iterator.next();

            e.y += y - oldy;

            eSetting es;

            for (Iterator iterator1 = e.settings.iterator(); iterator1.hasNext(); es.y += y - oldy) {
                es = (eSetting) iterator1.next();
            }
        }

    }

    public void initWidth() {
        this.cWidth = 100;
    }

    public void addModules() {
        Iterator iterator = ICEHack.fevents.moduleManager.moduleList.iterator();

        while (iterator.hasNext()) {
            Module module = (Module) iterator.next();

            if (module.getCategory().equals(this.category)) {
                this.modButtons.add(new Button(module, this.x, this.y + this.bottomY, this.cWidth, 15, module.name));
                this.bottomY += 15;
            }
        }

    }

    public void drawCategoryButton() {
        int guiColor;

        if (ICEHack.setmgr.getSettingByMod("Rainbow", ICEHack.fevents.moduleManager.getModule("ClickGUI")).getValBoolean()) {
            guiColor = Color.getHSBColor((float) (System.currentTimeMillis() % 7500L) / 7500.0F, 0.8F, 0.8F).getRGB();
        } else {
            guiColor = (new Color((int) ICEHack.setmgr.getSettingByName("Red").getValDouble(), (int) ICEHack.setmgr.getSettingByName("Green").getValDouble(), (int) ICEHack.setmgr.getSettingByName("Blue").getValDouble())).getRGB();
        }

        GuiUtil.drawHorizontalLine(this.x, this.x + this.cWidth - 1, this.y, guiColor);
        GuiUtil.drawHorizontalLine(this.x, this.x + this.cWidth - 1, this.y + 14, guiColor);
        GuiUtil.drawVerticalLine(this.x, this.y + 15, this.y, guiColor);
        GuiUtil.drawVerticalLine(this.x + this.cWidth - 1, this.y + 15, this.y, guiColor);
        GuiUtil.drawRect(this.x + 1, this.y + 1, this.x + this.cWidth - 1, this.y + 14, this.defaultBG);
        GuiUtil.drawCenteredString(this.category.categoryName, this.x + this.cWidth / 2, this.y + 3, -1);
    }

    public void toggleExtend() {
        Button b;

        for (Iterator iterator = this.modButtons.iterator(); iterator.hasNext(); b.visible = !b.visible) {
            b = (Button) iterator.next();
            if (b.visible) {
                this.extended = false;
            } else {
                this.extended = true;
            }
        }

    }

    public boolean hoveringCategory(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.cWidth && mouseY >= this.y && mouseY <= this.y + 15;
    }

    public boolean hoveringPanel(int mouseX) {
        return mouseX >= this.x && mouseX <= this.x + this.cWidth;
    }

    public void mouseClicked(int mButton, int mouseX, int mouseY) {
        if (this.hoveringCategory(mouseX, mouseY) && mButton == 1) {
            this.toggleExtend();
        }

        Iterator iterator = this.modButtons.iterator();

        Button b;
        Iterator iterator1;

        while (iterator.hasNext()) {
            b = (Button) iterator.next();
            if (b.isHovering(mouseX, mouseY) && mButton == 1) {
                iterator1 = this.modButtons.iterator();

                while (iterator1.hasNext()) {
                    Button s = (Button) iterator1.next();
                    Iterator iterator2;
                    eSetting s1;

                    if (b.extended) {
                        if (s.y + s.height > b.y + b.height) {
                            s.y -= b.getSettingsSpace();
                        }

                        iterator2 = s.settings.iterator();

                        while (iterator2.hasNext()) {
                            s1 = (eSetting) iterator2.next();
                            if (s1.y + s1.height > b.y + b.height) {
                                s1.y -= b.getSettingsSpace();
                            }
                        }
                    } else {
                        if (s.y + s.height > b.y + b.height) {
                            s.y += b.getSettingsSpace();
                        }

                        iterator2 = s.settings.iterator();

                        while (iterator2.hasNext()) {
                            s1 = (eSetting) iterator2.next();
                            if (s1.y + s1.height > b.y + b.height) {
                                s1.y += b.getSettingsSpace();
                            }
                        }
                    }
                }

                b.setExtended(!b.extended);
            }
        }

        iterator = this.modButtons.iterator();

        while (iterator.hasNext()) {
            b = (Button) iterator.next();
            if (b.isHovering(mouseX, mouseY) && mButton != 1) {
                b.mouseClicked(mButton, mouseX, mouseY);
            }

            iterator1 = b.settings.iterator();

            while (iterator1.hasNext()) {
                eSetting s2 = (eSetting) iterator1.next();

                s2.mouseClicked(mouseX, mouseY, mButton);
            }
        }

        if (this.hoveringCategory(mouseX, mouseY) && mButton == 0) {
            this.dragging = true;
        }

    }

    public void mouseRelease(int mouseX, int mouseY) {
        if (this.dragging) {
            this.dragging = false;
        }

        Iterator oldy = this.modButtons.iterator();

        while (oldy.hasNext()) {
            Button b = (Button) oldy.next();
            Iterator e = b.settings.iterator();

            while (e.hasNext()) {
                eSetting s = (eSetting) e.next();

                s.mouseRelease(mouseX, mouseY);
            }
        }

        int oldy1 = this.y;

        this.x = (int) ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap X").getValDouble() * Math.round((float) (this.x / (int) ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap X").getValDouble()));
        this.y = (int) ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap Y").getValDouble() * Math.round((float) (this.y / (int) ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap Y").getValDouble()));
        Iterator b1 = this.modButtons.iterator();

        while (b1.hasNext()) {
            Button e1 = (Button) b1.next();

            e1.x = this.x;
            e1.y += this.y - oldy1;

            eSetting es;

            for (Iterator s1 = e1.settings.iterator(); s1.hasNext(); es.y += this.y - oldy1) {
                es = (eSetting) s1.next();
                es.x = this.x;
            }
        }

    }

    public void keyTyped(char typedChar, int keyCode) throws IOException {
        Iterator iterator = this.modButtons.iterator();

        while (iterator.hasNext()) {
            Button b = (Button) iterator.next();
            Iterator iterator1 = b.settings.iterator();

            while (iterator1.hasNext()) {
                eSetting s = (eSetting) iterator1.next();

                s.keyTyped(typedChar, keyCode);
            }
        }

    }
}
