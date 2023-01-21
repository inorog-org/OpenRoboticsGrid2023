package modules.gamepad.buttons;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.Supplier;

public class TimeButton implements Button {
    private final Supplier<Boolean> condition;
    private boolean previousState = false;
    private boolean state = false;
    private long startingTime = 0;
    private final int time_in_ms = 1000;

    public TimeButton(Supplier<Boolean> button) {

        this.condition = button;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean listen() {

        if (condition.get()) {
            if (!previousState) {
                previousState = true;
                startingTime  = System.currentTimeMillis();
            } else {
                long timePressed = System.currentTimeMillis() - startingTime;

                if (timePressed >= time_in_ms) {
                    state = !state;
                    previousState = false;
                }
            }
        } else {
            startingTime = 0;
            previousState = false;
        }

        return state;
    }

}
