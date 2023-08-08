package modules.drive.subsystems.pursuit.splines;

import modules.drive.subsystems.pursuit.PurePursuit;
import modules.odometry.utils.Position;

public class Line implements Path {

    public Position start;
    public Position finish;

    private double orientation = 0.0;
    private double distance = 0.0;

    public Line(Position start, Position finish) {
        this.start  = start;
        this.finish = finish;
    }

    public void updatePathData(Position robotPosition) {

        // Central Sistemul de axe în jurul robotului
        Position newPositionStart  = new Position(start);
        Position newPositionFinish = new Position(finish);

        newPositionStart.decremenetPosition(robotPosition);
        newPositionFinish.decremenetPosition(robotPosition);

        newPositionStart.rotatePosition(-robotPosition.theta);
        newPositionFinish.rotatePosition(-robotPosition.theta);

        if(newPositionStart.x != newPositionFinish.x)
            forNonEqualX(newPositionStart, newPositionFinish);
        else forEqualX(newPositionStart, newPositionFinish);

    }

    private void forEqualX(Position newStart, Position newFinish) {

        double D = newFinish.x;

        if(D <= PurePursuit.PURSUIT_DISTANCE)
            findP12ForEqualX(newStart, newFinish, D);
        else {
            orientation = Math.atan2(0, D);
            distance = D + Math.sqrt(D * D + newFinish.y * newFinish.y);
        }

    }

    private void findP12ForEqualX(Position newStart, Position newFinish, double D) {

        double x = D;

        double y1 = PurePursuit.PURSUIT_DISTANCE * PurePursuit.PURSUIT_DISTANCE - D * D;
        double y2 = -y1;

        double t1 = (y1 - newStart.y) / (newFinish.y - newStart.y);
        double t2 = (y2 - newStart.y) / (newFinish.y - newStart.y);

        if(Math.max(t1, t2) >= 1) {
            orientation = Math.atan2(newFinish.y, newFinish.x);
            distance = Math.sqrt(newFinish.x * newFinish.x + newFinish.y * newFinish.y);
            return;
        }

        if(t1 > t2) {
            orientation = Math.atan2(y1, x);
            distance    = PurePursuit.PURSUIT_DISTANCE + Math.sqrt(Math.pow(x - newStart.x, 2) + Math.pow(y1 - newStart.y,2));
        } else {
            orientation = Math.atan2(y2, x);
            distance    = PurePursuit.PURSUIT_DISTANCE + Math.sqrt(Math.pow(x - newStart.x, 2) + Math.pow(y2 - newStart.y,2));
        }

    }

    private void forNonEqualX(Position newStart, Position newFinish) {

        double a = (newStart.y - newFinish.y) / (newStart.x - newFinish.x);
        double c = newStart.y - a * newStart.x;

        double D = Math.abs(c) / Math.sqrt(a * a + 1);

        if(D <= PurePursuit.PURSUIT_DISTANCE)
            findP12ForNonEqualX(a, c, newStart, newFinish);
        else {

            double xm = (- a * c) / (a * a + 1);
            double ym = c / (a * a + 1);

            orientation = Math.atan2(ym, xm);
            distance = D + Math.pow(xm - newStart.x, 2) + Math.pow(ym - newStart.y,2);
        }

    }

    private void findP12ForNonEqualX(double a, double c, Position newStart, Position newFinish) {

        double square_delta = 2 * Math.sqrt(PurePursuit.PURSUIT_DISTANCE * PurePursuit.PURSUIT_DISTANCE * (a * a + 1) - c * c);


        double x1 = (-2 * a * c - square_delta) / (2 * (a * a + 1));
        double x2 = (-2 * a * c + square_delta) / (2 * (a * a + 1));

        double f1 = a * x1 + c;
        double f2 = a * x2 + c;

        double t1 = (x1 - newStart.x) / (newFinish.x - newStart.x);
        double t2 = (x2 - newStart.x) / (newFinish.x - newStart.x);

        if(Math.max(t1, t2) >= 1) {
            orientation = Math.atan2(newFinish.y, newFinish.x);
            distance = Math.sqrt(newFinish.x * newFinish.x + newFinish.y * newFinish.y);
            return;
        }

        if(t1 > t2) {
            orientation = Math.atan2(f1, x1);
            distance    = PurePursuit.PURSUIT_DISTANCE + Math.sqrt(Math.pow(x1 - newStart.x, 2) + Math.pow(f1 - newStart.y,2));
        } else {
            orientation = Math.atan2(f2, x2);
            distance    = PurePursuit.PURSUIT_DISTANCE + Math.sqrt(Math.pow(x2 - newStart.x, 2) + Math.pow(f2 - newStart.y,2));
        }

    }

    public double getOrientation() {
        return orientation;
    }

    public double getDistance() {
        return distance;
    }
    public double xS, yS;
    public double xF, yF;

}
