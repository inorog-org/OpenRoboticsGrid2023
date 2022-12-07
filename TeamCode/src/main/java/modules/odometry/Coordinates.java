package modules.odometry;

import org.firstinspires.ftc.robotcore.external.navigation.Position;

public interface Coordinates {

    void initPositionTracker();

    Position getCoordinates();

}

