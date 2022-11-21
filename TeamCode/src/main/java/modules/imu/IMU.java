package modules.imu;

import static java.lang.Thread.sleep;

import com.qualcomm.hardware.bosch.BNO055IMU;

import com.qualcomm.hardware.bosch.BNO055IMUImpl;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Axis;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

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

        // Sensor Mode
        parameters.mode = SensorMode.IMU; // Sau cu NDOF dacă vrem o calibrare mai bună cu Magnetometrul

        // Units
        parameters.temperatureUnit = TempUnit.CELSIUS;
        parameters.angleUnit       = angleUnit;
        parameters.accelUnit       = accelUnit;

        // Accelerometer Parameters
        parameters.accelRange      = AccelRange.G4;
        parameters.accelBandwidth  = AccelBandwidth.HZ62_5;
        parameters.accelPowerMode  = AccelPowerMode.NORMAL;

        // Gyroscope Parameters
        parameters.gyroRange       = GyroRange.DPS2000;
        parameters.gyroBandwidth   = GyroBandwidth.HZ32;
        parameters.gyroPowerMode   = GyroPowerMode.NORMAL;

        // Magnetometer Parameters
        parameters.magRate         = MagRate.HZ30;
        parameters.magOpMode       = MagOpMode.REGULAR;
        parameters.magPowerMode    = MagPowerMode.NORMAL;

        // Calibration File
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // Fișier cu calibrarea

        // Algoritm
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Logging
        parameters.loggingEnabled = true;
        parameters.loggingTag     = "IMU";

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
    public void remapAxis(RemapAxis xAxis, RemapAxis yAxis, RemapAxis zAxis, Sign xSign, Sign ySign, Sign zSign) throws InterruptedException {

        byte AXIS_MAP_CONFIG_BYTE = (byte) (xAxis.value & (yAxis.value << 2) & (zAxis.value << 4));

        byte AXIS_MAP_SIGN_BYTE   = (byte) (xSign.sign  & (ySign.sign << 1)  & (zSign.sign << 2));

        remapAxis(AXIS_MAP_CONFIG_BYTE, AXIS_MAP_SIGN_BYTE);
    }

    public void remapAxis(byte AXIS_MAP_CONFIG_BYTE, byte AXIS_MAP_SIGN_BYTE) throws InterruptedException {

      // Setăm senzorul pe Configuration Mode
      write8(BNO055IMU.Register.OPR_MODE,BNO055IMU.SensorMode.CONFIG.bVal & 0x0F);

      sleep(100);

      // Modificăm registrul AXIS_MAP_CONFIG cu configurația dorită
      write8(BNO055IMU.Register.AXIS_MAP_CONFIG,AXIS_MAP_CONFIG_BYTE & 0x0F);

      // Modificăm registrul AXIS_MAP_SIGN cu configurația dorită
      write8(BNO055IMU.Register.AXIS_MAP_SIGN,AXIS_MAP_SIGN_BYTE & 0x0F);

      // Setăm Senzorul pe IMU Mode
      write8(BNO055IMU.Register.OPR_MODE,BNO055IMU.SensorMode.IMU.bVal & 0x0F);

      sleep(100);
    }

    // Sign for Maping Axis
    public enum Sign {
        POSITIVE(0),
        NEGATIVE(1);

        public final int sign;

        Sign(int sign){
            this.sign = sign;
        }
    }

    public enum RemapAxis{
        X(0x00),
        Y(0x01),
        Z(0x10),
        INVALID(0x11);

        public final int value;

        RemapAxis(int value){
            this.value = value;
        }
    }

    // Detect Axis
    public void detectAxis() {

    }

    // TODO: DETECT AXIS, IMU CALIBRATION MODE
}
