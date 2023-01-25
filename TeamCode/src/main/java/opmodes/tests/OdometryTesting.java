package opmodes.tests;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import modules.imu.IMU;
import modules.odometry.Odometry;
import modules.odometry.encoders.EncodersExceptions;

@TeleOp(name = "Lunatica: Odometry", group = "Testus")
public class OdometryTesting extends LinearOpMode {

    private IMU imu;
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

        waitForStart();

        while (opModeIsActive()) {


        }
    }
}
