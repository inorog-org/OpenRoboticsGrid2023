package opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@Autonomous(name="AutoDreaptaEficient", group="Linear Opmode")
public class AutonomieMecanumDreapta extends LinearOpMode {

    DcMotor frontLeft, frontRight, backLeft, backRight;
    DcMotor gliderSlide;
    DcMotor angleSlide;
    Servo clawLeft, clawRight;
    DistanceSensor distSensor;

    private final int TICKS_PER_CM = 45;

    // Claw half-open/closed
    private final double CLAW_LEFT_OPEN = 0.8;
    private final double CLAW_LEFT_CLOSED = 0.2;
    private final double CLAW_RIGHT_OPEN = 0.2;
    private final double CLAW_RIGHT_CLOSED = 0.8;

    @Override
    public void runOpMode() throws InterruptedException {

        frontLeft   = hardwareMap.get(DcMotor.class, "front_left");
        frontRight  = hardwareMap.get(DcMotor.class, "front_right");
        backLeft    = hardwareMap.get(DcMotor.class, "back_left");
        backRight   = hardwareMap.get(DcMotor.class, "back_right");
        gliderSlide = hardwareMap.get(DcMotor.class, "motorStanga");
        angleSlide = hardwareMap.get(DcMotor.class, "motorDreapta");
        clawLeft    = hardwareMap.get(Servo.class, "clawLeft");
        clawRight   = hardwareMap.get(Servo.class, "clawRight");
        distSensor  = hardwareMap.get(DistanceSensor.class, "distSensor");

        frontRight.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backRight.setDirection(DcMotor.Direction.FORWARD);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

        resetEncoders();

        while (!isStarted() && !isStopRequested()) {
            telemetry.addData("Dist [cm]", distSensor.getDistance(DistanceUnit.CM));
            telemetry.update();
        }

        waitForStart();

        if (opModeIsActive()) {

            // =========================
            // DETECTIE CUBURI
            // =========================
            boolean cubStanga = false, cubMijloc = false, cubDreapta = false;
            String mijlocHalf = "";

            // Pozitia Mijloc
            driveForward(45, 0.4);
            cubMijloc = checkCubePresence("Mijloc");


            // Pozitia Stanga
            strafeLeft(28, 0.4);
            cubStanga = checkCubePresence("Stanga");

            // Pozitia Dreapta
            strafeRight(56, 0.4); // 28cm de la mijloc + 28cm la dreapta
            cubDreapta = checkCubePresence("Dreapta");

            // =========================
            // COLECTARE CUBURI (direct)
            // =========================
            boolean leftHalfFree = true;
            boolean rightHalfFree = true;

            // Stanga
            if (cubStanga && leftHalfFree) {
                grabCubeHalf("LEFT");
                leftHalfFree = false;
            }

            // Mijloc
            if (cubMijloc) {
                if (leftHalfFree) {
                    grabCubeHalf("LEFT");
                    leftHalfFree = false;
                    mijlocHalf = "LEFT";
                } else if (rightHalfFree) {
                    grabCubeHalf("RIGHT");
                    rightHalfFree = false;
                    mijlocHalf = "RIGHT";
                }
            }

            // Dreapta
            if (cubDreapta && rightHalfFree) {
                grabCubeHalf("RIGHT");
                rightHalfFree = false;
            }

            // =========================
            // DEPUNERE CUBURI EFICIENT
            // =========================
            // Merge direct in zona finala (fara reveniri inutile)
            strafeRight(28, 0.4); // pozitionare centrala fata de zona depunere
            rotate(45, 0.4);
            driveForward(60, 0.4);

            // Stanga
            if (cubStanga) {
                moveGliderSlide(500, 0.5);
                angleSlide.setPower(0.5);
                sleep(1500);
                clawLeft.setPosition(CLAW_LEFT_OPEN);
                sleep(300);
                moveGliderSlide(0, 0.5);
                angleSlide.setPower(-0.5);
                sleep(1500);
            }

            // Mijloc
            if (cubMijloc) {
                moveGliderSlide(500, 0.5);
                angleSlide.setPower(0.5);
                sleep(1500);
                if (mijlocHalf.equals("LEFT")) clawLeft.setPosition(CLAW_LEFT_OPEN);
                else clawRight.setPosition(CLAW_RIGHT_OPEN);
                sleep(300);
                moveGliderSlide(0, 0.5);
                angleSlide.setPower(-0.5);
                sleep(1500);
            }

            // Dreapta
            if (cubDreapta) {
                moveGliderSlide(500, 0.5);
                angleSlide.setPower(0.5);
                sleep(1500);
                clawRight.setPosition(CLAW_RIGHT_OPEN);
                sleep(300);
                moveGliderSlide(0, 0.5);
                angleSlide.setPower(-0.5);
            }

            telemetry.addLine("Toate cuburile au fost depuse eficient!");
            telemetry.update();
        }
    }

    // ================= FUNCTII =================

    private boolean checkCubePresence(String poz) {
        double dist = distSensor.getDistance(DistanceUnit.CM);
        telemetry.addData("Detectie " + poz, "%.1f cm", dist);
        telemetry.update();
        sleep(400);
        return dist < 40;
    }

    private void grabCubeHalf(String half) {
        if (half.equals("LEFT")) clawLeft.setPosition(CLAW_LEFT_OPEN);
        else clawRight.setPosition(CLAW_RIGHT_OPEN);

        moveGliderSlide(500, 0.5);
        sleep(200);

        if (half.equals("LEFT")) clawLeft.setPosition(CLAW_LEFT_CLOSED);
        else clawRight.setPosition(CLAW_RIGHT_CLOSED);

        sleep(200);
        moveGliderSlide(0, 0.5);
    }

    private void resetEncoders() {
        frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void driveForward(double cm, double power) {
        int ticks = (int)(cm * TICKS_PER_CM);
        setTargetPositions(ticks, ticks, ticks, ticks);
        runToPos(power);
    }

    private void strafeLeft(double cm, double power) {
        int ticks = (int)(cm * TICKS_PER_CM);
        setTargetPositions(-ticks, ticks, ticks, -ticks);
        runToPos(power);
    }

    private void strafeRight(double cm, double power) {
        int ticks = (int)(cm * TICKS_PER_CM);
        setTargetPositions(ticks, -ticks, -ticks, ticks);
        runToPos(power);
    }

    private void rotate(double degrees, double power) {
        int TICKS_PER_DEG = 10;
        int targetTicks = (int)(degrees * TICKS_PER_DEG);

        frontLeft.setTargetPosition(frontLeft.getCurrentPosition() + targetTicks);
        backLeft.setTargetPosition(backLeft.getCurrentPosition() + targetTicks);
        frontRight.setTargetPosition(frontRight.getCurrentPosition() - targetTicks);
        backRight.setTargetPosition(backRight.getCurrentPosition() - targetTicks);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        setPowerAll(power);

        while (opModeIsActive() && (frontLeft.isBusy() || backLeft.isBusy() ||
                frontRight.isBusy() || backRight.isBusy())) {}

        stopAll();
    }

    private void setTargetPositions(int fl, int fr, int bl, int br) {
        frontLeft.setTargetPosition(frontLeft.getCurrentPosition() + fl);
        frontRight.setTargetPosition(frontRight.getCurrentPosition() + fr);
        backLeft.setTargetPosition(backLeft.getCurrentPosition() + bl);
        backRight.setTargetPosition(backRight.getCurrentPosition() + br);
    }

    private void runToPos(double power) {
        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        setPowerAll(power);
        while (opModeIsActive() && (frontLeft.isBusy() || frontRight.isBusy() ||
                backLeft.isBusy() || backRight.isBusy())) {}
        stopAll();
    }

    private void setPowerAll(double p) {
        frontLeft.setPower(p);
        frontRight.setPower(p);
        backLeft.setPower(p);
        backRight.setPower(p);
    }

    private void stopAll() { setPowerAll(0); }

    private void moveGliderSlide(int targetTicks, double power) {
        gliderSlide.setTargetPosition(targetTicks);
        gliderSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        gliderSlide.setPower(power);
        while (opModeIsActive() && gliderSlide.isBusy()) {}
        gliderSlide.setPower(0);
    }
}
