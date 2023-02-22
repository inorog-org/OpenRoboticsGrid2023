package modules.gamepad.sticks;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import modules.configuration.gamepad.sticks.GamepadSticks;
import modules.gamepad.sticks.equations.PolarCoordinates;

public class PolarStick implements Stick {

    private final Supplier<Double> xAxis;
    private final Supplier<Double> yAxis;
    private final BiFunction<Double,Double,Double> equation;
    private final PolarCoordinates coordinates;
    private final double DRIFT;

    public PolarStick(Supplier<Double> xAxis, Supplier<Double> yAxis, BiFunction<Double, Double, Double> equation, Double drift) {
         this.xAxis = xAxis;
         this.yAxis = yAxis;
         this.equation = equation;
         this.coordinates = new PolarCoordinates();
         this.DRIFT       = drift;
    }

    public PolarCoordinates getPolar() {
        return coordinates;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateData() {
         coordinates.magnitude = equation.apply(GamepadSticks.getMappingEquation(GamepadSticks.STICK_MAPPING).apply(xAxis.get(), yAxis.get()), DRIFT);
         coordinates.angle     = Math.atan2(yAxis.get(), xAxis.get());
         coordinates.active    = coordinates.magnitude != 0;
    }

}
