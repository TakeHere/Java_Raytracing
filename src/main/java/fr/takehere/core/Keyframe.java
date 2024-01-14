package fr.takehere.core;

import fr.takehere.core.utils.Vector3;

public class Keyframe {

    private Vector3 position;

    private float yaw;
    private float pitch;

    public Keyframe(Vector3 position, float yaw, float pitch) {
        this.position = position;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Vector3 getPosition() {
        return position;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
