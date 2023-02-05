package modules.odometry.encoders;

import modules.odometry.Heading;
import modules.odometry.configuration.OdometryConstants;

public class HeadingEncoders implements Heading {

    private final Encoder leftEncoder;
    private final Encoder rightEncoder;

    private double angle = 0;

    public HeadingEncoders(Encoder leftEncoder, Encoder rightEncoder){
        this.leftEncoder  = leftEncoder;
        this.rightEncoder = rightEncoder;
    }

    @Override
    public double getHeading() {
        angle += (rightEncoder.getDeltaDistance() - leftEncoder.getDeltaDistance()) / ((OdometryConstants.leftLength + OdometryConstants.rightLength) * OdometryConstants.LATERAL_MULTIPLIER);

        return  angle;
    }

}


