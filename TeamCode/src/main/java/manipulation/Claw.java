package manipulation;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;

public class Claw {

    private boolean contor=true;
    private boolean isClosed = true;
    private boolean prevState = false;
    public Servo claw;
    public static final double OPENED_CLAW = 0.7;
    public static final double CLOSED_CLAW = 0.4;
    public LinearOpMode opMode;

    public Claw(LinearOpMode opMode) {
        claw = opMode.hardwareMap.get(Servo.class, "claw");
        claw.setPosition(CLOSED_CLAW);
        this.opMode = opMode;
        //claw.setDirection(Servo.Direction.REVERSE);
    }


    public void claw() {
        if (isClosed) {
            claw.setPosition(CLOSED_CLAW);
            isClosed = false;
        } else {
            claw.setPosition(OPENED_CLAW);
            isClosed = true;
        }
    }


    public void actionClaw() {
        if (opMode.gamepad2.right_bumper) {
            if (!prevState) {
                prevState = true;
                claw();
            }
        } else {
            if (prevState) {
                prevState = false;
            }
        }
    }

    /**
     * Inchid gheara ca sa prind un con
     */
    public void closeClaw() {
        claw.setPosition(CLOSED_CLAW);
    }

    /**
     * Deschid gheara ca sa eliverez conul
     */
    public void openClawAuto(){
        if(contor) {
            claw.setPosition(OPENED_CLAW);
            contor = false;
        }
    }

    public void openClaw() {
        claw.setPosition(OPENED_CLAW);
    }
    public void teleopClaw(){
    actionClaw();

    }
}



