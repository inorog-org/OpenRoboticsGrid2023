package modules.gamepad.sticks;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import modules.gamepad.sticks.equations.PolarCoordinates;

public class AxisStick implements Stick {

  private final Supplier<Double> stick;
  private final BiFunction<Double,Double,Double> equation;
  private final PolarCoordinates coordinates;
  private final double DRIFT;

  public AxisStick(Supplier<Double> stick, BiFunction<Double, Double, Double> equation, Double drift) {

      this.stick    = stick;
      this.equation = equation;
      this.coordinates = new PolarCoordinates();
      this.DRIFT       = drift;
  }

  public PolarCoordinates getPolar() {
      return this.coordinates;
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public void updateData() {
      coordinates.magnitude = equation.apply(stick.get(), DRIFT);
      coordinates.angle     = 0;
      coordinates.active    = coordinates.magnitude != 0;
  }

}
