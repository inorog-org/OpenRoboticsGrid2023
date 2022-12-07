package modules.imu;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class AngleArithmetic {

    // Shortest Angle
    public static double getShortestAngleDEGREES(double current, double target) {

        double angle = target - current;

        return (angle + 540.0f) % 360 - 180.0f;
    }

    public static double getShortestAngleRADIANS(double current, double target) {

        double angle = target - current;

        return (angle + 3 * Math.PI) % (2 * Math.PI) - Math.PI;
    }

    public static double getShortestAngle(double current, double target, @NonNull AngleUnit angleUnit) {

        switch (angleUnit) {
            case DEGREES:
                return getShortestAngleDEGREES(current, target);
            case RADIANS:
                return getShortestAngleRADIANS(current, target);
            default:
                return 0;
        }
    }

}
