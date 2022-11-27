package modules.odometry;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;

public class Encoder extends DcMotorImplEx {

    private final int ENCODER_TICKS = 8192;

    private final double WHEEL_DIAMETER = 3.8; // 3.8 cm - 1.5 inch

    private final double TICKS_PER_CM = ENCODER_TICKS / (WHEEL_DIAMETER * Math.PI);

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

    public double getGlobalDelta() {

        return encoderPosition * TICKS_PER_CM;
    }
}
