package opmodes.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import modules.configuration.drive.MotorsConstants;

@TeleOp(name = "Lunatica: PID", group = "Testus")
@Config
@Disabled
public class PIDTest extends LinearOpMode {

    public static double Kp = 1, Kd = 0, Ki = 0, TARGET = 300;
    @Override
    public void runOpMode() throws InterruptedException {

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        PIDController pid = new PIDController(Kp,Ki,Kd);

        DcMotorEx motor = hardwareMap.get(DcMotorEx.class, "motor");
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();

        while(opModeIsActive()) {
            pid.setPID(Kp, Ki, Kd);
            double currentPosition = motor.getCurrentPosition();
            double pidVal = pid.calculate(currentPosition, TARGET);
            motor.setTargetPosition((int)TARGET);
            motor.setPower(pidVal);

            telemetry.addData("Position: ",currentPosition);
            telemetry.addData("PID: ",pidVal);
            telemetry.addData("Sum: ",pidVal + currentPosition);
            telemetry.update();
        }
    }
}