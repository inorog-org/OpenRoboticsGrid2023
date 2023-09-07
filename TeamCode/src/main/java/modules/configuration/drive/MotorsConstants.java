package modules.configuration.drive;

public class MotorsConstants {

    // Wheels Distances from Center
    public static double WIDTH   = 10.0;
    public static double HEIGHT  = 10.0;

    public static double COUNTS_PER_MOTOR_REV   =  384.5;
    public static double DRIVE_GEAR_REDUCTION   =   1.0 ;
    public static double   WHEEL_DIAMETER_CM    =   9.6 ;
    public static double   COUNTS_PER_CM        = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                                                        (WHEEL_DIAMETER_CM * Math.PI);

    public static double   ROLLER_COEFF            = 10.0 / 9.0;  // Este egală cu ctg(alpha) unde alpha este unghiul la care sunt dispuse rollerele de pe Mecanum
    public static double  MOTOR_MAX_RPM            =  435; // In fisa tehnica asa scrie

    // Powers for Motors
    public static double MAX_MOVEMENT_SPEED = 0.8;
    public static double MAX_ROTATE_SPEED   = 0.8;

    // Touchpad Power for Motors
    public static final double TOUCHPAD_SPEED = 0.5;

    // Digital Pad Power for Motors
    public static final double DPAD_SPEED = 0.6;

    // MAX-MIN Power
    public static final double MOTOR_MAX_POWER = 1.0;
    public static final double MOTOR_MIN_POWER = 0.2;
    public static final double MOTOR_SPEED_INCREMENT = 0.2;

    // Boost Time
    public static final int boostTime = 2000; // in ms

    public static final double BOOST_SPEED = 1.0;


}
