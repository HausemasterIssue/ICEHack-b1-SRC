package me.fluffycq.icehack.message;

import net.minecraft.client.Minecraft;

public class Messages {

    public static void sendChatMessage(String message) {
        sendRawChatMessage("&b[ICEHack] &r" + message);
    }

    public static void sendMessage(String message) {
        sendRawChatMessage(message);
    }

    public static void sendStringChatMessage(String[] messages) {
        sendChatMessage("");
        String[] astring = messages;
        int i = messages.length;

        for (int j = 0; j < i; ++j) {
            String s = astring[j];

            sendRawChatMessage(s);
        }

    }

    public static void sendRawChatMessage(String message) {
        Minecraft.getMinecraft().player.sendMessage(new ChatMessage(message));
    }
}
