package modules.drive.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;

import modules.drive.configuration.modes.BrakingMode;

public class BrakeSystem {

    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backRight;
    private final DcMotor backLeft;

    public BrakeSystem(BrakingMode brakingMode, DcMotor frontLeft, DcMotor frontRight, DcMotor backRight, DcMotor backLeft){

        this.frontLeft  = frontLeft;
        this.frontRight = frontRight;
        this.backRight  = backRight;
        this.backLeft   = backLeft;

        setBrakeConfiguration(brakingMode);
    }

    private void setBrakeConfiguration(BrakingMode brakeMode){
        switch (brakeMode){
            case BRAKE:
            case PID_BRAKE:
                setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                break;
            case FLOAT:
            case PID_FLOAT:
                setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                break;
        }
    }

    private void setZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior){
        frontLeft.setZeroPowerBehavior(behavior);
        backRight.setZeroPowerBehavior(behavior);
        frontRight.setZeroPowerBehavior(behavior);
        backLeft.setZeroPowerBehavior(behavior);
    }
}
