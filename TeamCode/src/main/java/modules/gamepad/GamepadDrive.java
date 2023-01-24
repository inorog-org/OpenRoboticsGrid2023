package modules.gamepad;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.lang.reflect.InvocationTargetException;

import modules.gamepad.configuration.ActivationInput;
import modules.gamepad.configuration.GamepadActivation;
import modules.gamepad.configuration.GamepadType;
import modules.gamepad.configuration.PrioritiesGamepad;
import modules.gamepad.configuration.lightbar.GamepadLightbar;
import modules.gamepad.configuration.sticks.GamepadSticks;
import modules.gamepad.lightbar.LightbarSupport;
import modules.gamepad.support.GamepadSupport;
import modules.gamepad.touchpad.TouchpadSupport;

public class GamepadDrive {

    public final Input driveInput;

    private final GamepadSupport gamepad;
    private final TouchpadSupport touchpad;
    private final LightbarSupport lightbar;
    private final Gamepad _gamepad;

    private GamepadType gamepadType;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public GamepadDrive(Gamepad gamepad) {

        // Init Gamepad Support
        try {
            this.gamepad = new GamepadSupport(gamepad);
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

        // Init Gamepad
        this._gamepad = gamepad;
    }

    private GamepadType detectGamepadType() {
        switch (_gamepad.type) {
            case XBOX_360:
            case LOGITECH_F310:
                return GamepadType.XBOX;
            case SONY_PS4:
            case SONY_PS4_SUPPORTED_BY_KERNEL:
                return GamepadType.DUALSHOCK;
            case UNKNOWN:
                return GamepadType.DUALSENSE;
        }
        return GamepadType.XBOX;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void updateInput() {

        // Detect Gamepad Type
        gamepadType = detectGamepadType();

        // Reset Input
        driveInput.reset();

        // Movement Input: Joystick + Touchpad + Digital Pads
        for (int i = 1; i <= 3 && !isMovementChanged(); i++) {
            switch (i) {
                case PrioritiesGamepad.MOVEMENT_JOYSTICK:
                    movementJoystick();
                    break;
                case PrioritiesGamepad.MOVEMENT_TOUCHPAD:
                    movementTouchpad();
                    break;
                case PrioritiesGamepad.MOVEMENT_DPAD:
                    movementDpad();
                    break;
            }
        }

        // Rotation Input: Joystick + Triggers
        for (int i = 1; i <= 2 && !isRotationChanged(); i++) {
            switch (i) {
                case PrioritiesGamepad.ROTATION_JOYSTICK:
                    rotationJoystick();
                    break;
                case PrioritiesGamepad.ROTATION_TRIGGERS:
                    rotationTriggers();
                    break;
            }
        }
        // Buttons: Locker + Boost + Memorate Position + Execute to Position
        driveInput.locked = isLocked();
        booster();
        realign();

        // Update Lightbar (if it is the Case) + Case for Stick and Case for Touchpad
        updateLight();
    }

    private void movementJoystick() {  // TODO
        if (GamepadActivation.MOVEMENT_JOYSTICK == ActivationInput.ACTIVE) {
            GamepadSticks.STICK_MAPPING = GamepadSticks.getMappingType(gamepadType);


        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void movementTouchpad() {
        if (gamepadType == GamepadType.DUALSHOCK) {
            if (GamepadActivation.MOVEMENT_TOUCHPAD == ActivationInput.ACTIVE) {
                touchpad.updateData();

                driveInput.magnitudeTouch = touchpad.magnitude;
                driveInput.angleTouchpad = touchpad.angle;
                driveInput.touchpad = touchpad.isTouched;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void movementDpad() {
        if (GamepadActivation.MOVEMENT_DPAD == ActivationInput.ACTIVE) {
            gamepad.getDigitalPads(driveInput);

            driveInput.angle = Math.toRadians(convertDPADtoAngle(driveInput.dpad_right, driveInput.dpad_up, driveInput.dpad_left, driveInput.dpad_down));
            driveInput.movement_dpad = isDpadChanged();
            driveInput.magnitude = boolToInteger(driveInput.movement_dpad);
        }
    }

    private void rotationJoystick() {  // TODO
        if (GamepadActivation.ROTATION_JOYSTICK == ActivationInput.ACTIVE) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void rotationTriggers() {
        if (GamepadActivation.ROTATION_TRIGGERS == ActivationInput.ACTIVE) {
            driveInput.spin = gamepad.getRightTrigger() - gamepad.getLeftTrigger();
            driveInput.spinTriggers = (gamepad.getLeftTrigger() != 0) || (gamepad.getRightTrigger() != 0);
        }
    }

    private void updateLight() {
        if (gamepadType == GamepadType.DUALSHOCK) {
            if (GamepadActivation.LIGHTBAR == ActivationInput.ACTIVE) {
                if (driveInput.magnitudeTouch != 0) {
                    lightbar.updateLightbarColor(GamepadLightbar.TOUCHPAD);
                } else {
                    lightbar.updateLightbarColor(driveInput.magnitude);
                    driveInput.HUE = lightbar.HUE;
                    driveInput.RED = lightbar.color.r;
                    driveInput.GREEN = lightbar.color.g;
                    driveInput.BLUE = lightbar.color.b;
                }
            } else lightbar.turnOff();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean isLocked() {
        if (GamepadActivation.LOCKER == ActivationInput.ACTIVE) {
            return gamepad.getB();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void booster() {
        if (GamepadActivation.BOOST == ActivationInput.ACTIVE) {
            driveInput.boost = gamepad.getA();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void realign() {
        if (GamepadActivation.REALIGN == ActivationInput.ACTIVE) {
            driveInput.memoratePosition = gamepad.getBumperLeft();
            driveInput.approachPosition = gamepad.getBumperRight();
        }
    }

    // CHANGED
    private boolean isMovementChanged() {
        return isJoystickChanged() || isTouchpadChanged() || isDpadChanged();
    }

    private boolean isDpadChanged() {
        return driveInput.dpad_down || driveInput.dpad_up || driveInput.dpad_right || driveInput.dpad_left;
    }

    private boolean isJoystickChanged() {
        return driveInput.movementStick;
    }

    private boolean isTouchpadChanged() {
        return driveInput.touchpad;
    }

    private boolean isRotationChanged() {
        return driveInput.rotationStick || driveInput.spinTriggers;
    }

    private int convertDPADtoAngle(boolean dr, boolean du, boolean dl, boolean dd) {
        int i_dr = boolToInteger(dr);
        int i_du = boolToInteger(du);
        int i_dl = boolToInteger(dl);
        int i_dd = boolToInteger(dd);

        if (i_dr + i_du + i_dl + i_dd == 0)
            return 0;
        else return (90 * i_du + 180 * i_dl + 270 * i_dd) / (i_dr + i_du + i_dl + i_dd);
    }

    private int boolToInteger(boolean value) {
        return (value) ? 1 : 0;
    }

}
