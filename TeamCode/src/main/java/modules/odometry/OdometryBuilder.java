package modules.odometry;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import modules.imu.IMU;
import modules.configuration.odometry.OdometryConstants;
import modules.odometry.encoders.Encoder;
import modules.odometry.encoders.EncodersExceptions;
import modules.odometry.encoders.HeadingEncoders;
import modules.odometry.utils.Heading;

/**
*    Clasa OdometryBuilder are drept scop inițializarea rapidă a unei odometrii cu 3 Encodere (+IMU/Heading).
* */

public class OdometryBuilder {

    public final Odometry odometry;

    public final Heading heading;
    public final Encoder centralEncoder;
    public final Encoder leftEncoder;
    public final Encoder rightEncoder;

    public OdometryBuilder(LinearOpMode opMode) {

        // Init Encoders
        centralEncoder = new Encoder(opMode.hardwareMap.get(DcMotorEx.class, "central"), OdometryConstants.CENTRAL_MULTIPLIER, OdometryConstants.CENTRAL_ENCODER_DIR);
        leftEncoder    = new Encoder(opMode.hardwareMap.get(DcMotorEx.class, "left"), OdometryConstants.LEFT_MULTIPLIER, OdometryConstants.LEFT_ENCODER_DIR);
        rightEncoder   = new Encoder(opMode.hardwareMap.get(DcMotorEx.class, "right"), OdometryConstants.RIGHT_MULTIPLIER, OdometryConstants.RIGHT_ENCODER_DIR);

        // Init Heading
        heading = new HeadingEncoders(leftEncoder, rightEncoder);

        try {
            odometry = new Odometry(heading, leftEncoder, rightEncoder, centralEncoder); // Init Odometry
        } catch (EncodersExceptions e) {
            throw new RuntimeException(e);
        }
    }

    public OdometryBuilder(LinearOpMode opMode, IMU imu) {

        // Init Encoders
        centralEncoder = new Encoder(opMode.hardwareMap.get(DcMotorEx.class, "central"), OdometryConstants.CENTRAL_MULTIPLIER, OdometryConstants.CENTRAL_ENCODER_DIR);
        leftEncoder    = new Encoder(opMode.hardwareMap.get(DcMotorEx.class, "left"), OdometryConstants.LEFT_MULTIPLIER, OdometryConstants.LEFT_ENCODER_DIR);
        rightEncoder   = new Encoder(opMode.hardwareMap.get(DcMotorEx.class, "right"), OdometryConstants.RIGHT_MULTIPLIER, OdometryConstants.RIGHT_ENCODER_DIR);

        // Init Heading
        heading = imu;

        try {
            odometry = new Odometry(heading, leftEncoder, rightEncoder, centralEncoder); // Init Odometry
        } catch (EncodersExceptions e) {
            throw new RuntimeException(e);
        }
    }

    public OdometryBuilder(LinearOpMode opMode, Heading heading) {

        // Init Encoders
        centralEncoder = new Encoder(opMode.hardwareMap.get(DcMotorEx.class, "central"), OdometryConstants.CENTRAL_MULTIPLIER, OdometryConstants.CENTRAL_ENCODER_DIR);
        leftEncoder    = new Encoder(opMode.hardwareMap.get(DcMotorEx.class, "left"), OdometryConstants.LEFT_MULTIPLIER, OdometryConstants.LEFT_ENCODER_DIR);
        rightEncoder   = new Encoder(opMode.hardwareMap.get(DcMotorEx.class, "right"), OdometryConstants.RIGHT_MULTIPLIER, OdometryConstants.RIGHT_ENCODER_DIR);

        // Init Heading
        this.heading = heading;

        try {
            odometry = new Odometry(heading, leftEncoder, rightEncoder, centralEncoder); // Init Odometry
        } catch (EncodersExceptions e) {
            throw new RuntimeException(e);
        }
    }

}
