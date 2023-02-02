package modules.odometry;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import modules.odometry.configuration.OdometryConstants;
import modules.odometry.encoders.Encoder;
import modules.odometry.encoders.EncodersExceptions;
import modules.odometry.encoders.OdometryEncoders;

/**

 Clasa poate da handle la următoarele tipuri de Odometrii:
   - 2 Encodere + IMU (Community/Vectorial)
   - 2 Encodere + Heading inițiat separat cu Encodere - practic inițiere cu 3 Encodere (Community/Vectorial)
   - 3 Encodere (Community/Vectorial)

 * */


public class Odometry {

    // --- Odometry Encoders --- //
    public final OdometryEncoders encoders;

    // --- Heading --- //
    private Heading heading;

    // --- Postion --- //
    private final Position startPosition;
    private final Position currentPosition;

    // --- Previous Heading --- //
    private double currentAbsoluteTheta;

    // --- Inertial --- //
    public final Inertials headingInertials;
    public final Inertials xAxisInertials;
    public final Inertials yAxisInertials;
    public final Inertials positionInertials;

    private MODE odometryMode = MODE.VECTORIAL;

    /// --- Constructors --- ///
    public Odometry(Heading heading, DcMotorEx leftEncoder, DcMotorEx rightEncoder, DcMotorEx centralEncoder) throws EncodersExceptions {
       this(new Position(), heading, leftEncoder, rightEncoder, centralEncoder);
    }

    public Odometry(Position startPosition, Heading heading, DcMotorEx leftEncoder, DcMotorEx rightEncoder, DcMotorEx centralEncoder) throws EncodersExceptions {

        this.startPosition   = startPosition;
        this.currentPosition = new Position();

        this.headingInertials = new Inertials();
        this.xAxisInertials   = new Inertials();
        this.yAxisInertials   = new Inertials();
        this.positionInertials = new Inertials();

        this.currentAbsoluteTheta = startPosition.theta;

        this.heading = heading;

        this.encoders = new OdometryEncoders(leftEncoder, rightEncoder, centralEncoder, heading);

        if(heading == null)
            this.heading = encoders.getHeading();
    }

    public Odometry(Heading heading, Encoder leftEncoder, Encoder rightEncoder, Encoder centralEncoder) throws EncodersExceptions {
        this(new Position(), heading, leftEncoder, rightEncoder, centralEncoder);
    }

    public Odometry(Position startPosition, Heading heading, Encoder leftEncoder, Encoder rightEncoder, Encoder centralEncoder) throws EncodersExceptions {

        this.startPosition   = startPosition;
        this.currentPosition = new Position();

        this.headingInertials = new Inertials();
        this.xAxisInertials   = new Inertials();
        this.yAxisInertials   = new Inertials();
        this.positionInertials = new Inertials();

        this.currentAbsoluteTheta = startPosition.theta;

        this.heading = heading;

        this.encoders = new OdometryEncoders(leftEncoder, rightEncoder, centralEncoder, heading);

        if(heading == null)
            this.heading = encoders.getHeading();
    }

    // Set Mode
    public void setOdometryMode(MODE odometryMode) {
        this.odometryMode = odometryMode;
    }

    // Update Position
    public Position updatePosition() {

        // Update Encoder Values
        encoders.updateEncodersValues();

        // Get Delta Encoders Value
        double deltaCentral = encoders.centralEncoder.getDeltaDistance();

        // Get Heading + Update Inertials
        double headingValue  = heading.getHeading();
        double absoluteTheta = startPosition.theta + headingValue;
        headingInertials.updateInertials(headingValue);

        // Compute Delta Theta & Remember Absolute Theta for next Iterations
        double deltaTheta = absoluteTheta - currentAbsoluteTheta;
        currentAbsoluteTheta = absoluteTheta;

        switch (odometryMode) {
            case COMMUNITY:   communityOdometryEquation(absoluteTheta, deltaTheta, deltaCentral); break;
            case VECTORIAL:   vectorialOdometryEquation(deltaTheta, deltaCentral - absoluteTheta * OdometryConstants.centralLength, encoders.getDeltaDistance(deltaTheta)); break;
        }

        // Update Inertials - X & Y Axis
        xAxisInertials.updateInertials(deltaX);
        yAxisInertials.updateInertials(deltaY);
        positionInertials.updateInertials(Math.hypot(deltaX, deltaY));

        // Update Position
        currentPosition.incrementPosition(deltaX, deltaY);
        currentPosition.theta  = absoluteTheta;

        return currentPosition;
    }

    // Values for Increment
    private double deltaX = 0;
    private double deltaY = 0;

    // Ecuatii de Odometrie
    private void communityOdometryEquation(double absoluteTheta, double deltaTheta, double deltaCentral) {

        // Compute Incremental Values for Position Update
        if(deltaTheta == 0) {

            double y = encoders.getDeltaLateral();

            double cos = Math.cos(absoluteTheta);
            double sin = Math.sin(absoluteTheta);

            deltaX = deltaCentral * cos - y * sin;
            deltaY = deltaCentral * sin + y * cos;

        } else {

            double s = 2 * Math.sin(absoluteTheta / 2);

            double x   = s * (deltaCentral / deltaTheta   + OdometryConstants.centralLength);
            double y   = s * encoders.getLateralRadius(deltaTheta);

            double cos = Math.cos(absoluteTheta + deltaTheta / 2);
            double sin = Math.sin(absoluteTheta + deltaTheta / 2);

            deltaX = x * cos - y * sin;
            deltaY = x * sin + y * cos;
        }

    }

    private void vectorialOdometryEquation(double deltaTheta, double deltaCentral, double deltaDistance) {

        deltaX = deltaDistance * Math.cos(deltaTheta) - deltaCentral * Math.sin(deltaTheta);
        deltaY = deltaDistance * Math.sin(deltaTheta) + deltaCentral * Math.cos(deltaTheta);
    }

    // Getters
    public Position getPosition() {

        return currentPosition;
    }

    public double getDistanceFromOrigin() {

        double dX = currentPosition.x - startPosition.x;
        double dY = currentPosition.y - startPosition.y;

        return Math.sqrt(dX * dX + dY * dY);
    }

    public enum MODE {
        COMMUNITY,
        VECTORIAL
    }

}
