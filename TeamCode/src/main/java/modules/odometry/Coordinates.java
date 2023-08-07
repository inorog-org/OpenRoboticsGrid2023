package modules.odometry;

import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

public interface Coordinates {

    void initPositionTracker(Position initalPosition, Velocity initialVelocity, int msPollInterval);

    Position getCoordinates();

}

