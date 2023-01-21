package modules.gamepad.support;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import modules.gamepad.Input;
import modules.gamepad.buttons.Button;
import modules.gamepad.configuration.GamepadButtons;

public class GamepadSupport {

    // Buttons
    private final Button a;
    private final Button b;
    private final Button x;
    private final Button y;

    // Digital Pads
    private final Button dpad_down;
    private final Button dpad_right;
    private final Button dpad_left;
    private final Button dpad_up;

    // Bumpers
    private final Button bumper_right;
    private final Button bumper_left;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public GamepadSupport(Gamepad gamepad) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {

        a = GamepadButtons.getButtonType(GamepadButtons.a).getDeclaredConstructor(Supplier.class).newInstance((Supplier<Boolean>)() -> gamepad.a);
        b = GamepadButtons.getButtonType(GamepadButtons.b).getDeclaredConstructor(Supplier.class).newInstance((Supplier<Boolean>)() -> gamepad.b);
        x = GamepadButtons.getButtonType(GamepadButtons.x).getDeclaredConstructor(Supplier.class).newInstance((Supplier<Boolean>)() -> gamepad.x);
        y = GamepadButtons.getButtonType(GamepadButtons.y).getDeclaredConstructor(Supplier.class).newInstance((Supplier<Boolean>)() -> gamepad.y);

        dpad_down  = GamepadButtons.getButtonType(GamepadButtons.dpad_down).getDeclaredConstructor(Supplier.class).newInstance((Supplier<Boolean>)()  -> gamepad.dpad_down);
        dpad_right = GamepadButtons.getButtonType(GamepadButtons.dpad_right).getDeclaredConstructor(Supplier.class).newInstance((Supplier<Boolean>)() -> gamepad.dpad_right);
        dpad_left  = GamepadButtons.getButtonType(GamepadButtons.dpad_left).getDeclaredConstructor(Supplier.class).newInstance((Supplier<Boolean>)()  -> gamepad.dpad_left);
        dpad_up    = GamepadButtons.getButtonType(GamepadButtons.dpad_up).getDeclaredConstructor(Supplier.class).newInstance((Supplier<Boolean>)() -> gamepad.dpad_up);

        bumper_right = GamepadButtons.getButtonType(GamepadButtons.bumper_right).getDeclaredConstructor(Supplier.class).newInstance((Supplier<Boolean>)() -> gamepad.right_bumper);
        bumper_left  = GamepadButtons.getButtonType(GamepadButtons.bumper_left).getDeclaredConstructor(Supplier.class).newInstance((Supplier<Boolean>)()  -> gamepad.left_bumper);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean getA() {
        return a.listen();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean getB() {
        return b.listen();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean getX() {
        return x.listen();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean getY() {
        return y.listen();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean getDpadDown() {
        return dpad_down.listen();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean getDpadUp() {
        return dpad_up.listen();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean getDpadRight() {
        return dpad_right.listen();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean getDpadLeft() {
        return dpad_left.listen();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean getBumperRight() {
        return bumper_right.listen();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean getBumperLeft() {
        return bumper_left.listen();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getDigitalPads(Input input) {

        input.dpad_down  = getDpadDown();
        input.dpad_up    = getDpadUp();
        input.dpad_right = getDpadRight();
        input.dpad_left  = getDpadLeft();

    }

}
