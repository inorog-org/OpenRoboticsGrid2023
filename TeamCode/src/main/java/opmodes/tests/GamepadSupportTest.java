package opmodes.tests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.lang.reflect.InvocationTargetException;

import modules.gamepad.support.GamepadSupport;

@TeleOp(name = "Lunatica: Gamepad Sticks", group = "Testus")
@Disabled
public class GamepadSupportTest extends LinearOpMode {

    private GamepadSupport gamepadSuS;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void runOpMode() throws InterruptedException, RuntimeException {

        try {
            gamepadSuS = new GamepadSupport(gamepad1);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        waitForStart();

        while (opModeIsActive()) {

            gamepadSuS.updateSticks();

            telemetry.addData("Left Stick Magnitude: ", gamepadSuS.left_stick.magnitude);
            telemetry.addData("Left Stick Angle: ", gamepadSuS.left_stick.angle);

            telemetry.addData("Right Stick Magnitude: ", gamepadSuS.right_stick.magnitude);
            telemetry.addData("Right Stick Angle: ", gamepadSuS.right_stick.angle);

            telemetry.update();
        }
    }
}
