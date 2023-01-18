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

    // Buttons
    public boolean boost  = false;
    public boolean locker = false;

    // Lightbar
    public double HUE = 0;
    public double RED   = 0;
    public double GREEN = 0;
    public double BLUE  = 0;
}
