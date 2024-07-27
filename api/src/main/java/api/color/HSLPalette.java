package api.color;

import one.util.streamex.IntStreamEx;

import static api.util.MathUtils.sq;

public class HSLPalette {

    public static int[] table = new int[128 * 512];

    static {
        generatePalette(0.80000000000000004D); // default is 0.8 in the engine
    }

    public static int getRgbForHsl(int hue, int saturation, int lightness) {
        int hsl = getHSL(hue, saturation, lightness);
        return getRgbForHsl(hsl);
    }

    public static int getRgbForHsl(int hsl) {
        if (hsl >= table.length) {
            return 0;
        }

        return table[hsl & 0xFFFF];
    }

    public static int findClosestHSL(int rgb) {
        double r1 = ((rgb >> 16) & 0xFF) / 255.0;
        double g1 = ((rgb >> 8) & 0xFF) / 255.0;
        double b1 = (rgb & 0xFF) / 255.0;
        double closestDist = Double.MAX_VALUE;
        int closestHSL = table[0];
        for (int hsl = 0; hsl < table.length; hsl++) {
            int color = table[hsl];
            double r2 = ((color >> 16) & 0xFF) / 255.0;
            double g2 = ((color >> 8) & 0xFF) / 255.0;
            double b2 = (color & 0xFF) / 255.0;
            double d = sq((r2 - r1) * 0.3) + sq((g2 - g1) * 0.59) + sq((b2 - b1) * 0.11);
            if (d < closestDist) {
                closestDist = d;
                closestHSL = hsl;
            }
        }
        return closestHSL;
    }

    public static int findClosest(int rgb) {
        return table[findClosestHSL(rgb)];
    }

    /**
     * @param rgb the rgb color value
     * @return the index in the palette of this rgb value, -1 if it does not exist
     */
    public static int getPaletteIndex(int rgb) {
        return IntStreamEx.range(table.length).findFirst(hsl -> table[hsl] == rgb).orElse(-1);
    }

    public static int getHSL(int hue, int saturation, int lightness) {
        return hue << 10 | saturation << 7 | lightness;
    }

    public static int getHue(int hsl) {
        return (hsl >> 10) & 0x3F;
    }

    public static int getSaturation(int hsl) {
        return (hsl >> 7) & 0x7;
    }

    public static int getLightness(int hsl) {
        return hsl & 0x7F;
    }

    public static int compute(int hsl) {
        return compute(hsl, 0.80000000000000004D);
    }

    public static int compute(int hsl, double gamma) {
        int h = hsl >> 10 & 0x3f;
        int s = hsl >> 7 & 0x7;
        int l = hsl & 0x7f;
        int x = l & 0x7F;
        int y = (h << 3) | s;
        return compute(x, y, gamma);
    }

    private static int compute(int x, int y, double gamma) {
        double hue = ((double) (y / 8) / 64.0) + 0.0078125; // 0.0078125 is 1 / 128
        double saturation = ((double) (y & 0x7) / 8.0) + 0.0625; // 0.0625 is 1 / 16
        double lightness = (double) x / 128.0;
        double red = saturation;
        double green = saturation;
        double blue = saturation;

        if (lightness != 0.0) {
            double a;

            if (saturation < 0.5) {
                a = saturation * (1.0 + lightness);
            } else {
                a = (saturation + lightness) - (saturation * lightness);
            }

            double b = (2.0 * saturation) - a;

            double fRed = hue + (1.0 / 3.0);
            double fBlue = hue - (1.0 / 3.0);

            if (fRed > 1.0) {
                fRed--;
            }
            if (fBlue < 0.0) {
                fBlue++;
            }

            red = hueToRgb(fRed, a, b);
            green = hueToRgb(hue, a, b);
            blue = hueToRgb(fBlue, a, b);
        }

        return adjustGamma(((int) (red * 256.0) << 16) | ((int) (green * 256.0) << 8) | (int) (blue * 256.0), gamma);
    }

    public static void generatePalette(double gamma) {
        int index = 0;
        for (int y = 0; y < 512; y++) {
            double hue = ((double) (y / 8) / 64.0) + 0.0078125;
            double saturation = ((double) (y & 0x7) / 8.0) + 0.0625;
            for (int x = 0; x < 128; x++) {
                double lightness = (double) x / 128.0;
                double red = lightness;
                double green = lightness;
                double blue = lightness;

                if (saturation != 0.0) {
                    double a;

                    if (lightness < 0.5) {
                        a = lightness * (1.0 + saturation);
                    } else {
                        a = (lightness + saturation) - (lightness * saturation);
                    }

                    double b = (2.0 * lightness) - a;

                    double fRed = hue + (1.0 / 3.0);
                    double fBlue = hue - (1.0 / 3.0);

                    if (fRed > 1.0) {
                        fRed--;
                    }
                    if (fBlue < 0.0) {
                        fBlue++;
                    }

                    red = hueToRgb(fRed, a, b);
                    green = hueToRgb(hue, a, b);
                    blue = hueToRgb(fBlue, a, b);
                }

                table[index++] = adjustGamma(((int) (red * 256.0) << 16) | ((int) (green * 256.0) << 8) | (int) (blue * 256.0), gamma);
            }
        }

    }

    private static double hueToRgb(double value, double a, double b) {
        if ((6.0 * value) < 1.0) {
            return b + ((a - b) * 6.0 * value);
        }

        if (2.0 * value < 1.0) {
            return a;
        }
        if (3.0 * value < 2.0) {
            return b + ((a - b) * ((2.0 / 3.0) - value) * 6.0);
        }
        return b;
    }

    public static int adjustGamma(int rgb, double gamma) {
        double r = (double) (rgb >> 16) / 256.0;
        double g = (double) (rgb >> 8 & 0xff) / 256.0;
        double b = (double) (rgb & 0xff) / 256.0;
        r = Math.pow(r, gamma);
        g = Math.pow(g, gamma);
        b = Math.pow(b, gamma);
        return ((int) (r * 256.0) << 16) + ((int) (g * 256.0) << 8) + (int) (b * 256.0);
    }

}
