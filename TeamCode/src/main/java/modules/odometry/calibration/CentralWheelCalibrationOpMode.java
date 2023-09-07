package modules.odometry.calibration;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import modules.configuration.odometry.OdometryConstants;
import modules.odometry.encoders.Encoder;

@TeleOp(name = "Odometry: CENTRAL ENCODER CALIBRATION", group = "Testus")
public class CentralWheelCalibrationOpMode extends LinearOpMode {
    private Encoder centralEncoder;
    private FtcDashboard dashboard;

    private final double TARGET_CM = 300.0; // cm
    private final double TARGET_TICKS = TARGET_CM / OdometryConstants.TICKS_TO_CM;

    private final double CENTRAL_MULTIPLIER  = 1.0;  // Valoare care trebuie modificată

    @Override
    public void runOpMode() throws InterruptedException {

        dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());

        centralEncoder  = new Encoder(hardwareMap.get(DcMotorEx.class, "central"), CENTRAL_MULTIPLIER, OdometryConstants.CENTRAL_ENCODER_DIR);

        waitForStart();

        while (opModeIsActive()) {

            centralEncoder.updateValues();

            telemetry.addData("Central Encoder Ticks", centralEncoder.getCurrentPosition());

            telemetry.addData("Target Ticks", TARGET_TICKS);
            telemetry.addData("Target CM", 300.0);

            telemetry.addData("Central Encoder Distance in CM",  centralEncoder.getGlobalDistance()  /  CENTRAL_MULTIPLIER);

            telemetry.addData("Central Ratio",  centralEncoder.getGlobalDistance()  / TARGET_CM / CENTRAL_MULTIPLIER);

            telemetry.update();
        }
    }
}
