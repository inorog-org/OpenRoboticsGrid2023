package modules.gamepad.sticks;

import modules.gamepad.sticks.equations.PolarCoordinates;

public interface Stick {

    PolarCoordinates getPolar();
    void updateData();

}
