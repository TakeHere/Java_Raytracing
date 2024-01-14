package fr.takehere.core;

import fr.takehere.core.listeners.KeyListener;
import fr.takehere.core.listeners.MouseListener;
import fr.takehere.core.utils.MathUtils;
import fr.takehere.core.utils.Vector3;
import org.lwjgl.glfw.GLFW;

public class Camera {
    private static Camera instance = null;

    private float yaw;
    private float pitch;
    private Vector3 position;

    private Camera() {
        this.yaw = 0.0f;
        this.pitch = 0.0f;
        this.position = new Vector3(0,0,0);
    }

    public void movementUpdate(float dt){
        float movementSpeed = Consts.MOVEMENT_SPEED * dt;

        Vector3 forwardVector = getForward();
        Vector3 rightVector = getRight();
        Vector3 upperVector = new Vector3(0, 1, 0);

        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_W)){
            this.position = this.position.sub(forwardVector.mul(movementSpeed));
        }else if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_S)){
            this.position = this.position.add(forwardVector.mul(movementSpeed));
        }

        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_A)){
            this.position = this.position.sub(rightVector.mul(movementSpeed));
        }else if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_D)){
            this.position = this.position.add(rightVector.mul(movementSpeed));
        }

        if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_E)){
            this.position = this.position.add(upperVector.mul(movementSpeed));
        }else if (KeyListener.isKeyPressed(GLFW.GLFW_KEY_Q)){
            this.position = this.position.sub(upperVector.mul(movementSpeed));
        }

        if (Window.isMouseLocked()){
            this.pitch += MouseListener.getMouseDeltaY() * Consts.MOUSE_SENSITIVITY;
            this.yaw += MouseListener.getMouseDeltaX() * Consts.MOUSE_SENSITIVITY;

            this.pitch = MathUtils.clamp(this.pitch, -90, 90);
        }
    }

    public Vector3 getForward() {
        float x = (float) (Math.cos(Math.toRadians(yaw + 90)) * Math.cos(Math.toRadians(pitch)));
        float y = (float) (Math.sin(Math.toRadians(pitch)));
        float z = (float) (Math.sin(Math.toRadians(yaw + 90)) * Math.cos(Math.toRadians(pitch)));

        return new Vector3(x, y, -z).normalize();
    }

    public Vector3 getRight() {
        float x = (float) (Math.cos(Math.toRadians(yaw)));
        float y = 0;
        float z = (float) (-Math.sin(Math.toRadians(yaw)));

        return new Vector3(x, y, z).normalize();
    }

    public static Camera get(){
        if (instance == null){
            Camera.instance = new Camera();
        }
        return instance;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }
}
