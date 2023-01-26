package modules.gamepad.touchpad.equations;

import java.util.function.BiFunction;
import java.util.function.Function;

public class TouchpadEquations {

    private static int WIDTH  = 1920;

    private static int HEIGHT = 1080;

    /// Magnitude
    public static final BiFunction<Double,Double,Double> MAG_MAPPING = (x,y) -> (Math.max(Math.abs(x), Math.abs(y)) > 1) ? 1 : Math.max(Math.abs(x), Math.abs(y));
    /// Axis
    // SQUARE
    public static final Function<Double,Double> AXIS_SQUARE_MAPPING = (x) -> x;
    // RECTANGULAR
    public static final Function<Double,Double> AXIS_RECTANGULAR_MAPPING_X = (x) -> x * WIDTH / 2;
    public static final Function<Double,Double> AXIS_RECTANGULAR_MAPPING_Y = (x) -> x * HEIGHT / 2;
}
