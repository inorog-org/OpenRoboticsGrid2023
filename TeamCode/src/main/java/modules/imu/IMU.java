package modules.imu;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.bosch.BNO055IMU;

import com.qualcomm.hardware.bosch.BNO055IMUImpl;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class IMU extends BNO055IMUImpl implements IMUInterface, RemapAxis {

    private final LinearOpMode opMode;

    private final BNO055IMU.AngleUnit angleUnit;
    private final BNO055IMU.AccelUnit accelUnit;

    private AxesOrder axesOrder;
    private AxesReference axesReference;

    private final int AXIS_MAP_BYTE = 0x24;
    private final int SIGN_MAP_BYTE = 0x0;

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

        parametersInit(angleUnit, accelUnit);

        remapAxis(AXIS_MAP_BYTE, SIGN_MAP_BYTE);
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

    // Detect Axis
    public void detectAxis() {

        // Each Detection
        byte zDetect;
        byte yDetect;

        // Index for each Axis
        byte zAxisIndex;
        byte yAxisIndex;
        byte xAxisIndex;

        // Sign for each Axis
        byte zSign;
        byte ySign;
        byte xSign;

        // Mapăm axele să fie DEFAULT
        remapAxis(0x24, 0x0);

        // Mesaj Init
        opMode.telemetry.addLine("Poziționează robotul într-o poziție de inițiere");
        opMode.telemetry.addLine();
        opMode.telemetry.addLine("Apasă A pentru a inițializa poziția");
        opMode.telemetry.update();

        // Wait press A
        while (!opMode.gamepad1.a) ;
        opMode.sleep(500);

        // Citire Unghi Init
        Orientation initOrientation = getAngularOrientation();

        /// Verificăm Axa Z
        zDetect    =  firstDetect(initOrientation);
        zAxisIndex = (byte) Math.abs(zDetect);
        zSign      = (byte) Math.signum(zDetect);

        // Mesaj
        opMode.telemetry.addLine("Poziționează robotul în poziția de inițiere");
        opMode.telemetry.addLine();
        opMode.telemetry.addLine("Apasă A pentru a începe detecția");
        opMode.telemetry.update();

        // Wait press A
        while (!opMode.gamepad1.a) ;
        opMode.sleep(500);

        // Verificăm Axa Y
        switch (zAxisIndex) {
            case 1: // Ne mai rămân de scanat secondAngle și thirdAngle
                yDetect = secondDetection(initOrientation, 1);
                break;
            case 2: // Ne mai rămân de scanat firstAngle și thirdAngle
                yDetect = secondDetection(initOrientation, 2);
                break;
            case 3: // Ne mai rămân de scanat firstAngle și secondAngle
                yDetect = secondDetection(initOrientation, 3);
                break;
            default: yDetect = 0; break;
        }
        yAxisIndex = (byte) Math.abs(yDetect);
        ySign      = (byte) Math.signum(yDetect);

        // Mesaj
        opMode.telemetry.addLine("Poziționează robotul în poziția de inițiere");
        opMode.telemetry.addLine();
        opMode.telemetry.addLine("Apasă A pentru a începe detecția");
        opMode.telemetry.update();

        // Wait press A
        while (!opMode.gamepad1.a) ;
        opMode.sleep(500);

        // Verificăm Axa X
        xAxisIndex = (byte) (6 - (zAxisIndex + yAxisIndex));
        xSign      = (byte) thirdDetection(initOrientation, xAxisIndex);

        // Facem Calculele aferente și afișăm

        int zByte = 3 - zAxisIndex;
        int yByte = 3 - yAxisIndex;
        int xByte = 3 - xAxisIndex;

        xSign = (byte) ((xSign == 1) ? 0 : 1);
        ySign = (byte) ((ySign == 1) ? 0 : 1);;
        zSign = (byte) ((zSign == 1) ? 0 : 1);;

        opMode.telemetry.addLine(zAxisIndex + " angle is for Z-axis.");
        opMode.telemetry.addLine(yAxisIndex + " angle is for Y-axis.");
        opMode.telemetry.addLine(xAxisIndex + " angle is for X-axis.");
        opMode.telemetry.addLine();
        opMode.telemetry.addData("Map Byte ",  "0x"  + Integer.toHexString((zByte << 4) | (yByte << 2) | xByte));
        opMode.telemetry.addData("Sign Byte ", "0x"  + Integer.toHexString((xSign << 3) | (ySign << 2) | zSign));
        opMode.telemetry.addLine();
        opMode.telemetry.addLine("Apasă A pentru a FINALIZA");
        opMode.telemetry.update();

        // Wait press A
        while (!opMode.gamepad1.a);
    }

    private byte firstDetect(Orientation initOrientation){

        /// Verificăm Axa Z
        while (true) {

            // Mesaj pentru rotirea robotului
            opMode.telemetry.addLine("Rotește robotul la 90 de grade la POZITIV pe Axa Z");

            // Citim Datele
            Orientation orientation = getAngularOrientation();

            // Afisam Datele
            opMode.telemetry.addData("First Angle ", orientation.firstAngle);
            opMode.telemetry.addData("Second Angle ", orientation.secondAngle);
            opMode.telemetry.addData("Third Angle ", orientation.thirdAngle);
            opMode.telemetry.update();

            // Vedem diferenta de grade dintre orientarea curentă și cea inițială
            double deltaFirst  = getShortestAngleDEGREES(initOrientation.firstAngle, orientation.firstAngle);
            double deltaSecond = getShortestAngleDEGREES(initOrientation.secondAngle, orientation.secondAngle);
            double deltaThird  = getShortestAngleDEGREES(initOrientation.thirdAngle, orientation.thirdAngle);

            // Verificăm care e primul unghi care execută 90 de grade
            // Axa care execută prima 90 de grade este axa Z
            // Vom returna Indexul Axei, dar și semnul acesteia

            if (Math.abs(deltaFirst) >= 90) {
                return (byte) (1 * Math.signum(deltaFirst));
            }

            if (Math.abs(deltaSecond) >= 90) {
                return (byte) (2 * Math.signum(deltaSecond));
            }

            if (Math.abs(deltaThird) >= 90) {
                return (byte) (3 * Math.signum(deltaThird));
            }
        }
    }

    private byte secondDetection(Orientation initOrientation, int zAxisIndex){
        while (true){

            // Mesaj pentru rotirea robotului
            opMode.telemetry.addLine("Rotește robotul la 45 de grade la POZITIV pe Axa Y");

            // Citim Datele
            Orientation orientation = getAngularOrientation();

            // Afisam Datele
            if (zAxisIndex != 1) opMode.telemetry.addData("First Angle ", orientation.firstAngle);
            if (zAxisIndex != 2) opMode.telemetry.addData("Second Angle ", orientation.secondAngle);
            if (zAxisIndex != 3) opMode.telemetry.addData("Third Angle ", orientation.thirdAngle);
            opMode.telemetry.update();

            // Vedem diferenta de grade dintre orientarea curentă și cea inițială
            double deltaFirst  = (zAxisIndex != 1) ? getShortestAngleDEGREES(initOrientation.firstAngle, orientation.firstAngle)   : 0;
            double deltaSecond = (zAxisIndex != 2) ? getShortestAngleDEGREES(initOrientation.secondAngle, orientation.secondAngle) : 0;
            double deltaThird  = (zAxisIndex != 3) ? getShortestAngleDEGREES(initOrientation.thirdAngle, orientation.thirdAngle)   : 0;

            // Verificăm care e primul unghi care execută 45 de grade
            // Vom returna Indexul Axei, dar și semnul acesteia
            if (Math.abs(deltaFirst) >= 45) {
                return (byte) (1 * Math.signum(deltaFirst));
            }

            if (Math.abs(deltaSecond) >= 45) {
                return (byte) (2 * Math.signum(deltaSecond));
            }

            if (Math.abs(deltaThird) >= 45) {
                return (byte) (3 * Math.signum(deltaThird));
            }
        }
    }

    private byte thirdDetection(Orientation initOrientation, int xAxisIndex){
        while (true) {

            // Mesaj pentru rotirea robotului
            opMode.telemetry.addLine("Rotește robotul la 45 de grade la POZITIV pe Axa X");

            // Citim Datele
            Orientation orientation = getAngularOrientation();

            double delta = 0;
            // Afisam Datele
            switch (xAxisIndex){
                case 1: {
                    opMode.telemetry.addData("First Angle ", orientation.firstAngle);
                    delta = getShortestAngleDEGREES(initOrientation.firstAngle, orientation.firstAngle);
                }
                    break;
                case 2: {
                    opMode.telemetry.addData("Second Angle ", orientation.secondAngle);
                    delta = getShortestAngleDEGREES(initOrientation.secondAngle, orientation.secondAngle);
                }
                    break;
                case 3: {
                    opMode.telemetry.addData("Third Angle ", orientation.thirdAngle);
                    delta = getShortestAngleDEGREES(initOrientation.thirdAngle, orientation.thirdAngle);
                }
                    break;
            }
            opMode.telemetry.update();

            if(Math.abs(delta) >= 45){
                return (byte) Math.signum(delta);
            }
        }
    }

    // IMU Calibration
    public void calibrateIMU() {


    }

    private double getShortestAngleDEGREES(double current, double target) {

        double angle = target - current;

        return (angle + 540.0f) % 360 - 180.0f;
    }

    private double getShortestAngleRADIANS(double current, double target) {

        double angle = target - current;

        return (angle + 3 * Math.PI) % (2 * Math.PI) - Math.PI;
    }

    private double getShortestAngle(double current, double target, @NonNull AngleUnit angleUnit) {

        switch (angleUnit) {
            case DEGREES:
                return getShortestAngleDEGREES(current, target);
            case RADIANS:
                return getShortestAngleRADIANS(current, target);
            default:
                return 0;
        }
    }

    // TODO: IMU CALIBRATION MODE
}
