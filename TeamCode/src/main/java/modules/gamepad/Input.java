package modules.gamepad;

public class Input {

    // Stick Movement - Angle and Magnitude
    public double angle     = 0;
    public double magnitude = 0;
    public boolean movementStick = false;

    // Rotation - Magnitude
    public double rotate = 0;
    public boolean rotationStick = false;

    // Spin - Magnitude
    public double spin = 0;
    public boolean spinTriggers  = false;

    // Touchpad Movement - Angle and Magnitude
    public double angleTouchpad  = 0;
    public double magnitudeTouch = 0;
    public boolean touchpad  = false;

    // Automated Position Buttons
    public boolean memoratePosition = false;
    public boolean approachPosition = false;

    // Digital Pad
    public boolean dpad_up    = false;
    public boolean dpad_down  = false;
    public boolean dpad_right = false;
    public boolean dpad_left  = false;
    public boolean movement_dpad = false;

    // Buttons
    public boolean boost  = false;
    public boolean locked = false;

    // SpeedChanger
    public boolean increment = false;
    public boolean decrement = false;

    // Lightbar
    public double HUE = 0;
    public double RED   = 0;
    public double GREEN = 0;
    public double BLUE  = 0;

    public void reset() {

        // Stick Movement - Angle and Magnitude
        angle     = 0;
        magnitude = 0;
        movementStick = false;

        // Rotation - Magnitude
        rotate = 0;
        rotationStick = false;

        // Spin - Magnitude
        spin = 0;
        spinTriggers  = false;

        // Touchpad Movement - Angle and Magnitude
        angleTouchpad  = 0;
        magnitudeTouch = 0;
        touchpad  = false;

        // Automated Position Buttons
        memoratePosition = false;
        approachPosition = false;

        // Digital Pad
        dpad_up    = false;
        dpad_down  = false;
        dpad_right = false;
        dpad_left  = false;

        // Buttons
        boost  = false;
        locked = false;

        // Speedchanger
        increment = false;
        decrement = false;

        // DPAD Movement
        movement_dpad = false;

        // Lightbar
        HUE   = 0;
        RED   = 0;
        GREEN = 0;
        BLUE  = 0;
    }
}
