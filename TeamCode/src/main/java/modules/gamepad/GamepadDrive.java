package modules.gamepad;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.lang.reflect.InvocationTargetException;

import modules.gamepad.configuration.touchpad.GamepadTouchpad;
import modules.gamepad.lightbar.LightbarSupport;
import modules.gamepad.support.GamepadSupport;
import modules.gamepad.touchpad.TouchpadSupport;

public class GamepadDrive {

      public final Input driveInput;

      private final GamepadSupport gamepad;
      private final TouchpadSupport touchpad;
      private final LightbarSupport lightbar;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public GamepadDrive(Gamepad gamepad) {

        // Init Gamepad Support
        try {
            this.gamepad  = new GamepadSupport(gamepad);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }

        // Init Touchpad Support
        this.touchpad = new TouchpadSupport(() -> (double) gamepad.touchpad_finger_1_x, () -> (double) gamepad.touchpad_finger_1_y, () -> gamepad.touchpad_finger_1);

        // Init Lightbar Support
        this.lightbar = new LightbarSupport(gamepad);

        // Init Input
        this.driveInput = new Input();
    }



}
