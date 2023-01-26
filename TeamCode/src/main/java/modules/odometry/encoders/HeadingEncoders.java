package modules.odometry.encoders;

import modules.odometry.Heading;
import modules.odometry.configuration.OdometryConstants;

public class HeadingEncoders implements Heading {

    private final Encoder leftEncoder;
    private final Encoder rightEncoder;

    public HeadingEncoders(Encoder leftEncoder, Encoder rightEncoder){
        this.leftEncoder  = leftEncoder;
        this.rightEncoder = rightEncoder;
    }

    @Override
    public double getHeading() {
        return (leftEncoder.getGlobalDistance() - rightEncoder.getGlobalDistance()) / (OdometryConstants.leftLength + OdometryConstants.rightLength);
    }

}
