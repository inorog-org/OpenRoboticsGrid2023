package modules.drive.subsystems.teleop;

import com.qualcomm.robotcore.util.Range;

import modules.drive.configuration.MotorsConstants;
import modules.gamepad.configuration.ActivationInput;
import modules.gamepad.configuration.GamepadActivation;

public class SpeedChangerSystem {

    public void execute(boolean increment, boolean decrement) {
        if (GamepadActivation.SPEEDCHANGER == ActivationInput.ACTIVE) {

            if (increment) {
                MotorsConstants.MAX_MOVEMENT_SPEED = Range.clip(MotorsConstants.MAX_MOVEMENT_SPEED + MotorsConstants.MOTOR_SPEED_INCREMENT, MotorsConstants.MOTOR_MIN_POWER, MotorsConstants.MOTOR_MAX_POWER);
                MotorsConstants.MAX_ROTATE_SPEED   = Range.clip(MotorsConstants.MAX_ROTATE_SPEED   + MotorsConstants.MOTOR_SPEED_INCREMENT, MotorsConstants.MOTOR_MIN_POWER, MotorsConstants.MOTOR_MAX_POWER);
            }

            if (decrement) {
                MotorsConstants.MAX_MOVEMENT_SPEED = Range.clip(MotorsConstants.MAX_MOVEMENT_SPEED - MotorsConstants.MOTOR_SPEED_INCREMENT, MotorsConstants.MOTOR_MIN_POWER, MotorsConstants.MOTOR_MAX_POWER);
                MotorsConstants.MAX_ROTATE_SPEED   = Range.clip(MotorsConstants.MAX_ROTATE_SPEED   - MotorsConstants.MOTOR_SPEED_INCREMENT, MotorsConstants.MOTOR_MIN_POWER, MotorsConstants.MOTOR_MAX_POWER);
            }
        }
    }
}


