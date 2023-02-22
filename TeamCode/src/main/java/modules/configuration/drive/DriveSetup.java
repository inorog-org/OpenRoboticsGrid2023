package modules.configuration.drive;

import modules.configuration.gamepad.buttons.ButtonInput;
import modules.configuration.gamepad.ActivationInput;
import modules.configuration.gamepad.GamepadActivation;
import modules.configuration.gamepad.buttons.GamepadButtons;

public class DriveSetup {

    private static void setupSpeedchanger() {
        GamepadButtons.dpad_left  = ButtonInput.DEFAULT;
        GamepadButtons.dpad_right = ButtonInput.DEFAULT;
        GamepadButtons.dpad_down  = ButtonInput.STICKY_BUTTON;
        GamepadButtons.dpad_up    = ButtonInput.STICKY_BUTTON;
    }

    private static void setupDigitalMovement() {
        GamepadButtons.dpad_left  = ButtonInput.DEFAULT;
        GamepadButtons.dpad_right = ButtonInput.DEFAULT;
        GamepadButtons.dpad_down  = ButtonInput.DEFAULT;
        GamepadButtons.dpad_up    = ButtonInput.DEFAULT;
    }

    public static void initSetup() {

        if(GamepadActivation.MOVEMENT_DPAD == ActivationInput.ACTIVE)
            setupDigitalMovement();

        if(GamepadActivation.SPEEDCHANGER == ActivationInput.ACTIVE)
             setupSpeedchanger();
    }

}
