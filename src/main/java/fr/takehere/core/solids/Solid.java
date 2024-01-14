package fr.takehere.core.solids;

import fr.takehere.core.ray.Ray;
import fr.takehere.core.utils.Vector3;
import fr.takehere.core.utils.Color;

public abstract class Solid {

    protected Vector3 position;
    protected Color color;
    protected float reflectivity;

    public Solid(Vector3 position, Color color, float reflectivity){
        this.position = position;
        this.color = color;
        this.reflectivity = reflectivity;
    }

    public abstract Vector3 searchIntersection(Ray ray);

    public abstract Vector3 getNormalAt(Vector3 point);

    public Color getColor(Vector3 point) {
        return color;
    }

    public Vector3 getPosition() {
        return position;
    }

    public float getReflectivity() {
        return reflectivity;
    }
}
