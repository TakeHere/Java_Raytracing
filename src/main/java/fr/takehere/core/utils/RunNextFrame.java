package fr.takehere.core.utils;

import java.util.ArrayList;
import java.util.List;

public class RunNextFrame {

    private static List<Runnable> runNextFrame = new ArrayList<>();

    public static void runNextFrame(Runnable runnable){
        runNextFrame.add(runnable);
    }

    public static void clear(){
        runNextFrame.clear();
    }

    public static List<Runnable> getRunNextFrame() {
        return runNextFrame;
    }
}
