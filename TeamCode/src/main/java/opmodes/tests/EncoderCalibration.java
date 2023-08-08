package opmodes.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import modules.configuration.odometry.OdometryConstants;
import modules.odometry.encoders.Encoder;

@TeleOp(name = "Odometry: ENCODER CALIBRATION", group = "Testus")
public class EncoderCalibration extends LinearOpMode {

    private Encoder leftEncoder;
    private Encoder rightEncoder;
    private FtcDashboard dashboard;

    private double TARGET_CM = 300.0;
    private double TARGET_TICKS = TARGET_CM / OdometryConstants.TICKS_TO_CM;

    private double LEFT_COEFF  = 0.9905;
    private double RIGHT_COEFF = 0.9821; // Nu uita sa modifici coeff

    @Override
    public void runOpMode() throws InterruptedException {

        dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        leftEncoder    = new Encoder(hardwareMap.get(DcMotorEx.class, "left"), OdometryConstants.LEFT_MULTIPLIER, OdometryConstants.LEFT_ENCODER_DIR);
        rightEncoder   = new Encoder(hardwareMap.get(DcMotorEx.class, "right"), OdometryConstants.RIGHT_MULTIPLIER, OdometryConstants.RIGHT_ENCODER_DIR);

        waitForStart();

        while (opModeIsActive()) {

            leftEncoder.updateValues();;
            rightEncoder.updateValues();

            telemetry.addData("Left Encoder Ticks", leftEncoder.getCurrentPosition());
            telemetry.addData("Right Encoder Ticks", rightEncoder.getCurrentPosition());

            telemetry.addData("Target Ticks", TARGET_TICKS);

            telemetry.addData("Left Encoder Distance in CM", leftEncoder.getGlobalDistance() / LEFT_COEFF);
            telemetry.addData("Right Encoder Distance in CM", rightEncoder.getGlobalDistance() / RIGHT_COEFF);

            telemetry.addData("Left Ratio",  leftEncoder.getGlobalDistance() / TARGET_CM / LEFT_COEFF);
            telemetry.addData("Right Ratio",  rightEncoder.getGlobalDistance() / TARGET_CM / RIGHT_COEFF);

            telemetry.update();

        }
    }
}
