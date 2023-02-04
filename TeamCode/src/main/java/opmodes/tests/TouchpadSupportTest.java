package opmodes.tests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import modules.gamepad.touchpad.TouchpadSupport;

@TeleOp(name = "Lunatica: Touchpad", group = "Testus")
@Disabled
public class TouchpadSupportTest extends LinearOpMode {

    private TouchpadSupport touchpadSuS;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void runOpMode() throws InterruptedException {

        touchpadSuS =  new TouchpadSupport(() -> (double) gamepad1.touchpad_finger_1_x, () -> (double) gamepad1.touchpad_finger_1_y, () -> gamepad1.touchpad_finger_1);

        waitForStart();

        while (opModeIsActive()) {

            touchpadSuS.updateData();

            telemetry.addData("Magnitude ", touchpadSuS.magnitude);
            telemetry.addData("Angle ", touchpadSuS.angle);
            telemetry.addData("X ", touchpadSuS.x);
            telemetry.addData("Y ", touchpadSuS.y);
            telemetry.addData("Is touched ", touchpadSuS.isTouched);

            telemetry.update();
        }
    }
}
