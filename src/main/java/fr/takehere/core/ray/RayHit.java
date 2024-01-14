package fr.takehere.core.ray;

import fr.takehere.core.solids.Solid;
import fr.takehere.core.utils.Vector3;

public final class RayHit {

    private final Vector3 hitPosition;
    private final Solid hitSolid;
    private final Ray ray;

    public RayHit(Vector3 hitPosition, Solid hitSolid, Ray ray) {
        this.hitPosition = hitPosition;
        this.hitSolid = hitSolid;
        this.ray = ray;
    }

    public Vector3 getHitPosition() {
        return hitPosition;
    }

    public Solid getHitSolid() {
        return hitSolid;
    }

    public Ray getRay() {
        return ray;
    }
}
