package fr.takehere.core.ray;

import fr.takehere.core.utils.Vector3;

public class Ray {

    private final Vector3 origin;
    private final Vector3 rayDirection;

    public Ray(Vector3 origin, Vector3 rayDirection) {
        this.origin = origin;
        this.rayDirection = rayDirection;
    }

    public Vector3 getOrigin() {
        return origin;
    }

    public Vector3 getRayDirection() {
        return rayDirection;
    }
}
