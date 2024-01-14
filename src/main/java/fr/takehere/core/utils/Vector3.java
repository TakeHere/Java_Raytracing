package fr.takehere.core.utils;

import java.awt.*;

public class Vector3 {

    public float x;
    public float y;
    public float z;

    public static final Vector3 ZERO = new Vector3(0,0, 0);

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double magnitude(){
        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2) + Math.pow(z,2));
    }

    public Vector3 normalize(){
        double magnitude = magnitude();
        return new Vector3((float) (x/magnitude), (float) (y/magnitude), (float) (z/magnitude));
    }

    public Vector3 add(Vector3 vector){
        return new Vector3(this.x + vector.x, this.y + vector.y, this.z + vector.z);
    }

    public Vector3 sub(Vector3 term) {
        return new Vector3(this.x - term.x, this.y - term.y, this.z - term.z);
    }

    public Vector3 mul(double factor){
        return new Vector3((float) (this.x * factor), (float) (this.y * factor), (float) (this.z * factor));
    }

    public Vector3 mul(Vector3 factor){
        return new Vector3(this.x * factor.x, this.y * factor.y, this.z * factor.z);
    }

    public float[] toArray(){
        return new float[]{x, y, z};
    }

    public Vector3 divide(double divisor){
        return new Vector3((float) (this.x / divisor), (float) (this.y / divisor), (float) (this.z / divisor));
    }

    public float dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }

    public static float dot(Vector3 first, Vector3 other) {
        return first.x * other.x + first.y * other.y + first.z * other.z;
    }

    public static Vector3 lerp(Vector3 start, Vector3 end, float t) {
        t = Math.max(0, Math.min(1, t)); // Clamp t between 0 and 1
        float lerpX = start.x + (end.x - start.x) * t;
        float lerpY = start.y + (end.y - start.y) * t;
        float lerpZ = start.z + (end.z - start.z) * t;
        return new Vector3(lerpX, lerpY, lerpZ);
    }

    public Vector3 rotateYP(float yaw, float pitch) {
        // Convert to radians
        double yawRads = Math.toRadians(yaw);
        double pitchRads = Math.toRadians(pitch);

        // Step one: Rotate around X axis (pitch)
        float _y = (float) (y*Math.cos(pitchRads) - z*Math.sin(pitchRads));
        float _z = (float) (y*Math.sin(pitchRads) + z*Math.cos(pitchRads));

        // Step two: Rotate around the Y axis (yaw)
        float _x = (float) (x*Math.cos(yawRads) + _z*Math.sin(yawRads));
        _z = (float) (-x*Math.sin(yawRads) + _z*Math.cos(yawRads));

        return new Vector3(_x, _y, _z);
    }

    public void log(){
        System.out.println("[x: " + x + "] [y: " + y + "] [z: " + z + "] [magnitude: " + magnitude() + "]");
    }
}
