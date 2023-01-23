package modules.gamepad.sticks.equations;

import java.util.function.BiFunction;

public class StickEquations {

    // -- Ecuatii pentru DRIFT -- //

    /*   FROM ZERO - STICK

    -1        0   0        1   - REMAPED STICK AFTER FROM_ZERO EQUATION - [-1,1]
     |--------|-|-|--------|   - STICK AREA
    -1       -D 0 D        1   - NORMAL STICK WITH DRIFT (D = drift)

    */
    public static final BiFunction<Double,Double,Double> FROM_ZERO  = (x, drift) -> (x - Math.signum(x) * drift) / (1 - drift) * (Math.signum(Math.abs(x) - drift) + 1) / 2;

    /*   FROM DRIFT - STICK

    -1       -D   D        1   - REMAPED STICK AFTER FROM_DRIFT EQUATION - [-1,1]\[-D,D]
     |--------|-|-|--------|   - STICK AREA
    -1       -D 0 D        1   - NORMAL STICK WITH DRIFT (D = drift)

    */
    public static final BiFunction<Double,Double,Double> FROM_DRIFT = (x, drift) -> x * (Math.signum(Math.abs(x) - drift) + 1) / 2 * Math.signum(Math.abs(x) - drift);

    // -- Ecuatii pentru Polar Stick -- //

    // Xbox Magnitude - BOX Mapping
    public static final BiFunction<Double, Double,Double> SQUARE_TO_CIRCLE = (x,y) -> Math.min(Math.abs(x), Math.abs(y));

    // Playstation Magnitude - Circle Mapping - Simple Mapping
    public static final BiFunction<Double, Double,Double> CIRCLE = Math::hypot;


}
