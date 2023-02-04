package opmodes.tests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import modules.drive.Drivebase;
import modules.imu.IMU;
import modules.odometry.Heading;
import modules.odometry.Odometry;
import modules.odometry.encoders.Encoder;
import modules.odometry.encoders.EncodersExceptions;
import modules.odometry.encoders.HeadingEncoders;

@TeleOp(name = "Lunatica: Odometry", group = "Testus")
public class OdometryTesting extends LinearOpMode {

    private Drivebase drivebase;
    private IMU imu;

    private Heading heading;

    private Odometry odometry;

    private Encoder centralEncoder;
    private Encoder leftEncoder;
    private Encoder rightEncoder;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void runOpMode() throws InterruptedException {

        imu = new IMU(this, BNO055IMU.AngleUnit.RADIANS, BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC); // Init IMU

        drivebase = new Drivebase(this, imu); // Init Drivebase

        // Init Encoders
        centralEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "central"));
        leftEncoder    = new Encoder(hardwareMap.get(DcMotorEx.class, "left"));
        rightEncoder   = new Encoder(hardwareMap.get(DcMotorEx.class, "right"));

        // Init Heading
        heading = new HeadingEncoders(leftEncoder, rightEncoder);

        try {
            odometry = new Odometry(heading, leftEncoder, rightEncoder, centralEncoder); // Init Odometry
        } catch (EncodersExceptions e) {
            throw new RuntimeException(e);
        }

        odometry.setOdometryMode(Odometry.MODE.VECTORIAL); // Init Vectorial

        waitForStart();

        while (opModeIsActive()) {

            odometry.updatePosition();

            drivebase.control();

            telemetry.addData("X-Pos", odometry.getPosition().x);
            telemetry.addData("Y-Pos", odometry.getPosition().y);
            telemetry.addLine();
            telemetry.addData("Orientation Encoders", Math.toDegrees(odometry.getPosition().theta) * 1.019);
            telemetry.addData("Orientation IMU", Math.toDegrees(imu.getHeading()));

            telemetry.update();

        }
    }
}
