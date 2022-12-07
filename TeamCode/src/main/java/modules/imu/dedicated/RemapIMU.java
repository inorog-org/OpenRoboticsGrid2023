package modules.imu.dedicated;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import modules.imu.AngleArithmetic;
import modules.imu.IMU;
import modules.imu.IMUDetectAxis;

public class RemapIMU implements IMUDetectAxis {

    private final IMU imu;
    private final LinearOpMode opMode;

    public RemapIMU(LinearOpMode opMode, IMU imu){
        this.imu = imu;
        this.opMode = opMode;
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
        imu.remapAxis(0x24, 0x0);

        // Mesaj Init
        opMode.telemetry.addLine("Poziționează robotul într-o poziție de inițiere");
        opMode.telemetry.addLine();
        opMode.telemetry.addLine("Apasă A pentru a inițializa poziția");
        opMode.telemetry.update();

        // Wait press A
        while (!opMode.gamepad1.a) ;
        opMode.sleep(500);

        // Citire Unghi Init
        Orientation initOrientation = imu.getAngularOrientation();

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
        ySign = (byte) ((ySign == 1) ? 0 : 1);
        zSign = (byte) ((zSign == 1) ? 0 : 1);

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
            Orientation orientation = imu.getAngularOrientation();

            // Afisam Datele
            opMode.telemetry.addData("First Angle ", orientation.firstAngle);
            opMode.telemetry.addData("Second Angle ", orientation.secondAngle);
            opMode.telemetry.addData("Third Angle ", orientation.thirdAngle);
            opMode.telemetry.update();

            // Vedem diferenta de grade dintre orientarea curentă și cea inițială
            double deltaFirst  = AngleArithmetic.getShortestAngleDEGREES(initOrientation.firstAngle, orientation.firstAngle);
            double deltaSecond = AngleArithmetic.getShortestAngleDEGREES(initOrientation.secondAngle, orientation.secondAngle);
            double deltaThird  = AngleArithmetic.getShortestAngleDEGREES(initOrientation.thirdAngle, orientation.thirdAngle);

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
            Orientation orientation = imu.getAngularOrientation();

            // Afisam Datele
            if (zAxisIndex != 1) opMode.telemetry.addData("First Angle ", orientation.firstAngle);
            if (zAxisIndex != 2) opMode.telemetry.addData("Second Angle ", orientation.secondAngle);
            if (zAxisIndex != 3) opMode.telemetry.addData("Third Angle ", orientation.thirdAngle);
            opMode.telemetry.update();

            // Vedem diferenta de grade dintre orientarea curentă și cea inițială
            double deltaFirst  = (zAxisIndex != 1) ? AngleArithmetic.getShortestAngleDEGREES(initOrientation.firstAngle, orientation.firstAngle)   : 0;
            double deltaSecond = (zAxisIndex != 2) ? AngleArithmetic.getShortestAngleDEGREES(initOrientation.secondAngle, orientation.secondAngle) : 0;
            double deltaThird  = (zAxisIndex != 3) ? AngleArithmetic.getShortestAngleDEGREES(initOrientation.thirdAngle, orientation.thirdAngle)   : 0;

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
            Orientation orientation = imu.getAngularOrientation();

            double delta = 0;
            // Afisam Datele
            switch (xAxisIndex){
                case 1: {
                    opMode.telemetry.addData("First Angle ", orientation.firstAngle);
                    delta = AngleArithmetic.getShortestAngleDEGREES(initOrientation.firstAngle, orientation.firstAngle);
                }
                break;
                case 2: {
                    opMode.telemetry.addData("Second Angle ", orientation.secondAngle);
                    delta = AngleArithmetic.getShortestAngleDEGREES(initOrientation.secondAngle, orientation.secondAngle);
                }
                break;
                case 3: {
                    opMode.telemetry.addData("Third Angle ", orientation.thirdAngle);
                    delta = AngleArithmetic.getShortestAngleDEGREES(initOrientation.thirdAngle, orientation.thirdAngle);
                }
                break;
            }
            opMode.telemetry.update();

            if(Math.abs(delta) >= 45){
                return (byte) Math.signum(delta);
            }
        }
    }

}
