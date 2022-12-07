package modules.odometry;

import modules.odometry.encoders.OdometryEncoders;

public class Odometry {

    // --- Odometry Encoders --- //
    private OdometryEncoders encoders;

    // --- Heading --- //
    private Heading heading;

    // --- Postion --- //
    private Position startPosition;
    private Position currentPosition;

    // --- Previous Heading --- //
    private double previousAbsoluteTheta;

    /// --- Constructors --- ///
    public Odometry(Heading heading){
       this(new Position(), heading);
    }

    public Odometry(Position startPosition, Heading heading){
        this.startPosition   = startPosition;
        this.currentPosition = new Position();

        this.previousAbsoluteTheta = startPosition.theta;

        this.heading = heading;
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

     */

    public Position updatePosition(){

        // Update Encoder Values
        encoders.updateEncodersValues();

        // Get Delta Encoders Value
        double deltaLeft    = encoders.leftEncoder.getDeltaDistance();
        double deltaRight   = encoders.rightEncoder.getDeltaDistance();
        double deltaCentral = encoders.centralEncoder.getDeltaDistance();

        // Get Heading
        double absoluteTheta = startPosition.theta + heading.getHeading();

        // Compute Delta Theta & Remember Absolute Theta for next Iterations
        double deltaTheta = absoluteTheta - previousAbsoluteTheta;
        previousAbsoluteTheta = absoluteTheta;

        // Values for Increment
        double deltaX;
        double deltaY;

        // Compute Incremental Values for Position Update
        if(deltaTheta == 0) {

            double x = deltaCentral;
            double y = deltaRight;

            double cos = Math.cos(absoluteTheta);
            double sin = Math.sin(absoluteTheta);

            deltaX = x * cos - y * sin;
            deltaY = x * sin + y * cos;

        } else {

            double s = 2 * Math.sin(absoluteTheta / 2);

            double x   = s * (deltaCentral / deltaTheta + OdometryEncoders.headingLength);
            double y   = s * (deltaRight   / deltaTheta + OdometryEncoders.rightLength);

            double cos = Math.cos(absoluteTheta + deltaTheta / 2);
            double sin = Math.sin(absoluteTheta + deltaTheta / 2);

            deltaX = x * cos - y * sin;
            deltaY = x * sin + y * cos;
        }

        // Update Position
        currentPosition.x     += deltaX;
        currentPosition.y     += deltaY;
        currentPosition.theta  = absoluteTheta;

        return currentPosition;
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
