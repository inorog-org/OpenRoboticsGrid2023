package modules.gamepad.support;

import android.os.Build;
import android.util.Base64DataException;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.lang.reflect.InvocationTargetException;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import modules.gamepad.Input;
import modules.gamepad.buttons.Button;
import modules.gamepad.configuration.buttons.GamepadButtons;
import modules.gamepad.configuration.sticks.GamepadSticks;
import modules.gamepad.sticks.Stick;
import modules.gamepad.sticks.equations.PolarCoordinates;

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

    // Sticks
    private Stick _right_stick;
    private Stick _left_stick;

    public PolarCoordinates left_stick;
    public PolarCoordinates right_stick;

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

        switch (GamepadSticks.RIGHT_STICK) {
            case AXIS:
                _right_stick = GamepadSticks.getStickType(GamepadSticks.RIGHT_STICK)
                                            .getDeclaredConstructor(Supplier.class, BiFunction.class, Double.class)
                                            .newInstance((Supplier<Double>)() -> (double) gamepad.right_stick_x, GamepadSticks.getDriftEquation(GamepadSticks.RIGHT_EQUATION), GamepadSticks.RIGHT_DRIFT); break;
            case POLAR:
                _right_stick = GamepadSticks.getStickType(GamepadSticks.RIGHT_STICK)
                                            .getDeclaredConstructor(Supplier.class, Supplier.class, BiFunction.class, Double.class)
                                            .newInstance((Supplier<Double>)() -> (double)  gamepad.right_stick_x,
                                                                 (Supplier<Double>)() -> (double) -gamepad.right_stick_y,
                                                                 GamepadSticks.getDriftEquation(GamepadSticks.RIGHT_EQUATION),
                                                                 GamepadSticks.RIGHT_DRIFT); break;
        }

        switch (GamepadSticks.LEFT_STICK) {
            case AXIS:
                _left_stick = GamepadSticks.getStickType(GamepadSticks.RIGHT_STICK)
                                           .getDeclaredConstructor(Supplier.class, BiFunction.class, Double.class)
                                           .newInstance((Supplier<Double>)() -> (double) gamepad.left_stick_x, GamepadSticks.getDriftEquation(GamepadSticks.LEFT_EQUATION),  GamepadSticks.LEFT_DRIFT); break;
            case POLAR:
                _left_stick = GamepadSticks.getStickType(GamepadSticks.RIGHT_STICK)
                                           .getDeclaredConstructor(Supplier.class, Supplier.class, BiFunction.class, Double.class)
                                           .newInstance((Supplier<Double>)() -> (double)  gamepad.left_stick_x,
                                                                (Supplier<Double>)() -> (double) -gamepad.left_stick_y,
                                                                GamepadSticks.getDriftEquation(GamepadSticks.LEFT_EQUATION),
                                                                GamepadSticks.LEFT_DRIFT); break;
        }

        left_stick  = _left_stick.getPolar();
        right_stick = _right_stick.getPolar();
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

    public void updateLeftStick() {

        _left_stick.updateData();
    }

    public void updateRightStick() {

        _right_stick.updateData();
    }

    public void updateSticks() {
        _left_stick.updateData();
        _right_stick.updateData();
    }

}
