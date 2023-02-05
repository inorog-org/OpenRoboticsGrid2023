package opmodes.tests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import modules.drive.subsystems.teleop.Drivebase;
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

    private TelemetryPacket packet;
    private FtcDashboard dashboard;

    private double headingIMU, headingEncoder;

    private double X0 =  35.0; // INCH
    private double Y0 = -61.8; // INCH

    private double WIDTH  = 40.5 / 2.54;
    private double HEIGHT = 42.5 / 2.54;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void runOpMode() throws InterruptedException {

        dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        imu = new IMU(this, BNO055IMU.AngleUnit.RADIANS, BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC); // Init IMU
        imu.remapAxis( 0x24, 0xc);

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

            headingEncoder = Math.toDegrees(odometry.getPosition().theta);
            headingIMU = Math.toDegrees(imu.getHeading());

            telemetry.addData("Orientation Encoders", headingEncoder);
            telemetry.addData("Orientation IMU", headingIMU);

            telemetry.addData("Encoder Right", rightEncoder.getCurrentPosition());
            telemetry.addData("Encoder Left", leftEncoder.getCurrentPosition());
            telemetry.addData("Encoder Central", centralEncoder.getCurrentPosition());

            sendFieldData();

            telemetry.update();

        }
    }

    public double convertToInches(double distance) {
        return distance * 0.390625;
    }

    public void sendFieldData() {
        packet.fieldOverlay().setFill("blue").fillRect(X0 + convertToInches(odometry.getPosition().x) - WIDTH/2, Y0 + convertToInches(odometry.getPosition().y) - HEIGHT/2, WIDTH, HEIGHT);
        dashboard.sendTelemetryPacket(packet);
    }
}
