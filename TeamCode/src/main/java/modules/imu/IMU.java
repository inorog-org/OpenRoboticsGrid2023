package modules.imu;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.bosch.BNO055IMU;

import com.qualcomm.hardware.bosch.BNO055IMUImpl;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import modules.odometry.utils.Coordinates;
import modules.odometry.utils.Heading;

public class IMU extends BNO055IMUImpl implements IMUInterface, IMURemapAxis, Heading, Coordinates {

    private final LinearOpMode opMode;

    private final BNO055IMU.AngleUnit angleUnit;
    private final BNO055IMU.AccelUnit accelUnit;

    private AxesOrder axesOrder;
    private AxesReference axesReference;

    private final int DEFAULT_AXIS_MAP_BYTE = 0x24;
    private final int DEFAULT_SIGN_MAP_BYTE = 0x0;

    // Constructors
    public IMU(LinearOpMode opMode, BNO055IMU.AngleUnit angleUnit, BNO055IMU.AccelUnit accelUnit) {
        this(opMode, angleUnit, accelUnit, AxesOrder.ZYX, AxesReference.INTRINSIC);
    }

    public IMU(LinearOpMode opMode, BNO055IMU.AngleUnit angleUnit, BNO055IMU.AccelUnit accelUnit, AxesOrder axesOrder, AxesReference axesReference) {

        super(opMode.hardwareMap.get(BNO055IMUImpl.class, "IMU").getDeviceClient(), true);

        this.opMode = opMode;

        this.angleUnit = angleUnit;
        this.accelUnit = accelUnit;

        setAxesOrder(axesOrder);
        setAxesRefrence(axesReference);

        parametersInit(angleUnit, accelUnit);

        remapAxis(DEFAULT_AXIS_MAP_BYTE, DEFAULT_SIGN_MAP_BYTE);
    }

    // Init
    public void parametersInit(AngleUnit angleUnit, AccelUnit accelUnit) {

        Parameters parameters = new Parameters();

        // Sensor Mode
        parameters.mode = SensorMode.IMU; // Sau cu NDOF dacă vrem o calibrare mai bună cu Magnetometrul

        // Units
        parameters.temperatureUnit = TempUnit.CELSIUS;
        parameters.angleUnit = angleUnit;
        parameters.accelUnit = accelUnit;

        // Accelerometer Parameters
        parameters.accelRange = AccelRange.G4;
        parameters.accelBandwidth = AccelBandwidth.HZ62_5;
        parameters.accelPowerMode = AccelPowerMode.NORMAL;

        // Gyroscope Parameters
        parameters.gyroRange = GyroRange.DPS2000;
        parameters.gyroBandwidth = GyroBandwidth.HZ32;
        parameters.gyroPowerMode = GyroPowerMode.NORMAL;

        // Magnetometer Parameters
        parameters.magRate = MagRate.HZ30;
        parameters.magOpMode = MagOpMode.REGULAR;
        parameters.magPowerMode = MagPowerMode.NORMAL;

        // Calibration File
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // Fișier cu calibrarea

        // Algoritm
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Logging
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";

        this.initialize(parameters);
    }

    // Setters
    public void setAxesOrder(AxesOrder axesOrder) {

        this.axesOrder = axesOrder;
    }

    public void setAxesRefrence(AxesReference axesReference) {

        this.axesReference = axesReference;
    }

    // Getters
    public Orientation getAngularOrientationDefault() {

        return getAngularOrientation(axesReference, axesOrder, angleUnit.toAngleUnit());
    }

    @Override
    public String getDeviceName() {

        return "BNO055";
    }

    @Override
    public Manufacturer getManufacturer() {

        return Manufacturer.Other;
    }

    // Remap Axis
    public void remapAxis(@NonNull Axis xAxis, @NonNull Axis yAxis, @NonNull Axis zAxis, @NonNull Sign xSign, @NonNull Sign ySign, @NonNull Sign zSign) {

        byte AXIS_MAP_CONFIG_BYTE = (byte) (xAxis.value | (yAxis.value << 2) | (zAxis.value << 4));

        byte AXIS_MAP_SIGN_BYTE = (byte) (xSign.sign | (ySign.sign << 1) | (zSign.sign << 2));

        remapAxis(AXIS_MAP_CONFIG_BYTE, AXIS_MAP_SIGN_BYTE);
    }

    public void remapAxis(int AXIS_MAP_CONFIG_BYTE, int AXIS_MAP_SIGN_BYTE) {

        // Setăm senzorul pe Configuration Mode
        write8(BNO055IMU.Register.OPR_MODE, BNO055IMU.SensorMode.CONFIG.bVal & 0x0F);

        opMode.sleep(100);

        // Modificăm registrul AXIS_MAP_CONFIG cu configurația dorită
        write8(BNO055IMU.Register.AXIS_MAP_CONFIG, AXIS_MAP_CONFIG_BYTE);

        // Modificăm registrul AXIS_MAP_SIGN cu configurația dorită
        write8(BNO055IMU.Register.AXIS_MAP_SIGN, AXIS_MAP_SIGN_BYTE);

        // Setăm Senzorul pe IMU Mode
        write8(BNO055IMU.Register.OPR_MODE, BNO055IMU.SensorMode.IMU.bVal & 0x0F);

        opMode.sleep(100);
    }

    @Override
    public double getHeading() {
        return getAngularOrientation().firstAngle;
    }

    @Override
    public void initPositionTracker(Position initalPosition, Velocity initialVelocity, int msPollInterval) {
          startAccelerationIntegration(initalPosition, initialVelocity, msPollInterval);
    }

    public Position getCoordinates() {
        return getPosition();

    }
}
