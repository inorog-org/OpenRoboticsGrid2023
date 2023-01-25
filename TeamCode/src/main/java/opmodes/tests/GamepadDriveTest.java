package opmodes.tests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import modules.gamepad.GamepadDrive;

@TeleOp(name = "Lunatica: Gamepad", group = "Testus")
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

            telemetry.addData("Movement Magnitude ", gamepadDrive.driveInput.magnitude);
            telemetry.addData("Movement Angle ", Math.toDegrees(gamepadDrive.driveInput.angle));
            telemetry.addData("Movement Stick", gamepadDrive.driveInput.movementStick);

            telemetry.addData("Rotation Magnitude ", gamepadDrive.driveInput.rotate);
            telemetry.addData("Rotation Stick ", gamepadDrive.driveInput.rotationStick);

            spinData();

            touchpadData();

            realignButtons();

            boostButton();

            locker();

            speedChanger();

            telemetryColor();

            telemetry.update();

        }

    }

    public void spinData() {

        telemetry.addData("Spin Magnitude ", gamepadDrive.driveInput.spin);
        telemetry.addData("Spin Triggers ", gamepadDrive.driveInput.spinTriggers);
    }
    public void touchpadData() {

        telemetry.addData("Touchpad Magnitude ", gamepadDrive.driveInput.magnitudeTouch);
        telemetry.addData("Touchpad Angle ", Math.toDegrees(gamepadDrive.driveInput.angleTouchpad));
        telemetry.addData("Is touched ", gamepadDrive.driveInput.touchpad);
    }
    public void realignButtons() {

        if(gamepadDrive.driveInput.memoratePosition) {
            memorate++;
        }
        telemetry.addData("Memorate Position ", memorate);

        if(gamepadDrive.driveInput.approachPosition) {
            approach++;
        }
        telemetry.addData("Approach Position ", approach);
    }
    public void boostButton() {

        if(gamepadDrive.driveInput.boost) {
            boost++;
        }
        telemetry.addData("Boost ", boost);
    }
    public void locker () {

        telemetry.addData("Locked ", gamepadDrive.driveInput.locked);
    }
    public void speedChanger() {

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

 }
