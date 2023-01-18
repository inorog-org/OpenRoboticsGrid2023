package modules.gamepad;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.Supplier;

public class StickyButton {

    private boolean previousState = false;
    private Supplier<Boolean> condition;
    private Supplier<Void> function;

    public StickyButton(Supplier<Boolean> condition, Supplier<Void> function) {
        this.condition = condition;
        this.function  = function;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void listen() {

        if (condition.get()) {
            if (!previousState) {
                previousState = true;
                function.get();
            }
        } else {
            if (previousState) {
                previousState = false;
            }
        }

    }


}
