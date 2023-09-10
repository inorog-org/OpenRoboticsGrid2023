package opmodes.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.lang.annotation.Target;

import modules.configuration.drive.MotorsConstants;

@TeleOp(name = "Lunatica: PID", group = "Testus")
@Config
@Disabled
public class PIDTest extends LinearOpMode {

    public static double Kp = 0.00001, Kd = 0.0001, Ki = 0, TARGET = 6000, kF = 2800;

    private DcMotorEx motor;
    @Override
    public void runOpMode() throws InterruptedException {

        double oldTarget = TARGET;

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        PIDController pid = new PIDController(Kp, Ki, Kd);

        motor = (DcMotorEx) hardwareMap.get(DcMotor.class, "launcher");
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        double pidVal = pid.calculate(getRPM(), oldTarget) + kF;

        waitForStart();

        while (opModeIsActive()) {

            motor.setVelocity(pidVal);

            pid.setPID(Kp, Ki, Kd);
            double RPM = getRPM();
            pidVal += pid.calculate(RPM);

            if(oldTarget != TARGET) {
                oldTarget = TARGET;
                pidVal += pid.calculate(RPM, oldTarget);
            }

            telemetry.addData("RPM: ", RPM);
            telemetry.addData("PID: ", pidVal);
            telemetry.update();
        }
    }

    public double getRPM() {
        return motor.getVelocity() / 537.6 * 60.0;
    }
}