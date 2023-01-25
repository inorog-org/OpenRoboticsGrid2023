package opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import modules.odometry.Odometry;
import modules.odometry.encoders.EncodersExceptions;

@TeleOp(name = "Lunatica: Odometry", group = "Testus")
public class OdometryTesting extends LinearOpMode {

    private Odometry odometry;

    private DcMotorEx centralEncoder;
    private DcMotorEx leftEncoder;
    private DcMotorEx rightEncoder;


    @Override
    public void runOpMode() throws InterruptedException {

        centralEncoder = hardwareMap.get(DcMotorEx.class, "central");
        leftEncoder    = hardwareMap.get(DcMotorEx.class, "left");
        rightEncoder   = hardwareMap.get(DcMotorEx.class, "right");

        try {
            odometry = new Odometry(null, leftEncoder, rightEncoder, centralEncoder);
        } catch (EncodersExceptions e) {
            throw new RuntimeException(e);
        }

        odometry.setOdometryMode(Odometry.MODE.VECTORIAL);

        odometry.encoders.centralEncoder.setDirection(DcMotor.Direction.FORWARD);
        assert odometry.encoders.leftEncoder != null : "Left Encoder is missing!";
        odometry.encoders.leftEncoder.setDirection(DcMotor.Direction.FORWARD);
        assert odometry.encoders.rightEncoder != null : "Right Encoder is missing!";
        odometry.encoders.rightEncoder.setDirection(DcMotor.Direction.FORWARD);

        waitForStart();

        while (opModeIsActive()) {
          odometry.updatePosition();

          telemetry.addData("X-Pos", odometry.getPosition().x);
          telemetry.addData("Y-Pos", odometry.getPosition().y);
          telemetry.addData("Orientation", odometry.getPosition().theta);

          telemetry.update();

        }
    }
}
