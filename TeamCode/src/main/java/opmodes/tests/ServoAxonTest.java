package opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Lunatica: Axon TEST", group = "Testus")
@Disabled
public class ServoAxonTest extends LinearOpMode {

    private CRServo axon;
    private double position = 1.0;

    @Override
    public void runOpMode() throws InterruptedException {

        axon = hardwareMap.get(CRServo.class, "axon");


        waitForStart();

        while(opModeIsActive()) {
            axon.setPower(gamepad1.left_stick_x);
        }

    }
}
