package modules.gamepad;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.lang.reflect.InvocationTargetException;

import modules.gamepad.configuration.ActivationInput;
import modules.gamepad.configuration.GamepadActivation;
import modules.gamepad.configuration.GamepadType;
import modules.gamepad.configuration.PrioritiesGamepad;
import modules.gamepad.configuration.buttons.ButtonInput;
import modules.gamepad.configuration.buttons.GamepadButtons;
import modules.gamepad.configuration.lightbar.GamepadLightbar;
import modules.gamepad.configuration.sticks.AxisInput;
import modules.gamepad.configuration.sticks.GamepadSticks;
import modules.gamepad.lightbar.LightbarSupport;
import modules.gamepad.sticks.equations.PolarCoordinates;
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

        driveInput.locked = isLocked();

        if(!driveInput.locked) {
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
            // Buttons: Boost + Memorate Position + Execute to Position + SpeedChanger
            speedchanger();
            speedMultiplierByTrigger();
            booster();
            realign();

            // Update Lightbar (if it is the Case) + Case for Stick and Case for Touchpad
            updateLight();
        }
    }

    private void movementJoystick() {
        if (GamepadActivation.MOVEMENT_JOYSTICK == ActivationInput.ACTIVE) {
            GamepadSticks.STICK_MAPPING = GamepadSticks.getMappingType(gamepadType);
            PolarCoordinates polar  = getMovement();
            assert polar != null : "Movement polar is null!";
            driveInput.magnitude     = polar.magnitude;
            driveInput.angle         = polar.angle;
            driveInput.movementStick = polar.magnitude != 0;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void movementTouchpad() {
        if (gamepadType == GamepadType.DUALSHOCK) {
            if (GamepadActivation.MOVEMENT_TOUCHPAD == ActivationInput.ACTIVE) {
                touchpad.updateData();

                if(touchpad.isTouched) {
                    driveInput.magnitudeTouch = touchpad.magnitude;
                    driveInput.angleTouchpad = touchpad.angle;
                    driveInput.touchpad = true;
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void movementDpad() {
        if (isDPADforMovement()) {

            if(!isDpadChanged()) {
                gamepad.getDigitalPads(driveInput);
            }

            driveInput.angle = convertDPADtoAngle(driveInput.dpad_right, driveInput.dpad_up, driveInput.dpad_left, driveInput.dpad_down);
            driveInput.movement_dpad = isDpadChanged();
            driveInput.magnitude = boolToInteger(driveInput.movement_dpad);
        }
    }

    private void rotationJoystick() {
        if (GamepadActivation.ROTATION_JOYSTICK == ActivationInput.ACTIVE) {
            PolarCoordinates polar  = getRotation();
            assert polar != null : "Rotational polar is null!";
            driveInput.rotate        = polar.magnitude;
            driveInput.rotationStick = polar.magnitude != 0;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void rotationTriggers() {
        if (GamepadActivation.ROTATION_TRIGGERS == ActivationInput.ACTIVE) {
            driveInput.spin = gamepad.getRightTrigger() - gamepad.getLeftTrigger();
            driveInput.spinTriggers = (gamepad.getLeftTrigger() != 0) || (gamepad.getRightTrigger() != 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void speedMultiplierByTrigger() {
        if(GamepadActivation.SPEEDMULTIPLIER == ActivationInput.ACTIVE && GamepadActivation.ROTATION_TRIGGERS == ActivationInput.INACTIVE) {
            driveInput.speedMultiplier = 1 - 0.75 * gamepad.getRightTrigger();
        }
    }

    private void updateLight() {
        if (gamepadType == GamepadType.DUALSHOCK) {
            if (GamepadActivation.LIGHTBAR == ActivationInput.ACTIVE) {
                if (driveInput.magnitudeTouch != 0) {
                    lightbar.updateLightbarColor(GamepadLightbar.TOUCHPAD);
                } else {
                    lightbar.updateLightbarColor(driveInput.magnitude);
                    driveInput.HUE   = lightbar.HUE;
                    driveInput.RED   = lightbar.color.r;
                    driveInput.GREEN = lightbar.color.g;
                    driveInput.BLUE  = lightbar.color.b;
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void speedchanger() {
        if(isDPADforSpeedchanger()) {

            if(!isDpadChanged()) {
               gamepad.getDigitalPads(driveInput);
            }

            driveInput.increment = driveInput.dpad_up;
            driveInput.decrement = driveInput.dpad_down;
        }
    }

    private PolarCoordinates getMovement() {
        if(GamepadSticks.LEFT_STICK == AxisInput.POLAR) {
            gamepad.updateLeftStick();
            return gamepad.left_stick;
        }

        if(GamepadSticks.RIGHT_STICK == AxisInput.POLAR) {
            gamepad.updateRightStick();
            return gamepad.right_stick;
        }

        return null;
    }

    private PolarCoordinates getRotation() {
        if(GamepadSticks.RIGHT_STICK == AxisInput.AXIS) {
            gamepad.updateRightStick();
            return gamepad.right_stick;
        }

        if(GamepadSticks.LEFT_STICK == AxisInput.AXIS) {
            gamepad.updateLeftStick();
            return gamepad.left_stick;
        }

        return null;
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

    private boolean isDPADforSpeedchanger() {
        return GamepadActivation.SPEEDCHANGER == ActivationInput.ACTIVE &&
                GamepadButtons.dpad_down == ButtonInput.STICKY_BUTTON &&
                GamepadButtons.dpad_up == ButtonInput.STICKY_BUTTON;
    }

    private boolean isDPADforMovement() {
        return GamepadActivation.MOVEMENT_DPAD == ActivationInput.ACTIVE &&
                GamepadButtons.dpad_down == ButtonInput.DEFAULT &&
                GamepadButtons.dpad_up == ButtonInput.DEFAULT &&
                GamepadButtons.dpad_right == ButtonInput.DEFAULT &&
                GamepadButtons.dpad_left == ButtonInput.DEFAULT;
    }

    private double convertDPADtoAngle(boolean dr, boolean du, boolean dl, boolean dd) {
        float i_dr = boolToInteger(dr);
        float i_du = boolToInteger(du);
        float i_dl = boolToInteger(dl);
        float i_dd = boolToInteger(dd);

        if (i_dr + i_du + i_dl + i_dd == 0)
            return 0;
        else return Math.toRadians((90 * i_du + 180 * i_dl + 270 * i_dd + 360 * boolToInteger(i_dr + i_dd == 2)) / (i_dr + i_du + i_dl + i_dd));
    }

    private int boolToInteger(boolean value) {
        return (value) ? 1 : 0;
    }

}
