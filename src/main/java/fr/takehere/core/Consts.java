package fr.takehere.core;

import fr.takehere.core.utils.Color;

import java.util.ArrayList;

public class Consts {

    // Window consts
    public static final int WIDTH = 1000;
    public static final int HEIGHT = 1000;
    public static final String TITLE = "Java Raytracing";

    // Gui consts
    public static final boolean SHOW_GUI = true;

    // Renderer consts
    public static int FOV = 90;
    public static final int STARTING_RESOLUTION = 50;
    public static final int MAX_REFLECTION_BOUNCES = 5;
    public static final float SHADOW_BRIGHTNESS_FACTOR = 0.3f;
    public static final float SPECULAR_POWER = 16.0f;
    public static final String[] SKYBOXES = {"sky.jpg", "studio.jpg", "beach.jpg"};
    public static final String STARTING_SKYBOX = "sky.jpg";

    //Checkerbox colors
    public static final Color GRAY = new Color(0.5f, 0.5f, 0.5f);
    public static final Color DARK_GRAY = new Color(0.2f, 0.2f, 0.2f);

    //Camera movement consts
    public static final float MOVEMENT_SPEED = 10.0f;
    public static final float MOUSE_SENSITIVITY = 1.0f;

}
