package modules.drive.subsystems.teleop;

import modules.drive.configuration.MotorsConstants;

public class BoostSystem {

    private boolean boostState = false;
    private long boostStart  = 0;

    public double execute(boolean boost) {

        if(!boostState && boost) {
            boostState = true;
            boostStart = System.currentTimeMillis();
        }

        if(boostState) {
            if (System.currentTimeMillis() - boostStart <= MotorsConstants.boostTime) {
                return MotorsConstants.BOOST_SPEED;
            } else boostState = false;
        }

        return MotorsConstants.MAX_MOVEMENT_SPEED;
    }

}
