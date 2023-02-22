package modules.configuration.gamepad.sticks;

import java.util.function.BiFunction;

import modules.configuration.gamepad.GamepadType;
import modules.gamepad.sticks.AxisStick;
import modules.gamepad.sticks.PolarStick;
import modules.gamepad.sticks.Stick;
import modules.gamepad.sticks.equations.StickEquations;

public class GamepadSticks {

    public static AxisInput RIGHT_STICK = AxisInput.AXIS;

    public static AxisInput LEFT_STICK  = AxisInput.POLAR;

    public static StickDrift RIGHT_EQUATION = StickDrift.FROM_ZERO;

    public static StickDrift LEFT_EQUATION  = StickDrift.FROM_ZERO;

    public static final double RIGHT_DRIFT  = 0.01;

    public static final double LEFT_DRIFT   = 0.01;
    public static StickMapping STICK_MAPPING   = StickMapping.BOX;

    public static Class<? extends Stick> getStickType(AxisInput stick) {

        switch(stick) {
            case AXIS:
                return AxisStick.class;
            case POLAR:
                return PolarStick.class;
        }
        return AxisStick.class;
    }

    public static BiFunction<Double, Double,Double> getMappingEquation(GamepadType gamepadType) {

        switch(gamepadType) {
            case XBOX:
                return StickEquations.SQUARE_TO_CIRCLE;
            case DUALSENSE:
            case DUALSHOCK:
                return StickEquations.CIRCLE;
        }
        return StickEquations.CIRCLE;

    }

    public static StickMapping getMappingType(GamepadType gamepadType) {
        switch (gamepadType) {
            case XBOX:
                return StickMapping.BOX;
            case DUALSHOCK:
            case DUALSENSE:
                return StickMapping.CIRCLE;
        }
        return StickMapping.CIRCLE;
    }

    public static BiFunction<Double, Double,Double> getMappingEquation(StickMapping stickMapping) {

        switch(stickMapping) {
            case BOX:
                return StickEquations.SQUARE_TO_CIRCLE;
            case CIRCLE:
                return StickEquations.CIRCLE;
        }
        return StickEquations.CIRCLE;
    }

    public static BiFunction<Double, Double,Double> getDriftEquation(StickDrift stickDrift) {

        switch(stickDrift) {
            case FROM_ZERO:
                return StickEquations.FROM_ZERO;
            case FROM_DRIFT:
                return StickEquations.FROM_DRIFT;
        }
        return StickEquations.FROM_ZERO;
    }

}
