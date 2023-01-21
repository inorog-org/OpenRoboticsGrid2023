package modules.gamepad.buttons;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.function.Supplier;

public class DefaultButton implements Button{

    private final Supplier<Boolean> condition;

    public DefaultButton(Supplier<Boolean> button) {
        this.condition = button;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean listen() {
        return condition.get();
    }
}
