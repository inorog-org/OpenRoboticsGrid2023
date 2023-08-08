package modules.odometry;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import modules.configuration.odometry.OdometryConstants;
import modules.odometry.encoders.Encoder;
import modules.odometry.encoders.EncodersExceptions;
import modules.odometry.encoders.EncodersHandler;
import modules.odometry.utils.Heading;
import modules.odometry.utils.Inertials;
import modules.odometry.utils.Position;

/**

 Clasa poate da handle la următoarele tipuri de Odometrii:
   - 2 Encodere + IMU (Vectorial)
   - 2 Encodere + Heading inițiat separat cu Encodere - practic inițiere cu 3 Encodere (Vectorial)
   - 3 Encodere (Vectorial)

 * */


public class Odometry {

    // --- Handler pentru Encodere --- //
    public final EncodersHandler encoders;

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

        this.encoders = new EncodersHandler(leftEncoder, rightEncoder, centralEncoder, heading);

        if(heading == null)
            this.heading = encoders.getHeading();
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

        this.encoders = new EncodersHandler(leftEncoder, rightEncoder, centralEncoder, heading);

        if(heading == null)
            this.heading = encoders.getHeading();
    }

    public Odometry(Heading heading, Encoder leftEncoder, Encoder rightEncoder, Encoder centralEncoder) throws EncodersExceptions {
        this(new Position(), heading, leftEncoder, rightEncoder, centralEncoder);
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
            case VECTORIAL:
                vectorialOdometryEquation(currentPosition.theta, deltaCentral - deltaTheta * OdometryConstants.centralLength, encoders.getDeltaDistance(deltaTheta)); break;
            case VECTORIAL_EXPONENTIAL:
                vectorialOdometryEquationExponentials(currentPosition.theta, deltaTheta,deltaCentral - deltaTheta * OdometryConstants.centralLength, encoders.getDeltaDistance(deltaTheta));
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
    private void vectorialOdometryEquation(double theta, double deltaCentral, double deltaDistance) {

        deltaX = deltaDistance * Math.cos(theta) - deltaCentral * Math.sin(theta);
        deltaY = deltaDistance * Math.sin(theta) + deltaCentral * Math.cos(theta);
    }

    private void vectorialOdometryEquationExponentials(double theta, double deltaTheta, double deltaCentral, double deltaDistance) {

        double cosT = Math.cos(theta);
        double sinT = Math.sin(theta);

        double cosDT = Math.cos(deltaTheta);
        double sinDT = Math.sin(deltaTheta);

        double a = cosT * sinDT + sinT * cosDT - sinT;
        double b = cosT * cosDT - sinT * sinDT - cosT;

        deltaX = ( deltaDistance * a + deltaCentral * b) / deltaTheta;
        deltaY = (-deltaDistance * b + deltaCentral * a) / deltaTheta;
    }

    // Getters
    public Position getPosition() {

        return currentPosition;
    }

    public enum MODE {
        VECTORIAL,
        VECTORIAL_EXPONENTIAL
    }

}
