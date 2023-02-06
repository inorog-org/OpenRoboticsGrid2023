package opmodes;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import modules.drive.subsystems.teleop.Drivebase;
import modules.imu.IMU;

@TeleOp(name = "Concept: Mecanum Lunatica", group = "Concept")
public class Cuirasat extends LinearOpMode {

    private Drivebase drivebase;
    private IMU imu;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void runOpMode() throws InterruptedException {

        imu = new IMU(this, BNO055IMU.AngleUnit.RADIANS, BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC);
        imu.remapAxis( 0x24, 0xc);

        drivebase = new Drivebase(this, imu);

        waitForStart();

        while (opModeIsActive()) {

            drivebase.control();
            drivebase.telemetry();
            telemetry.update();

        }

    }
}