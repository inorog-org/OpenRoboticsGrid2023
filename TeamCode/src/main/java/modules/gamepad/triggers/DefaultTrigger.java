package modules.gamepad.triggers;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.Supplier;

public class DefaultTrigger implements Trigger{

    private final Supplier<Double> value;

    public DefaultTrigger(Supplier<Double> value) {

        this.value = value;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public double listen() {
        return value.get();
    }
}
