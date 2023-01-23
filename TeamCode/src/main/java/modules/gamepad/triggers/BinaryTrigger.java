package modules.gamepad.triggers;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.Supplier;

import modules.gamepad.buttons.Button;

public class BinaryTrigger implements Trigger {

    private final Supplier<Double> value;

    public BinaryTrigger(Supplier<Double> value) {

        this.value = value;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public double listen() {
        return (value.get() == 1) ? 1.0 : 0.0;
    }
}
