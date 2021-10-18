package me.fluffycq.icehack.command.commands;

import java.util.Iterator;
import java.util.List;
import me.fluffycq.icehack.ICEHack;
import me.fluffycq.icehack.command.Command;
import me.fluffycq.icehack.message.Messages;

public class Help extends Command {

    public Help() {
        this.cmd = "help";
        this.desc = "View a list of commands.";
    }

    public void handleCommand(String msg, List args) {
        Messages.sendChatMessage("List of available commands in ICEHack:");
        Iterator iterator = ICEHack.cmdmanager.cmds.iterator();

        while (iterator.hasNext()) {
            Command c = (Command) iterator.next();

            Messages.sendMessage("&a" + c.cmd + " &7- " + c.desc);
        }

    }
}
