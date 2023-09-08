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

@TeleOp(name = "Odometry: FORWARD LENGTH CALIBRATION", group = "Testus")
@Disabled
public class ForwardLengthCalibrationOpMode extends LinearOpMode {

    private IMU imu;
    private Encoder centralEncoder;
    private FtcDashboard dashboard;

    private double TARGET_ROTATIONS = 10.0;
    private double FORWARD_MULTIPLIER = 1.0;
    private double TARGET_TICKS = TARGET_ROTATIONS * 2 * Math.PI * OdometryConstants.centralLength * FORWARD_MULTIPLIER / OdometryConstants.TICKS_TO_CM;

    @Override
    public void runOpMode() throws InterruptedException {

        imu = new IMU(this, BNO055IMU.AngleUnit.RADIANS, BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC);
        imu.remapAxis( 0x24, 0xc);

        dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        centralEncoder  = new Encoder(hardwareMap.get(DcMotorEx.class, "central"), OdometryConstants.CENTRAL_MULTIPLIER, OdometryConstants.CENTRAL_ENCODER_DIR);

        waitForStart();

        centralEncoder.updateValues();

        double firstAngleOnIMU     = imu.getHeading();
        double firstPosition       = centralEncoder.currentPosition;
        double firstGlobalDistance = centralEncoder.getGlobalDistance();

        double angleOnIMU      = 0.0;
        double angleOnEncoders = 0.0;
        double deltaTicks;

        while(opModeIsActive()) {

            centralEncoder.updateValues();

            deltaTicks = centralEncoder.getCurrentPosition() - firstPosition;

            telemetry.addData("Central Encoder Ticks", deltaTicks);

            telemetry.addData("Target Ticks", TARGET_TICKS);

            telemetry.addData("Ticks Ratio", deltaTicks / TARGET_TICKS);

            angleOnEncoders = (centralEncoder.getGlobalDistance() - firstGlobalDistance) / (OdometryConstants.centralLength * FORWARD_MULTIPLIER) ;
            angleOnIMU = imu.getHeading() - firstAngleOnIMU;

            telemetry.addData("Forward Degrees Made",  angleOnEncoders * 180.0 / Math.PI);

            telemetry.addData("IMU Degrees Made", angleOnIMU * 180.0 / Math.PI);

            telemetry.update();
        }

        while(!gamepad1.b) {

            telemetry.addData("Forward Ratio is ", angleOnEncoders / angleOnIMU);

            telemetry.update();
        }
    }
}
