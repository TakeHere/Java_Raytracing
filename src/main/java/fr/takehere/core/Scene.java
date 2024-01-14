package fr.takehere.core;

import fr.takehere.core.framebuffer.PixelData;
import fr.takehere.core.ray.Ray;
import fr.takehere.core.ray.RayHit;
import fr.takehere.core.solids.Box;
import fr.takehere.core.solids.Plane;
import fr.takehere.core.solids.Solid;
import fr.takehere.core.solids.Sphere;
import fr.takehere.core.utils.Color;
import fr.takehere.core.utils.MathUtils;
import fr.takehere.core.utils.Vector3;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;
import java.security.Key;
import java.util.ArrayList;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.opengl.GL11.*;

public class Scene {

    private static Scene instance = null;
    ArrayList<Solid> solids = new ArrayList<>();
    Vector3 light;

    Camera camera;
    float blockSize;



    private Scene() {
        camera = Camera.get();
        light = new Vector3(100, 100, -100);


        int gridSize = 5;
        int gridSizeY = 3;
        int spacing = 5;
        float sphereRadius = 5.0f;

        for (int x = 0; x < gridSize; x++) {
            for (int y = 0; y < gridSizeY; y++) {
                for (int z = 0; z < gridSize; z++) {
                    float xPos = x * (sphereRadius * 2 + spacing);
                    float yPos = y * (sphereRadius * 2 + spacing) + sphereRadius/2.0f + spacing/2.0f;
                    float zPos = z * (sphereRadius * 2 + spacing);
                    solids.add(new Sphere(new Vector3(xPos, yPos, zPos), new Color(0.0f, 0.0f, 0.0f), 1.0f, sphereRadius));
                }
            }
        }

        blockSize = (float) 1/Consts.STARTING_RESOLUTION;
    }

    public void calculateScene(){
        int rayIndex = 0;
        // Prevent widening of the screen when window is wider than higher
        float widthFovFactor = (float) Window.getWidth()/Window.getHeight();

        for (float u = -1; u<1.0; u+=blockSize) {
            for (float v = -1; v<1.0; v+=blockSize) {
                rayIndex++;
                displayRenderingProgress(rayIndex, blockSize);

                Vector3 eyePos = new Vector3(0,0, (float)(-1/Math.tan(Math.toRadians((float) Consts.FOV/2))));
                Vector3 rayDir = new Vector3(u * widthFovFactor, v, 0).sub(eyePos).rotateYP(camera.getYaw(), camera.getPitch()).normalize();

                Ray ray = new Ray(eyePos.add(camera.getPosition()), rayDir);

                RayHit rayHit = castRay(ray, true, null);

                if (rayHit != null){
                    Solid solid = rayHit.getHitSolid();
                    Vector3 pos = rayHit.getHitPosition();

                    float diffuseFactor = calculateDiffuse(light, pos, solid);
                    float shadowFactor = calculateShadow(pos, solid);
                    float specularFactor = calculateSpecular(rayHit, light);

                    Color finalColor = calculateReflectivity(rayHit, Consts.MAX_REFLECTION_BOUNCES).mul(shadowFactor).mul(diffuseFactor).add(specularFactor);

                    PixelData pixelData = new PixelData(u, v, finalColor);
                    drawPixel(pixelData);
                }else{
                    //Touched no solids, need to display skybox
                    PixelData pixelData = new PixelData(u, v, getSkyboxColorAt(rayDir));
                    drawPixel(pixelData);
                }
            }
        }

        if (!Window.realtimeRender) Window.changeTitle(Consts.TITLE);
    }

    public void drawPixel(PixelData pixelData){
        Color pixelColor = pixelData.getColor();
        float u = pixelData.getU();
        float v = pixelData.getV();

        glBegin(GL_QUADS);
        glColor3f(pixelColor.getRed(), pixelColor.getGreen(), pixelColor.getBlue());

        glVertex2f(u, v);
        glVertex2f(u + blockSize, v);
        glVertex2f(u + blockSize, v + blockSize);
        glVertex2f(u, v + blockSize);

        glEnd();
    }



    public RayHit castRay(Ray ray, boolean getClosest, Solid ignoreSolid){
        Solid closestSolid = null;
        Vector3 closestHit = new Vector3(0,0,0);
        double closestHitDistance = Double.MAX_VALUE;

        for (Solid solid : solids) {
            if (!solid.equals(ignoreSolid)){
                Vector3 pos = solid.searchIntersection(ray);
                if (pos != null){

                    if (getClosest){
                        double distance = pos.sub(ray.getOrigin()).magnitude();
                        if (distance < closestHitDistance){
                            closestHitDistance = distance;
                            closestHit = pos;
                            closestSolid = solid;
                        }
                    }else {
                        return new RayHit(pos, solid, ray);
                    }
                }
            }
        }

        if (getClosest){
            if (closestSolid != null){
                return new RayHit(closestHit, closestSolid, ray);
            }
        }

        // Touched no solids, return
        return null;
    }

    public Color calculateReflectivity(RayHit hit, int recursionLimit){
        Ray incommingRay = hit.getRay();
        Vector3 incommingRayDirection = incommingRay.getRayDirection();
        Vector3 solidNormal = hit.getHitSolid().getNormalAt(hit.getHitPosition());

        Vector3 reflectedVector = incommingRayDirection.sub(solidNormal.mul(2 * Vector3.dot(incommingRayDirection, solidNormal)));
        RayHit reflectionHit = recursionLimit > 0 ? castRay(new Ray(hit.getHitPosition(), reflectedVector), true, hit.getHitSolid()) : null;

        Color reflection;

        if (reflectionHit != null) {
            reflection = calculateReflectivity(reflectionHit, recursionLimit-1);
        } else {
            Color sbColor = getSkyboxColorAt(reflectedVector);
            reflection = sbColor;
        }

        Color finalColor = Color.lerp(hit.getHitSolid().getColor(hit.getHitPosition()), reflection, hit.getHitSolid().getReflectivity());

        Vector3 pos = hit.getHitPosition();
        Solid solid = hit.getHitSolid();

        float diffuseFactor = calculateDiffuse(light, pos, solid);
        float shadowFactor = calculateShadow(pos, solid);
        float specularFactor = calculateSpecular(hit, light);

        finalColor = finalColor.mul(shadowFactor).mul(diffuseFactor).add(specularFactor);

        return finalColor;
    }

    public float calculateShadow(Vector3 pos, Solid solid){
        float shadowFactor = 1.0f;
        Ray shadowRay = new Ray(pos, light.sub(pos).normalize());
        if (castRay(shadowRay, false, solid) != null) shadowFactor = Consts.SHADOW_BRIGHTNESS_FACTOR + (1 - Consts.SHADOW_BRIGHTNESS_FACTOR) * solid.getReflectivity();

        return shadowFactor;
    }

    public float calculateDiffuse(Vector3 light, Vector3 pos, Solid solid){
        Vector3 lightToIntersection = pos.sub(light).normalize();
        float dot = Vector3.dot(lightToIntersection, solid.getNormalAt(pos));
        //Not under 1
        dot = Math.max(0, dot);
        //Invert dot product
        dot = (float) 1-dot;

        return dot;
    }

    public float calculateSpecular(RayHit hit, Vector3 light){
        Vector3 lightToSolidVector = hit.getHitPosition().sub(light).normalize();
        Vector3 solidNormal = hit.getHitSolid().getNormalAt(hit.getHitPosition());

        Vector3 reflectedLight = lightToSolidVector.sub(solidNormal.mul(2 * Vector3.dot(lightToSolidVector, solidNormal)));

        float specularFactor = MathUtils.clamp(Vector3.dot(reflectedLight, Camera.get().getForward()), 0, 1);
        return (float) Math.pow(specularFactor, Consts.SPECULAR_POWER) * hit.getHitSolid().getReflectivity();
    }

    public Color getSkyboxColorAt(Vector3 rayDir){
        float skyboxU = (float) (0.5+Math.atan2(rayDir.z, rayDir.x)/(2*Math.PI));
        float skyboxV = (float) (0.5 - Math.asin(rayDir.y)/Math.PI);

        BufferedImage skybox = Window.getSkybox();

        int clr = skybox.getRGB((int) (skyboxU * (skybox.getWidth()-1)), (int) (skyboxV * (skybox.getHeight()-1)));

        float red =   (float) ((clr & 0x00ff0000) >> 16) / 255;
        float green = (float) ((clr & 0x0000ff00) >> 8) / 255;
        float blue =  (float) (clr & 0x000000ff) / 255;

        return new Color(red, green, blue);
    }

    public void displayRenderingProgress(int rayIndex, float blockSize){
        if (!Window.realtimeRender){
            int totalRays = (int) Math.round(Math.pow(2.0 / blockSize, 2));
            rayIndex = Math.min(totalRays, rayIndex);
            double progressPercentage = ((double) rayIndex/totalRays) * 100;

            if (rayIndex % (totalRays / 100) == 0){
                Window.changeTitle(Consts.TITLE + " | Render: " + Math.round(progressPercentage) + "%" + " | " + Math.round(rayIndex) + "/" + totalRays + " rays");
            }
        }
    }

    public static Scene get(){
        if (instance == null){
            Scene.instance = new Scene();
        }
        return instance;
    }

    public float getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(float blockSize) {
        this.blockSize = blockSize;
    }
}
