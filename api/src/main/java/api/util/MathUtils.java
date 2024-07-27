package api.util;

public final class MathUtils {

    public static float map(float value, float iStart, float iStop, float oStart, float oStop) {
        return oStart + (oStop - oStart) * ((value - iStart) / (iStop - iStart));
    }

    public static double map(double value, double iStart, double iStop, double oStart, double oStop) {
        return oStart + (oStop - oStart) * ((value - iStart) / (iStop - iStart));
    }

    //try the map T: [a,b] -> [c,d], T(n) = (d-c)/(b-a) n - ((d-c)/(b-a) a) + c
    public static float remap(float n, float a, float b, float c, float d) {
        return (d - c) / (b - a) * n - ((d - c) / (b - a) * a) + c;
    }

    public static float sq(float value) {
        return value * value;
    }

    public static double sq(double value) {
        return value * value;
    }

    public static float inverseLerp(float a, float b, float value) {
        if (a != b) {
            return clamp01((value - a) / (b - a));
        } else {
            return 0.0f;
        }
    }

    public static float pow(float n, float e) {
        return (float) Math.pow(n, e);
    }

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    public static float clamp01(float val) {
        return Math.max(0, Math.min(1, val));
    }

    public static float lerp(float a, float b, float t) {
        return a + (b - a) * clamp01(t);
    }


    public static float floatMod(float a, float b) {
        return ((a % b) + b) % b;
    }

    public static double floatMod(double a, double b) {
        return ((a % b) + b) % b;
    }

    public static int blend(int rgb1, int rgb2, float factor) {
        if (factor <= 0f) {
            return rgb1;
        }

        if (factor >= 1f) {
            return rgb2;
        }

        int r1 = (rgb1 >> 16) & 0xff;
        int g1 = (rgb1 >> 8) & 0xff;
        int b1 = (rgb1) & 0xff;

        int r2 = (rgb2 >> 16) & 0xff;
        int g2 = (rgb2 >> 8) & 0xff;
        int b2 = (rgb2) & 0xff;

        int r3 = r2 - r1;
        int g3 = g2 - g1;
        int b3 = b2 - b1;

        int r = (int) (r1 + (r3 * factor));
        int g = (int) (g1 + (g3 * factor));
        int b = (int) (b1 + (b3 * factor));

        return (r << 16) + (g << 8) + b;
    }
}
