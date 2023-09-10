package opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;

import manipulation.Intake;
import modules.gamepad.buttons.Button;

@TeleOp(name = "Concept: Conveyer", group = "Concept")
@Disabled
public class ConveyerTest extends LinearOpMode {

    private double POWER_ASPIRATOR_ON  = 1.0;
    private double POWER_ASPIRATOR_OFF = 0.0;

    private CRServo aspirator_femei_3;  // Axon

    private Button intakeButton;

    public State currentState;
    @Override
    public void runOpMode() throws InterruptedException {

        aspirator_femei_3  = hardwareMap.get(CRServo.class, "aspirator_3");

    }

    enum State {
        DEPOZITARE,
        ASPIRATOR
    }

    public void setAspirator(boolean activate) {
        if(activate) {
            aspirator_femei_3.setPower(POWER_ASPIRATOR_ON);
        } else {
            aspirator_femei_3.setPower(POWER_ASPIRATOR_OFF);
        }
    }

    public void teleOpIntake() {

        if(intakeButton.listen()) {
            if (currentState == State.ASPIRATOR) {
                currentState = State.DEPOZITARE;
                setAspirator(false);
            } else if(currentState == State.DEPOZITARE){
                currentState = State.ASPIRATOR;
                setAspirator(true);
            }
        }

    }
}
