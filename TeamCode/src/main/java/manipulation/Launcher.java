package manipulation;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PDController;
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

    private double kP = 1.0;
    private double kD = 1.0;

    private double COUNTS_PER_MOTOR_REV = 28.0;
    private double MAX_THEORETICAL_RPM = 6000.0;

    private double DISTANCE = 200.0; // cm
    private double ANGLE    = 20.0;  // grade
    private double HEIGHT   = 10.0;  // cm

    private double LIMBA_REPAUS = 0.0;
    private double LIMBA_LAUNCH = 1.0;
    private double DELAY = 300; // ms

    private double TARGET_RPM = 6000; // RPM
    private double TOLERANCE  = 100; // RPM

    private State stateFlywheel;
    private State stateLauncher;
    private Button flywheelButton;
    private Button launcherButton;

    private long launcherTimeStamp;

    public Launcher(HardwareMap hardwareMap, Gamepad gamepad) {

        flywheel = (DcMotorEx) hardwareMap.get(DcMotor.class, "launcher");
        pdController = new PDController(kP, kD);

        limba = hardwareMap.get(Servo.class, "limba");
        limba.setPosition(LIMBA_REPAUS);

        stateFlywheel = State.INACTIVE;
        stateLauncher = State.INACTIVE;

        flywheelButton = new StickyButton(()-> gamepad.y);
        launcherButton = new StickyButton(()-> gamepad.x);
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
                flywheel.setVelocity(pdController.calculate(RPM, TARGET_RPM));
            }
            else {
                stateFlywheel = State.INACTIVE;
                flywheel.setPower(0.0);
                pdController.reset();
            }
        }

        if(stateFlywheel == State.ACTIVE)
            flywheel.setVelocity(pdController.calculate(RPM));

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

}
