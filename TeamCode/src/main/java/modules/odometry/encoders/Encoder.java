package modules.odometry.encoders;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import modules.odometry.configuration.OdometryConstants;

public class Encoder extends DcMotorImplEx {

    public static final double TICKS_TO_CM = (Math.PI * OdometryConstants.WHEEL_DIAMETER) / OdometryConstants.ENCODER_TICKS;

    private double encoderPosition = 0.0;

    private double deltaPosition = 0.0;

    private double MULTIPLIER = 1.0;

    private int DIRECTION_COEFF = 1;

    public Encoder(DcMotorEx encoder, double multiplier, DcMotorSimple.Direction direction){

        super(encoder.getController(), encoder.getPortNumber());

        this.MULTIPLIER = multiplier;
        this.DIRECTION_COEFF = (direction == Direction.FORWARD) ? 1 : -1;

        setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void updateValues() {
        double currentPosition = getCurrentPosition(); // Tinem minte pozitia curenta

        deltaPosition    = currentPosition - encoderPosition; // Aflam delta

        encoderPosition = currentPosition; // Pozitia veche devine pozitia noua (Update Value)
    }

    public double getDeltaDistance() {

        return deltaPosition * TICKS_TO_CM * MULTIPLIER * DIRECTION_COEFF;
    }

    public double getGlobalDistance() {

        return encoderPosition * TICKS_TO_CM * MULTIPLIER * DIRECTION_COEFF;
    }

}
