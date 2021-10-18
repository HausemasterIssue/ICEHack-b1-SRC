package me.fluffycq.icehack.module.modules.hud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.clickgui.frame.Frame;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import net.minecraft.client.gui.ScaledResolution;

public class ModuleArray extends Module {

    public Setting topline;
    public Setting rainbow;
    public Setting red;
    public Setting green;
    public Setting blue;
    public Setting redinfo;
    public Setting greeninfo;
    public Setting blueinfo;
    public Setting v;
    public ArrayList hArrangement = new ArrayList();
    public ArrayList vArrangement = new ArrayList();
    ScaledResolution resolution;
    Frame frame;
    ArrayList enabled;
    int color;
    int infocolor;

    public ModuleArray() {
        super("ArrayList", 0, Category.HUD);
        this.resolution = new ScaledResolution(ModuleArray.mc);
        this.enabled = new ArrayList();
        this.rainbow = new Setting("Rainbow", this, false);
        this.red = new Setting("Red", this, 255.0D, 3.0D, 255.0D, true);
        this.green = new Setting("Green", this, 26.0D, 3.0D, 255.0D, true);
        this.blue = new Setting("Blue", this, 42.0D, 3.0D, 255.0D, true);
        this.redinfo = new Setting("Info Red", this, 255.0D, 3.0D, 255.0D, true);
        this.greeninfo = new Setting("Info Green", this, 26.0D, 3.0D, 255.0D, true);
        this.blueinfo = new Setting("Info Blue", this, 42.0D, 3.0D, 255.0D, true);
        this.hArrangement.add("Left");
        this.hArrangement.add("Right");
        this.vArrangement.add("Up");
        this.vArrangement.add("Down");
        this.v = new Setting("Array V", this, "Down", this.vArrangement);
    }

    public void onEnable() {
        Iterator iterator = ICEHack.fevents.moduleManager.moduleList.iterator();

        while (iterator.hasNext()) {
            Module mod = (Module) iterator.next();
            Comparator lengthComp;

            if (this.enabled.contains(mod) && (mod.isDisabled() || !mod.isVisible())) {
                this.enabled.remove(mod);
                lengthComp = new Comparator() {
                    public int compare(Module o1, Module o2) {
                        return Integer.compare(o2.getName().length() + o2.getModInfo().length(), o1.getName().length() + o1.getModInfo().length());
                    }
                };
                Collections.sort(this.enabled, lengthComp);
            }

            if (!this.enabled.contains(mod) && mod.isEnabled() && !mod.getCategory().equals(Category.HUD) && mod.isVisible()) {
                this.enabled.add(mod);
                lengthComp = new Comparator() {
                    public int compare(Module o1, Module o2) {
                        return Integer.compare(o2.getName().length() + o2.getModInfo().length(), o1.getName().length() + o1.getModInfo().length());
                    }
                };
                Collections.sort(this.enabled, lengthComp);
            }
        }

    }

    public void onDisable() {
        Iterator iterator = ICEHack.fevents.moduleManager.moduleList.iterator();

        while (iterator.hasNext()) {
            Module mod = (Module) iterator.next();
            Comparator lengthComp;

            if (this.enabled.contains(mod) && (mod.isDisabled() || !mod.isVisible())) {
                this.enabled.remove(mod);
                lengthComp = new Comparator() {
                    public int compare(Module o1, Module o2) {
                        return Integer.compare(o2.getName().length() + o2.getModInfo().length(), o1.getName().length() + o1.getModInfo().length());
                    }
                };
                Collections.sort(this.enabled, lengthComp);
            }

            if (!this.enabled.contains(mod) && mod.isEnabled() && !mod.getCategory().equals(Category.HUD) && mod.isVisible()) {
                this.enabled.add(mod);
                lengthComp = new Comparator() {
                    public int compare(Module o1, Module o2) {
                        return Integer.compare(o2.getName().length() + o2.getModInfo().length(), o1.getName().length() + o1.getModInfo().length());
                    }
                };
                Collections.sort(this.enabled, lengthComp);
            }
        }

    }

    public void onRender() {
        Iterator textY = ICEHack.fevents.moduleManager.moduleList.iterator();

        while (textY.hasNext()) {
            Module i = (Module) textY.next();
            Comparator s;

            if (this.enabled.contains(i) && (i.isDisabled() || !i.isVisible())) {
                this.enabled.remove(i);
                s = new Comparator() {
                    public int compare(Module o1, Module o2) {
                        return Integer.compare(o2.getName().length() + o2.getModInfo().length(), o1.getName().length() + o1.getModInfo().length());
                    }
                };
                Collections.sort(this.enabled, s);
            }

            if (!this.enabled.contains(i) && i.isEnabled() && !i.getCategory().equals(Category.HUD) && i.isVisible()) {
                this.enabled.add(i);
                s = new Comparator() {
                    public int compare(Module o1, Module o2) {
                        return Integer.compare(o2.getName().length() + o2.getModInfo().length(), o1.getName().length() + o1.getModInfo().length());
                    }
                };
                Collections.sort(this.enabled, s);
            }
        }

        this.frame = (Frame) ICEHack.clickgui.frames.get(0);
        if (this.frame.extended) {
            if (this.rainbow.getValBoolean()) {
                this.color = Color.getHSBColor((float) (System.currentTimeMillis() % 7500L) / 7500.0F, 0.8F, 0.8F).getRGB();
            } else {
                this.color = (new Color((int) this.red.getValDouble(), (int) this.green.getValDouble(), (int) this.blue.getValDouble())).getRGB();
            }

            this.infocolor = (new Color((int) this.redinfo.getValDouble(), (int) this.greeninfo.getValDouble(), (int) this.blueinfo.getValDouble())).getRGB();
            int i;
            int j;
            Iterator iterator;
            Module module;

            if (this.frame.x + 100 <= this.resolution.getScaledWidth() / 2 + 100) {
                i = this.frame.y + 15;
                if (this.v.getValString().equalsIgnoreCase("Up")) {
                    for (j = this.enabled.size() - 1; j >= 0; --j) {
                        ModuleArray.mc.fontRenderer.drawStringWithShadow(((Module) this.enabled.get(j)).getName(), (float) this.frame.x, (float) i, this.color);
                        ModuleArray.mc.fontRenderer.drawStringWithShadow(((Module) this.enabled.get(j)).getModInfo(), (float) (this.frame.x + 2 + ModuleArray.mc.fontRenderer.getStringWidth(((Module) this.enabled.get(j)).getName())), (float) i, this.infocolor);
                        i += 10;
                    }
                } else if (this.v.getValString().equalsIgnoreCase("Down")) {
                    for (iterator = this.enabled.iterator(); iterator.hasNext(); i += 10) {
                        module = (Module) iterator.next();
                        ModuleArray.mc.fontRenderer.drawStringWithShadow(module.getName(), (float) this.frame.x, (float) i, this.color);
                        ModuleArray.mc.fontRenderer.drawStringWithShadow(module.getModInfo(), (float) (this.frame.x + 2 + ModuleArray.mc.fontRenderer.getStringWidth(module.getName())), (float) i, this.infocolor);
                    }
                }
            }

            if (this.frame.x + 100 >= this.resolution.getScaledWidth() / 2 + 100) {
                i = this.frame.y + 15;
                if (this.v.getValString().equalsIgnoreCase("Up")) {
                    for (j = this.enabled.size() - 1; j >= 0; --j) {
                        int k = this.frame.x + 100 - ModuleArray.mc.fontRenderer.getStringWidth(((Module) this.enabled.get(j)).getName()) - 2 - ModuleArray.mc.fontRenderer.getStringWidth(((Module) this.enabled.get(j)).getModInfo());

                        ModuleArray.mc.fontRenderer.drawStringWithShadow(((Module) this.enabled.get(j)).getName(), (float) k, (float) i, this.color);
                        ModuleArray.mc.fontRenderer.drawStringWithShadow(((Module) this.enabled.get(j)).getModInfo(), (float) (k + ModuleArray.mc.fontRenderer.getStringWidth(((Module) this.enabled.get(j)).getName()) + 2), (float) i, this.infocolor);
                        i += 10;
                    }
                } else if (this.v.getValString().equalsIgnoreCase("Down")) {
                    for (iterator = this.enabled.iterator(); iterator.hasNext(); i += 10) {
                        module = (Module) iterator.next();
                        int baseX = this.frame.x + 100 - ModuleArray.mc.fontRenderer.getStringWidth(module.getName()) - 2 - ModuleArray.mc.fontRenderer.getStringWidth(module.getModInfo());

                        ModuleArray.mc.fontRenderer.drawStringWithShadow(module.getName(), (float) baseX, (float) i, this.color);
                        ModuleArray.mc.fontRenderer.drawStringWithShadow(module.getModInfo(), (float) (baseX + ModuleArray.mc.fontRenderer.getStringWidth(module.getName()) + 2), (float) i, this.infocolor);
                    }
                }
            }
        }

    }
}
