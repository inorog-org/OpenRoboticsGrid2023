package opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import manipulation.Intake;
import modules.drive.subsystems.teleop.Drivebase;
import modules.gamepad.buttons.Button;
import modules.gamepad.buttons.StickyButton;
import modules.imu.IMU;

@TeleOp(name = "Timisoara - TeleOp", group = "Concept")
public class Timisoara extends LinearOpMode {

    private Drivebase drivebase;
    private IMU imu;

    private Intake intake;
    private Arm arm;
    @Override
    public void runOpMode() throws InterruptedException {

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        imu = new IMU(this, BNO055IMU.AngleUnit.RADIANS, BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC);
        drivebase = new Drivebase(this, imu);
        intake = new Intake(hardwareMap, gamepad2);
        arm = new Arm(this);

        waitForStart();

        while (opModeIsActive()) {

            drivebase.control();
            intake.teleOpIntake();
            arm.teleOpArm();

            drivebase.telemetry();

            telemetry.addData("Aspirator", intake.currentState);
            telemetry.update();
        }
    }
}

class Arm {

    private Gamepad gamepad;

    private Telemetry telemetry;
    private Servo claw;
    private DcMotor elevator;
    private double CLOSE_CLAW = 0.4;
    private double OPEN_CLAW  = 0.7;
    private double SPEED = 0.6;

    private Button clawButton;
    private State clawState;

    public Arm(LinearOpMode opMode) {
        claw = opMode.hardwareMap.get(Servo.class, "claw");
        elevator = opMode.hardwareMap.get(DcMotor.class, "elevator");
        elevator.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        claw.setPosition(OPEN_CLAW);
        clawState = State.OPEN;

        gamepad = opMode.gamepad2;
        clawButton = new StickyButton(()-> gamepad.y);

        this.telemetry = opMode.telemetry;
    }

    enum State {
        CLOSE,
        OPEN
    }

    public void teleOpArm() {
        elevator.setPower(-gamepad.left_stick_y * SPEED);

        if(clawButton.listen()) {
            if(clawState == State.OPEN) {
               clawState = State.CLOSE;
               claw.setPosition(CLOSE_CLAW);
            } else {
               clawState = State.OPEN;
               claw.setPosition(OPEN_CLAW);
            }
        }

        telemetry.addData("State Claw", clawState.toString());

    }
}
