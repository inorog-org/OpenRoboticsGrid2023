package modules.odometry.utils;

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

    public Position(Position position) {
        this.x = position.x;
        this.y = position.y;
        this.theta = position.theta;
    }

    public void updatePosition(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
    }

    public void incrementPosition(double x, double y, double theta) {
        this.x += x;
        this.y += y;
        this.theta += theta;
    }

    public void incrementPosition(double x, double y) {
        this.x += x;
        this.y += y;
    }

    public void incremenetPosition(Position position) {
        this.x += position.x;
        this.y += position.y;
        this.theta += position.theta;
    }

    public void decremenetPosition(Position position) {
        this.x -= position.x;
        this.y -= position.y;
        this.theta -= position.theta;
    }

    public void rotatePosition(double theta) {

        double oldX = x;
        double oldY = y;

        double cos = Math.cos(theta);
        double sin = Math.sin(theta);

        x = oldX * cos - oldY * sin;
        y = oldX * sin + oldY * cos;
    }

}
