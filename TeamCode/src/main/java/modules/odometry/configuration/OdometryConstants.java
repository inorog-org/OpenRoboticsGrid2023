package modules.odometry.configuration;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class OdometryConstants {

    // --- Encoder TICKS_PER_REV --- //
    public static final int ENCODER_TICKS = 8192;

    // --- Encoder TICKS_PER_REV --- //
    public static final double WHEEL_DIAMETER = 3.81; // 3.81 cm - 1.5 inch

    // --- Distance from Robot Center --- //
    public static final double leftLength    = 17.5;  // 17.5 cm
    public static final double rightLength   = 17.5;  // 17.5 cm
    public static final double centralLength = 17.25; // 17.25 cm

    // --- Rotation Direction --- //
    public static final DcMotor.Direction LEFT_ENCODER_DIR     = DcMotor.Direction.FORWARD;
    public static final DcMotor.Direction RIGHT_ENCODER_DIR    = DcMotor.Direction.FORWARD;
    public static final DcMotor.Direction CENTRAL_ENCODER_DIR  = DcMotor.Direction.FORWARD;

    // --- Coeficienti de calibrare --- // * idee luată de pe RoadRunner *
    public static double X_MULTIPLIER      = 1;   // Coeficient de calibrare pentru Axa X
    public static double Y_MULTIPLIER      = 1;   // Coeficient de calibrare pentru Axa Y
    public static double LATERAL_DISTANCE  = 1;   // Coeficient de calibrare pentru Encoderele Heading - Calibrăm Raza
    public static double FORWARD_OFFSET    = 1;   // Coeficient de calibrarea pentru mersul înainte

}
