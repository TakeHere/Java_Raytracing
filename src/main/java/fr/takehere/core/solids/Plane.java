package fr.takehere.core.solids;

import fr.takehere.core.Camera;
import fr.takehere.core.Consts;
import fr.takehere.core.ray.Ray;
import fr.takehere.core.utils.Color;
import fr.takehere.core.utils.Vector2;
import fr.takehere.core.utils.Vector3;

import java.util.Random;

public class Plane extends Solid{

    private boolean gridPattern;

    public Plane(float height, Color color, float reflectivity, boolean gridPattern) {
        super(new Vector3(0, height, 0), color, reflectivity);
        this.gridPattern = gridPattern;
    }

    @Override
    public Vector3 searchIntersection(Ray ray) {
        float t = -(ray.getOrigin().y-position.y) / ray.getRayDirection().y;
        if (t > 0 && Float.isFinite(t))
        {
            return ray.getOrigin().add(ray.getRayDirection().mul(t));
        }

        return null;
    }

    @Override
    public Vector3 getNormalAt(Vector3 point) {
        return new Vector3(0, 1, 0);
    }

    @Override
    public Color getColor(Vector3 point){
        if (gridPattern){
            Color grayColor = Consts.GRAY;
            Color darkGrayColor = Consts.DARK_GRAY;

            int gridSize = 5;

            if (((point.x > 0) & (point.z > 0)) || ((point.x < 0) & (point.z < 0))) {
                int gridX = (int) (point.x / gridSize);
                int gridZ = (int) (point.z / gridSize);

                if ((gridX % 2 == 0) ^ (gridZ % 2 != 0)) {
                    return grayColor;
                } else {
                    return darkGrayColor;
                }
            } else {
                int gridX = (int) (-point.x / gridSize);
                int gridZ = (int) (-point.z / gridSize);

                if ((gridX % 2 == 0) ^ (gridZ % 2 != 0)) {
                    return darkGrayColor;
                } else {
                    return grayColor;
                }
            }
        }else return color;
    }

    public boolean isGridPattern() {
        return gridPattern;
    }
}
