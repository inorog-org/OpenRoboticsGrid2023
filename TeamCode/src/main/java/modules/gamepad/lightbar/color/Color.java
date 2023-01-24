package modules.gamepad.lightbar.color;

/**
 * Rosu             = rgb(255, 0, 0)      = hue(0)/hue(360)
 * Galben           = rgb(255, 255, 0)    = hue(60)
 * Verde            = rgb(0, 255, 0)      = hue(120)
 * Albastru Deschis = rgb(0, 255, 255)    = hue(180)
 * Albastru Inchis  = rgb(0, 0, 255)      = hue(240)
 * Roz              = rgb(255, 0, 255)    = hue(300)
 * */

public class Color {

    public static void HSLtoRGB(double hue, double saturation, double lightness, RGB color){
        double chroma = (1 - Math.abs(2 * lightness - 1)) * saturation;
        double huePrime = hue / 60.0;
        double x = chroma * (1 - Math.abs(huePrime % 2 - 1));

        formulaRGB(color, huePrime, chroma, x);

        double m = lightness - chroma / 2.0;

        color.r += m;
        color.g += m;
        color.b += m;
    }

    public static void HSVtoRGB(double hue, double saturation, double value, RGB color){
        double chroma = saturation * value;
        double huePrime = hue / 60.0;
        double x = chroma * (1 - Math.abs(huePrime % 2 - 1));

        formulaRGB(color, huePrime, chroma, x);

        double m = value - chroma;

        color.r += m;
        color.g += m;
        color.b += m;
    }

    private static void formulaRGB(RGB color, double huePrime, double chroma, double x) {

        if (0 <= huePrime && huePrime < 1) {
            color.r = chroma;
            color.g = x;
            color.b = 0;
        } else if (1 <= huePrime && huePrime < 2) {
            color.r = x;
            color.g = chroma;
            color.b = 0;
        } else if (2 <= huePrime && huePrime < 3) {
            color.r = 0;
            color.g = chroma;
            color.b = x;
        } else if (3 <= huePrime && huePrime < 4) {
            color.r = 0;
            color.g = x;
            color.b = chroma;
        } else if (4 <= huePrime && huePrime < 5) {
            color.r = x;
            color.g = 0;
            color.b = chroma;
        } else if (5 <= huePrime && huePrime < 6) {
            color.r = chroma;
            color.g = 0;
            color.b = x;
        }
    }

    public enum ColorHue{
        RED,
        ORANGE,
        YELLOW,
        LIME,
        GREEN,
        SPRING_GREEN,
        CYAN,
        BLUE,
        DARK_BLUE,
        PURPLE,
        FUCHSIA,
        ROSE
    }

    public static double colorToHue(ColorHue hue){
        return (360.0 / ColorHue.values().length) * hue.ordinal();
    }

    public static double linearHue(double t, ColorHue start, ColorHue finish){
        return colorToHue(start) * (1 - t) + colorToHue(finish) * t;
    }
}
