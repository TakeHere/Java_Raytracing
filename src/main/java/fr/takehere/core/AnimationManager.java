package fr.takehere.core;

import fr.takehere.core.utils.MathUtils;
import fr.takehere.core.utils.Time;
import fr.takehere.core.utils.Vector3;
import imgui.internal.ImGui;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimationManager {

    private ArrayList<Keyframe> keyframes;
    private int amountOfFramesPerKeyframe;

    // Indexes
    private int keyframePlayingIndex = 0;
    private int actualFrameIndex = 0;

    // Starting settings
    private int startingCameraFov;
    private int startingRenderResolution;

    // Camera settings
    private int cameraFov;
    private int renderResolution;

    private static AnimationManager instance;

    public void initialize(ArrayList<Keyframe> keyframes, int amountOfFramesPerKeyframe, int cameraFov, int renderResolution){
        this.keyframes = keyframes;
        this.amountOfFramesPerKeyframe = amountOfFramesPerKeyframe;
        this.cameraFov = cameraFov;
        this.renderResolution = renderResolution;
    }

    public void recordAnimation(){
        startingRenderResolution = Math.round((float) 1.0/Scene.get().getBlockSize());
        startingCameraFov = Consts.FOV;

        Scene.get().setBlockSize((float) (1.0/renderResolution));
        Consts.FOV = cameraFov;

        animate();
    }

    private void animate(){
        if (keyframePlayingIndex+1 < keyframes.size()){
            Keyframe currentKeyframe = keyframes.get(keyframePlayingIndex);
            Keyframe nextKeyFrame = keyframes.get(keyframePlayingIndex+1);

            if (actualFrameIndex >= amountOfFramesPerKeyframe){
                //Rendered all frames of that keyframe, going to next keyframe
                actualFrameIndex = 0;
                keyframePlayingIndex++;

                animate();
            }else{
                float t = (float) actualFrameIndex/amountOfFramesPerKeyframe;

                Camera camera = Camera.get();

                camera.setPosition(Vector3.lerp(currentKeyframe.getPosition(), nextKeyFrame.getPosition(), t));
                camera.setYaw(MathUtils.lerp(currentKeyframe.getYaw(), nextKeyFrame.getYaw(), t));
                camera.setPitch(MathUtils.lerp(currentKeyframe.getPitch(), nextKeyFrame.getPitch(), t));

                recordFrame();
                Window.SaveFrameBuffer(String.valueOf((keyframePlayingIndex * amountOfFramesPerKeyframe) + actualFrameIndex));

                actualFrameIndex++;
                animate();
            }
        }

        // Animation ended, putting back starting settings
        Scene.get().setBlockSize((float) (1.0/startingRenderResolution));
        Consts.FOV = startingCameraFov;
    }

    public void recordFrame(){
        Window.realtimeRender = false;
        Window.getFrameBuffer().bind();
        Scene.get().calculateScene();
        Window.getFrameBuffer().unbind();
    }

    public static AnimationManager get(){
        if (instance != null) return instance;

        instance = new AnimationManager();
        return instance;
    }
}
