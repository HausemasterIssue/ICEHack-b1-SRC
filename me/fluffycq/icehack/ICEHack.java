package me.fluffycq.icehack;

import java.util.Iterator;
import me.fluffycq.icehack.clickgui.ClickGUI;
import me.fluffycq.icehack.command.CommandManager;
import me.fluffycq.icehack.config.Configuration;
import me.fluffycq.icehack.events.ForgeEvents;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.SettingsManager;
import me.zero.alpine.EventBus;
import me.zero.alpine.EventManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = "icehack",
    name = "ICE Hack",
    version = "1.0"
)
public class ICEHack {

    public static final String MODID = "icehack";
    public static final String NAME = "ICE Hack";
    public static final String VERSION = "1.0";
    public static Logger logger;
    public static ForgeEvents fevents;
    public static final EventBus EVENT_BUS = new EventManager();
    public static SettingsManager setmgr;
    public static ClickGUI clickgui;
    public static CommandManager cmdmanager;
    public static Friends friends;

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ICEHack.setmgr = new SettingsManager();
        MinecraftForge.EVENT_BUS.register(ICEHack.fevents = new ForgeEvents());
        ICEHack.clickgui = new ClickGUI();
        ICEHack.cmdmanager = new CommandManager();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        Configuration conf = new Configuration();

        ICEHack.friends = new Friends();
        conf.createConfig(ICEHack.fevents.moduleManager, ICEHack.setmgr);
        Iterator iterator = ICEHack.fevents.moduleManager.moduleList.iterator();

        while (iterator.hasNext()) {
            Module m = (Module) iterator.next();

            conf.loadSettings(m);
        }

        if (!conf.checkPanelDir()) {
            conf.initPanels();
        } else {
            conf.loadPanels();
        }

        conf.initFrames();
        conf.loadFrames();
        ICEHack.friends.createFriends();
    }

    public static void save() {
        Configuration config = new Configuration();

        config.saveSettings(ICEHack.fevents.moduleManager, ICEHack.setmgr);
        config.savePanels();
        config.saveFrames();
        ICEHack.friends.saveFriends();
    }
}
