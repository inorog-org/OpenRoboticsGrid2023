package opmodes;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import modules.drive.subsystems.teleop.Drivebase;
import modules.imu.IMU;

@TeleOp(name = "Concept: Mecanum Lunatica + Extra+ org2025", group = "Concept")

public class MotorTimeTest extends LinearOpMode {

    private Drivebase drivebase;
    private IMU imu;

    // === Motoare suplimentare ===
    private DcMotor motorStanga;
    private DcMotor motorDreapta;

    // === Servo gheară ===
    private Servo clawLeft;
    private Servo clawRight;

    // Poziții presetate pentru gheară
    private final double CLAW_OPEN = 0.8;   // deschis
    private final double CLAW_CLOSED = 0.2; // închis

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void runOpMode() throws InterruptedException {

        // === Inițializare IMU + drivebase ===
        imu = new IMU(this, BNO055IMU.AngleUnit.RADIANS, BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC);
        imu.remapAxis(0x24, 0xc);
        drivebase = new Drivebase(this, imu);

        // === Inițializare hardware suplimentar ===
        motorStanga = hardwareMap.get(DcMotor.class, "motorStanga");
        motorDreapta = hardwareMap.get(DcMotor.class, "motorDreapta");

        clawLeft = hardwareMap.get(Servo.class, "clawLeft");
        clawRight = hardwareMap.get(Servo.class, "clawRight");

        // Direcții motoare (dacă e nevoie să inversezi unul)
        motorStanga.setDirection(DcMotor.Direction.FORWARD);
        motorDreapta.setDirection(DcMotor.Direction.FORWARD);

        waitForStart();

        while (opModeIsActive()) {

            // === Control drivebase existent ===
            drivebase.control();
            drivebase.telemetry();

            // === Control motoare suplimentare cu joystick gamepad2 ===
            double powerStanga = -gamepad2.left_stick_y;   // pe stânga
            double powerDreapta = -gamepad2.right_stick_y; // pe dreapta

            motorStanga.setPower(powerStanga);
            motorStanga.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            motorDreapta.setPower(powerDreapta);
            motorDreapta.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


            // === Control servo gheară cu bumpere ===
            if (gamepad2.left_bumper) {
                clawLeft.setPosition(CLAW_CLOSED);
                clawRight.setPosition(CLAW_OPEN);
            } else if (gamepad2.right_bumper) {
                clawLeft.setPosition(CLAW_OPEN);
                clawRight.setPosition(CLAW_CLOSED);
            }

            telemetry.addData("Motor Stanga", powerStanga);
            telemetry.addData("Motor Dreapta", powerDreapta);
            telemetry.addData("Claw Left Pos", clawLeft.getPosition());
            telemetry.addData("Claw Right Pos", clawRight.getPosition());
            telemetry.update();
        }
    }
}
