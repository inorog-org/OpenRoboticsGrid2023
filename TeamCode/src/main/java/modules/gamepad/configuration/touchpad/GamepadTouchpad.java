package modules.gamepad.configuration.touchpad;

import java.util.function.Function;

import modules.gamepad.touchpad.equations.TouchpadEquations;

public class GamepadTouchpad {

    public static TouchpadMapping TOUCHPAD_MAPPING = TouchpadMapping.SQUARE;

    public static Function<Double,Double> getMappingEquation(TouchpadMapping stickMapping, String string) {

        switch(stickMapping) {
            case SQUARE:
                return TouchpadEquations.AXIS_SQUARE_MAPPING;
            case RECTANGULAR: 
                if("X".equals(string))
                     return TouchpadEquations.AXIS_RECTANGULAR_MAPPING_X;
                else return TouchpadEquations.AXIS_RECTANGULAR_MAPPING_Y;
        }
        return TouchpadEquations.AXIS_SQUARE_MAPPING;
    }

}
