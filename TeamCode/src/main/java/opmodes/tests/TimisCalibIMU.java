package opmodes.tests;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import modules.imu.IMU;
import modules.imu.dedicated.CalibrateIMU;

@TeleOp(name = "Lunatica: Calib IMU", group = "Testus")
public class TimisCalibIMU extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        IMU imu = new IMU(this, BNO055IMU.AngleUnit.RADIANS, BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC);
        CalibrateIMU calibrateIMU = new CalibrateIMU(this, imu);

        waitForStart();

        calibrateIMU.calibrateIMU();

    }
}
