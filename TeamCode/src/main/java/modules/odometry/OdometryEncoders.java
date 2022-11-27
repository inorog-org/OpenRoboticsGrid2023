package modules.odometry;

import android.opengl.Matrix;
import android.renderscript.Matrix2f;

import com.qualcomm.robotcore.hardware.DcMotorEx;

public class OdometryEncoders {

    // --- External Encoders --- //
    private final Encoder leftEncoder;
    private final Encoder rightEncoder;
    private final Encoder centralEncoder;

    // --- Postion --- //
    private Position startPosition;
    private Position currentPosition;

    // --- Previous Heading --- //
    private double previousAbsoluteTheta;

    // --- Distance from Robot Center --- //
    private final double leftLength    = 17.5;  // 17.5 cm
    private final double rightLength   = 17.5;  // 17.5 cm
    private final double headingLength = 17.25; // 17.25 cm

    // --- Constructors --- //
    public OdometryEncoders(DcMotorEx leftEncoder, DcMotorEx rightEncoder, DcMotorEx centralEncoder){

        this.startPosition   = new Position();
        this.currentPosition = new Position();

        this.leftEncoder    = new Encoder(leftEncoder);
        this.rightEncoder   = new Encoder(rightEncoder);
        this.centralEncoder = new Encoder(centralEncoder);

        previousAbsoluteTheta = startPosition.theta;
    }

    /*   CONVENȚIE

     * deltaLeft    - distanta parcursa de roata stanga față de poziția anterioară
     * deltaRight   - distanta parcursa de roata dreaptă față de poziția anterioară
     * deltaHeading - distanta parcursa de roata din spata/față față de poziția anterioară

     * deltaGlobalLeft  - distanța parcursă de encoderul stânga față de poziția de reset
     * deltaGlobalRight - distanța parcursă de encoderul dreapta față de poziția de reset

     * absoluteTheta - orientarea robotului față de orientarea globală - Heading
     * previousAbsoluteTheta - orientarea globală precedentă a robotului - Previous Heading
     * deltaTheta - diferența de orientare dintre precedentă și cea actuală

        ALGORITM

     1. Aflam orientarea actuală a robotului - Heading-ul

         absoluteTheta =     startPosition.theta       +    (deltaGlobalLeft - deltaGlobalRight) / (leftLength + rightLength);
                             (orientarea la start)     +                         (orientarea față de start)

     2.

     3.

     4.

     5.

    */
    public void updatePosition(){

        // Update Encoder Values
        leftEncoder.updateValues();
        rightEncoder.updateValues();
        centralEncoder.updateValues();

        // Get Delta Encoders Value
        double deltaLeft    = leftEncoder.getDeltaDistance();
        double deltaRight   = rightEncoder.getDeltaDistance();
        double deltaHeading = centralEncoder.getDeltaDistance();

        // Get Global Delta Encoders Value
        double deltaGlobalLeft    = leftEncoder.getGlobalDelta();
        double deltaGlobalRight   = rightEncoder.getGlobalDelta();

        // Get Heading
        double absoluteTheta = startPosition.theta + (deltaGlobalLeft - deltaGlobalRight) / (leftLength + rightLength);

        // Compute Delta Theta & Remember Absolute Theta for next Iterations
        double deltaTheta = absoluteTheta - previousAbsoluteTheta;
        previousAbsoluteTheta = absoluteTheta;

        // Values for Increment
        double deltaX;
        double deltaY;

        // Compute Incremental Values for Position Update
        if(deltaTheta == 0) {
            deltaX = deltaHeading;
            deltaY = deltaRight;



        } else {
            double s = 2 * Math.sin(absoluteTheta / 2);
            deltaX   = s * (deltaHeading / deltaTheta + headingLength);
            deltaY   = s * (deltaRight   / deltaTheta + rightLength);
        }

        // Update Position
        currentPosition.x     += deltaX;
        currentPosition.y     += deltaY;
        currentPosition.theta = absoluteTheta;
    }

    // Getters
    public Position getPosition(){

        return currentPosition;
    }

    public double getDistanceFromOrigin() {

        double dX = currentPosition.x - startPosition.x;
        double dY = currentPosition.y - startPosition.y;

        return Math.sqrt(dX * dX + dY * dY);
    }

}
