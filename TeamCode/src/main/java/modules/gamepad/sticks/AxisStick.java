package modules.gamepad.sticks;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class AxisStick implements Sticks {

  private final Supplier<Double> stick;
  private final BiFunction<Double,Double,Double> equation;
  private final double DRIFT;

  public AxisStick(Supplier<Double> stick, BiFunction<Double, Double, Double> equation, double drift) {

      this.stick    = stick;
      this.equation = equation;
      this.DRIFT    = drift;
  }

  @RequiresApi(api = Build.VERSION_CODES.N)
  public double getAxis() {
      return equation.apply(stick.get(), DRIFT);
  }

}
