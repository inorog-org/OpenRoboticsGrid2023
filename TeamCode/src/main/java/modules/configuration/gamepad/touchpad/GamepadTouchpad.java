package modules.configuration.gamepad.touchpad;

import java.util.function.Function;

import modules.gamepad.touchpad.TouchpadSupport;
import modules.gamepad.touchpad.equations.TouchpadEquations;

public class GamepadTouchpad {

    public static TouchpadMapping TOUCHPAD_MAPPING = TouchpadMapping.RECTANGULAR;

    public static Function<Double,Double> getMappingEquation(TouchpadMapping stickMapping, TouchpadSupport.Axis axis) {

        switch(stickMapping) {
            case SQUARE:
                return TouchpadEquations.AXIS_SQUARE_MAPPING;
            case RECTANGULAR: 
                if(TouchpadSupport.Axis.X == axis)
                     return TouchpadEquations.AXIS_RECTANGULAR_MAPPING_X;
                else return TouchpadEquations.AXIS_RECTANGULAR_MAPPING_Y;
        }
        return TouchpadEquations.AXIS_SQUARE_MAPPING;
    }

}
