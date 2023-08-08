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
    private double lastTheta;

    // --- Inertial --- //
    public final Inertials headingInertials;
    public final Inertials xAxisInertials;
    public final Inertials yAxisInertials;
    public final Inertials positionInertials;

    // --- Matrici --- //
    private Matrix rotatie;
    private Matrix exponentiala;
    private Matrix produsMatrix;
    private MatrixColumn displacement;
    private MatrixColumn delta;


    private MODE odometryMode = MODE.VECTORIAL;

    /// --- Constructors --- ///
    public Odometry(Heading heading, DcMotorEx leftEncoder, DcMotorEx rightEncoder, DcMotorEx centralEncoder) throws EncodersExceptions {
       this(new Position(), heading, leftEncoder, rightEncoder, centralEncoder);
    }

    public Odometry(Position startPosition, Heading heading, DcMotorEx leftEncoder, DcMotorEx rightEncoder, DcMotorEx centralEncoder) throws EncodersExceptions {

        this.startPosition   = startPosition;
        this.currentPosition = new Position();

        this.headingInertials  = new Inertials();
        this.xAxisInertials    = new Inertials();
        this.yAxisInertials    = new Inertials();
        this.positionInertials = new Inertials();

        this.rotatie      = new Matrix();
        this.exponentiala = new Matrix();
        this.produsMatrix = new Matrix();
        this.displacement  = new MatrixColumn();
        this.delta         = new MatrixColumn();

        this.lastTheta = startPosition.theta;

        this.heading = heading;

        this.encoders = new EncodersHandler(leftEncoder, rightEncoder, centralEncoder, heading);

        if(heading == null)
            this.heading = encoders.getHeading();
    }

    public Odometry(Position startPosition, Heading heading, Encoder leftEncoder, Encoder rightEncoder, Encoder centralEncoder) throws EncodersExceptions {

        this.startPosition   = startPosition;
        this.currentPosition = new Position();

        this.headingInertials  = new Inertials();
        this.xAxisInertials    = new Inertials();
        this.yAxisInertials    = new Inertials();
        this.positionInertials = new Inertials();

        this.rotatie      = new Matrix();
        this.exponentiala = new Matrix();
        this.produsMatrix = new Matrix();
        this.displacement  = new MatrixColumn();
        this.delta         = new MatrixColumn();

        this.lastTheta = startPosition.theta;

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
        double currentTheta  = startPosition.theta + headingValue;
        headingInertials.updateInertials(headingValue);

        // Compute Delta Theta & Remember Absolute Theta for next Iterations
        double phi = currentTheta - lastTheta;
        lastTheta  = currentTheta;

        // Compute Displacement
        double centralDisplacement     = encoders.getDeltaDistance(phi);
        double horizontalDisplacement  = deltaCentral - phi * OdometryConstants.centralLength;

        switch (odometryMode) {
            case VECTORIAL:
                vectorialOdometryEquation(currentPosition.theta, horizontalDisplacement, centralDisplacement); break;
            case VECTORIAL_EXPONENTIAL:
                vectorialOdometryEquationExponentials(currentPosition.theta, phi, horizontalDisplacement, centralDisplacement);
        }

        // Update Inertials - X & Y Axis
        xAxisInertials.updateInertials(delta.R1);
        yAxisInertials.updateInertials(delta.R2);
        positionInertials.updateInertials(Math.hypot(delta.R1, delta.R2));

        // Update Position
        currentPosition.incrementPosition(delta.R1, delta.R2);
        currentPosition.theta  = currentTheta;

        return currentPosition;
    }

    // Ecuatii de Odometrie
    private void vectorialOdometryEquation(double theta, double horizontalDisplacement, double centralDisplacement) {

        rotatie.createRotationMatrix(theta);
        displacement.setValues(horizontalDisplacement, centralDisplacement);

        MatrixColumn.multiply(rotatie, displacement, delta);
    }

    private void vectorialOdometryEquationExponentials(double theta, double phi, double horizontalDisplacement, double centralDisplacement) {

        rotatie.createRotationMatrix(theta);
        exponentiala.createExponentialMatrix(phi);
        displacement.setValues(horizontalDisplacement, centralDisplacement);

        Matrix.multiply(rotatie, exponentiala, produsMatrix);

        MatrixColumn.multiply(produsMatrix, displacement, delta);
    }

    // Getters
    public Position getPosition() {

        return currentPosition;
    }

    public enum MODE {
        VECTORIAL,
        VECTORIAL_EXPONENTIAL
    }

    static class Matrix {
        public double E11 = 0, E12 = 0 , E21 = 0, E22 = 0;

        public void createRotationMatrix(double theta) {
            this.E11 =  Math.cos(theta);
            this.E12 = -Math.sin(theta);
            this.E21 =  Math.sin(theta);
            this.E22 =  Math.cos(theta);
        }

        public void createExponentialMatrix(double phi) {
            this.E11 =     Math.sin(phi)   / phi;
            this.E12 = (Math.cos(phi) - 1) / phi;
            this.E21 = - this.E12;
            this.E22 =   this.E11;
        }

        public static void multiply(Matrix first, Matrix second, Matrix result) {

            result.E11 = first.E11 * second.E11 + first.E12 * second.E21;
            result.E12 = first.E11 * second.E12 + first.E12 * second.E22;
            result.E21 = first.E21 * second.E11 + first.E22 * second.E21;
            result.E22 = first.E21 * second.E12 + first.E22 * second.E22;

        }
    }

    static class MatrixColumn {
        public double R1 = 0, R2 = 0;

        public void setValues(double firstRow, double secondRow) {
            this.R1 = firstRow;
            this.R2 = secondRow;
        }

        public static void multiply(Matrix matrix, MatrixColumn column, MatrixColumn result) {

            result.R1 = matrix.E11 * column.R1 + matrix.E12 * column.R2;
            result.R2 = matrix.E21 * column.R1 + matrix.E22 * column.R2;

        }
    }

}
