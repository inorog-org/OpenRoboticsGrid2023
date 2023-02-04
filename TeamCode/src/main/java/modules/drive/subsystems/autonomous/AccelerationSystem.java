package modules.drive.subsystems.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import modules.drive.configuration.MotorsConstants;
import modules.drive.Motors;


/**
 *  Accelerație folosing Kinematică Inversă.
 *
 * */
public class AccelerationSystem {

    private final LinearOpMode opMode;

    private final ElapsedTime time;

    private final Motors motors;

    private final int accelerationTime;
    private double midStageTime;
    private final int decelerationTime;

    private double startTime;

    private double DISTANCE_ACC_DEC;

    private final double BUFFER_TIME_MS = 20;
    private final double MINIMUM_SPEED  = 0.18;

    public AccelerationSystem(LinearOpMode opMode, Motors motors, int accelerationTime, int decelerationTime, ElapsedTime time) {
        this.opMode = opMode;

        this.motors = motors;

        this.time = time;

        this.accelerationTime = accelerationTime;
        this.decelerationTime = decelerationTime;
    }

    public void start(double power, double distance, double coefficient) {

        //  calculateMidStageTime(power, distance, coefficient);

        startTime = time.milliseconds();

        accelerationStage(power);

        setMotorColorPowers(power);

        while(motors.motorsAreBusy() && opMode.opModeIsActive() && !opMode.isStopRequested());

        if(opMode.isStopRequested() && !opMode.opModeIsActive()) {
            motors.stopPower();
            opMode.terminateOpModeNow();
        }

        //  midStage(power);

        //  decelerationStage(power);

    }

    private void accelerationStage(double power){

        while(time.milliseconds() < startTime + accelerationTime && motors.motorsAreBusy() && opMode.opModeIsActive() && !opMode.isStopRequested()){

            double x = (time.milliseconds() - startTime) / accelerationTime;

            x = equation(Range.clip(x, 0 ,1));

            setMotorColorPowers(x * power);
        }

        if(opMode.isStopRequested() && !opMode.opModeIsActive()) {
            motors.stopPower();
            opMode.terminateOpModeNow();
        }
    }

    private void midStage(double power){
        while (time.milliseconds() < startTime + accelerationTime + midStageTime && opMode.opModeIsActive() && !opMode.isStopRequested()){
            setMotorColorPowers(power);
        }

        if(opMode.isStopRequested() && !opMode.opModeIsActive()) {
            motors.stopPower();
            opMode.terminateOpModeNow();
        }
    }

    private void decelerationStage(double power){
        while(time.milliseconds() < startTime + accelerationTime + midStageTime + decelerationTime + BUFFER_TIME_MS){

            double x = (time.milliseconds() - (startTime + accelerationTime + midStageTime)) / (decelerationTime + BUFFER_TIME_MS);

            x = 1 - equation(Range.clip(x, 0 ,1));

            setMotorColorPowers(Range.clip(x * power, MINIMUM_SPEED,1)); /// TODO: Trebuie să luăm în considerare Torque-ul, RPM-ul real și PID-ul
        }
    }

    private void calculateMidStageTime(double power, double distance, double coefficient) {

        double DISTANCE_PER_SECOND_WITH_MAXPOWER = ((MotorsConstants.MOTOR_MAX_RPM / 60.0f) * MotorsConstants.WHEEL_DIAMETER_CM * Math.PI) / coefficient;

        DISTANCE_ACC_DEC = 0.5f * (power * DISTANCE_PER_SECOND_WITH_MAXPOWER) * (accelerationTime + decelerationTime) / 1000.0f;

        midStageTime = ((distance - DISTANCE_ACC_DEC) / (power * DISTANCE_PER_SECOND_WITH_MAXPOWER)) * 1000.0f;
    }

    private void setMotorColorPowers(double progressive){

        motors.frontLeft.setPower(motors.bluePower * progressive);
        motors.frontRight.setPower(motors.redPower * progressive);
        motors.backRight.setPower(motors.bluePower * progressive);
        motors.backLeft.setPower(motors.redPower   * progressive);
    }

    private double equation(double x){
        return 3 * x * x - 2 * x * x * x;
    }

}
