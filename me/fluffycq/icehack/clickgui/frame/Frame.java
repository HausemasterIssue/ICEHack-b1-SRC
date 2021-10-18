package me.fluffycq.icehack.clickgui.frame;

import java.awt.Color;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class Frame {

    public String title;
    public int x;
    public int y;
    public int width;
    public int height;
    public boolean dragging;
    public boolean extended;
    public Module parent;
    int defaultBG = (new Color(56, 56, 56, 170)).getRGB();
    ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());

    public void drawFrame(int mouseX, int mouseY) {}

    public boolean isHovering(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width && mouseY >= this.y && mouseY <= this.y + this.height;
    }

    public void mouseClicked(int mButton, int mouseX, int mouseY) {
        if (this.isHovering(mouseX, mouseY) && mButton == 0 && this.parent.isEnabled()) {
            this.dragging = true;
        }

        if (this.isHovering(mouseX, mouseY) && mButton == 1 && this.parent.isEnabled()) {
            this.extended = !this.extended;
        }

    }

    public void mouseRelease(int mouseX, int mouseY) {
        if (this.dragging) {
            this.x = (int) ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap X").getValDouble() * Math.round((float) (this.x / (int) ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap X").getValDouble()));
            this.y = (int) ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap Y").getValDouble() * Math.round((float) (this.y / (int) ICEHack.fevents.moduleManager.getModule("ClickGUI").getSetting("Snap Y").getValDouble()));
            this.dragging = false;
        }

    }
}
