package me.fluffycq.icehack.module;

import java.util.Iterator;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.config.Configuration;
import me.fluffycq.icehack.events.RenderEvent;
import me.fluffycq.icehack.setting.Setting;
import net.minecraft.client.Minecraft;

public class Module {

    public String name;
    public int key;
    public Category category;
    private boolean state;
    public String modInfo = "";
    protected static Minecraft mc = Minecraft.getMinecraft();
    Configuration mConfig = new Configuration();
    public boolean visible;

    public Module(String name, int keyCode, Category cate) {
        this.key = keyCode;
        this.category = cate;
        this.name = name;
        this.getName();
        new Setting("Bind", this, keyCode);
        new Setting("Visible", this, true);
        this.load();
    }

    public Setting getSetting(String name) {
        return ICEHack.setmgr.getSettingByMod(name, this);
    }

    public void addSetting(Setting setting) {
        ICEHack.setmgr.rSetting(setting);
    }

    public Category getCategory() {
        return this.category;
    }

    public void load() {
        Iterator iterator = ICEHack.setmgr.getSettingsByMod(this).iterator();

        while (iterator.hasNext()) {
            Setting e = (Setting) iterator.next();

            if (e.isBind()) {
                this.key = e.getKeyBind();
            }

            if (e.getName().equalsIgnoreCase("Visible")) {
                this.visible = e.getValBoolean();
            }
        }

    }

    public boolean isVisible() {
        return this.getSetting("Visible").getValBoolean();
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int keyCode) {
        Module module = ICEHack.fevents.moduleManager.getModule(this.name);

        if (module != null) {
            Iterator iterator = ICEHack.setmgr.getSettingsByMod(this).iterator();

            while (iterator.hasNext()) {
                Setting e = (Setting) iterator.next();

                if (e.isBind()) {
                    this.key = keyCode;
                    e.setValKey(keyCode);
                }
            }
        }

    }

    public String getModInfo() {
        return this.modInfo;
    }

    public void setModInfo(String info) {
        this.modInfo = info;
    }

    public void onToggle(boolean state) {}

    public void onEnable() {}

    public void onDisable() {}

    public void onUpdate() {}

    public void onRender() {}

    public void onWorld(RenderEvent e) {}

    public void onKey(int keyCode) {
        if (keyCode == this.getKey()) {
            this.setState(!this.getState());
            this.onToggle(this.getState());
        }

    }

    public boolean isEnabled() {
        return this.state;
    }

    public boolean isDisabled() {
        return !this.state;
    }

    public void enable() {
        this.state = true;
    }

    public void disable() {
        this.state = false;
    }

    public boolean getState() {
        return this.state;
    }

    public void setState(boolean state) {
        this.state = state;
        this.subscribeState(state);
    }

    public void subscribeState(boolean s) {
        if (s) {
            this.onEnable();
            ICEHack.EVENT_BUS.subscribe((Object) this);
        } else if (!s) {
            this.onDisable();
            ICEHack.EVENT_BUS.unsubscribe((Object) this);
        }

    }

    public String getName() {
        return this.name;
    }
}
