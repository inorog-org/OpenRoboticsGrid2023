package modules.odometry;

import com.qualcomm.robotcore.hardware.HardwareMap;

public class OdometryEncoders {

    private final Encoder leftEncoder;
    private final Encoder rightEncoder;
    private final Encoder headingEncoder;

    private Position startPosition;
    private Position currentPosition;

    private double previousAbsoluteTheta;

    // --- Distance from Robot Center --- //
    private final double leftLength    = 17.5;  // 17.5 cm
    private final double rightLength   = 17.5;  // 17.5 cm
    private final double headingLength = 17.25; // 17.25 cm

    public OdometryEncoders(HardwareMap hardwareMap){
        this.startPosition   = new Position();
        this.currentPosition = new Position();

        this.leftEncoder    = new Encoder(hardwareMap, "leftEncoder");
        this.rightEncoder   = new Encoder(hardwareMap, "rightEncoder");
        this.headingEncoder = new Encoder(hardwareMap, "headingEncoder");

        previousAbsoluteTheta = startPosition.theta;
    }

    public double getDistanceFromOrigin() {

        double dX = currentPosition.x - startPosition.x;
        double dY = currentPosition.y - startPosition.y;

        return Math.sqrt(dX * dX + dY * dY);
    }

    /*   CONVENTIE
     * deltaLeft    - distanta parcursa de roata stanga față de poziția anterioară
     * deltaRight   - distanta parcursa de roata dreaptă față de poziția anterioară
     * deltaHeading - distanta parcursa de roata din spata/față față de poziția anterioară
     * deltaGlobalLeft  - distanța parcursă de encoderul stânga față de poziția de reset
     * deltaGlobalRight - distanța parcursă de encoderul dreapta față de poziția de reset
     ALGORITM
     * absoluteTheta - orientarea robotului față de orientarea globală - până și orientarea de reset depinde de orientarea globală
     - Pozitia la ultimul reset e de fapt pozitia robotului pe teren la ultimul reset si orientarea lui față de orientarea globală
         absoluteTheta =      startPosition.theta           +    (deltaGlobalLeft - deltaGlobalRight) / (leftLength + rightLength);
                          (orientarea la ultimul reset)     +               (orientarea față de ultimul reset)
     * previousAbsoluteTheta - orientarea globală precedentă a robotului
     * deltaTheta - diferența de orientare dintre starea precedentă și starea actuală
          deltaTheta = absoluteTheta - previousAbsoluteTheta;
     - previousAbsoluteTheta devine egal cu absoluteTheta (Update previousAbsoluteTheta)
    */
    public void updatePosition(){

        leftEncoder.updateValues();
        rightEncoder.updateValues();
        headingEncoder.updateValues();

        double deltaLeft    = leftEncoder.getDeltaDistance();
        double deltaRight   = rightEncoder.getDeltaDistance();
        double deltaHeading = headingEncoder.getDeltaDistance();

        double deltaGlobalLeft    = leftEncoder.getGlobalDelta();
        double deltaGlobalRight   = rightEncoder.getGlobalDelta();

        double absoluteTheta = startPosition.theta + (deltaGlobalLeft - deltaGlobalRight) / (leftLength + rightLength);

        double deltaTheta = absoluteTheta - previousAbsoluteTheta;

        previousAbsoluteTheta = absoluteTheta;

        //////////////////////////////////////////

        double deltaX;
        double deltaY;

        if(deltaTheta == 0) {
            deltaX = deltaHeading;
            deltaY = deltaRight;
        } else {
            double s = 2 * Math.sin(absoluteTheta / 2);
            deltaX   = s * (deltaHeading / deltaTheta + headingLength);
            deltaY   = s * (deltaRight   / deltaTheta + rightLength);
        }

        currentPosition.x     += deltaX;
        currentPosition.y     += deltaY;

        /////////////////////////////////////

        // currentPosition.theta += deltaTheta;
    }


    public Position getPosition(){
        return currentPosition;
    }

}
