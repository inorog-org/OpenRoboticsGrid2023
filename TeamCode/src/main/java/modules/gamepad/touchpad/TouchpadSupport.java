package modules.gamepad.touchpad;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.Supplier;

import modules.configuration.gamepad.touchpad.GamepadTouchpad;
import modules.gamepad.touchpad.equations.TouchpadEquations;

public class TouchpadSupport {

    private final Supplier<Double> xAxis;
    private final Supplier<Double> yAxis;
    private final Supplier<Boolean> touchpad;

    public double x = 0;
    public double y = 0;
    public double angle = 0;
    public double magnitude = 0;
    public boolean isTouched = false;

    public TouchpadSupport(Supplier<Double> xAxis, Supplier<Double> yAxis, Supplier<Boolean> touch) {

       this.xAxis = xAxis;
       this.yAxis = yAxis;
       this.touchpad = touch;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateData() {
        x = xAxis.get();
        y = yAxis.get();
        magnitude = TouchpadEquations.MAG_MAPPING.apply(x, y);
        x = GamepadTouchpad.getMappingEquation(GamepadTouchpad.TOUCHPAD_MAPPING, Axis.X).apply(x);
        y = GamepadTouchpad.getMappingEquation(GamepadTouchpad.TOUCHPAD_MAPPING, Axis.Y).apply(y);;
        angle = Math.atan2(y,x);
        isTouched = touchpad.get();
    }

    public enum Axis { X, Y }

}
