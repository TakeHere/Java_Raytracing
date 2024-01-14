package fr.takehere.core.solids;

import fr.takehere.core.ray.Ray;
import fr.takehere.core.utils.Color;
import fr.takehere.core.utils.Vector3;

public class Sphere extends Solid{

    private float radius;

    public Sphere(Vector3 position, Color color, float reflectivity, float radius) {
        super(position, color, reflectivity);
        this.radius = radius;
    }


    @Override
    public Vector3 searchIntersection(Ray ray) {
        Vector3 rayOrigin = ray.getOrigin();
        Vector3 rayDirection = ray.getRayDirection();

        Vector3 oc = rayOrigin.sub(position); // Center of sphere to ray origin
        float a = rayDirection.dot(rayDirection);
        float b = 2.0f * oc.dot(rayDirection);
        float c = oc.dot(oc) - radius * radius;
        float discriminant = b * b - 4 * a * c;

        if (discriminant > 0) {
            float t1 = (-b - (float)Math.sqrt(discriminant)) / (2.0f * a);
            float t2 = (-b + (float)Math.sqrt(discriminant)) / (2.0f * a);

            if (t1 > 0 || t2 > 0) {
                float t = (t1 > 0) ? t1 : t2;
                Vector3 intersectionPoint = rayOrigin.add(rayDirection.mul(t));
                return intersectionPoint; // Ray intersects the sphere
            }
        }

        return null; // No intersection
    }

    @Override
    public Vector3 getNormalAt(Vector3 point) {
        return point.sub(position).normalize();
    }
}
