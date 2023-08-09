package modules.configuration.odometry;

import com.qualcomm.robotcore.hardware.DcMotor;

public class OdometryConstants {

    // --- Encoder TICKS_PER_REV --- //
    public static final int ENCODER_TICKS = 8192;

    public static final double TICKS_TO_CM = (Math.PI * OdometryConstants.WHEEL_DIAMETER) / OdometryConstants.ENCODER_TICKS;

    // --- Encoder TICKS_PER_REV --- //
    public static final double WHEEL_DIAMETER = 4.8;

    // --- Distance from Robot Center --- //
    public static final double leftLength    = OdometryConstants.measuredLeftLength    * OdometryConstants.LATERAL_LEFT_MULTIPLIER;
    public static final double rightLength   = OdometryConstants.measuredRightLength   * OdometryConstants.LATERAL_RIGHT_MULTIPLIER;
    public static final double centralLength = OdometryConstants.measuredCentralLength * OdometryConstants.FORWARD_MULTIPLIER;

    private static final double measuredLeftLength    = 11.7;
    private static final double measuredRightLength   = 10.7;
    private static final double measuredCentralLength = 3.20;

    // --- Rotation Direction --- //
    public static final DcMotor.Direction LEFT_ENCODER_DIR     = DcMotor.Direction.REVERSE;
    public static final DcMotor.Direction RIGHT_ENCODER_DIR    = DcMotor.Direction.FORWARD;
    public static final DcMotor.Direction CENTRAL_ENCODER_DIR  = DcMotor.Direction.REVERSE;

    // --- Coeficienti de calibrare --- // * idee luată de pe RoadRunner *
    public static double LEFT_MULTIPLIER     = 1;   // Coeficient de calibrare pentru Encoder Left
    public static double RIGHT_MULTIPLIER    = 1;   // Coeficient de calibrare pentru Encoder Right
    public static double CENTRAL_MULTIPLIER  = 1;   // Coeficient de calibrare pentru Encoder Central
    public static double LATERAL_LEFT_MULTIPLIER   = 1;   // Coeficient de calibrare pentru Lateral Distance
    public static double LATERAL_RIGHT_MULTIPLIER  = 1;   // Coeficient de calibrare pentru Lateral Distance
    public static double FORWARD_MULTIPLIER  = 1;   // Coeficient de calibrare pentru Central Length


}
