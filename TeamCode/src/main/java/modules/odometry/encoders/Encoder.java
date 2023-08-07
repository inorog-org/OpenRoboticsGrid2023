package modules.odometry.encoders;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorImplEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import modules.configuration.odometry.OdometryConstants;

public class Encoder extends DcMotorImplEx {

    public static final double TICKS_TO_CM = (Math.PI * OdometryConstants.WHEEL_DIAMETER) / OdometryConstants.ENCODER_TICKS;

    private double encoderPosition = 0.0;
    private double deltaPosition = 0.0;

    private double MULTIPLIER;
    private int DIRECTION_COEFF;

    public Encoder(DcMotorEx encoder, double multiplier, DcMotorSimple.Direction direction){

        super(encoder.getController(), encoder.getPortNumber());

        this.MULTIPLIER = multiplier;
        this.DIRECTION_COEFF = (direction == Direction.FORWARD) ? 1 : -1;

        setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

        // Integer Overflow

        this.lastPosition = 0;
        this.velocityEstimates = new double[3];
        this.lastUpdateTime = System.currentTimeMillis() / 1000.0;
    }

    public void updateValues() {
        double currentPosition = getCorrectedCurrentPosition(); // Tinem minte pozitia curenta

        deltaPosition    = currentPosition - encoderPosition; // Aflam delta

        encoderPosition = currentPosition; // Pozitia veche devine pozitia noua (Update Value)
    }

    public double getDeltaDistance() {

        return deltaPosition * TICKS_TO_CM * MULTIPLIER * DIRECTION_COEFF;
    }

    public double getGlobalDistance() {

        return encoderPosition * TICKS_TO_CM * MULTIPLIER * DIRECTION_COEFF;
    }

    // ---------------- Integer Overflow Code RoadRunner - Encoders ----------------- //

    private int lastPosition;
    private int velocityEstimateIdx;
    private double[] velocityEstimates;
    private double lastUpdateTime;

    private final static int CPS_STEP = 0x10000;
    private static double inverseOverflow(double input, double estimate) {
        // convert to uint16
        int real = (int) input & 0xffff;
        // initial, modulo-based correction: it can recover the remainder of 5 of the upper 16 bits
        // because the velocity is always a multiple of 20 cps due to Expansion Hub's 50ms measurement window
        real += ((real % 20) / 4) * CPS_STEP;
        // estimate-based correction: it finds the nearest multiple of 5 to correct the upper bits by
        real += Math.round((estimate - real) / (5 * CPS_STEP)) * 5 * CPS_STEP;
        return real;
    }

    /**
     * Gets the position from the underlying motor and adjusts for the set direction.
     * Additionally, this method updates the velocity estimates used for compensated velocity
     *
     * @return encoder position
     */
    public int getCorrectedCurrentPosition() {
        int currentPosition = getCurrentPosition() *  DIRECTION_COEFF;
        if (currentPosition != lastPosition) {
            double currentTime = System.currentTimeMillis() / 1000.0;
            double dt = currentTime - lastUpdateTime;
            velocityEstimates[velocityEstimateIdx] = (currentPosition - lastPosition) / dt;
            velocityEstimateIdx = (velocityEstimateIdx + 1) % 3;
            lastPosition = currentPosition;
            lastUpdateTime = currentTime;
        }
        return currentPosition;
    }

    /**
     * Gets the velocity directly from the underlying motor and compensates for the direction
     * See {@link #getCorrectedVelocity} for high (>2^15) counts per second velocities (such as on REV Through Bore)
     *
     * @return raw velocity
     */
    public double getRawVelocity() {
        return getVelocity() * DIRECTION_COEFF;
    }

    /**
     * Uses velocity estimates gathered in {@link #getCurrentPosition} to estimate the upper bits of velocity
     * that are lost in overflow due to velocity being transmitted as 16 bits.
     * CAVEAT: must regularly call {@link #getCurrentPosition} for the compensation to work correctly.
     *
     * @return corrected velocity
     */
    public double getCorrectedVelocity() {
        double median = velocityEstimates[0] > velocityEstimates[1]
                ? Math.max(velocityEstimates[1], Math.min(velocityEstimates[0], velocityEstimates[2]))
                : Math.max(velocityEstimates[0], Math.min(velocityEstimates[1], velocityEstimates[2]));
        return inverseOverflow(getRawVelocity(), median);
    }

}
