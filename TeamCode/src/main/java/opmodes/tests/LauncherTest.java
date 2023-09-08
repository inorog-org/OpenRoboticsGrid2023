package opmodes.tests;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import manipulation.Launcher;

@TeleOp(name = "Lunatica: Launcher PID", group = "Testus")
@Config
public class LauncherTest extends LinearOpMode {

    private Launcher launcher;
    @Override
    public void runOpMode() throws InterruptedException {

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(dashboard.getTelemetry(), telemetry);

        launcher = new Launcher(hardwareMap, gamepad2);

        waitForStart();

        while(opModeIsActive()) {

            launcher.teleOpLauncher();
        }

    }
}
