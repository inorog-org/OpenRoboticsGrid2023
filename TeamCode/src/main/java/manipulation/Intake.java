package manipulation;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Intake {

    private Servo upper_right; // Axon
    private Servo upper_left;  // Axon
    private Servo aspirator;   // Axon
    private Servo glider;      // Axon

    public Intake(HardwareMap hardwareMap) {

        upper_right = hardwareMap.get(Servo.class, "upper_right");
        upper_left  = hardwareMap.get(Servo.class, "upper_left");

        aspirator   = hardwareMap.get(Servo.class, "aspirator");

        glider      = hardwareMap.get(Servo.class, "glider");
    }

    enum State {


    }


}
