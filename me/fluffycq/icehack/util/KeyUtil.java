package me.fluffycq.icehack.util;

import java.lang.reflect.Field;
import org.lwjgl.input.Keyboard;

public class KeyUtil {

    public static int getKeyCode(String keyName) {
        Class keys = Keyboard.class;
        int keyCode = -69;

        try {
            Field key = keys.getField(keyName);

            keyCode = key.getInt((Object) null);
        } catch (IllegalAccessException illegalaccessexception) {
            illegalaccessexception.printStackTrace();
        } catch (NoSuchFieldException nosuchfieldexception) {
            nosuchfieldexception.printStackTrace();
        }

        return keyCode;
    }
}
