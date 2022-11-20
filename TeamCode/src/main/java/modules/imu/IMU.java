package modules.imu;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

public abstract class IMU implements BNO055IMU, IMUInterface {

    // Singleton Pattern
    private IMU imu;

    private final LinearOpMode opMode;

    private BNO055IMU.AngleUnit angleUnit;
    private BNO055IMU.AccelUnit accelUnit;

    private AxesOrder axesOrder         = AxesOrder.XYZ;
    private AxesReference axesReference = AxesReference.EXTRINSIC;

    public IMU(LinearOpMode opMode, BNO055IMU.AngleUnit angleUnit, BNO055IMU.AccelUnit accelUnit, AxesOrder axesOrder, AxesReference axesReference) {

        this.opMode = opMode;

        imu = (IMU) opMode.hardwareMap.get(BNO055IMU.class, "IMU");

        this.angleUnit = angleUnit;
        this.accelUnit = accelUnit;

        setAxesOrder(axesOrder);
        setAxesRefrence(axesReference);

        parametersInit(this.angleUnit, this.accelUnit);
    }

    public void parametersInit(BNO055IMU.AngleUnit angleUnit, BNO055IMU.AccelUnit accelUnit) {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();

        parameters.angleUnit = angleUnit;
        parameters.accelUnit = accelUnit;

        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // Fișier cu calibrarea
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu.initialize(parameters);
    }

    public void setAngleUnit(BNO055IMU.AngleUnit angleUnit) {
        this.angleUnit = angleUnit;
        imu.getParameters().angleUnit = angleUnit;
    }

    public void setAccelUnit(BNO055IMU.AccelUnit accelUnit) {
        this.accelUnit = accelUnit;
        imu.getParameters().accelUnit = accelUnit;
    }

    public void setAxesOrder(AxesOrder axesOrder) {
        this.axesOrder = axesOrder;
    }

    public void setAxesRefrence(AxesReference axesReference) {
        this.axesReference = axesReference;
    }

    public void setTemperatureUnit(BNO055IMU.TempUnit temperatureUnit) {
        imu.getParameters().temperatureUnit = temperatureUnit;
    }


}
