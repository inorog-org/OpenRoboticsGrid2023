package modules.odometry.encoders;

public class EncodersExceptions extends Exception{

    public EncodersExceptions(){
        super("Unul din Encoderele laterale necesită inițializate");
    }
}
