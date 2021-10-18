package me.fluffycq.icehack.clickgui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.element.Panel;
import me.fluffycq.icehack.clickgui.frame.ArrayListFrame;
import me.fluffycq.icehack.clickgui.frame.CombatInfo;
import me.fluffycq.icehack.clickgui.frame.Frame;
import me.fluffycq.icehack.clickgui.frame.InventoryFrame;
import me.fluffycq.icehack.module.Category;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

public class ClickGUI extends GuiScreen {

    public ArrayList panels = new ArrayList();
    public ArrayList frames = new ArrayList();
    int x = 1;
    int mx;
    int my;
    boolean addedCategories = false;

    public ClickGUI() {
        Category[] acategory = Category.values();
        int i = acategory.length;

        for (int j = 0; j < i; ++j) {
            Category c = acategory[j];

            if (ICEHack.fevents.moduleManager.hasModules(c)) {
                Panel cPanel = new Panel(c, this.x, 1);

                this.panels.add(cPanel);
                this.x += cPanel.cWidth + 5;
            }
        }

        this.frames.add(new ArrayListFrame("Enabled Modules", 0, 0, ICEHack.fevents.moduleManager.getModule("ArrayList")));
        this.frames.add(new CombatInfo("PvP Info", 0, 0, ICEHack.fevents.moduleManager.getModule("PvPInfo")));
        this.frames.add(new InventoryFrame("Inventory", 0, 0, ICEHack.fevents.moduleManager.getModule("Inventory")));
    }

    public void initGui() {
        super.initGui();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        Iterator iterator = this.panels.iterator();

        while (iterator.hasNext()) {
            Panel f = (Panel) iterator.next();

            f.drawPanel(mouseX, mouseY);
        }

        iterator = this.frames.iterator();

        while (iterator.hasNext()) {
            Frame f1 = (Frame) iterator.next();

            if (f1.parent.isEnabled()) {
                f1.drawFrame(mouseX, mouseY);
            }
        }

        this.mx = mouseX;
        this.my = mouseY;
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        Iterator iterator = this.panels.iterator();

        while (iterator.hasNext()) {
            Panel f = (Panel) iterator.next();

            f.mouseClicked(mouseButton, mouseX, mouseY);
        }

        iterator = this.frames.iterator();

        while (iterator.hasNext()) {
            Frame f1 = (Frame) iterator.next();

            f1.mouseClicked(mouseButton, mouseX, mouseY);
        }

    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        Iterator iterator = this.panels.iterator();

        while (iterator.hasNext()) {
            Panel f = (Panel) iterator.next();

            f.mouseRelease(mouseX, mouseY);
        }

        iterator = this.frames.iterator();

        while (iterator.hasNext()) {
            Frame f1 = (Frame) iterator.next();

            f1.mouseRelease(mouseX, mouseY);
        }

    }

    public void onGuiClosed() {
        ICEHack.fevents.moduleManager.getModule("ClickGUI").disable();
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.displayGuiScreen((GuiScreen) null);
            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }

        Iterator iterator = this.panels.iterator();

        while (iterator.hasNext()) {
            Panel p = (Panel) iterator.next();

            p.keyTyped(typedChar, keyCode);
        }

    }

    public void handleMouseInput() throws IOException {
        Iterator iterator;
        Panel panel;

        if (Mouse.getEventDWheel() > 0) {
            iterator = this.panels.iterator();

            while (iterator.hasNext()) {
                panel = (Panel) iterator.next();
                if (panel.hoveringPanel(this.mx)) {
                    panel.setY(panel.y - 5);
                }
            }
        }

        if (Mouse.getEventDWheel() < 0) {
            iterator = this.panels.iterator();

            while (iterator.hasNext()) {
                panel = (Panel) iterator.next();
                if (panel.hoveringPanel(this.mx)) {
                    panel.setY(panel.y + 5);
                }
            }
        }

        super.handleMouseInput();
    }
}
