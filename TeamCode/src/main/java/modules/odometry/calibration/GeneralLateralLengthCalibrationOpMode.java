package modules.odometry.calibration;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import modules.configuration.odometry.OdometryConstants;
import modules.imu.IMU;
import modules.odometry.encoders.Encoder;
import modules.odometry.encoders.HeadingEncoders;

@TeleOp(name = "Odometry: GENERAL LATERAL LENGTH CALIBRATION", group = "Testus")
public class GeneralLateralLengthCalibrationOpMode extends LinearOpMode {

    private IMU imu;
    private Encoder leftEncoder;
    private Encoder rightEncoder;
    private FtcDashboard dashboard;
    private double TARGET_ROTATIONS = 10.0;
    private double LATERAL_MULTIPLIER = 1.0;

    @Override
    public void runOpMode() throws InterruptedException {

        imu = new IMU(this, BNO055IMU.AngleUnit.RADIANS, BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC);
        imu.remapAxis( 0x24, 0xc);

        dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        leftEncoder   = new Encoder(hardwareMap.get(DcMotorEx.class, "left"),  OdometryConstants.LEFT_MULTIPLIER,  OdometryConstants.LEFT_ENCODER_DIR);
        rightEncoder  = new Encoder(hardwareMap.get(DcMotorEx.class, "right"), OdometryConstants.RIGHT_MULTIPLIER, OdometryConstants.RIGHT_ENCODER_DIR);

        waitForStart();

        double firstAngleOnIMU = imu.getHeading();
        double angleOnIMU      = 0.0;

        leftEncoder.updateValues();
        rightEncoder.updateValues();

        double angle = (rightEncoder.getDeltaDistance() - leftEncoder.getDeltaDistance()) / ((OdometryConstants.leftLength + OdometryConstants.rightLength) * LATERAL_MULTIPLIER);
        double firstLateralAngle = angle;
        double angleOnLateral = 0.0;

        while(opModeIsActive()) {

            leftEncoder.updateValues();
            rightEncoder.updateValues();

            angle += (rightEncoder.getDeltaDistance() - leftEncoder.getDeltaDistance()) / ((OdometryConstants.leftLength + OdometryConstants.rightLength) * LATERAL_MULTIPLIER);

            angleOnIMU     = imu.getHeading() - firstAngleOnIMU;
            angleOnLateral = angle - firstLateralAngle;

            telemetry.addData("Angle On IMU ", angleOnIMU * Math.PI / 180.0);
            telemetry.addData("Angle On Lateral ", angleOnLateral * Math.PI / 180.0);
            telemetry.addData("Percent to Target", angleOnIMU / (TARGET_ROTATIONS * 2 * Math.PI) * 100);

            telemetry.update();
        }

        while(!gamepad1.b) {

            telemetry.addData("Lateral Ratio is ", angleOnLateral  / angleOnIMU);

            telemetry.update();
        }
    }
}
