package opmodes.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import modules.drive.main.Motors;
import modules.drive.subsystems.pursuit.PurePursuit;
import modules.drive.subsystems.pursuit.splines.Line;
import modules.imu.IMU;
import modules.odometry.OdometryBuilder;
import modules.odometry.utils.Position;

@TeleOp(name = "Odometry: Autonomous", group = "Testus")
@Disabled
public class AutonomousOdometry extends LinearOpMode {

    private Motors motors;
    private IMU imu;
    private OdometryBuilder odometryBuilder;

    private PurePursuit pursuit;

    private FtcDashboard dashboard;

    private Line path;

    @Override
    public void runOpMode() throws InterruptedException {

        dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        path = new Line(new Position(0, 0), new Position(0,120,0));

        motors = new Motors(hardwareMap);
        motors.setupEncoders(DcMotor.RunMode.RUN_USING_ENCODER);

        imu = new IMU(this, BNO055IMU.AngleUnit.RADIANS, BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC);
        imu.remapAxis( 0x24, 0xc);

        odometryBuilder = new OdometryBuilder(this, imu);

        pursuit = new PurePursuit(this, motors, odometryBuilder);

        waitForStart();

        pursuit.handleTrajectory(path);
    }
}
