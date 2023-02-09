package modules.drive.subsystems.pursuit;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.Range;

import modules.drive.configuration.MotorsConstants;
import modules.drive.main.Motors;
import modules.drive.subsystems.pursuit.splines.Line;
import modules.odometry.OdometryHandler;

public class PurePursuit {

    private LinearOpMode opMode;

    public static final double PURSUIT_DISTANCE = 10.0;
    private Motors motors;
    private OdometryHandler odometryHandler;

    private double movementMagnitude = 0;

    public PurePursuit(LinearOpMode opMode,Motors motors, OdometryHandler odometryHandler) {
        this.opMode = opMode;
        this.motors = motors;
        this.odometryHandler = odometryHandler;
    }

    public void handleTrajectory(Line path) {

        double totalDistance = Math.sqrt(Math.pow(path.start.x - path.finish.x, 2) + Math.pow(path.start.y - path.finish.y, 2));

        do {
            path.updatePathData(odometryHandler.odometry.updatePosition());
            motors.updateMovementPowerNORMAL(path.getOrientation(), 1.0);
            // setMagnitude(totalDistance - path.getDistance(), totalDistance);
            movementMagnitude = 0.3;
            setPowerMotors();
        }while (path.getDistance() >= 0.4);
    }

    private void setPowerMotors() {

        motors.frontLeft.setPower(motors.bluePower * MotorsConstants.MAX_MOVEMENT_SPEED *  movementMagnitude);
        motors.frontRight.setPower(motors.redPower * MotorsConstants.MAX_MOVEMENT_SPEED * movementMagnitude);
        motors.backRight.setPower(motors.bluePower * MotorsConstants.MAX_MOVEMENT_SPEED * movementMagnitude);
        motors.backLeft.setPower(motors.redPower * MotorsConstants.MAX_MOVEMENT_SPEED * movementMagnitude);
    }

    private void setMagnitude(double distance, double totalDistance) {

        double coeff = Range.clip(distance / totalDistance, 0.0, 1.0);

        if(coeff <= 0.2) {
            movementMagnitude = equation(Range.clip(coeff / 0.2, 0.0, 1.0)); return;
        }

        if(coeff >= 0.2 && coeff <= 0.8) {
            movementMagnitude = 1.0; return;
        }

        if(coeff >= 0.8) {
            movementMagnitude = 1 - equation(Range.clip((1 - coeff) / 0.2, 0.0, 1.0)); return;
        }
    }

    private double equation(double x) {
        return 3 * x * x - 2 * x * x * x;
    }

}
