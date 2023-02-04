package modules.drive;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import modules.drive.configuration.modes.ControlMode;
import modules.drive.configuration.DriveSystemConfiguration;
import modules.drive.configuration.modes.MecanumWheelsConfiguration;

/**
 *  Aceasta este clasa care face Handle la motoarele pentru Mecanum
 *  Nota: Initierea și Configurarile principale se vor afla aici.
 * */
public class Motors {

    // Motors for Mecanum
    public final DcMotorEx frontLeft;
    public final DcMotorEx frontRight;
    public final DcMotorEx backRight;
    public final DcMotorEx backLeft;

    // Brake System
    private final BrakeSystem brakes;

    // Colored Powers for Movement
    public double redPower;
    public double bluePower;

    // Powers for Rotation
    public double rightPower;
    public double leftPower;

    // Mecanum Coefficients
    public int mecanumConfig_FIRSTCOEFF;   // IN ORIENTED  1 & OUT ORIENTED -1
    public int mecanumConfig_SECONDCOEFF;  // IN ORIENTED -1 & OUT ORIENTED  1
    public int DIRECTION_ROTATE_COEFF;     // 1 is for FORWARD and -1 is for REVERSE

/*
                        ~ Configurație motoare - porturi ~
        Inline - ordinea in sensul acelor de ceasornic începând cu front_left
            ┏━━━━━━━━━━━━━┳━━━━━━━━━━━━━┳━━━━━━━━━━━━━┳━━━━━━━━━━━━━┓
            ┃    Port 0   ┃    Port 1   ┃    Port 2   ┃   Port 3    ┃
            ┃  front_left ┃ front_right ┃  back_right ┃  back_left  ┃
            ┗━━━━━━━━━━━━━┻━━━━━━━━━━━━━┻━━━━━━━━━━━━━┻━━━━━━━━━━━━━┛
                       ~ Configurația motoare - culori ~
                         ┏━━━━━━━━━━━━━┳━━━━━━━━━━━━━┓
                         ┃  front_left ┃ front_right ┃
                         ┃  bluePower  ┃  redPower   ┃
                         ┠━━━━━━━━━━━━━╋━━━━━━━━━━━━━┫
                         ┃  back_left  ┃ back_right  ┃
                         ┃  redPower   ┃  bluePower  ┃
                         ┗━━━━━━━━━━━━━┻━━━━━━━━━━━━━┛
*/

    /// === Motors System Constructor === ///
    public Motors(HardwareMap hardwareMap){

        /// === Configurare Motoare === ///
        frontLeft  = hardwareMap.get(DcMotorEx.class, "front_left");
        frontRight = hardwareMap.get(DcMotorEx.class, "front_right");
        backRight  = hardwareMap.get(DcMotorEx.class, "back_right");
        backLeft   = hardwareMap.get(DcMotorEx.class, "back_left");

        /// === Configurare Sistem de franare === ///
        brakes = new BrakeSystem(DriveSystemConfiguration.brakeConfiguration, frontLeft, frontRight, backRight, backLeft);

        setMecanumWheelsConfiguration(DriveSystemConfiguration.mecanumWheelsConfiguration);
        setMecanumDirection(DriveSystemConfiguration.directionMotors);
    }

    /// === Setting Mecanum/Motors Direction === ///
    private void setMecanumDirection(DcMotor.Direction direction) {
        switch (direction) {
            case FORWARD:
                frontRight.setDirection(DcMotor.Direction.FORWARD);
                frontLeft.setDirection(DcMotor.Direction.REVERSE);
                backRight.setDirection(DcMotor.Direction.FORWARD);
                backLeft.setDirection(DcMotor.Direction.REVERSE);
                DIRECTION_ROTATE_COEFF =  1;
                break;
            case REVERSE:
                frontRight.setDirection(DcMotor.Direction.REVERSE);
                frontLeft.setDirection(DcMotor.Direction.FORWARD);
                backRight.setDirection(DcMotor.Direction.REVERSE);
                backLeft.setDirection(DcMotor.Direction.FORWARD);
                DIRECTION_ROTATE_COEFF = -1;
                break;
        }
    }

    /// === Setting Mecanum Configuration === ///
    private void setMecanumWheelsConfiguration(MecanumWheelsConfiguration mecanumConfig) {
        switch (mecanumConfig) {
            case IN_ORIENTED_CONFIG:
                mecanumConfig_FIRSTCOEFF  =  1;
                mecanumConfig_SECONDCOEFF = -1;
                break;
            case OUT_ORIENTED_CONFIG:
                mecanumConfig_FIRSTCOEFF = -1;
                mecanumConfig_SECONDCOEFF = 1;
                break;
        }
    }


    /// === Setting Encoders Mode by Control Mode === ///
    public void setupEncoders(ControlMode controlMode) {
        switch (controlMode) {
            case AUTONOMOUS:
                setupEncoders(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                setupEncoders(DcMotor.RunMode.RUN_USING_ENCODER);
                break;
            case DRIVE:
                setupEncoders(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                break;
        }
    }

    /// === Setting Encoders Mode by Run Mode === ///
    public void setupEncoders(DcMotor.RunMode runMode) {
        frontRight.setMode(runMode);
        frontLeft.setMode(runMode);
        backRight.setMode(runMode);
        backLeft.setMode(runMode);
    }

    /// === Motors are Busy === //
    public boolean motorsAreBusy(){
        return frontLeft.isBusy() || frontRight.isBusy() || backLeft.isBusy() || backRight.isBusy();
    }

    /// === The Equations of Mecanum Wheels  ~ The Masterpiece ~ === ///
    public void updateMovementPower(double angle, double magnitude) {

        double sinus   = Math.sin(angle);
        double cosinus = Math.cos(angle);

        double denominator = maxPowerMovement(cosinus, sinus);

        bluePower  =   ((mecanumConfig_FIRSTCOEFF  * cosinus  + sinus) / denominator) * magnitude;
        redPower   =   ((mecanumConfig_SECONDCOEFF * cosinus  + sinus) / denominator) * magnitude;
    }

    private double maxPowerMovement(double cos, double sin) {
        switch (DriveSystemConfiguration.powerMode) {
            case MAXIMUM:
                return Math.abs(cos)  + Math.abs(sin);
            case CONSTANT:
                return Math.sqrt(2);
        }
        return Math.sqrt(2);
    }

    public void updateRotatePower(double magnitude) {

        double coefficient = Range.clip( DIRECTION_ROTATE_COEFF * magnitude, -1, 1);

        rightPower  =  -coefficient;
        leftPower   =   coefficient;
    }

    /// === Stop Power for Motors === ///
    public void stopPower() {
        frontRight.setPower(0);
        frontLeft.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);
    }

}
