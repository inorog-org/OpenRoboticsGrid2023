package modules.imu.dedicated;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;

import modules.imu.IMU;

public class CalibrateIMU {

    private LinearOpMode opMode;
    private IMU imu;

    public CalibrateIMU(LinearOpMode opMode,IMU imu) {
        this.imu = imu;
        this.opMode = opMode;
    }

    // IMU Calibration
    public void calibrateIMU() {

        while(!opMode.gamepad1.a) {
            // Get the calibration data
            BNO055IMU.CalibrationData calibrationData = imu.readCalibrationData();

            // Save the calibration data
            String filename = "BNO055IMUCalibration.json";

            File file = AppUtil.getInstance().getSettingsFile(filename);
            ReadWriteFile.writeFile(file, calibrationData.serialize());

            opMode.telemetry.addLine("Apasa A pentru a termina calibrarea!");
            opMode.telemetry.update();
        }
    }
}
