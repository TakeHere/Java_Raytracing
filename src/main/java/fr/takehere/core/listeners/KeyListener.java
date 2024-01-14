package fr.takehere.core.listeners;

import fr.takehere.core.Window;
import imgui.ImGui;

import java.util.HashMap;
import java.util.Map;

public class KeyListener {
    private static KeyListener instance;

    public static Map<Integer, Runnable> pressEvents = new HashMap<>();
    public static Map<Integer, Runnable> releaseEvents = new HashMap<>();

    private KeyListener() {}

    public static KeyListener get() {
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }

        return KeyListener.instance;
    }

    public static boolean isKeyPressed(int keyCode) {
        if (Window.isMouseLocked()){
            return ImGui.getIO().getKeysDown(keyCode);
        }
        return false;
    }

    public static void addPressEvent(int key, Runnable task){
        pressEvents.put(key, task);
    }

    public static void addReleaseEvent(int key, Runnable task){
        releaseEvents.put(key, task);
    }
}
