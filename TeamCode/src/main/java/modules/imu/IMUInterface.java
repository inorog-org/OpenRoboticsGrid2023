package modules.imu;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.MagneticFlux;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Quaternion;
import org.firstinspires.ftc.robotcore.external.navigation.Temperature;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

public interface IMUInterface {

    //
    //                        # IMU BNO055 #
    //
    //       Aceasta trebuie schimbată în funcție de configurația robotului
    //
    //                           | Z axis
    //                           |
    //     (Motor Port Side)     |   / X axis
    //                       ____|__/____
    //          Y axis     / *   | /    /|   (IO Side)
    //          _________ /______|/    //       I2C
    //                   /___________ //      Digital
    //                  |____________|/       Analog
    //
    //                 (Servo Port Side)
    //
    //  ~ Configurația DEFAULT ~
    //  Axa X pozitivă este îndreptată către porturile USB
    //  Axa Y pozitivă este îndreptată către porturile I2C
    //  Axa Z pozitivă este îndreptată către fața REV Hub/Control Hub, senzorul
    //   fiind dispus pe plan orizontal
    //
    // ~ Poziționare relativă față de REV Hub/Control Hub ~
    //
    //  # Mențiune - distanța se va măsura de la marginea senzorului
    //               până la marginea REV/Control Hub-ului
    //  Axa X - 35MM (USB Ports)
    //  AXA Y - 83MM (Motor Ports)
    //

    /**
     *  Funcție pentru inițializarea Parametrilor Senzorilor - ESENȚIALĂ !!!
     * */
    void parametersInit(BNO055IMU.AngleUnit angleUnit, BNO055IMU.AccelUnit accelUnit);

    /**
     *  Funcția returnează dacă giroscopul este calibrat
     * */
    boolean isGyroCalibrated();

    /**
     *  Funcția returnează dacă accelerometrul este calibrat
     * */
    boolean isAccelerometerCalibrated();

    /**
     *  Funcția returnează dacă magnetometrul este calibrat
     * */
    boolean isMagnetometerCalibrated();

    /**
     *  Funcția returnează dacă senzorul este calibrat
     * */
    boolean isSystemCalibrated();

    /**
     *  Funcția setează unitatea de măsură a unghiului de pe senzor (RADIANI SAU GRADE)
     * */
    void setAngleUnit(BNO055IMU.AngleUnit angleUnit);

    /**
     *  Funcția setează unitatea de măsură a accelerației de pe senzor (METRI/S^2 SAU earthGravity/1000)
     * */
    void setAccelUnit(BNO055IMU.AccelUnit accelUnit);

    /**
     *  Funcția setează unitatea de măsură a temperaturei de pe senzor (CELSIUS SAU FARENHEIT)
     */
    void setTemperatureUnit(BNO055IMU.TempUnit temperatureUnit);

    /**
     *  Funcția care setează ordinea axelor
     * */
    void setAxesOrder(AxesOrder axesOrder);

    /**
     *  Funcția care setează referința axelor
     *
     *  INTRINSIC - Coordinate system of next rotation relative
     * to previous rotation
     *
     *  EXTRINSIC - Coordinate system of next rotation relative
     * to (fixed) world coordinate system
     *
     * @see AxesReference
     * */
    void setAxesRefrence(AxesReference axesReference);

    /**
     *  Funcția returnează unghiurile de orientare a senzorului
     * */
    Orientation getAngularOrientation();

    /**
     * Funcția returnează unghiurile de orientatre a senzorului cu Parametri Predefiniți
     * */
    Orientation getAngularOrientationParamPredef();

    /**
     *  Funcția returnează unghiurile de orientare a senzorului
     *
     * @param reference - tipul unghiurilor returnate (INTRINSIC SAU EXTRINSIC)
     * @param order     - ordinea axelor
     * @param angleUnit - unitatea de măsură a unghiului
     * */
    Orientation getAngularOrientation(AxesReference reference, AxesOrder order, org.firstinspires.ftc.robotcore.external.navigation.AngleUnit angleUnit);

    /**
     *  Funcța returnează vitezele unghiulare a senzorului în funcție de axe
     * */
    AngularVelocity getAngularVelocity();

    /**
     *  Funcția returnează accelerația senzorului luând totodată în considerare accelerația gravitațională
     * */
    Acceleration getOverallAcceleration();

    /**
     *  Funcția returnează accelerația senzorului fără a lua în considerare accelerația gravitațională
     * */
    Acceleration getLinearAcceleration();

    /**
     *  Funcția returnează vectorul accelerației gravitaționale
     * */
    Acceleration getGravity();

    /**
     *  Funcția returnează temperatura detectată de senzor
     * */
    Temperature getTemperature();

    /**
     *  Funcția returnează puterea câmpului magnetic pentru fiecare axă în parte - măsurată în TESLA
     * */
    MagneticFlux getMagneticFieldStrength();

    /**
     *  Funcția returnează orientarea senzorului drept Cuaternion
     *
     * @see Quaternion
     * */
    Quaternion getQuaternionOrientation();

    /**
     *  Funcția returnează poziția senzorului în spațiu integrând valorile de pe senzor
     *
     *  MENȚIUNE !!! - FUNCȚIA ARE POTENȚIAL
     * */
    Position getPosition();

    /**
     *  Funcția returnează viteza senzorului în spațiu
     * */
    Velocity getVelocity();

    /**
     *  Funcția returnează accelerația senzorului în spațiu
     * */
    Acceleration getAcceleration();

    /**
     *  Funcția returnează statusul senzorului în materie de Calibrare
     * */
    BNO055IMU.CalibrationStatus getCalibrationStatus();

}