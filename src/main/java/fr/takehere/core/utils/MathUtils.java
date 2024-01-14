package fr.takehere.core.utils;

public class MathUtils {

    public static float clamp(float value, float min, float max){
        return Math.max(min, Math.min(max, value));
    }

    public static float lerp(float start, float end, float t) {
        return start + (end - start) * t;
    }
}
