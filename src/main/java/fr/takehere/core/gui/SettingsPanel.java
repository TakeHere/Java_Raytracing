package fr.takehere.core.gui;

import fr.takehere.core.*;
import fr.takehere.core.Window;
import fr.takehere.core.utils.Vector3;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiSliderFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class SettingsPanel {

    private static ImBoolean realtimeRender = new ImBoolean(true);
    private static ImInt selectedSkybox = new ImInt();

    private static ImString fileName = new ImString();

    public static void init(){
        fileName.set("filename");
    }

    public static void update(){
        if (Consts.SHOW_GUI){
            Scene scene = Scene.get();

            ImGui.setNextWindowCollapsed(true, ImGuiCond.Once);
            ImGui.begin("Render settings", new ImBoolean(true), ImGuiWindowFlags.AlwaysAutoResize);


            //Change skybox dropbox
            ImGui.labelText("", "Change skybox");

            ArrayList<String> skyboxes = new ArrayList<>();
            Window.getAvailableSkyboxes().keySet().forEach(s -> {
                skyboxes.add(s);
            });

            ImGui.combo(" ", selectedSkybox, skyboxes.toArray(new String[0]), 15);

            if (ImGui.button("Change !")){
                Window.changeSkybox(skyboxes.get(selectedSkybox.get()));
            }


            spacings(10);


            ImGui.labelText("", "Camera settings");
            //Change render resolution
            int[] resolution = new int[] {Math.round((float) 1.0/scene.getBlockSize())};
            ImGui.dragInt("Render resolution", resolution);
            if (resolution[0] > 1.0){
                scene.setBlockSize((float) (1.0/resolution[0]));
            }


            //Change camera fov
            int[] fov = new int[] {Consts.FOV};
            ImGui.dragInt("Camera FOV", fov);
            if (fov[0] > 1.0 && fov[0] < 179.0){
                Consts.FOV = fov[0];
            }

            spacings(3);

            Camera camera = Camera.get();
            Vector3 camPos = camera.getPosition();

            float[] x = new float[] {camPos.x};
            float[] y = new float[] {camPos.y};
            float[] z = new float[] {camPos.z};

            float[] pitch = new float[] {camera.getPitch()};
            float[] yaw = new float[] {camera.getYaw()};

            if(ImGui.collapsingHeader("Camera position")){
                ImGui.dragFloat("X position", x, 0.1f);
                ImGui.dragFloat("Y position", y, 0.1f);
                ImGui.dragFloat("Z position", z, 0.1f);

                camera.setPosition(new Vector3(x[0], y[0], z[0]));
            }

            if(ImGui.collapsingHeader("Camera rotation")){
                ImGui.dragFloat("Yaw", yaw, 0.1f);
                ImGui.dragFloat("Pitch", pitch, 0.1f);

                camera.setPitch(pitch[0]);
                camera.setYaw(yaw[0]);
            }



            spacings(10);       


            //Toggle realtime render
            ImGui.checkbox("Realtime render", realtimeRender);
            if (realtimeRender.get()){
                Window.realtimeRender = true;
            }else{
                Window.realtimeRender = false;
                if (ImGui.button("Render !")){
                    Window.getFrameBuffer().bind();
                    scene.calculateScene();
                    Window.getFrameBuffer().unbind();
                }
            }


            spacings(5);


            //Save render
            ImGui.inputText("Filename (no filetype)", fileName);
            if (ImGui.button("Save render (FrameBuffer)")){
                Window.SaveFrameBuffer(fileName.get());
            }

            if (ImGui.button("animate")){
                ArrayList<Keyframe> keyframes = new ArrayList<>();

                /*
                keyframes.add(new Keyframe(new Vector3(1, 5, -6), 0, 17));
                keyframes.add(new Keyframe(new Vector3(13, 5, 6), -90, 17));
                keyframes.add(new Keyframe(new Vector3(1, 5, 17), -180, 17));
                keyframes.add(new Keyframe(new Vector3(-10, 5, 6), -270, 17));
                keyframes.add(new Keyframe(new Vector3(1, 5, -6), -360, 17));
                 */

                keyframes.add(new Keyframe(new Vector3(-20, 25, -20), 45, 7));
                keyframes.add(new Keyframe(new Vector3(60, 25, -25), -30, 8));
                keyframes.add(new Keyframe(new Vector3(80, 50, 30), -90, 40));
                keyframes.add(new Keyframe(new Vector3(-10, 50, 70), -225, 30));
                keyframes.add(new Keyframe(new Vector3(-30.6f, 26.6f, 12.5f), -284, 7.9f));
                keyframes.add(new Keyframe(new Vector3(-20, 25, -20), -315, 7));




                AnimationManager animationManager = AnimationManager.get();
                animationManager.initialize(keyframes, 30, 90, 1000);

                animationManager.recordAnimation();
            }
        }
    }

    private static void spacings(int nbSpacings){
        for (int i = 0; i < nbSpacings; i++) {
            ImGui.spacing();
        }
    }
}
