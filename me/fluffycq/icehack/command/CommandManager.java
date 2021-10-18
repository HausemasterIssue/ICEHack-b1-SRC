package me.fluffycq.icehack.command;

import java.util.ArrayList;
import java.util.Iterator;
import me.fluffycq.icehack.command.commands.Friend;
import me.fluffycq.icehack.command.commands.Help;
import me.fluffycq.icehack.message.Messages;

public class CommandManager {

    public ArrayList cmds = new ArrayList();

    public CommandManager() {
        this.cmds.add(new Friend());
        this.cmds.add(new Help());
    }

    public void handleCMD(String msg) {
        ArrayList args = new ArrayList();
        String[] iscmd = msg.split(" ");
        int i = iscmd.length;

        for (int c = 0; c < i; ++c) {
            String arg = iscmd[c];

            args.add(arg);
        }

        if (args.get(0) == null) {
            args.add(msg);
        }

        boolean flag = false;
        Iterator iterator = this.cmds.iterator();

        while (iterator.hasNext()) {
            Command command = (Command) iterator.next();

            if (((String) args.get(0)).equalsIgnoreCase(command.getPre()) || command.aliases.contains(((String) args.get(0)).toLowerCase())) {
                command.handleCommand((String) args.get(0), args);
                flag = true;
            }
        }

        if (!flag) {
            Messages.sendChatMessage("&cCommand not found. try \'help\' for help.");
        }

    }
}
