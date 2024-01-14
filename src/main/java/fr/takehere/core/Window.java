package fr.takehere.core;

import fr.takehere.core.gui.ImGuiLayer;
import fr.takehere.core.listeners.KeyListener;
import fr.takehere.core.listeners.MouseListener;
import fr.takehere.core.utils.Time;
import imgui.internal.ImGui;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import javax.imageio.ImageIO;

import static org.lwjgl.opengl.GL30.*;

public class Window {
    private static Window window = null;
    private static int width;
    private static int height;

    private ImGuiLayer imGuiLayer;
    private String title = Consts.TITLE;

    private static boolean mouseLocked = false;
    public static boolean realtimeRender = true;
    private static FrameBuffer frameBuffer;
    private static long windowHandle;

    private static HashMap<String, BufferedImage> availableSkyboxes = new HashMap<>();
    private static BufferedImage skybox;

    private Window(){
        width = Consts.WIDTH;
        height = Consts.HEIGHT;
        init();
    }

    public static Window get(){
        if (window == null){
            Window.window = new Window();
        }
        return window;
    }

    private void init(){
        for (String skybox : Consts.SKYBOXES) {
            addSkybox(skybox);
        }
        changeSkybox(Consts.STARTING_SKYBOX);

        GLFWErrorCallback.createPrint(System.err).set();

        // Initialisation de GLFW
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Impossible d'initialiser GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Configuration GLFW
        long glfwindow = GLFW.glfwCreateWindow(width, height, title, 0, 0);
        if (glfwindow == 0) {
            throw new IllegalStateException("Impossible de créer la fenêtre GLFW");
        }

        windowHandle = glfwindow;

        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        GLFW.glfwSetWindowPos(glfwindow, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);

        // Créer le contexte OpenGL
        GLFW.glfwMakeContextCurrent(glfwindow);
        GL.createCapabilities();

        // Enable v-sync
        glfwSwapInterval(1);

        glfwSetWindowSizeCallback(glfwindow, Window::windowResizeCallback);

        this.imGuiLayer = new ImGuiLayer(glfwindow);
        imGuiLayer.initImGui();

        frameBuffer = new FrameBuffer(width, height);


        int fps = 0;
        long lastTime = System.currentTimeMillis();

        float beginTime = Time.getTime();
        float endTime = Time.getTime();
        float dt = -1.0f;

        while (!glfwWindowShouldClose(glfwindow)) {
            glfwPollEvents();

            if (MouseListener.mouseButtonDown(0)){
                if (!ImGui.getIO().getWantCaptureMouse()){
                    glfwSetInputMode(glfwindow, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
                    mouseLocked = true;
                }
            }

            if (KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)){
                glfwSetInputMode(glfwindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
                mouseLocked = false;
            }


            if (dt >= 0){
                glClear(GL_COLOR_BUFFER_BIT);

                if (!realtimeRender) frameBuffer.bind();

                if (realtimeRender){
                    Camera.get().movementUpdate(dt);
                    Scene.get().calculateScene();
                }


                if (!realtimeRender){
                    glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBuffer.getFboID());
                    glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
                    glBlitFramebuffer(0, 0, width, height, 0, 0, width, height,
                            GL_COLOR_BUFFER_BIT, GL_NEAREST);

                    frameBuffer.unbind();
                }
            }


            if (dt == 0) dt = 0.001f;
            this.imGuiLayer.update(dt);

            glfwSwapBuffers(glfwindow);

            //--------< FPS/Deltatime Management>--------
            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;

            //--------< Display FPS >--------
            fps++;
            if (System.currentTimeMillis() - lastTime > 1000){
                if (realtimeRender) glfwSetWindowTitle(glfwindow, title + " | fps: " + fps);
                lastTime += 1000;
                fps = 0;
            }
        }

        // Nettoyage
        glfwFreeCallbacks(glfwindow);
        glfwDestroyWindow(glfwindow);

        imGuiLayer.destroyImGui();
        glfwTerminate();
        System.exit(0);
    }

    public static float[] getNormalizedCoordinates(int width, int height, double x, double y) {
        float normalizedX = (float) ((2.0 * x) / width - 1.0);
        float normalizedY = (float) (1.0 - (2.0 * y) / height);

        return new float[]{normalizedX, normalizedY};
    }

    public void addSkybox(String path){
        try {
            availableSkyboxes.put(path, ImageIO.read(getClass().getClassLoader().getResourceAsStream(path)));
        } catch (IOException e) {
            System.out.println(path + " is an invalid path for a skybox.");
            System.exit(1);
            throw new RuntimeException(e);
        }
    }

    private static void windowResizeCallback(long window, int width, int height) {
        Window.width = width;
        Window.height = height;
        frameBuffer = new FrameBuffer(width, height);
        glViewport(0, 0, width, height);
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    public static void changeTitle(String title){
        glfwSetWindowTitle(Window.windowHandle, title);
    }

    public static boolean isMouseLocked() {
        return mouseLocked;
    }

    public static FrameBuffer getFrameBuffer() {
        return frameBuffer;
    }

    public static BufferedImage getSkybox() {
        return skybox;
    }

    public static void SaveFrameBuffer(String filename) {
        Window.getFrameBuffer().bind();
        GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);

        int width = Window.getWidth();
        int height= Window.getHeight();
        int bpp = 4;
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer );

        File file = new File(filename + ".png");
        String format = "png";
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < width; x++)
        {
            for(int y = 0; y < height; y++)
            {
                int i = (x + (width * y)) * bpp;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }

        try {
            ImageIO.write(image, format, file);
        } catch (IOException e) { e.printStackTrace(); }

        Window.getFrameBuffer().unbind();
    }

    public static HashMap<String, BufferedImage> getAvailableSkyboxes() {
        return availableSkyboxes;
    }

    public static void changeSkybox(String path){
        BufferedImage newSkybox = availableSkyboxes.get(path);

        if (newSkybox != null){
            skybox = newSkybox;
        }else {
            System.err.println("No skybox was registered with this path.");
            System.exit(-1);
        }
    }
}
