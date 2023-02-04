package modules.drive.configuration;

public class MotorsConstants {

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
