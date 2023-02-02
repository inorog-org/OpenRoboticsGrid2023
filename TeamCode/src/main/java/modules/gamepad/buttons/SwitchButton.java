package modules.gamepad.buttons;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.Supplier;

public class SwitchButton implements Button {

    private boolean previousState = false;
    private final Supplier<Boolean> condition;
    private boolean state = false;

    public SwitchButton(Supplier<Boolean> button) {

        this.condition = button;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean listen() {

        boolean statuSus = condition.get();

        if(statuSus && !previousState){
            state = !state;
        }

        previousState = statuSus;

        return state;
    }

}
