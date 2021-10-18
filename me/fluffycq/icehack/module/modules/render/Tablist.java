package me.fluffycq.icehack.module.modules.render;

import java.util.ArrayList;
import me.fluffycq.icehack.friends.Friends;
import me.fluffycq.icehack.module.Category;
import me.fluffycq.icehack.module.Module;
import me.fluffycq.icehack.setting.Setting;
import me.fluffycq.icehack.util.ColorUtil;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;

public class Tablist extends Module {

    public static Tablist INST;
    public Setting friends;
    public Setting color;
    public ArrayList colors = new ArrayList();

    public Tablist() {
        super("Tablist", 0, Category.RENDER);
        Tablist.INST = this;
        this.friends = new Setting("Friends", this, true);
        this.colors.add("&4");
        this.colors.add("&c");
        this.colors.add("&6");
        this.colors.add("&e");
        this.colors.add("&2");
        this.colors.add("&a");
        this.colors.add("&b");
        this.colors.add("&3");
        this.colors.add("&1");
        this.colors.add("&9");
        this.colors.add("&d");
        this.colors.add("&5");
        this.colors.add("&f");
        this.colors.add("&7");
        this.color = new Setting("Color", this, "&a", this.colors);
    }

    public static String getName(NetworkPlayerInfo networkPlayerInfoIn) {
        String dname = networkPlayerInfoIn.getDisplayName() != null ? networkPlayerInfoIn.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(networkPlayerInfoIn.getPlayerTeam(), networkPlayerInfoIn.getGameProfile().getName());

        return Friends.isFriend(dname) && Tablist.INST.friends.getValBoolean() ? String.format("%s%s", new Object[] { ColorUtil.getColor(Tablist.INST.color.getValString()), dname}) : dname;
    }
}
