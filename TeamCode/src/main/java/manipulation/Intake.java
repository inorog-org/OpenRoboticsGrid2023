package manipulation;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Intake {

    private Gamepad gamepad;
    private double UPPER_ASPIRATOR  = 1.0;
    private double UPPER_DEPOZITARE = 0.0;
    private double POWER_ASPIRATOR_IN  = 1.0;
    private double POWER_ASPIRATOR_OUT = 0.0;

    private Servo upper_right; // Axon
    private Servo upper_left;  // Axon
    private CRServo aspirator; // Axon
    private CRServo glider;    // Axon

    public Intake(HardwareMap hardwareMap, Gamepad gamepad) {

        upper_right = hardwareMap.get(Servo.class, "upper_right");
        upper_left  = hardwareMap.get(Servo.class, "upper_left");

        aspirator   = hardwareMap.get(CRServo.class, "aspirator");

        glider      = hardwareMap.get(CRServo.class, "glider");
    }

    enum State {
        DEPOZITARE,
        ASPIRATOR
    }

    public void setUpperState(State state) {

        switch (state) {
            case ASPIRATOR:  setUpperAspirator();
            case DEPOZITARE: setUpperDepozitare();
        }
    }

    public void setUpperAspirator() {
         upper_right.setPosition(UPPER_ASPIRATOR);
         upper_left.setPosition(-UPPER_ASPIRATOR);
    }

    public void setUpperDepozitare() {
        upper_right.setPosition(UPPER_DEPOZITARE);
        upper_left.setPosition(-UPPER_DEPOZITARE);
    }

    public void manualGlider() {
        glider.setPower(gamepad.right_trigger - gamepad.left_trigger);
    }

    public void setAspirator(boolean activate) {
        if(activate) aspirator.setPower(1.0);
         else aspirator.setPower(0.0);
    }

}
