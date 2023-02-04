package modules.drive.subsystems.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import modules.drive.configuration.MotorsConstants;
import modules.drive.Motors;
import modules.imu.AngleArithmetic;
import modules.odometry.Heading;


/**
 *  Această clasă se folosește de Kinematică inversă pentru a face robotul să se miște în timpul Autonomiei.
 *
 * */
public class AutonomousBase {

    public LinearOpMode opMode;

    private final Motors motors;

    private final AccelerationSystem accelerationSystem;

    private final Heading heading;

    private double coefficientBlue;
    private double coefficientRed;

    public AutonomousBase(Motors motors, LinearOpMode opMode, Heading heading){
        this.motors = motors;

        this.accelerationSystem = new AccelerationSystem(opMode, motors, 1000, 1200, new ElapsedTime());

        this.opMode = opMode;

        this.heading = heading;
    }

    // === Movement 2D === ///

    public void setMovement2D(double distance, double angle, double speed){

        angle = Math.toRadians(angle + 90);

        setCoefficients(angle);

        setTargetPosition(distance);

        motors.updateMovementPowerNORMAL(angle, speed);

        motors.setupEncoders(DcMotor.RunMode.RUN_TO_POSITION);

        accelerationSystem.start(speed, distance, Math.max(Math.abs(coefficientBlue), Math.abs(coefficientRed)));

        motors.stopPower();
        motors.setupEncoders(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void setTargetPosition(double distance){

        motors.frontLeft.setTargetPosition(motors.frontLeft.getCurrentPosition()   + (int)(distance * MotorsConstants.COUNTS_PER_CM * coefficientBlue));
        motors.frontRight.setTargetPosition(motors.frontRight.getCurrentPosition() + (int)(distance * MotorsConstants.COUNTS_PER_CM * coefficientRed));
        motors.backRight.setTargetPosition(motors.backRight.getCurrentPosition()   + (int)(distance * MotorsConstants.COUNTS_PER_CM * coefficientBlue));
        motors.backLeft.setTargetPosition(motors.backLeft.getCurrentPosition()     + (int)(distance * MotorsConstants.COUNTS_PER_CM * coefficientRed));
    }

    private void setCoefficients(double angle){

        double sinus   = Math.sin(angle);
        double cosinus = Math.cos(angle);

        coefficientBlue = motors.mecanumConfig_FIRSTCOEFF  * cosinus + sinus;
        coefficientRed  = motors.mecanumConfig_SECONDCOEFF * cosinus + sinus;
    }

    // === Rotation === ///

    public void setRotationFromCurrentHeading(double angle ,double speed){

        double startAngle = Math.toDegrees(heading.getHeading());
        double degrees = AngleArithmetic.getShortestAngleDEGREES(startAngle + angle, startAngle);

        while((int) Math.abs(degrees) > 0 && opMode.opModeIsActive() && !opMode.isStopRequested()) {

            degrees = AngleArithmetic.getShortestAngleDEGREES(Math.toDegrees(heading.getHeading()), startAngle + angle);

            double coefficient = Math.signum(degrees) * Range.clip(Math.abs(degrees) / 90.0f, 0.2f, speed); // Will be transformed to graduated acceleration - deceleration

            motors.frontRight.setPower(coefficient);
            motors.backRight.setPower( coefficient);
            motors.frontLeft.setPower(-coefficient);
            motors.backLeft.setPower( -coefficient);
        }

        if(opMode.isStopRequested() && !opMode.opModeIsActive()) {
            motors.stopPower();
            opMode.terminateOpModeNow();
        }

        motors.stopPower();
    }

    public void setRotationFromStartHeading(double angle ,double speed){

        double degrees = AngleArithmetic.getShortestAngleDEGREES(Math.toDegrees(heading.getHeading()), angle);

        while((int) Math.abs(degrees) > 0 && opMode.opModeIsActive() && !opMode.isStopRequested()) {

            degrees = AngleArithmetic.getShortestAngleDEGREES(Math.toDegrees(heading.getHeading()), angle);

            double coefficient = Math.signum(degrees) * Range.clip(Math.abs(degrees) / 90.0f, 0.2f, speed); // Will be transformed to graduated acceleration - deceleration

            motors.frontRight.setPower(coefficient);
            motors.backRight.setPower( coefficient);
            motors.frontLeft.setPower(-coefficient);
            motors.backLeft.setPower( -coefficient);
        }

        if(opMode.isStopRequested() && !opMode.opModeIsActive()) {
            motors.stopPower();
            opMode.terminateOpModeNow();
        }

        motors.stopPower();
    }


}
