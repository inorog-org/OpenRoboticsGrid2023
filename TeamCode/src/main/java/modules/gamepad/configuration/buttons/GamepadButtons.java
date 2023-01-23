package modules.gamepad.configuration.buttons;

import modules.gamepad.buttons.Button;
import modules.gamepad.buttons.DefaultButton;
import modules.gamepad.buttons.StickyButton;
import modules.gamepad.buttons.SwitchButton;
import modules.gamepad.buttons.TimeButton;
import modules.gamepad.configuration.buttons.ButtonInput;

public class GamepadButtons {

    // Buttons
    public static ButtonInput a = ButtonInput.STICKY_BUTTON;
    public static ButtonInput b = ButtonInput.STICKY_BUTTON;
    public static ButtonInput x = ButtonInput.STICKY_BUTTON;
    public static ButtonInput y = ButtonInput.STICKY_BUTTON;

    // Digital Pads
    public static ButtonInput dpad_down  = ButtonInput.STICKY_BUTTON;
    public static ButtonInput dpad_right = ButtonInput.STICKY_BUTTON;
    public static ButtonInput dpad_left  = ButtonInput.STICKY_BUTTON;
    public static ButtonInput dpad_up    = ButtonInput.STICKY_BUTTON;

    // Bumpers
    public static ButtonInput bumper_right = ButtonInput.STICKY_BUTTON;
    public static ButtonInput bumper_left  = ButtonInput.STICKY_BUTTON;

    public static Class<? extends Button> getButtonType(ButtonInput button) {

        switch(button) {
            case STICKY_BUTTON:
                return StickyButton.class;
            case SWTICH_BUTTON:
                return SwitchButton.class;
            case TIME_BUTTON:
                return TimeButton.class;
            case DEFAULT:
                return DefaultButton.class;
        }
        return DefaultButton.class;
    }

}
