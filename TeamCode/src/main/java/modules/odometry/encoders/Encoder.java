package modules.odometry.encoders;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class Encoder extends DcMotorImplEx {

    public static final int ENCODER_TICKS = 8192;

    public static final double WHEEL_DIAMETER = 3.81; // 3.81 cm - 1.5 inch

    public static final double TICKS_PER_CM = ENCODER_TICKS / (WHEEL_DIAMETER * Math.PI);

    private double encoderPosition = 0.0;

    private double deltaPosition = 0.0;

    public Encoder(DcMotorEx encoder){

        super(encoder.getController(), encoder.getPortNumber());

        setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void updateValues() {
        double currentPosition = getCurrentPosition(); // Tinem minte pozitia curenta

        deltaPosition    = currentPosition - encoderPosition; // Aflam delta

        encoderPosition = currentPosition; // Pozitia veche devine pozitia noua (Update Value)
    }

    public double getDeltaDistance() {

        return deltaPosition * TICKS_PER_CM;
    }

    public double getGlobalDistance() {

        return encoderPosition * TICKS_PER_CM;
    }

}
