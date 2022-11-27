package modules.odometry;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Encoder {

    private final DcMotorEx encoder;

    private final int ENCODER_TICKS = 8192;

    private final double WHEEL_DIAMETER = 3.8; // 3.8 cm - 1.5 inch

    private final double TICKS_PER_CM = ENCODER_TICKS / (WHEEL_DIAMETER * Math.PI);

    private double previousPosition = 0.0;

    private double deltaPosition = 0.0;

    public Encoder(HardwareMap hardwareMap, String name){

        encoder = hardwareMap.get(DcMotorEx.class, name);

        encoder.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        encoder.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void updateValues() {
        double currentPosition = encoder.getCurrentPosition(); // Tinem minte pozitia curenta

        deltaPosition    = currentPosition - previousPosition; // Aflam delta

        previousPosition = currentPosition; // Pozitia veche devine pozitia noua (Update Value)
    }

    public double getDeltaDistance() {

        return deltaPosition * TICKS_PER_CM;
    }

    public double getGlobalDelta() {

        return previousPosition * TICKS_PER_CM;
    }
}
