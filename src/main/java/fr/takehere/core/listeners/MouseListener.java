package fr.takehere.core.listeners;

import imgui.internal.ImGui;

import java.util.HashMap;
import java.util.Map;

public class MouseListener {
    private static MouseListener instance;

    private double scrollX, scrollY;

    public static Map<Integer, Runnable> pressEvents = new HashMap<>();
    public static Map<Integer, Runnable> releaseEvents = new HashMap<>();

    private MouseListener(){}

    public static MouseListener get(){
        if (instance == null){
            instance = new MouseListener();
        }
        return instance;
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }


    public static boolean mouseButtonDown(int button){
        return ImGui.getIO().getMouseDown(button);
    }

    public static void addPressEvent(int mouseBtn, Runnable task){
        pressEvents.put(mouseBtn, task);
    }

    public static void addReleaseEvent(int mouseBtn, Runnable task){
        releaseEvents.put(mouseBtn, task);
    }

    public static float getScrollX() { return (float)get().scrollX; }

    public static float getScrollY() {
        return (float)get().scrollY;
    }

    public static float getMouseDeltaX(){
        return ImGui.getIO().getMouseDeltaX();
    }

    public static float getMouseDeltaY(){
        return ImGui.getIO().getMouseDeltaY();
    }
}