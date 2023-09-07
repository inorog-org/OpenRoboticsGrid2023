package manipulation;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import modules.gamepad.buttons.Button;
import modules.gamepad.buttons.StickyButton;

public class Intake {

    private Gamepad gamepad;
    private double POWER_ASPIRATOR_ON  = 1.0;
    private double POWER_ASPIRATOR_OFF = 0.0;

    private CRServo aspirator_femei_1; // Axon
    private CRServo aspirator_femei_2;  // Axon
    private CRServo glider;    // Axon

    private State currentState = State.DEPOZITARE;

    private Button intakeButton;

    public Intake(HardwareMap hardwareMap, Gamepad gamepad) {

        aspirator_femei_1  = hardwareMap.get(CRServo.class, "aspirator_1");
        aspirator_femei_2  = hardwareMap.get(CRServo.class, "aspirator_2");

        glider       = hardwareMap.get(CRServo.class, "glider");

        intakeButton = new StickyButton(()-> gamepad.a);
    }

    enum State {
        DEPOZITARE,
        ASPIRATOR
    }

    public void manualGlider() {
        glider.setPower(gamepad.right_trigger - gamepad.left_trigger);
    }

    public void setAspirator(boolean activate) {
        if(activate) {
            aspirator_femei_1.setPower(POWER_ASPIRATOR_ON);
            aspirator_femei_2.setPower(POWER_ASPIRATOR_ON);
        } else {
         aspirator_femei_1.setPower(POWER_ASPIRATOR_OFF);
         aspirator_femei_2.setPower(POWER_ASPIRATOR_OFF);
        }
    }

    public void teleOpIntake() {

        manualGlider();

        if(intakeButton.listen())
            if (currentState == State.ASPIRATOR) {
                currentState = State.DEPOZITARE;
                setAspirator(false);
            } else {
                currentState = State.ASPIRATOR;
                setAspirator(true);
            }

    }


}
