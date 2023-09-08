package modules.odometry.calibration;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import modules.configuration.odometry.OdometryConstants;
import modules.imu.IMU;
import modules.odometry.encoders.Encoder;

@TeleOp(name = "Odometry: INDIVIDUAL LATERAL LENGTH CALIBRATION", group = "Testus")
@Disabled
public class IndividualLateralLengthCalibrationOpMode extends LinearOpMode {

    private IMU imu;
    private Encoder leftEncoder;
    private Encoder rightEncoder;
    private FtcDashboard dashboard;
    private double TARGET_ROTATIONS = 10.0;
    private double LATERAL_LEFT_MULTIPLIER  = 1.0;
    private double LATERAL_RIGHT_MULTIPLIER = 1.0;
    private double TARGET_LEFT_TICKS  = TARGET_ROTATIONS * 2 * Math.PI * OdometryConstants.leftLength * LATERAL_LEFT_MULTIPLIER   / OdometryConstants.TICKS_TO_CM;
    private double TARGET_RIGHT_TICKS = TARGET_ROTATIONS * 2 * Math.PI * OdometryConstants.rightLength * LATERAL_RIGHT_MULTIPLIER / OdometryConstants.TICKS_TO_CM;

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

        double firstLeftPosition       = leftEncoder.currentPosition;
        double firstLeftGlobalDistance = leftEncoder.getGlobalDistance();

        double angleLeftOnEncoders = 0.0;
        double deltaLeftTicks;

        rightEncoder.updateValues();

        double firstRightPosition       = rightEncoder.currentPosition;
        double firstRightGlobalDistance = rightEncoder.getGlobalDistance();

        double angleRightOnEncoders = 0.0;
        double deltaRightTicks;

        while(opModeIsActive()) {

            // ---------------- Left ------------------ //

            leftEncoder.updateValues();

            deltaLeftTicks = leftEncoder.getCurrentPosition() - firstLeftPosition;

            telemetry.addData("Left Encoder Ticks", deltaLeftTicks);

            telemetry.addData("Left Target Ticks", TARGET_LEFT_TICKS);

            telemetry.addData("Percent Ticks: ", deltaLeftTicks / TARGET_LEFT_TICKS * 100);

            telemetry.addData("Left Ticks Ratio", deltaLeftTicks / TARGET_LEFT_TICKS);

            angleLeftOnEncoders = (leftEncoder.getGlobalDistance() - firstLeftGlobalDistance) / (Math.PI * OdometryConstants.leftLength * LATERAL_LEFT_MULTIPLIER) * 180.0;

            telemetry.addData("Left Degrees Made",  angleLeftOnEncoders);

             // ---------------- Right ------------------ //

            rightEncoder.updateValues();

            deltaRightTicks = rightEncoder.getCurrentPosition() - firstRightPosition;

            telemetry.addData("Right Encoder Ticks", deltaRightTicks);

            telemetry.addData("Right Target Ticks", TARGET_RIGHT_TICKS);

            telemetry.addData("Percent Ticks: ", deltaRightTicks / TARGET_RIGHT_TICKS * 100);

            telemetry.addData("Right Ticks Ratio", deltaRightTicks / TARGET_RIGHT_TICKS);

            angleRightOnEncoders = (rightEncoder.getGlobalDistance() - firstRightGlobalDistance) / (Math.PI * OdometryConstants.rightLength * LATERAL_RIGHT_MULTIPLIER) * 180.0;

            telemetry.addData("Right Degrees Made",  angleRightOnEncoders);

             // ----------------- IMU ---------------- //

            angleOnIMU = imu.getHeading() - firstAngleOnIMU;

            telemetry.addData("IMU Degrees Made", angleOnIMU * Math.PI / 180.0);

            telemetry.update();
        }

        while(!gamepad1.b) {

            telemetry.addData("Left Ratio is ", angleLeftOnEncoders  / angleOnIMU);
            telemetry.addData("Right Ratio is ",angleRightOnEncoders / angleOnIMU);

            telemetry.update();
        }

    }
}
