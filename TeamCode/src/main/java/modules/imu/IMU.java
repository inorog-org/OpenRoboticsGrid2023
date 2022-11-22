package modules.imu;


import static com.qualcomm.hardware.bosch.BNO055IMU.AngleUnit.DEGREES;

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

        // Index for each Axis
        byte zAxisIndex = 0;
        byte yAxisIndex = 0;
        byte xAxisIndex = 0;

        // Mapăm axele să fie DEFAULT
        remapAxis(0x24, 0x0);

        // Mesaj Init
        opMode.telemetry.addLine("Poziționează robotul într-o poziție de inițiere");
        opMode.telemetry.addLine("Apasă A pentru a inițializa poziția");
        opMode.telemetry.update();

        // Wait press A
        while (!opMode.gamepad1.a) ;
        opMode.sleep(1000);

        // Citire Unghi Init
        Orientation initOrientation = getAngularOrientation(axesReference, axesOrder, org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES);

        boolean notDetected = true;

        /// Verificăm Axa Z
        while (notDetected) {

            // Mesaj pentru rotirea robotului
            opMode.telemetry.addLine("Rotește robotul la 90 de grade la POZITIV pe Axa Z");

            // Citim Datele
            Orientation orientation = getAngularOrientation(axesReference, axesOrder, org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES);

            // Afisam Datele
            opMode.telemetry.addData("First Angle ", orientation.firstAngle);
            opMode.telemetry.addData("Second Angle ", orientation.secondAngle);
            opMode.telemetry.addData("Third Angle ", orientation.thirdAngle);
            opMode.telemetry.update();

            // Vedem diferenta de grade dintre orientarea curentă și cea inițială
            double deltaFirst = Math.abs(getShortestAngleDEGREES(initOrientation.firstAngle, orientation.firstAngle));
            double deltaSecond = Math.abs(getShortestAngleDEGREES(initOrientation.secondAngle, orientation.secondAngle));
            double deltaThird = Math.abs(getShortestAngleDEGREES(initOrientation.thirdAngle, orientation.thirdAngle));

            // Verificăm care e primul unghi care execută 90 de grade
            if (deltaFirst >= 90) {
                notDetected = false;
                zAxisIndex = 1;
            }

            if (deltaSecond >= 90) {
                notDetected = false;
                zAxisIndex = 2;
            }

            if (deltaThird >= 90) {
                notDetected = false;
                zAxisIndex = 3;
            }

            // Axa care execută prima 90 de grade este axa Z
        }

        notDetected = true;

        // Verificăm Axa Y și Axa rămasă din urma scanării va fi axa X

        switch (zAxisIndex) {
            case 1: // Ne mai rămân de scanat secondAngle și thirdAngle
                while(notDetected){
                    // Mesaj pentru rotirea robotului
                    opMode.telemetry.addLine("Rotește robotul la 45 de grade la POZITIV pe Axa Y");

                    // Citim Datele
                    Orientation orientation = getAngularOrientation(axesReference, axesOrder, org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES);

                    // Afisam Datele
                    opMode.telemetry.addData("Second Angle ", orientation.secondAngle);
                    opMode.telemetry.addData("Third Angle ", orientation.thirdAngle);
                    opMode.telemetry.update();

                    // Vedem diferenta de grade dintre orientarea curentă și cea inițială
                    double deltaSecond = Math.abs(getShortestAngleDEGREES(initOrientation.secondAngle, orientation.secondAngle));
                    double deltaThird = Math.abs(getShortestAngleDEGREES(initOrientation.thirdAngle, orientation.thirdAngle));

                    // Verificăm care e primul unghi care execută 45 de grade
                    if (deltaSecond >= 45) {
                        notDetected = false;
                        yAxisIndex = 2;
                        xAxisIndex = 3;
                    }

                    if (deltaThird >= 45) {
                        notDetected = false;
                        yAxisIndex = 3;
                        xAxisIndex = 2;
                    }
                }
                break;
            case 2: // Ne mai rămân de scanat firstAngle și thirdAngle
                while(notDetected){
                    // Mesaj pentru rotirea robotului
                    opMode.telemetry.addLine("Rotește robotul la 45 de grade la POZITIV pe Axa Y");

                    // Citim Datele
                    Orientation orientation = getAngularOrientation(axesReference, axesOrder, org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES);

                    // Afisam Datele
                    opMode.telemetry.addData("First Angle ", orientation.firstAngle);
                    opMode.telemetry.addData("Third Angle ", orientation.thirdAngle);
                    opMode.telemetry.update();

                    // Vedem diferenta de grade dintre orientarea curentă și cea inițială
                    double deltaFirst = Math.abs(getShortestAngleDEGREES(initOrientation.firstAngle, orientation.firstAngle));
                    double deltaThird = Math.abs(getShortestAngleDEGREES(initOrientation.thirdAngle, orientation.thirdAngle));

                    // Verificăm care e primul unghi care execută 45 de grade
                    if (deltaFirst >= 45) {
                        notDetected = false;
                        yAxisIndex = 1;
                        xAxisIndex = 3;
                    }

                    if (deltaThird >= 45) {
                        notDetected = false;
                        yAxisIndex = 3;
                        xAxisIndex = 1;
                    }
                }
                break;
            case 3: // Ne mai rămân de scanat firstAngle și secondAngle
                while(notDetected){
                    // Mesaj pentru rotirea robotului
                    opMode.telemetry.addLine("Rotește robotul la 45 de grade la POZITIV pe Axa Y");

                    // Citim Datele
                    Orientation orientation = getAngularOrientation(axesReference, axesOrder, org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES);

                    // Afisam Datele
                    opMode.telemetry.addData("First Angle ", orientation.firstAngle);
                    opMode.telemetry.addData("Second Angle ", orientation.secondAngle);
                    opMode.telemetry.update();

                    // Vedem diferenta de grade dintre orientarea curentă și cea inițială
                    double deltaFirst = Math.abs(getShortestAngleDEGREES(initOrientation.firstAngle, orientation.firstAngle));
                    double deltaSecond = Math.abs(getShortestAngleDEGREES(initOrientation.secondAngle, orientation.secondAngle));

                    // Verificăm care e primul unghi care execută 45 de grade
                    if (deltaFirst >= 45) {
                        notDetected = false;
                        yAxisIndex = 1;
                        xAxisIndex = 2;
                    }

                    if (deltaSecond >= 45) {
                        notDetected = false;
                        yAxisIndex = 2;
                        xAxisIndex = 1;
                    }
                }
                break;
        }

        int zByte = 3 - zAxisIndex;
        int yByte = 3 - yAxisIndex;
        int xByte = 3 - xAxisIndex;

        opMode.telemetry.addLine(zAxisIndex + " angle is for Z-axis.");
        opMode.telemetry.addLine(yAxisIndex + " angle is for Y-axis.");
        opMode.telemetry.addLine(xAxisIndex + " angle is for X-axis.");
        opMode.telemetry.addData("Map Byte ", "0x" + Integer.toHexString((zByte << 4) | (yByte << 2) | xByte));
        opMode.telemetry.update();

        /*
         firstAngle(index 1)  - bit-ul 4 și 5 - normal e axa Z
         secondAngle(index 2) - bit-ul 2 și 3 - normal e axa Y
         thirdAngle(index 3)  - bit-ul 0 și 1 - nromal e axa X

         firstAngle  - zIndex este axa nouă pentru Z
         secondAngle - yIndex este axa nouă pentru Y
         thirdAngle  - xIndex este axa nouă pentru X

         Valoare pentru set Axa X: 0x0
         Valoare pentru set Axa Y: 0x1
         Valoare pentru set Axa Z: 0x2

         Functie:

          * Noua Axa X = Axa reală X detectată
          * Noua Axa Y = Axa reală Y detectată
          * Noua Axa Z = Axa reală Z detectată

        */
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

    private double getShortestAngle(double current, double target, AngleUnit angleUnit) {

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
