package me.fluffycq.icehack.friends;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.client.Minecraft;

public class Friends {

    public static ArrayList tempFriends = new ArrayList();

    public Friends() {
        this.loadFriends();
    }

    public void createFriends() {
        File dir1 = new File(Minecraft.getMinecraft().gameDir.getAbsolutePath(), "\\ICEHack\\friends\\");

        if (!dir1.exists()) {
            dir1.mkdir();
        }

        File file = new File(Minecraft.getMinecraft().gameDir.getAbsolutePath(), "\\ICEHack\\friends\\friendlist.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }

    }

    public void saveFriends() {
        BufferedWriter writer = null;
        File file = new File(Minecraft.getMinecraft().gameDir.getAbsolutePath(), "\\ICEHack\\friends\\friendlist.txt");

        try {
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }

        if (writer != null) {
            try {
                Iterator e = Friends.tempFriends.iterator();

                while (e.hasNext()) {
                    String s = (String) e.next();

                    writer.write(s + "\r\n");
                }
            } catch (IOException ioexception1) {
                ioexception1.printStackTrace();
            }
        } else {
            System.out.println("[icehack] Could not add friend!");
        }

        try {
            writer.close();
        } catch (IOException ioexception2) {
            ioexception2.printStackTrace();
        }

    }

    public void addFriend(String username) {
        if (!isFriend(username.toLowerCase())) {
            Friends.tempFriends.add(username.toLowerCase());
        }

    }

    public void removeFriend(String username) {
        if (isFriend(username)) {
            Friends.tempFriends.remove(username.toLowerCase());
        }

    }

    public static boolean isFriend(String name) {
        return Friends.tempFriends.contains(name.toLowerCase());
    }

    public void loadFriends() {
        try {
            BufferedReader e = new BufferedReader(new FileReader(new File(Minecraft.getMinecraft().gameDir.getAbsolutePath(), "\\ICEHack\\friends\\friendlist.txt")));
            Throwable throwable = null;

            try {
                String line;

                try {
                    while ((line = e.readLine()) != null) {
                        Friends.tempFriends.add(line.toLowerCase());
                    }
                } catch (Throwable throwable1) {
                    throwable = throwable1;
                    throw throwable1;
                }
            } finally {
                if (e != null) {
                    if (throwable != null) {
                        try {
                            e.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        e.close();
                    }
                }

            }
        } catch (FileNotFoundException filenotfoundexception) {
            filenotfoundexception.printStackTrace();
        } catch (IOException ioexception) {
            ioexception.printStackTrace();
        }

    }
}
