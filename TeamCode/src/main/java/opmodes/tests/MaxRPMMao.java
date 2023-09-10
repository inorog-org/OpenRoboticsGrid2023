package opmodes.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "Lunatica: Max RPM TEST", group = "Testus")
@Disabled
public class MaxRPMMao extends LinearOpMode {

    public DcMotorEx motor;

    @Override
    public void runOpMode() throws InterruptedException {

        FtcDashboard dashboard = FtcDashboard.getInstance();

        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        motor = (DcMotorEx) hardwareMap.get(DcMotor.class, "mao");

        waitForStart();

        while (opModeIsActive()) {
            motor.setPower(gamepad1.right_stick_x);
            telemetry.addData("RPM", motor.getVelocity() / 28.0 * 60.0);
            telemetry.update();
        }

    }
}
