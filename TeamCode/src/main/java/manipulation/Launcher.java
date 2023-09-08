package manipulation;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PDController;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import modules.gamepad.buttons.Button;
import modules.gamepad.buttons.StickyButton;

@Config
public class Launcher {

    private Gamepad gamepad;

    private DcMotorEx flywheel;
    private PDController pdController;
    private Servo limba;

    private double COUNTS_PER_MOTOR_REV = 28.0;
    private double MAX_THEORETICAL_RPM = 6000.0;

    private double DISTANCE = 200.0; // cm
    private double ANGLE    = 20.0;  // grade
    private double HEIGHT   = 10.0;  // cm
    private double WHEEL_RADIUS = 4.0; // cm

    private double LIMBA_REPAUS = 0.0;
    private double LIMBA_LAUNCH = 1.0;
    private double DELAY = 300; // ms
    private double GRAVITY = 9.81; // m/s^2

    private double TARGET_RPM = 6000; // RPM
    private double TOLERANCE  = 100; // RPM

    private State stateFlywheel;
    private State stateLauncher;
    private Button flywheelButton;
    private Button launcherButton;

    private long launcherTimeStamp;

    public static double Kp = 0.00001, Kd = 0, Ki = 0;

    private PIDController pidController;
    private double pidVal;

    public Launcher(HardwareMap hardwareMap, Gamepad gamepad) {

        flywheel = (DcMotorEx) hardwareMap.get(DcMotor.class, "launcher");
        flywheel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        limba = hardwareMap.get(Servo.class, "limba");
        limba.setPosition(LIMBA_REPAUS);

        stateFlywheel = State.INACTIVE;
        stateLauncher = State.INACTIVE;

        flywheelButton = new StickyButton(()-> gamepad.y);
        launcherButton = new StickyButton(()-> gamepad.x);

        pidController = new PIDController(Kp, Ki, Kd);

        pidVal = pidController.calculate(getRPM(), TARGET_RPM);
    }

    enum State {
        ACTIVE,
        INACTIVE
    }

    public void teleOpLauncher() {

        double RPM = getRPM();

        // Motorul de 6000 RPM
        if(flywheelButton.listen()) {
            if (stateFlywheel == State.INACTIVE) {
                stateFlywheel = State.ACTIVE;
                pidVal += pidController.calculate(RPM, TARGET_RPM);
                flywheel.setVelocity(pidVal);
            }
            else {
                stateFlywheel = State.INACTIVE;
                flywheel.setPower(0.0);
                pidController.reset();
                pidVal = 0;
            }
        }

        if(stateFlywheel == State.ACTIVE) {
            pidVal += pidController.calculate(RPM);
            flywheel.setVelocity(pidVal);
        }

        // Limba
        if(launcherButton.listen()) {
            if(stateLauncher == State.INACTIVE  && RPM >= TARGET_RPM - TOLERANCE && RPM <= TARGET_RPM + TOLERANCE) {
                stateLauncher = State.ACTIVE;
                limba.setPosition(LIMBA_LAUNCH);
                launcherTimeStamp = System.currentTimeMillis();
            }
            else {
                stateLauncher = State.INACTIVE;
                limba.setPosition(LIMBA_REPAUS);
            }
        }

        if(stateLauncher == State.ACTIVE && System.currentTimeMillis() >= launcherTimeStamp + DELAY) {
            stateLauncher = State.INACTIVE;
            limba.setPosition(LIMBA_REPAUS);
        }

    }

    public double getRPM() {
        return flywheel.getVelocity() / COUNTS_PER_MOTOR_REV * 60.0;
    }

    public double getTargetRPM() {
        double velocity = Math.sqrt( GRAVITY * (DISTANCE * DISTANCE + 2 * HEIGHT * Math.cos(ANGLE) * Math.cos(ANGLE)) / (2 * DISTANCE * Math.sin(2 * ANGLE)));
        return velocity * 30 / (Math.PI * WHEEL_RADIUS);
    }

}

