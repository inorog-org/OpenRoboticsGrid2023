package opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "Lunatica: Axon TEST", group = "Testus")
@Disabled
public class ServoAxonTest extends LinearOpMode {

    private Servo axon;
    private double position = 1.0;

    @Override
    public void runOpMode() throws InterruptedException {

        axon = hardwareMap.get(Servo.class, "axon");

        axon.setPosition(position);

        waitForStart();

        while(opModeIsActive()) {
            axon.setPosition(gamepad1.left_stick_x);
        }


    }
}
