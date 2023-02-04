package modules.drive.subsystems.teleop;

import static modules.drive.configuration.DriveSystemConfiguration.centricMode;
import static modules.drive.configuration.DriveSystemConfiguration.realignMode;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.Range;

import modules.drive.configuration.DriveSetup;
import modules.drive.configuration.DriveSystemConfiguration;
import modules.drive.configuration.MotorsConstants;
import modules.drive.configuration.modes.CentricMode;
import modules.drive.main.Motors;
import modules.gamepad.GamepadDrive;
import modules.gamepad.configuration.ActivationInput;
import modules.gamepad.configuration.GamepadActivation;
import modules.imu.AngleArithmetic;
import modules.odometry.Heading;

public class Drivebase {

    private final LinearOpMode opMode;

    private final Motors motors;

    private final SpeedChangerSystem speedChanger;

    private final BoostSystem boostSystem;

    private final GamepadDrive gamepad;

    private final Heading heading;

    // Magnitudes
    private double rotationMagnitude = 0;
    private double movementMagnitude = 0;

    // Angle
    private double angle = 0;

    // Max Power
    private double maximumMovementPower = 0;
    private double maximumRotationPower = 0;

    // Realign
    private double realignAngle = 0;
    private boolean isRealigning = false;

    // Headings
    private double headingRobot = 0;
    private double headingSpin  = 0;
    private double headingField = 0;

    // Locker
    public boolean isLocked = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Drivebase(LinearOpMode opMode, Heading heading) {
        this(opMode, heading, new Motors(opMode.hardwareMap));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Drivebase(LinearOpMode opMode, Heading heading, Motors motors) {

        this.opMode = opMode;

        this.motors = motors;

        this.speedChanger = new SpeedChangerSystem();

        this.boostSystem  = new BoostSystem();

        this.gamepad = new GamepadDrive(opMode.gamepad1);

        this.heading = heading;

        DriveSetup.initSetup();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void control() {

        gamepad.updateInput();

        isLocked = gamepad.driveInput.locked;

        if(!isLocked) {

            updateMagnitudesAndAngle();

            normalMovement();

            realign();

            detectSpeedType();

            applyPower();

        } else motors.stopPower();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateMagnitudesAndAngle() {

        movementMagnitude = gamepad.driveInput.magnitude + gamepad.driveInput.magnitudeTouch;

        movementMagnitude = DriveSystemConfiguration.alterMovementMagnitude.apply(movementMagnitude);

        rotationMagnitude = gamepad.driveInput.rotate + gamepad.driveInput.spin;

        rotationMagnitude = DriveSystemConfiguration.alterRotationMagnitude.apply(rotationMagnitude);

        angle = gamepad.driveInput.angle + gamepad.driveInput.angleTouchpad;
    }

    // === Normal Movement === //
    private void normalMovement() {
        calculatePowers();

        speedChanger.execute(gamepad.driveInput.increment, gamepad.driveInput.decrement);
    }

    // === Calculate Powers === //
    private void calculatePowers() {

        computeRotatePower(rotationMagnitude);

        if (gamepad.driveInput.spinTriggers)
            spinMovement();
        else {
            prevSpinState = false;
            simpleMovement();
        }
    }

    // === 2D + Rotation Movement === //
    private void simpleMovement() {

        if (centricMode == CentricMode.FIELD_CENTRIC)
              headingField = heading.getHeading();

        motors.updateMovementPower(angle - headingField, movementMagnitude);
    }

    // === Rotate Power Calculator for TeleOp === //
    private void computeRotatePower(double xAxis) {

        motors.updateRotatePower(xAxis);

        rotationMagnitude = Math.abs(xAxis);
    }

    // === Spin Movement === //

    private void spinMovement() {

        headingRobot = heading.getHeading();

        switch (DriveSystemConfiguration.centricModeSpin) {
            case FIELD_CENTRIC:
                fieldCentricSpin();
                break;
            case ROBOT_CENTRIC:
                robotCentricSpin();
                break;
        }

        motors.updateMovementPower(angle - headingSpin, movementMagnitude);
    }

    // === Spin Case: Field Centric === ///
    private void fieldCentricSpin() {

        headingSpin = headingRobot;
    }

    // === Spin Case: Robot Centric === ///
    private boolean prevSpinState = false;
    private double startHeading = 0;

    private void robotCentricSpin() {

        if (!prevSpinState) {
            prevSpinState = true;
            startHeading = headingRobot;
        }

        headingSpin = headingRobot - startHeading;
    }

    // === Realign with Bumpers === //
    private void realign() {

        if (GamepadActivation.REALIGN == ActivationInput.ACTIVE && realignMode() && rotationMagnitude == 0) {

            if (gamepad.driveInput.memoratePosition && !isRealigning && movementMagnitude == 0) { // Memorate Angle
                    realignAngle = heading.getHeading();
            }

            if (gamepad.driveInput.approachPosition) { // execute to Target
                isRealigning = true;
            }

            if (isRealigning) {
                sendRealignPowers();
            }
        } else isRealigning = false;
    }

    private void sendRealignPowers() {
        double degrees = AngleArithmetic.getShortestAngleDEGREES(Math.toDegrees(heading.getHeading()), Math.toDegrees(realignAngle));

        if (Math.abs(degrees) > 0.1f) {
            double speed = Math.signum(-degrees) * Range.clip(Math.abs(degrees) / 90.0f, 0.15f, 1.0f); // Will be transformed to graduated acceleration - deceleration

            computeRotatePower(speed);
        } else isRealigning = false;
    }

    private boolean realignMode() {

        switch (realignMode) {
            case WITH_MOVEMENT: return true;
            case WITHOUT_MOVEMENT: return movementMagnitude == 0;
        }

        return movementMagnitude == 0;
    }

    // === Choose Maxim Power - Joystick or Touchpad === ///
    private void detectSpeedType() {

        maximumRotationPower = MotorsConstants.MAX_ROTATE_SPEED;

        if (gamepad.driveInput.movementStick) {
            maximumMovementPower = boostSystem.execute(gamepad.driveInput.boost);
            return;
        }

        if (gamepad.driveInput.touchpad) {
            maximumMovementPower = MotorsConstants.TOUCHPAD_SPEED;
            return;
        }

        if (gamepad.driveInput.movement_dpad) {
            maximumMovementPower = MotorsConstants.DPAD_SPEED;
            return;
        }

        maximumMovementPower = boostSystem.execute(gamepad.driveInput.boost);
    }

    // === Apply Powers for TeleOp === ///
    private void applyPower() {

        if (rotationMagnitude + movementMagnitude > 0) {
            motors.frontLeft.setPower(weightedPower(motors.bluePower, maximumMovementPower, movementMagnitude, motors.leftPower, maximumRotationPower, rotationMagnitude) * gamepad.driveInput.speedMultiplier);
            motors.frontRight.setPower(weightedPower(motors.redPower, maximumMovementPower, movementMagnitude, motors.rightPower, maximumRotationPower, rotationMagnitude) * gamepad.driveInput.speedMultiplier);
            motors.backRight.setPower(weightedPower(motors.bluePower, maximumMovementPower, movementMagnitude, motors.rightPower, maximumRotationPower, rotationMagnitude) * gamepad.driveInput.speedMultiplier);
            motors.backLeft.setPower(weightedPower(motors.redPower, maximumMovementPower, movementMagnitude, motors.leftPower, maximumRotationPower, rotationMagnitude) * gamepad.driveInput.speedMultiplier);
        } else motors.stopPower();
    }

    // === Weighted Sum for Power === //
    private double weightedPower(double movementCoefficientPower, double movementLimitPower, double movementWeight,
                                 double rotateCoefficientPower, double rotateLimitPower, double rotateWeight) {

        return (movementCoefficientPower * movementLimitPower * movementWeight + rotateCoefficientPower * rotateLimitPower * rotateWeight) /
                                                               (movementWeight + rotateWeight);
    }

    public void telemetry() {

        opMode.telemetry.addData("Movement Magnitude", movementMagnitude);
        opMode.telemetry.addData("Rotation Magnitude", rotationMagnitude);

        opMode.telemetry.addData("Movement Max Power", maximumMovementPower);
        opMode.telemetry.addData("Rotation Max Power", maximumRotationPower);

        opMode.telemetry.addData("Blue Power", motors.bluePower);
        opMode.telemetry.addData("Red Power",  motors.redPower);

    }

}
