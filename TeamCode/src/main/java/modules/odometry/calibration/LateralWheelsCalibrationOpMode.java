package modules.odometry.calibration;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import modules.configuration.odometry.OdometryConstants;
import modules.odometry.encoders.Encoder;

@TeleOp(name = "Odometry: LATERAL ENCODER CALIBRATION", group = "Testus")
public class LateralWheelsCalibrationOpMode extends LinearOpMode {

    private Encoder leftEncoder;
    private Encoder rightEncoder;
    private FtcDashboard dashboard;

    private final double TARGET_CM = 300.0; // cm
    private final double TARGET_TICKS = TARGET_CM / OdometryConstants.TICKS_TO_CM;

    private final double LEFT_MULTIPLIER  = 1.0;  // Valoare care trebuie modificată
    private final double RIGHT_MULTIPLIER = 1.0;  // Valoare care trebuie modificată

    @Override
    public void runOpMode() throws InterruptedException {

        dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        leftEncoder    = new Encoder(hardwareMap.get(DcMotorEx.class, "left"),   LEFT_MULTIPLIER, OdometryConstants.LEFT_ENCODER_DIR);
        rightEncoder   = new Encoder(hardwareMap.get(DcMotorEx.class, "right"), RIGHT_MULTIPLIER, OdometryConstants.RIGHT_ENCODER_DIR);

        waitForStart();

        while (opModeIsActive()) {

            leftEncoder.updateValues();
            rightEncoder.updateValues();

            telemetry.addData("Left Encoder Ticks", leftEncoder.getCurrentPosition());
            telemetry.addData("Right Encoder Ticks", rightEncoder.getCurrentPosition());

            telemetry.addData("Target Ticks", TARGET_TICKS);
            telemetry.addData("Target CM", 300.0);

            telemetry.addData("Left Encoder Distance in CM",  leftEncoder.getGlobalDistance()  /  LEFT_MULTIPLIER);
            telemetry.addData("Right Encoder Distance in CM", rightEncoder.getGlobalDistance() / RIGHT_MULTIPLIER);

            telemetry.addData("Left Ratio",  leftEncoder.getGlobalDistance()  / TARGET_CM /   LEFT_MULTIPLIER);
            telemetry.addData("Right Ratio", rightEncoder.getGlobalDistance() / TARGET_CM /  RIGHT_MULTIPLIER);

            telemetry.update();
        }
    }
}
