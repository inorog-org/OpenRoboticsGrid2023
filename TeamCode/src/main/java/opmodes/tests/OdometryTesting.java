package opmodes.tests;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import modules.drive.subsystems.teleop.Drivebase;
import modules.imu.IMU;
import modules.odometry.Odometry;
import modules.odometry.OdometryBuilder;

@TeleOp(name = "Lunatica: Odometry", group = "Testus")
@Disabled
public class OdometryTesting extends LinearOpMode {

    private Drivebase drivebase;
    private IMU imu;
    private OdometryBuilder odometryBuilder;
    private FtcDashboard dashboard;
    private TelemetryPacket packet;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void runOpMode() throws InterruptedException {

        dashboard = FtcDashboard.getInstance();  // Init Dashboard
        dashboard.setTelemetryTransmissionInterval(100);
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry()); // Init Telemetry

        imu = new IMU(this, BNO055IMU.AngleUnit.RADIANS, BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC); // Init IMU
        imu.remapAxis( 0x24, 0xc);

        drivebase = new Drivebase(this, imu); // Init Drivebase

        odometryBuilder = new OdometryBuilder(this, imu); // Init Odometry Handler
        odometryBuilder.odometry.setOdometryMode(Odometry.MODE.VECTORIAL); // Init Vectorial

        packet = new TelemetryPacket();  // Init Packet
        sendFieldData();

        waitForStart();

        while (opModeIsActive()) {

            odometryBuilder.odometry.updatePosition();

            drivebase.control();

            telemetry.addData("X-Pos", odometryBuilder.odometry.getPosition().x);
            telemetry.addData("Y-Pos", odometryBuilder.odometry.getPosition().y);

            double headingIMU = Math.toDegrees(imu.getHeading());
            telemetry.addData("Orientation IMU", headingIMU);

            telemetry.addData("Encoder Right", odometryBuilder.rightEncoder.getCurrentPosition());
            telemetry.addData("Encoder Left", odometryBuilder.leftEncoder.getCurrentPosition());
            telemetry.addData("Encoder Central", odometryBuilder.centralEncoder.getCurrentPosition());

            sendFieldData();

            telemetry.update();

        }
    }

    private static double convertToInches(double distance) {
        return distance * 0.390625;
    }

    public void sendFieldData() {
        packet.fieldOverlay()
              .strokeRect(X0 + convertToInches(odometryBuilder.odometry.getPosition().x) - WIDTH  / 2,
                          Y0 + convertToInches(odometryBuilder.odometry.getPosition().y) - HEIGHT / 2,
                             WIDTH, HEIGHT)
              .setStroke("blue");
        dashboard.sendTelemetryPacket(packet);
    }

    private double X0 = convertToInches(89.0);
    private double Y0 = convertToInches(-157.0);
    private double WIDTH  = convertToInches(40.5);
    private double HEIGHT = convertToInches(42.5);
}

