package me.fluffycq.icehack.events;

public class KeyEvent extends ICEEvent {

    public int key;

    public KeyEvent(int pressed) {
        this.key = pressed;
    }

    public int getPressed() {
        return this.key;
    }
}
