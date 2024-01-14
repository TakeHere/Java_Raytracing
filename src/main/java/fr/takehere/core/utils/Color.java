package fr.takehere.core.utils;

import java.util.Random;

public class Color {

    private float red;
    private float green;
    private float blue;
    private static Random rdm = new Random();

    public Color(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color mul(float factor){
        return new Color(this.red * factor, this.green * factor, this.blue * factor);
    }

    public static Color random(){
        return new Color(rdm.nextFloat(), rdm.nextFloat(), rdm.nextFloat());
    }

    public Color average(Color color) {
        float avgRed = (float) ((this.getRed() + color.getRed()) / 2.0);
        float avgGreen = (float) ((this.getGreen() + color.getGreen()) / 2.0);
        float avgBlue = (float) ((this.getBlue() + color.getBlue()) / 2.0);

        return new Color(avgRed, avgGreen, avgBlue);
    }

    public Color lerp(Color color, float t) {
        float lerpRed = this.getRed() * (1 - t) + color.getRed() * t;
        float lerpGreen = this.getGreen() * (1 - t) + color.getGreen() * t;
        float lerpBlue = this.getBlue() * (1 - t) + color.getBlue() * t;

        return new Color(lerpRed, lerpGreen, lerpBlue);
    }

    public Color add(float term){
        return new Color(this.red + term, this.green + term, this.blue + term);
    }

    public static Color lerp(Color a, Color b, float t) {
        float lerpRed = a.getRed() * (1 - t) + b.getRed() * t;
        float lerpGreen = a.getGreen() * (1 - t) + b.getGreen() * t;
        float lerpBlue = a.getBlue() * (1 - t) + b.getBlue() * t;

        return new Color(lerpRed, lerpGreen, lerpBlue);
    }

    public float getRed() {
        return red;
    }

    public void setRed(float red) {
        this.red = red;
    }

    public float getGreen() {
        return green;
    }

    public void setGreen(float green) {
        this.green = green;
    }

    public float getBlue() {
        return blue;
    }

    public void setBlue(float blue) {
        this.blue = blue;
    }
}
