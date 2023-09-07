package opmodes.tests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import modules.gamepad.GamepadDrive;

@TeleOp(name = "Lunatica: Gamepad", group = "Testus")
@Disabled
public class GamepadDriveTest extends LinearOpMode {

    private GamepadDrive gamepadDrive;

    private int boost = 0;
    private int increment = 0;
    private int decrement = 0;
    private int memorate = 0;
    private int approach = 0;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void runOpMode() throws InterruptedException {

        gamepadDrive = new GamepadDrive(gamepad1);

        waitForStart();

        while (opModeIsActive()) {

            gamepadDrive.updateInput();

            telemetryMovement();

            telemetryDPADmovement();

            telemetryRotation();

            telemetrySpinData();

            telemetryTouchpadData();

            telemetryRealignButtons();

            telemetryBoostButton();

            telemetryLocker();

            telemetrySpeedChanger();

            telemetryColor();

            telemetry.update();
        }
    }

    public void telemetryMovement() {

        telemetry.addData("Movement Magnitude ", gamepadDrive.driveInput.magnitude);
        telemetry.addData("Movement Angle ", Math.toDegrees(gamepadDrive.driveInput.angle));
        telemetry.addData("Movement Stick", gamepadDrive.driveInput.movementStick);
    }
    public void telemetryRotation() {

        telemetry.addData("Rotation Magnitude ", gamepadDrive.driveInput.rotate);
        telemetry.addData("Rotation Stick ", gamepadDrive.driveInput.rotationStick);
    }
    public void telemetrySpinData() {

        telemetry.addData("Spin Magnitude ", gamepadDrive.driveInput.spin);
        telemetry.addData("Spin Triggers ", gamepadDrive.driveInput.spinTriggers);
    }
    public void telemetryTouchpadData() {

        telemetry.addData("Touchpad Magnitude ", gamepadDrive.driveInput.magnitudeTouch);
        telemetry.addData("Touchpad Angle ", Math.toDegrees(gamepadDrive.driveInput.angleTouchpad));
        telemetry.addData("Is touched ", gamepadDrive.driveInput.touchpad);
    }
    public void telemetryRealignButtons() {

        if(gamepadDrive.driveInput.memoratePosition) {
            memorate++;
        }
        telemetry.addData("Memorate Position ", memorate);

        if(gamepadDrive.driveInput.approachPosition) {
            approach++;
        }
        telemetry.addData("Approach Position ", approach);
    }
    public void telemetryBoostButton() {

        if(gamepadDrive.driveInput.boost) {
            boost++;
        }
        telemetry.addData("Boost ", boost);
    }
    public void telemetryLocker () {

        telemetry.addData("Locked ", gamepadDrive.driveInput.locked);
    }
    public void telemetrySpeedChanger() {

        if(gamepadDrive.driveInput.increment) {
            increment++;
        }
        telemetry.addData("Increment ", increment);

        if(gamepadDrive.driveInput.decrement) {
            decrement++;
        }
        telemetry.addData("Decrement ", decrement);
    }
    public void telemetryColor() {

        telemetry.addData("HUE ", gamepadDrive.driveInput.HUE);
        telemetry.addData("RED ", gamepadDrive.driveInput.RED);
        telemetry.addData("GREEN ", gamepadDrive.driveInput.GREEN);
        telemetry.addData("BLUE ", gamepadDrive.driveInput.BLUE);
    }

    public void telemetryDPADmovement() {
        if(gamepadDrive.driveInput.movement_dpad) {
            telemetry.addData("MOVEMENT DPAD ENGAGED" , true);
            telemetry.addData("Magnitude DPAD: ", gamepadDrive.driveInput.magnitude);
            telemetry.addData("Angle DPAD: ", gamepadDrive.driveInput.angle);
        }
    }

 }
