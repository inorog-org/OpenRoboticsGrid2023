package manipulation;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PDController;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import modules.control.pid.PIDFController;
import modules.gamepad.buttons.Button;
import modules.gamepad.buttons.StickyButton;

@Config
public class Launcher {

    private Gamepad gamepad;

    private DcMotorEx flywheel;
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

    public State stateFlywheel;
    public State stateLauncher;
    private Button flywheelButton;
    private Button launcherButton;

    private long launcherTimeStamp;

    public static double Kp = 0.00001, Kd = 12, Ki = 0;
    public static double kF = 2800;

    public PIDController pidController;
    public PIDFController pidfController;
    public double pidVal;

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
        pidfController = new PIDFController(Kp,Ki,Kd, 0.0, 0.5);

        //pidController.setSetPoint(TARGET_RPM);
        pidfController.setTarget(TARGET_RPM);
        pidVal =  kF;
    }

    enum State {
        ACTIVE,
        INACTIVE
    }

    public double teleOpLauncher() {

        double RPM = getRPM();

        // Motorul de 6000 RPM
        if(flywheelButton.listen()) {
            if (stateFlywheel == State.INACTIVE) {
                stateFlywheel = State.ACTIVE;
                //pidVal += pidController.calculate(RPM, TARGET_RPM);
                pidVal += pidfController.calculate(RPM);
                flywheel.setVelocity(pidVal);
            }
            else {
                stateFlywheel = State.INACTIVE;
                flywheel.setPower(0.0);
                // pidController.reset();
                pidfController.setTarget(TARGET_RPM);
                pidVal = kF;
            }
        }

        if(stateFlywheel == State.ACTIVE) {
            //pidVal += pidController.calculate(RPM - kF);
            pidVal += pidfController.calculate(RPM);
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
       return RPM;
    }

    public double getRPM() {
        return flywheel.getVelocity() / COUNTS_PER_MOTOR_REV * 60.0;
    }

    public double getTargetRPM() {
        double velocity = Math.sqrt( GRAVITY * (DISTANCE * DISTANCE + 2 * HEIGHT * Math.cos(ANGLE) * Math.cos(ANGLE)) / (2 * DISTANCE * Math.sin(2 * ANGLE)));
        return velocity * 30 / (Math.PI * WHEEL_RADIUS);
    }

}

