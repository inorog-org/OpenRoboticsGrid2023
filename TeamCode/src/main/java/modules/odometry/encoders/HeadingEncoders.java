package modules.odometry.encoders;

import modules.odometry.Heading;

public class HeadingEncoders implements Heading {

    private final Encoder leftEncoder;
    private final Encoder rightEncoder;

    private double angle = 0;
    private double velocity = 0 ;
    private double acceleration = 0;

    public HeadingEncoders(Encoder leftEncoder, Encoder rightEncoder){
        this.leftEncoder  = leftEncoder;
        this.rightEncoder = rightEncoder;
    }

    @Override
    public double getHeading() {
        return (leftEncoder.getGlobalDistance() - rightEncoder.getGlobalDistance()) / (OdometryEncoders.leftLength + OdometryEncoders.rightLength);
    }

}
