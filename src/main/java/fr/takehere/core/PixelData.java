package fr.takehere.core.framebuffer;

import fr.takehere.core.utils.Color;

public class PixelData {

    private final float u;
    private final float v;
    private final Color color;

    public PixelData(float u, float v, Color color) {
        this.u = u;
        this.v = v;
        this.color = color;
    }

    public float getU() {
        return u;
    }

    public float getV() {
        return v;
    }

    public Color getColor() {
        return color;
    }
}
