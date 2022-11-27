package modules.odometry;

public class Position {

    public double x;      // X - Coordinate
    public double y;      // Y - Coordinate
    public double theta;  // Theta - Heading Angle - Radians

    public Position(double x, double y, double theta){
        this.x = x;
        this.y = y;
        this.theta = theta;
    }

    public Position(double value, double theta){
        new Position(value, value, theta);
    }

    public Position(){
        new Position(0, 0,0);
    }

    public void updatePosition(double x, double y, double theta){
        this.x = x;
        this.y = y;
        this.theta = theta;
    }

}
