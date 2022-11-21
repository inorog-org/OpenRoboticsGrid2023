package modules.imu;

import com.qualcomm.hardware.bosch.BNO055IMU;

import com.qualcomm.hardware.bosch.BNO055IMUImpl;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;

public class IMU extends BNO055IMUImpl implements IMUInterface{

    private final LinearOpMode opMode;

    private BNO055IMU.AngleUnit angleUnit;
    private BNO055IMU.AccelUnit accelUnit;

    private AxesOrder axesOrder;
    private AxesReference axesReference;

    // Constructors
    public IMU(LinearOpMode opMode, BNO055IMU.AngleUnit angleUnit, BNO055IMU.AccelUnit accelUnit) {
        this(opMode, angleUnit, accelUnit, AxesOrder.XYZ, AxesReference.EXTRINSIC);
    }

    public IMU(LinearOpMode opMode, BNO055IMU.AngleUnit angleUnit, BNO055IMU.AccelUnit accelUnit, AxesOrder axesOrder, AxesReference axesReference) {

        super(opMode.hardwareMap.i2cDeviceSynch.get("IMU"));

        this.opMode = opMode;

        this.angleUnit = angleUnit;
        this.accelUnit = accelUnit;

        setAxesOrder(axesOrder);
        setAxesRefrence(axesReference);

        parametersInit(angleUnit,accelUnit);
    }

    // Init
    public void parametersInit(AngleUnit angleUnit, AccelUnit accelUnit) {

        Parameters parameters = new Parameters();

        parameters.angleUnit = angleUnit;
        parameters.accelUnit = accelUnit;

        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // Fișier cu calibrarea
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        this.initialize(parameters);
    }

    // Setters
    public void setAngleUnit(BNO055IMU.AngleUnit angleUnit) {

        this.angleUnit = angleUnit;
        this.getParameters().angleUnit = angleUnit;
    }

    public void setAccelUnit(AccelUnit accelUnit) {

        this.accelUnit = accelUnit;
        this.getParameters().accelUnit = accelUnit;
    }

    public void setAxesOrder(AxesOrder axesOrder) {

        this.axesOrder = axesOrder;
    }

    public void setAxesRefrence(AxesReference axesReference) {

        this.axesReference = axesReference;
    }

    public void setTemperatureUnit(BNO055IMU.TempUnit temperatureUnit) {
        this.getParameters().temperatureUnit = temperatureUnit;
    }

    // Getters
    @Override
    public String getDeviceName() {

        return "BNO055";
    }

    @Override
    public Manufacturer getManufacturer() {

        return Manufacturer.Other;
    }
}
