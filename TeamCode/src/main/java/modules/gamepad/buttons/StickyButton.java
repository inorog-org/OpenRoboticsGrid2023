package modules.gamepad.buttons;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.Supplier;

public class StickyButton implements Button{

    private boolean previousState = false;
    private final Supplier<Boolean> condition;

    public StickyButton(Supplier<Boolean> button) {

        this.condition = button;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean listen() {

        if (condition.get()) {
            if (!previousState) {
                previousState = true;
                   return true;
            } else return false;
        } else {
            if (previousState) {
                previousState = false;
            }
            return false;
        }
    }


}
